package com.ruoyi.framework.aspectj;

import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.security.context.PermissionContextHolder;
import com.ruoyi.system.mapper.SysDeptMapper;

/**
 * 数据过滤处理
 *
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect
{
    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 单位层级数据权限（多级数据隔离）
     * 市级可见所有下级单位，区县级只能看本单位
     */
    public static final String DATA_SCOPE_UNIT_HIERARCHY = "6";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope)
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser))
        {
            SysUser currentUser = loginUser.getUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
            {
                String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), PermissionContextHolder.getContext());
                // 先执行角色数据权限过滤
                dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(), controllerDataScope.userAlias(), permission);

                // 检查是否有角色使用单位层级数据权限
                String unitHierarchySql = getUnitCodeHierarchySql(currentUser, controllerDataScope.deptAlias());
                if (StringUtils.isNotBlank(unitHierarchySql))
                {
                    // 将单位层级过滤条件添加到现有条件中
                    Object params = joinPoint.getArgs()[0];
                    if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
                    {
                        BaseEntity baseEntity = (BaseEntity) params;
                        String existingScope = (String) baseEntity.getParams().get(DATA_SCOPE);
                        if (StringUtils.isNotBlank(existingScope))
                        {
                            // 合并条件：原有条件 AND 单位层级条件
                            baseEntity.getParams().put(DATA_SCOPE, existingScope + " AND (" + unitHierarchySql + ")");
                        }
                        else
                        {
                            baseEntity.getParams().put(DATA_SCOPE, " AND (" + unitHierarchySql + ")");
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取单位层级过滤SQL
     * 根据用户的部门层级决定过滤方式：
     * - 市级(level=1)：可见所有下级单位(LIKE前缀匹配)
     * - 区县(level=2)/派出所(level=3)：只能看本单位(精确匹配)
     *
     * @param user 当前用户
     * @param deptAlias 部门表别名
     * @return 单位层级过滤SQL条件
     */
    private String getUnitCodeHierarchySql(SysUser user, String deptAlias)
    {
        // 检查用户是否有角色使用单位层级数据权限
        boolean hasUnitHierarchyRole = user.getRoles().stream()
            .anyMatch(role -> DATA_SCOPE_UNIT_HIERARCHY.equals(role.getDataScope())
                           && StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL));

        if (!hasUnitHierarchyRole)
        {
            return null;
        }

        // 获取用户的部门信息
        SysDept dept = deptMapper.selectDeptById(user.getDeptId());
        if (dept == null || StringUtils.isBlank(dept.getUnitCode()))
        {
            // 没有部门或没有单位编码，不做额外过滤
            return null;
        }

        String unitCode = dept.getUnitCode();
        Integer deptLevel = dept.getDeptLevel();

        if (deptLevel == null)
        {
            return null;
        }

        // 根据层级生成过滤条件
        if (deptLevel == 1)
        {
            // 市级：可见所有以本单位编码开头的单位（所有下级区县和派出所）
            return StringUtils.format("{}.unit_code LIKE CONCAT({}, '%')", deptAlias, unitCode);
        }
        else
        {
            // 区县和派出所：只能看本单位
            return StringUtils.format("{}.unit_code = {}", deptAlias, unitCode);
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     * @param permission 权限字符
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias, String permission)
    {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<String>();
        List<String> scopeCustomIds = new ArrayList<String>();
        user.getRoles().forEach(role -> {
            if (DATA_SCOPE_CUSTOM.equals(role.getDataScope()) && StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && (StringUtils.isEmpty(permission) || StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission))))
            {
                scopeCustomIds.add(Convert.toStr(role.getRoleId()));
            }
        });

        for (SysRole role : user.getRoles())
        {
            String dataScope = role.getDataScope();
            if (conditions.contains(dataScope) || StringUtils.equals(role.getStatus(), UserConstants.ROLE_DISABLE))
            {
                continue;
            }
            if (StringUtils.isNotEmpty(permission) && !StringUtils.containsAny(role.getPermissions(), Convert.toStrArray(permission)))
            {
                continue;
            }
            if (DATA_SCOPE_ALL.equals(dataScope))
            {
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                break;
            }
            else if (DATA_SCOPE_CUSTOM.equals(dataScope))
            {
                if (scopeCustomIds.size() > 1)
                {
                    // 多个自定数据权限使用in查询，避免多次拼接。
                    sqlString.append(StringUtils.format(" OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in ({}) ) ", deptAlias, String.join(",", scopeCustomIds)));
                }
                else
                {
                    sqlString.append(StringUtils.format(" OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias, role.getRoleId()));
                }
            }
            else if (DATA_SCOPE_DEPT.equals(dataScope))
            {
                sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
            }
            else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
            {
                sqlString.append(StringUtils.format(" OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )", deptAlias, user.getDeptId(), user.getDeptId()));
            }
            else if (DATA_SCOPE_SELF.equals(dataScope))
            {
                if (StringUtils.isNotBlank(userAlias))
                {
                    sqlString.append(StringUtils.format(" OR {}.user_id = {} ", userAlias, user.getUserId()));
                }
                else
                {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias));
                }
            }
            else if (DATA_SCOPE_UNIT_HIERARCHY.equals(dataScope))
            {
                // 单位层级数据权限由handleDataScope方法统一处理
                // 此处不做处理，仅标记条件已处理
            }
            conditions.add(dataScope);
        }

        // 角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (StringUtils.isEmpty(conditions))
        {
            sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias));
        }

        if (StringUtils.isNotBlank(sqlString.toString()))
        {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
            {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}

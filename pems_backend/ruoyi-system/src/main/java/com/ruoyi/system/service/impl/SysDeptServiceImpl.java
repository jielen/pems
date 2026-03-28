package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysDeptExtend;
import com.ruoyi.system.mapper.SysDeptExtendMapper;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 部门管理 服务实现
 * 
 * @author ruoyi
 */
@Service
public class SysDeptServiceImpl implements ISysDeptService
{
    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysDeptExtendMapper deptExtendMapper;

    /**
     * 查询部门管理数据
     * 
     * @param dept 部门信息
     * @return 部门信息集合
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept)
    {
        return deptMapper.selectDeptList(dept);
    }

    /**
     * 查询部门树结构信息
     * 
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<TreeSelect> selectDeptTreeList(SysDept dept)
    {
        List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
        return buildDeptTreeSelect(depts);
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts)
    {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId()))
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> depts)
    {
        List<SysDept> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询部门树信息
     * 
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return deptMapper.selectDeptListByRoleId(roleId, role.isDeptCheckStrictly());
    }

    /**
     * 根据部门ID查询信息
     * 
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDept selectDeptById(Long deptId)
    {
        return deptMapper.selectDeptById(deptId);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     * 
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId)
    {
        return deptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 是否存在子节点
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId)
    {
        int result = deptMapper.hasChildByDeptId(deptId);
        return result > 0;
    }

    /**
     * 查询部门是否存在用户
     * 
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId)
    {
        int result = deptMapper.checkDeptExistUser(deptId);
        return result > 0;
    }

    /**
     * 校验部门名称是否唯一
     * 
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDept dept)
    {
        Long deptId = StringUtils.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();
        SysDept info = deptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     * 
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId)
    {
        if (!SecurityUtils.isAdmin() && StringUtils.isNotNull(deptId))
        {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> depts = SpringUtils.getAopProxy(this).selectDeptList(dept);
            if (StringUtils.isEmpty(depts))
            {
                throw new ServiceException("没有权限访问部门数据！");
            }
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertDept(SysDept dept)
    {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new ServiceException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        int result = deptMapper.insertDept(dept);

        // 创建部门扩展记录并生成unit_code
        if (result > 0)
        {
            SysDeptExtend deptExtend = new SysDeptExtend();
            deptExtend.setDeptId(dept.getDeptId());

            // 计算deptLevel: 父节点为0(顶级)时为1,否则为父节点level+1
            Integer parentLevel = 0;
            SysDeptExtend parentExtend = deptExtendMapper.selectDeptExtendByDeptId(dept.getParentId());
            if (parentExtend != null)
            {
                parentLevel = parentExtend.getDeptLevel();
                deptExtend.setParentUnitCode(parentExtend.getUnitCode());
            }
            deptExtend.setDeptLevel(parentLevel + 1);

            // 生成unit_code
            String unitCode = generateUnitCode(dept.getParentId());
            deptExtend.setUnitCode(unitCode);

            // 计算序号
            Integer maxSeq = deptExtendMapper.selectMaxSequenceNumByParentId(dept.getParentId());
            deptExtend.setSequenceNum(maxSeq == null ? 1 : maxSeq + 1);

            // 设置ancestorsPath
            String ancestorsPath = "";
            if (parentExtend != null)
            {
                ancestorsPath = parentExtend.getAncestorsPath();
            }
            deptExtend.setAncestorsPath(ancestorsPath + "/" + unitCode);

            deptExtendMapper.insertDeptExtend(deptExtend);
        }

        return result;
    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDept(SysDept dept)
    {
        SysDept newParentDept = deptMapper.selectDeptById(dept.getParentId());
        SysDept oldDept = deptMapper.selectDeptById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept))
        {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result = deptMapper.updateDept(dept);

        // 如果父部门变更，需要重新计算unit_code链
        if (result > 0 && StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)
                && !oldDept.getParentId().equals(dept.getParentId()))
        {
            // 更新当前部门的extend信息
            SysDeptExtend deptExtend = deptExtendMapper.selectDeptExtendByDeptId(dept.getDeptId());
            if (deptExtend != null)
            {
                SysDeptExtend parentExtend = deptExtendMapper.selectDeptExtendByDeptId(dept.getParentId());
                Integer parentLevel = parentExtend != null ? parentExtend.getDeptLevel() : 0;
                String parentUnitCode = parentExtend != null ? parentExtend.getUnitCode() : "";

                deptExtend.setDeptLevel(parentLevel + 1);
                deptExtend.setParentUnitCode(parentUnitCode);

                // 重新生成unit_code
                String newUnitCode = generateUnitCode(dept.getParentId());
                deptExtend.setUnitCode(newUnitCode);

                // 更新序号
                Integer maxSeq = deptExtendMapper.selectMaxSequenceNumByParentId(dept.getParentId());
                deptExtend.setSequenceNum(maxSeq == null ? 1 : maxSeq + 1);

                // 更新ancestorsPath
                String ancestorsPath = "";
                if (parentExtend != null)
                {
                    ancestorsPath = parentExtend.getAncestorsPath();
                }
                deptExtend.setAncestorsPath(ancestorsPath + "/" + newUnitCode);

                deptExtendMapper.updateDeptExtend(deptExtend);

                // 递归更新所有子部门的unit_code链
                updateDescendantUnitCodes(dept.getDeptId(), newUnitCode, parentLevel + 1, deptExtend.getAncestorsPath());
            }
        }

        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
                && !StringUtils.equals("0", dept.getAncestors()))
        {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 递归更新所有子部门的unit_code链
     *
     * @param parentId 父部门ID
     * @param parentUnitCode 父单位编码
     * @param parentLevel 父层级
     * @param parentAncestorsPath 父ancestors路径
     */
    private void updateDescendantUnitCodes(Long parentId, String parentUnitCode, Integer parentLevel, String parentAncestorsPath)
    {
        List<SysDept> children = deptMapper.selectChildrenDeptById(parentId);
        for (SysDept child : children)
        {
            if (child.getDeptId().equals(parentId))
            {
                continue;
            }
            SysDeptExtend childExtend = deptExtendMapper.selectDeptExtendByDeptId(child.getDeptId());
            if (childExtend != null)
            {
                // 生成新的unit_code
                String newUnitCode = generateUnitCode(parentId);
                childExtend.setUnitCode(newUnitCode);
                childExtend.setParentUnitCode(parentUnitCode);
                childExtend.setDeptLevel(parentLevel + 1);
                childExtend.setAncestorsPath(parentAncestorsPath + "/" + newUnitCode);

                // 更新序号
                Integer maxSeq = deptExtendMapper.selectMaxSequenceNumByParentId(parentId);
                childExtend.setSequenceNum(maxSeq == null ? 1 : maxSeq + 1);

                deptExtendMapper.updateDeptExtend(childExtend);

                // 递归更新子部门
                updateDescendantUnitCodes(child.getDeptId(), newUnitCode, parentLevel + 1, childExtend.getAncestorsPath());
            }
        }
    }

    /**
     * 生成单位编码
     * 格式: 父编码 + 3位序号
     * 顶级部门(parentId=0)编码为"000"
     *
     * @param parentId 父部门ID
     * @return 单位编码
     */
    private String generateUnitCode(Long parentId)
    {
        // 顶级部门
        if (parentId == 0 || parentId == null)
        {
            return "000";
        }

        // 获取父部门的unit_code
        SysDeptExtend parentExtend = deptExtendMapper.selectDeptExtendByDeptId(parentId);
        if (parentExtend == null)
        {
            return "000";
        }

        String parentCode = parentExtend.getUnitCode();
        Integer maxSeq = deptExtendMapper.selectMaxSequenceNumByParentId(parentId);
        int nextSeq = (maxSeq == null ? 0 : maxSeq) + 1;

        return parentCode + String.format("%03d", nextSeq);
    }

    /**
     * 修改该部门的父级部门状态
     * 
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept)
    {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        deptMapper.updateDeptStatusNormal(deptIds);
    }

    /**
     * 修改子元素关系
     * 
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors)
    {
        List<SysDept> children = deptMapper.selectChildrenDeptById(deptId);
        for (SysDept child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     * 
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId)
    {
        return deptMapper.deleteDeptById(deptId);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t)
    {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t)
    {
        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext())
        {
            SysDept n = (SysDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t)
    {
        return getChildList(list, t).size() > 0;
    }
}

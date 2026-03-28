package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SysDeptExtend;

/**
 * 部门扩展 数据层
 *
 * @author ruoyi
 */
public interface SysDeptExtendMapper
{
    /**
     * 根据部门ID查询部门扩展信息
     *
     * @param deptId 部门ID
     * @return 部门扩展信息
     */
    public SysDeptExtend selectDeptExtendByDeptId(Long deptId);

    /**
     * 查询父部门下的最大序号
     *
     * @param parentId 父部门ID
     * @return 最大序号
     */
    public Integer selectMaxSequenceNumByParentId(Long parentId);

    /**
     * 新增部门扩展信息
     *
     * @param deptExtend 部门扩展信息
     * @return 结果
     */
    public int insertDeptExtend(SysDeptExtend deptExtend);

    /**
     * 修改部门扩展信息
     *
     * @param deptExtend 部门扩展信息
     * @return 结果
     */
    public int updateDeptExtend(SysDeptExtend deptExtend);

    /**
     * 根据部门ID删除部门扩展信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptExtendByDeptId(Long deptId);

    /**
     * 根据父单位编码查询所有子部门扩展信息
     *
     * @param parentUnitCode 父单位编码
     * @return 子部门扩展信息列表
     */
    public java.util.List<SysDeptExtend> selectDeptExtendByParentUnitCode(@Param("parentUnitCode") String parentUnitCode);
}

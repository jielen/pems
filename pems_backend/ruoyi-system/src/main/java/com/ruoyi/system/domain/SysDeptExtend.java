package com.ruoyi.system.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
/**
 * 部门扩展表 sys_dept_extend
 *
 * Extends sys_dept for multi-level unit hierarchy support:
 * - City level (1): e.g., "000" (provincial capital)
 * - District level (2): e.g., "000001" (district under city)
 * - Station level (3): e.g., "000001001" (police station under district)
 *
 * Unit code format: parent_code + 3-digit sequence
 *
 * @author ruoyi
 */
@TableName("sys_dept_extend")
public class SysDeptExtend extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 部门ID（关联sys_dept） */
    @TableId(value = "dept_id")
    private Long deptId;

    /** 单位编码（唯一，如000/000001/000001001） */
    private String unitCode;

    /** 单位层级：1=市级 2=区县 3=派出所 */
    private Integer deptLevel;

    /** 上级单位编码 */
    private String parentUnitCode;

    /** 同级单位内的序号（3位数字） */
    private Integer sequenceNum;

    /** 祖先单位编码路径（用于DataScope查询） */
    private String ancestorsPath;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public Integer getDeptLevel() {
        return deptLevel;
    }

    public void setDeptLevel(Integer deptLevel) {
        this.deptLevel = deptLevel;
    }

    public String getParentUnitCode() {
        return parentUnitCode;
    }

    public void setParentUnitCode(String parentUnitCode) {
        this.parentUnitCode = parentUnitCode;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getAncestorsPath() {
        return ancestorsPath;
    }

    public void setAncestorsPath(String ancestorsPath) {
        this.ancestorsPath = ancestorsPath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "SysDeptExtend{" +
            "deptId=" + deptId +
            ", unitCode='" + unitCode + "'" +
            ", deptLevel=" + deptLevel +
            ", parentUnitCode='" + parentUnitCode + "'" +
            ", sequenceNum=" + sequenceNum +
            ", ancestorsPath='" + ancestorsPath + "'" +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            "}";
    }
}

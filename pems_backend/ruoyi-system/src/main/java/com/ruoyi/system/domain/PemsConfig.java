package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 物证系统配置表 pems_config
 *
 * @author ruoyi
 */
@TableName("pems_config")
public class PemsConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 配置键（唯一） */
    @NotBlank(message = "配置键不能为空")
    @Size(min = 0, max = 100, message = "配置键长度不能超过100个字符")
    private String paramKey;

    /** 配置值 */
    private String paramValue;

    /** 配置类型：string/number/boolean/json */
    @Size(min = 0, max = 50, message = "配置类型长度不能超过50个字符")
    private String paramType;

    /** 备注说明 */
    @Size(min = 0, max = 255, message = "备注长度不能超过255个字符")
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        return "PemsConfig{" +
            "id=" + id +
            ", paramKey='" + paramKey + "'" +
            ", paramValue='" + paramValue + "'" +
            ", paramType='" + paramType + "'" +
            ", remark='" + remark + "'" +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            "}";
    }
}

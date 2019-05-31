package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zwj on 2019/1/3.
 */
@Entity
@Table(name = "tbl_lease_template")
public class LeaseTemplate extends BaseEntity {
    @Column(name = "template_name", columnDefinition = "varchar(20) COMMENT '模板名称'")
    private String templateName;
    @Column(name = "instance_id", columnDefinition = "varchar(32) COMMENT '实例id'")
    private String instanceId;
    @Column(name = "content", columnDefinition = "text COMMENT '模板内容'")
    private String content;
    @Column(name = "template_no", columnDefinition = "text COMMENT '模板编号'")
    private String templateNo;
    @Column(name = "status", columnDefinition = "text COMMENT '模板状态 0-审核中  1-审核通过  2-审核驳回'")
    private String status;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

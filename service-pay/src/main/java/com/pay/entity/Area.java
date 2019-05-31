package com.pay.entity;

import com.omv.database.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by MING on 2018/10/29.
 * Description:
 */
@Entity
@Table(name = "area")
public class Area extends BaseEntity {

    @Column(name = "area_id", columnDefinition = "varchar(10) COMMENT '区域ID'")
    private int areaId;
    @Column(name = "area_name", columnDefinition = "varchar(50) COMMENT '区域名称'")
    private String areaName;
    @Column(name = "parent_id", columnDefinition = "varchar(10) COMMENT '区域父ID'")
    private int parentId;

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}

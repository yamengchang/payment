package com.pay.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by zwj on 2018/10/25.
 * 支付平台用户表
 */
//@Entity
//@Table(name = "tbl_user")
public class PlatformUser {
    @Column(columnDefinition = "varchar(15) COMMENT '真实姓名'")
    private String realName;
    @Column(columnDefinition = "varchar(15) COMMENT '昵称'")
    private String nickName;
    @Column(columnDefinition = "varchar(13) COMMENT '手机号'")
    private String phone;
    @Column(columnDefinition = "varchar(15) COMMENT '用户名'")
    private String userName;
    @Column(columnDefinition = "varchar(50) COMMENT '邮箱'")
    private String mail;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

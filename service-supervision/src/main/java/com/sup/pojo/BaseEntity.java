package com.sup.pojo;


import com.omv.common.util.basic.*;
import com.omv.common.util.error.*;
import io.swagger.annotations.*;
import org.springframework.data.annotation.*;

import java.io.*;
import java.util.*;

/**
 * Created by WANG, RUIQING on 12/19/16
 * Twitter : @taylorwang789
 * E-mail : i@wrqzn.com
 */
public class BaseEntity<T> implements Serializable {

	@Id
	@ApiModelProperty(hidden = true)
	protected String id;
	@ApiModelProperty(hidden = true)
	protected Date createTime;
	@ApiModelProperty(hidden = true)
	protected Date lastUpdateTime;

	public Class<?> get() {
		return this.getClass();
	}


	public BaseEntity() {
		try {
			this.id = IDUtil.getID();
		}catch (CustomException e) {
			e.printStackTrace();
		}
		this.createTime = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}

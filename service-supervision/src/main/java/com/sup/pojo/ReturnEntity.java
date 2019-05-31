package com.sup.pojo;

/**
 * Created by zwj on 2019/1/8.
 */
public class ReturnEntity {
	private static final String SUCCESS = "success";
	private static final String FAILED = "failed";
	private String id;
	private String thirdNo;
	private String status;
	private String msg;

	public ReturnEntity(String id, String thirdNo, String status, String msg) {
		this.id = id;
		this.thirdNo = thirdNo;
		this.status = status;
		this.msg = msg;
	}

	public static ReturnEntity success(String id, String thirdNo) {
		return new ReturnEntity(id,thirdNo,SUCCESS,"成功");
	}

	public static ReturnEntity error(String id, String thirdNo,String msg) {
		return new ReturnEntity(id,thirdNo,FAILED,msg);
	}

	private static class SingletonClassInstance {
		private static final ReturnEntity INSTANCE = new ReturnEntity();
	}

	private ReturnEntity() {
	}

	public String getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public String getThirdNo() {
		return thirdNo;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setThirdNo(String thirdNo) {
		this.thirdNo = thirdNo;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	private ReturnEntity thirdNo(String thirdNo) {
		this.thirdNo = thirdNo;
		return this;
	}

	private ReturnEntity id(String id) {
		this.id = id;
		return this;
	}

	private ReturnEntity msg(String msg) {
		this.msg = msg;
		return this;
	}

	private ReturnEntity status(String status) {
		this.status = status;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

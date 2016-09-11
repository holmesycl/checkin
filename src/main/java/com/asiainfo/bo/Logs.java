package com.asiainfo.bo;

import java.io.Serializable;
import java.util.Date;

public class Logs implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8478875952934874675L;

	private long logId;

	private String username;

	private Date createDate;

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}

package com.huawei.sharedrive.isystem.initConfig.domain;

import java.util.List;

public class BmsUser {

//	private List<String> roles;
//
//
//
//	public List<String> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(List<String> roles) {
//		this.roles = roles;
//	}

	private String loginName;

	private String name;

	private String email;

	private String noteDesc;
	
	private byte type;

	
//	 public String getRoleNames() { StringBuilder sb = new StringBuilder(); if
//	 (roles != null && !roles.isEmpty()) { for (AdminRole role : roles) {
//	 sb.append(role.name()).append(','); } sb.deleteCharAt(sb.length() - 1); }
//	 return sb.toString(); }
	 

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNoteDesc() {
		return noteDesc;
	}

	public void setNoteDesc(String noteDesc) {
		this.noteDesc = noteDesc;
	}

}

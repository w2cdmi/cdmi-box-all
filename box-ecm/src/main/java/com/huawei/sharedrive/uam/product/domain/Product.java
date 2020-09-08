package com.huawei.sharedrive.uam.product.domain;


public class Product {
	
	public static byte TYPE_PERSON = 1;
	public static byte TYPE_ENTERPRISE = 2;

	private long id;

	private String name;
	
	private String companyName;
	//1.个人购买  2. 企业购买
	private byte type;

	private int accountNum;

	private long accountSpace;

	private int teamNum;

	private long teamSpace;

	private String introduce;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public int getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(int accountNum) {
		this.accountNum = accountNum;
	}

	public long getAccountSpace() {
		return accountSpace;
	}

	public void setAccountSpace(long accountSpace) {
		this.accountSpace = accountSpace;
	}

	public int getTeamNum() {
		return teamNum;
	}

	public void setTeamNum(int teamNum) {
		this.teamNum = teamNum;
	}

	public long getTeamSpace() {
		return teamSpace;
	}

	public void setTeamSpace(long teamSpace) {
		this.teamSpace = teamSpace;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}

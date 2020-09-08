package com.huawei.sharedrive.uam.weixin.domain;

public class ShareLevel {

	private int id;
	private long startRange;
	private long endRange;
	private String name;
	private String iconUrl;
	private String description;
	private float proportions;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getStartRange() {
		return startRange;
	}

	public void setStartRange(long startRange) {
		this.startRange = startRange;
	}

	public long getEndRange() {
		return endRange;
	}

	public void setEndRange(long endRange) {
		this.endRange = endRange;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getProportions() {
		return proportions;
	}

	public void setProportions(float proportions) {
		this.proportions = proportions;
	}

}

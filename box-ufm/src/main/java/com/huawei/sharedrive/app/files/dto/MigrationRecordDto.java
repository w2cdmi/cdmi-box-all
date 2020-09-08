package com.huawei.sharedrive.app.files.dto;

import java.util.Date;

/**
 * 移交记录
 * @author 77235
 *
 */
public class MigrationRecordDto {

	private long id;

	private long enterpriseId;

	private int migrationType;

	private Long departureUserId;

	private Long recipientUserId;

	private int migrationStatus;

	private Date migrationDate = new Date();

	private Date expiredDate;

	private long inodeId;

	private String appId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public int getMigrationType() {
		return migrationType;
	}

	public void setMigrationType(int migrationType) {
		this.migrationType = migrationType;
	}

	public Long getDepartureUserId() {
		return departureUserId;
	}

	public void setDepartureUserId(Long departureUserId) {
		this.departureUserId = departureUserId;
	}

	public Long getRecipientUserId() {
		return recipientUserId;
	}

	public void setRecipientUserId(Long recipientUserId) {
		this.recipientUserId = recipientUserId;
	}

	public int getMigrationStatus() {
		return migrationStatus;
	}

	public void setMigrationStatus(int migrationStatus) {
		this.migrationStatus = migrationStatus;
	}

	public Date getMigrationDate() {
		return migrationDate;
	}

	public void setMigrationDate(Date migrationDate) {
		this.migrationDate = migrationDate;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public long getInodeId() {
		return inodeId;
	}

	public void setInodeId(long inodeId) {
		this.inodeId = inodeId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
}

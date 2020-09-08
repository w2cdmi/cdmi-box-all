package com.huawei.sharedrive.uam.enterpriseuser.domain;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * Desc  : 数据迁移记录
 * Author: 77235
 * Date	 : 2016年12月23日
 */
public class MigrationRecord implements Serializable{
    // 移交状态：0 初始移交失败，  1 初始移交成功， 2 数据清理成功， 3 数据清理失败 
	public static final byte CONST_MIGRATION_TYPE_INIT_SUCCESS = (byte)0;
	
	public static final byte CONST_MIGRATION_TYPE_INIT_FAILED = (byte)1;
	
	public static final byte CONST_MIGRATION_TYPE_CLEAN_SUCCESS = (byte)2;
	
	public static final byte CONST_MIGRATION_TYPE_CLEAN_FAIL = (byte)3;
	
    private static final long serialVersionUID = 1L;
    
    private long id;
    
    private long enterpriseId;
    
    private int migrationType;
    
    private Long departureCloudUserId;
    
    private Long departureUserId;
    
    private Long recipientCloudUserId;
    
    private Long recipientUserId;
    
    private int migrationStatus;
    
    private Date migrationDate = new Date();
    
    private Date expiredDate;
    
    private Date clearDate;
    
    private long inodeId;
    
    private String appId;
    
    private String tableSuffix;

    public MigrationRecord() {
        super();
    }

	public MigrationRecord(long enterpriseId, long departureCloudUserId, int migrationType, int migrationStatus) {
		super();
		this.enterpriseId = enterpriseId;
		this.departureCloudUserId = departureCloudUserId;
		this.migrationType = migrationType;
		this.migrationStatus = migrationStatus;
	}

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

    public long getInodeId() {
        return inodeId;
    }

    public void setInodeId(long inodeId) {
        this.inodeId = inodeId;
    }

    public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Date getClearDate() {
		return clearDate;
	}

	public void setClearDate(Date clearDate) {
		this.clearDate = clearDate;
	}

	public Long getDepartureCloudUserId() {
		return departureCloudUserId;
	}

	public void setDepartureCloudUserId(Long departureCloudUserId) {
		this.departureCloudUserId = departureCloudUserId;
	}

	public Long getRecipientCloudUserId() {
		return recipientCloudUserId;
	}

	public void setRecipientCloudUserId(Long recipientCloudUserId) {
		this.recipientCloudUserId = recipientCloudUserId;
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
}

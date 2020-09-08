package com.huawei.sharedrive.app.files.dto;

import java.io.Serializable;

/**
 * 
 * Desc  : 传输数据信息 
 * Author: 77235
 * Date	 : 2016年12月23日
 */
public class DataMigrationRequestDto implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final int CONST_MIGRATION_TO_NEW_USER = 1;
    
    public static final int CONST_MIGRATION_TO_EXIST_USER = 2;

    /** 迁移方式 */
    private int migrationType;
    
    /** 企业账户信息 */
    private long enterpriseId;
    
    private long accountId;
    
    /** 迁移给全新用户信息 */
    private String name;
    
    private String alias;
    
    private String email;
    
    private String mobile;
    
    private String description;
    
    /** 接受用户的企业用户编号 */
    private long recipientUserId;
    /** 接受用户的虚拟用户编号 */
    private long recipientCloudUserId;
    
    /** 离职用户的企业用户编号 */
    private long departureUserId;
    
    private long departureCloudUserId;
    
    private String authServerId;
    
    private String appIds;
    
    private String recipientUserName;
    
    private String departureUserName;


    public int getMigrationType() {
        return migrationType;
    }

    public void setMigrationType(int migrationType) {
        this.migrationType = migrationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public long getRecipientCloudUserId() {
        return recipientCloudUserId;
    }

    public void setRecipientCloudUserId(long recipientCloudUserId) {
        this.recipientCloudUserId = recipientCloudUserId;
    }

    public long getDepartureUserId() {
        return departureUserId;
    }

    public void setDepartureUserId(long departureUserId) {
        this.departureUserId = departureUserId;
    }

    public long getDepartureCloudUserId() {
        return departureCloudUserId;
    }

    public void setDepartureCloudUserId(long departureCloudUserId) {
        this.departureCloudUserId = departureCloudUserId;
    }

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthServerId() {
        return authServerId;
    }

    public void setAuthServerId(String authServerId) {
        this.authServerId = authServerId;
    }

    public String getAppIds() {
        return appIds;
    }

    public void setAppIds(String appIds) {
        this.appIds = appIds;
    }

    public String getRecipientUserName() {
        return recipientUserName;
    }

    public void setRecipientUserName(String recipientUserName) {
        this.recipientUserName = recipientUserName;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getDepartureUserName() {
        return departureUserName;
    }

    public void setDepartureUserName(String departureUserName) {
        this.departureUserName = departureUserName;
    }
}

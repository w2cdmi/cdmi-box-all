package com.huawei.sharedrive.app.files.dto;

/**
 * 
 * Desc  : 数据迁移传输对象 
 * Author: 77235
 * Date	 : 2016年12月24日
 */
public class MigrationRequestDto {
    private String authAppId;
    
    private long departureCloudUserId;

    private long recipientId;
    
    private String recipientName;
    
    private String recipientEmail;
    
    private String recipientLoginName;


    public String getAuthAppId() {
        return authAppId;
    }

    public void setAuthAppId(String authAppId) {
        this.authAppId = authAppId;
    }

    public long getDepartureCloudUserId() {
        return departureCloudUserId;
    }

    public void setDepartureCloudUserId(long departureCloudUserId) {
        this.departureCloudUserId = departureCloudUserId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientLoginName() {
        return recipientLoginName;
    }

    public void setRecipientLoginName(String recipientLoginName) {
        this.recipientLoginName = recipientLoginName;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }
    
    
}

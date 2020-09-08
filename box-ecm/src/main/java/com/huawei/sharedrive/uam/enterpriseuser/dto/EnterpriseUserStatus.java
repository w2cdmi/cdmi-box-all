package com.huawei.sharedrive.uam.enterpriseuser.dto;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Desc  : 企业用户状态
 * Author: 77235
 * Date	 : 2016年12月23日
 */
public enum EnterpriseUserStatus {
    NORMAL("normal", 0), /** 正常 */
    BE_ENTRY("beingEntry", 9), /** 待入职 */
    ON_TRIAL("inTrial", 8), /** 试用中 */
    LEAVE_APPROVAL("leaveProcessing", 7), /** 离职审批中 */
    DATA_MIGRATED("dataMigrated", 6), /** 数据已迁移 */
    ACCOUNT_MIGRATED("accountMigrated", 5), /** 账号已迁移 */
    DISMISS("dismiss", -2), /** 标记离职 */
    HANDOVER("handover", -3), /** 文件已移交 */
    HAS_LEFT("hasLeft", 4), /** 已离职 */
    RETRIED("retried", 3); /** 已退休 */

    // 待入职，实习试用，正常，离职审批，已离职，已退休,新增冻结状态
    private int statusCode;
    
    private String statusDesc;

    EnterpriseUserStatus(String statusDesc, int statusCode){
        this.statusCode = statusCode;
        this.statusDesc = statusDesc;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

	/**
     * 获取企业用户状态信息
     * @param status
     * @return
     */
    public static EnterpriseUserStatus getEnterpriseUserStatus(String status){
        for (EnterpriseUserStatus userStatue : EnterpriseUserStatus.values()){
            if (StringUtils.equalsIgnoreCase(userStatue.getStatusDesc(), status)){
                return userStatue;
            }
        }
     
       throw new RuntimeException("levaemanagement.invalid.userstatus");
    }
}

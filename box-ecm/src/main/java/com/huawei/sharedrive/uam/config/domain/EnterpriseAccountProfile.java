
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.config.domain;

/************************************************************
 * @Description:
 * <pre>企业账号开户默认配额</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2018/2/10
 ************************************************************/
public class EnterpriseAccountProfile {
    //个人账户数量
    private int maxUserAmount = 20; //20个人账户数量

    //默认团队空间数量
    private int maxTeamspaceAmount = 1000; //默认1000个团队空间

    //企业总容量（所有该企业下的账户占用的空间容量）, 企业总容量 = 个人限额*个人账户数量 + 共享空间容量。
    private long maxTeamspaceQuota = -1; //不限制企业总容量

    //个人账号最大容量
    private long maxUserQuota = 1048576 * 200; //默认200M

    //企业共享容量
    private long maxShareQuota = 1048576 * 500; //默认500M

    public int getMaxUserAmount() {
        return maxUserAmount;
    }

    public void setMaxUserAmount(int maxMember) {
        this.maxUserAmount = maxMember;
    }

    public int getMaxTeamspaceAmount() {
        return maxTeamspaceAmount;
    }

    public void setMaxTeamspaceAmount(int maxTeamspace) {
        this.maxTeamspaceAmount = maxTeamspace;
    }

    public long getMaxTeamspaceQuota() {
        return maxTeamspaceQuota;
    }

    public void setMaxTeamspaceQuota(long maxSpace) {
        this.maxTeamspaceQuota = maxSpace;
    }

    public long getMaxUserQuota() {
        return maxUserQuota;
    }

    public void setMaxUserQuota(long maxUserQuota) {
        this.maxUserQuota = maxUserQuota;
    }

    public long getMaxShareQuota() {
        return maxShareQuota;
    }

    public void setMaxShareQuota(long maxShareQuota) {
        this.maxShareQuota = maxShareQuota;
    }
}

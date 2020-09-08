/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.rest;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>授权的企业信息</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/28
 ************************************************************/
public class AuthCorpInfo {
    String corpid;
    String corpName;
    String corpType;
    String corpSquareLogoUrl;
    Integer corpUserMax;
    Integer corpAgentMax;
    String corpFullName;
    Long verifiedEndTime;
    Integer subjectType;
    String corpWxqrcode;

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getCorpType() {
        return corpType;
    }

    public void setCorpType(String corpType) {
        this.corpType = corpType;
    }

    public String getCorpSquareLogoUrl() {
        return corpSquareLogoUrl;
    }

    public void setCorpSquareLogoUrl(String corpSquareLogoUrl) {
        this.corpSquareLogoUrl = corpSquareLogoUrl;
    }

    public Integer getCorpUserMax() {
        return corpUserMax;
    }

    public void setCorpUserMax(Integer corpUserMax) {
        this.corpUserMax = corpUserMax;
    }

    public Integer getCorpAgentMax() {
        return corpAgentMax;
    }

    public void setCorpAgentMax(Integer corpAgentMax) {
        this.corpAgentMax = corpAgentMax;
    }

    public String getCorpFullName() {
        return corpFullName;
    }

    public void setCorpFullName(String corpFullName) {
        this.corpFullName = corpFullName;
    }

    public Long getVerifiedEndTime() {
        return verifiedEndTime;
    }

    public void setVerifiedEndTime(Long verifiedEndTime) {
        this.verifiedEndTime = verifiedEndTime;
    }

    public Integer getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(Integer subjectType) {
        this.subjectType = subjectType;
    }

    public String getCorpWxqrcode() {
        return corpWxqrcode;
    }

    public void setCorpWxqrcode(String corpWxqrcode) {
        this.corpWxqrcode = corpWxqrcode;
    }
}

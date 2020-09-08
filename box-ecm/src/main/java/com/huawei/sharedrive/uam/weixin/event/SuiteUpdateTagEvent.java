package com.huawei.sharedrive.uam.weixin.event;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>标签变更事件</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public class SuiteUpdateTagEvent extends WxSuiteEvent {
    private String authCorpId;

    private Integer tagId;

    private String addUserItems;

    private String delUserItems;

    private String addPartyItems;

    private String delPartyItems;

    public String getAuthCorpId() {
        return authCorpId;
    }

    public void setAuthCorpId(String authCorpId) {
        this.authCorpId = authCorpId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getAddUserItems() {
        return addUserItems;
    }

    public void setAddUserItems(String addUserItems) {
        this.addUserItems = addUserItems;
    }

    public String getDelUserItems() {
        return delUserItems;
    }

    public void setDelUserItems(String delUserItems) {
        this.delUserItems = delUserItems;
    }

    public String getAddPartyItems() {
        return addPartyItems;
    }

    public void setAddPartyItems(String addPartyItems) {
        this.addPartyItems = addPartyItems;
    }

    public String getDelPartyItems() {
        return delPartyItems;
    }

    public void setDelPartyItems(String delPartyItems) {
        this.delPartyItems = delPartyItems;
    }
}

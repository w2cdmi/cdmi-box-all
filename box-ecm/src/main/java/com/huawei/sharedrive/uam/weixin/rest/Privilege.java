/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.rest;

import java.util.List;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>权限</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/28
 ************************************************************/
public class Privilege {
    Integer level;
    List<Integer> allowParty;
    List<String> allowUser;
    List<Integer> allowTag;
    List<Integer> extraParty;
    List<String> extraUser;
    List<Integer> extraTag;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Integer> getAllowParty() {
        return allowParty;
    }

    public void setAllowParty(List<Integer> allowParty) {
        this.allowParty = allowParty;
    }

    public List<String> getAllowUser() {
        return allowUser;
    }

    public void setAllowUser(List<String> allowUser) {
        this.allowUser = allowUser;
    }

    public List<Integer> getAllowTag() {
        return allowTag;
    }

    public void setAllowTag(List<Integer> allowTag) {
        this.allowTag = allowTag;
    }

    public List<Integer> getExtraParty() {
        return extraParty;
    }

    public void setExtraParty(List<Integer> extraParty) {
        this.extraParty = extraParty;
    }

    public List<String> getExtraUser() {
        return extraUser;
    }

    public void setExtraUser(List<String> extraUser) {
        this.extraUser = extraUser;
    }

    public List<Integer> getExtraTag() {
        return extraTag;
    }

    public void setExtraTag(List<Integer> extraTag) {
        this.extraTag = extraTag;
    }
}

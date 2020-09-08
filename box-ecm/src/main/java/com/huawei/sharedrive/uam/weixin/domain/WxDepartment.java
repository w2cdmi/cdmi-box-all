package com.huawei.sharedrive.uam.weixin.domain;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import java.util.Date;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>企业微信中的部门信息</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public class WxDepartment {
    public final static int STATE_NOT_SYNC = -1;
    public final static int STATE_DISABLE = -1;
    public final static int STATE_ENABLE = 1;
    Integer id;
    String corpId;
    Integer parentId;
    String name;
    Integer order;
    Integer state;
    Long boxEnterpriseId;
    Long boxDepartmentId;
    Date createdAt;
    Date modifiedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getBoxEnterpriseId() {
        return boxEnterpriseId;
    }

    public void setBoxEnterpriseId(Long boxEnterpriseId) {
        this.boxEnterpriseId = boxEnterpriseId;
    }

    public Long getBoxDepartmentId() {
        return boxDepartmentId;
    }

    public void setBoxDepartmentId(Long boxDepartmentId) {
        this.boxDepartmentId = boxDepartmentId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}

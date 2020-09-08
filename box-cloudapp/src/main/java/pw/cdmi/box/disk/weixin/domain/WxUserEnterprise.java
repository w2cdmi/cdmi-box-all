package pw.cdmi.box.disk.weixin.domain;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>记录微信用户在系统内对应的企业和用户信息，一个微信用户可能会属于多个公司</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public class WxUserEnterprise {
    String unionId;
    Long enterpriseId;
    Long enterpriseUserId;

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getEnterpriseUserId() {
        return enterpriseUserId;
    }

    public void setEnterpriseUserId(Long enterpriseUserId) {
        this.enterpriseUserId = enterpriseUserId;
    }
}

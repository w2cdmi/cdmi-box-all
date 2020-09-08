package pw.cdmi.box.disk.httpclient.rest.request;

import pw.cdmi.box.disk.httpclient.rest.common.EnterpriseInfo;

import java.io.Serializable;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>以个人身份登录时的响应，可能会存在多个企业用户，需要用户选择登录哪家企业</pre>
 * @Project Alpha CDMI Service Platform, box-cloudapp-web Component. 2017/11/21
 ************************************************************/
public class RestUserLoginResponse extends RestLoginResponse {
    EnterpriseInfo[] enterpriseList;

    public EnterpriseInfo[] getEnterpriseList() {
        return enterpriseList;
    }

    public void setEnterpriseList(EnterpriseInfo[] enterpriseList) {
        this.enterpriseList = enterpriseList;
    }
}

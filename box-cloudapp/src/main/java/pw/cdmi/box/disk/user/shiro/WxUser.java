/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.box.disk.user.shiro;

import pw.cdmi.box.disk.httpclient.rest.common.EnterpriseInfo;
import pw.cdmi.box.disk.oauth2.domain.UserToken;

/************************************************************
 * @Description:
 * <pre>微信用户登录信息</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-cloudapp-web Component. 2017/12/2
 ************************************************************/
public class WxUser extends UserToken {
    //微信用户临时token，用于从微信服务器获取用户信息
    private String code;

    //单个微信用户对应的企业账户列表
    private EnterpriseInfo[] enterpriseList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EnterpriseInfo[] getEnterpriseList() {
        return enterpriseList;
    }

    public void setEnterpriseList(EnterpriseInfo[] enterpriseList) {
        this.enterpriseList = enterpriseList;
    }
}

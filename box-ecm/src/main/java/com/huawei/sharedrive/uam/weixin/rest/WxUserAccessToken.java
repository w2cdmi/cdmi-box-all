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
 * @Description: <pre>通过Code获取的用户信息</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/11/15
 ************************************************************/
public class WxUserAccessToken extends WxApiResponse {
    //接口调用凭证
    private String accessToken;

    //access_token接口调用凭证超时时间，单位（秒）
    private Integer expiresIn;

    //用户刷新access_token
    private String refreshToken;

    //授权用户唯一标识
    private String openid;

    //用户授权的作用域，使用逗号（,）分隔
    private String scope;

    //当且仅当该网站应用已获得该用户的userinfo授权时，才会出现该字段。
    private String unionid;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}

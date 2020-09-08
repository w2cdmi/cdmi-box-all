/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.box.disk.weixin.service.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import pw.cdmi.box.disk.weixin.service.WeixinOauth2Service;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.utils.JsonUtils;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信侧OAuth2接口实现</pre>
 * @Project Alpha CDMI Service Platform, box-weixin Component. 2017/8/15
 ************************************************************/
@Component
public class WeixinOauth2ServiceImpl implements WeixinOauth2Service {
    private static Logger logger = LoggerFactory.getLogger(WeixinOauth2ServiceImpl.class);

    private String corpId = "wwba09b5d7931f8d7e";
    private String appId = "1000006";
    private String secretKey= "TL3w6h5dFsN_ytlP1Vle5xmyFLHS0rMjPfdwAq7F0l8";

    private CacheClient cacheClient;

    public String  getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public CacheClient getCacheClient() {
        return cacheClient;
    }

    public void setCacheClient(CacheClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    @Override
    public String getAccessToken() {
        String key = "Weixin_accessToken";
        String token = (String)cacheClient.getCache(key);
        if(token == null) {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + getCorpId() + "&corpsecret=" + getSecretKey();
            try {
                //发送请求，查询access key.
                String body = httpGet(url);
                Map map = JsonUtils.stringToMap(body);

                Integer code = (Integer)map.get("errcode");
                if(code == null || code == 0) {
                    token = (String)map.get("access_token");
                    Integer expires = (Integer)map.get("expires_in");
                    cacheClient.addCache(key, token, expires * 1000);
                } else {
                    //fail
                    String err = (String)map.get("errmsg");
                    logger.error("Can't Get The Access Token Info From Weixin: {}", err);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return token;
    }

    @Override
    public String getJsApiTicket() {
        String key = "Weixin_JsApiTicket";
        String ticket = (String)cacheClient.getCache(key);
        if(ticket == null) {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + getAccessToken();
            try {
                //发送请求，查询access key.
                String body = httpGet(url);
                Map map = JsonUtils.stringToMap(body);

                Integer code = (Integer)map.get("errcode");
                if(code == null || code == 0) {
                    ticket = (String)map.get("ticket");
                    Integer expires = (Integer)map.get("expires_in");
                    cacheClient.addCache(key, ticket, expires * 1000);
                } else {
                    //fail
                    String err = (String)map.get("errmsg");
                    logger.error("Can't Get The JS API Ticket From Weixin: {}", err);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ticket;
    }

    protected String httpGet(String url) throws IOException {
        HttpMethod post = new GetMethod(url);

        HttpClient client = new HttpClient();
        client.executeMethod(post);

        return post.getResponseBodyAsString();
    }
}

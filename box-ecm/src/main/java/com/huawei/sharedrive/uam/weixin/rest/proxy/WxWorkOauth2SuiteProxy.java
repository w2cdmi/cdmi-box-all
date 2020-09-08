package com.huawei.sharedrive.uam.weixin.rest.proxy;
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-${year} 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-${year} www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import java.util.HashMap;
import java.util.Map;

import com.huawei.sharedrive.uam.system.service.SystemConfigService;
import com.huawei.sharedrive.uam.weixin.rest.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.core.web.JsonMapper;
import com.huawei.sharedrive.uam.weixin.dao.WxEnterpriseDao;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.util.HttpClientUtils;

import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.domain.SystemConfig;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:
 * <pre>企业微信中，作为第三方服务商，与企业微信后台相关的功能接口</pre>
 * @Project Alpha CDMI Service Platform, ${project_name} Component. ${date}
 ************************************************************/
@Component
public class WxWorkOauth2SuiteProxy {
    private static final Logger logger = LoggerFactory.getLogger(WxWorkOauth2SuiteProxy.class);

    private static String SUITE_TICKET_KEY = "Suite_ticket";
    private static String CORP_ACCESS_TOKEN_KEY = "Corp_accessToken";
    private static String CORP_PERMANENT_CODE_KEY = "Corp_permanentCode";

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private WxEnterpriseDao enterpriseDao;

    @Autowired
    private SystemConfigService systemConfigService;

    private String corpId = "wwc7342fa63c523b9a";
    private String providerSecret = "iT75OHg3CvqMpdULN0QgHW1UflaESKrTkCIafwXPrjg";
    private String suiteId = "tje32d93de35487681";
    private String suiteSecret = "Yr1gUi7pk3uAd1bR-_GRGRIxMx63tjMoubUTwYlvLDs";

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getProviderSecret() {
        return providerSecret;
    }

    public void setProviderSecret(String providerSecret) {
        this.providerSecret = providerSecret;
    }

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    public String getSuiteSecret() {
        return suiteSecret;
    }

    public void setSuiteSecret(String suiteSecret) {
        this.suiteSecret = suiteSecret;
    }

    /**
     * 获取服务商的凭证。以corpid、provider_secret换取provider_access_token
     * @return 服务商凭证
     */
    public ProviderAccessTokenInfo getProviderAccessToken() {
        Map<String, String> map = new HashMap<>();
        map.put("corpid", corpId);
        map.put("provider_secret", providerSecret);

        logger.debug("parameters: corpId={}, provider_secret={}", corpId, providerSecret);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token", map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, ProviderAccessTokenInfo.class);
    }

    /**
     * 用于根据注册模板生成注册码
     * @return 服务商凭证
     */
    public RegisterCodeInfo getRegisterCodeInfo(String providerAccessToken, String templateId) {
        Map<String, String> map = new HashMap<>();
        map.put("template_id", templateId);

        logger.debug("parameters: template_id={}, provider_access_token={}", templateId, providerAccessToken);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_register_code?provider_access_token=" + providerAccessToken, map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, RegisterCodeInfo.class);
    }

    /**
     * 获取应用套件的ticket。ticket由微信后台推送，无法主动获取。
     * @return 应用套件ticket
     */
    public String getSuiteTicket() {
        String suiteTicket = null;
        try {
            suiteTicket = (String)cacheClient.getCache(SUITE_TICKET_KEY);
        } catch (Exception e) {
            logger.error("failed to take ticket from cache: ", e);
//            e.printStackTrace();
        }

        SystemConfig config = systemConfigService.getSystemConfig("suite.ticket", "WxWork");
        if(config != null) {
            suiteTicket = config.getValue();
        }

        return suiteTicket;
    }

    /**
     * 将应用套件的ticket放入的缓存。此方法主要被事件处理流程调用(微信后台每10分钟推送1次)。
     * @param suiteTicket 应用套件获取令牌时需要的ticket
     */
    public void setSuiteTicket(String suiteTicket) {
        cacheClient.setCacheNoExpire(SUITE_TICKET_KEY, suiteTicket);

        //保存到数据库，以防memcache失效系统无法正常工作。
        SystemConfig config = new SystemConfig();
        config.setAppId("WxWork");
        config.setId("suite.ticket");
        config.setValue(suiteTicket);
        systemConfigService.saveSystemConfig(config);
    }

    /**
     * 获取应用套件的访问令牌。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @return 应用套件访问令牌
     */
    public String getSuiteToken() {
        String SUITE_ACCESS_TOKEN_KEY = "Suite_accessToken";
        String token = (String)cacheClient.getCache(SUITE_ACCESS_TOKEN_KEY);

        if(token == null) {
            String ticket = getSuiteTicket();
            if(ticket != null) {
                SuiteAccessTokenInfo tokenInfo = getSuiteToken(ticket);
                if(tokenInfo != null) {
                    if(tokenInfo.getErrcode() == null || tokenInfo.getErrcode() == 0) {
                        token = tokenInfo.getSuiteAccessToken();

                        cacheClient.setCache(SUITE_ACCESS_TOKEN_KEY, token, tokenInfo.getExpiresIn() * 1000);
                    } else {
                        logger.error("Failed to query suite token: errcode={}, errmsg={}", tokenInfo.getErrcode(), tokenInfo.getErrmsg());
                    }
                }
            } else {
                logger.error("Failed to query suite token: no suite ticket found.");
            }
        }
        return token;
    }

    /*
    * 获取应用套件令牌。
    * 由于第三方服务商可能托管了大量的企业，其安全问题造成的影响会更加严重，故API中除了合法来源IP校验之外，还额外增加了以下安全策略：
    * 获取suite_access_token时，还额外需要suite_ticket参数（请永远使用最新接收到的suite_ticket）
     * 请求方式：POST（HTTPS）
     * 请求地址： https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token
     * @param suiteId
     * @param suiteSecret 应用套件secret
     * @param suiteTicket 企业微信后台推送的ticket
     * @return 应用套件令牌
     */
    public SuiteAccessTokenInfo getSuiteToken(String suiteTicket) {
        Map<String, String> map = new HashMap<>();
        map.put("suite_id", suiteId);
        map.put("suite_secret", suiteSecret);
        map.put("suite_ticket", suiteTicket);

        logger.debug("parameters: suite_id={}, suite_secret={}, suite_ticket={}", suiteId, suiteSecret, suiteTicket);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token", map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, SuiteAccessTokenInfo.class);
    }

    /**
     * 获取授权方（企业）的永久授权码。企业授权后会通过事件处理流程加入到缓存，如果缓存中没有，说明未授权，返回null。
     * @param corpId 授权方corpid
     * @return 授权方（企业）的永久授权码
     */
    public String getCorpPermanentCode(String corpId) {
        String key = CORP_PERMANENT_CODE_KEY + "." + corpId;

        String code = (String)cacheClient.getCache(key);
        if(code == null) {
            WxEnterprise enterprise = enterpriseDao.get(corpId);
            if(enterprise != null) {
                code = enterprise.getPermanentCode();
                cacheClient.setCacheNoExpire(key, code);
            }
        }

        return code;
    }

    /**
     * 将授权方（企业）的永久授权码放入缓存中。此方法主要被事件处理流程调用。
     * @param corpId 授权方corpid
     * @param permanentCode 授权方永久授权码
     */
    public void setCorpPermanentCode(String corpId, String permanentCode) {
        cacheClient.setCacheNoExpire(CORP_PERMANENT_CODE_KEY + "." + corpId, permanentCode);
    }

    /**
     * 该API用于获取预授权码。预授权码用于企业授权时的第三方服务商安全验证。
     * 请求方式：POST（HTTPS）
     * 请求地址： https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=SUITE_ACCESS_TOKEN
     * @param suiteToken 应用套件令牌
     * @return 预授权码
     */
    public PreAuthCodeInfo getPreAuthCode(String suiteToken) {
        Map<String, String> map = new HashMap<>();
        map.put("suite_id", suiteId);

        logger.debug("parameters: suite_access_token={}, suite_id={}", suiteToken, suiteId);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_pre_auth_code?suite_access_token=" + suiteToken, map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, PreAuthCodeInfo.class);
    }

    /**
     * 该API用于使用临时授权码换取授权方的永久授权码，并换取授权信息、企业access_token，临时授权码一次有效。建议第三方优先以userid或手机号为主键、其次以email为主键来建立自己的管理员账号。
     * 请求方式：POST（HTTPS）
     * 请求地址： https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=SUITE_ACCESS_TOKEN
     *
     * @param suiteToken 应用套件令牌
     * @param authCode 临时授权码，会在授权成功时附加在redirect_uri中跳转回第三方服务商网站，或通过回调推送给服务商。长度为64至512个字节
     * @return 永久授权码
     */
    public PermanentCodeInfo getPermanentCode(String suiteToken, String authCode) {
        Map<String, String> map = new HashMap<>();
        map.put("suite_id", suiteId);
        map.put("auth_code", authCode);

        logger.debug("parameters: suite_id={}, auth_code={}, suite_access_token={}", suiteId, authCode, suiteToken);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=" + suiteToken, map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, PermanentCodeInfo.class);
    }

    /**
     * 该API用于通过永久授权码换取企业微信的授权信息。 永久code的获取，是通过临时授权码使用get_permanent_code 接口获取到的permanent_code。
     * 请求方式：POST（HTTPS）
     * 请求地址： https://qyapi.weixin.qq.com/cgi-bin/service/get_auth_info?suite_access_token=SUITE_ACCESS_TOKEN
     * @param suiteToken 应用套件令牌
     * @param corpId 授权方corpid
     * @param permanentCode 永久授权码，通过get_permanent_code获取
     * @return 授权信息
     */
    public CorpAuthInfo getAuthInfo(String suiteToken, String corpId, String permanentCode) {
        Map<String, String> map = new HashMap<>();
        map.put("suite_id", suiteId);
        map.put("auth_corpid", corpId);
        map.put("permanent_code", permanentCode);

        logger.debug("parameters: suite_id={}, auth_corpid={}, permanent_code={}, suite_access_token={}", suiteId, corpId, permanentCode, suiteToken);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_auth_info?suite_access_token=" + suiteToken, map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, CorpAuthInfo.class);
    }


    /**
     * 获取企业的访问令牌。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）access_token
     */
    public String getCorpToken(String corpId) {
        String key = CORP_ACCESS_TOKEN_KEY + "." + corpId;

        String token = (String)cacheClient.getCache(key);
        if(token == null) {
            String suiteToken = getSuiteToken();
            String permanentCode = getCorpPermanentCode(corpId);
            CorpAccessTokenInfo tokenInfo = getCorpToken(suiteToken, corpId, permanentCode);
            if(tokenInfo.getErrcode() == null || tokenInfo.getErrcode() == 0) {
                token = tokenInfo.getAccessToken();
                cacheClient.setCache(key, token, tokenInfo.getExpiresIn() * 1000);
            } else {
                logger.error("Failed to query corp token: corpId={}， errcode={}, errmsg={}", corpId, tokenInfo.getErrcode(), tokenInfo.getErrmsg());
            }
        }

        return token;
    }

    /**
     * 第三方服务商在取得企业的永久授权码并完成对企业应用的设置之后，便可以开始通过调用企业接口来运营这些应用。其中，调用企业接口所需的access_token获取方法如下。
     * 请求方式：POST（HTTPS）
     * 请求地址： https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=SUITE_ACCESS_TOKEN
     * @param suiteToken 应用套件令牌
     * @param corpId 授权方corpid
     * @param permanentCode 永久授权码，通过get_permanent_code获取   @return 授权方（企业）access_token
     */
    public CorpAccessTokenInfo getCorpToken(String suiteToken, String corpId, String permanentCode) {
        Map<String, String> map = new HashMap<>();
        map.put("suite_id", suiteId);
        map.put("auth_corpid", corpId);
        map.put("permanent_code", permanentCode);

        logger.debug("parameters: suite_id={}, auth_corpid={}, permanent_code={}, suite_access_token={}", suiteId, corpId, permanentCode, suiteToken);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=" + suiteToken, map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, CorpAccessTokenInfo.class);
    }

    /**
     * 获取设置应用套件的访问令牌。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @param corpToken 授权方access_token
     */
    public void setCorpToken(String corpId, String corpToken, int expire) {
        String CORP_ACCESS_TOKEN_KEY = "Corp_accessToken";
        cacheClient.setCache(CORP_ACCESS_TOKEN_KEY + "." + corpId, corpToken, expire * 1000);
    }

    /**
     * 该接口可对某次授权进行配置。可支持测试模式（套件未发布时）；可设置哪些应用可以授权，不调用则默认允许所有应用进行授权。
     * @param suiteToken 应用套件令牌
     * @param preAuthCode 预授权码
     * @param appList 允许进行授权的应用id，如1、2、3， 不填或者填空数组都表示允许授权套件内所有应用
     * @param authType  	授权类型：0 正式授权， 1 测试授权， 默认值为0
     * @return 调用结果
     */
    public WxApiResponse setSessionInfo(String suiteToken, String preAuthCode, int[] appList, int authType) {
        Map<String, Object> map = new HashMap<>();
        map.put("pre_auth_code", preAuthCode);

        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put("appid", appList);
        sessionInfo.put("auth_type", authType);
        map.put("session_info", sessionInfo);

        logger.debug("parameters: pre_auth_code={}, appid={}, auth_type={}, suite_access_token={}", preAuthCode, appList, authType, suiteToken);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/set_session_info?suite_access_token=" + suiteToken, map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, WxApiResponse.class);
    }

    /**
     * 根据code获取成员信息
     * @param corpId 公司corpId
     * @param code 通过成员授权获取到的code，最大为512字节。每次成员授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
     */
    public WxWorkUserInfo getUserInfoByCode(String corpId, String code)  {
        String token = getCorpToken(corpId);
        logger.debug("parameters: code={}, access_token={}", code, token);
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + token + "&code=" + code);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, WxWorkUserInfo.class);
    }


    /**
     * 获取企业JS-SDK的访问票据。如果缓存未过期，直接使用；否则通过微信后台接口重新获取，然后缓存，直到过期。
     * @param corpId 授权方corpid
     * @return 授权方（企业）JS-SDK ticket
     */
    public String getCorpJsApiTicket(String corpId)  {
        String CORP_JSSDK_TICKET_KEY = "Corp_jsSdkTicket";
        String key = CORP_JSSDK_TICKET_KEY + "." + corpId;

        String ticket = (String)cacheClient.getCache(key);
        if(ticket == null) {
            String token = getCorpToken(corpId);
            JsApiTicketInfo ticketInfo = getJsApiTicket(token);
            if(ticketInfo.getErrcode() == null || ticketInfo.getErrcode() == 0) {
                ticket = ticketInfo.getTicket();
                cacheClient.setCache(key, ticket, ticketInfo.getExpiresIn() * 1000);
            } else {
                logger.error("Failed to query corp token: corpId={}, errcode={}, errmsg={}", corpId, ticketInfo.getErrcode(), ticketInfo.getErrmsg());
            }
        }

        return ticket;
    }

    /**
     * 通过access_token来获取jsapi_ticket，jsapi_ticket是H5应用调用企业微信JS接口的临时票据。
     * @param token 企业调用接口凭证
     */
    public JsApiTicketInfo getJsApiTicket(String token) {
        logger.debug("parameters: access_token={}", token);
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + token);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, JsApiTicketInfo.class);
    }

    /**
     * 第三方可通过如下接口，获取登录用户的信息。
     * @param authCode oauth2.0授权企业微信管理员登录产生的code，最长为512字节。只能使用一次，5分钟未被使用自动过期
     */
    public LoginInfo getLoginInfo(String authCode) {
        String token = getSuiteToken();
        Map<String, Object> map = new HashMap<>();
        map.put("auth_code", authCode);

        logger.debug("parameters: auth_code={}, access_token={}", authCode, token);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info?access_token=" + token, map);
        logger.debug(json);

        return JsonMapper.nonEmptyCamelMapper().fromJson(json, LoginInfo.class);
    }

    public AdminListInfo getCorpAdminList(String corpId, Integer appId) {
        String token = getSuiteToken();
        Map<String, Object> map = new HashMap<>();
        map.put("auth_corpid", corpId);
        map.put("agentid", appId);

        logger.debug("parameters: auth_corpid={}, agentid={}, suite_access_token={}", corpId, appId, token);
        String json = HttpClientUtils.httpPostWithJsonBody("https://qyapi.weixin.qq.com/cgi-bin/service/get_admin_list?suite_access_token=" + token, map);
        logger.debug(json);

        return JsonMapper.nonEmptyCamelMapper().fromJson(json, AdminListInfo.class);
    }

    //获取公司的部门列表
    public QueryDepartmentResponse getDepartmentList(String corpId) {
        return getDepartmentList(corpId, null);
    }

    //获取公司的部门列表
    public QueryDepartmentResponse getDepartmentList(String corpId, String deptId) {
        String token = getCorpToken(corpId);
        if(token == null) {
            logger.error("Failed to get department list: corp access token is null, corpId={}", corpId);
            return null;
        }

        logger.debug("parameters: id={}, access_token={}", deptId, token);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=" + token;
        if(StringUtils.isNotBlank(deptId)) {
            url = url + "&id=" + deptId;
        }

        String json = HttpClientUtils.httpGet(url);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, QueryDepartmentResponse.class);
    }

    public QueryUserResponse getUserListOfDept(String corpId, String deptId) {
        String token = getCorpToken(corpId);
        if(token == null) {
            logger.error("Failed to get user of department: corp access token is null.");
            return null;
        }

        logger.debug("parameters: department_id={}, fetch_child=0, access_token={}", deptId, token);
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=" + token + "&department_id=" + deptId + "&fetch_child=0");
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, QueryUserResponse.class);
    }

    public User getUserInfo(String corpId, String userId) {
        String token = getCorpToken(corpId);
        if(token == null) {
            logger.error("Failed to get user info: corp access token is null, corpId={}, userId={}", corpId, userId);
            return null;
        }

        logger.debug("parameters: userid={}, fetch_child=0, access_token={}", userId, token);
        String json = HttpClientUtils.httpGet("https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=" + token + "&userid=" + userId);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, User.class);
    }

    public SendMessageResult sendTextMessage(String corpId, Integer agentId, String userId, String content) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", userId);

        map.put("msgtype", "text");
        map.put("agentid", agentId);

        TextMessage message = new TextMessage();
        message.setContent(content);
        map.put("text", message);

        return sendMessage(corpId, map);
    }

    private SendMessageResult sendMessage(String corpId, Map<String, Object> map) {
        String token = getCorpToken(corpId);
        logger.debug("parameters: access_token={}", token);
        String json = HttpClientUtils.httpPostWithJsonBody(" https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token, map);
        logger.debug(json);
        return JsonMapper.nonEmptyCamelMapper().fromJson(json, SendMessageResult.class);
    }
}
package com.huawei.sharedrive.uam.weixin.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.weixin.rest.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.exception.ExistUserConflictException;
import com.huawei.sharedrive.uam.openapi.domain.GlobalErrorMessage;
import com.huawei.sharedrive.uam.openapi.domain.RestResponse;
import com.huawei.sharedrive.uam.openapi.manager.LoginManager;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.service.WxOauth2Service;
import com.huawei.sharedrive.uam.weixin.service.WxUserManager;
import com.huawei.sharedrive.uam.weixin.service.WxWorkOauth2Service;
import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.SHA1;

@Controller
@RequestMapping(value = "/api/v2/wxOauth2")
public class WxOauth2Controller {
	private static Logger logger = LoggerFactory.getLogger(WxOauth2Controller.class);

	@Autowired
    @Qualifier("wxWorkOauth2Service")
    WxWorkOauth2Service oauth2Service;

    @Autowired
    WxOauth2Service wxOauth2Service;

    @Autowired
    WxUserManager wxUserManager;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EnterpriseUserService   enterpriseUserService;

    @Autowired
    private LoginManager loginManager;

    /**
	 *  获取授权企业的访问票据
	 */
	@RequestMapping(value = "/getCorpToken", method = RequestMethod.GET)
    public ResponseEntity<String> getCorpToken(@RequestParam String corpId) throws Exception {
        String ticket = oauth2Service.getCorpToken(corpId);
        return new ResponseEntity<>(ticket, HttpStatus.OK);
    }

    /**
	 *  获取授权企业JS-SDK的访问票据
	 */
	@RequestMapping(value = "/getWxWorkJsApiTicket", method = RequestMethod.GET)
    public ResponseEntity<JsAuthObject> getCorpJsApiTicket(@RequestParam String corpId, @RequestParam String url) throws Exception {
        String ticket = oauth2Service.getCorpJsApiTicket(corpId);

        // 获取签名signature
        String noncestr = SHA1.getRandomString(16);
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String str = "jsapi_ticket=" + ticket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;
        String signature = null;
        try {
            signature = SHA1.sha1(str);
        } catch (AesException e) {
            e.printStackTrace();
        }

        JsAuthObject authData = new JsAuthObject();
        authData.setAppId(corpId);
        authData.setNoncestr(noncestr);
        authData.setTimestamp(Long.parseLong(timestamp));
        authData.setSignature(signature);

        return new ResponseEntity<>(authData, HttpStatus.OK);
    }

    /**
     *  将本系统内的账户，绑定微信账号。 此链接是用户扫码后，从微信服务器跳转过来的Get请求。
     */
    @RequestMapping(value = "/bindWxAccount", method = RequestMethod.GET)
    public String bindWxAccount(@RequestParam String code, @RequestParam Long eId, @RequestParam Long uId) throws Exception {
        WxUserInfo userInfo = wxOauth2Service.getWxUserInfo(code);
        if(userInfo == null) {
            logger.error("Can't get UserInfo of code {}: return value is null", code);
            return "enterprise/admin/user/bindWxAccountFail";
        }

        if(userInfo.getErrcode() != null && userInfo.getErrcode() != 0) {
            logger.error("Can't get UserInfo of code {}: errcode={}, errmsg={}", code, userInfo.getErrcode(), userInfo.getErrmsg());
            return "enterprise/admin/user/bindWxAccountFail";
        }

        try {
            //绑定微信用户
            WxUser user = new WxUser();
            user.setUnionId(userInfo.getUnionid());
            user.setOpenId(userInfo.getOpenid());
            user.setNickName(userInfo.getNickname());
            user.setGender(userInfo.getSex());
            user.setCountry(userInfo.getCountry());
            user.setProvince(userInfo.getProvince());
            user.setCity(userInfo.getCity());
            user.setAvatarUrl(userInfo.getHeadimgurl());

            wxUserManager.bindWxAccount(user, eId, uId);
        } catch (Exception e) {
            logger.error("Create WxUser Failed. unionId={}, nickName={}, enterpriseId={}, enterpriseUserId={}", userInfo.getUnionid(), userInfo.getNickname(), eId, uId);
            logger.error("Exception Occurred When create WxUser: ", e);
            return "enterprise/admin/user/bindWxAccountFail";
        }

        return "enterprise/admin/user/bindWxAccountSuccess";
    }

    /**
     *  将本系统内的账户，绑定微信账号。用户在终端界面上的绑定操作
     */
    @RequestMapping(value = "/bindWxAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> bindWxAccount(@RequestBody RestBindWxAccountRequest bindRequest) throws Exception {
        WxUserInfo userInfo = wxOauth2Service.getWxUserInfo(bindRequest.getCode());
        if(userInfo == null) {
            logger.error("Can't get UserInfo of code {}: return value is null", bindRequest.getCode());
            return new ResponseEntity<>("Failed to get UserInfo: return value is null", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(userInfo.hasError()) {
            logger.error("Can't get UserInfo of code {}: errcode={}, errmsg={}", bindRequest.getCode(), userInfo.getErrcode(), userInfo.getErrmsg());
            return new ResponseEntity<>("Failed to get UserInfo: err=" + userInfo.getErrmsg(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            //绑定微信用户
            WxUser user = new WxUser();
            user.setUnionId(userInfo.getUnionid());
            user.setOpenId(userInfo.getOpenid());
            user.setNickName(userInfo.getNickname());
            user.setGender(userInfo.getSex());
            user.setCountry(userInfo.getCountry());
            user.setProvince(userInfo.getProvince());
            user.setCity(userInfo.getCity());
            user.setAvatarUrl(userInfo.getHeadimgurl());

            wxUserManager.bindWxAccount(user, bindRequest.getEnterpriseId(), bindRequest.getEnterpriseUserId());
        } catch (Exception e) {
            logger.error("Create WxUser Failed. unionId={}, nickName={}, enterpriseId={}, enterpriseUserId={}", userInfo.getUnionid(), userInfo.getNickname(), bindRequest.getEnterpriseId(), bindRequest.getEnterpriseUserId());
            logger.error("Exception Occurred When create WxUser: ", e);
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    /**
     *  在微信小程序中绑定本系统内的账户
     */
    @RequestMapping(value = "/bindWxMpAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestResponse> bindWxMpAccount(@RequestBody RestBindWxMpAccountRequest bindRequest) throws Exception {
        WxMpUserInfo userInfo = wxOauth2Service.getWxMpUserInfo(bindRequest.getMpId(), bindRequest.getCode(), bindRequest.getIv(), bindRequest.getEncryptedData());
        if(userInfo == null) {
            logger.error("Failed to bind account: the wxMpUserInfo is null.");
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

        if(userInfo.hasError()) {
            logger.error("Can't get UserInfo of code {}: errcode={}, errmsg={}", bindRequest.getCode(), userInfo.getErrcode(), userInfo.getErrmsg());
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

        //
        Enterprise enterprise = enterpriseService.getByName(bindRequest.getEnterpriseName());
        if(enterprise == null) {
            logger.error("No such enterprise: {}", bindRequest.getEnterpriseName());
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

        EnterpriseUser enterpriseUser = enterpriseUserService.getByEnterpriseIdAndName(enterprise.getId(), bindRequest.getUsername());
        if(enterpriseUser == null) {
            logger.error("No such user: {}", bindRequest.getUsername());
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

        //检查密码
        HashPassword hashPassword = new HashPassword();
        hashPassword.setHashPassword(enterpriseUser.getPassword());
        hashPassword.setIterations(enterpriseUser.getIterations());
        hashPassword.setSalt(enterpriseUser.getSalt());
        if (!HashPasswordUtil.validatePassword(bindRequest.getPassword(), hashPassword)) {
            logger.error("Password is wrong: {}", bindRequest.getUsername());
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USERNAME_PASSWORD_WRONG), HttpStatus.OK);
        }

        try {
            //绑定微信用户
            WxUser user = new WxUser();
            user.setUnionId(userInfo.getUnionId());
            user.setOpenId(userInfo.getOpenId());
            user.setNickName(userInfo.getNickName());
            user.setGender(userInfo.getGender());
            user.setCountry(userInfo.getCountry());
            user.setProvince(userInfo.getProvince());
            user.setCity(userInfo.getCity());
            user.setAvatarUrl(userInfo.getAvatarUrl());

            wxUserManager.bindWxAccount(user, enterpriseUser.getEnterpriseId(), enterpriseUser.getId());
        } catch (ExistUserConflictException e) {
            logger.error("WxEnterpriseUser has existed. unionId={}, nickName={}, username={}, enterpriseId={}", userInfo.getUnionId(), userInfo.getNickName(), bindRequest.getUsername(), enterpriseUser.getId());
            logger.error("Exception Occurred When create WxUser: ", e);
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.FAIL), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Create WxUser Failed. unionId={}, nickName={}, username={}, enterpriseId={}", userInfo.getUnionId(), userInfo.getNickName(), bindRequest.getUsername(), enterpriseUser.getId());
            logger.error("Exception Occurred When create WxUser: ", e);
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.FAIL), HttpStatus.OK);
        }

        return new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    }

    /**
     *  使用微信扫码，开通本系统内的个人账户
     */
    @RequestMapping(value = "/openWxAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestResponse> openWxAccount(HttpServletRequest request, @RequestBody RestOpenWxAccountRequest openRequest) throws Exception {
        WxUserInfo wxUserInfo = wxOauth2Service.getWxUserInfo(openRequest.getCode());
        if(wxUserInfo == null) {
            logger.error("Failed to open account: the wxMpUserInfo is null.");
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

        if(wxUserInfo.hasError()) {
            logger.error("Can't get UserInfo of code {}: errcode={}, errmsg={}", openRequest.getCode(), wxUserInfo.getErrcode(), wxUserInfo.getErrmsg());
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

        WxUser wxUser = new WxUser();
        wxUser.setUnionId(wxUserInfo.getUnionid());
        wxUser.setOpenId(wxUserInfo.getOpenid());
        try {
            //微信返回的都是ISO-8859-1，中文会乱码
            wxUser.setNickName(new String(wxUserInfo.getNickname().getBytes("ISO-8859-1"),"UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }
        wxUser.setGender(wxUserInfo.getSex());
        wxUser.setCountry(wxUserInfo.getCountry());
        wxUser.setProvince(wxUserInfo.getProvince());
        wxUser.setCity(wxUserInfo.getCity());
        wxUser.setAvatarUrl(wxUserInfo.getHeadimgurl());
        wxUser.setEmail(new Date().getTime()+"@filepro.cn");
        wxUser.setStatus((byte)0);
        wxUser.setType((byte)0);
        wxUserManager.openAccount(wxUser);

        //
        return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.OK), HttpStatus.OK);
    }

    /**
     *  在微信小程序中开通本系统内的账户
     */
    @RequestMapping(value = "/openWxMpAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RestResponse> openWxMpAccount(HttpServletRequest request, @RequestBody RestOpenWxMpAccountRequest openRequest) throws Exception {
        WxMpUserInfo mpUserInfo = wxOauth2Service.getWxMpUserInfo(openRequest.getMpId(), openRequest.getCode(), openRequest.getIv(), openRequest.getEncryptedData());
        if(mpUserInfo == null) {
            logger.error("Failed to open account: the wxMpUserInfo is null.");
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

        if(mpUserInfo.hasError()) {
            logger.error("Can't get UserInfo of code {}: errcode={}, errmsg={}", openRequest.getCode(), mpUserInfo.getErrcode(), mpUserInfo.getErrmsg());
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }

//        //自动生成账户
//    	WxMpUserInfo mpUserInfo = new WxMpUserInfo();
//    	mpUserInfo.setUnionId("003nOGzm0xUESl1mt2Cm0xCLzm0nOGzB");
//    	mpUserInfo.setNickName("殷正勇");
//    	mpUserInfo.setGender(1);
//    	mpUserInfo.setAvatarUrl("http://p.qlogo.cn/bizmail/G7ILxE3apibhMmv2NmZQ5sRDLlsr5Q0csKflLkNddKEsePTUt65yoQA/0");
//    	mpUserInfo.setOpenId("003nOGzm0xUESl1mt2Cm0xCLzm0nOGzB");
//
        WxUser wxUser = new WxUser();
        wxUser.setUnionId(mpUserInfo.getUnionId());
        wxUser.setOpenId(mpUserInfo.getOpenId());
        if(mpUserInfo.getCountry().equals("CN")){
            try {
                wxUser.setNickName(new String(mpUserInfo.getNickName().getBytes("ISO-8859-1"),"UTF-8"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            wxUser.setNickName(mpUserInfo.getNickName());
        }
        wxUser.setGender(mpUserInfo.getGender());
        wxUser.setCountry(mpUserInfo.getCountry());
        wxUser.setProvince(mpUserInfo.getProvince());
        wxUser.setCity(mpUserInfo.getCity());
        wxUser.setLanguage(mpUserInfo.getLanguage());
        wxUser.setAvatarUrl(mpUserInfo.getAvatarUrl());
        wxUser.setEmail(new Date().getTime()+"@storbox.cn");
        wxUser.setStatus((byte)0);
        wxUser.setType((byte)0);
        wxUserManager.openAccount(wxUser);

        //
        return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.OK), HttpStatus.OK);
    }

    /**
     *  在微信小程序中开通本系统内的账户
     */
    @RequestMapping(value = "/phone", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getWxMpPhone(HttpServletRequest request, @RequestBody RestOpenWxMpAccountRequest openRequest) throws Exception {
        String phoneStr = wxOauth2Service.getWxMpUserPhone(openRequest.getMpId(), openRequest.getCode(), openRequest.getIv(), openRequest.getEncryptedData());
        if(StringUtils.isEmpty(phoneStr)) {
            logger.error("Failed to open account: the wxMpUserPhone is null.");
            return new ResponseEntity<>(new RestResponse(GlobalErrorMessage.USER_NOT_EXIST), HttpStatus.OK);
        }
        
        return new ResponseEntity<>(phoneStr, HttpStatus.OK);
    }
}

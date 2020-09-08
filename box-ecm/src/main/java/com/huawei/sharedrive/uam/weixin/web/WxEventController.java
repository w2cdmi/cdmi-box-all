package com.huawei.sharedrive.uam.weixin.web;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.event.*;
import com.huawei.sharedrive.uam.weixin.service.WwAppMessageService;
import com.huawei.sharedrive.uam.weixin.service.WxAppEventService;
import com.huawei.sharedrive.uam.weixin.service.WxSuiteEventService;
import com.huawei.sharedrive.uam.weixin.service.WxSysEventService;
import com.huawei.sharedrive.uam.weixin.service.impl.WxEventFactory;
import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>定义微信事件Controller的通用方法</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/27
 ************************************************************/
@Controller
@RequestMapping(value = "/wxEvent")
public class WxEventController {
    private static final Logger logger = LoggerFactory.getLogger(WxEventController.class);
    private String corpId = "wwc7342fa63c523b9a";
    private String token = "oOU4DJIE49vJj1";
    private String encodingAesKey = "Swe71JTWs4q1mz7YxtzvWwaRwChObCViOTW67Cbihri";

    //套件Id
    private String suiteId = "tje32d93de35487681";

    @Autowired
    WxSysEventService sysEventService;

    @Autowired
    WxSuiteEventService suiteEventService;

    @Autowired
    WxAppEventService appEventService;

    @Autowired
    WwAppMessageService wwAppMessageService;

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey;
    }

    public String getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(String suiteId) {
        this.suiteId = suiteId;
    }

    /**
     * 在配置事件接收URL时，微信后台用访问此地址用于验证接口是否正确。
     * 请求方式：GET（HTTPS）
     * @param signature 消息签名
     * @param timestamp 时间戳
     * @param nonce 随机值
     * @param echo 加密过的要求回显字符串
     * @return 解密后的回显字符串
     */
    @RequestMapping(value = "/suite", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> suiteVerify(@RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp,
                                          @RequestParam("nonce") String nonce, @RequestParam("echostr") String echo) {
        try {
            String text = verifyWithCorpId(signature, timestamp, nonce, echo);
            return new ResponseEntity<>(text, HttpStatus.OK);
        } catch (AesException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 请求方式：POST（HTTPS）
     * 请求地址：https://127.0.0.1/suite/receive?msg_signature=3a7b08bb8e6dbce3c9671d6fdb69d15066227608&timestamp=1403610513&nonce=380320359
     * @param signature 消息签名
     * @param timestamp 时间戳
     * @param nonce 随机值
     * @return HTTP状态
     */
    @RequestMapping(value = "/suite", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> suiteEvent(@RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp,
                                             @RequestParam("nonce") String nonce, @RequestBody String body) {
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, suiteId);
            String msg = wxcpt.DecryptMsg(signature, timestamp, nonce, body);
            logger.info("received suite event: " + msg);

            WxSuiteEvent event = WxEventFactory.parseSuiteEvent(msg);
            suiteEventService.handle(event);
        } catch (Exception e) {
            logger.error("Error occurred while handling Suite Event:", e);
//            e.printStackTrace();
            return new ResponseEntity<>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    /**
     * 用户(未开通企业微信)通过扫码注册企业微信并自动安装应用后，会回调此方法，发送授权消息。
     * 请求方式：POST（HTTPS）
     * 请求地址：https://127.0.0.1/suite/receive?msg_signature=3a7b08bb8e6dbce3c9671d6fdb69d15066227608&timestamp=1403610513&nonce=380320359
     * @param signature 消息签名
     * @param timestamp 时间戳
     * @param nonce 随机值
     * @return HTTP状态
     */
    @RequestMapping(value = "/sys", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> sysVerify(@RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp,
                                            @RequestParam("nonce") String nonce, @RequestParam("echostr") String echo) {
        try {
            //此处使用corpId验证URL
            String text = verifyWithCorpId(signature, timestamp, nonce, echo);
            return new ResponseEntity<>(text, HttpStatus.OK);
/*
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, suiteId);
            String msg = wxcpt.DecryptMsg(signature, timestamp, nonce, body);

            WxSuiteEvent event = WxEventFactory.parseSuiteEvent(msg);
            suiteEventService.handle(event);
*/
        } catch (Exception e) {
            logger.error("Error occurred while handling Suite Event:", e);
//            e.printStackTrace();
            return new ResponseEntity<>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/sys", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> sysEvent(@RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp,
                                           @RequestParam("nonce") String nonce, @RequestBody String body) {
        try {
            //使用服务商ID校验
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, corpId);
            String msg = wxcpt.DecryptMsg(signature, timestamp, nonce, body);

            WxSysEvent event = WxEventFactory.parseSysEvent(msg);
            sysEventService.handle(event);
        } catch (Exception e) {
            logger.error("Error occurred while handling Suite Event:", e);
//            e.printStackTrace();
            return new ResponseEntity<>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    /**
     * 用户(已开通企业微信)通过扫码安装应用后，会回调此URL。
     * 请求方式：POST（HTTPS）
     * 请求地址：https://127.0.0.1/wxEvent/install?auth_code=lLFKuYA90ZfBsGPKvokpJTQUJZbzG2gpldAdk9bTx_QGBxZcDwGuGYTDa2r745Ts0yih7UyKAdmenGUxvLEvS9egcViywmngklPHjlYFTYM&state=undefined&expires_in=1200
     * @param authCode 临时授权码
     * @param expiresIn 生效时长
     * @param state 随机值
     * @return HTTP状态
     */
    @RequestMapping(value = "/install", method = RequestMethod.GET)
    public String install(@RequestParam("auth_code") String authCode, @RequestParam("expires_in") String expiresIn,
                                          @RequestParam(value = "state", required = false) String state) {
        try {
            SuiteCreateAuthEvent event = new SuiteCreateAuthEvent();
            event.setSuiteId(suiteId);
            event.setAuthCode(authCode);
            suiteEventService.handle(event);
        } catch (Exception e) {
            logger.error("Error occurred while handling Suite Event:", e);
//            e.printStackTrace();
        }

        return "enterprise/admin/installSuccess";
    }

    /**
     * 在配置数据接收URL时，微信后台用访问此地址用于验证接口是否正确。
     * 请求方式：GET（HTTPS）
     * @param signature 消息签名
     * @param timestamp 时间戳
     * @param nonce 随机值
     * @param echo 加密过的要求回显字符串
     * @return 解密后的回显字符串
     */
    @RequestMapping(value = "/app", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> appVerify(@RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp,
                                            @RequestParam("nonce") String nonce, @RequestParam("echostr") String echo) {
        try {
            String text = verifyWithCorpId(signature, timestamp, nonce, echo);
            return new ResponseEntity<>(text, HttpStatus.OK);
        } catch (AesException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 用户在应用里发送的消息会推送给此地址。
     * 请求方式：POST（HTTPS）
     * 请求地址：https://127.0.0.1/suite/receive?msg_signature=3a7b08bb8e6dbce3c9671d6fdb69d15066227608&timestamp=1403610513&nonce=380320359
     * @param signature 消息签名
     * @param timestamp 时间戳
     * @param nonce 随机值
     * @return HTTP状态
     */
    @RequestMapping(value = "/app", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> appEvent(@RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce, @RequestBody String body) {
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, corpId);
            String msg = wxcpt.DecryptMsg(signature, timestamp, nonce, body);
            logger.info("received app event: " + msg);

            WwAppMessage event = WxEventFactory.parseAppMessage(msg);
            wwAppMessageService.handle(event);
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error("Error occurred while handling App Event:", e);
            return new ResponseEntity<>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }


    protected String verifyWithCorpId(String signature, String timestamp, String nonce, String echo) throws AesException {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, corpId);
        return wxcpt.VerifyURL(signature, timestamp, nonce, echo);
    }

    protected String verifyWithSuiteId(String signature, String timestamp, String nonce, String echo) throws AesException {
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, suiteId);
        return wxcpt.VerifyURL(signature, timestamp, nonce, echo);
    }
}

package com.huawei.sharedrive.uam.weixin.web;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>测试类</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/27
 ************************************************************/

@RunWith(JUnit4.class)
public class WeixinEventControllerTest {

    @Test
    public void testDecode() {
        WxEventController controller = new WxEventController();

        try {
            String sToken = "QDG6eK";
            String sCorpID = "wx5823bf96d3bd56c7";
            String sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";

            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);

            String sVerifyMsgSig = "5c45ff5e21c57e6ad56bac8758b79b1d9ac89fd3";
            // String sVerifyTimeStamp = HttpUtils.ParseUrl("timestamp");
            String sVerifyTimeStamp = "1409659589";
            // String sVerifyNonce = HttpUtils.ParseUrl("nonce");
            String sVerifyNonce = "263014780";
            // String sVerifyEchoStr = HttpUtils.ParseUrl("echostr");
            String sVerifyEchoStr = "P9nAzCzyDtyTWESHep1vC5X9xho/qYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp+4RPcs8TgAE7OaBO+FZXvnaqQ==";

            String sEchoStr; //需要返回的明文
            try {
                sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, sVerifyEchoStr);
                System.out.println("verifyurl echostr: " + sEchoStr);
                // 验证URL成功，将sEchoStr返回
                // HttpUtils.SetResponse(sEchoStr);
            } catch (Exception e) {
                //验证URL失败，错误原因请查看异常
                e.printStackTrace();
            }





            // String sReqMsgSig = HttpUtils.ParseUrl("msg_signature");
            String sReqMsgSig = "477715d11cdb4164915debcba66cb864d751f3e6";
            // String sReqTimeStamp = HttpUtils.ParseUrl("timestamp");
            String sReqTimeStamp = "1409659813";
            // String sReqNonce = HttpUtils.ParseUrl("nonce");
            String sReqNonce = "1372623149";
            // post请求的密文数据
            // sReqData = HttpUtils.PostData();
            String sReqData = "<xml><ToUserName><![CDATA[wx5823bf96d3bd56c7]]></ToUserName><Encrypt><![CDATA[RypEvHKD8QQKFhvQ6QleEB4J58tiPdvo+rtK1I9qca6aM/wvqnLSV5zEPeusUiX5L5X/0lWfrf0QADHHhGd3QczcdCUpj911L3vg3W/sYYvuJTs3TUUkSUXxaccAS0qhxchrRYt66wiSpGLYL42aM6A8dTT+6k4aSknmPj48kzJs8qLjvd4Xgpue06DOdnLxAUHzM6+kDZ+HMZfJYuR+LtwGc2hgf5gsijff0ekUNXZiqATP7PF5mZxZ3Izoun1s4zG4LUMnvw2r+KqCKIw+3IQH03v+BCA9nMELNqbSf6tiWSrXJB3LAVGUcallcrw8V2t9EL4EhzJWrQUax5wLVMNS0+rUPA3k22Ncx4XXZS9o0MBH27Bo6BpNelZpS+/uh9KsNlY6bHCmJU9p8g7m3fVKn28H3KDYA5Pl/T8Z1ptDAVe0lXdQ2YoyyH2uyPIGHBZZIs2pDBS8R07+qN+E7Q==]]></Encrypt><AgentID><![CDATA[218]]></AgentID></xml>";

            try {
                String sMsg = wxcpt.DecryptMsg(sReqMsgSig, sReqTimeStamp, sReqNonce, sReqData);
                System.out.println("after decrypt msg: " + sMsg);
            } catch (Exception e) {
                // 解密失败，失败原因请查看异常
                e.printStackTrace();
            }
        } catch (AesException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void encode() {
        String encode = "P9nAzCzyDtyTWESHep1vC5X9xho%2FqYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp%2B4RPcs8TgAE7OaBO%2BFZXvnaqQ%3D%3D";

        try {
            String decode = URLDecoder.decode(encode, "UTF-8");

            Assert.assertEquals("P9nAzCzyDtyTWESHep1vC5X9xho/qYX3Zpb4yKa9SKld1DsH3Iyt3tP3zNdtp+4RPcs8TgAE7OaBO+FZXvnaqQ==", decode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}

/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.rest;

import com.huawei.sharedrive.uam.weixin.event.*;
import com.huawei.sharedrive.uam.weixin.service.impl.WxEventFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>测试与JSON数据的转换</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/28
 ************************************************************/
@RunWith(JUnit4.class)
public class Xml2ObjectTest {
    @Test
    public void testCreateUserEvent() {
        String xml ="<xml>" +
                "<SuiteId><![CDATA[tje32d93de35487681]]></SuiteId>" +
                "<AuthCorpId><![CDATA[wwf6c2440675462456]]></AuthCorpId>" +
                "<InfoType><![CDATA[change_contact]]></InfoType>" +
                "<TimeStamp>1518169037</TimeStamp>" +
                "<ChangeType><![CDATA[create_user]]></ChangeType>" +
                "<UserID><![CDATA[RoxLiu]]></UserID>" +
                "<Name><![CDATA[Rox]]></Name>" +
                "<Gender>1</Gender>" +
                "<Department><![CDATA[1,2]]></Department>" +
                "</xml>";
        WxSuiteEvent event = WxEventFactory.parseSuiteEvent(xml);

        Assert.assertTrue(event instanceof SuiteCreateUserEvent);
    }

    @Test
    public void testUpdateUserEvent() {
        String xml ="<xml>" +
                "<SuiteId><![CDATA[tje32d93de35487681]]></SuiteId>" +
                "<AuthCorpId><![CDATA[wwf6c2440675462456]]></AuthCorpId>" +
                "<InfoType><![CDATA[change_contact]]></InfoType>" +
                "<TimeStamp>1518171482</TimeStamp>" +
                "<ChangeType><![CDATA[update_user]]></ChangeType>" +
                "<UserID><![CDATA[LiuYongHua]]></UserID>" +
                "<Name><![CDATA[鍒樻案鍗]]></Name>" +
                "<NewUserID><![CDATA[LiuYongHua2]]></NewUserID>" +
                "</xml>";
        WxSuiteEvent event = WxEventFactory.parseSuiteEvent(xml);

        Assert.assertTrue(event instanceof SuiteUpdateUserEvent);
        Assert.assertNotNull(((SuiteUpdateUserEvent)event).getNewUserID());

        xml = "<xml><SuiteId><![CDATA[tje32d93de35487681]]></SuiteId><AuthCorpId><![CDATA[wwf6c2440675462456]]></AuthCorpId><InfoType><![CDATA[change_contact]]></InfoType><TimeStamp>1518172440</TimeStamp><ChangeType><![CDATA[update_user]]></ChangeType><UserID><![CDATA[LiuYongHua2]]></UserID><Name><![CDATA[鍒樻案鍗3]]></Name></xml>";
        event = WxEventFactory.parseSuiteEvent(xml);

        Assert.assertTrue(event instanceof SuiteUpdateUserEvent);
        Assert.assertNull(((SuiteUpdateUserEvent)event).getNewUserID());
    }

    @Test
    public void testTextMessage() {
        String xml ="<xml><ToUserName><![CDATA[ww0315f3de399a8a60]]></ToUserName>" +
                "<FromUserName><![CDATA[Chen]]></FromUserName>" +
                "<CreateTime>1517814688</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[？]]></Content>" +
                "<MsgId>1672902549</MsgId>" +
                "<AgentID>1000002</AgentID>" +
                "</xml>";
        WwAppMessage message = WxEventFactory.parseAppMessage(xml);

        Assert.assertTrue(message instanceof WwAppTextMessage);
        Assert.assertNotNull(message.getToUserName());
        Assert.assertNotNull(message.getFromUserName());
        Assert.assertNotNull(message.getMsgType());
        Assert.assertNotNull(message.getMsgId());
        Assert.assertNotNull(((WwAppTextMessage)message).getContent());
    }

    @Test
    public void testImageMessage() {
        String xml ="<xml>" +
                "<ToUserName><![CDATA[toUser]]></ToUserName>" +
                "<FromUserName><![CDATA[fromUser]]></FromUserName>" +
                "<CreateTime>1348831860</CreateTime>" +
                "<MsgType><![CDATA[image]]></MsgType>" +
                "<PicUrl><![CDATA[this is a url]]></PicUrl>" +
                "<MediaId><![CDATA[media_id]]></MediaId>" +
                "<MsgId>1234567890123456</MsgId>" +
                "<AgentID>1</AgentID>" +
                "</xml>";
        WwAppMessage message = WxEventFactory.parseAppMessage(xml);

        Assert.assertTrue(message instanceof WwAppImageMessage);
        Assert.assertNotNull(message.getToUserName());
        Assert.assertNotNull(message.getFromUserName());
        Assert.assertNotNull(message.getMsgType());
        Assert.assertNotNull(message.getMsgId());
        Assert.assertNotNull(((WwAppImageMessage)message).getMediaId());
        Assert.assertNotNull(((WwAppImageMessage)message).getPicUrl());
    }

    @Test
    public void testVoiceMessage() {
        String xml ="<xml>" +
                "<ToUserName><![CDATA[toUser]]></ToUserName>" +
                "<FromUserName><![CDATA[fromUser]]></FromUserName>" +
                "<CreateTime>1357290913</CreateTime>" +
                "<MsgType><![CDATA[voice]]></MsgType>" +
                "<MediaId><![CDATA[media_id]]></MediaId>" +
                "<Format><![CDATA[Format]]></Format>" +
                "<MsgId>1234567890123456</MsgId>" +
                "<AgentID>1</AgentID>" +
                "</xml>";
        WwAppMessage message = WxEventFactory.parseAppMessage(xml);

        Assert.assertTrue(message instanceof WwAppVoiceMessage);
        Assert.assertNotNull(message.getToUserName());
        Assert.assertNotNull(message.getFromUserName());
        Assert.assertNotNull(message.getMsgType());
        Assert.assertNotNull(message.getMsgId());
        Assert.assertNotNull(((WwAppVoiceMessage)message).getMediaId());
        Assert.assertNotNull(((WwAppVoiceMessage)message).getFormat());
    }

    @Test
    public void testVideoMessage() {
        String xml ="<xml>" +
                "<ToUserName><![CDATA[toUser]]></ToUserName>" +
                "<FromUserName><![CDATA[fromUser]]></FromUserName>" +
                "<CreateTime>1357290913</CreateTime>" +
                "<MsgType><![CDATA[video]]></MsgType>" +
                "<MediaId><![CDATA[media_id]]></MediaId>" +
                "<ThumbMediaId><![CDATA[thumb_media_id]]></ThumbMediaId>" +
                "<MsgId>1234567890123456</MsgId>" +
                "<AgentID>1</AgentID>" +
                "</xml>";
        WwAppMessage message = WxEventFactory.parseAppMessage(xml);

        Assert.assertTrue(message instanceof WwAppVideoMessage);
        Assert.assertNotNull(message.getToUserName());
        Assert.assertNotNull(message.getFromUserName());
        Assert.assertNotNull(message.getMsgType());
        Assert.assertNotNull(message.getMsgId());
        Assert.assertNotNull(((WwAppVideoMessage)message).getMediaId());
        Assert.assertNotNull(((WwAppVideoMessage)message).getThumbMediaId());
    }

    @Test
    public void testLocationMessage() {
        String xml ="<xml>" +
                "<ToUserName><![CDATA[toUser]]></ToUserName>" +
                "<FromUserName><![CDATA[fromUser]]></FromUserName>" +
                "<CreateTime>1351776360</CreateTime>" +
                "<MsgType><![CDATA[location]]></MsgType>" +
                "<Location_X>23.134521</Location_X>" +
                "<Location_Y>113.358803</Location_Y>" +
                "<Scale>20</Scale>" +
                "<Label><![CDATA[位置信息]]></Label>" +
                "<MsgId>1234567890123456</MsgId>" +
                "<AgentID>1</AgentID>" +
                "</xml>";
        WwAppMessage message = WxEventFactory.parseAppMessage(xml);

        Assert.assertTrue(message instanceof WwAppLocationMessage);
        Assert.assertNotNull(message.getToUserName());
        Assert.assertNotNull(message.getFromUserName());
        Assert.assertNotNull(message.getMsgType());
        Assert.assertNotNull(message.getMsgId());
        Assert.assertNotNull(((WwAppLocationMessage)message).getLocation_X());
        Assert.assertNotNull(((WwAppLocationMessage)message).getLocation_Y());
        Assert.assertNotNull(((WwAppLocationMessage)message).getLabel());
        Assert.assertNotNull(((WwAppLocationMessage)message).getScale());
    }

    @Test
    public void testLinkMessage() {
        String xml ="<xml>" +
                "<ToUserName><![CDATA[toUser]]></ToUserName>" +
                "<FromUserName><![CDATA[fromUser]]></FromUserName> " +
                "<CreateTime>1348831860</CreateTime>" +
                "<MsgType><![CDATA[link]]></MsgType>" +
                "<Title><![CDATA[this is a title！]]></Title>" +
                "<Description><![CDATA[this is a description！]]></Description>" +
                "<PicUrl><![CDATA[this is a url]]></PicUrl>" +
                "<MsgId>1234567890123456</MsgId>" +
                "<AgentID>1</AgentID>" +
                "</xml>";
        WwAppMessage message = WxEventFactory.parseAppMessage(xml);

        Assert.assertTrue(message instanceof WwAppLinkMessage);
        Assert.assertNotNull(message.getToUserName());
        Assert.assertNotNull(message.getFromUserName());
        Assert.assertNotNull(message.getMsgType());
        Assert.assertNotNull(message.getMsgId());
        Assert.assertNotNull(((WwAppLinkMessage)message).getTitle());
        Assert.assertNotNull(((WwAppLinkMessage)message).getDescription());
        Assert.assertNotNull(((WwAppLinkMessage)message).getPicUrl());
    }



}

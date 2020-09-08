package com.huawei.sharedrive.uam.weixin.service.impl;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import com.huawei.sharedrive.uam.weixin.event.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>测试类 </pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
@RunWith(JUnit4.class)
public class WxEventFactoryTest {
    @Test
    public void testSysRegisterCorpEvent() {
        String body = "    <xml>\n" +
                "        <ServiceCorpId><![CDATA[wxfjdkasfasjkfdsa]]></ServiceCorpId>\n" +
                "        <InfoType><![CDATA[register_corp]]></InfoType>\n" +
                "        <TimeStamp>1502682173</TimeStamp>\n" +
                "        <RegisterCode><![CDATA[pIKi3wRPNWCGF-pyP-YU5KWjDDD]]></RegisterCode>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <ContactSync>\n" +
                "            <AccessToken><![CDATA[accesstoken000001]]></AccessToken>\n" +
                "            <ExpiresIn>7200</ExpiresIn>\n" +
                "        </ContactSync>\n" +
                "        <AuthUserInfo>\n" +
                "            <Email><![CDATA[zhangshan@qq.com]]></Email>\n" +
                "            <Mobile><![CDATA[12345678901]]></Mobile>\n" +
                "            <UserId><![CDATA[zhangshan]]></UserId>\n" +
                "        </AuthUserInfo>\n" +
                "    </xml>";

        WxSysEvent e = WxEventFactory.parseSysEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SysRegisterCorpEvent);

        Assert.assertNotNull(e.getServiceCorpId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertNotNull(((SysRegisterCorpEvent) e).getAuthCorpId());
        Assert.assertNotNull(((SysRegisterCorpEvent) e).getContactSync());
        Assert.assertNotNull(((SysRegisterCorpEvent) e).getContactSync().getAccessToken());
        Assert.assertNotNull(((SysRegisterCorpEvent) e).getAuthUserInfo());
        Assert.assertNotNull(((SysRegisterCorpEvent) e).getAuthUserInfo().getEmail());
    }

    @Test
    public void testSuiteTicketEvent() {
        String body = "    {\n" +
                "        <xml>\n" +
                "            <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "            <InfoType> <![CDATA[suite_ticket]]></InfoType>\n" +
                "            <TimeStamp>1403610513</TimeStamp>\n" +
                "            <SuiteTicket><![CDATA[asdfasfdasdfasdf]]></SuiteTicket>\n" +
                "        </xml>\n" +
                "    }";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteTicketEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertNotNull(((SuiteTicketEvent) e).getSuiteTicket());
    }

    @Test
    public void testSuiteCreateAuthEvent() {
        String body = "    {\n" +
                "        <xml>\n" +
                "            <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "            <AuthCode><![CDATA[AUTHCODE]]></AuthCode>\n" +
                "            <InfoType><![CDATA[create_auth]]></InfoType>\n" +
                "            <TimeStamp>1403610513</TimeStamp>\n" +
                "        </xml>\n" +
                "    }";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteCreateAuthEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertNotNull(((SuiteCreateAuthEvent)e).getAuthCode());
    }

    @Test
    public void testSuiteChangeAuthEvent() {
        String body = "    {\n" +
                "        <xml>\n" +
                "            <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "            <InfoType><![CDATA[change_auth]]></InfoType>\n" +
                "            <TimeStamp>1403610513</TimeStamp>\n" +
                "            <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        </xml>\n" +
                "    }";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteChangeAuthEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteChangeAuthEvent) e).getAuthCorpId());
    }

    @Test
    public void testSuiteCancelAuthEvent() {
        String body = "        {\n" +
                "        <xml>\n" +
                "            <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "            <InfoType><![CDATA[cancel_auth]]></InfoType>\n" +
                "            <TimeStamp>1403610513</TimeStamp>\n" +
                "            <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        </xml>\n" +
                "        }";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteCancelAuthEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteCancelAuthEvent) e).getAuthCorpId());
    }

    @Test
    public void testSuiteCreateUserEvent() {
        String body = "    <xml>\n" +
                "        <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <InfoType><![CDATA[change_contact]]></InfoType>\n" +
                "        <TimeStamp>1403610513</TimeStamp>\n" +
                "        <ChangeType><![CDATA[create_user]]></ChangeType>\n" +
                "        <UserID><![CDATA[zhangsan]]></UserID>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <Department><![CDATA[1,2,3]]></Department>\n" +
                "        <Mobile><![CDATA[15913215421]]></Mobile>\n" +
                "        <Position><![CDATA[产品经理]]></Position>\n" +
                "        <Gender>1</Gender>\n" +
                "        <Email><![CDATA[zhangsan@gzdev.com]]></Email>\n" +
                "        <Avatar><![CDATA[http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0]]></Avatar>\n" +
                "        <EnglishName><![CDATA[zhangsan]]></EnglishName>\n" +
                "        <IsLeader>1</IsLeader>\n" +
                "        <Telephone><![CDATA[020-3456788]]></Telephone>\n" +
                "        <ExtAttr>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[爱好]]></Name>\n" +
                "            <Value><![CDATA[旅游]]></Value>\n" +
                "            </Item>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[卡号]]></Name>\n" +
                "            <Value><![CDATA[1234567234]]></Value>\n" +
                "            </Item>\n" +
                "        </ExtAttr>\n" +
                "    </xml>";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteCreateUserEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteCreateUserEvent) e).getAuthCorpId());
        Assert.assertEquals("产品经理", ((SuiteCreateUserEvent) e).getPosition());
        Assert.assertEquals(1, ((SuiteCreateUserEvent) e).getGender().longValue());
    }

    @Test
    public void testSuiteUpdateUserEvent() {
        String body = "    <xml>\n" +
                "        <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <InfoType><![CDATA[change_contact]]></InfoType>\n" +
                "        <TimeStamp>1403610513</TimeStamp>\n" +
                "        <ChangeType><![CDATA[update_user]]></ChangeType>\n" +
                "        <UserID><![CDATA[zhangsan]]></UserID>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <Department><![CDATA[1,2,3]]></Department>\n" +
                "        <Mobile><![CDATA[15913215421]]></Mobile>\n" +
                "        <Position><![CDATA[产品经理]]></Position>\n" +
                "        <Gender>1</Gender>\n" +
                "        <Email><![CDATA[zhangsan@gzdev.com]]></Email>\n" +
                "        <Status>1</Status>\n" +
                "        <Avatar><![CDATA[http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0]]></Avatar>\n" +
                "        <EnglishName><![CDATA[zhangsan]]></EnglishName>\n" +
                "        <IsLeader>1</IsLeader>\n" +
                "        <Telephone><![CDATA[020-3456788]]></Telephone>\n" +
                "        <ExtAttr>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[爱好]]></Name>\n" +
                "            <Value><![CDATA[旅游]]></Value>\n" +
                "            </Item>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[卡号]]></Name>\n" +
                "            <Value><![CDATA[1234567234]]></Value>\n" +
                "            </Item>\n" +
                "        </ExtAttr>\n" +
                "    </xml>";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteUpdateUserEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteUpdateUserEvent) e).getAuthCorpId());
        Assert.assertEquals("产品经理", ((SuiteUpdateUserEvent) e).getPosition());
        Assert.assertEquals(1, ((SuiteUpdateUserEvent) e).getGender().longValue());
    }

    @Test
    public void testSuiteDeleteUserEvent() {
        String body = "    <xml>\n" +
                "        <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <InfoType><![CDATA[change_contact]]></InfoType>\n" +
                "        <TimeStamp>1403610513</TimeStamp>\n" +
                "        <ChangeType><![CDATA[delete_user]]></ChangeType>\n" +
                "        <UserID><![CDATA[zhangsan]]></UserID>\n" +
                "    </xml>";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteDeleteUserEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteDeleteUserEvent) e).getAuthCorpId());
        Assert.assertEquals("zhangsan", ((SuiteDeleteUserEvent) e).getUserID());
    }

    @Test
    public void testSuiteCreatePartyEvent() {
        String body = "    <xml>\n" +
                "        <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <InfoType><![CDATA[change_contact]]></InfoType>\n" +
                "        <TimeStamp>1403610513</TimeStamp>\n" +
                "        <ChangeType><![CDATA[create_party]]></ChangeType>\n" +
                "        <Id>2</Id>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <ParentId><![CDATA[1]]></ParentId>\n" +
                "        <Order>1</Order>\n" +
                "    </xml>";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteCreatePartyEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteCreatePartyEvent) e).getAuthCorpId());
        Assert.assertEquals("张三", ((SuiteCreatePartyEvent) e).getName());
        Assert.assertEquals(1, ((SuiteCreatePartyEvent) e).getOrder().longValue());
    }

    @Test
    public void testSuiteUpdatePartyEvent() {
        String body = "    <xml>\n" +
                "        <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <InfoType><![CDATA[change_contact]]></InfoType>\n" +
                "        <TimeStamp>1403610513</TimeStamp>\n" +
                "        <ChangeType><![CDATA[update_party]]></ChangeType>\n" +
                "        <Id>2</Id>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <ParentId><![CDATA[1]]></ParentId>\n" +
                "    </xml>";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteUpdatePartyEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteUpdatePartyEvent) e).getAuthCorpId());
        Assert.assertEquals("张三", ((SuiteUpdatePartyEvent) e).getName());
        Assert.assertEquals("1", ((SuiteUpdatePartyEvent) e).getParentId());
    }

    @Test
    public void testSuiteDeletePartyEvent() {
        String body = "    <xml>\n" +
                "        <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <InfoType><![CDATA[change_contact]]></InfoType>\n" +
                "        <TimeStamp>1403610513</TimeStamp>\n" +
                "        <ChangeType><![CDATA[delete_party]]></ChangeType>\n" +
                "        <Id>2</Id>\n" +
                "    </xml>";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteDeletePartyEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteDeletePartyEvent) e).getAuthCorpId());
        Assert.assertEquals(2, ((SuiteDeletePartyEvent) e).getId().longValue());
    }

    @Test
    public void testSuiteUpdateTagEvent() {
        String body = "    <xml>\n" +
                "        <SuiteId><![CDATA[tj4asffe99e54c0f4c]]></SuiteId>\n" +
                "        <AuthCorpId><![CDATA[wxf8b4f85f3a794e77]]></AuthCorpId>\n" +
                "        <InfoType><![CDATA[change_contact]]></InfoType>\n" +
                "        <TimeStamp>1403610513</TimeStamp>\n" +
                "        <ChangeType><![CDATA[update_tag]]></ChangeType>\n" +
                "        <TagId>1</TagId>\n" +
                "        <AddUserItems><![CDATA[zhangsan,lisi]]></AddUserItems>\n" +
                "        <DelUserItems><![CDATA[zhangsan1,lisi1]]></DelUserItems>\n" +
                "        <AddPartyItems><![CDATA[1,2]]></AddPartyItems>\n" +
                "        <DelPartyItems><![CDATA[3,4]]></DelPartyItems>\n" +
                "    </xml>";

        WxSuiteEvent e = WxEventFactory.parseSuiteEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof SuiteUpdateTagEvent);

        Assert.assertNotNull(e.getSuiteId());
        Assert.assertNotNull(e.getInfoType());
        Assert.assertNotNull(e.getTimeStamp());
        Assert.assertEquals("wxf8b4f85f3a794e77", ((SuiteUpdateTagEvent) e).getAuthCorpId());
        Assert.assertEquals(1, ((SuiteUpdateTagEvent) e).getTagId().longValue());
        Assert.assertEquals("zhangsan,lisi", ((SuiteUpdateTagEvent) e).getAddUserItems());
        Assert.assertEquals("zhangsan1,lisi1", ((SuiteUpdateTagEvent) e).getDelUserItems());
        Assert.assertEquals("1,2", ((SuiteUpdateTagEvent) e).getAddPartyItems());
        Assert.assertEquals("3,4", ((SuiteUpdateTagEvent) e).getDelPartyItems());
    }

    @Test
    public void testAppSubscribeEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[UserID]]></FromUserName>\n" +
                "        <CreateTime>1348831860</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[subscribe]]></Event>\n" +
                "        <AgentID>1</AgentID>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppSubscribeEvent);

        Assert.assertEquals("toUser", e.getToUserName());
        Assert.assertEquals("UserID", e.getFromUserName());
        Assert.assertEquals(1348831860L, e.getCreateTime().longValue());
        Assert.assertEquals("subscribe", e.getEvent());
        Assert.assertEquals(1, e.getAgentID().longValue());
    }

    @Test
    public void testAppUnsubscribeEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[UserID]]></FromUserName>\n" +
                "        <CreateTime>1348831860</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[unsubscribe]]></Event>\n" +
                "        <AgentID>1</AgentID>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppUnsubscribeEvent);

        Assert.assertEquals("toUser", e.getToUserName());
        Assert.assertEquals("UserID", e.getFromUserName());
        Assert.assertEquals(1348831860L, e.getCreateTime().longValue());
        Assert.assertEquals("unsubscribe", e.getEvent());
        Assert.assertEquals(1, e.getAgentID().longValue());
    }

    @Test
    public void testAppEnterAgentEvent() {
        String body = "<xml><ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1408091189</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[enter_agent]]></Event>\n" +
                "<EventKey><![CDATA[]]></EventKey>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppEnterAgentEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("enter_agent", e.getEvent());
        Assert.assertEquals("", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());
    }

    @Test
    public void testAppLocationEvent() {
        String body = "<xml>\n" +
                "   <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "   <FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "   <CreateTime>123456789</CreateTime>\n" +
                "   <MsgType><![CDATA[event]]></MsgType>\n" +
                "   <Event><![CDATA[LOCATION]]></Event>\n" +
                "   <Latitude>23.104105</Latitude>\n" +
                "   <Longitude>113.320107</Longitude>\n" +
                "   <Precision>65.000000</Precision>\n" +
                "   <AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppLocationEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("LOCATION", e.getEvent());
        Assert.assertEquals(1, e.getAgentID().longValue());

        Assert.assertEquals(23.104105, ((AppLocationEvent) e).getLatitude().doubleValue(), 0.000001);
        Assert.assertEquals(113.320107, ((AppLocationEvent) e).getLongitude().doubleValue(), 0.000001);
        Assert.assertEquals(65.000000, ((AppLocationEvent) e).getPrecision().doubleValue(), 0.000001);
    }

    @Test
    public void testAppBatchJobResultEvent() {
        String body = "<xml><ToUserName><![CDATA[wx28dbb14e37208abe]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1425284517</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[batch_job_result]]></Event>\n" +
                "<BatchJob><JobId><![CDATA[S0MrnndvRG5fadSlLwiBqiDDbM143UqTmKP3152FZk4]]></JobId>\n" +
                "<JobType><![CDATA[sync_user]]></JobType>\n" +
                "<ErrCode>0</ErrCode>\n" +
                "<ErrMsg><![CDATA[ok]]></ErrMsg>\n" +
                "</BatchJob>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppBatchJobResultEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("batch_job_result", e.getEvent());

//        Assert.assertNotNull(((AppBatchJobResultEvent) e).getBatchJob());
//        Assert.assertEquals(23.104105, ((AppBatchJobResultEvent) e).getBatchJob().getJobId());
    }

    @Test
    public void testAppCreateUserEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1403610513</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_contact]]></Event>\n" +
                "        <ChangeType>create_user</ChangeType>\n" +
                "        <UserID><![CDATA[zhangsan]]></UserID>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <Department><![CDATA[1,2,3]]></Department>\n" +
                "        <Position><![CDATA[产品经理]]></Position>\n" +
                "        <Mobile>15913215421</Mobile>\n" +
                "        <Gender>1</Gender>\n" +
                "        <Email><![CDATA[zhangsan@gzdev.com]]></Email>\n" +
                "        <Status>1</Status>\n" +
                "        <Avatar><![CDATA[http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0]]></Avatar>\n" +
                "        <EnglishName><![CDATA[zhangsan]]></EnglishName>\n" +
                "        <IsLeader>1</IsLeader>\n" +
                "        <Telephone><![CDATA[020-3456788]]></Telephone>\n" +
                "        <ExtAttr>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[爱好]]></Name>\n" +
                "            <Value><![CDATA[旅游]]></Value>\n" +
                "            </Item>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[卡号]]></Name>\n" +
                "            <Value><![CDATA[1234567234]]></Value>\n" +
                "            </Item>\n" +
                "        </ExtAttr>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppCreateUserEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("change_contact", e.getEvent());
        Assert.assertEquals("create_user", e.getChangeType());

        Assert.assertNotNull(((AppCreateUserEvent) e).getAvatar());
        Assert.assertEquals("zhangsan", ((AppCreateUserEvent) e).getEnglishName());
    }

    @Test
    public void testAppUpdateUserEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1403610513</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_contact]]></Event>\n" +
                "        <ChangeType>update_user</ChangeType>\n" +
                "        <UserID><![CDATA[zhangsan]]></UserID>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <Department><![CDATA[1,2,3]]></Department>\n" +
                "        <Position><![CDATA[产品经理]]></Position>\n" +
                "        <Mobile>15913215421</Mobile>\n" +
                "        <Gender>1</Gender>\n" +
                "        <Email><![CDATA[zhangsan@gzdev.com]]></Email>\n" +
                "        <Status>1</Status>\n" +
                "        <Avatar><![CDATA[http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0]]></Avatar>\n" +
                "        <EnglishName><![CDATA[zhangsan]]></EnglishName>\n" +
                "        <IsLeader>1</IsLeader>\n" +
                "        <Telephone><![CDATA[020-3456788]]></Telephone>\n" +
                "        <ExtAttr>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[爱好]]></Name>\n" +
                "            <Value><![CDATA[旅游]]></Value>\n" +
                "            </Item>\n" +
                "            <Item>\n" +
                "            <Name><![CDATA[卡号]]></Name>\n" +
                "            <Value><![CDATA[1234567234]]></Value>\n" +
                "            </Item>\n" +
                "        </ExtAttr>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppUpdateUserEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("change_contact", e.getEvent());
        Assert.assertEquals("update_user", e.getChangeType());

        Assert.assertNotNull(((AppUpdateUserEvent) e).getAvatar());
        Assert.assertEquals("zhangsan", ((AppUpdateUserEvent) e).getEnglishName());
    }

    @Test
    public void testAppDeleteUserEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1403610513</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_contact]]></Event>\n" +
                "        <ChangeType>delete_user</ChangeType>\n" +
                "        <UserID><![CDATA[zhangsan]]></UserID>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppDeleteUserEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("change_contact", e.getEvent());
        Assert.assertEquals("delete_user", e.getChangeType());
    }

    @Test
    public void testAppCreatePartyEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1403610513</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_contact]]></Event>\n" +
                "        <ChangeType>create_party</ChangeType>\n" +
                "        <Id>2</Id>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <ParentId><![CDATA[1]]></ParentId>\n" +
                "        <Order>1</Order>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppCreatePartyEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("change_contact", e.getEvent());
        Assert.assertEquals("create_party", e.getChangeType());

        Assert.assertEquals(2, ((AppCreatePartyEvent) e).getId().longValue());
        Assert.assertEquals("张三", ((AppCreatePartyEvent) e).getName());
        Assert.assertEquals("1", ((AppCreatePartyEvent) e).getParentId());
        Assert.assertEquals(1, ((AppCreatePartyEvent) e).getOrder().longValue());
    }

    @Test
    public void testAppUpdatePartyEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1403610513</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_contact]]></Event>\n" +
                "        <ChangeType>update_party</ChangeType>\n" +
                "        <Id>2</Id>\n" +
                "        <Name><![CDATA[张三]]></Name>\n" +
                "        <ParentId><![CDATA[1]]></ParentId>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppUpdatePartyEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("change_contact", e.getEvent());
        Assert.assertEquals("update_party", e.getChangeType());

        Assert.assertEquals(2, ((AppUpdatePartyEvent) e).getId().longValue());
        Assert.assertEquals("张三", ((AppUpdatePartyEvent) e).getName());
        Assert.assertEquals("1", ((AppUpdatePartyEvent) e).getParentId());
    }

    @Test
    public void testAppDeletePartyEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1403610513</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_contact]]></Event>\n" +
                "        <ChangeType>delete_party</ChangeType>\n" +
                "        <Id>2</Id>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppDeletePartyEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("change_contact", e.getEvent());
        Assert.assertEquals("delete_party", e.getChangeType());

        Assert.assertEquals(2, ((AppDeletePartyEvent) e).getId().longValue());
    }

    @Test
    public void testAppUpdateTagEvent() {
        String body = "    <xml>\n" +
                "        <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "        <FromUserName><![CDATA[sys]]></FromUserName> \n" +
                "        <CreateTime>1403610513</CreateTime>\n" +
                "        <MsgType><![CDATA[event]]></MsgType>\n" +
                "        <Event><![CDATA[change_contact]]></Event>\n" +
                "        <ChangeType><![CDATA[update_tag]]></ChangeType>\n" +
                "        <TagId>1</TagId>\n" +
                "        <AddUserItems><![CDATA[zhangsan,lisi]]></AddUserItems>\n" +
                "        <DelUserItems><![CDATA[zhangsan1,lisi1]]></DelUserItems>\n" +
                "        <AddPartyItems><![CDATA[1,2]]></AddPartyItems>\n" +
                "        <DelPartyItems><![CDATA[3,4]]></DelPartyItems>\n" +
                "    </xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppUpdateTagEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("change_contact", e.getEvent());
        Assert.assertEquals("update_tag", e.getChangeType());

        Assert.assertEquals(1, ((AppUpdateTagEvent) e).getTagId().longValue());
        Assert.assertEquals("zhangsan,lisi", ((AppUpdateTagEvent) e).getAddUserItems());
        Assert.assertEquals("zhangsan1,lisi1", ((AppUpdateTagEvent) e).getDelUserItems());
        Assert.assertEquals("1,2", ((AppUpdateTagEvent) e).getAddPartyItems());
        Assert.assertEquals("3,4", ((AppUpdateTagEvent) e).getDelPartyItems());
    }

    @Test
    public void testAppClickEvent() {
        String body = "<xml>\n" +
                "<ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>123456789</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[click]]></Event>\n" +
                "<EventKey><![CDATA[EVENTKEY]]></EventKey>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppClickEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("click", e.getEvent());
        Assert.assertEquals("EVENTKEY", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());
    }

    @Test
    public void testAppViewEvent() {
        String body = "<xml>\n" +
                "<ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>123456789</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[view]]></Event>\n" +
                "<EventKey><![CDATA[www.qq.com]]></EventKey>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppViewEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("view", e.getEvent());
        Assert.assertEquals("www.qq.com", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());
    }

    @Test
    public void testAppScanCodePushEvent() {
        String body = "<xml><ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1408090502</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[scancode_push]]></Event>\n" +
                "<EventKey><![CDATA[6]]></EventKey>\n" +
                "<ScanCodeInfo><ScanType><![CDATA[qrcode]]></ScanType>\n" +
                "<ScanResult><![CDATA[1]]></ScanResult>\n" +
                "</ScanCodeInfo>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppScanCodePushEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("scancode_push", e.getEvent());
        Assert.assertEquals("6", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());

        //
    }

    @Test
    public void testAppScanCodeWaitMessageEvent() {
        String body = "<xml><ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1408090606</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[scancode_waitmsg]]></Event>\n" +
                "<EventKey><![CDATA[6]]></EventKey>\n" +
                "<ScanCodeInfo><ScanType><![CDATA[qrcode]]></ScanType>\n" +
                "<ScanResult><![CDATA[2]]></ScanResult>\n" +
                "</ScanCodeInfo>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppScanCodeWaitMessageEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("scancode_waitmsg", e.getEvent());
        Assert.assertEquals("6", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());

        //
    }

    @Test
    public void testAppPicturePhoneEvent() {
        String body = "<xml><ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1408090651</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[pic_sysphoto]]></Event>\n" +
                "<EventKey><![CDATA[6]]></EventKey>\n" +
                "<SendPicsInfo><Count>1</Count>\n" +
                "<PicList><item><PicMd5Sum><![CDATA[1b5f7c23b5bf75682a53e7b6d163e185]]></PicMd5Sum>\n" +
                "</item>\n" +
                "</PicList>\n" +
                "</SendPicsInfo>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppPicturePhoneEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("pic_sysphoto", e.getEvent());
        Assert.assertEquals("6", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());

        //
    }

    @Test
    public void testAppPicturePhoneOrAlbumEvent() {
        String body = "<xml><ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1408090816</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[pic_photo_or_album]]></Event>\n" +
                "<EventKey><![CDATA[6]]></EventKey>\n" +
                "<SendPicsInfo><Count>1</Count>\n" +
                "<PicList><item><PicMd5Sum><![CDATA[5a75aaca956d97be686719218f275c6b]]></PicMd5Sum>\n" +
                "</item>\n" +
                "</PicList>\n" +
                "</SendPicsInfo>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppPicturePhoneOrAlbumEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("pic_photo_or_album", e.getEvent());
        Assert.assertEquals("6", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());

        //
    }

    @Test
    public void testAppPictureWeixinEvent() {
        String body = "<xml><ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1408090816</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[pic_weixin]]></Event>\n" +
                "<EventKey><![CDATA[6]]></EventKey>\n" +
                "<SendPicsInfo><Count>1</Count>\n" +
                "<PicList><item><PicMd5Sum><![CDATA[5a75aaca956d97be686719218f275c6b]]></PicMd5Sum>\n" +
                "</item>\n" +
                "</PicList>\n" +
                "</SendPicsInfo>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppPictureWeixinEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("pic_weixin", e.getEvent());
        Assert.assertEquals("6", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());

        //
    }

    @Test
    public void testAppLocationSelectEvent() {
        String body = "<xml><ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "<FromUserName><![CDATA[FromUser]]></FromUserName>\n" +
                "<CreateTime>1408091189</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[location_select]]></Event>\n" +
                "<EventKey><![CDATA[6]]></EventKey>\n" +
                "<SendLocationInfo><Location_X><![CDATA[23]]></Location_X>\n" +
                "<Location_Y><![CDATA[113]]></Location_Y>\n" +
                "<Scale><![CDATA[15]]></Scale>\n" +
                "<Label><![CDATA[ 广州市海珠区客村艺苑路 106号]]></Label>\n" +
                "<Poiname><![CDATA[]]></Poiname>\n" +
                "</SendLocationInfo>\n" +
                "<AgentID>1</AgentID>\n" +
                "</xml>";

        WxAppEvent e = WxEventFactory.parseAppEvent(body);

        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof AppLocationSelectEvent);

        Assert.assertNotNull(e.getToUserName());
        Assert.assertNotNull(e.getFromUserName());
        Assert.assertNotNull(e.getCreateTime());
        Assert.assertEquals("location_select", e.getEvent());
        Assert.assertEquals("6", e.getEventKey());
        Assert.assertEquals(1, e.getAgentID().longValue());

        //
    }

}

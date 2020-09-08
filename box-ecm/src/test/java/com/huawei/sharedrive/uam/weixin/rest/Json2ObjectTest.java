/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.rest;

import com.huawei.sharedrive.uam.core.web.JsonMapper;
import com.huawei.sharedrive.uam.teamspace.domain.RestUserTeamSpaceList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import pw.cdmi.core.utils.JsonUtils;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>测试与JSON数据的转换</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/28
 ************************************************************/
@RunWith(JUnit4.class)
public class Json2ObjectTest {
    @Test
    public void testToWeixinSuiteTokenInfo() {
        String json = "    {\n" +
                "        \"suite_access_token\":\"61W3mEpU66027wgNZ_MhGHNQDHnFATkDa9-2llqrMBjUwxRSNPbVsMmyD-yq8wZETSoE5NQgecigDrSHkPtIYA\",\n" +
                "        \"expires_in\":7200\n" +
                "    }";

        SuiteAccessTokenInfo tokenInfo = JsonMapper.nonEmptyCamelMapper().fromJson(json, SuiteAccessTokenInfo.class);

        Assert.assertNotNull(tokenInfo);
        Assert.assertEquals("61W3mEpU66027wgNZ_MhGHNQDHnFATkDa9-2llqrMBjUwxRSNPbVsMmyD-yq8wZETSoE5NQgecigDrSHkPtIYA", tokenInfo.getSuiteAccessToken());
        Assert.assertEquals(7200, tokenInfo.getExpiresIn().longValue());
    }

    @Test
    public void testToPermanentCodeInfo() {
        String json = "    {\n" +
                "        \"access_token\": \"xxxxxx\", \n" +
                "        \"expires_in\": 7200, \n" +
                "        \"permanent_code\": \"xxxx\", \n" +
                "        \"auth_corp_info\": \n" +
                "        {\n" +
                "            \"corpid\": \"xxxx\",\n" +
                "            \"corp_name\": \"name\",\n" +
                "            \"corp_type\": \"verified\",\n" +
                "            \"corp_square_logo_url\": \"yyyyy\",\n" +
                "            \"corp_user_max\": 50,\n" +
                "            \"corp_agent_max\": 30,\n" +
                "            \"corp_full_name\":\"full_name\",\n" +
                "            \"verified_end_time\":1431775834,\n" +
                "            \"subject_type\": 1,\n" +
                "            \"corp_wxqrcode\": \"zzzzz\"\n" +
                "        },\n" +
                "        \"auth_info\":\n" +
                "        {\n" +
                "            \"agent\" :\n" +
                "            [\n" +
                "                {\n" +
                "                    \"agentid\":1,\n" +
                "                    \"name\":\"NAME\",\n" +
                "                    \"round_logo_url\":\"xxxxxx\",\n" +
                "                    \"square_logo_url\":\"yyyyyy\",\n" +
                "                    \"appid\":1,\n" +
                "                    \"privilege\":\n" +
                "                    {\n" +
                "                        \"level\":1,\n" +
                "                        \"allow_party\":[1,2,3],\n" +
                "                        \"allow_user\":[\"zhansan\",\"lisi\"],\n" +
                "                        \"allow_tag\":[1,2,3],\n" +
                "                        \"extra_party\":[4,5,6],\n" +
                "                        \"extra_user\":[\"wangwu\"],\n" +
                "                        \"extra_tag\":[4,5,6]\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"agentid\":2,\n" +
                "                    \"name\":\"NAME2\",\n" +
                "                    \"round_logo_url\":\"xxxxxx\",\n" +
                "                    \"square_logo_url\":\"yyyyyy\",\n" +
                "                    \"appid\":5\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        \"auth_user_info\":\n" +
                "        {\n" +
                "            \"email\":\"xxxx@aaa.com\",\n" +
                "            \"mobile\":\"1234567890\",\n" +
                "            \"userid\":\"aa\"\n" +
                "        }\n" +
                "    }";

        PermanentCodeInfo codeInfo = JsonMapper.nonEmptyCamelMapper().fromJson(json, PermanentCodeInfo.class);

        Assert.assertNotNull(codeInfo);
        Assert.assertEquals("xxxxxx", codeInfo.getAccessToken());
        Assert.assertEquals("xxxx", codeInfo.getPermanentCode());
        Assert.assertEquals(7200, codeInfo.getExpiresIn().longValue());

        Assert.assertNotNull(codeInfo.getAuthCorpInfo());
        Assert.assertEquals("name", codeInfo.getAuthCorpInfo().getCorpName());
    }

    @Test
    public void testToQueryUserResponse() {
        String json = "{\n" +
                "    \"errcode\": 0,\n" +
                "    \"errmsg\": \"ok\",\n" +
                "    \"userlist\": [\n" +
                "        {\n" +
                "            \"userid\": \"zhangsan\",\n" +
                "            \"name\": \"李四\",\n" +
                "            \"department\": [1, 2],\n" +
                "            \"order\": [1, 2],\n" +
                "            \"position\": \"后台工程师\",\n" +
                "            \"mobile\": \"15913215421\",\n" +
                "            \"gender\": \"1\",\n" +
                "            \"email\": \"zhangsan@gzdev.com\",\n" +
                "            \"isleader\": 0,\n" +
                "            \"avatar\":           \"http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0\",\n" +
                "            \"telephone\": \"020-123456\",\n" +
                "            \"english_name\": \"jackzhang\",\n" +
                "            \"status\": 1,\n" +
                "            \"extattr\": {\"attrs\":[{\"name\":\"爱好\",\"value\":\"旅游\"},{\"name\":\"卡号\",\"value\":\"1234567234\"}]}\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        QueryUserResponse res = JsonMapper.nonEmptyCamelMapper().fromJson(json, QueryUserResponse.class);

        Assert.assertNotNull(res);
        Assert.assertEquals(0, res.getErrcode().longValue());
        Assert.assertEquals("ok", res.getErrmsg());

        Assert.assertNotNull(res.getUserlist());
        Assert.assertEquals(1, res.getUserlist().size());
        Assert.assertEquals("zhangsan", res.getUserlist().get(0).getUserid());

        Assert.assertNotNull(res.getUserlist().get(0).getExtattr());
        Assert.assertNotNull(res.getUserlist().get(0).getExtattr().getAttrs());
        Assert.assertEquals(2, res.getUserlist().get(0).getExtattr().getAttrs().size());
        Assert.assertEquals("爱好", res.getUserlist().get(0).getExtattr().getAttrs().get(0).getName());
        Assert.assertEquals("1234567234", res.getUserlist().get(0).getExtattr().getAttrs().get(1).getValue());
    }

    @Test
    public void testToRestUserTeamSpaceList() {
        String json = "{\"memberships\":[\n" +
                "\n" +
                "{\"id\":2,\"teamId\":122,\"teamRole\":\"member\",\"role\":\"viewer\",\"member\":{\"id\":\"128\",\"type\":\"user\"},\"teamspace\":{\"id\":122,\"name\":\"测试组\",\"status\":0,\"curNumbers\":2,\"ownedBy\":-2,\"createdBy\":-2,\"createdAt\":1504001355000,\"spaceQuota\":-1,\"spaceUsed\":0,\"maxMembers\":-1,\"maxVersions\":-1,\"regionId\":0}}\n" +
                "\n" +
                "\n" +
                "],\"limit\":99,\"offset\":0,\"totalCount\":1}";

        RestUserTeamSpaceList restTeamSpaceList = JsonUtils.stringToObject(json, RestUserTeamSpaceList.class);

        Assert.assertNotNull(restTeamSpaceList);
    }

    @Test
    public void testLoginInfo() {
        String json = "{\n" +
                "       \"usertype\": 1,\n" +
                "       \"user_info\":{\n" +
                "           \"userid\":\"xxxx\",\n" +
                "           \"name\":\"xxxx\",\n" +
                "           \"avatar\":\"xxxx\",\n" +
                "           \"email\":\"xxxx\"\n" +
                "       },\n" +
                "       \"corp_info\":{\n" +
                "           \"corpid\":\"wx6c698d13f7a409a4\"\n" +
                "        },\n" +
                "       \"agent\":[\n" +
                "           {\"agentid\":0,\"auth_type\":1},\n" +
                "           {\"agentid\":1,\"auth_type\":1},\n" +
                "           {\"agentid\":2,\"auth_type\":1}\n" +
                "       ],\n" +
                "       \"auth_info\":{\n" +
                "           \"department\":[\n" +
                "               {\n" +
                "                   \"id\":\"2\",\n" +
                "                   \"writable\":\"true\"\n" +
                "               }\n" +
                "           ]\n" +
                "       }\n" +
                "    }";

        LoginInfo loginInfo = JsonMapper.nonEmptyCamelMapper().fromJson(json, LoginInfo.class);

        Assert.assertNotNull(loginInfo);
        Assert.assertNotNull(loginInfo.getUsertype());
        Assert.assertNotNull(loginInfo.getUserInfo());
        Assert.assertNotNull(loginInfo.getUserInfo().getUserid());
        Assert.assertNotNull(loginInfo.getCorpInfo());
        Assert.assertNotNull(loginInfo.getCorpInfo().getCorpid());
        Assert.assertNotNull(loginInfo.getAgent());
        Assert.assertNotNull(loginInfo.getAgent().get(0).getAgentid());
        Assert.assertNotNull(loginInfo.getAuthInfo());
        Assert.assertNotNull(loginInfo.getAuthInfo().getDepartment().get(0));
        Assert.assertNotNull(loginInfo.getAuthInfo().getDepartment().get(0).getId());
    }


    @Test
    public void testWxMpUserInfo() {
        String json = "{\"openId\":\"oozYN0ds9gnk6UL_jcV-VUgfmu7k\",\"nickName\":\"永往直前\",\"gender\":1,\"language\":\"zh_CN\",\"city\":\"Chengdu\",\"province\":\"Sichuan\",\"country\":\"China\",\"avatarUrl\":\"https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83epdRic4libntTWLpyoXuyXCh0vOpZn9Qo8sxkbgMUP7eoWiaqMXNuR7YkkBY7hcg6UbKicicWJpc8sJJRg/0\",\"unionId\":\"oC4P41G8Q6luRIsAl_Rb9ApgDJLU\",\"watermark\":{\"timestamp\":1509890829,\"appid\":\"wx44a25d378f40e564\"}}";

        WxMpUserInfo userInfo = JsonMapper.nonEmptyMapper().fromJson(json, WxMpUserInfo.class);

        Assert.assertNotNull(userInfo);
        Assert.assertNotNull(userInfo.getOpenId());
        Assert.assertNotNull(userInfo.getUnionId());
        Assert.assertNotNull(userInfo.getNickName());
        Assert.assertNotNull(userInfo.getGender());
        Assert.assertNotNull(userInfo.getCity());
        Assert.assertNotNull(userInfo.getProvince());
        Assert.assertNotNull(userInfo.getWatermark());
        Assert.assertNotNull(userInfo.getWatermark().getTimestamp());
        Assert.assertNotNull(userInfo.getWatermark().getAppid());
    }
}

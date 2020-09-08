/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.weixin.service.impl;

import com.huawei.sharedrive.uam.core.web.JsonMapper;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.PermanentCodeInfo;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信事件处理逻辑测试</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/8/2
 ************************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test*.xml" })
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Component
public class WeixinEnterpriseServiceImplTest {
    @Autowired
    WxEnterpriseService weixinEnterpriseService;

    @Test
    public void testCreateEnterprise() {
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

//        WxEnterpriseService wxEnterpriseService = new WxEnterpriseService();

        PermanentCodeInfo codeInfo = JsonMapper.nonEmptyCamelMapper().fromJson(json, PermanentCodeInfo.class);
        WxEnterprise enterprise = WxDomainUtils.toWxEnterprise(codeInfo);
        weixinEnterpriseService.create(enterprise);

        String corpId = "xxxx";
        WxEnterprise corp = weixinEnterpriseService.get(corpId);

        Assert.assertNotNull(corp);
    }
}


/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.test;

import org.junit.Assert;
import org.junit.Test;

/************************************************************
 * @Description:
 * <pre></pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/12/29
 ************************************************************/
public class StringTest {
    @Test
    public void testReplaceAll() {
        String template = "您好！您的管理员账户已经生成。<br>用户名: ${username}<br> 密码: ${password}<br>请尽快登录<a href=\"http://www.jmapi.cn/ecm\">www.jmapi.cn/ecm</a>修改。";
        testReplace(template);

        template = "您好，你的微信文件宝已经开通：<br>你的账号如下：<br>企业名称：${enterpriseName}<br>用户名：${username}<br> 密码: ${password}<br>您可以在微信小程序中搜索“企业云盘”，进入小程序进行绑定。绑定后你可以访问<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。通过微信扫描登陆管理您的知识文档，与同事进行工作协同。您也可以登陆<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。首次登陆通过企业微信扫描登陆，然后在设置->用户信息中绑定您的微信号码。";
        testReplace(template);
    }

    private void testReplace(String template) {
        String message = template.replaceAll("(\\$\\{username})", "user*-!@*+");
        message = message.replaceAll("(\\$\\{password})", "pwd1");

        System.out.println(message);
        Assert.assertNotEquals(-1, message.indexOf("user"));
        Assert.assertNotEquals(-1, message.indexOf("pwd"));
    }
}

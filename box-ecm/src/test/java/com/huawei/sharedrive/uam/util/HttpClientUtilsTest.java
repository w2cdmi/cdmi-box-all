/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>下载工具类测试</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/11/1
 ************************************************************/
@RunWith(JUnit4.class)
public class HttpClientUtilsTest {
    @Test
    public void testDownloadAsStream() {
        byte[] bytes = HttpClientUtils.downloadAsStream("http://p.qlogo.cn/bizmail/jp5RoMm2ricfCwsiabdM3zlZ8c8ubXicCNgocA9UiawmsW2CrfKhrviaPzQ/0");
        Assert.assertNotNull(bytes);
        Assert.assertNotEquals(bytes.length, 0);
    }
}

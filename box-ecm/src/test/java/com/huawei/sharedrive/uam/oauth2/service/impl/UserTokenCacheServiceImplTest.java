
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package com.huawei.sharedrive.uam.oauth2.service.impl;

import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.UserTokenCacheService;
import com.huawei.sharedrive.uam.util.RandomKeyGUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.cache.CacheClientFactory;
import pw.cdmi.core.utils.SpringContextUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;


/************************************************************
 * @Description:
 * <pre>UserTokenCacheServiceImpl测试类</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2018/1/16
 ************************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration

public class UserTokenCacheServiceImplTest {
    AnnotationConfigApplicationContext context;
    UserTokenCacheService userTokenCacheService;

    @Before
    public void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//        context.addApplicationListener();
        context.register(Config.class);
        context.register(UserTokenCacheServiceImpl.class);
        context.refresh();
//        Assert.assertNotNull(ac.getBean("cacheClient"));
        Assert.assertNotNull(context);
        userTokenCacheService = (UserTokenCacheService)context.getBean("userTokenCacheServiceImpl");
        Assert.assertNotNull(userTokenCacheService);
    }

    @Test
    public void testTimeout() {
        String accessToken = RandomKeyGUID.getSecureRandomGUID();
        String refreshToken = RandomKeyGUID.getSecureRandomGUID();
        long cloudUserId = 10;

        UserToken token = new UserToken();
        token.setToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setDeviceAddress("127.0.0.1");
        token.setCloudUserId(cloudUserId);

        userTokenCacheService.saveUserToken(token);

        UserToken t = userTokenCacheService.getUserToken(accessToken);
        Assert.assertNotNull(t);
        Assert.assertEquals(token.getDeviceAddress(), t.getDeviceAddress());

        try {
            //accessToken过期
            Thread.sleep(6000);
            t = userTokenCacheService.getUserToken(accessToken);
            Assert.assertNull(t);

            //refresh未过期
            t = userTokenCacheService.getUserTokenByRefreshToken(refreshToken);
            Assert.assertNotNull(t);
            Assert.assertEquals(token.getDeviceAddress(), t.getDeviceAddress());

            //accessToken过期
            Thread.sleep(90000);
            t = userTokenCacheService.getUserTokenByRefreshToken(refreshToken);
            Assert.assertNull(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLogout() {
        String accessToken = RandomKeyGUID.getSecureRandomGUID();
        String refreshToken = RandomKeyGUID.getSecureRandomGUID();
        long cloudUserId = 10;

        UserToken token = new UserToken();
        token.setToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setDeviceAddress("127.0.0.1");
        token.setCloudUserId(cloudUserId);

        userTokenCacheService.saveUserToken(token);

        UserToken t = userTokenCacheService.getUserToken(accessToken);
        Assert.assertNotNull(t);
        Assert.assertEquals(token.getDeviceAddress(), t.getDeviceAddress());

        //测试注销access
        token.setExpiredAt(null);//设置为null, saveUserToken方法会自动赋默认值
        userTokenCacheService.saveUserToken(token);
        userTokenCacheService.deleteToken(accessToken);
        Assert.assertNull(userTokenCacheService.getUserToken(accessToken));
        Assert.assertNull(userTokenCacheService.getUserToken(refreshToken));

        //测试注销userId
        token.setExpiredAt(null);//设置为null, saveUserToken方法会自动赋默认值
        userTokenCacheService.saveUserToken(token);
        userTokenCacheService.deleteUserToken(cloudUserId);
        Assert.assertNull(userTokenCacheService.getUserToken(accessToken));
        Assert.assertNull(userTokenCacheService.getUserToken(refreshToken));    }

    @Configuration
    static class Config {
        @Bean(name="cacheClient")
        CacheClient getCacheClient() {
            return CacheClientFactory.getInstance(
                    "uam_cache",
                    "127.0.0.1",
                    11211,
                    50,
                    2000,
                    60000,
                    600000,
                    "uam_",
                    true,
                    false,
                    true,
                    true,
                    30000
            );
        }

        @Bean()
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            PropertySourcesPlaceholderConfigurer  configurer = new PropertySourcesPlaceholderConfigurer ();
            configurer.setLocation(new ClassPathResource("application.properties"));
            return configurer;
        }
    }
}

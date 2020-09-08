package com.huawei.sharedrive.app.openapi.domain.user.test;

import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.user.domain.User;

public class RestUserCreateRequestTest
{
    @Test
    public void junitTest()
    {
        RestUserCreateRequest rq = new RestUserCreateRequest();
        rq.getCreatedAt();
        rq.setCreatedAt(new Date());
        rq.getDescription();
        rq.getEmail();
        rq.getFileCount();
        rq.getId();
        rq.getLoginName();
        rq.getMaxVersions();
        rq.getName();
        rq.getRegionId();
        rq.getSpaceQuota();
        rq.getSpaceUsed();
        rq.getStatus();
        rq.setDescription("description");
        rq.setEmail("huawei@qq.com");
        rq.setFileCount(1234L);
        rq.setId(1234L);
        rq.setLoginName("loginName");
        rq.setMaxVersions(123);
        rq.setName("name");
        rq.setRegionId((byte) 2);
        rq.setSpaceQuota(1234L);
        rq.setSpaceUsed(1234L);
        rq.setStatus((byte) 2);
        rq.setDefaultValue();
        rq.transUserFromCreate(new User());
        rq.setCreatedAt(null);
        rq.getCreatedAt();
        try
        {
            rq.checkUserAddParamter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void copyFromTest()
    {
        RestUserCreateRequest rq = new RestUserCreateRequest();
        User user = new User();
        user.setId(123L);
        user.setLoginName("tom");
        user.setName("tom");
        user.setEmail("huawei@qq.com");
        user.setSpaceQuota(123L);
        user.setSpaceUsed(123L);
        user.setStatus("1");
        user.setRegionId(123);
//        user.setDepartment("1");
        user.setFileCount(123L);
        rq.copyFrom(user);
    }
}

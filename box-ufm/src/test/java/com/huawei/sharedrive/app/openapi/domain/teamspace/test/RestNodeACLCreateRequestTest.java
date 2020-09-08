package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.openapi.domain.teamspace.Resource;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestNodeACLCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMember;

import java.util.ArrayList;
import java.util.List;

public class RestNodeACLCreateRequestTest
{
    @Test
    public void junitTest()
    {
        try
        {
            RestNodeACLCreateRequest rr = new RestNodeACLCreateRequest();
            rr.setRole("admin");
            rr.getRole();
            rr.setUserList(null);
            rr.setResource(null);
            rr.getResource();
            rr.getResourceNodeId();
            rr.getResourceOwnerId();
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void junitTest1()
    {
        try
        {
            RestNodeACLCreateRequest rr = new RestNodeACLCreateRequest();
            rr.setRole("admin");
            rr.getRole();

            List<RestTeamMember> userList = new ArrayList<>();
            RestTeamMember user = new RestTeamMember();
            user.setName("group");
            user.setType("type");
            user.setLoginName("name");
            user.setId("123");
            user.setDescription("qazwsx");
            userList.add(user);
            rr.setUserList(userList);

            Resource resource = new Resource();
            resource.setNodeId(124L);
            resource.setOwnerId(123L);
            rr.setResource(resource);
            rr.getResource();
            rr.getResourceNodeId();
            rr.getResourceOwnerId();
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void junitTest2()
    {
        try
        {
            RestNodeACLCreateRequest rr = new RestNodeACLCreateRequest();
            rr.setRole("");

            List<RestTeamMember> userList = new ArrayList<>();
            RestTeamMember user = new RestTeamMember();
            user.setName("group");
            user.setType("type");
            user.setLoginName("name");
            user.setId("123");
            user.setDescription("qazwsx");
            userList.add(user);
            rr.setUserList(userList);

            Resource resource = new Resource();
            resource.setOwnerId(123L);
            rr.setResource(resource);
            rr.getResource();
            rr.getResourceNodeId();
            rr.getResourceOwnerId();
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void junitTest3()
    {
        try
        {
            RestNodeACLCreateRequest rr = new RestNodeACLCreateRequest();
            rr.setRole("234234");

            List<RestTeamMember> userList = new ArrayList<>();
            RestTeamMember user = new RestTeamMember();
            user.setName("group");
            user.setLoginName("name");
            user.setId("123");
            user.setDescription("qazwsx");
            userList.add(user);
            rr.setUserList(userList);

            Resource resource = new Resource();
            resource.setOwnerId(123L);
            rr.setResource(resource);
            rr.getResource();
            rr.getResourceNodeId();
            rr.getResourceOwnerId();
            rr.checkParameter();
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

package com.huawei.sharedrive.app.openapi.domain.teamspace.test;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.teamspace.SetTeamSpaceAttrRequest;

public class SetTeamSpaceAttrRequestTest
{
    @Test
    public void checkParameterTest()
    {
        try
        {
            SetTeamSpaceAttrRequest ss = new SetTeamSpaceAttrRequest();
            ss.setName("niubi");
            ss.setValue("ASDFG");
            ss.getName();
            ss.getValue();
            ss.checkParameter();
            ss.setName("");
            ss.checkParameter();
            ss.setName("niubi");
            ss.setValue("");
            ss.checkParameter();
            ss.setName("uploadNotice");
            ss.setValue("ASDFG");
            ss.checkParameter();
            ss.setName("u3sdfce");
            ss.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkParameterTest1()
    {
        try
        {
            SetTeamSpaceAttrRequest ss = new SetTeamSpaceAttrRequest();
            ss.setName("");
            ss.setValue("ASDFG");
            ss.checkParameter();
            ss.setName("niubi");
            ss.setValue("");
            ss.checkParameter();
            ss.setName("uploadNotice");
            ss.setValue("ASDFG");
            ss.checkParameter();
            ss.setName("u3sdfce");
            ss.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkParameterTest2()
    {
        try
        {
            SetTeamSpaceAttrRequest ss = new SetTeamSpaceAttrRequest();
            ss.setName("niubi");
            ss.setValue("");
            ss.checkParameter();
            ss.setName("uploadNotice");
            ss.setValue("ASDFG");
            ss.checkParameter();
            ss.setName("u3sdfce");
            ss.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkParameterTest3()
    {
        try
        {
            SetTeamSpaceAttrRequest ss = new SetTeamSpaceAttrRequest();
            ss.setName("uploadNotice");
            ss.setValue("ASDFG");
            ss.checkParameter();
            ss.setName("u3sdfce");
            ss.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkParameterTest4()
    {
        try
        {
            SetTeamSpaceAttrRequest ss = new SetTeamSpaceAttrRequest();
            ss.setName("u3sdfce");
            ss.setValue("ASDFG");
            ss.checkParameter();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

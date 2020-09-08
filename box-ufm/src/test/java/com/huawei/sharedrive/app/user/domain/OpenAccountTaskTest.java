package com.huawei.sharedrive.app.user.domain;

import java.util.Date;

import org.junit.Test;

public class OpenAccountTaskTest
{
    @Test
    public void openAccountTaskTest()
    {
        OpenAccountTask open = new OpenAccountTask();
        open.equals(null);
        open.getBeginDate();
        open.getClass();
        open.getEndDate();
        open.getFail();
        open.getSearchBase();
        open.getSearchFiler();
        open.getSuccess();
    }
    
    @Test
    public void openAccountTaskTest2()
    {
        OpenAccountTask open = new OpenAccountTask();
        open.setBeginDate(new Date());
        open.setEndDate(new Date());
        open.setFail(0);
        open.setSearchBase("s");
        open.setSearchFiler("tset");
        open.setSuccess(0);
    }
}

package com.huawei.sharedrive.isystem.account.dao.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.account.dao.AccountDao;

import pw.cdmi.core.utils.SpringContextUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext*test.xml"})
public class AccountDaoImplTest
{
    
    @Test
    @Transactional
    @Rollback(true)
    public void test()
    {
        try
        {
            System.out.println("yues");
            AccountDao dao = (AccountDaoImpl)SpringContextUtil.getBean("accountDao");
            dao.getFilterd(null);
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void test1()
    {
        try
        {
            System.out.println("yues");
            AccountDao dao = (AccountDaoImpl)SpringContextUtil.getBean("accountDao");
            dao.getFilterdCount(null);
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }
    
}

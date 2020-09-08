package com.huawei.sharedrive.app.test.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.plugins.preview.dao.AccountWatermarkDao;
import com.huawei.sharedrive.app.plugins.preview.domain.AccountWatermark;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;

public class AccountWatermarkDaoTest extends AbstractSpringTest
{
    
    @Autowired
    private AccountWatermarkDao accountWatermarkDao;
    
    @Test
    public void testReplace() throws Exception
    {
        File water = new File("e:/watermark.png");
        byte[] buf = new byte[1024 * 1024];
        byte[] data;
        BufferedInputStream in = null;
        try
        {
            in = new BufferedInputStream(new FileInputStream(water));
            int n = 0;
            int pos = 0;
            while (n != -1)
            {
                pos += n;
                n = in.read(buf, pos, 1024);
            }
            data = Arrays.copyOf(buf, pos);
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        AccountWatermark accountWatermark = new AccountWatermark();
        accountWatermark.setAccountId(0);
        accountWatermark.setWatermark(data);
        accountWatermark.setLastConfigTime(new Date());
        accountWatermarkDao.replace(accountWatermark);
    }
    
    @Test
    public void testGet() throws Exception
    {
        AccountWatermark accountWatermark = accountWatermarkDao.get(0);
        System.out.println(accountWatermark.getLastConfigTime());
        File water = new File("e:/watermark0.png");
        byte[] data = accountWatermark.getWatermark();
        BufferedOutputStream out = null;
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(water));
            out.write(data);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }
}

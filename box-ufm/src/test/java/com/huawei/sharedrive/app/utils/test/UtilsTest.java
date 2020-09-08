package com.huawei.sharedrive.app.utils.test;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.zip.ZipOutputStream;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.utils.Utils;

public class UtilsTest
{
    @Test
    public void isIntegerStrTest()
    {
        String integerStr = "1234";
        boolean integerStr2 = Utils.isIntegerStr(integerStr);
        System.out.println(integerStr2);
        Assert.assertEquals(true, integerStr2);
        String integerStr3 = "huawei@123";
        boolean integerStr4 = Utils.isIntegerStr(integerStr3);
        System.out.println(integerStr4);
        Assert.assertEquals(false, integerStr4);
    }
    
    @Test
    public void notNegativeIntegerStrTest()
    {
        String integerStr = "123";
        boolean integerStr2 = Utils.notNegativeIntegerStr(integerStr);
        System.out.println(integerStr2);
        Assert.assertEquals(true, integerStr2);
        Utils.notNegativeIntegerStr("0");
        Utils.notNegativeIntegerStr("dsdfsds");
        String integerStr3 = "-123";
        boolean integerStr4 = Utils.notNegativeIntegerStr(integerStr3);
        System.out.println(integerStr4);
        Assert.assertEquals(false, integerStr4);
    }
    
    @Test
    public void parseIntTest()
    {
        int parseInt = Utils.parseInt(new Integer(88), 99);
        System.out.println(parseInt);
        Assert.assertEquals(88, parseInt);
        int parseInt2 = Utils.parseInt("88", 99);
        Utils.parseInt(null, 99);
        Utils.parseInt("", 99);
        Utils.parseInt("86666666666666666666666666666666666666668", 99);
        System.out.println(parseInt2);
        Assert.assertEquals(88, parseInt2);
    }
    
    @Test
    public void parseLongTest()
    {
        long parseInt = Utils.parseLong(new Long(8888888L), 9999999L);
        System.out.println(parseInt);
        Assert.assertEquals(8888888L, parseInt);
        long parseInt2 = Utils.parseLong("8888888", 9999999L);
        Utils.parseLong(null, 9999999L);
        Utils.parseLong("", 9999999L);
        Utils.parseLong("88444444444444444444444444444444488888", 9999999L);
        System.out.println(parseInt2);
        Assert.assertEquals(8888888, parseInt2);
    }
    
    @Test
    public void closeTest()
    {
        ZipOutputStream zos = null;
        Utils.close(zos);
        zos = new ZipOutputStream(new OutputStream()
        {
            
            @Override
            public void write(int b) throws IOException
            {
                // TODO Auto-generated method stub
                
            }
        });
        Utils.close(zos);
        Connection conn1 = null;
        ResultSet set1 = null;
        Statement state1 = null;
        ResultSet set2 = null;
        Statement state2 = null;
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/sysdb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&socketTimeout=1800000",
                "root",
                "123456");
            Statement state = conn.createStatement();
            String sql = "select * from admin";
            ResultSet set = state.executeQuery(sql);
            conn1 = conn;
            set1 = set;
            state1 = state;
            set2 = set;
            state2 = state;
            Utils.close(conn);
            Utils.close(set);
            Utils.close(state);
            conn = null;
            state = null;
            set = null;
            Utils.close(conn);
            Utils.close(state);
            Utils.close(set);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            Utils.close(set2, state2);
            Utils.close(set1, state1, conn1);
        }
    }
    
    @Test
    public void isIntegerStrTest1()
    {
        Utils.isIntegerStr(null);
        Utils.isIntegerStr("11111111111111.1111");
        Utils.isIntegerStr("-82147483648");
        Utils.isIntegerStr("214748364");
    }
    
}

package com.huawei.sharedrive.app.utils.test;

import junit.framework.Assert;

import org.junit.Test;
import pw.cdmi.core.utils.SqlUtils;


public class SqlUtilsTest
{
    @Test
    public void stringToSqlLikeFieldsTest()
    {
        String input = "select t.* from admin t where t.loginName like 'ad_%'";
        String sqlLikeFields = SqlUtils.stringToSqlLikeFields(input);
        System.out.println(sqlLikeFields);
        Assert.assertEquals("select t.* from admin t where t.loginName like 'ad\\_\\%'", sqlLikeFields);
    }
}

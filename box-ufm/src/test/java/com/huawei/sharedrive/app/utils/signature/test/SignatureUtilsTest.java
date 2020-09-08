package com.huawei.sharedrive.app.utils.signature.test;

import java.util.Date;

import org.junit.Test;
import pw.cdmi.common.util.signature.SignatureUtils;


public class SignatureUtilsTest
{
    @Test
    public void getSignatureTest()
    {
        String signature = SignatureUtils.getSignature("huawei@123", new Date().toString());
        System.out.println(signature);
    }
}

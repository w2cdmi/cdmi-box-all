package pw.cdmi.box.disk.test.utils;

import org.junit.Test;

import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.utils.HashPasswordUtil;

public class UserPwdTest
{
    @Test
    public void  testGetSaltPassword() throws Exception
    {
        HashPassword hashPassword = HashPasswordUtil.generateHashPassword("cse@disk123");
        
        System.out.println(hashPassword.getHashPassword());
        System.out.println(hashPassword.getSalt());
    }
}

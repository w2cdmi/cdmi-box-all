package com.huawei.sharedrive.isystem.util.test;

import org.junit.Assert;
import org.junit.Test;

import com.huawei.sharedrive.isystem.util.Validate;

public class ValidateTest
{
    @Test
    public void validateFullTest()
    {
        boolean validateFull = Validate.validateFull("s345435345", "^[a-zA-Z]{1}[a-zA-Z0-9]+$");
        System.out.println(validateFull);
        Assert.assertEquals(true, validateFull);
    }
    
    @Test
    public void validateFullTest1()
    {
        boolean validateFull = Validate.validateFull("", "^[a-zA-Z]{1}[a-zA-Z0-9]+$");
        System.out.println(validateFull);
        Assert.assertEquals(false, validateFull);
    }
    
    @Test
    public void validateFullTest2()
    {
        try
        {
            boolean validateFull = Validate.validateFull(null, "^[a-zA-Z]{1}[a-zA-Z0-9]+$");
            System.out.println(validateFull);
        }
        catch (Exception e)
        {
            
        }
    }
    
    @Test
    public void validateFullTest3()
    {
        boolean validateFull = Validate.validateFull("s345435345", "");
        System.out.println(validateFull);
        Assert.assertEquals(false, validateFull);
    }
    
    @Test
    public void valiDateUserNameTest()
    {
        boolean name = Validate.valiDateUserName("34535");
        Assert.assertEquals(true, name);
    }
    
    @Test
    public void valiDateUserNameTest1()
    {
        try
        {
            boolean name = Validate.valiDateUserName("");
            Assert.assertEquals(false, name);
        }
        catch (Exception e)
        {
            
        }
    }
    
    @Test
    public void valiDateUserNameTest2()
    {
        try
        {
            Validate.valiDateUserName(null);
        }
        catch (Exception e)
        {
            
        }
    }
    
    @Test
    public void valiDateUserNameTest3()
    {
        try
        {
            boolean name = Validate.valiDateUserName("a");
            Assert.assertEquals(false, name);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

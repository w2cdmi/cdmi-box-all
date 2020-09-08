package com.huawei.sharedrive.app.files.domain.test;

import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.files.domain.ObjectReference;

public class ObjectReferenceTest
{
    @Test
    public void getBlockMD5Test()
    {
        ObjectReference obj = new ObjectReference();
        String blockMD5 = obj.getBlockMD5();
        System.out.println(blockMD5);
    }
    
    @Test
    public void getLastDeleteTimeTest()
    {
        ObjectReference obj = new ObjectReference();
        Date blockMD5 = obj.getLastDeleteTime();
        System.out.println(blockMD5);
    }
    
    @Test
    public void setLastDeleteTimeTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setLastDeleteTime(null);
    }
    
    @Test
    public void setLastDeleteTimeTest2()
    {
        ObjectReference obj = new ObjectReference();
        obj.setLastDeleteTime(new Date());
    }
    
    @Test
    public void getIdTest()
    {
        ObjectReference obj = new ObjectReference();
        String id = obj.getId();
        System.out.println(id);
    }
    
    @Test
    public void getSecurityVersionTest()
    {
        ObjectReference obj = new ObjectReference();
        String id = obj.getSecurityVersion();
        System.out.println(id);
    }
    
    @Test
    public void getSha1Test()
    {
        ObjectReference obj = new ObjectReference();
        String id = obj.getSha1();
        System.out.println(id);
    }
    
    @Test
    public void toStringTest()
    {
        ObjectReference obj = new ObjectReference();
        String id = obj.toString();
        System.out.println(id);
    }
    
    @Test
    public void equalsTest()
    {
        ObjectReference obj = new ObjectReference();
        boolean id = obj.equals(null);
        System.out.println(id);
    }
    
    @Test
    public void getClassTest()
    {
        ObjectReference obj = new ObjectReference();
        Class<? extends ObjectReference> class1 = obj.getClass();
        System.out.println(class1);
    }
    
    @Test
    public void getRefCountTest()
    {
        ObjectReference obj = new ObjectReference();
        int id = obj.getRefCount();
        System.out.println(id);
    }
    
    @Test
    public void getResourceGroupIdTest()
    {
        ObjectReference obj = new ObjectReference();
        int id = obj.getResourceGroupId();
        System.out.println(id);
    }
    
    @Test
    public void getTableSuffixTest()
    {
        ObjectReference obj = new ObjectReference();
        int id = obj.getTableSuffix();
        System.out.println(id);
    }
    
    @Test
    public void getSHA1_LENGTHTest()
    {
        ObjectReference obj = new ObjectReference();
        int id = obj.SHA1_LENGTH;
        System.out.println(id);
    }
    
    @Test
    public void getSizeTest()
    {
        ObjectReference obj = new ObjectReference();
        long id = obj.getSize();
        System.out.println(id);
    }
    
    @Test
    public void setBlockMD5Test()
    {
        ObjectReference obj = new ObjectReference();
        obj.setBlockMD5("abc");
    }
    
    @Test
    public void setIdTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setId("1");
    }
    
    @Test
    public void setRefCountTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setRefCount(0);
    }
    
    @Test
    public void setResourceGroupIdTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setResourceGroupId(0);
    }
    
    @Test
    public void setSecurityLabelTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setSecurityLabel(null);
    }
    
    @Test
    public void setSecurityVersionTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setSecurityVersion("version1");
    }
    
    @Test
    public void setSha1Test()
    {
        ObjectReference obj = new ObjectReference();
        obj.setSha1("256");
    }
    
    @Test
    public void setSizeTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setSize(0);
    }
    
    @Test
    public void setTableSuffixTest()
    {
        ObjectReference obj = new ObjectReference();
        obj.setTableSuffix(0);
    }
}

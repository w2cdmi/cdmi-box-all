package com.huawei.sharedrive.app.utils.test;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.exception.BadRequestException;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import com.huawei.sharedrive.app.utils.ShareLinkExceptionUtil;
import pw.cdmi.box.domain.Limit;

public class FilesCommonUtilsTest
{
    @Test
    public void checkAndSetLimitObjTest()
    {
        Limit checkObj = FilesCommonUtils.checkAndSetLimitObj(100L, 99);
        System.out.println(checkObj);
    }
    
    @Test
    public void checkAndSetLimitObjTest2()
    {
        try
        {
            Limit checkObj = FilesCommonUtils.checkAndSetLimitObj(null, 99);
            System.out.println(checkObj);
            Limit checkObj1 = FilesCommonUtils.checkAndSetLimitObj(100L, null);
            System.out.println(checkObj1);
            Limit checkObj2 = FilesCommonUtils.checkAndSetLimitObj(100L, 0);
            System.out.println(checkObj2);
        }
        catch (Exception e)
        {
            String className = ShareLinkExceptionUtil.getClassName(new BusinessException());
            System.out.println(className);
            Assert.assertEquals("BusinessException", className);
        }
    }
    
    @Test
    public void checkAndSetLimitObjTest3()
    {
        Limit checkObj = null;
        try
        {
            checkObj = FilesCommonUtils.checkAndSetLimitObj(null, 0);
        }
        catch (InvalidParamException e)
        {
            Assert.assertEquals(true, null == checkObj);
        }
    }
    
    @Test
    public void checkAndSetLimitObjTest4()
    {
        Limit checkObj = null;
        try
        {
            checkObj = FilesCommonUtils.checkAndSetLimitObj(null, 1001);
        }
        catch (InvalidParamException e)
        {
            Assert.assertEquals(true, null == checkObj);
        }
    }
    
    @Test
    public void checkAndSetLimitObjTest5()
    {
        Limit checkObj = null;
        try
        {
            checkObj = FilesCommonUtils.checkAndSetLimitObj(null, -9);
        }
        catch (InvalidParamException e)
        {
            Assert.assertEquals(true, null == checkObj);
        }
    }
    
    @Test
    public void checkEncryptKeyTest()
    {
        FilesCommonUtils.checkEncryptKey("encryptKey");
    }
    
    @Test
    public void checkEncryptKeyTest2()
    {
        try
        {
            FilesCommonUtils.checkEncryptKey("");
        }
        catch (BadRequestException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("BadRequestException", className);
        }
    }
    
    @Test
    public void checkEncryptKeyTest3()
    {
        try
        {
            FilesCommonUtils.checkEncryptKey("1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i1ad3rtr67i");
        }
        catch (BadRequestException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("BadRequestException", className);
        }
    }
    
    @Test
    public void checkNodeNameVaildTest()
    {
        try
        {
            FilesCommonUtils.checkNodeNameVaild("");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkNodeNameVaildTest1()
    {
        try
        {
            FilesCommonUtils.checkNodeNameVaild("test");
            FilesCommonUtils.checkNodeNameVaild(".test");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkNodeNameVaildTest2()
    {
        FilesCommonUtils.checkNodeNameVaild("test.test");
    }
    
    @Test
    public void checkNonNegativeIntegersTest()
    {
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers();
            FilesCommonUtils.checkNonNegativeIntegers(new Integer[]{2, 45, null, 1});
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkNonNegativeIntegersTest1()
    {
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(new Integer[]{2, 45, 890, 1});
            FilesCommonUtils.checkNonNegativeIntegers(new Number[]{2, 45.1, null, 1});
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkNonNegativeIntegersTest2()
    {
        FilesCommonUtils.checkNonNegativeIntegers(99);
    }
    
    @Test
    public void checkNonNegativeIntegersTest3()
    {
        try
        {
            FilesCommonUtils.checkNonNegativeIntegers(7.0f);
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkTeamNameVaildTest0()
    {
        try
        {
            FilesCommonUtils.checkTeamNameVaild("^•!#/<>%?'");
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkTeamNameVaildTest()
    {
        try
        {
            FilesCommonUtils.checkTeamNameVaild("huawei");
            FilesCommonUtils.checkTeamNameVaild("");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkTeamNameVaildTest1()
    {
        try
        {
            FilesCommonUtils.checkTeamNameVaild("");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkTeamNameVaildTest2()
    {
        try
        {
            FilesCommonUtils.checkTeamNameVaild(null);
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkTeamNameVaildIgnoreNull()
    {
        FilesCommonUtils.checkTeamNameVaildIgnoreNull("huawei");
    }
    
    @Test
    public void checkTeamNameVaildIgnoreNull1()
    {
        try
        {
            FilesCommonUtils.checkTeamNameVaildIgnoreNull("");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkTeamNameVaildIgnoreNull2()
    {
        try
        {
            FilesCommonUtils.checkTeamNameVaildIgnoreNull(null);
        }
        catch (NullPointerException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void checkLinkCodeVaild0()
    {
        try
        {
            FilesCommonUtils.checkLinkCodeVaild("!@#$%^&");
        }
        catch (InvalidParamException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void checkLinkCodeVaild()
    {
        FilesCommonUtils.checkLinkCodeVaild("huawei");
    }
    
    @Test
    public void checkLinkCodeVaild1()
    {
        try
        {
            FilesCommonUtils.checkLinkCodeVaild("");
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkLinkCodeVaild2()
    {
        try
        {
            FilesCommonUtils.checkLinkCodeVaild(null);
        }
        catch (InvalidParamException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkVaildMD5()
    {
        FilesCommonUtils.checkVaildMD5("hua23423wehua23423wehua23423we5f");
    }
    
    @Test
    public void checkVaildMD51()
    {
        try
        {
            FilesCommonUtils.checkVaildMD5("");
        }
        catch (BaseRunException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkVaildMD52()
    {
        try
        {
            FilesCommonUtils.checkVaildMD5(null);
        }
        catch (NullPointerException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void checkVaildSha1Test()
    {
        try
        {
            FilesCommonUtils.checkVaildSha1("sha1");
        }
        catch (BaseRunException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("InvalidParamException", className);
        }
    }
    
    @Test
    public void checkVaildSha1Test1()
    {
        FilesCommonUtils.checkVaildSha1("hua23423wehua23423wehua23423we5f1qaz2wsx");
    }
    
    @Test
    public void checkVaildSha1Test2()
    {
        try
        {
            FilesCommonUtils.checkVaildSha1(null);
        }
        catch (NullPointerException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void decodeUft8ValueTest()
    {
        String uft8Value = FilesCommonUtils.decodeUft8Value("huawei@123");
        System.out.println(uft8Value);
        Assert.assertEquals("huawei@123", uft8Value);
    }
    
    @Test
    public void decodeUft8ValueTest1()
    {
        String uft8Value = FilesCommonUtils.decodeUft8Value("");
        System.out.println(uft8Value);
        Assert.assertEquals("", uft8Value);
    }
    
    @Test
    public void decodeUft8ValueTest2()
    {
        String uft8Value = FilesCommonUtils.decodeUft8Value(null);
        System.out.println(uft8Value);
        Assert.assertEquals(null, uft8Value);
    }
    
    @Test
    public void encodeUft8ValueTest()
    {
        String uft8Value = FilesCommonUtils.encodeUft8Value("huawei@123");
        System.out.println(uft8Value);
        Assert.assertEquals("huawei%40123", uft8Value);
    }
    
    @Test
    public void encodeUft8ValueTest1()
    {
        String uft8Value = FilesCommonUtils.encodeUft8Value("");
        System.out.println(uft8Value);
        Assert.assertEquals("", uft8Value);
    }
    
    @Test
    public void encodeUft8ValueTest2()
    {
        String uft8Value = FilesCommonUtils.encodeUft8Value(null);
        System.out.println(uft8Value);
        Assert.assertEquals(null, uft8Value);
    }
    
    @Test
    public void getFileSuffixTest()
    {
        String suffix = FilesCommonUtils.getFileSuffix("testfile.doc");
        System.out.println(suffix);
        Assert.assertEquals("doc", suffix);
    }
    
    @Test
    public void getFileSuffixTest0()
    {
        String suffix = FilesCommonUtils.getFileSuffix("testfiledoc.");
        System.out.println(suffix);
        Assert.assertEquals("", suffix);
    }
    
    @Test
    public void getFileSuffixTest1()
    {
        String suffix = FilesCommonUtils.getFileSuffix("testfileqdoc");
        Assert.assertEquals("", suffix);
    }
    
    @Test
    public void getFileSuffixTest2()
    {
        try
        {
            FilesCommonUtils.getFileSuffix(null);
        }
        catch (NullPointerException e)
        {
            String className = ShareLinkExceptionUtil.getClassName(e);
            System.out.println(className);
            Assert.assertEquals("NullPointerException", className);
        }
    }
    
    @Test
    public void getLimitedLengthFileNameTest()
    {
        String fileName = FilesCommonUtils.getLimitedLengthFileName("oldNamewerwer232", (byte) 3, 95);
        Assert.assertEquals("oldNamewerwer232", fileName);
    }
    
    @Test
    public void getLimitedLengthFileNameTest0()
    {
        FilesCommonUtils.getLimitedLengthFileName("oldNamewerwer232.", (byte) 3, 95);
    }
    
    @Test
    public void getLimitedLengthFileNameTest1()
    {
        String fileName = FilesCommonUtils.getLimitedLengthFileName("oldN(amewe(rwer.232", (byte) 1, 5);
        System.out.println(fileName);
        Assert.assertEquals("ol...(rwer.232", fileName);
    }
    
    @Test
    public void getNewNameTest()
    {
        String newName = FilesCommonUtils.getNewName((byte) 2, "oldN(amewe(rwer.232", 4);
        System.out.println(newName);
        Assert.assertEquals("oldN(amewe(rwer.232(4)", newName);
    }
    
    @Test
    public void getNewNameTest1()
    {
        String newName = FilesCommonUtils.getNewName((byte) 2, "oldN(amewe(rwer.232.", 4);
        System.out.println(newName);
        Assert.assertEquals("oldN(amewe(rwer.232.(4)", newName);
    }
    
    @Test
    public void getNewNameTest2()
    {
        String newName = FilesCommonUtils.getNewName((byte) 2, "oldN(amewe(rwer.232.)", 4);
        System.out.println(newName);
    }
    
    @Test
    public void isImageTest()
    {
        boolean image = FilesCommonUtils.isImage("tomPhoto.jpg");
        System.out.println(image);
        Assert.assertEquals(true, image);
    }
    
    @Test
    public void isImageTest1()
    {
        boolean image = FilesCommonUtils.isImage("tomPhoto.doc");
        System.out.println(image);
        Assert.assertEquals(false, image);
    }
    
    @Test
    public void isImageTest2()
    {
        boolean image = FilesCommonUtils.isImage("");
        System.out.println(image);
        Assert.assertEquals(false, image);
    }
    
    @Test
    public void isImageTest3()
    {
        boolean image = FilesCommonUtils.isImage(null);
        System.out.println(image);
        Assert.assertEquals(false, image);
    }
    
    @Test
    public void parseMD5Test()
    {
        Map<String, String> md5 = FilesCommonUtils.parseMD5("md5Str");
        Assert.assertEquals(true, md5.isEmpty());
    }
    
    @Test
    public void parseMD5Test1()
    {
        Map<String, String> md5 = FilesCommonUtils.parseMD5("mewrwerd5:Swert:werr:456wert:werr456456");
        Assert.assertEquals(false, md5.isEmpty());
    }
    
    @Test
    public void transferStringTest()
    {
        String string = FilesCommonUtils.transferString("so\\urc'e");
        System.out.println(string);
        Assert.assertEquals("so\\urc\\'e", string);
    }
    
    @Test
    public void transferStringForSqlTest()
    {
        String forSql = FilesCommonUtils.transferStringForSql("select * from admin where loginName like 'admi_%'");
        System.out.println(forSql);
        Assert.assertEquals("select * from admin where loginName like \\'admi\\_\\%\\'", forSql);
    }
    
    @Test
    public void transferStringForSqlTest1()
    {
        String forSql = FilesCommonUtils.transferStringForSql("");
        System.out.println(forSql);
        Assert.assertEquals("", forSql);
    }
    
    @Test
    public void transferStringForSqlTest2()
    {
        String forSql = FilesCommonUtils.transferStringForSql(null);
        System.out.println(forSql);
        Assert.assertEquals("", forSql);
    }
    
    @Test
    public void setNodeVersionsForV2Test()
    {
        try
        {
            INode node = new INode();
            node.setVersion("version");
            FilesCommonUtils.setNodeVersionsForV2(node);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void transferStringTest1()
    {
        FilesCommonUtils.transferString("");
    }
}

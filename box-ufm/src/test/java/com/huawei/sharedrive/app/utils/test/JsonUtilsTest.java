package com.huawei.sharedrive.app.utils.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.JsonUtils;


public class JsonUtilsTest
{
    @Test
    public void stringToCollectionTest()
    {
        String jsonStr = null;
        JsonUtils.stringToCollection(jsonStr, ArrayList.class);
        JsonUtils.stringToList(null, ArrayList.class, ArrayList.class);
        Assert.assertNull(JsonUtils.stringToList("sdfsdfsdf", ArrayList.class, ArrayList.class));
    }
    
    @Test
    public void stringToMapTest()
    {
        String jsonStr = null;
        JsonUtils.stringToMap(jsonStr);
        Assert.assertNull(JsonUtils.stringToMap("jsonStr"));
    }
    
    @Test
    public void fillJsonMapTest() throws IOException
    {
        Map<String, String> map = new HashMap<String, String>();
        String jsonStr = null;
        JsonUtils.fillJsonMap(jsonStr, map);
    }
    
    @Test
    public void fillJsonMapTest1() throws IOException
    {
        try
        {
            Map<String, String> map = new HashMap<String, String>();
            String jsonStr = "{\'name\':\'123\',\'password\':\'456\'}";
            JsonUtils.fillJsonMap(jsonStr, map);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void stringToObjectTest()
    {
        String jsonStr = null;
        JsonUtils.stringToObject(jsonStr, Limit.class);
    }
    @Test
    public void stringToObjectTest1()
    {
        String jsonStr = "";
        JsonUtils.stringToObject(jsonStr, Limit.class);
    }
    @Test
    public void stringToObjectTest2()
    {
        String jsonStr = "sdfdsfsdfsdf";
        JsonUtils.stringToObject(jsonStr, Limit.class);
    }
    
    @Test
    public void toJsonTest()
    {
        Limit limit = new Limit();
        limit.setLength(8);
        limit.setOffset(9L);
        String json = JsonUtils.toJson(limit);
        System.out.println(json);
    }
    
    @Test
    public void toJsonExcludeNullTest()
    {
        Limit limit = new Limit();
        limit.setLength(8);
        limit.setOffset(9L);
        String json = JsonUtils.toJsonExcludeNull(limit);
        System.out.println(json);
    }
    
    @Test
    public void toJsonExcludeNullTest2()
    {
        Limit limit = null;
        JsonUtils.toJsonExcludeNull(limit);
    }
    @Test
    public void stringToCollectionTest1()
    {
        Assert.assertNull(JsonUtils.stringToCollection("234234dsfsdfsfsd", ArrayList.class));
    }
    
}

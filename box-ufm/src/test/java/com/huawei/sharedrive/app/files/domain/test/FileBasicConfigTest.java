package com.huawei.sharedrive.app.files.domain.test;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.huawei.sharedrive.app.files.domain.FileBasicConfig;

public class FileBasicConfigTest
{
    @Test
    public void buildConfigFromJsonTest()
    {
        FileBasicConfig buildConfigFromJson = FileBasicConfig.buildConfigFromJson(null);
        System.out.println(buildConfigFromJson);
    }
    
    @Test
    public void buildConfigFromJsonTest2()
    {
        FileBasicConfig buildConfigFromJson = FileBasicConfig.buildConfigFromJson("abc");
        System.out.println(buildConfigFromJson);
    }
    
    @Test
    public void buildConfigFromMapTest()
    {
        FileBasicConfig buildConfigFromJson = FileBasicConfig.buildConfigFromMap(null);
        System.out.println(buildConfigFromJson);
    }
    
    @Test
    public void buildConfigFromMapTest2()
    {
        Map<String, String> configMap = new Map<String, String>()
        {
            
            @Override
            public Collection<String> values()
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public int size()
            {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public String remove(Object key)
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public void putAll(Map<? extends String, ? extends String> m)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public String put(String key, String value)
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public Set<String> keySet()
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public boolean isEmpty()
            {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public String get(Object key)
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public Set<Entry<String, String>> entrySet()
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public boolean containsValue(Object value)
            {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean containsKey(Object key)
            {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public void clear()
            {
                // TODO Auto-generated method stub
                
            }
        };
        FileBasicConfig buildConfigFromJson = FileBasicConfig.buildConfigFromMap(configMap);
        System.out.println(buildConfigFromJson);
    }
    
    @Test
    public void getJsonTest()
    {
        String json = FileBasicConfig.getJson(null);
        System.out.println(json);
    }
    
    @Test
    public void getJsonTest2()
    {
        FileBasicConfig basicConfig = new FileBasicConfig();
        String json = FileBasicConfig.getJson(basicConfig);
        System.out.println(json);
    }
    
    @Test
    public void equalsTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        boolean equals = filecofig.equals(null);
        System.out.println(equals);
    }
    
    @Test
    public void equalsTest2()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        boolean equals = filecofig.equals(new Date());
        System.out.println(equals);
    }
    
    @Test
    public void getAllowBatchTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        boolean equals = filecofig.getAllowBatch();
        System.out.println(equals);
    }
    
    @Test
    public void getOverwriteAclTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        boolean equals = filecofig.getOverwriteAcl();
        System.out.println(equals);
    }
    
    @Test
    public void getDEFAULT_ALLOW_PATCHTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        boolean equals = filecofig.DEFAULT_ALLOW_PATCH;
        System.out.println(equals);
    }
    
    @Test
    public void getDEFAULT_OVERWRITE_ACLTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        boolean equals = filecofig.DEFAULT_OVERWRITE_ACL;
        System.out.println(equals);
    }
    
    @Test
    public void getClassTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        Class<? extends FileBasicConfig> class1 = filecofig.getClass();
        System.out.println(class1);
    }
    
    @Test
    public void hashCodeTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        int hashCode = filecofig.hashCode();
        System.out.println(hashCode);
    }
    
    @Test
    public void toStringTest()
    {
        FileBasicConfig filecofig = new FileBasicConfig();
        String hashCode = filecofig.toString();
        System.out.println(hashCode);
    }
}

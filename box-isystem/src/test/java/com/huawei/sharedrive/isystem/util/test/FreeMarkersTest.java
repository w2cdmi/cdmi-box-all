package com.huawei.sharedrive.isystem.util.test;

import java.io.StringWriter;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.isystem.util.FreeMarkers;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkersTest
{
    @Test
    public void renderStringTest()
    {
        String string = FreeMarkers.renderString("templateString", new HashMap<String, String>());
        System.out.println(string);
        Assert.assertEquals("templateString", string);
    }
    
    @Test
    public void buildConfigurationTest()
    {
        try
        {
            Configuration configuration = FreeMarkers.buildConfiguration("email");
            configuration.setDefaultEncoding("UTF-8");
            Template template = configuration.getTemplate("testMailContent.ftl");
            FreeMarkers.renderTemplate(template, new StringWriter());
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}

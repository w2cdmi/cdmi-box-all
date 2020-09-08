/**
 * 
 */
package com.huawei.sharedrive.app.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author s00108907
 * 
 */
public final class FreeMarkers
{
    private static Logger logger = LoggerFactory.getLogger(FreeMarkers.class);
    
    private FreeMarkers()
    {
        
    }
    
    /**
     * 创建默认配置，设定模板目录.
     */
    public static Configuration buildConfiguration(String directory) throws IOException
    {
        Configuration cfg = new Configuration();
        Resource path = new DefaultResourceLoader().getResource(directory);
        cfg.setDirectoryForTemplateLoading(path.getFile());
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }
    
    /**
     * 渲染模板字符串。
     */
    public static String renderString(String templateString, Map<String, ?> model)
    {
        StringReader sr = null;
        StringWriter result = null;
        try
        {
            result = new StringWriter();
            sr = new StringReader(templateString);
            Template t = new Template("default", sr, new Configuration());
            t.process(model, result);
            return result.toString();
        }
        catch (Exception e)
        {
            logger.error("Fail in renderString", e);
        }
        finally
        {
            IOUtils.closeQuietly(sr);
            IOUtils.closeQuietly(result);
        }
        return null;
    }
    
    /**
     * 渲染Template文件.
     */
    public static String renderTemplate(Template template, Object model)
    {
        StringWriter result = null;
        try
        {
            result = new StringWriter();
            template.process(model, result);
            return result.toString();
        }
        catch (Exception e)
        {
            logger.error("Fail in renderTemplate", e);
        }
        finally
        {
            IOUtils.closeQuietly(result);
        }
        return null;
    }
    
}

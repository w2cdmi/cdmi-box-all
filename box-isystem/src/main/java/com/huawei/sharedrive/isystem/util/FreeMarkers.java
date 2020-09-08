/**
 * 
 */
package com.huawei.sharedrive.isystem.util;

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
import freemarker.template.TemplateException;

/**
 * @author s00108907
 * 
 */
public final class FreeMarkers
{
    private FreeMarkers()
    {
        
    }
    
    private static Logger logger = LoggerFactory.getLogger(FreeMarkers.class);
    
    /**
     * 渲染模板字符串。
     */
    public static String renderString(String templateString, Map<String, ?> model)
    {
        StringWriter result = null;
        StringReader reader = null;
        try
        {
            reader = new StringReader(templateString);
            result = new StringWriter();
            Template t = new Template("default", reader, new Configuration());
            t.process(model, result);
            return result.toString();
        }
        catch (IOException e)
        {
            logger.error("Fail in renderString", e);
        }
        catch (TemplateException e)
        {
            logger.error("Fail in renderString", e);
        }
        finally
        {
            IOUtils.closeQuietly(reader);
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
        catch (IOException e)
        {
            logger.error("Fail in renderTemplate", e);
        }
        catch (TemplateException e)
        {
            logger.error("Fail in renderTemplate", e);
        }
        finally
        {
            IOUtils.closeQuietly(result);
        }
        return null;
    }
    
    /**
     * 创建默认配置，设定模板目录.
     */
    public static Configuration buildConfiguration(String directory) throws IOException
    {
        Configuration cfg = new Configuration();
        Resource path = new DefaultResourceLoader().getResource(directory);
        cfg.setDirectoryForTemplateLoading(path.getFile());
        return cfg;
    }
}

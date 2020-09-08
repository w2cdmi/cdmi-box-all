package pw.cdmi.box.disk.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public final class FreeMarkers
{
    private FreeMarkers()
    {
        
    }
    
    private static Logger logger = LoggerFactory.getLogger(FreeMarkers.class);
    
    public static Configuration buildConfiguration(String directory) throws IOException
    {
        Configuration cfg = new Configuration();
        Resource path = new DefaultResourceLoader().getResource(directory);
        cfg.setDirectoryForTemplateLoading(path.getFile());
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }
    
    public static String renderString(String templateString, Map<String, ?> model)
    {
        try
        {
            StringWriter result = new StringWriter();
            Template t = new Template("default", new StringReader(templateString), new Configuration());
            t.process(model, result);
            return result.toString();
        }
        catch (TemplateException e)
        {
            logger.error("TemplateException Fail in renderString", e);
        }
        catch (IOException e)
        {
            logger.error("IOException Fail in renderString", e);
        }
        return null;
    }
    
    public static String renderTemplate(Template template, Object model)
    {
        try
        {
            StringWriter result = new StringWriter();
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
        return null;
    }
}

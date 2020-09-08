package pw.cdmi.box.disk.message.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.box.disk.utils.FreeMarkers;

public final class MessageFreemarker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFreemarker.class);
    
    private static final String MESSAGE_FTL_DIRECTORY = "message";
    
    private static Configuration configuration;
    
    private MessageFreemarker()
    {
        
    }
    
    static
    {
        try
        {
            configuration = FreeMarkers.buildConfiguration(MESSAGE_FTL_DIRECTORY);
        }
        catch (IOException e)
        {
            LOGGER.error("Freemarkder initialize failed!", e);
        }
    }
    
    public static String getFreeMarkerContent(String templateName, Map<String, Object> param, String encode)
    {
        
        try
        {
            StringWriter writer = new StringWriter();
            configuration.getTemplate(templateName).process(param, writer);
            return writer.toString();
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage(), e);
        }
    }
}

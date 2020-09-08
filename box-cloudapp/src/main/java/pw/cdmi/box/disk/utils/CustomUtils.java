package pw.cdmi.box.disk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.thoughtworks.xstream.XStream;

import pw.cdmi.box.disk.system.domain.custom.CustomAttribute;
import pw.cdmi.box.disk.system.domain.custom.CustomOffice;

public final class CustomUtils
{
    private CustomUtils()
    {
        
    }
    
    private static List<CustomOffice> customOfficeList;
    
    private static final String FILENAME = "custom.xml";
    
    private static Logger logger = LoggerFactory.getLogger(CustomUtils.class);
    
    public static String getParams(String key)
    {
        try
        {
            initCustomOfficeList();
        }
        catch (IOException e)
        {
            return "";
        }
        if (null == customOfficeList || customOfficeList.isEmpty())
        {
            return "";
        }
        return getResult(key, false);
    }
    
    public static String getValue(String key)
    {
        try
        {
            initCustomOfficeList();
        }
        catch (IOException e)
        {
            logger.error("", e);
            return "";
        }
        if (null == customOfficeList || customOfficeList.isEmpty())
        {
            return "";
        }
        return getResult(key, true);
    }
    /**
     * @param key
     * @param isValue
     * @return
     */
    private static String getResult(String key, boolean isValue)
    {
        String result = "";
        String officeName = PropertiesUtils.getProperty("officeName", "");
        for (CustomOffice customOffice : customOfficeList)
        {
            if (!"true".equalsIgnoreCase(customOffice.getStatus()))
            {
                continue;
            }
            if (!StringUtils.equals(officeName, customOffice.getName()))
            {
                continue;
            }
            if (customOffice.getAttList() == null)
            {
                continue;
            }
            result = getResultFromAttList(key, isValue, customOffice);
        }
        return result;
    }
    
    private static String getResultFromAttList(String key, boolean isValue, CustomOffice customOffice)
    {
        String result = "";
        for (CustomAttribute attribute : customOffice.getAttList())
        {
            if (attribute.getKey().equalsIgnoreCase(key))
            {
                if (isValue)
                {
                    result = StringUtils.trimToEmpty(attribute.getValue());
                }
                else
                {
                    result = StringUtils.trimToEmpty(attribute.getParam());
                }
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private static void initCustomOfficeList() throws IOException
    {
        synchronized (CustomUtils.class)
        {
            if (null == customOfficeList)
            {
                InputStream in = null;
                try
                {
                    in = new ClassPathResource(FILENAME).getInputStream();
                    XStream xstream = new XStream();
                    xstream.alias("customList", List.class);
                    xstream.alias("attList", List.class);
                    xstream.alias("custom", CustomOffice.class);
                    xstream.alias("attribute", CustomAttribute.class);
                    customOfficeList = (List<CustomOffice>) xstream.fromXML(in);
                }
                finally
                {
                    IOUtils.closeQuietly(in);
                }
            }
        }
    }
}

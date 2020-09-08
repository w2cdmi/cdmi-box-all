package pw.cdmi.box.disk.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.core.exception.BusinessException;

public final class CommonTools
{
    private CommonTools()
    {
        
    }
    
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonTools.class);
    
    /**
     * 
     * @param str
     * @param enc
     * @return
     */
    public static String encodeStr(String str, String enc)
    {
        try
        {
            str = URLEncoder.encode(str, enc);
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.warn("", e);
        }
        return str;
    }
    
    /**
     * 
     * @param linkCode
     * @return
     */
    public static String getAccessCode(String linkCode)
    {
        String accessCode = (String) SecurityUtils.getSubject()
            .getSession()
            .getAttribute(RestConstants.SESSION_KEY_LINK_ACCESSCODE + linkCode);
        return accessCode;
    }
    
    public static List<String> decodeTrunkDate(String input)
    {
        if (StringUtils.isBlank(input))
        {
            return null;
        }
        ArrayList<String> result = new ArrayList<String>(BusinessConstants.INITIAL_CAPACITIES);
        int totalLen = input.length();
        int index = 0;
        int trunkDataLen = 0;
        String value;
        BusinessException businessException = new BusinessException();
        while (index < totalLen)
        {
            try
            {
                trunkDataLen = Integer.parseInt(input.substring(index, index + 4));
            }
            catch (NumberFormatException e)
            {
                LOGGER.error("failed in decode trunkDate", e);
                
                businessException.setMessage(e.getMessage());
                throw businessException;
            }
            index += 4;
            value = input.substring(index, index + trunkDataLen);
            index += trunkDataLen;
            result.add(value);
        }
        return result;
    }
}

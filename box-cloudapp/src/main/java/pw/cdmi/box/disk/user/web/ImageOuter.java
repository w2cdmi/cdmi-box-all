package pw.cdmi.box.disk.user.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImageOuter
{
    private static Logger logger = LoggerFactory.getLogger(ImageOuter.class);
    
    private ImageOuter()
    {
        
    }
    
    public static void outputImage(HttpServletResponse resp, byte[] data, String contentType)
    {
        OutputStream outputStream = null;
        try
        {
            if (data == null || data.length == 0)
            {
                return;
            }
            resp.setContentType(contentType);
            resp.setHeader("Pragma", "No-cache");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expire", 0);
            outputStream = resp.getOutputStream();
            outputStream.write(data);
        }
        catch (IOException e)
        {
            logger.error("Error in output icon img!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
}

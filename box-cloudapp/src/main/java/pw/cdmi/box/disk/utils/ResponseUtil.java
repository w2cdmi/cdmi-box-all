package pw.cdmi.box.disk.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
	
	public static void outputImage(HttpServletResponse resp, byte[] data, String contentType) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            if (null == data || data.length == 0)
            {
                return;
            }
            resp.setContentType(contentType);
            outputStream = resp.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
    public static byte[] getBytesFromFile(File f)
    {
        if (f == null)
        {
            return new byte[]{};
        }
        FileInputStream stream = null;
        ByteArrayOutputStream out = null;
        try
        {
            stream = new FileInputStream(f);
            out = new ByteArrayOutputStream(1000);
            
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
            {
                out.write(b, 0, n);
                out.flush();
            }
            return out.toByteArray();
        }
        catch (IOException e)
        {
            logger.error("read file to byte fail!", e);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
            IOUtils.closeQuietly(out);
        }
        return new byte[0];
    }
}

package pw.cdmi.box.disk.init.develop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ShiroConfigReplaceTools
{
    
    @Test
    public void testAppCxtReplace() throws IOException
    {
        StringBuilder sb = new StringBuilder();
        File appCxtFile = new File(LocalConfig.getDevResourcePath() + "applicationContext-pat-shiro.xml");
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(appCxtFile));
            String tempLine;
            while((tempLine = reader.readLine()) != null)
            {
                if(tempLine.contains("<property name=\"secure\""))
                {
                    sb.append(replaceSecure(tempLine));
                }
                else
                {
                    sb.append(tempLine);
                }
                sb.append(System.lineSeparator());
            }
        }
        finally
        {
            IOUtils.closeQuietly(reader);
        }
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(appCxtFile);
            IOUtils.write(sb.toString(), out);
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }

    private String replaceSecure(String tempLine)
    {
        return  tempLine.replaceAll("true", "false");
    }
}

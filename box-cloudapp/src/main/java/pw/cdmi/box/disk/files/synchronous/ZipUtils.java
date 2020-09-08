package pw.cdmi.box.disk.files.synchronous;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.box.disk.utils.Utils;

public final class ZipUtils
{
    private static final Log LOGGER = LogFactory.getLog(ZipUtils.class);
    
    private ZipUtils()
    {
        
    }
    
    /**
     * 
     * @param files
     * @param entryName
     * @param target
     */
    public static void writeFileToZip(File[] files, String[] entryName, File target)
    {
        ZipOutputStream zos = null;
        DataOutputStream ros = null;
        try
        {
            if (files.length != entryName.length)
            {
                throw new InvalidParamException("file's length not equals entryName's length");
            }
            CheckedOutputStream csum = new CheckedOutputStream(new FileOutputStream(target), new Adler32());
            zos = new ZipOutputStream(csum);
            ros = new DataOutputStream(new BufferedOutputStream(zos));
            
            int bufferSize = 4 * 1024;
            byte[] buf = new byte[bufferSize];
            File file;
            String entryNameStr;
            for (int i = 0; i < entryName.length; i++)
            {
                LOGGER.info("start write file " + files[i].getCanonicalPath() + " to output stream.");
                file = files[i];
                entryNameStr = entryName[i];
                writeSliceFileToZip(file, entryNameStr, buf, zos, ros);
                LOGGER.info("end write file " + files[i].getCanonicalPath() + " to output stream successful.");
            }
            zos.finish();
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        finally
        {
            Utils.close(zos);
            Utils.close(ros);
        }
    }
    
    private static void writeSliceFileToZip(File file, String entryNameStr, byte[] buf, ZipOutputStream zos,
        DataOutputStream ros)
    {
        DataInputStream iz = null;
        
        try
        {
            iz = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            zos.putNextEntry(new ZipEntry(entryNameStr));
            int read;
            while (true)
            {
                read = iz.read(buf);
                
                if (read == -1)
                {
                    break;
                }
                ros.write(buf, 0, read);
            }
            ros.flush();
            zos.closeEntry();
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        finally
        {
            Utils.close(ros);
            Utils.close(zos);
            Utils.close(iz);
        }
    }
    
}

package com.huawei.sharedrive.app.files.synchronous;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.utils.Utils;

public final class ZipUtils
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);
    private ZipUtils()
    {
        
    }
    
    /**
     * 压缩
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
            CheckedOutputStream csum = new CheckedOutputStream(new FileOutputStream(target), new Adler32());
            zos = new ZipOutputStream(csum);
            ros = new DataOutputStream(new BufferedOutputStream(zos));
            
            int bufferSize = 4 * 1024;
            byte[] buf = new byte[bufferSize];
            File file = null;
            String entryNameStr = null;
            for (int i = 0; i < entryName.length; i++)
            {
                LOGGER.info("start write file " + files[i].getCanonicalPath() + " to output stream.");
                file = files[i];
                entryNameStr = entryName[i];
                writeSliceFileToZip(file, entryNameStr, buf, zos, ros);
                LOGGER.info("end write file " + files[i].getCanonicalPath() + " to output stream successful.");
            }
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("FileNotFoundException");
            throw new BusinessException("write File To Zip failed", e);
        }
        catch(IOException e)
        {
            LOGGER.error("IOException");
            throw new BusinessException("write File To Zip failed", e);
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
            int read = 0;
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
        catch (RuntimeException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        catch (Exception e)
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

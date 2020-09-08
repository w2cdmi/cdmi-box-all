package com.huawei.sharedrive.app.system.license;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.utils.PropertiesUtils;
import com.huawei.sharedrive.app.utils.file.TempFileUtils;
import com.huawei.sharedrive.common.license.CseLicenseException;
import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.EsnTools;
import com.huawei.sharedrive.common.license.LicTools;

import pw.cdmi.common.util.signature.HSCloudKey;

public final class AcLicTools
{
    private AcLicTools()
    {
        
    }
    
    private static final String KEY_SERVICE_IP = "self.serviceAddr";
    
    private static Logger logger = LoggerFactory.getLogger(AcLicTools.class);
    
    public static String getBusinessIp()
    {
        return PropertiesUtils.getProperty(KEY_SERVICE_IP);
    }
    
    public static String getEsn()
    {
        String serviceIp = PropertiesUtils.getProperty(KEY_SERVICE_IP, "").trim();
        if (StringUtils.isEmpty(serviceIp))
        {
            logger.error("Can not get the serviceIp");
            return "unknown";
        }
        try
        {
            String mac = EsnTools.getMac(serviceIp);
            logger.info("[licenseLog] mac is " + mac);
            String waitSha256 = serviceIp + mac;
            String esn = HSCloudKey.sha256Encoded(waitSha256.getBytes("utf8")).substring(0, 40);
            logger.info("[licenseLog] esn is " + esn);
            return esn;
        }
        catch (RuntimeException e)
        {
            logger.error("", e);
            return "unknown";
        }
        catch (Exception e)
        {
            logger.error("", e);
            return "unknown";
        }
    }
    
    
    /**
     * @param tempFile
     * @return
     * @throws InterruptedException
     * @throws CseLicenseException
     */
    public static CseLicenseInfo valiLicense(File tempFile) throws CseLicenseException
    {
        try
        {
            String esn = getEsn();
            logger.info("[licenseLog] macheEsn is " + esn);
            LicTools licTools = new LicTools();
            for (int i = 0; i <= 3; i++)
            {
                try
                {
                    return licTools.validateLicense(tempFile, true, esn, TempFileUtils.getTempPath());
                }
                catch (Exception e)
                {
                    try
                    {
                        Thread.currentThread();
                        Thread.sleep(4 * 1000);
                    }
                    catch (InterruptedException e1)
                    {
                        logger.warn(e1.getMessage());
                    }
                }
            }
            return licTools.validateLicense(tempFile, true, esn, TempFileUtils.getTempPath());
        }
        finally
        {
            try
            {
                FileUtils.forceDeleteOnExit(tempFile);
            }
            catch (IOException e)
            {
                logger.warn(e.getMessage());
            }
        }
    }
    
    public static String getNodeName()
    {
        return getBusinessIp();
    }
    
    public static int getPort()
    {
        try
        {
            return Integer.parseInt(PropertiesUtils.getProperty("", "8443"));
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }
}

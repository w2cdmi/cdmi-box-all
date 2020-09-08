/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.system.dao.LicenseDAO;
import com.huawei.sharedrive.app.system.dao.LicenseNodeDAO;
import com.huawei.sharedrive.app.system.license.AcLicTools;
import com.huawei.sharedrive.app.system.service.LicenseAlarmHelper;
import com.huawei.sharedrive.app.system.service.LicenseService;
import com.huawei.sharedrive.app.utils.file.TempFileUtils;
import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.LicenseFile;
import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.common.license.LicenseStatus;

/**
 * @author s00108907
 * 
 */
@Component
public class LicenseServiceImpl implements LicenseService
{
    public static final byte CURRENT_STATUS = 2;
    
    private static CseLicenseInfo current = null;
    
    private static Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);
    
    @Autowired
    private LicenseAlarmHelper licenseAlarmHelper;
    
    @Autowired
    private LicenseDAO licenseDAO;
    
    @Autowired
    private LicenseNodeDAO licenseNodeDAO;
    
    public static CseLicenseInfo currentLicense()
    {
        return current;
    }
    
    @Override
    public void loadLastestLicense()
    {
        CseLicenseInfo cseLicenseInfo = null;
        try
        {
            List<LicenseFile> fileList = licenseDAO.getLicenseFileListByStatus(CURRENT_STATUS);
            LicenseFile licenseFile = getCurLicenseFile(fileList);
            if (null == licenseFile)
            {
                logger.info("[loadLicense] can not get current license");
            }
            else
            {
                File tempFile = getTempLicenseFile(licenseFile.getId());
                FileUtils.writeByteArrayToFile(tempFile, licenseFile.getContent());
                cseLicenseInfo = AcLicTools.valiLicense(tempFile);
            }
        }
        catch (Exception e)
        {
            logger.error("Can validate right license", e);
        }
        LicenseNode licenseNode = new LicenseNode();
        licenseNode.setNodeAddress(AcLicTools.getBusinessIp());
        licenseNode.setPort(AcLicTools.getPort());
        licenseNode.setEsn(AcLicTools.getEsn());
        logger.info("[licenseLog] esn is " + licenseNode.getEsn());
        licenseNode.setName(AcLicTools.getNodeName());
        licenseNode.setServerType(LicenseNode.NODE_AC);
        if (null == cseLicenseInfo)
        {
            licenseNode.setStatus(LicenseStatus.ERROR_VALID_FAIL);
        }
        else
        {
            licenseNode.setStatus(LicenseStatus.NORMAL);
        }
        reportAcLicenseNode(licenseNode, cseLicenseInfo);
    }
    
    @Override
    public void reportAcLicenseNode(LicenseNode licenseNode, CseLicenseInfo licenseInfo)
    {
        logger.info("[licenseLog] reportAcLicenseNode " + licenseNode.getEsn());
        licenseNode.setLastModified(new Date());
        setCurrentLicenseInfo(licenseInfo);
        saveOrUpdateLicenseNode(licenseNode);
        licenseAlarmHelper.checkValidate(licenseInfo);
    }
    
    
    
    private LicenseFile getCurLicenseFile(List<LicenseFile> fileList)
    {
        if (null == fileList || fileList.isEmpty())
        {
            return null;
        }
        return fileList.get(0);
    }
    
    private File getTempLicenseFile(String fileName)
    {
        File file = new File(TempFileUtils.getTempPath() + fileName);
        return file;
    }
    
    /**
     * @param licenseNode
     */
    private void saveOrUpdateLicenseNode(LicenseNode licenseNode)
    {
        LicenseNode dbNode = licenseNodeDAO.getLicenseNode(licenseNode.getEsn(), licenseNode.getServerType());
        if (dbNode == null)
        {
            licenseNode.setId(UUID.randomUUID().toString());
            licenseNodeDAO.save(licenseNode);
        }
        else
        {
            licenseNode.setId(dbNode.getId());
            licenseNodeDAO.update(licenseNode);
        }
    }
    
    public static void setCurrentLicenseInfo(CseLicenseInfo cseLicenseInfo)
    {
        current = cseLicenseInfo;
    }
    
}

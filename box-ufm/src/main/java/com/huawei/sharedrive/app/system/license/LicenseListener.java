package com.huawei.sharedrive.app.system.license;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.system.dao.LicenseDAO;
import com.huawei.sharedrive.app.system.service.LicenseAlarmHelper;
import com.huawei.sharedrive.app.system.service.LicenseChecker;
import com.huawei.sharedrive.app.system.service.LicenseService;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;
import com.huawei.sharedrive.app.utils.file.TempFileUtils;
import com.huawei.sharedrive.common.license.CseLicenseConstants;
import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.LicenseFile;
import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.common.license.LicenseStatus;

import pw.cdmi.common.config.service.ConfigListener;

@Component("licenseListener")
public class LicenseListener implements ConfigListener
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseListener.class);
    
    @Autowired
    private LicenseChecker licenseChecker;
    
    @Autowired
    private LicenseDAO licenseDAO;
    
    @Autowired
    private LicenseService licenseService;
    
    @Autowired
    private TeamSpaceDAO teamSpaceDAO;
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @Autowired
    private LicenseAlarmHelper licenseAlarmHelper;
    
    @PostConstruct
    public void init()
    {
        try
        {
            LOGGER.info("[licenseLog]Init license listener");
            licenseService.loadLastestLicense();
        }
        catch (Exception e)
        {
            LOGGER.error("[licenseLog]Can not load lastst license ", e);
        }
        
    }
    
    /**
     * @param licenseNode
     * @param licenseInfo
     */
    private void handleUpdate(LicenseNode licenseNode, CseLicenseInfo licenseInfo)
    {
        licenseService.reportAcLicenseNode(licenseNode, licenseInfo);
    }
    
    private File saveToLocal(LicenseFile licenseFile) throws IOException
    {
        File file = new File(TempFileUtils.getTempPath() + licenseFile.getName());
        FileUtils.writeByteArrayToFile(file, licenseFile.getContent());
        return file;
    }
    
    @Override
    public void configChanged(String key, Object valueObj)
    {
        if (!StringUtils.equals(CseLicenseConstants.ZK_LICENSE_KEY, key))
        {
            return;
        }
        LOGGER.info("[licenseLog] uas received the request from isystem");
        LicenseNode licenseNode = new LicenseNode();
        licenseNode.setNodeAddress(AcLicTools.getBusinessIp());
        licenseNode.setPort(AcLicTools.getPort());
        licenseNode.setEsn(AcLicTools.getEsn());
        LOGGER.info("[licenseLog] esn is " + licenseNode.getEsn());
        licenseNode.setName(AcLicTools.getNodeName());
        licenseNode.setServerType(LicenseNode.NODE_AC);
        File localFile = null;
        LicenseFile dbLicenseFile = null;
        try
        {
            dbLicenseFile = licenseDAO.getLastest();
            if (null == dbLicenseFile)
            {
                LOGGER.error("[licenseLog] dbLicenseFile is null");
                return;
            }
            licenseNode.setLicenseId(dbLicenseFile.getId());
            localFile = saveToLocal(dbLicenseFile);
            checkAndLogZkLicenseId(valueObj, dbLicenseFile);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            licenseNode.setStatus(LicenseStatus.EEROR_UPDATE);
            handleUpdate(licenseNode, null);
            return;
        }
        CseLicenseInfo licenseInfo = null;
        try
        {
            licenseInfo = AcLicTools.valiLicense(localFile);
            if (null == licenseInfo)
            {
                licenseNode.setStatus(LicenseStatus.ERROR_VALID_FAIL);
            }
            else
            {
                licenseNode.setStatus(LicenseStatus.NORMAL);
            }
            
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            licenseNode.setStatus(LicenseStatus.ERROR_VALID_FAIL);
        }
        try
        {
            handleUpdate(licenseNode, licenseInfo);
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
        }
        
        try
        {
            // 导入新的license之后，促发一次告警，看是否能发送恢复告警
            doAlarm();
        }
        catch (Exception e)
        {
            LOGGER.warn("[licenseLog] do alarm failed.", e);
        }
    }
    
    private void doAlarm()
    {
        // 检查过期时间
        licenseAlarmHelper.checkExpirationTime();
        
        long spaceCount = teamSpaceDAO.getTeamSpaceCount(null);
        long userCount = userReverseDAO.getFilterdCount(null);
        
        licenseChecker.checkTotalUsers(userCount - spaceCount);
        licenseChecker.checkTeamSpaces(spaceCount);
    }
    
    /**
     * @param valueObj
     * @param dbLicenseFile
     */
    private void checkAndLogZkLicenseId(Object valueObj, LicenseFile dbLicenseFile)
    {
        try
        {
            String licenseId = valueObj.toString();
            LOGGER.info("[licenseId] receive license Id is " + licenseId);
            if (!StringUtils.equals(licenseId, dbLicenseFile.getId()))
            {
                LOGGER.warn("[licenseId] dbLicenseId is " + dbLicenseFile.getId());
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("[licenseId]Can not get licenseId from zookeeper, just for debug.", e);
        }
    }
    
}

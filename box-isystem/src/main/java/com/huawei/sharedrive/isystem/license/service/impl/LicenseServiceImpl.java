package com.huawei.sharedrive.isystem.license.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.huawei.sharedrive.common.license.CseLicenseConstants;
import com.huawei.sharedrive.common.license.CseLicenseException;
import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.LicTools;
import com.huawei.sharedrive.common.license.LicenseFile;
import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.license.LicenseCompareException;
import com.huawei.sharedrive.isystem.license.dao.LicenseFileDAO;
import com.huawei.sharedrive.isystem.license.dao.LicenseNodeDAO;
import com.huawei.sharedrive.isystem.license.service.LicenseService;
import com.huawei.sharedrive.isystem.util.file.TempFileUtils;

import pw.cdmi.common.config.service.ConfigManager;

@Component
public class LicenseServiceImpl implements LicenseService
{
    
    private static final int DATE_CONFLICT = 1;
    
    
    private static final int ESN_NUMBER_CONFLICT = 7;
    
    private static Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);
    
    private static final int TEAMSPACE_CONFLICT = 3;
    
    
    private static final int USER_CONFLICT = 2;
    
    private static Calendar getDateFromString(String str)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        try
        {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(str));
        }
        catch (Exception e)
        {
            return null;
        }
        return calendar;
    }
    
    @Autowired
    private ConfigManager configManager;
    
    @Autowired
    private LicenseFileDAO licenseFileDAO;
    
    @Autowired
    private LicenseNodeDAO licenseNodeDAO;
    
    @Override
    public void checkDifferenceWithCurrent(CseLicenseInfo newLicenseInfo) throws LicenseCompareException
    {
        CseLicenseInfo currentLicense = null;
        try
        {
            currentLicense = getCurrentLicenseInfo();
        }
        catch (Exception e)
        {
            logger.warn("checkDifferenceWithCurrent fail", e);
            return;
        }
        if (null == currentLicense)
        {
            return;
        }
        if (newLicenseInfo.getUsers() < currentLicense.getUsers())
        {
            throw new LicenseCompareException(USER_CONFLICT);
        }
        if (newLicenseInfo.getTeamSpaceNumber() < currentLicense.getTeamSpaceNumber())
        {
            throw new LicenseCompareException(TEAMSPACE_CONFLICT);
        }
        if (newLicenseInfo.getEsnList().size() < currentLicense.getEsnList().size())
        {
            throw new LicenseCompareException(ESN_NUMBER_CONFLICT);
        }
        checkDateConflict(newLicenseInfo.getDeadline(), currentLicense.getDeadline());
    }
    
    @Override
    public void confirmLicense(String licenseUuid)
    {
        updateDbStatus(licenseUuid);
        logger.info("[licenseLog] update zkdata; " + licenseUuid);
        configManager.setConfig(CseLicenseConstants.ZK_LICENSE_KEY, licenseUuid);
    }
    
    @Override
    public CseLicenseInfo getCseLicenseInfo(MultipartFile mulFile, String fileName)
        throws CseLicenseException, IOException
    {
        
        File file = getTempLicenseFile(fileName);
        InputStream inputstream = null;
        try
        {
            inputstream = mulFile.getInputStream();
            FileUtils.copyInputStreamToFile(inputstream, file);
            LicTools licTools = new LicTools();
            
            CseLicenseInfo licenseInfo = licTools.validateLicenseWithoutDeadLine(file,
                false,
                null,
                getLicenseDbPath());
            licenseInfo.setFile(file);
            return licenseInfo;
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }
    }
    
    public LicenseFile getCurrentLicenseFile()
    {
        List<LicenseFile> fileList = licenseFileDAO.getLicenseFileListByStatus(STATUS_CURRENT);
        LicenseFile licenseFile = getCurLicenseFile(fileList);
        if (null == licenseFile)
        {
            return null;
        }
        return licenseFile;
    }
    
    @Override
    public CseLicenseInfo getCurrentLicenseInfo() throws IOException, CseLicenseException
    {
        File tempFile = null;
        try
        {
            List<LicenseFile> fileList = licenseFileDAO.getLicenseFileListByStatus(STATUS_CURRENT);
            LicenseFile licenseFile = getCurLicenseFile(fileList);
            if (null == licenseFile)
            {
                return null;
            }
            tempFile = getTempLicenseFile(licenseFile.getId());
            FileUtils.writeByteArrayToFile(tempFile, licenseFile.getContent());
            LicTools licTools = new LicTools();
            return licTools.validateLicenseWithoutDeadLine(tempFile, false, null, getLicenseDbPath());
        }
        finally
        {
            if (null != tempFile)
            {
                try
                {
                    FileUtils.forceDelete(tempFile);
                }
                catch (IOException e)
                {
                    logger.warn("Can not delete the tempFile");
                }
            }
        }
    }
    
    @Override
    public List<LicenseNode> getLienseNode()
    {
        return licenseNodeDAO.listAll();
    }
    
    @Override
    public void saveToDb(CseLicenseInfo licenseInfo, String uuid, long optId, byte licenseStatus)
        throws IOException
    {
        File tempFile = licenseInfo.getFile();
        LicenseFile licenseFile = new LicenseFile();
        FileInputStream stream = null;
        try
        {
            stream = new FileInputStream(tempFile);
            byte[] content = new byte[(int)tempFile.length()];
            if (stream.read(content) < 0)
            {
                String message = "Read License File Failed.";
                logger.warn(message);
                throw new BusinessException(message);
            }
            licenseFile.setContent(content);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
            FileUtils.forceDeleteOnExit(tempFile);
        }
        licenseFile.setCreatedAt(new Date());
        licenseFile.setCreatedBy(optId);
        licenseFile.setName(tempFile.getName());
        licenseFile.setTempFile(tempFile);
        licenseFile.setStatus(licenseStatus);
        licenseFile.setId(uuid);
        licenseFileDAO.save(licenseFile);
        
        List<LicenseFile> licenseFiles = new ArrayList<LicenseFile>(10);
        licenseFiles = licenseFileDAO.getAllExceptCurrentFile(STATUS_CURRENT);
        int fileSize = licenseFiles.size();
        if (fileSize > MAX_FILES - 1)
        {
            try
            {
                for (int i = 0; i < fileSize - MAX_FILES + 1; i++)
                {
                    licenseFileDAO.deleteById(licenseFiles.get(i).getId());
                }
            }
            catch (Exception e)
            {
                logger.info("Fail to delete license file", e.getMessage());
            }
        }
        
    }
    
    /**
     * @param licenseUuid
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateDbStatus(String licenseUuid)
    {
        logger.info("[licenseLog] update database; " + licenseUuid);
        licenseFileDAO.updateStatusWithId(licenseUuid, STATUS_CURRENT);
        licenseFileDAO.updateStatusWithoutId(licenseUuid, STATUS_PAST);
    }
    
    private void checkDateConflict(String newDeadline, String oldDeadLine) throws LicenseCompareException
    {
        if (StringUtils.equals(newDeadline, CseLicenseConstants.PERMANENT))
        {
            return;
        }
        if (StringUtils.equals(oldDeadLine, CseLicenseConstants.PERMANENT))
        {
            throw new LicenseCompareException(DATE_CONFLICT);
        }
        Calendar newTime = getDateFromString(newDeadline);
        Calendar oldTime = getDateFromString(oldDeadLine);
        if (null == newTime || null == oldTime)
        {
            return;
        }
        if (oldTime.after(newTime))
        {
            throw new LicenseCompareException(DATE_CONFLICT);
        }
    }
    
    private LicenseFile getCurLicenseFile(List<LicenseFile> fileList)
    {
        if (null == fileList || fileList.isEmpty())
        {
            return null;
        }
        return fileList.get(0);
    }
    
    private String getLicenseDbPath()
    {
        return TempFileUtils.getTempPath();
    }
    
    private File getTempLicenseFile(String fileName)
    {
        File file = new File(TempFileUtils.getTempPath() + fileName);
        return file;
    }
    
}

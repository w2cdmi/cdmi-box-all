package com.huawei.sharedrive.isystem.license.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.huawei.sharedrive.common.license.CseLicenseException;
import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.LicenseFile;
import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.license.LicenseCompareException;
import com.huawei.sharedrive.isystem.license.service.LicenseService;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.user.domain.Admin;

import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/authorize")
public class LicenseController extends AbstractCommonController
{
    public static final int MAX_SIZE = 1024 * 1024;
    
    private static Logger logger = LoggerFactory.getLogger(LicenseController.class);
    
    public static final String LICENSE_DAT = "dat";
    
    public static final String LICENSE_XML = "xml";
    
    @Autowired
    private LicenseService licenseService;
    
    @Autowired
    private UserLogService userLogService;
    
    @RequestMapping(value = "/exportLicense", method = RequestMethod.GET)
    public void exportLicense(HttpServletResponse response, HttpServletRequest request)
    {
        
        UserLog userLog = userLogService.initUserLog(request, UserLogType.LICENSE_EXPORT, null);
        userLogService.saveUserLog(userLog);
        try
        {
            LicenseFile licenseFile = licenseService.getCurrentLicenseFile();
            if (null == licenseFile)
            {
                String message = "license file is null.";
                logger.warn(message);
                throw new BusinessException(message);
            }
            String filename = licenseFile.getId() + ".dat";
            response.setContentType("application/octet-stream;charset=utf-8");
            
            response.setHeader("Content-Disposition",
                "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            response.getOutputStream().write(licenseFile.getContent());
            response.getOutputStream().flush();
            userLog.setDetail(UserLogType.LICENSE_EXPORT.getDetails(null));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("UnsupportedEncodingException.", e);
        }
        catch (IOException e)
        {
            logger.error("Can not export licesne.", e);
        }
        finally
        {
            try
            {
                IOUtils.closeQuietly(response.getOutputStream());
            }
            catch (IOException e)
            {
                logger.warn("Can not close the io.", e);
            }
        }
    }
    
    @RequestMapping(value = "/confirmLicense", method = RequestMethod.POST)
    public ResponseEntity<String> confirmLicense(String licenseUuid, Model model, HttpServletRequest request,
        HttpServletResponse response, String token)
    {
        super.checkToken(token);
        UserLog userLog = userLogService.initUserLog(request, UserLogType.LICENSE_CONFIM, null);
        userLogService.saveUserLog(userLog);
        if (StringUtils.isBlank(licenseUuid) || licenseUuid.length() > 255)
        {
            userLog.setDetail(UserLogType.LICENSE_CONFIM.getCommonErrorParamDetails(null));
            userLog.setType(UserLogType.LICENSE_CONFIM.getValue());
            userLogService.update(userLog);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        licenseService.confirmLicense(licenseUuid);
        userLog.setDetail(UserLogType.LICENSE_CONFIM.getDetails(null));
        userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        userLogService.update(userLog);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
    
    @RequestMapping(value = "/gotoLicense", method = RequestMethod.GET)
    public String gotoLicenseInfo(Model model)
    {
        try
        {
            CseLicenseInfo licenseInfo = licenseService.getCurrentLicenseInfo();
            model.addAttribute("licenseInfo", licenseInfo);
            if (null != licenseInfo)
            {
                model.addAttribute("defaultTeams", getMaxTeamspaces(licenseInfo.getUsers()));
                String esnString = StringUtils.join(licenseInfo.getEsnList(), ", ");
                model.addAttribute("esnString", esnString);
            }
            else
            {
                model.addAttribute("esnString", "");
            }
        }
        catch (CseLicenseException e)
        {
            model.addAttribute("error", "Exception");
        }
        catch (IOException e)
        {
            model.addAttribute("error", "Exception");
        }
        return "license/licenseIndex";
    }
    
    @RequestMapping(value = "/listLicenseNode", method = RequestMethod.POST)
    public ResponseEntity<List<LicenseNode>> listLicenseNode(String licenseUuid, Model model,
        HttpServletRequest request, HttpServletResponse response, String token)
    {
        List<LicenseNode> nodeList = licenseService.getLienseNode();
        
        super.checkToken(token);
        
        for (LicenseNode licenseNode : nodeList)
        {
            transNode(licenseNode);
        }
        return new ResponseEntity<List<LicenseNode>>(nodeList, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/uploadLicense", method = RequestMethod.POST)
    public String uploadLicense(MultipartHttpServletRequest request, Model model, String token)
    {
        
        UserLog userLog = userLogService.initUserLog(request, UserLogType.LICENSE_UPLOAD, null);
        userLogService.saveUserLog(userLog);
        CseLicenseInfo licenseInfo = null;
        String message = null;
        super.checkToken(token);
        try
        {
            Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
            long optId = sessAdmin.getId();
            Map<String, MultipartFile> fileMap = request.getFileMap();
            LicenseImportResult result = null;
            try
            {
                result = parseLicenseFile(model, optId, fileMap);
            }
            catch (LicenseCompareException e)
            {
                logger.error("", e);
                message = "license.file.error";
                model.addAttribute("error", e.getType());
            }
            
            if (null != result)
            {
                message = result.getMessage();
                licenseInfo = result.getLicenseInfo();
            }
            
            model.addAttribute("licenseInfo", licenseInfo);
            if (null != licenseInfo && CollectionUtils.isNotEmpty(licenseInfo.getEsnList()))
            {
                String esnString = StringUtils.join(licenseInfo.getEsnList(), ", ");
                model.addAttribute("esnString", esnString);
            }
            else
            {
                model.addAttribute("esnString", "");
            }
        }
        catch (CseLicenseException e)
        {
            logger.error("", e);
            message = "license.upload.error";
            model.addAttribute("error", "CseLicenseException");
        }
        catch (IOException e)
        {
            message = "license.upload.error";
            model.addAttribute("error", "Throwable");
        }
        finally
        {
            deleteLocalFile(licenseInfo);
        }
        if (message != null)
        {
            model.addAttribute("message", message);
            return gotoLicenseInfo(model);
        }
        else
        {
            userLog.setDetail(UserLogType.LICENSE_UPLOAD.getDetails(null));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
            userLogService.update(userLog);
            return "license/licenseConfirm";
        }
    }
    
    private LicenseImportResult parseLicenseFile(Model model, long optId, Map<String, MultipartFile> fileMap)
        throws CseLicenseException, IOException, LicenseCompareException
    {
        LicenseImportResult result = new LicenseImportResult();
        if (null == fileMap)
        {
            return result;
        }
        
        CseLicenseInfo licenseInfo = null;
        String message = null;
        MultipartFile file = null;
        String uuid = null;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
        {
            if (!"licenseFile".equals(entry.getKey()))
            {
                continue;
            }
            
            file = entry.getValue();
            if (StringUtils.isEmpty(file.getOriginalFilename()))
            {
                message = "licesen.file.null.error";
                break;
            }
            if (!checkLicensePix(file.getOriginalFilename()))
            {
                message = "licesen.file.pix.error";
                break;
            }
            if (file.getSize() > MAX_SIZE)
            {
                message = "licesen.file.max.error";
                model.addAttribute("error", "sizeError");
                break;
            }
            licenseInfo = licenseService.getCseLicenseInfo(file, file.getOriginalFilename());
            if (null == licenseInfo || StringUtils.isEmpty(licenseInfo.getProductName()))
            {
                message = "license.file.error";
                model.addAttribute("error", "invalidLicense");
                break;
            }
            model.addAttribute("defaultTeams", getMaxTeamspaces(licenseInfo.getUsers()));
            licenseService.checkDifferenceWithCurrent(licenseInfo);
            uuid = UUID.randomUUID().toString();
            licenseService.saveToDb(licenseInfo, uuid, optId, LicenseService.STATUS_WAIT_CONFIRM);
            model.addAttribute("licenseUuid", uuid);
            
            result.setSuccess(true);
            break;
        }
        
        result.setMessage(message);
        result.setLicenseInfo(licenseInfo);
        return result;
    }
    
    /**
     * @param currentLicense
     * @return
     */
    private static int getMaxTeamspaces(int currentUsers)
    {
        int result = 0;
        if (currentUsers > 1000000)
        {
            result = currentUsers / 1000000 * 100000;
        }
        else if (currentUsers > 100000)
        {
            result = currentUsers / 100000 * 10000;
        }
        else if (currentUsers > 10000)
        {
            result = currentUsers / 10000 * 1000;
        }
        else if (currentUsers > 1000)
        {
            result = currentUsers / 1000 * 100;
        }
        else
        {
            result = currentUsers / 100 * 10;
        }
        return result;
    }
    
    private void deleteLocalFile(CseLicenseInfo licenseInfo)
    {
        if (null != licenseInfo && licenseInfo.getFile() != null)
        {
            try
            {
                FileUtils.forceDelete(licenseInfo.getFile());
            }
            catch (IOException e)
            {
                logger.warn("Can not delete the temp license file" + licenseInfo.getFile().getName());
            }
        }
    }
    
    private void transNode(LicenseNode licenseNode)
    {
        if (licenseNode == null)
        {
            return;
        }
        if (licenseNode.getLicenseId() == null)
        {
            licenseNode.setLicenseId("-");
        }
    }
    
    private boolean checkLicensePix(String fileName)
    {
        return checksuffix(fileName, LICENSE_DAT) || checksuffix(fileName, LICENSE_XML);
    }
    
    private String getSuffix(String fileName)
    {
        String[] temp = fileName.split("\\.");
        int len = temp.length;
        if (len < 2)
        {
            return "";
        }
        String fileSuffix = temp[len - 1];
        return fileSuffix;
    }
    
    private boolean checksuffix(String fileName, String suffix)
    {
        String fileSuffix = getSuffix(fileName);
        
        return suffix.equalsIgnoreCase(fileSuffix);
        
    }
}

package com.huawei.sharedrive.uam.enterpriseuser.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.user.domain.Admin;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogOperateType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.QueryCondition;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ImportEnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.ImportEnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.impl.EnterpriseUserImportor;
import com.huawei.sharedrive.uam.enterpriseuser.service.impl.ImportEnterpriseUsersThread;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/import/enterprise/admin/user")
public class ImportEnterpriseUserController extends AbstractCommonController
{
    private static Logger logger = LoggerFactory.getLogger(ImportEnterpriseUserController.class);
    
    private static final long MAX_EXCEL_FILE_SIZE = Long.parseLong(PropertiesUtils.getProperty("user.import.excel.size",
        String.valueOf(3 * 1024))) * 1024L;
    
    private static final long MAX_IMPORT_USER = Integer.parseInt(PropertiesUtils.getProperty("user.import.user.size",
        "5000"));
    
    private static final String EXCEL_2003 = ".xls";
    
    private static final String EXCEL_2007 = ".xlsx";
    
    private static final String LOCAL_ZH_CN = "zh_CN";
    
    private static final String LOCAL_ZH = "zh";
    
    @Autowired
    private ImportEnterpriseUserManager importEnterpriseUserManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @RequestMapping(value = "enterImportEmployees/{authServerId}", method = RequestMethod.GET)
    public String enterImportEmployees(@PathVariable("authServerId") Long authServerId, Model model)
    {
        long enterpriseId = checkAdminAndGetId();
        ExcelImport excelImport = new ExcelImport();
        excelImport.setEnterpriseId(enterpriseId);
        excelImport.setAuthServerId(authServerId);
        List<ExcelImport> list = importEnterpriseUserManager.getByEnterAuthId(excelImport);
        
        model.addAttribute("excelImportList", list);
        model.addAttribute("authServerId", authServerId);
        model.addAttribute("unit", ExcelImport.MAX_IMPORT);
        return "/enterprise/admin/user/importEmployeesLog";
    }
    
    @RequestMapping(value = "/importEmployee/{authServerId}", method = RequestMethod.POST)
    public ResponseEntity<String> importUserFromUploadFile(@PathVariable("authServerId") Long authServerId,
        MultipartHttpServletRequest request, Model model, String token)
    {
        super.checkToken(token);
        String[] description = new String[]{getEnterpriseName(), getAuthServerName(authServerId)};
        String logId = systemLogManager.save(request,
            OperateType.ImportUser,
            OperateDescription.ENTERPRISE_EMPLOYEES_IMPORT,
            null,
            description);

        Admin admin = checkAdminAndGet();

        String saveState = "success";
        String errMsg = "";
        List<ImportEnterpriseUser> allUsers = null;
        Locale locale = Locale.CHINA;
        try
        {
            locale = setLocale(request);
            allUsers = parseFile(request, locale);
            String fileName = getFileName(request);
            if (allUsers.isEmpty())
            {
                throw new InvalidParamterException("file format error");
            }
            if (allUsers.size() > MAX_IMPORT_USER)
            {
                throw new ValidationException("User max size should be: " + MAX_IMPORT_USER + "  invalid size: " + allUsers.size());
            }

            LogOwner owner = new LogOwner();
            owner.setEnterpriseId(admin.getEnterpriseId());
            owner.setLoginName(admin.getLoginName());
            owner.setAuthServerId(authServerId);
            owner.setIp(IpUtils.getClientAddress(request));
            owner.setOperatType(AdminLogOperateType.KEY_IMPORT_USER.getValue() + "");
            owner.setDescription(fileName);

            ImportEnterpriseUsersThread importThread = new ImportEnterpriseUsersThread(allUsers, owner, locale, importEnterpriseUserManager);
            EnterpriseUserImportor.doImport(importThread);
        }
        catch (Exception e)
        {
            saveState = "fail";
            errMsg = OperateDescription.EXCEL_CLIENT_UPLOAD_FAILED.getDetails(locale, null);
            logger.error(errMsg, e);
        }
        
        ExcelImport excelImport = new ExcelImport();
        excelImport.setAuthServerId(authServerId);
        excelImport.setEnterpriseId(admin.getEnterpriseId());
        List<ExcelImport> list = importEnterpriseUserManager.getByEnterAuthId(excelImport);
        model.addAttribute("excelImportList", list);
        model.addAttribute("saveState", saveState);
        model.addAttribute("errMsg", errMsg);
        model.addAttribute("unit", ExcelImport.MAX_IMPORT);
        if ("success".equals(saveState))
        {
            systemLogManager.updateSuccess(logId);
            return new ResponseEntity<>("success", HttpStatus.OK);
        }


        return new ResponseEntity<>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private String getAuthServerName(long authServerId)
    {
        try
        {
            AuthServer authServer = authServerManager.getAuthServer(authServerId);
            return authServer.getName();
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    private Locale setLocale(MultipartHttpServletRequest request)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        String language = locale.getLanguage();
        if (LOCAL_ZH_CN.equals(language) || LOCAL_ZH.equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        return locale;
    }
    
    private List<ImportEnterpriseUser> parseFile(MultipartHttpServletRequest request, Locale locale) throws BusinessException, IOException
    {
        List<ImportEnterpriseUser> allUsers = new ArrayList<ImportEnterpriseUser>(10);
        Map<String, MultipartFile> fileMap = request.getFileMap();
        
        MultipartFile file;
        String fileName;
        String suffix;
        List<ImportEnterpriseUser> users = null;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
        {
            file = entry.getValue();
            if (null == file)
            {
                continue;
            }

            fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName))
            {
                throw new InvalidParamterException("file name is null");
            }

            suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
            if (!EXCEL_2003.equals(suffix) && !EXCEL_2007.equals(suffix))
            {
                throw new InvalidParamterException("Invalid excel format");
            }

            if (file.getSize() <= 0)
            {
                throw new ValidationException("Minimum file should be greater than zero");
            }

            if (file.getSize() > MAX_EXCEL_FILE_SIZE)
            {
                throw new ValidationException("File max size should be: " + MAX_EXCEL_FILE_SIZE
                    + " invalid: " + file.getSize());
            }

            if (EXCEL_2003.equals(suffix))
            {
                users = importEnterpriseUserManager.parseFile2003(file.getInputStream(), locale);
            }
            else if (EXCEL_2007.equals(suffix))
            {
                users = importEnterpriseUserManager.parseFile2007(file.getInputStream(), locale);
            }

            if (null != users)
            {
                allUsers.addAll(users);
            }
        }

        return allUsers;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "logList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<EnterpriseAdminLog>> enterpriseUserImportLogList(Integer page, String filter,
        String newHeadItem, boolean newFlag, String token, HttpServletRequest req)
    {
        long enterpriseId = checkAdminAndGetId();
        Locale locale = setLocale(req);
        
        super.checkToken(token);
        if (null == page || page < 1)
        {
            page = 1;
        }
        PageRequest request = new PageRequest();
        request.setSize(Constants.PAGE_SIZE);
        request.setPage(page);
        if (StringUtils.isNotBlank(newHeadItem))
        {
            Order order = new Order();
            order.setField(newHeadItem);
            order.setDesc(newFlag);
            request.setOrder(order);
        }
        QueryCondition qc = new QueryCondition();
        qc.setPageRequest(request);
        qc.setEnterpriseId(enterpriseId);
        qc.setOperatDesc(filter);
        qc.setOperateType(AdminLogOperateType.KEY_IMPORT_USER.getValue());
        Page<EnterpriseAdminLog> enterpriseUserLogList = adminLogManager.getSyncLog(qc, locale);
        return new ResponseEntity<Page<EnterpriseAdminLog>>(enterpriseUserLogList, HttpStatus.OK);
    }
    
    private Locale setLocale(HttpServletRequest req)
    {
        Locale locale = RequestContextUtils.getLocaleResolver(req).resolveLocale(req);
        String language = locale.getLanguage();
        if ("zh_CN".equals(language) || "zh".equals(language))
        {
            locale = Locale.CHINA;
        }
        else
        {
            locale = Locale.ENGLISH;
        }
        return locale;
    }
    
    private String getFileName(MultipartHttpServletRequest request)
    {
        Map<String, MultipartFile> fileMap = request.getFileMap();
        
        MultipartFile file;
        
        String fileName = "";
        
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
        {
            file = entry.getValue();
            if (null == file)
            {
                continue;
            }
            fileName = file.getOriginalFilename();
            if (!StringUtils.isEmpty(fileName))
            {
                break;
            }
            if (StringUtils.isBlank(fileName))
            {
                throw new InvalidParamterException("file name is null");
            }
        }
        return fileName;
    }
}

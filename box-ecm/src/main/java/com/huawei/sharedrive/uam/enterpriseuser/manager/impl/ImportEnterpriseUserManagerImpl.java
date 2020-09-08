package com.huawei.sharedrive.uam.enterpriseuser.manager.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogOperateType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.EnterpriseAdminLog;
import com.huawei.sharedrive.uam.enterpriseadminlog.service.AdminLogService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ImportEnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.ImportEnterpriseUserManager;
import com.huawei.sharedrive.uam.enterpriseuser.service.ExcelImportService;
import com.huawei.sharedrive.uam.enterpriseuser.service.ImportEmployeesExcelService;
import com.huawei.sharedrive.uam.exception.BusinessException;

@Component
public class ImportEnterpriseUserManagerImpl implements ImportEnterpriseUserManager
{
    
    @Value("${admin.import.user.log.count}")
    private int importUserLogCount;
    
    @Autowired
    private ExcelImportService excelImportService;
    
    @Autowired
    private ImportEmployeesExcelService importEmployeesExcelService;
    
    @Autowired
    private AdminLogService adminLogService;
    
    @Override
    public List<ExcelImport> getByEnterAuthId(ExcelImport excelImport)
    {
        return excelImportService.getByEnterAuthId(excelImport);
    }
    
    @Override
    public List<ImportEnterpriseUser> parseFile2007(InputStream excelFile, Locale locale) throws BusinessException
    {
        return importEmployeesExcelService.parseExcelFile2007(excelFile, locale);
    }
    
    @Override
    public List<ImportEnterpriseUser> parseFile2003(InputStream excelFile, Locale locale) throws BusinessException
    {
        return importEmployeesExcelService.parseExcelFile2003(excelFile, locale);
    }
    
    @Override
    public void importUserListToDB(List<ImportEnterpriseUser> allUsers, LogOwner owner, Locale locale)
    {
        importEmployeesExcelService.importUserListToDB(allUsers, owner, locale);
        
        long enterpriseId = owner.getEnterpriseId();
        String operateType = AdminLogOperateType.KEY_IMPORT_USER.getValue() + "";
        autoDeleteExcessLog(enterpriseId, operateType);
    }
    
    @Override
    public void autoDeleteExcessLog(long enterpriseId, String operateType)
    {
        int enterpriseLogCount = adminLogService.getFilterCount(enterpriseId, operateType);
        if (enterpriseLogCount > importUserLogCount)
        {
            EnterpriseAdminLog log = adminLogService.getMinCreateTime(enterpriseId, operateType);
            log.setEnterpriseId(enterpriseId);
            log.setOperatType(operateType);
            adminLogService.deleteImportUserRecordLog(log);
        }
    }
    
}

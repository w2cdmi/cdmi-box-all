package com.huawei.sharedrive.uam.enterpriseuser.manager;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ImportEnterpriseUser;
import com.huawei.sharedrive.uam.exception.BusinessException;

public interface ImportEnterpriseUserManager
{
    List<ExcelImport> getByEnterAuthId(ExcelImport excelImport);
    
    List<ImportEnterpriseUser> parseFile2007(InputStream excelFile, Locale locale) throws BusinessException;
    
    List<ImportEnterpriseUser> parseFile2003(InputStream excelFile, Locale locale) throws BusinessException;
    
    void importUserListToDB(List<ImportEnterpriseUser> allUsers, LogOwner owner, Locale locale);
    
    void autoDeleteExcessLog(long enterpriseId, String operateType);
}

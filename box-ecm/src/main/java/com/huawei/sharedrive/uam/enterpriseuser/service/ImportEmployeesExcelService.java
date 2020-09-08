package com.huawei.sharedrive.uam.enterpriseuser.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ImportEnterpriseUser;
import com.huawei.sharedrive.uam.exception.BusinessException;

public interface ImportEmployeesExcelService
{
    void exportEmployeeList(HttpServletRequest request, HttpServletResponse response, EnterpriseUser enterpriseUser, String id) throws IOException;
    
    List<ImportEnterpriseUser> parseExcelFile2007(InputStream excelFile, Locale locale) throws BusinessException;
    
    List<ImportEnterpriseUser> parseExcelFile2003(InputStream excelFile, Locale locale) throws BusinessException;
    
    void importUserListToDB(List<ImportEnterpriseUser> allUsers, LogOwner owner, Locale locale);
}

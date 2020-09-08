package com.huawei.sharedrive.uam.enterpriseuser.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;

public interface EmployeesExcelService
{
    void downloadEmployeeInfoTemplateFile(HttpServletRequest request, HttpServletResponse response,long enterpriseId)
        throws IOException;
    
    void exportEmployeeList(HttpServletRequest request, HttpServletResponse response,
        EnterpriseUser enterpriseUser, String id) throws IOException;
    
}

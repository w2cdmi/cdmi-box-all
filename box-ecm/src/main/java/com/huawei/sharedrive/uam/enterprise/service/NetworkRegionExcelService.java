package com.huawei.sharedrive.uam.enterprise.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.uam.enterprise.domain.ImportNetRegionIp;
import com.huawei.sharedrive.uam.exception.BusinessException;

public interface NetworkRegionExcelService
{
    void downloadNetRegionIpTemplateFile(HttpServletRequest request, HttpServletResponse response)
        throws IOException;
    
    String exportNetRegionList(long accountId, HttpServletRequest request, HttpServletResponse response)
        throws IOException;
    
    void exportImportExcelResult(HttpServletRequest request, HttpServletResponse response, long id)
        throws IOException;
    
    List<ImportNetRegionIp> parseFile2003ForNetRegionIp(InputStream excelFile, Locale locale)
        throws BusinessException;
    
    List<ImportNetRegionIp> parseFile2007ForNetRegionIp(InputStream excelFile, Locale locale)
        throws BusinessException;
    
    void importNetRegionIpListToDB(List<ImportNetRegionIp> allUsers, long id, Locale locale, long accountId);
    
    boolean isDuplicateTask(long accountId);
}

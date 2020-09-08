package com.huawei.sharedrive.uam.enterpriseuser.service;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;

public interface ExcelImportService
{
    
    List<ExcelImport> getByEnterAuthId(ExcelImport excelImport);
    
    ExcelImport getById(String id);
    
    void delete(String id);
    
    String insert(ExcelImport excelImport);
    
    void update(ExcelImport excelImport);
    
}

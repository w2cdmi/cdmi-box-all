package com.huawei.sharedrive.uam.enterpriseuser.dao;

import java.util.List;

import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;

public interface ExcelImportDao
{
    
    List<ExcelImport> getByEnterAuthId(ExcelImport excelImport);
    
    ExcelImport getById(String id);
    
    void delete(String id);
    
    String insert(ExcelImport excelImport);
    
    void update(ExcelImport excelImport);
    
}

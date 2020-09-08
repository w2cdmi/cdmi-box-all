package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterpriseuser.dao.ExcelImportDao;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;
import com.huawei.sharedrive.uam.enterpriseuser.service.ExcelImportService;

@Service
public class ExcelImportServiceImpl implements ExcelImportService
{
    @Autowired
    private ExcelImportDao excelImportDao;
    
    @Override
    public List<ExcelImport> getByEnterAuthId(ExcelImport excelImport)
    {
        return excelImportDao.getByEnterAuthId(excelImport);
    }
    
    @Override
    public ExcelImport getById(String id)
    {
        return excelImportDao.getById(id);
    }
    
    @Override
    public void delete(String id)
    {
        excelImportDao.delete(id);
        
    }
    
    @Override
    public String insert(ExcelImport excelImport)
    {
        List<ExcelImport> list = getByEnterAuthId(excelImport);
        int size = list.size();
        ExcelImport iter = null;
        for (int i = 0; i < list.size(); i++)
        {
            if (size < ExcelImport.MAX_IMPORT)
            {
                break;
            }
            
            iter = list.get(i);
            if (!(iter.getStatus() == ExcelImport.IMPORTING))
            {
                delete(iter.getId());
                size--;
            }
        }
        if (size >= ExcelImport.MAX_IMPORT)
        {
            return null;
        }
        ExcelImport values = new ExcelImport();
        Date date = new Date();
        values.setRuntime(date);
        values.setEnterpriseId(excelImport.getEnterpriseId());
        values.setAuthServerId(excelImport.getAuthServerId());
        return excelImportDao.insert(values);
    }
    
    @Override
    public void update(ExcelImport excelImport)
    {
        Date date = new Date();
        excelImport.setCompleteTime(date);
        excelImportDao.update(excelImport);
        
    }
    
}

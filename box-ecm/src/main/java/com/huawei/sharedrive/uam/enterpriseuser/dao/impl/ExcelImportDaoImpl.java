package com.huawei.sharedrive.uam.enterpriseuser.dao.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.enterpriseuser.dao.ExcelImportDao;
import com.huawei.sharedrive.uam.enterpriseuser.domain.ExcelImport;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class ExcelImportDaoImpl extends CacheableSqlMapClientDAO implements ExcelImportDao
{
    
    @Override
    public List<ExcelImport> getByEnterAuthId(ExcelImport excelImport)
    {
        return sqlMapClientTemplate.queryForList("ExcelImport.getByEnterAuthId", excelImport);
    }
    
    @Override
    public ExcelImport getById(String id)
    {
        
        return (ExcelImport) sqlMapClientTemplate.queryForObject("ExcelImport.getByID", id);
    }
    
    @Override
    public void delete(String id)
    {
        sqlMapClientTemplate.delete("ExcelImport.delete", id);
    }
    
    @Override
    public String insert(ExcelImport excelImport)
    {
        excelImport.setId(UUID.randomUUID().toString().replace("-", ""));
        sqlMapClientTemplate.insert("ExcelImport.insert", excelImport);
        return excelImport.getId();
    }
    
    @Override
    public void update(ExcelImport excelImport)
    {
        sqlMapClientTemplate.update("ExcelImport.update", excelImport);
    }
    
}

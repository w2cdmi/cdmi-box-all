/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.dao.impl;

import org.springframework.stereotype.Repository;

import pw.cdmi.core.db.dao.MultiTableEntity;
import pw.cdmi.file.engine.core.ibatis.IbatisMultiTableSupportDAO;
import pw.cdmi.file.engine.object.dao.FileObjectDao;
import pw.cdmi.file.engine.object.domain.FileObject;

/**
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings({"deprecation"})
@Repository("fileObjectDao")
public class FileObjectDaoImpl extends IbatisMultiTableSupportDAO<FileObject> implements FileObjectDao
{
    @Override
    protected void doInsert(FileObject obj)
    {
        MultiTableEntity<FileObject> entry = generateMultiTableEntity(obj);
        sqlMapClientTemplate.insert(warpSqlstatement(FileObject.class, SQL_INSERT), entry.buildParameterMap());
    }
    
    @Override
    protected void doDelete(FileObject obj)
    {
        MultiTableEntity<FileObject> entry = generateMultiTableEntity(obj);
        sqlMapClientTemplate.delete(warpSqlstatement(FileObject.class, SQL_DELETE), entry.buildParameterMap());
    }
    
    @Override
    protected FileObject doSelect(FileObject obj)
    {
        MultiTableEntity<FileObject> entry = generateMultiTableEntity(obj);
        return (FileObject) sqlMapClientTemplate.queryForObject(warpSqlstatement(FileObject.class, SQL_SELECT),
            entry.buildParameterMap());
    }
    
    @Override
    protected int doUpdate(FileObject obj)
    {
        MultiTableEntity<FileObject> entry = generateMultiTableEntity(obj);
        return sqlMapClientTemplate.update(warpSqlstatement(FileObject.class, SQL_UPDATE), entry.buildParameterMap());
    }
    
    @Override
    protected MultiTableEntity<FileObject> generateMultiTableEntity(final FileObject fileObject)
    {
        return new FileObjectMultiTableEntity(fileObject);
    }
    
    private static class FileObjectMultiTableEntity extends MultiTableEntity<FileObject>
    {
        protected FileObjectMultiTableEntity(FileObject obj)
        {
            super(obj);
        }

        @Override
        protected String getTablePrefix()
        {
            return "fileobject_";
        }
        
        @Override
        protected String getSplitKey()
        {
            return this.getObject().getObjectID();
        }
    }
}

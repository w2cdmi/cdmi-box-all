/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import pw.cdmi.core.db.dao.MultiTableEntity;
import pw.cdmi.file.engine.core.ibatis.IbatisMultiTableSupportDAO;
import pw.cdmi.file.engine.object.dao.FileObjectAttachmentDao;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectAttachment;

/**
 * 
 * @author s90006125
 * 
 */
@SuppressWarnings("deprecation")
@Repository("fileObjectAttachmentDao")
public class FileObjectAttachmentDaoImpl extends IbatisMultiTableSupportDAO<FileObjectAttachment> implements
    FileObjectAttachmentDao
{
    
    private static final String TABLE_PREFIX = "fileobject_attachment_";
    
    @Override
    protected void doInsert(FileObjectAttachment obj)
    {
        MultiTableEntity<FileObjectAttachment> entry = generateMultiTableEntity(obj);
        sqlMapClientTemplate.insert(warpSqlstatement(FileObjectAttachment.class, SQL_INSERT),
            entry.buildParameterMap());
    }
    
    @Override
    protected void doDelete(FileObjectAttachment obj)
    {
        MultiTableEntity<FileObjectAttachment> entry = generateMultiTableEntity(obj);
        sqlMapClientTemplate.insert(warpSqlstatement(FileObjectAttachment.class, SQL_DELETE),
            entry.buildParameterMap());
    }
    
    @Override
    public void deleteByFileObject(final FileObject fileObject)
    {
        MultiTableEntity<FileObject> entry = generateMultiTableEntity(fileObject);
        
        sqlMapClientTemplate.insert(warpSqlstatement(FileObjectAttachment.class, "deleteByFileObject"),
            entry.buildParameterMap());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<FileObjectAttachment> queryAll(final FileObject fileObject)
    {
        MultiTableEntity<FileObject> entry = generateMultiTableEntity(fileObject);
        
        return sqlMapClientTemplate.queryForList(warpSqlstatement(FileObjectAttachment.class,
            "selectAllByFileObject"),
            entry.buildParameterMap());
    }
    
    @Override
    protected FileObjectAttachment doSelect(FileObjectAttachment obj)
    {
        MultiTableEntity<FileObjectAttachment> entry = generateMultiTableEntity(obj);
        return (FileObjectAttachment) sqlMapClientTemplate.queryForObject(warpSqlstatement(FileObjectAttachment.class,
            SQL_SELECT),
            entry.buildParameterMap());
    }
    
    @Override
    protected int doUpdate(FileObjectAttachment obj)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected MultiTableEntity<FileObjectAttachment> generateMultiTableEntity(final FileObjectAttachment obj)
    {
        return new AttachementMultiTableEntity(obj);
    }
    
    protected MultiTableEntity<FileObject> generateMultiTableEntity(final FileObject obj)
    {
        return new FileObjectMultiTableEntity(obj);
    }
    
    private static class AttachementMultiTableEntity extends MultiTableEntity<FileObjectAttachment>
    {
        protected AttachementMultiTableEntity(FileObjectAttachment obj)
        {
            super(obj);
        }

        @Override
        protected String getTablePrefix()
        {
            return TABLE_PREFIX;
        }
        
        @Override
        protected String getSplitKey()
        {
            return this.getObject().getObjectID();
        }
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
            return TABLE_PREFIX;
        }
        
        @Override
        protected String getSplitKey()
        {
            return this.getObject().getObjectID();
        }
    }
}

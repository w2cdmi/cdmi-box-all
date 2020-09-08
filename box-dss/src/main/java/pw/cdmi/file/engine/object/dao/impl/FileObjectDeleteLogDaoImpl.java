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
import pw.cdmi.file.engine.object.dao.FileObjectDeleteLogDao;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteLog;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteTask;

/**
 * fileobject_delete_log表，删除失败，删除成功的数据，都记录到该表中，作为日志归档
 * @author s90006125
 *
 */
@Repository("ileObjectDeleteLogDao")
public class FileObjectDeleteLogDaoImpl extends IbatisMultiTableSupportDAO<FileObjectDeleteLog> implements FileObjectDeleteLogDao
{
    @Override
    protected void doInsert(FileObjectDeleteLog obj)
    {
        MultiTableEntity<FileObjectDeleteLog> entry = generateMultiTableEntity(obj);
        sqlMapClientTemplate.insert(warpSqlstatement(FileObjectDeleteTask.class, SQL_INSERT), entry.buildParameterMap());
    }

    @Override
    protected void doDelete(FileObjectDeleteLog obj)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected FileObjectDeleteLog doSelect(FileObjectDeleteLog obj)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int doUpdate(FileObjectDeleteLog obj)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    protected MultiTableEntity<FileObjectDeleteLog> generateMultiTableEntity(FileObjectDeleteLog obj)
    {
        return new FileObjectMultiTableEntity(obj);
    }
    
    private static class FileObjectMultiTableEntity extends MultiTableEntity<FileObjectDeleteLog>
    {
        protected FileObjectMultiTableEntity(FileObjectDeleteLog obj)
        {
            super(obj);
        }

        @Override
        protected String getTablePrefix()
        {
            return "fileobject_delete_log_";
        }
        
        @Override
        protected String getSplitKey()
        {
            return this.getObject().getObjectID();
        }
        
        @Override
        protected int getTableCount()
        {
            return 20;
        }
    }
}

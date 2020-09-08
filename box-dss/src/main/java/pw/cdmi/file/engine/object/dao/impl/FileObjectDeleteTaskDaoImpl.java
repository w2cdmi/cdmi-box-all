/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import pw.cdmi.core.db.dao.MultiTableEntity;
import pw.cdmi.file.engine.core.ibatis.IbatisMultiTableSupportDAO;
import pw.cdmi.file.engine.object.dao.FileObjectDeleteTaskDao;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteTask;

/**
 * fileobject_delete_task表，只保存等待删除的数据，删除失败和删除成功的数据，都不在这张表保存
 * @author s90006125
 *
 */
@Repository("fileObjectDeleteTaskDao")
public class FileObjectDeleteTaskDaoImpl extends IbatisMultiTableSupportDAO<FileObjectDeleteTask> implements FileObjectDeleteTaskDao
{
    private static final String SQL_LIST = "list";
    
    @Override
    protected void doDelete(FileObjectDeleteTask obj)
    {
        MultiTableEntity<FileObjectDeleteTask> entry = generateMultiTableEntity(obj);
        sqlMapClientTemplate.delete(warpSqlstatement(FileObject.class, SQL_DELETE), entry.buildParameterMap());
    }

    @Override
    protected void doInsert(FileObjectDeleteTask obj)
    {
        MultiTableEntity<FileObjectDeleteTask> entry = generateMultiTableEntity(obj);
        sqlMapClientTemplate.insert(warpSqlstatement(FileObjectDeleteTask.class, SQL_INSERT), entry.buildParameterMap());
    }

    @Override
    protected FileObjectDeleteTask doSelect(FileObjectDeleteTask obj)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FileObjectDeleteTask> listFileObjectDeleteTask(Map<String, Object> parameter)
    {
        return sqlMapClientTemplate.queryForList(warpSqlstatement(FileObjectDeleteTask.class, SQL_LIST), parameter);
    }
    
    @Override
    public void resumeFailedFileObjectDeleteTask(Map<String, Object> parameter)
    {
        sqlMapClientTemplate.update(warpSqlstatement(FileObjectDeleteTask.class, "resumeFailed"), parameter);
    }
    
    @Override
    protected int doUpdate(FileObjectDeleteTask obj)
    {
        MultiTableEntity<FileObjectDeleteTask> entry = generateMultiTableEntity(obj);
        return sqlMapClientTemplate.update(warpSqlstatement(FileObjectDeleteTask.class, SQL_UPDATE), entry.buildParameterMap());
    }

    @Override
    public int markFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask)
    {
        MultiTableEntity<FileObjectDeleteTask> entry = generateMultiTableEntity(fileObjectDeleteTask);
        return sqlMapClientTemplate.update(warpSqlstatement(FileObjectDeleteTask.class, "markFileObjectDeleteTask"), entry.buildParameterMap());
    }
    
    @Override
    protected MultiTableEntity<FileObjectDeleteTask> generateMultiTableEntity(final FileObjectDeleteTask fileObject)
    {
        return new FileObjectMultiTableEntity(fileObject);
    }
    
    private static class FileObjectMultiTableEntity extends MultiTableEntity<FileObjectDeleteTask>
    {
        protected FileObjectMultiTableEntity(FileObjectDeleteTask obj)
        {
            super(obj);
        }

        @Override
        protected String getTablePrefix()
        {
            return "fileobject_delete_task_";
        }
        
        @Override
        protected String getSplitKey()
        {
            return this.getObject().getObjectID();
        }
        
        @Override
        protected int getTableCount()
        {
            return 10;
        }
    }
}

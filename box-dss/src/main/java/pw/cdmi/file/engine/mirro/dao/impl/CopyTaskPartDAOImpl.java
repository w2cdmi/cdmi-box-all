package pw.cdmi.file.engine.mirro.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.mirro.dao.CopyTaskPartDAO;
import pw.cdmi.file.engine.mirro.domain.CopyTaskPart;

@SuppressWarnings({"unchecked", "deprecation"})
@Repository
public class CopyTaskPartDAOImpl extends IbatisSupportDAO<CopyTaskPart> implements CopyTaskPartDAO
{
    public int countCopyTaskPart(String taskId)
    {
        return ((Integer) this.sqlMapClientTemplate.queryForObject(warpSqlstatement(CopyTaskPart.class, "countCopyTaskPart"), taskId)).intValue();
    }
    
    public void batchReplace(List<CopyTaskPart> parts)
    {
        this.sqlMapClientTemplate.insert(warpSqlstatement(CopyTaskPart.class, "batchReplace"), parts);
    }
    
    public List<CopyTaskPart> listCopyTaskPartByTaskId(String taskId)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(CopyTaskPart.class, "listCopyTaskPartByTaskId"), taskId);
    }
    
    public int countUnSuccessTaskParts(String taskId)
    {
        return ((Integer) this.sqlMapClientTemplate.queryForObject(warpSqlstatement(CopyTaskPart.class, "countUnSuccessTaskParts"), taskId)).intValue();
    }
    
    public void updateTaskPartsStatus(CopyTaskPart taskPart)
    {
        this.sqlMapClientTemplate.update(warpSqlstatement(CopyTaskPart.class, "updateTaskSartsStatus"), taskPart);
    }
    
    @Override
    public void deleteByTaskId(String taskId)
    {
        this.sqlMapClientTemplate.delete(warpSqlstatement(CopyTaskPart.class, "deleteByTaskId"), taskId);
    }
    
    @Override
    public void deleteUnExistTaskPart()
    {
        this.sqlMapClientTemplate.delete(warpSqlstatement(CopyTaskPart.class, "deleteUnExistTaskPart"));
    }
    
    public int update(CopyTaskPart obj, Map<String, Object> map)
    {
        throw new UnsupportedOperationException();
    }
    
    protected void doDelete(CopyTaskPart obj)
    {
        throw new InnerException("unimplement method");
    }
    
    protected void doInsert(CopyTaskPart obj)
    {
        throw new InnerException("unimplement method");
    }
    
    protected CopyTaskPart doSelect(CopyTaskPart obj)
    {
        throw new InnerException("unimplement method");
    }
    
    protected int doUpdate(CopyTaskPart obj)
    {
        throw new UnsupportedOperationException();
    }
}
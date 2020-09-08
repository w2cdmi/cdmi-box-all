package pw.cdmi.file.engine.mirro.dao.impl;

import org.springframework.stereotype.Repository;

import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.mirro.dao.CopyExeTimeDao;
import pw.cdmi.file.engine.mirro.domain.CopyExeTime;

@SuppressWarnings({"unchecked", "deprecation"})
@Repository
public class CopyExeTimeDaoImpl extends IbatisSupportDAO<CopyExeTime> implements CopyExeTimeDao
{
    @Override
    public void insertExeTimeRecord(CopyExeTime copyExe)
    {
        this.sqlMapClientTemplate.insert(warpSqlstatement(CopyExeTime.class, "insert"), copyExe);
        
    }

    @Override
    protected void doDelete(CopyExeTime obj)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void doInsert(CopyExeTime obj)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected CopyExeTime doSelect(CopyExeTime obj)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int doUpdate(CopyExeTime obj)
    {
        throw new UnsupportedOperationException();
    }
   
    
}

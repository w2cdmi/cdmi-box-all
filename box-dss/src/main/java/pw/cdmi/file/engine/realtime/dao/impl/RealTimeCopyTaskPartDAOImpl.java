/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.ibatis.IbatisSupportDAO;
import pw.cdmi.file.engine.realtime.dao.RealTimeCopyTaskPartDAO;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskPart;

@SuppressWarnings({"unchecked", "deprecation"})
@Repository
public class RealTimeCopyTaskPartDAOImpl extends IbatisSupportDAO<RealTimeCopyTaskPart> implements RealTimeCopyTaskPartDAO
{

    @Override
    public void updateTaskPartsStatus(RealTimeCopyTaskPart taskPart)
    {
        this.sqlMapClientTemplate.update(warpSqlstatement(RealTimeCopyTaskPart.class, "updateTaskPartsStatus"), taskPart);
        
    }

    @Override
    protected void doDelete(RealTimeCopyTaskPart obj)
    {
        throw new InnerException("unimplement method");
        
    }

    @Override
    protected void doInsert(RealTimeCopyTaskPart obj)
    {
        throw new InnerException("unimplement method");
        
    }

    @Override
    protected RealTimeCopyTaskPart doSelect(RealTimeCopyTaskPart obj)
    {
        throw new InnerException("unimplement method");
    }

    @Override
    protected int doUpdate(RealTimeCopyTaskPart obj)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int countRealTimeCopyTaskPart(String taskId)
    {
        return ((Integer) this.sqlMapClientTemplate.queryForObject(warpSqlstatement(RealTimeCopyTaskPart.class, "countRealTimeCopyTaskPart"), taskId)).intValue();
    }

    @Override
    public void batchReplace(List<RealTimeCopyTaskPart> parts)
    {
        this.sqlMapClientTemplate.insert(warpSqlstatement(RealTimeCopyTaskPart.class, "batchReplace"), parts);      
    }

    @Override
    public List<RealTimeCopyTaskPart> listRealTimeCopyTaskPartByTaskId(String taskId)
    {
        return this.sqlMapClientTemplate.queryForList(warpSqlstatement(RealTimeCopyTaskPart.class, "listRealTimeCopyTaskPartByTaskId"), taskId);
    }

    @Override
    public int countUnSuccessTaskParts(String taskId)
    {
        return ((Integer) this.sqlMapClientTemplate.queryForObject(warpSqlstatement(RealTimeCopyTaskPart.class, "countUnSuccessTaskParts"), taskId)).intValue();
    }

    @Override
    public void deleteByTaskId(String taskId)
    {
        this.sqlMapClientTemplate.delete(warpSqlstatement(RealTimeCopyTaskPart.class, "deleteByTaskId"), taskId);
        
    }

    @Override
    public void deleteUnExistTaskPart()
    {
        this.sqlMapClientTemplate.delete(warpSqlstatement(RealTimeCopyTaskPart.class, "deleteUnExistTaskPart"));
        
    }
    
}

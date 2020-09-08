package com.huawei.sharedrive.app.mirror.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.SlaveDBSQLException;
import com.huawei.sharedrive.app.mirror.dao.CopyTaskSlaveDBDAO;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.slavedb.manager.SlaveDatabaseManager;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class CopyTaskSlaveDBDAOImpl implements CopyTaskSlaveDBDAO
{
    private static final String SQL_DBNAME = "sysdb";
    
    private static final String SQL_UPDATE = "update copy_task set exeType=?, exeStartAt=?,exeEndAt=?,modifiedAt=?,isPop=?,state=?,priority=?,exeResult=?,destResourceGroupId =? where taskId=?";
    
    private static final String SQL_UPDATEFORPOP = "update copy_task set modifiedAt=?,isPop=? where taskId=?";
    
    private static final String SQL_INSERT = "insert into copy_task(taskId,srcOwnedBy,srcINodeId,srcObjectId,fileName,destOwnedBy,destINodeId,destObjectId,size,copyType,exeType,createdAt,modifiedAt,isPop,exeStartAt,exeEndAt,policyId,srcRegionId,srcResourceGroupId,destRegionId,destResourceGroupId,state,priority,exeResult) values(?,?,?, ?,?,?, ?,?,?, ?,?,?,?,?,?, ?, ?,  ?,?,?,?,?,?,?)";
    
    private static final String SQL_DELETE = "delete from copy_task where taskId=?";
    
    private static final String SQL_GET = "select * from copy_task where taskId=?";
    
    private static final String SQL_UPDATETIMETASKSTATEFORSERIAL = "update copy_task set state=? where exeType=? and exeStartAt<exeEndAt and exeStartAt<? and exeEndAt>?";
    
    private static final String SQL_UPDATETIMETASKSTATEFORREVERSE1 = "update copy_task set state=? where exeType=? and exeStartAt>exeEndAt and exeStartAt<?";
    
    private static final String SQL_UPDATETIMETASKSTATEFORREVERSE2 = "update copy_task set state=? where exeType=? and exeStartAt>exeEndAt and exeEndAt>?";
    
    private static final String SQL_RECOVERYNOEXETASKFORPOPSTATE = "update copy_task set isPop=? where state=? and isPop=? and modifiedAt<?";
    
    private static final String SQL_STATISTICBYSTATE = "SELECT count(state) as number,SUM(size) as allSize from copy_task  where state=?";
    
    private static final String SQL_DEACTIVATOVERDUETIMETASKFORSERIAL1 = "update copy_task set state=? where state=? and isPop=? and exeType=? and exeStartAt<exeEndAt and exeStartAt>?";
    
    private static final String SQL_DEACTIVATOVERDUETIMETASKFORSERIAL2 = "update copy_task set state=? where state=? and isPop=? and exeType=? and exeStartAt<exeEndAt and exeEndAt<?";
    
    private static final String SQL_DEACTIVATOVERDUETIMETASKFORREVERSE = "update copy_task set state=? where state=? and isPop=? and exeType=? and exeStartAt>exeEndAt and exeStartAt>? and exeEndAt<?";
    
    private static final String SQL_GETTASKBYSRCOBJECTANDRESOURCEGROUPID = "select * from copy_task where srcObjectId=? and destResourceGroupId =?  and copyType=?";
    
    private static final String SQL_RECOVERYEXETIMEOUTTASK = "update copy_task set state=?,isPop=? where state=? and modifiedAt<?";
    
    private static final String SQL_RECOVERYFAILEDTASK = "update copy_task set state=?,isPop=?,exeResult=? where state=? and exeType=?";
    
    private static final String SQL_DELETETASKBYERRORCODE = "delete from copy_task where state=? and exeResult=?";
    
    private static final String SQL_GETBYSRCOBJECTID = "select * from copy_task where srcObjectId=?";
    
    private static final String SQL_DELETE_TASK_FOR_DATA_MIGRATION = "delete from copy_task where  srcOwnedBy = ? and copyType=?";
    
    private static final String SQL_CLEAR_NOT_EXECUTE_TASK_FOR_DATA_MIGRATION = "delete from copy_task where  srcOwnedBy = ? and copyType= ? and state <> ? and isPop=?";
    
    private static final String SQL_GET_NOT_COMPLETE_TASK_FOR_DATA_MIGRATION = "select count(*)  as total from copy_task where  srcOwnedBy = ? and copyType=?";
    
    private static final String SQL_DELETE_TASK= "delete from copy_task where  srcOwnedBy = ?";
    
    private static final String SQL_UPDATETASKSTATESAMEWITHSYSTEMCONFIGSTATE = "update copy_task set state=? where state = ?";
    
    private static final String SQL_SELECTALLPOLICYID = "select policyId from copy_task  group by policyId";
    
    private static final String SQL_DELETETASKBYPOLICY = "delete from copy_task where state=0 and isPop=false and policyId=? limit 10000";
    
    private static final String SQL_LSTTASKBYERRORCODE ="select * from copy_task where state=? and exeResult=?";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskSlaveDBDAOImpl.class);
    
    @Autowired
    private SlaveDatabaseManager slaveDatabaseManager;
    
    @Override
    public void create(CopyTask t) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_INSERT);
            ps.setString(1, t.getTaskId());
            ps.setLong(2, t.getSrcOwnedBy());
            ps.setLong(3, t.getSrcINodeId());
            ps.setString(4, t.getSrcObjectId());
            ps.setString(5, t.getFileName());
            ps.setLong(6, t.getDestOwnedBy());
            ps.setLong(7, t.getDestINodeId());
            ps.setString(8, t.getDestObjectId());
            ps.setLong(9, t.getSize());
            ps.setInt(10, t.getCopyType());
            ps.setInt(11, t.getExeType());
            ps.setString(12, DateUtils.dateToString(t.getCreatedAt()));
            ps.setString(13, DateUtils.dateToString(t.getModifiedAt()));
            ps.setBoolean(14, t.isPopValue());
            ps.setString(15, t.getExeStartAt());
            ps.setString(16, t.getExeEndAt());
            ps.setInt(17, t.getPolicyId());
            ps.setInt(18, t.getSrcRegionId());
            ps.setInt(19, t.getSrcResourceGroupId());
            ps.setInt(20, t.getDestRegionId());
            ps.setInt(21, t.getDestResourceGroupId());
            ps.setInt(22, t.getState());
            ps.setInt(23, t.getPriority());
            ps.setInt(24, t.getExeResult());
            execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("create use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("create use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    
    @Override
    public void delete(String id) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_DELETE);
            ps.setString(1, id);
            execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("delete use slavedb error" + e.getMessage(), e);
            throw e;
        }
        catch (Exception e)
        {
            LOGGER.error("delete use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    @Override
    public CopyTask get(String id) throws DataAccessException
    {
        List<CopyTask> lstCopyTasks = null;
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_GET);
            ps.setString(1, id);
            lstCopyTasks = getCopyTasks(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("get use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("get use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        if (!lstCopyTasks.isEmpty())
        {
            return lstCopyTasks.get(0);
        }
        return null;
    }
    
    
    @Override
    public void update(CopyTask t) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_UPDATE);
            ps.setInt(1, t.getExeType());
            ps.setString(2, t.getExeStartAt());
            ps.setString(3, t.getExeEndAt());
            ps.setString(4, DateUtils.dateToString(t.getModifiedAt()));
            ps.setBoolean(5, t.isPopValue());
            ps.setInt(6, t.getState());
            ps.setInt(7, t.getPriority());
            ps.setInt(8, t.getExeResult());
            ps.setInt(9, t.getDestResourceGroupId());
            ps.setString(10, t.getTaskId());
            execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("update use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("update use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    @Override
    public List<CopyTask> getBystatusAndExeType(int exeType, int state, boolean isPop, Limit limit) throws DataAccessException
    {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("select * from copy_task where state = ? and isPop = ? order by copyType desc ");
        if (null != limit)
        {
            sqlBuffer.append(" limit ").append("?,?");
        }
        List<CopyTask> lstCopyTask = null;
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(sqlBuffer.toString());
            ps.setInt(1, state);
            ps.setBoolean(2, isPop);
            if (null != limit)
            {
                ps.setLong(3, limit.getOffset());
                ps.setInt(4, limit.getLength());
            }
            lstCopyTask = getCopyTasks(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("getBystatusAndExeType use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("getBystatusAndExeType use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        return lstCopyTask;
    }
    
    
    @Override
    public void updateForPop(List<CopyTask> lstTask, boolean isPop)
    {
        if (null == lstTask || lstTask.isEmpty())
        {
            return;
        }
        Date date = new Date();
        for (CopyTask task : lstTask)
        {
            try
            {
                LOGGER.info(JsonUtils.toJson(task));
            }
            catch (Exception e)
            {
                LOGGER.info(e.getMessage());
            }
            if (1 == updateForPopForOne(task, isPop, date))
            {
                LOGGER.info("update copytask succeed,taskid is:" + task.getTaskId());
            }
            else
            {
                LOGGER.info("update copytask failed,nothing changed,taksid is:" + task.getTaskId());
            }
        }
    }
    
    /**
     * 主要为了重用一个sqlconnect连接，提高效率
     * @param lstTask
     * @param isPop
     * @throws DataAccessException
     */
    public void updateForPopForList(List<CopyTask> lstTask, boolean isPop) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        
        try
        {
            connection = getConnection(SQL_DBNAME);
            boolean autoCommit=connection.getAutoCommit();
            connection.setAutoCommit(false);
            Date date = null;
            for(CopyTask copyTask : lstTask)
            {
                try{
                    ps = connection.prepareStatement(SQL_UPDATEFORPOP);
                    date = new Date();
                    ps.setString(1, DateUtils.dateToString(date));
                    ps.setBoolean(2, isPop);
                    ps.setString(3, copyTask.getTaskId());
                    if(1 == execSql(ps))
                    {
                        LOGGER.info("update copytask succeed,taskid is:" + copyTask.getTaskId());
                    }
                    else
                    {
                        LOGGER.info("update copytask failed,nothing changed,taksid is:" + copyTask.getTaskId());
                    }
                }
                catch (RuntimeException e)
                {
                    LOGGER.error("updateForPopForList use slavedb error,taskid is:"+copyTask.getTaskId()+ " " + e.getMessage(), e);
                    continue;
                }
                catch (Exception e)
                {
                    LOGGER.error("updateForPopForList use slavedb error,taskid is:"+copyTask.getTaskId()+ " " + e.getMessage(), e);
                    continue;
                }
                finally
                {
                    closeStatement(ps);
                }
            }
            connection.commit();
            connection.setAutoCommit(autoCommit);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("updateForPopForList use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("updateForPopForList use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
        }
    }
    
    private int updateForPopForOne(CopyTask copyTask, boolean isPop, Date date) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_UPDATEFORPOP);
            ps.setString(1, DateUtils.dateToString(date));
            ps.setBoolean(2, isPop);
            ps.setString(3, copyTask.getTaskId());
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("updateForPopForOne use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("updateForPopForOne use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    
    @Override
    public void createCopyTask(List<CopyTask> lstTask)
    {
        if (null == lstTask || lstTask.isEmpty())
        {
            return;
        }
        
        for (CopyTask t : lstTask)
        {
            this.create(t);
        }
    }
    
    
    @Override
    public void activateTimeTask(String curTime) throws DataAccessException
    {
        // 当执行时间的结束时间大于开始时间时，是顺序
        // 当执行时间的结束时间小于开始时间时，是倒序
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_UPDATETIMETASKSTATEFORSERIAL);
            ps.setInt(1, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps.setInt(2, MirrorCommonStatic.EXE_TYPE_TIME);
            ps.setString(3, curTime);
            ps.setString(4, curTime);
            execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("activateTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("activateTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        
        Connection connection2 = null;
        PreparedStatement ps2 = null;
        try
        {
            connection2 = getConnection(SQL_DBNAME);
            ps2 = connection2.prepareStatement(SQL_UPDATETIMETASKSTATEFORREVERSE1);
            ps2.setInt(1, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps2.setInt(2, MirrorCommonStatic.EXE_TYPE_TIME);
            ps2.setString(3, curTime);
            execSql(ps2);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("activateTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("activateTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection2);
            closeStatement(ps2);
        }
        
        Connection connection3 = null;
        PreparedStatement ps3 = null;
        try
        {
            connection3 = getConnection(SQL_DBNAME);
            ps3 = connection3.prepareStatement(SQL_UPDATETIMETASKSTATEFORREVERSE2);
            ps3.setInt(1, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps3.setInt(2, MirrorCommonStatic.EXE_TYPE_TIME);
            ps3.setString(3, curTime);
            execSql(ps3);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("activateTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("activateTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection3);
            closeStatement(ps3);
        }
    }
    
    /**
     * 主要两种情况 1:执行时间是 开始时间小于结束执行，例如1点到6点执行 2:执行数据是 开始时间大于结束之际，例如22点6点执行
     */
    
    @Override
    public void deactivatOverdueTimeTask(String curTime) throws DataAccessException
    {
        // 考虑执行的开始时间和结束时间是否是顺序还是倒序
        
        // 当执行时间的结束时间大于开始时间时，是顺序
        // 当执行时间的结束时间小于开始时间时，是倒序
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_DEACTIVATOVERDUETIMETASKFORSERIAL1);
            ps.setInt(1, MirrorCommonStatic.TASK_STATE_NOACTIVATE);
            ps.setInt(2, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps.setBoolean(3, false);
            ps.setInt(4, MirrorCommonStatic.EXE_TYPE_TIME);
            ps.setString(5, curTime);
            execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("deactivatOverdueTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("deactivatOverdueTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        
        Connection connection2 = null;
        PreparedStatement ps2 = null;
        try
        {
            connection2 = getConnection(SQL_DBNAME);
            ps2 = connection2.prepareStatement(SQL_DEACTIVATOVERDUETIMETASKFORSERIAL2);
            ps2.setInt(1, MirrorCommonStatic.TASK_STATE_NOACTIVATE);
            ps2.setInt(2, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps2.setBoolean(3, false);
            ps2.setInt(4, MirrorCommonStatic.EXE_TYPE_TIME);
            ps2.setString(5, curTime);
            execSql(ps2);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("deactivatOverdueTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("deactivatOverdueTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection2);
            closeStatement(ps2);
        }
        
        Connection connection3 = null;
        PreparedStatement ps3 = null;
        try
        {
            connection3 = getConnection(SQL_DBNAME);
            ps3 = connection3.prepareStatement(SQL_DEACTIVATOVERDUETIMETASKFORREVERSE);
            ps3.setInt(1, MirrorCommonStatic.TASK_STATE_NOACTIVATE);
            ps3.setInt(2, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps3.setBoolean(3, false);
            ps3.setInt(4, MirrorCommonStatic.EXE_TYPE_TIME);
            ps3.setString(5, curTime);
            ps3.setString(6, curTime);
            execSql(ps3);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("deactivatOverdueTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("deactivatOverdueTimeTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection3);
            closeStatement(ps3);
        }
    }
    
    @Override
    public long recoveryNoExeTaskForPopState(Date outTime) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_RECOVERYNOEXETASKFORPOPSTATE);
            ps.setBoolean(1, false);
            ps.setInt(2, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps.setBoolean(3, true);
            ps.setString(4, DateUtils.dateToString(outTime));
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("recoveryNoExeTaskForPopState use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("recoveryNoExeTaskForPopState use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    @Override
    public Map<Long, Long> statisticCopyTask(int state) throws DataAccessException
    {
        // 為了實現簡單簡單，使用MAP 傳值，key 對應 number,value為SIZE
        Connection connection = null;
        PreparedStatement ps = null;
        Map<Long, Long> map = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_STATISTICBYSTATE);
            ps.setInt(1, state);
            map = getMirrorStatistic(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("statisticCopyTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("statisticCopyTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        return map;
    }
    
    
    @Override
    public long recoveryExeTimeOutTask(Date outTime) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_RECOVERYEXETIMEOUTTASK);
            ps.setInt(1, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps.setBoolean(2, false);
            ps.setInt(3, MirrorCommonStatic.TASK_STATE_EXEING);
            ps.setString(4, DateUtils.dateToString(outTime));
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("recoveryExeTimeOutTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("recoveryExeTimeOutTask use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    
    @Override
    public CopyTask getTaskBySrcObjectAndResourceGroupID(CopyTask task) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        List<CopyTask> lstCopyTask = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_GETTASKBYSRCOBJECTANDRESOURCEGROUPID);
            ps.setString(1, task.getSrcObjectId());
            ps.setInt(2, task.getDestResourceGroupId());
            ps.setInt(3, task.getCopyType());
            lstCopyTask = getCopyTasks(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("getTaskBySrcObjectAndResourceGroupID use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("getTaskBySrcObjectAndResourceGroupID use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        if (!lstCopyTask.isEmpty())
        {
            return lstCopyTask.get(0);
        }
        return null;
    }
    
    
    @Override
    public long recoveryFailedTaskForByExeType(int exeType) throws DataAccessException
    {
        CopyTask task = new CopyTask();
        if (MirrorCommonStatic.EXE_TYPE_NOW == exeType)
        {
            task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        }
        else
        {
            task.setState(MirrorCommonStatic.EXE_TYPE_TIME);
        }
        
        task.setExeResult(0);
        task.setPop(false);
        
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_RECOVERYFAILEDTASK);
            ps.setInt(1, task.getState());
            ps.setBoolean(2, false);
            ps.setInt(3, 0);
            ps.setInt(4, MirrorCommonStatic.TASK_STATE_FAILED);
            ps.setInt(5, exeType);
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("recoveryFailedTaskForByExeType use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("recoveryFailedTaskForByExeType use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    
    @Override
    public long deleteTaskByErrorCode(int errCode) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_DELETETASKBYERRORCODE);
            ps.setInt(1, MirrorCommonStatic.TASK_STATE_FAILED);
            ps.setInt(2, errCode);
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("deleteTaskByErrorCode use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("deleteTaskByErrorCode use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }
    
    @Override
    public List<CopyTask> getCopyTaskBySrcObjectId(String srcObjectId) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        List<CopyTask> lstCopyTasks = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_GETBYSRCOBJECTID);
            ps.setString(1, srcObjectId);
            lstCopyTasks = getCopyTasks(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("getCopyTaskBySrcObjectId use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("getCopyTaskBySrcObjectId use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        return lstCopyTasks;
    }
    
    private Map<Long, Long> getMirrorStatistic(PreparedStatement ps) throws SQLException
    {
        ResultSet res = null;
        Map<Long, Long> map = null;
        try
        {
            res = ps.executeQuery();
            map = new HashMap<Long, Long>(1);
            while (res.next())
            {
                map.put(res.getLong("number"), res.getLong("allSize"));
                break;
            }
        }
        finally
        {
            closeResultSet(res);
        }
        return map;
    }
    
    private int execSql(PreparedStatement ps) throws SQLException
    {
        int result = 0;
        result = ps.executeUpdate();
        return result;
    }
    
    private List<CopyTask> getCopyTasks(PreparedStatement ps) throws SQLException
    {
        ResultSet res = null;
        List<CopyTask> lstCopyTask = null;
        CopyTask copyTask = null;
        try
        {
            res = ps.executeQuery();
            lstCopyTask = new ArrayList<CopyTask>(3);
            while (res.next())
            {
                copyTask = new CopyTask();
                copyTask.setTaskId(res.getString("taskId"));
                copyTask.setSrcOwnedBy(res.getLong("srcOwnedBy"));
                copyTask.setSrcINodeId(res.getLong("srcINodeId"));
                copyTask.setSrcObjectId(res.getString("srcObjectId"));
                copyTask.setFileName(res.getString("fileName"));
                copyTask.setDestOwnedBy(res.getLong("destOwnedBy"));
                copyTask.setDestINodeId(res.getLong("destINodeId"));
                copyTask.setDestObjectId(res.getString("destObjectId"));
                copyTask.setSize(res.getLong("size"));
                copyTask.setCopyType(res.getInt("copyType"));
                copyTask.setExeType(res.getInt("exeType"));
                copyTask.setCreatedAt(res.getDate("createdAt"));
                copyTask.setModifiedAt(res.getDate("modifiedAt"));
                copyTask.setPop(res.getBoolean("isPop"));
                copyTask.setExeStartAt(res.getString("exeStartAt"));
                copyTask.setExeEndAt(res.getString("exeEndAt"));
                copyTask.setPolicyId(res.getInt("policyId"));
                copyTask.setSrcRegionId(res.getInt("srcRegionId"));
                copyTask.setSrcResourceGroupId(res.getInt("srcResourceGroupId"));
                copyTask.setDestRegionId(res.getInt("destRegionId"));
                copyTask.setDestResourceGroupId(res.getInt("destResourceGroupId"));
                copyTask.setState(res.getInt("state"));
                copyTask.setPriority(res.getInt("priority"));
                copyTask.setExeResult(res.getInt("exeResult"));
                lstCopyTask.add(copyTask);
            }
        }
        finally
        {
            closeResultSet(res);
        }
        return lstCopyTask;
    }
    
    private void closeResultSet(ResultSet rs)
    {
        if (null != rs)
        {
            try
            {
                rs.close();
            }
            catch (SQLException e)
            {
                LOGGER.debug("Caught while closing statement: " + e, e);
            }
        }
    }
    
    private void closeConnect(Connection connection)
    {
        if (connection != null)
        {
            try
            {
                if (!connection.getAutoCommit())
                {
                    connection.commit();
                }
            }
            catch (SQLException e)
            {
                LOGGER.error("commit connectioin error",e);
            }
            try
            {
                connection.close();
            }
            catch (SQLException e)
            {
                LOGGER.error("close connectioin error", e);
            }
        }
    }
    
    private void closeStatement(PreparedStatement stmt)
    {
        if (null != stmt)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException e)
            {
                LOGGER.debug("Caught while closing statement: " + e, e);
            }
        }
    }
    
    /**
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection getConnection(String dbName) throws ClassNotFoundException, SQLException
    {
        Connection conn = slaveDatabaseManager.getConnection(dbName);
        return conn;
    }
    
    @Override
    public long deleteTaskForDataMigration(long userId) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_DELETE_TASK_FOR_DATA_MIGRATION);
            ps.setLong(1, userId);
            ps.setInt(2, CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("deleteTaskForDataMigration use slavedb error" + e.getMessage(), e);
            // warning:返回-1，共上层业务处理，不能返回0
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("deleteTaskForDataMigration use slavedb error" + e.getMessage(), e);
            // warning:返回-1，共上层业务处理，不能返回0
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        
    }
    
    @Override
    public long getNotCompleteTaskForDataMigration(long userId) throws DataAccessException
    {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_GET_NOT_COMPLETE_TASK_FOR_DATA_MIGRATION);
            ps.setLong(1, userId);
            ps.setInt(2, CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
            
            res =  ps.executeQuery();
            
            while (res.next())
            {
                return res.getLong("total");
            }
            
            return 0;
        }
        
        catch (RuntimeException e)
        {
            LOGGER.error("getNotCompleteTaskForDataMigration use slavedb error" + e.getMessage(), e);
            // warning:返回-1，共上层业务处理，不能返回0
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("getNotCompleteTaskForDataMigration use slavedb error" + e.getMessage(), e);
            // warning:返回-1，共上层业务处理，不能返回0
            //eturn -1;
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeResultSet(res);
            closeConnect(connection);
            closeStatement(ps);
        }
    }

    
    
    @Override
    public long clearNotExeTaskForDataMigration(long userId) throws DataAccessException
    {
        
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_CLEAR_NOT_EXECUTE_TASK_FOR_DATA_MIGRATION);
            ps.setLong(1, userId);
            ps.setInt(2, CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
            ps.setInt(3, MirrorCommonStatic.TASK_STATE_WAITTING);
            ps.setBoolean(4, false);
            
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("deleteTaskForDataMigration use slavedb error" + e.getMessage(), e);
            // warning:返回-1，共上层业务处理，不能返回0
            //return -1;
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("deleteTaskForDataMigration use slavedb error" + e.getMessage(), e);
            // warning:返回-1，共上层业务处理，不能返回0
            //return -1;
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }


    @Override
    public long cleanMirrorCopyTaskBySrcOwnedby(long userId)
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_DELETE_TASK);
            ps.setLong(1, userId);
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("cleanMirrorCopyTaskBySrcOwnedby use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("cleanMirrorCopyTaskBySrcOwnedby use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }


    @Override
    public long updateTaskStateSameWithSystemConfigState(int newState, int oldState)
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_UPDATETASKSTATESAMEWITHSYSTEMCONFIGSTATE);
            ps.setLong(1, newState);
            ps.setLong(2, oldState);
            return execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("updateTaskStateSameWithSystemConfigState use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("updateTaskStateSameWithSystemConfigState use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }

    private List<Integer> getPolicyIds(PreparedStatement ps) throws SQLException
    {
        ResultSet res = null;
        List<Integer> list = null;
        try
        {
            res = ps.executeQuery();
            list = new ArrayList<Integer>(1);
            while (res.next())
            {
                list.add(res.getInt("policyId"));
                break;
            }
        }
        finally
        {
            closeResultSet(res);
        }
        return list;
    }
    
    @Override
    public List<Integer> selectAllPolicyId()
    {
        Connection connection = null;
        PreparedStatement ps = null;
        List<Integer> list = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_SELECTALLPOLICYID);
            list = getPolicyIds(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("selectAllPolicyId use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("selectAllPolicyId use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        return list;
    }


    @Override
    public void deleteCopyTaskByPolicy(Integer policyId)
    {
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_DELETETASKBYPOLICY);
            ps.setInt(1, policyId);
            execSql(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("deleteCopyTaskByPolicy use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("deleteCopyTaskByPolicy use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
    }


    @Override
    public List<CopyTask> lstTaskByErrorCode(int errCode)
    {
        List<CopyTask> lstCopyTasks = null;
        Connection connection = null;
        PreparedStatement ps = null;
        try
        {
            connection = getConnection(SQL_DBNAME);
            ps = connection.prepareStatement(SQL_LSTTASKBYERRORCODE);
            ps.setInt(1, MirrorCommonStatic.TASK_STATE_FAILED);
            ps.setInt(2, errCode);
            lstCopyTasks = getCopyTasks(ps);
        }
        catch (RuntimeException e)
        {
            LOGGER.error("lstTaskByErrorCode use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("lstTaskByErrorCode use slavedb error" + e.getMessage(), e);
            throw new SlaveDBSQLException(e);
        }
        finally
        {
            closeConnect(connection);
            closeStatement(ps);
        }
        return lstCopyTasks;
    }
    
}

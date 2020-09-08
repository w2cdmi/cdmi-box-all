package com.huawei.sharedrive.isystem.monitor.thrift;

import java.security.InvalidParameterException;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.monitor.dao.NodeDao;
import com.huawei.sharedrive.isystem.monitor.dao.ProcessInfoDao;
import com.huawei.sharedrive.isystem.monitor.dao.ProcessInfoHistoryDao;
import com.huawei.sharedrive.isystem.monitor.domain.NodeRunningInfo;
import com.huawei.sharedrive.isystem.monitor.domain.ProcessInfo;
import com.huawei.sharedrive.isystem.util.ProcessValidateUtil;
import com.huawei.sharedrive.thrift.process.ProcessRunningInfo;
import com.huawei.sharedrive.thrift.process.ProcessRunningThriftService;
import com.huawei.sharedrive.thrift.process.TBusinessException;

public class ProcessServiceImpl implements ProcessRunningThriftService.Iface
{
    private static Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);
    
    @Autowired
    private ProcessInfoDao processInfoDao;
    
    @Autowired
    private ProcessInfoHistoryDao processInfoHistoryDao;
    
    @Autowired
    private NodeDao nodeDao;
    
    /**
     * 1:异常
     * 
     */
    private static final int STATUS_ABNORMAL = 1;
    
    /**
     * :0：正常
     * 
     */
    private static final int STATUS_NORMAL = 0;
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void reportProcessRunningInfo(ProcessRunningInfo arg0) throws TBusinessException, TException
    {
        writeProcessToDB(arg0);
        
        updateNodeStatus(arg0);
    }
    
    /**
     * 根据进程状态更新节点状态
     * 
     * @param arg0
     */
    private void updateNodeStatus(ProcessRunningInfo arg0)
    {
        NodeRunningInfo tmp = nodeDao.getOneNode(arg0.hostName);
        if (tmp != null)
        {
            if (tmp.getStatus() == STATUS_ABNORMAL || arg0.status == STATUS_ABNORMAL)
            {
                tmp.setStatus(STATUS_ABNORMAL);// 进程状态：0，正常；1，异常
            }
            else
            {
                tmp.setStatus(STATUS_NORMAL);// 进程状态：0，正常；1，异常
            }
            nodeDao.update(tmp);
        }
    }
    
    private void writeProcessToDB(ProcessRunningInfo arg0)
    {
        ProcessInfo processInfo = new ProcessInfo(arg0);
        
        try
        {
            if (ProcessValidateUtil.validate(processInfo))
            {
                ProcessInfo temp = processInfoDao.get(processInfo);
                if (temp == null)
                {
                    processInfoDao.insert(processInfo);
                }
                else
                {
                    processInfoDao.update(processInfo);
                }
                processInfoHistoryDao.insert(processInfo);
                logger.info("writeProcessToDB success");
            }
            else
            {
                throw new InvalidParameterException();
            }
        }
        catch (RuntimeException e)
        {
            logger.error(processInfo.toString(), e);
            throw e;
        }
    }
    
}

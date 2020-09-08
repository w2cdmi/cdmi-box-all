/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.file.engine.realtime.dao.RealTimeCopyTaskPartDAO;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTaskPart;
import pw.cdmi.file.engine.realtime.service.RealTimeCopyTaskPartService;

@Service("realTimeCopyTaskPartService")
public class RealTimeCopyTaskPartServiceImpl implements RealTimeCopyTaskPartService
{

    private static final int DEAULT_BATCH_SIZE = 50;
    
    @Autowired
    private RealTimeCopyTaskPartDAO realTimeCopyTaskPartDAO;
    
    @Override
    public void updateTaskPartsStatus(List<RealTimeCopyTaskPart> taskPart)
    {
        if (CollectionUtils.isEmpty(taskPart))
        {
            return;
        }
        for (RealTimeCopyTaskPart p : taskPart)
        {
            realTimeCopyTaskPartDAO.updateTaskPartsStatus(p);
        }       
    }

    @Override
    public List<RealTimeCopyTaskPart> listRealTimeCopyTaskPartByTaskId(String taskId)
    {
        return realTimeCopyTaskPartDAO.listRealTimeCopyTaskPartByTaskId(taskId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchInsertOrReplace(List<RealTimeCopyTaskPart> parts)
    {
        if (CollectionUtils.isEmpty(parts))
        {
            return;
        }
        
        // 每次提交50笔
        List<RealTimeCopyTaskPart> part = new ArrayList<RealTimeCopyTaskPart>(10);
        for (int i = 0; i < parts.size(); i++)
        {
            part.add(parts.get(i));
            if (i > 0 && i % DEAULT_BATCH_SIZE == 0)
            {
                realTimeCopyTaskPartDAO.batchReplace(part);
                part = new ArrayList<RealTimeCopyTaskPart>(10);
            }
        }
        if (CollectionUtils.isNotEmpty(part))
        {
            realTimeCopyTaskPartDAO.batchReplace(part);
        }
        
    }

    @Override
    public boolean hasRealTimeCopyTaskPart(String taskId)
    {
        int count = realTimeCopyTaskPartDAO.countRealTimeCopyTaskPart(taskId);
        return count > 0;
    }

    @Override
    public boolean allPartsSuccess(String taskId)
    {
        int count = realTimeCopyTaskPartDAO.countUnSuccessTaskParts(taskId);
        return count == 0;
    }

    @Override
    public void deleteUnExistTaskPart()
    {
        realTimeCopyTaskPartDAO.deleteUnExistTaskPart();
        
    }
   
}

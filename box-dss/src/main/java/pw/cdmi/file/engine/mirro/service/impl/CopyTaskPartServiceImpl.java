/**
 * 
 */
package pw.cdmi.file.engine.mirro.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.file.engine.mirro.dao.CopyTaskPartDAO;
import pw.cdmi.file.engine.mirro.domain.CopyTaskPart;
import pw.cdmi.file.engine.mirro.service.CopyTaskPartService;

/**
 * @author w00186884
 * 
 */
@Service
public class CopyTaskPartServiceImpl implements CopyTaskPartService
{
    
    private static final int DEAULT_BATCH_SIZE = 50;
    
    @Autowired
    private CopyTaskPartDAO copyTaskpartDAO;
    
    @Override
    public boolean hasCopyTaskPart(String taskId)
    {
        int count = copyTaskpartDAO.countCopyTaskPart(taskId);
        return count > 0;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchInsertOrReplace(List<CopyTaskPart> parts)
    {
        if (CollectionUtils.isEmpty(parts))
        {
            return;
        }
        
        // 每次提交50笔
        List<CopyTaskPart> part = new ArrayList<CopyTaskPart>(10);
        for (int i = 0; i < parts.size(); i++)
        {
            part.add(parts.get(i));
            if (i > 0 && i % DEAULT_BATCH_SIZE == 0)
            {
                copyTaskpartDAO.batchReplace(part);
                part = new ArrayList<CopyTaskPart>(10);
            }
        }
        if (CollectionUtils.isNotEmpty(part))
        {
            copyTaskpartDAO.batchReplace(part);
        }
    }
    
    @Override
    public List<CopyTaskPart> listCopyTaskPartByTaskId(String taskId)
    {
        return copyTaskpartDAO.listCopyTaskPartByTaskId(taskId);
    }
    
    @Override
    public boolean allPartsSuccess(String taskId)
    {
        int count = copyTaskpartDAO.countUnSuccessTaskParts(taskId);
        return count == 0;
    }
    
    @Override
    public void updateTaskPartsStatus(List<CopyTaskPart> taskPart)
    {
        if (CollectionUtils.isEmpty(taskPart))
        {
            return;
        }
        for (CopyTaskPart p : taskPart)
        {
            copyTaskpartDAO.updateTaskPartsStatus(p);
        }
    }
    
}

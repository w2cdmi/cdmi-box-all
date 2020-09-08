package pw.cdmi.box.isystem.convertTask.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.isystem.convertTask.dao.TaskBeanDao;
import pw.cdmi.box.isystem.convertTask.domain.QueryCondition;
import pw.cdmi.box.isystem.convertTask.domain.TaskBean;
import pw.cdmi.box.isystem.convertTask.service.ConvertTaskService;


@Service("convertTaskService")
public class ConvertTaskServiceImpl implements ConvertTaskService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertTaskServiceImpl.class);
	
	@Autowired
    private TaskBeanDao taskBeanDao;
	
	@Override
	public Page<TaskBean> getTaskBeanList(QueryCondition condition)
	{
		LOGGER.info("Enter getTaskBeanList,condition={}",condition);
		int total = taskBeanDao.getTotals(condition);
        List<TaskBean> content = taskBeanDao.getTaskBeanList(condition);
        Page<TaskBean> page = new PageImpl<TaskBean>(content, condition.getPageRequest(), total);
        LOGGER.info("End getTaskBeanList");
        return page;
	}
	
	
	@Override
	public int save(String taskId, int level)
	{
		LOGGER.info("Enter save,taskId={},level={}",taskId,level);
		int result = taskBeanDao.save(taskId, level);
		LOGGER.info("End save,result={}",result);
		return result;
	}
	
	public void resetState(String taskId)
	{
		LOGGER.info("Enter resetState,taskId={}",taskId);
		taskBeanDao.resetState(taskId);
		LOGGER.info("End resetState");
	}
}

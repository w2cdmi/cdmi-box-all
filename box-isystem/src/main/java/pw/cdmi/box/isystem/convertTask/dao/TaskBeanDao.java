package pw.cdmi.box.isystem.convertTask.dao;

import java.util.List;

import pw.cdmi.box.isystem.convertTask.domain.QueryCondition;
import pw.cdmi.box.isystem.convertTask.domain.TaskBean;

public interface TaskBeanDao
{
	List<TaskBean> getTaskBeanList(QueryCondition condition);
	
	int getTotals(QueryCondition condition);
	
	int save(String taskId,int level);
	
	void resetState(String taskId);
}

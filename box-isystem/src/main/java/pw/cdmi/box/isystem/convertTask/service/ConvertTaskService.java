package pw.cdmi.box.isystem.convertTask.service;


import pw.cdmi.box.domain.Page;
import pw.cdmi.box.isystem.convertTask.domain.QueryCondition;
import pw.cdmi.box.isystem.convertTask.domain.TaskBean;

public interface ConvertTaskService
{
	Page<TaskBean> getTaskBeanList(QueryCondition condition);
	
	int save(String taskId, int level);
	
	void resetState(String taskId);
}

package pw.cdmi.box.isystem.convertTask.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.isystem.convertTask.dao.TaskBeanDao;
import pw.cdmi.box.isystem.convertTask.domain.QueryCondition;
import pw.cdmi.box.isystem.convertTask.domain.TaskBean;

@SuppressWarnings("deprecation")
@Service("taskBeanDao")
public class TaskBeanDaoImpl extends AbstractDAOImpl implements TaskBeanDao
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskBeanDaoImpl.class);
	/**
	 * 查询转换任务列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TaskBean> getTaskBeanList(QueryCondition condition)
	{
		Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", condition);
        map.put("order", condition.getPageRequest().getOrder());
        map.put("limit", condition.getPageRequest().getLimit());
		List<TaskBean> list = sqlMapClientTemplate.queryForList("TaskBean.getTaskBeanList", map);
		return list;
	}
	
	@Override
	public int getTotals(QueryCondition condition)
	{
		LOGGER.info("Enter getTotals,condition={}",condition);
		Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", condition);
        return (Integer) sqlMapClientTemplate.queryForObject("TaskBean.getTaskBeanCount", map);
	}
	
	@Override
	public int save(String taskId,int level)
	{
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("taskId", taskId);
		map.put("level", level);
		int status = (Integer)sqlMapClientTemplate.queryForObject("TaskBean.checkStatus",map);
		int result = 1;
		if(status == 9 || status == 99)
		{
			sqlMapClientTemplate.update("TaskBean.updateLevel",map);
			result = 0;
		}
		return result;
	}
	
	@Override
	public void resetState(String taskId)
	{
		sqlMapClientTemplate.update("TaskBean.resetState",taskId);
		
	}
}

package pw.cdmi.box.app.convertservice.dao.impl;

import pw.cdmi.box.app.convertservice.dao.TaskBeanDAO;
import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.convertservice.service.impl.ConvertServiceImpl;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings("deprecation")
@Service("taskBeanDao")
public class TaskBeanDAOImpl extends AbstractDAOImpl implements TaskBeanDAO {
	private static final Logger logger = LoggerFactory.getLogger(TaskBeanDAOImpl.class);
    /**
     * 新增转换任务
     */
    @Override
    public void addTask(TaskBean task) {
        sqlMapClientTemplate.insert("TaskBean.addTask", task);
    }

    /**
     * 更新转换任务
     */
    @Override
    public void updateTask(TaskBean task) {
        sqlMapClientTemplate.update("TaskBean.updateTask", task);
    }

    /**
     * 删除当前任务
     */
    @Override
    public void deleteTask(List<String> taskIds) {
        StringBuffer sb = new StringBuffer();

        for (String s : taskIds) {
            sb.append(s).append("','");
        }

        String taskId = "'" + sb.substring(0, sb.length() - 3) + "'";
        sqlMapClientTemplate.delete("TaskBean.deleteTask", taskId);
    }

    /**
     * 删除转换任务的历史表
     */
    @Override
    public void deleteHistoryTask(List<String> taskIds) {
        StringBuffer sb = new StringBuffer();

        for (String s : taskIds) {
            sb.append(s).append("','");
        }

        String taskId = "'" + sb.substring(0, sb.length() - 3) + "'";
        sqlMapClientTemplate.delete("TaskBean.deleteHistoryTask", taskId);
    }

    /**
     * 获取convertService处理的IP
     */
    @Override
    public String getTaskHandleIp(String resourcegroupid) {
        String csIp = sqlMapClientTemplate.queryForObject("TaskBean.getTaskHandleIp",
                resourcegroupid).toString();

        return csIp;
    }

    /**
     * 获取convertService处理的IP，剔除异常的IP
     */
    @Override
    public String getHandleIp(String ip) {
        String csIp = sqlMapClientTemplate.queryForObject("TaskBean.getHandleIp",
                ip).toString();

        return csIp;
    }

    /**
     * 根据转换任务的convertService处理IP
     */
    public void updateTaskHandleIp(String taskid, String ip) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taskid", taskid);
        map.put("ip", ip);
        sqlMapClientTemplate.update("TaskBean.updateTaskHandleIp", map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskBean> getTaskBeanList(TaskBean condition) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (null != condition.getOrderyBy()) {
            map.put("orderBy", "level DESC");
        } else {
            map.put("orderBy", condition.getOrderyBy());
        }

        if ((null != condition.getCurrPage()) &&
                (null != condition.getPageSize())) {
            int offset = Integer.parseInt(condition.getPageSize()) * (Integer.parseInt(condition.getCurrPage()) -
                1);
            map.put("limit", Integer.parseInt(condition.getPageSize()));
            map.put("offset", offset);
        }

        if (null != condition.getObjectIds()) {
            StringBuffer sb = new StringBuffer();

            for (String s : condition.getObjectIds()) {
                sb.append(s).append("','");
            }

            String objectIds = "'" + sb.substring(0, sb.length() - 3) + "'";
            map.put("objectIds", objectIds);
        }

        if (0 != condition.getTaskSize()) {
            map.put("limit", condition.getTaskSize());
            map.put("offset", 0);
        }

        map.put("filter", condition);

        List<TaskBean> list = sqlMapClientTemplate.queryForList("TaskBean.getTaskBeanList",
                map);

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskBean> getTaskBeanThriftList(TaskBean condition) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (null != condition.getOrderyBy()) {
            map.put("orderBy", "level DESC");
        } else {
            map.put("orderBy", condition.getOrderyBy());
        }

        if ((null != condition.getCurrPage()) &&
                (null != condition.getPageSize())) {
            int offset = Integer.parseInt(condition.getPageSize()) * Integer.parseInt(condition.getCurrPage());
            map.put("limit", Integer.parseInt(condition.getPageSize()));
            map.put("offset", offset);
        }

        if (null != condition.getObjectIds()) {
            StringBuffer sb = new StringBuffer();

            for (String s : condition.getObjectIds()) {
                sb.append(s).append("','");
            }

            String objectIds = "'" + sb.substring(0, sb.length() - 3) + "'";
            map.put("objectIds", objectIds);
        }

        if (0 != condition.getTaskSize()) {
            map.put("limit", condition.getTaskSize());
            map.put("offset", 0);
        }

        map.put("filter", condition);
        //logger.info("------condition1-----: " + map.get("filter"));
        map.remove("filter");
        //logger.info("------condition2-----: " + map);
        map.put("filter", condition);
        List<TaskBean> list = sqlMapClientTemplate.queryForList("TaskBean.getTaskBeanThriftList",
                map);

        return list;
    }

    @Override
    public TaskBean getTaskBean(Map<String, Object> map) {
        TaskBean taskBean = (TaskBean) sqlMapClientTemplate.queryForObject("TaskBean.getTaskBean",
                map);

        return taskBean;
    }

    @Override
    public TaskBean getTaskInfo(Map<String, Object> map) {
        TaskBean taskBean = (TaskBean) sqlMapClientTemplate.queryForObject("TaskBean.getTaskInfo",
                map);

        return taskBean;
    }

    @Override
    public int checkTaskId(Map<String, Object> map) {
        int count = Integer.valueOf(sqlMapClientTemplate.queryForObject(
                    "TaskBean.checkTaskId", map).toString());

        return count;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskBean> getHistoryTaskBeanList(TaskBean condition) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (null != condition.getOrderyBy()) {
            map.put("orderBy", condition.getOrderyBy());
        }

        if ((null != condition.getCurrPage()) &&
                (null != condition.getPageSize())) {
            int offset = Integer.parseInt(condition.getPageSize()) * (Integer.parseInt(condition.getCurrPage()) -
                1);
            map.put("limit", Integer.parseInt(condition.getPageSize()));
            map.put("offset", offset);
        }

        if (null != condition.getObjectIds()) {
            StringBuffer sb = new StringBuffer();

            for (String s : condition.getObjectIds()) {
                sb.append(s).append("','");
            }

            String objectIds = "'" + sb.substring(0, sb.length() - 3) + "'";
            map.put("objectIds", objectIds);
        }

        map.put("filter", condition);

        List<TaskBean> list = sqlMapClientTemplate.queryForList("TaskBean.getHistoryTaskBeanList",
                map);

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskBean> getTasks(String ip) {
        List<TaskBean> list = sqlMapClientTemplate.queryForList("TaskBean.getTasks",
                ip);

        return list;
    }

    @Override
    public void updateTaskStatus(List<String> taskIds) {
        StringBuffer sb = new StringBuffer();

        for (String s : taskIds) {
            sb.append(s).append("','");
        }

        String taskId = "'" + sb.substring(0, sb.length() - 3) + "'";
        sqlMapClientTemplate.update("TaskBean.updateTaskStatus", taskId);
    }

    @Override
    public void updateConvertEndTime(TaskBean task) {
        sqlMapClientTemplate.update("TaskBean.updateConvertEndTime", task);
    }

    @Override
    public void updateConvertBeginTime(TaskBean task) {
        sqlMapClientTemplate.update("TaskBean.updateConvertBeginTime", task);
    }

    @Override
    public void insertNodeSign(Map<String, Object> map) {
        sqlMapClientTemplate.insert("TaskBean.insertNodeSign", map);
    }

    @Override
    public void deleteNodeSign(String resourceGroupId) {
        sqlMapClientTemplate.delete("TaskBean.deleteNodeSign", resourceGroupId);
    }

    @Override
    public String getNodeSign(String resourceGroupId) {
        Object ip = sqlMapClientTemplate.queryForObject("TaskBean.getNodeSign",
                resourceGroupId);

        if (null != ip) {
            return ip.toString();
        }

        return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskBean> getRenewTasks(Map<String, Object> map) {
        List<TaskBean> list = sqlMapClientTemplate.queryForList("TaskBean.getRenewTasks",
                map);

        return list;
    }

    @Override
    public int getAssignIpTaskCount(String ip) {
        int count = Integer.valueOf(sqlMapClientTemplate.queryForObject(
                    "TaskBean.getAssignIpTaskCount", ip).toString());

        return count;
    }
}

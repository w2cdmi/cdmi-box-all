package pw.cdmi.box.app.convertservice.dao;

import java.util.List;
import java.util.Map;

import pw.cdmi.box.app.convertservice.domain.TaskBean;

public interface TaskBeanDAO {
    void addTask(TaskBean task);

    TaskBean getTaskBean(Map<String, Object> map);

    List<TaskBean> getTaskBeanList(TaskBean condition);

    int checkTaskId(Map<String, Object> map);

    void updateTask(TaskBean task);

    void deleteTask(List<String> taskIds);

    List<TaskBean> getHistoryTaskBeanList(TaskBean condition);

    void deleteHistoryTask(List<String> taskIds);

    String getTaskHandleIp(String resourcegroupid);

    List<TaskBean> getTasks(String ip);

    String getHandleIp(String ip);

    void updateTaskHandleIp(String taskid, String ip);

    List<TaskBean> getTaskBeanThriftList(TaskBean condition);

    void updateTaskStatus(List<String> taskIds);

    TaskBean getTaskInfo(Map<String, Object> map);

    void updateConvertEndTime(TaskBean task);

    void updateConvertBeginTime(TaskBean task);

    void insertNodeSign(Map<String, Object> map);

    void deleteNodeSign(String resourceGroupId);

    String getNodeSign(String resourceGroupId);

    List<TaskBean> getRenewTasks(Map<String, Object> map);

    int getAssignIpTaskCount(String ip);
}

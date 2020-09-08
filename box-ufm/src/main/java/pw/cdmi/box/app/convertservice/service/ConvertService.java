package pw.cdmi.box.app.convertservice.service;

import java.util.List;

import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.app.convertservice.domain.NodeRunningInfo;
import pw.cdmi.box.app.convertservice.domain.TaskBean;


public interface ConvertService {
    String addTask(TaskBean task);

    TaskBean getTaskBean(String objectId);

    List<TaskBean> getTaskBeanList(TaskBean condition);

    String addImage(ImgObject o);

    ImgObject getImage(String sourceObjectId);

    String updateTask(TaskBean task);

    String deleteTask(List<String> taskIds);

    List<TaskBean> getHistoryTaskBeanList(TaskBean condition);

    String deleteHistoryTask(List<String> taskIds);

    void deleteImageObject(String sourceObjectId);

    void cleanTaskIp(NodeRunningInfo nodeRunningInfo);

    List<TaskBean> getTaskBeanThriftList(TaskBean condition);

    void updateTaskStatus(List<String> taskIds);

    TaskBean getTaskInfo(String objectId);

    void renewTask(NodeRunningInfo nodeRunningInfo);
    
    void resetNodeRunningStatus();
}

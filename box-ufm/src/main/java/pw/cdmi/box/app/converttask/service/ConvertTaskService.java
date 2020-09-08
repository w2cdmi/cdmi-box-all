package pw.cdmi.box.app.converttask.service;

import com.huawei.sharedrive.app.oauth2.domain.UserToken;

import pw.cdmi.box.app.converttask.domain.ConvertInfo;


public interface ConvertTaskService {
    public ConvertInfo getDoingConvertTask(UserToken user, long ownerId,
        String spaceType, String curpage, String size);

    public ConvertInfo getDoneConvertTask(UserToken user, long ownerId,
        String spaceType, String curpage, String size);

    public void deleteDoneConvertTask(UserToken user, String[] taskids,
        String spaceType, long ownerId);

    public void retryDoing(UserToken user, String taskId, String spaceType,
        long ownerId);
}

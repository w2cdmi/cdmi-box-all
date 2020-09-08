package com.huawei.sharedrive.app.mirror.datamigration.manager;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchReginException;
import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;
import com.huawei.sharedrive.app.mirror.datamigration.exception.NoSuchMigrationTaskException;
import com.huawei.sharedrive.app.mirror.datamigration.exception.TaskConflictException;
import com.huawei.sharedrive.app.mirror.datamigration.exception.TaskTooManyException;
import com.huawei.sharedrive.app.mirror.datamigration.service.UserDataMigrationTaskService;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.utils.DateUtils;

/**
 * 数据迁移任务管理 包含创建任务 删除任务 获取任务 查看任务状态
 * 
 * @author c00287749
 * 
 */
@Component
public class UserDataMigrationTaskManager
{
    private static final int DEFAULT_MAX_TASK_TOTAL = 100;
    
    private static final int DEFAULT_TASK_RETAIN_DAYS = 30;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataMigrationTaskManager.class);
    
    private static final String USER_DATAMIGRATION_TASK_MAX = "user.data.migration.task.max";
    
    private static final String USER_DATAMIGRATION_TASK_RETAIN_DAYS = "user.data.migration.task.retain.days";
    
    
    //扫描超时认为是20分钟
    private static final int DEFAULT_USER_MIGRATION_TASK_SCAN_TIME_OUT_SECOND = 1200;
    
    private static final String USER_MIGRATION_TASK_SCAN_TIME_OUT_SECOND = "user.migration.task.scan.timeout.second";
    
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private UserDataMigrationTaskService userDataMigrationTaskService;
    
    /**
     * 数据迁移任务的权限操作
     * 
     * @param userToken
     * @param user
     */
    private void checkACL(UserToken userToken, User user)
    {
        String authorization = userToken.getAuth();
        
        if (authorization.startsWith(UserTokenHelper.APP_PREFIX)
            || authorization.startsWith(UserTokenHelper.APP_ACCOUNT_PREFIX))
        {
            // 判断操作的用户是不是该User的account账户
            if (user.getAccountId().longValue() == userToken.getId())
            {
                LOGGER.info("account or app data migration oper");
                return;
            }
        }
        else if (userToken.getId() == user.getId())
        {
            // 自己操作自己的空间
            LOGGER.info("user personal data migration oper");
            return;
        }
        else if (user.getType() == User.USER_TYPE_TEAMSPACE)
        {
            // 操作者是否是否是团队空间的owner
            TeamSpace teamSpace = teamSpaceService.getTeamSpaceNoCheck(user.getId());
            if (null != teamSpace && teamSpace.getOwnerBy() == userToken.getId())
            {
                // 拥有者迁移自己的数据
                LOGGER.info("teamspace data migration oper");
                return;
            }
            throw new ForbiddenException(userToken.getId() + "no permission to operate " + user.getId()
                + " space.");
        }
        else
        {
            throw new ForbiddenException(userToken.getId() + "no permission to operate " + user.getId()
                + " space.");
        }
    }
    
    /**
     * 完成数据迁移任务
     * 
     * @param task
     */
    public String checkDataMigrationTask(UserDataMigrationTask task)
    {
        String msg;
        // 任务完成
        if (task.getCurFiles() == task.getTotalFiles() && task.getCurSizes() == task.getTotalSizes())
        {
            task.setStatus(UserDataMigrationTask.COMPELETE_STATUS);
            task.setModifiedAt(new Date());
            userDataMigrationTaskService.updateStatus(task);
            
            msg = "The user " + task.getCloudUserId() + " data migration complete ,file's number:"
                + task.getCurFiles() + ",size:" + task.getCurSizes();
        }
        else
        {
            if (0 == copyTaskService.getNotCompleteTaskForDataMigration(task.getCloudUserId()))
            {
                // 出大问题了，数据迁移数据不一致,出现业务逻辑技术错误
                task.setStatus(UserDataMigrationTask.FAILED_STATUS);
                task.setModifiedAt(new Date());
                userDataMigrationTaskService.updateStatus(task);
                msg = "The user " + task.getCloudUserId() + " data migration complete,file's number:"
                    + task.getCurFiles() + ",but  current size:" + task.getCurSizes() + "!= total size:"
                    + task.getTotalSizes();
                LOGGER.error(msg);
                
            }
            else
            {
                msg = "The user " + task.getCloudUserId()
                    + " data migration executing,had complete file's number:" + task.getCurFiles()
                    + "and need complete file's number:" + task.getTotalSizes();
            }
            
        }
        
        return msg;
    }
    
    /**
     * 做任务清理
     * 
     * @param record
     * @param migrationTask
     */
    public String cleanCompleteDataMigrationTask(UserDataMigrationTask migrationTask)
    {
        String msg = null;
        if (UserDataMigrationTask.COMPELETE_STATUS != migrationTask.getStatus()
            && UserDataMigrationTask.FAILED_STATUS != migrationTask.getStatus())
        {
            msg = "The user " + migrationTask.getCloudUserId() + " data migration executing";
            return msg;
        }
        
        int retainDays = DEFAULT_TASK_RETAIN_DAYS;
        SystemConfig config = systemConfigService.getConfig(USER_DATAMIGRATION_TASK_RETAIN_DAYS);
        
        if (null != config)
        {
            retainDays = Integer.parseInt(config.getValue());
        }
        // 清理三个月前的任务
        if (DateUtils.getDateAfter(migrationTask.getModifiedAt(), retainDays).getTime() <  new Date().getTime())
        {
            userDataMigrationTaskService.delete(migrationTask);
            msg = "The user " + migrationTask.getCloudUserId()
                + " data migration had compelted ,clean the task record,finish time :"
                + migrationTask.getModifiedAt().toString();
        }
        else
        {
            msg = "The user " + migrationTask.getCloudUserId()
                + " data migration had compelted ,finish time :" + migrationTask.getModifiedAt().toString();
        }
        
        return msg;
        
    }
    
    public UserDataMigrationTask createDataMigrationTask(UserToken userToken, User user, int regionId)
    {
        checkACL(userToken, user);
        
        // 检查异步复制是否开启，没有开启则不允许数据迁移
        if (!copyConfigLocalCache.isSystemMirrorEnable())
        {
            LOGGER.info("mirror_global_enable is false");
            throw new ForbiddenException("The system don't enable mirror feature");
        }
        
        if (!copyConfigLocalCache.isSystemMirrorTimerEnable())
        {
            LOGGER.info("mirror_global_enable_timer is false");
            throw new ForbiddenException("The system don't enable mirror feature");
        } 
        
        checkMirgationTaskTooMany();
        
        // 检测相同的User在TASK是否存在相同的任务
        UserDataMigrationTask task = userDataMigrationTaskService.getByUserId(user.getId());
        if (null != task)
        {
            LOGGER.warn("The user data migration task is existing ,create time:" + task.getCreatedAt());
            throw new TaskConflictException("The cloudUserId:" + user.getId());
        }
        
        //检查目标区域是否存在可用的资源组
        ResourceGroup group = checkRegionResouceGroup(regionId);
        
        // 创建任务
        return userDataMigrationTaskService.createTask(user.getId(), regionId, group.getId());
        
    }

    /**
     * 
     * @param regionId
     * @return
     */
    private ResourceGroup checkRegionResouceGroup(int regionId)
    {
        List<ResourceGroup> groups = resourceGroupService.listGroupsByRegion(regionId);
        ResourceGroup group = null;
        for (ResourceGroup tmp : groups)
        {
            if (ResourceGroup.Status.Enable == (ResourceGroup.Status) tmp.getStatus())
            {
                group = tmp;
                break;
            }
        }
        
        // 是否存在可用的group
        if (null == group)
        {
            LOGGER.warn("The region not available ResourceGroup");
            throw new NoSuchReginException();
        }
        return group;
    }

    /**
     * 检查用户数据迁移任务是否太多
     */
    private void checkMirgationTaskTooMany()
    {
        int maxTotal = DEFAULT_MAX_TASK_TOTAL;
        
        SystemConfig config = systemConfigService.getConfig(USER_DATAMIGRATION_TASK_MAX);
        
        if (null != config)
        {
            maxTotal = Integer.parseInt(config.getValue());
        }
        
        int currentTask = userDataMigrationTaskService.getNotCompletedTaskTotal();
        
        // 校验当前任务书是否超标
        if (currentTask >= maxTotal)
        {
            String msg = "The System only support task numbers:" + maxTotal + ",current task numbers:"
                + currentTask;
            LOGGER.error(msg);
            
            throw new TaskTooManyException(msg);
        }
        String msg = "The System  support task numbers:" + maxTotal + ",current task numbers:"
            + currentTask;
        LOGGER.info(msg);
    }
    
    public void deleteDataMigrationTask(UserToken userToken, User user)
    {
        checkACL(userToken, user);
        
        UserDataMigrationTask task = userDataMigrationTaskService.getByUserId(user.getId());
        if (null == task)
        {
            String msg = "The user data migration task  not is existing ,cloudUserId:" + user.getId();
            LOGGER.error(msg);
            throw new NoSuchMigrationTaskException(msg);
        }
        
        if (task.getStatus() != UserDataMigrationTask.FAILED_STATUS
            && task.getStatus() != UserDataMigrationTask.COMPELETE_STATUS)
        {
            // 对未执行的任务需要做清理
            copyTaskService.clearNotExeTaskForDataMigration(user.getId());
            
        }
        
        userDataMigrationTaskService.delete(task);
    }
    
    public UserDataMigrationTask getDataMigrationTask(UserToken userToken, User user)
    {
        checkACL(userToken, user);
        UserDataMigrationTask task = userDataMigrationTaskService.getByUserId(user.getId());
        if (null == task)
        {
            String msg = "The user data migration task  not is existing ,cloudUserId:" + user.getId();
            LOGGER.error(msg);
            throw new NoSuchMigrationTaskException(msg);
        }
        return task;
    }
    
    /**
     * 更新用户进度
     * @param userId
     * @param size
     */
    public void updateDataMigrationProcess(long userId, long size)
    {
        UserDataMigrationTask task = userDataMigrationTaskService.getByUserId(userId);
        if(null == task)
        {
            String msg = "while updateDataMigrationProcess happene unexpected thing,the user " + userId+" data migration task  not is existing ";
            LOGGER.info(msg);
            return;
        }
        
        task = userDataMigrationTaskService.updateProgressForEveryFile(userId, size);
        
        // 误认为
        if (null != task )
        {
            LOGGER.info(task.toTaskStr());
            if(task.getCurFiles() == task.getTotalFiles())
            {
                // 数量正确，同时
                checkDataMigrationTask(task);
            }
           
        }
    }
    
    /**
     * 检查扫描任务是否超时
     * @param task
     * @return
     */
    public String checkTaskScanTimeOut(UserDataMigrationTask task)
    {
        String msg = null;
        if(UserDataMigrationTask.EXECUTE_SCAN_STATUS != task.getStatus())
        {
            msg = "The user " +task.getCloudUserId() +" data migration task status:"+task.getStatus();
            return msg;
        }
        
        int timeout = DEFAULT_USER_MIGRATION_TASK_SCAN_TIME_OUT_SECOND;
        
        SystemConfig config = systemConfigService.getConfig(USER_MIGRATION_TASK_SCAN_TIME_OUT_SECOND);
        if (null != config)
        {
            timeout = Integer.parseInt(config.getValue());
        }
        
        //检查任务执行是否超时，超时则重置，任务状态，避免出现任务私锁
        if(DateUtils.getDateAfterSeconds(task.getModifiedAt(),timeout).getTime() < new Date().getTime())
        {
            task.setStatus(UserDataMigrationTask.INIT_STATUS);
            task.setExeAgent(null);
            task.setModifiedAt(new Date());
            userDataMigrationTaskService.update(task);
            msg = "The user " +task.getCloudUserId() +" data migration task scan time out,scan start time:"+task.getModifiedAt();
        }
        
        return msg;
    }
    
}

/**
 * 
 */
package com.huawei.sharedrive.app.files.service.job;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.files.service.AsyncJobService;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.files.service.NodeService;
import com.huawei.sharedrive.app.files.service.RecentBrowseService;
import com.huawei.sharedrive.app.files.service.TrashServiceV2;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.task.ConflictNode;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.ResponseGetTask;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.share.service.INodeLinkApproveService;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingClearTrashService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * @author l90003768
 * 
 */
@Component("asyncJobService")
public class AsyncJobServiceImpl implements AsyncJobService
{
    
    public static final String ASYNC_START_STATUS = "Starting";
    
    /** 异步任务根路径 */
    public static final String ASYNC_TASK_PATH = "/webAsyncTask";
    
    public static final String FORBID_EXCEPTION = "forbidden";
    
    public static final String SRC_NOT_FOUND = "src_not_found";
    
    public static final String STATUS_ASYNC_CONFLICT = "AsyncNodesConflict";
    
    public static final String STATUS_CONFLICT_SAMENAME = "SameNameConflict";
    
    public static final String STATUS_CONFLICT_SUBFOLDER = "SubFolderConflict";
    
    public static final String STATUS_DOING = "Doing";
    
    public static final String STATUS_NO_SOURCE = "NoSuchSource";
    
    public static final String STATUS_OTHER = "other";
    
    public static final String STATUS_SYSTEM_EXCEPTION = "SystemException";
    
    public static final String SYS_EXCEPTION = "sys_exception";
    
    private static Logger logger = LoggerFactory.getLogger(AsyncJobServiceImpl.class);
    
    public final static String STATUS_NOT_FOUND = "NotFound";
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private FolderService folderService;
    
    @Autowired
    private NodeService nodeService;
    
    @Autowired
    private IFileLabelService filelabelService;
    
    private ExecutorService taskPool = Executors.newCachedThreadPool();
    
    @Autowired
    private TrashServiceV2 trashServiceV2;
    
    private CuratorFramework zkClient;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Autowired
    private RecordingClearTrashService recordingClearTrashService;
    
    @Autowired
    private RecentBrowseService recentBrowseService;
    
    @Autowired
    private INodeLinkApproveService linkApproveService;
    @Autowired
    FolderServiceV2 folderServiceV2;
    
    @Override
    public String asyncCleanTrash(UserToken user, RequestAddAsyncTask taskRequest)
    {
        String taskId = user.getId() + '-' + UUID.randomUUID().toString();
        final String taskPath = ASYNC_TASK_PATH + '/' + taskId;
        zkClient = zookeeperServer.getClient();
        try
        {
            zkClient.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(taskPath, "Starting".getBytes(Charset.defaultCharset()));
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
        taskPool.execute(new AsynTrashCleanJob(user, taskPath, trashServiceV2, recordingClearTrashService,
            zkClient, taskRequest.getSrcOwnerId()));
        String[] logMsgs = new String[]{String.valueOf(taskId), String.valueOf(taskRequest.getSrcOwnerId())};
        String keyword = String.valueOf(taskRequest.getSrcOwnerId());
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.CLEAN_TRASH_ASYNC,
            logMsgs,
            keyword);
        
        return taskId;
    }
    
    @Override
    public String asyncCopy(UserToken user, RequestAddAsyncTask taskRequest, boolean valiLinkAccessCode)
    
    {
        String taskId = user.getId() + '-' + UUID.randomUUID().toString();
        final String taskPath = ASYNC_TASK_PATH + '/' + taskId;
        try
        {
            zkClient.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(taskPath, ASYNC_START_STATUS.getBytes(Charset.defaultCharset()));
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
        taskPool.execute(new AsyncCopyJob(taskRequest, user, taskPath, zkClient, folderService,
            valiLinkAccessCode, filelabelService));
        
        if (taskRequest.getSrcNodeList() != null)
        {
            StringBuffer keyword = new StringBuffer();
            int size = taskRequest.getSrcNodeList().size();
            String[] logMsgs = new String[]{String.valueOf(taskId),
                String.valueOf(taskRequest.getSrcOwnerId()), String.valueOf(size),
                String.valueOf(taskRequest.getDestOwnerId()), String.valueOf(taskRequest.getDestFolderId())};
            for (int i = 0; i < size; i++)
            {
                if (i != 0)
                {
                    keyword.append(", ");
                }
                keyword.append(taskRequest.getSrcNodeList().get(i).getSrcNodeId());
            }
            fileBaseService.sendINodeEvent(user,
                EventType.OTHERS,
                null,
                null,
                UserLogType.COPY_ASYNC,
                logMsgs,
                keyword.toString());
        }
        return taskId;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.app.files.service.AsyncJobService#asyncDelete(com.huawei.
     * sharedrive.app.oauth2.domain.UserToken, long, java.lang.String)
     */
    @Override
    public String asyncDelete(final UserToken user, RequestAddAsyncTask taskRequest)
    {
        String taskId = user.getId() + '-' + UUID.randomUUID().toString();
        final String taskPath = ASYNC_TASK_PATH + '/' + taskId;
        try
        {
            zkClient.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(taskPath, ASYNC_START_STATUS.getBytes(Charset.defaultCharset()));
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
        taskPool.execute(new AsyncDeleteJob(taskRequest, user, taskPath, zkClient, nodeService,
            fileBaseService,recentBrowseService,linkApproveService,folderServiceV2));
        if (taskRequest.getSrcNodeList() != null)
        {
            String[] logMsgs = null;
            StringBuffer keyword = new StringBuffer();
            for (int i = 0; i < taskRequest.getSrcNodeList().size(); i++)
            {
                logMsgs = new String[]{String.valueOf(taskId), String.valueOf(taskRequest.getSrcOwnerId()),
                    String.valueOf(taskRequest.getSrcNodeList().get(i).getSrcNodeId())};
                if (i != 0)
                {
                    keyword.append(", ");
                }
                keyword.append(taskRequest.getSrcNodeList().get(i).getSrcNodeId());
            }
            fileBaseService.sendINodeEvent(user,
                EventType.OTHERS,
                null,
                null,
                UserLogType.DELETE_ASYNC,
                logMsgs,
                keyword.toString());
        }
        return taskId;
    }
    
    @Override
    public String asyncMove(UserToken user, RequestAddAsyncTask taskRequest)
    {
        String taskId = user.getId() + '-' + UUID.randomUUID().toString();
        final String taskPath = ASYNC_TASK_PATH + '/' + taskId;
        try
        {
            zkClient.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(taskPath, ASYNC_START_STATUS.getBytes(Charset.defaultCharset()));
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
        taskPool.execute(new AsyncMoveJob(taskRequest, user, taskPath, zkClient, folderService));
        if (taskRequest.getSrcNodeList() != null)
        {
            StringBuffer keyword = new StringBuffer();
            int size = taskRequest.getSrcNodeList().size();
            String[] logMsgs = new String[]{String.valueOf(taskId),
                String.valueOf(taskRequest.getSrcOwnerId()), String.valueOf(size),
                String.valueOf(taskRequest.getDestOwnerId()), String.valueOf(taskRequest.getDestFolderId())};
            for (int i = 0; i < size; i++)
            {
                if (i != 0)
                {
                    keyword.append(", ");
                }
                keyword.append(taskRequest.getSrcNodeList().get(i).getSrcNodeId());
            }
            fileBaseService.sendINodeEvent(user,
                EventType.OTHERS,
                null,
                null,
                UserLogType.MOVE_ASYNC,
                logMsgs,
                keyword.toString());
        }
        return taskId;
    }
    
    @Override
    public String asyncRestoreTrash(UserToken user, RequestAddAsyncTask taskRequest)
    {
        String taskId = user.getId() + '-' + UUID.randomUUID().toString();
        String taskPath = ASYNC_TASK_PATH + '/' + taskId;
        zkClient = zookeeperServer.getClient();
        try
        {
            zkClient.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(taskPath, "Starting".getBytes(Charset.defaultCharset()));
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
        taskPool.execute(new AsyncTrashRestoreJob(user, taskPath, taskRequest.getSrcOwnerId(), zkClient,
            folderService, trashServiceV2, taskRequest.getDestFolderId()));
        String[] logMsgs = new String[]{String.valueOf(taskId), String.valueOf(taskRequest.getSrcOwnerId())};
        String keyword = String.valueOf(taskRequest.getSrcOwnerId());
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.RESTORE_TRASH_ASYNC,
            logMsgs,
            keyword);
        
        return taskId;
    }
    
    @Override
    public ResponseGetTask getTaskStatus(UserToken user, String taskId) throws InternalServerErrorException
    {
        ResponseGetTask result = new ResponseGetTask();
        result.setTaskId(taskId);
        String path = ASYNC_TASK_PATH + '/' + taskId;
        Stat stat = findTaskData(path);
        if (stat == null)
        {
            result.setStatus(STATUS_NOT_FOUND);
            return result;
        }
        
        byte[] byteData = null;
        try
        {
            byteData = zkClient.getData().forPath(path);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
        parseAsyncTaskResult(result, path, byteData);
        return result;
    }
    
    @PostConstruct
    public void init()
    {
        try
        {
            zkClient = zookeeperServer.getClient();
            Stat stat = zkClient.checkExists().forPath(ASYNC_TASK_PATH);
            if (stat == null)
            {
                zkClient.create().forPath(ASYNC_TASK_PATH);
            }
        }
        catch (Exception e)
        {
            logger.error("init asyncTaskPath fail!", e);
        }
    }
    
    /**
     * 删除异步任务
     * 
     * @param path
     * @throws Exception
     */
    private void deleteZkJob(String path)
    {
        try
        {
            zkClient.delete().forPath(path);
        }
        catch (Exception e)
        {
            logger.warn("Fail to delete zkjob for " + path);
        }
    }
    
    /**
     * 封装发生冲突的节点数据 复制粘贴时的重名冲突 还原时的找不到父文件夹冲突
     * 
     * @param result
     * @param data
     */
    private void fillAsyncNodeConflict(ResponseGetTask result, String data)
    {
        String[] conflictIds = data.split(",");
        List<ConflictNode> conflictList = new LinkedList<ConflictNode>();
        for (String idStr : conflictIds)
        {
            fillConflictList(conflictList, idStr);
        }
        if (conflictList.isEmpty())
        {
            result.setStatus(STATUS_SYSTEM_EXCEPTION);
        }
        else
        {
            result.setConflictNodes(conflictList);
            result.setStatus(STATUS_ASYNC_CONFLICT);
        }
    }
    
    private void fillConflictList(List<ConflictNode> conflictList, String idStr)
    {
        try
        {
            ConflictNode conflictNode = new ConflictNode();
            conflictNode.setId(Long.parseLong(idStr));
            conflictList.add(conflictNode);
        }
        catch (NumberFormatException e)
        {
            logger.warn("parseAsyncTaskResult encounter unexpected String " + idStr);
        }
    }
    
    /**
     * 查找任务执行结果数据
     * 
     * @param path
     * @return
     * @throws InternalServerErrorException
     */
    private Stat findTaskData(String path) throws InternalServerErrorException
    {
        try
        {
            return zkClient.checkExists().forPath(path);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
    }
    
    /**
     * 解析异步任务执行结果
     * 
     * @param result
     * @param path
     * @param byteData
     */
    private void parseAsyncTaskResult(ResponseGetTask result, String path, byte[] byteData)
    {
        String data = new String(byteData, Charset.defaultCharset());
        if (StringUtils.isEmpty(data))
        {
            result.setStatus(STATUS_NOT_FOUND);
            return;
        }
        if (ASYNC_START_STATUS.equals(data))
        {
            result.setStatus(STATUS_DOING);
            return;
        }
        deleteZkJob(path);
        if (data.indexOf(AsyncTaskStatus.NO_SUCH_DEST) != -1)
        {
            result.setStatus(AsyncTaskStatus.NO_SUCH_DEST);
            return;
        }
        if (data.indexOf(AsyncTaskStatus.NO_SUCH_SOURCE) != -1)
        {
            result.setStatus(AsyncTaskStatus.NO_SUCH_SOURCE);
            return;
        }
        if (data.indexOf(SRC_NOT_FOUND) != -1)
        {
            result.setStatus(STATUS_NO_SOURCE);
            return;
        }
        if (data.indexOf(AsyncTaskStatus.FORBBIDEN) != -1)
        {
            result.setStatus(AsyncTaskStatus.FORBBIDEN);
            return;
        }
        if (data.indexOf(SYS_EXCEPTION) != -1)
        {
            result.setStatus(STATUS_SYSTEM_EXCEPTION);
            return;
        }
        if (data.indexOf(ErrorCode.SUB_FOLDER_CONFILICT.getCode()) != -1)
        {
            result.setStatus(STATUS_CONFLICT_SUBFOLDER);
            return;
        }
        if (data.indexOf(ErrorCode.SAME_NODE_CONFILICT.getCode()) != -1)
        {
            result.setStatus(ErrorCode.SAME_NODE_CONFILICT.getCode());
            return;
        }
        if (data.indexOf(ErrorCode.SAME_PARENT_CONFILICT.getCode()) != -1)
        {
            result.setStatus(ErrorCode.SAME_PARENT_CONFILICT.getCode());
            return;
        }
        logger.info("AsyncTask other info is " + data);
        fillAsyncNodeConflict(result, data);
        
    }
    
}

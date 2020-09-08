package com.huawei.sharedrive.app.files.service.job;

import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FolderServiceV2;
import com.huawei.sharedrive.app.files.service.NodeService;
import com.huawei.sharedrive.app.files.service.RecentBrowseService;
import com.huawei.sharedrive.app.files.service.lock.Locks;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.RequestNode;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.share.service.INodeLinkApproveService;

public class AsyncDeleteJob implements Runnable
{
    public static final String ASYNC_START_STATUS = "Starting";
    
    /** 异步任务根路径 */
    public static final String ASYNC_TASK_PATH = "/webAsyncTask";
    
    private static Logger logger = LoggerFactory.getLogger(AsyncDeleteJob.class);
    
    private static final String SRC_NOT_FOUND = "src_not_found";
    
    private NodeService nodeService;
    
    private RequestAddAsyncTask requestParam;
    
    private String taskPath;
    
    private UserToken user;
    
    private CuratorFramework zkClient;
    
    private FileBaseService fileBaseService;
    
    private RecentBrowseService recentBrowseService;
    
    private INodeLinkApproveService linkApproveService;
    
    private FolderServiceV2 folderServiceV2;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public AsyncDeleteJob(RequestAddAsyncTask requestParam, UserToken user, String taskPath,
        CuratorFramework zkClient, NodeService nodeService, FileBaseService fileBaseService, 
        RecentBrowseService recentBrowseService, INodeLinkApproveService linkApproveService,FolderServiceV2 folderServiceV2)
    {
        this.requestParam = requestParam;
        this.user = user;
        this.taskPath = taskPath;
        this.zkClient = zkClient;
        this.nodeService = nodeService;
        this.fileBaseService = fileBaseService;
        this.recentBrowseService=recentBrowseService;
        this.linkApproveService=linkApproveService;
        this.folderServiceV2=folderServiceV2;
    }
    
    @Override
    public void run()
    {
        StringBuffer errorIds = new StringBuffer();
        INode node = null;
        for (RequestNode srcNode : requestParam.getSrcNodeList())
        {
            try
            {
                node = fileBaseService.getAndCheckNode(requestParam.getSrcOwnerId(), srcNode.getSrcNodeId(), INode.TYPE_ALL);
                try
                {
                    Locks.DELETE_LOCK.tryLock();
                    nodeService.deleteNode(user, node);
                    folderServiceV2.deleteRecentByNode(node.getOwnedBy(),node.getId());
                    folderServiceV2.deleteShortByNodeId(node.getOwnedBy(),node.getId());
                }
                finally
                {
                    Locks.DELETE_LOCK.unlock();
                }
            }
            catch (NoSuchItemsException e)
            {
                errorIds.append(SRC_NOT_FOUND + ',');
            }
            catch (ForbiddenException e)
            {
                errorIds.append(AsyncTaskStatus.FORBBIDEN).append(',');
            }
            catch (Exception e)
            {
                logger.error("[asyncDeleteLog]", e);
                if (!StringUtils.isEmpty(errorIds.toString()))
                {
                    errorIds.append(',');
                }
                errorIds.append(srcNode.getSrcNodeId());
            }
        }
        
        deleteOrUpdateZK(taskPath, errorIds.toString());
        
    }
    
    private void deleteOrUpdateZK(final String taskPath, String errorIds)
    {
        try
        {
            if (StringUtils.isBlank(errorIds))
            {
                zkClient.delete().forPath(taskPath);
            }
            else
            {
                zkClient.setData().forPath(taskPath, errorIds.getBytes(Charset.defaultCharset()));
            }
        }
        catch (Exception e)
        {
            logger.error("update task status error!", e);
        }
    }
    
}

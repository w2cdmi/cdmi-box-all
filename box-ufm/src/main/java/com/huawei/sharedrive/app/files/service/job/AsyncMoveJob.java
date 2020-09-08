package com.huawei.sharedrive.app.files.service.job;

import java.nio.charset.Charset;
import java.security.InvalidParameterException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.exception.FilesNameConflictException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchDestException;
import com.huawei.sharedrive.app.exception.NoSuchItemsException;
import com.huawei.sharedrive.app.exception.NoSuchSourceException;
import com.huawei.sharedrive.app.exception.SameNodeConflictException;
import com.huawei.sharedrive.app.exception.SameParentConflictException;
import com.huawei.sharedrive.app.exception.SubFolderConflictException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.RequestNode;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class AsyncMoveJob implements Runnable
{
    private static final String SRC_NOT_FOUND = "src_not_found";
    
    private static final String SYS_EXCEPTION = "sys_exception";
    
    public static final String ASYNC_START_STATUS = "Starting";
    
    /** 异步任务根路径 */
    public static final String ASYNC_TASK_PATH = "/webAsyncTask";
    
    private static Logger logger = LoggerFactory.getLogger(AsyncMoveJob.class);
    
    private FolderService folderService;
    
    private RequestAddAsyncTask requestParam;
    
    private String taskPath;
    
    private UserToken user;
    
    private CuratorFramework zkClient;
    
    public AsyncMoveJob(RequestAddAsyncTask requestParam, UserToken user, String taskPath,
        CuratorFramework zkClient, FolderService folderService)
    {
        this.requestParam = requestParam;
        this.user = user;
        this.taskPath = taskPath;
        this.zkClient = zkClient;
        this.folderService = folderService;
    }
    
    @Override
    public void run()
    {
        StringBuilder conflictIds = new StringBuilder();
        INode parentNode = new INode();
        parentNode.setId(requestParam.getDestFolderId());
        parentNode.setOwnedBy(requestParam.getDestOwnerId());
        INode srcNode = null;
        if (CollectionUtils.isEmpty(requestParam.getSrcNodeList()))
        {
            throw new InvalidParameterException("Empty getSrcNodeList");
        }
        for (RequestNode srcReqNode : requestParam.getSrcNodeList())
        {
            srcNode = new INode();
            srcNode.setId(srcReqNode.getSrcNodeId());
            srcNode.setOwnedBy(requestParam.getSrcOwnerId());
            doMoveForOne(user, conflictIds, parentNode, srcNode);
        }
        
        deleteOrUpdateZK(taskPath, conflictIds.toString());
        
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
    
    /**
     * @param user
     * @param conflictIds
     * @param parentNode
     * @param autoRename
     * @param sourceNode
     * @throws BaseRunException
     */
    private void doMove(final UserToken user, StringBuilder conflictIds, INode parentNode, INode sourceNode)
        throws BaseRunException
    {
        String name = sourceNode.getName();
        String newName = name;
        int renameNumber = 1;
        boolean autoRename = requestParam.getAutoRename();
        do
        {
            try
            {
                folderService.moveNodeToFolder(user, sourceNode, parentNode, newName);
                break;
            }
            catch (FilesNameConflictException e)
            {
                if (autoRename)
                {
                    newName = FilesCommonUtils.getNewName(sourceNode.getType(), name, renameNumber);
                    renameNumber++;
                    continue;
                }
                conflictIds.append(sourceNode.getId()).append(',');
                break;
            }
        } while (true);
    }
    
    /**
     * @param user
     * @param conflictIds
     * @param parentNode
     * @param srcNode
     * @param autoRename
     */
    private void doMoveForOne(final UserToken user, StringBuilder conflictIds, INode parentNode, INode srcNode)
    {
        try
        {
            INode sourceNode = srcNode;
            if (null != requestParam.getAutoRename() && requestParam.getAutoRename())
            {
                sourceNode = folderService.getNodeInfo(user, srcNode.getOwnedBy(), srcNode.getId());
                if (null == sourceNode)
                {
                    String message = "node not exists, [ " + srcNode.getOwnedBy() + ", " + srcNode.getId()
                        + " ]";
                    logger.warn(message);
                    throw new NoSuchItemsException(message);
                }
            }
            doMove(user, conflictIds, parentNode, sourceNode);
        }
        catch (NoSuchItemsException noEx)
        {
            conflictIds.append(SRC_NOT_FOUND).append(',');
        }
        catch (ForbiddenException e)
        {
            conflictIds.append(AsyncTaskStatus.FORBBIDEN).append(',');
        }
        catch (SubFolderConflictException e)
        {
            conflictIds.append(ErrorCode.SUB_FOLDER_CONFILICT.getCode()).append(',');
        }
        catch (SameNodeConflictException e)
        {
            conflictIds.append(e.getCode()).append(',');
        }
        catch (SameParentConflictException e)
        {
            conflictIds.append(e.getCode()).append(',');
        }
        catch(NoSuchSourceException e)
        {
            conflictIds.append(AsyncTaskStatus.NO_SUCH_SOURCE).append(',');
        }
        catch(NoSuchDestException e)
        {
            conflictIds.append(AsyncTaskStatus.NO_SUCH_DEST).append(',');
        }
        catch (Exception e)
        {
            logger.warn("", e);
            conflictIds.append(SYS_EXCEPTION).append(',');
        }
    }
    
}

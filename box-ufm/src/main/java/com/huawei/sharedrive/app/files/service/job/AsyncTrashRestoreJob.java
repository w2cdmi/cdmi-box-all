package com.huawei.sharedrive.app.files.service.job;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.FilesNameConflictException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.NoSuchParentException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.files.service.TrashServiceV2;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class AsyncTrashRestoreJob implements Runnable
{
    private static Logger logger = LoggerFactory.getLogger(AsyncTrashRestoreJob.class);
    
    private static final int RESULT_SIZE = 1000;
    
    private String fileRestoreTaskPath;
    
    private FolderService folderService;
    
    private long ownerId;
    
    private UserToken token;
    
    private TrashServiceV2 trashServiceV2;
    
    private CuratorFramework zkClient;
    
    private Long parentId;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public AsyncTrashRestoreJob(UserToken token, String taskPath, long ownerId, CuratorFramework zkClient,
        FolderService folderService, TrashServiceV2 trashServiceV2, Long parentId)
    {
        this.token = token;
        this.fileRestoreTaskPath = taskPath;
        this.ownerId = ownerId;
        this.zkClient = zkClient;
        this.trashServiceV2 = trashServiceV2;
        this.folderService = folderService;
        this.parentId = parentId;
    }
    
    @Override
    public void run()
    {
        StringBuilder conflictIds = new StringBuilder();
        try
        {
            List<INode> nodeList = null;
            long offset = 0L;
            boolean isEmpty = false;
            do
            {
                nodeList = new ArrayList<INode>(BusinessConstants.INITIAL_CAPACITIES);
                offset = restoreFixTrashItems(nodeList, conflictIds, offset);
                isEmpty = nodeList.isEmpty();
            } while (!isEmpty);
        }
        catch(ForbiddenException e)
        {
            conflictIds.append(AsyncTaskStatus.FORBBIDEN);
            logger.error("", e);
        }
        catch (Exception e)
        {
            conflictIds.append(AsyncTaskStatus.SYSTEM_EXCEPTION);
            logger.error("", e);
        }
        deleteOrUpdateZK(fileRestoreTaskPath, conflictIds.toString());
    }
    
    private long restoreFixTrashItems(List<INode> nodeList, StringBuilder noParentIds, long offset)
        throws BaseRunException
    {
        FileINodesList list = null;

        list = trashServiceV2.listTrashItems(token, ownerId, RESULT_SIZE, offset, null, null, true);
        nodeList.addAll(list.getFiles());
        nodeList.addAll(list.getFolders());
        INode parent = null;
        if(null != parentId)
        {
            parent = new INode();
            parent.setOwnedBy(ownerId);
            parent.setId(parentId);
        }
        for (INode node : nodeList)
        {
            try
            {
                trashServiceV2.restoreTrashItem(token, node, null, parent);
            }
            catch (FilesNameConflictException e)
            {
                restoreWithNewName(node.getOwnedBy(), node.getId(), parent);
            }
            catch (NoSuchParentException e)
            {
                noParentIds.append(String.valueOf(node.getId())).append(',');
                offset++;
            }
        }
        return offset;
    }
    
    /**
     * 还原时如果文件名冲突, 进行重命名操作
     * 
     * @param ownerId
     * @param nodeId
     * @param parent
     * @throws NumberFormatException
     * @throws BaseRunException
     */
    private void restoreWithNewName(Long ownerId, Long nodeId, INode parent) throws NumberFormatException,
        BaseRunException
    {
        String newName = null;
        INode node = folderService.getNodeNoCheckStatus(token, ownerId, nodeId);
        
        int i = 1;
        while (true)
        {
            newName = FilesCommonUtils.getNewName(node.getType(), node.getName(), i);
            try
            {
                trashServiceV2.restoreTrashItem(token, node, newName, parent);
                break;
            }
            catch (FilesNameConflictException e)
            {
                i++;
            }
        }
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

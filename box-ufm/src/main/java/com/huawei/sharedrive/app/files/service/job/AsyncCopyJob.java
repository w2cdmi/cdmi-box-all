package com.huawei.sharedrive.app.files.service.job;

import java.nio.charset.Charset;

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
import com.huawei.sharedrive.app.exception.NoSuchLinkException;
import com.huawei.sharedrive.app.exception.NoSuchSourceException;
import com.huawei.sharedrive.app.exception.SameNodeConflictException;
import com.huawei.sharedrive.app.exception.SameParentConflictException;
import com.huawei.sharedrive.app.exception.SubFolderConflictException;
import com.huawei.sharedrive.app.filelabel.dto.BindType;
import com.huawei.sharedrive.app.filelabel.dto.FileMoveAndCopyDto;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.filelabel.util.FilelabelUtils;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.RequestNode;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class AsyncCopyJob implements Runnable
{
    
    private static final String SRC_NOT_FOUND = "src_not_found";
    
    private static final String SYS_EXCEPTION = "sys_exception";
    
    public static final String ASYNC_START_STATUS = "Starting";
    
    /** 异步任务根路径 */
    public static final String ASYNC_TASK_PATH = "/webAsyncTask";
    
    private static Logger logger = LoggerFactory.getLogger(AsyncCopyJob.class);
    
    private FolderService folderService;
    
    private RequestAddAsyncTask requestParam;
    
    private String taskPath;
    
    private UserToken user;
    
    private boolean valiLinkAccessCode;
    
    private CuratorFramework zkClient;
    
    private IFileLabelService filelabelService;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public AsyncCopyJob(RequestAddAsyncTask requestParam, UserToken user, String taskPath,
        CuratorFramework zkClient, FolderService folderService, boolean valiLinkAccessCode, 
        IFileLabelService filelabelService)
    {
        this.requestParam = requestParam;
        this.user = user;
        this.taskPath = taskPath;
        this.zkClient = zkClient;
        this.folderService = folderService;
        this.valiLinkAccessCode = valiLinkAccessCode;
        this.filelabelService = filelabelService;
    }
    
    @Override
    public void run()
    {
        StringBuilder conflictIds = new StringBuilder();
        INode parentNode = new INode();
        parentNode.setId(requestParam.getDestFolderId());
        parentNode.setOwnedBy(requestParam.getDestOwnerId());
        INode srcNode = null;
        for (RequestNode srcReqNode : requestParam.getSrcNodeList())
        {
            FileMoveAndCopyDto fmacDto = new FileMoveAndCopyDto();
            fmacDto.setFromOwnerId(requestParam.getSrcOwnerId());
            fmacDto.setFormNodeId(srcReqNode.getSrcNodeId());
            fmacDto.setToOwnerId(requestParam.getDestOwnerId());
            fmacDto.setOptUserId(user.getId());
            fmacDto.setEnterpriseId(user.getAccountId());
            fmacDto.setBindType(BindType.get(requestParam.getEndPoint()));

            srcNode = new INode();
            srcNode.setId(srcReqNode.getSrcNodeId());
            srcNode.setOwnedBy(requestParam.getSrcOwnerId());
            doCopyForOne(user, conflictIds, parentNode, srcNode, fmacDto);
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
    private void doCopy(final UserToken user, StringBuilder conflictIds, INode parentNode, INode sourceNode,
        FileMoveAndCopyDto fmacDto) throws BaseRunException {
        int renameNumber = 1;
        String name = sourceNode.getName();
        String newName = name;
        boolean autoRename = requestParam.getAutoRename();
        do
        {
            try
            {
                INode newNode = folderService.copyNodeToFolder(user, sourceNode, parentNode, newName, valiLinkAccessCode);
                
                try {
					fmacDto.setDestNodeId(newNode.getId());
					fmacDto.setToOwnerId(newNode.getOwnedBy());
					fmacDto.setEnterpriseId(user.getAccountId());
					if(fmacDto.getToOwnerId() <= 0){
					    fmacDto.setToOwnerId(parentNode.getOwnedBy());
					}
					FilelabelUtils.bindFilelabelForNode(fmacDto, filelabelService);
				} catch (Throwable e) {
				}
                
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
    private void doCopyForOne(final UserToken user, StringBuilder conflictIds, INode parentNode, INode srcNode, 
        FileMoveAndCopyDto fmacDto)
    {
        try
        {
            INode sourceNode = srcNode;
            if (requestParam.getAutoRename())
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
            doCopy(user, conflictIds, parentNode, sourceNode, fmacDto);
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
        catch(NoSuchLinkException e)
        {
            conflictIds.append(AsyncTaskStatus.FORBBIDEN).append(',');
        }
        catch (Exception e)
        {
            logger.warn("", e);
            conflictIds.append(SYS_EXCEPTION).append(',');
        }
    }
    
}

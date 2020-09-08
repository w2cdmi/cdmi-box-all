package com.huawei.sharedrive.app.files.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.NodeCopyService;
import com.huawei.sharedrive.app.files.service.lock.Locks;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingAddedFilesService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Limit;

@Component
@Service("nodeCopyService")
public class NodeCopyServiceImpl implements NodeCopyService
{
    private static final int LIMIT = 1000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeCopyServiceImpl.class);
    
    // 注入上下文
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    @Autowired
    private RecordingAddedFilesService recordingAddedFilesService;
    
    private NodeCopyService proxySelf; // ② 表示代理对象，不是目标对象
    
    @Autowired
    private UserDAOV2 userDao;
    
    /**
     * 复制文件到文件夹下
     * 
     * @param useId
     * @param srcFile
     * @param destFolder
     * @return
     * @throws BaseRunException
     */
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public INode copyFileNodeToFolder(UserToken user, INode srcFile, INode destFolder)
        throws BaseRunException
    {
        ObjectReference objRef = objectReferenceDAO.get(srcFile.getObjectId());
        if(null == objRef)
        {
            throw new NoSuchFileException();
        }
        if (fileBaseService.increaseObjectRefCount(objRef) <= 0)
        {
            throw new InternalServerErrorException();
        }
        try
        {
            INode inode = fileBaseService.createNode(user.getId(), srcFile, destFolder);
            if (!fileBaseService.checkObjectInUserStorRegion(user.getId(), objRef))
            {
                LOGGER.warn("Object not In User Storage Region, useId:" + user.getId()
                    + ", object ResourceGroupID:" + objRef.getResourceGroupId());
            }
            recordingAddedFilesService.put(new FilesAdd(inode.getOwnedBy(), inode.getId(),
                userDao.get(inode.getOwnedBy()).getAccountId(), inode.getSize()));
            return inode;
        }
        catch (Exception e)
        {
            fileBaseService.decreaseRefObjectCount(objRef);
            throw e;
        }
    }
    
    /**
     * 
     * @param user_id
     * @param srcFolder
     * @param destParent
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode copyFolderItemToFolder(UserToken user, INode srcFolder, INode destParent)
    {
        INode node = INode.valueOf(srcFolder);
        node.setId(fileBaseService.buildINodeID(destParent.getOwnedBy()));
        node.setParentId(destParent.getId());
        Date date = new Date();
        node.setCreatedAt(date);
        node.setCreatedBy(user.getId());
        node.setModifiedAt(date);
        node.setModifiedBy(user.getId());
        node.setOwnedBy(destParent.getOwnedBy());
        // 复制节点需要去掉共享外链状态
        node.setLinkCode(null);
        node.setShareStatus(INode.SHARE_STATUS_UNSHARED);
        iNodeDAO.create(node);
        return node;
        
    }
    
    /**
     * 复制文件夹
     * 
     * @param user_id
     * @param srcFolder
     * @param destParent
     * @return
     * @throws BaseRunException
     */
    @Override
    public INode copyFolderNodeToFolder(UserToken user, INode srcFolder, INode destParent)
        throws BaseRunException
    {
        try
        {
            Locks.COPY_LOCK.tryLock();
            INode destNode = proxySelf.copyFolderItemToFolder(user, srcFolder, destParent);
            // 复制子文件夹, 封装统计信息
            return copyNodesToFolderByRecursive(user, srcFolder, destNode, destNode.getSyncVersion());
        }
        finally
        {
            Locks.COPY_LOCK.unlock();
        }
    }
    
    /**
     * 递归复制
     * 
     * @param userId
     * @param srcNode
     * @param destFolder
     * @throws BaseRunException
     */
    @Override
    public INode copyNodesToFolderByRecursive(UserToken user, INode srcNode, INode destFolder,
        long syncVersionNum) throws BaseRunException
    {
        Limit limit = new Limit();
        long offset = 0;
        // 文件夹大小(含子文件)
        long size = 0;
        // 文件夹文件数(含子文件夹及文件)
        long fileCount = 0;
        List<INode> subNodeList;
        INode subDestFolder;
        
        // 定义变量保存递归后返回的值, 修改 checkstyle: Control variable 'subNode' is modified
        INode temp;
        while (true)
        {
            // 批量更新节点的状态
            limit.setOffset(offset);
            limit.setLength(LIMIT);
            subNodeList = iNodeDAO.getINodeByParent(srcNode, null, limit);
            
            if (CollectionUtils.isEmpty(subNodeList))
            {
                break;
            }
            
            for (INode subNode : subNodeList)
            {
                if (subNode.getStatus() != INode.STATUS_NORMAL)
                {
                    LOGGER.warn("INode status is abnormal, owner id: {} , node id: {}, status: {}, ",
                        subNode.getOwnedBy(),
                        subNode.getId(),
                        subNode.getStatus());
                    continue;
                }
                
                if (FilesCommonUtils.isFolderType(subNode.getType()))
                {
                    // 创建本节点
                    subNode.setSyncVersion(syncVersionNum);
                    subDestFolder = proxySelf.copyFolderItemToFolder(user, subNode, destFolder);
                    
                    // 递归
                    temp = copyNodesToFolderByRecursive(user, subNode, subDestFolder, syncVersionNum);
                    size += temp.getSize();
                    fileCount += temp.getFileCount() + 1;
                }
                else if (INode.TYPE_FILE == subNode.getType())
                {
                    fileBaseService.setNodeSyncStatus(subNode, destFolder);
                    subNode.setSyncVersion(syncVersionNum);
                    copyFileNodeToFolder(user, subNode, destFolder);
                    size = size + subNode.getSize();
                    fileCount++;
                }
                else
                {
                    continue;
                }
            }
            
            offset = offset + LIMIT;
        }
        
        // 封装子节点统计信息
        INode result = INode.valueOf(destFolder);
        result.setSize(size);
        result.setFileCount(fileCount);
        return result;
    }
    
    @PostConstruct
    public void setSelf()
    {
        // 从上下文获取代理对象（如果通过proxtSelf=this是不对的，this是目标对象）
        // 此种方法不适合于prototype Bean，因为每次getBean返回一个新的Bean
        proxySelf = context.getBean(NodeCopyServiceImpl.class);
    }
}

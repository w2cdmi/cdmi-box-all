/**
 * 
 */
package com.huawei.sharedrive.app.share.service;

import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.RestException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.share.domain.INodeLink;

import pw.cdmi.box.domain.Limit;

/**
 * @author s00108907
 * 
 */

public interface LinkService
{
    
    /**
     * 创建外链
     * 
     * @param link
     */
    void createLinkNoCheck(INodeLink link);
    
    /**
     * 设置外链,包括文件夹及文件外链
     * 
     * @param user
     * @param iNodeLink
     * @return
     * @throws BaseRunException
     */
    INodeLink createLinkV2(UserToken user, INodeLink iNodeLink, INode node) throws BaseRunException;
    
    /**
     * 删除节点外链关系,判断用户权限,供设置外链界面调用 会更新节点外链状态
     * 
     * @param inode
     */
    void deleteLinkByNode(UserToken user, INode inode) throws BaseRunException;
    
    void deleteByOwner(long ownerId);
    
    /**
     * 获取文件外链下载地址
     * 
     * @param user
     * @param linkCode
     * @param linktype，外链
     * @param fileId
     * @return
     * @throws BaseRunException
     */
    String getFileLinkDownloadUrl(UserToken user, String linkCode, Long fileId) throws BaseRunException;
    
    /**
     * 
     * @return
     */
    String getLinkBasePath();
    
    /**
     * 获取外链,登陆用户调用,如果不存在，返回null， 不需要校验外链状态
     * 
     * @param user
     * @param iNodeId
     * @param linktype
     * @return
     * @throws BaseRunException
     */
    INodeLink getLinkByINodeId(UserToken user, long ownerId, long iNodeId) throws BaseRunException;
    
    /**
     * 获取外链,登陆用户调用,如果不存在，返回null， 不需要校验外链状态
     * 
     * @param user
     * @param iNodeId
     * @param linktype
     * @return
     * @throws BaseRunException
     */
    INodeLink getLinkByINodeIdV2(UserToken user, long ownerId, long iNodeId) throws BaseRunException;
    
    /**
     * 根据外链码获取外链, web 匿名用户调用
     * 
     * @param user
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    INodeLink getLinkByLinkCode(UserToken user, String linkCode) throws BaseRunException;
    
    /**
     * 根据提取码获取外链
     * 
     * @param linkCode
     * @return
     */
    INodeLink getLinkByLinkCodeForClientV2(String linkCode) throws BaseRunException;
    
    /**
     * 根据外链码获取外链, web 匿名用户调用
     * 
     * @param user
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    boolean getLinkStatusByLinkCode(UserToken user, String linkCode) throws BaseRunException;
    
    /**
     * 获取外链的节点信息，列举外链时使用
     * 
     * @param user
     * @param inodetype
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    INode getNodeInfoByLinkCode(UserToken user, String linkCode) throws BaseRunException;
    
    /**
     * 列举外链文件夹
     * 
     * @param user
     * @param linkCode
     * @param order
     * @param limit
     * @return
     * @throws BaseRunException
     */
    FileINodesList listFolderLinkByFilter(UserToken user, String linkCode, Long folderId, OrderV1 order,
        Limit limit) throws BaseRunException, RestException;
    
    /**
     * 更新外链
     * 
     * @param user
     * @param inodeLink
     * @return
     * @throws BaseRunException
     */
    INodeLink updateLinkV2(UserToken user, INodeLink iNodeLink) throws BaseRunException;
    
    /**
     * 获取外链,登陆用户调用,如果不存在，返回null， 不需要校验外链状态
     * 
     * @param user
     * @param iNode
     * @return
     * @throws BaseRunException
     */
    INodeLink getLinkByINodeIdV2(UserToken user, INode iNode) throws BaseRunException;

    /**
     * 通过外链码获取外链，直链使用
     * 
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    INodeLink getLinkByLinkCode(String linkCode) throws BaseRunException;

    /**
     * 通过外链获取外链对象,直链post
     * @param userToken
     * @param linkCode
     * @return
     */
    INodeLink getLinkForDirect(UserToken userToken, String linkCode);
}

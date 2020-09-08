/**
 * 
 */
package com.huawei.sharedrive.app.share.service;

import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkDynamicResponse;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.share.domain.INodeLinkDynamic;

import pw.cdmi.box.domain.Order;

/**
 * @author s00108907
 * 
 */

public interface LinkServiceV2
{
    /**
     * 创建外链
     * 
     * @param link
     */
    void createLinkNoCheck(INodeLink link);
    
    /**
     * 删除节点外链关系,判断用户权限,供设置外链界面调用 会更新节点外链状态,支持多外链场景
     * 
     * @param inode
     */
    void deleteLinkByTypeOrId(UserToken user, INode inode, String type, String linkCode)
        throws BaseRunException;
    
    /**
     * 设置外链,包括文件夹及文件外链
     * 
     * @param user
     * @param iNodeLink
     * @return
     * @throws BaseRunException
     */
    INodeLink createLinkV2(UserToken user, INodeLink iNodeLink,List<INode> nodes) throws BaseRunException;
    
    INodeLink getLink(UserToken user, INode iNode, String linkCode) throws BaseRunException;
    
    List<INodeLinkDynamic> getLinkDynamicCode(String linkCode) throws BaseRunException;
    
    /**
     * 根据提取码获取外链
     * 
     * @param linkCode
     * @return
     */
    INodeLink getLinkByLinkCodeForClient(String linkCode) throws BaseRunException;
    
    INodeLink updateLink(UserToken user, INode iNode, INodeLink iNodeLink) throws BaseRunException;
    
    List<INodeLink> listNodeAllLinks(UserToken curUser, long ownerId, long nodeId);
    
    /**
     * 列举目录
     * 
     * @param user
     * @param filter
     * @param limit
     * @param offset
     * @param orderList
     * @param thumbnailList
     * @return
     * @throws BaseRunException
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    FileINodesList listAllLinkNodes(UserToken user, long ownerId, String name, long offset, int limit,
        List<Order> orderList, List<Thumbnail> thumbnailList, Map<String, String> headerCustomMap) throws BaseRunException;
    
    RestLinkDynamicResponse addDynamicAccessCode(String linkCode, String identity) throws BaseRunException;
    
    void deleteLinkDynamicCode(String linkCode, String identity) throws BaseRunException;
    
    void updateExpiredAt(INodeLinkDynamic iNodeLinkDynamic) throws BaseRunException;
    
    void deleteAllLinkByNode(INode node) throws BaseRunException;
    
    /**
     * 校验外链操作权限;对外提供的接口,供folderservice调用
     * 
     * @param user
     * @param linkCode
     * @param oper
     * @return 用户ID
     * @throws BaseRunException
     */
    long vaildLinkOperACL(UserToken user, INode inode, String oper, boolean valiAccessCode)
        throws BaseRunException;

	void updateLinkStatus(UserToken userInfo, INode iNode, INodeLink link);

	INodeLink createLinkV2(UserToken user, INodeLink iNodeLink, INode iNode) throws BaseRunException;

	List<String> listAllLinkCodes(UserToken userToken);

	List<INodeLink> listNodeAllLinksNoCheck(UserToken curUser, long ownerId, long nodeId);
}

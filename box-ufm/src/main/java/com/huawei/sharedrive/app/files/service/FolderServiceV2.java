/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.RecentBrowse;
import com.huawei.sharedrive.app.files.domain.Shortcut;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.node.ListFolderRequest;

import pw.cdmi.box.domain.Order;

/**
 * @author s00108907
 * 
 */
public interface FolderServiceV2
{
    
    /**
     * 列举目录
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    FileINodesList listNodesByFilter(UserToken user, INode filter, long offset, int limit,
                                     List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType, Map<String, String> headerCustomMap) throws BaseRunException;
    /**
     * 列举目录
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    FileINodesList wxMpListNodesByFilter(UserToken user, INode filter, long offset, int limit,
                                         List<Order> orderList, List<Thumbnail> thumbnailList, boolean withExtraType, Map<String, String> headerCustomMap) throws BaseRunException;
    
    INode getFolderInfo(UserToken user, long ownerId, long folderId) throws BaseRunException;

    void setNodeThumbnailUrl(UserToken user, List<Thumbnail> thumbnailList, long userId, INode node,
        Map<String, String> headerCustomMap) throws BaseRunException;
    
    /**
     * 
     * 以json格式返回doctype.json文件内容
     *
     * <参数类型> @return
     * <参数类型> @throws Exception 
     *
     * @return String
     */
    String getJsonDoctype() throws Exception;
    
    /**
     * 
     * 云盘文件智能分类对应的表进行割接。将DocType=5对应的类型修改DocType=x
     *
     * <参数类型>  @param paramMap
     *          keys:{long owner_id,int doctype}
     * <参数类型> @return
     * <参数类型> @throws Exception 
     *
     * @return List<INode>
     */
    void  updateINodeDoctype(Map <String , Map <String , Object>> paramMap) throws Exception ;

    /**
     * 
     * 云盘文件智能分类对应的表进行割接。将DocType=5对应的类型修改DocType=x
     *
     * <参数类型> @return
     * <参数类型> @throws Exception 
     *
     * @return List<INode>
     */
    void updateINodeDoctypeAll() throws Exception;
    
	/**
     * 列举最近浏览记录
     * 
     */
	List<INode> listFolderForRecent(UserToken userToken, int offset, int limit,List<Thumbnail> thumbnailList, Map<String, String> headerCustomMap);

	List<Shortcut> listFolderForShortcut(UserToken userToken);

	void deleteShortcut(long id);

	void createShortcut(Shortcut shortcut);

	void deleteShortByNodeId(long ownedBy, long id);
	
	void deleteRecentByNode(long ownerId, long nodeId);
	
	void deleteRecent(long userId,long ownerId, long nodeId);
	
	void createRecent(long userId, long ownerId, long nodeId);
	FileINodesList listNodesByFilter(UserToken userToken,INode parentNode, ListFolderRequest listFolderRequest,Map<String, String> headerCustomMap);
	void deleteRecentByOwner(long ownerId);
	void deleteShortByOwner(long ownerId);
}

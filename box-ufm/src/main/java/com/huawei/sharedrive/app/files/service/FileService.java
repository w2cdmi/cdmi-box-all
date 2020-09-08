/**
 * 
 */
package com.huawei.sharedrive.app.files.service;

import java.util.List;

import org.springframework.http.HttpHeaders;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.domain.ObjectUpdateInfo;
import com.huawei.sharedrive.app.files.domain.UploadMultipartFileRSP;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadRequest;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponseV1;

import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.domain.Limit;

/**
 * @author s00108907
 * 
 */
public interface FileService
{
    
    /**
     * 取消上传
     * 
     * @param objectId
     * @param ownerId
     * @throws BaseRunException
     */
    void abortUpload(String objectId, Long ownerId) throws BaseRunException;
    
    /**
     * 重刪流程，執行了重刪為true,否則為false
     * 
     * @param fileNode
     * @param objRef
     */
    boolean dedupObject(INode fileNode, ObjectReference objRef) throws BaseRunException;
    
    /**
     * 内部使用接口：删除对象应用计数
     * 
     * @param objectReference
     */
    int deleteObjectReference(ObjectReference objectReference);
    
    /**
     * 获取文件版本总数(含本文件)
     * 
     * @param ownerId
     * @param nodeId
     * @return
     */
    int getCurrentVersionNum(Long ownerId, Long nodeId);
    
    /**
     * 获取文件版本总数(含本文件)
     * 
     * @param ownerId
     * @param nodeId
     * @return
     */
    int getCurrentVersionNumForUpdate(Long ownerId, Long nodeId);
    
    /**
     * 获取文件最早的N条历史版本记录
     * 
     * @param ownerId
     * @param nodeId
     * @param limit
     * @return
     */
    List<INode> getEarliestVersions(Long ownerId, Long nodeId, int limit);
    
    /**
     * 获取下载文件的地址信息，并在header中增加objectId
     * 
     * @param user
     * @param node 节点信息
     * @param header
     * @return
     * @throws BaseRunException
     */
    String getFileDownloadUrl(UserToken user, INode node, HttpHeaders header) throws BaseRunException;
    
    /**
     * 获取文件的下载地址信息
     * 
     * @param user 操作用户
     * @param ownerId 所属用户ID
     * @param fileId 文件ID
     * @return 下载地址
     */
    String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId) throws BaseRunException;
    
    /**
     * 获取下载文件的地址信息，并在header中增加objectId
     * 
     * @param userToken
     * @param ownerId
     * @param fileId
     * @param header
     * @return
     * @throws BaseRunException
     */
    String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId, HttpHeaders header)
        throws BaseRunException;
    
    /**
     * 获取文件Object的下载地址信息
     * 
     * @param user 操作用户
     * @param ownerId 所属用户ID
     * @param fileId 文件ID
     * @return 下载地址
     */
    String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId, String objectId)
        throws BaseRunException;
    
    String getFileDownloadUrlWithoutName(UserToken user, INode node, HttpHeaders header)
        throws BaseRunException;
    
    /**
     * 分页获取文件版本
     * 
     * @param user
     * @param fileNode
     * @param limit
     * @return
     * @throws BaseRunException
     */
    
    FileINodesList getFileVersionLists(UserToken user, INode fileNode, Limit limit) throws BaseRunException;
    
    UploadMultipartFileRSP getMultipartFilesToken(UserToken user, UploadMultipartFileRSP multipartFileRSP,
        Long ownerId, long fileId) throws BaseRunException;
    
    /**
     * 查询用户文件总数(含文件/文件夹/版本/回收站中的数据)
     * 
     * @param ownerId
     * @return
     */
    long getUserTotalFiles(Long ownerId);
    
    /**
     * 获取用户已用空间(不包括回收站中正在删除的文件)
     * 
     * @param long
     * @return
     */
    long getUserTotalSpace(long ownerId);
    
    /**
     * 列举指定库表的文件和文件版本
     * 
     * @param dbNum
     * @param tableNum
     * @param offset
     * @param length
     * @return
     */
    List<INode> listFileAndVersions(int dbNum, int tableNum, long offset, int length);
    
    /**
     * 预上传，对已经传入sha1值做闪传特性，无法闪转的对象返回上传地址
     * 
     * @param user
     * @param filenode
     * @param tokenTimeout
     * @param ownerId 
     * @return
     * @throws BaseRunException
     */
    FilePreUploadResponseV1 preUploadFile(UserToken user, INode filenode, FilePreUploadRequest request, boolean enableUploadNearest, Long ownerId)
        throws BaseRunException;
    
    /**
     * 更新文件版本
     * 
     * @param objectupdateinfo
     * @throws BaseRunException
     */
    void updateObjectInfo(ObjectUpdateInfo objectupdateinfo) throws BaseRunException;
    
    /**
     * 更新删除时间
     * 
     * @param objectReference
     */
    void updateObjectRefDeleteTime(ObjectReference objectReference);

	/**
	 * 获取文件预览对象
	 * @param node 源文件标识
	 * @return
	 */
	ImgObject getImgObject(INode node);
	
	/**
	 * 获取转换任务
	 * @param node
	 * @return
	 */
	TaskBean getTaskBean(INode node);

}

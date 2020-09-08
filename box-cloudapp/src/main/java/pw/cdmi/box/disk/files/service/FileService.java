package pw.cdmi.box.disk.files.service;

import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFileInfo;
import pw.cdmi.box.disk.files.domain.ObjectSecretLevel;
import pw.cdmi.box.disk.oauth2.domain.UserToken;

public interface FileService
{
    
    String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId, String objectId)
        throws BaseRunException, RestException;
    
    String getFileThumbUrls(UserToken user, Long ownerId, Long fileId) throws RestException;
    
    String getFileThumbUrls(UserToken user, INode node, String linkCode, String thumbNailUrl) throws BaseRunException;
    
    RestFileInfo getFileInfo(UserToken user, Long ownerId, Long fileId) throws RestException;

    ObjectSecretLevel getNodeSecretLevel(String token, long ownerId, long iNodeId);
    
}

package pw.cdmi.box.disk.files.service;

import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;

public interface FolderService
{
    
    INode getNodeInfo(UserToken user, long ownerId, long nodeID) throws BaseRunException, RestException;
    
    INode getNodeInfoCheckType(UserToken user, long ownerId, long nodeID, int type) throws RestException;
    
    RestFolderLists listNodesbyFilter(BasicNodeListRequest listFolderRequest, long ownerId, UserToken user,
        Long folderId) throws BaseRunException, RestException;
    
}

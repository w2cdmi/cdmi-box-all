package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

/**
 * 节点集合Rest对象
 * 
 * @version CloudStor CSE Service Platform Subproject, 2014-9-5
 * @see
 * @since
 */
public class RestNodeList
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RestNodeList.class);
    
    private List<RestFileInfo> files;
    
    private List<RestFolderInfo> folders;
    
    public RestNodeList()
    {
        this.files = new ArrayList<RestFileInfo>(BusinessConstants.INITIAL_CAPACITIES);
        this.folders = new ArrayList<RestFolderInfo>(BusinessConstants.INITIAL_CAPACITIES);
    }
    
    public RestNodeList(List<INode> nodeList, int clientType)
    {
        this();
        if (CollectionUtils.isEmpty(nodeList))
        {
            return;
        }
        
        for (INode temp : nodeList)
        {
            if (INode.TYPE_FILE == temp.getType())
            {
                FilesCommonUtils.setNodeVersionsForV2(temp);
                files.add(new RestFileInfo(temp, clientType));
            }
            else if (FilesCommonUtils.isFolderType(temp.getType()))
            {
                folders.add(new RestFolderInfo(temp));
            }
            else
            {
                LOGGER.warn("Invalid node type, node id: {}, node type: {}", temp.getId(), temp.getType());
            }
        }
        
    }
    
    public List<RestFileInfo> getFiles()
    {
        return files;
    }
    
    public List<RestFolderInfo> getFolders()
    {
        return folders;
    }
    
    public void setFiles(List<RestFileInfo> files)
    {
        this.files = files;
    }
    
    public void setFolders(List<RestFolderInfo> folders)
    {
        this.folders = folders;
    }
    
}

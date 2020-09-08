package pw.cdmi.box.disk.client.domain.node;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.FilesCommonUtils;

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
    
    public RestNodeList(List<INode> nodeList)
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
                files.add(new RestFileInfo(temp));
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

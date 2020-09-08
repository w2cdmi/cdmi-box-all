package pw.cdmi.box.disk.client.domain.node;

import java.io.Serializable;

import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.InvalidParamException;

public class RenameAndSetSyncRequest implements Serializable
{
    
    private static final long serialVersionUID = 8123331276821820075L;

    private String name;
    
    private Boolean syncStatus;
    
    public void checkParameter() throws InvalidParamException
    {
        if (name != null)
        {
            FilesCommonUtils.checkNodeNameVaild(name);
        }
    }
    
    public String getName()
    {
        return name;
    }
    
    public Boolean getSyncStatus()
    {
        return syncStatus;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setSyncStatus(Boolean syncStatus)
    {
        this.syncStatus = syncStatus;
    }
    
}

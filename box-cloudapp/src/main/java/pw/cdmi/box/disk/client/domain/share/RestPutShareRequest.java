package pw.cdmi.box.disk.client.domain.share;

import java.io.Serializable;
import java.util.List;

import pw.cdmi.box.disk.share.domain.INodeShare;

public class RestPutShareRequest implements Serializable
{
    
    private static final long serialVersionUID = 2299157953956521496L;

    private String message;
    
    private List<INodeShare> shareList;

    public String getMessage()
    {
        return message;
    }

    public List<INodeShare> getShareList()
    {
        return shareList;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setShareList(List<INodeShare> shareList)
    {
        this.shareList = shareList;
    }
    
}

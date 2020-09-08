package pw.cdmi.box.disk.client.domain.node;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.BaseRunException;

public class RestFilesRenameRequest
{
    private String name;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name) throws BaseRunException
    {
        if (StringUtils.isNotBlank(name))
        {
            FilesCommonUtils.checkNodeNameVaild(name);
            this.name = name;
        }
        else
        {
            throw new BadRquestException();
        }
    }
    
}

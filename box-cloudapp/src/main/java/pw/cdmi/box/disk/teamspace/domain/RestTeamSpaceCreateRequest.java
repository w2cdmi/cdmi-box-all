package pw.cdmi.box.disk.teamspace.domain;

import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.InvalidParamException;

public class RestTeamSpaceCreateRequest extends RestTeamSpaceBaseRequest
{
    @Override
    public void checkParameter() throws BaseRunException
    {
        checkNameValid();
        
        checkDescValid();
        
        checkSpaceQuotaValid();
        
        checkStatusValid();
        
        checkMaxVersions();
        
        checkMaxMembersValid();
        
    }
    
    private void checkMaxMembersValid()
    {
        if (maxMembers != null && maxMembers != -1)
        {
            if (maxMembers <= 0)
            {
                throw new InvalidParamException("Invalid maxMembers: " + maxMembers);
            }
        }
        else
        {
            maxMembers = -1;
        }
    }
    
    private void checkMaxVersions()
    {
        if (maxVersions != null && maxVersions != -1)
        {
            if (maxVersions <= 0)
            {
                throw new InvalidParamException("Invalid maxVersions: " + maxVersions);
            }
        }
        else
        {
            maxVersions = -1;
        }
    }
    
    private void checkStatusValid()
    {
        if (status != null)
        {
            if (status != Constants.SPACE_STATUS_ENABLE && status != Constants.SPACE_STATUS_DISABLE)
            {
                throw new InvalidParamException("Invalid status: " + status);
            }
        }
        else
        {
            status = Constants.SPACE_STATUS_ENABLE;
        }
    }
    
    private void checkSpaceQuotaValid()
    {
        if (spaceQuota != null && spaceQuota != -1L)
        {
            if (spaceQuota <= 0)
            {
                throw new InvalidParamException("Invalid spaceQuota: " + spaceQuota);
            }
        }
        else
        {
            spaceQuota = -1L;
        }
    }
    
    private void checkDescValid()
    {
        if (description != null && description.length() > 255)
        {
            throw new InvalidParamException("Invalid description: " + description);
        }
    }
    
    private void checkNameValid()
    {
        if (name == null)
        {
            throw new InvalidParamException("name is null");
        }
        
        if (name.length() == 0 || name.length() > 255)
        {
            throw new InvalidParamException("Invalid name: " + name);
        }
    }
}

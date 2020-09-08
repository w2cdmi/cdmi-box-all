package pw.cdmi.box.disk.teamspace.domain;

import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.InvalidParamException;

public class RestTeamSpaceModifyRequest extends RestTeamSpaceBaseRequest
{
    @Override
    public void checkParameter() throws BaseRunException
    {
        checkName();
        
        checkDescription();
        
        checkSpaceQuota();
        
        checkStatus();
        
        checkMaxVersions();
        
        checkMaxMembers();
        
    }

    private void checkMaxMembers()
    {
        if (maxMembers != null && maxMembers != -1)
        {
            if (maxMembers <= 0)
            {
                throw new InvalidParamException("Invalid maxMembers: " + maxMembers);
            }
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
    }

    private void checkStatus()
    {
        if (status != null)
        {
            if (status != Constants.SPACE_STATUS_ENABLE && status != Constants.SPACE_STATUS_DISABLE)
            {
                throw new InvalidParamException("Invalid status: " + status);
            }
        }
    }

    private void checkSpaceQuota()
    {
        if (spaceQuota != null && spaceQuota != -1)
        {
            if (spaceQuota <= 0)
            {
                throw new InvalidParamException("Invalid spaceQuota: " + spaceQuota);
            }
        }
    }

    private void checkDescription()
    {
        if (description != null && description.length() > 255)
        {
            throw new InvalidParamException("Invalid description: " + description);
        }
    }

    private void checkName()
    {
        if (name != null)
        {
            if (name.length() == 0 || name.length() > 255)
            {
                throw new InvalidParamException("Invalid name: " + name);
            }
        }
    }
}

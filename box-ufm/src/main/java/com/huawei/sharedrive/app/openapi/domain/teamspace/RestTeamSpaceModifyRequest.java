package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

public class RestTeamSpaceModifyRequest extends RestTeamSpaceBaseRequest
{
    public void checkParameter() throws BaseRunException
    {
        // 校验团队空间名称合法性
        if (name != null)
        {
            name = name.trim();
            if (name.length() == 0 || name.length() > 255)
            {
                throw new InvalidParamException("Invalid name: " + name);
            }
        }
        
        // 校验团队空间描述
        if (description != null && description.length() > 255)
        {
            throw new InvalidParamException("Invalid description: " + description);
        }
        
        // 校验团队空间最大容量。 单位MB。-1表示无限制。默认值为-1。
        if (spaceQuota != null && spaceQuota != -1)
        {
            if (spaceQuota <= 0)
            {
                throw new InvalidParamException("Invalid spaceQuota: " + spaceQuota);
            }
        }
        
        // 校验团队空间状态
        if (status != null)
        {
            if (status != TeamSpace.STATUS_ENABLE && status != TeamSpace.STATUS_DISABLE)
            {
                throw new InvalidParamException("Invalid status: " + status);
            }
        }
        
        // 校验团队空间最大版本数
        if (maxVersions != null && maxVersions != -1)
        {
            if (maxVersions <= 0)
            {
                throw new InvalidParamException("Invalid maxVersions: " + maxVersions);
            }
        }
        
        // 校验团队空间最大成员数
        if (maxMembers != null && maxMembers != -1)
        {
            if (maxMembers <= 0)
            {
                throw new InvalidParamException("Invalid maxMembers: " + maxMembers);
            }
        }
        
    }
}

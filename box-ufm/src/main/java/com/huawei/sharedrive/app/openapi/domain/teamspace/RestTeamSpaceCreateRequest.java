package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

public class RestTeamSpaceCreateRequest extends RestTeamSpaceBaseRequest
{
    @Override
    public void checkParameter() throws BaseRunException
    {
        // 校验团队空间名称合法性
        if (name == null)
        {
            throw new InvalidParamException("Invalid name: " + name);
        }
        name = name.trim();
        if (name.length() == 0 || name.length() > 255)
        {
            throw new InvalidParamException("Invalid name: " + name);
        }
        
        // 校验团队空间描述
        if (description != null && description.length() > 255)
        {
            throw new InvalidParamException("Invalid description: " + description);
        }

        // 校验团队空间最大容量。 单位Byte。-1表示无限制。默认值为-1。
        this.spaceQuota = chechAndGetSpaceQuota(this.spaceQuota);
        
        // 校验团队空间状态
        this.status = checkAndGetStatus(this.status);
        
        // 校验团队空间最大版本数
        this.maxVersions = checkAndGetMaxVersions(this.maxVersions);
        
        // 校验团队空间最大成员数
        this.maxMembers = checkAndGetMaxMembers(this.maxMembers);
        
    }
    
    private Long chechAndGetSpaceQuota(Long spaceQuota) throws InvalidParamException
    {
        // 校验团队空间最大容量。 单位Byte。-1表示无限制。默认值为-1。
        if (spaceQuota != null && spaceQuota != -1)
        {
            if (spaceQuota <= 0)
            {
                throw new InvalidParamException("Invalid spaceQuota: " + spaceQuota);
            }
        }
        else
        {
            // 设置默认值
            spaceQuota = -1L;
        }
        
        return spaceQuota;
    }
    
    private Integer checkAndGetMaxMembers(Integer maxMembers) throws InvalidParamException
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
            // 设置默认值
            maxMembers = -1;
        }
        
        return maxMembers;
    }
    
    private Integer checkAndGetMaxVersions(Integer maxVersions) throws InvalidParamException
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
            // 设置默认值
            maxVersions = -1;
        }
        
        return maxVersions;
    }
    
    private Integer checkAndGetStatus(Integer status) throws InvalidParamException
    {
        // 校验团队空间状态
        if (status != null)
        {
            if (status != TeamSpace.STATUS_ENABLE && status != TeamSpace.STATUS_DISABLE)
            {
                throw new InvalidParamException("Invalid status: " + status);
            }
        }
        else
        {
            // 设置默认值
            status = TeamSpace.STATUS_ENABLE;
        }
        
        return status;
    }
}

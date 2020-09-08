/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.enterprise.domain;

/**
 * 
 * @author s90006125
 *
 */
public class EnterpriseAccountModifyRequestInfo
{
    private Boolean filePreviewable;
    private Integer maxMember;
    private Integer maxTeamspace;
    private Long maxSpace;
    public Boolean getFilePreviewable()
    {
        return filePreviewable;
    }
    public void setFilePreviewable(Boolean filePreviewable)
    {
        this.filePreviewable = filePreviewable;
    }
    public Integer getMaxMember()
    {
        return maxMember;
    }
    public void setMaxMember(Integer maxMember)
    {
        this.maxMember = maxMember;
    }
    public Integer getMaxTeamspace()
    {
        return maxTeamspace;
    }
    public void setMaxTeamspace(Integer maxTeamspace)
    {
        this.maxTeamspace = maxTeamspace;
    }
    public Long getMaxSpace()
    {
        return maxSpace;
    }
    public void setMaxSpace(Long maxSpace)
    {
        this.maxSpace = maxSpace;
    }
}

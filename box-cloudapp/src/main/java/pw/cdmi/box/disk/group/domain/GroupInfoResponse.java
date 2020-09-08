package pw.cdmi.box.disk.group.domain;

import java.util.List;

public class GroupInfoResponse
{
    /** */
    private Long memberNums;
    
    private List<GroupMembershipsInfo> responseData;
    
    public Long getMemberNums()
    {
        return memberNums;
    }
    
    public List<GroupMembershipsInfo> getResponseData()
    {
        return responseData;
    }
    
    public void setMemberNums(Long memberNums)
    {
        this.memberNums = memberNums;
    }
    
    public void setResponseData(List<GroupMembershipsInfo> responseData)
    {
        this.responseData = responseData;
    }
    
}

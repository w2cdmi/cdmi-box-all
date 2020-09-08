package pw.cdmi.box.uam.log.domain;

public class OperateCategoryDomain
{
    private UserLogCategory logCategory;
    
    private String operatrDetail;
    
    private int selfId;
    
    public String getOperatrDetail()
    {
        return operatrDetail;
    }
    
    public void setOperatrDetail(String operatrDetail)
    {
        this.operatrDetail = operatrDetail;
    }
    
    public UserLogCategory getLogCategory()
    {
        return logCategory;
    }
    
    public void setLogCategory(UserLogCategory logCategory)
    {
        this.logCategory = logCategory;
    }
    
    public int getSelfId()
    {
        return selfId;
    }
    
    public void setSelfId(int selfId)
    {
        this.selfId = selfId;
    }
    
}

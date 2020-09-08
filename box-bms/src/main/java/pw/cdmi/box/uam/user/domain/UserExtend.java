/**
 * 
 */
package pw.cdmi.box.uam.user.domain;

public class UserExtend extends User
{
    private static final long serialVersionUID = -6836413448479561401L;
    
    private String tagId;
    
    private String tag;
    
    public String getTagId()
    {
        return tagId;
    }
    
    public void setTagId(String tagId)
    {
        this.tagId = tagId;
    }
    
    public String getTag()
    {
        return tag;
    }
    
    public void setTag(String tag)
    {
        this.tag = tag;
    }
    
}

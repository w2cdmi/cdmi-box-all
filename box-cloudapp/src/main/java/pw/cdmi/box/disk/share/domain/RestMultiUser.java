package pw.cdmi.box.disk.share.domain;

import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.group.domain.GroupConstants;
import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.User;

public class RestMultiUser extends UserToken
{
    private static final long serialVersionUID = -8084354064407387502L;
    
    private byte userType;
    
    public static RestMultiUser buildObject(User user, RestGroup group, byte userType)
    {
        RestMultiUser multUser = new RestMultiUser();
        if (userType == GroupConstants.GROUP_USERTYPE_USER && user != null)
        {
            multUser.setCreatedAt(user.getCreatedAt());
            multUser.setDepartment(HtmlUtils.htmlEscape(user.getDepartment()));
            multUser.setDomain(HtmlUtils.htmlEscape(user.getDomain()));
            multUser.setEmail(HtmlUtils.htmlEscape(user.getEmail()));
            multUser.setId(user.getId());
            multUser.setCloudUserId(user.getCloudUserId());
            multUser.setLoginName(HtmlUtils.htmlEscape(user.getLoginName()));
            multUser.setModifiedAt(user.getModifiedAt());
            multUser.setName(HtmlUtils.htmlEscape(user.getName()));
            multUser.setObjectSid(user.getObjectSid());
            multUser.setPassword(user.getPassword());
            multUser.setSpaceUsed(user.getSpaceUsed());
            multUser.setStatus(user.getStatus());
            multUser.setTeamSpaceFlag(user.getTeamSpaceFlag());
            multUser.setTeamSpaceMaxNum(user.getTeamSpaceMaxNum());
            multUser.setLabel(HtmlUtils.htmlEscape(user.getName()));
            
        }
        else if (userType == GroupConstants.GROUP_USERTYPE_GROUP && group != null)
        {
            multUser.setId(group.getId());
            multUser.setCloudUserId(group.getId());
            multUser.setLoginName(HtmlUtils.htmlEscape(group.getName()));
            multUser.setDepartment(HtmlUtils.htmlEscape(group.getDescription()));
            multUser.setName(HtmlUtils.htmlEscape(group.getName()));
            multUser.setLabel(HtmlUtils.htmlEscape(group.getName()));
            multUser.setEmail(HtmlUtils.htmlEscape(group.getId() + GroupConstants.GROUP_EMAIL));
        }
        multUser.setUserType(userType);
        return multUser;
    }
    
    public byte getUserType()
    {
        return userType;
    }
    
    public void setUserType(byte userType)
    {
        this.userType = userType;
    }
    
}

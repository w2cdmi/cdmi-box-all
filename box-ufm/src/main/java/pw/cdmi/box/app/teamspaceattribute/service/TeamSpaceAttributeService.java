   
	 /**     
 * @discription 
 * @author zhangxinren       
 * @created 2016年7月11日 下午4:22:02            
     */
    
package pw.cdmi.box.app.teamspaceattribute.service;

import java.util.List;

import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttribute;
import com.huawei.sharedrive.app.user.domain.User;

  
    /**        
 * Title: TeamSpaceAttributeService.java    
 * Description: 
 * @author zhangxinren       
 * @created 2016年7月11日 下午4:22:02    
 */

public interface TeamSpaceAttributeService {
    
    /**
     * @discription 添加团队空间属性表属性
     * @author zhangxinren       
     * @param teamSpaceAttribute 团队空间属性
     * @param user user token
     * @return int 
     * @created 2016年7月11日 下午7:14:18
     */
    void addTeamSpaceAttribute(TeamSpaceAttribute teamSpaceAttribute, User user);
    
    /**
     * @discription 创建团队空间时添加团队空间属性表默认属性
     * @param cloudUserId
     */
    void addTeamSpaceDefaultAttributes(long cloudUserId, User user);
    
    /**
     * @discription 删除团队空间属性表属性
     * @author zhangxinren       
     * @param cloudUserId 团队空间ID
     * @param user user token
     * @return int 
     * @created 2016年7月11日 下午7:16:04
     */
    int deleteTeamSpaceAttribute(long cloudUserId, User user);
    
    /**
     * @discription 通过团队空间ID获取团队空间属性表属性列表
     * @author zhangxinren       
     * @param cloudUserId 团队空间ID
     * @return int 
     * @created 2016年7月11日 下午7:16:34
     */
    List<TeamSpaceAttribute> getTeamSpaceAttribute(long cloudUserId);
    
    /**
     * @discription 通过团队空间ID与属性名获取团队空间属性表属性
     * @author zhangxinren       
     * @param teamSpaceAttribute 团队空间属性
     * @return TeamSpaceAttribute 
     * @created 2016年7月11日 下午7:20:20
     */
    TeamSpaceAttribute getTeamSpaceAttribute(TeamSpaceAttribute teamSpaceAttribute);
    
    /**
     * @discription 设置团队空间属性表属性
     * @author zhangxinren       
     * @param cloudUserId 团队空间ID
     * @param teamSpaceAttribute 团队空间属性
     * @return int 
     * @created 2016年7月11日 下午7:17:03
     */
    int setTeamSpaceAttribute(TeamSpaceAttribute teamSpaceAttribute, UserToken user);
    
    /**
     * @discription 设置团队空间属性表某团队空间所有属性
     * @author zhangxinren       
     * @param
     * @return int 
     * @created 2016年7月11日 下午7:23:12
     */
    void setAllTeamSpaceAttribute(long cloudUserId, List<TeamSpaceAttribute> teamSpaceAttribute, UserToken user);
}

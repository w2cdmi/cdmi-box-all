   
	 /**     
 * @discription 
 * @author zhangxinren       
 * @created 2016年7月11日 下午4:22:51            
     */
    
package pw.cdmi.box.app.teamspaceattribute.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.teamspace.SetTeamSpaceAttrRequest;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttribute;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttributeEnum;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.app.teamspaceattribute.dao.TeamSpaceAttributeDAO;
import pw.cdmi.box.app.teamspaceattribute.service.TeamSpaceAttributeService;

  
    /**        
     * Title: TeamSpaceAttributeServiceImpl.java    
     * Description: 团队空间属性服务，uploadNotice在teamSpace表中，不能在此查到
     * @author zhangxinren       
     * @created 2016年7月11日 下午4:22:51    
     */

/* 其它地方调用该服务的名称  */
@Service("teamSpaceAttributeService")
public class TeamSpaceAttributeServiceImpl implements TeamSpaceAttributeService{

    private static final Logger logger = LoggerFactory.getLogger(TeamSpaceAttributeServiceImpl.class);
    
    @Autowired
    private TeamSpaceAttributeDAO teamSpaceAttributeDAO;
    
    @Autowired
    private TeamSpaceService teamSpaceService;
    
    @Autowired
    private TeamSpaceMembershipService teamSpaceMembershipService;
    
        /**
         * @discription 添加团队空间属性表属性
         * @author zhangxinren       
         * @param cloudUserId 团队空间ID
         * @param teamSpaceAttribute 团队空间属性
         * @param user user token
         * @return int 
         * @created 2016年7月11日 下午7:14:18
         */
        @Override
        public void addTeamSpaceAttribute(TeamSpaceAttribute teamSpaceAttribute, User user){
            logger.info("ufm.addTeamSpaceAttribute begin.");
            try{
                // 先校验
                /*TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceAttribute.getCloudUserId());
                checkACL(user, teamSpace.getCloudUserId());*/
                
                // 插入属性
                teamSpaceAttributeDAO.set(teamSpaceAttribute);
                logger.info("ufm.addTeamSpaceAttribute end.");
            }catch(BaseRunException e){
                logger.error("ufm.addTeamSpaceAttribute failed.", e);
            }
        }

          
        /**
         * @discription 删除团队空间属性表属性
         * @author zhangxinren       
         * @param cloudUserId 团队空间ID
         * @param user user token
         * @return int 
         * @created 2016年7月11日 下午7:16:04
         */
        @Override
        public int deleteTeamSpaceAttribute(long cloudUserId, User user) {
            logger.info("ufm.deleteTeamSpaceAttribute begin.");
            int result = 0;
            // 返回0则说明出现异常，否则为正常
            try {
                /*TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(cloudUserId);
                checkACL(user, teamSpace.getCloudUserId());*/
                result = teamSpaceAttributeDAO.deleteTeamspaceAttribute(cloudUserId);
                logger.info("ufm.deleteTeamSpaceAttribute end.");
                return result;
            } catch (BaseRunException e) {
                logger.error("ufm.deleteTeamSpaceAttribute failed, team space ID: " + cloudUserId, e);
                return result;
            }
        }

          
        /**
         * @discription 通过团队空间ID获取团队空间属性表属性列表
         * @author zhangxinren       
         * @param cloudUserId 团队空间ID
         * @return int 
         * @created 2016年7月11日 下午7:16:34
         */
        @Override
        public List<TeamSpaceAttribute> getTeamSpaceAttribute(long cloudUserId) {
            logger.debug("ufm.getTeamSpaceAttribute begin.");
            try {
                teamSpaceService.checkAndGetTeamSpaceExist(cloudUserId);
            } catch (BaseRunException e) {
                logger.error("ufm.getTeamSpaceAttribute failed.", e);
                return null;
            }
            List<TeamSpaceAttribute> teamSpaceAttributes = new ArrayList<TeamSpaceAttribute>();
            teamSpaceAttributes = teamSpaceAttributeDAO.selectByTeamSpaceId(cloudUserId);
            if(teamSpaceAttributes == null){
                logger.error("ufm.getTeamSpaceAttribute: no such team space attributes.");
                return teamSpaceAttributes;
            }
            logger.debug("ufm.getTeamSpaceAttribute end.");
            return teamSpaceAttributes;
        }

          
        /**
         * @discription 通过团队空间ID与属性名获取团队空间属性表属性
         * @author zhangxinren       
         * @param teamSpaceAttribute 团队空间属性
         * @return TeamSpaceAttribute 
         * @created 2016年7月11日 下午7:20:20
         */
        @Override
        public TeamSpaceAttribute getTeamSpaceAttribute( TeamSpaceAttribute teamSpaceAttribute) {
            logger.debug("ufm.getTeamSpaceAttribute begin.");
            try {
                teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceAttribute.getCloudUserId());
            } catch (BaseRunException e) {
                logger.error("ufm.getTeamSpaceAttribute failed.", e);
                return null;
            }
            TeamSpaceAttribute teamSpaceAttributeRet = new TeamSpaceAttribute();
            teamSpaceAttributeRet = teamSpaceAttributeDAO.select(teamSpaceAttribute);
            if(teamSpaceAttributeRet == null){
                logger.error("ufm.getTeamSpaceAttribute: no such team space attribute.");
                return teamSpaceAttributeRet;
            }
            logger.debug("ufm.getTeamSpaceAttribute end.");
            return teamSpaceAttributeRet;
        }

          
        /**
         * @discription 设置团队空间属性表属性,在teamSpaceService中已有实现
         * @author zhangxinren       
         * @param user user token
         * @param teamSpaceAttribute 团队空间属性
         * @return int 
         * @created 2016年7月11日 下午7:17:03
         */
        @Override
        public int setTeamSpaceAttribute(TeamSpaceAttribute teamSpaceAttribute, UserToken user) {
            logger.info("ufm.setTeamSpaceAttribute begin.");
            int result = 0;
            try {
                TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(teamSpaceAttribute.getCloudUserId());
                checkACL(user, teamSpace.getCloudUserId(),""+user.getAccountVistor().getEnterpriseId());
                result = teamSpaceAttributeDAO.update(teamSpaceAttribute);
            } catch (BaseRunException e) {
                logger.error("ufm.setTeamSpaceAttribute failed.", e);
                return result;
            }
            
            logger.info("ufm.setTeamSpaceAttribute end.");
            return result;
        }

          
        /**
         * @discription 设置团队空间属性表某团队空间所有属性
         * @author zhangxinren       
         * @param cloudUserId 团队空间ID
         * @param teamSpaceAttributes 团队空间属性
         * @return int 
         * @created 2016年7月11日 下午9:42:52
         */
        @Override
        public void setAllTeamSpaceAttribute(long cloudUserId, List<TeamSpaceAttribute> teamSpaceAttributes, UserToken user) {
            logger.info("ufm.setTeamSpaceAttribute begin.");
            try {
                TeamSpace teamSpace = teamSpaceService.checkAndGetTeamSpaceExist(cloudUserId);
                checkACL(user, teamSpace.getCloudUserId(),""+user.getAccountVistor().getEnterpriseId());
                for(TeamSpaceAttribute teamSpaceAttribute : teamSpaceAttributes){
                    SetTeamSpaceAttrRequest setTeamSpaceAttrRequest = new SetTeamSpaceAttrRequest(teamSpaceAttribute.getName(), teamSpaceAttribute.getValue());
                    setTeamSpaceAttrRequest.checkParameter();
                    teamSpaceAttributeDAO.update(teamSpaceAttribute);
                }
            } catch (BaseRunException e) {
                logger.error("ufm.setTeamSpaceAttribute failed.", e);
            }
            
            logger.info("ufm.setTeamSpaceAttribute end.");
        }

        public void checkACL(UserToken user, long cloudUserId,String enterpriseId)
        {
            // 团队空间的拥有者以及应用管理员可以更新团队空间信息
            if (user.getId() != User.APP_USER_ID)
            {
                TeamSpaceMemberships teamSpaceMember = teamSpaceMembershipService.getUserMemberShips(cloudUserId,
                    user.getId(),enterpriseId);
                
                if (teamSpaceMember == null)
                {
                    logger.error("ufm.checkACL: User {} is not the member of the teamspace {}", user.getId(), cloudUserId);
                    throw new ForbiddenException("User not the member of teamspace");
                }
                
                if (!TeamRole.ROLE_ADMIN.equals(teamSpaceMember.getTeamRole()))
                {
                    logger.error("ufm.checkACL: operation is not allowed.");
                    String excepMessage = "Operation is not allowed , team Role:"
                        + teamSpaceMember.getTeamRole();
                    throw new ForbiddenException(excepMessage);
                }
            }
        }


        /**
         * @discription 创建团队空间时添加团队空间属性表默认属性
         * @param cloudUserId
         */
        @Override
		public void addTeamSpaceDefaultAttributes(long cloudUserId, User user) {

        	// 增加自动预览和优先级默认值
        	addTeamSpaceAttribute(new TeamSpaceAttribute(cloudUserId, TeamSpaceAttributeEnum.AUTO_PREVIEW.getName(), TeamSpaceAttribute.AUTO_PREVIEW_ENABLE_VALUE), user);
        	//addTeamSpaceAttribute(new TeamSpaceAttribute(cloudUserId, TeamSpaceAttributeEnum.PRIORITY.getName(), TeamSpaceAttribute.MEDIUM_PRIORITY_VALUE), user);
			
		}
        
}

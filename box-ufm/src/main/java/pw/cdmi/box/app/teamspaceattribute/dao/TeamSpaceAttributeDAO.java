package pw.cdmi.box.app.teamspaceattribute.dao;

import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttribute;

/**
 * 
 * @author 
 *
 */
public interface TeamSpaceAttributeDAO
{
    /**
     * 设置扩展属性
     * 
     * @return
     */
	void set(TeamSpaceAttribute teamSpaceAttribute);
    
    
    /**
     * 更新扩展属性
     * 
     * @return
     */
     int update(TeamSpaceAttribute teamSpaceAttribute);
     
     
     /**
      * 查询扩展属性是否存在
      * 
      * @return
      */
     TeamSpaceAttribute select(TeamSpaceAttribute teamSpaceAttribute);
     
     /**
      * 查询某个团队空间的扩展属性
      * @param teamSpaceId
      * @return
      */
     List<TeamSpaceAttribute> selectByTeamSpaceId(long teamSpaceId);
     
     /**
      * 删除团队空间时删除扩展属性表的相关属性
      * @param teamId
      * @return
      */
     int deleteTeamspaceAttribute (long teamId);
     
     
     
}

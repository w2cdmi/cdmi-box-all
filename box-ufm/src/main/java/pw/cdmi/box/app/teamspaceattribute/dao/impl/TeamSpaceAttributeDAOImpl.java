package pw.cdmi.box.app.teamspaceattribute.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttribute;

import pw.cdmi.box.app.teamspaceattribute.dao.TeamSpaceAttributeDAO;
import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Repository("teamSpaceAttributeDAO")
@SuppressWarnings(value={"deprecation","unchecked"})
public class TeamSpaceAttributeDAOImpl extends AbstractDAOImpl implements TeamSpaceAttributeDAO
{

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void set(TeamSpaceAttribute teamSpaceAttribute) {
		
		sqlMapClientTemplate.insert("TeamSpaceAttribute.insert", teamSpaceAttribute);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int update(TeamSpaceAttribute teamSpaceAttribute) {
		int result = sqlMapClientTemplate.update("TeamSpaceAttribute.update", teamSpaceAttribute);
		return result;
	}

	@Override
	public TeamSpaceAttribute select(TeamSpaceAttribute teamSpaceAttribute) {
		return (TeamSpaceAttribute)sqlMapClientTemplate.queryForObject("TeamSpaceAttribute.get", teamSpaceAttribute);
	}

	@Override
	public List<TeamSpaceAttribute> selectByTeamSpaceId(long teamSpaceId) {
		return (List<TeamSpaceAttribute>)sqlMapClientTemplate.queryForList("TeamSpaceAttribute.selectByTeamSpaceId", teamSpaceId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int deleteTeamspaceAttribute(long teamId) {
		return sqlMapClientTemplate.delete("TeamSpaceAttribute.deleteTeamspaceAttribute", teamId);
	}
}

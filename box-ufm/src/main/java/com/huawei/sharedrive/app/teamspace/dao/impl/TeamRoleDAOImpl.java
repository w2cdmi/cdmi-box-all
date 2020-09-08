package com.huawei.sharedrive.app.teamspace.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.teamspace.dao.TeamRoleDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service
@SuppressWarnings("deprecation")
public class TeamRoleDAOImpl extends AbstractDAOImpl implements TeamRoleDAO
{
    @SuppressWarnings("unchecked")
    @Override
    public List<TeamRole> listTeamRole()
    {
        return sqlMapClientTemplate.queryForList("TeamRole.getAll");
    }
    
    @Override
    public TeamRole getTeamRole(String role)
    {
        return (TeamRole) sqlMapClientTemplate.queryForObject("TeamRole.get",role);
    }
    
}

package com.huawei.sharedrive.app.openapi.domain.teamspace;

import java.util.List;

import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;

public class RestBatchMigrationTeamSpace {
	
	
	private List<TeamSpace> teams;
	
	private long destUserId;


	public List<TeamSpace> getTeams() {
		return teams;
	}

	public void setTeams(List<TeamSpace> teams) {
		this.teams = teams;
	}

	public long getDestUserId() {
		return destUserId;
	}

	public void setDestUserId(long destUserId) {
		this.destUserId = destUserId;
	}
	
	
	

}

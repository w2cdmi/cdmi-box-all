package com.huawei.sharedrive.uam.teamspace.domain;

import java.util.List;

public class RestTeamMemberCreateRequest {
    private String teamRole;

    private List<RestTeamMember> memberList;

    private String role;
    
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MANAGER = "manager";
    public static final String ROLE_MEMBER = "member";

    public String getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(String teamRole) {
        this.teamRole = teamRole;
    }



	public List<RestTeamMember> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<RestTeamMember> memberList) {
		this.memberList = memberList;
	}

	public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

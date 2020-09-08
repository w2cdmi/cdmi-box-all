package com.huawei.sharedrive.uam.teamspace.domain;

public class RestTeamMemberInfo {
    private Long id;

    private Long teamId;

    private String teamRole;

    private String role;

    private RestTeamMember member;

    private RestTeamSpaceInfo teamspace;

    public RestTeamMemberInfo() {
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(String teamRole) {
        this.teamRole = teamRole;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public RestTeamMember getMember() {
        return member;
    }

    public void setMember(RestTeamMember member) {
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RestTeamSpaceInfo getTeamspace() {
        return teamspace;
    }

    public void setTeamspace(RestTeamSpaceInfo teamspace) {
        this.teamspace = teamspace;
    }

}

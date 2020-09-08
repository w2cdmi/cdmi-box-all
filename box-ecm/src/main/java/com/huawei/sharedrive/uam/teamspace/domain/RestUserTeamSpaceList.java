package com.huawei.sharedrive.uam.teamspace.domain;

import java.util.List;

public class RestUserTeamSpaceList {
    private List<RestTeamMemberInfo> memberships;

    private int limit;

    private long offset;

    private long totalCount;

    public RestUserTeamSpaceList() {
    }

    public List<RestTeamMemberInfo> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<RestTeamMemberInfo> teamMemberList) {
        this.memberships = teamMemberList;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

}

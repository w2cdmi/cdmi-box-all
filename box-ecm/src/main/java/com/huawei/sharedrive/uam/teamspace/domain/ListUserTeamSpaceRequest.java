package com.huawei.sharedrive.uam.teamspace.domain;

public class ListUserTeamSpaceRequest extends BaseListRequest {
    private Long userId;
    private int type;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

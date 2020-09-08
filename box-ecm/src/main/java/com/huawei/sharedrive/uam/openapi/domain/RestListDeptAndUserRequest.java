package com.huawei.sharedrive.uam.openapi.domain;

import java.io.Serializable;

public class RestListDeptAndUserRequest implements Serializable {
    private Long deptId;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
}

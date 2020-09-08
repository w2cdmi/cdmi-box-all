package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestNodeACLCreateRequest {
    private static final String[] USER_TYPE = {INodeACL.TYPE_USER, INodeACL.TYPE_GROUP, INodeACL.TYPE_TEAM,
            INodeACL.TYPE_SYSTEM, INodeACL.TYPE_PUBLIC, INodeACL.TYPE_DEPT};

    private static final Pattern PATTERN_NON_NEGATIVE_INTEGER = Pattern.compile("^\\d+$");

    private Resource resource;

    private String role;

    private List<RestTeamMember> userList;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<RestTeamMember> getUserList() {
        return userList;
    }

    public void setUserList(List<RestTeamMember> userList) {
        this.userList = userList;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Long getResourceOwnerId() {
        if (resource == null) {
            throw new InvalidParamException("resource is null");
        }
        return resource.getOwnerId();
    }

    public Long getResourceNodeId() {
        if (resource == null) {
            throw new InvalidParamException("resource is null");
        }
        return resource.getNodeId();
    }

    public void checkParameter() throws InvalidParamException {
        if (resource == null) {
            throw new InvalidParamException("resource is null");
        }

        if (resource.getNodeId() == null) {
            resource.setNodeId(INode.FILES_ROOT);
        }

        FilesCommonUtils.checkNonNegativeIntegers(resource.getOwnerId(), resource.getNodeId());

        if (StringUtils.isBlank(role)) {
            throw new InvalidParamException("role is null");
        }

        if (userList == null || userList.isEmpty()) {
            throw new InvalidParamException("member is null");
        }

        for (RestTeamMember user : userList) {
            if (user.getId() == null) {
                throw new InvalidParamException("userId is null");
            }

            if (!isUserTypeValid(user.getType())) {
                throw new InvalidParamException("userType is invalid of user: " + user.getId());
            }

            if ((INodeACL.TYPE_USER.equals(user.getType()) || INodeACL.TYPE_GROUP.equals(user.getType()) || INodeACL.TYPE_SYSTEM.equals(user.getType()))) {
                Matcher m = PATTERN_NON_NEGATIVE_INTEGER.matcher(user.getId());
                if (!m.matches()) {
                    throw new InvalidParamException(user.getId() + " is not a non-negative integer");
                }
            }
        }
    }

    @JsonIgnore
    private boolean isUserTypeValid(String userType) {
        for (String temp : USER_TYPE) {
            if (temp.equalsIgnoreCase(userType)) {
                return true;
            }

        }
        return false;
    }

}

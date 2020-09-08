package pw.cdmi.box.disk.teamspace.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.InvalidParamException;

import java.util.List;

public class RestNodeACLCreateRequest {
    private static final String[] USER_TYPE = {Constants.SPACE_TYPE_USER, Constants.SPACE_TYPE_GROUP,
            Constants.SPACE_TYPE_TEAM, Constants.SPACE_TYPE_SYSTEM, Constants.SPACE_TYPE_PUBLIC};

    private Resource resource;

    private String role;

    private List<RestTeamMember> userList;

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
            throw new InvalidParamException("user object or userType is null");
        }

        for(RestTeamMember user : userList) {
            if (!isUserTypeValid(user.getType())) {
                throw new InvalidParamException("userType is invalid");
            }

            if ((Constants.SPACE_TYPE_USER.equals(user.getType()) || Constants.SPACE_TYPE_GROUP.equals(user.getType()))) {
                FilesCommonUtils.checkNonNegativeIntegers(user.getId());
            } else {
                user.setId(User.ANONYMOUS_USER_ID);
            }
        }
    }

    public Resource getResource() {
        return resource;
    }

    public String getRole() {
        return role;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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

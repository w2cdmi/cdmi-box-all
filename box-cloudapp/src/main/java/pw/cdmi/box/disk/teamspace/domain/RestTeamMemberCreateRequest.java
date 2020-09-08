package pw.cdmi.box.disk.teamspace.domain;

import org.apache.commons.lang.StringUtils;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.core.exception.InvalidParamException;

import java.util.List;

public class RestTeamMemberCreateRequest {
    private String teamRole;

    private List<RestTeamMember> memberList;

    private String role;

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

    public void checkParameter() throws InvalidParamException {
        if (StringUtils.isBlank(teamRole)) {
            throw new InvalidParamException("teamRole is blank");
        }

        if (memberList == null || memberList.isEmpty()) {
            throw new InvalidParamException("member is null");
        }

        for (RestTeamMember member : memberList) {
            FilesCommonUtils.checkNonNegativeIntegers(member.getId());

            String userType = member.getType();
            if (StringUtils.isNotBlank(userType)) {
                if (!"user".equals(userType) && !"group".equals(userType)) {
                    throw new InvalidParamException("member  userType is invalid:" + userType);
                }
            } else {
                member.setType("user");
            }
        }
    }
}

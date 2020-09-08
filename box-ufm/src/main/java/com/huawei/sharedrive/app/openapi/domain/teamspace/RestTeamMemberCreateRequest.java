package com.huawei.sharedrive.app.openapi.domain.teamspace;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestTeamMemberCreateRequest {
    private static final Pattern PATTERN_NON_NEGATIVE_INTEGER = Pattern.compile("^\\d+$");

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
            String userType = member.getType();
            if (StringUtils.isNotBlank(userType)) {
                if (TeamSpaceMemberships.TYPE_USER.equals(userType) || TeamSpaceMemberships.TYPE_GROUP.equals(userType) || TeamSpaceMemberships.TYPE_SYSTEM.equals(userType) || TeamSpaceMemberships.TYPE_DEPT.equals(userType)) {
                    if (member.getId() == null) {
                        throw new InvalidParamException("userId is null");
                    }

                    Matcher m = PATTERN_NON_NEGATIVE_INTEGER.matcher(member.getId());
                    if (!m.matches()) {
                        throw new InvalidParamException(member.getId() + " is not a non-negative integer");
                    }
                } else {
                    throw new InvalidParamException("member  userType is invalid:" + userType);
                }
            } else {
                FilesCommonUtils.checkNonNegativeIntegers(Long.valueOf(member.getId()));
                member.setType(TeamSpaceMemberships.TYPE_USER);
            }
        }
    }
}

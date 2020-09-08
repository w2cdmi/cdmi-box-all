package com.huawei.sharedrive.uam.enterpriseuser.manager;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseSecurityPrivilege;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUserEntity;
import com.huawei.sharedrive.uam.enterpriseuser.dto.DataMigrationRequestDto;
import com.huawei.sharedrive.uam.enterpriseuser.dto.EnterpriseUserStatus;
import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.openapi.domain.BasicUserUpdateRequest;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface EnterpriseUserManager
{
    
    long createLocal(EnterpriseUser enterpriseUser, boolean createLoginInfo) throws IOException;

    long createWeixin(EnterpriseUser enterpriseUser, boolean createLoginInfo) throws IOException;

    EnterpriseUserEntity createLocalUser(EnterpriseUser enterpriseUser) throws IOException;
    
    long createLdap(EnterpriseUser enterpriseUser) throws IOException;
    
    Page<EnterpriseUser> getPagedEnterpriseUser(String filter, Long authServerId,String deptId, Long enterpriseId,
        PageRequest pageRequest);
    
    EnterpriseUser getEUserByLoginName(String loginName, String domain);
    
    void updateLdap(EnterpriseUser enterpriseUser, Long userId, Long enterpriseId);
    
    EnterpriseUser getByObjectSid(String objectSid, long enterpriseId) throws LoginAuthFailedException;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void delete(long enterpriseId, long authServerId, String dn, String filter, String ids, String sessionId);
    
    String[] getAllUserId(long enterpriseId, long authServerId, String dn, String filter, String sessionId);
    
    void delete(long userId, long enterpriseId);
    
    boolean update(BasicUserUpdateRequest ruser, EnterpriseUser selEnterpriseUser, String domain);
    
    void exportEmployeeList(HttpServletRequest request, HttpServletResponse response,
        EnterpriseUser enterpriseUser, String id) throws IOException;
    
    void downloadEmployeeInfoTemplateFile(HttpServletRequest request, HttpServletResponse response,long enterpriseId)
        throws IOException;
    
    void updatePassword(EnterpriseUser enterpriseUser);
    
    void updateEnterpriseUserStatus(List<Long> userIds, EnterpriseUserStatus status, Long enterpriseId);
    
    void migrateData(DataMigrationRequestDto migrationInfo, long enterpriseId, List<String> appIdList);
    
    boolean isMigratedForUser(long enterpriseId, long departureUserId);
	
    boolean checkOrganizeEnabled(long enterpriseId);

    EnterpriseSecurityPrivilege getDeptManager(long enterpriseId, long deptId);

    void addDeptManager(long enterpriseId, long deptId, long enterpriseUserId, String appId);

    void updateDepartment(long userId, long enterpriseId, long deptId, String appId);

    List<EnterpriseUser> listInfoSecurityManager(long enterpriseId);

    void setInfoSecurityManager(long enterpriseId, long enterpriseUserId,long deptId);

    EnterpriseUser getArchiveOwner(long enterpriseId, Long deptId);

    Page<EnterpriseUser> getPagedSecurityPrivilege(long enterpriseId, Long deptId, String authServerId, String filter, PageRequest pageRequest,byte roleType);

	Page<EnterpriseUser> getPagedEnterpriseUser(String stringToSqlLikeFields, Long authServerId, String deptId,
			long enterpriseId, PageRequest pageRequest, long type, String status);

	void updateDepartmentInfo(long enterpriseId, long authServerId, long deptId, String dn, String filter,
			String enterpriseUserIds, String sessionId, String appId);

	void deletePrivilegeOwner(long enterpriseId, long enterpriseUserId, byte roleType);

	EnterpriseSecurityPrivilege getPrivilege(EnterpriseSecurityPrivilege securityPrivilege);

	List<EnterpriseSecurityPrivilege> listSecurityPrivilege(EnterpriseSecurityPrivilege filter, Limit limitObj);

	void addDeptManager(long enterpriseId, long deptid, long employeeid,UserToken userToken);

	void addArchiveOwner(long enterpriseId, long deptId, long enterpriseUserId,UserToken userToken);

	void deletePrivilege(EnterpriseSecurityPrivilege enterpriseSecurityPrivilege,UserToken userToken);

	int listSecurityPrivilegeTotal(EnterpriseSecurityPrivilege filter, Limit limitObj);
}

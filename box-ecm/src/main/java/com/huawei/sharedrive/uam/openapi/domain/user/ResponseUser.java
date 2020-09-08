package com.huawei.sharedrive.uam.openapi.domain.user;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.organization.domain.Department;
import com.huawei.sharedrive.uam.user.domain.User;
import pw.cdmi.common.domain.enterprise.Enterprise;

public class ResponseUser {

	public static final String STATUS_NO_SYSTEM = "nonesystemuser";

	public static final String STATUS_DISABLE = "disable";

	public static final String STATUS_ENABLE = "enable";

	public static final int SPACE_UNIT = 1024;

	public static ResponseUser convetToResponseUser(User user) {
		ResponseUser rspUser = new ResponseUser();
		if (user.getCloudUserId() != null && user.getCloudUserId() != 0L) {
			rspUser.setCloudUserId(user.getCloudUserId());
		}
		rspUser.setDescription(user.getDepartment());
		rspUser.setEmail(user.getEmail());
		if (user.getId() == 0L) {
			rspUser.setStatus(STATUS_NO_SYSTEM);
		} else {
			rspUser.setId(user.getId());
			rspUser.setStatus(user.getStatus());
			rspUser.setRegionId(user.getRegionId());
			rspUser.setAppId(user.getAppId());
		}
		rspUser.setLoginName(user.getLoginName());
		rspUser.setName(user.getName());
		rspUser.setSpaceQuota(user.getSpaceQuota());
		rspUser.setSpaceUsed(user.getSpaceUsed());
		rspUser.setFileCount(user.getFileCount());
		rspUser.setMaxVersions(user.getMaxVersions());
		return rspUser;
	}

	public static ResponseUser convetToResponseUser(EnterpriseUserAccount user, String appId) {
		ResponseUser rspUser = new ResponseUser();
		if (user.getCloudUserId() != 0L) {
			rspUser.setCloudUserId(user.getCloudUserId());
		}
		rspUser.setDescription(null == user.getDescription() ? "" : user.getDescription());
		rspUser.setEmail(user.getEmail());
		if (user.getId() == 0L) {
			rspUser.setStatus(STATUS_NO_SYSTEM);
		} else {
			rspUser.setId(user.getId());
			rspUser.setStatus(user.getAccountStatus() == 0 ? STATUS_ENABLE : STATUS_DISABLE);
			rspUser.setRegionId((int) user.getRegionId());
			rspUser.setSpaceUsed(user.getSpaceUsed());
			rspUser.setFileCount(user.getFileCount());
			rspUser.setMaxVersions(null == user.getMaxVersions() ? 0 : user.getMaxVersions());
			rspUser.setAppId(appId);
			rspUser.setDomain(user.getDomain());
			rspUser.setEnterpriseId(user.getEnterpriseId());
			rspUser.setAccountId(user.getAccountId());
			rspUser.setSpaceQuota(null == user.getSpaceQuota() ? 0L : user.getSpaceQuota());
		}
		rspUser.setLoginName(user.getName());
		rspUser.setName(user.getAlias());
		rspUser.setMobile(user.getMobile());
		return rspUser;
	}

    public static ResponseUser convetToResponseUser(EnterpriseUser user,UserAccount account)
    {
        ResponseUser rspUser = new ResponseUser();
        rspUser.setCloudUserId(account.getCloudUserId());
        rspUser.setId(account.getUserId());
        rspUser.setEmail(user.getEmail());
        rspUser.setLoginName(user.getName());
        return rspUser;
    }

	public static ResponseUser convertToResponseUser(EnterpriseUserAccount user, String appId, Enterprise enterprise, Department department) {
		ResponseUser rspUser = convetToResponseUser(user, appId);

		rspUser.setDomain(enterprise.getDomainName());
		rspUser.setEnterpriseName(enterprise.getName());

		if(department != null) {
			rspUser.setDepartmentName(department.getName());
		}

		return rspUser;
	}

	private String appId;

	private Long cloudUserId;

	private String description;

	private String email;

	private Long id;

	private String loginName;

	private String name;

	private Integer regionId;

	private String status;

	private long spaceQuota;

	private int maxVersions;

	private Long spaceUsed;

	private Long fileCount;

	private String domain;

	private Long enterpriseId;

	private String enterpriseName;

	private Long accountId;

	private String mobile;

	private String departmentName;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getMaxVersions() {
		return maxVersions;
	}

	public void setMaxVersions(int maxVersions) {
		this.maxVersions = maxVersions;
	}

	public Long getSpaceUsed() {
		return spaceUsed;
	}

	public void setSpaceUsed(Long spaceUsed) {
		this.spaceUsed = spaceUsed;
	}

	public Long getFileCount() {
		return fileCount;
	}

	public void setFileCount(Long fileCount) {
		this.fileCount = fileCount;
	}

	public long getSpaceQuota() {
		return spaceQuota;
	}

	public void setSpaceQuota(long spaceQuota) {
		this.spaceQuota = spaceQuota;
	}

	public String getAppId() {
		return appId;
	}

	public Long getCloudUserId() {
		return cloudUserId;
	}

	public String getDescription() {
		return description;
	}

	public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public String getLoginName() {
		return loginName;
	}

	public String getName() {
		return name;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public String getStatus() {
		return status;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setCloudUserId(Long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
}

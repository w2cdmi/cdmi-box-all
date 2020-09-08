package com.huawei.sharedrive.uam.organization.service.impl;

import com.huawei.sharedrive.uam.organization.dao.DepartmentAccountDao;
import com.huawei.sharedrive.uam.organization.domain.DepartmentAccount;
import com.huawei.sharedrive.uam.organization.service.DepartmentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AppBasicConfig;

import java.util.List;

@Service
public class DepartmentAccountServiceImpl implements DepartmentAccountService {
    private final static int SPACE_UNIT = 1024;

    @Autowired
    private DepartmentAccountDao deptAccountDao;

    @Override
    public void create(DepartmentAccount deptAccount) {
        deptAccountDao.create(deptAccount);
    }

    @Override
    public void update(DepartmentAccount deptAccount) {
        deptAccountDao.update(deptAccount);
    }

    @Override
    public void delete(long id) {
        deptAccountDao.deleteById(id);
    }

    @Override
    public DepartmentAccount getByDeptIdAndAccountId(long deptId, long accountId) {
        return deptAccountDao.getByDeptIdAndAccountId(deptId, accountId);
    }

    @Override
    public DepartmentAccount getById(long id, long accountId) {
        return deptAccountDao.getById(id, accountId);
    }

    @Override
    public void buildDepartmentAccount(DepartmentAccount deptAccount, AppBasicConfig appBasicConfig) {
        final int UNLIMITED = -1;
        deptAccount.setMaxVersions(appBasicConfig.getMaxFileVersions());
        deptAccount.setRegionId(appBasicConfig.getUserDefaultRegion());
        if (null == appBasicConfig.getUserSpaceQuota() || appBasicConfig.getUserSpaceQuota() <= 0) {
            deptAccount.setSpaceQuota((long)UNLIMITED);
        } else {
            deptAccount.setSpaceQuota(appBasicConfig.getUserSpaceQuota() * SPACE_UNIT * SPACE_UNIT * SPACE_UNIT);
        }

        if (appBasicConfig.isEnableTeamSpace()) {
            deptAccount.setTeamSpaceFlag(0);
            deptAccount.setTeamSpaceMaxNum(appBasicConfig.getMaxTeamSpaces());
            if (null == appBasicConfig.getTeamSpaceQuota() || appBasicConfig.getTeamSpaceQuota() <= 0) {
                deptAccount.setTeamSpaceQuota((long)UNLIMITED);
            } else {
                deptAccount.setTeamSpaceQuota(appBasicConfig.getTeamSpaceQuota() * SPACE_UNIT * SPACE_UNIT * SPACE_UNIT);
            }
        } else {
            deptAccount.setTeamSpaceFlag(1);
            deptAccount.setTeamSpaceMaxNum(UNLIMITED);
            deptAccount.setTeamSpaceQuota((long) UNLIMITED);
        }
        deptAccount.setUploadBandWidth(appBasicConfig.getUploadBandWidth());
        deptAccount.setDownloadBandWidth(appBasicConfig.getDownloadBandWidth());
    }

    @Override
    public int countFiltered(long accountId, long enterpriseId, long userSource, String filter, Integer status) {
        return deptAccountDao.countFiltered(accountId, enterpriseId, userSource, filter, status);
    }

    @Override
    public List<DepartmentAccount> getFiltered(DepartmentAccount deptAccount, long userSource, Limit limit, String filter) {

        return deptAccountDao.getFilterd(deptAccount, userSource, limit, filter);
    }

    @Override
    public void updateStatus(DepartmentAccount deptAccount, String ids) {
        deptAccountDao.updateStatus(deptAccount, ids);

    }

    @Override
    public DepartmentAccount getByCloudDepartmentAccountId(DepartmentAccount deptAccount) {
        return deptAccountDao.getByCloudDepartmentAccountId(deptAccount);
    }

    @Override
    public void buildDepartmentAccountParam(DepartmentAccount deptAccount, AppBasicConfig appBasicConfig) {
        final int UNLIMITED = -1;
        deptAccount.setRegionId(appBasicConfig.getUserDefaultRegion());
        if (deptAccount.getSpaceQuota() != null && deptAccount.getSpaceQuota() > 0) {
            long spaceQuota = deptAccount.getSpaceQuota() == -1 ? -1 : deptAccount.getSpaceQuota() * SPACE_UNIT * SPACE_UNIT * SPACE_UNIT;
            deptAccount.setSpaceQuota(spaceQuota);
        } else {
            if (null == appBasicConfig.getUserSpaceQuota() || appBasicConfig.getUserSpaceQuota() <= 0) {
                deptAccount.setSpaceQuota(appBasicConfig.getUserSpaceQuota());
            } else {
                deptAccount.setSpaceQuota(appBasicConfig.getUserSpaceQuota() * SPACE_UNIT * SPACE_UNIT * SPACE_UNIT);
            }
        }
        if (deptAccount.getMaxVersions() == null || deptAccount.getMaxVersions() <= 0) {
            deptAccount.setMaxVersions(appBasicConfig.getMaxFileVersions());
        }
        if (deptAccount.getTeamSpaceMaxNum() == null || deptAccount.getTeamSpaceMaxNum() <= 0) {
            deptAccount.setTeamSpaceMaxNum(appBasicConfig.getMaxTeamSpaces());
        }

        if (appBasicConfig.isEnableTeamSpace()) {
            deptAccount.setTeamSpaceFlag(0);
            if (null == appBasicConfig.getTeamSpaceQuota() || appBasicConfig.getTeamSpaceQuota() <= 0) {
                deptAccount.setTeamSpaceQuota(appBasicConfig.getTeamSpaceQuota());
            } else {
                deptAccount.setTeamSpaceQuota(appBasicConfig.getTeamSpaceQuota() * SPACE_UNIT * SPACE_UNIT * SPACE_UNIT);
            }
        } else {
            deptAccount.setTeamSpaceFlag(1);
            // deptAccount.setTeamSpaceMaxNum(UNLIMITED);
            deptAccount.setTeamSpaceQuota((long) UNLIMITED);
        }
        deptAccount.setUploadBandWidth(appBasicConfig.getUploadBandWidth());
        deptAccount.setDownloadBandWidth(appBasicConfig.getDownloadBandWidth());
    }

    @Override
    public void updateUserIdById(DepartmentAccount deptAccount) {
        deptAccountDao.updateUserIdById(deptAccount);
    }

    @Override
    public List<DepartmentAccount> listByAccountIdAndDeptId(long accountId, List<Long> deptList) {
        return deptAccountDao.listByAccountIdAndDeptId(accountId, deptList);
    }

	@Override
	public DepartmentAccount getByDeptIdAndEnterpriseId(long deptId, Long enterpriseId) {
		// TODO Auto-generated method stub
		return deptAccountDao.getByDeptIdAndEnterpriseId(deptId,enterpriseId);
	}

	@Override
	public void deleteBydeptId(long deptid, long enterpriseId) {
		// TODO Auto-generated method stub
		deptAccountDao.deleteBydeptId(deptid,enterpriseId);
		
	}
}

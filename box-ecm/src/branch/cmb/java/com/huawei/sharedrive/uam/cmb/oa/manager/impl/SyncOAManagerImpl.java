package com.huawei.sharedrive.uam.cmb.oa.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.common.domain.AuthServer;
import com.huawei.sharedrive.common.domain.enterprise.Enterprise;
import com.huawei.sharedrive.common.domain.enterprise.EnterpriseAccount;
import com.huawei.sharedrive.common.job.JobExecuteContext;
import com.huawei.sharedrive.common.job.JobExecuteRecord;
import com.huawei.sharedrive.common.job.quartz.QuartzJobTask;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.cmb.control.manager.CMBOAControlManager;
import com.huawei.sharedrive.uam.cmb.control.manager.Constants;
import com.huawei.sharedrive.uam.cmb.oa.client.OAClient;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;
import com.huawei.sharedrive.uam.cmb.oa.manager.CMBOrgInfoManager;
import com.huawei.sharedrive.uam.cmb.oa.manager.CMBSapUserManager;
import com.huawei.sharedrive.uam.cmb.oa.manager.CMBUserRelationInfoManager;
import com.huawei.sharedrive.uam.cmb.oa.manager.SyncOAManager;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;

@Component
public class SyncOAManagerImpl extends QuartzJobTask implements SyncOAManager
{
    private static final long serialVersionUID = 1181341044966495493L;
    
    // TODO待生成配置文件,密码需要加密分段存储
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncOAManagerImpl.class);
    
    @Autowired
    private OAClient oAClient;
    
    @Autowired
    private CMBOrgInfoManager cMBOrgInfoManager;
    
    @Autowired
    private CMBSapUserManager cMBSapUserManager;
    
    @Autowired
    private CMBUserRelationInfoManager cMBUserRelationInfoManager;
    
    @Autowired
    private CMBOAControlManager cMBOAControlManager;
    
    @Autowired
    private EnterpriseUserManager enterpriseUserManager;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Override
    public void doTask(JobExecuteContext arg0, JobExecuteRecord arg1)
    {
        syncUser();
    }
    
    private boolean isListValid(List<CMBOrgInfo> cmbOrgInfoList, List<CMBSapUser> cmbSapUserList,
        List<CMBUserRelationInfo> cmbUserRelationInfoList)
    {
        if (cmbOrgInfoList == null || cmbSapUserList == null || cmbUserRelationInfoList == null)
        {
            return false;
        }
        if (cmbOrgInfoList.size() < 1 || cmbSapUserList.size() < 1 || cmbUserRelationInfoList.size() < 1)
        {
            return false;
        }
        return true;
    }
    
    @Override
    public void syncUser()
    {
        if (!Constants.isCMB)
        {
            return;
        }
        long currentTime = System.currentTimeMillis();
        LOGGER.info("---sync cmbuser start---");
        List<CMBOrgInfo> cmbOrgInfoList = oAClient.getAllCMBOrgInfo(Constants.URL,
            Constants.APPCODE,
            Constants.PTOKE,
            0);
        List<CMBSapUser> cmbSapUserList = oAClient.getAllCMBSapUser(Constants.URL,
            Constants.APPCODE,
            Constants.PTOKE,
            0);
        List<CMBUserRelationInfo> cmbUserRelationInfoList = oAClient.getAllCMBUserRelationInfo(Constants.URL,
            Constants.APPCODE,
            Constants.PTOKE,
            0);
        if (!isListValid(cmbOrgInfoList, cmbSapUserList, cmbUserRelationInfoList))
        {
            return;
        }
        cMBOrgInfoManager.insertCMBOrgInfo(cmbOrgInfoList);
        cMBSapUserManager.insertCMBSapUser(cmbSapUserList);
        cMBUserRelationInfoManager.insertCMBUserRelationInfo(cmbUserRelationInfoList);
        
        List<CMBSapUser> selCMBSapUserList = null;
        if (StringUtils.isBlank(Constants.CMB_ORG_USERCREATE))
        {
            selCMBSapUserList = cMBSapUserManager.getAll();
        }
        else
        {
            String[] fatherGroupIdStr = Constants.CMB_ORG_USERCREATE.split(",");
            List<String> fatherGroupIds = Arrays.asList(fatherGroupIdStr);
            List<String> groupIds = new ArrayList<String>();
            cMBOrgInfoManager.getGroupIds(fatherGroupIds, groupIds);
            if (groupIds != null && groupIds.size() > 0)
            {
                selCMBSapUserList = cMBUserRelationInfoManager.listCMBSapUserByGroupIds(groupIds);
            }
        }
        if (null != selCMBSapUserList && selCMBSapUserList.size() > 0)
        {
            Enterprise enterprise = cMBOAControlManager.getEnterprise();
            if (null == enterprise)
            {
                LOGGER.error("enterprise is null");
                return;
            }
            AuthServer authServer = cMBOAControlManager.getAuthServer(enterprise.getId());
            if (null == authServer)
            {
                LOGGER.error("authServer is null enterpriseId:" + enterprise.getId());
                return;
            }
            for (CMBSapUser cmbSapUser : selCMBSapUserList)
            {
                try
                {
                    createUpdateCMBSapUser(cmbSapUser, enterprise, authServer);
                }
                catch (IOException e)
                {
                    LOGGER.error("IOException:" + cmbSapUser.getSapId() + " name:" + cmbSapUser.getName(), e);
                    return;
                }
                catch (Exception e)
                {
                    LOGGER.error("create user failed sapId:" + cmbSapUser.getSapId() + " name:"
                        + cmbSapUser.getName(),
                        e);
                }
            }
            
        }
        LOGGER.info("---sync cmbuser end---" + (System.currentTimeMillis() - currentTime));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createUpdateCMBSapUser(CMBSapUser cmbSapUser, Enterprise enterprise, AuthServer authServer)
        throws IOException
    {
        long enterpriseId = enterprise.getId();
        EnterpriseUser enterpriseUser = getEnterpriseUser(cmbSapUser);
        EnterpriseUser selEnterpriseUser = enterpriseUserManager.getByObjectSid(cmbSapUser.getSapId(),
            enterpriseId);
        if (null == selEnterpriseUser)
        {
            enterpriseUser.setUserSource(authServer.getId());
            enterpriseUser.setEnterpriseId(enterpriseId);
            enterpriseUserManager.createLdap(enterpriseUser);
            selEnterpriseUser = enterpriseUserManager.getByObjectSid(enterpriseUser.getObjectSid(),
                enterpriseId);
        }
        else
        {
            if (isEnterpriseUserChanged(enterpriseUser, selEnterpriseUser))
            {
                enterpriseUserManager.updateLdap(enterpriseUser, selEnterpriseUser.getId(), enterpriseId);
            }
        }
        List<EnterpriseAccount> list = enterpriseAccountManager.getByEnterpriseId(enterpriseId);
        for (EnterpriseAccount enterpriseAccount : list)
        {
            String authAppId = enterpriseAccount.getAuthAppId();
            UserAccount userAccount = userAccountManager.getUserAccountByApp(selEnterpriseUser.getId(),
                enterpriseId,
                authAppId);
            if (null == userAccount)
            {
                userAccountManager.create(selEnterpriseUser.getId(), enterpriseId, authAppId);
                userAccount = userAccountManager.getUserAccountByApp(selEnterpriseUser.getId(),
                    enterpriseId,
                    authAppId);
                if (Constants.USER_STATUS == UserAccount.INT_STATUS_DISABLE)
                {
                    userAccount.setStatus(UserAccount.INT_STATUS_DISABLE);
                    userAccountManager.update(userAccount, authAppId);
                }
            }
            // else {
            // int cmbSapUserStatus = CMBSapUser.STATUS_NORMAL
            // .equals(cmbSapUser.getStatus()) ? UserAccount.INT_STATUS_ENABLE
            // : UserAccount.INT_STATUS_DISABLE;
            // if (cmbSapUserStatus != userAccount.getStatus()) {
            // userAccountManager.update(userAccount, authAppId);
            // }
            // }
        }
        
    }
    
    private EnterpriseUser getEnterpriseUser(CMBSapUser cmbSapUser)
    {
        EnterpriseUser enterpriseUser = new EnterpriseUser();
        enterpriseUser.setName(cmbSapUser.getSapId());
        enterpriseUser.setObjectSid(cmbSapUser.getSapId());
        enterpriseUser.setAlias(cmbSapUser.getName());
        enterpriseUser.setEmail(cmbSapUser.getEmail());
        enterpriseUser.setMobile(cmbSapUser.getOfficeTel());
        List<CMBUserRelationInfo> cmbUserRelationInfoList = cMBUserRelationInfoManager.getByUserId(cmbSapUser.getUserId());
        if (null != cmbUserRelationInfoList && cmbUserRelationInfoList.size() > 0)
        {
            String path = "";
            for (CMBUserRelationInfo cmbUserRelationInfo : cmbUserRelationInfoList)
            {
                CMBOrgInfo info = cMBOrgInfoManager.getCMBOrgInfo(cmbUserRelationInfo.getOrgId());
                if (info != null)
                {
                    path = path + info.getPathName() + ";";
                }
            }
            if (StringUtils.isNotBlank(path))
            {
                path = path.substring(0, path.length() - 1);
            }
            enterpriseUser.setDescription(path);
        }
        return enterpriseUser;
    }
    
    private boolean isEnterpriseUserChanged(EnterpriseUser enterpriseUser, EnterpriseUser selEnterpriseUser)
    {
        boolean isChanged = false;
        String alias = enterpriseUser.getAlias();
        String email = enterpriseUser.getEmail();
        String name = enterpriseUser.getName();
        String description = enterpriseUser.getDescription();
        String mobile = enterpriseUser.getMobile();
        if (StringUtils.isNotBlank(alias))
        {
            if (!alias.equalsIgnoreCase(selEnterpriseUser.getAlias()))
            {
                return true;
            }
        }
        if (StringUtils.isNotBlank(email))
        {
            if (!email.equalsIgnoreCase(selEnterpriseUser.getEmail()))
            {
                return true;
            }
        }
        if (StringUtils.isNotBlank(name))
        {
            if (!name.equalsIgnoreCase(selEnterpriseUser.getName()))
            {
                return true;
            }
        }
        if (StringUtils.isBlank(description))
        {
            if (StringUtils.isNotBlank(selEnterpriseUser.getDescription()))
            {
                return true;
            }
        }
        else
        {
            if (!description.equals(selEnterpriseUser.getDescription()))
            {
                return true;
            }
        }
        if (StringUtils.isNotBlank(mobile))
        {
            if (!mobile.equalsIgnoreCase(selEnterpriseUser.getMobile()))
            {
                return true;
            }
        }
        return isChanged;
    }
}

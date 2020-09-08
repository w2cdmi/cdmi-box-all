package com.huawei.sharedrive.uam.anon.service.impl;

import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.anon.service.EnterpriseBySelfService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserver;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverManager;
import com.huawei.sharedrive.uam.authserver.service.AuthServerService;
import com.huawei.sharedrive.uam.enterprise.dao.impl.EnterpriseIdGenerator;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.manager.EnterpriseUserManager;
import com.huawei.sharedrive.uam.exception.*;
import com.huawei.sharedrive.uam.httpclient.rest.EnterpriseHttpClient;
import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.log.service.SystemLogService;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountRequest;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountResponse;
import com.huawei.sharedrive.uam.system.domain.MailServer;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;
import com.huawei.sharedrive.uam.system.service.MailServerService;
import com.huawei.sharedrive.uam.teamspace.domain.*;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.domain.AdminRole;
import com.huawei.sharedrive.uam.user.service.AdminService;
import com.huawei.sharedrive.uam.util.Constants;
import com.huawei.sharedrive.uam.util.PasswordGenerateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContextUtils;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.AuthServer;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.BundleUtil;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.core.utils.IpUtils;
import pw.cdmi.uam.domain.AuthApp;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by hepan on 2017/4/15.
 *
 */
@Service
public class EnterpriseBySelfServiceImpl implements EnterpriseBySelfService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseBySelfServiceImpl.class);

    @Autowired
    EnterpriseService enterpriseService;

    @Autowired
    AdminService adminService;

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    private AuthServerService authServerService;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private MailServerService mailServerService;

    @Autowired
    private EnterpriseIdGenerator enterpriseIdGenerator;

    @Resource
    private RestClient ufmClientService;

    @Autowired
    private EnterpriseAccountService enterpriseAccountService;

    @Autowired
    private AccountAuthserverManager accountAuthserverManager;

    @Autowired
    private SystemLogService systemLogService;

    @Autowired
    private AdminLogManager adminLogManager;

    @Autowired
    EnterpriseUserManager enterpriseUserManager;

    @Autowired
    UserAccountManager userAccountManager;
    
    @Autowired
    private TeamSpaceService teamSpaceService;

    @Autowired
    private SystemLogManager systemLogManager;

    @Autowired
    private AppBasicConfigService appBasicConfigService;

    public static final byte STATUS_ENABLE = 0;

    public static final byte STATUS_DISABLE = 1;

    public static final int UNLIMIT_NUM_RESTORE = 99999999;

    public static final int UNLIMIT_NUM = -1;

    public static final long UNLIMIT_NUM_RESTORE_SPACE = 999999999999L;

    /** 非负整数正则表达式 */
    private static final Pattern PATTERN_NON_NEGATIVE_INTEGER = Pattern.compile("^\\d+$");

    private static final int MINVALUE = 1;

    private static final long MAXVALUE = Long.MAX_VALUE;

    private static final int LIMITLESS = -1;

    private static final int SUFFIX_LENG_THREE = 3;

    private static final int MAX_LENG_FIFTY_FIVE = 255;

    private static final int MAX_LENG_TWENTY_SEVEN = 127;

    private static final int MAX_LENG_SIXTY_FOUR = 64;

    /** 企业注册
         * 自助注册企业，分为以下几步
     *  1 创建企业
     *  2 绑定应用
     *  3 绑定认证方式
     *  4 创建员工
     *  5 开户
     *  */
    @Override
    public EnterpriseUser enterpriseRegister(Enterprise enterprise, RestEnterpriseAccountRequest enterpriseAccountRequest, String  userName, String loginName,HttpServletRequest request) throws IOException {
//        setDefaultValue(enterpriseAccountRequest);
        Set violations = validator.validate(enterprise);
        if (!violations.isEmpty()) {
            saveValidateLog(request, OperateType.CreateEnterprise);
            throw new ConstraintViolationException(violations);
        }
        try {
            checkBindAccountParam(enterpriseAccountRequest.getMaxMember(), enterpriseAccountRequest.getMaxTeamspace(), enterpriseAccountRequest.getMaxSpace());
        } catch (InvalidParamterException e) {
            saveValidateLog(request, OperateType.BindApp);
            throw e;
        }
        enterprise.setStatus(Enterprise.STATUS_ENABLE);

        Locale locale = RequestContextUtils.getLocale(request);
        String language = locale.getLanguage();
        if ("en_US".equals(language) || "en".equals(language)) {
            locale = Locale.ENGLISH;
        } else {
            locale = Locale.CHINA;
        }

        String localAuthServerName = OperateDescription.AUTHSERVER_TYPE_LOCAL.getDetails(locale, null);
        String localAuthServerDescription = OperateDescription.AUTHSERVER_DESCRIPTION_LOCAL.getDetails(locale, null);

        int step = 1;
        List<EnterpriseAccount> bindApplications = null;
        AuthServer authServer = null;
        EnterpriseUser enterpriseUser = null;
        try {
            authServer = createEnterprise(request, enterprise, localAuthServerName, localAuthServerDescription,userName);
            step++;
            bindApplications = bindApplication(request, enterprise, enterpriseAccountRequest, authServer);
            step++;
            bindAuthServer(enterprise, authServer.getId(), bindApplications, authServer.getName(), request);
            step++;
            enterpriseUser = createEnterpriseUser(request, enterprise, authServer, userName,loginName);
        } catch (Exception e) {
            unregisterEnterprise(enterprise, enterpriseAccountRequest, userName, bindApplications, authServer, enterpriseUser, step);
            throw e;
        }
        BundleUtil.addBundle("messages",new Locale[]{locale});
        if(bindApplications.size()!=0){
            try {
                //添加默认知识库
                String archivestore = BundleUtil.getText("messages", locale, "enterprise.teamspace.archivestore");
                createDefaultTeamSpace(enterprise.getId(), bindApplications.get(0).getAuthAppId(), TeamSpace.TYPE_ARCHIVE_STORE, archivestore, "uploadAndView");
            } catch (Exception e) {
                LOGGER.error("Failed to create archiveStore and inbox", e);
            }

            try {
                //添加收件箱
                String inbox = BundleUtil.getText("messages", locale, "enterprise.teamspace.inbox");
                createDefaultTeamSpace(enterprise.getId(),  bindApplications.get(0).getAuthAppId(), TeamSpace.TYPE_RECEIVE_FOLDER, inbox, "editor");
            } catch (Exception e) {
                LOGGER.error("Failed to create archiveStore and inbox", e);
            }
        }

        return enterpriseUser;

    }

/*
    private void setDefaultValue(RestEnterpriseAccountRequest enterpriseAccountRequest) {
        AppBasicConfig config = appBasicConfigService.getAppBasicConfig(authAppService.getCurrentAppId());
        AccountBasicConfig accountBasicConfig = new AccountBasicConfig();
        accountBasicConfig.setAccountId(userInfo.getAccountId());
        accountBasicConfig = accountBasicConfigService.get(accountBasicConfig, authAppService.getCurrentAppId());

        if (!accountBasicConfig.isEnableTeamSpace()) {
            restTeamSpaceCreateRequest.setMaxMembers(-1);
            restTeamSpaceCreateRequest.setMaxVersions(-1);
            restTeamSpaceCreateRequest.setSpaceQuota((long) -1);
        } else {

            if (!accountBasicConfig.getTeamSpaceQuota().equals("-1")) {
                config.setTeamSpaceQuota(Long.parseLong(accountBasicConfig.getTeamSpaceQuota()));
            }
            if (!accountBasicConfig.getTeamSpaceVersions().equals("-1")) {
                config.setMaxFileVersions(Integer.parseInt(accountBasicConfig.getTeamSpaceVersions()));
            }
            Integer value = config.getTeamSpaceMaxMembers();
            if (value != null) {
                restTeamSpaceCreateRequest.setMaxMembers(value);
            }

            value = config.getMaxFileVersions();
            if (value != null) {
                restTeamSpaceCreateRequest.setMaxVersions(value);
            }

            Long spaceQuota = config.getTeamSpaceQuota();
            if (spaceQuota != null && spaceQuota != -1) {
                restTeamSpaceCreateRequest.setSpaceQuota(spaceQuota * GB_BYTE);
            }
        }
    }
*/

    public void createDefaultTeamSpace(long enterpriseId, String appId, int type, String name, String role) {
        //添加收件箱空间
        RestTeamSpaceCreateRequest request = new RestTeamSpaceCreateRequest();
        request.setName(name);
        request.setMaxMembers(-1);
        request.setMaxVersions(-1);
        request.setSpaceQuota((long) -1);
        request.setType(type);//收件箱
        request.setOwnerBy(-2L);//系统所有。此处不指定Owner，使用[account,...]Token，UFM会自动设置owner为-2（APP_USER_ID)
        AppBasicConfig appBasicConfig = appBasicConfigService.getAppBasicConfig(appId);
        request.setRegionId(appBasicConfig.getUserDefaultRegion().byteValue());
        RestTeamSpaceInfo restInfo = teamSpaceService.createTeamSpace(enterpriseId, appId, request);

        //添加成员
        RestTeamMember restTeamMember = new RestTeamMember();
        restTeamMember.setId(""+enterpriseId);
        restTeamMember.setLoginName(RestTeamMember.TYPE_SYSTEM);
        restTeamMember.setName(RestTeamMember.TYPE_SYSTEM);
        restTeamMember.setType(RestTeamMember.TYPE_SYSTEM);
        List<RestTeamMember> memberList = new ArrayList<>();
        memberList.add(restTeamMember);
        RestTeamMemberCreateRequest memberRequest = new RestTeamMemberCreateRequest();
        
        memberRequest.setMemberList(memberList);
        memberRequest.setTeamRole(RestTeamMemberCreateRequest.ROLE_MEMBER);
        memberRequest.setRole(role);

        teamSpaceService.addTeamSpaceMember(enterpriseId, appId, restInfo.getId(), memberRequest);
    }

    // 邀请注册用户,只创建不开户
    @Override
    public void enterpriseUserRegister(String domain, EnterpriseUser enterpriseUser, HttpServletRequest request) throws Exception {
        Enterprise enterprise = enterpriseService.getByDomainName(domain);
        if (enterprise == null) {
            throw new Exception("enterprise is not exists!");
        }

        AuthServer authServer = authServerService.getByEnterpriseIdType(enterprise.getId(), AuthServer.AUTH_TYPE_LOCAL);
        if (authServer == null) {
            throw new Exception("enterprise's Auth server does not config");
        }
        enterpriseUser.setEnterpriseId(enterprise.getId());
        enterpriseUser.setStatus(EnterpriseUser.STATUS_ENABLE);

        // 管理员未审核的状态置未不可用状态，且无须发送密码邮件
//        try {
//            enterpriseUser = createEnterpriseUser(request, enterprise, enterpriseUser, authServer, true);
//            openEnterpriseUser(request, enterprise, authServer, enterpriseUser);
//        } catch (Exception e) {
//            enterpriseUserManager.delete(enterpriseUser.getId(), enterprise.getId());
//            throw e;
//        }
    }

    // 邀请注册

    /**
     * 一旦注册失败，进行回退所有数据库内容
     *
     * */
    public void unregisterEnterprise(Enterprise enterprise, RestEnterpriseAccountRequest enterpriseAccountRequest, String accountId, List<EnterpriseAccount> bindApplications,
                                     AuthServer authServer, EnterpriseUser enterpriseUser, int step) {
        switch (step) {
        case 5:
        case 4:// 删除用户信息
            if (null != enterpriseUser)
                enterpriseUserManager.delete(enterpriseUser.getId(), enterprise.getId());
        case 3:// 解除认证绑定
            if (null != bindApplications)
                bindApplications.forEach(enterpriseAccount -> accountAuthserverManager.unBindApp(authServer.getId(), enterpriseAccount.getAccountId()));
        case 2:// 解除应用绑定
            if (null != bindApplications)
                bindApplications.forEach(enterpriseAccount -> enterpriseAccountService.deleteByAccountId(enterpriseAccount.getAccountId()));
        case 1: // 删除企业
            Admin admin = adminService.getAdminByLoginName(enterprise.getContactEmail());
            if (null != admin && enterprise.getId() == admin.getEnterpriseId())
                adminService.delete(admin.getId());
            enterpriseService.deleteById(enterprise.getId());
        default:
            break;
        }
    }

    // Step 1 创建企业
    private AuthServer createEnterprise(HttpServletRequest request, Enterprise enterprise, String localAuthServerName, String localAuthServerDescription,String adminLoginName) throws IOException {
        String byteStr = String.valueOf(enterprise.getStatus());
        String[] description = new String[] { enterprise.getName(), enterprise.getDomainName(), enterprise.getContactEmail(), enterprise.getContactPerson(),
                enterprise.getContactPhone(), byteStr };
        String id = saveFailLog(request, OperateType.CreateEnterprise, OperateDescription.CREATE_ENTERPRISE, null, description, enterprise.getContactPerson(),
            enterprise.getContactPhone());

        if (enterpriseService.isDuplicateValues(enterprise)) {
            throw new ExistEnterpriseConflictException();
        }
        enterprise.setId(enterpriseIdGenerator.getNextId());
        enterpriseService.create(enterprise);

        // Create enterprise bussines administrator
        createEnterpriseAdmin(enterprise, enterprise.getId(),adminLoginName);

        // Create enterprise local Auth
        AuthServer authServer = authServerService.getDefaultAuthServer();
        if (authServer == null) {
            authServer = new AuthServer();
            authServer.setType(AuthServer.AUTH_TYPE_LOCAL);
            authServer.setEnterpriseId(enterprise.getId());
            authServer.setDescription(localAuthServerDescription);

            authServerService.createAuthServer(authServer);
        }

        systemLogService.updateSuccess(id);

        return authServer;
    }

    // Step 2 绑定应用
    private List<EnterpriseAccount> bindApplication(HttpServletRequest request, Enterprise enterprise, RestEnterpriseAccountRequest enterpriseAccountRequest, AuthServer authServer)
        throws IOException {
        List<EnterpriseAccount> bindApplications = new ArrayList<>();
        List<AuthApp> authApps = authAppService.getAuthAppList(null, null, null);
        if (null == authApps || authApps.isEmpty()) {
            saveValidateLog(request, OperateType.BindApp);
            throw new InvalidParameterException();
        }

        String[] description2 = new String[] { enterprise.getName(), authApps.toString() };
        String logId = saveFailLog(request, OperateType.BindApp, OperateDescription.BIND_APP, null, description2, enterprise.getName(), enterprise.getContactPhone());
        enterpriseAccountRequest.setEnterpriseId(enterprise.getId());
        enterpriseAccountRequest.setDomain(enterprise.getDomainName());
        for (AuthApp authApp : authApps) {
            EnterpriseAccount enterpriseAccount = createEnterpriseAccount(enterpriseAccountRequest, enterprise, authApp);
            bindApplications.add(enterpriseAccount);
        }
        systemLogService.updateSuccess(logId);
        return bindApplications;
    }

    private void createEnterpriseAdmin(Enterprise enterprise, long id,String adminLoginName) throws IOException {
        Admin admin = new Admin();
        HashSet<AdminRole> roles = new HashSet<AdminRole>(1);
        roles.add(AdminRole.ENTERPRISE_MANAGER);

        // 优先使用email作为login name.
        if(StringUtils.isNotBlank(adminLoginName)){
            admin.setLoginName(adminLoginName);
        } else if (StringUtils.isNotBlank(enterprise.getContactEmail())) {
            admin.setLoginName(enterprise.getContactEmail());
        } else if (StringUtils.isNotBlank(enterprise.getContactPhone())) {
            admin.setLoginName(enterprise.getContactPhone());
        } else {
            admin.setLoginName(enterprise.getContactPerson());
        }

        //
        admin.setName(adminLoginName);
        admin.setEmail(enterprise.getContactEmail());
        admin.setRoles(roles);
        admin.setEnterpriseId(id);
        admin.setDomainType(Constants.DOMAIN_TYPE_LOCAL);

        String randomPassword = PasswordGenerateUtil.getRandomPassword();
        admin.setPassword(randomPassword);
        admin.setType(Constants.ROLE_ENTERPRISE_ADMIN);

        // loginName + enterpriseId唯一，不会有重名
        Admin oldAdmin = adminService.getByLoginNameAndEnterpriseId(admin.getLoginName(), id);
        if (oldAdmin != null) {
            enterpriseService.deleteById(id);
            throw new BadRequestException();
        }
        adminService.create(admin);

        // 如果设置了邮箱就发送邮件
        if (StringUtils.isNotBlank(admin.getEmail())) {
            MailServer mailServer = mailServerService.getDefaultMailServer();
            if (mailServer == null) {
                throw new InvalidParamterException("MailServerNotExist");
            }
            String link = Constants.SERVICE_URL + "login";
            admin.setPassword(randomPassword);
            sendEmail(admin, link);
        }
    }

    private EnterpriseAccount createEnterpriseAccount(RestEnterpriseAccountRequest request, Enterprise enterprise, AuthApp authApp) {
        EnterpriseHttpClient enterpriseHttpClient = new EnterpriseHttpClient(ufmClientService);
        RestEnterpriseAccountRequest accountRequest = new RestEnterpriseAccountRequest();
        accountRequest.setDomain(enterprise.getDomainName());
        accountRequest.setEnterpriseId(enterprise.getId());
        accountRequest.setMaxMember(request.getMaxMember());
        accountRequest.setMaxSpace(request.getMaxSpace());
        accountRequest.setMaxTeamspace(request.getMaxTeamspace());
        accountRequest.setStatus(Enterprise.STATUS_ENABLE_STR);

        RestEnterpriseAccountResponse response = enterpriseHttpClient.getAccountInfo(authApp, accountRequest);
        if (response == null) {
            throw new InvalidParameterException();
        }
        if (!"enable".equals(response.getStatus())) {
            throw new BusinessException("BMS:bind app to enterprise failed");
        }
        EnterpriseAccount enterpriseAccount = transAccount(request, response, authApp.getAuthAppId());

        if (Enterprise.STATUS_DISABLE_STR.equalsIgnoreCase(response.getStatus())) {
            enterpriseAccount.setStatus(Enterprise.STATUS_ENABLE);
        }

        enterpriseAccountService.create(enterpriseAccount);
        return enterpriseAccount;
    }

    // Step 3 绑定认证方式
    private void bindAuthServer(Enterprise enterprise, long authServerId, List<EnterpriseAccount> bindApplications, String authServerName, HttpServletRequest req) {
        for (EnterpriseAccount enterpriseAccount : bindApplications) {
            LogOwner owner = new LogOwner();
            owner.setEnterpriseId(enterprise.getId());
            owner.setIp(IpUtils.getClientAddress(req));
            owner.setAppId(enterpriseAccount.getAuthAppId());
            owner.setLoginName(enterprise.getName());
            String typeDetail = AdminLogType.KEY_BINDAPP_OPENACCOUNT_OPEN.getDetails();
            String[] description = new String[] { enterprise.getName(), authServerName, typeDetail, enterpriseAccount.getAuthAppId() };
            accountAuthserverManager.bindApp(authServerId, enterpriseAccount.getAccountId(), AccountAuthserver.UNDEFINED_OPEN_ACCOUNT);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_BIND_APP, description);
        }

    }

    // Step 4 创建员工
    public EnterpriseUser createEnterpriseUser(HttpServletRequest request, Enterprise enterprise, AuthServer authServer, String userName,String loginName) throws IOException {
        EnterpriseUser enterpriseUser = new EnterpriseUser();
        enterpriseUser.setEnterpriseId(enterprise.getId());
        enterpriseUser.setName(userName);
        enterpriseUser.setStatus(EnterpriseUser.STATUS_ENABLE);
        enterpriseUser.setAlias(enterprise.getContactPerson());
        enterpriseUser.setEmail(enterprise.getContactEmail());
        enterpriseUser.setMobile(enterprise.getContactPhone());
        enterpriseUser.setType(EnterpriseUser.TYPE_MEMBER);
        // 为Enterprise生成对应的EnterpriseUser信息，不需要创建登录信息。
        return createEnterpriseUser(request, enterprise, enterpriseUser, authServer, false,loginName);
    }

    // Step 5 开户
    private void openEnterpriseUser(HttpServletRequest request, Enterprise enterprise, AuthServer authServer, EnterpriseUser enterpriseUser,String loginName) {
        List<String> appIds = authAppService.getAppId();
        String[] description = new String[] { enterprise.getName(), authServer.getName(), enterpriseUser.getName(), appIds.toString(), null, null };
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterprise.getId());
        owner.setIp(IpUtils.getClientAddress(request));
        owner.setAppId(appIds.toString());
        try {
            for (String appId : appIds) {
                userAccountManager.create(enterpriseUser.getId(), enterprise.getId(), appId,loginName);
            }
        } catch (RuntimeException e) {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_OPEN_ACCOUNT_ERROR, description);
            throw new InternalServerErrorException(e);
        }
        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_OPEN_ACCOUNT, description);
    }

    /**
     * 生成EnterpriseUser信息。
     * @throws IOException
     */
    public EnterpriseUser createEnterpriseUser(HttpServletRequest request, Enterprise enterprise, EnterpriseUser enterpriseUser, AuthServer authServer, boolean createLoginInfo,String loginName)
        throws IOException {
        String[] description = new String[] { enterprise.getName(), authServer.getName(), enterpriseUser.getName(), enterpriseUser.getAlias(), enterpriseUser.getEmail(),
                enterpriseUser.getMobile(), enterpriseUser.getDescription() };

        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(enterprise.getId());
        owner.setIp(IpUtils.getClientAddress(request));

        Set violations = validator.validate(enterpriseUser);
        if (!violations.isEmpty()) {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new ConstraintViolationException(violations);
        }

        if (!StringUtils.equals(AuthServer.AUTH_TYPE_LOCAL, authServer.getType())) {
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD_ERROR, description);
            throw new InvalidParamterException("wrong authServerType:" + authServer.getType());
        }
        enterpriseUser.setUserSource(authServer.getId());

        long userId = enterpriseUserManager.createLocal(enterpriseUser, createLoginInfo);

        //新建用户自动开通默认应用
        AuthApp app = authAppService.getDefaultWebApp();

        userAccountManager.create(userId, enterpriseUser.getEnterpriseId(), app.getAuthAppId(),loginName);

        adminLogManager.saveAdminLog(owner, AdminLogType.KEY_EMPLOYEES_ADD, description);
        return enterpriseUser;
    }

    private EnterpriseAccount transAccount(RestEnterpriseAccountRequest request, RestEnterpriseAccountResponse response, String authAppId) {
        EnterpriseAccount enterpriseAccount = new EnterpriseAccount();
        enterpriseAccount.setAccessKeyId(response.getAccessToken().getAccessKey());
        enterpriseAccount.setSecretKey(response.getAccessToken().getSecretKey());
        encode(enterpriseAccount);
        enterpriseAccount.setAccountId(response.getId());
        enterpriseAccount.setAuthAppId(authAppId);
        enterpriseAccount.setEnterpriseId(request.getEnterpriseId());
        enterpriseAccount.setMaxSpace(request.getMaxSpace());
        enterpriseAccount.setMaxMember(request.getMaxMember());
        enterpriseAccount.setMaxFiles(-1);
        enterpriseAccount.setMaxTeamspace(request.getMaxTeamspace());
        transNum(enterpriseAccount);
        Date date = new Date();
        enterpriseAccount.setCreatedAt(date);
        enterpriseAccount.setModifiedAt(date);
        return enterpriseAccount;
    }

    private void transNum(EnterpriseAccount enterpriseAccount) {
        if (enterpriseAccount.getMaxFiles() == UNLIMIT_NUM) {
            enterpriseAccount.setMaxFiles(UNLIMIT_NUM_RESTORE);
        }

        if (enterpriseAccount.getMaxSpace() == UNLIMIT_NUM) {
            enterpriseAccount.setMaxSpace(UNLIMIT_NUM_RESTORE_SPACE);
        } else {
            enterpriseAccount.setMaxSpace(enterpriseAccount.getMaxSpace() * 1024);
        }

        if (enterpriseAccount.getMaxMember() == UNLIMIT_NUM) {
            enterpriseAccount.setMaxMember(UNLIMIT_NUM_RESTORE);
        }
        if (enterpriseAccount.getMaxTeamspace() == UNLIMIT_NUM) {
            enterpriseAccount.setMaxTeamspace(UNLIMIT_NUM_RESTORE);
        }
    }

    private static EnterpriseAccount encode(EnterpriseAccount enterpriseAccount) {
        if (enterpriseAccount == null) {
            return enterpriseAccount;
        }

        String secretKey = enterpriseAccount.getSecretKey();
        String key = enterpriseAccount.getSecretKeyEncodeKey();
        if (StringUtils.isBlank(secretKey)) {
            enterpriseAccount.setSecretKey("");
        } else if (StringUtils.isBlank(key)) {
            Map<String, String> map = EDToolsEnhance.encode(secretKey);
            enterpriseAccount.setSecretKey(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            enterpriseAccount.setSecretKeyEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
        } else {
            enterpriseAccount.setSecretKey(EDToolsEnhance.encode(secretKey, key));
        }
        return enterpriseAccount;
    }

    private void sendEmail(Admin admin, String link) throws IOException {
        MailServer mailServer = mailServerService.getDefaultMailServer();
        if (mailServer == null) {
            throw new BusinessException();
        }
        Map<String, Object> messageModel = new HashMap<String, Object>(3);
        messageModel.put("username", admin.getName());
        messageModel.put("loginName", admin.getLoginName());
        messageModel.put("password", admin.getPassword());
        messageModel.put("link", link);
        String msg = mailServerService.getEmailMsgByTemplate(Constants.INITSET_PWD_MAIL_CONTENT, messageModel);
        String subject = mailServerService.getEmailMsgByTemplate(Constants.INITSET_PWD_MAIL_SUBJECT, new HashMap<String, Object>(1));
        mailServerService.sendHtmlMail(admin.getName(), mailServer.getId(), admin.getEmail(), null, null, subject, msg);
    }

    private String saveFailLog(HttpServletRequest request, OperateType operateType, OperateDescription operateDescription, String[] paramsType, String[] paramsDescription,
        String name, String phone) {
        SystemLog log = getSystemLog(request, name, phone);
        return systemLogManager.saveSystemLog(log, operateType, operateDescription, paramsType, paramsDescription);
    }

    private static SystemLog getSystemLog(HttpServletRequest request, String name, String phone) {
        SystemLog systemLog = new SystemLog();
        try {
            systemLog.setClientAddress(IpUtils.getClientAddress(request));
            systemLog.setClientDeviceName(EnvironmentUtils.getHostName());
            systemLog.setLoginName("anon");
            systemLog.setShowName((null == name ? "" : name) + "(" + (null == phone ? "" : phone) + ")");
        } catch (RuntimeException e) {
            if (null == systemLog.getLoginName()) {
                systemLog.setLoginName("-");
                systemLog.setShowName("-");
            }
            return systemLog;
        }
        return systemLog;
    }

    protected void saveValidateLog(HttpServletRequest request, OperateType operateType) {
        saveValidateLog(request, operateType, null, null);
    }

    protected void saveValidateLog(HttpServletRequest request, OperateType operateType, String name, String phone) {
        SystemLog systemLog = getSystemLog(request, name, phone);
        systemLogManager.saveSystemLog(systemLog, operateType, OperateDescription.CHECKCONFIG, null, null);
    }

    protected static void checkMaxSpace(long maxSpace) {
        if (maxSpace != LIMITLESS && maxSpace < MINVALUE) {
            throw new InvalidParamterException("maxSpace<" + MINVALUE);
        }
        if (maxSpace > MAXVALUE) {
            throw new InvalidParamterException("maxSpace>" + MAXVALUE);
        }
    }

    protected static void checkMaxTeamspace(int maxTeamspace) {
        if (maxTeamspace != LIMITLESS && maxTeamspace < MINVALUE) {
            throw new InvalidParamterException("maxTeamspace<" + MINVALUE);
        }
        if (maxTeamspace > MAXVALUE) {
            throw new InvalidParamterException("maxTeamspace>" + MAXVALUE);
        }
    }

    /**
     *
     */
    protected static void checkMaxMember(int maxMember) {
        if (maxMember != LIMITLESS && maxMember < MINVALUE) {
            throw new InvalidParamterException("maxMember<" + MINVALUE);
        }
        if (maxMember > MAXVALUE) {
            throw new InvalidParamterException("maxMember>" + MAXVALUE);
        }
    }

    protected static void checkBindAccountParam(int maxMember, int maxTeamspace, long maxSpace) {
        checkMaxMember(maxMember);
        checkMaxSpace(maxSpace);
        checkMaxTeamspace(maxTeamspace);
    }
}

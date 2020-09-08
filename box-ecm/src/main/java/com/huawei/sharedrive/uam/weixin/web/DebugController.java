package com.huawei.sharedrive.uam.weixin.web;

import ch.qos.logback.classic.Level;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.exception.BusinessException;
import com.huawei.sharedrive.uam.httpclient.rest.UserHttpClient;
import com.huawei.sharedrive.uam.system.service.AppBasicConfigService;
import com.huawei.sharedrive.uam.teamspace.domain.*;
import com.huawei.sharedrive.uam.teamspace.service.TeamSpaceService;
import com.huawei.sharedrive.uam.user.service.UserNotifyService;
import com.huawei.sharedrive.uam.util.PasswordGenerateUtil;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
import com.huawei.sharedrive.uam.weixin.domain.WxEnterprise;
import com.huawei.sharedrive.uam.weixin.rest.CorpAuthInfo;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxWorkOauth2SuiteProxy;
import com.huawei.sharedrive.uam.weixin.service.WxDepartmentService;
import com.huawei.sharedrive.uam.weixin.service.WxEnterpriseService;
import com.huawei.sharedrive.uam.weixin.service.WxWorkCorpAppService;
import com.huawei.sharedrive.uam.weixin.service.task.SyncAddressListTask;
import com.huawei.sharedrive.uam.weixin.service.task.SyncEnterpriseAccountTask;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.common.domain.AppBasicConfig;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.BundleUtil;
import pw.cdmi.core.utils.HashPasswordUtil;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping(value = "/wxEvent/debug")
public class DebugController extends AbstractCommonController {
    private static Logger logger = LoggerFactory.getLogger(DebugController.class);

    @Autowired
    private WxWorkOauth2SuiteProxy wxOauth2SuiteProxy;

    @Autowired
    private WxEnterpriseService wxEnterpriseService;

    @Autowired
    WxDepartmentService wxDepartmentService;

    @Autowired
    WxWorkCorpAppService wxWorkCorpAppService;

    @Autowired
    UserNotifyService userNotifyService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private EnterpriseAccountService enterpriseAccountService;

    @Autowired
    private AppBasicConfigService appBasicConfigService;

    @Autowired
    private TeamSpaceService teamSpaceService;

    @Autowired
    private RestClient ufmClientService;

    private String password;

    public DebugController() {
        password = "" + new Random(System.currentTimeMillis()).nextInt();
        System.out.println("The password of DebugController: " + password);
    }

    @RequestMapping(value = "/{password}", method = RequestMethod.GET)
    public String init(Model model, @PathVariable String password) {
        //检查随机数，防止他人误进入
        if (!this.password.equals(password)) {
//            throw new RuntimeException();
        }

        return "enterprise/admin/debug";
    }

    @RequestMapping(value = "/syncEnterpriseAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> syncEnterpriseAccount(@RequestParam String corpId) {
        try {
            WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
            SyncEnterpriseAccountTask task = SpringContextUtil.getBean(SyncEnterpriseAccountTask.class);
            task.setWxEnterprise(wxEnterprise);

            new Thread(task).start();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to open syncEnterpriseAccount=" + corpId, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("任务已经启动。", HttpStatus.OK);
    }

    @RequestMapping(value = "/syncAddressList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> syncAddressList(@RequestParam String corpId) {
        String result = null;
        try {
            WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);

            if (wxEnterprise == null) {
                return new ResponseEntity<>("No such WxEnterprise: " + corpId, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String code = wxOauth2SuiteProxy.getCorpPermanentCode(corpId);
            if (StringUtils.isBlank(code)) {
                return new ResponseEntity<>("No permanent code found of WxEnterprise: " + corpId, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String suiteToken = wxOauth2SuiteProxy.getSuiteToken();
            if (StringUtils.isBlank(suiteToken)) {
                return new ResponseEntity<>("No access code found of WxEnterprise: " + corpId, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            CorpAuthInfo corpAuthInfo = wxOauth2SuiteProxy.getAuthInfo(suiteToken, corpId, code);
            if (corpAuthInfo == null) {
                return new ResponseEntity<>("Get null auth_info of WxEnterprise: " + corpId, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (corpAuthInfo.hasError()) {
                return new ResponseEntity<>("Failed to get auth_info of WxEnterprise: " + corpId + ", error=" + corpAuthInfo.getErrmsg(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            SyncAddressListTask task = SpringContextUtil.getBean(SyncAddressListTask.class);
            task.setWxEnterprise(wxEnterprise);
            task.setAuthInfo(corpAuthInfo.getAuthInfo());

            new Thread(task).start();
            return new ResponseEntity<>("任务已经启动。", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Failed to open syncAddressList=" + corpId, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestParam(value = "corpId", defaultValue = "wwf6c2440675462456") String corpId,
            @RequestParam(value = "userId", defaultValue = "") String userId) {
        WxEnterprise wxEnterprise = wxEnterpriseService.get(corpId);
        if (wxEnterprise == null) {
            return new ResponseEntity<>("没有该企业的信息：corpId=" + corpId, HttpStatus.OK);
        }

        StringBuilder sb = new StringBuilder();

        try {
            long enterpriseId = wxEnterprise.getBoxEnterpriseId();

            EnterpriseUser user = enterpriseUserService.getByEnterpriseIdAndName(enterpriseId, userId);

            long enterpriseUserId = user.getId();

            String password = PasswordGenerateUtil.getRandomPassword(6);
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(password);

            user.setPassword(hashPassword.getHashPassword());
            user.setIterations(hashPassword.getIterations());
            user.setSalt(hashPassword.getSalt());
            enterpriseUserService.update(user);

            String message = "您好，你的微信文件宝已经开通：<br>你的账号如下：<br>企业名称：" + wxEnterprise.getName() + "<br>用户名：" + user.getName() + "<br> 密码: " + password +
                    "<br>您可以在微信小程序中搜索“企业云盘”，进入小程序进行绑定。绑定后你可以访问<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。通过微信扫描登陆管理您的知识文档，与同事进行工作协同。您也可以登陆<a href=\"http://www.jmapi.cn\">www.jmapi.cn</a>。首次登陆通过企业微信扫描登陆，然后在设置->用户信息中绑定您的微信号码。";

            userNotifyService.sendMessage(enterpriseId, enterpriseUserId, message);
        } catch (Exception e) {
            sb.append("修改密码错误：userId=").append(userId).append("<br>");
            e.printStackTrace();
        }

        if (sb.length() == 0) {
            sb.append("修改成功。");
        }

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/syncEnterpriseTeamspace", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> syncEnterpriseTeamspace(@RequestParam(value = "enterpriseId", defaultValue = "1") Long enterpriseId,
                                                     @RequestParam(value = "appId", defaultValue = "StorBox") String appId) {
        StringBuilder sb = new StringBuilder();
        try {
            BundleUtil.addBundle("messages", new Locale[]{Locale.ENGLISH, Locale.CHINESE});
            Locale locale;
            String defaultLang = PropertiesUtils.getProperty("default.system.lang");
            if (defaultLang.equals("cn")) {
                locale = Locale.CHINESE;
            } else {
                locale = Locale.ENGLISH;
            }

            EnterpriseAccount enterpriseAccount = enterpriseAccountService.getByEnterpriseApp(enterpriseId, appId);
            if(enterpriseAccount != null) {
            	  String inbox = BundleUtil.getText("messages", locale, "enterprise.teamspace.inbox");
                  createDefaultTeamSpace(enterpriseAccount.getEnterpriseId(), enterpriseAccount.getAuthAppId(),
                          TeamSpace.TYPE_RECEIVE_FOLDER, inbox, "editor");

                //收件箱
//                List<RestTeamSpaceInfo> spaceList = getAllTeamSpace(enterpriseAccount, TeamSpace.TYPE_RECEIVE_FOLDER);
//                if(spaceList.isEmpty()) {
//                    String inbox = BundleUtil.getText("messages", locale, "enterprise.teamspace.inbox");
//                    createDefaultTeamSpace(enterpriseAccount.getEnterpriseId(), enterpriseAccount.getAuthAppId(),
//                            TeamSpace.TYPE_RECEIVE_FOLDER, inbox, "editor");
//
//                }

                //企业文库
                  String archivestore = BundleUtil.getText("messages", locale, "enterprise.teamspace.archivestore");
                  createDefaultTeamSpace(enterpriseAccount.getEnterpriseId(), enterpriseAccount.getAuthAppId(), TeamSpace.TYPE_ARCHIVE_STORE, archivestore, "uploadAndView");
//                spaceList = getAllTeamSpace(enterpriseAccount, TeamSpace.TYPE_ARCHIVE_STORE);
//                if(spaceList.isEmpty()) {
//                    String archivestore = BundleUtil.getText("messages", locale, "enterprise.teamspace.archivestore");
//                    createDefaultTeamSpace(enterpriseAccount.getEnterpriseId(), enterpriseAccount.getAuthAppId(), TeamSpace.TYPE_ARCHIVE_STORE, archivestore, "uploadAndView");
//                }
            } else {
                sb.append("企业账号不存在：enterpriseId=").append(enterpriseId).append(", appId=").append(appId);
            }

        } catch (Exception e) {
            sb.append(e);
        }

        if (sb.length() == 0) {
            sb.append("修改成功。");
        }

        return new ResponseEntity<>(sb.toString(), HttpStatus.OK);
    }

    private List<RestTeamSpaceInfo> getAllTeamSpace(EnterpriseAccount enterpriseAccount, int type) {
        Map<String, String> headerMap = UserHttpClient.assembleAccountToken(enterpriseAccount);

        ListAllTeamSpaceRequest request = new ListAllTeamSpaceRequest(1000, 0L);
        request.setType(type);
        TextResponse response = ufmClientService.performJsonPostTextResponse("/api/v2/teamspaces/all", headerMap, request);

        String content = response.getResponseBody();
        if (response.getStatusCode() == HttpStatus.OK.value()) {
            return JsonUtils.stringToObject(content, RestAllTeamSpaceList.class).getTeamSpaces();
        }

        throw new BusinessException(content);
    }

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
        List<RestTeamMember> memberlist=new ArrayList<RestTeamMember>();
        memberlist.add(restTeamMember);
        RestTeamMemberCreateRequest memberRequest = new RestTeamMemberCreateRequest();
        memberRequest.setMemberList(memberlist);
        memberRequest.setTeamRole(RestTeamMemberCreateRequest.ROLE_MEMBER);
        memberRequest.setRole(role);

        teamSpaceService.addTeamSpaceMember(enterpriseId, appId, restInfo.getId(), memberRequest);
    }

    @RequestMapping(value = "/setLoggerLevel", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> setLoggerLevel(@RequestParam String clazz, @RequestParam(defaultValue = "WARN") String level) {
        String result;
        try {
            Logger logger = LoggerFactory.getLogger(clazz);

            if (logger != null) {
                if (logger instanceof ch.qos.logback.classic.Logger) {
                    ((ch.qos.logback.classic.Logger) logger).setLevel(Level.toLevel(level));
                    result = "Set successfully.";
                } else {
                    result = "Not support logger: name=" + logger.getName() + ", class=" + logger.getClass().getName();
                }
            } else {
                result = "No logger found, name=" + clazz;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "Failed to set logger level: name=" + clazz + ", level=" + level;
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

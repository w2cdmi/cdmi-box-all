package com.huawei.sharedrive.isystem.initConfig.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.wcc.crypt.Crypter;
import org.wcc.crypt.CrypterFactory;

import com.huawei.sharedrive.common.license.CseLicenseException;
import com.huawei.sharedrive.common.license.CseLicenseInfo;
import com.huawei.sharedrive.common.license.LicenseNode;
import com.huawei.sharedrive.isystem.authapp.service.AppAccessKeyService;
import com.huawei.sharedrive.isystem.authapp.service.AuthAppService;
import com.huawei.sharedrive.isystem.cluster.FileSystemConstant;
import com.huawei.sharedrive.isystem.cluster.domain.DataCenter;
import com.huawei.sharedrive.isystem.cluster.domain.Region;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.NasStorage;
import com.huawei.sharedrive.isystem.cluster.domain.filesystem.UdsStorage;
import com.huawei.sharedrive.isystem.cluster.service.DCService;
import com.huawei.sharedrive.isystem.cluster.service.RegionService;
import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.httpclient.BMSRestClient;
import com.huawei.sharedrive.isystem.httpclient.UAMRestClient;
import com.huawei.sharedrive.isystem.httpclient.bmsRest.BmsConfigRestClient;
import com.huawei.sharedrive.isystem.httpclient.uamRest.UamConfigRestClient;
import com.huawei.sharedrive.isystem.initConfig.domain.AccessAddressConfig;
import com.huawei.sharedrive.isystem.initConfig.domain.AdminAccount;
import com.huawei.sharedrive.isystem.initConfig.domain.BmsUser;
import com.huawei.sharedrive.isystem.initConfig.domain.ConfigStep;
import com.huawei.sharedrive.isystem.initConfig.domain.Enterprise;
import com.huawei.sharedrive.isystem.initConfig.domain.StorageConfig;
import com.huawei.sharedrive.isystem.license.LicenseCompareException;
import com.huawei.sharedrive.isystem.license.service.LicenseService;
import com.huawei.sharedrive.isystem.license.web.LicenseImportResult;
import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.system.service.MailServerService;
import com.huawei.sharedrive.isystem.system.service.PwdConfuser;
import com.huawei.sharedrive.isystem.thrift.client.DCManageServiceClient;
import com.huawei.sharedrive.isystem.thrift.client.StorageResourceServiceClient;
import com.huawei.sharedrive.isystem.user.domain.Admin;
import com.huawei.sharedrive.isystem.user.domain.AdminRole;
import com.huawei.sharedrive.isystem.user.service.AdminService;
import com.huawei.sharedrive.isystem.user.service.AdminUpdateService;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;
import com.huawei.sharedrive.isystem.util.PasswordGenerateUtil;
import com.huawei.sharedrive.isystem.util.PropertiesUtils;
import com.huawei.sharedrive.thrift.app2isystem.ResourceGroupCreateInfo;
import com.huawei.sharedrive.thrift.filesystem.StorageInfo;

import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.domain.MailServer;
import pw.cdmi.common.log.UserLog;
import pw.cdmi.common.thrift.client.ThriftClientProxyFactory;
import pw.cdmi.core.encrypt.HashPassword;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.HashPasswordUtil;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/systeminit")
public class SystemInitConfigController extends AbstractCommonController {

    private static Logger logger = LoggerFactory.getLogger(SystemInitConfigController.class);

    /**
     * 初次设置密码消息体
     */
    private static final String INITSET_PWD_MAIL_CONTENT = "initSetPasswordContent.ftl";

    /**
     * 初次设置密码主题
     */
    private static final String INITSET_PWD_MAIL_SUBJECT = "initSetPasswordSubject.ftl";

    public static final int MAX_SIZE = 1024 * 1024;

    public static final String LICENSE_DAT = "dat";

    public static final String LICENSE_XML = "xml";

    @Value("${resourceGroup.managePort}")
    private int managePort;

    @Autowired
    private RegionService regionService;

    @Autowired
    private DCService dcService;

    @Autowired
    private ThriftClientProxyFactory ufmThriftClientProxyFactory;

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private BMSRestClient bmsRestClient;

    @Autowired
    private UAMRestClient uamRestClient;

    @Autowired
    private MailServerService mailService;

    @Autowired
    private AppAccessKeyService appAccessKeyService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private AdminUpdateService adminUpdateService;

    private UamConfigRestClient uamConfigRestClient;

    private BmsConfigRestClient bmsConfigRestClient;

    @Value("${isystem.app.authurl}")
    private String authUrl;

    @Value("${isystem.app.appid}")
    private String appId;

    @PostConstruct
    void init() {
        this.bmsConfigRestClient = new BmsConfigRestClient(bmsRestClient);
        this.uamConfigRestClient = new UamConfigRestClient(uamRestClient);
    }

    private static final String DEFAULT_PROTOCOL = "https";

    @RequestMapping(value = "/isystem/admin/config", method = RequestMethod.GET)
    public String enterIsystemAdminConfig(HttpServletRequest request, Model model) {
        AdminAccount adminAccount = new AdminAccount();
        List<Admin> adminList = adminService.getFilterd(null, null, null);
        for (Admin admin : adminList) {
            if (!admin.getRoleNames().contains("ADMIN_MANAGER")) {
                adminAccount.setLoginName(admin.getLoginName());
                adminAccount.setName(admin.getName());
                adminAccount.setEmail(admin.getEmail());
                break;
            }
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("employeeUserAdd")) {
                adminAccount.setIsConfigEnterpriseUser((byte) 1);
            }
        }

        model.addAttribute("account", adminAccount);

        return "anon/createIsystemAdminUser";
    }

    @RequestMapping(value = "/config/step", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ConfigStep> getConfigStep(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        ConfigStep configStep = new ConfigStep();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("enterpriseConfig")) {
                configStep.setEnterpriseConfig(cookie.getValue());
            } else if (cookie.getName().equals("storageConfig")) {
                configStep.setStorageConfig(cookie.getValue());
            } else if (cookie.getName().equals("accessConfig")) {
                configStep.setAccessConfig(cookie.getValue());
            } else if (cookie.getName().equals("mailConfig")) {
                configStep.setMailConfig(cookie.getValue());
            }
        }

        // Cookie中没有检查企业配置
        if ("0".equals(configStep.getEnterpriseConfig()) || StringUtils.isBlank(configStep.getEnterpriseConfig())) {
            Enterprise enterprise = bmsConfigRestClient.findEnterpriseByOwnerId(-1);
            if (enterprise != null) {
                Cookie enterpriseConfigCookie = new Cookie("enterpriseConfig", "1");
                response.addCookie(enterpriseConfigCookie);
            }
        } 
        if ("0".equals(configStep.getStorageConfig()) || StringUtils.isBlank(configStep.getStorageConfig())) {
            Region region = regionService.getDefaultRegion();
            if (region != null) {
                Cookie storageConfigCookie = new Cookie("storageConfig", "1");
                response.addCookie(storageConfigCookie);
            }
        } 
        if ("0".equals(configStep.getAccessConfig()) || StringUtils.isBlank(configStep.getAccessConfig())) {
            AccessAddressConfig accessAddressConfig = bmsConfigRestClient.getAccessAddress();
            if (accessAddressConfig != null) {
                Cookie accessAddressCookie = new Cookie("accessConfig", "1");
                response.addCookie(accessAddressCookie);
            }
        } 
        if ("0".equals(configStep.getMailConfig()) || StringUtils.isBlank(configStep.getMailConfig())) {
            MailServer mailServer = mailService.getMailServer();
            if (mailServer != null) {
                Cookie mailConfigCookie = new Cookie("accessConfig", "1");
                response.addCookie(mailConfigCookie);
            }
        }

        return new ResponseEntity<ConfigStep>(configStep, HttpStatus.OK);
    }

    @RequestMapping(value = "/storage/config/", method = RequestMethod.GET)
    public String enterStorageConfig() {
        return "anon/isystemDCConfig";
    }

    @RequestMapping(value = "/storage/config", method = RequestMethod.GET)
    public String enterStorageConfig(Model model, HttpServletRequest request) {
        Region region = regionService.getDefaultRegion();

        if (region != null) {
            DataCenter dataCenter = dcService.getSingleDataCenter(region.getId());
            if (dataCenter != null) {
                StorageConfig sConfig = new StorageConfig();

                sConfig.setRegionName(region.getCode());
                sConfig.setRegionId(String.valueOf(region.getId()));

                sConfig.setDcName(dataCenter.getName());
                sConfig.setManageIp(dataCenter.getResourceGroup().getManageIp());
                sConfig.setDcId(String.valueOf(dataCenter.getId()));

                List<NasStorage> nasStorages = getNasStorages(dataCenter.getId());
                if (nasStorages.size() > 0) {
                    sConfig.setPath(nasStorages.get(0).getPath());// 只获取第一个
                }
                model.addAttribute("region", sConfig);
                List<UdsStorage> udsStorages = getUdsStorages(dataCenter.getId());
                if (udsStorages.size() > 0) {
                    sConfig.setDomain(udsStorages.get(0).getDomain());
                    sConfig.setHttpPort(udsStorages.get(0).getPort());
                    sConfig.setHttpsPort(udsStorages.get(0).getHttpsport());
                    sConfig.setAccessKey(udsStorages.get(0).getAccessKey());
                    sConfig.setSecretKey(udsStorages.get(0).getSecretKey());
                    sConfig.setProvider(udsStorages.get(0).getProvider());
                }
            }
        }
        return "anon/isystemDCConfig";
    }

    private List<NasStorage> getNasStorages(int dcId) {
        try {
            // 获取当前存储资源
            List<StorageInfo> storageResources = ufmThriftClientProxyFactory
                .getProxy(StorageResourceServiceClient.class).getAllStorageResource(dcId);
            if (null == storageResources) {
                throw new BusinessException();
            }
            List<NasStorage> cluser = new ArrayList<NasStorage>();
            for (StorageInfo storageInfo : storageResources) {
                if (FileSystemConstant.FILE_SYSTEM_NAS.equals(storageInfo.getFsType())) {
                    NasStorage storage = new NasStorage(storageInfo);
                    cluser.add(storage);
                }
            }
            return cluser;
        } catch (TException e) {
            throw new BusinessException(e);
        }
    }

    private List<UdsStorage> getUdsStorages(int dcId) {
        try {
            // 获取当前存储资源
            List<StorageInfo> storageResources = ufmThriftClientProxyFactory
                .getProxy(StorageResourceServiceClient.class).getAllStorageResource(dcId);
            if (null == storageResources) {
                throw new BusinessException();
            }

            List<UdsStorage> cluser = new ArrayList<UdsStorage>();
            for (StorageInfo storageInfo : storageResources) {
                if (!FileSystemConstant.FILE_SYSTEM_NAS.equals(storageInfo.getFsType())) {
                    UdsStorage storage = new UdsStorage(storageInfo);
                    String endpoint = storageInfo.getEndpoint();
                    String[] endpoint_slices = endpoint.split(";");
                    if (endpoint_slices.length > 5) {
                        storage.setProvider(endpoint_slices[5]);
                    }
                    cluser.add(storage);
                }
            }
            return cluser;
        } catch (TException e) {
            throw new BusinessException(e);
        }
    }

    @RequestMapping(value = "/accessAddress/config", method = RequestMethod.GET)
    public String enterAccessAddressConfig(Model model) {
        AccessAddressConfig accessAddressConfig = bmsConfigRestClient.getAccessAddress();
        model.addAttribute("accessAddress", accessAddressConfig);
        return "anon/bmsAccessConfig";
    }

    @RequestMapping(value = "/bmsUser/config", method = RequestMethod.GET)
    public String enterBmsUserConfig() {
        return "anon/createBMSAdminUser";
    }

    @RequestMapping(value = "/enterprise/config", method = RequestMethod.GET)
    public String enterEnterpriseConfig(Model model, HttpServletRequest request, HttpServletResponse response) {
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        adminUpdateService.updateStatus((byte) 2, admin.getId());

        long enterpriseId = -1;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("enterpriseId")) {
                enterpriseId = Long.parseLong(cookie.getValue());
            }
        }
        if (enterpriseId != -1) {
            Enterprise enterprise = bmsConfigRestClient.findEnterpriseById(enterpriseId);
            model.addAttribute("enterprise", enterprise);
        } else {
            Enterprise enterprise = bmsConfigRestClient.findEnterpriseByOwnerId(-1);
            model.addAttribute("enterprise", enterprise);
        }

        return "anon/createBmsEnterprise";
    }

    @RequestMapping(value = "/mail/config", method = RequestMethod.GET)
    public String enterMailConfig(Model model) {
        MailServer mailServer = mailService.getMailServer();
        model.addAttribute("mailServer", mailServer);
        return "anon/createMailServer";
    }

    @RequestMapping(value = "/license/config", method = RequestMethod.GET)
    public String enterLicenseConfig() {
        // model.addAttribute("enterpriseId", id);
        return "anon/isystemLicenseConfig";
    }

    @RequestMapping(value = "/isystem/config/over", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> configOver(HttpServletRequest request, HttpServletResponse response) {
        SecurityUtils.getSubject().getSession().setAttribute("isInitPwd", true);
        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        adminUpdateService.updateStatus((byte) 1, admin.getId());
        adminUpdateService.updateLastLoginTime(admin.getId());
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @RequestMapping(value = "/all/admin", method = RequestMethod.GET)
    public String list(Model model, HttpServletRequest request) {
        List<AdminAccount> adminAccounts = new ArrayList<AdminAccount>();
        List<Admin> adminList = adminService.getFilterd(null, null, null);
        for (Admin admin : adminList) {
            AdminAccount account = new AdminAccount();
            account.setLoginName(admin.getLoginName());
            account.setName(admin.getName());
            adminAccounts.add(account);
        }

        adminAccounts.addAll(bmsConfigRestClient.getAllBMSAdmin());

        model.addAttribute("adminList", adminAccounts);
        return "anon/allAdminList";
    }

    private void delRegion(int regionId) {
        regionService.deleteRegion(regionId);
    }

    private void deleteResourceGroup(int dcId) {
        try {
            ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class).deleteResourceGroup(dcId);
        } catch (TException e) {
            logger.error("system init config delet resourceGrou:" + e.getMessage());
        }
    }

    private void delStorageResource(int dcId, String storageResId) {
        try {
            ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).deleteStorageResource(dcId,
                storageResId);
        } catch (TException e) {
            logger.error("system init config delet storage resource:" + e.getMessage());
        }
    }

    private void disableStorageResource(int dcId, String storageResId) {
        try {
            ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).disableStorageResource(dcId,
                storageResId);
        } catch (TException e) {
            logger.error("system init config disable storage resource:" + e.getMessage());
        }
    }

    @RequestMapping(value = "/cloudstorage/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveCloudStorageConfig(StorageConfig storageConfig, String token,
        HttpServletRequest request, HttpServletResponse response) throws TException {

        if (!FormValidateUtil.isValidIPv4(storageConfig.getManageIp())) {
            throw new ConstraintViolationException("ip is invaid", null);
        }

        // add Region
        Region region = null;
        String region_name = StringUtils.isBlank(storageConfig.getRegionName()) ? "default"
                : storageConfig.getRegionName();
        if (StringUtils.isNotBlank(region_name)) {
            region = regionService.getRegionByCode(region_name);
        }
        if (region == null) { // 默认的DC不存在，添加；存在不做处理
            regionService.addRegion("Default", "default", "");
        } else if (StringUtils.isNotBlank(storageConfig.getDcId())) {  // 已存在，DataCenter也存在
            DataCenter dataCenter = dcService.getDataCenter(Integer.valueOf(storageConfig.getDcId()));
            if (dataCenter != null) {
                try {   // 先删除，再新增
                    ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class)
                        .deleteResourceGroup(dataCenter.getId());
                } catch (Exception e) {
                    logger.error("system init config add resource group:" + e.getMessage());
                    return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
                }
            } else {// DataCenter不存在

            }
        }

        // add DC
        List<Region> regionList = regionService.listRegion();
        for (Region reg : regionList) {
            if (reg.getCode().equals(region_name)) {
                region = reg;
            }
        }
        if (region == null) {
            throw new RuntimeException("region not save");
        }

        ResourceGroupCreateInfo createInfo = new ResourceGroupCreateInfo();
        String dcname = StringUtils.isBlank(storageConfig.getDcName()) ? "default" : storageConfig.getDcName();
        createInfo.setName(dcname);
        createInfo.setManagerIp(storageConfig.getManageIp());
        createInfo.setManagerPort(managePort);
        createInfo.setRegionId(region.getId());
        createInfo.setDomainName("");
        createInfo.setGetProtocol(DEFAULT_PROTOCOL);
        createInfo.setPutProtocol(DEFAULT_PROTOCOL);

        try {
            ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class).addResourceGroup(createInfo);
        } catch (Exception e) {
            logger.error("system init config add resource group:" + e.getMessage());
            delRegion(region.getId());
            return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
        }

        // DC已存在
        if (StringUtils.isNotBlank(storageConfig.getDcId())) {
            modifyStorage(region.getId(), Integer.valueOf(storageConfig.getDcId()), storageConfig);
            return new ResponseEntity<StorageConfig>(storageConfig, HttpStatus.OK);
        }

        // add DC storage
        DataCenter dataCenter = dcService.findByName(dcname);

        String udsStorageId = "";

        // add uds storage
        if (StringUtils.isNotBlank(storageConfig.getDomain()) && StringUtils.isNotBlank(storageConfig.getAccessKey())
                && StringUtils.isNotBlank(storageConfig.getSecretKey())) {
            if (storageConfig.getHttpPort() == 0) {
                storageConfig.setHttpPort(80);
            }
            if (storageConfig.getHttpsPort() == 0) {
                storageConfig.setHttpsPort(443);
            }
            String storageResource = new StringBuffer().append(storageConfig.getDomain())
                .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getHttpPort())
                .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getHttpsPort())
                .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getAccessKey())
                .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getSecretKey())
                .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getProvider()).toString();

            StorageInfo udsStorageInfo = new StorageInfo();
            udsStorageInfo.setEndpoint(storageResource);
            if ("ALIAI".equals(storageConfig.getProvider()) || "ALIOSS".equals(storageConfig.getProvider())) {
                udsStorageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_ALIYUN_OSS);
            } else if ("QYOS".equals(storageConfig.getProvider())) {
                udsStorageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_QCloud_OS);
            } else if ("HWOBS".equals(storageConfig.getProvider())) {
                udsStorageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_UDS);
            } else {
                throw new RuntimeException("Cloud storage services not supported");
            }
            udsStorageInfo.setWriteAlbe(true);
            udsStorageInfo.setAvailAble(true);
            udsStorageInfo.setNoSpace(false);
            try {
                ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                    .addStorageResource(dataCenter.getId(), udsStorageInfo);
            } catch (Exception e) {
                logger.error("system init config add uds storage resource:" + e.getMessage());
                delRegion(region.getId());
                deleteResourceGroup(dataCenter.getId());
                if (StringUtils.isNotBlank(udsStorageId)) {
                    delStorageResource(dataCenter.getId(), udsStorageId);
                }
                return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
            }
        }

        dataCenter = dcService.getDataCenter(dataCenter.getId());
        boolean isEnableDC = false;
        List<UdsStorage> udsStorages = getUdsStorages(dataCenter.getId());
        if (udsStorages.size() > 0) {
            try {
                ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                    .enableStorageResource(dataCenter.getId(), udsStorages.get(0).getFsId());
            } catch (Exception e) {
                logger.error("system init config enable uds storage resource:" + e.getMessage());
                delRegion(region.getId());
                deleteResourceGroup(dataCenter.getId());
                if (StringUtils.isNotBlank(udsStorageId)) {
                    delStorageResource(dataCenter.getId(), udsStorageId);
                }
                return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
            }

            isEnableDC = true;
        }

        // enable data center
        if (isEnableDC) {
            try {
                ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class)
                    .activeResourceGroup(dataCenter.getId());
            } catch (Exception e) {
                logger.error("system init config enable uds storage resource:" + e.getMessage());
                delRegion(region.getId());
                deleteResourceGroup(dataCenter.getId());
                if (udsStorageId.length() > 0) {
                    disableStorageResource(dataCenter.getId(), udsStorageId);
                    delStorageResource(dataCenter.getId(), udsStorageId);
                }
                return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
            }
        }

        return new ResponseEntity<StorageConfig>(storageConfig, HttpStatus.OK);
    }

    @RequestMapping(value = "/nasstorage/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveNASStorageConfig(StorageConfig storageConfig, String token, HttpServletRequest request,
        HttpServletResponse response) throws TException {

        int dcId = -1;
        int regionId = -1;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("regionId")) {
                regionId = Integer.parseInt(cookie.getValue());
            } else if (cookie.getName().equals("dcId")) {
                dcId = Integer.parseInt(cookie.getValue());
            }
        }
        Region region = regionService.getRegion(regionId);
        DataCenter dCenter = dcService.getDataCenter(dcId);
        if (dCenter == null && region == null) {
            // add region
            String rgname = StringUtils.isBlank(storageConfig.getRegionName()) ? "default"
                    : storageConfig.getRegionName();
            regionService.addRegion("Default", rgname, "");

            // add DC

            if (!FormValidateUtil.isValidIPv4(storageConfig.getManageIp())) {
                throw new ConstraintViolationException("ip is invaid", null);
            }

            List<Region> regionList = regionService.listRegion();
            for (Region reg : regionList) {
                if (reg.getCode().equals(storageConfig.getRegionName())) {
                    region = reg;
                }
            }
            response.addCookie(new Cookie("regionId", String.valueOf(region.getId())));

            ResourceGroupCreateInfo createInfo = new ResourceGroupCreateInfo();
            String dcname = StringUtils.isBlank(storageConfig.getDcName()) ? "default" : storageConfig.getDcName();
            createInfo.setName(dcname);
            createInfo.setManagerIp(storageConfig.getManageIp());
            createInfo.setManagerPort(managePort);
            createInfo.setRegionId(region.getId());
            createInfo.setDomainName("");
            createInfo.setGetProtocol(DEFAULT_PROTOCOL);
            createInfo.setPutProtocol(DEFAULT_PROTOCOL);

            try {
                ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class).addResourceGroup(createInfo);
            } catch (Exception e) {
                logger.error("system init config add resource group:" + e.getMessage());
                delRegion(region.getId());
                return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
            }

            // add DC storage

            DataCenter dataCenter = dcService.findByName(dcname);
            response.addCookie(new Cookie("dcId", String.valueOf(dataCenter.getId())));
            String nasStorageId = "";
            String udsStorageId = "";
            if (storageConfig.getPath() != null && storageConfig.getPath().length() != 0) {
                StorageInfo storageInfo = new StorageInfo();
                storageInfo.setEndpoint(storageConfig.getPath());
                storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_NAS);
                storageInfo.setWriteAlbe(true);
                storageInfo.setAvailAble(true);
                storageInfo.setNoSpace(false);
                storageInfo.setMaxUtilization(60);
                storageInfo.setRetrieval(50);

                try {
                    nasStorageId = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                        .addStorageResource(dataCenter.getId(), storageInfo);
                } catch (Exception e) {
                    logger.error("system init config add nas storage resource:" + e.getMessage());
                    delRegion(region.getId());
                    deleteResourceGroup(dcId);
                    return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
                }
            }

            dataCenter = dcService.getDataCenter(dataCenter.getId());
            ResourceGroup resourceGroup = dataCenter.getResourceGroup();
            List<NasStorage> storages = listNasStorage(resourceGroup.getId());
            // enable data center flag
            boolean isEnableDC = false;
            if (storages != null && storages.size() > 0) {
                NasStorage nasStorage = storages.get(0);
                String fsId = nasStorage.getFsId();

                // enable nas storage
                try {
                    ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                        .enableStorageResource(dataCenter.getId(), fsId);
                } catch (Exception e) {
                    logger.error("system init config enable nas storage resource:" + e.getMessage());
                    delRegion(region.getId());
                    deleteResourceGroup(dcId);
                    if (nasStorageId.length() > 0) {
                        delStorageResource(dcId, nasStorageId);
                    }
                    if (udsStorageId.length() > 0) {
                        delStorageResource(dcId, udsStorageId);
                    }
                    return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
                }
                isEnableDC = true;
            }

            // enable data center
            if (isEnableDC) {
                try {
                    ufmThriftClientProxyFactory.getProxy(DCManageServiceClient.class)
                        .activeResourceGroup(dataCenter.getId());
                } catch (Exception e) {
                    logger.error("system init config enable uds storage resource:" + e.getMessage());
                    delRegion(region.getId());
                    deleteResourceGroup(dcId);
                    if (nasStorageId.length() > 0) {
                        disableStorageResource(dcId, nasStorageId);
                        delStorageResource(dcId, nasStorageId);
                    }
                    if (udsStorageId.length() > 0) {
                        disableStorageResource(dcId, udsStorageId);
                        delStorageResource(dcId, udsStorageId);
                    }
                    return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
                }
            }
            Cookie storageConfigCookie = new Cookie("storageConfig", "1");
            storageConfigCookie.setPath("/");
            response.addCookie(storageConfigCookie);
        } else {
            modifyStorage(regionId, dcId, storageConfig);
        }

        return new ResponseEntity<StorageConfig>(storageConfig, HttpStatus.OK);
    }

    private void modifyStorage(int regionId, int dcId, StorageConfig storageConfig) throws TException {
        // modify region
        regionService.changeRegion(regionId, "Default", storageConfig.getRegionName(), "");
        if (dcId < 0) {
            throw new RuntimeException("dcId not found");
        }
        List<StorageInfo> storageInfos = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
            .getAllStorageResource(dcId);

        if (StringUtils.isNotBlank(storageConfig.getProvider())) {
            // modify uds storage
            if (storageConfig.getDomain() != null && storageConfig.getAccessKey() != null
                    && storageConfig.getSecretKey() != null) {

                List<UdsStorage> oldUdsStorages = getUdsStorages(dcId);
                StorageInfo storageInfo = new StorageInfo();
                StringBuffer storageBuffer = new StringBuffer().append(storageConfig.getDomain())
                    .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getHttpPort())
                    .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getHttpsPort());
                if (oldUdsStorages.size() == 0) {
                    // is null add uds
                    storageBuffer.append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getAccessKey())
                        .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getSecretKey())
                        .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getProvider()).toString();
                    String storageResource = storageBuffer.toString();
                    storageInfo.setEndpoint(storageResource);
                    if ("ALIAI".equals(storageConfig.getProvider()) || "ALIOSS".equals(storageConfig.getProvider())) {
                        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_ALIYUN_OSS);
                    } else if ("QYOS".equals(storageConfig.getProvider())) {
                        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_QCloud_OS);
                    } else if ("HWOBS".equals(storageConfig.getProvider())) {
                        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_UDS);
                    } else {
                        throw new RuntimeException("Cloud storage services not supported");
                    }
                    storageInfo.setWriteAlbe(true);
                    storageInfo.setAvailAble(true);
                    storageInfo.setNoSpace(false);
                    ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).addStorageResource(dcId,
                        storageInfo);
                    // enable uds
                    List<UdsStorage> udsStorages = getUdsStorages(dcId);
                    if (udsStorages.size() > 0) {
                        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                            .enableStorageResource(dcId, udsStorages.get(0).getFsId());
                    }
                } else {
                    storageBuffer.append(Constants.UDS_STORAGE_SPLIT_CHAR).append(oldUdsStorages.get(0).getAccessKey())
                        .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(oldUdsStorages.get(0).getSecretKey())
                        .append(Constants.UDS_STORAGE_SPLIT_CHAR).append(storageConfig.getProvider()).toString();
                    storageInfo.setId(oldUdsStorages.get(0).getFsId());
                    String storageResource = storageBuffer.toString();

                    storageInfo.setEndpoint(storageResource);
                    if ("ALIAI".equals(storageConfig.getProvider()) || "ALIOSS".equals(storageConfig.getProvider())) {
                        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_ALIYUN_OSS);
                    } else if ("QYOS".equals(storageConfig.getProvider())) {
                        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_QCloud_OS);
                    } else if ("HWOBS".equals(storageConfig.getProvider())) {
                        storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_UDS);
                    } else {
                        throw new RuntimeException("Cloud storage services not supported");
                    }

                    ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).changeStorageResource(dcId,
                        storageInfo);
                }
            }
        } else {
            // modify nas storageResource
            if (StringUtils.isNotBlank(storageConfig.getPath())) {
                // is null add nas
                List<NasStorage> oldNasStorages = getNasStorages(dcId);
                if (oldNasStorages.size() == 0) {
                    StorageInfo storageInfo = new StorageInfo();
                    storageInfo.setEndpoint(storageConfig.getPath());
                    storageInfo.setFsType(FileSystemConstant.FILE_SYSTEM_NAS);
                    storageInfo.setWriteAlbe(true);
                    storageInfo.setAvailAble(true);
                    storageInfo.setNoSpace(false);
                    storageInfo.setMaxUtilization(60);
                    storageInfo.setRetrieval(50);
                    ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class).addStorageResource(dcId,
                        storageInfo);
                    // enable nas
                    List<NasStorage> nasStorages = getNasStorages(dcId);
                    if (nasStorages.size() > 0)
                        ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                            .enableStorageResource(dcId, nasStorages.get(0).getFsId());

                } else {
                    for (StorageInfo info : storageInfos) {
                        if (info.getFsType() == FileSystemConstant.FILE_SYSTEM_NAS
                                && !info.getEndpoint().equals(storageConfig.getPath())) {
                            // StorageInfo storageInfo = new StorageInfo();
                            info.setEndpoint(storageConfig.getPath());
                            ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                                .changeStorageResource(dcId, info);
                        }
                    }
                }
            } else {
                throw new RuntimeException("NAS Path Is NULL");
            }
        }
    }

    public List<NasStorage> listNasStorage(int resourceGroupId) {
        List<NasStorage> nasInfos = null;
        for (int i = 0; i < 3; i++) {
            List<StorageInfo> storageResList = null;
            try {
                storageResList = ufmThriftClientProxyFactory.getProxy(StorageResourceServiceClient.class)
                    .getAllStorageResource(resourceGroupId);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (null == storageResList) {
                continue;
            }

            nasInfos = new ArrayList<NasStorage>(1);

            for (StorageInfo storageInfo : storageResList) {
                if (FileSystemConstant.FILE_SYSTEM_NAS.equals(storageInfo.getFsType())) {
                    NasStorage nasStorage = new NasStorage(storageInfo);
                    nasStorage.setDcId(resourceGroupId);
                    nasInfos.add(nasStorage);
                }
            }

            return nasInfos;
        }

        return nasInfos;
    }

    @RequestMapping(value = "/accessAddress/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveAccessAddressConfig(AccessAddressConfig accessAddressConfig,
        HttpServletResponse response) {

        String result = bmsConfigRestClient.configAccessAddress(accessAddressConfig);
        if (result != null && !result.equals("")) {
            return new ResponseEntity<String>(result, HttpStatus.FAILED_DEPENDENCY);
        }

        Cookie accessConfigCookie = new Cookie("accessConfig", "1");
        accessConfigCookie.setPath("/");
        response.addCookie(accessConfigCookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<?> saveBmsUserConfig(BmsUser admin) {

        String result = bmsConfigRestClient.configBmsUserLocal(admin);
        if (result != null && !result.equals("")) {
            return new ResponseEntity<String>(result, HttpStatus.FAILED_DEPENDENCY);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<?> saveEnterpriseAdmin(long enterpriseId, String email, HttpServletRequest request) {

        Enterprise enterprise = bmsConfigRestClient.findEnterpriseById(enterpriseId);
        if (!enterprise.getContactEmail().equals(email)) {
            return new ResponseEntity<String>("enterprise email error", HttpStatus.FAILED_DEPENDENCY);
        }
        Locale locale = RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
        String language = locale.getLanguage();

        // Bms create enterprise admin and enterprise bind app
        String result = bmsConfigRestClient.configEnterpriseAdmin(email, language, appId);
        if (result != null && !result.equals("")) {
            return new ResponseEntity<String>(result, HttpStatus.FAILED_DEPENDENCY);
        }

        // Uam enterprise bind app
        result = uamConfigRestClient.enterpriseBindApp(enterprise, appId);
        if (result != null && !result.equals("")) {
            return new ResponseEntity<String>(result, HttpStatus.FAILED_DEPENDENCY);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/enterprise/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveEnterpriseConfig(Enterprise enterprise, HttpServletRequest request,
        HttpServletResponse response) {
        long enterpriseId = (enterprise.getId() > 0) ? enterprise.getId() : -1;
        Enterprise enterprisefind = null;

        if (enterpriseId == -1) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("enterpriseId")) {
                    enterpriseId = Long.parseLong(cookie.getValue());
                }
            }
        }
        if (enterpriseId != -1) {
            enterprisefind = bmsConfigRestClient.findEnterpriseById(enterpriseId);
        }

        if (enterprisefind == null) {

            // Create app
            AuthApp authApp = new AuthApp();
            List<Admin> admins = adminService.getFilterd(null, null, null);
            for (Admin admin : admins) {
                if (!admin.getRoleNames().contains("ADMIN_MANAGER")) {
                    authApp.setCreateBy(String.valueOf(admin.getId()));
                    break;
                }
            }
            authApp.setAuthAppId(appId);
            authApp.setAuthUrl(authUrl);
            authApp.setStatus(0);
            AppAccessKey accessKey = authAppService.create(authApp);

            appAccessKeyService.updateFirstScan(accessKey);

            // decode secretKey
            Crypter crypter = CrypterFactory.getCrypter(CrypterFactory.AES_CBC);
            String key = crypter.decryptByRootKey(accessKey.getSecretKeyEncodeKey());
            String secretKey = crypter.decrypt(accessKey.getSecretKey(), key);
            accessKey.setSecretKey(secretKey);

            // Bms creat app
            String result = bmsConfigRestClient.configBmsApp(accessKey, authApp.getAuthAppId());
            if (result != null && !result.equals("")) {
                return new ResponseEntity<String>(result, HttpStatus.FAILED_DEPENDENCY);
            }

            TextResponse res = bmsConfigRestClient.configEnterprise(enterprise);

            if (res.getStatusCode() != HttpStatus.OK.value()) {
                return new ResponseEntity<String>(result, HttpStatus.FAILED_DEPENDENCY);
            }

            Cookie cookie = new Cookie("enterpriseId", res.getResponseBody());
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            enterprisefind.setDomainName(enterprise.getDomainName());
            enterprisefind.setName(enterprise.getName());
            TextResponse res = bmsConfigRestClient.updateEnterprise(enterprisefind);
            if (res.getStatusCode() != HttpStatus.OK.value()) {
                return new ResponseEntity<String>(res.getResponseBody(), HttpStatus.FAILED_DEPENDENCY);
            }
        }

        // 执行第一步时候，重写Cookie，已保证程序可以执行下一步
        Cookie enterpriseCookie = new Cookie("enterpriseConfig", "1");
        enterpriseCookie.setPath("/");
        response.addCookie(enterpriseCookie);

        return new ResponseEntity<>(enterpriseId, HttpStatus.OK);

    }

    @RequestMapping(value = "/mail/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveMailServer(MailServer mailServer, HttpServletResponse response) {

        MailServer mailServerDb = mailService.getMailServer();
        mailServerDb.setAppId(mailServer.getAppId());
        mailServerDb.setAuthPassword(mailServer.getAuthPassword());
        mailServerDb.setAuthUsername(mailServer.getAuthUsername());
        mailServerDb.setEnableAuth(mailServer.isEnableAuth());
        mailServerDb.setSslPort(mailServer.getSslPort());
        mailServerDb.setPort(mailServer.getPort());
        mailServerDb.setTestMail(mailServer.getTestMail());
        mailServerDb.setSenderMail(mailServer.getSenderMail());
        mailServerDb.setServer(mailServer.getServer());
        mailServerDb.setMailSecurity(mailServer.getMailSecurity());
        mailServerDb.setAuthPassword(PwdConfuser.getSysMailPwd(mailServerDb, mailServerDb.getAuthPassword()));
        mailService.saveMailServer(mailServerDb);

        bmsConfigRestClient.createMail(mailServer);

        // mailServer.setAppId(appId);
        // bmsConfigRestClient.createMail(mailServer);
        Cookie cookieMailConifg = new Cookie("mailConfig", "1");
        cookieMailConifg.setPath("/");
        response.addCookie(cookieMailConifg);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/isystem/admin/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveAdmin(AdminAccount adminAccount, HttpServletRequest request,
        HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        // add enterprise admin

        Enterprise enterprise = bmsConfigRestClient.findEnterpriseByOwnerId(-1);

        if (enterprise == null) {
            return new ResponseEntity<String>(HttpStatus.FAILED_DEPENDENCY);
        }
        enterprise.setContactEmail(adminAccount.getEmail());
        bmsConfigRestClient.updateEnterprise(enterprise);

        bmsConfigRestClient.saveAppBaseConfig(appId);

        // add uam admin
        ResponseEntity<?> responseEntity = saveEnterpriseAdmin(enterprise.getId(), adminAccount.getEmail(), request);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }

        Admin admin = new Admin();
        admin.setLoginName(adminAccount.getLoginName());
        admin.setName(adminAccount.getName());
        admin.setEmail(adminAccount.getEmail());

        // add isystem admin
        responseEntity = saveIsystemAdmin(admin);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }

        BmsUser bmsUser = new BmsUser();
        bmsUser.setLoginName(adminAccount.getLoginName());
        bmsUser.setName(adminAccount.getLoginName());
        bmsUser.setEmail(adminAccount.getEmail());

        // add bms admin
        responseEntity = saveBmsUserConfig(bmsUser);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }

        // add enterprise user
        if (adminAccount.getIsConfigEnterpriseUser() == 1) {
            Enterprise enterpriseUser = new Enterprise();
            enterpriseUser.setId(enterprise.getId());
            enterpriseUser.setName(adminAccount.getLoginName());
            enterpriseUser.setDomainName(adminAccount.getName());
            enterpriseUser.setContactEmail(adminAccount.getEmail());
            String result = uamConfigRestClient.createEnterpriseUser(enterpriseUser, appId);
            response.addCookie(new Cookie("employeeUserAdd", "1"));
            if (result != null && !result.equals("")) {
                return new ResponseEntity<String>(result, HttpStatus.FAILED_DEPENDENCY);
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<?> saveIsystemAdmin(Admin admin)
        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        admin.setDomainType(Constants.DOMAIN_TYPE_LOCAL);
        String randomPassword = PasswordGenerateUtil.getRandomPassword();
        boolean sendMail = false;
        if (StringUtils.isEmpty(admin.getPassword())) {
            sendMail = true;
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(randomPassword);
            admin.setPassword(hashPassword.getHashPassword());
            admin.setIterations(hashPassword.getIterations());
            admin.setSalt(hashPassword.getSalt());
        } else {
            HashPassword hashPassword = HashPasswordUtil.generateHashPassword(admin.getPassword());
            admin.setPassword(hashPassword.getHashPassword());
            admin.setIterations(hashPassword.getIterations());
            admin.setSalt(hashPassword.getSalt());
        }

        Set<AdminRole> aRoles = new HashSet<AdminRole>();
        for (AdminRole value : AdminRole.values()) {
            if (value.equals(AdminRole.ADMIN_MANAGER) || value.equals(AdminRole.STATISTIC_MANAGER)
                    || value.equals(AdminRole.PLUGINSERVER_MANAGER) || value.equals(AdminRole.MONITOR_MANAGER)) {
                continue;
            }
            aRoles.add(value);
        }

        admin.setRoles(aRoles);
        admin.setStatus(Admin.STATUS_ENABLE);
        Admin administrator = adminService.getAdminByLoginName(admin.getLoginName());
        if (administrator == null) {
            adminService.create(admin);
            admin = adminService.getAdminByLoginName(admin.getLoginName());
        } else {
            admin = administrator;
        }

        authAppService.updateCreate(appId, admin.getId());
        if (sendMail) {

            String link = PropertiesUtils.getServiceUrl() + "login";
            admin.setPassword(randomPassword);
            sendEmail(admin, link);

        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void sendEmail(Admin admin, String link) throws IOException {
        Map<String, Object> messageModel = new HashMap<String, Object>(3);
        messageModel.put("username", admin.getName());
        messageModel.put("loginName", admin.getLoginName());
        messageModel.put("password", admin.getPassword());
        messageModel.put("link", link);
        String msg = mailService.getEmailMsgByTemplate(INITSET_PWD_MAIL_CONTENT, messageModel);
        String subject = mailService.getEmailMsgByTemplate(INITSET_PWD_MAIL_SUBJECT, new HashMap<String, Object>(1));
        mailService.sendHtmlMail(admin.getEmail(), null, null, subject, msg);
    }

    @RequestMapping(value = "/license/config", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> licenseConfig(MultipartHttpServletRequest request, Model model) {

        UserLog userLog = userLogService.initUserLog(request, UserLogType.LICENSE_UPLOAD, null);
        userLogService.saveUserLog(userLog);
        CseLicenseInfo licenseInfo = null;
        try {
            Admin sessAdmin = (Admin) SecurityUtils.getSubject().getPrincipal();
            long optId = sessAdmin.getId();
            Map<String, MultipartFile> fileMap = request.getFileMap();
            LicenseImportResult result = null;
            try {
                result = parseLicenseFile(model, optId, fileMap);
            } catch (LicenseCompareException e) {

            }

            if (null != result) {
                licenseInfo = result.getLicenseInfo();
            }

            model.addAttribute("licenseInfo", licenseInfo);
            if (null != licenseInfo && CollectionUtils.isNotEmpty(licenseInfo.getEsnList())) {
                // String esnString = StringUtils.join(licenseInfo.getEsnList(),
                // ", ");
                // model.addAttribute("esnString", esnString);
            } else {
                model.addAttribute("esnString", "");
            }
        } catch (CseLicenseException e) {
            // message = "license.upload.error";
            // model.addAttribute("error", "CseLicenseException");
        } catch (IOException e) {
            // message = "license.upload.error";
            // model.addAttribute("error", "Throwable");
        } finally {
            deleteLocalFile(licenseInfo);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/listLicense", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<LicenseNode>> listLicenseNode(Model model, HttpServletRequest request,
        HttpServletResponse response) {
        List<LicenseNode> nodeList = licenseService.getLienseNode();

        for (LicenseNode licenseNode : nodeList) {
            transNode(licenseNode);
        }
        return new ResponseEntity<List<LicenseNode>>(nodeList, HttpStatus.OK);
    }

    private void transNode(LicenseNode licenseNode) {
        if (licenseNode == null) {
            return;
        }
        if (licenseNode.getLicenseId() == null) {
            licenseNode.setLicenseId("-");
        }
    }

    private LicenseImportResult parseLicenseFile(Model model, long optId, Map<String, MultipartFile> fileMap)
        throws CseLicenseException, IOException, LicenseCompareException {
        LicenseImportResult result = new LicenseImportResult();
        if (null == fileMap) {
            return result;
        }

        CseLicenseInfo licenseInfo = null;
        String message = null;
        MultipartFile file = null;
        String uuid = null;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
            if (!"licenseFile".equals(entry.getKey())) {
                continue;
            }

            file = entry.getValue();
            if (StringUtils.isEmpty(file.getOriginalFilename())) {
                message = "licesen.file.null.error";
                break;
            }
            if (!checkLicensePix(file.getOriginalFilename())) {
                message = "licesen.file.pix.error";
                break;
            }
            if (file.getSize() > MAX_SIZE) {
                message = "licesen.file.max.error";
                model.addAttribute("error", "sizeError");
                break;
            }
            licenseInfo = licenseService.getCseLicenseInfo(file, file.getOriginalFilename());
            if (null == licenseInfo || StringUtils.isEmpty(licenseInfo.getProductName())) {
                message = "license.file.error";
                model.addAttribute("error", "invalidLicense");
                break;
            }
            model.addAttribute("defaultTeams", getMaxTeamspaces(licenseInfo.getUsers()));
            licenseService.checkDifferenceWithCurrent(licenseInfo);
            uuid = UUID.randomUUID().toString();
            licenseService.saveToDb(licenseInfo, uuid, optId, LicenseService.STATUS_WAIT_CONFIRM);
            model.addAttribute("licenseUuid", uuid);

            result.setSuccess(true);
            break;
        }

        result.setMessage(message);
        result.setLicenseInfo(licenseInfo);
        return result;
    }

    private static int getMaxTeamspaces(int currentUsers) {
        int result = 0;
        if (currentUsers > 1000000) {
            result = currentUsers / 1000000 * 100000;
        } else if (currentUsers > 100000) {
            result = currentUsers / 100000 * 10000;
        } else if (currentUsers > 10000) {
            result = currentUsers / 10000 * 1000;
        } else if (currentUsers > 1000) {
            result = currentUsers / 1000 * 100;
        } else {
            result = currentUsers / 100 * 10;
        }
        return result;
    }

    private boolean checksuffix(String fileName, String suffix) {
        String fileSuffix = getSuffix(fileName);

        return suffix.equalsIgnoreCase(fileSuffix);

    }

    private String getSuffix(String fileName) {
        String[] temp = fileName.split("\\.");
        int len = temp.length;
        if (len < 2) {
            return "";
        }
        String fileSuffix = temp[len - 1];
        return fileSuffix;
    }

    private boolean checkLicensePix(String fileName) {
        return checksuffix(fileName, LICENSE_DAT) || checksuffix(fileName, LICENSE_XML);
    }

    private void deleteLocalFile(CseLicenseInfo licenseInfo) {
        if (null != licenseInfo && licenseInfo.getFile() != null) {
            try {
                FileUtils.forceDelete(licenseInfo.getFile());
            } catch (IOException e) {

            }
        }
    }
}

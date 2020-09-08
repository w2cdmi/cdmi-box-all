package com.huawei.sharedrive.uam.openapi.rest.enterprise;

import com.huawei.sharedrive.uam.accountuser.service.UserAccountService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.domain.AccountConfigAttribute;
import com.huawei.sharedrive.uam.enterprise.service.AccountConfigService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.httpclient.rest.EnterpriseHttpClient;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.EnterpriseSpaceQuota;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountResponse;
import com.huawei.sharedrive.uam.openapi.domain.UpdateEnterpriseSpaceQuota;
import com.huawei.sharedrive.uam.openapi.domain.user.SpaceQuotaRequest;
import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.uservip.service.EnterpriseVipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.common.domain.AccountConfig;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.uam.domain.AuthApp;

import java.io.IOException;

@Controller
@RequestMapping(value = "/api/v2/enterprise")
@Api(description = "企业配额管理api")
public class EnterpriseSpaceQuotaController extends AbstractCommonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseEmployeeAPIController.class);

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private EnterpriseAccountService enterpriseAccountService;

    @Autowired
    private EnterpriseVipService enterpriseVipService;

    @Autowired
    private AccountConfigService accountConfigService;

    @Autowired
    private UserTokenHelper userTokenHelper;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private RestClient ufmClientService;

    @RequestMapping(value = "/spaceQuota", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获得公司空间配额信息")
    public EnterpriseSpaceQuota getSpaceQuota(@RequestHeader("Authorization") String authorization) throws IOException {
        UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);

        long enterpriseId = userToken.getEnterpriseId();
        long accountId = userToken.getAccountId();

        EnterpriseSpaceQuota quota = new EnterpriseSpaceQuota();
        quota.setEnterpriseId(enterpriseId);
        quota.setAccountId(accountId);

        //企业已有套餐
        Product product = enterpriseVipService.getProductByEnterpriseAccountId(userToken.getAppId(), accountId);
        //套餐名称
        quota.setPackageName(product.getName());
        //套餐规格（个人配额）
        quota.setPackageAccountQuota(product.getAccountSpace());
        //套餐规格（账号数量）
        quota.setPackageAccountNumber(product.getAccountNum());
        //套餐规格（团队空间数量）
        quota.setPackageTeamNumber(product.getTeamNum());
        //套餐规格（团队空间限额）
        quota.setPackageTeamQuota(product.getTeamSpace());

        //公司容量
        EnterpriseAccount enterprise = enterpriseAccountService.getByAccountId(accountId);

        AuthApp authApp = authAppService.getByAuthAppID(enterprise.getAuthAppId());
        EnterpriseHttpClient enterpriseHttpClient = new EnterpriseHttpClient(ufmClientService);
        RestEnterpriseAccountResponse rest = enterpriseHttpClient.getAccountById(authApp, accountId);

        //企业总容量
        quota.setMaxSpace(enterprise.getMaxSpace());
        //企业已用容量
        if(rest.getCurrentSpace() != null) {
            quota.setUsedSpace(rest.getCurrentSpace());
        }

        //共享空间容量
        quota.setMaxShareSpace(enterprise.getMaxShareSpace());
        //共享空间已用容量
        if(rest.getCurrentShareSpace() != null) {
            quota.setUsedShareSpace(rest.getCurrentShareSpace());
        }

        //账号数量
        quota.setMaxAccountNumber(enterprise.getMaxMember());
        //已有账号
        int members = userAccountService.countByAccountId(accountId);
        quota.setUsedAccountNumber(members);

        //团队空间数量
        quota.setMaxTeamNumber(enterprise.getMaxTeamspace());
        //
        quota.setUsedTeamNumber(0);

        AccountConfig accountConfig = accountConfigService.get(accountId, AccountConfigAttribute.ENTERPRISE_ACCOUNT_SPACE_QUOTA.getName());
        if(accountConfig != null) {
            quota.setDefaultAccountSpaceQuota(Long.parseLong(accountConfig.getValue()));
        } else {
            //如果没有配置，使用当前套餐的个人容量
            quota.setDefaultAccountSpaceQuota(product.getAccountSpace());
        }

        return quota;
    }

    @RequestMapping(value = "/spaceQuota", method = RequestMethod.PUT)
    @ApiOperation(value = "统一修改账户个人空间配额", notes = "统一修改账户个人空间配额")
    public ResponseEntity<?> updateUserSpaceQuota(@ApiParam(value = "分配大小", required = true) @RequestBody UpdateEnterpriseSpaceQuota spaceQuota,
                                                  @RequestHeader("Authorization") String authorization) throws IOException {
        UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);

        if(spaceQuota.getDefaultAccountSpaceQuota() < 0) {
            throw new InvalidParamterException();
        }

        long accountId = userToken.getAccountId();
        long original = 0;
        AccountConfig accountConfig = accountConfigService.get(accountId, AccountConfigAttribute.ENTERPRISE_ACCOUNT_SPACE_QUOTA.getName());
        if(accountConfig != null) {
            original = Long.parseLong(accountConfig.getValue());
        } else {
            //企业已有套餐
            Product product = enterpriseVipService.getProductByEnterpriseAccountId(userToken.getAppId(), accountId);
            original = product.getAccountSpace();
        }

        long newQuota = spaceQuota.getDefaultAccountSpaceQuota();

        //调整额度
        userAccountService.updateAccountQuota(accountId, original, newQuota);

        //保存配置
        accountConfig = new AccountConfig();
        accountConfig.setAccountId(accountId);
        accountConfig.setName(AccountConfigAttribute.ENTERPRISE_ACCOUNT_SPACE_QUOTA.getName());
        accountConfig.setValue(String.valueOf(spaceQuota.getDefaultAccountSpaceQuota()));
        accountConfigService.save(accountConfig);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/employees/spaceQuota" ,method = RequestMethod.PUT)
    @ApiOperation(value = "设置单个用户的个人配额",notes = "企业配额，是在个人额度上的增加")
    public ResponseEntity<?>  updateEmployeeSpaceQuota(@RequestBody SpaceQuotaRequest spaceQuota, @RequestHeader("Authorization") String authorization) {
        if (spaceQuota.getEnterpriseUserIds() == null || spaceQuota.getEnterpriseUserIds().isEmpty() || spaceQuota.getQuota() < 0) {
            throw new InvalidParamterException("参数输入错误");
        }

        UserToken userToken = userTokenHelper.checkTokenAndGetAdminUser(authorization);

        //
        userAccountService.updateUserAccountSpaceQuota(userToken.getAccountId(), spaceQuota.getEnterpriseUserIds(), spaceQuota.getQuota());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

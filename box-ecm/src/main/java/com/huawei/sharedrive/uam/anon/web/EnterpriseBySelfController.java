package com.huawei.sharedrive.uam.anon.web;

import com.huawei.sharedrive.uam.anon.domain.RegisterEnterpriseParam;
import com.huawei.sharedrive.uam.anon.service.EnterpriseBySelfService;
import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;
import com.huawei.sharedrive.uam.config.service.SystemProfileService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.openapi.domain.RestEnterpriseAccountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pw.cdmi.common.domain.enterprise.Enterprise;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by hepan on 2017/4/15.
 */
@Controller
@RequestMapping(value = "/register/")
public class EnterpriseBySelfController extends AbstractCommonController {

    @Autowired
    private EnterpriseBySelfService enterpriseBySelfService;

    @Autowired
    private AuthAppService authAppService;

    @Autowired
    private SystemProfileService systemProfileService;

    @RequestMapping(value = "enterprise/self", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> enterpriseRegister(@RequestBody RegisterEnterpriseParam registerEnterpriseParam, HttpServletRequest request)
            throws IOException
    {
        try{
            Enterprise enterprise = new Enterprise();
            enterprise.setContactEmail(registerEnterpriseParam.getContactEmail());
            enterprise.setContactPerson(registerEnterpriseParam.getContactPerson());
            enterprise.setContactPhone(registerEnterpriseParam.getContactPhone());
            enterprise.setDomainName(registerEnterpriseParam.getDomainName());
            enterprise.setName(registerEnterpriseParam.getName());
            enterprise.setIsdepartment(true);

            RestEnterpriseAccountRequest enterpriseAccountRequest = new RestEnterpriseAccountRequest();
            String appId = authAppService.getDefaultWebApp().getAuthAppId();
            EnterpriseAccountProfile profile = systemProfileService.buildEnterpriseAccountProfile(appId);

            enterpriseAccountRequest.setMaxMember(profile.getMaxUserAmount());
            enterpriseAccountRequest.setMaxSpace(profile.getMaxTeamspaceQuota());
            enterpriseAccountRequest.setMaxTeamspace(profile.getMaxTeamspaceAmount());

            enterpriseAccountRequest.setMaxMember(-1);
            enterpriseAccountRequest.setMaxSpace(-1);
            enterpriseAccountRequest.setMaxTeamspace(-1);
//            enterpriseBySelfService.enterpriseRegister(enterprise,enterpriseAccountRequest,registerEnterpriseParam.getAccountId(),request);
        }catch (Exception e){
            throw new InvalidParamterException(e.getMessage());
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "enterpriseUser/self", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> enterpriseUserRegister(@RequestBody RegisterEnterpriseParam registerEnterpriseParam, HttpServletRequest request)
            throws IOException
    {
        try{
            EnterpriseUser enterpriseUser = new EnterpriseUser();
            enterpriseUser.setName(registerEnterpriseParam.getAccountId());
            enterpriseUser.setAlias(registerEnterpriseParam.getContactPerson());
            enterpriseUser.setEmail(registerEnterpriseParam.getContactEmail());
            enterpriseUser.setMobile(registerEnterpriseParam.getContactPhone());


            enterpriseBySelfService.enterpriseUserRegister(registerEnterpriseParam.getDomainName(), enterpriseUser, request);
        }catch (Exception e){
            throw new InvalidParamterException(e.getMessage());
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}

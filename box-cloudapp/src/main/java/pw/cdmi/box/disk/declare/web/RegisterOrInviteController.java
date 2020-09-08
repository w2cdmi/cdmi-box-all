package pw.cdmi.box.disk.declare.web;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wcc.crypt.Crypter;
import org.wcc.crypt.CrypterFactory;

import pw.cdmi.box.disk.enterprise.manager.EnterpriseManager;
import pw.cdmi.box.disk.enterprise.service.EnterpriseService;
import pw.cdmi.box.disk.logininfo.service.LoginInfoService;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.dao.EnterpriseUserDao;
import pw.cdmi.box.disk.user.domain.EnterpriseUser;
import pw.cdmi.common.domain.enterprise.Enterprise;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.InternalServerErrorException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

/**
 * Created by hepan on 2017/4/18.
 */
@Controller
public class RegisterOrInviteController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    LoginInfoService loginInfoService;

    @Autowired
    EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private EnterpriseManager enterpriseManager;

    @Resource
    private RestClient uamClientService;

    @RequestMapping(value = "syscommon/registerEnterprise", method = RequestMethod.GET)
    public String registerEnterprisePage(Model model) throws BaseRunException
    {
        return "anon/registerEnterprise";
    }
    @RequestMapping(value = "syscommon/registerEnterprise", method = RequestMethod.POST)
    @ResponseBody
    public String registerEnterprise(RegisterEnterpriseParam registerEnterpriseParam) throws BaseRunException
    {
        if(StringUtils.isNotEmpty(registerEnterpriseParam.getDomainName())){
            Enterprise enterprise = enterpriseService.getByDomainName(registerEnterpriseParam.getDomainName());
            if(null!=enterprise){
                return "abbr_conflict";
            }
        }
        if(StringUtils.isNotEmpty(registerEnterpriseParam.getContactEmail())){
            int cnt = loginInfoService.getCountByLoginName(registerEnterpriseParam.getContactEmail());
            if(cnt>0){
                return "email_conflict";
            }
        }
        TextResponse restResponse = uamClientService.performJsonPostTextResponse("/register/enterprise/self",
                null,registerEnterpriseParam);
        if (null == restResponse || restResponse.getStatusCode() != 200)
        {
            return null;
        }
        return "success";
    }

    @RequestMapping(value="user/invite/code",method = RequestMethod.GET)
    @ResponseBody
    public String getInviteCode(){
        Crypter crypter = CrypterFactory.getCrypter(CrypterFactory.AES_CBC);
        UserToken user = (UserToken) SecurityUtils.getSubject().getPrincipal();
        List<Enterprise> enterpriseList = enterpriseManager.listEnterprise();
        String domain = "";
        if(null==enterpriseList || enterpriseList.isEmpty()){
            throw  new InternalServerErrorException("");

        }
        for(Enterprise enterprise:enterpriseList){
            if(enterprise.getId()==user.getEnterpriseId()){
                domain = enterprise.getDomainName();
                break;
            }
        }
        if(StringUtils.isEmpty(domain)){
            throw  new InternalServerErrorException("");
        }
        return user.getLoginName()+"/"+crypter.encrypt(domain,user.getLoginName());
    }

    @RequestMapping(value="syscommon/invite/{loginName}/{code}",method = RequestMethod.GET)
    public String inviteRegister(@PathVariable String loginName, @PathVariable String code,Model model) {
        String result = "anon/invalidate";
        if (StringUtils.isEmpty(loginName) || StringUtils.isEmpty(code)) {
            return result;
        }
        Crypter crypter = CrypterFactory.getCrypter(CrypterFactory.AES_CBC);
        String domain = crypter.decrypt(code, loginName);
        if (StringUtils.isEmpty(domain)) {
            return result;
        }
        Enterprise enterprise = enterpriseManager.getByDomainName(domain);
        if (null == enterprise) {
            return result;
        }

        EnterpriseUser enterpriseUser = enterpriseUserDao.getByLoginname(loginName,enterprise.getId());
        if (null == enterprise) {
            return result;
        }
        model.addAttribute("inviter",enterpriseUser.getAlias());
        model.addAttribute("domain",enterprise.getDomainName());
        model.addAttribute("enterpriseName",enterprise.getName());
        return "anon/registerEnterpriseUser";
    }
    @RequestMapping(value="syscommon/invite/registerEnterpriseUser",method = RequestMethod.POST)
    @ResponseBody
    public String registerEnterpriseUser(RegisterEnterpriseParam request) {
        if(StringUtils.isEmpty(request.getDomainName()) || StringUtils.isEmpty(request.getAccountId())
                || StringUtils.isEmpty(request.getContactEmail())){
            return "abbr_conflict";
        }
        int cnt = loginInfoService.getCountByLoginName(request.getContactEmail());
        if(cnt>0){
            return "email_conflict";
        }
        Enterprise enterprise = enterpriseManager.getByDomainName(request.getDomainName());
        if (null == enterprise) {
            return "abbr_conflict";
        }
        EnterpriseUser enterpriseUser = enterpriseUserDao.getByLoginname(request.getAccountId(),enterprise.getId());
        if(null!=enterpriseUser){
            return "account_conflict";
        }
        enterpriseUser = enterpriseUserDao.getByLoginname(request.getContactPerson(),enterprise.getId());
        if(null!=enterpriseUser ){
            return "phone_conflict";
        }

        TextResponse restResponse = uamClientService.performJsonPostTextResponse("/register/enterpriseUser/self",
                null,request);
        if (null == restResponse || restResponse.getStatusCode() != 200)
        {
            return null;
        }
        return "success";
    }
    static class RegisterEnterpriseParam{
        String name;
        String domainName;
        String accountId;
        String contactPerson;
        String contactPhone;
        String contactEmail;
        long maxSpace=-1;
        int maxMember=-1;
        int maxTeamspace=-1;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDomainName() {
            return domainName;
        }

        public void setDomainName(String domainName) {
            this.domainName = domainName;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getContactPerson() {
            return contactPerson;
        }

        public void setContactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public long getMaxSpace() {
            return maxSpace;
        }

        public void setMaxSpace(long maxSpace) {
            this.maxSpace = maxSpace;
        }

        public int getMaxMember() {
            return maxMember;
        }

        public void setMaxMember(int maxMember) {
            this.maxMember = maxMember;
        }

        public int getMaxTeamspace() {
            return maxTeamspace;
        }

        public void setMaxTeamspace(int maxTeamspace) {
            this.maxTeamspace = maxTeamspace;
        }
    }
}

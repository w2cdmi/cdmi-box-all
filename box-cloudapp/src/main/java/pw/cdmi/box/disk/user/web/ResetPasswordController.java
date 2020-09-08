package pw.cdmi.box.disk.user.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.logininfo.service.LoginInfoService;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.ForgetPwdRequest;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.LoginRule;
import pw.cdmi.box.disk.utils.PasswordValidateUtil;
import pw.cdmi.box.disk.utils.PatternRegUtil;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

@Controller
@RequestMapping(value = "/syscommon")
public class ResetPasswordController extends CommonController
{
    @Resource
    private RestClient uamClientService;
    
    @Resource
    private AuthAppService authAppService;
    
    @Resource
    private LoginInfoService loginService;
    
    private int pwdLevel = 1;
    
    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    public ResponseEntity<?> checkName(String username){
    	
    	int count = loginService.getCountByLoginName(username);
    	if(count>1){
    		return new ResponseEntity<Object>(HttpStatus.NOT_ACCEPTABLE);
    	}else{
    		return new ResponseEntity<Object>(HttpStatus.OK);
    	}
    }
    
    @RequestMapping(value = "/checkContactPhone", method = RequestMethod.GET)
    public ResponseEntity<?> checkPhone(String username){
    	
    	int count = loginService.getCountByLoginName(username);
    	if(count==1){
    		return new ResponseEntity<Object>(HttpStatus.OK);
    	}else{
    		return new ResponseEntity<Object>(HttpStatus.NOT_ACCEPTABLE);
    	}
    }
    
    /**
     * 
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "enterforget", method = RequestMethod.GET)
    public String enterForgetPwd()
    {
        if (!LoginRule.forgetPwd())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        return "anon/forgetPwd";
    }
    
    @RequestMapping(value = "confirmAccount", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> confirmAccount(String userAccount) {
        
        if (StringUtils.isBlank(userAccount)) {
            new ResponseEntity<String>("account error",HttpStatus.BAD_REQUEST);
        }
        
        String accountType = "";
        
        if (isEmail(userAccount)) {
            int count = loginService.getCountByLoginName(userAccount);
            if(count==1){
                accountType = "email";
            }else{
                return new ResponseEntity<String>("account not exist",HttpStatus.NOT_ACCEPTABLE);
            }
        }else {
            int count = loginService.getCountByLoginName(userAccount);
            if(count==1){
                accountType = "phone";
            }else{
                return new ResponseEntity<String>("account not exist",HttpStatus.NOT_ACCEPTABLE);
            }
        }
        
        return new ResponseEntity<String>(accountType,HttpStatus.OK);
    }
       
    @RequestMapping(value = "sendlink", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> sendResetLink(ForgetPwdRequest request, String captcha,
        HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        if (!LoginRule.forgetPwd())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        String loginName = request.getLoginName();
        String email = request.getEmail();
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(captcha) || StringUtils.isBlank(email))
        {
            throw new BadRquestException();
        }
        if (loginName.length() > 255)
        {
            throw new BadRquestException();
        }
        
        if(!PatternRegUtil.checkMailLegal(email)){
        	return new ResponseEntity<String>("email legal error.",HttpStatus.BAD_REQUEST);
        }
        Session session = SecurityUtils.getSubject().getSession();
        Object captchaInSession = session.getAttribute("HWVerifyCode");
        session.setAttribute("HWVerifyCode", "");
        if (captcha.length() != 4
            || !captcha.equalsIgnoreCase(captchaInSession == null ? "" : captchaInSession.toString()))
        {
            throw new BadRquestException();
        }
        request.setAppId(authAppService.getCurrentAppId());
        TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/account/forgetpwd",
            null,
            request);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @RequestMapping(value = "setPasswordByPhone", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> resetPassword(ForgetPwdRequest request,
    		HttpServletRequest httpServletRequest)
    {
    	super.checkToken(httpServletRequest);
    	if (!LoginRule.forgetPwd())
    	{
    		throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
    	}
    	String contactPhone = request.getContactPhone();
    	if (StringUtils.isBlank(contactPhone) || StringUtils.isBlank(request.getIdentifyCode()))
    	{
    		throw new BadRquestException();
    	}
    	if (contactPhone.length()!=11)
    	{
    		throw new BadRquestException();
    	}
    	
    	request.setAppId(authAppService.getCurrentAppId());
    	TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/sms/resetPassword",
    			null,
    			request);
    	if (response.getStatusCode() == HttpStatus.OK.value())
    	{
    		return new ResponseEntity<String>(HttpStatus.OK);
    	}
    	return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @RequestMapping(value = "createidentifyCode", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createIdentifyCode(ForgetPwdRequest request,
    		HttpServletRequest httpServletRequest)
    {
    	if (request==null||request.getContactPhone().length()!=11) {
			throw new BadRquestException("phoneNumber is error");
		}
    	TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/sms/sendIdentifyCode", null,request);
    	if (response.getStatusCode() == HttpStatus.OK.value())
    	{
    		return new ResponseEntity<String>(HttpStatus.OK);
    	}
    	return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String resetPwd(Model model, ForgetPwdRequest request)
    {
        String name = request.getName();
        String validateKey = request.getValidateKey();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(validateKey))
        {
            throw new BadRquestException();
        }
        request.setAppId(authAppService.getCurrentAppId());
        try{
            TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/account/validateurl",
                    null,
                    request);
            if (response.getStatusCode() == HttpStatus.OK.value())
            {
                model.addAttribute("loginName", name);
                model.addAttribute("validateKey", validateKey);
                return "anon/reset";
            }
        }catch (RuntimeException e){

        }
        return "anon/invalidate";
    }
    
    @RequestMapping(value = "doreset", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> doReset(ForgetPwdRequest request, HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        if (!LoginRule.forgetPwd())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        if (StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getPassword())
            || !PasswordValidateUtil.isValidPassword(request.getPassword(),pwdLevel))
        {
            throw new BadRquestException();
        }
        String loginName = request.getLoginName();
        String name = request.getName();
        if (loginName == null || name == null || !loginName.equals(request.getName()))
        {
            throw new BadRquestException("nameError", "the name was inputed error.");
        }
        if (loginName.equals(request.getPassword()))
        {
            throw new BadRquestException("namePwdSameError", "name and password can't be same.");
        }
        request.setAppId(authAppService.getCurrentAppId());
        TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/account/resetpwd",
            null,
            request);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    
    @RequestMapping(value = "validpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validPassword(User user, HttpServletRequest httpServletRequest)
        throws BadRquestException
    {
        super.checkToken(httpServletRequest);
        //根据企业ID查找密码复杂度
        UserToken token = getCurrentUser();
        if (token == null) {
            pwdLevel = 1;
        }else{
            long enterpriseId = getCurrentUser().getEnterpriseId();
            TextResponse response = uamClientService.performJsonPostTextResponse("/api/v2/account/getEnterpriseAccountPwdLevel/"+enterpriseId, null, null);
            String responseBody = response.getResponseBody();
            if(!StringUtils.isBlank(responseBody)){
                pwdLevel = Integer.parseInt(responseBody);
            }
        }
        if (!PasswordValidateUtil.isValidPassword(user.getPassword(),pwdLevel))
        {
        	return new ResponseEntity<Integer>(pwdLevel,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "validOldpwd", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> validOldPassword(User user, HttpServletRequest httpServletRequest)
        throws BadRquestException
    {
        super.checkToken(httpServletRequest);
        if (!PasswordValidateUtil.isValidPassword(user.getOldPassword(),pwdLevel))
        {
            throw new BadRquestException();
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "validResetCaptcha")
    @ResponseBody
    public  ResponseEntity<?> validResetCaptcha(HttpServletRequest httpServletRequest, @RequestParam String loginName, @RequestParam String captcha){
        super.checkToken(httpServletRequest);
        if (!LoginRule.forgetPwd())
        {
            throw new BadRquestException("Do not enalbe the funciton to forget pwd.");
        }
        if (StringUtils.isBlank(loginName) || StringUtils.isBlank(captcha))
        {
            throw new BadRquestException();
        }
        if (loginName.length() > 255)
        {
            throw new BadRquestException();
        }

        Session session = SecurityUtils.getSubject().getSession();
        Object captchaInSession = session.getAttribute("HWVerifyCode");
        session.setAttribute("HWVerifyCode", "");
        if (captcha.length() != 4
                || !captcha.equalsIgnoreCase(captchaInSession == null ? "" : captchaInSession.toString()))
        {
            throw new BadRquestException();
        }
        int count = loginService.getCountByLoginName(loginName);
        if(count<1){
            throw new BadRquestException();
        }
        return new ResponseEntity(HttpStatus.OK);
    }
@RequestMapping(value = "sendIdentifyCode", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> sendIdentifyCode(String loginName,String type,String inputValue,HttpServletRequest httpServletRequest)
    {
        super.checkToken(httpServletRequest);
        if (StringUtils.isBlank(loginName))
        {
            throw new BadRquestException();
        }

        if(!"email".equals(type)&&!"phone".equals(type)){
            throw new BadRquestException();
        }
        String url="/api/v2/sms/sendIdentifyCode";
        ForgetPwdRequest request = new ForgetPwdRequest();
        request.setLoginName(loginName);
        if("phone".equals(type)){
            if(null==inputValue||inputValue.length()!=11){
                throw new BadRquestException();
            }
            try{
                Integer.parseInt(inputValue);
            }catch (Exception e){
                throw new BadRquestException();
            }
            request.setContactPhone(inputValue);
        }else{
            if(!PatternRegUtil.checkMailLegal(inputValue)){
                return new ResponseEntity<String>("email legal error.",HttpStatus.BAD_REQUEST);
            }
            url="/api/v2/account/forgetpwd";
            request.setAppId(authAppService.getCurrentAppId());
            request.setEmail(inputValue);
        }
        TextResponse response = uamClientService.performJsonPostTextResponse(url, null,request);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    public boolean isEmail(String loginName) {
        String match = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        if (loginName.matches(match)) {
            return false;
        }
        return true;
    }
}

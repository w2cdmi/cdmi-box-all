package pw.cdmi.box.disk.user.domain;

public class ForgetPwdRequest
{
    private String appId;
    
    private String contactPhone;
    
    private String loginName;
    
    private String name;
    
    private String email;
    
    private String oldPasswd;
    
    private String password;
    
    private String validateKey;
    
    private String newPassword;
    
    private String identifyCode;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getOldPasswd()
    {
        return oldPasswd;
    }
    
    public void setOldPasswd(String oldPasswd)
    {
        this.oldPasswd = oldPasswd;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getValidateKey()
    {
        return validateKey;
    }
    
    public void setValidateKey(String validateKey)
    {
        this.validateKey = validateKey;
    }

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getIdentifyCode() {
		return identifyCode;
	}

	public void setIdentifyCode(String identifyCode) {
		this.identifyCode = identifyCode;
	}

}

package com.huawei.sharedrive.uam.exception;

public enum ErrorCode
{
    AES_ENCRYPT_ERROR("aes_error", ""),
    
    BAD_REQUEST("bad_request", "The requested resource or the request parameter error."),
    
    CLIENTUNAUTHORIZED("ClientUnauthorized", "Authentication failed, The terminal is disabled."),
    
    EXIST_USER_CONFLICT("ExistUserConflict", "user existed"),
    
    ADAUTH_USER_CONFLICT("AdAuthUserConflict", " adAuth user existed"),
    
    EMAIL_CHANGE_CONFLICT("EmailChangeConflict", "Can not change the mail when existed mail is not empy"),
    
    MOBILE_CHANGE_CONFLICT("MobileChangeConflict", "Can not change the moblie when existed mobile is not empy"),
    
    EXIST_TAG_CONFLICT("ExistTagConflict", "tag existed"),
    
    FILES_CONFLICT("conflict", "This folder already exists with the same name file or folder."),
    
    CONFLICT_USER("ConflictUser", "user already existed."),
    
    FORBIDDEN_OPER("Forbidden", "The operation is prohibited."),
    
    INTERNAL_SERVER_ERROR("InternalServerError", "Server internal error, please try again later."),
    
    INVALID_PARAMTER("InvalidParameter", "The request parameter is invalid."),
    
    LDAPUSER_FORBIDDEN("LdapUserForbidden", "Operation not allowed LDAP users"),
    
    LOCALUSER_FORBIDDEN("LocalUserForbidden", "Operation not allowed Local users"), INVALID_VERSIONS(
        "InvalidVersionsException", "The file max versions is invalid"),
    
    LOGINUNAUTHORIZED("Unauthorized", "Authentication fails, the user name or password is incorrect."),
    
    ACCOUNTUNAUTHORIZED("Unauthorized", "Authentication fails, the account illegal or invalid."),
    
    METHOD_NOT_ALLOWED("method_not_allowed", "This method does not allow."),
    
    MissingParameter("missing_parameter", "The request missing required parameters"),
    
    NetworkException("Network exception",
        "Network exception, please try again this operation when the network is not so busy."),
    
    NO_SUCH_OPTION("NoSuchOption", "No such option"),
    
    NO_SUCH_TOKEN("NoSuchToken", "token is not exist."),
    
    NO_SUCH_AUTHSERVER("NoSuchAuthServer", "authServer is not exist."),
    
    NO_SUCH_USER("NoSuchUser", "This user does not exist."),
    
    NO_SUCH_ITEM("NoSuchItem", "This item does not exist."),
    
    NO_SUCH_REGION("NoSuchRegion", "This region does not exist."),
    
    SecurityMartix("SecurityForbidden", "forbidden, the security martix deny the request."),
    
    SHA_ENCRYPT_ERROR("sha_error", ""),
    
    USER_DISABLED("UserDisabled", "The user has been disabled"),
    
    TOKENUNAUTHORIZED("Unauthorized", "Authentication fails, the token illegal or invalid."),
    
    TOO_MANY_REQUESTS("TooManyRequests", "Too many requests, please try again later."),
    
    LICENSE_FORBIDDEN("LicenseInvalid", "The license is invalid."),
    
    USERLOCKED("UserLocked", "forbidden, the user is locked."),
    
    EXIST_ENTERPRISE_CONFLICT("ExistEnterpriseConflict", "DomainName or contactEmail already existed"),
    
    INVALID_APPID("InvalidAppId", "Invalid AppId"),
    
    EXIST_ACCOUNT_USER_CONFLICT("ExistAccountUserConflict", "Exist Account User Conflict"),
    
    NO_SUCH_DECLARATION("NoSuchExistDeclaration", "No Such Declaration"),
    
    NOT_LOCAL_AUTH("NotLocalAuth", "Not Local Auth"),
    
    EXIST_ACCESSCONFIG_CONFLICT("ExistAccessConfigConflict", "Access config already existed"),
    
    EXIST_FILECOPY_CONFLICT("ExistFileCopyConflict", "File of copy configing already existed"),
    
    EXIST_SPACESCONFIG_CONFLICT("ExistAccessConfigConflict", "Access space config aleady existed"),
    
    EXIST_SECURITY_ROLE_CONFLICT("ExistSecurityRoleConflict", "Security Role already existed"),
    
    EXIST_NETWORK_REGION_IP_CONFLICT("ExistNetworkRegionIpConflictException",
        "Network region ip already existed"),
    
    EXIST_RESOURCE_STRATEGY_CONFLICT("ExistResourceStrategyConflict", "Resource Strategy already existed"),
    
    NO_SUCH_ANNOUNCEMENT("NoSuchAnnouncement", "This announcement does not exist."),
    
    INVALID_SIZE("InvalidSize", "The size of image file is not allowed"), INVALID_SCALE("InvalidScale",
        "The scale of image file is not allowed."), INVALID_IMAGE("InvalidImage",
        "The file is not image file or is not jpg/png format image"),
    
    NO_SUCH_MESSAGE("NoSuchMessage", "This message does not exist."),
    
    NO_SUCH_RESOURCE_STRATEGY("NoSuchResourceStrategy", "This resource strategy does not exist."),
    
    NO_SUCH_SECUTIRY_LEVEL("NoSuchSecurityLevel", "This security level does not exist."),
    
    NO_SUCH_NETWORK_REGION_IP("NoSuchNetworkRegionIp", "This ip of network region does not exist."),
    
    EXCEED_QUOTA("ExceedQuota", "Perform the current operation will cause the quantity exceeds maximum"),
    
    NO_SUCH_VERSION("NoSuchVersion", "This version not exist or type not exist"),
    
    EXCEED_MAX_ENTERPRISE_NUM("ExceedMaxEnterpriseNum", "The sum of enterprise exceed the maximum"),
    
    NEEDSIGNDECLARATION("NeedSignDeclaration", "You should sign the Privacy Statement."),
    
    NEEDCHANGEPASSWORD("NeedChangePassword", "You should reset your passwod."),
	DEPARTMENT_NOT_FOUND("DeptNotFound","Specified department have not been found in system.");
    
    private String code;
    
    private String message;
    
    private ErrorCode(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getMessage()
    {
        return message;
    }
}

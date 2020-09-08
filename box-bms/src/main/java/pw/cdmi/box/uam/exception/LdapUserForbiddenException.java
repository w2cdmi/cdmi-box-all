package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class LdapUserForbiddenException extends BaseRunException
{
    private static final long serialVersionUID = 3187889527609485428L;
    
    public LdapUserForbiddenException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.LDAPUSER_FORBIDDEN.getCode(),
            ErrorCode.LDAPUSER_FORBIDDEN.getMessage());
    }
    
    public LdapUserForbiddenException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.LDAPUSER_FORBIDDEN.getCode(),
            ErrorCode.LDAPUSER_FORBIDDEN.getMessage(), excepMessage);
    }
}

package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class LicenseException extends BaseRunException
{
    
    private static final long serialVersionUID = -2600619696337248857L;
    
    public LicenseException()
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.LICENSE_FORBIDDEN.getCode(),
            ErrorCode.LICENSE_FORBIDDEN.getMessage());
    }
    
    public LicenseException(String excepMessage)
    {
        super(HttpStatus.FORBIDDEN, ErrorCode.LICENSE_FORBIDDEN.getCode(),
            ErrorCode.LICENSE_FORBIDDEN.getMessage(), excepMessage);
    }
    
}

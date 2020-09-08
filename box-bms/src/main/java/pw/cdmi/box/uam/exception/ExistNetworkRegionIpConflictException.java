package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistNetworkRegionIpConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = 1965940030381077499L;
    
    public ExistNetworkRegionIpConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_NETWORK_REGION_IP_CONFLICT.getCode(),
            ErrorCode.EXIST_NETWORK_REGION_IP_CONFLICT.getMessage());
    }
    
}

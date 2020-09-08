package com.huawei.sharedrive.uam.exception;

import org.springframework.http.HttpStatus;

public class NoSuchNetworkRegionIpException extends BaseRunException
{
    private static final long serialVersionUID = 1826643366776562778L;
    
    public NoSuchNetworkRegionIpException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_NETWORK_REGION_IP.getCode(),
            ErrorCode.NO_SUCH_NETWORK_REGION_IP.getMessage());
    }
    
    public NoSuchNetworkRegionIpException(Throwable e)
    {
        super(e, HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_NETWORK_REGION_IP.getCode(),
            ErrorCode.NO_SUCH_NETWORK_REGION_IP.getMessage());
    }
    
    public NoSuchNetworkRegionIpException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_NETWORK_REGION_IP.getCode(),
            ErrorCode.NO_SUCH_NETWORK_REGION_IP.getMessage(), excepMessage);
    }
    
}

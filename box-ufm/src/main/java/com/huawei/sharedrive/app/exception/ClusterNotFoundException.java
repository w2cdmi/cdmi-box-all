package com.huawei.sharedrive.app.exception;

import org.springframework.http.HttpStatus;

public class ClusterNotFoundException extends BaseRunException
{
    
    private static final long serialVersionUID = -8815724515346564011L;

    public ClusterNotFoundException()
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.CLUSTER_NOT_FOUND.getCode(),
            ErrorCode.CLUSTER_NOT_FOUND.getMessage());
    }
    
    public ClusterNotFoundException(String excepMessage)
    {
        super(HttpStatus.NOT_FOUND, ErrorCode.CLUSTER_NOT_FOUND.getCode(),
            ErrorCode.CLUSTER_NOT_FOUND.getMessage(), excepMessage);
    }
    
}

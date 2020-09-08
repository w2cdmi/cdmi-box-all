package pw.cdmi.box.uam.exception;

import org.springframework.http.HttpStatus;

public class ExistResourceStrategyConflictException extends BaseRunException
{
    
    private static final long serialVersionUID = -3002892640656677750L;
    
    public ExistResourceStrategyConflictException()
    {
        super(HttpStatus.CONFLICT, ErrorCode.EXIST_RESOURCE_STRATEGY_CONFLICT.getCode(),
            ErrorCode.EXIST_RESOURCE_STRATEGY_CONFLICT.getMessage());
    }
    
}

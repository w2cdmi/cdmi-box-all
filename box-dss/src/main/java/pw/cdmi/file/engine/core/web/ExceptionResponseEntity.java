package pw.cdmi.file.engine.core.web;

import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import pw.cdmi.file.engine.core.exception.BusinessException;
import pw.cdmi.file.engine.core.exception.errorcode.ErrorCode;
import pw.cdmi.file.engine.filesystem.exception.FSException;

public class ExceptionResponseEntity extends CommonResponseEntiy
{
    
    public ExceptionResponseEntity(String requestID)
    {
        super(requestID);
        this.setCode("Error");
    }
    
    public ExceptionResponseEntity(String requestID, BusinessException exception)
    {
        this(requestID);
        this.setCode(exception.getCode());
        this.setMessage(exception.getMessage());
    }
    
    public ExceptionResponseEntity(String requestID, FSException exception)
    {
        this(requestID);
        this.setCode("FSException");
        this.setMessage(exception.getMessage());
    }
    
    public ExceptionResponseEntity(String requestID, MissingServletRequestParameterException exception)
    {
        this(requestID);
        this.setCode(ErrorCode.MissingParameterException.getCode());
        this.setMessage(exception.getMessage());
    }
    
    public ExceptionResponseEntity(String requestID, TypeMismatchException exception)
    {
        this(requestID);
        this.setCode(ErrorCode.BadRequestException.getCode());
        this.setMessage(exception.getMessage());
    }
}

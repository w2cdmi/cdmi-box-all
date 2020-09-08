/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import pw.cdmi.core.JsonMapper;
import pw.cdmi.file.engine.core.exception.BusinessException;
import pw.cdmi.file.engine.core.web.ExceptionResponseEntity;
import pw.cdmi.file.engine.filesystem.exception.FSException;
import pw.cdmi.file.engine.object.exception.BadRequestException;
import pw.cdmi.file.engine.object.exception.CallbackFailedException;
import pw.cdmi.file.engine.object.exception.InvalidParameterException;
import pw.cdmi.file.engine.object.exception.NotAcceptableException;
import pw.cdmi.file.engine.object.exception.ObjectAlreadyExistException;
import pw.cdmi.file.engine.object.exception.ObjectNotFoundException;
import pw.cdmi.file.engine.object.exception.PreconditionFailedException;
import pw.cdmi.file.engine.object.rest.support.RequestConstants;

/**
 * 
 * @author s90006125
 * 
 */
@ControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private JsonMapper jsonMapper;
    
    @PostConstruct
    public void init()
    {
        jsonMapper = JsonMapper.nonEmptyMapper();
    }
    
    /**
     * 默认的异常
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefaultException(Exception exception, HttpServletRequest request)
    {
        String message = "Sorry, Server Exception!";
        LOGGER.warn("Sorry, Server Exception!", exception);
        ExceptionResponseEntity entry = new ExceptionResponseEntity(getRequestID(request));
        entry.setMessage(message);
        String body = jsonMapper.toJson(entry);
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    
    
    /**
     * 参数缺失异常
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleBusinessException(MissingServletRequestParameterException exception,
        HttpServletRequest request)
    {
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 参数缺失异常
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object> handleBusinessException(TypeMismatchException exception,
        HttpServletRequest request)
    {
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 业务异常
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException exception,
        HttpServletRequest request)
    {
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 文件系统异常
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(FSException.class)
    public ResponseEntity<Object> handleFSException(FSException exception, HttpServletRequest request)
    {
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    public HttpStatus getHttpStatus(HttpServletRequest request, Exception exception)
    {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof BusinessException)
        {
            if (exception instanceof BadRequestException)
            {
                status = HttpStatus.BAD_REQUEST;
            }
            else if (exception instanceof InvalidParameterException)
            {
                status = HttpStatus.BAD_REQUEST;
            }
            else if (exception instanceof ObjectNotFoundException)
            {
                status = HttpStatus.NOT_FOUND;
            }
            else if (exception instanceof NotAcceptableException)
            {
                status = HttpStatus.NOT_ACCEPTABLE;
            }
            else if (exception instanceof ObjectAlreadyExistException)
            {
                status = HttpStatus.CONFLICT;
            }
            else if (exception instanceof PreconditionFailedException)
            {
                status = HttpStatus.PRECONDITION_FAILED;
            }
            else if (exception instanceof CallbackFailedException)
            {
                status = ((CallbackFailedException) exception).getStatus();
            }
        }
        else if (exception instanceof MissingServletRequestParameterException
            || exception instanceof TypeMismatchException)
        {
            status = HttpStatus.BAD_REQUEST;
        }
        
        request.setAttribute(HttpStatus.class.toString(), status);
        
        return status;
    }
    
    /**
     * 获取请求ID
     * 
     * @return
     */
    protected String getRequestID(HttpServletRequest request)
    {
        return (String) request.getAttribute(RequestConstants.REQUEST_ID);
    }
}

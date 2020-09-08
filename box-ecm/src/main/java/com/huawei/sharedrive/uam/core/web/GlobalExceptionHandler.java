package com.huawei.sharedrive.uam.core.web;

import com.fasterxml.jackson.core.JsonParseException;
import com.huawei.sharedrive.uam.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pw.cdmi.core.log.LoggerUtil;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private JsonMapper jsonMapper;

    public HttpStatus getHttpStatus(HttpServletRequest request, Exception exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (exception instanceof BaseRunNoStackException) {
            status = ((BaseRunNoStackException) exception).getHttpcode();
        } else if (exception instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof BaseRunException) {
            status = ((BaseRunException) exception).getHttpcode();
        } else if (exception instanceof BusinessException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof MissingServletRequestParameterException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof ServletRequestBindingException) {
            status = HttpStatus.BAD_REQUEST;
        }
        request.setAttribute(HttpStatus.class.toString(), status);

        return status;
    }
    
    /**
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(BaseRunException.class)
    public ResponseEntity<Object> handleBusinessException(BaseRunException exception, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("BaseRunException", exception);
        } else {
            LOGGER.warn("BaseRunException", exception);
        }

        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(BaseRunNoStackException.class)
    public ResponseEntity<Object> handleBusinessNoStackException(BaseRunNoStackException exception, HttpServletRequest request) {
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleBusinessException(MissingServletRequestParameterException exception, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MissingServletRequestParameterException", exception);
        } else {
            LOGGER.warn("MissingServletRequestParameterException", exception);
        }

        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefaultException(Exception exception, HttpServletRequest request) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exception, url=" + request.getRequestURL(), exception);
        } else {
            LOGGER.warn("Exception, url=" + request.getRequestURL(), exception);
        }
        if (exception instanceof HttpMessageNotReadableException) {
            BadRequestException bad = new BadRequestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        if (exception instanceof JsonParseException) {
            BadRequestException bad = new BadRequestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        if (exception instanceof IllegalArgumentException) {
            BadRequestException bad = new BadRequestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(request), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        ExceptionResponseEntity entry = new ExceptionResponseEntity(getRequestID(request));

        entry.setMessage("Sorry, Server Exception!");
        String body = jsonMapper.toJson(entry);
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    @PostConstruct
    public void init()
    {
        jsonMapper = JsonMapper.nonEmptyMapper();
    }

    protected String getRequestID(HttpServletRequest request) {
        return LoggerUtil.getCurrentLogID();
    }
}

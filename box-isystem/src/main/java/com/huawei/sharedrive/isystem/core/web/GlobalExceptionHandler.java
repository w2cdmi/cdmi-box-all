/**
 * 
 */
package com.huawei.sharedrive.isystem.core.web;

import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @author d00199602
 * 
 */
@ControllerAdvice
public class GlobalExceptionHandler
{
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private JsonMapper jsonMapper = new JsonMapper();
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public final ResponseEntity<?> handleAjaxException(ConstraintViolationException ex, WebRequest request)
    {
        logger.error("GlobalExceptionHandler handleAjaxException", ex);
        Map<String, String> errors = BeanValidators.extractPropertyAndMessage(ex.getConstraintViolations());
        String body = jsonMapper.toJson(errors);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity(body, headers, HttpStatus.BAD_REQUEST);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ExceptionHandler(value = {Exception.class})
    public final ResponseEntity<?> handleDefaultException(Exception ex, WebRequest request)
    {
        logger.error("GlobalExceptionHandler handleDefaultException", ex);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity("", headers, HttpStatus.BAD_REQUEST);
    }
    
}

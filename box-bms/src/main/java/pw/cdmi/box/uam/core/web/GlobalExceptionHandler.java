package pw.cdmi.box.uam.core.web;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;

import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.BaseRunException;
import pw.cdmi.box.uam.exception.BaseRunNoStackException;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.ExceptionResponseEntity;
import pw.cdmi.core.log.LoggerUtil;

@ControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private JsonMapper jsonMapper;
    
    public HttpStatus getHttpStatus(HttpServletRequest request, Exception exception)
    {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (exception instanceof BaseRunNoStackException)
        {
            status = ((BaseRunNoStackException) exception).getHttpcode();
        }
        if (exception instanceof BaseRunException)
        {
            status = ((BaseRunException) exception).getHttpcode();
        }
        else if (exception instanceof BadRquestException)
        {
            status = HttpStatus.BAD_REQUEST;
        }
        else if (exception instanceof BusinessException)
        {
            status = HttpStatus.BAD_REQUEST;
        }
        else if (exception instanceof MissingServletRequestParameterException)
        {
            status = HttpStatus.BAD_REQUEST;
        }
        else if (exception instanceof ServletRequestBindingException)
        {
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
    public ResponseEntity<Object> handleBusinessException(BaseRunException exception,
        HttpServletRequest request)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("BaseRunException", exception);
        }
        else
        {
            LOGGER.warn("BaseRunException", exception);
        }
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(BaseRunNoStackException.class)
    public ResponseEntity<Object> handleBusinessNoStackException(BaseRunNoStackException exception,
        HttpServletRequest request)
    {
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleBusinessException(MissingServletRequestParameterException exception,
        HttpServletRequest request)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("MissingServletRequestParameterException", exception);
        }
        else
        {
            LOGGER.warn("MissingServletRequestParameterException", exception);
        }
        
        String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), exception));
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    /**
     * 
     * @param e
     * @param request
     * @param writer
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefaultException(Exception exception, HttpServletRequest request)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Exception", exception);
        }
        else
        {
            LOGGER.warn("Exception", exception);
        }
        if (exception instanceof HttpMessageNotReadableException)
        {
            BadRquestException bad = new BadRquestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        if (exception instanceof JsonParseException)
        {
            BadRquestException bad = new BadRquestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        if (exception instanceof IllegalArgumentException)
        {
            BadRquestException bad = new BadRquestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        if (exception instanceof TypeMismatchException)
        {
            BadRquestException bad = new BadRquestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        if (exception instanceof BindException)
        {
            BadRquestException bad = new BadRquestException(exception);
            String body = jsonMapper.toJson(new ExceptionResponseEntity(getRequestID(), bad));
            return new ResponseEntity<Object>(body, getHttpStatus(request, bad));
        }
        ExceptionResponseEntity entry = new ExceptionResponseEntity(getRequestID());
        entry.setMessage("Sorry, Server Exception!");
        String body = jsonMapper.toJson(entry);
        return new ResponseEntity<Object>(body, getHttpStatus(request, exception));
    }
    
    @PostConstruct
    public void init()
    {
        jsonMapper = JsonMapper.nonEmptyMapper();
    }
    
    protected String getRequestID()
    {
        return LoggerUtil.getCurrentLogID();
    }
}

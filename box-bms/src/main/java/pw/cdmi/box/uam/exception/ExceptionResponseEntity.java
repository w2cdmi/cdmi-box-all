package pw.cdmi.box.uam.exception;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.util.HtmlUtils;

public class ExceptionResponseEntity extends CommonResponseEntiy
{
    private String type;
    
    private static final String ERROR = "error";
    
    public ExceptionResponseEntity(String requestID)
    {
        super(requestID);
        this.setType(ERROR);
    }
    
    public ExceptionResponseEntity(String requestID, BaseRunException exception)
    {
        this(HtmlUtils.htmlEscape(requestID));
        this.setCode(HtmlUtils.htmlEscape(exception.getCode()));
        this.setMessage(HtmlUtils.htmlEscape(exception.getMsg()));
        this.setType(ERROR);
    }
    
    public ExceptionResponseEntity(String requestID, BaseRunNoStackException exception)
    {
        this(HtmlUtils.htmlEscape(requestID));
        this.setCode(HtmlUtils.htmlEscape(exception.getCode()));
        this.setMessage(HtmlUtils.htmlEscape(exception.getMsg()));
        this.setType(ERROR);
    }
    
    public ExceptionResponseEntity(String requestID, MissingServletRequestParameterException exception)
    {
        this(HtmlUtils.htmlEscape(requestID));
        this.setType(ERROR);
        this.setCode(ErrorCode.MissingParameter.getCode());
        this.setMessage(exception.getMessage() + "name:" + exception.getParameterName() + "type:"
            + exception.getParameterType());
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
}

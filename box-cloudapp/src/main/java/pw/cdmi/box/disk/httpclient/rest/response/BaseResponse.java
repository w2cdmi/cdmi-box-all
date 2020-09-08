package pw.cdmi.box.disk.httpclient.rest.response;

/**
 * 
 * <ol>
 * <li>code {@link BaseResponse#code}
 * <li>message {@link BaseResponse#message}
 * <li>request_id {@link BaseResponse#request_id}
 * <li>type {@link BaseResponse#type}
 * <ol>
 */
public class BaseResponse
{
    private String type;
    
    private String code;
    
    private String message;
    
    private String requestId;
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public String getRequestId()
    {
        return requestId;
    }
    
    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }
    
}

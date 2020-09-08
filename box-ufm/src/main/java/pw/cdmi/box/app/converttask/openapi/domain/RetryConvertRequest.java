package pw.cdmi.box.app.converttask.openapi.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RetryConvertRequest
{
  
	
	
	private long ownerId;

    private final static Logger LOGGER = LoggerFactory.getLogger(RetryConvertRequest.class);
    
    public RetryConvertRequest()
    {
        
    }
    
    
    
    public RetryConvertRequest(long ownerId) {
		
		
		this.ownerId=ownerId;
	}



	public void checkParameter() 
    {
        
    }





	public long getOwnerId() {
		return ownerId;
	}



	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
    
   
    
   
    
}

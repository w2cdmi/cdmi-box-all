package pw.cdmi.box.disk.converttask.client.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.exception.BadRquestException;


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



	public void checkParameter() throws BadRquestException
    {
        
    }





	public long getOwnerId() {
		return ownerId;
	}



	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
    
   
    
   
    
}

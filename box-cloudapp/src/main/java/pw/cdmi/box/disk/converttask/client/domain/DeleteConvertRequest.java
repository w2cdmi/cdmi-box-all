package pw.cdmi.box.disk.converttask.client.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.exception.BadRquestException;


public class DeleteConvertRequest
{
  
	private String[] taskids;
	
	private long ownerId;

    private final static Logger LOGGER = LoggerFactory.getLogger(DeleteConvertRequest.class);
    
    public DeleteConvertRequest()
    {
        
    }
    
    
    
    public DeleteConvertRequest(String[] taskids,long ownerId) {
		
		this.taskids = taskids;
		this.ownerId=ownerId;
	}



	public void checkParameter() throws BadRquestException
    {
        
    }



	public String[] getTaskids() {
		return taskids;
	}



	public void setTaskids(String[] taskids) {
		this.taskids = taskids;
	}



	public long getOwnerId() {
		return ownerId;
	}



	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
    
   
    
   
    
}

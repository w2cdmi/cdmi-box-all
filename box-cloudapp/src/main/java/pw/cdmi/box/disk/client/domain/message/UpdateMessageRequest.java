package pw.cdmi.box.disk.client.domain.message;

import java.io.Serializable;

/**
 * @version  CloudStor CSE Service Platform Subproject, 2015-3-25
 * @see  
 * @since  
 */
public class UpdateMessageRequest implements Serializable
{

    private static final long serialVersionUID = 6516811537929379261L;
    
    private String status;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}

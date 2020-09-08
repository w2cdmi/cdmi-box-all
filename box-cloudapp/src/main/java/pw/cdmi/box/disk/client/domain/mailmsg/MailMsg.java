package pw.cdmi.box.disk.client.domain.mailmsg;

public class MailMsg
{
    /** mail message type -0.share */
    public final static String SOURCE_SHARE = "share";
    
    /** mail message type -1.link */
    public final static String SOURCE_LINK = "link";
    
    private long sender;
    
    private String source;
    
    /** node owner */
    private long ownedBy;
    
    /** node id */
    private long nodeId;
    
    /** mail subject*/
    private String subject;
    
    /** mail message*/
    private String message;

    public long getSender()
    {
        return sender;
    }

    public void setSender(long sender)
    {
        this.sender = sender;
    }

    public long getOwnedBy()
    {
        return ownedBy;
    }

    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }

    public long getNodeId()
    {
        return nodeId;
    }

    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }
    
    
}

package pw.cdmi.box.disk.client.domain.message;

import pw.cdmi.core.exception.InvalidParamException;

public enum MessageType
{
    SHARE("share", (byte) 1),
    
    DELETE_SHARE("deleteShare", (byte) 2),
    
    TEAMSPACE_UPLOAD("teamspaceUpload", (byte) 3),
    
    TEAMSPACE_ADD_MEMBER("teamspaceAddMember", (byte) 4),
    
    LEAVE_TEAMSPACE("leaveTeamspace", (byte) 5),
    
    TEAMSPACE_DELETE_MEMBER("teamspaceDeleteMember", (byte) 6),
    
    GROUP_ADD_MEMBER("groupAddMember", (byte) 7),
    
    LEAVE_GROUP("leaveGroup", (byte) 8),
    
    GROUP_DELETE_MEMBER("groupDeleteMember", (byte) 9),
    
    TEAMSPACE_ROLE_UPDATE("teamspaceRoleUpdate", (byte) 10),
    
    GROUP_ROLE_UPDATE("groupRoleUpdate", (byte) 11),
    
    SYSTEM("system", (byte) 12);
    
    private String type;
    
    private byte value;
    
    private MessageType(String type, byte value)
    {
        this.type = type;
        this.value = value;
    }
    
    public static MessageType getMessageType(byte value)
    {
        for (MessageType messageType : MessageType.values())
        {
            if (messageType.getValue() == value)
            {
                return messageType;
            }
        }
        return null;
    }
    
    public static MessageType getMessageType(String type)
    {
        for (MessageType messageType : MessageType.values())
        {
            if (messageType.getType().equals(type))
            {
                return messageType;
            }
        }
        return null;
    }
    
    public static String getType(byte value)
    {
        for (MessageType messageType : MessageType.values())
        {
            if (messageType.getValue() == value)
            {
                return messageType.getType();
            }
        }
        throw new InvalidParamException("Invalid message type value: " + value);
    }
    
    public static byte getValue(String type)
    {
        for (MessageType messageType : MessageType.values())
        {
            if (messageType.getType().equals(type))
            {
                return messageType.getValue();
            }
        }
        throw new InvalidParamException("Invalid message type: " + type);
    }
    
    public String getType()
    {
        return type;
    }
    
    public byte getValue()
    {
        return value;
    }
}

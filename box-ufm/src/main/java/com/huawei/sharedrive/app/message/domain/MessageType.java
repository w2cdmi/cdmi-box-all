package com.huawei.sharedrive.app.message.domain;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public enum MessageType
{
    /** 共享 */
    SHARE("share", (byte) 1),
    
    /** 取消共享 */
    DELETE_SHARE("deleteShare", (byte) 2),
    
    /** 团队空间上传文件 */
    TEAMSPACE_UPLOAD("teamspaceUpload", (byte) 3),
    
    /** 加入团队空间 */
    TEAMSPACE_ADD_MEMBER("teamspaceAddMember", (byte) 4),
    
    /** 退出团队空间 */
    LEAVE_TEAMSPACE("leaveTeamspace", (byte) 5),
    
    /** 被团队空间管理员移出团队空间 */
    TEAMSPACE_DELETE_MEMBER("teamspaceDeleteMember", (byte) 6),
    
    /** 加入群组 */
    GROUP_ADD_MEMBER("groupAddMember", (byte) 7),
    
    /** 退出群组 */
    LEAVE_GROUP("leaveGroup", (byte) 8),
    
    /** 被群组管理员移出群组 */
    GROUP_DELETE_MEMBER("groupDeleteMember", (byte) 9),
    
    /** 团队空间权限角色变更 */
    TEAMSPACE_ROLE_UPDATE("teamspaceRoleUpdate", (byte) 10),
    
    /** 群组权限角色变更 */
    GROUP_ROLE_UPDATE("groupRoleUpdate", (byte) 11),
    
    /** 系统消息 */
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

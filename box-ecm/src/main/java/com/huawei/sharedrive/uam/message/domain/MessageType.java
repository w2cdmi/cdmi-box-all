package com.huawei.sharedrive.uam.message.domain;

public class MessageType
{
    public static final byte SHARE = 1;
    
    public static final byte CANCEL_SHARE = 2;
    
    public static final byte TEAMSPACE_NEW_FILE = 3;
    
    public static final byte JOIN_TEAMSPACE = 4;
    
    public static final byte QUIT_TEAMSPACE = 5;
    
    public static final byte REMOVE_FROM_TEAMSPACE = 6;
    
    public static final byte JOIN_GROUP = 7;
    
    public static final byte QUIT_GROUP = 8;
    
    public static final byte REMOVE_FROM_GROUP = 9;
    
    public static final byte TEAMSPACE_ROLE_CHANGE = 10;
    
    public static final byte GROUP_ROLE_CHANGE = 11;
    
    public static final String SYSTEM_ANNOUNCEMENT = "system";
    
}

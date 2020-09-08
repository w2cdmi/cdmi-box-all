package pw.cdmi.box.uam.message.domain;

public class MessageType
{
    /** 共享 */
    public static final byte SHARE = 1;
    
    /** 取消共享 */
    public static final byte CANCEL_SHARE = 2;
    
    /** 团队空间新增文件 */
    public static final byte TEAMSPACE_NEW_FILE = 3;
    
    /** 加入团队空间 */
    public static final byte JOIN_TEAMSPACE = 4;
    
    /** 退出团队空间 */
    public static final byte QUIT_TEAMSPACE = 5;
    
    /** 被团队空间管理员移出团队空间 */
    public static final byte REMOVE_FROM_TEAMSPACE = 6;
    
    /** 加入群组 */
    public static final byte JOIN_GROUP = 7;
    
    /** 退出群组 */
    public static final byte QUIT_GROUP = 8;
    
    /** 被群组管理员移出群组 */
    public static final byte REMOVE_FROM_GROUP = 9;
    
    /** 团队空间权限角色变更 */
    public static final byte TEAMSPACE_ROLE_CHANGE = 10;
    
    /** 群组权限角色变更 */
    public static final byte GROUP_ROLE_CHANGE = 11;
    
    /** 系统公告 */
    public static final String SYSTEM_ANNOUNCEMENT = "system";
    
}

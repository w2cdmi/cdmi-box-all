/**
 * 
 */
package com.huawei.sharedrive.app.event.domain;

/**
 * @author q90003805
 * 
 */
public enum EventType
{
    INODE_CONTENT_CHANGE(1),
    
    INODE_COPY(2),
    
    INODE_CREATE(3),
    
    INODE_DELETE(4),
    
    INODE_DOWNLOAD(5),
    
    INODE_MOVE(6),
    
    INODE_PRELOAD_BEGIN(7),
    
    INODE_PRELOAD_END(8),
    
    INODE_PREVIEW(9),
    
    INODE_RENAME(10),
    
    INODE_UPDATE_SYNC(11),
    
    INODE_UPDATE_NAME_SYNC(12),
    
    VERSION_DELETE(13), // 删除版本文件
    
    VERSION_RESTORE(14), // 还原版本文件
    
    LINK_CREATE(15),
    
    LINK_DELETE(16),
    
    LINK_DOWNLOAD(17),
    
    LINK_PREVIEW(18),
    
    LINK_UPDATE(19),
    
    SHARE_CREATE(20),
    
    SHARE_DELETE(21),
    
    SHARE_UPDATE(22),
    
    TERMINAL_DISABLE(23),
    
    TERMINAL_ENABLE(24),
    
    TRASH_CLEAR(25),
    
    TRASH_INODE_DELETE(26),
    
    TRASH_INODE_RECOVERY(27),
    
    TRASH_RECOVERY(28),
    
    USER_CREATE(29),
    
    USER_LOCKED(30),
    
    USER_LOGIN(31),
    
    USER_LOGIN_FAIL(32),
    
    USER_LOGOUT(33),
    
    TEAMSPACE_CREATE(34),
    
    TEAMSPACE_DELETE(35),
    
    TEAMSPACE_UPDATE(36),
    
    TEAMSPACE_CHANGEOWNER(37),
    
    TEAMSPACE_MEMBER_CREATE(38),
    
    TEAMSPACE_MEMBER_DELETE(39),
    
    TEAMSPACE_MEMBER_UPDATE(40),
    
    ACL_CREATE(41),
    
    ACL_DELETE(42),
    
    ACL_UPDATE(43),
    
    MIRROR_COPY_OBJECT_SUCCESS(44),
    
    GROUP_CREATE(45),
    
    GROUP_DELETE(46),
    
    GROUP_UPDATE(47),
    
    GROUP_MEMBER_CREATE(48),
    
    GROUP_MEMBER_DELETE(49),
    
    GROUP_MEMBER_UPDATE(50),
    
    USER_DATA_MIGRATION(60),
    
    
    INNER_DEDUP(98),//内部DEDUP事件，用于通知mirror模块使用
    
    OTHERS(99),
	
	PREVIEW_URL(100);
    
    
    
    private int code;
    
    private EventType(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return code;
    }
    
}

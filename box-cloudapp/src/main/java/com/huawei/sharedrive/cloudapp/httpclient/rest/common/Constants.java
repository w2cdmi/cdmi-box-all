package com.huawei.sharedrive.cloudapp.httpclient.rest.common;

import pw.cdmi.box.disk.utils.BasicConstants;

public final class Constants extends BasicConstants {

	/* ============== HTTP Constants============== */
	public final static String SERVER_ADDRESS = "";

	public static final String RESOURCE_NODE = "/api/v2/nodes";
	
	public static final String RESOURCE_ACCOUNTS = "/api/v2/accounts";

	public static final String RESOURCE_MESSAGE = "/api/v2/messages";

	public static final String RESOURCE_ANNOUNCEMENT = "/api/v2/announcements";

	public static final String RESOURCE_USER = "/api/v2/users/";
	
	public static final String FILES_API_PATH = "/api/v2/files/";

	public static final String FOLDERS_API_PATH = "/api/v2/folders/";

	public static final String API_PATH_OF_LINK = "/api/v2/links/";

	public static final String API_PATH_OF_SHARE = "/api/v2/shareships/";

	public static final String NODES_API_PATH = "/api/v2/nodes/";

	public static final String DOCTYPE_API_PATH = "/api/v2/doctype";

	public static final byte HAS_SET_LINK_FLAG = 1;

	public static final byte HAS_SHARED_NODE_FLAG = 1;

	public static final String DEFAULT_THUMB_SIZE_URL = "/thumbUrl?height=32&width=32";

	public static final String MEDIUM_THUMB_SIZE_URL = "/thumbUrl?height=200&width=200";

	public static final String API_TEAM_SPACES = "/api/v2/teamspaces";

	public static final String NODES_API_ROLES = "/api/v2/roles";

	public static final String NODES_API_ACL = "/api/v2/acl";

	public static final String NODES_API_PERMISSION = "/api/v2/permissions";

	public static final String NODES_API_MAILMSG = "/api/v2/mailmsgs/";

	public static final String SPACE_TYPE_USER = "user";

	public static final String SPACE_TYPE_GROUP = "group";
	
	public static final String SPACE_TYPE_ORGANIZATION= "department";

	public static final String SPACE_TYPE_TEAM = "team";

	public static final String SPACE_TYPE_SYSTEM = "system";

	public static final String SPACE_TYPE_PUBLIC = "public";

	public static final long SPACE_ID_TEAM_PUBLIC = 1;

	public static final int SPACE_STATUS_ENABLE = 0;

	public static final int SPACE_STATUS_DISABLE = 1;

	public static final String SPACE_ROLE_ADMIN = "admin";

	public static final String SPACE_ROLE_MANAGER = "manager";

	public static final String SPACE_ROLE_MEMBER = "member";

	public static final String API_MAILS = "/api/v2/mails";

	public static final String RESOURCE_FAVORITE = "/api/v2/favorites";

	public static final int GROUP_LIMIT_DEFAULT = 100;

	public static final long GROUP_OFFSET_DEFAULT = 0;

	public static final String GROUP_TYPE_DEFAULT = "all";

	public static final byte GROUP_TYPE_PUBLIC = 1;

	public static final byte GROUP_STATUS_DISABLE = 1;

	public static final String TYPE_PUBLIC = "public";

	public static final String TYPE_PRIVATE = "private";

	public static final String STATUS_DISABLE = "disable";

	public static final String STATUS_ENABLE = "enable";
   public static final int DEFAULT_PAGE_SIZE = 15;
    
    public static final String API_USER_FEED_BACK_LIST = "/api/v2/terminal/list";
    
    public static final String API_ADD_FEED_BACK = "/api/v2/terminal/add";
    
    public static final String API_DELETE_FEED_BACK = "/api/v2/terminal/delete";
    
    public static final String API_DELETE_FEED_BACK_SUB = "/api/v2/terminal/deleteSub";
    
    public static final String API_UPDATE_FEED_BACK = "/api/v2/terminal/update";
    
    public static final String API_USER_FEED_BACK_COUNT="/api/v2/terminal/count";
    
    public static final String API_GET_FEED_BACK = "/api/v2/terminal/findFeedBack";
    
    public static final String API_GET_FEED_BACK_SUB = "/api/v2/terminal/findFeedBackSubs";
    
    public static final String API_UPDATE_FEED_BACK_TIME = "/api/v2/terminal/updateFeedBack";
    
    public static final String API_ADD_FEED_BACK_SUB = "/api/v2/terminal/addFeedBackSub";
    
	public static final String CONVERT_TASK = "/api/v2/convertTask";

}

package com.huawei.sharedrive.app.exception;

/**
 * 
 * @author c00110381
 * 
 */
public enum ErrorCode
{
    ASYNC_NODES_CONFLICT("AsyncNodesConflict",
        "Async task has conflicts with samename nodee or nonexistent parent nodes."),
    /**
     * 存储区域不存在
     */
    NO_SUCH_TOKEN("NoSuchToken", "token is not exsit."),
    /**
     * 存储区域不存在
     */
    NO_SUCH_REGION("NoSuchRegion", "region is not exsit."),
    /**
     * 用户存储区域冲突
     */
    CONFLICT_REGION("ConflictRegion", "The user region has been setted."),
    /**
     * 登录名冲突
     */
    CONFLICT_USER("ConflictUser", "A same name user is already exsit."),
    
    /**
     * 企业二级域名冲突
     */
    CONFLICT_ACCOUNT("ConflictAccount", "A same domain account is already exist."),
    /**
     * 
     */
    BAD_REQUEST("BadRequest", "The requested resource or the request parameter error."),
    /**
     * 
     */
    CLIENTUNAUTHORIZED("ClientUnauthorized", "Authentication failed, The terminal is disabled."),
    /**
     * 数据库事务回滚异常
     */
    DB_ROLL_BACK_EXCEPTION("TransactionRollbackError", "Fail to rollback the db transaction."),
    /**
     * 数据库事务提交异常
     */
    DB_SUBMIT_EXCEPTION("TransactionCommitError", "Fail to commit the db transaction."),
    /**
     * 
     */
    FILES_CONFLICT("RepeatNameConflict", "A same name file or folder is already exsit."),
    /**
     * 
     */
    FORBIDDEN_OPER("Forbidden", "The operation is prohibited."),
    
    FORBIDDEN_LINK_MAIL_OPER("DynamicMailForbidden", "The operation is prohibited."),
    
    FORBIDDEN_LINK_PHONE_OPER("DynamicPhoneForbidden", "The operation is prohibited."),
    
    SECURITY_MATRIX_FORBIDDEN("SecurityMatrixForbidden", "The operation is prohibited by security matrix."),
    /**
     * 
     */
    INTERNAL_SERVER_ERROR("InternalServerError", "Server internal error, please try again later."),
    /**
     * 
     */
    INVALID_PARAMTER("InvalidParameter", "The request parameter is invalid."),
    
    FILE_SCANNING("FileScanning", "The request file is not ready"),
    
    SCANNED_FORBIDDEN("ScannedForbidden", "This file is not allowed to be downloaded"),
    
    VIRUS_FORBIDDEN("VirusForbidden", "This file is not allowed to be operated for detected virus"),
    
    CLUSTER_NOT_FOUND("ClusterNotFound", "Can not found the cluster"),
    
    FILE_CONVERTING("FileConverting", "The request file is converting"), FILE_CONVERT_FAILED(
        "FileConvertFailed", "The request file is failed to convert"), FILE_CONVERT_NOT_SUPPORT(
        "FileConvertNotSupport", "The request file is not support to convert"),
    /**
     * 
     */
    INVALID_RANGE("InvalidRange", "The request parameter is out of range."),
    
    /**
     * 
     */
    LINK_CONFLICT("LinkExistedConflict", "This  folder or file is already set a link."),
    /**
     * 
     */
    LINK_EXPIRED("LinkExpired", "This link is expired."),
    /**
     * 
     */
    LINK_NOT_EFFECTIVE("LinkNotEffective", "This link does not effective."),
    /**
     * 公共部分 start 签名错误
     */
    LOGINUNAUTHORIZED("Unauthorized", "Authentication fails, the user name or password is incorrect."),
    /**
     * 
     */
    METHOD_NOT_ALLOWED("MethodNotAllowed", "This method does not allow."),
    /**
     * 
     */
    MISSING_PARAMETER("MissingParameter", "The request missing required parameters"),
    /**
     * 从网络读取数据失败（比如客户端上传文件时，服务端不能获取到整个文件内容）
     */
    NetworkException("NetworkException",
        "Network exception, please try again this operation when the network is not so busy."),
    
    /**
     * 
     */
    NO_SUCH_FILE("NoSuchFile", "This file does not exist."),
    /**
     * 
     */
    NO_SUCH_FOLDER("NoSuchFolder", "This folder does not exist."),
    
    NO_SUCH_PARENT("NoSuchParent", "This parent does not exist."),
    
    NO_SUCH_SOURCE("NoSuchSource", "The source does not exist."),
    
    NO_SUCH_DEST("NoSuchDest", "The destination does not exist."),
    
    /** 外链错误 */
    NO_SUCH_LINK("NoSuchLink", "This Link does not exist."),
    /**
     * 
     */
    NO_SUCH_ITEM("NoSuchItem", "The node of request not found."),
    /**
     * 
     */
    NO_SUCH_USER("NoSuchUser", "This user does not exist."),
    /**
     * 
     */
    NO_SUCH_VERSION("NoSuchVersion", "This version does not exist."),
    /**
     * 
     */
    NO_SUCH_TEAMSPACE("NoSuchTeamspace", "This teamspace does not exist."),
    
    /**
     * 消息不存在
     */
    NO_SUCH_MESSAGE("NoSuchMessage", "This message does not exist."),
    /**
     * 
     */
    INVALID_SPACE_STATUS("InvalidSpaceStatus", "User space in a non normal state."),
    /**
     * 
     */
    NO_SUCH_ACL("NoSuchACL", "This acl does not exist."),
    
    NO_SUCH_GROUP("NoSuchGroup", "This group does not exist."),
    
    NO_SUCH_ACCESSKEY("NoSuchAccessKey", "This accessKey does not exist."),
    
    NO_SUCH_ACCOUNT("NoSuchAccount", "This account does not exist."),
    
    NO_SUCH_APPLICATION("NoSuchApplication", "This application does not exist."),
    /**
     * 复制或移动到子文件夹冲突
     */
    SUB_FOLDER_CONFILICT("SubFolderConflict", "The dest folder is sub folder for the src folder."),
    
    /**
     * 复制或移动时，目标节点和源节点相同冲突
     */
    SAME_NODE_CONFILICT("SameNodeConflict", "The dest folder is same as the src folder."),
    /**
     * 复制或移动时，目标节点是源节点的父文件夹
     */
    SAME_PARENT_CONFILICT("SameParentConflict", "The dest folder is parent for the src nodes."),
    /**
     * 
     */
    TOKENUNAUTHORIZED("Unauthorized", "Authentication fails, the token illegal or invalid."),
    /**
     * 
     */
    TOO_MANY_REQUESTS("TooManyRequests", "Too many requests, please try again later."),
    /**
     * 
     */
    USERLOCKED("UserLocked", "forbidden, the user is locked."),
    
    /**
     * 无效的团队角色
     */
    INVALID_TEAMROLE("InvalidTeamRole", "The teamRole is invalid."),
    
    /**
     * 无效的权限角色
     */
    INVALID_RESOURCE_ROLE("InvalidPermissionRole", "The permissionRole is invalid."),
    
    /**
     * 团队空间处于非正常状态
     */
    ABNORMAL_TEAMSPACE_STATUS("AbnormalTeamStatus", "The teamSpace is abnormal."),
    
    /**
     * 团队成员已存在
     */
    EXIST_MEMBER_CONFLICT("ExistMemberConflict", "The member is already exist."),
    
    /**
     * 团队空间已存在
     */
    EXIST_TEAMSPACE_CONFLICT("ExistTeamspaceConflict", "The teamSpace is already exist."),
    
    /**
     * 權限已存在
     */
    ACL_CONFLICT("ExistACLConflict", "The ACL is already exist."),
    
    /**
     * 收藏节点已存在
     */
    FAVORITE_CONFLICT("ExistFavoriteConflict", "The Favorite Node is already exist."),
    
    /**
     * 超过用户元数据最大限制
     */
    EXCEED_MAX_NODE_NUM("ExceedUserMaxNodeNum", "The sum of user nodes exceed the maximum"),
    
    /**
     * 超过最大外链数限制
     */
    EXCEED_MAX_LINK_NUM("ExceedMaxLinkNum", "The sum of node links exceed the maximum"),
    
    /**
     * 超过用户文件版本最大数限制
     */
    EXCEED_MAX_VERSION_NUM("ExceedFileMaxVersionNum", "The sum of file versions exceed the maximum"),
    
    EXCEED_MAX_TEAMSPACE_MEMBER_NUM("ExceedTeamSpaceMaxMemberNum",
        "The sum of teamSpace members exceed the maximum"),
    
    EXCEED_MAX_GROUP_MEMBER_NUM("ExceedMaxMembers", "The sum of members exceed the maximum"),
    
    EXCEED_QUOTA("ExceedQuota", "Perform the current operation will cause the quantity exceeds maximum"),
    /**
     * 刷新文件上传地址时, 如果原上传url和当前上传文件不匹配时抛出该异常
     */
    UNMATCHED_UPLOADURL("UnmatchedUploadUrl", "The upload url does not match"),
    
    /**
     * 获取文件缩略图下载地址时, 如果是不支持的文件类型时抛出该异常
     */
    INVALID_FILE_TYPE("InvalidFileType", "The file type is invalid."),
    
    /**
     * 没有有效的license
     */
    LICENSE_FORBIDDEN("LicenseInvalid", "The license is invalid."),
    
    /** 用户空间配额不足 */
    EXCEED_SPACE_QUOTA("ExceedQuota", "User space quota exceeded"),
    
    /** 文件大小超过个人可用容量 */
    EXCEED_USER_AVAILABLE_SPACE("ExceedUserAvailableSpace", 
        "This file size exceeded userAvailableSpace"), 
    
    /** 文件大小超过企业可用容量 */
    EXCEED_ENTERPRISE_AVAILABLE_SPACE("ExceedEnterpriseAvailableSpace",
        "This file size exceeded enterpriseAvailableSpace"),
    
    /**
     * 上传超过大小限制
     */
    UPLOAD_SIZE_TOO_LARGE("UploadSizeTooLarge", "The upload size is too large"),
    
    /**
     * 用户已经存在迁移任务
     */
    NO_AVAILABLE_REGION("NoAvailableRegion", "The request no available storage region."),
    
    EXIS_USER_MIGRATION_TASK("ExistMigrationTask",
        "The user had  size exist migration task,not allow to create new task。"),
    
    NO_SUCH_USER_MIGRATION_TASK("NoSuchUserMigrationTask", "This user's migration task does not exist."),
    
    TOO_MANY_MIGRATION_TASK("TooManyMigrationTask", "The system task too many,please later again."),
    
    NEEDSIGNDECLARATION("NeedSignDeclaration", "You should sign the Privacy Statement."),
    
    NEEDCHANGEPASSWORD("NeedChangePassword", "You should reset your passwod."),
	
    /**
     * 快捷目录已存在
     */
	EXSIT_SHORTCUT("ExsitShortcut", "The shortcut is already exist."),
    
    /**
     * 不支持预览的文件类型
     */
	PREVIEW_NOT_SUPPORTED("PreviewNotSupported", "The file is not supported to preview.");

    private String code;
    
    private String message;
    
    private ErrorCode(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public String getMessage()
    {
        return message;
    }
}

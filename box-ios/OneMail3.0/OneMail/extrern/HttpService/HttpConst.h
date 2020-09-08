
//

#import <Foundation/Foundation.h>

//接口类型枚举
typedef enum {
    //获取用户头像
    ServiceUserHeadIcon,
    //获取隐私申明内容
    ServiceUserDeclarationContent,
    //设置隐私签名状态
    ServiceUserDeclarationStatus,
    //邮箱配置信息获取
    ServiceUserEmailConfig,
    //用户登录
    ServiceUserLogin,
    //获取服务地址
    ServiceUserServerAddress,
    //刷新Token
    ServiceUserTokenRefresh,
    //用户注销
    ServiceUserLogout,
    //搜索用户
    ServiceUserSearch,
    //创建用户
    ServiceUserCreate,
    //更新用户
    ServiceUserUpdate,
    //获取用户信息
    ServiceUserInfo,
    //删除用户
    ServiceUserDelete,
    //发送邮件
    ServiceUserSendEmail,
    //设置邮件信息
    ServiceUserEmailMessageSet,
    //获取邮件信息
    ServiceUserEmailMessageGet,
    //获取外链配置信息
    ServiceUserSystermLinksOption,
    
    //校验客户端完整性
    ServiceClientCheckCode,
    //获取客户端版本信息
    ServiceClientInfo,
    
    //文件夹操作
    //获取文件夹信息
    ServiceFolderInfo,
    //列举文件夹
    ServiceFolderList,
    //创建文件夹
    ServiceFolderCreate,
    //删除文件夹
    ServiceFolderDelete,
    //重命名文件夹
    ServiceFolderRename,
    //移动文件夹
    ServiceFolderMove,
    //复制文件夹
    ServiceFolderCopy,

    //文件操作
    //获取文件内容
    ServiceFileInfo,
    //列举文件版本列表
    ServiceFileVersionList,
    //删除文件
    ServiceFileDelete,
    //重命名文件
    ServiceFileRename,
    //移动文件
    ServiceFileMove,
    //复制文件
    ServiceFileCopy,
    //文件搜索
    ServiceFileSearch,
    //文件缩略图
    ServiceFileThumbnail,
    
    //上传操作
    ServiceUpload,
    //文件预上传
    ServiceUploadPre,
    //文件整体上传
    ServiceUploadWhole,
    //文件分片上传
    ServiceUploadPart,
    //取消文件分片上传
    ServiceUploadCancel,
    //获取文件分片信息
    ServiceUploadPartInfo,
    //分片上传结束
    ServiceUploadFinish,
    
    //下载操作
    //文件下载
    ServiceDownload,
    //取消文件下载任务
    ServiceDownloadCancel,
    //暂停文件下载任务
    ServiceDownloadPause,
    //恢复文件下载任务
    ServiceDownloadResume,
    
    //共享操作
    //添加共享关系
    ServiceShareAdd,
    //列举收到的共享文件
    ServiceShareReceiveList,
    //列举发出的共享文件
    ServiceShareSendList,
    //列举文件共享用户
    ServiceShareUserList,
    //删除共享关系
    ServiceShareDelete,
    
    //外链操作
    //创建外链
    ServiceLinksCreate,
    //刷新外链
    ServiceLinksRefresh,
    //列举外链
    ServiceLinksInfoList,
    //删除外链
    ServiceLinksDelete,
    //获取外链信息
    ServiceLinkInfo,
    
    ServiceLinksObject,
    
    //团队空间
    //列举所有团队空间
    ServiceSpaceListAll,
    //列举用户团队空间
    ServiceSpaceList,
    //创建团队空间
    ServiceSpaceCreate,
    //更新团队空间
    ServiceSpaceUpdate,
    //获取团队空间信息
    ServiceSpaceInfo,
    //删除团队空间
    ServiceSpaceDelete,
    //团队空间成员添加
    ServiceSpaceMemberAdd,
    //获取团队空间成员信息
    ServiceSpaceMemberInfo,
    //团队空间成员信息更新
    ServiceSpaceMemberInfoUpdate,
    //列举团队空间成员
    ServiceSpaceMemberList,
    //团队空间成员删除
    ServiceSpaceMemberDelete,
    
    
    
} ServiceType;

typedef enum
{
    NoError,
    BadRequest,
    InvalidParameter,
    InvalidPart,
    InvalidRange,
    InvalidTeamRole,
    InvalidRegion,
    InvalidPermissonRole,
    InvalidFileType,
    UnmatchedDownloadUrl,
    
    Unauthorized,
    ClientUnauthorized,
    
    Forbidden,
    UserLocked,
    InvalidSpaceStatus,
    SourceForbidden,
    DestForbidden,
    
    NoFound,
    NoSuchUser,
    NoSuchNode,
    NoSuchItem,
    NoSuchFolder,
    NoSuchFile,
    NoSuchVersion,
    NoSuchToken,
    NoSuchLink,
    NoSuchShare,
    NoSuchRegion,
    NoSuchParent,
    NoSuchApplication,
    LinkNoEffective,
    LinkExpired,
    NoSuchSource,
    NoSuchDest,
    NoThumbnail,
    NoSuchOption,
    AbnormalTeamStatus,
    NoSuchGroup,
    NoSuchMember,
    AbnormalGroupStatus,
    NoSuchTeamspace,
    NoSuchACL,
    
    InvalidProtocol,
    MethodNotAllowed,
    
    Conflict,
    ConflictRegion,
    RegionConflict,
    ConflictUser,
    RepeatNameConflict,
    SubFolderConflict,
    SameParentConflict,
    LinkExistedConflict,
    ExistMemberConflict,
    ExistTeamspaceConflict,
    AsyncNodesConflict,
    OutOfQuota,
    
    TooManyRequest,
    PreconditionFailed,
    
    TransCommitFailed,
    TransRollbackError,
    
    InternalServerError,
    InsufficientStorage,
    ServerErrorOther,
    
    TimeOut,
    
    UnkownError,
    
    
    UnableToConnectToServers,
    
    
} ErrorType;
















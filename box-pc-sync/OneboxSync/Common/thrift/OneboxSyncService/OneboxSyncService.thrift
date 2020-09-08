/*
 * Onebox thrift service
 */

/**
 * Thrift files can namespace, package, or prefix their output in various
 * target languages.
 */
 
namespace cpp Onebox.SyncService
namespace csharp Onebox.ThriftClient
 
/**
 * Authen type
 */
enum Authen_Type
{
    Authen_Type_Normal,
    Authen_Type_Domain
}

/**
 * service status
 */
enum Service_Status
{
    Service_Status_Online,
    Service_Status_Offline,
    Service_Status_Error,
    Service_Status_Pause,
    Service_Status_Uninitial
}

/**
 * File type
 */
enum File_Type
{
   File_Type_Dir,
   File_Type_File,
}

/**
 * Trans speed information
 */
struct Trans_Speed_Info
{
   1: i64 upload,
   2: i64 download,
}

/**
 * Update information
 */
struct Update_Info
{
   1: string versionInfo,
   2: string downloadUrl,
}

/**
 * Node info
 */
struct Node_Info
{
   1: i64 remoteId,
   2: bool hasShareLink,
}

/**
 * list info
 */
struct List_Info
{
   1: i64 id,
   2: string name,
   3: i32 type,
   4: i32 flags,
}

/**
 * error info
 */
struct Error_Info
{
   1: string path,
   2: i32 errorCode,
}

/**
 * share right
 */
enum Share_Right
{
    Share_Right_R,
    Share_Right_RW
}

/**
 * share user info
 */
struct Share_User_Info
{
   1: i64 id,
   2: string userName,
   3: string loginName,
   4: string department,
   5: string email,
   6: i32 right 
}

/**
 * share link info
 */
struct Share_Link_Info
{
   1: string url,
   2: string accessCode,
   3: i64 effectAt,
   4: i64 expireAt,
}

/**
 * overlay icon status
 */
enum OverlayIcon_Status
{
    OverlayIcon_Status_None,
    OverlayIcon_Status_Synced,
    OverlayIcon_Status_Syncing,
    OverlayIcon_Status_NoActionDelete
    OverlayIcon_Status_Invalid
}

/**
 * teamspace meta data
 */
struct Teamspace_Info
{
	1: i64 id,
	2: string name,
	3: string description,
	4: i32 curNumbers,
	5: i64 createdAt,
	6: i64 createdBy,
	7: string createdByUserName,
	8: i64 ownedBy,
	9: string ownedByUserName,
	10: i32 status,
	11: i64 spaceQuota,
	12: i64 spaceUsed,
	13: i32 maxVersions,
	14: i32 maxMembers,
	15: i32 regionId,
}

/**
 * teamspace member 
 */
struct Teamspace_Member_Info
{
	1: i64 id,
	2: string loginName,
	3: i32 type,
	4: string name,
	5: string description,
}

/**
 * user teamspace data  
 */
struct Teamspace_Membership
{
	1: i64 id,
	2: string teamRole,
	3: string role,
	4: Teamspace_Info teamspace,
	5: Teamspace_Member_Info member,
}

/**
 * thrift service interface
 */
service SyncService {
   
   /**
    * initial userContext
    */
   i32 initUserContext(1:i64 uiHandle, 2:string confPath),

   /**
    * release userContext
    */
   i32 releaseUserContext(),
   
   /**
    * get service status
    */
   i32 getServiceStatus(),
   
    /**
    * change service work mode
    */
   i32 changeServiceWorkMode(1:Service_Status status),
   
   
   /**
    * send message
    */
   i32 sendMessage(1:i32 type, 2:string msg1, 3:string msg2, 4:string msg3, 5:string msg4, 6:string msg5),
   
      
   /**
    * upload file/directory
    */
   i32 uploadFile(1:string source, 2:string target, 3:File_Type fileType),   
   
   /**
   * download file/directory
   */
   i32 downloadFile(1:string source, 2:string target, 3:File_Type fileType),   
   
   /**
    * delete file/directory
    */
   i32 deleteFile(1:string filePath, 2:File_Type fileType),
   
   /**
    * rename file/directory
    */
   i32 renameFile(1:string source, 2:string target, 3:File_Type fileType),   
   
   /**
    * move file/directory
    */
   i32 moveFile(1:string source, 2:string target, 3:File_Type fileType),   
   
   /**
    * copy file/directory
    */
   i32 copyFile(1:string source,2:string target, 3:File_Type fileType),
   
   /**
    * create directory
    */
   i32 createDir(1:string dirPath),
   
   /**
    * list directory
    */
   list<List_Info> listRemoteDir(1:i64 parent, 2:i64 owner_id),
   
   /**
    * upload file/directory
    */
   i64 upload(1:string path, 2:i64 parent, 3:i64 owner_id),
   
   
   
   /**
    * login
    */
   i32 login(1:i32 type, 2:string username, 3:string password, 4:string domain), 
   
   /**
    * logout
    */
   i32 logout(),
   
   /**
    * encypt string
    */
   string encyptString(1:string src),
   
   /**
    * decypt string
    */
   string decyptString(1:string src),
   
   /**
    * update configure
    */
   i32 updateConfigure(),

   /**
    * get trans speed information
    */
   Trans_Speed_Info getTransSpeed(),
   
   /**
    * get user id
    */
   i64 getUserId(),
   
   /**
    * get update information
    */
   Update_Info getUpdateInfo(),
   
   /**
    * download client
    */
   i32 downloadClient(1:string downloadUrl, 2:string location),
   
   /**
    * get local node
    */
   Node_Info getNodeInfo(1:string path),

   /**
    * get overlay icon status
    */
   i32 getOverlayIconStatus(1:string path),

   /**
    * get remote id
    */
   i64 getRemoteId(1:string path),

   /**
    * list error
    */
   list<Error_Info> listError(1:i32 offset, 2:i32 limit),
   
   /**
    * list domain users
    */
   list<Share_User_Info> listDomainUsers(1:string keyword),
   
   /**
    * list share users
    */
   list<Share_User_Info> listShareUsers(1:string path),
   
   /**
    * set share member
    */ 
   i32 setShareMember(1:string path, 2:list<Share_User_Info> shareUserInfos,3:string emailMsg), 
   
   /**
    * del share member
    */
   i32 delShareMember(1:string path, 2:Share_User_Info shareUserInfo), 
   
   /**
    * cancel share
    */
   i32 cancelShare(1:string path),
   
   /**
    * get share link
    */
   Share_Link_Info getShareLink(1:string path),
   
   /**
    * modify share link
    */
   Share_Link_Info modifyShareLink(1:string path, 2:Share_Link_Info shareLinkInfo),
   
   /**
    * del share link
    */
   i32 delShareLink(1:string path),
   
   /**
    * get random string(length:8-20)
    */
   string getRandomString(),

   /**
    * send share link by email 
    */   
   i32 sendEmail(1:string type, 2:string path, 3:Share_Link_Info shareLinkInfo, 4:string emailMsg, 5:list<string>mailto),
   
   /**
    * list batch of domain users
    */   
   map<string, Share_User_Info> listBatchDomainUsers(1:list<string> keyword),
   
   /**
    * upload outlook attachements
    */ 
   i32 uploadAttachements(1:list<string> attachements, 2:string parent, 3:string taskGroupId),
   
   /**
    * is outlook attachements trans complete
    */ 
   bool isAttachementsTransComplete(1:string taskGroupId),
   
   /**
    * get outlook attachements share links
    */ 
   map<string, string> getAttachementsLinks(1:string transGroupId),
   
   /**
    * delete trans tasks by task group id
    */
   i32 deleteTransTasksByGroupId(1:string transGroupId),
   
   /**
    * show trans task
    */
   i32 showTransTask(1:string transGroupId),
	
   /**
    * list user teamspaces
    */
   list<Teamspace_Membership> listTeamspacesByUser(1:i64 userId)
}


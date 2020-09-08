/*
 * Onebox thrift service
 */

/**
 * Thrift files can namespace, package, or prefix their output in various
 * target languages.
 */
 
namespace cpp OneboxThriftService

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
 * file node
 */
struct File_Node
{
   1: i64 id,
   2: i64 parent,
   3: string name,
   4: i32 type,
   5: i64 size,
   6: i64 mtime,
   7: i64 ctime,
   8: i32 flags,
   9: i32 extraType,

}

/**
 * trans task root node
 */
struct TransTask_RootNode
{
   1: string group,
   2: string source,
   3: string parent,
   4: string name,
   5: i32 type,
   6: i32 fileType,   
   7: i32 status,
   8: i32 statusEx,
   9: i64 userId,
   10: i32 userType,
   11: string userName,
   12: i32 priority,
   13: i64 size,
   14: i64 transedSize,
   15: i32 errorCode,
}

/**
 * teamspace node
 */
struct TeamSpace_Node
{
	1: i64 id,
	2: string name,
}

/**
 * thrift service interface
 */
service ThriftService 
{
   i32 getServiceStatus(),
   
   i64 getCurrentUserId(),
   
   list<File_Node> listRemoteDir(1:i64 fileId, 2:i64 userId, 3:i32 userType),
   
   list<File_Node> listLocalDir(1:string localPath),

   string GetPathName(1:i64 fileId),
   
   i32 upload(1:string localPath, 2:i64 remoteParentId, 3:i64 userId, 4:i32 userType, 5:string group),
   
   TransTask_RootNode getTask(1:string group),
   
   i32 pauseTask(1:string group),
   
   i32 delTask(1:string group),
   
   i32 resumeTask(1:string group),
   
   i32 isTaskExist(1:string group),
   
   string createShareLink(1:i64 fileId),
   
   i32 addNotify(1:i64 handle),
   
   i32 removeNotify(1:i64 handle),
   
   list<TeamSpace_Node> listTeamspace(),
   
   i32 sendMessage(1:i32 type, 2:string msg1, 3:string msg2, 4:string msg3, 5:string msg4, 6:string msg5),
   
   string getNewName(1:i64 userId, 2:i32 userType, 3:i64 parentId, 4:string defaultName), 
   
   File_Node createFolder(1:i64 userId, 2:i32 userType, 3:i64 parentId, 4:string name),
   
   File_Node createFolderNoSync(1:i64 userId, 2:i32 userType, 3:i64 parentId, 4:string name,5:i32 extraType),
   
   i32 renameFolder(1:i64 userId, 2:i32 userType, 3:i64 fileId, 4:string name),
   
   i32 uploadOutlook(1:string localPath, 2:i64 remoteParentId, 3:string group),
   
   i32 uploadOffice(1:string localPath, 2:i64 remoteParentId, 3:string group),
   
   bool needAddFullBackup(1:string strPath),
   
   i32 addFullBackup(1:string strPath),
}


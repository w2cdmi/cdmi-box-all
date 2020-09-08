#ifndef _ONEBOX_TABLE_DEFINE_H_
#define _ONEBOX_TABLE_DEFINE_H_

#define SQLITE_LOCALINFO_TABLE (L"localInfo.db")
#define SQLITE_REMOTEINFO_TABLE (L"remoteInfo.db")
#define SQLITE_DIFFINFO_TABLE (L"diffInfo.db")
#define SQLITE_RELATIONINFO_TABLE (L"relationInfo.db")
#define SQLITE_UPLOAD_TABLE (L"uploadInfo.db")

// local table define begin
#ifndef LOCAL_TABLE_NAME
#define LOCAL_TABLE_NAME ("tb_localInfo")
#endif

#ifndef LOCAL_ROW_ID
#define LOCAL_ROW_ID ("id")
#endif

#ifndef LOCAL_ROW_PARENT
#define LOCAL_ROW_PARENT ("parent")
#endif

#ifndef LOCAL_ROW_NAME
#define LOCAL_ROW_NAME ("name")
#endif

#ifndef LOCAL_ROW_TYPE
#define LOCAL_ROW_TYPE ("type")
#endif

#ifndef LOCAL_ROW_STATUS
#define LOCAL_ROW_STATUS ("status")
#endif

#ifndef LOCAL_ROW_CTIME
#define LOCAL_ROW_CTIME ("ctime")
#endif

#ifndef LOCAL_ROW_MTIME
#define LOCAL_ROW_MTIME ("mtime")
#endif

#ifndef LOCAL_ROW_MARK
#define LOCAL_ROW_MARK ("mark")
#endif
// local table define end

// remote table define begin
#ifndef REMOTE_TABLE_NAME
#define REMOTE_TABLE_NAME ("tb_remoteInfo")
#endif

#ifndef REMOTE_ROW_ID
#define REMOTE_ROW_ID ("id")
#endif

#ifndef REMOTE_ROW_PARENT
#define REMOTE_ROW_PARENT ("parent")
#endif

#ifndef REMOTE_ROW_NAME
#define REMOTE_ROW_NAME ("name")
#endif

#ifndef REMOTE_ROW_TYPE
#define REMOTE_ROW_TYPE ("type")
#endif

#ifndef REMOTE_ROW_STATUS
#define REMOTE_ROW_STATUS ("status")
#endif

#ifndef REMOTE_ROW_VERSION
#define REMOTE_ROW_VERSION ("version")
#endif

#ifndef REMOTE_ROW_MARK
#define REMOTE_ROW_MARK ("mark")
#endif
// remote table define end

// sync table define begin
#ifndef SYNC_TABLE_NAME
#define SYNC_TABLE_NAME ("files_inode")
#endif

#ifndef SYNC_ROW_ID
#define SYNC_ROW_ID ("id")
#endif

#ifndef SYNC_ROW_NAME
#define SYNC_ROW_NAME ("name")
#endif

#ifndef SYNC_ROW_TYPE
#define SYNC_ROW_TYPE ("type")
#endif

#ifndef SYNC_ROW_OBJID
#define SYNC_ROW_OBJID ("objectId")
#endif

#ifndef SYNC_ROW_PARENTID
#define SYNC_ROW_PARENTID ("parentId")
#endif

#ifndef SYNC_ROW_SYNCVERSION
#define SYNC_ROW_SYNCVERSION ("syncVersion")
#endif

#ifndef SYNC_ROW_STATUS
#define SYNC_ROW_STATUS ("status")
#endif

#ifndef SYNC_ROW_SYNCSTATUS
#define SYNC_ROW_SYNCSTATUS ("syncStatus")
#endif

#ifndef SYNC_ROW_CONTENT_CREATE
#define SYNC_ROW_CONTENT_CREATE ("contentCreatedAt")
#endif

#ifndef SYNC_ROW_CONTENT_MODIFY
#define SYNC_ROW_CONTENT_MODIFY ("contentModifiedAt")
#endif

#ifndef SYNC_ROW_SIZE
#define SYNC_ROW_SIZE ("size")
#endif
// sync table define end

// diff table define begin
#ifndef DIFF_TABLE_NAME
#define DIFF_TABLE_NAME ("tb_diffInfo")
#endif

#ifndef DIFF_ROW_DIFFID
#define DIFF_ROW_DIFFID ("diffId")
#endif

#ifndef DIFF_ROW_KEY
#define DIFF_ROW_KEY ("key")
#endif

#ifndef DIFF_ROW_SIZE
#define DIFF_ROW_SIZE ("size")
#endif

#ifndef DIFF_ROW_KEYTYPE
#define DIFF_ROW_KEYTYPE ("keyType")
#endif

#ifndef DIFF_ROW_OPER
#define DIFF_ROW_OPER ("oper")
#endif

#ifndef DIFF_ROW_STATUS
#define DIFF_ROW_STATUS ("status")
#endif

#ifndef DIFF_ROW_PRIORITY
#define DIFF_ROW_PRIORITY ("priority")
#endif

#ifndef DIFFPATH_TABLE_NAME
#define DIFFPATH_TABLE_NAME ("tb_diffPath")
#endif

#ifndef DIFFPATH_ROW_TYPE
#define DIFFPATH_ROW_TYPE ("type")
#endif

#ifndef DIFFPATH_ROW_ID
#define DIFFPATH_ROW_ID ("id")
#endif

#ifndef DIFFPATH_ROW_PATH
#define DIFFPATH_ROW_PATH ("path")
#endif

#ifndef DIFFPATH_ROW_REMOTEPATH
#define DIFFPATH_ROW_REMOTEPATH ("remotePath")
#endif

#ifndef DIFFPATH_ROW_SIZE
#define DIFFPATH_ROW_SIZE ("size")
#endif

#ifndef DIFFPATH_ROW_ERRORCODE
#define DIFFPATH_ROW_ERRORCODE ("errorCode")
#endif
// diff table define end

// relation table define begin
#ifndef RELATION_TABLE_NAME
#define RELATION_TABLE_NAME ("tb_relationInfo")
#endif

#ifndef RELATION_ROW_LOCALID
#define RELATION_ROW_LOCALID ("localId")
#endif

#ifndef RELATION_ROW_REMOTEID
#define RELATION_ROW_REMOTEID ("remoteId")
#endif
// relation table define end

// transTask table define begin
#ifndef TRANS_TASK_TABLE_NAME
#define TRANS_TASK_TABLE_NAME ("tb_transTaskInfo")
#endif

#ifndef TRANS_TASK_ROW_ID
#define TRANS_TASK_ROW_ID ("id")
#endif

#ifndef TRANS_TASK_ROW_GROUP
#define TRANS_TASK_ROW_GROUP ("groupId")
#endif

#ifndef TRANS_TASK_ROW_PARENT
#define TRANS_TASK_ROW_PARENT ("parent")
#endif

#ifndef TRANS_TASK_ROW_NAME
#define TRANS_TASK_ROW_NAME ("name")
#endif

#ifndef TRANS_TASK_ROW_TYPE
#define TRANS_TASK_ROW_TYPE ("type")
#endif

#ifndef TRANS_TASK_ROW_STATUS
#define TRANS_TASK_ROW_STATUS ("status")
#endif

#ifndef TRANS_TASK_ROW_PRIORITY
#define TRANS_TASK_ROW_PRIORITY ("priority")
#endif

#ifndef TRANS_TASK_ROW_SIZE
#define TRANS_TASK_ROW_SIZE ("size")
#endif

#ifndef TRANS_TASK_ROW_SIGNATURE
#define TRANS_TASK_ROW_SIGNATURE ("signature")
#endif

#ifndef TRANS_TASK_ROW_ALGORITHM
#define TRANS_TASK_ROW_ALGORITHM ("algorithm")
#endif

#ifndef TRANS_TASK_ROW_BLOCK_SIGNATURE
#define TRANS_TASK_ROW_BLOCK_SIGNATURE ("blockSignature")
#endif

#ifndef TRANS_TASK_ROW_BLOCKS
#define TRANS_TASK_ROW_BLOCKS ("blocks")
#endif

#ifndef TRANS_TASK_ROW_USER_DEFINE
#define TRANS_TASK_ROW_USER_DEFINE ("userDefine")
#endif

#ifndef TRANS_TASK_ROW_OWNERID
#define TRANS_TASK_ROW_OWNERID ("ownerId")
#endif

// transTask table define end


// upload table define begin
#ifndef UPLOAD_TABLE_NAME
#define UPLOAD_TABLE_NAME ("tb_filterInfo")
#endif

#ifndef UPLOAD_ROW_PATH
#define UPLOAD_ROW_PATH ("path")
#endif

#ifndef UPLOAD_ROW_ID
#define UPLOAD_ROW_ID ("id")
#endif

#ifndef UPLOAD_ROW_LASTTIME
#define UPLOAD_ROW_LASTTIME ("lastTime")
#endif
// upload table define end

#endif

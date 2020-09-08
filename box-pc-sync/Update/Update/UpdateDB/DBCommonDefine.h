#pragma once
#ifndef _DBCOMMONDEFINE_H
#define _DBCOMMONDEFINE_H


#ifndef ERROR_INFO_DB_NAME
#define ERROR_INFO_DB_NAME ("error_table.db")
#endif

#ifndef ERROR_TABLE_NAME
#define ERROR_TABLE_NAME ("tb_errorInfo")
#endif

#ifndef ERROR_ROW_KEY
#define ERROR_ROW_KEY ("key")
#endif

#ifndef ERROR_ROW_KEYTYPE
#define ERROR_ROW_KEYTYPE ("keyType")
#endif

#ifndef ERROR_ROW_OPER
#define ERROR_ROW_OPER ("oper")
#endif

#ifndef ERROR_ROW_LOCALPATH
#define ERROR_ROW_LOCALPATH ("localPath")
#endif

#ifndef ERROR_ROW_ERRORCODE
#define ERROR_ROW_ERRORCODE ("errorCode")
#endif

#ifndef ERROR_ROW_TIME
#define ERROR_ROW_TIME ("time")
#endif



#ifndef DIFF_INFO_DB_NAME
#define DIFF_INFO_DB_NAME ("diff_table.db")
#endif

#ifndef DIFF_TABLE_NAME
#define DIFF_TABLE_NAME ("tb_diffInfo")
#endif

#ifndef DIFF_ROW_KEY
#define DIFF_ROW_KEY ("key")
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

#ifndef DIFF_ROW_FAILEDCNT
#define DIFF_ROW_FAILEDCNT ("failedCnt")
#endif

#ifndef DIFF_ROW_TASKID
#define DIFF_ROW_TASKID ("taskId")
#endif

#ifndef DIFF_ROW_LOCALPATH
#define DIFF_ROW_LOCALPATH ("localPath")
#endif




#ifndef LOCAL_INFO_DB_NAME
#define LOCAL_INFO_DB_NAME ("local_table.db")
#endif

#ifndef LOCAL_TABLE_NAME
#define LOCAL_TABLE_NAME ("tb_localInfo")
#endif

#ifndef LOCAL_ROW_ID
#define LOCAL_ROW_ID ("id")
#endif

#ifndef LOCAL_ROW_TYPE
#define LOCAL_ROW_TYPE ("type")
#endif

#ifndef LOCAL_ROW_SHA1
#define LOCAL_ROW_SHA1 ("sha1")
#endif

#ifndef LOCAL_ROW_PARENTID
#define LOCAL_ROW_PARENTID ("parentId")
#endif

#ifndef LOCAL_ROW_VERSION
#define LOCAL_ROW_VERSION ("version")
#endif

#ifndef LOCAL_ROW_PATH
#define LOCAL_ROW_PATH ("path")
#endif

#ifndef LOCAL_ROW_LOCALCREATETIME
#define LOCAL_ROW_LOCALCREATETIME ("local_create_time")
#endif

#ifndef LOCAL_ROW_LOCALMODIFYTIME
#define LOCAL_ROW_LOCALMODIFYTIME ("local_modify_time")
#endif

#ifndef LOCAL_ROW_STATIC
#define LOCAL_ROW_STATIC ("static_status")
#endif

#ifndef LOCAL_ROW_TRANS
#define LOCAL_ROW_TRANS ("trans_status")
#endif

#ifndef LOCAL_ROW_REFCNT
#define LOCAL_ROW_REFCNT ("trans_status_ref_cnt")
#endif

#ifndef LOCAL_ROW_ISEXIST
#define LOCAL_ROW_ISEXIST ("is_exist")
#endif



#ifndef REMORE_INFO_DB_NAME
#define REMORE_INFO_DB_NAME ("remote_table.db")
#endif

#ifndef REMOTE_TABLE_NAME
#define REMOTE_TABLE_NAME ("tb_remoteInfo")
#endif

#ifndef REMOTE_ROW_ID
#define REMOTE_ROW_ID ("id")
#endif

#ifndef REMOTE_ROW_NAME
#define REMOTE_ROW_NAME ("name")
#endif

#ifndef REMOTE_ROW_TYPE
#define REMOTE_ROW_TYPE ("type")
#endif

#ifndef REMOTE_ROW_SHA1
#define REMOTE_ROW_SHA1 ("sha1")
#endif

#ifndef REMOTE_ROW_SYNCSTATUS
#define REMOTE_ROW_SYNCSTATUS ("syncStatus")
#endif

#ifndef REMOTE_ROW_OBJID
#define REMOTE_ROW_OBJID ("objectId")
#endif

#ifndef REMOTE_ROW_PARENTID
#define REMOTE_ROW_PARENTID ("parentId")
#endif

#ifndef REMOTE_ROW_PATH
#define REMOTE_ROW_PATH ("path")
#endif



#ifndef SYNC_INFO_DB_NAME
#define SYNC_INFO_DB_NAME ("syncdata_table.db")
#endif

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

#ifndef SYNC_ROW_SHA1
#define SYNC_ROW_SHA1 ("sha1")
#endif

#ifndef SYNC_ROW_CONTENT_CREATE
#define SYNC_ROW_CONTENT_CREATE ("contentCreatedAt")
#endif

#ifndef SYNC_ROW_CONTENT_MODIFY
#define SYNC_ROW_CONTENT_MODIFY ("contentModifiedAt")
#endif




#ifndef TRANS_TASK_DB_NAME
#define TRANS_TASK_DB_NAME ("TransTask.db")
#endif

#ifndef TRANSMIT_TABLE_NAME
#define TRANSMIT_TABLE_NAME ("tb_task")
#endif

#ifndef TRANSMIT_ROW_PRIORITY
#define TRANSMIT_ROW_PRIORITY ("task_priority")
#endif

#ifndef TRANSMIT_ROW_FILE_ID
#define TRANSMIT_ROW_FILE_ID ("task_id")
#endif

#ifndef TRANSMIT_ROW_TRANS_STATE
#define TRANSMIT_ROW_TRANS_STATE ("task_state")
#endif

#ifndef TRANSMIT_ROW_VERSION
#define TRANSMIT_ROW_VERSION ("version")
#endif

#ifndef TRANSMIT_ROW_META_DATA
#define TRANSMIT_ROW_META_DATA ("task_meta")
#endif

#ifndef TRANSMIT_ROW_SHCDULE_TIMES
#define TRANSMIT_ROW_SHCDULE_TIMES ("task_schedule_times")
#endif

#endif
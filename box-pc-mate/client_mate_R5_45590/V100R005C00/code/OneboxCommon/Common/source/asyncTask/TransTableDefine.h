#ifndef _ONEBOX_TRANS_TABLEDEFINE_H_
#define _ONEBOX_TRANS_TABLEDEFINE_H_

#include "AsyncTaskCommon.h"

#ifndef TRANS_TABLE_DEFINE_V1
// trans table file name
#define TTFN_ROOT (L"root_table.db")
#define TTFN_BACKUP_ROOT (L"backup_root_table.db")
#define TTFN_DATA (L"data_table.db")
#define TTFN_COMPLETE (L"complete_table.db")
#define TTDN_DETAIL (L"\\details\\")
// trans table name
#define TTN_ROOT ("tb_trans_root")
#define TTN_DETAIL ("tb_trans_detail")
#define TTN_DATA ("tb_trans_data")
#define TTN_COMPLETE ("tb_trans_complete")
// trans table row name
#define TTRN_GROUP ("groupId")
#define TTRN_SOURCE ("source")
#define TTRN_PARENT ("parent")
#define TTRN_NAME ("name")
#define TTRN_TYPE ("type")
#define TTRN_FILETYPE ("fileType")
#define TTRN_STATUS ("status")
#define TTRN_STATUSEX ("statusEx")
#define TTRN_USERID ("userId")
#define TTRN_USERTYPE ("userType")
#define TTRN_USERNAME ("userName")
#define TTRN_PRIORITY ("priority")
#define TTRN_SIZE ("size")
#define TTRN_TRANSEDSIZE ("transedSize")
#define TTRN_ERRORCODE ("errorCode")
#define TTRN_BLOCKNUM ("blockNum")
#define TTRN_BLOCKS ("blocks")
#define TTRN_MTIME ("mtime")
#define TTRN_ALGORITHM ("algorithm")
#define TTRN_FINGERPRINT ("fingerprint")
#define TTRN_BLOCKFINGERPRINT ("blockFingerprint")
#define TTRN_USERDEFINE ("userDefine")
#define TTRN_COMPLETETIME ("completeTime")
#define TTRN_UNIQUEID ("uniqueId")
#endif

#endif
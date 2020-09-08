#ifndef _ONEBOX_CACHE_MSGINFO_H_
#define _ONEBOX_CACHE_MSGINFO_H_

#include "MsgMgr.h"
#include "PageParam.h"

#define SQLITE_CACHE_MSG (L"cacheMsg.db")
#define TABLE_MSG ("tb_msg")
#define MSG_ROW_ID ("id")
#define MSG_ROW_PROVIDERID ("providerId")
#define MSG_ROW_RECEIVERID ("receiverId")
#define MSG_ROW_APPID ("appId")
#define MSG_ROW_TYPE ("type")
#define MSG_ROW_STATUS ("status")
#define MSG_ROW_CREATEDAT ("createdAt")
#define MSG_ROW_EXPIREDAT ("expiredAt")
#define MSG_ROW_PUSERNAME ("providerUsername")
#define MSG_ROW_PNAME ("providerName")
#define MSG_ROW_NODEID ("nodeId")
#define MSG_ROW_NODENAME ("nodeName")
#define MSG_ROW_NODETYPE ("nodeType")
#define MSG_ROW_TEAMSPACEID ("teamSpaceId")
#define MSG_ROW_TEAMSPACENAME ("teamSpaceName")
#define MSG_ROW_GROUPID ("groupId")
#define MSG_ROW_GROUPNAME ("groupName")
#define MSG_ROW_ORIROLE ("originalRole")
#define MSG_ROW_CURROLE ("currentRole")
#define MSG_ROW_TITLE ("title")
#define MSG_ROW_CONTENT ("content")
#define MSG_ROW_ANNOUNCEMENTID ("announcementId")

class CacheMsgInfo
{
public:
	virtual ~CacheMsgInfo(){}

	static CacheMsgInfo* create(UserContext* userContext, const std::wstring& parent);

	virtual int32_t getMsg(const PageParam& pageParam, const MsgTypeList& msgTypeList, MsgList& msgNodes, int64_t& count, MsgStatus status = MS_All) = 0;

	virtual int32_t getAllMsgKeyInfo(MsgKeyInfo& msgKeyInfo) = 0;

	virtual int32_t updateMsg(const int64_t msgId, MsgStatus status = MS_Readed) = 0;

	virtual int32_t deleteMsg(const int64_t msgId) = 0;

	virtual int32_t deleteMsg(const MsgKeyInfo& msgKeyInfo) = 0;

	virtual int32_t deleteAll() = 0;

	virtual int32_t replaceMsg(const MsgList& msgNodes) = 0;

	virtual int64_t hasUnRead(const MsgTypeList& msgTypeList) = 0;
};

#endif
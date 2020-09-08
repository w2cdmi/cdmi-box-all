#ifndef _MSGINFO_H_
#define _MSGINFO_H_

#include "CommonValue.h"
#include <list>
#include <map>
#include <functional>

#define MSG_PAGE_LIMIT 100

enum MsgType
{
	MT_Invalid = 0,
	MT_Share = 1,
	MT_Share_Delete = 2,
	MT_TeamSpace_Upload = 3,
	MT_TeamSpace_Add = 4,
	MT_TeamSpace_Delete = 5,
	MT_TeamSpace_Leave = 6,
	MT_Group_Add = 7,
	MT_Group_Delete = 8,
	MT_Group_Leave = 9,
	MT_TeamSpace_RoleUpdate = 10,
	MT_Group_RoleUpdate = 11,
	MT_System = 12
};

inline MsgType convertMsgType(const std::string& msgType)
{
	if("share"==msgType) return MT_Share;								//共享
	if("deleteShare"==msgType) return  MT_Share_Delete;					//取消共享
	if("teamspaceUpload"==msgType) return  MT_TeamSpace_Upload;			//团队空间上传文件
	if("teamspaceAddMember"==msgType) return  MT_TeamSpace_Add;			//团队空间添加成员
	if("teamspaceDeleteMember"==msgType) return  MT_TeamSpace_Delete;	//团队空间删除成员
	if("leaveTeamspace"==msgType) return MT_TeamSpace_Leave;			//成员退出团队空间
	if("groupAddMember"==msgType) return MT_Group_Add;					//群组添加成员
	if("groupDeleteMember"==msgType) return MT_Group_Delete;			//群组删除成员
	if("leaveGroup"==msgType) return MT_Group_Leave;					//成员退出群组
	if("teamspaceRoleUpdate"==msgType) return MT_TeamSpace_RoleUpdate;	//团队空间成员角色变更
	if("groupRoleUpdate"==msgType) return MT_Group_RoleUpdate;			//群组成员角色变更
	if("system"==msgType) return MT_System;								//系统消息
	return MT_Invalid;
}

enum MsgStatus
{
	MS_All = 0,			//未读 + 已读
	MS_UnRead = 1,		//未读
	MS_Readed = 2		//已读
};

struct MsgParams
{
	int64_t nodeId;					//文件或文件夹id
	std::string nodeName;			//文件或文件夹名称
	int32_t nodeType;				//节点类型：0为文件夹 1为文件  
	int64_t teamSpaceId;			//团队空间id
	std::string teamSpaceName;		//团队空间名称
	int64_t groupId;				//群组id
	std::string groupName;			//群组名称
	std::string originalRole;		//原权限角色
	std::string currentRole;		//现权限角色
	std::string title;				//系统公告标题
	std::string content;			//系统公告内容
	int64_t announcementId;			//系统公告id

	MsgParams():nodeId(-1L), nodeName(""), nodeType(0),
		teamSpaceId(-1L), teamSpaceName(""),
		groupId(-1L), groupName(""),
		originalRole(""), currentRole(""),
		title(""), content(""), announcementId(-1L)
	{
	}
};

struct MsgNode
{
	int64_t id;						//消息id
	int64_t providerId;				//消息提供者id
	std::string providerUsername;	//消息提供者的用户名
	std::string providerName;		//消息提供者的姓名
	int64_t receiverId;				//消息接收者id
	std::string appId;				//产生消息的应用id
	MsgType type;					//消息类型
	MsgStatus status;				//消息状态
	int64_t createdAt;				//消息产生时间
	int64_t expiredAt;				//消息过期时间
	MsgParams params;				//消息参数

	MsgNode():id(-1L), providerId(-1L)
		,providerUsername(""), providerName("")
		,receiverId(-1L)
		,appId("")
		,type(MT_Invalid)
		,status(MS_UnRead)
		,createdAt(0L)
		,expiredAt(0L)
	{
	}
};

typedef std::list<MsgNode> MsgList;
typedef std::list<MsgType> MsgTypeList;
typedef std::map<int64_t, MsgStatus> MsgKeyInfo;
typedef std::function<int32_t(const MsgNode&)> MsgInfoChangeHandler;

#endif

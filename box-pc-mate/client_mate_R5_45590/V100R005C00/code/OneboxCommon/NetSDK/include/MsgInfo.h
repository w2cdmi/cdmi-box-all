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
	if("share"==msgType) return MT_Share;								//����
	if("deleteShare"==msgType) return  MT_Share_Delete;					//ȡ������
	if("teamspaceUpload"==msgType) return  MT_TeamSpace_Upload;			//�Ŷӿռ��ϴ��ļ�
	if("teamspaceAddMember"==msgType) return  MT_TeamSpace_Add;			//�Ŷӿռ���ӳ�Ա
	if("teamspaceDeleteMember"==msgType) return  MT_TeamSpace_Delete;	//�Ŷӿռ�ɾ����Ա
	if("leaveTeamspace"==msgType) return MT_TeamSpace_Leave;			//��Ա�˳��Ŷӿռ�
	if("groupAddMember"==msgType) return MT_Group_Add;					//Ⱥ����ӳ�Ա
	if("groupDeleteMember"==msgType) return MT_Group_Delete;			//Ⱥ��ɾ����Ա
	if("leaveGroup"==msgType) return MT_Group_Leave;					//��Ա�˳�Ⱥ��
	if("teamspaceRoleUpdate"==msgType) return MT_TeamSpace_RoleUpdate;	//�Ŷӿռ��Ա��ɫ���
	if("groupRoleUpdate"==msgType) return MT_Group_RoleUpdate;			//Ⱥ���Ա��ɫ���
	if("system"==msgType) return MT_System;								//ϵͳ��Ϣ
	return MT_Invalid;
}

enum MsgStatus
{
	MS_All = 0,			//δ�� + �Ѷ�
	MS_UnRead = 1,		//δ��
	MS_Readed = 2		//�Ѷ�
};

struct MsgParams
{
	int64_t nodeId;					//�ļ����ļ���id
	std::string nodeName;			//�ļ����ļ�������
	int32_t nodeType;				//�ڵ����ͣ�0Ϊ�ļ��� 1Ϊ�ļ�  
	int64_t teamSpaceId;			//�Ŷӿռ�id
	std::string teamSpaceName;		//�Ŷӿռ�����
	int64_t groupId;				//Ⱥ��id
	std::string groupName;			//Ⱥ������
	std::string originalRole;		//ԭȨ�޽�ɫ
	std::string currentRole;		//��Ȩ�޽�ɫ
	std::string title;				//ϵͳ�������
	std::string content;			//ϵͳ��������
	int64_t announcementId;			//ϵͳ����id

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
	int64_t id;						//��Ϣid
	int64_t providerId;				//��Ϣ�ṩ��id
	std::string providerUsername;	//��Ϣ�ṩ�ߵ��û���
	std::string providerName;		//��Ϣ�ṩ�ߵ�����
	int64_t receiverId;				//��Ϣ������id
	std::string appId;				//������Ϣ��Ӧ��id
	MsgType type;					//��Ϣ����
	MsgStatus status;				//��Ϣ״̬
	int64_t createdAt;				//��Ϣ����ʱ��
	int64_t expiredAt;				//��Ϣ����ʱ��
	MsgParams params;				//��Ϣ����

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

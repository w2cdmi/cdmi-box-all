#ifndef _TEAMSPACES_NODE_H_
#define _TEAMSPACES_NODE_H_

/******************************************************************************
Description  : �Ŷӹ�ϵ�ڵ���Ϣ

*******************************************************************************/

#include "CommonValue.h"
#include <vector>

// /**
//  * �Ŷӿռ��Ԫ����  
//  */
// struct TeamSpaceInfo
// {
// 	1: i64 id,					/*�Ŷӿռ�ID*/
// 	2: string name,				/*�Ŷӿռ�����*/
// 	3: string description,		/*�Ŷӿռ�����*/
// 	4: i32 curNumbers,			/*��Ա����*/
// 	5: i64 createdAt,			/*����ʱ��*/
// 	6: i64 createdBy,			/*�����ߵ�ID*/
// 	7: string createdByUserName,/*�����ߵ�����*/
// 	8: byte status,				/*״̬*/
// 	9: i32 spaceQuota,			/*�Ŷӿռ����������λΪMB*/
// 	10: i32 usedQuota,			/*�Ŷӿռ��Ѿ�ʹ�õ���������λΪMB*/
// 	11: string ownerBy,			/*ӵ���ߵ�ID*/
// 	12: string ownerByUserName,	/*ӵ���ߵ��û���*/
// 	13: i32 maxVersions,		/*���汾����Ĭ��ֵΪ-1����ʾ������*/
// 	14: i32 maxMembers,			/*����Ա��*/
// }
// 

// struct TeamSpaceMemberInfo
// {
// 	1: string id,				/*��ԱID*/
// 	2: string loginName,		/*��¼�û���*/
// 	3: string type,				/*user����group*/
// 	4: string name,				/*�û�����Ⱥ������*/
// 	5: string desc,				/*�û�����Ⱥ������*/
// }
// 
// /**
//  * �û��Ŷӿռ������  
//  */
// struct UserTeamSpaceInfo
// {
// 	1: i32 id,
// 	2: i32 teamId,
// 	3: string teamRole,
// 	4: TeamSpaceInfo teamInfo,
// 	5: TeamSpaceMemberInfo member,
// }

class  TeamSpaceMemberNodeInfo
{
public:
	TeamSpaceMemberNodeInfo()
	{
		id_ = 0L;
		loginName_ = "";
		type_ = "";
		name_ = "";
		desc_ = "";
	}

	virtual ~TeamSpaceMemberNodeInfo()
	{

	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(std::string, loginName)
	FUNC_DEFAULT_SET_GET(std::string, type)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, desc)

	TeamSpaceMemberNodeInfo& operator=(const TeamSpaceMemberNodeInfo &rhs)
	{
		if (&rhs != this)
		{
			id_ = rhs.id();
			loginName_ = rhs.loginName();
			type_ = rhs.type();
			name_ = rhs.name();
			desc_ = rhs.desc();
		}
		return *this;
	}

private:
	int64_t id_;				/*��ԱID*/
	std::string loginName_;			/*��¼�û���*/
	std::string type_;				/*user����group*/
	std::string name_;				/*�û�����Ⱥ������*/
	std::string desc_;				/*�û�����Ⱥ������*/
};

class TeamSpacesNode
{
public:
	TeamSpacesNode()
	{
		id_ = 0L;
		name_ = "";
		description_ = "";
		curNumbers_ = 0;
		createdAt_ = 0L;
		createdBy_ = 0L;
		createdByUserName_ = "";
		status_ = 0;	
		spaceQuota_ = -1;
		usedQuota_ = 0L;
		ownerBy_ = 0L;
		ownerByUserName_ = "";
		maxVersions_ = -1;
		maxMembers_ = -1;
	}

	virtual ~TeamSpacesNode()
	{

	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, description)
	FUNC_DEFAULT_SET_GET(int32_t, curNumbers)
	FUNC_DEFAULT_SET_GET(int64_t, createdAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(std::string, createdByUserName)
	FUNC_DEFAULT_SET_GET(int8_t, status)
	FUNC_DEFAULT_SET_GET(int64_t, spaceQuota)
	FUNC_DEFAULT_SET_GET(int64_t, usedQuota)
	FUNC_DEFAULT_SET_GET(int64_t, ownerBy)
	FUNC_DEFAULT_SET_GET(std::string, ownerByUserName)
	FUNC_DEFAULT_SET_GET(int32_t, maxVersions)
	FUNC_DEFAULT_SET_GET(int32_t, maxMembers)

	TeamSpacesNode& operator=(const TeamSpacesNode &rhs)
	{
		if (&rhs != this)
		{
			id_ = rhs.id();			
			name_ = rhs.name();
			description_ = rhs.description();
			curNumbers_ = rhs.curNumbers();
			createdAt_ = rhs.createdAt();
			createdBy_ = rhs.createdBy();
			createdByUserName_ = rhs.createdByUserName();
			status_ = rhs.status();
			spaceQuota_ = rhs.spaceQuota();
			usedQuota_ = rhs.usedQuota();
			ownerBy_ = rhs.ownerBy();
			ownerByUserName_ = rhs.ownerByUserName();
			maxVersions_ = rhs.maxVersions();
			maxMembers_ = rhs.maxMembers();
		}
		return *this;
	}

private:
	int64_t id_;				/*�Ŷӿռ�ID*/
	std::string name_;				/*�Ŷӿռ�����*/
	std::string description_;		/*�Ŷӿռ�����*/
	int32_t curNumbers_;				/*��Ա����*/
	int64_t createdAt_;			/*����ʱ��*/
	int64_t createdBy_;			/*�����ߵ�ID*/
	std::string createdByUserName_;	/*�����ߵ�����*/
	int8_t status_;					/*״̬*/
	int64_t spaceQuota_;				/*�Ŷӿռ����������λΪKB*/
	int64_t usedQuota_;					/*�Ŷӿռ��Ѿ�ʹ�õ���������λΪKB*/
	int64_t ownerBy_;			/*ӵ���ߵ�ID*/
	std::string ownerByUserName_;	/*ӵ���ߵ��û���*/
	int32_t maxVersions_;				/*���汾����Ĭ��ֵΪ-1����ʾ������*/
	int32_t maxMembers_;				/*����Ա��*/
};


class  UserTeamSpaceNodeInfo
{
public:
	UserTeamSpaceNodeInfo()
	{
		id_ = 0L;
		teamId_ = 0L;
		teamRole_ = "";
		role_ = "";
	}

	virtual ~UserTeamSpaceNodeInfo()
	{
	}

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(int64_t, teamId)
	FUNC_DEFAULT_SET_GET(std::string, teamRole)
	FUNC_DEFAULT_SET_GET(std::string, role)

	UserTeamSpaceNodeInfo& operator=(const UserTeamSpaceNodeInfo &rhs)
	{
		if (&rhs != this)
		{
			id_ = rhs.id();
			teamId_ = rhs.teamId();
			teamRole_ = rhs.teamRole();
			role_ = rhs.role();
			member_ = rhs.member_;
			teamInfo_ = rhs.teamInfo_;
		}
		return *this;
	}

private:
	int64_t id_;
	int64_t teamId_;
	std::string teamRole_;
	std::string role_;

public:
	TeamSpacesNode member_;
	TeamSpaceMemberNodeInfo teamInfo_;
};

typedef std::vector<UserTeamSpaceNodeInfo> UserTeamSpaceNodeInfoArray;

#endif

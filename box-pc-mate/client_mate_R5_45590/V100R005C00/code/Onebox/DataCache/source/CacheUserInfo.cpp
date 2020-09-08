#include "CacheUserInfo.h"
#include "CppSQLite3.h"
#include "Utility.h"
#include "ErrorCode.h"
#include "CommonDefine.h"
#include "CacheCommon.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "TeamSpaceResMgr.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("CacheUserInfo")
#endif

class CacheUserInfoImpl : public CacheUserInfo
{
public:
	CacheUserInfoImpl(UserContext* userContext, const std::wstring& parent):userContext_(userContext)
	{
		createUserTable(parent);
	}

	virtual ~CacheUserInfoImpl()
	{
	}

	virtual int32_t addUser(const ShareUserInfo& userInfo)
	{
		ShareUserInfoList userInfoList;
		userInfoList.push_back(userInfo);
		return addUser(userInfoList);
	}

	virtual int32_t addUser(const ShareUserInfoList& userInfoList)
	{
		if(userInfoList.empty())
		{
			return RT_OK;
		}
		boost::mutex::scoped_lock lock(mutex_);
		try
		{
			std::auto_ptr<PrintObj> printObj = PrintObj::create();
			db_.beginTransaction();
			for(ShareUserInfoList::const_iterator it = userInfoList.begin();
				it != userInfoList.end(); ++it)
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("REPLACE INTO %s (%s,%s,%s,%s,%s,%s) VALUES (%lld,'%s',%d,'%s','%s','%s')", 
					TABLE_USER, 
					USER_ROW_ID, 
					USER_ROW_NAME, 
					USER_ROW_TYPE, 
					USER_ROW_LOGINNAME, 
					USER_ROW_EMAIL,
					USER_ROW_DEPARTMENT,
					it->id(),
					CppSQLiteUtility::formatSqlStr(it->name()).c_str(), 
					it->type(),
					CppSQLiteUtility::formatSqlStr(it->loginName()).c_str(),
					CppSQLiteUtility::formatSqlStr(it->email()).c_str(),
					CppSQLiteUtility::formatSqlStr(it->department()).c_str());
				(void)db_.execDML(bufSQL);

				printObj->addField<int64_t>(it->id());
				printObj->lastField<std::string>(it->name());
			}
			db_.commitTransaction();
			SERVICE_DEBUG(MODULE_NAME, RT_OK, "saveCache. size:%d, [%s]", userInfoList.size(), printObj->getMsg().c_str());
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		db_.rollbackTransaction();
		return RT_SQLITE_ERROR;
	}
	
	//ËÑË÷¹Ø¼ü×ÖÆ¥ÅäµÇÂ¼Ãû¡¢È«Ãû¡¢ÓÊÏä
	virtual int32_t getUser(const std::string& keyword, ShareUserInfoList& userInfoList, int32_t limit)
	{
		try
		{
			std::stringstream likeStr;
			likeStr << "WHERE " << USER_ROW_LOGINNAME << "<> '" << SD::Utility::String::wstring_to_utf8(userContext_->getUserInfoMgr()->getUserName()) << "' ";

			if(!keyword.empty())
			{
					likeStr << "AND ("<< USER_ROW_NAME << " LIKE '%" << keyword << "%' OR "
					<< USER_ROW_LOGINNAME << " LIKE '%" << keyword << "%' OR "
					<< USER_ROW_EMAIL << " LIKE '%" << keyword << "%@huawei.com') ";
			}

			CppSQLite3Buffer bufSQL;
			(void)bufSQL.format("SELECT %s, %s, %s, %s, %s, %s FROM %s %s LIMIT 0, %d", 
								USER_ROW_ID, USER_ROW_NAME, 
								USER_ROW_TYPE, USER_ROW_LOGINNAME,
								USER_ROW_EMAIL, USER_ROW_DEPARTMENT,
								TABLE_USER,
								likeStr.str().c_str(),
								limit);
			CppSQLite3Query qSet = db_.execQuery(bufSQL);

			while(!qSet.eof())
			{
				ShareUserInfo userInfo;
				userInfo.id(qSet.getInt64Field(0));
				userInfo.name(qSet.getStringField(1));
				userInfo.type(qSet.getIntField(2));
				userInfo.loginName(qSet.getStringField(3));
				userInfo.email(qSet.getStringField(4));
				userInfo.department(qSet.getStringField(5));
				userInfoList.push_back(userInfo);
				qSet.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	virtual std::string getUserName(int64_t teamId, int64_t id)
	{
		std::map<int64_t, std::string>::const_iterator it = userInfo_.find(id);
		if(userInfo_.end() != it)
		{
			return it->second;
		}
		try
		{
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer bufCount;
			(void)bufCount.format("SELECT %s FROM %s WHERE %s = %lld LIMIT 0, 1", 
								USER_ROW_NAME,
								TABLE_USER,
								USER_ROW_ID, id);
			CppSQLite3Query qCount = db_.execQuery(bufCount);
			if(!qCount.eof())
			{
				std::string userName = qCount.getStringField(0);
				userInfo_.insert(std::make_pair(id, userName));
				return userName;
			}
		}
		CATCH_SQLITE_EXCEPTION;

		std::string userName = addUser(teamId, id);
		if(!userName.empty())
		{
			userInfo_.insert(std::make_pair(id, userName));
		}
		return userName;
	}

	virtual int32_t getCurUserInfo(StorageUserInfo& storageUserInfo)
	{
		try
		{
			boost::mutex::scoped_lock lock(mutex_);
			CppSQLite3Buffer bufCount;
			(void)bufCount.format("SELECT %s, %s, %s, %s, %s FROM %s WHERE %s='%s' LIMIT 0, 1", 
								USER_ROW_ID, USER_ROW_NAME, 
								USER_ROW_LOGINNAME, USER_ROW_EMAIL,
								USER_ROW_DEPARTMENT,
								TABLE_USER,
								USER_ROW_LOGINNAME, CppSQLiteUtility::formatSqlStr(userContext_->getUserInfoMgr()->getUserName()).c_str());
			CppSQLite3Query qSet = db_.execQuery(bufCount);
			if(!qSet.eof())
			{
				storageUserInfo.user_id = qSet.getInt64Field(0);
				storageUserInfo.name = qSet.getStringField(1);
				storageUserInfo.login_name = qSet.getStringField(2);
				storageUserInfo.email = qSet.getStringField(3);
				storageUserInfo.description = qSet.getStringField(4);
				return RT_OK;
			}
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

private:
	void createUserTable(const std::wstring& parent)
	{
		try
		{
			if (!SD::Utility::FS::is_exist(parent))
			{
				(void)SD::Utility::FS::create_directories(parent);
			}
			std::wstring path = parent + PATH_DELIMITER + SQLITE_CACHE_USERINFO;
			db_.open(SD::Utility::String::wstring_to_utf8(path).c_str());
			if(!db_.tableExists(TABLE_USER))
			{
				CppSQLite3Buffer bufSQL;
				(void)bufSQL.format("CREATE TABLE %s (\
									%s INTEGER PRIMARY KEY NOT NULL,\
									%s INTEGER NOT NULL,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s VARCHAR,\
									%s VARCHAR);", 
									TABLE_USER,
									USER_ROW_ID,
									USER_ROW_TYPE,
									USER_ROW_NAME,
									USER_ROW_LOGINNAME,
									USER_ROW_EMAIL,
									USER_ROW_DEPARTMENT);
				(void)db_.execDML(bufSQL);

				CppSQLite3Buffer bufSQLIdx1;
				(void)bufSQLIdx1.format("CREATE INDEX %s ON %s(%s);", 
					"tb_user_idx1", TABLE_USER, USER_ROW_ID);
				(void)db_.execDML(bufSQLIdx1);

				StorageUserInfo storageUserInfo;
				if(RT_OK == userContext_->getUserInfoMgr()->getCurUserInfo(storageUserInfo))
				{
					ShareUserInfo userInfo;
					userInfo.department(storageUserInfo.description);
					userInfo.email(storageUserInfo.email);
					userInfo.id(storageUserInfo.user_id);
					userInfo.loginName(storageUserInfo.login_name);
					userInfo.name(storageUserInfo.name);
					addUser(userInfo);
				}
			}
		}
		CATCH_SQLITE_EXCEPTION;
	}

	virtual std::string addUser(int64_t teamId, int64_t id)
	{
		UserTeamSpaceNodeInfoArray userInfos;
		PageParam page;
		page.limit = 1000;
		int64_t total = 0;
		userContext_->getTeamSpaceMgr()->getTeamSpaceListMemberInfo(teamId,"","all",page,total,userInfos);
		if(userInfos.empty())
		{
			return "";
		}

		ShareUserInfoList userInfoList;
		std::string userName = "";
		for(UserTeamSpaceNodeInfoArray::iterator it = userInfos.begin(); it != userInfos.end(); ++it)
		{
			ShareUserInfo userInfo;
			userInfo.id(it->teamInfo_.id());
			//userInfo.type(it->teamInfo_.type());
			userInfo.name(it->teamInfo_.name());
			userInfo.loginName(it->teamInfo_.loginName());
			userInfo.department(it->teamInfo_.desc());
			userInfoList.push_back(userInfo);

			if(id == it->teamInfo_.id())
			{
				userName = it->teamInfo_.name();
			}
		}
		addUser(userInfoList);

		return userName;	
	}

private:
	UserContext* userContext_;

	boost::mutex mutex_;
	CppSQLite3DB db_;
	std::map<int64_t, std::string> userInfo_;
};

CacheUserInfo* CacheUserInfo::create(UserContext* userContext, const std::wstring& parent)
{
	return static_cast<CacheUserInfo*>(new CacheUserInfoImpl(userContext, parent));
}
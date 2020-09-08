#include "UpdateGeneral.h"
#include "UpdateDBCommon.h"
#include "InIHelper.h"
#include "CppSQLite3.h"
#include "OperateFile.h"
#include "OperateSQL.h"
#include "Configure.h"
#include "ConfigureMgr.h"
#include "UserInfoMgr.h"
#include "UserContext.h"
#include "UserContextMgr.h"
#include "UpdateGeneralDefine.h"
#include "Version.h"
#include <map>
#include <vector>

#ifndef MODULE_NAME
#define MODULE_NAME ("UpdateDBMgr")
#endif

#define BACKUP_NAME (L"_Backup")

#pragma comment(lib, "Shlwapi.lib")

struct CommandInfo
{
	std::wstring  command;
	std::wstring param;
	CommandInfo():command(L""),param(L"")
	{
	}
};

struct UpdateInfo
{
	std::wstring version;
	CommandInfo commandInfo;
	
	UpdateInfo():version(L"")
	{
	}
}; 

typedef std::vector<UpdateInfo> UpdateInfoList;

class CUpdateGeneralImpl
{
public:
	CUpdateGeneralImpl(const std::wstring& oldversion, const std::wstring& newversion, UserContext * usercontext)
	{
		m_oldversion_ = oldversion;
		m_newversion_ = newversion;
		m_usercontext_ = usercontext;

		std::wstring logFile = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_NAME+PATH_DELIMITER+L"UpdateDB.log";
		ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_NAME + L"\\log4cpp.conf"), TP_FILE, SD::Utility::String::wstring_to_string(logFile));

		if ( usercontext )
		{
			userDataPath_ = m_usercontext_->getConfigureMgr()->getConfigure()->appUserDataPath();
		}
	}

public:
	bool UpdateDB()
	{
		std::wstring userid = SD::Utility::String::type_to_string<std::wstring>(m_usercontext_->getUserInfoMgr()->getUserId());
		std::wstring userdatapath = userDataPath_ + PATH_DELIMITER + userid;
		std::wstring userdatabackup = userdatapath + BACKUP_NAME;

		//userdatabackup = SD::Utility::String::replace_all(userdatabackup, USER_ID, userid);
		SD::Utility::FS::remove_all(userdatabackup);
		if(S_OK!=copy_dir(userdatapath.c_str(), userdatabackup.c_str()))
		{
			return false;
		}

		if(OperateCMD())
		{
			if(ERROR_SUCCESS!=SD::Utility::FS::rename(userdatapath, userdatapath + L"_tmp"))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "rename userdatapath failed.");
				return false;
			}
			if(ERROR_SUCCESS!=SD::Utility::FS::rename(userdatabackup, userdatapath))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "rename userdatabackup failed.");
				if(ERROR_SUCCESS!=SD::Utility::FS::rename(userdatapath + L"_tmp", userdatapath))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "restore userdatapath failed.");
				}
				
				return false;
			}
			if(ERROR_SUCCESS!=SD::Utility::FS::remove_all(userdatapath + L"_tmp"))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "remove userdatapath_tmp failed.");
				return false;
			}
			updateDataVersion(userid);
			return true;
		}

		return false;
	}

private:
	int32_t copy_dir(const wchar_t *strSourcePath, const wchar_t *strDestPath)
	{
		if (NULL == strSourcePath || NULL == strDestPath)
		{
			return ERROR_BAD_PATHNAME;
		}

		wchar_t src[MAX_PATH] = {0};
		wchar_t des[MAX_PATH] = {0};
		wcscpy_s(src, strSourcePath);
		wcscpy_s(des, strDestPath);

		SHFILEOPSTRUCT lpfile;
		ZeroMemory(&lpfile , sizeof(SHFILEOPSTRUCT));
		lpfile.hwnd = NULL;   
		lpfile.wFunc = FO_COPY;   
		lpfile.fFlags = FOF_NOCONFIRMATION|FOF_SILENT|FOF_NOERRORUI|FOF_NOCONFIRMMKDIR;   
		lpfile.pFrom = src;
		lpfile.pTo = des;   

		return SHFileOperation(&lpfile);
	}

private:
	int32_t getUpdateInfo(UpdateInfoList& updateInfoList)
	{
		try
		{
			CppSQLite3DB updateInfodb;
			CppSQLite3Buffer sql;
			CppSQLite3Query q;

			updateInfodb.open(SD::Utility::String::wstring_to_utf8((getInstallPath()+SQLITE_DBUPDATEINFO_TABLE)).c_str());
			sql.format("SELECT * FROM %s", DB_UPDATEINFO_TABLE_NAME);
			q = updateInfodb.execQuery(sql);
			if (q.eof())
			{
				return RT_SQLITE_NOEXIST;
			}

			while (!q.eof())
			{
				UpdateInfo updateInfo;
				updateInfo.version = SD::Utility::String::utf8_to_wstring(q.getStringField(0));
				if (Version(updateInfo.version) > Version(m_oldversion_))
				{
					updateInfo.commandInfo.command = SD::Utility::String::utf8_to_wstring(q.getStringField(1));
					updateInfo.commandInfo.param = SD::Utility::String::utf8_to_wstring(q.getStringField(2));
					updateInfoList.push_back(updateInfo);
				}				
				q.nextRow();
			}
			return RT_OK;
		}
		CATCH_SQLITE_EXCEPTION;
		return RT_SQLITE_ERROR;
	}

	bool OperateCMD()
	{
		UpdateInfoList updateInfoList;
		getUpdateInfo(updateInfoList);
		std::wstring workDirPath = OperateFileMgr::getInstance()->getDesDirPath();

		for (UpdateInfoList::const_iterator it = updateInfoList.begin(); it != updateInfoList.end(); ++it)
		{
			if(0==_tcsicmp(it->commandInfo.command.c_str(),COMMAND_IFCOPY)
				||0==_tcsicmp(it->commandInfo.command.c_str(),COMMAND_IFRENAME))
			{
				if(!OperateFileMgr::getInstance()->runCommand(it->commandInfo.command, it->commandInfo.param))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "runCommand %s error.", 
						SD::Utility::String::wstring_to_string(it->commandInfo.command).c_str());
					return false;
				}
			}
			else
			{
				std::wstring userid = SD::Utility::String::type_to_string<std::wstring>(m_usercontext_->getUserInfoMgr()->getUserId());
				userid += BACKUP_NAME;
				{
					OperateSQLMgr operatefilemgr(m_usercontext_);
					if(!operatefilemgr.runCommand(userid, it->commandInfo.param))
					{
						SERVICE_ERROR(MODULE_NAME, RT_ERROR, "runCommand %s error.", 
							SD::Utility::String::wstring_to_string(it->commandInfo.command).c_str());
						return false;
					}
				}
			}
		}
		OperateFileMgr::releaseInstance();
		return true;
	}

	void updateDataVersion(const std::wstring userid)
	{
		SetUserConfValue(CONF_VERSION_SECTION,CONF_VERSION_KEY, m_newversion_, userDataPath_, userid);
	}

private:
	std::wstring userDataPath_;

	std::wstring m_oldversion_;
	std::wstring m_newversion_;

	UserContext *m_usercontext_;
};

CUpdateGeneral::CUpdateGeneral(const std::wstring& oldversion, const std::wstring& newversion, UserContext * usercontext)
{
	m_impl = new CUpdateGeneralImpl(oldversion, newversion, usercontext);
}

CUpdateGeneral::~CUpdateGeneral()
{
	if ( m_impl )
	{
		delete m_impl;
		m_impl = NULL;
	}
}

bool CUpdateGeneral::UpdateDB()
{
	bool bret = false;

	if ( m_impl )
	{
		bret = m_impl->UpdateDB();
	}

	return bret;
}

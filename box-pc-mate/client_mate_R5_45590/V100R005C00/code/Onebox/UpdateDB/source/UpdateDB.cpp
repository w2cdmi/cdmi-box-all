#include "UpdateDB.h"
#include "UpdateDBCommon.h"
#include "InIHelper.h"
#include "CppSQLite3.h"
#include "OperateFile.h"
#include "OperateSQL.h"
#include <map>
#include <vector>
#include "Configure.h"
#include "ConfigureMgr.h"
#include "UserInfoMgr.h"
#include "UserContext.h"
#include "UserContextMgr.h"
#include "UpdateGeneral.h"
#include "DataTransfer.h"
#include "Version.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("UpdateDBMgr")
#endif

#pragma comment(lib, "Shlwapi.lib")
#define ONEBOX_VERSION_R3 (L"1.3.10.2038")
#define ONEBOX_VERSION_R5 (L"1.5.00.1325")

class UpdateDBImpl : public UpdateDBMgr
{
public:
	UpdateDBImpl(UserContext* usercontext) : 
		userContext_(usercontext)
	{
		std::wstring logFile = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+L"UpdateDB.log";
		ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"), TP_FILE, SD::Utility::String::wstring_to_string(logFile));
	}

	~UpdateDBImpl()
	{
		try
		{
			ISSP_LogExit();
		}
		catch(...)
		{
		}
	}

public:
	bool update()
	{
		if(ONEBOX_VERSION_R3 == m_old_version_)
		{
			CDataTransfer datatransfer(SD::Utility::String::type_to_string<std::wstring>(
				userContext_->getUserInfoMgr()->getUserId()));
			if (!datatransfer.UpdateDB())
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Data transfer update db failed.");
				return false;
			}
		}

		CUpdateGeneral updategeneral(m_old_version_, m_new_version_, userContext_);
		if (!updategeneral.UpdateDB())
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "Update general update db failed.");
			return false;
		}

		return true;
	}

	bool isUpdate()
	{
		m_new_version_ = getNewVersion();
		m_old_version_ = getOldVersion();

		SERVICE_INFO(MODULE_NAME, RT_OK, "new_version: %s; old_version: %s"
			, SD::Utility::String::wstring_to_string(m_new_version_).c_str()
			, SD::Utility::String::wstring_to_string(m_old_version_).c_str());

		if(m_old_version_.empty())
		{
			return false;
		}
		return Version(m_new_version_) > Version(m_old_version_);
	}

private:
	std::wstring getNewVersion()
	{
		CInIHelper InIHelper(SD::Utility::FS::format_path((getInstallPath()+DEFAULT_CONFIG_NAME).c_str()));
		return InIHelper.GetString(CONF_VERSION_SECTION, CONF_VERSION_KEY, L"");
	}

	std::wstring getOldVersion()
	{
		std::wstring oldversion = L"";
		std::wstring userID = SD::Utility::String::type_to_string<std::wstring>(userContext_->getUserInfoMgr()->getUserId());
		std::wstring userDataPath = userContext_->getConfigureMgr()->getConfigure()->appUserDataPath();
		oldversion = GetUserConfValue(CONF_VERSION_SECTION,CONF_VERSION_KEY, oldversion, userDataPath, userID);
		//if can't get the old version
		if(oldversion.empty())
		{
			//if the installation directory has "UserData" directory, oldversion is 2038.
			if(SD::Utility::FS::is_directory(SD::Utility::FS::get_work_directory()+PATH_DELIMITER+DEFAULT_USER_DATA_NAME))
			{
				oldversion = ONEBOX_VERSION_R3;
			}
			//else if appUserData directory exist, oldversion is 1325.
			else if(SD::Utility::FS::is_directory(userDataPath + PATH_DELIMITER + userID))
			{
				oldversion = ONEBOX_VERSION_R5;
			}
			//otherwise it is a new installation.
			else
			{
				//Write the new version to the configuration file
				SetUserConfValue(CONF_VERSION_SECTION,CONF_VERSION_KEY, m_new_version_, userDataPath, userID);
			}
		}
		return oldversion;
	}

private:
	std::wstring m_old_version_;
	std::wstring m_new_version_;

	UserContext *userContext_;
};

UpdateDBMgr* UpdateDBMgr::instance_ = NULL;

UpdateDBMgr* UpdateDBMgr::getInstance(UserContext* usercontext)
{
	if (NULL == instance_)
	{
		instance_ = static_cast<UpdateDBMgr*>(new UpdateDBImpl(usercontext));
	}
	return instance_;
}

void UpdateDBMgr::release()
{
	if (NULL != instance_)
	{
		delete instance_;
		instance_ = NULL;
	}
}
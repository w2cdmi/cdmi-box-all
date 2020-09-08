#include "Utility.h"
#include "CommonDefine.h"

#define CONF_VERSION_SECTION (L"VERSION")
#define CONF_VERSION_KEY (L"Version")

#define DEFAULT_USER_DATA_NAME (L"UserData")

#define SQLITE_DBUPDATEINFO_TABLE (L"updateInfo.db")

#ifndef DB_UPDATEINFO_TABLE_NAME
#define DB_UPDATEINFO_TABLE_NAME ("tb_dbUpdateInfo")
#endif

#ifndef DB_UPDATEINFO_VERSION
#define DB_UPDATEINFO_VERSION ("version")
#endif

#ifndef DB_UPDATEINFO_COMMAND
#define DB_UPDATEINFO_COMMAND ("command")
#endif

#ifndef DB_UPDATEINFO_PARAM
#define DB_UPDATEINFO_PARAM ("param")
#endif

template<typename T>
static T GetUserConfValue(const std::wstring& section, const std::wstring& valueKey, const T& defaultValue,const std::wstring& userDataPath,const std::wstring& userID)
{
	if (0==_tcsicmp(userDataPath.c_str(),L"") ||0==_tcsicmp(userID.c_str(),L"") )
	{
		return defaultValue;;
	}
	std::wstring configPath = userDataPath;
	configPath += PATH_DELIMITER;
	configPath += userID;
	configPath += PATH_DELIMITER;
	configPath += userID;
	configPath += PATH_DELIMITER;
	configPath += DEFAULT_CONFIG_NAME;
	CInIHelper InIHelper(SD::Utility::FS::format_path(configPath.c_str()));
	return InIHelper.GetValue<T>(section,valueKey,defaultValue);
}

template<typename T>
static void SetUserConfValue(const std::wstring& section, const std::wstring& valueKey, T const & defaultValue,const std::wstring& userDataPath, const std::wstring& userID)
{
	if (0==_tcsicmp(userDataPath.c_str(),L"") ||0==_tcsicmp(userID.c_str(),L"") )
	{
		return;
	}
	std::wstring configPath = userDataPath;
	configPath += PATH_DELIMITER;
	configPath += userID;
	configPath += PATH_DELIMITER;
	configPath += userID;
	configPath += PATH_DELIMITER;
	configPath += DEFAULT_CONFIG_NAME;
	CInIHelper InIHelper(SD::Utility::FS::format_path(configPath.c_str()));
	InIHelper.SetValue<T>(section,valueKey,defaultValue);
}

static std::wstring getInstallPath()
{
	std::wstring wstrInstallPath = L"";
	if (0 != SD::Utility::Registry::get(HKEY_LOCAL_MACHINE,ONEBOX_REG_PATH,ONEBOX_REG_PATH_KEY,wstrInstallPath) &&
		0 != SD::Utility::Registry::get(HKEY_CURRENT_USER,ONEBOX_REG_PATH,ONEBOX_REG_PATH_KEY,wstrInstallPath))
	{
		return L"";
	}
	if (wstrInstallPath.length() !=0 && (wstrInstallPath[wstrInstallPath.length()-1] != L'\\' || 
		wstrInstallPath[wstrInstallPath.length()-1] != L'/'))
	{
		wstrInstallPath += PATH_DELIMITER;
	}
	wstrInstallPath = SD::Utility::FS::format_path(wstrInstallPath.c_str());
	return wstrInstallPath;
}



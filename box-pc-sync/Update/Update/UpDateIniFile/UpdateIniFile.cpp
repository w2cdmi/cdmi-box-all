#include "../stdafx.h"
#include "UpdateIniFile.h"
#include <utility>

UpdateIniFile::UpdateIniFile(void)
{
	wstring wstrAppPath = L"";
	
	if(Utility::GetAppPath(wstrAppPath))
	{
		m_strFilePath = wstrAppPath + CSE_INI_FILE;
		m_UpdateFilePath = wstrAppPath + CSE_UPDATEINI_FILE;
	}
}

UpdateIniFile::~UpdateIniFile(void)
{
}


void UpdateIniFile::GetPersistValue(void)
{
	CInIHelper IniHelper(m_strFilePath);
	map<wstring,wstring> PersistValueTemp;
	wstring wstrMonitorPath = IniHelper.GetString(CONF_CONFIGURE_SECTION,CONF_MONITOR_PATH_KEY,L"");
	wstring wstrCachePath = IniHelper.GetString(CONF_CONFIGURE_SECTION,CONF_CACHE_PATH_KEY,L"");
	PersistValueTemp.insert(make_pair(CONF_MONITOR_PATH_KEY,wstrMonitorPath));
	PersistValueTemp.insert(make_pair(CONF_CACHE_PATH_KEY,wstrCachePath));
	PersistValue.insert(make_pair(CONF_CONFIGURE_SECTION,PersistValueTemp));

	wstring wstrServerUrl = IniHelper.GetString(CONF_NETWORK_SECTION,CONF_SERVER_URL_KEY,L"");
	wstring wstrUseSSL = IniHelper.GetString(CONF_NETWORK_SECTION,CONF_USE_SSL,L"");
	PersistValueTemp.clear();
	PersistValueTemp.insert(make_pair(CONF_SERVER_URL_KEY,wstrServerUrl));
	PersistValueTemp.insert(make_pair(CONF_USE_SSL,wstrUseSSL));
	PersistValue.insert(make_pair(CONF_NETWORK_SECTION,PersistValueTemp));

	wstring wstrBootStartRun = IniHelper.GetString(CONF_USERINFO_SECTION,CONF_LOGIN_BOOTSTARTRUN_KEY,L"");
	wstring wstrUserName = IniHelper.GetString(CONF_USERINFO_SECTION,CONF_USERNAME_KEY,L"");
	wstring wstrPassWord = IniHelper.GetString(CONF_USERINFO_SECTION,CONF_PASSWORD_KEY,L"");
	wstring wstrLoginType = IniHelper.GetString(CONF_USERINFO_SECTION,CONF_LOGIN_TYPE_KEY,L"");
	PersistValueTemp.clear();
	PersistValueTemp.insert(make_pair(CONF_LOGIN_BOOTSTARTRUN_KEY,wstrBootStartRun));
	PersistValueTemp.insert(make_pair(CONF_USERNAME_KEY,wstrUserName));
	PersistValueTemp.insert(make_pair(CONF_PASSWORD_KEY,wstrPassWord));
	PersistValueTemp.insert(make_pair(CONF_LOGIN_TYPE_KEY,wstrLoginType));
	PersistValue.insert(make_pair(CONF_USERINFO_SECTION,PersistValueTemp));
}

bool UpdateIniFile::UpdateConfigFile(void)
{
	bool bRet = true;

	try
	{
		GetPersistValue();
		CopyFile(m_UpdateFilePath.c_str(),m_strFilePath.c_str(),false);
		CInIHelper IniHelper(m_strFilePath);
		map<wstring,map<wstring,wstring> >::iterator itr = PersistValue.begin();
		while (itr !=PersistValue.end() )
		{
			map<wstring,wstring> ::iterator itrs = (itr->second).begin();
			while (itrs !=  (itr->second).end())
			{
				IniHelper.SetString(itr->first,itrs->first,itrs->second);
				itrs++;
			}
		
			itr++;
		}

		bRet = true;
	}
	catch(...)
	{
		bRet = false;
	}

	return bRet;
}


bool UpdateIniFile::GetVersion(wstring& strOldVersion,wstring& strNewVersion)
{
	bool bRet = true;
	CInIHelper IniHelper(m_strFilePath);
	CInIHelper IniHelperNew(m_UpdateFilePath);

	strOldVersion = IniHelper.GetString(CONF_VERSION_SECTION,CONF_VERSION_KEY,L"");
	strNewVersion = IniHelperNew.GetString(CONF_VERSION_SECTION,CONF_VERSION_KEY,L"");

	if(L""==strOldVersion || L""==strNewVersion)
	{
		bRet = false;
	}
	return bRet;
}

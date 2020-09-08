#include "SysConfigureMgr.h"
#include "Utility.h"
#include <Windows.h>
#include <Shlobj.h>
#include "NetworkMgr.h"
#include "InIHelper.h"
#include <Shlwapi.h>
#include "ConfigureMgr.h"

using namespace SD::Utility;

#ifndef MODULE_NAME
#define MODULE_NAME ("SysConfigureMgr")
#endif

#define CONF_CONFIGURE_SECTION (L"CONFIGURE")
#define CONF_MONITOR_PATH_KEY (L"MonitorRootPath")

#define CONF_BACKUPDISABLE_ATTR (L"BackupDisableAttr")
#define CONF_BACKUPDISABLE_SPECIAL (L"BackupDisableSpecial")
#define CONF_BACKUPDISABLE_PATH (L"BackupDisablePath")
#define CONF_BACKUPDISABLE_SYNCDIR (L"BackupDisableSyncDir")
#define CONF_NAME_STARTWITH (L"NameStartWith")
#define CONF_NAME_ENDWITH (L"NameEndWith")
#define CONF_NAME_FILTER (L"NameFilter")

class SysConfigureMgrImpl : public SysConfigureMgr
{
public:
	SysConfigureMgrImpl(UserContext* userContext):userContext_(userContext)
	{
		init_ = false;
	}

	virtual int32_t getBackupDisableList(std::list<std::wstring>& backupDisableList)
	{
		if(!init_)
		{
			init();
		}
		backupDisableList = backupDisableList_;
		return RT_OK;
	}

	virtual bool isBackupDisable(const std::wstring& path)
	{
		if(!init_)
		{
			init();
		}
		for(std::list<std::wstring>::const_iterator it = backupDisableList_.begin(); it!=backupDisableList_.end(); ++it)
		{
			if(0==StrCmpI(it->c_str(), path.c_str()))
			{
				return true;
			}
		}
		return false;
	}

	virtual bool isBackupDisableAttr(int32_t attr)
	{
		if(!init_)
		{
			init();
		}
		return 0!=(attr&backupDisableAttr_);
	}

	virtual SysFilter getSysFilter()
	{
		if(!init_)
		{
			init();
		}
		return sysFilter_;
	}
private:
	void init()
	{
		MAKE_CLIENT(client);
		ServerSysConfig serverSysConfig;
		client().getServerSysConfig(serverSysConfig, OPTION_SYSTEM_FORBIDDEN_BACKUP);
		/*
		<BackupDisableAttr=6>
			#define FILE_ATTRIBUTE_HIDDEN               0x00000002  
			#define FILE_ATTRIBUTE_SYSTEM               0x00000004 
		<BackupDisableSpecial=36|38|42>
			//#define CSIDL_WINDOWS                   0x0024     36   // GetWindowsDirectory()
			//#define CSIDL_PROGRAM_FILES             0x0026     38   // C:\Program Files
			//#define CSIDL_PROGRAM_FILESX86          0x002a     42   // x86 C:\Program Files on RISC
		<BackupDisablePath=C:\appdata|C:\background|C:\Drivers|C:\Hotfix|C:\Intel|C:\IrmTool|C:\PerfLogs>
		<BackupDisableSyncDir=0>	//默认0，不禁止选择同步目录；1：禁止选择同步目录
		<NameStartWith=.|~>
		<NameEndWith=.|.tmp|.lnk>
		<NameFilter=Onebox|Outlook>
		*/
		ruleStr_ = String::utf8_to_wstring(serverSysConfig.backupForbiddenRule());
		initBackupDisableAttr(ruleStr_);
		initBackupDisableList(ruleStr_);
		initSysFilter(ruleStr_);
		init_ = true;
	}

	void initBackupDisableAttr(const std::wstring& ruleStr)
	{
		HSLOG_TRACE(MODULE_NAME, RT_OK, "ruleStr:%s", String::wstring_to_string(ruleStr).c_str());
		std::wstring::size_type pos = ruleStr.find(CONF_BACKUPDISABLE_ATTR);
		if (std::wstring::npos != pos)
		{
			std::wstring::size_type endPos = ruleStr.find_first_of(L">", pos);
			if(endPos > pos+18)
			{
				backupDisableAttr_ = String::string_to_type<int32_t>(ruleStr.substr(pos+18, endPos-pos-18));
			}
		}
		else
		{
			backupDisableAttr_ = userContext_->getConfigureMgr()->getConfigure()->disableAttr();
		}
	}

	void initBackupDisableList(const std::wstring& ruleStr)
	{
		HSLOG_TRACE(MODULE_NAME, RT_OK, "ruleStr:%s", String::wstring_to_string(ruleStr).c_str());
		std::wstring backupDisableSpecial;
		std::wstring backupDisablePath;
		std::wstring backupDisableSyncDir;
		std::wstring::size_type pos = ruleStr.find(CONF_BACKUPDISABLE_SPECIAL);
		std::stringstream printInfo;
		printInfo << "initBackupDisableList:";
		if (std::wstring::npos != pos)
		{
			std::wstring::size_type endPos = ruleStr.find_first_of(L">", pos);
			if(endPos > pos+21)
			{
				backupDisableSpecial = ruleStr.substr(pos+21, endPos-pos-21);
			}
		}
		else
		{
			TCHAR path[MAX_PATH];
			if(SHGetSpecialFolderPath(0,path,CSIDL_WINDOWS,0))
			{
				std::wstring tempPath = path;
				printInfo << String::wstring_to_string(tempPath);
				backupDisableList_.push_back(tempPath);
			}
			if(SHGetSpecialFolderPath(0,path,CSIDL_PROGRAM_FILES,0))
			{
				std::wstring tempPath = path;
				//Program Files (x86) -> Program Files
				pos = tempPath.find(L" (x86)");
				if(std::wstring::npos != pos)
				{
					tempPath = tempPath.substr(0, pos);
				}
				printInfo << String::wstring_to_string(tempPath);
				backupDisableList_.push_back(tempPath);
			}
			if(SHGetSpecialFolderPath(0,path,CSIDL_PROGRAM_FILESX86,0))
			{
				std::wstring tempPath = path;
				printInfo << String::wstring_to_string(tempPath);
				backupDisableList_.push_back(tempPath);
			}
		}

		pos = ruleStr.find(CONF_BACKUPDISABLE_PATH);
		if (std::wstring::npos != pos)
		{
			std::wstring::size_type endPos = ruleStr.find_first_of(L">", pos);
			if(endPos>pos+18)
			{
				backupDisablePath = ruleStr.substr(pos+18, endPos-pos-18);
			}
		}

		pos = ruleStr.find(CONF_BACKUPDISABLE_SYNCDIR);
		if (std::wstring::npos != pos)
		{
			std::wstring::size_type endPos = ruleStr.find_first_of(L">", pos);
			if(endPos>pos+21)
			{
				backupDisableSyncDir = ruleStr.substr(pos+21, endPos-pos-21);
			}
		}

		if(!backupDisableSpecial.empty())
		{
			std::vector<std::wstring> specialInfo; 
			String::split(backupDisableSpecial, specialInfo, L"|");
			TCHAR path[MAX_PATH];
			for(u_int i = 0; i<specialInfo.size(); ++i)
			{
				int csidl = String::string_to_type<int>(specialInfo[i]);
				if(SHGetSpecialFolderPath(0,path,csidl,0))
				{
					std::wstring tempPath = path;
					//Program Files (x86) -> Program Files
					pos = tempPath.find(L" (x86)");
					if(CSIDL_PROGRAM_FILES==csidl && std::wstring::npos != pos)
					{
						tempPath = tempPath.substr(0, pos);
					}
					printInfo << String::wstring_to_string(tempPath);
					backupDisableList_.push_back(tempPath);
				}
			}
		}

		if(!backupDisablePath.empty())
		{
			std::vector<std::wstring> pathInfo; 
			String::split(backupDisablePath, pathInfo, L"|");
			for(u_int i = 0; i<pathInfo.size(); ++i)
			{
				printInfo << String::wstring_to_string(pathInfo[i]);
				backupDisableList_.push_back(pathInfo[i]);
			}
		}

		if(L"1"==backupDisableSyncDir)
		{
			std::wstring syncPath = getSyncPath();
			if(!syncPath.empty())
			{
				printInfo << String::wstring_to_string(syncPath);
				backupDisableList_.push_back(syncPath);
			}
		}
		HSLOG_TRACE(MODULE_NAME, RT_OK, "%s", printInfo.str().c_str());
	}

	void initSysFilter(const std::wstring& ruleStr)
	{
		std::wstring::size_type pos = ruleStr.find(CONF_NAME_STARTWITH);
		if (std::wstring::npos != pos)
		{
			std::wstring::size_type endPos = ruleStr.find_first_of(L">", pos);
			if(endPos > pos+14)
			{
				sysFilter_.nameStartFilter = ruleStr.substr(pos+14, endPos-pos-14);
			}
		}
		pos = ruleStr.find(CONF_NAME_ENDWITH);
		if (std::wstring::npos != pos)
		{
			std::wstring::size_type endPos = ruleStr.find_first_of(L">", pos);
			if(endPos > pos+12)
			{
				sysFilter_.nameEndFilter = ruleStr.substr(pos+12, endPos-pos-12);
			}
		}
		pos = ruleStr.find(CONF_NAME_FILTER);
		if (std::wstring::npos != pos)
		{
			std::wstring::size_type endPos = ruleStr.find_first_of(L">", pos);
			if(endPos > pos+11)
			{
				sysFilter_.nameFilter = ruleStr.substr(pos+11, endPos-pos-11);
			}
		}
		HSLOG_TRACE(MODULE_NAME, RT_OK, "ruleStr:%s", String::wstring_to_string(ruleStr).c_str());
	}

	std::wstring getSyncPath()
	{
		std::wstring wstrInstallPath = L"";
		if (0 != SD::Utility::Registry::get(HKEY_LOCAL_MACHINE,ONEBOXSYNC_REG_PATH,ONEBOX_REG_PATH_KEY,wstrInstallPath))
			return L"";
		std::wstring configePath = wstrInstallPath + L"\\Config.ini";
		CInIHelper iniHelper(SD::Utility::FS::format_path(configePath));
		std::wstring syncPath = SD::Utility::FS::format_path(iniHelper.GetString(CONF_CONFIGURE_SECTION,CONF_MONITOR_PATH_KEY, L""));
		// rtrim all the '\'
		std::wstring::size_type pos = syncPath.rfind(PATH_DELIMITER);
		while (!syncPath.empty() && pos == (syncPath.length()-1))
		{
			syncPath = syncPath.substr(0, pos);
			pos = syncPath.rfind(PATH_DELIMITER);
		}
		return syncPath;
	}

private:
	UserContext* userContext_;
	bool init_;
	std::wstring ruleStr_;
	int32_t backupDisableAttr_;
	std::list<std::wstring> backupDisableList_;
	SysFilter sysFilter_;
};

SysConfigureMgr* SysConfigureMgr::create(UserContext* userContext)
{
	return static_cast<SysConfigureMgr*>(new SysConfigureMgrImpl(userContext));
}

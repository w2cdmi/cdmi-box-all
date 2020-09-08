#include "OperateFile.h"
#include "Utility.h"
#include "UpdateDBCommon.h"
#include <tchar.h>

#ifndef MODULE_NAME
#define MODULE_NAME ("OperateFileMgr")
#endif

class OperateFileImpl : public OperateFileMgr
{
public:
	bool runCommand(std::wstring command,std::wstring param)
	{
		if(0==_tcsicmp(command.c_str(),COMMAND_IFCOPY))
		{
			return IfCopy(param);
		}
		else if(0==_tcsicmp(command.c_str(),COMMAND_IFRENAME))
		{
			return IfRename(param);
		}
		return true;
	}
	
	std::wstring getSourceDirPath()
	{
		return sourceDirPath_;
	}

	std::wstring getDesDirPath()
	{
		return desDirPath_;
	}

private:
	bool IfCopy(const std::wstring& param)
	{
		std::wstring sourceDirName = L"";
		std::wstring desDirName = L"";
		parseParam(param,sourceDirName,desDirName);
		sourceDirPath_ = getInstallPath() + sourceDirName;
		desDirPath_ = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ ONEBOX_APP_DIR +PATH_DELIMITER+ desDirName;
		int32_t ret = CopyFiles((sourceDirPath_+ L"\\*.*").c_str(),desDirPath_.c_str());
		if(0 != ret)
		{
			SERVICE_ERROR(MODULE_NAME,ret,"copy file failed,source:%s,destination:%s.",SD::Utility::String::wstring_to_string(sourceDirPath_).c_str(),SD::Utility::String::wstring_to_string(desDirPath_).c_str());
			return false;
		}
		return true;
	}

	bool IfRename(const std::wstring& param)
	{
		std::wstring sourceDirName = L"";
		std::wstring desDirName = L"";
		parseParam(param,sourceDirName,desDirName);
		sourceDirPath_ = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ ONEBOX_APP_DIR +PATH_DELIMITER + sourceDirName;
		desDirPath_ = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ ONEBOX_APP_DIR +PATH_DELIMITER+ desDirName;
		int32_t ret = CopyFiles(sourceDirPath_.c_str(),desDirPath_.c_str());
		if (0 != ret)
		{
			SERVICE_ERROR(MODULE_NAME,ret,"rename file failed,source:%s,destination:%s.",SD::Utility::String::wstring_to_string(sourceDirPath_).c_str(),SD::Utility::String::wstring_to_string(desDirPath_).c_str());
			return false;
		}
		return true;
	}

	void parseParam(const std::wstring& param,std::wstring& source,std::wstring& destination)
	{
		if (0==_tcsicmp(param.c_str(),L""))
		{
			return;
		}

		size_t nPos = param.find_first_of(L",");
		if(std::wstring::npos == nPos||nPos == (param.length()-1))
		{
			return;
		}
		source = param.substr(0,nPos);
		destination = param.substr(nPos+1);
	}

	int32_t CopyFiles(const wchar_t *strSourcePath, const wchar_t *strDestPath)
	{
		if (NULL == strSourcePath || NULL == strDestPath)
		{
			return RT_INVALID_PARAM;
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

	int32_t RenameFile(const wchar_t *strSourcePath, const wchar_t *strDestPath)
	{
		if (NULL == strSourcePath || NULL == strDestPath)
		{
			return RT_INVALID_PARAM;
		}

		wchar_t src[MAX_PATH] = {0};
		wchar_t des[MAX_PATH] = {0};
		wcscpy_s(src, strSourcePath);
		wcscpy_s(des, strDestPath);

		SHFILEOPSTRUCT lpfile;
		ZeroMemory(&lpfile , sizeof(SHFILEOPSTRUCT));
		lpfile.hwnd = NULL;   
		lpfile.wFunc = FO_RENAME;   
		lpfile.fFlags = FOF_NOCONFIRMATION|FOF_SILENT|FOF_NOERRORUI|FOF_NOCONFIRMMKDIR;   
		lpfile.pFrom = src;
		lpfile.pTo = des;   

		return SHFileOperation(&lpfile);
	}
private:
	std::wstring sourceDirPath_;
	std::wstring desDirPath_;
};


OperateFileMgr* OperateFileMgr::instance_ = NULL;

OperateFileMgr* OperateFileMgr::getInstance()
{
	if (NULL == instance_)
	{
		instance_ = static_cast<OperateFileMgr*>(new OperateFileImpl());
	}
	return instance_;
}

void OperateFileMgr::releaseInstance()
{
	if (NULL != instance_)
	{
		delete instance_;
		instance_ = NULL;
	}
}


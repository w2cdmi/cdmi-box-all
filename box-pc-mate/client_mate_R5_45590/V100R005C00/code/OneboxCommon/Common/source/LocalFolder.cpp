#include "LocalFolder.h"
#include "LocalFile.h"
#include "Utility.h"
#include "SmartHandle.h"
#include <windows.h>
#include "SysConfigureMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("LocalFolder")
#endif

#define UR_BUF_LEN 1024

LocalFolder::LocalFolder(const Path& path, UserContext* userContext)
    : IFolder(path)
    , userContext_(userContext)
{
}

LocalFolder::~LocalFolder()
{
}

int32_t LocalFolder::create()
{
    if(path_.empty())
    {
        error(RT_INVALID_PARAM);
        return RT_INVALID_PARAM;
    }

    if(Utility::FS::is_exist(path_))
    {
        error(RT_FILE_EXIST_ERROR);
        HSLOG_ERROR(MODULE_NAME, RT_FILE_EXIST_ERROR,
                    "create local folder %s failed, folder already exist.",
                    Utility::String::wstring_to_string(path_).c_str());
        return RT_FILE_EXIST_ERROR;
    }

    int32_t ret = Utility::FS::create_directory(path_);

    if(RT_OK != ret)
    {
        error(ret);
        HSLOG_ERROR(MODULE_NAME, ret,
                    "create local folder %s failed.",
                    Utility::String::wstring_to_string(path_).c_str());
        return ret;
    }

    FILE_DIR_INFO fileDirInfo;
    (void)getProperty(fileDirInfo, PROPERTY_MASK_ALL);
    return RT_OK;
}

int32_t LocalFolder::remove()
{
    if(path_.empty())
    {
        error(RT_INVALID_PARAM);
        return RT_INVALID_PARAM;
    }

    if(!Utility::FS::is_exist(path_))
    {
        error(RT_FILE_NOEXIST_ERROR);
        HSLOG_ERROR(MODULE_NAME, RT_FILE_NOEXIST_ERROR,
                    "remove local folder %s failed, folder not exist.",
                    Utility::String::wstring_to_string(path_).c_str());
        return RT_FILE_NOEXIST_ERROR;
    }

    int32_t ret = Utility::FS::remove(path_);

    if(RT_OK != ret)
    {
        error(ret);
        HSLOG_ERROR(MODULE_NAME, ret,
                    "remove local folder %s failed.",
                    Utility::String::wstring_to_string(path_).c_str());
        return ret;
    }

    return RT_OK;
}

int32_t LocalFolder::rename(const std::wstring& new_name)
{
    if(path_.empty() || new_name.empty())
    {
        error(RT_INVALID_PARAM);
        return RT_INVALID_PARAM;
    }

    if(Utility::FS::get_file_name(path_) == new_name)
    {
        error(RT_FILE_EXIST_ERROR);
        HSLOG_ERROR(MODULE_NAME, RT_FILE_EXIST_ERROR,
                    "rename local folder %s failed, same path exist.",
                    Utility::String::wstring_to_string(path_).c_str());
        return RT_FILE_EXIST_ERROR;
    }

    std::wstring newPath = Utility::FS::get_parent_path(path_) + PATH_DELIMITER + new_name;
    int32_t ret = Utility::FS::rename(path_, newPath);

    if(RT_OK != ret)
    {
        error(ret);
        HSLOG_ERROR(MODULE_NAME, ret,
                    "rename local folder %s failed.",
                    Utility::String::wstring_to_string(path_).c_str());
        return ret;
    }

    property_.name = new_name;
    return RT_OK;
}

int32_t LocalFolder::copy(const Path& new_parent, bool autoRename)
{
    return RT_NOT_IMPLEMENT;
}

int32_t LocalFolder::move(const Path& new_parent, bool autoRename)
{
    std::wstring newParent = new_parent.path();

    if(path_.empty() || newParent.empty())
    {
        error(RT_INVALID_PARAM);
        return RT_INVALID_PARAM;
    }

    if(Utility::FS::get_parent_path(path_) == newParent)
    {
        error(RT_FILE_EXIST_ERROR);
        HSLOG_ERROR(MODULE_NAME, RT_FILE_EXIST_ERROR,
                    "move local folder %s failed, same path exist.",
                    Utility::String::wstring_to_string(path_).c_str());
        return RT_FILE_EXIST_ERROR;
    }

    std::wstring newPath = newParent + PATH_DELIMITER + Utility::FS::get_file_name(path_);
    int32_t ret = Utility::FS::rename(path_, newPath);

    if(RT_OK != ret)
    {
        error(ret);
        HSLOG_ERROR(MODULE_NAME, ret,
                    "move local folder %s failed.",
                    Utility::String::wstring_to_string(path_).c_str());
        return ret;
    }

    property_.parent = new_parent.parent();
    return RT_OK;
}

bool LocalFolder::isExist()
{
    if(path_.empty())
    {
        error(RT_INVALID_PARAM);
        return false;
    }

    return Utility::FS::is_exist(path_);
}

int32_t LocalFolder::listFolder(LIST_FOLDER_RESULT& result)
{
    result.clear();

    if(path_.empty())
    {
        error(RT_INVALID_PARAM);
        return RT_INVALID_PARAM;
    }

    if(!Utility::FS::is_exist(path_))
    {
        error(RT_FILE_NOEXIST_ERROR);
        HSLOG_ERROR(MODULE_NAME, RT_FILE_NOEXIST_ERROR,
                    "list local folder %s failed, folder is not exist.",
                    Utility::String::wstring_to_string(path_).c_str());
        return RT_FILE_NOEXIST_ERROR;
    }

    int32_t ret = RT_OK;
   	LIST_FOLDER_RESULT files;

	if (Utility::FS::is_local_root(path_))
    //if(path_.length() == 2 && path_.back() == L':')
    {
        WIN32_FIND_DATA wfd;
        HANDLE hFind = FindFirstFile(std::wstring(path_ + L"\\*").c_str(), &wfd);

        if(hFind == INVALID_HANDLE_VALUE)
        {
            return GetLastError();
        }

        std::wstring tempName = L"";       

        while(FindNextFile(hFind, &wfd))
        {
            tempName = wfd.cFileName;
            if((L"." != tempName) && (L".." != tempName) && !userContext_->getSysConfigureMgr()->isBackupDisableAttr(wfd.dwFileAttributes))
            {
                FILE_DIR_INFO fileDirInfo = LocalFile::getPropertyByPath(path_ + PATH_DELIMITER + tempName);
                fileDirInfo.parent = property_.id;
                fileDirInfo.name = tempName;
				/*
                if((wfd.dwFileAttributes&FILE_ATTRIBUTE_DIRECTORY)!=0)
                {
                    fileDirInfo.type = FILE_TYPE_DIR;
                }

                fileDirInfo.ctime = LocalFile::filetimetoll(wfd.ftCreationTime);
                fileDirInfo.mtime = LocalFile::filetimetoll(wfd.ftLastWriteTime);
				int64_t wfdsize=wfd.nFileSizeHigh;
                fileDirInfo.size = (wfdsize<<32) +wfd.nFileSizeLow;*/

                if(fileDirInfo.type == FILE_TYPE_FILE)
                {
                    files.push_back(fileDirInfo);
                }
                else
                {
                    result.push_back(fileDirInfo);
                }
            }
        }
		if ( INVALID_HANDLE_VALUE != hFind )
		{
			(void)FindClose(hFind);
			hFind = NULL;
		}
    }
    else
    {
		SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\" + path_).c_str(),
			GENERIC_READ,
			FILE_SHARE_READ,
			NULL,
			OPEN_ALWAYS,
			FILE_ATTRIBUTE_NORMAL | FILE_FLAG_BACKUP_SEMANTICS,
			NULL);

		if(INVALID_HANDLE_VALUE == hFile)
		{
			ret = GetLastError();
			error(ret);
			HSLOG_ERROR(MODULE_NAME, ret,
				"list folder faild, open handle failed.");
			return ret;
		}

		std::auto_ptr<char> buf(new char[UR_BUF_LEN]);
		(void)memset_s(buf.get(), UR_BUF_LEN, 0, UR_BUF_LEN);
		FILE_ID_BOTH_DIR_INFO *fileInfo = (PFILE_ID_BOTH_DIR_INFO)buf.get();

		fileInfo = (PFILE_ID_BOTH_DIR_INFO)((CHAR*)fileInfo + fileInfo->NextEntryOffset);
        if(!GetFileInformationByHandleEx(hFile, FileIdBothDirectoryInfo, (LPVOID)buf.get(), UR_BUF_LEN))
        {
            ret = GetLastError();
            error(ret);
            HSLOG_ERROR(MODULE_NAME, ret,
                        "list folder faild, get parent information failed.");
            return ret;
        }

        //fileInfo = (PFILE_ID_BOTH_DIR_INFO)((CHAR*)fileInfo + fileInfo->NextEntryOffset);
        std::wstring name = L"";

        //LIST_FOLDER_RESULT files;
        do
        {
            while(true)
            {
                name = L"";
                name.append(fileInfo->FileName, fileInfo->FileNameLength / sizeof(wchar_t));

                if(L"." != name && L".." != name&& !userContext_->getSysConfigureMgr()->isBackupDisableAttr(fileInfo->FileAttributes))
                {
                    FILE_DIR_INFO fileDirInfo;
                    fileDirInfo.id = LocalFile::li64toll(fileInfo->FileId);
                    fileDirInfo.parent = property_.id;
                    fileDirInfo.name = name;

                    if(fileInfo->FileAttributes & FILE_ATTRIBUTE_DIRECTORY)
                    {
                        fileDirInfo.type = FILE_TYPE_DIR;
                    }

                    fileDirInfo.ctime = LocalFile::li64toll(fileInfo->CreationTime);
                    fileDirInfo.mtime = LocalFile::li64toll(fileInfo->LastWriteTime);
                    fileDirInfo.size = LocalFile::li64toll(fileInfo->EndOfFile);

                    if(fileDirInfo.type == FILE_TYPE_FILE)
                    {
                        files.push_back(fileDirInfo);
                    }
                    else
                    {
                        result.push_back(fileDirInfo);
                    }
                }

                if(0 == fileInfo->NextEntryOffset)
                {
                    break;
                }

                fileInfo = (PFILE_ID_BOTH_DIR_INFO)((CHAR*)fileInfo + fileInfo->NextEntryOffset);
            }

            fileInfo = (PFILE_ID_BOTH_DIR_INFO)buf.get();
            (void)memset_s(buf.get(), UR_BUF_LEN, 0, UR_BUF_LEN);
        }
        while(GetFileInformationByHandleEx(hFile, FileIdBothDirectoryInfo, (LPVOID)buf.get(), UR_BUF_LEN));
    }

    result.splice(result.end(), files);
    ret = GetLastError();

    if(ERROR_NO_MORE_FILES == ret)
    {
        ret = RT_OK;
    }

    if(RT_OK != ret)
    {
        error(ret);
        HSLOG_ERROR(MODULE_NAME, ret, "list folder faild.");
        return ret;
    }

    return RT_OK;
}

int32_t LocalFolder::getProperty(FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
    if(path_.empty())
    {
        error(RT_INVALID_PARAM);
        return RT_INVALID_PARAM;
    }

    FILE_DIR_INFO fileDirInfo = LocalFile::getPropertyByPath(path_);
    property.type = fileDirInfo.type;
    property_.type = property.type;

    if(PROPERTY_ID & mask)
    {
        property.id = fileDirInfo.id;
        property_.id = property.id;
    }

    if(PROPERTY_CREATE & mask)
    {
        property.ctime = fileDirInfo.ctime;
        property_.ctime = property.ctime;
    }

    if(PROPERTY_MODIFY & mask)
    {
        property.mtime = fileDirInfo.mtime;
        property_.mtime = property.mtime;
    }

    return RT_OK;
}

int32_t LocalFolder::setProperty(const FILE_DIR_INFO& property, const PROPERTY_MASK& mask)
{
    return RT_NOT_IMPLEMENT;
}

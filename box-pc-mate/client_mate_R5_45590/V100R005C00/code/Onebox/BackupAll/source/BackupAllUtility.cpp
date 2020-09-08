#include "BackupAllUtility.h"
#include "SmartHandle.h"
#include "SysConfigureMgr.h"
#include "Utility.h"
#include <time.h>

#ifndef MODULE_NAME
#define MODULE_NAME ("BackupAllUtility")
#endif

namespace BackupAll
{
	typedef ULONG (__stdcall *PNtCreateFile)(
		PHANDLE FileHandle,
		ULONG DesiredAccess,
		PVOID ObjectAttributes,
		PVOID IoStatusBlock,
		PLARGE_INTEGER AllocationSize,
		ULONG FileAttributes,
		ULONG ShareAccess,
		ULONG CreateDisposition,
		ULONG CreateOptions,
		PVOID EaBuffer,
		ULONG EaLength );

	typedef struct _UNICODE_STRING {
		USHORT Length, MaximumLength;
		PWCH Buffer;
	} UNICODE_STRING, *PUNICODE_STRING;

	typedef struct _OBJECT_ATTRIBUTES {
		ULONG Length;
		HANDLE RootDirectory;
		PUNICODE_STRING ObjectName;
		ULONG Attributes;
		PVOID SecurityDescriptor;
		PVOID SecurityQualityOfService;
	} OBJECT_ATTRIBUTES;

	const ULONG OBJ_CASE_INSENSITIVE = 0x00000040UL;
	const ULONG FILE_OPEN_BY_FILE_ID = 0x00002000UL;
	const ULONG FILE_OPEN            = 0x00000001UL;

	typedef struct _IO_STATUS_BLOCK {
		union {
			NTSTATUS Status;
			PVOID Pointer;
		};
		ULONG_PTR Information;
	} IO_STATUS_BLOCK, *PIO_STATUS_BLOCK;

	typedef enum _FILE_INFORMATION_CLASS {
		// бнбн
		FileNameInformation = 9
		// бнбн
	} FILE_INFORMATION_CLASS, *PFILE_INFORMATION_CLASS;

	typedef NTSTATUS (__stdcall *PNtQueryInformationFile)(
		HANDLE FileHandle,
		PIO_STATUS_BLOCK IoStatusBlock,
		PVOID FileInformation,
		DWORD Length,
		FILE_INFORMATION_CLASS FileInformationClass );

	typedef struct _OBJECT_NAME_INFORMATION {
		UNICODE_STRING Name;
	} OBJECT_NAME_INFORMATION, *POBJECT_NAME_INFORMATION;

	int64_t li64toll(const LARGE_INTEGER& li64)
	{
		int64_t ll_ = 0L;
		ll_ = li64.HighPart;
		ll_ = (ll_<<32)+li64.LowPart;
		return ll_;
	}

	void pasreNextRunTime(int32_t backupType, const std::wstring& period, int64_t& nextRunTime)
	{
		if(BAT_REAL_TIME==backupType)
		{
			nextRunTime = time(NULL) + 600;
			return;
		}

		size_t pos = period.find(L"_");
		std::wstring tmpDayStr = period.substr(0,pos);
		int32_t tmpDay = SD::Utility::String::string_to_type<int32_t>(tmpDayStr.empty()?L"0":tmpDayStr);
		int32_t tmpHour = SD::Utility::String::string_to_type<int32_t>(period.substr(pos+1,2));
		int32_t tmpMinute = SD::Utility::String::string_to_type<int32_t>(period.substr(pos+4,2));

		time_t curTime = time(NULL);
		tm curTm;
		localtime_s(&curTm, &curTime);

		curTm.tm_hour = tmpHour;
		curTm.tm_min = tmpMinute;
		curTm.tm_sec = 0;

		if(BAT_PER_WEEK == backupType)
		{
			curTm.tm_mday += tmpDay - curTm.tm_wday;
		}
		else if(BAT_PER_MONTH == backupType)
		{
			curTm.tm_mday = tmpDay;
			int32_t tempMon = curTm.tm_mon;
			mktime(&curTm);
			if(tempMon!=curTm.tm_mon)
			{
				curTm.tm_mday = 0;
			}
		}
		nextRunTime = (int64_t)mktime(&curTm);

		getNextRunTime(backupType, nextRunTime, tmpDay);
	}

	void getNextRunTime(int32_t backupType, int64_t& nextRuntime, int32_t day)
	{
		if(time(NULL) < nextRuntime)
		{
			return;
		}

		while (time(NULL) > nextRuntime)
		{
			if(BAT_REAL_TIME==backupType)
			{
				nextRuntime += 600;
				return;
			}
			if(BAT_PER_DAY == backupType)
			{
				nextRuntime += 24*3600;
			}
			else if(BAT_PER_WEEK == backupType)
			{
				nextRuntime += 7*24*3600;
			}
			else if(BAT_PER_MONTH == backupType)
			{
				tm curTm;
				localtime_s(&curTm, &nextRuntime);
				++curTm.tm_mon;
				mktime(&curTm);
				int32_t tempMon = curTm.tm_mon;

				curTm.tm_mday = day;
				mktime(&curTm);
				if(tempMon!=curTm.tm_mon)
				{
					curTm.tm_mday = 0;
				}
				nextRuntime = (int64_t)mktime(&curTm);
			}
		}
	}

	std::string getInStr(const std::list<int64_t>& idList)
	{
		if(idList.empty())
		{
			return "";
		}

		std::stringstream inStr;
		std::list<int64_t>::const_iterator it = idList.begin();

		inStr<<*it;
		++it;

		for(; it != idList.end(); ++it)
		{
			inStr<<",";
			inStr<<*it;
		}
		return inStr.str();
	}

	std::string getInStr(const std::set<int64_t>& idList)
	{
		if(idList.empty())
		{
			return "";
		}

		std::stringstream inStr;
		std::set<int64_t>::const_iterator it = idList.begin();

		inStr<<*it;
		++it;

		for(; it != idList.end(); ++it)
		{
			inStr<<",";
			inStr<<*it;
		}
		return inStr.str();	
	}

	int64_t getIdByPath(UserContext* userContext, const std::wstring& path)
	{
		if(path.length()<3)
		{
			return VOLUME_ROOTID;
		}

		SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+path).c_str(), 
			GENERIC_READ, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			NULL, 
			OPEN_EXISTING, 
			FILE_ATTRIBUTE_NORMAL|FILE_FLAG_BACKUP_SEMANTICS, 
			NULL);
		if (INVALID_HANDLE_VALUE == hFile)
		{
			HSLOG_ERROR(MODULE_NAME, GetLastError(), "get id failed, open handle failed. path:%s", SD::Utility::String::wstring_to_string(path).c_str());
			return -1;
		}

		BY_HANDLE_FILE_INFORMATION bhfi;
		(void)memset_s(&bhfi, sizeof(BY_HANDLE_FILE_INFORMATION), 0, sizeof(BY_HANDLE_FILE_INFORMATION));
		if(GetFileInformationByHandle(hFile, &bhfi))
		{
			if(userContext->getSysConfigureMgr()->isBackupDisableAttr(bhfi.dwFileAttributes))
			{
				HSLOG_ERROR(MODULE_NAME, RT_ERROR, "isBackupDisableAttr. path:%s", SD::Utility::String::wstring_to_string(path).c_str());
				return -1;
			}
			int64_t curId = bhfi.nFileIndexHigh;
			curId = (curId<<32)+bhfi.nFileIndexLow;
			return curId;
		}
		HSLOG_ERROR(MODULE_NAME, GetLastError(), "GetFileInformationByHandle failed. path:%s", SD::Utility::String::wstring_to_string(path).c_str());
		return -1;
	}

	std::wstring getFullPathByFileId(const std::wstring& disk, DWORDLONG FileReferenceNumber)
	{
		TCHAR path[MAX_PATH] = {0};
		_stprintf_s(path, TEXT("\\\\.\\%c:"), disk[0]);

		SmartHandle volumeHandle = CreateFile(path, 
			GENERIC_READ, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			NULL, 
			OPEN_ALWAYS, 
			0, 
			NULL);
		if (INVALID_HANDLE_VALUE == volumeHandle)
		{
			int32_t ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "open volume failed.");
			return L"";
		}

		std::wstring filePath;
		PNtCreateFile NtCreatefile = (PNtCreateFile)GetProcAddress( GetModuleHandle(L"ntdll.dll"), "NtCreateFile" );
		UNICODE_STRING fidstr = { 8, 8, (PWSTR)&FileReferenceNumber };
		OBJECT_ATTRIBUTES oa = { sizeof(OBJECT_ATTRIBUTES), volumeHandle, &fidstr, OBJ_CASE_INSENSITIVE, 0, 0 };
		HANDLE hFile;
		ULONG iosb[2];
		ULONG status = NtCreatefile( &hFile, GENERIC_ALL, &oa, iosb, NULL, FILE_ATTRIBUTE_NORMAL, FILE_SHARE_READ|FILE_SHARE_WRITE, FILE_OPEN, FILE_OPEN_BY_FILE_ID, NULL, 0 );
		if( status == 0 )
		{
			PNtQueryInformationFile NtQueryInformationFile = (PNtQueryInformationFile)GetProcAddress( GetModuleHandle(L"ntdll.dll"), "NtQueryInformationFile" );
			IO_STATUS_BLOCK IoStatus;
			size_t allocSize = sizeof(OBJECT_NAME_INFORMATION) + MAX_PATH*sizeof(WCHAR);
			POBJECT_NAME_INFORMATION pfni = (POBJECT_NAME_INFORMATION)operator new(allocSize);
			status = NtQueryInformationFile(hFile, &IoStatus, pfni, allocSize, FileNameInformation);
			if( status == 0 )
			{
				filePath.append((WCHAR*)((char*)&pfni->Name.Buffer), pfni->Name.Length/sizeof(WCHAR));
			}
			operator delete(pfni);

			CloseHandle(hFile);
		}
		if(!filePath.empty())
		{
			filePath = disk.substr(0,1) + L":" + filePath;
		}
		return filePath;
	}
}
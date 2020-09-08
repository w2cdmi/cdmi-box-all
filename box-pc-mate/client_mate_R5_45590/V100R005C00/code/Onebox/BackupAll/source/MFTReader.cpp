#include "MFTReader.h"
#include <tchar.h>
#include "SmartHandle.h"
#include "Utility.h"
#include <boost/thread.hpp>
#include "SysConfigureMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("MFTReader")
#endif

#define UR_BUF_LEN 256*USN_PAGE_SIZE  //1024*1024

class MFTReaderImpl : public MFTReader
{
public:
	MFTReaderImpl(UserContext* userContext, const std::wstring& root)
		:userContext_(userContext)
		,init_(false)
		,root_(root)
		,volumeHandle_(INVALID_HANDLE_VALUE)
	{
		if(RT_OK==init())
		{
			init_ = true;
		}
	}

	virtual ~MFTReaderImpl()
	{
	}

	virtual int32_t getNextUsn(USN& nextUsn)
	{
		if(!init_)
		{
			return RT_ERROR;
		}
		nextUsn = ujd_.NextUsn;
		return RT_OK;
	}

	virtual int32_t readUsnJournal(callback_type callback, const USN& lastUsn)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("readUsnJournal lastUsn:%I64d, nextUsn:%I64d", lastUsn, ujd_.NextUsn));
		if(!init_)
		{
			return RT_ERROR;
		}

		//ChangeJournal
		return readChangeJournal(callback, lastUsn);

		//MFT
		//return readMFT(callback, lastUsn);
	}

private:
	virtual int32_t init()
	{
		int32_t ret = openVolume(root_[0], volumeHandle_);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "open volume handle failed.");
			return ret;
		}

		NTFS_VOLUME_DATA_BUFFER ntfsVolData;  
		DWORD dwWritten = 0;  
		if(!DeviceIoControl(volumeHandle_, FSCTL_GET_NTFS_VOLUME_DATA, NULL, 0, &ntfsVolData, sizeof(ntfsVolData), &dwWritten, NULL))
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "read change journal failed.");
			return ret;
		}
		LARGE_INTEGER num;  
		num.QuadPart = 1024;  
		LONGLONG total_file_count = (ntfsVolData.MftValidDataLength.QuadPart/num.QuadPart);  

		DWORD dwBytes;
		memset(&ujd_, 0, sizeof(USN_JOURNAL_DATA));
		while(!DeviceIoControl(volumeHandle_, FSCTL_QUERY_USN_JOURNAL, NULL,0,&ujd_, sizeof(ujd_),&dwBytes,NULL))
		{
			if(ERROR_JOURNAL_NOT_ACTIVE==ret)
			{
				ret = GetLastError();
				HSLOG_ERROR(MODULE_NAME, ret, "query change journal failed.");
				return ret;
			}
			ret = GetLastError();
			switch (ret)
			{
				case ERROR_JOURNAL_DELETE_IN_PROGRESS:
					HSLOG_EVENT(MODULE_NAME, ret, "change journal is deleting.");
					// wait for deleted and recreate the change journal
					boost::this_thread::sleep(boost::posix_time::seconds(1));
					break;
				case ERROR_JOURNAL_NOT_ACTIVE:
					HSLOG_EVENT(MODULE_NAME, ret, "change journal is not active.");
					// recreate the change journal
					createChangeJournal();
					break;
				default:
					break;
			}
		}
		
		return RT_OK;
	}

	int32_t openVolume(TCHAR volume, SmartHandle& handle)
	{
		int32_t ret = RT_OK;

		TCHAR path[MAX_PATH] = {0};
		_stprintf_s(path, TEXT("\\\\.\\%c:"), volume);

		handle = CreateFile(path, 
			GENERIC_READ, 
			FILE_SHARE_READ|FILE_SHARE_WRITE, 
			NULL, 
			OPEN_ALWAYS, 
			0, 
			NULL);
		if (INVALID_HANDLE_VALUE == handle)
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "open volume failed.");
			return ret;
		}

		return RT_OK;
	}

	int32_t readChangeJournal(callback_type callback, const USN& lastUsn)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "readChangeJournal");
		int32_t ret = RT_OK;

		DWORD cb;
		DWORD reasonMask = 0;//USN_REASON_CLOSE;
		reasonMask |= USN_REASON_DATA_EXTEND|USN_REASON_DATA_OVERWRITE|USN_REASON_DATA_TRUNCATION|USN_REASON_EA_CHANGE;
		reasonMask |= USN_REASON_FILE_CREATE|USN_REASON_FILE_DELETE|USN_REASON_RENAME_NEW_NAME|USN_REASON_BASIC_INFO_CHANGE;
		READ_USN_JOURNAL_DATA rujd;
		memset(&rujd, 0, sizeof(rujd));
		rujd.StartUsn = lastUsn;
		rujd.ReasonMask = reasonMask;
		rujd.UsnJournalID = ujd_.UsnJournalID;
		//rujd.BytesToWaitFor = sizeof(USN);
		rujd.BytesToWaitFor = 0;
		rujd.MaxMajorVersion = ujd_.MaxSupportedMajorVersion;
		rujd.MinMajorVersion = 2;

		USN_RECORD_V2 *pUsnRecordV2 = NULL;
		USN_RECORD_V3 *pUsnRecordV3 = NULL;
		std::auto_ptr<BYTE> buf = std::auto_ptr<BYTE>(new BYTE[UR_BUF_LEN]);
		BATaskBaseNodeList localNodeList;
		std::set<int64_t> createDirs;
		USN curUsn = lastUsn;
		while (curUsn < ujd_.NextUsn)
		{
			(void)memset_s(buf.get(), UR_BUF_LEN, 0, UR_BUF_LEN);

			if(!DeviceIoControl(volumeHandle_, FSCTL_READ_USN_JOURNAL, &rujd, sizeof(rujd), buf.get(), UR_BUF_LEN, &cb, NULL))
			{
				ret = GetLastError();
				HSLOG_ERROR(MODULE_NAME, ret, "read change journal failed.");
				return ret;
			}

			if (cb <= sizeof(USN))
			{
				break;
			}

			pUsnRecordV2 = (PUSN_RECORD_V2) &buf.get()[sizeof(USN)];
			while (0 != pUsnRecordV2->RecordLength && (PBYTE)pUsnRecordV2 < (buf.get()+cb))
			{
				switch (pUsnRecordV2->MajorVersion)
				{
				case 2:
					if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV2->SourceInfo)
					{
						usnRecordToSyncNode(pUsnRecordV2, localNodeList, createDirs);
					}
					pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV2 + pUsnRecordV2->RecordLength);
					break;
				case 3:
					pUsnRecordV3 = (PUSN_RECORD_V3)pUsnRecordV2;
					if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV3->SourceInfo)
					{
						usnRecordToSyncNode(pUsnRecordV3, localNodeList, createDirs);
					}
					pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV2 + pUsnRecordV2->RecordLength);
					break;
				case 4:
					// the USN record v4 may followed by another v4 record
					// the last USN record v4 always followed by a record v3 which contains the information we needed
					pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV2 + pUsnRecordV2->RecordLength);
					break;
				default:
					HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "unsupport usn record");
					break;
				}
			}
			
			// update the USN to read journal next time
			rujd.StartUsn = *(USN*)buf.get();
			curUsn = rujd.StartUsn;
		}

		if(!localNodeList.empty())
		{
			HSLOG_TRACE(MODULE_NAME, ret, "callback size: %d", localNodeList.size());
			ret = callback(localNodeList, createDirs);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "read usn journal sync callback failed");
				return ret;
			}
		}
		return RT_OK;
	}
	/*
	int32_t readMFT(callback_type callback, const USN& lastUsn)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "readMFT");
		int32_t ret = RT_OK;

		DWORD cb;
		MFT_ENUM_DATA med;
		med.StartFileReferenceNumber = 0;  
		med.LowUsn = lastUsn;
		med.HighUsn = ujd_.NextUsn;
		  
		USN_RECORD_V2 *pUsnRecordV2 = NULL;
		USN_RECORD_V3 *pUsnRecordV3 = NULL;
		std::auto_ptr<BYTE> buf = std::auto_ptr<BYTE>(new BYTE[UR_BUF_LEN]);
		BATaskBaseNodeList localNodeList;
		while (true) 
		{
			(void)memset_s(buf.get(), UR_BUF_LEN, 0, UR_BUF_LEN);
			if(!DeviceIoControl(volumeHandle_, FSCTL_ENUM_USN_DATA, &med, sizeof(med), buf.get(), UR_BUF_LEN, &cb, NULL))
			{
				ret = GetLastError();
				if(38!=ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "read change journal failed.");
					return ret;
				}
				if(!localNodeList.empty())
				{
					HSLOG_ERROR(MODULE_NAME, ret, "callback size£º%d", localNodeList.size());
					ret = callback(localNodeList);
					if (RT_OK != ret)
					{
						HSLOG_ERROR(MODULE_NAME, ret, "read usn journal sync callback failed");
						return ret;
					}
				}
				return RT_OK;
			}
			pUsnRecordV2 = (PUSN_RECORD_V2) &buf.get()[sizeof(USN)];
			while (0 != pUsnRecordV2->RecordLength && (PBYTE)pUsnRecordV2 < (buf.get()+cb))
			{
				switch (pUsnRecordV2->MajorVersion)
				{
				case 2:
					{
						if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV2->SourceInfo)
						{
							usnRecordToSyncNode(pUsnRecordV2, localNodeList);
						}
					}
					pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV2 + pUsnRecordV2->RecordLength);
					break;
				case 3:
					pUsnRecordV3 = (PUSN_RECORD_V3)pUsnRecordV2;
					{
						if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV3->SourceInfo)
						{
							usnRecordToSyncNode(pUsnRecordV3, localNodeList);
						}
					}
					pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV3 + pUsnRecordV3->RecordLength);
					break;
				default:
					HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "unsupport usn record");
					break;
				}
			}
			med.StartFileReferenceNumber=*(USN*)buf.get();
		}

		return RT_OK;
	}
	*/

	void usnRecordToSyncNode(PUSN_RECORD_V2 pUsnRecord, BATaskBaseNodeList& localNodeList, std::set<int64_t>& createDirs)
	{
		BATaskBaseNode node;
		node.path.append((wchar_t*)(((char*)pUsnRecord)+pUsnRecord->FileNameOffset), pUsnRecord->FileNameLength/sizeof(wchar_t));
		node.type = (pUsnRecord->FileAttributes&FILE_ATTRIBUTE_DIRECTORY)?FILE_TYPE_DIR:FILE_TYPE_FILE;
		node.localId = pUsnRecord->FileReferenceNumber;

		if((USN_REASON_FILE_DELETE&pUsnRecord->Reason)
			||userContext_->getSysConfigureMgr()->isBackupDisableAttr(pUsnRecord->FileAttributes))
		{
			// if the node is delete, set parent invalid id
			node.localParent = -1;
		}
		else
		{
			node.localParent = pUsnRecord->ParentFileReferenceNumber;
		}
		localNodeList.push_back(node);

		if(FILE_TYPE_DIR==node.type && USN_REASON_FILE_CREATE&pUsnRecord->Reason)
		{
			createDirs.insert(node.localId);
		}
	}

	void usnRecordToSyncNode(PUSN_RECORD_V3 pUsnRecord, BATaskBaseNodeList& localNodeList, std::set<int64_t>& createDirs)
	{
		BATaskBaseNode node;
		node.path.append((wchar_t*)(((char*)pUsnRecord)+pUsnRecord->FileNameOffset), pUsnRecord->FileNameLength/sizeof(wchar_t));
		node.type = (pUsnRecord->FileAttributes&FILE_ATTRIBUTE_DIRECTORY)?FILE_TYPE_DIR:FILE_TYPE_FILE;
		// will lost the hight 8 digest
		node.localId = *(int64_t*)&pUsnRecord->FileReferenceNumber.Identifier[0];
		if((USN_REASON_FILE_DELETE&pUsnRecord->Reason)
			||userContext_->getSysConfigureMgr()->isBackupDisableAttr(pUsnRecord->FileAttributes))
		{
			// if the node is delete, set parent invalid id
			node.localParent = -INVALID_ID;
		}
		else
		{
			node.localParent = *(int64_t*)&pUsnRecord->ParentFileReferenceNumber.Identifier[0];
		}
		localNodeList.push_back(node);

		if(FILE_TYPE_DIR==node.type && USN_REASON_FILE_CREATE&pUsnRecord->Reason)
		{
			createDirs.insert(node.localId);
		}
	}

	int32_t createChangeJournal()
	{
		int32_t ret = RT_OK;
		CREATE_USN_JOURNAL_DATA cujd;
		cujd.MaximumSize = 0x800000; // 8MB
		cujd.AllocationDelta = 0x100000; // 1MB, one-eight to MaximumSize
		DWORD cb;
		if(!DeviceIoControl(volumeHandle_, 
			FSCTL_CREATE_USN_JOURNAL, 
			&cujd, 
			sizeof(CREATE_USN_JOURNAL_DATA), 
			NULL, 
			0, 
			&cb, 
			NULL))
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "create change journal failed");
		}
		return ret;
	}

private:
	UserContext* userContext_;

	bool init_;
	std::wstring root_;
	SmartHandle volumeHandle_;
	USN_JOURNAL_DATA ujd_;
};

std::auto_ptr<MFTReader> MFTReader::create(UserContext* userContext, const std::wstring& root)
{
	return std::auto_ptr<MFTReader>(new MFTReaderImpl(userContext, root));
}

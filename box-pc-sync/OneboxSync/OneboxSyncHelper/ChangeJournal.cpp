#include "ChangeJournal.h"
#include <tchar.h>
#include <windows.h>
#include "SmartHandle.h"
#include "Utility.h"
#include <boost/thread.hpp>

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("ChangeJournal")
#endif

#define MAX_NODE_COUNT 10000
#define UR_BUF_LEN (1024*1024)
#define CHECK_STOP_COND() if (isStop_) {ret = RT_CANCEL; break;}

class ChangeJournalImpl : public ChangeJournal
{
public:
	ChangeJournalImpl(const std::wstring& root, bool async);

	virtual ~ChangeJournalImpl(void);

	virtual int32_t start();

	virtual int32_t stop();

	virtual int32_t enumUsnJournal(callback_type callback, const std::wstring& path=L"");

	virtual int32_t readUsnJournal(callback_type callback, const USN usn, bool async = true);

	virtual USN getUsn();

	virtual int64_t getRootId();

private:
	int32_t readUsnJournalAsyncThread(callback_type callback);

	int32_t readUsnJournalAsync(const USN usn);

	int32_t readUsnJournalSync(callback_type callback, const USN usn);

	int32_t getSyncNodesByPath(callback_type callback, const std::wstring& path, LocalSyncNodes& nodes);

	int32_t getSyncNodesByPathRecursive(callback_type callback, const std::wstring& path, LocalSyncNodes& nodes);

private:
	int32_t init();

	int32_t release();

	int32_t getChangeJournalInfo();

	int32_t openVolume(TCHAR volume, SmartHandle& handle, bool async = true);

	int32_t createChangeJournal();

	void usnRecordToSyncNode(PUSN_RECORD_V2 pUsnRecord, LocalSyncNode& node);

	void usnRecordToSyncNode(PUSN_RECORD_V3 pUsnRecord, LocalSyncNode& node);

	void fileDirInfoToSyncNode(PFILE_ID_BOTH_DIR_INFO pFileDirInfo, LocalSyncNode& node);

	int64_t li64toll(const LARGE_INTEGER& li64);

private:
	bool init_;
	std::wstring root_;
	SmartHandle volumeHandle_;
	USN_JOURNAL_DATA ujd_;
	USN usn_;
	int64_t rootId_;
	SmartHandle completePort_;
	OVERLAPPED overlapped_;
	std::auto_ptr<BYTE> buf_;
	bool isStop_;
	LocalSyncNodes syncNodesCache1_; // first level cache of read data
	LocalSyncNodes syncNodesCache2_; // second level cache of read data
	boost::mutex mutex_;
};

 std::auto_ptr<ChangeJournal> ChangeJournal::create(const std::wstring& root, bool async)
{
	return std::auto_ptr<ChangeJournal>(static_cast<ChangeJournal*>(new ChangeJournalImpl(root, async)));
}

 ChangeJournalImpl::ChangeJournalImpl(const std::wstring& root, bool async)
	 :init_(false)
	 ,root_(root)
	 ,volumeHandle_(INVALID_HANDLE_VALUE)
	 ,usn_(0L)
	 ,rootId_(INVALID_ID)
	 ,completePort_(NULL)
	 ,buf_(NULL)
	 ,isStop_(true)
 {
	 try
	 {
		 ISSP_LogInit("./log4cpp.conf", TP_FILE, "./OneboxSyncHelper.log");

		 memset(&ujd_, 0, sizeof(USN_JOURNAL_DATA));
		 memset(&overlapped_, 0, sizeof(OVERLAPPED));

		 if (RT_OK == init())
		 {
			 init_ = true;
		 }
	 }
	 catch(...){}
 }

 ChangeJournalImpl::~ChangeJournalImpl(void)
 {
	 try
	 {
		 (void)release();
		 init_ = false;

		 ISSP_LogExit();
	 }
	 catch(...){}
 }

 int32_t ChangeJournalImpl::init()
 {
	 int32_t ret = openVolume(root_[0], volumeHandle_);
	 if (RT_OK != ret)
	 {
		 HSLOG_ERROR(MODULE_NAME, ret, "open volume handle failed.");
		 return ret;
	 }

	 buf_ = std::auto_ptr<BYTE>(new BYTE[UR_BUF_LEN]);
	 assert(NULL != buf_.get());

	 overlapped_.hEvent = CreateEvent(NULL, FALSE, FALSE, NULL);
	 if (NULL == overlapped_.hEvent)
	 {
		 ret = GetLastError();
		 HSLOG_ERROR(MODULE_NAME, ret, "CreateEvent of OVERLAPPED failed.");
		 return ret;
	 }

	 return RT_OK;
 }

 int32_t ChangeJournalImpl::release()
 {
	 (void)stop();

	 return RT_OK;
 }

 int32_t ChangeJournalImpl::openVolume(TCHAR volume, SmartHandle& handle, bool async)
 {
	 int32_t ret = RT_OK;

	 TCHAR path[MAX_PATH] = {0};
	 _stprintf_s(path, TEXT("\\\\.\\%c:"), volume);

	 handle = CreateFile(path, 
		 GENERIC_READ, 
		 FILE_SHARE_READ|FILE_SHARE_WRITE, 
		 NULL, 
		 OPEN_ALWAYS, 
		 async?FILE_FLAG_OVERLAPPED:0, 
		 NULL);
	 if (INVALID_HANDLE_VALUE == handle)
	 {
		 ret = GetLastError();
		 HSLOG_ERROR(MODULE_NAME, ret, "open volume failed.");
		 return ret;
	 }

	 return RT_OK;
 }

 int32_t ChangeJournalImpl::getChangeJournalInfo()
 {
	 int32_t ret = ERROR_SUCCESS;
	 DWORD cb;
	 if(!DeviceIoControl(volumeHandle_, 
		 FSCTL_QUERY_USN_JOURNAL, 
		 NULL, 
		 0, 
		 &ujd_, 
		 sizeof(USN_JOURNAL_DATA), 
		 &cb, 
		 NULL))
	 {
		 ret = GetLastError();
		 switch (ret)
		 {
		 case ERROR_JOURNAL_DELETE_IN_PROGRESS:
			 HSLOG_EVENT(MODULE_NAME, ret, "change journal is deleting.");
			 // wait for deleted and recreate the change journal
			 while (true)
			 {
				 boost::this_thread::sleep(boost::posix_time::seconds(1));
				 if(!DeviceIoControl(volumeHandle_, 
					 FSCTL_QUERY_USN_JOURNAL, 
					 NULL, 
					 0, 
					 &ujd_, 
					 sizeof(USN_JOURNAL_DATA), 
					 &cb, 
					 NULL))
				 {
					 ret = GetLastError();
					 if (ERROR_JOURNAL_NOT_ACTIVE == ret)
					 {
						 HSLOG_EVENT(MODULE_NAME, RT_OK, "the change journal has disabled");
						 // recreate the change journal
						 CHECK_RESULT(createChangeJournal());
						 // re-get the change journal information
						 if(!DeviceIoControl(volumeHandle_, 
							 FSCTL_QUERY_USN_JOURNAL, 
							 NULL, 
							 0, 
							 &ujd_, 
							 sizeof(USN_JOURNAL_DATA), 
							 &cb, 
							 NULL))
						 {
							 ret = GetLastError();
							 HSLOG_EVENT(MODULE_NAME, RT_OK, "get change journal information failed");
							 return ret;
						 }
						 ret = RT_OK;
						 break;
					 }
				 }
				 else
				 {
					 HSLOG_EVENT(MODULE_NAME, RT_OK, "the change journal has actived by another application");
					 ret = RT_OK;
					 break;
				 }
			 }
		 	break;
		 case ERROR_JOURNAL_NOT_ACTIVE:
			 HSLOG_EVENT(MODULE_NAME, ret, "change journal is not active.");
			 // recreate the change journal
			 CHECK_RESULT(createChangeJournal());
			 // re-get the change journal information
			 if(!DeviceIoControl(volumeHandle_, 
				 FSCTL_QUERY_USN_JOURNAL, 
				 NULL, 
				 0, 
				 &ujd_, 
				 sizeof(USN_JOURNAL_DATA), 
				 &cb, 
				 NULL))
			 {
				 ret = GetLastError();
				 HSLOG_EVENT(MODULE_NAME, RT_OK, "get change journal information failed");
				 return ret;
			 }
			 break;
		 default:
			 break;
		 }
	 }

	 if (RT_OK != ret)
	 {
		 HSLOG_ERROR(MODULE_NAME, ret, "query change journal failed.");
	 }
	 else
	 {
		 std::stringstream stream;
		 stream << "-------------Basic Information-------------" << std::endl;
		 stream << "USN Journal ID           : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.UsnJournalID << std::endl;
		 stream << "First USN                : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.FirstUsn << std::endl;
		 stream << "Next USN                 : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.NextUsn << std::endl;
		 stream << "Lowest USN               : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.LowestValidUsn << std::endl;
		 stream << "Max USN                  : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.MaxUsn << std::endl;
		 stream << "Max Size                 : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.MaximumSize << std::endl;
		 stream << "Allocate Size            : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.AllocationDelta << std::endl;
		 stream << "MinSupportedMajorVersion : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.MinSupportedMajorVersion << std::endl;
		 stream << "MaxSupportedMajorVersion : " << "0x" << std::hex << std::setfill('0') << std::setw(16) << ujd_.MaxSupportedMajorVersion << std::endl;
		 stream << "-------------Basic Information-------------" << std::endl;
		 HSLOG_EVENT(MODULE_NAME, RT_OK, "%s", stream.str().c_str());
	 }

	 return ret;
 }

 int32_t ChangeJournalImpl::createChangeJournal()
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
		 return ret;
	 }
	 return RT_OK;
 }

void ChangeJournalImpl::usnRecordToSyncNode(PUSN_RECORD_V2 pUsnRecord, LocalSyncNode& node)
{
	node->name.append((wchar_t*)(((char*)pUsnRecord)+pUsnRecord->FileNameOffset), pUsnRecord->FileNameLength/sizeof(wchar_t));
	node->attributes = pUsnRecord->FileAttributes;
	node->oper = pUsnRecord->Reason;
	node->id = pUsnRecord->FileReferenceNumber;
	node->parent = pUsnRecord->ParentFileReferenceNumber;
	node->usn = pUsnRecord->Usn;
}

void ChangeJournalImpl::usnRecordToSyncNode(PUSN_RECORD_V3 pUsnRecord, LocalSyncNode& node)
{
	node->name.append((wchar_t*)(((char*)pUsnRecord)+pUsnRecord->FileNameOffset), pUsnRecord->FileNameLength/sizeof(wchar_t));
	node->attributes = pUsnRecord->FileAttributes;
	node->oper = pUsnRecord->Reason;
	// will lost the hight 8 digest
	node->id = *(int64_t*)&pUsnRecord->FileReferenceNumber.Identifier[0];
	node->parent = *(int64_t*)&pUsnRecord->ParentFileReferenceNumber.Identifier[0];
	node->usn = pUsnRecord->Usn;
}

void ChangeJournalImpl::fileDirInfoToSyncNode(PFILE_ID_BOTH_DIR_INFO pFileDirInfo, LocalSyncNode& node)
{
	node->id = li64toll(pFileDirInfo->FileId);
	node->attributes = pFileDirInfo->FileAttributes;
	node->name.append(pFileDirInfo->FileName, pFileDirInfo->FileNameLength/sizeof(wchar_t));
	node->ctime = li64toll(pFileDirInfo->CreationTime);
	node->mtime = li64toll(pFileDirInfo->LastWriteTime);
}

int64_t ChangeJournalImpl::li64toll(const LARGE_INTEGER& li64)
{
	int64_t ll_ = 0L;
	ll_ = li64.HighPart;
	ll_ = (ll_<<32)+li64.LowPart;
	return ll_;
}

USN ChangeJournalImpl::getUsn()
{
	return usn_;
}

int64_t ChangeJournalImpl::getRootId()
{
	if (INVALID_ID == rootId_)
	{
		return getIdByPath(root_);
	}
	return rootId_;
}

int64_t ChangeJournal::getIdByPath(const std::wstring& path)
{
	int32_t ret = RT_OK;
	if (path.empty())
	{
		return ERROR_INVALID_PARAMETER;
	}

	int64_t id = INVALID_ID;
	DWORD attr = FILE_ATTRIBUTE_NORMAL;
	if (Utility::FS::is_directory(path))
	{
		attr |= FILE_FLAG_BACKUP_SEMANTICS;
	}
	SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+path).c_str(), 
		GENERIC_READ, 
		FILE_SHARE_READ, 
		NULL, 
		OPEN_EXISTING, 
		attr, 
		NULL);
	if (INVALID_HANDLE_VALUE == hFile)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get id failed, open handle failed.");
		return id;
	}

	BY_HANDLE_FILE_INFORMATION bhfi;
	memset(&bhfi, 0, sizeof(BY_HANDLE_FILE_INFORMATION));

	if (!GetFileInformationByHandle(hFile, &bhfi))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, 
			"GetFileInformationByHandle failed.");
		return id;
	}

	id = bhfi.nFileIndexHigh;
	id = (id<<32)+bhfi.nFileIndexLow;

	return id;
}

int32_t ChangeJournalImpl::readUsnJournalAsyncThread(callback_type callback)
{
	try
	{
		int32_t ret = RT_OK;
		while (!isStop_)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(200));
			bool isIdle = true;
			{
				boost::mutex::scoped_lock lock(mutex_);
				if (!syncNodesCache1_.empty())
				{
					// move data in first level cahce into second level cache
					syncNodesCache2_.splice(syncNodesCache2_.end(), syncNodesCache1_);
					isIdle = false;
				}
			}

			if (isIdle && !syncNodesCache2_.empty())
			{
				ret = callback(syncNodesCache2_);
				if (RT_OK != ret)
				{
					HSLOG_ERROR(MODULE_NAME, ret, "read usn journal async thread callback failed");
				}
			}
		}
	}
	catch(boost::thread_interrupted) {}

	return RT_CANCEL;
}

int32_t ChangeJournalImpl::readUsnJournalAsync(const USN usn)
{
	int32_t ret = RT_OK;
	LocalSyncNodes nodes;
	DWORD cb;
	READ_USN_JOURNAL_DATA rujd;
	USN_RECORD_V2 *pUsnRecordV2 = NULL;
	USN_RECORD_V3 *pUsnRecordV3 = NULL;

	DWORD reasonMask = 0;//USN_REASON_CLOSE;
	reasonMask |= USN_REASON_DATA_EXTEND|USN_REASON_DATA_OVERWRITE|USN_REASON_DATA_TRUNCATION|USN_REASON_EA_CHANGE;
	reasonMask |= USN_REASON_FILE_CREATE|USN_REASON_FILE_DELETE|USN_REASON_RENAME_NEW_NAME|USN_REASON_RENAME_OLD_NAME;
	//reasonMask |= USN_REASON_OBJECT_ID_CHANGE;

	memset(&rujd, 0, sizeof(rujd));
	rujd.StartUsn = usn;
	rujd.ReasonMask = reasonMask;
	rujd.UsnJournalID = ujd_.UsnJournalID;
	//rujd.ReturnOnlyOnClose = TRUE;
	rujd.BytesToWaitFor = sizeof(USN);
	rujd.MaxMajorVersion = ujd_.MaxSupportedMajorVersion;
	rujd.MinMajorVersion = /*ujd_.MinSupportedMajorVersion*/0;

	memset(buf_.get(), 0, UR_BUF_LEN);
	if(!DeviceIoControl(volumeHandle_, 
		FSCTL_READ_USN_JOURNAL, 
		&rujd, 
		sizeof(READ_USN_JOURNAL_DATA), 
		buf_.get(), 
		UR_BUF_LEN, 
		NULL, 
		&overlapped_))
	{
		ret = GetLastError();
		if (ERROR_IO_PENDING != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "read change journal failed.");
			return ret;
		}
	}

	while (true)
	{
		if (!GetOverlappedResult(volumeHandle_, &overlapped_, &cb, TRUE))
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "GetOverlappedResult failed.");
			return ret;
		}

		CHECK_STOP_COND();

		// update the USN to read journal next time
		rujd.StartUsn = *(USN*)buf_.get();
		usn_ = rujd.StartUsn;

		pUsnRecordV2 = (PUSN_RECORD_V2) &buf_.get()[sizeof(USN)];

		while (0 != pUsnRecordV2->RecordLength && (PBYTE)pUsnRecordV2 < (buf_.get()+cb))
		{
			switch (pUsnRecordV2->MajorVersion)
			{
			case 2:
				if (pUsnRecordV2->Reason&USN_REASON_CLOSE || 
					pUsnRecordV2->Reason&USN_REASON_RENAME_OLD_NAME)
				{
					if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV2->SourceInfo)
					{
						LocalSyncNode node(new st_LocalSyncNode);
						usnRecordToSyncNode(pUsnRecordV2, node);
						// add read data to first level cache
						boost::mutex::scoped_lock lock(mutex_);
						syncNodesCache1_.push_back(node);
					}
				}
				pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV2 + pUsnRecordV2->RecordLength);
				break;
			case 3:
				pUsnRecordV3 = (PUSN_RECORD_V3)pUsnRecordV2;
				if (pUsnRecordV3->Reason&USN_REASON_CLOSE || 
					pUsnRecordV3->Reason&USN_REASON_RENAME_OLD_NAME)
				{
					if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV3->SourceInfo)
					{
						LocalSyncNode node(new st_LocalSyncNode);
						usnRecordToSyncNode(pUsnRecordV3, node);
						// add read data to first level cache
						boost::mutex::scoped_lock lock(mutex_);
						syncNodesCache1_.push_back(node);
					}
				}
				pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV3 + pUsnRecordV3->RecordLength);
				break;
			default:
				HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "unsupport usn record");
				break;
			}
		}

		memset(buf_.get(), 0, UR_BUF_LEN);
		if(!DeviceIoControl(volumeHandle_, 
			FSCTL_READ_USN_JOURNAL, 
			&rujd, 
			sizeof(READ_USN_JOURNAL_DATA), 
			buf_.get(), 
			UR_BUF_LEN, 
			NULL, 
			&overlapped_))
		{
			ret = GetLastError();
			if (ERROR_IO_PENDING != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "read change journal failed.");
				return ret;
			}
		}
	}

	return ret;
}

int32_t ChangeJournalImpl::readUsnJournalSync(callback_type callback, const USN usn)
{
	SmartHandle volumeHandle;
	int32_t ret = openVolume(root_[0], volumeHandle, false);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "open volume handle failed.");
		return ret;
	}

	LocalSyncNodes nodes;
	DWORD cb;
	READ_USN_JOURNAL_DATA rujd;
	USN_RECORD_V2 *pUsnRecordV2 = NULL;
	USN_RECORD_V3 *pUsnRecordV3 = NULL;

	DWORD reasonMask = 0;//USN_REASON_CLOSE;
	reasonMask |= USN_REASON_DATA_EXTEND|USN_REASON_DATA_OVERWRITE|USN_REASON_DATA_TRUNCATION|USN_REASON_EA_CHANGE;
	reasonMask |= USN_REASON_FILE_CREATE|USN_REASON_FILE_DELETE|USN_REASON_RENAME_NEW_NAME|USN_REASON_RENAME_OLD_NAME;
	//reasonMask |= USN_REASON_OBJECT_ID_CHANGE;

	memset(&rujd, 0, sizeof(rujd));
	rujd.StartUsn = usn;
	rujd.ReasonMask = reasonMask;
	rujd.UsnJournalID = ujd_.UsnJournalID;
	//rujd.ReturnOnlyOnClose = TRUE;
	rujd.BytesToWaitFor = sizeof(USN);
	rujd.MaxMajorVersion = ujd_.MaxSupportedMajorVersion;
	rujd.MinMajorVersion = /*ujd_.MinSupportedMajorVersion*/0;
	usn_ = usn;

	LocalSyncNodes syncNodes;

	while (true)
	{
		CHECK_STOP_COND();

		if (usn_ >= ujd_.NextUsn)
		{
			break;
		}

		memset(buf_.get(), 0, UR_BUF_LEN);
		if(!DeviceIoControl(volumeHandle, 
			FSCTL_READ_USN_JOURNAL, 
			&rujd, 
			sizeof(READ_USN_JOURNAL_DATA), 
			buf_.get(), 
			UR_BUF_LEN, 
			&cb, 
			NULL))
		{
			ret = GetLastError();
			HSLOG_ERROR(MODULE_NAME, ret, "read change journal failed.");
			return ret;
		}

		if (cb <= sizeof(USN))
		{
			break;
		}

		// update the USN to read journal next time
		rujd.StartUsn = *(USN*)buf_.get();
		usn_ = rujd.StartUsn;

		pUsnRecordV2 = (PUSN_RECORD_V2) &buf_.get()[sizeof(USN)];

		while (0 != pUsnRecordV2->RecordLength && (PBYTE)pUsnRecordV2 < (buf_.get()+cb))
		{
			switch (pUsnRecordV2->MajorVersion)
			{
			case 2:
				if (pUsnRecordV2->Reason&USN_REASON_CLOSE || 
					pUsnRecordV2->Reason&USN_REASON_RENAME_OLD_NAME)
				{
					if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV2->SourceInfo)
					{
						LocalSyncNode node(new st_LocalSyncNode);
						usnRecordToSyncNode(pUsnRecordV2, node);
						syncNodes.push_back(node);
						if (MAX_NODE_COUNT <= syncNodes.size())
						{
							ret = callback(syncNodes);
							if (RT_OK != ret)
							{
								HSLOG_ERROR(MODULE_NAME, ret, "read usn journal sync callback failed");
								return ret;
							}
						}
					}
				}
				pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV2 + pUsnRecordV2->RecordLength);
				break;
			case 3:
				pUsnRecordV3 = (PUSN_RECORD_V3)pUsnRecordV2;
				if (pUsnRecordV3->Reason&USN_REASON_CLOSE || 
					pUsnRecordV3->Reason&USN_REASON_RENAME_OLD_NAME)
				{
					if (USN_SOURCE_AUXILIARY_DATA != pUsnRecordV3->SourceInfo)
					{
						LocalSyncNode node(new st_LocalSyncNode);
						usnRecordToSyncNode(pUsnRecordV3, node);
						syncNodes.push_back(node);
						if (MAX_NODE_COUNT <= syncNodes.size())
						{
							ret = callback(syncNodes);
							if (RT_OK != ret)
							{
								HSLOG_ERROR(MODULE_NAME, ret, "read usn journal sync callback failed");
								return ret;
							}
						}
					}
				}
				pUsnRecordV2 = (PUSN_RECORD_V2)((PBYTE)pUsnRecordV3 + pUsnRecordV3->RecordLength);
				break;
			default:
				HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "unsupport usn record");
				break;
			}
		}
	}

	if (RT_OK == ret)
	{
		ret = callback(syncNodes);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "read usn journal sync callback failed");
		}
	}

	return ret;
}

int32_t ChangeJournalImpl::getSyncNodesByPath(callback_type callback, const std::wstring& path, LocalSyncNodes& nodes)
{
	int32_t ret = RT_OK;
	if (path.empty())
	{
		return ERROR_INVALID_PARAMETER;
	}

	SmartHandle hFile = CreateFile(std::wstring(L"\\\\?\\"+path).c_str(), 
		GENERIC_READ, 
		FILE_SHARE_READ, 
		NULL, 
		OPEN_ALWAYS, 
		FILE_ATTRIBUTE_NORMAL|FILE_FLAG_BACKUP_SEMANTICS, 
		NULL);
	if (INVALID_HANDLE_VALUE == hFile)
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get child syncNodes of %s faild, open handle failed.", 
			Utility::String::wstring_to_string(path).c_str());
		return ret;
	}

	std::auto_ptr<char> buf(new char[UR_BUF_LEN]);
	memset(buf.get(), 0, UR_BUF_LEN);
	FILE_ID_BOTH_DIR_INFO *fileInfo = (PFILE_ID_BOTH_DIR_INFO)buf.get();

	if (!GetFileInformationByHandleEx(hFile, FileIdBothDirectoryInfo, (LPVOID)buf.get(), UR_BUF_LEN))
	{
		ret = GetLastError();
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get child syncNodes information of %s faild, get parent information failed.", 
			Utility::String::wstring_to_string(path).c_str());
		return ret;
	}

	int64_t parentId = li64toll(fileInfo->FileId);

	fileInfo = (PFILE_ID_BOTH_DIR_INFO)((CHAR*)fileInfo+fileInfo->NextEntryOffset);

	do
	{
		CHECK_STOP_COND();

		while(true)
		{
			CHECK_STOP_COND();

			LocalSyncNode syncNode(new st_LocalSyncNode(parentId));
			fileDirInfoToSyncNode(fileInfo, syncNode);
			if (L"." != syncNode->name && L".." != syncNode->name)
			{
				syncNode->path = path + PATH_DELIMITER + syncNode->name;
				nodes.push_back(syncNode);
			}
			if (0 == fileInfo->NextEntryOffset)
			{
				break;
			}
			fileInfo = (PFILE_ID_BOTH_DIR_INFO)((CHAR*)fileInfo+fileInfo->NextEntryOffset);
		}

		fileInfo = (PFILE_ID_BOTH_DIR_INFO)buf.get();
		memset(buf.get(), 0, UR_BUF_LEN);
	}while(GetFileInformationByHandleEx(hFile, FileIdBothDirectoryInfo, (LPVOID)buf.get(), UR_BUF_LEN));

	ret = GetLastError();
	ret = (ERROR_NO_MORE_FILES==ret?ERROR_SUCCESS:ret);
	if (ERROR_SUCCESS != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, 
			"get child syncNodes information of %s faild, get parent information failed.", 
			Utility::String::wstring_to_string(path).c_str());
	}

	return ret;
}

int32_t ChangeJournalImpl::getSyncNodesByPathRecursive(callback_type callback, const std::wstring& path, LocalSyncNodes& nodes)
{
	int32_t ret = RT_OK;
	LocalSyncNodes syncNodes;
	ret = getSyncNodesByPath(callback, path, syncNodes);
	if (RT_OK != ret)
	{
		return ret;
	}
	for (LocalSyncNodes::iterator it = syncNodes.begin(); it != syncNodes.end(); ++it)
	{
		CHECK_STOP_COND();

		LocalSyncNode node = *it;
		nodes.push_back(node);

		if (MAX_NODE_COUNT <= nodes.size())
		{
			ret = callback(nodes);
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME,  ret, "enum usn journal callback failed");;
				return ret;
			}
		}

		if (node->attributes&FILE_ATTRIBUTE_DIRECTORY)
		{
			ret = getSyncNodesByPathRecursive(callback, path+PATH_DELIMITER+node->name, nodes);
			if (RT_OK != ret)
			{
				return ret;
			}
		}
	}
	return ret;
}

int32_t ChangeJournalImpl::start()
{
	if (!init_)
	{
		return RT_INVALID_PARAM;
	}

	if (!isStop_)
	{
		return RT_OK;
	}

	int32_t ret = getChangeJournalInfo();
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get volume basic information failed.");
		return ret;
	}

	isStop_ = false;

	return RT_OK;
}

int32_t ChangeJournalImpl::stop()
{
	if (isStop_)
	{
		return RT_OK;
	}

	isStop_ = true;

	SetEvent(overlapped_.hEvent);	

	return RT_OK;
}

int32_t ChangeJournalImpl::enumUsnJournal(callback_type callback, const std::wstring& path)
{
	LocalSyncNodes syncNodes;
	std::wstring rootPath = root_;
	if (path.empty())
	{
		// add root node
		LocalSyncNode rootNode(new st_LocalSyncNode);
		rootNode->id = getRootId();
		if (INVALID_ID == rootNode->id)
		{
			return RT_INVALID_PARAM;
		}
		// root node's name is the full monitor path
		// root node's parent is 0
		rootNode->parent = 0;
		rootNode->name = root_;
		rootNode->attributes = FILE_ATTRIBUTE_DIRECTORY;
		syncNodes.push_back(rootNode);
	}
	else
	{
		rootPath = path;
	}	
	
	int32_t ret = getSyncNodesByPathRecursive(callback, rootPath, syncNodes);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "get sync node by path recursive failed");
		return ret;
	}
	if (!syncNodes.empty())
	{
		ret = callback(syncNodes);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME,  ret, "enum usn journal callback failed");;
			return ret;
		}
	}
	// if the path is empty, means this is not the first time to enum usn
	// do not update the usn_
	if (path.empty())
	{
		usn_ = ujd_.NextUsn;
	}
	return ret;
}

int32_t ChangeJournalImpl::readUsnJournal(callback_type callback, const USN usn, bool async)
{
	if (0 > usn)
	{
		return RT_INVALID_PARAM;
	}

	HSLOG_EVENT(MODULE_NAME, RT_OK, "read usn journal, usn is %I64d", usn);

	int32_t ret = RT_OK;

	if (async)
	{
		syncNodesCache1_.clear();
		syncNodesCache2_.clear();

		boost::thread th = boost::thread(&ChangeJournalImpl::readUsnJournalAsyncThread, this, callback);

		ret = readUsnJournalAsync(usn);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "read usn journal async failed");
		}

		th.interrupt();
		th.join();

		return ret;
	}

	ret = readUsnJournalSync(callback, usn);
	if (RT_OK != ret)
	{
		HSLOG_ERROR(MODULE_NAME, ret, "read usn journal sync failed");
	}
	return ret;
}

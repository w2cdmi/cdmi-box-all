#ifndef _ONEBOX_UPLOAD_TABLE_H_
#define _ONEBOX_UPLOAD_TABLE_H_

#include "SyncCommon.h"
#include "CppSQLite3.h"

class UploadTable
{
public:
	virtual ~UploadTable(){}

	static std::auto_ptr<UploadTable> create(const std::wstring& parent, const long& offset);

	virtual int32_t replaceFilterInfo(const int64_t& local_id) = 0;

	virtual bool isFilter(const int64_t& local_id) = 0;

	virtual int32_t getAllUploadInfo(IdList& idList) = 0;

	virtual int32_t deleteUplaodInfo(const IdList & idList) = 0;

};

#endif
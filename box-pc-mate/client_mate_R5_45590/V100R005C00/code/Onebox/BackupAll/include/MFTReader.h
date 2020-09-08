#ifndef _ONEBOX_BACKUPALL_MFT_READER_H_
#define _ONEBOX_BACKUPALL_MFT_READER_H_

#include "BackupAllCommon.h"
#include <boost/function.hpp>
#include <set>

class MFTReader
{
public:
	typedef boost::function<int32_t(BATaskBaseNodeList&, std::set<int64_t>&)> callback_type;

	virtual ~MFTReader() {}

	static std::auto_ptr<MFTReader> create(UserContext* userContext, const std::wstring& root);

	//获取当前最大的USN
	virtual int32_t getNextUsn(USN& nextUsn) = 0;

	virtual int32_t readUsnJournal(callback_type callback, const USN& lastUsn) = 0;

};

#endif

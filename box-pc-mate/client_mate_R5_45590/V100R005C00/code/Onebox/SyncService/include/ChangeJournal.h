#ifndef _ONEBOX_CHANGE_JOURNAL_H_
#define _ONEBOX_CHANGE_JOURNAL_H_

#include "SyncCommon.h"
#include "OneboxExport.h"
#include <boost/function.hpp>

class ONEBOX_DLL_EXPORT ChangeJournal
{
public:
	typedef boost::function<int32_t(LocalSyncNodes&)> callback_type;

	virtual ~ChangeJournal() {}

	virtual int32_t start() = 0;

	virtual int32_t stop() = 0;

	virtual int32_t enumUsnJournal(callback_type callback, const std::wstring& path=L"") = 0;

	virtual int32_t readUsnJournal(callback_type callback, const USN usn, bool async = true) = 0;

	virtual USN getUsn() = 0;

	virtual int64_t getRootId() = 0;

	static int64_t getIdByPath(const std::wstring& path);

	static std::auto_ptr<ChangeJournal> create(const std::wstring& root, bool async = true);
};

#endif

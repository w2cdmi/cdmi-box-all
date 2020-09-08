#ifndef _ONEBOX_TRANS_DATA_TABLE_H_
#define _ONEBOX_TRANS_DATA_TABLE_H_

#include "AsyncTaskCommon.h"

class TransDataTable
{
public:
	TransDataTable(UserContext* userContext, const std::wstring& path);

	int32_t create(const AsyncTransCacheMode cacheMode = ATCM_NoCache);

	int32_t flushCache();

	int32_t getNode(const std::wstring& group, const std::wstring& source, AsyncTransDataNode& node);

	int32_t replaceNode(const AsyncTransDataNode& node);

	int32_t deleteNode(const std::wstring& group, const std::wstring& source);

	int32_t updateBlocks(const std::wstring& group, const std::wstring& source, const AsyncTransBlocks& blocks);

	int32_t updatemTime(const std::wstring& group, const std::wstring& source, const int64_t mtime);

	int32_t updateFingerprint(const std::wstring& group, const std::wstring& source, const Fingerprint& fingerprint);

	int32_t updateUserDefine(const std::wstring& group, const std::wstring& source, const std::wstring& userDefine);

	int32_t addNodes(const AsyncTransDataNodes& nodes);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
#ifndef _ONEBOX_ASYNCTASK_IADAPTER_H_
#define _ONEBOX_ASYNCTASK_IADAPTER_H_

#include "UserContext.h"
#include "AsyncTaskCommon.h"
#include <ISSPThreads_md.h>
#include "IFile.h"

class IAsyncTaskAdapter
{
public:
	virtual ~IAsyncTaskAdapter() {};

	virtual bool isSurpport(const AsyncTransTaskNode& node) = 0;

	virtual int32_t updateNode(CISSPTask* ptrTask, const FILE_DIR_INFO& fileNode) {return RT_NOT_IMPLEMENT;}

	virtual int32_t completeNode(CISSPTask* ptrTask) {return RT_NOT_IMPLEMENT;}

	virtual int32_t convertLocalId2Path(CISSPTask* ptrTask, const int64_t id, std::wstring& path) {return RT_NOT_IMPLEMENT;}

	virtual int32_t convertLocalId2RemoteId(CISSPTask* ptrTask, const int64_t localId, int64_t& remoteId) {return RT_NOT_IMPLEMENT;}

public:
	void setUserContext(UserContext* userContext) {userContext_ = userContext;}

protected:
	UserContext* userContext_;
};

#endif

#ifndef _RESTTASK_MGR_H_
#define _RESTTASK_MGR_H_

#include "UserContext.h"
#include <list>

#define RESTTASK_DELETE ("delete")
#define RESTTASK_COPY ("copy")
#define RESTTASK_SAVE ("save")		//转存接口调sdk时转为copy
#define RESTTASK_MOVE ("move")

class ONEBOX_DLL_EXPORT RestTaskMgr
{
public:
	virtual ~RestTaskMgr(){}

	static RestTaskMgr* create(UserContext* userContext);

	virtual int32_t addRestTask(int64_t srcOwnerId, int64_t srcParentId, const std::list<int64_t>& srcNodeId, 
		int64_t destOwnerId, int64_t destFolderId, 
		const std::string& type, bool autoRename = false) = 0;

	virtual std::string getLastType() = 0;
};

#endif

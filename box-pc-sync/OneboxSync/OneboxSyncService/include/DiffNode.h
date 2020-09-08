#ifndef _ONEBOX_DIFF_NODE_H_
#define _ONEBOX_DIFF_NODE_H_

#include "UserContext.h"
#include "SyncCommon.h"

class CDiffNode
{
public:
	virtual ~CDiffNode(){}

	virtual void init(const OperNode& operNode) = 0;

	virtual int64_t getLocalId() = 0;
	
	virtual int64_t getRemoteId() = 0;
	
	virtual OperNodes& getLocalList() = 0;
	
	virtual OperNodes& getRemoteList() = 0;

	virtual ExecuteActions& getActions() = 0;

	virtual SyncRuleKey getSyncRuleKey() = 0;

	virtual void refreshDiff(const DiffStatus status, int errorCode = 0) = 0;

	virtual void stepComplete(OperType operType) = 0;

	static std::shared_ptr<CDiffNode> create(UserContext* userContext);
};

typedef std::shared_ptr<CDiffNode> DiffNode;

#endif
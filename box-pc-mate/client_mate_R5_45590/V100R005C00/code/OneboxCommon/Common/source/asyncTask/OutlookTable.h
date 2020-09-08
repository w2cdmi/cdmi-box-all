#ifndef _ONEBOX_OUTLOOK_TABLE_H_
#define _ONEBOX_OUTLOOK_TABLE_H_

#include "CommonDefine.h"
#include <list>

// outlook table file name
#define TFN_OUTLOOK (L"outlook.db")

struct st_OutlookNode
{
	std::wstring emailId;
	std::wstring group;
	std::wstring localPath;
	std::wstring shareLink;
};
typedef std::shared_ptr<st_OutlookNode> OutlookNode;
typedef std::list<OutlookNode> OutlookNodes;

class OutlookTable
{
public:
	OutlookTable(const std::wstring& path);

	int32_t addNode(const OutlookNode& node);

	int32_t getNode(const std::wstring& group, OutlookNode& node);

	int32_t getNodes(const std::wstring& emailId, OutlookNodes& nodes);

	int32_t getUnShareLinkNodes(const std::wstring& emailId, OutlookNodes& nodes);

	int32_t getShareLinkedNodes(const std::wstring& emailId, OutlookNodes& nodes);

	int32_t updateShareLink(const std::wstring& group, const std::wstring& shareLink);

	int32_t deleteNode(const std::wstring& group);

	int32_t deleteNodes(const std::wstring& emailId);

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};

#endif
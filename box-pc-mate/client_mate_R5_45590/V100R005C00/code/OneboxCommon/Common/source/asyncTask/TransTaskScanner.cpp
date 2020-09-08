#include "TransTaskScanner.h"
#include <boost/thread.hpp>
#include "SyncFileSystemMgr.h"
#include "PathMgr.h"
#include "Utility.h"
#include "UserContextMgr.h"
#include "NotifyMgr.h"
#include "ConfigureMgr.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("TransTaskScanner")
#endif

#define MAX_SCAN_FAILED_COUNT (5)
#define SCAN_INTERVAL_LAZY (2000)
#define SCAN_INTERVAL (1)

class TransTaskScanner::Impl
{
public:
	Impl(UserContext* userContext, TransTableMgr* transTableMgr)
		:userContext_(userContext)
		,transTableMgr_(transTableMgr)
	{
		init();
	}

	~Impl()
	{

	}

	int32_t run()
	{
		interrupt();
		thread_ = boost::thread(boost::bind(&Impl::scan, this));
		return RT_OK;
	}

	int32_t interrupt()
	{
		try
		{
			thread_.interrupt();
			thread_.join();
		}
		catch(...) {}
		return RT_OK;
	}

private:
	void init()
	{
		std::wstring retryErrorCodes = userContext_->getConfigureMgr()->getConfigure()->retryTaskErrorCodes();
		std::vector<std::wstring> result;
		Utility::String::split(retryErrorCodes, result, L";");
		for (std::vector<std::wstring>::iterator it = result.begin(); 
			it != result.end(); ++it)
		{
			if (!it->empty())
			{
				errorCodes_.push_back(Utility::String::string_to_type<int32_t>(*it));
			}
		}
	}

	void scan()
	{
		try
		{
			bool lastNoTask = false;
			while (true)
			{
				SLEEP(boost::posix_time::milliseconds(lastNoTask?SCAN_INTERVAL_LAZY:SCAN_INTERVAL));
				//////////////////////////////////////////////////////////////////////////
				// 1. get root table
				// 2. get root node
				// 3. get detail table
				// 4. get detail node
				// 5. get child nodes
				// 6. add child nodes
				// 7. clear parent scanning statusEx
				// 8. update root size
				// 9. notify size change message to UI
				//////////////////////////////////////////////////////////////////////////
				// get root table
				TransRootTable* rootTable = transTableMgr_->getRootTable(ATT_Upload);
				if (NULL == rootTable)
				{
					lastNoTask = true;
					continue;
				}
				// get root node
				AsyncTransRootNode rootNode;
				int32_t ret = rootTable->getTopScanNode(rootNode);
				if (RT_OK != ret)
				{
					if(RT_SQLITE_NOEXIST != ret)
					{
						HSLOG_ERROR(MODULE_NAME, ret, "get top scan node from root table failed.");
					}
					lastNoTask = true;
					continue;
				}

				if (RT_OK != scanImpl(rootNode, rootTable))
				{
					lastNoTask = true;
					continue;
				}
				lastNoTask = false;
			}
		}
		catch(boost::thread_interrupted)
		{
			HSLOG_EVENT(MODULE_NAME, RT_CANCEL, "async trans task scanning interrupted.");
		}
	}

	int32_t scanImpl(AsyncTransRootNode& node, TransRootTable* rootTable)
	{
		if(NULL == node.get() || NULL == rootTable)
		{
			return RT_INVALID_PARAM;
		}
		TransDetailTablePtr detailTable = transTableMgr_->getDetailTable(node->group);
		if (NULL == detailTable.get())
		{
			HSLOG_ERROR(MODULE_NAME, RT_ERROR, 
				"failed to get detail table, root group id is %s.", 
				Utility::String::wstring_to_string(node->group).c_str());
			return RT_OK;
		}

		// get detail node
		AsyncTransDetailNode detailNode;
		int32_t ret = detailTable->getTopScanNode(detailNode);
		if (RT_SQLITE_NOEXIST == ret)
		{
			if (0 == detailTable->getScanNodesCount())
			{
				// clear the scanning statusEx in root node
				(void)rootTable->removeStatusEx(node->group, ATSEX_Scanning);
				return RT_OK;
			}
			return RT_SQLITE_NOEXIST;
		}
		else if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "get scan node from detail table failed.");
			return ret;
		}

		UserContextId userContextId;
		userContextId.id = detailNode->root->userId;
		userContextId.type = detailNode->root->userType;
		userContextId.name = detailNode->root->userName;
		UserContext *userContext = UserContextMgr::getInstance()->getUserContext(userContextId);
		if (NULL == userContext)
		{
			HSLOG_ERROR(MODULE_NAME, RT_MEMORY_MALLOC_ERROR, "get user conetext of %I64d failed.", userContextId.id);
			(void)detailTable->updateStatusAndErrorCode(detailNode->source, ATS_Error, RT_MEMORY_MALLOC_ERROR);
			return RT_OK;
		}
		AsyncTransDetailNodes nodes;
		ret = createChildTasks(userContext, detailNode, nodes);
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "create child tasks of %s failed.", 
				Utility::String::wstring_to_string(detailNode->source).c_str());
			(void)detailTable->updateStatusAndErrorCode(detailNode->source, ATS_Error, ret);
			return ret;
		}
		// add detail nodes
		ret = detailTable->addNodes(nodes);
		if ( RT_OK != ret )
		{
			HSLOG_ERROR(MODULE_NAME, ret, "failed to add child nodes of %s.", 
				Utility::String::wstring_to_string(detailNode->source).c_str());
			(void)detailTable->updateStatusAndErrorCode(detailNode->source, ATS_Error, ret);
			return RT_OK;
		}
		// clear parent's scanning statusEx (if failed, may cause repeated data, whatever...)
		(void)detailTable->removeStatusEx(detailNode->source, ATSEX_Scanning);
		// update root size
		(void)rootTable->updateSize(detailNode->root->group, detailNode->root->size);
		// notify size change message to UI
		(void)userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(
			NOTIFY_MSG_TRANS_TASK_UPDATE_SIZE, 
			detailNode->root->group, 
			Utility::String::type_to_string<std::wstring>(detailNode->root->type), 
			Utility::String::type_to_string<std::wstring>(detailNode->root->size)));

		return RT_OK;
	}

	int32_t createChildTasks(UserContext* userContext, AsyncTransDetailNode& parent, AsyncTransDetailNodes& nodes)
	{
		if (parent->fileType == FILE_TYPE_FILE)
		{
			return RT_INVALID_PARAM;
		}
		ADAPTER_FILE_TYPE type;
		LIST_FOLDER_RESULT result;
		Path path = userContext->getPathMgr()->makePath();
		if (parent->root->type == ATT_Download)
		{
			path.id(Utility::String::string_to_type<int64_t>(parent->source));
			type = ADAPTER_FOLDER_TYPE_REST;
		}
		else
		{
			path.path(parent->source);
			type = ADAPTER_FOLDER_TYPE_LOCAL;
		}

		int32_t ret = userContext->getSyncFileSystemMgr()->listFolder(path, result, type);
		// the folder is not exist, do nothing
		if (RT_FILE_NOEXIST_ERROR == ret || HTTP_NOT_FOUND == ret)
		{
			HSLOG_EVENT(MODULE_NAME, RT_OK, "the folder of %s is not exist.", 
				Utility::String::wstring_to_string(parent->source).c_str());
			return RT_OK;
		}
		else if (RT_OK != ret)
		{
			RETRY(userContext_->getConfigureMgr()->getConfigure()->retryTaskTimes())
			{
				SLEEP(boost::posix_time::milliseconds(userContext_->getConfigureMgr()->getConfigure()->retryTaskInterval()));
				result.clear();
				ret = userContext->getSyncFileSystemMgr()->listFolder(path, result, type);
				if (RT_OK == ret)
				{
					break;
				}
				else if (RT_FILE_NOEXIST_ERROR == ret || HTTP_NOT_FOUND == ret)
				{
					HSLOG_EVENT(MODULE_NAME, RT_OK, "the folder of %s is not exist.", 
						Utility::String::wstring_to_string(parent->source).c_str());
					return RT_OK;
				}
			}
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "list folder of %s failed when scanning.", 
					Utility::String::wstring_to_string(parent->source).c_str());
				return ret;
			}
		}

		bool isVirtualStatusSet = false;
		for (LIST_FOLDER_RESULT::iterator it = result.begin(); it != result.end(); ++it)
		{
			boost::this_thread::interruption_point();

			AsyncTransDetailNode node(new st_AsyncTransDetailNode);
			if (parent->root->type == ATT_Download)
			{
				node->source = Utility::String::type_to_string<std::wstring>(it->id);
				// the parent is not the real parent, use to update the real parent id
				// when parent node transmit success
				node->parent = Utility::String::type_to_string<std::wstring>(path.id());
			}
			else
			{
				node->source = path.path() + PATH_DELIMITER + it->name;
				// the parent is not the real parent, use to update the real parent id
				// when parent node transmit success
				node->parent = path.path();
			}
			node->name = it->name;
			node->fileType = FILE_TYPE(it->type);
			node->root = parent->root;
			node->status = ATS_Waiting;
			if (node->fileType == FILE_TYPE_FILE)
			{
				node->size = it->size;
				node->root->size += node->size;
				
				// Set first file to VirtualParent status for batch prepare upload
				if(!isVirtualStatusSet && it->size < BATCH_PREUPLOAD_FILE_SIZE && ATT_Upload == parent->root->type)
				{
					node->statusEx = AsyncTransStatusEx(ATSEX_VirtualParent|ATSEX_Uninitial);
					isVirtualStatusSet = true;
					SERVICE_INFO(MODULE_NAME, RT_OK, "Set batch preupload virtual parent: %s", 
						Utility::String::wstring_to_string(node->source).c_str())
				}
				else
				{
					node->statusEx = ATSEX_Uninitial;
				}
			}
			else
			{
				node->statusEx = AsyncTransStatusEx(ATSEX_Scanning|ATSEX_Uninitial);
			}
			nodes.push_back(node);
		}

		return RT_OK;
	}

private:
	UserContext* userContext_;
	TransTableMgr* transTableMgr_;
	boost::thread thread_;
	std::list<int32_t> errorCodes_;
};

TransTaskScanner::TransTaskScanner(UserContext* userContext, TransTableMgr* transTableMgr)
	:impl_(new Impl(userContext, transTableMgr))
{

}

int32_t TransTaskScanner::run()
{
	return impl_->run();
}

int32_t TransTaskScanner::interrupt()
{
	return impl_->interrupt();
}

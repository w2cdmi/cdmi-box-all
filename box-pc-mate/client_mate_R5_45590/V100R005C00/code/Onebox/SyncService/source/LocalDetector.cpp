#include "LocalDetector.h"
#include "ChangeJournal.h"
#include <WinIoCtl.h>
#include "ConfigureMgr.h"
#include "DataBaseMgr.h"
#include "InIHelper.h"
#include "FilterMgr.h"
#include <boost/bind.hpp>
#include <boost/thread.hpp>
#include "Utility.h"
#include "NotifyMgr.h"
#include "SyncConfigure.h"

using namespace SD;

#ifndef MODULE_NAME
#define MODULE_NAME ("LocalDetector")
#endif

//#define _TEST

class NotifyObject
{
public:
	explicit NotifyObject(UserContext* userContext)
		:userContext_(userContext)
	{
		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SCAN, L"begin"));
	}

	~NotifyObject() 
	{
		try
		{
			userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_SCAN, L"end"));
		}
		catch(...)
		{
		}
	}
private:
	UserContext* userContext_;
}; 

class LocalDetectorImpl : public LocalDetector
{
public:
	LocalDetectorImpl(UserContext* userContext)
		:userContext_(userContext)
		,changeJournal_(ChangeJournal::create(SyncConfigure::getInstance(userContext_)->monitorRootPath()))
		,iniHelper_(userContext_->getConfigureMgr()->getConfigure()->userDataPath()+SYNC_CONF_NAME)
	{		
	}

	virtual ~LocalDetectorImpl(void)
	{
	}

	virtual int32_t start()
	{
		// check if the monitor root is changed
		LocalNode rootNode(new st_LocalNode);
		int32_t ret = DataBaseMgr::getInstance(userContext_)->getLocalTable()->getRootNode(rootNode);
		if (RT_OK != ret && RT_SQLITE_NOEXIST != ret)
		{
			return ret;
		}
		else if (RT_OK == ret)
		{
			int64_t rootId = ChangeJournal::getIdByPath(rootNode->name);
			if (rootNode->id != rootId)
			{
				HSLOG_EVENT(MODULE_NAME, RT_OK, "monitor root has changed");
				return userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_ROOT_CHANGE));
			}
		}

		ret = changeJournal_->start();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "start change journal failed");
			return ret;
		}

		// when start, should do full detect for one time
		return fullDetect();
	}

	virtual int32_t stop()
	{
		int32_t ret = changeJournal_->stop();
		if (RT_OK != ret)
		{
			HSLOG_ERROR(MODULE_NAME, ret, "stop change journal failed");
		}
		return ret;
	}

	virtual int32_t fullDetect()
	{
		//add root relation
		int64_t remoteRoot = ROOT_PARENTID;
		if(!DataBaseMgr::getInstance(userContext_)->getRelationTable()->remoteIdIsExist(remoteRoot))
		{
			int32_t ret = RT_OK;
			int64_t localRoot = changeJournal_->getRootId();
			if (INVALID_ID == localRoot)
			{
				return RT_INVALID_PARAM;
			}
			CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getRelationTable()->addRelation(remoteRoot,localRoot));
		}

		return fullDetectImpl();
	}

	virtual int32_t incDetect()
	{
		// 1) when USN <= 0, do full detect
		// 2) when increase detect failed, do full detect then do increase detect 

		int32_t ret = RT_OK;

		USN usn = iniHelper_.GetInt64(L"", L"USN", -1);
		if (0 > usn || 0 >= DataBaseMgr::getInstance(userContext_)->getLocalTable()->getCount())
		{
			CHECK_RESULT(fullDetectImpl());
		}

		ret = incDetectImpl();
		if (RT_OK == ret || RT_CANCEL == ret)
		{
			return ret;
		}

		CHECK_RESULT(fullDetectImpl());

		return incDetectImpl();
	}

private:
	int32_t incDetectImpl()
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "LocalDetectorImpl::incDetectImpl");
		LocalSyncNodes syncNodes;
		USN usn = iniHelper_.GetInt64(L"", L"USN", -1);
		int32_t ret = changeJournal_->readUsnJournal(boost::bind(&LocalDetectorImpl::readUsnCallbackWithLock, this, _1), usn);
		if (RT_OK != ret)
		{
			return ret;
		}

		return RT_OK;
	}

	int32_t fullDetectImpl()
	{
		NotifyObject notifyObject(userContext_);

		SERVICE_FUNC_TRACE(MODULE_NAME, "LocalDetectorImpl::fullDetectImpl");

		boost::mutex::scoped_lock lock(GlobalMutex::localMutex());
		
		int32_t ret = RT_OK;

		// read usn journal(increase mode) first, if failed, do enum usn journal(full mode)
		// increase mode
		USN usn = iniHelper_.GetInt64(L"", L"USN", -1);
		ret = changeJournal_->readUsnJournal(boost::bind(&LocalDetectorImpl::readUsnCallback, this, _1), usn, false);
		if (RT_OK == ret)
		{
			iniHelper_.SetInt64(L"", L"USN", changeJournal_->getUsn());
			return RT_OK;
		}
		else
		{
			HSLOG_ERROR(MODULE_NAME, ret, "read usn journal with sync mode failed");
		}

		// full mode
		if (0 > usn || 
			0 >= DataBaseMgr::getInstance(userContext_)->getLocalTable()->getCount())
		{
			// if the usn is less than 0 or no data is in local table
			// just add all the nodes into the local table, 
			// before this, delete all the node in the local table and diff table
			if (0 > usn)
			{
				CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getLocalTable()->clearTable());
				CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getDiffTable()->clearDiff(Key_LocalID));
			}
			ret = changeJournal_->enumUsnJournal(boost::bind(&LocalDetectorImpl::enumUsnAddAllImpl, this, _1));
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "enum usn journal add all failed");
				return ret;
			}
			
			// update autoInc
			iniHelper_.SetValue<int64_t>(L"", CONF_AUTOINC_ID, DataBaseMgr::getInstance(userContext_)->getDiffTable()->getMaxInc());
		}
		else
		{
			// to get delete node, should do 3 steps
			// 1. initial mask status to MS_Missed
			// 2. update the node to MS_Marked if the node is exist in filesystem
			// 3. get the MS_Missed nodes which is deleted
			CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getLocalTable()->initMarkStatus());
			ret = changeJournal_->enumUsnJournal(boost::bind(&LocalDetectorImpl::enumUsnCompareAllImpl, this, _1));
			if (RT_OK != ret)
			{
				HSLOG_ERROR(MODULE_NAME, ret, "enum usn journal compare all failed");
				return ret;
			}
			// add delete diff
			IdList trashDeleteNodes;
			CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getLocalTable()->getNodesByMarkStatus(trashDeleteNodes, MS_Missed));
			OperNodes operNodes;
			for(IdList::const_iterator it = trashDeleteNodes.begin(); it != trashDeleteNodes.end(); ++it)
			{
				OperNode operNode(new st_OperNode);
				operNode->keyType = Key_LocalID;
				operNode->key = *it;
				operNode->oper = OT_Deleted;
				operNode->priority = PRIORITY_LEVEL3;
				operNodes.push_back(operNode);
			}
			CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getDiffTable()->addOperList(operNodes));
			CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getLocalTable()->trashDeleteNodes(trashDeleteNodes));
		}

		iniHelper_.SetInt64(L"", L"USN", changeJournal_->getUsn());

		return RT_OK;
	}

	int32_t readUsnCallbackWithLock(LocalSyncNodes& syncNodes)
	{
		boost::mutex::scoped_lock lock(GlobalMutex::localMutex());
		return readUsnCallback(syncNodes);
	}

	int32_t readUsnCallback(LocalSyncNodes& syncNodes)
	{
		if (syncNodes.empty())
		{
			return RT_OK;
		}
		int64_t usn = (*(syncNodes.rbegin()))->usn;
		int32_t ret = readUsnImpl(syncNodes);
		if (RT_OK == ret)
		{
			iniHelper_.SetInt64(L"", L"USN", usn);
		}
		return ret;
	}

	int32_t enumUsnAddAllImpl(LocalSyncNodes& syncNodes)
	{
		if (syncNodes.empty())
		{
			return RT_OK;
		}
		LocalTable* localTable = DataBaseMgr::getInstance(userContext_)->getLocalTable();
		assert(NULL != localTable);

		int32_t ret = RT_OK;
		OperNodes operNodes;
		DiffPathNodes diffPathNodes;
		int64_t rootId = changeJournal_->getRootId();
		std::wstring cachePath = SyncConfigure::getInstance(userContext_)->cachePath();
		if (INVALID_ID == rootId)
		{
			HSLOG_ERROR(MODULE_NAME, RT_INVALID_PARAM, "root id is invalid");
			return RT_INVALID_PARAM;
		}
		for(LocalSyncNodes::iterator it = syncNodes.begin(); it != syncNodes.end();)
		{
			boost::this_thread::interruption_point();

			//skip root node
			if (rootId == it->get()->id)
			{
				++it;
				continue;
			}
			// ignore nodes in cache path
			if (it->get()->path.length() >= cachePath.length() && 
				it->get()->path.substr(0, cachePath.length()) == cachePath)
			{
				it = syncNodes.erase(it);
				continue;
			}
			// ignore filter file
			if (rootId == it->get()->parent && it->get()->name == L"Desktop.ini")
			{
				it = syncNodes.erase(it);
				continue;
			}

			OperNode operNode(new st_OperNode());
			operNode->keyType = Key_LocalID;
			operNode->key = it->get()->id;
			operNode->oper = OT_Created;
			operNode->priority = PRIORITY_LEVEL5;
			if(!((*it)->attributes&FILE_ATTRIBUTE_DIRECTORY))
			{
				operNode->size = 0;
			}
			operNodes.push_back(operNode);

			// add path for diff proccessor
			DiffPathNode diffPathNode(new st_DiffPathNode);
			diffPathNode->key = it->get()->id;
			diffPathNode->keyType = Key_LocalID;
			diffPathNode->size = operNode->size;
			diffPathNode->localPath = it->get()->path;
			diffPathNodes.push_back(diffPathNode);

			++it;
		}

		CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getDiffTable()->addOperList(operNodes));
		CHECK_RESULT(localTable->replaceNodes(syncNodes));
		(void)DataBaseMgr::getInstance(userContext_)->getDiffTable()->insertIncPath(diffPathNodes);
		
		syncNodes.clear();

		return RT_OK;
	}

	int32_t enumUsnCompareAllImpl(LocalSyncNodes& syncNodes)
	{
		if (syncNodes.empty())
		{
			return RT_OK;
		}
		LocalTable* localTable = DataBaseMgr::getInstance(userContext_)->getLocalTable();
		assert(NULL != localTable);

		int32_t ret = RT_OK;
		LocalSyncNode syncNode;
		OperNodes operNodes;
		IdList existList;
		int64_t rootId = changeJournal_->getRootId();
		std::wstring cachePath = Utility::FS::get_file_name(SyncConfigure::getInstance(userContext_)->cachePath());

		LocalNodes localNodes;
		for (LocalSyncNodes::iterator it = syncNodes.begin(); it != syncNodes.end(); ++it)
		{
			boost::this_thread::interruption_point();

			syncNode = *it;
			assert(NULL != syncNode.get());

			//skip root node
			if (rootId == syncNode->id)
			{
				++it;
				existList.push_back(syncNode->id);
				continue;
			}
			// ignore nodes in cache path
			if (it->get()->path.length() >= cachePath.length() && 
				it->get()->path.substr(0, cachePath.length()) == cachePath)
			{
				continue;
			}
			// ignore filter file
			if (rootId == syncNode->parent && syncNode->name == L"Desktop.ini")
			{
				continue;
			}

			LocalNode localNode(new st_LocalNode);
			ret = localTable->getNode(syncNode->id, localNode);
			if(RT_OK == ret)
			{
				existList.push_back(syncNode->id);

				bool isChange = false;

				//add local rename
				if(syncNode->name!=localNode->name)
				{
					OperNode operNode(new st_OperNode());
					operNode->keyType = Key_LocalID;
					operNode->key = syncNode->id;
					operNode->oper = OT_Renamed;
					operNodes.push_back(operNode);

					localNode->name = syncNode->name;
					isChange = true;
				}
				//add local move
				if(syncNode->parent!=localNode->parent)
				{
					OperNode operNode(new st_OperNode());
					operNode->keyType = Key_LocalID;
					operNode->key = syncNode->id;
					operNode->oper = OT_Moved;
					operNodes.push_back(operNode);

					localNode->parent = syncNode->parent;
					isChange = true;
				}
				//add local edit
				if(syncNode->mtime!=localNode->mtime && (FILE_TYPE_FILE==localNode->type))
				{
					OperNode operNode(new st_OperNode());
					operNode->keyType = Key_LocalID;
					operNode->key = syncNode->id;
					operNode->oper = OT_Edited;
					operNode->size = 0;
					operNodes.push_back(operNode);

					//isChange = true;
				}
				//add local create
				if (LS_Delete_Status == localNode->status)
				{
					OperNode operNode(new st_OperNode());
					operNode->keyType = Key_LocalID;
					operNode->key = syncNode->id;
					operNode->oper = OT_Created;
					if(!(syncNode->attributes&FILE_ATTRIBUTE_DIRECTORY))
					{
						operNode->size = 0;
					}
					operNodes.push_back(operNode);

					localNode->status = LS_Normal;
					isChange = true;
				}

				if (isChange)
				{
					localNodes.push_back(localNode);
				}
			}
			else if(RT_SQLITE_NOEXIST == ret)
			{
				//add local create
				OperNode operNode(new st_OperNode());
				operNode->keyType = Key_LocalID;
				operNode->key = syncNode->id;
				operNode->oper = OT_Created;
				if(!(syncNode->attributes&FILE_ATTRIBUTE_DIRECTORY))
				{
					localNode->type = FILE_TYPE_FILE;
					operNode->size = 0;
				}
				operNodes.push_back(operNode);

				localNode->id = syncNode->id;
				localNode->parent = syncNode->parent;
				localNode->name = syncNode->name;
				localNodes.push_back(localNode);
			}
		}		

		// add diff
		CHECK_RESULT(DataBaseMgr::getInstance(userContext_)->getDiffTable()->addOperList(operNodes));
		// update the mark status to MS_Marked which node is exist in filesystem
		CHECK_RESULT(localTable->updateMarkStatus(existList, MS_Marked));
		// update local table
		CHECK_RESULT(localTable->replaceNodes(localNodes));

		syncNodes.clear();

		return RT_OK;
	}

#ifdef _TEST
	void printOper(const uint32_t oper)
	{
		HANDLE hOut = GetStdHandle(STD_OUTPUT_HANDLE);
		SetConsoleTextAttribute(hOut, FOREGROUND_GREEN|BACKGROUND_BLUE);
		if (USN_REASON_DATA_OVERWRITE&oper)
		{
			printf("     USN_REASON_DATA_OVERWRITE\n");
		}
		if (USN_REASON_DATA_EXTEND&oper)
		{
			printf("     USN_REASON_DATA_EXTEND\n");
		}
		if (USN_REASON_DATA_TRUNCATION&oper)
		{
			printf("     USN_REASON_DATA_TRUNCATION\n");
		}
		if (USN_REASON_NAMED_DATA_OVERWRITE&oper)
		{
			printf("     USN_REASON_NAMED_DATA_OVERWRITE\n");
		}
		if (USN_REASON_NAMED_DATA_EXTEND&oper)
		{
			printf("     USN_REASON_NAMED_DATA_EXTEND\n");
		}
		if (USN_REASON_NAMED_DATA_TRUNCATION&oper)
		{
			printf("     USN_REASON_NAMED_DATA_TRUNCATION\n");
		}
		if (USN_REASON_FILE_CREATE&oper)
		{
			printf("     USN_REASON_FILE_CREATE\n");
		}
		if (USN_REASON_FILE_DELETE&oper)
		{
			printf("     USN_REASON_FILE_DELETE\n");
		}
		if (USN_REASON_EA_CHANGE&oper)
		{
			printf("     USN_REASON_EA_CHANGE\n");
		}
		if (USN_REASON_SECURITY_CHANGE&oper)
		{
			printf("     USN_REASON_SECURITY_CHANGE\n");
		}
		if (USN_REASON_RENAME_OLD_NAME&oper)
		{
			printf("     USN_REASON_RENAME_OLD_NAME\n");
		}
		if (USN_REASON_RENAME_NEW_NAME&oper)
		{
			printf("     USN_REASON_RENAME_NEW_NAME\n");
		}
		if (USN_REASON_INDEXABLE_CHANGE&oper)
		{
			printf("     USN_REASON_INDEXABLE_CHANGE\n");
		}
		if (USN_REASON_BASIC_INFO_CHANGE&oper)
		{
			printf("     USN_REASON_BASIC_INFO_CHANGE\n");
		}
		if (USN_REASON_HARD_LINK_CHANGE&oper)
		{
			printf("     USN_REASON_HARD_LINK_CHANGE\n");
		}
		if (USN_REASON_COMPRESSION_CHANGE&oper)
		{
			printf("     USN_REASON_COMPRESSION_CHANGE\n");
		}
		if (USN_REASON_ENCRYPTION_CHANGE&oper)
		{
			printf("     USN_REASON_ENCRYPTION_CHANGE\n");
		}
		if (USN_REASON_OBJECT_ID_CHANGE&oper)
		{
			printf("     USN_REASON_OBJECT_ID_CHANGE\n");
		}
		if (USN_REASON_REPARSE_POINT_CHANGE&oper)
		{
			printf("     USN_REASON_REPARSE_POINT_CHANGE\n");
		}
		if (USN_REASON_STREAM_CHANGE&oper)
		{
			printf("     USN_REASON_STREAM_CHANGE\n");
		}
		if (USN_REASON_TRANSACTED_CHANGE&oper)
		{
			printf("     USN_REASON_TRANSACTED_CHANGE\n");
		}
		if (USN_REASON_INTEGRITY_CHANGE&oper)
		{
			printf("     USN_REASON_INTEGRITY_CHANGE\n");
		}
		if (USN_REASON_CLOSE&oper)
		{
			printf("     USN_REASON_CLOSE\n");
		}
		SetConsoleTextAttribute(hOut, FOREGROUND_GREEN|FOREGROUND_BLUE|FOREGROUND_RED|BACKGROUND_BLUE);
	}
#endif

	struct ReadUsnImplParam
	{
		LocalNodes updateNodes;
		LocalNodes addNodes;
		IdList trashDeleteNodes;
		IdList deleteNodes;
		bool isInMonitor;
		bool isParentInMonitor;
		OperType lastOper;
		FILE_TYPE type;
		LocalTable* localTable;
		DiffTable* diffTable;

		ReadUsnImplParam()
			:isInMonitor(false)
			,isParentInMonitor(false)
			,lastOper(OT_Invalid)
			,type(FILE_TYPE_DIR)
			,localTable(NULL)
			,diffTable(NULL)
		{
			updateNodes.clear();
			addNodes.clear();
			trashDeleteNodes.clear();
			deleteNodes.clear();
		}
	};

	bool isMonitorNode(const int64_t& id)
	{
		return DataBaseMgr::getInstance(userContext_)->getLocalTable()->isExist(id);
	}

	int32_t readUsnCreateImpl(ReadUsnImplParam& param, LocalSyncNode& syncNode, OperNodes& operNodes)
	{
		int32_t ret = RT_OK;

		// ignore file not in monitor directory
		if (!param.isParentInMonitor)
		{
			return RT_OK;
		}

		// if the oper is not the same as last oper, dump the cache to database
		if (OT_Created != param.lastOper)
		{
			CHECK_RESULT(param.diffTable->addOperList(operNodes));
			operNodes.clear();

			CHECK_RESULT(param.localTable->getChildren(param.trashDeleteNodes));
			CHECK_RESULT(param.localTable->trashDeleteNodes(param.trashDeleteNodes));
			param.trashDeleteNodes.clear();

			CHECK_RESULT(param.localTable->deleteNodes(param.deleteNodes));
			param.deleteNodes.clear();

			CHECK_RESULT(param.localTable->updateNodes(param.updateNodes));
			param.updateNodes.clear();

			CHECK_RESULT(param.localTable->replaceNodes(param.addNodes));
			param.addNodes.clear();

			// after dump cache data, should re-init the param of isInMonitor
			param.isInMonitor = param.localTable->isExist(syncNode->id);
		}
		param.lastOper = OT_Created;

		// ignore repeat oper
		if (param.isInMonitor)
		{
			return RT_OK;
		}

		// create
#ifdef _TEST
		printf("create [%s]\n", Utility::String::wstring_to_string(
			param.localTable->getPath(syncNode->parent)+PATH_DELIMITER+syncNode->name).c_str());
#endif					
		OperNode operNode(new st_OperNode);
		operNode->keyType = Key_LocalID;
		operNode->key = syncNode->id;
		operNode->oper = OT_Created;
		if(FILE_TYPE_DIR != param.type)
		{
			operNode->size = 0;
		}

		LocalNode localNode(new st_LocalNode(syncNode->id, syncNode->parent, syncNode->name, param.type));
		// directory create should add to database  immediately
		if (FILE_TYPE_DIR == param.type)
		{
			CHECK_RESULT(param.diffTable->addOper(operNode));
			CHECK_RESULT(param.localTable->replaceNode(localNode));
		}
		else
		{
			operNodes.push_back(operNode);
			param.addNodes.push_back(localNode);
		}
		
		return RT_OK;
	}

	int32_t readUsnDeleteImpl(ReadUsnImplParam& param, LocalSyncNode& syncNode, OperNodes& operNodes)
	{
		int32_t ret = RT_OK;

		// ignore file not in monitor directory
		if (!param.isParentInMonitor)
		{
			return RT_OK;
		}

		// if the oper is not the same as last oper, dump the cache to database
		if (OT_Deleted != param.lastOper)
		{
			CHECK_RESULT(param.diffTable->addOperList(operNodes));
			operNodes.clear();

			CHECK_RESULT(param.localTable->replaceNodes(param.addNodes));
			param.addNodes.clear();

			CHECK_RESULT(param.localTable->updateNodes(param.updateNodes));
			param.updateNodes.clear();

			CHECK_RESULT(param.localTable->getChildren(param.trashDeleteNodes));
			CHECK_RESULT(param.localTable->trashDeleteNodes(param.trashDeleteNodes));
			param.trashDeleteNodes.clear();

			CHECK_RESULT(param.localTable->deleteNodes(param.deleteNodes));
			param.deleteNodes.clear();

			// after dump cache data, should re-init the param of isInMonitor
			param.isInMonitor = param.localTable->isExist(syncNode->id);
		}
		param.lastOper = OT_Deleted;

		// ignore repeat oper
		if (!param.isInMonitor)
		{
			return RT_OK;
		}

		// delete
#ifdef _TEST
		printf("delete [%s]\n", Utility::String::wstring_to_string(
			param.localTable->getPath(syncNode->id)).c_str());
#endif					
		OperNode operNode(new st_OperNode);
		operNode->keyType = Key_LocalID;
		operNode->key = syncNode->id;
		operNode->oper = OT_Deleted;
		operNode->priority = PRIORITY_LEVEL3;
		operNodes.push_back(operNode);

		if (FILE_TYPE_DIR == param.type)
		{
			param.trashDeleteNodes.push_back(syncNode->id);
		}
		else
		{
			// file should delete imediately, 
			// this id may re-used by the next edit temporary file in the case of office edit
			param.deleteNodes.push_back(syncNode->id);
		}

		return RT_OK;
	}

	int32_t readUsnRenameImpl(ReadUsnImplParam& param, LocalSyncNode& syncNode, LocalSyncNode& newNode, OperNodes& operNodes)
	{
		int32_t ret = RT_OK;

		bool isMove = (syncNode->parent!=newNode->parent);
		bool isRename = (syncNode->name!=newNode->name);
		// ignore rename not in monitor directory
		if (!isMove && !param.isParentInMonitor)
		{
			return RT_OK;
		}
		bool isNewParentInMonitor = isMonitorNode(newNode->parent);
		// ignore move not in monitor directory
		if (isMove && !param.isParentInMonitor && !isNewParentInMonitor)
		{
			return RT_OK;
		}

		LocalNode localNode(new st_LocalNode);

		// move
		if (isMove && param.isParentInMonitor && isNewParentInMonitor)
		{
			// if the oper is not the same as last oper, dump the cache to database
			if (OT_Moved != param.lastOper)
			{
				CHECK_RESULT(param.diffTable->addOperList(operNodes));
				operNodes.clear();
				CHECK_RESULT(param.localTable->replaceNodes(param.addNodes));
				param.addNodes.clear();
			}
			param.lastOper = OT_Moved;

			// ignore repeat oper
			(void)param.localTable->getNode(newNode->id, localNode);
			if (localNode->parent == newNode->parent && 
				localNode->name == newNode->name)
			{
				return RT_OK;
			}
			// update the parent
			localNode->parent = newNode->parent;

#ifdef _TEST
			printf("move [%s] to [%s]\n", 
				Utility::String::wstring_to_string(param.localTable->getPath(syncNode->id)).c_str(), 
				Utility::String::wstring_to_string(param.localTable->getPath(newNode->parent)+PATH_DELIMITER+newNode->name).c_str());
#endif
			OperNode operNode(new st_OperNode);
			operNode->keyType = Key_LocalID;
			operNode->key = syncNode->id;
			operNode->oper = OT_Moved;

			CHECK_RESULT(param.diffTable->addOper(operNode));
			CHECK_RESULT(param.localTable->updateNode(localNode));

			//return RT_OK;
		}
		// rename
		if ((!isMove || isRename) && param.isParentInMonitor && isNewParentInMonitor)
		{
			// if the oper is not the same as last oper, dump the cache to database
			if (OT_Renamed != param.lastOper)
			{
				CHECK_RESULT(param.diffTable->addOperList(operNodes));
				operNodes.clear();
				CHECK_RESULT(param.localTable->replaceNodes(param.addNodes));
				param.addNodes.clear();
			}
			param.lastOper = OT_Renamed;

			// ignore repeat oper
			if (INVALID_ID == localNode->id)
			{
				(void)param.localTable->getNode(newNode->id, localNode);
			}		
			if (localNode->parent == newNode->parent && 
				localNode->name == newNode->name)
			{
				return RT_OK;
			}
			// update the name
			localNode->name = newNode->name;

#ifdef _TEST
			printf("rename [%s] to [%s]\n", 
				Utility::String::wstring_to_string(param.localTable->getPath(syncNode->parent)+PATH_DELIMITER+syncNode->name).c_str(), 
				Utility::String::wstring_to_string(param.localTable->getPath(newNode->parent)+PATH_DELIMITER+newNode->name).c_str());
#endif						
			OperNode operNode(new st_OperNode);
			operNode->keyType = Key_LocalID;
			operNode->key = newNode->id;
			operNode->oper = OT_Renamed;

			CHECK_RESULT(param.diffTable->addOper(operNode));
			CHECK_RESULT(param.localTable->updateNode(localNode));

			return RT_OK;
		}
		// create
		if (isMove && !param.isParentInMonitor && isNewParentInMonitor)
		{
			param.isInMonitor = param.localTable->isExist(newNode->id);
			param.isParentInMonitor = true;
			CHECK_RESULT(readUsnCreateImpl(param, newNode, operNodes));
			// if the type is directory, should add it's child nodes
			if (FILE_TYPE_DIR == param.type)
			{
				// change the last oper type, make the cache data dump to the database
				param.lastOper = OperType(OT_Created+OT_Moved);
				CHECK_RESULT(readUsnCreateImpl(param, newNode, operNodes));
				return changeJournal_->enumUsnJournal(boost::bind(&LocalDetectorImpl::enumUsnAddAllImpl, this, _1), 
					param.localTable->getPath(newNode->id));
			}

			return RT_OK;
		}
		// delete
		if (isMove && param.isParentInMonitor && !isNewParentInMonitor)
		{
			param.isInMonitor = param.localTable->isExist(syncNode->id);
			param.isParentInMonitor = true;
			return readUsnDeleteImpl(param, syncNode, operNodes);
		}

		return RT_OK;
	}

	int32_t readUsnEditImpl(ReadUsnImplParam& param, LocalSyncNode& syncNode, OperNodes& operNodes)
	{
		int32_t ret = RT_OK;

		// ignore file not in monitor directory
		if (!param.isParentInMonitor)
		{
			return RT_OK;
		}

		// now can not ignore repeat oper of edit
		// ...

		//edit
#ifdef _TEST
		printf("edit file [%s]\n", Utility::String::wstring_to_string(
			param.localTable->getPath(syncNode->parent)+PATH_DELIMITER+syncNode->name).c_str());
#endif					
		OperNode operNode(new st_OperNode);
		operNode->keyType = Key_LocalID;
		operNode->key = syncNode->id;
		operNode->oper = OT_Edited;
		operNode->size = 0;
		operNodes.push_back(operNode);

		//LocalNode localNode(new st_LocalNode(syncNode->id, syncNode->parent, syncNode->name, param.type));
		//param.updateNodes.push_back(localNode);

		param.lastOper = OT_Edited;
		
		return RT_OK;
	}

	int32_t readUsnImpl(LocalSyncNodes& syncNodes)
	{
		if (syncNodes.empty())
		{
			return 0;
		}

		int32_t ret = RT_OK;
		ReadUsnImplParam param;
		LocalSyncNode syncNode;
		OperNodes operNodes;
		param.localTable = DataBaseMgr::getInstance(userContext_)->getLocalTable();
		assert(NULL != param.localTable);
		param.diffTable = DataBaseMgr::getInstance(userContext_)->getDiffTable();
		assert(NULL != param.diffTable);

		int64_t rootId = changeJournal_->getRootId();
		std::wstring cacheName = Utility::FS::get_file_name(SyncConfigure::getInstance(userContext_)->cachePath());

		for (LocalSyncNodes::iterator it = syncNodes.begin(); it != syncNodes.end(); )
		{
			syncNode = *it;
			assert(NULL != syncNode.get());

#ifdef _TEST
			static uint32_t index = 1;
			printf("\n[%d] 0x%016I64X 0x%016I64X %s\n", 
				index++, syncNode->id, syncNode->parent, 
				Utility::String::wstring_to_string(syncNode->name).c_str());
			printOper(syncNode->oper);
			//it = syncNodes.erase(it);
			//continue;
#endif
			//skip root node
			if (rootId == syncNode->id)
			{
				it = syncNodes.erase(it);
				continue;
			}
			// ignore create and then delete oper
			if (syncNode->oper&USN_REASON_FILE_CREATE && 
				syncNode->oper&USN_REASON_FILE_DELETE)
			{
				it = syncNodes.erase(it);
				continue;
			}

			param.type = (syncNode->attributes&FILE_ATTRIBUTE_DIRECTORY)?FILE_TYPE_DIR:FILE_TYPE_FILE;
			param.isParentInMonitor = isMonitorNode(syncNode->parent);
			param.isInMonitor = isMonitorNode(syncNode->id);

			// create
			if ((syncNode->oper&USN_REASON_FILE_CREATE))
			{
				// ignore cache path and Desktop.ini
				if (rootId == syncNode->parent && 
					(cacheName == syncNode->name || syncNode->name == L"Desktop.ini"))
				{
					it = syncNodes.erase(it);
					continue;
				}
				CHECK_RESULT(readUsnCreateImpl(param, syncNode, operNodes));
				it = syncNodes.erase(it);
				continue;
			}
			// delete
			else if (syncNode->oper&USN_REASON_FILE_DELETE)
			{
				// ignore cache path and Desktop.ini
				if (rootId == syncNode->parent && 
					(cacheName == syncNode->name || syncNode->name == L"Desktop.ini"))
				{
					it = syncNodes.erase(it);
					continue;
				}
				CHECK_RESULT(readUsnDeleteImpl(param, syncNode, operNodes));
				it = syncNodes.erase(it);
				continue;
			}
			// rename/move
			else if (syncNode->oper&USN_REASON_RENAME_OLD_NAME)
			{
				LocalSyncNode newNode;
				bool found = false;
				// find next oper of USN_REASON_RENAME_NEW_NAME
				LocalSyncNodes::iterator itor = it;
				++itor;
				for (; itor != syncNodes.end(); ++itor)
				{
					// in some times, USN_REASON_RENAME_NEW_NAME may lost
					if ((*itor)->id == syncNode->id && (*itor)->oper&USN_REASON_RENAME_OLD_NAME)
					{
						syncNodes.erase(it);
						break;
					}
					if ((*itor)->id == syncNode->id && (*itor)->oper&USN_REASON_RENAME_NEW_NAME)
					{
						newNode = *itor;
						found = true;
						break;
					}
				}
				// if can not find, then wait for next time
				if (!found)
				{
					return RT_CONTINUE;
				}

				// ignore cache path
				if ((rootId == syncNode->parent && 
					(cacheName == syncNode->name || syncNode->name == L"Desktop.ini")) || 
					(rootId == newNode->parent && 
					(cacheName == newNode->name || newNode->name == L"Desktop.ini")))
				{
					it = syncNodes.erase(it);
					continue;
				}

				CHECK_RESULT(readUsnRenameImpl(param, syncNode, newNode, operNodes));
				// delete the USN_REASON_RENAME_NEW_NAME oper after success
				syncNodes.erase(itor);
				it = syncNodes.erase(it);
				continue;
			}
			// edit
			else if (syncNode->oper&USN_REASON_DATA_EXTEND ||
				syncNode->oper&USN_REASON_DATA_OVERWRITE ||
				syncNode->oper&USN_REASON_DATA_TRUNCATION ||
				syncNode->oper&USN_REASON_EA_CHANGE)
			{
				// ignore Desktop.ini
				if (rootId == syncNode->parent && syncNode->name == L"Desktop.ini")
				{
					it = syncNodes.erase(it);
					continue;
				}
				CHECK_RESULT(readUsnEditImpl(param, syncNode, operNodes));
				it = syncNodes.erase(it);
				continue;
			}
			else
			{
				it = syncNodes.erase(it);
				continue;
			}
		}

		CHECK_RESULT(param.diffTable->addOperList(operNodes));
		CHECK_RESULT(param.localTable->replaceNodes(param.addNodes));
		CHECK_RESULT(param.localTable->updateNodes(param.updateNodes));
		CHECK_RESULT(param.localTable->deleteNodes(param.deleteNodes));
		CHECK_RESULT(param.localTable->getChildren(param.trashDeleteNodes));
		CHECK_RESULT(param.localTable->trashDeleteNodes(param.trashDeleteNodes));

		return RT_OK;
	}
	
private:
	UserContext* userContext_;
	std::auto_ptr<ChangeJournal> changeJournal_;
	CInIHelper iniHelper_;
};

std::auto_ptr<LocalDetector> LocalDetector::create(UserContext* userContext)
{
	return std::auto_ptr<LocalDetector>(
		static_cast<LocalDetector*>(new LocalDetectorImpl(userContext)));
}
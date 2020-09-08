#ifndef _REMOTE_COMMON_FILE_DIALOG_H_
#define _REMOTE_COMMON_FILE_DIALOG_H_

#include "CommonFileDialog.h"
#include "SkinConfMgr.h"
#include "UserContextMgr.h"
#include "SyncFileSystemMgr.h"
#include "PathMgr.h"
#include "TeamSpaceResMgr.h"
#include "Utility.h"
#include "InILanguage.h"

#define SORT_FILED_NAME ("name")
#define SORT_FILED_TYPE ("type")
#define SORT_DIRECTION_ASC ("asc")
#define SORT_DIRECTION_DESC ("desc")

namespace Onebox
{
	using namespace SD;

	struct RemoteCommonFileDialogData
	{
		int64_t id;
		UserContext *userContext;

		RemoteCommonFileDialogData()
			:id(INVALID_ID)
			,userContext(NULL) {}
	};

	enum MyFileCommonFileDialogType
	{
		MFRFDT_ROOT,
		MFRFDT_FILENODE
	};

	struct MyFileCommonFileDialogData : public RemoteCommonFileDialogData
	{
		MyFileCommonFileDialogType type;

		MyFileCommonFileDialogData()
			:type(MFRFDT_FILENODE) {}
	};

	class MyFileCommonFileDialogNotify : public ICommonFileDialogNotify
	{
	public:
		MyFileCommonFileDialogNotify(UserContext *userContext, uint32_t languageId)
			:userContext_(userContext)
			,languageId_(languageId)
		{

		}

		virtual int32_t listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
		{
			// root
			if (NULL == parent.get())
			{
				CommonFileDialogItem root(new st_CommonFileDialogItem);
				root->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_default_myfile_name);
				root->path = L"";
				root->type = CFDFT_DIR;
				root->dataType = CFDDT_remote;
				MyFileCommonFileDialogData *rootData = (MyFileCommonFileDialogData*)makeItemData();

				if (rootData)
				{
					rootData->id = 0;
					rootData->userContext = userContext_;
					rootData->type = MFRFDT_ROOT;
					root->data.reset(rootData);
					root->notify = this;
					root->autoExpand = true;
					result.push_back(root);

					return E_CFD_SUCCESS;
				}
				else
				{
					return E_CFD_INVALID_PARAM;
				}
			}

			if (parent->dataType != CFDDT_remote || parent->type != CFDFT_DIR)
			{
				return E_CFD_INVALID_PARAM;
			}
			return listRemoteFolder(parent, result);
		}

		virtual std::wstring loadIcon(const CommonFileDialogItem& item)
		{
			if (NULL == item.get())
			{
				return L"";
			}
			MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return L"";
			}
			if (data->type == MFRFDT_ROOT)
			{
				return GetInstallPath() + L"skin\\Image\\" + common_file_dialog_default_myfile_icon;
			}
			return SkinConfMgr::getInstance()->getIconPath(item->type, item->name, item->commonFolderEXType);
		}

		virtual CommonFileDialogItem createItem(const CommonFileDialogItem& parent)
		{
			CommonFileDialogItem item(NULL);
			if (NULL == parent.get())
			{
				item;
			}

			MyFileCommonFileDialogData *parentData = (MyFileCommonFileDialogData*)parent->data.get();
			if (NULL == parentData)
			{
				return item;
			}
			if (NULL != parentData->userContext)
			{
				Path path = parentData->userContext->getPathMgr()->makePath();
				path.parent(parentData->id);
				std::wstring newName = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_default_new_folder_name);
				if (E_CFD_SUCCESS != parentData->userContext->getSyncFileSystemMgr()->getNewName(path, newName))
				{
					return item;
				}
				FILE_DIR_INFO fileInfo;
				path.id(parentData->id);
				if (E_CFD_SUCCESS != parentData->userContext->getSyncFileSystemMgr()->create(path, newName, fileInfo, ADAPTER_FOLDER_TYPE_REST))
				{
					return item;
				}
				// fill in item informamtion
				item.reset(new st_CommonFileDialogItem);
				item->name = newName;
				item->path = parent->path + PATH_DELIMITER + newName;
				item->type = CFDFT_DIR;
				item->dataType = CFDDT_remote;
				MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)makeItemData();
				if ( data )
				{
					data->id = fileInfo.id;
					data->userContext = parentData->userContext;
					item->data.reset(data);
					item->notify = this;
				}
			}
			return item;
		}

		virtual int32_t renameItem(const CommonFileDialogItem& item, const std::wstring& name)
		{
			if (NULL == item.get())
			{
				return E_CFD_INVALID_PARAM;
			}
			RemoteCommonFileDialogData *data = (RemoteCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return E_CFD_INVALID_PARAM;
			}
			if (NULL == data->userContext || INVALID_ID == data->id || name.empty())
			{
				return E_CFD_INVALID_PARAM;
			}
			Path path = userContext_->getPathMgr()->makePath();
			path.id(data->id);
			path.ownerId(data->userContext->id.id);
			
			int32_t nResult = userContext_->getSyncFileSystemMgr()->rename(path, name, ADAPTER_FOLDER_TYPE_REST);
			if (E_CFD_SUCCESS != nResult)
			{
				return nResult;
			}
			return E_CFD_SUCCESS;
		}

		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button)
		{
			if (NULL == item.get())
			{
				return false;
			}
			if(CFDCB_OK == button)
			{
				return false;
			}
			if (item->type == CFDFT_DIR)
			{
				return item->commonFolderEXType != COMPUTER;
			}
			return false;
		}

		virtual int32_t removeItem(const CommonFileDialogItem& item)
		{
			if (NULL == item.get())
			{
				return E_CFD_INVALID_PARAM;
			}
			RemoteCommonFileDialogData *data = (RemoteCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return E_CFD_INVALID_PARAM;
			}
			if (NULL == data->userContext || INVALID_ID == data->id)
			{
				return E_CFD_INVALID_PARAM;
			}

			Path path = userContext_->getPathMgr()->makePath();
			path.id(data->id);
			path.ownerId(data->userContext->id.id);
			
			int32_t nResult = userContext_->getSyncFileSystemMgr()->remove(path, ADAPTER_FOLDER_TYPE_REST);
			if (E_CFD_SUCCESS != nResult)
			{
				return nResult;
			}
			return E_CFD_SUCCESS;
		}

	protected:
		int32_t listRemoteFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
		{
			MyFileCommonFileDialogData *parentData = (MyFileCommonFileDialogData*)parent->data.get();
			if (NULL == parentData)
			{
				return E_CFD_INVALID_PARAM;
			}

			Path path = parentData->userContext->getPathMgr()->makePath();
			path.id(parentData->id);
			LIST_FOLDER_RESULT listResult;
			PageParam pageParam;
			pageParam.limit = 1000;
			OrderParam order;
			order.field	= SORT_FILED_TYPE;
			order.direction = SORT_DIRECTION_ASC;
			pageParam.orderList.push_back(order);
			order.field	= SORT_FILED_NAME;
			order.direction = SORT_DIRECTION_ASC;
			pageParam.orderList.push_back(order);

			int64_t count = 0;
			if (E_CFD_SUCCESS != parentData->userContext->getSyncFileSystemMgr()->listPage(path, listResult, pageParam, count))
			{
				return E_CFD_ERROR;
			}

			int32_t extraType;
			for (LIST_FOLDER_RESULT::iterator it = listResult.begin(); it != listResult.end(); ++it)
			{
				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = it->name;
				item->path = parent->path + PATH_DELIMITER + it->name;
				item->type = (CommonFileDialogFileType)it->type;
				item->dataType = CFDDT_remote;

				if(L"" == it->extraType)
				{
					extraType = FOLDER;
				}
				else if(L"computer" == it->extraType)
				{
					extraType = COMPUTER;
				}
				else if (L"disk" == it->extraType)
				{
					extraType = DISK;
				}
				item->commonFolderEXType = extraType;

				MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)makeItemData();
				if ( data )
				{
					data->id = it->id;
					data->userContext = parentData->userContext;
					item->data.reset(data);
					item->notify = this;
					result.push_back(item);
				}
				else
				{
					return E_CFD_INVALID_PARAM;
				}
			}
			return E_CFD_SUCCESS;
		}

	private:
		virtual RemoteCommonFileDialogData* makeItemData()
		{
			return new MyFileCommonFileDialogData;
		}

	protected:
		UserContext *userContext_;
		uint32_t languageId_;
	};

	enum TeamspaceCommonFileDialogType
	{
		TRFDT_ROOT,
		TRFDT_TEAMSPACE,
		TRFDT_FILENODE
	};

	struct TeamspaceCommonFileDialogData : public RemoteCommonFileDialogData
	{
		TeamspaceCommonFileDialogType type;

		TeamspaceCommonFileDialogData()
			:type(TRFDT_FILENODE) {}
	};

	class TeamspaceCommonFileDialogNotify : public MyFileCommonFileDialogNotify
	{
	public:
		TeamspaceCommonFileDialogNotify(UserContext *userContext, uint32_t languageId)
			:MyFileCommonFileDialogNotify(userContext, languageId)
		{
			rootType_ = TRFDT_ROOT;
		}

		TeamspaceCommonFileDialogNotify(UserContext *userContext, uint32_t languageId, TeamspaceCommonFileDialogType rootType)
			:MyFileCommonFileDialogNotify(userContext, languageId)
		{
			rootType_ = rootType;
		}

		virtual int32_t listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
		{
			// root
			if (NULL == parent.get())
			{
				CommonFileDialogItem root(new st_CommonFileDialogItem);
				root->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_default_teamspace_name);
				if(TRFDT_TEAMSPACE == rootType_)
				{
					root->name += L"(" + userContext_->id.name +L")";
				}
				root->path = L"";
				root->type = CFDFT_DIR;
				root->dataType = CFDDT_remote;
				TeamspaceCommonFileDialogData *rootData = (TeamspaceCommonFileDialogData*)makeItemData();

				if ( rootData )
				{
					if(TRFDT_TEAMSPACE == rootType_)
					{
						rootData->id = 0;
					}
					rootData->userContext = userContext_;
					rootData->type = rootType_;
					root->data.reset(rootData);
					root->notify = this;
					root->autoExpand = true;
					result.push_back(root);

					return E_CFD_SUCCESS;
				}
				else
				{
					return E_CFD_INVALID_PARAM;
				}
			}

			if (parent->dataType != CFDDT_remote || parent->type != CFDFT_DIR)
			{
				return E_CFD_INVALID_PARAM;
			}

			TeamspaceCommonFileDialogData *parentData = (TeamspaceCommonFileDialogData*)parent->data.get();
			if (NULL == parentData)
			{
				return E_CFD_INVALID_PARAM;
			}
			if (parentData->type == TRFDT_ROOT)
			{
				return listTeamspace(result);
			}
			return listRemoteFolder(parent, result);
		}

		virtual std::wstring loadIcon(const CommonFileDialogItem& item)
		{
			if (NULL == item.get())
			{
				return L"";
			}

			TeamspaceCommonFileDialogData *data = (TeamspaceCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return L"";
			}
			// load team space default icon
			if (data->type == TRFDT_ROOT || data->type == TRFDT_TEAMSPACE)
			{
				return GetInstallPath() + L"skin\\Image\\" + common_file_dialog_default_teamspace_icon;
			}
			return SkinConfMgr::getInstance()->getIconPath(item->type, item->name);
		}

		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button)
		{
			if (NULL == item.get())
			{
				return false;
			}

			TeamspaceCommonFileDialogData *data = (TeamspaceCommonFileDialogData*)item->data.get();

			if(CFDCB_OK == button)
			{
				return false;
			}

			if (NULL == data)
			{
				return false;
			}
			if (data->type == TRFDT_ROOT)
			{
				return false;
			}
			return (item->type == CFDFT_DIR);
		}

		virtual bool okButtonEnabled(const CommonFileDialogItem& item)
		{
			TeamspaceCommonFileDialogData *data = (TeamspaceCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return false;
			}
			return (data->type != TRFDT_ROOT);
		}

		virtual bool isRename(const CommonFileDialogItem& item)
		{
			TeamspaceCommonFileDialogData *data = (TeamspaceCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return false;
			}
			return (data->type == TRFDT_FILENODE);
		}

	protected:
		int32_t listTeamspace(CommonFileDialogListResult& result)
		{
			// list team space
			UserTeamSpaceNodeInfoArray teamspaceListArray;
			PageParam pageparam;
			userContext_->getTeamSpaceMgr()->getTeamSpaceListUser(teamspaceListArray, pageparam);
			for (UserTeamSpaceNodeInfoArray::iterator it = teamspaceListArray.begin(); 
				it != teamspaceListArray.end(); ++it)
			{
				if("viewer" == it->role())
				{
					continue;
				}
				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = Utility::String::utf8_to_wstring(it->member_.name());
				item->path = PATH_DELIMITER + item->name;
				item->type = CFDFT_DIR;
				item->dataType = CFDDT_remote;
				TeamspaceCommonFileDialogData *data = (TeamspaceCommonFileDialogData*)makeItemData();
				if ( data )
				{
					data->id = 0;
					data->userContext = UserContextMgr::getInstance()->createUserContext(userContext_, it->teamId(), UserContext_Teamspace, item->name);
					data->type = TRFDT_TEAMSPACE;
					item->data.reset(data);
					item->notify = this;
					result.push_back(item);
				}
				else
				{
					return E_CFD_INVALID_PARAM;
				}
			}
			return E_CFD_SUCCESS;
		}

	private:
		virtual RemoteCommonFileDialogData* makeItemData()
		{
			return new TeamspaceCommonFileDialogData;
		}

	protected:
		TeamspaceCommonFileDialogType rootType_;
	};

	class MyFileTeamspaceMixedCommonFileDialogNotify : public ICommonFileDialogNotify
	{
	public:
		MyFileTeamspaceMixedCommonFileDialogNotify(UserContext *userContext, uint32_t languageId)
			:userContext_(userContext)
		{
			try
			{
				myFileImpl_ = new MyFileCommonFileDialogNotify(userContext, languageId);
				teamspaceImpl_ = new TeamspaceCommonFileDialogNotify(userContext, languageId);
			}
			catch(...) {}
		}

		virtual ~MyFileTeamspaceMixedCommonFileDialogNotify()
		{
			try
			{
				if (myFileImpl_)
				{
					delete myFileImpl_;
					myFileImpl_ = NULL;
				}
				if (teamspaceImpl_)
				{
					delete teamspaceImpl_;
					teamspaceImpl_ = NULL;
				}
			}
			catch(...) {}
		}

		virtual int32_t listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
		{
			// root
			if (NULL == parent.get())
			{
				teamspaceImpl_->listFolder(parent, result);
				if (!result.empty())
				{
					result.front()->autoExpand = false;
				}

				myFileImpl_->listFolder(parent, result);

				return E_CFD_SUCCESS;
			}
			return E_CFD_INVALID_PARAM;
		}

		virtual std::wstring loadIcon(const CommonFileDialogItem& item)
		{
			return L"";
		}

		virtual CommonFileDialogItem createItem(const CommonFileDialogItem& parent)
		{
			return CommonFileDialogItem(NULL);
		}

		virtual int32_t renameItem(const CommonFileDialogItem& item, const std::wstring& name)
		{
			return E_CFD_INVALID_PARAM;
		}

		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button)
		{
			return false;
		}

	private:
		UserContext *userContext_;
		MyFileCommonFileDialogNotify *myFileImpl_;
		TeamspaceCommonFileDialogNotify *teamspaceImpl_;
	};

	class SaveToMyFileDialogNotify : public MyFileCommonFileDialogNotify
	{
	public:
		SaveToMyFileDialogNotify(UserContext *userContext, uint32_t languageId)
			:MyFileCommonFileDialogNotify(userContext, languageId)
		{

		}

		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button)
		{
			if (NULL == item.get())
			{
				return false;
			}

			MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return false;
			}

			if(item->commonFolderEXType == COMPUTER)
			{
				return false;
			}

			if (CFDCB_COPY == button || CFDCB_MOVE == button)
			{
				return false;
			}

			return (item->type == CFDFT_DIR);
		}

	};
}

#endif
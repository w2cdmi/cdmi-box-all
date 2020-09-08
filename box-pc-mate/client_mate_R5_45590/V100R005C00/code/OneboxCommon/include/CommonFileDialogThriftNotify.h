#ifndef _REMOTE_COMMON_FILE_DIALOG_THRIFT_H_
#define _REMOTE_COMMON_FILE_DIALOG_THRIFT_H_

#include "CommonFileDialog.h"
#include "SkinConfMgr.h"
#include "CommonFileDialogThriftClient.h"
#include "Utility.h"
#include "UserContext.h"
#include "InILanguage.h"

namespace Onebox
{
	using namespace SD;

	struct RemoteCommonFileDialogThriftData
	{
		int64_t id;
		int64_t userId;
		UserContextType userType;

		RemoteCommonFileDialogThriftData()
			:id(INVALID_ID)
			,userId(INVALID_ID)
			,userType((UserContextType)UserContext_Invalid)
		{}
	};

	enum MyFileCommonFileDialogThriftType
	{
		MFRFDT_ROOT,
		MFRFDT_FILENODE
	};

	struct MyFileCommonFileDialogThriftData : public RemoteCommonFileDialogThriftData
	{
		MyFileCommonFileDialogThriftType type;

		MyFileCommonFileDialogThriftData()
			:type(MFRFDT_FILENODE) {}
	};

	class MyFileCommonFileDialogThriftNotify : public ICommonFileDialogNotify
	{
	public:
		MyFileCommonFileDialogThriftNotify(int64_t userId, uint32_t languageId, UserContextType userType=UserContext_User)
			:userId_(userId)
			,userType_(userType)
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
				std::shared_ptr<MyFileCommonFileDialogThriftData> rootData((MyFileCommonFileDialogThriftData*)makeItemData());
				rootData->id = 0;
				rootData->userId = userId_;
				rootData->userType = userType_;
				rootData->type = MFRFDT_ROOT;
				root->data = rootData;
				root->notify = this;
				root->autoExpand = true;
				result.push_back(root);
				return E_CFD_SUCCESS;
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
			MyFileCommonFileDialogThriftData *data = (MyFileCommonFileDialogThriftData*)item->data.get();
			if (NULL == data)
			{
				return L"";
			}
			if (data->type == MFRFDT_ROOT)
			{
				return GetInstallPath() + L"skin\\image\\"+ common_file_dialog_default_myfile_icon;
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

			MyFileCommonFileDialogThriftData *parentData = (MyFileCommonFileDialogThriftData*)parent->data.get();
			if (NULL == parentData)
			{
				return item;
			}
			if (INVALID_ID != parentData->userId && INVALID_ID != parentData->id)
			{
				std::wstring newName = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_default_new_folder_name);
				newName = CommonFileDailogThriftClient::getInstance()->getNewName(parentData->userId, parentData->userType, parentData->id, newName);
				if (newName.empty())
				{
					return item;
				}

				File_Node fileInfo = CommonFileDailogThriftClient::getInstance()->createFolder(parentData->userId, parentData->userType, parentData->id, newName);
				if (fileInfo.id < 0 || fileInfo.name.empty())
				{
					return item;
				}
				// fill in item informamtion
				item.reset(new st_CommonFileDialogItem);
				item->name = newName;
				item->path = parent->path + PATH_DELIMITER + newName;
				item->type = CFDFT_DIR;
				item->dataType = CFDDT_remote;
				std::shared_ptr<MyFileCommonFileDialogThriftData> data ((MyFileCommonFileDialogThriftData*)makeItemData());
				data->id = fileInfo.id;
				data->userId = parentData->userId;
				data->userType = parentData->userType;
				item->data = data;
				item->notify = this;
			}
			return item;
		}

		virtual int32_t renameItem(const CommonFileDialogItem& item, const std::wstring& name)
		{
			if (NULL == item.get())
			{
				return E_CFD_INVALID_PARAM;
			}
			RemoteCommonFileDialogThriftData *data = (RemoteCommonFileDialogThriftData*)item->data.get();
			if (NULL == data)
			{
				return E_CFD_INVALID_PARAM;
			}
			if (INVALID_ID == data->userId || INVALID_ID == data->id || name.empty())
			{
				return E_CFD_INVALID_PARAM;
			}
			int32_t nResult = CommonFileDailogThriftClient::getInstance()->renameFolder(data->userId, data->userType, data->id, name);
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
			if (CFDCB_COPY == button || CFDCB_MOVE == button)
			{
				return false;
			}
			if(item->type == CFDFT_DIR)
			{
				return item->commonFolderEXType != COMPUTER;
			}
			return false;
		}

		virtual bool okButtonEnabled(const CommonFileDialogItem& item)
		{
			return item->commonFolderEXType != COMPUTER;
		}

	protected:
		int32_t listRemoteFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
		{
			MyFileCommonFileDialogThriftData *parentData = (MyFileCommonFileDialogThriftData*)parent->data.get();
			if (NULL == parentData)
			{
				return E_CFD_INVALID_PARAM;
			}

			std::vector<File_Node> fileNodes;
			if (E_CFD_SUCCESS != CommonFileDailogThriftClient::getInstance()->listRemoteDir(fileNodes, parentData->id, parentData->userId, parentData->userType))
			{
				return E_CFD_ERROR;
			}
			for (std::vector<File_Node>::iterator it = fileNodes.begin(); it != fileNodes.end(); ++it)
			{
				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = Utility::String::utf8_to_wstring(it->name);
				item->path = parent->path + PATH_DELIMITER + item->name;
				item->type = (CommonFileDialogFileType)it->type;
				item->dataType = CFDDT_remote;
				std::shared_ptr<MyFileCommonFileDialogThriftData> data(static_cast<MyFileCommonFileDialogThriftData*>(makeItemData()));
				data->id = it->id;
				data->userId = parentData->userId;
				data->userType = parentData->userType;
				item->data = data;
				item->notify = this;
				item->commonFolderEXType=it->extraType;
				result.push_back(item);
			}
			return E_CFD_SUCCESS;
		}

	private:
		virtual RemoteCommonFileDialogThriftData* makeItemData()
		{
			return new MyFileCommonFileDialogThriftData;
		}

	protected:
		int64_t userId_;
		UserContextType userType_;
		uint32_t languageId_;
	};

	enum TeamspaceCommonFileDialogThriftType
	{
		TRFDT_ROOT,
		TRFDT_TEAMSPACE,
		TRFDT_FILENODE
	};

	struct TeamspaceCommonFileDialogThriftData : public RemoteCommonFileDialogThriftData
	{
		TeamspaceCommonFileDialogThriftType type;

		TeamspaceCommonFileDialogThriftData()
			:type(TRFDT_FILENODE) {}
	};

	class TeamspaceCommonFileDialogThriftNotify : public MyFileCommonFileDialogThriftNotify
	{
	public:
		TeamspaceCommonFileDialogThriftNotify(int64_t userId, uint32_t languageId, UserContextType userType=UserContext_Teamspace)
			:MyFileCommonFileDialogThriftNotify(userId, languageId, userType)
		{

		}

		virtual int32_t listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result)
		{
			// root
			if (NULL == parent.get())
			{
				CommonFileDialogItem root(new st_CommonFileDialogItem);
				root->name = IniLanguageHelper(languageId_).GetCommonString(common_file_dialog_default_teamspace_name);
				root->path = L"";
				root->type = CFDFT_DIR;
				root->dataType = CFDDT_remote;
				std::shared_ptr<TeamspaceCommonFileDialogThriftData> rootData(static_cast<TeamspaceCommonFileDialogThriftData*>(makeItemData()));
				rootData->userId = userId_;
				rootData->userType = userType_;
				rootData->type = TRFDT_ROOT;
				root->data = rootData;
				root->notify = this;
				root->autoExpand = true;
				result.push_back(root);
				return E_CFD_SUCCESS;
			}

			if (parent->dataType != CFDDT_remote || parent->type != CFDFT_DIR)
			{
				return E_CFD_INVALID_PARAM;
			}

			TeamspaceCommonFileDialogThriftData *parentData = (TeamspaceCommonFileDialogThriftData*)parent->data.get();
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

			TeamspaceCommonFileDialogThriftData *data = (TeamspaceCommonFileDialogThriftData*)item->data.get();
			if (NULL == data)
			{
				return L"";
			}
			// load teamspace default icon
			if (data->type == TRFDT_ROOT || data->type == TRFDT_TEAMSPACE)
			{
				return GetInstallPath() + L"skin\\image\\" + common_file_dialog_default_teamspace_icon;
			}
			return SkinConfMgr::getInstance()->getIconPath(item->type, item->name);
		}

		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button)
		{
			if (NULL == item.get())
			{
				return false;
			}

			if (CFDCB_MOVE == button || CFDCB_COPY == button)
			{
				return false;
			}

			TeamspaceCommonFileDialogThriftData *data = (TeamspaceCommonFileDialogThriftData*)item->data.get();
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
			TeamspaceCommonFileDialogThriftData *data = (TeamspaceCommonFileDialogThriftData*)item->data.get();
			if (NULL == data)
			{
				return false;
			}
			return (data->type != TRFDT_ROOT);
		}

	protected:
		int32_t listTeamspace(CommonFileDialogListResult& result)
		{
			// list teamspace
			std::vector<TeamSpace_Node> teamspaceNodes;
			if (E_CFD_SUCCESS != CommonFileDailogThriftClient::getInstance()->listTeamspace(teamspaceNodes))
			{
				return E_CFD_ERROR;
			}
			for (std::vector<TeamSpace_Node>::iterator it = teamspaceNodes.begin(); it != teamspaceNodes.end(); ++it)
			{
				CommonFileDialogItem item(new st_CommonFileDialogItem);
				item->name = Utility::String::utf8_to_wstring(it->name);
				item->path = PATH_DELIMITER + item->name;
				item->type = CFDFT_DIR;
				item->dataType = CFDDT_remote;
				std::shared_ptr<TeamspaceCommonFileDialogThriftData> data(static_cast<TeamspaceCommonFileDialogThriftData*>(makeItemData()));
				data->id = 0;
				data->userId = it->id;
				data->userType = UserContext_Teamspace;
				data->type = TRFDT_TEAMSPACE;
				item->data = data;
				item->notify = this;
				result.push_back(item);
			}
			return E_CFD_SUCCESS;
		}

	private:
		virtual RemoteCommonFileDialogThriftData* makeItemData()
		{
			return new TeamspaceCommonFileDialogThriftData;
		}
	};

	class MyFileTeamspaceMixedCommonFileDialogThriftNotify : public ICommonFileDialogNotify
	{
	public:
		MyFileTeamspaceMixedCommonFileDialogThriftNotify(int64_t userId, uint32_t languageId)
		{
			try
			{
				myFileImpl_ = new MyFileCommonFileDialogThriftNotify(userId, languageId, UserContext_User);
				teamspaceImpl_ = new TeamspaceCommonFileDialogThriftNotify(userId, languageId, UserContext_Teamspace);
			}
			catch(...) {}
		}

		virtual ~MyFileTeamspaceMixedCommonFileDialogThriftNotify()
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
		MyFileCommonFileDialogThriftNotify *myFileImpl_;
		TeamspaceCommonFileDialogThriftNotify *teamspaceImpl_;
	};
}

#endif
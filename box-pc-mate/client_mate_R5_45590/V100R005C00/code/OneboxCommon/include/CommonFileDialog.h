#ifndef _COMMON_FILE_DIALOG_H_
#define _COMMON_FILE_DIALOG_H_

#include "CommonDefine.h"
#include <list>
#include <functional>

namespace Onebox
{
#define common_file_dialog_default_new_folder_name (L"common_file_dialog_default_new_folder_name")
#define common_file_dialog_my_computer_name (L"common_file_dialog_my_computer_name")
#define common_file_dialog_desktop_name (L"common_file_dialog_desktop_name")
#define common_file_dialog_my_document_name (L"common_file_dialog_my_document_name")
#define common_file_dialog_my_music_name (L"common_file_dialog_my_music_name")
#define common_file_dialog_my_picture_name (L"common_file_dialog_my_picture_name")
#define common_file_dialog_my_video_name (L"common_file_dialog_my_video_name")
#define common_file_dialog_default_disk_name (L"common_file_dialog_default_disk_name")
#define common_file_dialog_default_myfile_name (L"common_file_dialog_default_myfile_name")
#define common_file_dialog_default_teamspace_name (L"common_file_dialog_default_teamspace_name")

#define common_file_dialog_default_myfile_icon (L"ic_pop_myfiles.png")
#define common_file_dialog_default_teamspace_icon (L"ic_pop_transto_teamspace.png")

	enum COMMFILEDIALOG_USE_ACTION
	{
		COMMFILEDIALOG_UPLOAD = 0,	
		COMMFILEDIALOG_DOWNLOAD,
		COMMFILEDIALOG_COPYMOVE,
		COMMFILEDIALOG_SAVETOMYFILE,
		COMMFILEDIALOG_SAVETOTEAMSPACE,
		COMMFILEDIALOG_BACKUPTASK_LOCALL,
		COMMFILEDIALOG_BACKUPTASK_ONEBOX,
		COMMFILEDIALOG_RIGHTUPLOAD,
	};

	enum CommonFileDialogError
	{
		E_CFD_SUCCESS = 0,
		E_CFD_CANCEL = -1,
		E_CFD_ERROR = -2,
		E_CFD_USE_DEFAULT_ICON = -3,
		E_CFD_NOT_IMPLEMENT = -4,
		E_CFD_INVALID_PARAM = -5
	};

	enum CommonFileDialogOption
	{
		CFDO_only_show_folder = 0x01,
		CFDO_enable_multi_select = 0x02,
		CFDO_ok_button_visible = 0x04,
		CFDO_cancel_button_visible = 0x08
	};

	enum CommonFileDialogFileType
	{
		CFDFT_DIR,
		CFDFT_FILE
	};

	enum CommonFileDialogCustomButton
	{
		CFDCB_CREATE = 0x01,
		CFDCB_MOVE = 0x02,
		CFDCB_COPY = 0x04,
		CFDCB_OK = 0x08
	};

	enum CommonFileDialogDataType
	{
		CFDDT_local,
		CFDDT_remote
	};

	class ICommonFileDialogNotify;

	struct st_CommonFileDialogItem
	{
		std::wstring name;
		std::wstring path;
		CommonFileDialogFileType type;
		int32_t commonFolderEXType;

		CommonFileDialogDataType dataType;
		std::shared_ptr<void> data;

		ICommonFileDialogNotify *notify;
		bool autoExpand;

		st_CommonFileDialogItem()
		{
			name = L"";
			path = L"";
			data = NULL;
			notify = NULL;
			autoExpand = false;
			commonFolderEXType=0;

			type = CommonFileDialogFileType::CFDFT_FILE;
			dataType = CommonFileDialogDataType::CFDDT_local;
		}

		~st_CommonFileDialogItem()
		{
		}
	};
	typedef std::shared_ptr<st_CommonFileDialogItem> CommonFileDialogItem;

	typedef std::list<CommonFileDialogItem> CommonFileDialogListResult;

	static std::list<CommonFileDialogItem> commonFileData;

	static void resultHanlder(const CommonFileDialogListResult& result)
	{
		if(1 != result.size())
		{
			return;
		}

		if(0 != commonFileData.size())
		{
			commonFileData.clear();
		}

		for (CommonFileDialogListResult::const_iterator it = result.begin(); it != result.end(); ++it)
		{
			commonFileData.push_back(*it);
		}
	}

	class ICommonFileDialogNotify
	{
	public:
		virtual ~ICommonFileDialogNotify() {};
		virtual int32_t listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result) = 0;
		virtual std::wstring loadIcon(const CommonFileDialogItem& item) = 0;
		virtual CommonFileDialogItem createItem(const CommonFileDialogItem& parent) = 0;
		virtual int32_t renameItem(const CommonFileDialogItem& item, const std::wstring& name) = 0;
		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button) = 0;
		virtual bool okButtonEnabled(const CommonFileDialogItem& parent) { return true; }
		virtual bool cancelButtonEnabled(const CommonFileDialogItem& parent) { return true; }
		virtual int32_t removeItem(const CommonFileDialogItem& item) { return RT_NOT_IMPLEMENT; };
		virtual bool isRename(const CommonFileDialogItem& item) { return false; };
	};

	class CommonFileDialog;
	typedef std::auto_ptr<CommonFileDialog> CommonFileDialogPtr;
	class CommonFileDialog
	{
	public:
		typedef std::function<void(const CommonFileDialogListResult&)> ResultHandler;

		static CommonFileDialogPtr createInstance(const std::wstring& xmlFolder, 
			HWND parent = NULL, 
			CommonFileDialogItem root = NULL, 
			const std::wstring& okButtonName = L"OK", 
			const std::wstring& wndClsName = L"CommonFileDialogFrame", 
			const std::wstring& wndName = L"CommonFileDialogFrame", 
			const std::wstring& title = L"File Dialog", 
			const std::wstring& titleTooltip = L"File Dialog");

		virtual ~CommonFileDialog();

		virtual void setOption(const CommonFileDialogOption option);

		virtual void setNotify(ICommonFileDialogNotify* notify);

		// when call this function, the CommonFileDialog will release memory after the window destroy
		// so the caller should give up the ownership of the CommonFileDialogPtr
		virtual void show(ResultHandler handler);

		virtual CommonFileDialogError showModal(ResultHandler handler,int iAction=0);

		virtual int GetControlIndex();

	protected:
		CommonFileDialog(const std::wstring& xmlFolder, 
			HWND parent, 
			CommonFileDialogItem root, 
			const std::wstring& okButtonName, 
			const std::wstring& wndClsName, 
			const std::wstring& wndName, 
			const std::wstring& title, 
			const std::wstring& titleTooltip);

	private:
		class Impl;
		std::shared_ptr<Impl> impl_;
	};
}

#endif
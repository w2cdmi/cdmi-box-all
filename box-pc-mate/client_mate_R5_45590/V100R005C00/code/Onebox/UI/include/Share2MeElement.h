#pragma once

#include "Common.h"
#include "Utility.h"
#include "ListContainerElement.h"

namespace Onebox
{
	const TCHAR* const SHARE2ME_LISTTAB = _T("share2Me_listTab");
	const TCHAR* const SHARE2ME_LIST =_T("share2Me_List");
	const TCHAR* const SHARE2ME_TILE = _T("share2Me_Tile");

	const TCHAR* const SHARE2ME_BACK = _T("share2Me_back");
	const TCHAR* const SHARE2ME_NEXT = _T("share2Me_next");
	const TCHAR* const SHARE2ME_FLUSH = _T("share2Me_flush");

	const TCHAR* const SHARE2ME_COUNT = _T("share2Me_count");
	const TCHAR* const SHARE2ME_UPLOAD = _T("share2Me_upload");
	const TCHAR* const SHARE2ME_CREATE = _T("share2Me_create");
	const TCHAR* const SHARE2ME_SHARE = _T("share2Me_share");
	const TCHAR* const SHARE2ME_MORE = _T("share2Me_more");
	const TCHAR* const SHARE2ME_DOWNLOAD = _T("share2Me_download");
	const TCHAR* const SHARE2ME_SAVE = _T("share2Me_save");
	const TCHAR* const SHARE2ME_EXIT = _T("share2Me_exit");
	const TCHAR* const SHARE2ME_VERSION = _T("share2Me_version");
	const TCHAR* const SHARE2ME_LISTOPT = _T("share2Me_listOpt");
	const TCHAR* const SHARE2ME_ICONOPT = _T("share2Me_tabloidOpt");
	const TCHAR* const SHARE2ME_SEARCHLAYOUT = _T("share2Me_searchLayout");
	const TCHAR* const SHARE2ME_SEARCHTXT = _T("share2Me_searchtxt");
	
	const TCHAR* const SHARE2ME_FILEICON = _T("share2Me_fileicon");
	const TCHAR* const SHARE2ME_FILENAME = _T("share2Me_filename");
	const TCHAR* const SHARE2ME_SHAREUSER = _T("share2Me_shareUser");
	const TCHAR* const SHARE2ME_FILESIZE = _T("share2Me_filesize");
	const TCHAR* const SHARE2ME_SHARETIME = _T("share2Me_sharetime");

	const TCHAR* const SHARE2ME_ICONITEM = _T("share2Me_iconItem");
	const TCHAR* const SHARE2ME_ICON = _T("share2Me_icon");
	const TCHAR* const SHARE2ME_NAME = _T("share2Me_name");

	const TCHAR* const SHARE2ME_SELECTALL = _T("share2Me_selectall");
	const TCHAR* const SHARE2ME_CHECKBOX = _T("share2Me_checkbox");

	struct ShareDirNode
	{
		bool isListView;
		std::wstring keyWord;
		std::list<PathNode> parentList;
		int64_t id;
		int64_t ownerId;
		std::wstring ownerName;
		int64_t shareTime;
		std::wstring shareFileName;
		File_Permissions filePermissions;
		UserContext* shareContext;

		ShareDirNode():isListView(true), keyWord(L""), id(-1), ownerId(-1), ownerName(L""), 
			shareTime(0), shareFileName(L""), filePermissions(FP_INVALID), shareContext(NULL)
		{}

		bool operator == (ShareDirNode& des) const
		{
			if(isListView!=des.isListView) return false;
			if(keyWord!=des.keyWord) return false;
			if(id!=des.id) return false;
			if(ownerId!=des.ownerId) return false;
			return true;
		}

		std::string toString()
		{
			std::stringstream stream;
			stream << "id:" << id 
				<< ", name:" << SD::Utility::String::wstring_to_string(shareFileName)
				<< ", isListView:" << isListView;
			if(0==id)
			{
				stream << ", keyWord:" << SD::Utility::String::wstring_to_string(keyWord);
			}
			else
			{
				stream << ", ownerName:" << SD::Utility::String::wstring_to_string(ownerName);	
			}
			return stream.str();
		}
	};

	class CShare2MeElementUI : public CShadeListContainerElement
	{
	public:
		void fillData(const ShareNode& shareNode, UserContext* userContext, int64_t parentId);

		void rename();

		std::wstring iconPath;
		int64_t ownerId;
		std::wstring ownerName;
		int64_t shareTime;
		bool isNoThumb;
	};

	class Share2MeListContainerElement : public CShare2MeElementUI
	{
	public:
		void initUI(bool renameable);

		bool flushThumb(const std::string& thumbKey);
	};

	class Share2MeTileLayoutListContainerElement : public CShare2MeElementUI
	{
	public:
		void initUI(bool renameable);

		bool flushThumb(const std::string& thumbKey);
	};
}
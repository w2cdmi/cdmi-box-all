#pragma once

#include "Common.h"
#include "Utility.h"
#include "ListContainerElement.h"

namespace Onebox
{
	const TCHAR* const MYFILE_MODULE_NAME = _T("myFile");
	const TCHAR* const MYFILE_SEARCHLAYOUT = _T("myFile_searchLayout");
	const TCHAR* const MYFILE_SEARCHTXT = _T("myFile_searchtxt");
	const TCHAR* const MYFILE_LIST = _T("myFile_list");
	const TCHAR* const MYFILE_LARGEICON = _T("myFile_largeIcon");

	const TCHAR* const MYFILE_LISTLISTVIEW = _T("myFile_listView");
	const TCHAR* const MYFILE_LIST_ICON =_T("myFile_icon");
	const TCHAR* const MYFILE_LIST_NAME = _T("myFile_listName");
	const TCHAR* const MYFILE_LIST_SIZE = _T("myFile_size");
	const TCHAR* const MYFILE_LIST_TYPE = _T("myFile_type");
	const TCHAR* const MYFILE_LIST_CTIME = _T("myFile_ctime");
	const TCHAR* const MYFILE_LISTTAB = _T("myFile_listTab");
	const TCHAR* const MYFILE_GROUPBTN = _T("myFile_groupBtn");
	const TCHAR* const MYFILE_SELECTALL = _T("myFile_selectAll");

	const TCHAR* const MYFILE_TILELAYOUT_LISTVIEW = _T("myFile_tileLayout_listView");
	const TCHAR* const MYFILE_LARGEICON_ICON = _T("myFile_nameIcon");
	const TCHAR* const MYFILE_LARGEICON_NAME = _T("myFile_name");

	const TCHAR* const MYFILE_SEARCHLISTVIEW = _T("myFile_SearchlistView");

	const TCHAR* const MYFILE_ISSYNC = _T("myFile_isSync");
	const TCHAR* const MYFILE_ISSYNC_CONTROL = _T("myFile_isSync_control");
	const TCHAR* const MYFILE_ISSHARE = _T("myFile_isShare");
	const TCHAR* const MYFILE_ISSHARE_CONTROL = _T("myFile_isShare_control");
	const TCHAR* const MYFILE_ISSHARELINK = _T("myFile_isShareLink");
	const TCHAR* const MYFILE_ISSHARELINK_CONTROL = _T("myFile_isShareLink_control");

	class CMyFileElementUI : public CShadeListContainerElement
	{
	public:
		void fillData(const FILE_DIR_INFO& fileNode, UserContext* userContext);

		void rename();

		std::wstring str_type;
		bool isNoThumb;
	};

	class MyFilesListContainerElement : public CMyFileElementUI
	{
	public:
		void initUI();

		bool flushThumb(const std::string& thumbKey);

		void initUI(const std::wstring& path);

		void mouseEnter();

		void mouseLeave();
	};

	class MyFilesTileLayoutListContainerElement : public CMyFileElementUI
	{
	public:
		void initUI();

		bool flushThumb(const std::string& thumbKey);
	};

}
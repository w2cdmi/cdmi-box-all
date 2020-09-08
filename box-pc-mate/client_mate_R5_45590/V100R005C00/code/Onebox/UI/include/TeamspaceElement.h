#pragma once

#include "Common.h"
#include "Utility.h"
#include "ListContainerElement.h"

namespace Onebox
{
	const TCHAR* const TEAMSPACE_FILETILELAYOUTLIST_ITEM_XML = _T("teamSpaceFileTileLayoutListItem.xml");
	const TCHAR* const TEAMSPACE_MODULE_NAME = _T("TeamSpace");
	const TCHAR* const TEAMSPACE_SEARCHLAYOUT = _T("teamSpace_searchLayout");
	const TCHAR* const TEAMSPACE_SEARCHTXT = _T("teamSpace_searchtxt");
	const TCHAR* const TEAMSPACE_FILESEARCHTXT = _T("teamSpace_fileSearchtxt");
	const TCHAR* const TEAMSPACE_FILELIST = _T("teamSpace_filelist");
	const TCHAR* const TEAMSPACE_LARGEICON = _T("teamSpace_largeIcon");
	const TCHAR* const TEAMSPACE_DEVIDE = _T("teamSpace_devide");
	const TCHAR* const TEAMSPACE_LISTLISTVIEW = _T("teamSpace_FilelistView");
	const TCHAR* const TEAMSPACE_LISTTAB = _T("teamSpace_listTab");
	const TCHAR* const TEAMSPACE_LISTITEM_LISTNAME =_T("teamSpace_List_listName");
	const TCHAR* const TEAMSPACE_LISTITEM_LISTSIZE =_T("teamSpace_List_size");
	const TCHAR* const TEAMSPACE_LISTITEM_LISTTYPE =_T("teamSpace_List_type");
	const TCHAR* const TEAMSPACE_LISTITEM_LISTMTIME =_T("teamSpace_List_mtime");
	const TCHAR* const TEAMSPACE_TILELAYOUTITEM_LISTNAME =_T("teamSpace_File_name");

	const TCHAR* const TEAMSPACE_GROUPBTN = _T("teamSpace_groupBtn");
	const TCHAR* const TEAMSPACE_SELECTALL = _T("teamSpace_selectAll");
	const TCHAR* const TEAMSPACE_TILELAYOUT_LISTVIEW = _T("teamSpace_tileLayout_listView");	
	const TCHAR* const TEAMSPACE_TILELAYOUT_FILELISTVIEW=_T("teamSpace_tile_FilelistView");
	const TCHAR* const TEAMSPACE_LARGEICON_LAYOUTNAME = _T("teamSpace_largeIconItemVLayout");

	const TCHAR* const TEAMSPACE_LARGEICON_ICON = _T("teamSpace_nameIcon");
	const TCHAR* const TEAMSPACE_LARGEICON_NAME = _T("teamSpace_name");
	const TCHAR* const TEAMSPACE_FILELISTITEM = _T("teamSpace_filelistItem");

	struct MyTeamSpaceFilesDirNode
	{
		bool isListView;
		int64_t dirId;
		std::wstring dirName;
		std::wstring keyWord;
		UITeamSpaceNode teamSpaceNode;
		File_Permissions filePermissions;

		MyTeamSpaceFilesDirNode():isListView(true), dirId(-1), dirName(L""), keyWord(L""), filePermissions(FP_INVALID)
		{
		}

		bool operator == (MyTeamSpaceFilesDirNode& des) const
		{
			if(isListView!=des.isListView) return false;
			if(keyWord!=des.keyWord) return false;
			if(teamSpaceNode.userContext!=des.teamSpaceNode.userContext) return false;
			if(dirId!=des.dirId) return false;
			
			return true;
		}
	};

	class CTeamspaceFileElementUI : public CShadeListContainerElement
	{
	public:
		void fillData(const FILE_DIR_INFO& fileNode, UserContext* userContext);

		void rename();

		std::wstring str_type;
		bool isNoThumb;
	};

	class TeamspaceListFileElement : public CTeamspaceFileElementUI
	{
	public:
		void initUI();

		bool flushThumb(const std::string& thumbKey);

		void initUI(const std::wstring& path);
	};

	class TeamspaceTileFileElement : public CTeamspaceFileElementUI
	{
	public:
		void initUI();

		bool flushThumb(const std::string& thumbKey);
	};

	struct TeamSpaceTileLayoutListContainerElement : public CListContainerElementUI
	{
	public:
		void DoEvent(TEventUI& event);
		UITeamSpaceNode nodeData;
	};

}
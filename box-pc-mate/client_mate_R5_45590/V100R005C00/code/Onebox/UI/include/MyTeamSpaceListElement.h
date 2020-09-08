#pragma once

#include "Common.h"
#include "Utility.h"
#include "TeamSpacesNode.h"
#include "ListContainerElement.h"

namespace Onebox
{
// contrl key
	const TCHAR* const TEAMSPACE_MODULE_NAME = _T("TeamSpace");
	const TCHAR* const TEAMSPACE_SEARCHLAYOUT = _T("teamSpace_searchLayout");
	const TCHAR* const TEAMSPACE_SEARCHTXT = _T("teamSpace_searchtxt");
	const TCHAR* const TEAMSPACE_FILELIST = _T("teamSpace_filelist");
	const TCHAR* const TEAMSPACE_LARGEICON = _T("teamSpace_largeIcon");
	const TCHAR* const TEAMSPACE_LISTLISTVIEW = _T("teamSpace_FilelistView");
	const TCHAR* const TEAMSPACE_LISTTAB = _T("teamSpace_listTab");
	const TCHAR* const TEAMSPACE_GROUPBTN = _T("teamSpace_groupBtn");
	const TCHAR* const TEAMSPACE_SELECTALL = _T("teamSpace_selectAll");
	const TCHAR* const TEAMSPACE_TILELAYOUT_LISTVIEW = _T("teamSpace_tileLayout_listView");	
	const TCHAR* const TEAMSPACE_LARGEICON_LAYOUTNAME = _T("teamSpace_largeIconItemVLayout");
	const TCHAR* const TEAMSPACE_LARGEICON_ICON = _T("teamSpace_nameIcon");
	const TCHAR* const TEAMSPACE_LARGEICON_NAME = _T("teamSpace_name");
//文件list
	const TCHAR* const TEAMSPACE_FILELISTITEM = _T("teamSpace_filelistItem");




	//用于团队空间首页
	class CTeamSpaceElementUI : public EditableContainerElement
	{
	public:
		UserContext* getTeamSPaceContext(UserContext* context);

		void fillData(const UserTeamSpaceNodeInfo& fillNode,const wstring iconpath =L"icon_video_large.png");
		UserContext* userContext; // the pointer of the userConetxt
		UserTeamSpaceNodeInfo teamspaceInfo;
		wstring iconPath_;

	};

	class TeamSpaceTileLayoutListContainerElement : public CTeamSpaceElementUI
	{
	public:
		void initUI();
			
	};

	//用于具体某团队空间的文件展示
	class TeamspaceListContainerElement : public EditableContainerElement
	{
		public:
		UIFileNode nodeData;

	};


}
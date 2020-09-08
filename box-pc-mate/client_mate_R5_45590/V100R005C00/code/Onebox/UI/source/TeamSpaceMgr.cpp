#include "stdafxOnebox.h"
#include "TeamSpaceMgr.h"
#include "ControlNames.h"
#include "Configure.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "ListContainerElement.h"
#include "ChildLayout.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "PathMgr.h"
#include "ListContainerElement.h"
#include "DialogBuilderCallbackImpl.h"
#include "SearchTxt.h"
#include "CustomListUI.h"
#include "TileLayoutListUI.h"
#include "TransTaskMgr.h"
#include "GroupButton.h"
#include "TeamSpacesNode.h"
#include "PageParam.h"
#include "TeamSpaceResMgr.h"
#include "NoticeFrame.h"
#include "IFolder.h"
#include "SyncFileSystemMgr.h"
#include "CommonDefine.h"
#include "UploadFrame.h"
#include "FileVersionDialog.h"
#include "ProxyMgr.h"
#include "SimpleNoticeFrame.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "OpenFileDbClick.h"
#include "FilterMgr.h"
#include "RestTaskMgr.h"
#include "ErrorConfMgr.h"
#include "TeamspaceElement.h"
#include "ConfigureMgr.h"
#include <boost/algorithm/string.hpp>
#include "CommonLoadingFrame.h"
#include "ShellCommonFileDialog.h"
#include "CommonFileDialog.h"
#include "CommonFileDialogLocalNotify.h"
#include "CommonFileDialogRemoteNotify.h"
#include "OpenFileDbClick.h"
#include "UIScaleIconButton.h"
#include "ShareLinkCountDialog.h"
#include "SelectDialog.h"
#include "DelayLoadingFrame.h"
#include <boost/thread.hpp>

using namespace SD::Utility;

#ifndef MODULE_NAME
#define MODULE_NAME ("TeamSpaceMgr")
#endif

#define SEARCH_SORT_FILED_TIME ("modifiedAt")
#define SORT_DIRECTION_ASC ("asc")
#define SORT_DIRECTION_DESC ("desc")

namespace Onebox
{
	#define TEAMSPACE_SCROLL_LIMEN (100)	//触发翻页的滚动条阈值	

	//右键菜单枚举与xml内id匹配
	enum TeamSpaceMenu
	{
		TSM_Open = 0,
		TSM_Share = 1,
		TSM_Download = 2,
		TSM_CopyMove = 3,
		TSM_Delete = 4,
		TSM_Rename = 5,
		TSM_Save= 6,
		TSM_Version = 7
	};

	class TeamSpaceMgrImpl : public TeamSpaceMgr
	{
	public:
		TeamSpaceMgrImpl(UserContext* context, CPaintManagerUI& paintManager)
			:userContext_(context)
			,paintManager_(paintManager)
			,teamspaceFileList_(NULL)
			,teamspaceSearchList_(NULL)
			,teamspaceTileLayoutListFile_(NULL)
			,teamspaceList_(NULL)
			,m_pHorizontalLevel1(NULL)
			,m_pHorizontalLevel2(NULL)
			,m_pHorizontalSwitch(NULL)
		{
			curCnt_ = 0;
			isNextPage_ = false;
			isRenaming_ = false;
			downloadFlag_ = false;
			m_pSelectWnd = NULL;
			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);

			teamspaceList_ = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(TEAMSPACE_TILELAYOUT_LISTVIEW));
			teamspaceTileLayoutListFile_ = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(L"teamSpace_tile_FilelistView"));
			teamspaceFileList_ = static_cast<CCustomListUI*>(paintManager_.FindControl(TEAMSPACE_LISTLISTVIEW));
			teamspaceSearchList_ = static_cast<CCustomListUI*>(paintManager_.FindControl(L"teamSpace_SearchlistView"));
			m_pHorizontalLevel1 = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"teamSpace_btngroup_level1"));
			m_pHorizontalLevel2 = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"teamSpace_btngroup_level2"));
			m_pHorizontalSwitch = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"teamSpace_switch"));

			if (teamspaceList_ && teamspaceList_->GetListInfo()){
				teamspaceList_->GetListInfo()->dwSelectedBkColor = 0xFFDEF0FF;
				teamspaceList_->GetListInfo()->dwHotBkColor = 0xFFEBEBEB;
				teamspaceList_->GetListInfo()->dwBkColor = 0xFFFAFAFA;
			}

			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(TEAMSPACE_FILESEARCHTXT));
			CDuiString defaultTxt = iniLanguageHelper.GetCommonString(MSG_MYSHARE_SEARCHDEFAULT_TEXT_KEY).c_str();
			if (NULL != searchtxt)
				searchtxt->setDefaultTxt(defaultTxt);

			m_pBtnClearSearch = static_cast<CButtonUI*>(paintManager_.FindControl(_T("teamSpace_clearsearchbtn")));

			m_noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());

			isLoading_ = false;
			delayLoading_ = userContext_->getConfigureMgr()->getConfigure()->delayLoading();
			isShowModal_ = false;

			funcMaps_.insert(std::make_pair(L"back_click", boost::bind(&TeamSpaceMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"next_click", boost::bind(&TeamSpaceMgrImpl::nextClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"update_click", boost::bind(&TeamSpaceMgrImpl::updateClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"List_listName_killfocus", boost::bind(&TeamSpaceMgrImpl::listNameReturn, this,_1)));
			funcMaps_.insert(std::make_pair(L"File_name_killfocus", boost::bind(&TeamSpaceMgrImpl::listNameReturn, this,_1)));
		
			funcMaps_.insert(std::make_pair(L"create_click", boost::bind(&TeamSpaceMgrImpl::createClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"detail_click", boost::bind(&TeamSpaceMgrImpl::lookClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"look_click", boost::bind(&TeamSpaceMgrImpl::lookClick, this, _1)));	
			funcMaps_.insert(std::make_pair(L"more_click", boost::bind(&TeamSpaceMgrImpl::moreClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"uploadFile_click", boost::bind(&TeamSpaceMgrImpl::uploadClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"shareLink_click", boost::bind(&TeamSpaceMgrImpl::fileShareLinkClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"downFile_click", boost::bind(&TeamSpaceMgrImpl::downFileClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"fileSearch_click", boost::bind(&TeamSpaceMgrImpl::fileSearchClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_itemDragMove", boost::bind(&TeamSpaceMgrImpl::itemDragMove, this,_1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_itemDragFile", boost::bind(&TeamSpaceMgrImpl::itemDragFile, this,_1)));
			funcMaps_.insert(std::make_pair(L"noFiles_itemDragFile", boost::bind(&TeamSpaceMgrImpl::itemDragFile, this,_1)));

			funcMaps_.insert(std::make_pair(L"listHeaderItemNameSortIcon_click", boost::bind(&TeamSpaceMgrImpl::nameHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemSizeSortIcon_click", boost::bind(&TeamSpaceMgrImpl::sizeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemCtimeSortIcon_click", boost::bind(&TeamSpaceMgrImpl::ctimeHeaderClick, this)));

			funcMaps_.insert(std::make_pair(L"listHeaderItemName_click", boost::bind(&TeamSpaceMgrImpl::nameHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemSize_click", boost::bind(&TeamSpaceMgrImpl::sizeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemCtime_click", boost::bind(&TeamSpaceMgrImpl::ctimeHeaderClick, this)));
			
			funcMaps_.insert(std::make_pair(L"listheaderitem_name_headerclick", boost::bind(&TeamSpaceMgrImpl::nameHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_size_headerclick", boost::bind(&TeamSpaceMgrImpl::sizeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_type_headerclick", boost::bind(&TeamSpaceMgrImpl::typeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_mtime_headerclick", boost::bind(&TeamSpaceMgrImpl::ctimeHeaderClick, this)));

			funcMaps_.insert(std::make_pair(L"FilelistView_itemDragMove", boost::bind(&TeamSpaceMgrImpl::itemDragMove, this,_1)));
			funcMaps_.insert(std::make_pair(L"FilelistView_itemDragFile", boost::bind(&TeamSpaceMgrImpl::itemDragFile, this,_1)));		
			funcMaps_.insert(std::make_pair(L"listItem_itemclick", boost::bind(&TeamSpaceMgrImpl::FilelistitemClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"File_largeIconItem_itemclick", boost::bind(&TeamSpaceMgrImpl::FilelistitemClick, this,_1)));

			funcMaps_.insert(std::make_pair(L"tileLayout_listView_selectchanged", boost::bind(&TeamSpaceMgrImpl::tileSelectItemChanged, this, _1)));
			funcMaps_.insert(std::make_pair(L"largeIconItem_itemdbclick", boost::bind(&TeamSpaceMgrImpl::iconItemdbclick, this, _1)));
			funcMaps_.insert(std::make_pair(L"filelist_selectchanged", boost::bind(&TeamSpaceMgrImpl::listSelectchanged, this,_1)));
			funcMaps_.insert(std::make_pair(L"largeIcon_selectchanged", boost::bind(&TeamSpaceMgrImpl::largeIconSelectchanged, this,_1)));
			funcMaps_.insert(std::make_pair(L"listItem_mouseenter", boost::bind(&TeamSpaceMgrImpl::listItemMouseEnter, this,_1)));
			funcMaps_.insert(std::make_pair(L"listItem_mouseleave", boost::bind(&TeamSpaceMgrImpl::listItemMouseLeave, this,_1)));
			funcMaps_.insert(std::make_pair(L"List_isShareLink_click", boost::bind(&TeamSpaceMgrImpl::fileShareLinkClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"largeIconItem_mouseenter", boost::bind(&TeamSpaceMgrImpl::largeIconItemMouseEnter, this,_1)));
			funcMaps_.insert(std::make_pair(L"largeIconItem_mouseleave", boost::bind(&TeamSpaceMgrImpl::largeIconItemMouseLeave, this,_1)));

			funcMaps_.insert(std::make_pair(L"FilelistView_selectItemChanged", boost::bind(&TeamSpaceMgrImpl::FileShowButton, this)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_selectItemChanged", boost::bind(&TeamSpaceMgrImpl::FileShowButton, this)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_selectItemChanged", boost::bind(&TeamSpaceMgrImpl::FileShowButton, this)));
			funcMaps_.insert(std::make_pair(L"FilelistView_selectchanged", boost::bind(&TeamSpaceMgrImpl::FileShowButton, this)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_selectchanged", boost::bind(&TeamSpaceMgrImpl::FileShowButton, this)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_selectchanged", boost::bind(&TeamSpaceMgrImpl::FileShowButton, this)));
			funcMaps_.insert(std::make_pair(L"listItem_itemdbclick", boost::bind(&TeamSpaceMgrImpl::listDbclick, this,_1)));
			funcMaps_.insert(std::make_pair(L"File_largeIconItem_itemdbclick", boost::bind(&TeamSpaceMgrImpl::listDbclick, this,_1)));
			funcMaps_.insert(std::make_pair(L"createDir_click", boost::bind(&TeamSpaceMgrImpl::createDirClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"List_version_click", boost::bind(&TeamSpaceMgrImpl::listVersionClick, this,_1)));

			funcMaps_.insert(std::make_pair(L"listItem_itemdelete", boost::bind(&TeamSpaceMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"listItem_itemselectall", boost::bind(&TeamSpaceMgrImpl::selectAllItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"FilelistView_itemselectall", boost::bind(&TeamSpaceMgrImpl::selectAllClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_itemselectall", boost::bind(&TeamSpaceMgrImpl::selectAllClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"File_largeIconItem_itemdelete", boost::bind(&TeamSpaceMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"File_largeIconItem_itemselectall", boost::bind(&TeamSpaceMgrImpl::selectAllItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"name_click", boost::bind(&TeamSpaceMgrImpl::TeamSpaceNameClick, this,_1)));

			funcMaps_.insert(std::make_pair(L"listItem_back", boost::bind(&TeamSpaceMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"File_largeIconItem_back", boost::bind(&TeamSpaceMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"FilelistView_back", boost::bind(&TeamSpaceMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_back", boost::bind(&TeamSpaceMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"noFiles_back", boost::bind(&TeamSpaceMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_back", boost::bind(&TeamSpaceMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"FilelistView_itemdelete", boost::bind(&TeamSpaceMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_itemdelete", boost::bind(&TeamSpaceMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"FilelistView_listselectall", boost::bind(&TeamSpaceMgrImpl::selectAllItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_listselectall", boost::bind(&TeamSpaceMgrImpl::selectAllItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"FilelistView_nextPage", boost::bind(&TeamSpaceMgrImpl::nextPage, this,_1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_nextPage", boost::bind(&TeamSpaceMgrImpl::nextPage, this,_1)));

			funcMaps_.insert(std::make_pair(L"fileSearchtxt_return", boost::bind(&TeamSpaceMgrImpl::fileSearchClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"List_path_click", boost::bind(&TeamSpaceMgrImpl::searchPathClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"FilelistView_menuadded", boost::bind(&TeamSpaceMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"FilelistView_menuitemclick", boost::bind(&TeamSpaceMgrImpl::menuItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_menuadded", boost::bind(&TeamSpaceMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"tile_FilelistView_menuitemclick", boost::bind(&TeamSpaceMgrImpl::menuItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_menuadded", boost::bind(&TeamSpaceMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_menuitemclick", boost::bind(&TeamSpaceMgrImpl::menuItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"more_menuadded", boost::bind(&TeamSpaceMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"more_menuitemclick", boost::bind(&TeamSpaceMgrImpl::menuItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"clearsearchbtn_click", boost::bind(&TeamSpaceMgrImpl::clearSearchBtnClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"fileSearchtxt_textchanged", boost::bind(&TeamSpaceMgrImpl::searchTextChanged, this, _1)));
		
			userContext_->getUserInfoMgr()->getCurUserInfo(m_storageUserInfo);
		}

		~TeamSpaceMgrImpl()
		{
			if (m_noticeFrame_)
			{
				delete m_noticeFrame_;
				m_noticeFrame_ = NULL;	
			}
			if (m_pSelectWnd)
			{
				m_pSelectWnd->Close();
				m_pSelectWnd = NULL;
			}
		}

		virtual void initData()
		{
			browseHistory_.clear();

			MyTeamSpaceFilesDirNode myFilDirNode;
			myFilDirNode.isListView = true;
			myFilDirNode.dirId = -2;
			myFilDirNode.teamSpaceNode.userContext = userContext_;
			myFilDirNode.dirName = iniLanguageHelper.GetCommonString(MSG_TEAMSPACE_BASENAME_KEY).c_str();
			browseHistory_.push_back(myFilDirNode);
			curPage_ = 0;
	
			loadTeamSpaceFileList(myFilDirNode,false);

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
			if (NULL != pControl)
			{
				pControl->SelectItem(0);
			}
			COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(L"teamSpace_filelist"));
			if (NULL != pOption)
			{
				pOption->Selected(true);
			}

			if (!m_pSelectWnd)
			{
				m_pSelectWnd = new SelectDialog;
			}
			if ( m_pSelectWnd)
			{
				POINT pt;
				pt.x = teamspaceFileList_->GetRelativePos().nMoveXPercent;
				pt.y = teamspaceFileList_->GetRelativePos().nMoveYPercent;
				m_pSelectWnd->Init(L"SelectDialog.xml", teamspaceFileList_->GetManager()->GetPaintWindow(), pt);
				m_pSelectWnd->SetWindowShow(false);

				teamspaceFileList_->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				if (teamspaceSearchList_)
				{
					teamspaceSearchList_->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}
				if (teamspaceList_)
				{
					teamspaceList_->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}
				if( teamspaceTileLayoutListFile_ )
				{
					teamspaceTileLayoutListFile_->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}
			}
		}

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg)
		{
			std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);			
			
			if(it!=funcMaps_.end())
			{
				it->second(msg);
			}

			if (funcName.substr(0,10) == L"groupMenu_")
			{
				if(msg.sType != DUI_MSGTYPE_CLICK && 
					msg.sType != DUI_MSGTYPE_MENUADDED && 
					msg.sType != DUI_MSGTYPE_MENUITEM_CLICK) return;
				CGroupButtonUI* pContainer = static_cast<CGroupButtonUI*>(paintManager_.FindControl(TEAMSPACE_GROUPBTN));
				if(NULL == pContainer) return;
				if (msg.sType == DUI_MSGTYPE_MENUITEM_CLICK) {
					PathNode node = pContainer->getPathNodeById(msg.lParam);
					MyTeamSpaceFilesDirNode fileNode;
					fileNode.dirId = node.fileId;
					if(-2==fileNode.dirId)
					{
						fileNode.teamSpaceNode.userContext = userContext_;
					}
					else
					{
						fileNode.teamSpaceNode.userContext = browseHistory_[curPage_].teamSpaceNode.userContext;
						Path filePath = fileNode.teamSpaceNode.userContext->getPathMgr()->makePath();
						filePath.id(fileNode.dirId);
						fileNode.teamSpaceNode.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
							userContext_->id.id, fileNode.filePermissions);
						fileNode.teamSpaceNode =  browseHistory_[curPage_].teamSpaceNode;
					}
					fileNode.isListView = browseHistory_[curPage_].isListView;
					for(int32_t i = curPage_; i>=0; --i)
					{
						if(browseHistory_[i].dirId==fileNode.dirId)
						{
							fileNode.dirName =  browseHistory_[i].dirName;
							break;
						}
					}
					loadTeamSpaceFileList(fileNode,false);
				}
				else pContainer->showMenu(_T("teamSpace"), msg);
			}

			if (funcName.substr(0,10) == L"groupMain_")
			{
				std::vector<std::wstring> vecInfo;  
				SD::Utility::String::split(funcName, vecInfo, L"_");
				if(vecInfo.size()<3) return;
				if(L"click"!=vecInfo[2]) return;

				SERVICE_INFO(MODULE_NAME, RT_OK, "executeFunc %s", SD::Utility::String::wstring_to_string(funcName).c_str());
				MyTeamSpaceFilesDirNode fileNode;
				fileNode.dirId = SD::Utility::String::string_to_type<int64_t>(vecInfo[1]);
				if(-2==fileNode.dirId)
				{
					fileNode.teamSpaceNode.userContext = userContext_;
				}
				else
				{
					fileNode.teamSpaceNode.userContext = browseHistory_[curPage_].teamSpaceNode.userContext;
					Path filePath = fileNode.teamSpaceNode.userContext->getPathMgr()->makePath();
					filePath.id(fileNode.dirId);
					fileNode.teamSpaceNode.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
						userContext_->id.id, fileNode.filePermissions);
					fileNode.teamSpaceNode =  browseHistory_[curPage_].teamSpaceNode;
				}
				fileNode.isListView = browseHistory_[curPage_].isListView;
				for(int32_t i = curPage_; i>=0; --i)
				{
					if(browseHistory_[i].dirId==fileNode.dirId)
					{
						fileNode.dirName =  browseHistory_[i].dirName;
						break;
					}
				}
				loadTeamSpaceFileList(fileNode,false);
			}
		}

		virtual void showPage(UserContext* userContext, int64_t pageId, int64_t selectId = -1)
		{
			MyTeamSpaceFilesDirNode fileDirNode;
			fileDirNode.isListView = true;
			fileDirNode.teamSpaceNode.userContext = userContext;
			fileDirNode.dirId = pageId;

			Path filePath = userContext->getPathMgr()->makePath();
			filePath.id(pageId);
			userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
				userContext_->id.id, fileDirNode.filePermissions);
			loadTeamSpaceFileList(fileDirNode,false);
					
			if (-1 == selectId || NULL == teamspaceFileList_) return;
			for (int i = 0; i<teamspaceFileList_->GetCount(); ++i)
			{
				TeamspaceListFileElement* item = static_cast<TeamspaceListFileElement*>(teamspaceFileList_->GetItemAt(i));
				if (item == NULL) continue;
				if (item->m_uNodeData.basic.id == selectId)
				{
					item->Select();
					break;
				}
			}
		}

		virtual void showPage(UserContext* userContext, int64_t pageId, std::wstring& selectFilename)
		{
			MyTeamSpaceFilesDirNode fileDirNode;
			fileDirNode.isListView = true;
			fileDirNode.teamSpaceNode.userContext = userContext;
			fileDirNode.dirId = pageId;

			Path filePath = userContext->getPathMgr()->makePath();
			filePath.id(pageId);
			userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
				userContext_->id.id, fileDirNode.filePermissions);
			loadTeamSpaceFileList(fileDirNode,false);

			if (L"" == selectFilename || NULL == teamspaceFileList_) return;
			for (int i = 0; i<teamspaceFileList_->GetCount(); ++i)
			{
				TeamspaceListFileElement* item = static_cast<TeamspaceListFileElement*>(teamspaceFileList_->GetItemAt(i));
				if (item == NULL) continue;
				if (item->m_uNodeData.basic.name == selectFilename)
				{
					item->Select();

					SIZE posscroll = teamspaceFileList_->GetScrollPos();

					posscroll.cy = item->GetFixedHeight() * i;
					teamspaceFileList_->SetScrollPos(posscroll);
					break;
				}
			}
		}

		virtual void enterTeamspace(int64_t selectTeamId)
		{
			MyTeamSpaceFilesDirNode myFilDirNode;
			myFilDirNode.isListView = true;
			myFilDirNode.dirId = -2;
			myFilDirNode.dirName = iniLanguageHelper.GetCommonString(MSG_TEAMSPACE_BASENAME_KEY).c_str();
			myFilDirNode.teamSpaceNode.userContext = userContext_;
			if(browseHistory_.empty())
			{
				browseHistory_.push_back(myFilDirNode);
				curPage_ = 0;
			}
			else if(!(browseHistory_[curPage_]==myFilDirNode))
			{
				browseHistory_.push_back(myFilDirNode);
				++curPage_;
			}
			loadTeamSpaceFileList(myFilDirNode,false);

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
			if (NULL != pControl)
			{
				pControl->SelectItem(0);
			}
			COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(L"teamSpace_filelist"));
			if (NULL != pOption)
			{
				pOption->Selected(true);
			}

			for (int i = 0; i<teamspaceList_->GetCount(); ++i)
			{
				TeamSpaceTileLayoutListContainerElement* item = static_cast<TeamSpaceTileLayoutListContainerElement*>(teamspaceList_->GetItemAt(i));
				if (item == NULL) continue;
				if(item->nodeData.basic.teamId()==selectTeamId)
				{
					selectTeamSpace_ = item->nodeData.basic;

					MyTeamSpaceFilesDirNode fileDirNode;
					fileDirNode.teamSpaceNode = item->nodeData;
					fileDirNode.isListView = true;
					fileDirNode.dirId = 0;
					fileDirNode.dirName=SD::Utility::String::utf8_to_wstring(item->nodeData.basic.member_.name());
					fileDirNode.teamSpaceNode.userContext->id.name = fileDirNode.dirName;
					Path filePath = item->nodeData.userContext->getPathMgr()->makePath();
					filePath.id(0);
					item->nodeData.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
							userContext_->id.id, fileDirNode.filePermissions);
		
					loadTeamSpaceFileList(fileDirNode,false);		

					CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(TEAMSPACE_LISTTAB));
					if (NULL != pControl)
					{
						pControl->SelectItem(1);
						showBtnByLevel(false);
					}
					COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(L"teamSpace_filelist"));
					if (NULL != pOption)
					{
						pOption->Selected(true);
					}
					FileShowButton();
					break;
				}
			}
		}

		virtual void reloadCache(int64_t ownerId, int64_t curId, bool isFlush = false)
		{
			if(isShowModal_)
			{
				return;
			}
			MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
			if(!fileDirNode.keyWord.empty()) return;
			if(-2==fileDirNode.dirId) return;
			if(fileDirNode.teamSpaceNode.userContext->id.id == ownerId && fileDirNode.dirId == curId)
			{
				loadMetaData(fileDirNode, isFlush);
			}
		}

		virtual bool isTheCurPage(int64_t ownerId, int64_t curId)
		{
			MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
			if(!fileDirNode.keyWord.empty()) return false;
			if(-2==fileDirNode.dirId) return false;
			if(fileDirNode.teamSpaceNode.userContext->id.id == ownerId && fileDirNode.dirId == curId)
			{
				return true;
			}
			return false;
		}

		virtual void reloadCache()
		{
			if(isShowModal_)
			{
				return;
			}
			loadMetaData(browseHistory_[curPage_]);
		}

		virtual void reloadAllCache(int64_t ownerId, int64_t curId, bool isFlush = false)
		{
			if(isShowModal_)
			{
				return;
			}
			MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
			if(-2==fileDirNode.dirId) return;
			if(fileDirNode.teamSpaceNode.userContext->id.id == ownerId && fileDirNode.dirId == curId)
			{
				loadMetaData(fileDirNode, isFlush);
			}
		}

		virtual bool reloadThumb(const std::string& thumbKey)
		{
			if(isShowModal_)
			{
				return false;
			}
			MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
			if (-2==fileDirNode.dirId) return false;

			if(browseHistory_[curPage_].isListView)
			{
				if(browseHistory_[curPage_].keyWord.empty())
				{
					for(int i = 0; i < teamspaceFileList_->GetCount(); ++i)
					{
						TeamspaceListFileElement* fileNode = static_cast<TeamspaceListFileElement*>(teamspaceFileList_->GetItemAt(i));
						if (NULL == fileNode)continue;
						if(fileNode->isNoThumb)
						{
							if(fileNode->flushThumb(thumbKey))
							{
								return true;
							}
						}
					}
				}
				else
				{
					for(int i = 0; i < teamspaceSearchList_->GetCount(); ++i)
					{
						TeamspaceListFileElement* fileNode = static_cast<TeamspaceListFileElement*>(teamspaceSearchList_->GetItemAt(i));
						if (NULL == fileNode)continue;
						if(fileNode->isNoThumb)
						{
							if(fileNode->flushThumb(thumbKey))
							{
								return true;
							}
						}
					}
				}
			}
			else
			{
				for(int i = 0; i < teamspaceTileLayoutListFile_->GetCount(); ++i)
				{
					TeamspaceTileFileElement* fileNode = static_cast<TeamspaceTileFileElement*>(teamspaceTileLayoutListFile_->GetItemAt(i));
					if (NULL == fileNode)continue;
					if(fileNode->isNoThumb)
					{
						if(fileNode->flushThumb(thumbKey))
						{
							return true;
						}
					}
				}
			}
			return false;
		}

		virtual void setPageFocus()
		{
			MyTeamSpaceFilesDirNode & currentfilenode = browseHistory_[curPage_];
		
			if( -2 == currentfilenode.dirId) return;
			if(currentfilenode.isListView)
			{
				if (currentfilenode.keyWord.empty()&&teamspaceFileList_)
				{
					teamspaceFileList_->SetFocus();
				}
				else if(teamspaceSearchList_)
				{
					teamspaceSearchList_->SetFocus();
				}
			}
			else
			{
				if (teamspaceTileLayoutListFile_)
				{
					teamspaceTileLayoutListFile_->SetFocus();
				}
			}
		}

		virtual void updateClick(TNotifyUI& msg);
	private:
		void backClick(TNotifyUI& msg);

		void nextClick(TNotifyUI& msg);

		void createClick(TNotifyUI& msg);

		void lookClick(TNotifyUI& msg);

		void quitClick(TNotifyUI& msg);

		void modifyAdminClick(TNotifyUI& msg);

		void moreClick(TNotifyUI& msg);

		void itemDragMove(TNotifyUI& msg);

		void nameHeaderClick()
		{
			HeaderClickSort(CMD_ROW_NAME);
		}

		void sizeHeaderClick()
		{
			HeaderClickSort(CMD_ROW_SIZE);
		}

		void typeHeaderClick()
		{
			HeaderClickSort(CMD_ROW_TYPESTR);
		}

		void ctimeHeaderClick()
		{
			HeaderClickSort(CMD_ROW_MTIME);
		}

		void HeaderClickSort(const std::string& name)
		{
			CButtonUI* pBtnName = static_cast<CButtonUI*>(paintManager_.FindControl(_T("teamSpace_listHeaderItemNameSortIcon")));
			CButtonUI* pBtnSize = static_cast<CButtonUI*>(paintManager_.FindControl(_T("teamSpace_listHeaderItemSizeSortIcon")));
			CButtonUI* pBtnCtime = static_cast<CButtonUI*>(paintManager_.FindControl(_T("teamSpace_listHeaderItemCtimeSortIcon")));
			if (NULL==pBtnName||NULL==pBtnSize||NULL==pBtnCtime) return;
			
			CButtonUI* pControl = NULL;
			if(CMD_ROW_NAME==name)
			{
				pControl = pBtnName;
			}
			else if(CMD_ROW_SIZE==name)
			{
				pControl = pBtnSize;
			}
			else
			{
				pControl = pBtnCtime;
			}

			OrderParam orderParam;
			orderParam.field = name;
			if (pControl->IsVisible() && (_tcsicmp(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,0,6,6'"),pControl->GetNormalImage()) == 0))
			{
				pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,0,6,6'"));
				pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,16,6,22'"));
				orderParam.direction = "desc";
			} 
			else
			{
				pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,0,6,6'"));
				pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,16,6,22'"));
				orderParam.direction = "asc";
			}

			m_oldOrderParam = orderParam;
			pBtnName->SetVisible(CMD_ROW_NAME==name);
			pBtnSize->SetVisible(CMD_ROW_SIZE==name);
			pBtnCtime->SetVisible(CMD_ROW_MTIME==name);

			curPageParam_.orderList.clear();
			curPageParam_.orderList.push_back(orderParam);
			loadMetaData(browseHistory_[curPage_]);
		}

		void listTeamSpace(UserTeamSpaceNodeInfoArray& teamspaceListArray)
		{
			PageParam pageparam;
			userContext_->getTeamSpaceMgr()->getTeamSpaceListUser(teamspaceListArray, pageparam);
			isLoading_ = false;
		}

		void loadTeamSpace()
		{
			setTeamSpaceFileListVisible();

			//需要记录一个MyTeamSpaceFilesDirNode  到面包屑数组
			if (teamspaceList_ == NULL) return;
			if (teamspaceList_->GetCount() > 0)
				teamspaceList_->RemoveAll();

			UserTeamSpaceNodeInfoArray teamspaceListArray;
			isLoading_ = true;
			loadThread_ = boost::thread(boost::bind(&TeamSpaceMgrImpl::listTeamSpace, this, boost::ref(teamspaceListArray)));
			int32_t timeCnt = 0;
			while(isLoading_ && timeCnt<delayLoading_)
			{
				boost::this_thread::sleep(boost::posix_time::milliseconds(10));
				timeCnt += 10;
			}
			if(isLoading_)
			{
				isShowModal_ = true;
				DelayLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(), teamspaceList_->GetPos(), loadThread_);
				isShowModal_ = false;
			}

			CVerticalLayoutUI*	pVertical	= static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(L"teamSpace_noTeamSpace"));
			if(NULL != pVertical) 
			{
				if(0 == teamspaceListArray.size())
				{		
					pVertical->SetVisible(true);
				}
				else
				{
					pVertical->SetVisible(false);
				}
			}
			for (size_t i = 0; i < teamspaceListArray.size(); ++i)
			{
				CDialogBuilder builder;
				TeamSpaceTileLayoutListContainerElement* node = static_cast<TeamSpaceTileLayoutListContainerElement*>(
					builder.Create(ControlNames::SKIN_XML_TEAMSPACE_TILELAYOUTITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;

				node->nodeData.basic = teamspaceListArray[i];
				node->nodeData.userContext = UserContextMgr::getInstance()->createUserContext(userContext_,
					teamspaceListArray[i].teamId(), UserContext_Teamspace, 
					String::utf8_to_wstring(teamspaceListArray[i].member_.name()));
            
				CLabelUI* teamsicon = static_cast<CLabelUI*>(node->FindSubControl(L"teamSpace_nameIcon"));
				if (teamsicon != NULL)
				{
					//teamsicon->SetBkImage(iconPath_.c_str());
				}

				CButtonUI* teamsname = static_cast<CButtonUI*>(node->FindSubControl( L"teamSpace_name"));
				if (NULL != teamsname)	
				{
					teamsname->SetText(SD::Utility::String::utf8_to_wstring(node->nodeData.basic.member_.name()).c_str());
					teamsname->SetToolTip(SD::Utility::String::utf8_to_wstring(node->nodeData.basic.member_.name()).c_str());
				}

				CLabelUI* teamsDes = static_cast<CLabelUI*>(node->FindSubControl(L"teamSpace_description"));
				if (teamsDes != NULL)
				{
					teamsDes->SetText(SD::Utility::String::utf8_to_wstring(node->nodeData.basic.member_.description()).c_str());
					teamsDes->SetToolTip(SD::Utility::String::utf8_to_wstring(node->nodeData.basic.member_.description()).c_str());
				}
				CLabelUI* teamsowner = static_cast<CLabelUI*>(node->FindSubControl(L"teamSpace_owner"));
				if (teamsowner != NULL)
				{
					std::wstring admin = iniLanguageHelper.GetCommonString(TEAMSPACE_AUTHER_KEY);
					admin += L":";
					admin += SD::Utility::String::utf8_to_wstring(node->nodeData.basic.member_.ownerByUserName());
					teamsowner->SetText(admin.c_str());
				}
				CLabelUI* teamsNum = static_cast<CLabelUI*>(node->FindSubControl(L"teamSpace_memNum"));
				if (teamsNum != NULL)
				{
					std::wstring num = iniLanguageHelper.GetCommonString(TEAMSPACE_MEMNUM_KEY);
					num += SD::Utility::String::type_to_string<std::wstring,int64_t>(node->nodeData.basic.member_.curNumbers());
					teamsNum->SetText(num.c_str());
				}

				if (!teamspaceList_->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void listPage(MyTeamSpaceFilesDirNode& fillnode, bool isFlush, LIST_FOLDER_RESULT& lfResult, std::map<int64_t, std::wstring>& pathInfo)
		{
			UserContext* teamspaceContext = fillnode.teamSpaceNode.userContext;
			if(fillnode.keyWord.empty())
			{
				Path listPath = teamspaceContext->getPathMgr()->makePath();
				listPath.id(fillnode.dirId);
				(void)ProxyMgr::getInstance(userContext_)->listPage(teamspaceContext, listPath, lfResult, curPageParam_, curCnt_, isFlush);
			}
			else
			{
				teamspaceContext->getShareResMgr()->search(SD::Utility::String::wstring_to_utf8(fillnode.keyWord), lfResult, curPageParam_, curCnt_,
					fillnode.isListView, pathInfo);
			}
			isLoading_ = false;
		}

		void loadMetaDataAsyc(MyTeamSpaceFilesDirNode& fillnode, bool isFlush = false)
		{
			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
			if((!browseHistory_[curPage_].isListView)&&browseHistory_[curPage_].keyWord.empty())
			{
				pageLimit_ = 2*pageLimit_;
			}
			curPageParam_.limit = pageLimit_;
			if(isNextPage_)
			{
				isNextPage_ = false;
			}
			else
			{
				curPageParam_.offset = 0;
			}
			curPageParam_.orderList.clear();

			CTextUI* cntText			= static_cast<CTextUI*>(paintManager_.FindControl(_T("teamSpace_count")));
			CCustomListUI* myNullList	= static_cast<CCustomListUI*>(paintManager_.FindControl(_T("teamSpace_noFiles")));
			if (NULL == cntText || NULL == myNullList) return;
			UserContext* teamspaceContext = fillnode.teamSpaceNode.userContext;
			LIST_FOLDER_RESULT lfResult;
			std::map<int64_t, std::wstring> pathInfo;

			if(fillnode.keyWord.empty())
			{
				curPageParam_.orderList.push_back(m_oldOrderParam);
			}
			else
			{
				OrderParam order;
				order.direction = SORT_DIRECTION_ASC;
				order.field		= SEARCH_SORT_FILED_TIME;
				myNullList->SetVisible(false);
				cntText->SetVisible(false);
				teamspaceFileList_->SetVisible(false);
				teamspaceSearchList_->SetVisible(false);
				teamspaceTileLayoutListFile_->SetVisible(false);
			}

			isLoading_ = true;
			loadThread_ = boost::thread(boost::bind(&TeamSpaceMgrImpl::listPage, this, fillnode, isFlush, boost::ref(lfResult), boost::ref(pathInfo)));
			int32_t timeCnt = 0;
			while(isLoading_ && timeCnt<delayLoading_)
			{
				boost::this_thread::sleep(boost::posix_time::milliseconds(10));
				timeCnt += 10;
			}
			if(isLoading_)
			{
				DelayLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(), teamspaceFileList_->GetPos(), loadThread_);
			}

			setTeamSpaceFileListVisible(!(0 == lfResult.size()));

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
			if (NULL == pControl) return;

			if(fillnode.isListView)
			{					
				if (!fillnode.keyWord.empty())
				{
					cntText->SetVisible(true);
					loadSearchResult(teamspaceContext, lfResult, pathInfo);
					pControl->SelectItem(2);
					teamspaceSearchList_->SetVisible(true);
				}
				else
				{
					loadFileList(teamspaceContext, lfResult);
					pControl->SelectItem(0);
					if (teamspaceFileList_)
					{
						teamspaceFileList_->SetVisible(true);
						teamspaceFileList_->SetFocus();
					}
				}
			}
			else
			{
				loadFileTile(teamspaceContext, lfResult);
				pControl->SelectItem(1);
				if (teamspaceTileLayoutListFile_)
				{
					teamspaceTileLayoutListFile_->SetVisible(true);
					teamspaceTileLayoutListFile_->SetFocus();
				}
			}
		}
		
		void loadTeamSpaceFileList(MyTeamSpaceFilesDirNode& currentfilenode, bool ishistory)
		{
			if(!(ishistory||browseHistory_[curPage_]==currentfilenode))
			{
				for(;curPage_<browseHistory_.size()-1;)
				{
					browseHistory_.erase(--browseHistory_.end());
				}
				browseHistory_.push_back(currentfilenode);
				++curPage_;
			}

			if (-2==currentfilenode.dirId)
			{
				loadTeamSpace();
				CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(TEAMSPACE_LISTTAB));
				pControl->SelectItem(0);
			}
			else
			{
				selectedItems_.clear();
				loadMetaDataAsyc(currentfilenode);
			}

			//reset path
			reShowPath(currentfilenode);
			FileShowButton();

			//reset tab
			if (-2==currentfilenode.dirId)
			{
				CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(TEAMSPACE_LISTTAB));
				pControl->SelectItem(0);
				showBtnByLevel(true);
			}
			else
			{
				CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(TEAMSPACE_LISTTAB));
				if(currentfilenode.isListView)
				{
					COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(TEAMSPACE_FILELIST));
					pOption->Selected(true);
					pControl->SelectItem(1);
					showBtnByLevel(false);
				}
				else
				{
					COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(TEAMSPACE_LARGEICON));
					pOption->Selected(true);
					pControl->SelectItem(2);
				}
				//reset searchtxt
// 				CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(TEAMSPACE_FILESEARCHTXT));
// 				searchtxt->resetText(currentfilenode.keyWord);
			}
		}

		void loadFileList(UserContext* userContext, const LIST_FOLDER_RESULT& lfResult)
		{
			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
			pControl->SelectItem(0);
			if (teamspaceFileList_ == NULL) return;

			if (teamspaceFileList_->GetCount() > 0  && 0==curPageParam_.offset)
				teamspaceFileList_->RemoveAll();

			bool isDragable = true;
			if (browseHistory_[curPage_].teamSpaceNode.basic.teamRole() == "member" && browseHistory_[curPage_].teamSpaceNode.basic.role() == "viewer" )
			{
				isDragable = false;
			}
			teamspaceFileList_->setDragable(isDragable);

			for (LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				CDialogBuilder builder;
				TeamspaceListFileElement* node = static_cast<TeamspaceListFileElement*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_TEAMSPACE_FILELISTITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_));
				if (NULL == node) continue;

				File_Permissions perm = browseHistory_[curPage_].filePermissions;
		
				if(perm & FP_EDIT)
				{		
					node->setRenameFlag(true);
				}
				else
				{
					node->setRenameFlag(false);
				}

				node->fillData(*it, userContext);
				node->initUI();

				if (!teamspaceFileList_->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void loadSearchResult(UserContext* userContext, const LIST_FOLDER_RESULT& lfResult, std::map<int64_t, std::wstring>& pathInfo)
		{
			if (NULL == teamspaceSearchList_)	return;
			if (teamspaceSearchList_->GetCount() > 0  && 0==curPageParam_.offset)
				teamspaceSearchList_->RemoveAll();

			for (LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				CDialogBuilder builder;
				TeamspaceListFileElement* node = static_cast<TeamspaceListFileElement*>(
					builder.Create(L"myTeamSpaceSearchItem.xml", L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_));
				if (NULL == node)	continue;

				node->fillData(*it, userContext);
				std::wstring path;
				std::map<int64_t, std::wstring>::iterator pathIt = pathInfo.find(it->id);
				if(pathIt!=pathInfo.end())
				{
					path = pathIt->second;
				}
				else
				{
					userContext->getShareResMgr()->getFilePath(it->id, path);
				}
				node->initUI(path);

				if (!teamspaceSearchList_->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void loadFileTile(UserContext* userContext, const LIST_FOLDER_RESULT& lfResult)
		{
			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
			pControl->SelectItem(1);

			if (teamspaceTileLayoutListFile_ == NULL) return;

			if (teamspaceTileLayoutListFile_->GetCount() > 0  && 0==curPageParam_.offset)
				teamspaceTileLayoutListFile_->RemoveAll();

			bool isDragable = true;
			if (browseHistory_[curPage_].teamSpaceNode.basic.teamRole() == "member" && browseHistory_[curPage_].teamSpaceNode.basic.role() == "viewer" )
			{
				isDragable = false;
			}
			teamspaceTileLayoutListFile_->setDragable(isDragable);

			for (LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				CDialogBuilder builder;
				TeamspaceTileFileElement* node = static_cast<TeamspaceTileFileElement*>(
					builder.Create(TEAMSPACE_FILETILELAYOUTLIST_ITEM_XML, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_));
				if (NULL == node) continue;

				node->fillData(*it, userContext);
				node->initUI();

				if (!teamspaceTileLayoutListFile_->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void loadMetaData(MyTeamSpaceFilesDirNode& fillnode, bool isFlush = false)
		{
			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
			if((!browseHistory_[curPage_].isListView)&&browseHistory_[curPage_].keyWord.empty())
			{
				pageLimit_ = 2*pageLimit_;
			}
			curPageParam_.limit = pageLimit_;
			if(isNextPage_)
			{
				isNextPage_ = false;
			}
			else
			{
				curPageParam_.offset = 0;
			}
			curPageParam_.orderList.clear();

			CTextUI* cntText			= static_cast<CTextUI*>(paintManager_.FindControl(_T("teamSpace_count")));
			CCustomListUI* myNullList	= static_cast<CCustomListUI*>(paintManager_.FindControl(_T("teamSpace_noFiles")));
			if (NULL == cntText || NULL == myNullList) return;
			UserContext* teamspaceContext = fillnode.teamSpaceNode.userContext;
			Path listPath = teamspaceContext->getPathMgr()->makePath();
			listPath.id(fillnode.dirId);
			LIST_FOLDER_RESULT lfResult;
			std::map<int64_t, std::wstring> pathInfo;
			if(fillnode.keyWord.empty())
			{
				curPageParam_.orderList.push_back(m_oldOrderParam);
				(void)ProxyMgr::getInstance(userContext_)->listPage(teamspaceContext, listPath, lfResult, curPageParam_, curCnt_, isFlush);
			}
			else
			{
				OrderParam order;
				order.direction = SORT_DIRECTION_ASC;
				order.field		= SEARCH_SORT_FILED_TIME;
				myNullList->SetVisible(false);
				cntText->SetVisible(false);
				teamspaceFileList_->SetVisible(false);
				teamspaceSearchList_->SetVisible(false);
				teamspaceTileLayoutListFile_->SetVisible(false);
				CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(),paintManager_.GetPaintWindow(),teamspaceFileList_->GetPos(),boost::bind(
					&ShareResMgr::search, 
					teamspaceContext->getShareResMgr(), 
					SD::Utility::String::wstring_to_utf8(fillnode.keyWord), 
					boost::ref(lfResult), 
					curPageParam_, 
					boost::ref(curCnt_), 
					fillnode.isListView,
					boost::ref(pathInfo)));
			}

			setTeamSpaceFileListVisible(!(0 == lfResult.size()));

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
			if (NULL == pControl) return;

			if(fillnode.isListView)
			{					
				if (!fillnode.keyWord.empty())
				{
					cntText->SetVisible(true);
					loadSearchResult(teamspaceContext, lfResult, pathInfo);
					pControl->SelectItem(2);
					teamspaceSearchList_->SetVisible(true);
				}
				else
				{
					loadFileList(teamspaceContext, lfResult);
					pControl->SelectItem(0);
					if (teamspaceFileList_)
					{
						teamspaceFileList_->SetVisible(true);
						teamspaceFileList_->SetFocus();
					}
				}
			}
			else
			{
				loadFileTile(teamspaceContext, lfResult);
				pControl->SelectItem(1);
				if (teamspaceTileLayoutListFile_)
				{
					teamspaceTileLayoutListFile_->SetVisible(true);
					teamspaceTileLayoutListFile_->SetFocus();
				}
			}
		}

		void setTeamSpaceFileListVisible(bool bVisible = true)
		{
			CCustomListUI*			pNoFiles	= static_cast<CCustomListUI*>(paintManager_.FindControl(L"teamSpace_noFiles"));
			CHorizontalLayoutUI*	pHorizontal = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"teamSpace_Files"));
			if(NULL == pNoFiles || NULL == pHorizontal) return;
			if(!bVisible)
			{			
				pNoFiles->SetVisible(true);
				pNoFiles->SetFocus();
				pHorizontal->SetVisible(false);
			}
			else
			{
				pNoFiles->SetVisible(false);
				pHorizontal->SetVisible(true);
			}
		}
		
		void itemDragFile(TNotifyUI& msg);

		void tileSelectItemChanged(TNotifyUI& msg);

		void iconItemdbclick(TNotifyUI& msg);

		void FilelistitemClick(TNotifyUI& msg);

		void listSelectchanged(TNotifyUI& msg);

		void largeIconSelectchanged(TNotifyUI& msg);

		void listItemMouseEnter(TNotifyUI& msg);

		void listItemMouseLeave(TNotifyUI& msg);

		void fileShareLinkClick(TNotifyUI& msg);

		void TeamSpaceNameClick(TNotifyUI& msg);

		void largeIconItemMouseEnter(TNotifyUI& msg);

		void largeIconItemMouseLeave(TNotifyUI& msg);

		void downFileClick(TNotifyUI& msg);
		
		void fileSearchClick(TNotifyUI& msg);

		void listDbclick(TNotifyUI& msg)
		{
			CTeamspaceFileElementUI* pFileListItem = NULL;
			if(browseHistory_[curPage_].isListView)
			{
				if(browseHistory_[curPage_].keyWord.empty())
				{
					pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceFileList_->GetItemAt(teamspaceFileList_->GetCurSel()));
				}
				else
				{
					pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceSearchList_->GetItemAt(teamspaceSearchList_->GetCurSel()));
				}
			}
			else
			{
				pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceTileLayoutListFile_->GetItemAt(teamspaceTileLayoutListFile_->GetCurSel()));
			}
			if (pFileListItem == NULL)  return;
			if (FILE_TYPE::FILE_TYPE_DIR == pFileListItem->m_uNodeData.basic.type)
			{
				//open folder
				MyTeamSpaceFilesDirNode fileDirNode;
				fileDirNode.isListView = browseHistory_[curPage_].isListView;
				fileDirNode.teamSpaceNode = browseHistory_[curPage_].teamSpaceNode;
				fileDirNode.dirId = pFileListItem->m_uNodeData.basic.id;
				fileDirNode.dirName = pFileListItem->m_uNodeData.basic.name;
				Path filePath = pFileListItem->m_uNodeData.userContext->getPathMgr()->makePath();
				filePath.id(pFileListItem->m_uNodeData.basic.id);
				pFileListItem->m_uNodeData.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
					userContext_->id.id, fileDirNode.filePermissions);
				loadTeamSpaceFileList(fileDirNode,false);
			}	
			else if (browseHistory_[curPage_].filePermissions& FP_DOWNLOAD)
			{
				bool isOpen = dbClickEnabled(pFileListItem->m_uNodeData.basic.name);
				OpenFileDbClickDialog* pDialog = new OpenFileDbClickDialog(userContext_,pFileListItem->m_uNodeData,isOpen);
				pDialog->Create(paintManager_.GetPaintWindow(),_T("OpenFileDbClickDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
				pDialog->CenterWindow();
				isShowModal_ = true;
				UINT tmp = pDialog->ShowModal();
				isShowModal_ = false;
				delete pDialog;
				pDialog=NULL;
				if (tmp == 1)  //open
				{
					std::wstring path;
					WCHAR buf[255] = {0};
					GetTempPath(255,buf);
					path = buf;
					path += L"oneBoxCache_";				
					time_t tm = time(NULL);
					path += SD::Utility::String::type_to_string<std::wstring>(tm);
					SD::Utility::FS::create_directory(path);

					std::list<UIFileNode> itemList;
					itemList.push_back(pFileListItem->m_uNodeData);
					(void)TransTaskMgr::download(itemList, path, true);
				}
				else if (tmp == 2) // save
				{
					std::map<int64_t, UIFileNode> items;
					items.insert(std::make_pair(pFileListItem->m_uNodeData.basic.id, pFileListItem->m_uNodeData));
					download(items);
				}
			}
		}

		void createDirClick(TNotifyUI& msg);

		void uploadClick(TNotifyUI& msg);

		void listVersionClick(TNotifyUI& msg);

		void FileShowButton();

		void showButton();
		
		void listNameReturn(TNotifyUI& msg);

		bool checkFileIsRename(std::map<int64_t, UIFileNode>& sourceList, int64_t& destId, UserContext* userContext)
		{
			Path listPath = userContext->getPathMgr()->makePath();
			listPath.id(destId);
			LIST_FOLDER_RESULT lfResult;
			ProxyMgr::getInstance(userContext)->listAll(userContext, listPath, lfResult);
			for (LIST_FOLDER_RESULT::iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				for (std::map<int64_t, UIFileNode>::iterator itor = sourceList.begin();itor != sourceList.end();itor++)
				{
					if (itor->second.basic.name == it->name)
					{
						return true;
					}
				}
			}
			return false;
		}

		void selectAllItemClick(TNotifyUI& msg)
		{
			MyTeamSpaceFilesDirNode curDirNode = browseHistory_[curPage_];
			if (curDirNode.isListView)
			{
				if(curDirNode.keyWord.empty())
				{
					if (NULL == teamspaceFileList_) return;
					teamspaceFileList_->SelectAllItem(true);
				}
				else
				{
					if (NULL == teamspaceSearchList_) return;
					teamspaceSearchList_->SelectAllItem(true);
				}
			}
			else
			{
				if (NULL == teamspaceTileLayoutListFile_) return;
				teamspaceTileLayoutListFile_->SelectAllItem(true);
			}
			msg.wParam = true;
			if(curDirNode.keyWord.empty())
			{
				selectAllClick(msg);
			}
		}

		void selectAllClick(TNotifyUI& msg)
		{
			selectedItems_.clear();
			if(msg.wParam==0) return;
			MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];

			UserContext* teamspaceContext = fileDirNode.teamSpaceNode.userContext;
			//选中全页对象
			Path listPath = teamspaceContext->getPathMgr()->makePath();
			listPath.id(fileDirNode.dirId);
			LIST_FOLDER_RESULT lfResult;
			ProxyMgr::getInstance(userContext_)->listAll(teamspaceContext, listPath, lfResult);
			for(LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				UIFileNode fileNode;
				fileNode.basic = *it;
				fileNode.userContext = userContext_;
				selectedItems_.insert(std::make_pair(fileNode.basic.id, fileNode));
			}		
		}

		void deleteAllClick();

		void reShowPath(MyTeamSpaceFilesDirNode& currentfilenode);
		
	private:
		void getUIFileNode()
		{
			selectedItems_.clear();
			if(browseHistory_[curPage_].isListView)
			{
				CCustomListUI* myFile = browseHistory_[curPage_].keyWord.empty()?teamspaceFileList_:teamspaceSearchList_;
				if(NULL == myFile) return;
				CStdValArray* curSelects = myFile->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CTeamspaceFileElementUI* element = static_cast<CTeamspaceFileElementUI*>(myFile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;
					UIFileNode fileNode;
					fileNode = element->m_uNodeData;
					if (-1 == fileNode.basic.id)continue;					
					selectedItems_.insert(std::make_pair(fileNode.basic.id, fileNode));
				}
			}
			else
			{
				if(NULL == teamspaceTileLayoutListFile_) return;
				CStdValArray* curSelects = teamspaceTileLayoutListFile_->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CTeamspaceFileElementUI* element = static_cast<CTeamspaceFileElementUI*>(teamspaceTileLayoutListFile_->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;
					UIFileNode fileNode;
					fileNode = element->m_uNodeData;
					if (-1 == fileNode.basic.id) continue;
					selectedItems_.insert(std::make_pair(fileNode.basic.id, fileNode));
				}
			}
		}

		void download(std::map<int64_t, UIFileNode>& items)
		{
			if (items.empty())
			{
				return;
			}
			if (true == downloadFlag_)
			{
				return;
			}

			std::wstring defaultValue = L"";
			std::wstring strDownloadDir = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_DOWNLOADPATH_KEY, defaultValue);
			if ( strDownloadDir.empty() )
			{
				ShellCommonFileDialogParam param;
				param.type = OpenAFolder;
				std::auto_ptr<ShellCommonFileDialog> fileDialog(new ShellCommonFileDialog(param));
				ShellCommonFileDialogResult result;
				downloadFlag_ = true;
				bool openresult = fileDialog->getResults(result);
				downloadFlag_ = false;
				if ( !openresult ) return;

				if( 1 != result.size() || !SD::Utility::FS::is_exist( *(result.begin()) ) )
				{
					if (m_noticeFrame_)
					{
						m_noticeFrame_->Run(Confirm, Warning, MSG_SETTING_SAVETITLE_KEY ,MSG_SETSYNCDIR_INVALIDPATH_KEY, Modal);
					}
					return ;
				}

				strDownloadDir = *(result.begin());
			}

			std::list<UIFileNode> itemList;
			for(std::map<int64_t, UIFileNode>::const_iterator it = items.begin(); it != items.end(); ++it)
			{
				itemList.push_back(it->second);
			}		
			(void)TransTaskMgr::download(itemList, strDownloadDir);	
		}

		void addRestTask(UserContext* teamspaceContext, const std::list<int64_t>& srcNodeList, int64_t destOwnerId, int64_t destFolderId, const std::string& type, bool autoRename = false)
		{
			int32_t ret = teamspaceContext->getRestTaskMgr()->addRestTask(teamspaceContext->getUserInfoMgr()->getUserId(), 
				browseHistory_[curPage_].dirId, srcNodeList, destOwnerId, destFolderId, type, autoRename);
			if(RT_RESTTASK_DOING==ret)
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				simlpeNoticeFrame->Show(Error, MSG_RESTTASK_START_DOING, 
					simlpeNoticeFrame->GetShowMsg(SD::Utility::String::string_to_wstring(teamspaceContext->getRestTaskMgr()->getLastType())).c_str());
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
			else if(RT_OK!=ret)
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				simlpeNoticeFrame->Show(Error, MSG_RESTTASK_ERROR_KEY, 
					simlpeNoticeFrame->GetShowMsg(SD::Utility::String::string_to_wstring(type)).c_str(), 
					ErrorConfMgr::getInstance()->getDescription(ret).c_str());
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
		}

		void searchPathClick(TNotifyUI& msg)
		{
			CButtonUI* pBtn = static_cast<CButtonUI*>(msg.pSender);		
			if (NULL == pBtn) return;
			TeamspaceListFileElement* node = reinterpret_cast<Onebox::TeamspaceListFileElement*>(pBtn->GetTag());		
			showPage(node->m_uNodeData.userContext, node->m_uNodeData.basic.parent, node->m_uNodeData.basic.id);
		}

		void nextPage(TNotifyUI& msg)
		{
			int32_t range = msg.wParam;
			int32_t pos = msg.lParam;

			//无下一页，或未滚到倒数SCROLL_LIMEN条以下，不处理
			if((!browseHistory_[curPage_].keyWord.empty())
				||(curPageParam_.offset + curPageParam_.limit >= curCnt_) 
				||(pos < range - TEAMSPACE_SCROLL_LIMEN)) return;

			curPageParam_.offset += pageLimit_;
			isNextPage_ = true;
			loadMetaData(browseHistory_[curPage_]);
		}

		void showBtnByLevel(bool bLevel1 = true)
		{
			CButtonUI* pDetail = static_cast<CButtonUI*>(paintManager_.FindControl(L"teamSpace_detail"));
			if (m_pHorizontalLevel1 && m_pHorizontalLevel2)
			{
				m_pHorizontalLevel1->SetVisible(bLevel1);
				m_pHorizontalLevel2->SetVisible(!bLevel1);
				pDetail->SetVisible(!bLevel1);
			}

			if (m_pHorizontalSwitch)
			{				
				m_pHorizontalSwitch->SetVisible(true);
			}

			COptionUI* pOptionList = static_cast<COptionUI*>(paintManager_.FindControl(TEAMSPACE_FILELIST));
			COptionUI* pOptionIcon = static_cast<COptionUI*>(paintManager_.FindControl(TEAMSPACE_LARGEICON));
			CLabelUI* pLabelDevide = static_cast<CLabelUI*>(paintManager_.FindControl(TEAMSPACE_DEVIDE));
			CHorizontalLayoutUI* pSearch = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"teamSpace_fileSearchLayout"));
			
			if(NULL == pOptionList || NULL == pOptionIcon || NULL == pLabelDevide || NULL == pSearch)
			{
				return;
			}
			
			if(bLevel1)
			{				
				pOptionList->SetVisible(false);
				pOptionIcon->SetVisible(false);
				pLabelDevide->SetVisible(false);
				pSearch->SetVisible(false);
			}
			else
			{
				pOptionList->SetVisible(true);
				pOptionIcon->SetVisible(true);
				pLabelDevide->SetVisible(true);
				pSearch->SetVisible(true);
			}
		}

		void menuAdded(TNotifyUI& msg)
		{
			if (NULL == msg.pSender) return;

			CMenuUI* pMenu = (CMenuUI*)msg.wParam;
			if (NULL == pMenu) return;

			if(0 == selectedItems_.size()){
				pMenu->SetVisible(false);
				return;
			}
			else
			{
				pMenu->SetVisible(true);
			}

			//"more menu" never display "open" "download"
			std::wstring tempItemName = msg.pSender->GetName();
			if(L"teamSpace_more" == tempItemName)
			{
				RECT rcMenuPadding  = {0};
				RECT rcTrianglePadding = {0};
				if((int32_t)UI_LANGUGE::CHINESE == iniLanguageHelper.GetLanguage())
				{
					rcMenuPadding.left		=	-30;
					rcMenuPadding.top		=	32;
					rcMenuPadding.right		=	0;
					rcMenuPadding.bottom	=	0;

					rcTrianglePadding.left		=	0;
					rcTrianglePadding.top		=	25;
					rcTrianglePadding.right		=	-42;
					rcTrianglePadding.bottom	=	0;
				}
				else
				{
					rcMenuPadding.left		=	-30;
					rcMenuPadding.top		=	32;
					rcMenuPadding.right		=	0;
					rcMenuPadding.bottom	=	0;

					rcTrianglePadding.left		=	0;
					rcTrianglePadding.top		=	25;
					rcTrianglePadding.right		=	-63;
					rcTrianglePadding.bottom	=	0;
				}
				
				pMenu->SetMenuPadding(rcMenuPadding);
				pMenu->SetMenuPaddingEnabled(true);		
				pMenu->SetTrianglePadding(rcTrianglePadding);
				pMenu->SetTriangleEnabled(true);

				pMenu->hiddenMenuItemById(TSM_Open);
				pMenu->hiddenMenuItemById(TSM_Download);
			}
			else
			{
				doMenuPoint(pMenu, msg.ptMouse);
			}
				
			// selected none
			if (selectedItems_.empty())
			{
				pMenu->SetVisible(false);
				return;
			}
			File_Permissions perm = browseHistory_[curPage_].filePermissions;
			// selected multi items
			if (selectedItems_.size() > 1)
			{
				pMenu->hiddenMenuItemById(TSM_Open);
				pMenu->hiddenMenuItemById(TSM_Share);
				pMenu->hiddenMenuItemById(TSM_Rename);
				pMenu->hiddenMenuItemById(TSM_Version);
			}
			// selected a item
			else 
			{
				UIFileNode fileNode = selectedItems_.begin()->second;
				// file do not have open
				if (fileNode.basic.type == FILE_TYPE_FILE)
				{
					pMenu->hiddenMenuItemById(TSM_Open);
				}
				// folder do not have version menu
				else 
				{
					pMenu->hiddenMenuItemById(TSM_Version);
				}

				if (!(perm & FP_PUBLISHLINK))
				{
					pMenu->hiddenMenuItemById(TSM_Share);
				}
				if (!(perm & FP_EDIT))
				{
					pMenu->hiddenMenuItemById(TSM_Rename);
				}
			}

			if (!(perm & FP_DOWNLOAD))
			{
				pMenu->hiddenMenuItemById(TSM_Download);
				pMenu->hiddenMenuItemById(TSM_Save);
			}
			if (!(perm & FP_EDIT))
			{
				pMenu->hiddenMenuItemById(TSM_CopyMove);
			}
			if (!(perm & FP_DELETE))
			{
				pMenu->hiddenMenuItemById(TSM_Delete);
			}
		}

		void menuItemClick(TNotifyUI& msg)
		{
			int menuId = msg.lParam;
			if (menuId < 0)
			{
				return;
			}
			switch (menuId)
			{
			case TSM_Open:
				listDbclick(msg);
				break;
			case TSM_Share:
				fileShareLinkClick(msg);
				break;
			case TSM_Download:
				download(selectedItems_);
				break;
			case TSM_CopyMove:
				{
					std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
					std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_copymove").c_str();
					UserContext* teamspaceContext = browseHistory_[curPage_].teamSpaceNode.userContext;

					CommonFileDialogPtr myFileDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
					TeamspaceCommonFileDialogNotify *notify = new TeamspaceCommonFileDialogNotify(teamspaceContext, iniLanguageHelper.GetLanguage(), TRFDT_TEAMSPACE);			
					myFileDialog->setOption(CFDO_only_show_folder);
					myFileDialog->setNotify(notify);
					isShowModal_ = true;
					if (E_CFD_CANCEL == myFileDialog->showModal(resultHanlder,COMMFILEDIALOG_COPYMOVE))
					{
						isShowModal_ = false;
						return;
					}
					isShowModal_ = false;

					int iControlIndex = myFileDialog->GetControlIndex();

					TeamspaceCommonFileDialogData *data = (TeamspaceCommonFileDialogData*)(*(commonFileData.begin()))->data.get();

					if (1==iControlIndex && selectedItems_.begin()->second.basic.parent == data->id)
					{
						SimpleNoticeFrame* psimlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						psimlpeNoticeFrame->Show(Warning, MSG_SAVEFILE_COPYMOVE_FAIL_KEY);
						delete psimlpeNoticeFrame;
						psimlpeNoticeFrame = NULL;
						return;
					}

					int64_t destFolderId = data->id;
					if (checkFileIsRename(selectedItems_, destFolderId, teamspaceContext))
					{
						m_noticeFrame_->Run(Choose, Warning, MSG_SAVEFILE_COPYMOVE_TITLE_KEY, MSG_SAVEFILE_COPYMOVE_TEXT_KEY, Modal);
						if(!m_noticeFrame_->IsClickOk()) return;
					}
					std::list<int64_t> srcNodeList;
					for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
					{
						srcNodeList.push_back(it->first);
					}
					std::string type = (0==iControlIndex)?RESTTASK_COPY:RESTTASK_MOVE;
					addRestTask(teamspaceContext, srcNodeList, teamspaceContext->getUserInfoMgr()->getUserId(), destFolderId, type, true);

					if (1 == iControlIndex)
					{
						TNotifyUI tnui;
						updateClick(tnui);
					}
				}
				break;
			case TSM_Delete:
				{
					m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
					if (!m_noticeFrame_->IsClickOk()) return;

					std::list<int64_t> srcNodeList;
					for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
					{
						srcNodeList.push_back(it->first);
					}
					addRestTask(browseHistory_[curPage_].teamSpaceNode.userContext, srcNodeList, 0, 0, RESTTASK_DELETE);
				}
				break;
			case TSM_Rename:
				{
					CTeamspaceFileElementUI* pFileListItem = NULL;
					if (selectedItems_.size() == 1)
					{
						if (browseHistory_[curPage_].isListView)
						{
							if(browseHistory_[curPage_].keyWord.empty())
							{
								if (NULL == teamspaceFileList_) return;
								pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceFileList_->GetItemAt(teamspaceFileList_->GetCurSel()));
							}
							else
							{
								if (NULL == teamspaceSearchList_) return;
								pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceSearchList_->GetItemAt(teamspaceSearchList_->GetCurSel()));
							}
						}
						else
						{
							if (NULL == teamspaceTileLayoutListFile_) return;
							pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceTileLayoutListFile_->GetItemAt(teamspaceTileLayoutListFile_->GetCurSel()));
						}
						if (NULL == pFileListItem) return;
						pFileListItem->rename();
					}
				}
				break;
			case TSM_Save:
				{
					std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
					std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_savetoonebox").c_str();

					CommonFileDialogPtr teamspaceDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
					MyFileCommonFileDialogNotify *notify = new SaveToMyFileDialogNotify(userContext_, iniLanguageHelper.GetLanguage());			
					teamspaceDialog->setOption(CFDO_only_show_folder);
					teamspaceDialog->setNotify(notify);
					isShowModal_ = true;
					if (E_CFD_CANCEL == teamspaceDialog->showModal(resultHanlder,COMMFILEDIALOG_SAVETOMYFILE))
					{		
						isShowModal_ = false;
						return;
					}
					isShowModal_ = false;

					MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)(*(commonFileData.begin()))->data.get();

					int64_t destFolderId = data->id;
					if (checkFileIsRename(selectedItems_, destFolderId, userContext_))
					{
						m_noticeFrame_->Run(Choose, Warning, MSG_SAVEFILE_COPYMOVE_TITLE_KEY, MSG_SAVEFILE_COPYMOVE_TEXT_KEY, Modal);
						if(!m_noticeFrame_->IsClickOk()) return;
					}	
					std::list<int64_t> srcNodeList;
					for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
					{
						srcNodeList.push_back(it->first);
					}
					addRestTask(browseHistory_[curPage_].teamSpaceNode.userContext, srcNodeList, 
						userContext_->getUserInfoMgr()->getUserId(), destFolderId, RESTTASK_SAVE, true);
				}
				break;
			case TSM_Version:
				{
					if(1!=selectedItems_.size()) return;
					FileVersionDialog *pVersion = new FileVersionDialog(browseHistory_[curPage_].teamSpaceNode.userContext, selectedItems_.begin()->second.basic,m_storageUserInfo.user_id);
					pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
					pVersion->CenterWindow();
					isShowModal_ = true;
					pVersion->ShowModal();
					delete pVersion;
					pVersion = NULL;
					loadMetaData(browseHistory_[curPage_]);
					isShowModal_ = false;
				}
				break;
			default:
				break;
			}
		}

		void clearSearchBtnClick(TNotifyUI& msg)
		{
			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(TEAMSPACE_FILESEARCHTXT));
			if (NULL != searchtxt)
				searchtxt->resetText(L"");

			CButtonUI* pBtn = (CButtonUI*)(msg.pSender);
			if (pBtn)
				pBtn->SetVisible(false);

			MyTeamSpaceFilesDirNode curDirNode = browseHistory_[curPage_];
			curDirNode.keyWord = L"";
			curDirNode.dirId = 0;
			loadTeamSpaceFileList(curDirNode, false);

			if (searchtxt)
				searchtxt->SetFocus();
		}

		void searchTextChanged(TNotifyUI& msg)
		{
			CSearchTxtUI* pSearch = (CSearchTxtUI*)(msg.pSender);
			if (pSearch && m_pBtnClearSearch){
				CDuiString strText = pSearch->GetText();
				if (strText.IsEmpty()){
					m_pBtnClearSearch->SetVisible(false);
				}else{
					m_pBtnClearSearch->SetVisible(true);
				}
			}
		}

		void doMenuPoint(CMenuUI* pMenu, POINT pt)
		{
			if (NULL == pMenu)return;
			if (selectedItems_.size() <= 0)return;


			pMenu->SetVisible(false);

			if(browseHistory_[curPage_].isListView)
			{
				CCustomListUI* myFile = browseHistory_[curPage_].keyWord.empty()?teamspaceFileList_:teamspaceSearchList_;
				if(NULL == myFile) return;
				CStdValArray* curSelects = myFile->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CTeamspaceFileElementUI* element = static_cast<CTeamspaceFileElementUI*>(myFile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;
					UIFileNode fileNode;
					fileNode = element->m_uNodeData;
					if (-1 == fileNode.basic.id)continue;

					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
			else
			{
				if(NULL == teamspaceTileLayoutListFile_) return;
				CStdValArray* curSelects = teamspaceTileLayoutListFile_->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CTeamspaceFileElementUI* element = static_cast<CTeamspaceFileElementUI*>(teamspaceTileLayoutListFile_->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;
					UIFileNode fileNode;
					fileNode = element->m_uNodeData;
					if (-1 == fileNode.basic.id) continue;

					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
		}
	private:
		UserContext* userContext_;
		std::map<std::wstring, call_func> funcMaps_;
		CPaintManagerUI& paintManager_;
		CCustomListUI* teamspaceFileList_;
		CCustomListUI* teamspaceSearchList_;
		CTileLayoutListUI* teamspaceList_;
		CTileLayoutListUI* teamspaceTileLayoutListFile_;
		UserTeamSpaceNodeInfo selectTeamSpace_;
		CHorizontalLayoutUI*	m_pHorizontalLevel1;
		CHorizontalLayoutUI*	m_pHorizontalLevel2;
		CHorizontalLayoutUI*	m_pHorizontalSwitch;

		std::vector<MyTeamSpaceFilesDirNode> browseHistory_;
		size_t curPage_;
		NoticeFrameMgr* m_noticeFrame_;
		std::map<int64_t, UIFileNode> selectedItems_;
		int64_t curCnt_;
		PageParam curPageParam_;
		bool isNextPage_;
		bool isRenaming_;
		bool downloadFlag_;
		int32_t pageLimit_;
		TNotifyUI rightMsg_;

		CButtonUI*		m_pBtnClearSearch;

		SelectDialog*	m_pSelectWnd;
		StorageUserInfo m_storageUserInfo;	

		OrderParam m_oldOrderParam;

		bool isLoading_;
		int32_t delayLoading_;
		boost::thread loadThread_;
		bool isShowModal_;
	};

	TeamSpaceMgr* TeamSpaceMgr::instance_ = NULL;

	TeamSpaceMgr* TeamSpaceMgr::getInstance(UserContext* context, CPaintManagerUI& paintManager)
	{
		if (NULL == instance_)
		{
			instance_ = new TeamSpaceMgrImpl(context, paintManager);
		}
		return instance_;
	}

	void TeamSpaceMgrImpl::reShowPath(MyTeamSpaceFilesDirNode& currentfilenode)
	{
		CGroupButtonUI* pContainer = static_cast<CGroupButtonUI*>(paintManager_.FindControl(TEAMSPACE_GROUPBTN));
		if(NULL == pContainer) return;

		//get new path
		std::list<PathNode> pathNodes;
		if(!browseHistory_[curPage_].keyWord.empty()||0==currentfilenode.dirId)
		{
			PathNode node;
			node.fileId = 0;
			pathNodes.push_back(node);
		}
		else if(currentfilenode.dirId>0)
		{
			currentfilenode.teamSpaceNode.userContext->getShareResMgr()->getFilePathNodes(currentfilenode.dirId, pathNodes);
			PathNode node;
			node.fileId = currentfilenode.dirId;
			node.fileName = currentfilenode.dirName;
			if(node.fileName.empty())
			{
				Path remotePath = currentfilenode.teamSpaceNode.userContext->getPathMgr()->makePath();
				remotePath.id(node.fileId);
				FILE_DIR_INFO fileNode;
				(void)currentfilenode.teamSpaceNode.userContext->getSyncFileSystemMgr()->getProperty(remotePath, fileNode, ADAPTER_FOLDER_TYPE_REST);
				browseHistory_[curPage_].dirName = fileNode.name;
				node.fileName = fileNode.name;
			}
			pathNodes.push_back(node);	
		}

		if(!pathNodes.empty())
		{
			if(0==pathNodes.begin()->fileId)
			{
				pathNodes.begin()->fileName = currentfilenode.teamSpaceNode.userContext->id.name;
			}		
		}

		PathNode node;
		node.fileId = -2;
		node.fileName = iniLanguageHelper.GetCommonString(MSG_TEAMSPACE_BASENAME_KEY).c_str();
		pathNodes.push_front(node);

		pContainer->showPath(_T("teamSpace"), pathNodes);
	}

	void TeamSpaceMgrImpl::backClick(TNotifyUI& msg)
	{
		if(curPage_<1)
		{
			return;
		}
		UserContext* teamspaceContext = browseHistory_[--curPage_].teamSpaceNode.userContext;
		Path path = teamspaceContext->getPathMgr()->makePath();
		int64_t fileID = browseHistory_[curPage_].dirId;
		if(-2 != fileID && 0 != fileID)
		{
			path.id(fileID);	

			if(RT_OK == teamspaceContext->getSyncFileSystemMgr()->isExist(path, ADAPTER_FOLDER_TYPE_REST))
			{
				loadTeamSpaceFileList(browseHistory_[curPage_], true);
			}
			else
			{
				NoticeData data;
				data.noticeCode = MSG_DESTFOLDER_NOEXIST_KEY;
				NoticeFrame* noticeFrame = new NoticeFrame(paintManager_.GetPaintWindow());
				noticeFrame->ShowNoticeWindow( FrameType::Confirm, data, ShowType::Modal);
				delete noticeFrame;
				noticeFrame = NULL;
				++curPage_;
			}
		}
		else
		{
			loadTeamSpaceFileList(browseHistory_[curPage_], true);
		}
		
		File_Permissions filePermissions;
		Path filePath = browseHistory_[curPage_].teamSpaceNode.userContext->getPathMgr()->makePath();
		filePath.id(browseHistory_[curPage_].dirId);
		if(RT_OK == browseHistory_[curPage_].teamSpaceNode.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
			userContext_->id.id, filePermissions))
		{
			browseHistory_[curPage_].filePermissions = filePermissions;
		}
		FileShowButton();
	}

	void TeamSpaceMgrImpl::nextClick(TNotifyUI& msg)
	{
		if(curPage_+2>browseHistory_.size())
		{
			return;
		}

		UserContext* teamspaceContext = browseHistory_[++curPage_].teamSpaceNode.userContext;
		Path path = teamspaceContext->getPathMgr()->makePath();
		int64_t fileID = browseHistory_[curPage_].dirId;	
		if(-2 != fileID && 0 != fileID)
		{
			path.id(fileID);	
			if(RT_OK == teamspaceContext->getSyncFileSystemMgr()->isExist(path, ADAPTER_FOLDER_TYPE_REST))
			{			
				loadTeamSpaceFileList(browseHistory_[curPage_], true);
			}
			else
			{
				NoticeData data;
				data.noticeCode = MSG_DESTFOLDER_NOEXIST_KEY;
				NoticeFrame* noticeFrame = new NoticeFrame(paintManager_.GetPaintWindow());
				noticeFrame->ShowNoticeWindow( FrameType::Confirm, data, ShowType::Modal);
				delete noticeFrame;
				noticeFrame = NULL;
				--curPage_;
			}	
		}
		else
		{
			loadTeamSpaceFileList(browseHistory_[curPage_], true);
		}

		File_Permissions filePermissions;
		Path filePath = browseHistory_[curPage_].teamSpaceNode.userContext->getPathMgr()->makePath();
		filePath.id(browseHistory_[curPage_].dirId);
		if(RT_OK == browseHistory_[curPage_].teamSpaceNode.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
			userContext_->id.id, filePermissions))
		{
			browseHistory_[curPage_].filePermissions = filePermissions;
		}
		FileShowButton();
	}

	void TeamSpaceMgrImpl::updateClick(TNotifyUI& msg)
	{
		MyTeamSpaceFilesDirNode myFilesDirNode = browseHistory_[curPage_];
		File_Permissions filePermissions;
		Path filePath = browseHistory_[curPage_].teamSpaceNode.userContext->getPathMgr()->makePath();
		filePath.id(browseHistory_[curPage_].dirId);
		if(RT_OK == browseHistory_[curPage_].teamSpaceNode.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
			userContext_->id.id, filePermissions))
		{
			browseHistory_[curPage_].filePermissions = filePermissions;
		}
		isRenaming_ = false;
		loadTeamSpaceFileList(browseHistory_[curPage_], true);
		//刷新触发重命名时，需要重刷
		if(isRenaming_)
		{
			loadTeamSpaceFileList(browseHistory_[curPage_], true);
		}
		myFilesDirNode.isListView?teamspaceFileList_->HomeUp():teamspaceTileLayoutListFile_->HomeUp();
	}

	void TeamSpaceMgrImpl::fileSearchClick(TNotifyUI& msg)
	{
 		MyTeamSpaceFilesDirNode curDirNode = browseHistory_[curPage_];
 		CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(TEAMSPACE_FILESEARCHTXT));
 		curDirNode.keyWord = searchtxt->GetText();
 		curDirNode.dirId = 0;
		boost::algorithm::trim(curDirNode.keyWord);
//		if(curDirNode.keyWord.empty()) return;
 		loadTeamSpaceFileList(curDirNode, false);
	}

	void TeamSpaceMgrImpl::createClick(TNotifyUI& msg)
	{
		std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
		Configure* config =  configureMgr->getConfigure();				
		std::wstring  str_server = config->serverUrl();
		str_server = str_server.substr(0,str_server.length()-6);
		str_server += L"teamspace";
		(void)ShellExecute(NULL,L"open",L"explorer.exe",str_server.c_str(),NULL,SW_SHOWNORMAL);
	}

	void TeamSpaceMgrImpl::lookClick(TNotifyUI& msg)
	{
		std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
		Configure* config =  configureMgr->getConfigure();				
		std::wstring  str_server = config->serverUrl();
		str_server = str_server.substr(0,str_server.length()-6);
		str_server += L"teamspace";
		(void)ShellExecute(NULL,L"open",L"explorer.exe",str_server.c_str(),NULL,SW_SHOWNORMAL);
	}

	void TeamSpaceMgrImpl::moreClick(TNotifyUI& msg)
	{
		msg.pSender->SetContextMenuUsed(true);
		TEventUI event;
		event.dwTimestamp = msg.dwTimestamp;
		event.pSender = msg.pSender;
		event.lParam = msg.lParam;
		event.wParam = msg.wParam;
		event.ptMouse = msg.ptMouse;
		event.Type = UIEVENT_CONTEXTMENU;
		msg.pSender->Event(event);
		msg.pSender->SetContextMenuUsed(false);		
	}

	void TeamSpaceMgrImpl::uploadClick(TNotifyUI& msg)
	{
		MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];

		std::list<std::wstring> str_path;
		ShellCommonFileDialogParam param;
		param.okButtonName = iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY);
		param.title = param.okButtonName;
		param.parent = paintManager_.GetPaintWindow();
		ShellCommonFileDialog fileDialog(param);
		if (!fileDialog.getResults(str_path) || str_path.empty())
		{
			return;
		}

		if (ProxyMgr::getInstance(userContext_)->checkDirIsExist(str_path, fileDirNode.teamSpaceNode.userContext, fileDirNode.dirId))
		{
			NoticeData data;
			data.noticeType = Warning;
			data.noticeTitleCode = MSG_DRAGFILE_TITLE_KEY;
			data.noticeCode = MSG_DRAGFILE_TEXT_TIP_KEY;
			NoticeFrame* noticeFrame = new NoticeFrame(paintManager_.GetPaintWindow());
			if(NULL != noticeFrame)
			{
				noticeFrame->ShowNoticeWindow( FrameType::Choose, data, ShowType::Modal);
				if(!noticeFrame->IsClickOk())
				{
					delete noticeFrame;
					noticeFrame = NULL;
					return;
				}
				delete noticeFrame;
				noticeFrame = NULL;
			}
		}

		UIFileNode fileNode;
		fileNode.basic.id = fileDirNode.dirId;
		fileNode.basic.name = fileDirNode.dirName;
		fileNode.userContext = fileDirNode.teamSpaceNode.userContext;
		if(0 == TransTaskMgr::upload(str_path, fileNode))
		{
			setTeamSpaceFileListVisible();
		}		
	}

	void TeamSpaceMgrImpl::itemDragMove(TNotifyUI& msg)
	{
		CShadeListContainerElement* pm = NULL;
		std::wstring tempItemName = msg.pSender->GetName();
		if(L"teamSpace_FilelistView" ==tempItemName)
		{
			CCustomListUI* fileList = static_cast<CCustomListUI*>(msg.pSender);
			if(fileList==NULL) return;
			pm = static_cast<CShadeListContainerElement*>(fileList->GetItemAt(fileList->getCurEnter()));
		}
		else
		{
			CTileLayoutListUI* fileList = static_cast<CTileLayoutListUI*>(msg.pSender);
			if(fileList==NULL) return;
			pm = static_cast<CShadeListContainerElement*>(fileList->GetItemAt(fileList->getCurEnter()));
		}
		if(pm==NULL) return;

		getUIFileNode();
		if(selectedItems_.empty()) return;	
		
		if(selectedItems_.find(pm->m_uNodeData.basic.id) != selectedItems_.end() || FILE_TYPE_FILE == pm->m_uNodeData.basic.type)  return;

		m_noticeFrame_->Run(Choose, Ask, MSG_MOVECONFIRM_TITLE_KEY, MSG_MOVECONFIRM_NOTICE_KEY, Modal);
		if (!m_noticeFrame_->IsClickOk()) return;

		bool isRename = true;
		if (checkFileIsRename(selectedItems_, pm->m_uNodeData.basic.id, browseHistory_[curPage_].teamSpaceNode.userContext))
		{
			NoticeData data;
			data.noticeType = Warning;
			data.noticeTitleCode = MSG_SAVEFILE_COPYMOVE_TITLE_KEY;
			data.noticeCode = MSG_SAVEFILE_COPYMOVE_TEXT_KEY;
			NoticeFrame* noticeFrame = new NoticeFrame(paintManager_.GetPaintWindow());
			noticeFrame->ShowNoticeWindow( FrameType::Choose, data, ShowType::Modal);
			isRename = noticeFrame->IsClickOk();
			delete noticeFrame;
			noticeFrame = NULL;
		}
		if(!isRename) return;
		std::list<int64_t> srcNodeList;
		for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
		{
			srcNodeList.push_back(it->first);
		}
		addRestTask(browseHistory_[curPage_].teamSpaceNode.userContext, srcNodeList, 
			browseHistory_[curPage_].teamSpaceNode.basic.teamId(), pm->m_uNodeData.basic.id, RESTTASK_MOVE, isRename);
	}

	void TeamSpaceMgrImpl::itemDragFile(TNotifyUI& msg)
	{
		MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
		std::list<std::wstring> drogFile;	
		std::wstring tempItemName = msg.pSender->GetName();
		UIFileNode fileNode;
		CTeamspaceFileElementUI* pm = NULL;
		if(L"teamSpace_FilelistView" == tempItemName)
		{
			CCustomListUI* fileList = static_cast<CCustomListUI*>(msg.pSender);
			if(fileList==NULL) return;
			pm = static_cast<CTeamspaceFileElementUI*>(fileList->GetItemAt(fileList->getCurEnter()));
			drogFile = fileList->getDropFile();
			fileList->clearDropFile();
		}
		else if(L"teamSpace_tile_FilelistView" == tempItemName)
		{
			CTileLayoutListUI* fileList = static_cast<CTileLayoutListUI*>(msg.pSender);
			if(fileList==NULL) return;
			pm = static_cast<CTeamspaceFileElementUI*>(fileList->GetItemAt(fileList->getCurEnter()));
			drogFile = fileList->getDropFile();
			fileList->clearDropFile();
		}
		else
		{
			CCustomListUI* fileList = static_cast<CCustomListUI*>(msg.pSender);
			if(fileList==NULL) return;
			drogFile = fileList->getDropFile();
			fileList->clearDropFile();
		}
		if (drogFile.empty())  return;

		if(pm==NULL  || pm->m_uNodeData.basic.type == FILE_TYPE_FILE)
		{
			fileNode.userContext = fileDirNode.teamSpaceNode.userContext;
			fileNode.basic.id = fileDirNode.dirId;
			fileNode.basic.name = fileDirNode.dirName;
		}
		else
		{
			fileNode = pm->m_uNodeData;
		}

		if (SD::Utility::FS::is_invalidPath(*drogFile.begin()))
		{
			NoticeData data;
			data.noticeType = Warning;
			data.noticeTitleCode = MSG_DRAGFILE_TITLE_KEY;
			data.noticeCode = MSG_DRAGFILE_TEXT_KEY;
			NoticeFrame* noticeFrame = new NoticeFrame(paintManager_.GetPaintWindow());
			noticeFrame->ShowNoticeWindow( FrameType::Confirm, data, ShowType::Modal);
			delete noticeFrame;
			noticeFrame = NULL;
			return;
		}

		if (ProxyMgr::getInstance(userContext_)->checkDirIsExist(drogFile, fileNode.userContext, fileNode.basic.id))
		{
			NoticeData data;
			data.noticeType = Warning;
			data.noticeTitleCode = MSG_DRAGFILE_TITLE_KEY;
			data.noticeCode = MSG_DRAGFILE_TEXT_TIP_KEY;
			NoticeFrame* noticeFrame = new NoticeFrame(paintManager_.GetPaintWindow());
			noticeFrame->ShowNoticeWindow( FrameType::Choose, data, ShowType::Modal);
			if(!noticeFrame->IsClickOk())
			{
				delete noticeFrame;
				noticeFrame = NULL;
				return;
			}
			delete noticeFrame;
			noticeFrame = NULL;
		}
		(void)TransTaskMgr::upload(drogFile,fileNode);

		SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
		simlpeNoticeFrame->Show(Right,MSG_DRAPFILE_SETTING_KEY);
		delete simlpeNoticeFrame;
		simlpeNoticeFrame = NULL;
	}

	void TeamSpaceMgrImpl::tileSelectItemChanged(TNotifyUI& msg)
	{
		if (teamspaceList_->GetSelects()->GetSize() == 1)
		{
			TeamSpaceTileLayoutListContainerElement* item = static_cast<TeamSpaceTileLayoutListContainerElement*>(teamspaceList_->GetItemAt(teamspaceList_->GetCurSel()));
			if (item == NULL) return;

			selectTeamSpace_ = item->nodeData.basic;
		}
		showButton();
	}

	void TeamSpaceMgrImpl::iconItemdbclick(TNotifyUI& msg)
	{
		if (1 != teamspaceList_->GetSelects()->GetSize()) return;
		
		TeamSpaceTileLayoutListContainerElement* item = static_cast<TeamSpaceTileLayoutListContainerElement*>(teamspaceList_->GetItemAt(teamspaceList_->GetCurSel()));
		if (item == NULL) return;

		MyTeamSpaceFilesDirNode fileDirNode;
		fileDirNode.teamSpaceNode = item->nodeData;
		fileDirNode.isListView = true;
		fileDirNode.dirId = 0;
		fileDirNode.dirName =SD::Utility::String::utf8_to_wstring(item->nodeData.basic.member_.name());
		Path filePath = item->nodeData.userContext->getPathMgr()->makePath();
		filePath.id(0);
		item->nodeData.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
			userContext_->id.id, fileDirNode.filePermissions);
		loadTeamSpaceFileList(fileDirNode,false);		

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(TEAMSPACE_LISTTAB));
		if (NULL != pControl)
		{
			pControl->SelectItem(1);
			showBtnByLevel(false);
		}
		COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(L"teamSpace_filelist"));
		if (NULL != pOption)
		{
			pOption->Selected(true);
		}
		FileShowButton();
	}
	
	void TeamSpaceMgrImpl::listSelectchanged(TNotifyUI& msg)
	{
		if (teamspaceFileList_ == NULL || teamspaceTileLayoutListFile_ == NULL) return;
		teamspaceTileLayoutListFile_->clearDialog();
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
		if (NULL != pControl)
		{
			pControl->SelectItem(0);
		}		
		
		if (teamspaceTileLayoutListFile_->GetCount() > 0)
		{
			teamspaceTileLayoutListFile_->RemoveAll();
		}

		MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
		fileDirNode.isListView = true;

		loadTeamSpaceFileList(fileDirNode,false);			
	}

	void TeamSpaceMgrImpl::largeIconSelectchanged(TNotifyUI& msg)
	{
		if (teamspaceFileList_ == NULL || teamspaceTileLayoutListFile_ == NULL) return;
		teamspaceFileList_->clearDialog();
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
		if (NULL != pControl)
		{
			pControl->SelectItem(1);
		}
		
		if (teamspaceFileList_->GetCount() > 0)
		{
			teamspaceFileList_->RemoveAll();
		}
		
		MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
		fileDirNode.isListView = false;
		loadTeamSpaceFileList(fileDirNode, false);
	}

	void TeamSpaceMgrImpl::listItemMouseEnter(TNotifyUI& msg)
	{		
		TeamspaceListFileElement* pControl =  static_cast<TeamspaceListFileElement*>(msg.pSender);
		if( pControl == NULL)  return;
		if (teamspaceFileList_ == NULL) return;
		teamspaceFileList_->setCurEnter(pControl->GetIndex());
		if (teamspaceFileList_->isDragFileList()) return;
		if (NULL == pControl->GetInterface(DUI_CTR_LISTITEM)) return;
		CHorizontalLayoutUI * pListHot = static_cast<CHorizontalLayoutUI*>(paintManager_.FindSubControlByName(pControl, _T("teamSpace_List_listOperation")));

		if (browseHistory_.size() == 0) return;
		File_Permissions perm = browseHistory_[curPage_].filePermissions;
		if (!(perm & FP_PUBLISHLINK)) 
		{
			return;
		}
		if (teamspaceFileList_->GetSelects()->GetSize() > 1) return;
		pControl->SetMouseChildEnabled();
		if ( pListHot != NULL )
		{	
			CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(pControl, _T("teamSpace_List_isShareLink")));
			if(NULL != _pIsShareLink)
			{
				_pIsShareLink->SetEnabled(true);
				_pIsShareLink->SetVisible(true);
			}

			pListHot->SetVisible();
		}
	}

	void TeamSpaceMgrImpl::listItemMouseLeave(TNotifyUI& msg)
	{
		TeamspaceListFileElement* pControl =  static_cast<TeamspaceListFileElement*>(msg.pSender);
		if( pControl == NULL)  return;
		if (teamspaceFileList_ != NULL) teamspaceFileList_->setCurEnter(-1);
		if (NULL == pControl->GetInterface(DUI_CTR_LISTITEM)) return;
		CHorizontalLayoutUI * pListHot = static_cast<CHorizontalLayoutUI*>(paintManager_.FindSubControlByName(pControl, _T("teamSpace_List_listOperation")));
		pControl->SetMouseChildEnabled(false);
		if ( pListHot == NULL ) return;
		if (!pListHot->IsVisible()) return;

		if (browseHistory_.size() == 0) return;
		File_Permissions perm = browseHistory_[curPage_].filePermissions;
		if (!(perm & FP_PUBLISHLINK)) 
		{
			return;
		}

		CButtonUI * _pIsShareLink = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(pControl, _T("teamSpace_List_isShareLink")));
		if(NULL != _pIsShareLink)
		{
			if (pControl->m_uNodeData.basic.flags & OBJECT_FLAG_SHARELINK)
			{
				_pIsShareLink->SetEnabled(false);
			}
			else
			{
				_pIsShareLink->SetVisible(false);
			}
		}
	}

	void TeamSpaceMgrImpl::largeIconItemMouseEnter(TNotifyUI& msg)
	{
		TeamSpaceTileLayoutListContainerElement* pControl =  static_cast<TeamSpaceTileLayoutListContainerElement*>(msg.pSender);
		if( pControl == NULL)  return;
		if (teamspaceList_ != NULL)
		{
			teamspaceList_->setCurEnter(pControl->GetIndex());
		}
	}

	void TeamSpaceMgrImpl::largeIconItemMouseLeave(TNotifyUI& msg)
	{
		if (teamspaceList_ != NULL)
		{
			teamspaceList_->setCurEnter(-1);
		}
		TeamSpaceTileLayoutListContainerElement* pControl =  static_cast<TeamSpaceTileLayoutListContainerElement*>(msg.pSender);
		if( pControl == NULL)  return;
	}


	void TeamSpaceMgrImpl::fileShareLinkClick(TNotifyUI& msg)
	{
		CTeamspaceFileElementUI* pFileListItem =NULL;
		if (browseHistory_[curPage_].isListView)
		{
			if (!browseHistory_[curPage_].keyWord.empty())
			{
				pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceSearchList_->GetItemAt(teamspaceSearchList_->getCurEnter()));
				if (pFileListItem == NULL)
				{
					pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceSearchList_->GetItemAt(teamspaceSearchList_->GetCurSel()));
				}
			}
			else{
				pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceFileList_->GetItemAt(teamspaceFileList_->getCurEnter()));
				if (pFileListItem == NULL)
				{
					pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceFileList_->GetItemAt(teamspaceFileList_->GetCurSel()));
				}
			}
		}
		else
		{
			pFileListItem = static_cast<CTeamspaceFileElementUI*>(teamspaceTileLayoutListFile_->GetItemAt(teamspaceTileLayoutListFile_->GetCurSel()));
		}
		if (pFileListItem == NULL)  return;

		isShowModal_ = true;
		ShareLinkCountDialog::CreateDlg(paintManager_, pFileListItem->m_uNodeData.userContext, pFileListItem->m_uNodeData, false);
		updateClick(msg);
		isShowModal_ = false;
	}

	void TeamSpaceMgrImpl::TeamSpaceNameClick(TNotifyUI& msg)
	{
		TeamSpaceTileLayoutListContainerElement* item = static_cast<TeamSpaceTileLayoutListContainerElement*>(teamspaceList_->GetItemAt(teamspaceList_->getCurEnter()));
		if (item == NULL) return;

		selectTeamSpace_ = item->nodeData.basic;

		MyTeamSpaceFilesDirNode fileDirNode;
		fileDirNode.teamSpaceNode = item->nodeData;
		fileDirNode.isListView = true;
		fileDirNode.dirId = 0;
		fileDirNode.dirName=SD::Utility::String::utf8_to_wstring(item->nodeData.basic.member_.name());
		fileDirNode.teamSpaceNode.userContext->id.name = fileDirNode.dirName;
		Path filePath = item->nodeData.userContext->getPathMgr()->makePath();
		filePath.id(0);
		item->nodeData.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
				userContext_->id.id, fileDirNode.filePermissions);
		
		loadTeamSpaceFileList(fileDirNode,false);		

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(TEAMSPACE_LISTTAB));
		if (NULL != pControl)
		{
			pControl->SelectItem(1);
			showBtnByLevel(false);
		}
		COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(L"teamSpace_filelist"));
		if (NULL != pOption)
		{
			pOption->Selected(true);
		}
		FileShowButton();
	}

	void TeamSpaceMgrImpl::downFileClick(TNotifyUI& msg)
	{
		getUIFileNode();
		download(selectedItems_);
	}

	void TeamSpaceMgrImpl::createDirClick(TNotifyUI& msg)
	{
		setTeamSpaceFileListVisible();

		MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
		SYSTEMTIME sysTime; 
		FILETIME ft;
		ft.dwLowDateTime = 0;
		ft.dwHighDateTime = 0;
		ULARGE_INTEGER ularge;
		GetSystemTime(&sysTime);
		SystemTimeToFileTime(&sysTime,&ft);
		ularge.LowPart = ft.dwLowDateTime;
		ularge.HighPart = ft.dwHighDateTime;
		int64_t currentTime = ularge.QuadPart;

		FILE_DIR_INFO fileNode;
		fileNode.id = -1;
		fileNode.name = iniLanguageHelper.GetCommonString(COMMENT_CREATENEWDIR_KEY);
		fileNode.parent = fileDirNode.dirId;
		fileNode.type = FILE_TYPE_DIR;
		fileNode.ctime = currentTime;
		fileNode.mtime = currentTime;
		fileNode.size = 0;
		fileNode.flags = OBJECT_FLAG_SYNC;
		Path path = browseHistory_[curPage_].teamSpaceNode.userContext->getPathMgr()->makePath();
		path.parent(fileDirNode.dirId);
		browseHistory_[curPage_].teamSpaceNode.userContext->getSyncFileSystemMgr()->getNewName(path, fileNode.name);

		if (fileDirNode.isListView)
		{
			CDialogBuilder builder;
			TeamspaceListFileElement* pFileListItem = static_cast<TeamspaceListFileElement*>(
				builder.Create(Onebox::ControlNames::SKIN_XML_TEAMSPACE_FILELISTITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_));

			if (NULL == pFileListItem) return;

			pFileListItem->fillData(fileNode, fileDirNode.teamSpaceNode.userContext);
			pFileListItem->initUI();
			
			if (!teamspaceFileList_->AddAt(pFileListItem,0))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				return;
			}
			teamspaceFileList_->HomeUp();
			teamspaceFileList_->SelectAllItem(false);
			pFileListItem->rename();
		}
		else
		{
			CDialogBuilder builder;
			TeamspaceTileFileElement* pFileListItem = static_cast<TeamspaceTileFileElement*>(
				builder.Create(TEAMSPACE_FILETILELAYOUTLIST_ITEM_XML, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_));
			if (NULL == pFileListItem) return;;

			pFileListItem->fillData(fileNode, fileDirNode.teamSpaceNode.userContext);
			pFileListItem->initUI();

			if (!teamspaceTileLayoutListFile_->AddAt(pFileListItem,0))
			{
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				return;
			}
			teamspaceTileLayoutListFile_->HomeUp();
			teamspaceTileLayoutListFile_->SelectAllItem(false);
			pFileListItem->rename();
		}
	}

	void TeamSpaceMgrImpl::listVersionClick(TNotifyUI& msg)
	{
		CButtonUI* versionBtn = static_cast<CButtonUI*>(msg.pSender);
		if (NULL == versionBtn) return;
		if (NULL == versionBtn->GetParent()) return;
		if (NULL == versionBtn->GetParent()->GetParent()) return;
		CTeamspaceFileElementUI* pItem =  static_cast<CTeamspaceFileElementUI*> (versionBtn->GetParent()->GetParent());
		if (NULL == pItem) return;

		FileVersionDialog *pVersion = new FileVersionDialog(browseHistory_[curPage_].teamSpaceNode.userContext, pItem->m_uNodeData.basic,m_storageUserInfo.user_id);
		pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		pVersion->CenterWindow();
		isShowModal_ = true;
		pVersion->ShowModal();
		delete pVersion;
		pVersion = NULL;
		loadMetaData(browseHistory_[curPage_]);
		isShowModal_ = false;
	}

	void TeamSpaceMgrImpl::FileShowButton()
	{
		if(isNextPage_) return;

		CButtonUI* backBtn = static_cast<CButtonUI*>(paintManager_.FindControl(L"teamSpace_back"));
		CButtonUI* nextBtn = static_cast<CButtonUI*>(paintManager_.FindControl(L"teamSpace_next"));
		bool isFirst = (curPage_==0);
		bool isLast = (curPage_+1==browseHistory_.size());
		backBtn->SetEnabled(!isFirst);
		nextBtn->SetEnabled(!isLast);

		CScaleIconButtonUI* pUpload = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"teamSpace_uploadFile"));
		CButtonUI* pCreate = static_cast<CButtonUI*>(paintManager_.FindControl(L"teamSpace_createDir"));
		CButtonUI* pDown = static_cast<CButtonUI*>(paintManager_.FindControl(L"teamSpace_downFile"));
		CButtonUI* pMore = static_cast<CButtonUI*>(paintManager_.FindControl(L"teamSpace_more"));

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"teamSpace_listFileTab"));
		if (pControl == NULL) return;
		getUIFileNode();
		size_t count = selectedItems_.size();

		CCustomListUI*			pNoFiles	= static_cast<CCustomListUI*>(paintManager_.FindControl(L"teamSpace_noFiles"));
		if(NULL != pNoFiles && pNoFiles->Activate())
		{
				pNoFiles->SetFocus();
		}

		File_Permissions perm = browseHistory_[curPage_].filePermissions;
		if (perm & FP_UPLOAD)
		{
			if(pUpload != NULL)
			{
				pUpload->SetVisible(true);
			}
			pControl->GetCurSel() == 0 ? teamspaceFileList_->SetAttribute(L"droptarget",L"true") : teamspaceTileLayoutListFile_->SetAttribute(L"droptarget",L"true") ;
		}
		else
		{
			if(pUpload != NULL)
			{
				pUpload->SetVisible(false);
			}
			pControl->GetCurSel() == 0 ? teamspaceFileList_->SetAttribute(L"droptarget",L"false") : teamspaceTileLayoutListFile_->SetAttribute(L"droptarget",L"false") ;		
		}
		if (perm & FP_EDIT )
		{
			if(pCreate != NULL) pCreate->SetVisible();
		}
		else
		{
			if(pCreate != NULL) pCreate->SetVisible(false);
		}
		if (perm & FP_DOWNLOAD )
		{
			if(pDown != NULL)
			{
				pDown->SetEnabled(count != 0);
				pDown->SetVisible(count != 0);	
			}
		}
		else
		{
			if(pDown != NULL)
			{
				pDown->SetEnabled(false);
				pDown->SetVisible(false);
			}
		}

		if (perm & FP_DELETE || perm & FP_PUBLISHLINK || perm & FP_AUTHORIZE)
		{
			if(pMore != NULL) 
			{
				pMore->SetEnabled(count != 0);
				pMore->SetVisible(count != 0);
			}
		}
		else
		{
			if(pMore != NULL)
			{
				pMore->SetEnabled(false);
				pMore->SetVisible(false);
			}
		}

		CTextUI* cntText = static_cast<CTextUI*>(paintManager_.FindControl(L"teamSpace_count"));
		if (cntText)cntText->SetVisible(false);
		if (!browseHistory_[curPage_].keyWord.empty())
		{
			if (pUpload)pUpload->SetVisible(false);
			if (pCreate)pCreate->SetVisible(false);
			if(0==count)
			{
				if (pDown)pDown->SetVisible(false);
				if (pMore)pMore->SetVisible(false);
				if (cntText)
				{
					std::wstringstream showText;
					showText<< iniLanguageHelper.GetCommonString(MSG_SHARE2ME_SHOWBTN_START_KEYWORD_KEY).c_str();
					showText<< L" " << curCnt_ << L" ";
					showText<< iniLanguageHelper.GetCommonString(MSG_SHARE2ME_SHOWBTN_END_KEYWORD_KEY).c_str();
					if(curCnt_>curPageParam_.limit)
					{
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						showText << simlpeNoticeFrame->GetMsg(MSG_SEARCH_LIMIT_INFO_KEY, curPageParam_.limit);
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
					}
					cntText->SetText(showText.str().c_str());
					cntText->SetTextColor(0xFF666666);
					cntText->SetFont(10);
					cntText->SetVisible(true);
				}
			}
		}
	}

	void TeamSpaceMgrImpl::showButton() 
	{
		CButtonUI* pDetail = static_cast<CButtonUI*>(paintManager_.FindControl(L"teamSpace_detail"));
		if (pDetail == NULL)
		{
			return;
		}

		if (teamspaceList_->GetSelects()->GetSize() == 1)
		{
			pDetail->SetEnabled();
			pDetail->SetVisible(true);
		}
		else
		{
			pDetail->SetEnabled(false);
			pDetail->SetVisible(false);
		}
	}

	void TeamSpaceMgrImpl::FilelistitemClick(TNotifyUI& msg)
	{
		if (1 != msg.wParam) return;
		rightMsg_ = msg;	

		getUIFileNode();
	}

	void TeamSpaceMgrImpl::listNameReturn(TNotifyUI& msg)
	{
		isRenaming_ = true;
		CRenameRichEditUI *richEdit = static_cast<CRenameRichEditUI*>(msg.pSender);
		if (NULL == richEdit) return;
		if (NULL == richEdit->GetParent()) return;
		CTeamspaceFileElementUI* pFileListItem = static_cast<CTeamspaceFileElementUI*>(richEdit->GetParent()->GetParent());		
		if (NULL == pFileListItem)	return;
		MyTeamSpaceFilesDirNode fileDirNode = browseHistory_[curPage_];
		UserContext* teamspaceContext = fileDirNode.teamSpaceNode.userContext;
		std::wstring str_newName = richEdit->GetText();
		str_newName = SD::Utility::String::replace_all(str_newName,L"\r",L" ");

		int tipType = -1;
		std::wstring str_des = L"";

		if (-1 ==pFileListItem->m_uNodeData.basic.id)
		{
			pFileListItem->m_uNodeData.basic.name = str_newName;
			FILE_DIR_INFO fdi = pFileListItem->m_uNodeData.basic;

			Path path = teamspaceContext->getPathMgr()->makePath();
			path.id(fileDirNode.dirId);

			//int32_t result = teamspaceContext->getSyncFileSystemMgr()->create(path,str_newName,fdi,ADAPTER_FOLDER_TYPE_REST);
			int32_t result = ProxyMgr::getInstance(userContext_)->create(teamspaceContext, path,str_newName,fdi,ADAPTER_FOLDER_TYPE_REST);
			if (RT_OK == result)
			{
				tipType = Right;
				str_des =  MSG_CREATEDIR_SUCCESSFUL_KEY;
				if(!fileDirNode.isListView)
				{
					pFileListItem->SetToolTip(str_newName.c_str());
				}
				richEdit->SetToolTip(str_newName.c_str());
				pFileListItem->m_uNodeData.basic.id = fdi.id;
			}
			else
			{	
				if(fileDirNode.isListView)
				{
					if (NULL != teamspaceFileList_)
					{
						teamspaceFileList_->Remove(pFileListItem);
					}
				}
				else
				{
					if (NULL != teamspaceTileLayoutListFile_)
					{
						teamspaceTileLayoutListFile_->Remove(pFileListItem);
					}
				}
				if (RT_DIFF_FILTER == result || RT_FILE_EXIST_ERROR == result)
				{
					SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
					simlpeNoticeFrame->Show(Error, MSG_CREATEDIR_FAILED_EX_KEY,  
						ErrorConfMgr::getInstance()->getDescription(result).c_str());
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
				else
				{
					tipType = Error;
					str_des = MSG_CREATEDIR_FAILED_KEY;
				}
			}
		}
		else
		{
			if (str_newName == pFileListItem->m_uNodeData.basic.name) return;

			Path listPath = teamspaceContext->getPathMgr()->makePath();
			listPath.id(pFileListItem->m_uNodeData.basic.id);
			ADAPTER_FILE_TYPE type = (pFileListItem->m_uNodeData.basic.type == FILE_TYPE_DIR)?ADAPTER_FOLDER_TYPE_REST : ADAPTER_FILE_TYPE_REST;
			int32_t ret = ProxyMgr::getInstance(userContext_)->rename(teamspaceContext, listPath, str_newName, type);
			if (RT_OK != ret)
			{
				if(!fileDirNode.isListView)
				{
					pFileListItem->SetToolTip(pFileListItem->m_uNodeData.basic.name.c_str());
				}
				richEdit->SetToolTip(pFileListItem->m_uNodeData.basic.name.c_str());
				richEdit->SetText(pFileListItem->m_uNodeData.basic.name.c_str());
				if (RT_DIFF_FILTER == ret || RT_FILE_EXIST_ERROR == ret)
				{
					SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
					simlpeNoticeFrame->Show(Error, MSG_RENAME_FAILED_EX_KEY,  
						ErrorConfMgr::getInstance()->getDescription(ret).c_str());
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
				else
				{
					tipType = Error;
					str_des = MSG_RENAME_FAILED_KEY;
				}
			}
			else
			{
				tipType = Right;
				str_des = MSG_RENAME_SUCCESSFUL_KEY;
				if(!fileDirNode.isListView)
				{
					pFileListItem->SetToolTip(str_newName.c_str());
				}
				richEdit->SetToolTip(str_newName.c_str());
				pFileListItem->m_uNodeData.basic.name = str_newName;
			}
		}
		SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
		simlpeNoticeFrame->Show((NoticeType)tipType, str_des.c_str());
		delete simlpeNoticeFrame;
		simlpeNoticeFrame = NULL;
	}

	void TeamSpaceMgrImpl::deleteAllClick()
	{
		/*getUIFileNode();*/
		if(selectedItems_.empty())
		{
			return;
		}
		
		m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
		if (!m_noticeFrame_->IsClickOk()) return;
		std::list<int64_t> srcNodeList;
		for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
		{
			srcNodeList.push_back(it->first);
		}

		addRestTask(browseHistory_[curPage_].teamSpaceNode.userContext, srcNodeList,
			browseHistory_[curPage_].teamSpaceNode.userContext->getUserInfoMgr()->getUserId(), 0, RESTTASK_DELETE);
	}
}
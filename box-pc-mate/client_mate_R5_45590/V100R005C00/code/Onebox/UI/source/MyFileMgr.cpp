#include "stdafxOnebox.h"
#include "MyFileMgr.h"
#include "ControlNames.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "ListContainerElement.h"
#include "ChildLayout.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "SyncFileSystemMgr.h"
#include "PathMgr.h"
#include "ListContainerElement.h"
#include "DialogBuilderCallbackImpl.h"
#include "ShareFrame.h"
#include "SearchTxt.h"
#include "CustomListUI.h"
#include "TileLayoutListUI.h"
#include "TransTaskMgr.h"
#include "GroupButton.h"
#include "FileVersionDialog.h"
#include <atltime.h>
#include "UploadFrame.h"
#include "ProxyMgr.h"
#include "TeamSpaceResMgr.h"
#include "SimpleNoticeFrame.h"
#include "NoticeFrame.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "OpenFileDbClick.h"
#include "MyFileElement.h"
#include "FilterMgr.h"
#include "ConfigureMgr.h"
#include "RestTaskMgr.h"
#include "ErrorConfMgr.h"
#include <boost/algorithm/string.hpp>
#include "DelayLoadingFrame.h"
#include "ShellCommonFileDialog.h"
#include "CommonFileDialog.h"
#include "CommonFileDialogRemoteNotify.h"
#include "ShareLinkCountDialog.h"
#include "SelectDialog.h"
#include <boost/thread.hpp>

using namespace SD::Utility;

#ifndef MODULE_NAME
#define MODULE_NAME ("MyFile")
#endif

#define SEARCH_SORT_FILED_TIME ("modifiedAt")
#define SEARCH_SORT_TYPE ("type")
#define SORT_DIRECTION_ASC ("asc")
#define SORT_DIRECTION_DESC ("desc")

namespace Onebox
{
	#define MYFILE_SCROLL_LIMEN (100)	//触发翻页的滚动条阈值

	enum MyFileCountType
	{
		MYFILE_COUNT_DEFAULT = 0,
		MYFILE_COUNT_ADD,
		MYFILE_COUNT_MINUS
	};

	class TransToTeamspaceDialogNotify : public TeamspaceCommonFileDialogNotify
	{
	public:

		TransToTeamspaceDialogNotify(UserContext *userContext, uint32_t languageId)
			:TeamspaceCommonFileDialogNotify(userContext, languageId)
		{

		}

		virtual bool customButtonVisible(const CommonFileDialogItem& item, const CommonFileDialogCustomButton button)
		{
			if (NULL == item.get())
			{
				return false;
			}

			TeamspaceCommonFileDialogData *data = (TeamspaceCommonFileDialogData*)item->data.get();
			if (NULL == data)
			{
				return false;
			}

			if (CFDCB_COPY == button || CFDCB_MOVE == button)
			{
				return false;
			}

			if (data->type == TRFDT_ROOT)
			{
				return false;
			}
			return (item->type == CFDFT_DIR);
		}

	};

	class MyFileMgrImpl : public MyFileMgr
	{
	public:
		MyFileMgrImpl(UserContext* context, CPaintManagerUI& paintManager)
			:userContext_(context)
			,paintManager_(paintManager)
		{
			myFileList = NULL;
			myFileListLargeIcon = NULL;
			myFileSearchList = NULL;
			pBtnName = NULL;
			pBtnSize = NULL;
			pBtnCtime = NULL;
			m_IfLageIcon = false;
			m_ListCount  = 0;
			m_pSelectWnd = NULL;
			downloadFlag_ = false;
			isLoading_ = false;
			delayLoading_ = userContext_->getConfigureMgr()->getConfigure()->delayLoading();
			isShowModal_ = false;

			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYFILE_SEARCHTXT));
			CDuiString defaultTxt = iniLanguageHelper.GetCommonString(MSG_MYSHARE_SEARCHDEFAULT_TEXT_KEY).c_str();
			if (NULL != searchtxt)
				searchtxt->setDefaultTxt(defaultTxt);

			m_pBtnClearSearch = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_clearsearchbtn")));

			m_noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());
			funcMaps_.insert(std::make_pair(L"back_click", boost::bind(&MyFileMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"next_click", boost::bind(&MyFileMgrImpl::nextClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"upload_click", boost::bind(&MyFileMgrImpl::uploadClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"update_click", boost::bind(&MyFileMgrImpl::updateClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"create_click", boost::bind(&MyFileMgrImpl::createClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"download_click", boost::bind(&MyFileMgrImpl::downloadClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"share_click", boost::bind(&MyFileMgrImpl::shareClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"more_click", boost::bind(&MyFileMgrImpl::moreClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"search_click", boost::bind(&MyFileMgrImpl::searchClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"list_selectchanged", boost::bind(&MyFileMgrImpl::listSelectchanged, this)));
			funcMaps_.insert(std::make_pair(L"largeIcon_selectchanged", boost::bind(&MyFileMgrImpl::largeIconSelectchanged, this)));
			
			funcMaps_.insert(std::make_pair(L"listHeaderItemNameSortIcon_click", boost::bind(&MyFileMgrImpl::nameHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_name_headerclick", boost::bind(&MyFileMgrImpl::nameHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemName_click", boost::bind(&MyFileMgrImpl::nameHeaderClick, this)));

			funcMaps_.insert(std::make_pair(L"listHeaderItemSizeSortIcon_click", boost::bind(&MyFileMgrImpl::sizeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_size_headerclick", boost::bind(&MyFileMgrImpl::sizeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemSize_click", boost::bind(&MyFileMgrImpl::sizeHeaderClick, this)));
			
			funcMaps_.insert(std::make_pair(L"listHeaderItemCtimeSortIcon_click", boost::bind(&MyFileMgrImpl::ctimeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_ctime_headerclick", boost::bind(&MyFileMgrImpl::ctimeHeaderClick, this)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemCtime_click", boost::bind(&MyFileMgrImpl::ctimeHeaderClick, this)));

			funcMaps_.insert(std::make_pair(L"listItem_mouseenter", boost::bind(&MyFileMgrImpl::listItemMouseEnter, this,_1)));
			funcMaps_.insert(std::make_pair(L"listItem_mouseleave", boost::bind(&MyFileMgrImpl::listItemMouseLeave, this,_1)));
			funcMaps_.insert(std::make_pair(L"listName_killfocus", boost::bind(&MyFileMgrImpl::nameReturn, this,_1)));
			funcMaps_.insert(std::make_pair(L"listItem_itemdbclick", boost::bind(&MyFileMgrImpl::dbclick, this,_1)));

			funcMaps_.insert(std::make_pair(L"isShare_click", boost::bind(&MyFileMgrImpl::fileShareClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"isShareLink_click", boost::bind(&MyFileMgrImpl::fileShareLinkClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"isSync_click", boost::bind(&MyFileMgrImpl::fileIsSyncClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"listView_selectItemChanged", boost::bind(&MyFileMgrImpl::showButton, this)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_selectItemChanged", boost::bind(&MyFileMgrImpl::showButton, this)));
			funcMaps_.insert(std::make_pair(L"listView_selectchanged", boost::bind(&MyFileMgrImpl::showButton, this)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_selectchanged", boost::bind(&MyFileMgrImpl::showButton, this)));
			funcMaps_.insert(std::make_pair(L"version_click", boost::bind(&MyFileMgrImpl::versionClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"listItem_itemdelete", boost::bind(&MyFileMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"listItem_itemselectall", boost::bind(&MyFileMgrImpl::selectAllItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listView_itemselectall", boost::bind(&MyFileMgrImpl::selectAllClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_itemselectall", boost::bind(&MyFileMgrImpl::selectAllClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"largeIconItem_itemdelete", boost::bind(&MyFileMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"largeIconItem_itemselectall", boost::bind(&MyFileMgrImpl::selectAllItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"tileLayout_listView_selectItemChanged", boost::bind(&MyFileMgrImpl::showButton, this)));	
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_selectchanged", boost::bind(&MyFileMgrImpl::showButton, this)));	
			funcMaps_.insert(std::make_pair(L"largeIconItem_itemdbclick", boost::bind(&MyFileMgrImpl::dbclick, this,_1)));	
			funcMaps_.insert(std::make_pair(L"name_killfocus", boost::bind(&MyFileMgrImpl::nameReturn, this,_1)));		

			funcMaps_.insert(std::make_pair(L"tileLayout_listView_itemDragMove", boost::bind(&MyFileMgrImpl::itemDragMove, this,_1)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_itemDragFile", boost::bind(&MyFileMgrImpl::itemDragFile, this,_1)));
			funcMaps_.insert(std::make_pair(L"listView_itemDragMove", boost::bind(&MyFileMgrImpl::itemDragMove, this,_1)));
			funcMaps_.insert(std::make_pair(L"listView_itemDragFile", boost::bind(&MyFileMgrImpl::itemDragFile, this,_1)));
			funcMaps_.insert(std::make_pair(L"noFiles_itemDragFile", boost::bind(&MyFileMgrImpl::itemDragFile, this,_1)));

			funcMaps_.insert(std::make_pair(L"listView_nextPage", boost::bind(&MyFileMgrImpl::nextPage, this,_1)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_nextPage", boost::bind(&MyFileMgrImpl::nextPage, this,_1)));

			funcMaps_.insert(std::make_pair(L"listItem_back", boost::bind(&MyFileMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"largeIconItem_back", boost::bind(&MyFileMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_back", boost::bind(&MyFileMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"noFiles_back", boost::bind(&MyFileMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listView_back", boost::bind(&MyFileMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_back", boost::bind(&MyFileMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listView_itemdelete", boost::bind(&MyFileMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_itemdelete", boost::bind(&MyFileMgrImpl::deleteAllClick, this)));
			funcMaps_.insert(std::make_pair(L"listView_listselectall", boost::bind(&MyFileMgrImpl::selectAllItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_listselectall", boost::bind(&MyFileMgrImpl::selectAllItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"path_click", boost::bind(&MyFileMgrImpl::searchPathClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"searchtxt_return", boost::bind(&MyFileMgrImpl::searchClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"listView_menuadded", boost::bind(&MyFileMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"listView_menuitemclick", boost::bind(&MyFileMgrImpl::menuItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_menuadded", boost::bind(&MyFileMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"tileLayout_listView_menuitemclick", boost::bind(&MyFileMgrImpl::menuItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_menuadded", boost::bind(&MyFileMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"SearchlistView_menuitemclick", boost::bind(&MyFileMgrImpl::menuItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"more_menuadded", boost::bind(&MyFileMgrImpl::moreMenuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"more_menuitemclick", boost::bind(&MyFileMgrImpl::moreMenuItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"clearsearchbtn_click", boost::bind(&MyFileMgrImpl::clearSearchBtnClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"searchtxt_textchanged", boost::bind(&MyFileMgrImpl::searchTextChanged, this, _1)));

			userContext_->getUserInfoMgr()->getCurUserInfo(m_storageUserInfo);
		}

		~MyFileMgrImpl()
		{
			delete m_noticeFrame_;
			m_noticeFrame_ = NULL;	

			if (m_pSelectWnd)
			{
				m_pSelectWnd->Close();
				m_pSelectWnd = NULL;
			}
		}

		virtual void initData()
		{
			browseHistory_.clear();
			MyFilesDirNode myFilDirNode;
			myFilDirNode.isListView = true;
			myFilDirNode.ownerId = userContext_->id.id;
			myFilDirNode.fileData.basic.id = 0;
			myFilDirNode.fileData.basic.name = iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY).c_str();
			myFilDirNode.fileData.userContext = userContext_;
			browseHistory_.push_back(myFilDirNode);
			curPage_ = 0;
			curCnt_ = 0;
			isNextPage_ = false;
			isRenaming_ = false;		

			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
			
			pBtnName			= static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_listHeaderItemNameSortIcon")));
			pBtnSize			= static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_listHeaderItemSizeSortIcon")));
			pBtnCtime			= static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_listHeaderItemCtimeSortIcon")));
			myFileList			= static_cast<CCustomListUI*>(paintManager_.FindControl(MYFILE_LISTLISTVIEW));
			myFileListLargeIcon = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(MYFILE_TILELAYOUT_LISTVIEW));
			myFileSearchList	= static_cast<CCustomListUI*>(paintManager_.FindControl(MYFILE_SEARCHLISTVIEW));

			loadData(myFilDirNode, false);

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(MYFILE_LISTTAB));
			if (NULL != pControl)
			{
				pControl->SelectItem(0);
			}
			COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(MYFILE_LIST));
			if (NULL != pOption)
			{
				pOption->Selected(true);
			}

			setTotalText(MYFILE_COUNT_DEFAULT);

			if (!m_pSelectWnd)
			{
				m_pSelectWnd = new SelectDialog;
			}
			if ( m_pSelectWnd)
			{
				POINT pt;
				pt.x = 0;
				pt.y = 0;
				m_pSelectWnd->Init(L"SelectDialog.xml", paintManager_.GetPaintWindow(), pt);
				m_pSelectWnd->SetWindowShow(false);

				if (myFileList)
				{
					m_pSelectWnd->Init(L"SelectDialog.xml", myFileList->GetManager()->GetPaintWindow(), pt);
					m_pSelectWnd->SetWindowShow(false);
					myFileList->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}
				if (myFileListLargeIcon)
				{
					m_pSelectWnd->Init(L"SelectDialog.xml", myFileListLargeIcon->GetManager()->GetPaintWindow(), pt);
					m_pSelectWnd->SetWindowShow(false);
					myFileListLargeIcon->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}

				if ( myFileSearchList )
				{
					m_pSelectWnd->Init(L"SelectDialog.xml", myFileSearchList->GetManager()->GetPaintWindow(), pt);
					m_pSelectWnd->SetWindowShow(false);
					myFileSearchList->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}
			}
		}

		virtual void setTotalText(MyFileCountType countType,int nMutiCount = 0)
		{
			switch(countType)
			{
			case MYFILE_COUNT_DEFAULT:
				{
				}
				break;
			case MYFILE_COUNT_ADD:
				{
					m_ListCount += 	nMutiCount;			
				}
				break;
			case MYFILE_COUNT_MINUS:
				{
					m_ListCount -= nMutiCount;
				}
				break;
			}
					
			CTextUI* cntTextC = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_Total")));
			if(NULL == cntTextC) return;
			std::wstringstream showText;
			showText<< iniLanguageHelper.GetCommonString(MSG_MYFILE_TOTAL_START).c_str();
			showText<< L" " << m_ListCount << L" ";
			showText<< iniLanguageHelper.GetCommonString(MSG_MYFILE_TOTAL_END).c_str();
			cntTextC->SetText(showText.str().c_str());
			cntTextC->SetFont(10);
			RECT rc = {20,20,20,15};
			cntTextC->SetPadding(rc);
		}

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg)
		{
			std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
						
			if(it!=funcMaps_.end())
			{
				if (funcName == L"listItem_selectchanged" )
				{
					int i = 0;
				}
				it->second(msg);
			}

			if (funcName.substr(0,10) == L"groupMenu_")
			{
				if(msg.sType != DUI_MSGTYPE_CLICK && 
					msg.sType != DUI_MSGTYPE_MENUADDED && 
					msg.sType != DUI_MSGTYPE_MENUITEM_CLICK) return;
				CGroupButtonUI* pContainer = static_cast<CGroupButtonUI*>(paintManager_.FindControl(MYFILE_GROUPBTN));
				if(NULL == pContainer) return;
				if (msg.sType == DUI_MSGTYPE_MENUITEM_CLICK) {
					PathNode node = pContainer->getPathNodeById(msg.lParam);
					MyFilesDirNode fileNode;
					fileNode.fileData.basic.id = node.fileId;
					fileNode.isListView = browseHistory_[curPage_].isListView;
					fileNode.ownerId = userContext_->id.id;
					for(int32_t i = curPage_; i>=0; --i)
					{
						if(browseHistory_[i].fileData.basic.id==fileNode.fileData.basic.id)
						{
							fileNode.fileData =  browseHistory_[i].fileData;
							break;
						}
					}
					loadData(fileNode, false);
				}
				else pContainer->showMenu(MYFILE_MODULE_NAME, msg);
			}
			if (funcName.substr(0,10) == L"groupMain_")
			{
				std::vector<std::wstring> vecInfo;
				SD::Utility::String::split(funcName, vecInfo, L"_");
				if(vecInfo.size()<3) return;
				if(L"click"!=vecInfo[2]) return;

				SERVICE_INFO(MODULE_NAME, RT_OK, "executeFunc %s", SD::Utility::String::wstring_to_string(funcName).c_str());
				MyFilesDirNode fileNode;
				fileNode.fileData.basic.id = SD::Utility::String::string_to_type<int64_t>(vecInfo[1]);
				fileNode.isListView = browseHistory_[curPage_].isListView;
				fileNode.ownerId = userContext_->id.id;
				for(int32_t i = curPage_; i>=0; --i)
				{
					if(browseHistory_[i].fileData.basic.id==fileNode.fileData.basic.id)
					{
						fileNode.fileData =  browseHistory_[i].fileData;
						break;
					}
				}
				loadData(fileNode, false);
			}
			if (funcName.substr(0,9) == L"groupSub_")
			{
				std::vector<std::wstring> vecInfo;  
				SD::Utility::String::split(funcName, vecInfo, L"_");
				if(vecInfo.size()<3) return;
				if(L"click"!=vecInfo[2]) return;

				SERVICE_INFO(MODULE_NAME, RT_OK, "executeFunc %s", SD::Utility::String::wstring_to_string(funcName).c_str());
				int64_t fileId = SD::Utility::String::string_to_type<int64_t>(vecInfo[1]);
				//TODO 获取子文件夹
				//显示子文件夹菜单
			}
		}

		virtual void reloadCache(int64_t ownerId, int64_t curId, bool isFlush = false)
		{
			if(isShowModal_)
			{
				return;
			}
			if(!browseHistory_[curPage_].keyWord.empty()) return;
			if(browseHistory_[curPage_].ownerId == ownerId && browseHistory_[curPage_].fileData.basic.id == curId)
			{
				loadMetaData(browseHistory_[curPage_], isFlush);
			}
		}

		virtual bool isTheCurPage(int64_t ownerId, int64_t curId)
		{
			if(!browseHistory_[curPage_].keyWord.empty()) return false;
			if(browseHistory_[curPage_].ownerId == ownerId && browseHistory_[curPage_].fileData.basic.id == curId)
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
			if(browseHistory_[curPage_].ownerId == ownerId && browseHistory_[curPage_].fileData.basic.id == curId)
			{
				loadMetaData(browseHistory_[curPage_], isFlush);
			}
		}

		virtual bool reloadThumb(const std::string& thumbKey)
		{
			if(isShowModal_)
			{
				return false;
			}
			if(browseHistory_[curPage_].isListView)
			{
				if(browseHistory_[curPage_].keyWord.empty())
				{
					for(int i = 0; i < myFileList->GetCount(); ++i)
					{
						MyFilesListContainerElement* fileNode = static_cast<MyFilesListContainerElement*>(myFileList->GetItemAt(i));
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
					for(int i = 0; i < myFileSearchList->GetCount(); ++i)
					{
						MyFilesListContainerElement* fileNode = static_cast<MyFilesListContainerElement*>(myFileSearchList->GetItemAt(i));
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
				for(int i = 0; i < myFileListLargeIcon->GetCount(); ++i)
				{
					MyFilesTileLayoutListContainerElement* fileNode = static_cast<MyFilesTileLayoutListContainerElement*>(myFileListLargeIcon->GetItemAt(i));
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

		virtual void showPage(int64_t pageId, int64_t selectId = -1)
		{
			MyFilesDirNode dirNode;
			dirNode.ownerId = userContext_->id.id;
			dirNode.fileData.basic.id = pageId;
			dirNode.fileData.userContext = userContext_;
			loadData(dirNode, false);
					
			if (-1 == selectId || NULL == myFileList) return;
			for (int i = 0; i<myFileList->GetCount(); ++i)
			{
				MyFilesListContainerElement* item = static_cast<MyFilesListContainerElement*>(myFileList->GetItemAt(i));
				if (item == NULL) continue;
				if (item->m_uNodeData.basic.id == selectId)
				{
					item->Select();
					SIZE szPos = myFileList->GetScrollPos();
					szPos.cy = i*item->GetFixedHeight();
					myFileList->SetScrollPos(szPos);
					break;
				}
			}
		}

		virtual void showPage(int64_t pageId, std::wstring& selectFileName )
		{
			MyFilesDirNode dirNode;
			dirNode.ownerId = userContext_->id.id;
			dirNode.fileData.basic.id = pageId;
			dirNode.fileData.userContext = userContext_;
			loadData(dirNode, false);

			if (L"" == selectFileName || NULL == myFileList) return;

			for (int i = 0; i<myFileList->GetCount(); ++i)
			{
				MyFilesListContainerElement* item = static_cast<MyFilesListContainerElement*>(myFileList->GetItemAt(i));
				if (item == NULL) continue;
				if (item->m_uNodeData.basic.parent == pageId && item->m_uNodeData.basic.name == selectFileName)
				{
					item->Select();

					SIZE posscroll = myFileList->GetScrollPos();

					posscroll.cy = item->GetFixedHeight() * i;
					myFileList->SetScrollPos(posscroll);
					break;
				}
			}
		}

		virtual void updateClick(TNotifyUI& msg)
		{
			MyFilesDirNode myFilesDirNode = browseHistory_[curPage_];
			isRenaming_ = false;
			loadData(myFilesDirNode, true);
			//刷新触发重命名时，需要重刷
			if(isRenaming_)
			{
				loadData(myFilesDirNode, true);
			}
			myFilesDirNode.isListView?myFileList->HomeUp():myFileListLargeIcon->HomeUp();
		}
	private:
		void backClick(TNotifyUI& msg)
		{
			if(curPage_ < 1) return;
			
			Path path = userContext_->getPathMgr()->makePath();
			int64_t fileID = browseHistory_[--curPage_].fileData.basic.id;
			if(0 != fileID)
			{
				path.id(fileID);	
				
				if(RT_OK == userContext_->getSyncFileSystemMgr()->isExist(path, ADAPTER_FOLDER_TYPE_REST))
				{
					loadData(browseHistory_[curPage_], true);
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
				loadData(browseHistory_[curPage_], true);
			}
		}

		void nextClick(TNotifyUI& msg)
		{
			if(curPage_+2>browseHistory_.size()) return;
			Path path = userContext_->getPathMgr()->makePath();
			int64_t fileID = browseHistory_[++curPage_].fileData.basic.id;	
			path.id(fileID);	
			if(RT_OK == userContext_->getSyncFileSystemMgr()->isExist(path, ADAPTER_FOLDER_TYPE_REST))
			{			
				loadData(browseHistory_[curPage_], true);
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

		void uploadClick(TNotifyUI& msg);

		void SetMyFileListVisible(bool bVisible = true)
		{
			CVerticalLayoutUI*	pVertical	= static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(L"myFile_noFiles"));
			CVerticalLayoutUI*	pHorizontal = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(L"myFile_files"));
			
			if(NULL == pVertical || NULL == pHorizontal)
			{
				return;
			}
			if(!bVisible)
			{			
				pVertical->SetVisible(true);
				pHorizontal->SetVisible(false);
				return;
			}
			else
			{
				pVertical->SetVisible(false);
				pHorizontal->SetVisible(true);
			}
		}

		void createClick(TNotifyUI& msg)
		{
			SetMyFileListVisible();
			MyFilesDirNode fileDirNode = browseHistory_[curPage_];
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
			fileNode.parent = fileDirNode.fileData.basic.id;
			fileNode.type = FILE_TYPE_DIR;
			fileNode.ctime = currentTime;
			fileNode.mtime = currentTime;
			fileNode.size = 0;
			fileNode.flags = OBJECT_FLAG_SYNC;
			Path path = userContext_->getPathMgr()->makePath();
			path.parent(fileNode.parent);
			userContext_->getSyncFileSystemMgr()->getNewName(path, fileNode.name);

			if (fileDirNode.isListView)
			{
				CDialogBuilder builder;
				Onebox::MyFilesListContainerElement* pFileListItem = static_cast<Onebox::MyFilesListContainerElement*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_MY_FILE_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == pFileListItem) return;

				pFileListItem->fillData(fileNode, userContext_);
				pFileListItem->initUI();

				if (!myFileList->AddAt(pFileListItem,0))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create dir node failed.");
					return;
				}
				myFileList->HomeUp();
				myFileList->SelectAllItem(false);
				pFileListItem->rename();
			}
			else
			{
				CDialogBuilder builder;
				Onebox::MyFilesTileLayoutListContainerElement* pFileListItem = static_cast<Onebox::MyFilesTileLayoutListContainerElement*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_MY_FILE_TILELAYOUT_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == pFileListItem) return;

				pFileListItem->fillData(fileNode, userContext_);
				pFileListItem->initUI();

				if (!myFileListLargeIcon->AddAt(pFileListItem,0))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create dir node failed.");
					return;
				}			
				myFileListLargeIcon->HomeUp();
				myFileListLargeIcon->SelectAllItem(false);
				pFileListItem->rename();
			}
			setTotalText(MYFILE_COUNT_DEFAULT);
		}

		void shareClick(TNotifyUI& msg)
		{
			getUIFileNode();
			if(1!=selectedItems_.size()) return;
			isShowModal_ = true;
			Onebox::ShareFrame* myShareFrame =  new Onebox::ShareFrame(userContext_, selectedItems_.begin()->second, paintManager_);
			myShareFrame->ShowSharedFrame();
			delete myShareFrame;
			myShareFrame = NULL;
			loadMetaData(browseHistory_[curPage_]);
			isShowModal_ = false;
		}

		void downloadClick(TNotifyUI& msg);

		void moreClick(TNotifyUI& msg)
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

		void searchClick(TNotifyUI& msg)
		{
			MyFilesDirNode curDirNode = browseHistory_[curPage_];
			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYFILE_SEARCHTXT));
			curDirNode.keyWord = searchtxt->GetText();
			curDirNode.fileData.basic.id = 0;
			boost::algorithm::trim(curDirNode.keyWord);
	//		if(curDirNode.keyWord.empty()) return;
			loadData(curDirNode, false);
		}

		void listSelectchanged();

		void largeIconSelectchanged();

		void loadData(MyFilesDirNode& myFilesDirNode, bool isHistory, bool isFlush = false)
		{
			SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("MyFileMgrImpl::loadData MyFilesDirNode id:%I64d, name:%s",
				myFilesDirNode.fileData.basic.id, (String::wstring_to_string(myFilesDirNode.fileData.basic.name)).c_str()));
			
			if(!(isHistory||browseHistory_[curPage_]==myFilesDirNode))
			{
				for(;curPage_<browseHistory_.size()-1;)
				{
					browseHistory_.erase(--browseHistory_.end());
				}
				browseHistory_.push_back(myFilesDirNode);
				++curPage_;
			}

			curPageParam_.offset = 0;
			selectedItems_.clear();
			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
			if((!browseHistory_[curPage_].isListView)&&browseHistory_[curPage_].keyWord.empty())
			{
				pageLimit_ = 2*pageLimit_;
			}
			curPageParam_.limit = pageLimit_;
			loadMetaDataAsyc(myFilesDirNode, isFlush);
			parentDirInfo_ = myFilesDirNode.fileData.basic;
			showButton();
			reShowPath(myFilesDirNode);

			//reset tab
			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(MYFILE_LISTTAB));
			if(myFilesDirNode.isListView)
			{
				COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(MYFILE_LIST));
				pOption->Selected(true);
				pControl->SelectItem(myFilesDirNode.keyWord.empty()?0:2);
			}
			else
			{
				COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(MYFILE_LARGEICON));
				pOption->Selected(true);
				pControl->SelectItem(1);
			}

			setTotalText(MYFILE_COUNT_DEFAULT);
		}

		void reShowPath(MyFilesDirNode& myFilesDirNode)
		{
			CGroupButtonUI* pContainer = static_cast<CGroupButtonUI*>(paintManager_.FindControl(MYFILE_GROUPBTN));
			if(NULL == pContainer) return;

			//get new path
			std::list<PathNode> pathNodes;
			if(browseHistory_[curPage_].keyWord.empty())
			{
				userContext_->getShareResMgr()->getFilePathNodes(myFilesDirNode.fileData.basic.id, pathNodes);
				PathNode node;
				node.fileId = myFilesDirNode.fileData.basic.id;
				node.fileName = myFilesDirNode.fileData.basic.name;
				if(node.fileName.empty())
				{
					Path remotePath = userContext_->getPathMgr()->makePath();
					remotePath.id(node.fileId);
					FILE_DIR_INFO fileNode;
					(void)userContext_->getSyncFileSystemMgr()->getProperty(remotePath, fileNode, ADAPTER_FOLDER_TYPE_REST);
					browseHistory_[curPage_].fileData.basic.name = fileNode.name;
					node.fileName = fileNode.name;
				}
				pathNodes.push_back(node);	
			}
			else
			{
				//set search path as root
				PathNode node;
				node.fileId = 0;
				pathNodes.push_back(node);	
			}
			if(!pathNodes.empty())
			{
				if(0==pathNodes.begin()->fileId)
				{
					pathNodes.begin()->fileName = iniLanguageHelper.GetCommonString(MSG_MYFILE_BASENAME_KEY);
				}
			}
			pContainer->showPath(MYFILE_MODULE_NAME, pathNodes);
		}

		void listPage(MyFilesDirNode& myFilesDirNode, bool isFlush, LIST_FOLDER_RESULT& lfResult, std::map<int64_t, std::wstring>& pathInfo)
		{
			if(myFilesDirNode.keyWord.empty())
			{
				Path listPath = userContext_->getPathMgr()->makePath();
				listPath.id(myFilesDirNode.fileData.basic.id);
				ProxyMgr::getInstance(userContext_)->listPage(userContext_, listPath, lfResult, curPageParam_, curCnt_, isFlush);
				if(curCnt_ <= 0)
				{
					m_ListCount = 0;
				}
				else
				{
					m_ListCount = (size_t)curCnt_;	
				}
			}
			else
			{
				userContext_->getShareResMgr()->search(SD::Utility::String::wstring_to_utf8(myFilesDirNode.keyWord), lfResult, curPageParam_, curCnt_,
					myFilesDirNode.isListView, pathInfo);
			}
			isLoading_ = false;
		}

		void loadMetaDataAsyc(MyFilesDirNode& myFilesDirNode, bool isFlush = false)
		{
			LIST_FOLDER_RESULT lfResult;
			std::map<int64_t, std::wstring> pathInfo;
			if(isNextPage_)
			{
				isNextPage_ = false;
			}
			else
			{
				curPageParam_.offset = 0;
			}
			curPageParam_.orderList.clear();

			CTextUI* cntText = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_count")));			
			CTextUI* totalText = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_Total")));
			CCustomListUI* myNullList = static_cast<CCustomListUI*>(paintManager_.FindControl(_T("myFile_noFiles")));			
			if (NULL == cntText || NULL == totalText || NULL == myNullList) return;
			if(myFilesDirNode.keyWord.empty())
			{
				curPageParam_.orderList.push_back(m_oldOrderParam);
			}
			else
			{
				OrderParam order;
				order.field	= SEARCH_SORT_TYPE;
				order.direction = SORT_DIRECTION_ASC;
				curPageParam_.orderList.push_back(order);
				order.field	= SEARCH_SORT_FILED_TIME;
				order.direction = SORT_DIRECTION_DESC;
				curPageParam_.orderList.push_back(order);
				myNullList->SetVisible(false);
				myFileListLargeIcon->SetVisible(false);
				myFileSearchList->SetVisible(false);
				myFileList->SetVisible(false);
				cntText->SetVisible(false);
				totalText->SetVisible(false);
			}

			isLoading_ = true;
			loadThread_ = boost::thread(boost::bind(&MyFileMgrImpl::listPage, this, myFilesDirNode, isFlush, boost::ref(lfResult), boost::ref(pathInfo)));
			int32_t timeCnt = 0;
			while(isLoading_ && timeCnt<delayLoading_)
			{
				boost::this_thread::sleep(boost::posix_time::milliseconds(10));
				timeCnt += 10;
			}
			if(isLoading_)
			{
				isShowModal_ = true;
				DelayLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(), myFileList->GetPos(), loadThread_);
				isShowModal_ = false;
			}

			SetMyFileListVisible(!(0 == lfResult.size()));	

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(MYFILE_LISTTAB));
			if (NULL == pControl) return;
			if(myFilesDirNode.isListView)
			{
				if(myFilesDirNode.keyWord.empty())
				{
					pControl->SelectItem(0);
					loadMyFileList(lfResult);
					if (myFileList)
					{
						totalText->SetVisible(true);
						myFileList->SetVisible(true);
						myFileList->SetFocus();
					}
				}
				else
				{
					pControl->SelectItem(2);
					loadSearchResult(lfResult, pathInfo);
					if (myFileSearchList)
					{
						cntText->SetVisible(true);
						myFileSearchList->SetVisible(true);
						myFileSearchList->SetFocus();
					}
				}
			}
			else
			{
				pControl->SelectItem(1);
				loadFileListLargeIcon(lfResult);
				if (myFileListLargeIcon)
				{
					myFileListLargeIcon->SetVisible(true);
					myFileListLargeIcon->SetFocus();
				}
			}
			setTotalText(MYFILE_COUNT_DEFAULT);
		}

		void loadMetaData(MyFilesDirNode& myFilesDirNode, bool isFlush = false)
		{
			Path listPath = userContext_->getPathMgr()->makePath();
			listPath.id(myFilesDirNode.fileData.basic.id);
			LIST_FOLDER_RESULT lfResult;
			std::map<int64_t, std::wstring> pathInfo;
			if(isNextPage_)
			{
				isNextPage_ = false;
			}
			else
			{
				curPageParam_.offset = 0;
			}
			curPageParam_.orderList.clear();
			CTextUI* cntText = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_count")));			
			CTextUI* totalText = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_Total")));
			CCustomListUI* myNullList = static_cast<CCustomListUI*>(paintManager_.FindControl(_T("myFile_noFiles")));			
			if (NULL == cntText || NULL == totalText || NULL == myNullList) return;
			if(myFilesDirNode.keyWord.empty())
			{
				curPageParam_.orderList.clear();
				curPageParam_.orderList.push_back(m_oldOrderParam);
				ProxyMgr::getInstance(userContext_)->listPage(userContext_, listPath, lfResult, curPageParam_, curCnt_, isFlush);
				if(curCnt_ <= 0)
				{
					m_ListCount = 0;
				}
				else
				{
					m_ListCount = (size_t)curCnt_;	
				}	
			}
			else
			{				
				OrderParam order;
				order.field	= SEARCH_SORT_TYPE;
				order.direction = SORT_DIRECTION_ASC;
				curPageParam_.orderList.push_back(order);
				order.field	= SEARCH_SORT_FILED_TIME;
				order.direction = SORT_DIRECTION_DESC;
				curPageParam_.orderList.push_back(order);
				myNullList->SetVisible(false);
				myFileListLargeIcon->SetVisible(false);
				myFileSearchList->SetVisible(false);
				myFileList->SetVisible(false);
				cntText->SetVisible(false);
				totalText->SetVisible(false);
				userContext_->getShareResMgr()->search(SD::Utility::String::wstring_to_utf8(myFilesDirNode.keyWord), lfResult, curPageParam_, curCnt_,
					myFilesDirNode.isListView, pathInfo);		
			}

			SetMyFileListVisible(!(0 == lfResult.size()));	

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(MYFILE_LISTTAB));
			if (NULL == pControl) return;
			if(myFilesDirNode.isListView)
			{
				if(myFilesDirNode.keyWord.empty())
				{
					pControl->SelectItem(0);
					loadMyFileList(lfResult);
					if (myFileList)
					{
						totalText->SetVisible(true);
						myFileList->SetVisible(true);
						myFileList->SetFocus();
					}
				}
				else
				{
					pControl->SelectItem(2);
					loadSearchResult(lfResult, pathInfo);
					if (myFileSearchList)
					{
						cntText->SetVisible(true);
						myFileSearchList->SetVisible(true);
						myFileSearchList->SetFocus();
					}
				}
			}
			else
			{
				pControl->SelectItem(1);
				loadFileListLargeIcon(lfResult);
				if (myFileListLargeIcon)
				{
					myFileListLargeIcon->SetVisible(true);
					myFileListLargeIcon->SetFocus();
				}
			}

			setTotalText(MYFILE_COUNT_DEFAULT);
		}

		void loadMyFileList(const LIST_FOLDER_RESULT& lfResult)
		{
			if(NULL == myFileList) return; 

			if (myFileList->GetCount()>0 && 0==curPageParam_.offset)
			{
				myFileList->RemoveAll();
			}

			for (LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				CDialogBuilder builder;
				Onebox::MyFilesListContainerElement* node = static_cast<Onebox::MyFilesListContainerElement*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_MY_FILE_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;
				// fill in file node information
				node->fillData(*it, userContext_);
				node->initUI();

				if (!myFileList->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void loadSearchResult(const LIST_FOLDER_RESULT& lfResult, std::map<int64_t, std::wstring>& pathInfo)
		{
			if(NULL == myFileSearchList) return; 

			if (myFileSearchList->GetCount()>0 && 0==curPageParam_.offset)
			{
				myFileSearchList->RemoveAll();
			}

			for (LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				CDialogBuilder builder;
				Onebox::MyFilesListContainerElement* node = static_cast<Onebox::MyFilesListContainerElement*>(
					builder.Create(L"myFileSearchListItem.xml",	L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;
				// fill in file node information
				node->fillData(*it, userContext_);
				std::wstring path;
				std::map<int64_t, std::wstring>::iterator pathIt = pathInfo.find(it->id);
				if(pathIt!=pathInfo.end())
				{
					path = pathIt->second;
				}
				else
				{
					userContext_->getShareResMgr()->getFilePath(it->id, path);
				}
				node->initUI(path);

				if (!myFileSearchList->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void loadFileListLargeIcon(const LIST_FOLDER_RESULT& lfResult)
		{
			if (myFileListLargeIcon == NULL) return;
			if (myFileListLargeIcon->GetCount()>0 && 0==curPageParam_.offset)
			{
				myFileListLargeIcon->RemoveAll();
			}

			for (LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				CDialogBuilder builder;
				Onebox::MyFilesTileLayoutListContainerElement* node = static_cast<Onebox::MyFilesTileLayoutListContainerElement*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_MY_FILE_TILELAYOUT_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;;

				// fill in file node information
				node->fillData(*it, userContext_);
				node->initUI();

				if (!myFileListLargeIcon->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void nameHeaderClick()
		{
			HeaderClickSort(pBtnName, CMD_ROW_NAME);
		}

		void sizeHeaderClick()
		{
			HeaderClickSort(pBtnSize, CMD_ROW_SIZE);
		}

		void ctimeHeaderClick()
		{
			HeaderClickSort(pBtnCtime, CMD_ROW_MTIME);
		}

		void HeaderClickSort(CButtonUI *pControl,const std::string& name)
		{
			if (NULL==pBtnName||NULL==pBtnSize||NULL==pBtnCtime) return;

			OrderParam orderParam;
			orderParam.field = name;
			if (pControl->IsVisible() && (_tcsicmp(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,0,6,6'"),pControl->GetNormalImage()) == 0))
			{
				pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,0,6,6'"));
				pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,16,6,22'"));
				pControl->SetPushedImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,16,6,22'"));
				orderParam.direction = "desc";
			} 
			else
			{
				pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,0,6,6'"));
				pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,16,6,22'"));
				pControl->SetPushedImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,16,6,22'"));
				orderParam.direction = " asc";
			}
			m_oldOrderParam = orderParam;

			pBtnName->SetVisible(CMD_ROW_NAME==name);
			pBtnSize->SetVisible(CMD_ROW_SIZE==name);
			pBtnCtime->SetVisible(CMD_ROW_MTIME==name);

			curPageParam_.orderList.clear();
			curPageParam_.orderList.push_back(orderParam);
			loadMetaData(browseHistory_[curPage_]);
		}

		void listItemMouseEnter(TNotifyUI& msg)
		{
			MyFilesListContainerElement* pControl =  static_cast<MyFilesListContainerElement*>(msg.pSender);
			if( pControl == NULL)  return;

			if (myFileList != NULL) myFileList->setCurEnter(pControl->GetIndex());
			if (NULL == pControl->GetInterface(DUI_CTR_LISTITEM)) return;

			if (myFileList != NULL && myFileList->isDragFileList())
			{
				return;
			}
			pControl->mouseEnter();
		}

		void listItemMouseLeave(TNotifyUI& msg)
		{
			MyFilesListContainerElement* pControl =  static_cast<MyFilesListContainerElement*>(msg.pSender);
			if( pControl == NULL)  return;
			if (myFileList != NULL && !(myFileList->isDragFileList())) myFileList->setCurEnter(-1);
			if (NULL == pControl->GetInterface(DUI_CTR_LISTITEM)) return;

			pControl->mouseLeave();
		}

		void dbclick(TNotifyUI& msg)
		{
			getUIFileNode();
			if(1 != selectedItems_.size()) return;
			UIFileNode curItem = selectedItems_.begin()->second;
			if (FILE_TYPE::FILE_TYPE_DIR == curItem.basic.type)
			{
				//open folder
				MyFilesDirNode fileDirNode;
				fileDirNode.ownerId = userContext_->id.id;
				fileDirNode.isListView = browseHistory_[curPage_].isListView;
				fileDirNode.fileData = curItem;
				loadData(fileDirNode, false);
				setTotalText(MYFILE_COUNT_DEFAULT);
			}
			else if (FILE_TYPE::FILE_TYPE_FILE == curItem.basic.type)
			{
				bool isOpen = dbClickEnabled(curItem.basic.name);
				OpenFileDbClickDialog* pDialog = new OpenFileDbClickDialog(userContext_, curItem, isOpen);
				pDialog->Create(paintManager_.GetPaintWindow(),_T("OpenFileDbClickDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
				pDialog->CenterWindow();
				isShowModal_ = true;
				UINT tmp = pDialog->ShowModal();
				isShowModal_ = false;
				delete pDialog;
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
					itemList.push_back(curItem);
					(void)TransTaskMgr::download(itemList, path, true);
				}
				else if (tmp == 2) // save
				{
					std::map<int64_t, UIFileNode> itemList;
					itemList.insert(std::make_pair(curItem.basic.id, curItem));
					download(itemList);
				}
			}
		}

		void fileShareClick(TNotifyUI& msg);

		void fileShareLinkClick(TNotifyUI& msg);

		void fileIsSyncClick(TNotifyUI& msg);

		void searchPathClick(TNotifyUI& msg);

		void selectAllItemClick(TNotifyUI& msg)
		{
			if (browseHistory_[curPage_].isListView)
			{
				if(browseHistory_[curPage_].keyWord.empty())
				{
					if (NULL == myFileList) return;
					myFileList->SelectAllItem(true);
				}
				else
				{
					if (NULL == myFileSearchList) return;
					myFileSearchList->SelectAllItem(true);
				}
			}
			else
			{
				if (NULL == myFileListLargeIcon) return;
				myFileListLargeIcon->SelectAllItem(true);
			}
			msg.wParam = true;
			if(browseHistory_[curPage_].keyWord.empty())
			{
				selectAllClick(msg);
			}
		}

		void selectAllClick(TNotifyUI& msg)
		{
			selectedItems_.clear();
			if(msg.wParam==0) return;

			//选中全页对象
			Path listPath = userContext_->getPathMgr()->makePath();
			listPath.id(browseHistory_[curPage_].fileData.basic.id);
			LIST_FOLDER_RESULT lfResult;
			ProxyMgr::getInstance(userContext_)->listAll(userContext_, listPath, lfResult);

			for(LIST_FOLDER_RESULT::const_iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				UIFileNode fileNode;
				fileNode.basic = *it;
				fileNode.userContext = userContext_;
				selectedItems_.insert(std::make_pair(fileNode.basic.id, fileNode));
			}
		}

		void nameReturn(TNotifyUI& msg)
		{
			isRenaming_ = true;
			MyFilesDirNode fileDirNode = browseHistory_[curPage_];

			CRenameRichEditUI *richEdit = static_cast<CRenameRichEditUI*>(msg.pSender);
			if (NULL == richEdit) return;
			if (NULL == richEdit->GetParent()) return;
			CMyFileElementUI* pFileListItem = static_cast<CMyFileElementUI*>(richEdit->GetParent()->GetParent());		
			if (NULL == pFileListItem) return;
			int tipType = -1;
			std::wstring str_des = L"";
			std::wstring str_newName = richEdit->GetText();
			str_newName = SD::Utility::String::replace_all(str_newName,L"\r",L" ");

			if (-1 ==pFileListItem->m_uNodeData.basic.id)
			{
				Path path = userContext_->getPathMgr()->makePath();
				path.id(fileDirNode.fileData.basic.id);
				pFileListItem->m_uNodeData.basic.name = str_newName;
				FILE_DIR_INFO fdi = pFileListItem->m_uNodeData.basic;
				//int32_t ret = userContext_->getSyncFileSystemMgr()->create(path,str_newName,fdi,ADAPTER_FOLDER_TYPE_REST);
				int32_t ret = ProxyMgr::getInstance(userContext_)->create(userContext_, path,str_newName,fdi,ADAPTER_FOLDER_TYPE_REST);
				if (RT_OK == ret)
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
						myFileList->Remove(pFileListItem);
					}
					else
					{
						myFileListLargeIcon->Remove(pFileListItem);
					}
					if (RT_DIFF_FILTER == ret || RT_FILE_EXIST_ERROR == ret)
					{
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						simlpeNoticeFrame->Show(Error, MSG_CREATEDIR_FAILED_EX_KEY,  
							ErrorConfMgr::getInstance()->getDescription(ret).c_str());
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
				if (str_newName == pFileListItem->m_uNodeData.basic.name)
				{
					return;
				}
				Path path = userContext_->getPathMgr()->makePath();
				path.id(pFileListItem->m_uNodeData.basic.id);
				ADAPTER_FILE_TYPE type = (pFileListItem->m_uNodeData.basic.type == FILE_TYPE_DIR)?ADAPTER_FOLDER_TYPE_REST : ADAPTER_FILE_TYPE_REST;
				int32_t ret = ProxyMgr::getInstance(userContext_)->rename(userContext_, path, str_newName, type);
				if (RT_OK == ret)
				{
					tipType = Right;
					str_des =  MSG_RENAME_SUCCESSFUL_KEY;
					pFileListItem->m_uNodeData.basic.name = str_newName;
					richEdit->SetToolTip(str_newName.c_str());
					if(!fileDirNode.isListView)
					{
						pFileListItem->SetToolTip(str_newName.c_str());
					}
				}
				else
				{
					richEdit->SetText(pFileListItem->m_uNodeData.basic.name.c_str());
					richEdit->SetToolTip(pFileListItem->m_uNodeData.basic.name.c_str());
					if(!fileDirNode.isListView)
					{
						pFileListItem->SetToolTip(pFileListItem->m_uNodeData.basic.name.c_str());
					}
					if (RT_DIFF_FILTER == ret || RT_FILE_EXIST_ERROR == ret)
					{
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						if(NULL == simlpeNoticeFrame)
						{
							return;
						}
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
			}
			//loadMetaData(browseHistory_[curPage_]);
			SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
			simlpeNoticeFrame->Show((NoticeType)tipType, str_des.c_str());
			delete simlpeNoticeFrame;
			simlpeNoticeFrame = NULL;
		}

		void showButton()
		{
			if(isNextPage_) return;

			CButtonUI* backBtn = static_cast<CButtonUI*>(paintManager_.FindControl(L"myFile_back"));
			CButtonUI* nextBtn = static_cast<CButtonUI*>(paintManager_.FindControl(L"myFile_next"));
			bool isFirst = (curPage_==0);
			bool isLast = (curPage_+1==browseHistory_.size());
			backBtn->SetEnabled(!isFirst);
			nextBtn->SetEnabled(!isLast);

			CButtonUI* uploadBtn = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_upload")));
			CButtonUI* createBtn = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_create")));
			CButtonUI* shareBtn = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_share")));
			CButtonUI* downloadBtn = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_download")));
			CButtonUI* moreBtn = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_more")));
			CTextUI* cntText = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_count")));
			CTextUI* cntTextC = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_Total")));
			CCustomListUI*	pNoFiles	= static_cast<CCustomListUI*>(paintManager_.FindControl(L"myFile_noFiles"));
			
			if(NULL == uploadBtn 
				|| NULL == createBtn 
				|| NULL == moreBtn 
				|| NULL == shareBtn 
				|| NULL == downloadBtn 
				|| NULL == cntText 
				|| NULL == cntTextC 
				|| NULL == pNoFiles)
			{
				return;
			}

			if(pNoFiles->Activate())
			{
				pNoFiles->SetFocus();
			}

			getUIFileNode();
			if(selectedItems_.empty() || pNoFiles->Activate())	//hidden all
			{
				shareBtn->SetVisible(false);
				downloadBtn->SetVisible(false);
				moreBtn->SetVisible(false);				
			}
			else
			{
				shareBtn->SetVisible(1 == selectedItems_.size());
				downloadBtn->SetEnabled(true);
				moreBtn->SetEnabled(true);
				downloadBtn->SetVisible(true);
				moreBtn->SetVisible(true);
			}
			
			RemoveNoSelectItem();

			if(1 < selectedItems_.size())
			{
				for(std::map<int64_t, UIFileNode>::const_iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
				{			
					if((FOLDER_ICON_COMPUTER == it->second.basic.extraType) || (FOLDER_ICON_DISK == it->second.basic.extraType))
					{
						moreBtn->SetVisible(false);
						break;
					}
				}
			}

			uploadBtn->SetEnabled(true);
			createBtn->SetEnabled(true);

			cntText->SetVisible(false);
			uploadBtn->SetVisible(true);
			createBtn->SetVisible(true);

			if(!browseHistory_[curPage_].keyWord.empty())
			{
				uploadBtn->SetVisible(false);
				createBtn->SetVisible(false);
				if(selectedItems_.empty())
				{
					shareBtn->SetVisible(false);
					downloadBtn->SetVisible(false);
					moreBtn->SetVisible(false);
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
					cntTextC->SetVisible(false);
				}
			}
			else
			{
				if(!m_IfLageIcon)
				{
					cntTextC->SetVisible(true);	
				}					
			}

			showBackupButton();
		}

		void itemDragMove(TNotifyUI& msg)
		{
			CMyFileElementUI* pm = NULL;
			if(browseHistory_[curPage_].isListView)
			{
				pm = static_cast<CMyFileElementUI*>(myFileList->GetItemAt(myFileList->getCurEnter()));
			}
			else
			{
				pm = static_cast<CMyFileElementUI*>(myFileListLargeIcon->GetItemAt(myFileListLargeIcon->getCurEnter()));
			}	
			if(pm==NULL) return;
						
			getUIFileNode();
			if(selectedItems_.empty()) return;			
			if(selectedItems_.find(pm->m_uNodeData.basic.id) != selectedItems_.end() || FILE_TYPE_FILE == pm->m_uNodeData.basic.type)  return;
			
			m_noticeFrame_->Run(Choose, Ask, MSG_MOVECONFIRM_TITLE_KEY, MSG_MOVECONFIRM_NOTICE_KEY, Modal);
			if (!m_noticeFrame_->IsClickOk()) return;

			bool isRename = true;
			if (checkFileIsRename(selectedItems_, pm->m_uNodeData.basic.id, userContext_))
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
			addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, userContext_->getUserInfoMgr()->getUserId(), pm->m_uNodeData.basic.id, RESTTASK_MOVE, isRename);			
		}

		void itemDragFile(TNotifyUI& msg);		

		void versionClick(TNotifyUI& msg);

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

		void getUIFileNode()
		{
			selectedItems_.clear();
			if(browseHistory_[curPage_].isListView)
			{
				CCustomListUI* myFile = browseHistory_[curPage_].keyWord.empty()?myFileList:myFileSearchList;
				if(NULL == myFile) return;
				CStdValArray* curSelects = myFile->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CMyFileElementUI* elementUI = static_cast<CMyFileElementUI*>(myFile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == elementUI) continue;
					UIFileNode fileNode;
					fileNode = elementUI->m_uNodeData;
					if (-1 == fileNode.basic.id)continue;					
					selectedItems_.insert(std::make_pair(fileNode.basic.id, fileNode));
				}
			}
			else
			{
				CTileLayoutListUI* myFile = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(MYFILE_TILELAYOUT_LISTVIEW));
				if(NULL == myFile) return;
				CStdValArray* curSelects = myFile->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CMyFileElementUI* elementUI = static_cast<CMyFileElementUI*>(myFile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == elementUI) continue;
					UIFileNode fileNode;
					fileNode = elementUI->m_uNodeData;
					if (-1 == fileNode.basic.id)continue;
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

		void deleteAllClick();

		void nextPage(TNotifyUI& msg)
		{
			int32_t range = msg.wParam;
			int32_t pos = msg.lParam;
			//无下一页，或未滚到倒数SCROLL_LIMEN条以下，不处理
			if((!browseHistory_[curPage_].keyWord.empty())
				||(curPageParam_.offset + curPageParam_.limit >= curCnt_) 
				||(pos < range - MYFILE_SCROLL_LIMEN)) return;

			curPageParam_.offset += pageLimit_;
			isNextPage_ = true;
			loadMetaData(browseHistory_[curPage_]);
		}

		void addRestTask(int64_t srcOwnerId, const std::list<int64_t>& srcNodeList, int64_t destOwnerId, int64_t destFolderId, const std::string& type, bool autoRename = false)
		{
			int32_t ret = userContext_->getRestTaskMgr()->addRestTask(srcOwnerId, browseHistory_[curPage_].fileData.basic.id, srcNodeList, destOwnerId, destFolderId, type, autoRename);
			if(RT_RESTTASK_DOING==ret)
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				simlpeNoticeFrame->Show(Error, MSG_RESTTASK_START_DOING, 
					simlpeNoticeFrame->GetShowMsg(SD::Utility::String::string_to_wstring(userContext_->getRestTaskMgr()->getLastType())).c_str());
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

		virtual void setPageFocus()
		{
			if( browseHistory_[curPage_].isListView)
			{
				if (myFileList)
				{
					myFileList->SetFocus();
				}
			}
			else
			{
				if (myFileListLargeIcon)
				{
					myFileListLargeIcon->SetFocus();
				}
			}
		}

		void RemoveNoSelectItem()
		{
			CStdValArray* pSelectArray = myFileList->GetSelects();
			for (int i = 0; i < pSelectArray->GetSize(); ++i)
			{
				bool ifExist = false;
				if(NULL==pSelectArray->GetAt(i)) continue;
				CMyFileElementUI* elementUI = static_cast<CMyFileElementUI*>(myFileList->GetItemAt(*(int*)pSelectArray->GetAt(i)));
				if(NULL == elementUI) continue;
				for(std::map<int64_t, UIFileNode>::const_iterator it = selectedItems_.begin(); it != selectedItems_.end();++it)
				{
					if((selectedItems_.find(elementUI->m_uNodeData.basic.id) == selectedItems_.end()) 
						|| !elementUI->IsSelected())
					{
						if(elementUI->m_uNodeData.basic.id == it->second.basic.id)
						{
							selectedItems_.erase(it);
							break;
						}
					}
				}
			}
		}

		void menuAdded(TNotifyUI& msg)
		{
			if (NULL == msg.pSender)
			{
				return;
			}

			/*getUIFileNode();*/

			CMenuUI* pMenu = (CMenuUI*)msg.wParam;
			if (NULL == pMenu) return;

			doMenuPoint(pMenu, msg.ptMouse);

			// selected none
			if (selectedItems_.empty())
			{
				pMenu->SetVisible(false);
			}
			// selected multi items
			else if (selectedItems_.size() > 1)
			{
				bool ifShow = false;
				for(std::map<int64_t, UIFileNode>::const_iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
				{			
					if((FOLDER_ICON_COMPUTER == it->second.basic.extraType) || (FOLDER_ICON_DISK == it->second.basic.extraType))
					{
						ifShow = true;
						break;
					}
				}
				// the max menu id is 10
				// only enable: 
				// Download (id = 3)
				// Transmit To Team Space (id = 6)
				// Copy/Move (id = 7)
				// Delete (id = 8)
				CMenuItemUI* pMenuItem = NULL;
				for (int i = 0; i <= 10; ++i)
				{
					if(ifShow)
					{
						if(i == 3)
						{
							continue;
						}
					}
					else
					{
						if (i == 3 || i == 6 || i == 7 || i == 8)
						{
							continue;
						}
					}
					pMenuItem = pMenu->GetMenuItemById(i);
					if (NULL == pMenuItem) continue;
					pMenuItem->SetVisible(false);
				}
			}
			// selected a item
			else 
			{
				CMenuItemUI* pMenuItem = NULL;
				UIFileNode fileNode = selectedItems_.begin()->second;
				// file do not have open and sync menu
				if (fileNode.basic.type == FILE_TYPE_FILE)
				{
					// open
					pMenuItem = pMenu->GetMenuItemById(0);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);

					if (fileNode.basic.parent != ROOT_PARENTID) 
					{
						// cancel sync
						pMenuItem = pMenu->GetMenuItemById(4);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						// set sync
						pMenuItem = pMenu->GetMenuItemById(5);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					}
					else
					{
						if ((fileNode.basic.flags&OBJECT_FLAG::OBJECT_FLAG_SYNC) == 0)
						{
							// cancel sync
							pMenuItem = pMenu->GetMenuItemById(4);
							if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						}
						else
						{
							// set sync
							pMenuItem = pMenu->GetMenuItemById(5);
							if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						}
					}
				}
				// folder do not have version menu
				else 
				{
					// version
					pMenuItem = pMenu->GetMenuItemById(10);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					// only the folder in the root have sync menu
					if (fileNode.basic.parent != ROOT_PARENTID) 
					{
						// cancel sync
						pMenuItem = pMenu->GetMenuItemById(4);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						// set sync
						pMenuItem = pMenu->GetMenuItemById(5);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					}
					else 
					{
						if ((fileNode.basic.flags&OBJECT_FLAG::OBJECT_FLAG_SYNC) == 0)
						{
							// cancel sync
							pMenuItem = pMenu->GetMenuItemById(4);
							if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						}
						else
						{
							// set sync
							pMenuItem = pMenu->GetMenuItemById(5);
							if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						}
					}

					if((FOLDER_ICON_COMPUTER == fileNode.basic.extraType) || (FOLDER_ICON_DISK == fileNode.basic.extraType)) 
					{
						// cancel sync
						pMenuItem = pMenu->GetMenuItemById(4);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						// set sync
						pMenuItem = pMenu->GetMenuItemById(5);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						// save to teamspace
						pMenuItem = pMenu->GetMenuItemById(6);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						// copy/move
						pMenuItem = pMenu->GetMenuItemById(7);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
						// delete
						pMenuItem = pMenu->GetMenuItemById(8);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					}
				}
			}
		}

		void menuItemClick(TNotifyUI& msg)
		{
			int menuId = msg.lParam;
			//getUIFileNode();
			if(0 == selectedItems_.size()) return;
			UIFileNode curItem = selectedItems_.begin()->second;
			if (menuId < 0)
			{
				return;
			}
			switch (menuId)
			{
				// open
			case 0:
				{
					MyFilesDirNode fileDirNode;
					fileDirNode.ownerId = userContext_->id.id;
					fileDirNode.isListView = browseHistory_[curPage_].isListView;
					fileDirNode.fileData = curItem;
					loadData(fileDirNode, false);
				}
				break;
				// share
			case 1:
				{
					shareClick(TNotifyUI());
					loadMetaData(browseHistory_[curPage_]);
				}
				break;
				// share link
			case 2:
				{
				}
				break;
				// download
			case 3:
				downloadClick(TNotifyUI());
				break;
				// cancel sync
			case 4:
				// set sync
			case 5:
				{
					m_noticeFrame_->Run(Choose,Ask,MSG_SYNCSTTINGS_TITLE_KEY,
					(5 == menuId)?MSG_SYNCSETTINGS_SETTING_KEY:MSG_SYNCSETTINGS_CANCEL_KEY,Modal);
					if (m_noticeFrame_->IsClickOk())
					{
						int32_t ret = -1;
						for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
						{
							Path path = it->second.userContext->getPathMgr()->makePath();
							path.id(it->second.basic.id);
							path.parent(it->second.basic.parent);
							path.type((FILE_TYPE)it->second.basic.type);
							ret = it->second.userContext->getShareResMgr()->setSync(path, 5 == menuId);
						}
						loadMetaData(browseHistory_[curPage_]);
						int tipType = -1;
						std::wstring str_des = L"";
						if (RT_OK == ret)
						{
							tipType = Right;
							str_des = (5 == menuId)?MSG_SETSYNC_SUCCESSFUL_KEY:MSG_CANCELSYNC_SUCCESSFUL_KEY;
						}
						else
						{
							tipType = Error;
							str_des = (5 == menuId)?MSG_SETSYNC_FAILED_KEY:MSG_CANCELSYNC_FAILED_KEY;
						}
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						simlpeNoticeFrame->Show((NoticeType)tipType, str_des.c_str());
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
					}
				}
				break;
				// transmit to team space
			case 6:
				{
					UserTeamSpaceNodeInfoArray  teamspaceListArray_;
					PageParam pageparam_;
					userContext_->getTeamSpaceMgr()->getTeamSpaceListUser(teamspaceListArray_, pageparam_);
					bool bExistTeamspace = false;
					for (size_t i = 0; i < teamspaceListArray_.size(); ++i)
					{
						if ("viewer" == teamspaceListArray_[i].role()) continue;

						bExistTeamspace = true;
						break;
					}
					if (!bExistTeamspace)
					{
						//MessageBox(paintManager_.GetPaintWindow(),L"您没有可转存的团队空间",L"转存到团队空间",MB_OK);
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						simlpeNoticeFrame->Show(Warning,MSG_NOTDEST_TEAMSPACE_KEY);
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						return;
					}

					std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
					std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_savetoteamspace").c_str();

					CommonFileDialogPtr teamspaceDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
					TeamspaceCommonFileDialogNotify *notify = new TransToTeamspaceDialogNotify(userContext_, iniLanguageHelper.GetLanguage());			
					teamspaceDialog->setOption(CFDO_only_show_folder);
					teamspaceDialog->setNotify(notify);

					isShowModal_ = true;
					if (E_CFD_CANCEL == teamspaceDialog->showModal(resultHanlder,COMMFILEDIALOG_SAVETOTEAMSPACE))
					{
						isShowModal_ = false;
						return;
					}
					isShowModal_ = false;

					if(1==commonFileData.size())
					{
						MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)(*(commonFileData.begin()))->data.get();
						if (checkFileIsRename(selectedItems_, data->id, data->userContext))
						{
							m_noticeFrame_->Run(Choose, Warning, MSG_SAVEFILE_COPYMOVE_TITLE_KEY, MSG_SAVEFILE_COPYMOVE_TEXT_KEY, Modal);
							if(!m_noticeFrame_->IsClickOk()) return;
						}
						std::list<int64_t> srcNodeList;
						for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
						{
							srcNodeList.push_back(it->first);
						}
						int64_t destFolderId = data->id;
						addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, 
							data->userContext->getUserInfoMgr()->getUserId(), destFolderId, RESTTASK_SAVE, true);
					}
				}
				break;
				// copy/move
			case 7:
				{
					std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
					std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_copymove").c_str();

					CommonFileDialogPtr myFileDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
					MyFileCommonFileDialogNotify *notify = new MyFileCommonFileDialogNotify(userContext_, iniLanguageHelper.GetLanguage());			
					if(NULL == notify) return;
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

					if(1==commonFileData.size())
					{
						MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)(*(commonFileData.begin()))->data.get();

						if (1==iControlIndex && selectedItems_.begin()->second.basic.parent == data->id)
						{
							SimpleNoticeFrame* psimlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
							psimlpeNoticeFrame->Show(Warning, MSG_SAVEFILE_COPYMOVE_FAIL_KEY);
							delete psimlpeNoticeFrame;
							psimlpeNoticeFrame = NULL;
							return;
						}

						if (checkFileIsRename(selectedItems_, data->id, data->userContext))
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
						int64_t destFolderId = data->id;
						addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, userContext_->getUserInfoMgr()->getUserId(), destFolderId, type, true);

						if(RESTTASK_MOVE == type)
						{
							setTotalText(MYFILE_COUNT_DEFAULT);
						}
					}
				}
				break;
				// delete
			case 8:
				{
					m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
					if (!m_noticeFrame_->IsClickOk()) return;
					std::list<int64_t> srcNodeList;
					for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
					{
						srcNodeList.push_back(it->first);					
					}
					addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, userContext_->getUserInfoMgr()->getUserId(), 0, RESTTASK_DELETE);
					setTotalText(MYFILE_COUNT_DEFAULT);
				}
				break;
				// rename
			case 9:
				{
					CMyFileElementUI* pFileListItem = NULL;
					if (selectedItems_.size() == 1)
					{
						if (browseHistory_[curPage_].isListView)
						{
							if(browseHistory_[curPage_].keyWord.empty())
							{
								if (NULL == myFileList) return;
								pFileListItem = static_cast<CMyFileElementUI*>(myFileList->GetItemAt(myFileList->GetCurSel()));
							}
							else
							{
								if (NULL == myFileSearchList) return;
								pFileListItem = static_cast<CMyFileElementUI*>(myFileSearchList->GetItemAt(myFileSearchList->GetCurSel()));
							}
						}
						else
						{
							if (NULL == myFileListLargeIcon) return;
							pFileListItem = static_cast<CMyFileElementUI*>(myFileListLargeIcon->GetItemAt(myFileListLargeIcon->GetCurSel()));
						}
						if (NULL == pFileListItem) return;
						pFileListItem->rename();
					}
				}
				break;
				// version
			case 10:
				{
					FileVersionDialog *pVersion = new FileVersionDialog(userContext_, curItem.basic,m_storageUserInfo.user_id);
					if(NULL != pVersion)
					{
						pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
						pVersion->CenterWindow();
						isShowModal_ = true;
						pVersion->ShowModal();
						isShowModal_ = false;
						delete pVersion;
						pVersion = NULL;
					}
				}
				break;
			default:
				break;
			}
		}

		void moreMenuAdded(TNotifyUI& msg)
		{
			if (NULL == msg.pSender)
			{
				return;
			}

			/*getUIFileNode();*/

			CMenuUI* pMenu = (CMenuUI*)msg.wParam;
			if (NULL == pMenu) return;

			// selected none or multi select
			if (selectedItems_.empty())
			{
				pMenu->SetVisible(false);
				return;
			}
			else if (selectedItems_.size() > 1)
			{
				bool ifShow = false;
				for(std::map<int64_t, UIFileNode>::const_iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
				{			
					if((FOLDER_ICON_COMPUTER == it->second.basic.extraType) || (FOLDER_ICON_DISK == it->second.basic.extraType))
					{
						ifShow = true;
						break;
					}
				}
				// the max menu id is 6
				// only enable: 
				// Transmit To Team Space (id = 2)
				// Copy/Move (id = 3)
				// Delete (id = 4)
				CMenuItemUI* pMenuItem = NULL;
				for (int i = 0; i <= 6; ++i)
				{
					if(!ifShow)
					{
						if (i == 2 || i == 3 || i == 4)
						{
							continue;
						}
					}
					pMenuItem = pMenu->GetMenuItemById(i);
					if (NULL == pMenuItem) continue;
					pMenuItem->SetVisible(false);
				}
				return;
			}

			CMenuItemUI* pMenuItem = NULL;
			UIFileNode fileNode = selectedItems_.begin()->second;
			// file do not have sync menu
			if (fileNode.basic.type == FILE_TYPE_FILE)
			{
				if (fileNode.basic.parent != ROOT_PARENTID) 
				{
					// cancel sync
					pMenuItem = pMenu->GetMenuItemById(0);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					// set sync
					pMenuItem = pMenu->GetMenuItemById(1);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
				}
				else 
				{
					if ((fileNode.basic.flags&OBJECT_FLAG::OBJECT_FLAG_SYNC) == 0)
					{
						// cancel sync
						pMenuItem = pMenu->GetMenuItemById(0);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					}
					else
					{
						// set sync
						pMenuItem = pMenu->GetMenuItemById(1);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					}
				}
			}
			// folder do not have version menu
			else 
			{
				// version
				pMenuItem = pMenu->GetMenuItemById(6);
				if (NULL != pMenuItem) pMenuItem->SetVisible(false);
				// only the folder in the root have sync menu
				if (fileNode.basic.parent != ROOT_PARENTID) 
				{
					// cancel sync
					pMenuItem = pMenu->GetMenuItemById(0);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					// set sync
					pMenuItem = pMenu->GetMenuItemById(1);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
				}
				else 
				{
					if ((fileNode.basic.flags&OBJECT_FLAG::OBJECT_FLAG_SYNC) == 0)
					{
						// cancel sync
						pMenuItem = pMenu->GetMenuItemById(0);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					}
					else
					{
						// set sync
						pMenuItem = pMenu->GetMenuItemById(1);
						if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					}
				}

				if((FOLDER_ICON_COMPUTER == fileNode.basic.extraType) || (FOLDER_ICON_DISK == fileNode.basic.extraType)) 
				{
					// cancel sync
					pMenuItem = pMenu->GetMenuItemById(0);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					// set sync
					pMenuItem = pMenu->GetMenuItemById(1);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					// save to teamspace
					pMenuItem = pMenu->GetMenuItemById(2);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					// copy/move
					pMenuItem = pMenu->GetMenuItemById(3);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
					// delete
					pMenuItem = pMenu->GetMenuItemById(4);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);
				}
			}
		}

		void moreMenuItemClick(TNotifyUI& msg)
		{
			int menuId = msg.lParam;
			if (menuId < 0) return;
			/*getUIFileNode();*/
			if(0 == selectedItems_.size()) return;
			UIFileNode curItem = selectedItems_.begin()->second;
			switch (menuId)
			{
				// cancel sync
			case 0:
				// set sync
			case 1:
				{
					m_noticeFrame_->Run(Choose,Ask,MSG_SYNCSTTINGS_TITLE_KEY,
					(1 == menuId)?MSG_SYNCSETTINGS_SETTING_KEY:MSG_SYNCSETTINGS_CANCEL_KEY,Modal);
					if (m_noticeFrame_->IsClickOk())
					{
						int32_t ret = -1;
						for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
						{
							Path path = it->second.userContext->getPathMgr()->makePath();
							path.id(it->second.basic.id);
							path.parent(it->second.basic.parent);
							path.type((FILE_TYPE)it->second.basic.type);
							ret = it->second.userContext->getShareResMgr()->setSync(path, 1 == menuId);
						}
						loadMetaData(browseHistory_[curPage_]);
						int tipType = -1;
						std::wstring str_des = L"";
						if (RT_OK == ret)
						{
							tipType = Right;
							str_des = (1 == menuId)?MSG_SETSYNC_SUCCESSFUL_KEY:MSG_CANCELSYNC_SUCCESSFUL_KEY;
						}
						else
						{
							tipType = Error;
							str_des = (1 == menuId)?MSG_SETSYNC_FAILED_KEY:MSG_CANCELSYNC_FAILED_KEY;
						}
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						simlpeNoticeFrame->Show((NoticeType)tipType, str_des.c_str());
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
					}
				}
				break;
				// transmit to team space
			case 2:
				{
					UserTeamSpaceNodeInfoArray  teamspaceListArray_;
					PageParam pageparam_;
					userContext_->getTeamSpaceMgr()->getTeamSpaceListUser(teamspaceListArray_, pageparam_);
					bool bExistTeamspace = false;
					for (size_t i = 0; i < teamspaceListArray_.size(); ++i)
					{
						if ("viewer" == teamspaceListArray_[i].role()) continue;

						bExistTeamspace = true;
						break;
					}
					if (!bExistTeamspace)
					{
						//MessageBox(paintManager_.GetPaintWindow(),L"您没有可转存的团队空间",L"转存到团队空间",MB_OK);
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						simlpeNoticeFrame->Show(Warning,MSG_NOTDEST_TEAMSPACE_KEY);
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						return;
					}

					std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
					std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_savetoteamspace").c_str();

					CommonFileDialogPtr teamspaceDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
					TeamspaceCommonFileDialogNotify *notify = new TransToTeamspaceDialogNotify(userContext_, iniLanguageHelper.GetLanguage());			
					teamspaceDialog->setOption(CFDO_only_show_folder);
					teamspaceDialog->setNotify(notify);
					isShowModal_ = true;
					if (E_CFD_CANCEL == teamspaceDialog->showModal(resultHanlder,COMMFILEDIALOG_SAVETOTEAMSPACE))
					{
						isShowModal_ = false;
						return;
					}
					isShowModal_ = false;

					if(1==commonFileData.size())
					{
						MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)(*(commonFileData.begin()))->data.get();
						if(NULL == data) return;
						if (checkFileIsRename(selectedItems_, data->id, data->userContext))
						{
							m_noticeFrame_->Run(Choose, Warning, MSG_SAVEFILE_COPYMOVE_TITLE_KEY, MSG_SAVEFILE_COPYMOVE_TEXT_KEY, Modal);
							if(!m_noticeFrame_->IsClickOk()) return;
						}
						std::list<int64_t> srcNodeList;
						for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
						{
							srcNodeList.push_back(it->first);
						}
						int64_t destFolderId = data->id;
						addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, 
							data->userContext->getUserInfoMgr()->getUserId(), destFolderId, RESTTASK_SAVE, true);
					}
				}
				break;
				// copy/move
			case 3:
				{
					std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
					std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_copymove").c_str();

					CommonFileDialogPtr myFileDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
					MyFileCommonFileDialogNotify *notify = new MyFileCommonFileDialogNotify(userContext_, iniLanguageHelper.GetLanguage());			
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

					if(1==commonFileData.size())
					{
						MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)(*(commonFileData.begin()))->data.get();

						if (1==iControlIndex && selectedItems_.begin()->second.basic.parent == data->id)
						{
							SimpleNoticeFrame* psimlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
							psimlpeNoticeFrame->Show(Warning, MSG_SAVEFILE_COPYMOVE_FAIL_KEY);
							delete psimlpeNoticeFrame;
							psimlpeNoticeFrame = NULL;
							return;
						}

						if (checkFileIsRename(selectedItems_, data->id, data->userContext))
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
						int64_t destFolderId = data->id;
						addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, userContext_->getUserInfoMgr()->getUserId(), destFolderId, type, true);

						if(RESTTASK_MOVE == type)
						{
							setTotalText(MYFILE_COUNT_DEFAULT);
						}
					}
				}
				break;
				// delete
			case 4:
				{
					m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
					if (!m_noticeFrame_->IsClickOk()) return;
					std::list<int64_t> srcNodeList;
					for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
					{
						srcNodeList.push_back(it->first);
					}
					addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, userContext_->getUserInfoMgr()->getUserId(), 0, RESTTASK_DELETE);
					setTotalText(MYFILE_COUNT_DEFAULT);					
				}
				break;
				// rename
			case 5:
				{
					CMyFileElementUI* pFileListItem = NULL;
					if (selectedItems_.size() == 1)
					{
						if (browseHistory_[curPage_].isListView)
						{
							if(browseHistory_[curPage_].keyWord.empty())
							{
								if (NULL == myFileList) return;
								pFileListItem = static_cast<CMyFileElementUI*>(myFileList->GetItemAt(myFileList->GetCurSel()));
							}
							else
							{
								if (NULL == myFileSearchList) return;
								pFileListItem = static_cast<CMyFileElementUI*>(myFileSearchList->GetItemAt(myFileSearchList->GetCurSel()));
							}
						}
						else
						{
							if (NULL == myFileListLargeIcon) return;
							pFileListItem = static_cast<CMyFileElementUI*>(myFileListLargeIcon->GetItemAt(myFileListLargeIcon->GetCurSel()));
						}
						if (NULL == pFileListItem) return;
						pFileListItem->rename();
					}
				}
				break;
				// view version
			case 6:
				{
					FileVersionDialog *pVersion = new FileVersionDialog(userContext_, curItem.basic,m_storageUserInfo.user_id);
					if(NULL != pVersion)
					{
						pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
						pVersion->CenterWindow();
						isShowModal_ = true;
						pVersion->ShowModal();
						isShowModal_ = false;
						delete pVersion;
						pVersion = NULL;
					}
				}
				break;
			default:
				break;
			}
		}
		
		void clearSearchBtnClick(TNotifyUI& msg)
		{
			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYFILE_SEARCHTXT));
			if (NULL != searchtxt)
				searchtxt->resetText(L"");

			CButtonUI* pBtn = (CButtonUI*)(msg.pSender);
			if (pBtn)
				pBtn->SetVisible(false);

			MyFilesDirNode curDirNode = browseHistory_[curPage_];
			curDirNode.keyWord = L"";
			curDirNode.fileData.basic.id = 0;
			loadData(curDirNode, false);

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
			if ( selectedItems_.size() <= 0)return;

			pMenu->SetVisible(false);

			if(browseHistory_[curPage_].isListView)
			{
				CCustomListUI* myFile = browseHistory_[curPage_].keyWord.empty()?myFileList:myFileSearchList;
				if(NULL == myFile) return;
				CStdValArray* curSelects = myFile->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CMyFileElementUI* elementUI = static_cast<CMyFileElementUI*>(myFile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == elementUI) continue;
					UIFileNode fileNode;
					fileNode = elementUI->m_uNodeData;
					if (-1 == fileNode.basic.id)continue;					

					RECT rt = elementUI->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
			else
			{
				CTileLayoutListUI* myFile = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(MYFILE_TILELAYOUT_LISTVIEW));
				if(NULL == myFile) return;
				CStdValArray* curSelects = myFile->GetSelects();
				//正选
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CMyFileElementUI* elementUI = static_cast<CMyFileElementUI*>(myFile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == elementUI) continue;
					UIFileNode fileNode;
					fileNode = elementUI->m_uNodeData;
					if (-1 == fileNode.basic.id)continue;

					RECT rt = elementUI->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
		}

		void showBackupButton();
	private:
		UserContext* userContext_;
		std::map<std::wstring, call_func> funcMaps_;
		CPaintManagerUI& paintManager_;
		CCustomListUI* myFileList;
		CTileLayoutListUI *myFileListLargeIcon;
		CCustomListUI* myFileSearchList;
		CButtonUI* pBtnName;
		CButtonUI* pBtnSize;
		CButtonUI* pBtnCtime;
		std::vector<MyFilesDirNode> browseHistory_;
		size_t curPage_;
		PageParam curPageParam_;
		int64_t curCnt_;
		NoticeFrameMgr* m_noticeFrame_;
		std::map<int64_t, UIFileNode> selectedItems_;
		bool isNextPage_;
		bool isRenaming_;
		int32_t pageLimit_;
		bool m_IfLageIcon;
		size_t m_ListCount;
		CButtonUI*	m_pBtnClearSearch;

		bool downloadFlag_;
		SelectDialog* m_pSelectWnd;
		StorageUserInfo m_storageUserInfo;		

		OrderParam m_oldOrderParam;
		FILE_DIR_INFO parentDirInfo_;

		bool isLoading_;
		int32_t delayLoading_;
		boost::thread loadThread_;
		bool isShowModal_;
	};

	MyFileMgr* MyFileMgr::instance_ = NULL;

	MyFileMgr* MyFileMgr::getInstance(UserContext* context, CPaintManagerUI& paintManager)
	{
		if (NULL == instance_)
		{
			instance_ = new MyFileMgrImpl(context, paintManager);
		}
		return instance_;
	}

	void MyFileMgrImpl::uploadClick(TNotifyUI& msg)
	{
		MyFilesDirNode fileDirNode = browseHistory_[curPage_];

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

		fileDirNode.fileData.userContext = userContext_;
		if (ProxyMgr::getInstance(userContext_)->checkDirIsExist(str_path, fileDirNode.fileData.userContext, fileDirNode.fileData.basic.id))
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

		UIFileNode fileNode;
		fileNode.basic.id =0;
		fileNode = fileDirNode.fileData;
		int nResult = TransTaskMgr::upload(str_path, fileNode);
		if(0 == nResult)
		{
			SetMyFileListVisible();			
		}
	}
	
	void MyFileMgrImpl::downloadClick(TNotifyUI& msg)
	{		
		getUIFileNode();
		download(selectedItems_);
	}

	void MyFileMgrImpl::listSelectchanged()
	{
		if (myFileList == NULL || myFileListLargeIcon == NULL) return;
		myFileListLargeIcon->clearDialog();
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(MYFILE_LISTTAB));
		if(NULL != pControl)
		{
			pControl->SelectItem(0);
		}
		m_IfLageIcon = false;
		if (myFileListLargeIcon->GetCount() > 0)
		{
			myFileListLargeIcon->RemoveAll();
		}
		MyFilesDirNode fileDirNode = browseHistory_[curPage_];
		fileDirNode.isListView = true;
//		fileDirNode.keyWord = L"";
		loadData(fileDirNode, false);
	}

	void MyFileMgrImpl::largeIconSelectchanged()
	{
		if (myFileList == NULL || myFileListLargeIcon == NULL) return;
		myFileList->clearDialog();
		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(MYFILE_LISTTAB));
		if(NULL != pControl)
		{
			pControl->SelectItem(1);
		}	
		CTextUI* cntTextC = static_cast<CTextUI*>(paintManager_.FindControl(_T("myFile_Total")));
		if(NULL != cntTextC)
		{
			cntTextC->SetVisible(false);
		}
		m_IfLageIcon = true;
		if (myFileList->GetCount() > 0)
		{
			myFileList->RemoveAll();
		}
		MyFilesDirNode fileDirNode = browseHistory_[curPage_];
		fileDirNode.isListView = false;
//		fileDirNode.keyWord = L"";
		loadData(fileDirNode, false);
	}

	void MyFileMgrImpl::fileShareClick(TNotifyUI& msg)
	{
		if(NULL==msg.pSender->GetParent()) return;
		if(NULL==msg.pSender->GetParent()->GetParent()) return;
		MyFilesListContainerElement* pFileListItem = static_cast<MyFilesListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
		if(NULL==pFileListItem) return;
		isShowModal_ = true;
		Onebox::ShareFrame* myShareFrame =  new Onebox::ShareFrame(userContext_, pFileListItem->m_uNodeData, paintManager_);
		myShareFrame->ShowSharedFrame();
		delete myShareFrame;
		myShareFrame = NULL;
		loadMetaData(browseHistory_[curPage_]);
		isShowModal_ = false;
	}

	void MyFileMgrImpl::fileShareLinkClick(TNotifyUI& msg)
	{
		if(NULL==msg.pSender->GetParent()) return;
		if(NULL==msg.pSender->GetParent()->GetParent()) return;
		MyFilesListContainerElement* pFileListItem = static_cast<MyFilesListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
		if(NULL==pFileListItem) return;
		isShowModal_ = true;
		ShareLinkCountDialog::CreateDlg(paintManager_, userContext_, pFileListItem->m_uNodeData);
		loadMetaData(browseHistory_[curPage_]);
		isShowModal_ = false;
	}

	void MyFileMgrImpl::fileIsSyncClick(TNotifyUI& msg)
	{
		if(NULL==msg.pSender->GetParent()) return;
		if(NULL==msg.pSender->GetParent()->GetParent()) return;
		MyFilesListContainerElement* pFileListItem = static_cast<MyFilesListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
		if (NULL == pFileListItem)return;
		int ret =-1;
		bool bIsSync = false;
		std::wstring str_des = L"";
		Path path = userContext_->getPathMgr()->makePath();
		path.id(pFileListItem->m_uNodeData.basic.id);
		path.parent(pFileListItem->m_uNodeData.basic.parent);
		path.type((FILE_TYPE)pFileListItem->m_uNodeData.basic.type);
		if(pFileListItem->m_uNodeData.basic.flags & OBJECT_FLAG_SYNC)
		{
			bIsSync = true;
			str_des = MSG_SYNCSETTINGS_CANCEL_KEY;
		}
		else
		{
			bIsSync = false;
			str_des = MSG_SYNCSETTINGS_SETTING_KEY;
		}
		m_noticeFrame_->Run(Choose,Ask,MSG_SYNCSTTINGS_TITLE_KEY,str_des.c_str(),Modal);
		if (m_noticeFrame_->IsClickOk())
		{
			int32_t ret = userContext_->getShareResMgr()->setSync(path,!bIsSync);
			loadMetaData(browseHistory_[curPage_]);
			int tipType = -1;
			std::wstring str_des = L"";
			if (RT_OK == ret)
			{
				tipType = Right;
				bIsSync == true?str_des = MSG_CANCELSYNC_SUCCESSFUL_KEY:str_des = MSG_SETSYNC_SUCCESSFUL_KEY;
			}
			else
			{
				tipType = Error;
				bIsSync == true?str_des = MSG_CANCELSYNC_FAILED_KEY:str_des = MSG_SETSYNC_FAILED_KEY;
			}
			SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
			simlpeNoticeFrame->Show((NoticeType)tipType,str_des.c_str());
			delete simlpeNoticeFrame;
			simlpeNoticeFrame = NULL;
		}
	}

	void MyFileMgrImpl::searchPathClick(TNotifyUI& msg)
	{
		CButtonUI* pBtn = static_cast<CButtonUI*>(msg.pSender);		
		if (NULL == pBtn) return;
		Onebox::MyFilesListContainerElement* node = reinterpret_cast<Onebox::MyFilesListContainerElement*>(pBtn->GetTag());		
		showPage(node->m_uNodeData.basic.parent, node->m_uNodeData.basic.id);
	}

	void MyFileMgrImpl::itemDragFile(TNotifyUI& msg)
	{
		MyFilesDirNode fileDirNode = browseHistory_[curPage_];
		std::list<std::wstring> drogFile;	
		std::wstring tempItemName = msg.pSender->GetName();
		UIFileNode fileNode;
		fileNode.basic.id = 0;
		if(MYFILE_LISTLISTVIEW == tempItemName)
		{
			CCustomListUI* fileList = static_cast<CCustomListUI*>(msg.pSender);
			if(fileList == NULL) return;
			MyFilesListContainerElement* pm = static_cast<MyFilesListContainerElement*>(fileList->GetItemAt(fileList->getCurEnter()));
			if(pm==NULL || pm->m_uNodeData.basic.type == FILE_TYPE_FILE)
			{
				fileNode = fileDirNode.fileData;
			}
			else
			{
				fileNode = pm->m_uNodeData;
			}
			drogFile = fileList->getDropFile();

			fileList->clearDropFile();
			if (drogFile.empty())  return;
		}
		else if(MYFILE_TILELAYOUT_LISTVIEW == tempItemName)
		{
			CTileLayoutListUI* fileList = static_cast<CTileLayoutListUI*>(msg.pSender);
			if(fileList==NULL) return;
			MyFilesTileLayoutListContainerElement* pm = static_cast<MyFilesTileLayoutListContainerElement*>(fileList->GetItemAt(fileList->getCurEnter()));
			if(pm==NULL  || pm->m_uNodeData.basic.type == FILE_TYPE_FILE)
			{
				fileNode = fileDirNode.fileData;
			}
			else
			{
				fileNode = pm->m_uNodeData;
			}
			drogFile = fileList->getDropFile();

			fileList->clearDropFile();
			if (drogFile.empty())  return;
		}
		else
		{
			CCustomListUI* fileList = static_cast<CCustomListUI*>(msg.pSender);
			if(fileList==NULL) return;
			drogFile = fileList->getDropFile();			

			fileList->clearDropFile();
			fileNode = fileDirNode.fileData;
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
		fileNode.userContext = userContext_;
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

		if(0 == TransTaskMgr::upload(drogFile,fileNode))
		{			
			SetMyFileListVisible();
		}
		
		SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
		simlpeNoticeFrame->Show(Right,MSG_DRAPFILE_SETTING_KEY);
		delete simlpeNoticeFrame;
		simlpeNoticeFrame = NULL;
	}

	void MyFileMgrImpl::versionClick(TNotifyUI& msg)
	{
		CButtonUI* versionBtn = static_cast<CButtonUI*>(msg.pSender);
		if (NULL == versionBtn) return;
		if(NULL==versionBtn->GetParent()) return;
		if(NULL==versionBtn->GetParent()->GetParent()) return;
		MyFilesListContainerElement* pItem =  static_cast<MyFilesListContainerElement*> (versionBtn->GetParent()->GetParent());
		if (NULL == pItem) return;
		FileVersionDialog *pVersion = new FileVersionDialog(userContext_,pItem->m_uNodeData.basic,m_storageUserInfo.user_id);
		if(NULL == pVersion) return;
		pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		pVersion->CenterWindow();
		isShowModal_ = true;
		pVersion->ShowModal();
		delete pVersion;
		pVersion = NULL;
		loadMetaData(browseHistory_[curPage_]);
		isShowModal_ = false;
	}

	void MyFileMgrImpl::deleteAllClick()
	{
		/*getUIFileNode();*/
		bool showDeleteFrame = false;
		std::list<int64_t> srcNodeList;
		for(std::map<int64_t, UIFileNode>::const_iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
		{			
			if(L""== it->second.basic.extraType)
			{
				srcNodeList.push_back(it->first);
			}
		}
		if(srcNodeList.empty())
		{
			return;
		}

		m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
		if (!m_noticeFrame_->IsClickOk()) return;
		
		addRestTask(userContext_->getUserInfoMgr()->getUserId(), srcNodeList, userContext_->getUserInfoMgr()->getUserId(), 0, RESTTASK_DELETE);			
	}

	void MyFileMgrImpl::showBackupButton()
	{
		if (0 == _tcsicmp(parentDirInfo_.extraType.c_str(), FOLDER_ICON_COMPUTER))
		{
			CButtonUI* uploadBtn = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_upload")));
			CButtonUI* createBtn = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myFile_create")));
			if (NULL != uploadBtn && NULL != createBtn){
				uploadBtn->SetVisible(false);
				createBtn->SetVisible(false);
			}
		}
	}
} 
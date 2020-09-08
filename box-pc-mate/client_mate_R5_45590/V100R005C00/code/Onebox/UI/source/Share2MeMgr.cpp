#include "stdafxOnebox.h"
#include "Share2MeMgr.h"
#include "Utility.h"
#include "ShareNode.h"
#include "ShareResMgr.h"
#include "ControlNames.h"
#include "UserInfoMgr.h"
#include "Share2MeElement.h"
#include "DialogBuilderCallbackImpl.h"
#include "TileLayoutListUI.h"
#include "CustomListUI.h"
#include "PathMgr.h"
#include "TransTaskMgr.h"
#include "SearchTxt.h"
#include "SyncFileSystemMgr.h"
#include "UploadFrame.h"
#include "GroupButton.h"
#include "FileVersionDialog.h"
#include "ProxyMgr.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "SimpleNoticeFrame.h"
#include "NoticeFrame.h"
#include "ErrorConfMgr.h"
#include "RestTaskMgr.h"
#include "ShareFrame.h"
#include "FilterMgr.h"
#include "OpenFileDbClick.h"
#include <boost/algorithm/string.hpp>
#include "ConfigureMgr.h"
#include "ShellCommonFileDialog.h"
#include "CommonFileDialog.h"
#include "CommonFileDialogRemoteNotify.h"
#include "ShareLinkCountDialog.h"
#include "SelectDialog.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("Share2Me")
#endif

#ifndef MODULE_MENUCOUNT
#define MODULE_MENUCOUNT 9
#endif

#ifndef MODULE_MOREMENUCOUNT
#define MODULE_MOREMENUCOUNT 5
#endif

#define EXTRA_TYPE_COMPUTER (L"computer")
#define EXTRA_TYPE_DISK (L"disk")
namespace Onebox
{
	#define SHARE2ME_SCROLL_LIMEN (100)	//触发翻页的滚动条阈值

	class Share2MeMgrImpl : public Share2MeMgr
	{
	public:
		Share2MeMgrImpl(UserContext* context, CPaintManagerUI& paintManager)
			:context_(context), paintManager_(paintManager)
		{
			m_noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());

			curCnt_ = 0;
			m_nQueryCount = 0;
			isNextPage_ = false;
			isRenaming_ = false;
			downloadFlag_ = false;
			m_pSelectWnd = NULL;
			isShowModal_ = false;

			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(SHARE2ME_SEARCHTXT));
			CDuiString defaultTxt = iniLanguageHelper.GetCommonString(MSG_SHARE2ME_SEARCHDEFAULT_TEXT_KEY).c_str();
			if (NULL != searchtxt)
				searchtxt->setDefaultTxt(defaultTxt);
			
			backBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_BACK));
			nextBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_NEXT));
			searchLayout_ = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(SHARE2ME_SEARCHLAYOUT));
			uploadBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_UPLOAD));
			createBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_CREATE));
			shareBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_SHARE));
			moreBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_MORE));
			downloadBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_DOWNLOAD));
			saveBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_SAVE));
			exitBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_EXIT));
			cntText_ = static_cast<CTextUI*>(paintManager_.FindControl(SHARE2ME_COUNT));
			versionBtn_ = static_cast<CButtonUI*>(paintManager_.FindControl(SHARE2ME_VERSION));
			m_pBtnClearSearch = static_cast<CButtonUI*>(paintManager_.FindControl(_T("share2Me_clearsearchbtn")));
			
			share2MeList_ = static_cast<CCustomListUI*>(paintManager_.FindControl(SHARE2ME_LIST));;
			share2MeTile_ = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(SHARE2ME_TILE));;

			funcMaps_.insert(std::make_pair(L"back_click", boost::bind(&Share2MeMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"next_click", boost::bind(&Share2MeMgrImpl::nextClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"flush_click", boost::bind(&Share2MeMgrImpl::flushClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"upload_click", boost::bind(&Share2MeMgrImpl::uploadClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"create_click", boost::bind(&Share2MeMgrImpl::createClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"share_click", boost::bind(&Share2MeMgrImpl::shareClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"download_click", boost::bind(&Share2MeMgrImpl::downloadClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"version_click", boost::bind(&Share2MeMgrImpl::versionClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"save_click", boost::bind(&Share2MeMgrImpl::saveClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"exit_click", boost::bind(&Share2MeMgrImpl::exitClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"searchbtn_click", boost::bind(&Share2MeMgrImpl::searchbtnClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"more_click", boost::bind(&Share2MeMgrImpl::moreClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"List_selectItemChanged", boost::bind(&Share2MeMgrImpl::showButton, this)));
			funcMaps_.insert(std::make_pair(L"Tile_selectItemChanged", boost::bind(&Share2MeMgrImpl::showButton, this)));
			funcMaps_.insert(std::make_pair(L"List_selectchanged", boost::bind(&Share2MeMgrImpl::showButton, this)));
			funcMaps_.insert(std::make_pair(L"Tile_selectchanged", boost::bind(&Share2MeMgrImpl::showButton, this)));

			funcMaps_.insert(std::make_pair(L"filename_killfocus", boost::bind(&Share2MeMgrImpl::nameReturn, this,_1)));
			funcMaps_.insert(std::make_pair(L"name_killfocus", boost::bind(&Share2MeMgrImpl::nameReturn, this,_1)));
			funcMaps_.insert(std::make_pair(L"listItem_itemdbclick", boost::bind(&Share2MeMgrImpl::itemdbclick, this, _1)));
			funcMaps_.insert(std::make_pair(L"iconItem_itemdbclick", boost::bind(&Share2MeMgrImpl::itemdbclick, this, _1)));

			funcMaps_.insert(std::make_pair(L"listOpt_selectchanged", boost::bind(&Share2MeMgrImpl::listSelectchanged, this, _1)));
			funcMaps_.insert(std::make_pair(L"tabloidOpt_selectchanged", boost::bind(&Share2MeMgrImpl::tabloidSelectchanged, this, _1)));

			funcMaps_.insert(std::make_pair(L"listheaderitem_name_headerclick", boost::bind(&Share2MeMgrImpl::nameHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_owner_headerclick", boost::bind(&Share2MeMgrImpl::ownerHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_size_headerclick", boost::bind(&Share2MeMgrImpl::sizeHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listheaderitem_time_headerclick", boost::bind(&Share2MeMgrImpl::timeHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemNameSortIcon_click", boost::bind(&Share2MeMgrImpl::nameHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemOwnerSortIcon_click", boost::bind(&Share2MeMgrImpl::ownerHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemSizeSortIcon_click", boost::bind(&Share2MeMgrImpl::sizeHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemTimeSortIcon_click", boost::bind(&Share2MeMgrImpl::timeHeaderClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"listHeaderItemName_click", boost::bind(&Share2MeMgrImpl::nameHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemOwner_click", boost::bind(&Share2MeMgrImpl::ownerHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemSize_click", boost::bind(&Share2MeMgrImpl::sizeHeaderClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"listHeaderItemTime_click", boost::bind(&Share2MeMgrImpl::timeHeaderClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"List_itemDragMove", boost::bind(&Share2MeMgrImpl::itemDragMove, this,_1)));
			funcMaps_.insert(std::make_pair(L"List_itemDragFile", boost::bind(&Share2MeMgrImpl::itemDragFile, this,_1)));
			funcMaps_.insert(std::make_pair(L"Tile_itemDragMove", boost::bind(&Share2MeMgrImpl::itemDragMove, this,_1)));
			funcMaps_.insert(std::make_pair(L"Tile_itemDragFile", boost::bind(&Share2MeMgrImpl::itemDragFile, this,_1)));
			funcMaps_.insert(std::make_pair(L"noFiles_itemDragFile", boost::bind(&Share2MeMgrImpl::itemDragFile, this,_1)));

			funcMaps_.insert(std::make_pair(L"listItem_back", boost::bind(&Share2MeMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"iconItem_back", boost::bind(&Share2MeMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"List_back", boost::bind(&Share2MeMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"Tile_back", boost::bind(&Share2MeMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"noFiles_back", boost::bind(&Share2MeMgrImpl::backClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"searchtxt_return", boost::bind(&Share2MeMgrImpl::Return, this, _1)));
 			funcMaps_.insert(std::make_pair(L"List_listselectall", boost::bind(&Share2MeMgrImpl::selectAllItemClick, this, _1)));
 			funcMaps_.insert(std::make_pair(L"Tile_listselectall", boost::bind(&Share2MeMgrImpl::selectAllItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"List_itemselectall", boost::bind(&Share2MeMgrImpl::selectAllClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"List_nextPage", boost::bind(&Share2MeMgrImpl::nextPage, this, _1)));
 			funcMaps_.insert(std::make_pair(L"Tile_nextPage", boost::bind(&Share2MeMgrImpl::nextPage, this, _1)));

			funcMaps_.insert(std::make_pair(L"List_menuadded", boost::bind(&Share2MeMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"List_menuitemclick", boost::bind(&Share2MeMgrImpl::menuItemClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"Tile_menuadded", boost::bind(&Share2MeMgrImpl::menuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"Tile_menuitemclick", boost::bind(&Share2MeMgrImpl::menuItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"more_menuadded", boost::bind(&Share2MeMgrImpl::moreMenuAdded, this, _1)));
			funcMaps_.insert(std::make_pair(L"more_menuitemclick", boost::bind(&Share2MeMgrImpl::moreMenuItemClick, this, _1)));

			funcMaps_.insert(std::make_pair(L"clearsearchbtn_click", boost::bind(&Share2MeMgrImpl::clearSearchBtnClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"searchtxt_textchanged", boost::bind(&Share2MeMgrImpl::searchTextChanged, this, _1)));

			funcMaps_.insert(std::make_pair(L"listItem_itemdelete", boost::bind(&Share2MeMgrImpl::deleteItem, this)));
			funcMaps_.insert(std::make_pair(L"iconItem_itemdelete", boost::bind(&Share2MeMgrImpl::deleteItem, this)));
			funcMaps_.insert(std::make_pair(L"List_itemdelete", boost::bind(&Share2MeMgrImpl::deleteItem, this)));
			
			context_->getUserInfoMgr()->getCurUserInfo(m_storageUserInfo);
		}
		
		~Share2MeMgrImpl()
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
			ShareDirNode shareDirNode;
			shareDirNode.isListView = true;
			shareDirNode.shareFileName = iniLanguageHelper.GetCommonString(MSG_SHARE2ME_BASE_NAME_KEY).c_str();	
			shareDirNode.id = 0;
			shareDirNode.ownerId = context_->id.id;
			shareDirNode.shareContext = context_;
			browseHistory_.push_back(shareDirNode);
			curPage_ = 0;

			headerClick(S2M_ROW_STIME, "desc");

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			if (NULL != pControl)
			{
				if (m_nQueryCount > 0)
					pControl->SelectItem(0);
			}

			if (!m_pSelectWnd)
			{
				m_pSelectWnd = new SelectDialog;
			}
			if ( m_pSelectWnd)
			{
				POINT pt;
				pt.x = 0;
				pt.y = 0;

				if (share2MeList_)
				{
					m_pSelectWnd->Init(L"SelectDialog.xml", share2MeList_->GetManager()->GetPaintWindow(), pt);
					m_pSelectWnd->SetWindowShow(false);
					share2MeList_->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}
				if (share2MeTile_)
				{
					share2MeTile_->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				}
			}
		}
	
		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg)
		{
			std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
			if(it!=funcMaps_.end())
			{
				SERVICE_INFO(MODULE_NAME, RT_OK, "executeFunc %s", SD::Utility::String::wstring_to_string(funcName).c_str());
				it->second(msg);
				return;
			}

			if (funcName.substr(0,10) == L"groupMenu_")
			{
				if(msg.sType != DUI_MSGTYPE_CLICK && 
					msg.sType != DUI_MSGTYPE_MENUADDED && 
					msg.sType != DUI_MSGTYPE_MENUITEM_CLICK) return;
				CGroupButtonUI* pContainer = static_cast<CGroupButtonUI*>(paintManager_.FindControl(_T("share2Me_groupBtn")));
				if(NULL == pContainer) return;
				if (msg.sType == DUI_MSGTYPE_MENUITEM_CLICK) {
					PathNode node = pContainer->getPathNodeById(msg.lParam);
					ShareDirNode shareNode;
					shareNode.id = node.fileId;
					if(0==shareNode.id)
					{
						shareNode.shareFileName = iniLanguageHelper.GetCommonString(MSG_SHARE2ME_BASE_NAME_KEY).c_str();
						shareNode.ownerId = context_->id.id;
						shareNode.shareContext = context_;
					}
					else
					{
						for(size_t i = 0; i < browseHistory_.size(); ++i)
						{
							if(browseHistory_[i].id==shareNode.id)
							{
								shareNode = browseHistory_[i];
								break;
							}
						}
					}
					shareNode.isListView = browseHistory_[curPage_].isListView;
					loadData(shareNode, false);
				}
				else pContainer->showMenu(_T("share2Me"), msg);
			}

			if (funcName.substr(0,10) == L"groupMain_")
			{
				std::vector<std::wstring> vecInfo;  
				SD::Utility::String::split(funcName, vecInfo, L"_");
				if(vecInfo.size()<3) return;
				if(L"click"!=vecInfo[2]) return;

				SERVICE_INFO(MODULE_NAME, RT_OK, "executeFunc %s", SD::Utility::String::wstring_to_string(funcName).c_str());

				ShareDirNode shareNode;
				shareNode.id = SD::Utility::String::string_to_type<int64_t>(vecInfo[1]);
				if(0==shareNode.id)
				{
					shareNode.shareFileName = iniLanguageHelper.GetCommonString(MSG_SHARE2ME_BASE_NAME_KEY).c_str();
					shareNode.ownerId = context_->id.id;
					shareNode.shareContext = context_;
				}
				else
				{
					for(size_t i = 0; i < browseHistory_.size(); ++i)
					{
						if(browseHistory_[i].id==shareNode.id)
						{
							shareNode = browseHistory_[i];
							break;
						}
					}
				}
				shareNode.isListView = browseHistory_[curPage_].isListView;
				loadData(shareNode, false);
			}
		}

		virtual void reloadCache(int64_t ownerId, int64_t curId, bool isFlush = false)
		{
			if(isShowModal_)
			{
				return;
			}
			if(browseHistory_[curPage_].ownerId == ownerId && browseHistory_[curPage_].id == curId)
			{
				loadData(browseHistory_[curPage_], false, isFlush);
			}
		}

		virtual bool isTheCurPage(int64_t ownerId, int64_t curId)
		{
			if(browseHistory_[curPage_].ownerId == ownerId && browseHistory_[curPage_].id == curId)
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
			loadMetaData(browseHistory_[curPage_], false);
		}

		virtual void showPage(int64_t ownerId, int64_t fileId)
		{
			ShareDirNode shareDirNode;
			shareDirNode.isListView = true;
			shareDirNode.shareFileName = iniLanguageHelper.GetCommonString(MSG_SHARE2ME_BASE_NAME_KEY).c_str();	
			shareDirNode.id = 0;
			shareDirNode.ownerId = context_->id.id;
			shareDirNode.shareContext = context_;

			if(browseHistory_.empty())
			{
				browseHistory_.push_back(shareDirNode);
				curPage_ = 0;
			}
			else if(!(browseHistory_[curPage_]==shareDirNode))
			{
				browseHistory_.push_back(shareDirNode);
				++curPage_;
			}

			headerClick(S2M_ROW_STIME, "desc");

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			if (NULL != pControl)
			{
				if (m_nQueryCount > 0)
					pControl->SelectItem(0);
			}

			for (int i = 0; i<share2MeList_->GetCount(); ++i)
			{
				Share2MeListContainerElement* item = static_cast<Share2MeListContainerElement*>(share2MeList_->GetItemAt(i));
				if (item == NULL) continue;
				if (item->m_uNodeData.basic.id == fileId && item->ownerId == ownerId)
				{
					if(FILE_TYPE_DIR==item->m_uNodeData.basic.type)
					{
						//open folder
						ShareDirNode shareDirNode;
						shareDirNode.id = item->m_uNodeData.basic.id;
						shareDirNode.ownerId = item->ownerId;
						shareDirNode.ownerName = item->ownerName;
						shareDirNode.shareTime = item->shareTime;
						shareDirNode.isListView = browseHistory_[curPage_].isListView ;
						shareDirNode.shareFileName = item->m_uNodeData.basic.name;
						shareDirNode.parentList = browseHistory_[curPage_].parentList;
						PathNode pathNode;
						pathNode.fileId = browseHistory_[curPage_].id;
						pathNode.fileName = browseHistory_[curPage_].shareFileName;
						shareDirNode.parentList.push_back(pathNode);
						shareDirNode.shareContext = item->m_uNodeData.userContext;
						Path filePath = shareDirNode.shareContext->getPathMgr()->makePath();
						filePath.id(shareDirNode.id);
						shareDirNode.shareContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
							context_->getUserInfoMgr()->getUserId(), shareDirNode.filePermissions);
						loadData(shareDirNode, false);
					}
					else
					{
						item->Select(true);
						SIZE szPos = share2MeList_->GetScrollPos();
						szPos.cy = i*item->GetFixedHeight();
						share2MeList_->SetScrollPos(szPos);
					}
					break;					
				}
			}
		}

		virtual void showPage(int64_t ownerId, std::wstring& selectFilename)
		{
			if ( selectFilename == L"" ) return;

			ShareDirNode shareDirNode;
			shareDirNode.isListView = true;
			shareDirNode.shareFileName = iniLanguageHelper.GetCommonString(MSG_SHARE2ME_BASE_NAME_KEY).c_str();	
			shareDirNode.id = 0;
			shareDirNode.ownerId = context_->id.id;
			shareDirNode.shareContext = context_;

			if(browseHistory_.empty())
			{
				browseHistory_.push_back(shareDirNode);
				curPage_ = 0;
			}
			else if(!(browseHistory_[curPage_]==shareDirNode))
			{
				browseHistory_.push_back(shareDirNode);
				++curPage_;
			}

			headerClick(S2M_ROW_STIME, "desc");

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			if (NULL != pControl)
			{
				if (m_nQueryCount > 0)
					pControl->SelectItem(0);
			}

			for (int i = 0; i<share2MeList_->GetCount(); ++i)
			{
				Share2MeListContainerElement* item = static_cast<Share2MeListContainerElement*>(share2MeList_->GetItemAt(i));
				if (item == NULL) continue;
				if (item->m_uNodeData.basic.parent == ownerId && item->m_uNodeData.basic.name == selectFilename )
				{
					item->Select(true);

					SIZE posscroll = share2MeList_->GetScrollPos();

					posscroll.cy = item->GetFixedHeight() * i;
					share2MeList_->SetScrollPos(posscroll);

					break;
				}
				else
				{
					if(FILE_TYPE_DIR==item->m_uNodeData.basic.type)
					{
						//open folder
						ShareDirNode shareDirNode;
						shareDirNode.id = item->m_uNodeData.basic.id;
						shareDirNode.ownerId = item->ownerId;
						shareDirNode.ownerName = item->ownerName;
						shareDirNode.shareTime = item->shareTime;
						shareDirNode.isListView = browseHistory_[curPage_].isListView ;
						shareDirNode.shareFileName = item->m_uNodeData.basic.name;
						shareDirNode.parentList = browseHistory_[curPage_].parentList;
						PathNode pathNode;
						pathNode.fileId = browseHistory_[curPage_].id;
						pathNode.fileName = browseHistory_[curPage_].shareFileName;
						shareDirNode.parentList.push_back(pathNode);
						shareDirNode.shareContext = item->m_uNodeData.userContext;
						Path filePath = shareDirNode.shareContext->getPathMgr()->makePath();
						filePath.id(shareDirNode.id);
						shareDirNode.shareContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
							context_->getUserInfoMgr()->getUserId(), shareDirNode.filePermissions);
						loadData(shareDirNode, false);
					}
				}
			}
		}

		virtual bool reloadThumb(const std::string& thumbKey)
		{
			if(isShowModal_)
			{
				return false;
			}
			if( browseHistory_[curPage_].isListView)
			{
				if (NULL == share2MeList_) return false;
				for(int i = 0; i < share2MeList_->GetCount(); ++i)
				{
					Share2MeListContainerElement* fileNode = static_cast<Share2MeListContainerElement*>(share2MeList_->GetItemAt(i));
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
				if (NULL == share2MeTile_) return false;
				for(int i = 0; i < share2MeTile_->GetCount(); ++i)
				{
					Share2MeTileLayoutListContainerElement* fileNode = static_cast<Share2MeTileLayoutListContainerElement*>(share2MeTile_->GetItemAt(i));
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
			if( browseHistory_[curPage_].isListView)
			{
				if (share2MeList_)
				{
					share2MeList_->SetFocus();
				}
			}
			else
			{
				if (share2MeTile_)
				{
					share2MeTile_->SetFocus();
				}
			}
		}

		virtual void flushClick(TNotifyUI& msg)
		{
			ShareDirNode myFilesDirNode = browseHistory_[curPage_];
			isRenaming_ = false;
			loadData(browseHistory_[curPage_], true, true);
			//刷新触发重命名时，需要重刷
			if(isRenaming_)
			{
				loadData(browseHistory_[curPage_], true, true);
			}
			myFilesDirNode.isListView?share2MeList_->HomeUp():share2MeTile_->HomeUp();
		}
	private:
		void loadMetaData(ShareDirNode& shareDirNode, bool isFlush = false)
		{
			ShareNodeParent parent;
			parent.id = shareDirNode.id;
			parent.keyWord = SD::Utility::String::wstring_to_utf8(shareDirNode.keyWord);
			parent.ownerId = shareDirNode.ownerId;
			parent.ownerName = SD::Utility::String::wstring_to_utf8(shareDirNode.ownerName);
			parent.shareTime = shareDirNode.shareTime;
			if(isNextPage_)
			{
				isNextPage_ = false;
			}
			else
			{
				curPageParam_.offset = 0;
			}

			ShareNodeList shareNodes;
			ProxyMgr::getInstance(context_)->listReceiveShareRes(shareDirNode.shareContext, parent, shareNodes, 
				curPageParam_, curCnt_, isFlush);

			m_nQueryCount = shareNodes.size();
			if (-1 == m_nQueryCount)
				m_nQueryCount = 0;
			
			if(shareDirNode.isListView)
			{
				loadList(shareNodes);
				
				if(share2MeList_)
				{
					share2MeList_->SetFocus();
				}				
			}
			else
			{
				loadLargeIcon(shareNodes);

				if(share2MeTile_)
				{
					share2MeTile_->SetFocus();
				}
			}
		}

		void loadData(ShareDirNode& shareDirNode, bool isHistory, bool isFlush = false)
		{
			SERVICE_FUNC_TRACE(MODULE_NAME, SD::Utility::String::format_string("Share2MeMgrImpl::loadData ShareDirNode:%s", shareDirNode.toString().c_str()));

			if(!(isHistory||browseHistory_[curPage_]==shareDirNode))
			{
				for(;curPage_<browseHistory_.size()-1;)
				{
					browseHistory_.erase(--browseHistory_.end());
				}
				browseHistory_.push_back(shareDirNode);
				++curPage_;
			}

			selectedItems_.clear();
			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
			if(!browseHistory_[curPage_].isListView)
			{
				pageLimit_ = 2*pageLimit_;
			}
			curPageParam_.limit = pageLimit_;
			curPageParam_.offset = 0;

			loadMetaData(shareDirNode, isFlush);

			//reset path
			reShowPath(shareDirNode);

			showButton();
			if(!isHistory)
			{
				return;
			}

			//reset tab
			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			if(shareDirNode.isListView)
			{
				COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(SHARE2ME_LISTOPT));
				pOption->Selected(true);
				if (m_nQueryCount > 0)
					pControl->SelectItem(0);					
			}
			else
			{
				COptionUI* pOption = static_cast<COptionUI*>(paintManager_.FindControl(SHARE2ME_ICONOPT));
				pOption->Selected(true);
				if (m_nQueryCount > 0)
					pControl->SelectItem(1);
			}

			//reset searchtxt
			if(0==shareDirNode.id)
			{
				CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(SHARE2ME_SEARCHTXT));
				searchtxt->resetText(shareDirNode.keyWord);
			}
		}

		void reShowPath(ShareDirNode& shareDirNode)
		{
			CGroupButtonUI* pContainer = static_cast<CGroupButtonUI*>(paintManager_.FindControl(_T("share2Me_groupBtn")));
			if(NULL == pContainer) return;

			std::list<PathNode> pathList;
			pathList = shareDirNode.parentList;
			PathNode curNode;
			curNode.fileId = shareDirNode.id;
			curNode.fileName = shareDirNode.shareFileName;
			pathList.push_back(curNode);

			pContainer->showPath(L"share2Me", pathList);
		}

		void loadList(const ShareNodeList& shareNodes)
		{
			if(NULL == share2MeList_) return;
			if (share2MeList_->GetCount()>0 && 0==curPageParam_.offset)
				share2MeList_->RemoveAll();

			bool renameable = 0!=(browseHistory_[curPage_].filePermissions&FP_EDIT);
			share2MeList_->setDragable(renameable);
			for (size_t i = 0; i < shareNodes.size(); ++i)
			{
				CDialogBuilder builder;
				Share2MeListContainerElement* node = static_cast<Share2MeListContainerElement*>(
					builder.Create(ControlNames::SKIN_XML_SHARE2ME_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;

				node->fillData(shareNodes[i], browseHistory_[curPage_].shareContext, browseHistory_[curPage_].id);
				node->initUI(renameable);

				if (!share2MeList_->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void loadLargeIcon(const ShareNodeList& shareNodes)
		{
			if(NULL == share2MeTile_) return;
			if (share2MeTile_->GetCount()>0 && 0==curPageParam_.offset)
				share2MeTile_->RemoveAll();

			bool renameable = 0!=(browseHistory_[curPage_].filePermissions&FP_EDIT);
			share2MeTile_->setDragable(renameable);
			for (size_t i = 0; i < shareNodes.size(); ++i)
			{
				CDialogBuilder builder;
				Share2MeTileLayoutListContainerElement* node = static_cast<Share2MeTileLayoutListContainerElement*>(
					builder.Create(ControlNames::SKIN_XML_SHARE2ME_ICONITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;

				node->fillData(shareNodes[i], browseHistory_[curPage_].shareContext, browseHistory_[curPage_].id);
				node->initUI(renameable);

				if (!share2MeTile_->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
			}
		}

		void getUIFileNode()
		{
			selectedItems_.clear();
			if(browseHistory_[curPage_].isListView)
			{
				if(NULL == share2MeList_) return;
				CStdValArray* curSelects = share2MeList_->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					Share2MeListContainerElement* element = static_cast<Share2MeListContainerElement*>(share2MeList_->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == element) continue;
					UIFileNode fileNode = element->m_uNodeData;
					selectedItems_.insert(std::make_pair(fileNode.basic.id, fileNode));
				}
			}
			else
			{
				if(NULL == share2MeTile_) return;
				CStdValArray* curSelects = share2MeTile_->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					Share2MeListContainerElement* element = static_cast<Share2MeListContainerElement*>(share2MeTile_->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == element) continue;
					UIFileNode fileNode = element->m_uNodeData;
					selectedItems_.insert(std::make_pair(fileNode.basic.id, fileNode));
				}
			}
		}

		void nameHeaderClick(TNotifyUI& msg)
		{
			headerClick(S2M_ROW_NAME);
		}

		void ownerHeaderClick(TNotifyUI& msg)
		{
			headerClick(S2M_ROW_OWNERNAME);
		}

		void sizeHeaderClick(TNotifyUI& msg)
		{
			headerClick(S2M_ROW_SIZE);
		}

		void timeHeaderClick(TNotifyUI& msg)
		{
			headerClick(S2M_ROW_STIME);
		}

		void headerClick(const std::string& name, const std::string& direction = "")
		{
			CButtonUI* pBtnName = static_cast<CButtonUI*>(paintManager_.FindControl(_T("share2Me_listHeaderItemNameSortIcon")));
			CButtonUI* pBtnOwner = static_cast<CButtonUI*>(paintManager_.FindControl(_T("share2Me_listHeaderItemOwnerSortIcon")));
			CButtonUI* pBtnSize = static_cast<CButtonUI*>(paintManager_.FindControl(_T("share2Me_listHeaderItemSizeSortIcon")));
			CButtonUI* pBtnTime = static_cast<CButtonUI*>(paintManager_.FindControl(_T("share2Me_listHeaderItemTimeSortIcon")));
			if(NULL==pBtnName||NULL==pBtnOwner||NULL==pBtnSize||NULL==pBtnTime) return;

			CButtonUI* pControl = NULL;
			if(S2M_ROW_NAME==name)
			{
				pControl = pBtnName;
			}
			else if(S2M_ROW_OWNERNAME==name)
			{
				pControl = pBtnOwner;
			}
			else if(S2M_ROW_SIZE==name)
			{
				pControl = pBtnSize;
			}
			else
			{
				pControl = pBtnTime;
			}

			OrderParam orderParam;
			orderParam.field = name;
			if(direction.empty())
			{
				orderParam.direction = (pControl->IsVisible()
					&&_tcsicmp(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,0,6,6' dest='6,6,12,12'"),
					pControl->GetNormalImage()) == 0)?"desc":"asc";
			}
			else
			{
				orderParam.direction = direction;
			}

			if ("desc"==orderParam.direction)
			{
				pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,0,6,6' dest='6,6,12,12'"));
				pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,16,6,22' dest='6,6,12,12'"));
			} 
			else
			{
				pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,0,6,6' dest='6,6,12,12'"));
				pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,16,6,22' dest='6,6,12,12'"));
			}
			
			pBtnName->SetVisible(S2M_ROW_NAME==name);
			pBtnOwner->SetVisible(S2M_ROW_OWNERNAME==name);
			pBtnSize->SetVisible(S2M_ROW_SIZE==name);
			pBtnTime->SetVisible(S2M_ROW_STIME==name);

			curPageParam_.orderList.clear();
			curPageParam_.orderList.push_back(orderParam);
			loadData(browseHistory_[curPage_], false);
		}

		void backClick(TNotifyUI& msg)
		{
			if(curPage_<1)
			{
				return;
			}
			loadData(browseHistory_[--curPage_], true);
		}

		void nextClick(TNotifyUI& msg)
		{
			if(curPage_+2>browseHistory_.size())
			{
				return;
			}
			loadData(browseHistory_[++curPage_], true);
		}

		void Return(TNotifyUI& msg)
		{
			searchbtnClick(msg);
		}

		void uploadClick(TNotifyUI& msg)
		{
			std::list<std::wstring> str_path;
			std::list<FileBaseInfo> desList;

			ShellCommonFileDialogParam param;
			param.okButtonName = iniLanguageHelper.GetCommonString(COMMENT_UPLOAD_KEY);
			param.title = param.okButtonName;
			param.parent = paintManager_.GetPaintWindow();

			ShellCommonFileDialog fileDialog(param);
			if (!fileDialog.getResults(str_path) || str_path.empty())
			{
				return;
			}

			if (str_path.size() == 0) return;

			if  (0==(browseHistory_[curPage_].filePermissions&FP_UPLOAD))
			
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				simlpeNoticeFrame->Show(Error, MSG_NO_PERMISSIONS_KEY);
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
				return;
			}

			UIFileNode fileNode;
			fileNode.basic.id = browseHistory_[curPage_].id;
			fileNode.userContext = browseHistory_[curPage_].shareContext;
			if (ProxyMgr::getInstance(context_)->checkDirIsExist(str_path, fileNode.userContext, fileNode.basic.id))
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

			(void)TransTaskMgr::upload(str_path, fileNode);
		}

		void createClick(TNotifyUI& msg)
		{
			ShareDirNode curShareDirNode = browseHistory_[curPage_];
			//curShareDirNode.isListView?share2MeList_->resumeLastRename():share2MeTile_->resumeLastRename();

			ShareNode shareNode;
			shareNode.id(-1);
			shareNode.type(FILE_TYPE_DIR);
			shareNode.size(0);
			shareNode.ownerId(curShareDirNode.ownerId);
			shareNode.ownerName(SD::Utility::String::wstring_to_utf8(curShareDirNode.ownerName));
			shareNode.modifiedAt(curShareDirNode.shareTime);

			Path path = curShareDirNode.shareContext->getPathMgr()->makePath();
			path.parent(curShareDirNode.id);
			std::wstring dirName = iniLanguageHelper.GetCommonString(COMMENT_CREATENEWDIR_KEY);
			curShareDirNode.shareContext->getSyncFileSystemMgr()->getNewName(path, dirName);
			shareNode.name(SD::Utility::String::wstring_to_utf8(dirName));


			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			if (curShareDirNode.isListView)
			{
				CDialogBuilder builder;
				Share2MeListContainerElement* node = static_cast<Share2MeListContainerElement*>(
					builder.Create(ControlNames::SKIN_XML_SHARE2ME_ITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) return;

				node->fillData(shareNode, curShareDirNode.shareContext, curShareDirNode.id);
				node->initUI(true);

				if (!share2MeList_->AddAt(node,0))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create dir node failed.");
					return;
				}
				m_nQueryCount++;
				if (NULL != pControl)
					pControl->SelectItem(0);

				share2MeList_->HomeUp();
				share2MeList_->SelectAllItem(false);
				node->rename();
			}
			else
			{
				CDialogBuilder builder;
				Share2MeTileLayoutListContainerElement* node = static_cast<Share2MeTileLayoutListContainerElement*>(
					builder.Create(ControlNames::SKIN_XML_SHARE2ME_ICONITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) return;

				node->fillData(shareNode, curShareDirNode.shareContext, curShareDirNode.id);
				node->initUI(true);

				if (!share2MeTile_->AddAt(node,0))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "create dir node failed.");
					return;
				}
				m_nQueryCount++;
				if (NULL != pControl)
					pControl->SelectItem(0);

				share2MeTile_->HomeUp();
				share2MeTile_->SelectAllItem(false);
				node->rename();
			}
		}

		void shareClick(TNotifyUI& msg)
		{
			getUIFileNode();
			if(1!=selectedItems_.size()) return;
			ShareLinkCountDialog::CreateDlg(paintManager_, selectedItems_.begin()->second.userContext, selectedItems_.begin()->second, false);
		}

		void downloadClick(TNotifyUI& msg)
		{
			getUIFileNode();
			File_Permissions filePermissions;
			UserContext* shareContext = selectedItems_.begin()->second.userContext;
			if(NULL != shareContext)
			{
				Path filePath = shareContext->getPathMgr()->makePath();
				filePath.id(selectedItems_.begin()->second.basic.id);
				shareContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
					context_->getUserInfoMgr()->getUserId(), filePermissions);
				if(!(filePermissions&FP_DOWNLOAD))
				{
					NoticeFrameMgr* noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());
					noticeFrame_->Run(Confirm,Error,L"",MSG_DOWNLOAD_CHANGE_TEXT_KEY,Modal);					
					delete noticeFrame_;
					noticeFrame_= NULL;
					return;
				}
			}
			std::list<UIFileNode> itemList;
			for(std::map<int64_t, UIFileNode>::const_iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
			{
				itemList.push_back(it->second);
			}
			download(itemList);
		}

		void versionClick(TNotifyUI& msg)
		{
			CShare2MeElementUI* currentItem = NULL;
			if ( browseHistory_[curPage_].isListView)
			{
				currentItem =  static_cast<CShare2MeElementUI*>(share2MeList_->GetItemAt(share2MeList_->GetCurSel()));
			}
			else
			{
				currentItem =  static_cast<CShare2MeElementUI*>(share2MeTile_->GetItemAt(share2MeTile_->GetCurSel()));
			}

			if (NULL == currentItem) return;

			FileVersionDialog *pVersion = new FileVersionDialog(currentItem->m_uNodeData.userContext, currentItem->m_uNodeData.basic,m_storageUserInfo.user_id);
			pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
			pVersion->CenterWindow();
			isShowModal_ = true;
			pVersion->ShowModal();
			isShowModal_ = false;
			delete pVersion;
			pVersion = NULL;
		}

		bool checkFileIsRename(const std::map<int64_t, UIFileNode>& sourceList, int64_t& id, UserContext* userContext)
		{
			Path listPath = userContext->getPathMgr()->makePath();
			listPath.id(id);
			LIST_FOLDER_RESULT lfResult;
			ProxyMgr::getInstance(context_)->listAll(userContext, listPath, lfResult);
			for (LIST_FOLDER_RESULT::iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				for (std::map<int64_t, UIFileNode>::const_iterator itor = sourceList.begin();itor != sourceList.end();itor++)
				{
					if (itor->second.basic.name == it->name)
					{
						return true;
					}
				}
			}
			return false;
		}

		void saveClick(TNotifyUI& msg)
		{
			getUIFileNode();
			if(selectedItems_.empty()) return;

			std::wstring strButtonName = iniLanguageHelper.GetCommonString(COMMENT_SAVE_KEY).c_str();
			std::wstring strTitle = iniLanguageHelper.GetCommonString(COMMENT_SAVETOONEBOX_KEY).c_str();

			CommonFileDialogPtr myFileDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
			MyFileCommonFileDialogNotify *notify = new SaveToMyFileDialogNotify(context_, iniLanguageHelper.GetLanguage());			
			myFileDialog->setOption(CFDO_only_show_folder);
			myFileDialog->setNotify(notify);
			isShowModal_ = true;
			if (E_CFD_CANCEL == myFileDialog->showModal(Onebox::resultHanlder,COMMFILEDIALOG_SAVETOMYFILE))
			{
				isShowModal_ = false;
				return;
			}
			isShowModal_ = false;

			int iControlIndex = myFileDialog->GetControlIndex();

			if(1!=commonFileData.size())return;
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
			int64_t destOwnerId = context_->getUserInfoMgr()->getUserId();
			addRestTask(selectedItems_.begin()->second.userContext, srcNodeList, destOwnerId, destFolderId, RESTTASK_SAVE, true);
		}

		void exitClick(TNotifyUI& msg)
		{
			if (NULL == m_noticeFrame_)return;
			m_noticeFrame_->Run(Choose,Ask,MSG_SETTING_SHARETILE_KEY,MSG_CANCLSHARE2MECONFIRM_NOTICE_KEY,Modal);

			if (!m_noticeFrame_->IsClickOk())
			{
				return;
			}

			getUIFileNode();
			for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
			{
				int32_t ret = context_->getShareResMgr()->exitShare(it->second.userContext->getUserInfoMgr()->getUserId(), it->first);
				if(RT_OK != ret)	
				{
					SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
					simlpeNoticeFrame->Show(Error,MSG_SHARE2ME_OPERATION_FAILED_KEY,it->second.basic.name.c_str(), ret,
						 ErrorConfMgr::getInstance()->getDescription(ret).c_str(),
						 ErrorConfMgr::getInstance()->getAdvice(ret).c_str());
					delete simlpeNoticeFrame;
					simlpeNoticeFrame = NULL;
					return;
				}
			}
			loadData(browseHistory_[curPage_], true);
		}

		void listSelectchanged(TNotifyUI& msg)
		{
			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			pControl->SelectItem(0);
			ShareDirNode shareDirNode = browseHistory_[curPage_];
			shareDirNode.isListView = true;
			loadData(shareDirNode, false);
		}

		void tabloidSelectchanged(TNotifyUI& msg)
		{
			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			pControl->SelectItem(1);
			ShareDirNode shareDirNode = browseHistory_[curPage_];
			shareDirNode.isListView = false;
			loadData(shareDirNode, false);
		}

		void searchbtnClick(TNotifyUI& msg)
		{
			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(SHARE2ME_SEARCHTXT));
			ShareDirNode shareDirNode = browseHistory_[curPage_];
			shareDirNode.keyWord = searchtxt->GetText();
			boost::algorithm::trim(shareDirNode.keyWord);
			loadData(shareDirNode, false);
		}

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

		void nameReturn(TNotifyUI& msg)
		{
			isRenaming_ = true;
			CRenameRichEditUI *richEdit = static_cast<CRenameRichEditUI*>(msg.pSender);
			if (NULL == richEdit) return;
			CControlUI* temp = static_cast<CControlUI*>(richEdit->GetParent());
			if (NULL == temp) return;
			CShare2MeElementUI* pItem = static_cast<CShare2MeElementUI*>(temp->GetParent());
			if (NULL == pItem)	return;

			ShareDirNode curShareDirNode = browseHistory_[curPage_];
			int tipType = -1;
			std::wstring str_des = L"";
			std::wstring str_newName = richEdit->GetText();
			str_newName = SD::Utility::String::replace_all(str_newName,L"\r",L" ");

			if (-1 ==pItem->m_uNodeData.basic.id)
			{
				Path path = curShareDirNode.shareContext->getPathMgr()->makePath();
				path.id(curShareDirNode.id);
				pItem->m_uNodeData.basic.name = str_newName;
				FILE_DIR_INFO fdi = pItem->m_uNodeData.basic;
				int32_t ret = curShareDirNode.shareContext->getSyncFileSystemMgr()->create(path,str_newName,fdi,ADAPTER_FOLDER_TYPE_REST);
				if (RT_OK == ret)
				{
					tipType = Right;
					str_des =  MSG_CREATEDIR_SUCCESSFUL_KEY;
					if(!curShareDirNode.isListView)
					{
						pItem->SetToolTip(str_newName.c_str());
					}
					richEdit->SetToolTip(str_newName.c_str());
					pItem->m_uNodeData.basic.id = fdi.id;
				}
				else
				{
					if(curShareDirNode.isListView)
					{
						share2MeList_->Remove(pItem);
					}
					else
					{
						share2MeTile_->Remove(pItem);
					}
					if (RT_DIFF_FILTER == ret|| RT_FILE_EXIST_ERROR == ret)
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
				if (str_newName == pItem->m_uNodeData.basic.name) return;
				Path path = curShareDirNode.shareContext->getPathMgr()->makePath();
				path.id(pItem->m_uNodeData.basic.id);
				ADAPTER_FILE_TYPE type = (pItem->m_uNodeData.basic.type == FILE_TYPE_DIR)?ADAPTER_FOLDER_TYPE_REST : ADAPTER_FILE_TYPE_REST;
				int32_t ret = curShareDirNode.shareContext->getSyncFileSystemMgr()->rename(path, str_newName, type);
				if (RT_OK == ret)
				{
					tipType = Right;
					str_des =  MSG_RENAME_SUCCESSFUL_KEY;
					pItem->m_uNodeData.basic.name = str_newName;
					richEdit->SetToolTip(str_newName.c_str());
					if(!curShareDirNode.isListView)
					{
						pItem->SetToolTip(str_newName.c_str());
					}
				}
				else
				{
					richEdit->SetText(pItem->m_uNodeData.basic.name.c_str());
					richEdit->SetToolTip(pItem->m_uNodeData.basic.name.c_str());
					if(!curShareDirNode.isListView)
					{
						pItem->SetToolTip(pItem->m_uNodeData.basic.name.c_str());
					}
					if (RT_DIFF_FILTER == ret || RT_FILE_EXIST_ERROR == ret)
					{
						SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
						simlpeNoticeFrame->Show(Error, MSG_RENAME_FAILED_EX_KEY,  
							ErrorConfMgr::getInstance()->getDescription(ret).c_str());
						delete simlpeNoticeFrame;
						simlpeNoticeFrame = NULL;
						//loadData(browseHistory_[curPage_], true);
						return;
					}
					else
					{
						tipType = Error;
						str_des = MSG_RENAME_FAILED_KEY;
						//loadData(browseHistory_[curPage_], true);
					}
				}
			}
			SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
			simlpeNoticeFrame->Show((NoticeType)tipType, str_des.c_str());
			delete simlpeNoticeFrame;
			simlpeNoticeFrame = NULL;
		}

		void itemdbclick(TNotifyUI& msg)
		{
			CShare2MeElementUI* pItem = static_cast<CShare2MeElementUI*>(msg.pSender);
			if(NULL==pItem) return;
			if(FILE_TYPE_FILE == pItem->m_uNodeData.basic.type)
			{
				File_Permissions filePermissions;
				if(0==browseHistory_[curPage_].id)
				{
					Path filePath = pItem->m_uNodeData.userContext->getPathMgr()->makePath();
					filePath.id(pItem->m_uNodeData.basic.id);
					pItem->m_uNodeData.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
						context_->getUserInfoMgr()->getUserId(), filePermissions);
				}
				else
				{
					filePermissions = browseHistory_[curPage_].filePermissions;
				}
				if(0==(filePermissions&FP_DOWNLOAD)) return;

				bool isOpen = dbClickEnabled(pItem->m_uNodeData.basic.name);
				OpenFileDbClickDialog* pDialog = new OpenFileDbClickDialog(pItem->m_uNodeData.userContext, pItem->m_uNodeData, isOpen);
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
					itemList.push_back(pItem->m_uNodeData);
					(void)TransTaskMgr::download(itemList, path, true);
				}
				else if (tmp == 2) //download file
				{
					std::list<UIFileNode> itemList;
					itemList.push_back(pItem->m_uNodeData);
					download(itemList);
				}
			}
			else
			{
				//open folder
				ShareDirNode shareDirNode;
				shareDirNode.id = pItem->m_uNodeData.basic.id;
				shareDirNode.ownerId = pItem->ownerId;
				shareDirNode.ownerName = pItem->ownerName;
				shareDirNode.shareTime = pItem->shareTime;
				shareDirNode.isListView = browseHistory_[curPage_].isListView ;
				shareDirNode.shareFileName = pItem->m_uNodeData.basic.name;
				shareDirNode.parentList = browseHistory_[curPage_].parentList;
				PathNode pathNode;
				pathNode.fileId = browseHistory_[curPage_].id;
				pathNode.fileName = browseHistory_[curPage_].shareFileName;
				shareDirNode.parentList.push_back(pathNode);
				shareDirNode.shareContext = pItem->m_uNodeData.userContext;
				Path filePath = shareDirNode.shareContext->getPathMgr()->makePath();
				filePath.id(shareDirNode.id);
				shareDirNode.shareContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
					context_->getUserInfoMgr()->getUserId(), shareDirNode.filePermissions);

				parentDirInfo_ = pItem->m_uNodeData.basic;

				loadData(shareDirNode, false);
			}
		}

		void showButton()
		{
			if(isNextPage_) return;
			
			//前进、后退按钮
			bool isFirst = (curPage_==0);
			bool isLast = (curPage_+1==browseHistory_.size());
			backBtn_->SetEnabled(!isFirst);
			nextBtn_->SetEnabled(!isLast);

			//搜素框
			searchLayout_->SetVisible(0==browseHistory_[curPage_].id);

			File_Permissions filePermissions = browseHistory_[curPage_].filePermissions;
			getUIFileNode();

			CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(SHARE2ME_LISTTAB));
			if ( m_nQueryCount <= 0)
			{
				m_nQueryCount = 0;
				if (pControl)
				{
					if(0==browseHistory_[curPage_].id)
					{
						pControl->SelectItem(2);
					}
					else
					{
						pControl->SelectItem(3);
						// DTS2016012909956
						pControl->SetFocus();
					}
				}
			}else
			{
				COptionUI* pList = static_cast<COptionUI*>(paintManager_.FindControl(_T("share2Me_listOpt")));
				if (pControl && pList)
				{
					if (pList->IsSelected())
						pControl->SelectItem(0);
					else
						pControl->SelectItem(1);
				}
			}

			if ( NULL != versionBtn_){
				if ((1 == selectedItems_.size() && (FILE_TYPE_FILE == selectedItems_.begin()->second.basic.type)))
					versionBtn_->SetVisible( true);
				else
					versionBtn_->SetVisible(false);
			}

			//第一层
			if(0==browseHistory_[curPage_].id)
			{
				uploadBtn_->SetVisible(false);
				createBtn_->SetVisible(false);
				shareBtn_->SetVisible(false);
				moreBtn_->SetVisible(false);

				if(selectedItems_.empty())
				{
					
					exitBtn_->SetVisible(false);
					std::wstringstream showText;
					showText<< L"{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(browseHistory_[curPage_].keyWord.empty()?MSG_SHARE2ME_SHOWBTN_START_TEXT_KEY:MSG_SHARE2ME_SHOWBTN_START_KEYWORD_KEY).c_str() << L"{/f}{/c}";
					showText<< L" {c #000000}{f 12}" << m_nQueryCount << L"{/f}{/c} ";
					showText<< L"{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(browseHistory_[curPage_].keyWord.empty()?MSG_SHARE2ME_SHOWBTN_END_TEXT_KEY:MSG_SHARE2ME_SHOWBTN_END_KEYWORD_KEY).c_str() << L"{/f}{/c}";
					cntText_->SetText(showText.str().c_str());
					cntText_->SetVisible(true);
				}
				else
				{
					exitBtn_->SetVisible(true);
					cntText_->SetVisible(false);
				}

				if(1==selectedItems_.size())
				{
					UserContext* shareContext = selectedItems_.begin()->second.userContext;
					UIFileNode fileNode = selectedItems_.begin()->second;
					if(NULL != shareContext)
					{
						filePermissions = FP_DOWNLOAD;
						Path filePath = shareContext->getPathMgr()->makePath();
						filePath.id(selectedItems_.begin()->second.basic.id);
						shareContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
							context_->getUserInfoMgr()->getUserId(), filePermissions);
					}
					if(filePermissions&FP_DOWNLOAD)
					{
						if(EXTRA_TYPE_COMPUTER != fileNode.basic.extraType && EXTRA_TYPE_DISK != fileNode.basic.extraType)
						{
							saveBtn_->SetVisible(true);
						}
						else
						{
							saveBtn_->SetVisible(false);
						}
						downloadBtn_->SetVisible(true);
					}
					else
					{
						saveBtn_->SetVisible(false);
						downloadBtn_->SetVisible(false);
					}
				}
				else
				{
					saveBtn_->SetVisible(false);
					downloadBtn_->SetVisible(false);				
				}
				return;
			}

			UserContext* shareContext = browseHistory_[curPage_].shareContext;
			Path filePath = shareContext->getPathMgr()->makePath();
			filePath.id(browseHistory_[curPage_].id);
			shareContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
				context_->getUserInfoMgr()->getUserId(), filePermissions);

			//第二层以上、FP_EDIT权限捆绑上传、下载、删除、分享
			if (EXTRA_TYPE_COMPUTER == parentDirInfo_.extraType)
			{
				uploadBtn_->SetVisible(false);
				createBtn_->SetVisible(false);
			}
			else
			{
				uploadBtn_->SetVisible(0!=(filePermissions&FP_UPLOAD));
				createBtn_->SetVisible(0!=(filePermissions&FP_EDIT));
			}
			exitBtn_->SetVisible(false);

			if(selectedItems_.empty())
			{
				shareBtn_->SetVisible(false);
				saveBtn_->SetVisible(false);
				downloadBtn_->SetVisible(false);	
				moreBtn_->SetVisible(false);
			}
			else
			{
				shareBtn_->SetVisible((1==selectedItems_.size()) && (0!=(filePermissions&FP_PUBLISHLINK)));
				downloadBtn_->SetVisible(0!=(filePermissions&FP_DOWNLOAD));	
				moreBtn_->SetVisible(0!=(filePermissions&FP_EDIT));
				if((filePermissions&FP_DOWNLOAD)&&!(filePermissions&FP_EDIT))
				{
					//有下载权限，且有位置显示时显示
					saveBtn_->SetVisible(true);
				}
				else
				{
					saveBtn_->SetVisible(false);
				}
			}

			if(selectedItems_.empty()&&!((filePermissions&FP_UPLOAD)||(filePermissions&FP_EDIT)))
			{
				std::wstringstream showText;
				showText<< L"{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(MSG_SHARE2ME_SHOWBTNNAME_START_TEXT_KEY).c_str()  << "{/f}{/c}"
					<< L" {c #000000}{f 12}" << browseHistory_[curPage_].ownerName << L" {/f}{/c}"
					<< L"{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(MSG_SHARE2ME_SHOWBTNNAME_END_TEXT_KEY).c_str() << "{/f}{/c}";
				cntText_->SetText(showText.str().c_str());
				cntText_->SetVisible(true);
			}
			else
			{
				cntText_->SetVisible(false);
			}
		}

		void download(std::list<UIFileNode>& itemList)
		{
			if (itemList.empty()) return;
			if (true == downloadFlag_) return;

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

			(void)TransTaskMgr::download(itemList, strDownloadDir);
		}

		void addRestTask(UserContext* userContext, const std::list<int64_t>& srcNodeList, int64_t destOwnerId, int64_t destFolderId, const std::string& type, bool autoRename = false)
		{
			int32_t ret = userContext->getRestTaskMgr()->addRestTask(userContext->getUserInfoMgr()->getUserId(), 
				browseHistory_[curPage_].id, srcNodeList, destOwnerId, destFolderId, type, autoRename);
			if(RT_RESTTASK_DOING==ret)
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				simlpeNoticeFrame->Show(Error, MSG_RESTTASK_START_DOING, 
					simlpeNoticeFrame->GetShowMsg(SD::Utility::String::string_to_wstring(userContext->getRestTaskMgr()->getLastType())).c_str());
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

		void itemDragMove(TNotifyUI& msg)
		{
			if(0 == browseHistory_[curPage_].id) return;

			getUIFileNode();
			if(selectedItems_.empty()) return;

			CShare2MeElementUI* pm = NULL;
			if(browseHistory_[curPage_].isListView)
			{
				pm = static_cast<CShare2MeElementUI*>(share2MeList_->GetItemAt(share2MeList_->getCurEnter()));
			}
			else
			{
				pm = static_cast<CShare2MeElementUI*>(share2MeTile_->GetItemAt(share2MeTile_->getCurEnter()));
			}	
			if(pm==NULL) return;
			if(selectedItems_.find(pm->m_uNodeData.basic.id) != selectedItems_.end() || FILE_TYPE_FILE == pm->m_uNodeData.basic.type)  return;

			m_noticeFrame_->Run(Choose, Ask, MSG_MOVECONFIRM_TITLE_KEY, MSG_MOVECONFIRM_NOTICE_KEY, Modal);
			if (!m_noticeFrame_->IsClickOk()) return;

			bool isRename = true;
			if (checkFileIsRename(selectedItems_, pm->m_uNodeData.basic.id, pm->m_uNodeData.userContext))
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
			addRestTask(selectedItems_.begin()->second.userContext, srcNodeList, 
				browseHistory_[curPage_].ownerId, pm->m_uNodeData.basic.id, RESTTASK_MOVE, isRename);
		}

		void itemDragFile(TNotifyUI& msg)
		{
			File_Permissions filePermissions = browseHistory_[curPage_].filePermissions;

			std::list<std::wstring> drogFile;	
			std::wstring tempItemName = msg.pSender->GetName();
			UIFileNode fileNode;
			fileNode.basic.id = browseHistory_[curPage_].id;
			fileNode.userContext = browseHistory_[curPage_].shareContext;
			if(SHARE2ME_LIST==tempItemName)
			{
				if(share2MeList_==NULL) return;
				CShare2MeElementUI* pm = static_cast<CShare2MeElementUI*>(share2MeList_->GetItemAt(share2MeList_->getCurEnter()));
				if(pm!=NULL && pm->m_uNodeData.basic.type == FILE_TYPE_DIR)
				{
					fileNode.basic.id = pm->m_uNodeData.basic.id;
					fileNode.userContext = pm->m_uNodeData.userContext;
				}
				drogFile = share2MeList_->getDropFile();
				share2MeList_->clearDropFile();
				if (drogFile.empty())  return;
			}
			else if(SHARE2ME_TILE==tempItemName)
			{
				if(share2MeTile_==NULL) return;
				CShare2MeElementUI* pm = static_cast<CShare2MeElementUI*>(share2MeTile_->GetItemAt(share2MeTile_->getCurEnter()));
				if(pm!=NULL && pm->m_uNodeData.basic.type == FILE_TYPE_DIR)
				{
					fileNode.basic.id = pm->m_uNodeData.basic.id;
					fileNode.userContext = pm->m_uNodeData.userContext;
				}
				drogFile = share2MeTile_->getDropFile();
				share2MeTile_->clearDropFile();
				if (drogFile.empty())  return;
			}
			else
			{
				CCustomListUI* fileList = static_cast<CCustomListUI*>(msg.pSender);
				if(fileList==NULL) return;
				drogFile = fileList->getDropFile();
				fileList->clearDropFile();			
			}

			if(0 == browseHistory_[curPage_].id)
			{
				filePermissions = FP_DOWNLOAD;
				Path filePath = fileNode.userContext->getPathMgr()->makePath();
				filePath.id(fileNode.basic.id);
				fileNode.userContext->getSyncFileSystemMgr()->getFilePermissions(filePath,
					context_->getUserInfoMgr()->getUserId(), filePermissions);
			}
			
			if(0==(filePermissions&FP_UPLOAD))
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				simlpeNoticeFrame->Show(Error, MSG_NO_PERMISSIONS_KEY);
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
				return;
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
			
			if (ProxyMgr::getInstance(context_)->checkDirIsExist(drogFile, fileNode.userContext, fileNode.basic.id))
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

		bool checkHasDir(std::list<std::wstring>& sourceList)
		{
			for (std::list<std::wstring>::iterator itor = sourceList.begin();itor != sourceList.end();itor++)
			{
				if (SD::Utility::FS::is_directory(*itor))
				{
					return true;
				}
			}
			return false;
		}

		void selectAllItemClick(TNotifyUI& msg)
		{
			if (browseHistory_[curPage_].isListView)
			{
				if (NULL == share2MeList_) return;
				share2MeList_->SelectAllItem(true);
			}
			else
			{
				if (NULL == share2MeTile_) return;
				share2MeTile_->SelectAllItem(true);
			}

			msg.wParam = true;
			selectAllClick(msg);
		}

		void nextPage(TNotifyUI& msg)
		{
			int32_t range = msg.wParam;
			int32_t pos = msg.lParam;
			//无下一页，或未滚到倒数SCROLL_LIMEN条以下，不处理
			if((!browseHistory_[curPage_].keyWord.empty())||(curPageParam_.offset + curPageParam_.limit >= curCnt_) || (pos < range - SHARE2ME_SCROLL_LIMEN)) return;

			curPageParam_.offset += pageLimit_;
			isNextPage_ = true;
			loadMetaData(browseHistory_[curPage_]);
		}

		void selectAllClick(TNotifyUI& msg)
		{
			selectedItems_.clear();
			if(msg.wParam==0) return;

			//选中全页对象
			ShareDirNode shareDirNode = browseHistory_[curPage_];
			ShareNodeParent parent;
			parent.id = shareDirNode.id;
			parent.keyWord = SD::Utility::String::wstring_to_utf8(shareDirNode.keyWord);
			parent.ownerId = shareDirNode.ownerId;
			parent.ownerName = SD::Utility::String::wstring_to_utf8(shareDirNode.ownerName);
			parent.shareTime = shareDirNode.shareTime;
			ShareNodeList shareNodes;
			int64_t count = 0;
			PageParam pageParam;
			pageParam.offset = 0;
			pageParam.limit = 0;
			ProxyMgr::getInstance(context_)->listReceiveShareRes(shareDirNode.shareContext, parent, shareNodes, 
				pageParam, count, false);

			for(ShareNodeList::const_iterator it = shareNodes.begin(); it != shareNodes.end(); ++it)
			{
				UIFileNode nodeData;
				nodeData.basic.id = it->id();
				nodeData.basic.name = SD::Utility::String::utf8_to_wstring(it->name());
				nodeData.basic.type = it->type();
				nodeData.basic.size = it->size();
				nodeData.basic.parent = shareDirNode.id;
				nodeData.userContext = shareDirNode.shareContext;

				selectedItems_.insert(std::make_pair(nodeData.basic.id, nodeData));
			}
		}

		void copyMove(TNotifyUI& msg)
		{
			getUIFileNode();
			if(selectedItems_.empty()) return;

			ShareDirNode curShareDirNode = browseHistory_[curPage_];
			if(browseHistory_[curPage_].parentList.empty()) return;

			std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
			std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_copymove").c_str();

			std::shared_ptr<st_CommonFileDialogItem> fileItem(new st_CommonFileDialogItem());
			//根节点为父节点中除去0之外的第一个节点，只有一个父节点时，根节点为当前节点。
			MyFileCommonFileDialogData *fileData = new MyFileCommonFileDialogData;
			fileItem->data.reset(fileData);
			MyFileCommonFileDialogNotify *notify = new MyFileCommonFileDialogNotify(curShareDirNode.shareContext, iniLanguageHelper.GetLanguage());			
			if(1==browseHistory_[curPage_].parentList.size())
			{
				fileItem->name = curShareDirNode.shareFileName;
				fileData->id =  curShareDirNode.id;
			}
			else
			{ 
				std::list<PathNode>::iterator it = curShareDirNode.parentList.begin();
				it++;
				fileItem->name = (*it).fileName;
				fileData->id =  (*it).fileId;
			}
			fileData->type = MFRFDT_FILENODE;
			fileData->userContext = curShareDirNode.shareContext;
			fileItem->autoExpand = true;
			fileItem->dataType = CFDDT_remote;
			fileItem->type = CFDFT_DIR;
			fileItem->notify = notify;

			CommonFileDialogPtr myFileDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),fileItem,strButtonName,strTitle,strTitle,strTitle,strTitle);
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

			if(1!=commonFileData.size()) return;
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
			addRestTask(curShareDirNode.shareContext, srcNodeList, curShareDirNode.ownerId, destFolderId, type, true);
		}

		void deleteFile(TNotifyUI& msg)
		{
			getUIFileNode();
			if(selectedItems_.empty()) return;

			m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
			if (!m_noticeFrame_->IsClickOk()) return;
			std::list<int64_t> srcNodeList;
			for(std::map<int64_t, UIFileNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
			{
				srcNodeList.push_back(it->first);
			}
			addRestTask(browseHistory_[curPage_].shareContext, srcNodeList, 0, 0, RESTTASK_DELETE);

		}

		/************************************************************************/
		/* 0. Open
		/* 1. Share link
		/* 2. Download
		/* 3. Copy/Move
		/* 4. Delete
		/* 5. Rename
		/* 6. Save to my space
		/* 7. List versions
		/* 8. Cancel share
		/************************************************************************/
		void menuAdded(TNotifyUI& msg)
		{
			if (NULL == msg.pSender)
			{
				return;
			}

			getUIFileNode();			

			CMenuUI* pMenu = (CMenuUI*)msg.wParam;
			if (NULL == pMenu) return;

			doMenuPoint(pMenu, msg.ptMouse);

			if (browseHistory_.size() < curPage_)
			{
				pMenu->SetVisible(false);
				return;
			}

			//bool isRoot = (browseHistory_[curPage_].id == ROOT_PARENTID);

			// selected none
			if (selectedItems_.empty())
			{
				pMenu->SetVisible(false);
				return;
			}

			CMenuItemUI* pMenuItem = NULL;

			for (int i = 0; i < MODULE_MENUCOUNT; ++i)
			{	
				pMenuItem = pMenu->GetMenuItemById(i);
				if (NULL != pMenuItem) pMenuItem->SetVisible(false);
			}

			if (selectedItems_.size() == 1)
			{
				UIFileNode fileNode = selectedItems_.begin()->second;

				if (fileNode.basic.type == FILE_TYPE_FILE)
				{
					// version
					pMenuItem = pMenu->GetMenuItemById(7);
					if (NULL != pMenuItem) pMenuItem->SetVisible();
				}
				else
				{
					// open
					pMenuItem = pMenu->GetMenuItemById(0);
					if (NULL != pMenuItem) pMenuItem->SetVisible();

				}
			}

			if (shareBtn_->IsVisible())
			{
				pMenuItem = pMenu->GetMenuItemById(1);
				if (NULL != pMenuItem) pMenuItem->SetVisible();
			}
			if (downloadBtn_->IsVisible())
			{
				pMenuItem = pMenu->GetMenuItemById(2);
				if (NULL != pMenuItem) pMenuItem->SetVisible();
				if(ifShowBtn())
				{
					pMenuItem= NULL;
					pMenuItem = pMenu->GetMenuItemById(6);
					if (NULL != pMenuItem) pMenuItem->SetVisible();
				}
			}
			if (exitBtn_->IsVisible())
			{
				pMenuItem = pMenu->GetMenuItemById(8);
				if (NULL != pMenuItem) pMenuItem->SetVisible(); 
			}
			if (moreBtn_->IsVisible())
			{
				if(ifShowBtn())
				{
					pMenuItem = pMenu->GetMenuItemById(3);
					if (NULL != pMenuItem) pMenuItem->SetVisible(); 
					pMenuItem = NULL;
					pMenuItem = pMenu->GetMenuItemById(4);
					if (NULL != pMenuItem) pMenuItem->SetVisible(); 
				}
			}
			if (shareBtn_->IsVisible())
			{
				pMenuItem = pMenu->GetMenuItemById(5);
				if (NULL != pMenuItem) pMenuItem->SetVisible(); 
			}

		}

		void menuItemClick(TNotifyUI& msg)
		{
			int menuId = msg.lParam;
			if (menuId < 0)
			{
				return;
			}
			TNotifyUI msgItem;
			CShare2MeElementUI* currentItem = NULL;
			 if ( browseHistory_[curPage_].isListView)
			 {
				 currentItem =  static_cast<CShare2MeElementUI*>(share2MeList_->GetItemAt(share2MeList_->GetCurSel()));
			 }
			 else
			 {
				 currentItem =  static_cast<CShare2MeElementUI*>(share2MeTile_->GetItemAt(share2MeTile_->GetCurSel()));
			 }
			
			if (NULL == currentItem) return;
			msgItem.pSender = currentItem;
			switch (menuId)
			{
			case 0:
				// open
				itemdbclick(msgItem);
				break;
			case 1:
				// link
				 shareClick(msgItem);
				break;
			case 2:
				// download
				downloadClick(msgItem);
				break;
			case 3:
				// copymove
				copyMove(msgItem);
				break;
			case 4:
				//delete
				deleteFile(msgItem);
				break;
			case 5:
				//rename
				currentItem->rename();
				break;
				// save to my files
			case 6:
				saveClick(msgItem);
				break;
				// version
			case 7:
				{
					FileVersionDialog *pVersion = new FileVersionDialog(currentItem->m_uNodeData.userContext, currentItem->m_uNodeData.basic,m_storageUserInfo.user_id);
					pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
					pVersion->CenterWindow();
					isShowModal_ = true;
					pVersion->ShowModal();
					isShowModal_ = false;
					delete pVersion;
					pVersion = NULL;
				}
				break;
				// exit share
			case 8:
				exitClick(msgItem);
				break;
			default:
				break;
			}
		}

		/************************************************************************/
		/* 0. Copy/Move
		/* 1. Delete
		/* 2. Rename
		/* 3. Save to my space
		/* 4. List verions
		/************************************************************************/
		void moreMenuAdded(TNotifyUI& msg)
		{
			if (NULL == msg.pSender)
			{
				return;
			}

			getUIFileNode();			

			CMenuUI* pMenu = (CMenuUI*)msg.wParam;
			if (NULL == pMenu) return;

			if (browseHistory_.size() < curPage_)
			{
				pMenu->SetVisible(false);
				return;
			}

			// selected none
			if (selectedItems_.empty())
			{
				pMenu->SetVisible(false);
				return;
			}
			CMenuItemUI* pMenuItem = NULL;
			if (selectedItems_.size() == 1)
			{
				UIFileNode fileNode = selectedItems_.begin()->second;
				if (fileNode.basic.type == FILE_TYPE_DIR)
				{
					pMenuItem = pMenu->GetMenuItemById(4);
					if (NULL != pMenuItem) pMenuItem->SetVisible(false);			
				}
			}
			else
			{
				pMenuItem = pMenu->GetMenuItemById(2);
				if (NULL != pMenuItem) pMenuItem->SetVisible(false);		
				pMenuItem = pMenu->GetMenuItemById(4);
				if (NULL != pMenuItem) pMenuItem->SetVisible(false);
			}

			if(!ifShowBtn())
			{
				pMenuItem = pMenu->GetMenuItemById(0);
				if (NULL != pMenuItem) pMenuItem->SetVisible(false);	
				pMenuItem = pMenu->GetMenuItemById(1);
				if (NULL != pMenuItem) pMenuItem->SetVisible(false);		
				pMenuItem = pMenu->GetMenuItemById(3);
				if (NULL != pMenuItem) pMenuItem->SetVisible(false);
			}
		}

		void moreMenuItemClick(TNotifyUI& msg)
		{
			int menuId = msg.lParam;
			if (menuId < 0)
			{
				return;
			}
			TNotifyUI msgItem;
			CShare2MeElementUI* currentItem = NULL;
			if ( browseHistory_[curPage_].isListView)
			{
				currentItem =  static_cast<CShare2MeElementUI*>(share2MeList_->GetItemAt(share2MeList_->GetCurSel()));
			}
			else
			{
				currentItem =  static_cast<CShare2MeElementUI*>(share2MeTile_->GetItemAt(share2MeTile_->GetCurSel()));
			}

			if (NULL == currentItem) return;
			msgItem.pSender = currentItem;
			switch (menuId)
			{
			case 0:
				// copymove
				copyMove(msgItem);
				break;
			case 1:
				// delete
				deleteFile(msgItem);
				break;
			case 2:
				// rename
				currentItem->rename();
				break;
			case 3:
				//save
				saveClick(msgItem);
				break;
			case 4:
				//version
				{
					FileVersionDialog *pVersion = new FileVersionDialog(currentItem->m_uNodeData.userContext, currentItem->m_uNodeData.basic,m_storageUserInfo.user_id);
					pVersion->Create(paintManager_.GetPaintWindow(),_T("FileVersionDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
					pVersion->CenterWindow();
					isShowModal_ = true;
					pVersion->ShowModal();
					isShowModal_ = false;
					delete pVersion;
					pVersion = NULL;
				}
				break;
			default:
				break;
			}
		}

		void clearSearchBtnClick(TNotifyUI& msg)
		{
			CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(SHARE2ME_SEARCHTXT));
			if (NULL != searchtxt)
				searchtxt->resetText(L"");

			CButtonUI* pBtn = (CButtonUI*)(msg.pSender);
			if (pBtn)
				pBtn->SetVisible(false);

			ShareDirNode shareDirNode = browseHistory_[curPage_];
			shareDirNode.keyWord = L"";
			loadData(shareDirNode, false);

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
				if(NULL == share2MeList_) return;
				CStdValArray* curSelects = share2MeList_->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					Share2MeListContainerElement* element = static_cast<Share2MeListContainerElement*>(share2MeList_->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == element) continue;

					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
			else
			{
				if(NULL == share2MeTile_) return;
				CStdValArray* curSelects = share2MeTile_->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					Share2MeListContainerElement* element = static_cast<Share2MeListContainerElement*>(share2MeTile_->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL == element) continue;

					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
		}

		void deleteItem()
		{
			TNotifyUI msg;
			exitClick(msg);
// 		if (!moreBtn_->IsVisible()) return;
// 
// 			CShare2MeElementUI* currentItem = NULL;
// 			if ( browseHistory_[curPage_].isListView)
// 			{
// 				currentItem =  static_cast<CShare2MeElementUI*>(share2MeList_->GetItemAt(share2MeList_->GetCurSel()));
// 			}
// 			else
// 			{
// 				currentItem =  static_cast<CShare2MeElementUI*>(share2MeTile_->GetItemAt(share2MeTile_->GetCurSel()));
// 			}
// 
// 			if (NULL == currentItem) return;
// 
// 			TNotifyUI msgItem;
// 			msgItem.pSender = currentItem;
// 			deleteFile(msgItem);
		}

		bool ifShowBtn()
		{
			// Check if contains computer folder or disk folder
			bool ifShow = false;
			for(std::map<int64_t, UIFileNode>::const_iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
			{			
				if((EXTRA_TYPE_COMPUTER != it->second.basic.extraType) && (EXTRA_TYPE_DISK != it->second.basic.extraType))
				{
					ifShow = true;
					break;
				}
			}
			return ifShow;
		}

	private:
		UserContext* context_;
		CPaintManagerUI& paintManager_;
		NoticeFrameMgr* m_noticeFrame_;

		std::map<std::wstring, call_func> funcMaps_;
		int64_t curCnt_;
		int64_t m_nQueryCount;
		std::vector<ShareDirNode> browseHistory_;
		PageParam curPageParam_;
		size_t curPage_;
		CShare2MeElementUI* rightItem_;		
		std::map<int64_t, UIFileNode> selectedItems_;
		int32_t pageLimit_;
		bool isNextPage_;
		bool isRenaming_;
		bool downloadFlag_;

		CButtonUI* backBtn_;
		CButtonUI* nextBtn_;
		CHorizontalLayoutUI* searchLayout_;
		CButtonUI* uploadBtn_;
		CButtonUI* createBtn_;
		CButtonUI* shareBtn_;
		CButtonUI* moreBtn_;
		CButtonUI* downloadBtn_;
		CButtonUI* saveBtn_;
		CButtonUI* exitBtn_;
		CButtonUI* versionBtn_;
		CTextUI* cntText_;
		CCustomListUI* share2MeList_;
		CTileLayoutListUI *share2MeTile_;
		CButtonUI*	m_pBtnClearSearch;

		SelectDialog* m_pSelectWnd;
		StorageUserInfo m_storageUserInfo;
		FILE_DIR_INFO parentDirInfo_;
		bool isShowModal_;
	};

	Share2MeMgr* Share2MeMgr::instance_ = NULL;

	Share2MeMgr* Share2MeMgr::getInstance(UserContext* context, CPaintManagerUI& paintManager)
	{
		if (NULL == instance_)
		{
			instance_ = new Share2MeMgrImpl(context, paintManager);
		}
		return instance_;
	}
}
#include "stdafxOnebox.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "ShareNode.h"
#include "ShareResMgr.h"
#include "ControlNames.h"
#include "UserInfoMgr.h"
#include "ListContainerElement.h"
#include "DialogBuilderCallbackImpl.h"
#include "TileLayoutListUI.h"
#include "CustomListUI.h"
#include "SearchTxt.h"
#include "PathMgr.h"
#include "SyncFileSystemMgr.h"
#include "ShareFrame.h"
#include "MyShareElement.h"
#include "GroupButton.h"
#include "MyShareMgr.h"
#include "NoticeFrame.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "ProxyMgr.h"
#include "NoticeFrame.h"
#include "CacheMyshare.h"
#include "NotifyMgr.h"
#include "ConfigureMgr.h"
#include <boost/algorithm/string.hpp>
#include "DelayLoadingFrame.h"
#include "UIScaleIconButton.h"
#include "ShareLinkCountDialog.h"
#include "SelectDialog.h"
#include <boost/thread.hpp>

#ifndef MODULE_NAME
#define MODULE_NAME ("MyShareMgr")
#endif

namespace Onebox {

	#define MYSHARE_SCROLL_LIMEN (100)	//触发翻页的滚动条阈值

    class MyShareMgrImpl : public MyShareMgr
    {
    public:
        MyShareMgrImpl(UserContext* context, CPaintManagerUI& paintManager);
        ~MyShareMgrImpl();

        virtual void initData();
        
        virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg);

		virtual void reloadCache()
		{
			loadData(browseHistory_[curPage_], false);
		}

        virtual bool reloadThumb(const std::string& thumbKey)
		{
			if (NULL == myShareList_)
				return false;

			for(int i = 0; i < myShareList_->GetCount(); ++i)
			{
				CMyShareElementUI* node = static_cast<CMyShareElementUI*>(myShareList_->GetItemAt(i));
				if (NULL == node)continue;
				if(node->isNoThumb)
				{
					if(node->flushThumb(thumbKey))
					{
						return true;
					}
				}
			}
			return false;
		}
        
		virtual void flushClick(TNotifyUI& msg);

    private:
		void loadData(const std::wstring& keyWord, bool isHistory = false);

		void loadDataAsyc(MyShareNodeList& nodeList, bool isShare, bool isFlush);
		void loadShareList(bool isFlush = false);
		void loadLinkList(bool isFlush = false);
		void loadShareTile(bool isFlush = false);
		void loadLinkTile(bool isFlush = false);

		void getUIFileNode();
		void getListUIFileNode(CCustomListUI* pList);
		void getTileUIFileNode(CTileLayoutListUI* pTile);

		void searchbtnClick(TNotifyUI& msg);

		void loadMyFileList(int64_t currentID);

		void canclshareClick(TNotifyUI& msg);

		void setshareClick(TNotifyUI& msg);

		void backClick(TNotifyUI& msg);

		void nextClick(TNotifyUI& msg);

		void showButton();

		void initHeader();
        void nameHeaderClick(TNotifyUI& msg);
		void pathHeaderClick(TNotifyUI& msg);
		void sizeHeaderClick(TNotifyUI& msg);
		void linkHeaderClick(TNotifyUI& msg);

		void pathItemClick(TNotifyUI& msg);
		void shareLinkClick(TNotifyUI& msg);

		void itemDbClick(TNotifyUI& msg);

        void headerClick(const std::string& name);

		void selectAllItemClick(TNotifyUI& msg);

		void nextPage(TNotifyUI& msg);

		void selectAllClick(TNotifyUI& msg);

		void sharedClick(TNotifyUI& msg);
		void linkedClick(TNotifyUI& msg);
		void listSelectchanged(TNotifyUI& msg);
		void tabloidSelectchanged(TNotifyUI& msg);
		void iconItemDbClick(TNotifyUI& msg);

		void menuAdded(TNotifyUI& msg);
		void menuItemClick(TNotifyUI& msg);

		void clearSearchBtnClick(TNotifyUI& msg);
		void searchTextChanged(TNotifyUI& msg);

		void doMenuPoint(CMenuUI* pMenu, POINT pt);
    private:
        UserContext* context_;
        CPaintManagerUI& paintManager_;
        std::map<std::wstring, call_func> funcMaps_;
        CCustomListUI* myShareList_;
		CTileLayoutListUI *myShareTile_;
        std::vector<std::wstring> browseHistory_;
        size_t curPage_ ;
        PageParam curPageParam_;
		int64_t curCnt_;
		std::map<int64_t, MyShareNode> selectedItems_;
		bool isNextPage_;
		int32_t pageLimit_;

        NoticeFrameMgr* m_noticeFrame_;
		CTabLayoutUI*	m_pShareOrLinkTab;
		CTabLayoutUI*	m_pShareTab;
		CTabLayoutUI*	m_pLinkTab;
		CCustomListUI*	m_pShareList;
		CCustomListUI*	m_pLinkList;
		CTileLayoutListUI*	m_pShareTile;
		CTileLayoutListUI*	m_pLinkTile;
		bool			m_bShareList;
		bool			m_bLinkList;
		COptionUI*		m_pListOpt;
		COptionUI*		m_pTileOpt;
		CButtonUI*		m_pBtnClearSearch;

		std::wstring	m_strShareKey;
		std::wstring	m_strLinkKey;
		std::wstring	m_strInputShareKey;	//只是输入的文字，
		std::wstring	m_strInputLinkKey;
		SelectDialog*	m_pSelectWnd;

		bool isLoading_;
		int32_t delayLoading_;
		boost::thread loadThread_;
		bool linkIsInCache_;
		bool shareIsInCache_;
    };

    MyShareMgrImpl::MyShareMgrImpl(UserContext* context, CPaintManagerUI& paintManager)
        :context_(context), paintManager_(paintManager)
    {
        CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYSHARE_SEARCHTXT));
        CDuiString defaultTxt = iniLanguageHelper.GetCommonString(MSG_MYSHARE_SEARCHDEFAULT_TEXT_KEY).c_str();
		if (searchtxt)        
			searchtxt->setDefaultTxt(defaultTxt);

		myShareList_ = static_cast<CCustomListUI*>(paintManager_.FindControl(MYSHARE_LIST));
		myShareTile_ = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(MYSHARE_TILE));
		m_strShareKey = L"";
		m_strLinkKey = L"";
		m_strInputShareKey = L"";
		m_strInputLinkKey = L"";

		m_pShareOrLinkTab = NULL;
		m_pShareTab = NULL;
		m_pLinkTab = NULL;
		m_pShareList = NULL;
		m_pLinkList = NULL;
		m_pShareTile = NULL;
		m_pLinkTile = NULL;
		m_pListOpt = NULL;
		m_pTileOpt = NULL;
		m_pBtnClearSearch = NULL;
		m_pSelectWnd = NULL;

		isLoading_ = false;
		delayLoading_ = context_->getConfigureMgr()->getConfigure()->delayLoading();
		linkIsInCache_ = false;
		shareIsInCache_ = false;

		m_pShareOrLinkTab = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"myShare_listTabShareOrLink"));
		m_pShareTab = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"myShare_listTabShare"));
		m_pLinkTab = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"myShare_listTabLink"));
		m_pShareList = static_cast<CCustomListUI*>(paintManager_.FindControl(L"myShare_ListShare"));
		m_pLinkList = static_cast<CCustomListUI*>(paintManager_.FindControl(L"myShare_ListLink"));
		m_pShareTile = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(L"myShare_TileShare"));
		m_pLinkTile = static_cast<CTileLayoutListUI*>(paintManager_.FindControl(L"myShare_TileLink"));
		m_pListOpt = static_cast<COptionUI*>(paintManager_.FindControl(L"myShare_listOpt"));
		m_pTileOpt = static_cast<COptionUI*>(paintManager_.FindControl(L"myShare_tabloidOpt"));
		m_pBtnClearSearch = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myShare_clearsearchbtn")));

        m_noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());
        funcMaps_.insert(std::make_pair(L"setShare_click", boost::bind(&MyShareMgrImpl::setshareClick, this, _1)));
        funcMaps_.insert(std::make_pair(L"canclShare_click", boost::bind(&MyShareMgrImpl::canclshareClick, this, _1)));
        funcMaps_.insert(std::make_pair(L"flush_click", boost::bind(&MyShareMgrImpl::flushClick, this, _1)));
        funcMaps_.insert(std::make_pair(L"back_click", boost::bind(&MyShareMgrImpl::backClick, this, _1)));
        funcMaps_.insert(std::make_pair(L"next_click", boost::bind(&MyShareMgrImpl::nextClick, this, _1)));
        funcMaps_.insert(std::make_pair(L"searchbtn_click", boost::bind(&MyShareMgrImpl::searchbtnClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListShare_selectItemChanged", boost::bind(&MyShareMgrImpl::showButton, this)));
		funcMaps_.insert(std::make_pair(L"TileShare_selectItemChanged", boost::bind(&MyShareMgrImpl::showButton, this)));
		funcMaps_.insert(std::make_pair(L"ListLink_selectItemChanged", boost::bind(&MyShareMgrImpl::showButton, this)));
		funcMaps_.insert(std::make_pair(L"TileLink_selectItemChanged", boost::bind(&MyShareMgrImpl::showButton, this)));
		funcMaps_.insert(std::make_pair(L"ListShare_selectchanged", boost::bind(&MyShareMgrImpl::showButton, this)));
		funcMaps_.insert(std::make_pair(L"TileShare_selectchanged", boost::bind(&MyShareMgrImpl::showButton, this)));
		funcMaps_.insert(std::make_pair(L"ListLink_selectchanged", boost::bind(&MyShareMgrImpl::showButton, this)));
		funcMaps_.insert(std::make_pair(L"TileLink_selectchanged", boost::bind(&MyShareMgrImpl::showButton, this)));

        funcMaps_.insert(std::make_pair(L"listheaderitem_name_headerclick", boost::bind(&MyShareMgrImpl::nameHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listheaderitem_path_headerclick", boost::bind(&MyShareMgrImpl::pathHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listheaderitem_size_headerclick", boost::bind(&MyShareMgrImpl::sizeHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listheaderitem_link_headerclick", boost::bind(&MyShareMgrImpl::linkHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listHeaderItemNameSortIcon_click", boost::bind(&MyShareMgrImpl::nameHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listHeaderItemPathSortIcon_click", boost::bind(&MyShareMgrImpl::pathHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listHeaderItemSizeSortIcon_click", boost::bind(&MyShareMgrImpl::sizeHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listHeaderItemLinkSortIcon_click", boost::bind(&MyShareMgrImpl::linkHeaderClick, this, _1)));

		funcMaps_.insert(std::make_pair(L"listHeaderItemName_click", boost::bind(&MyShareMgrImpl::nameHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listHeaderItemPath_click", boost::bind(&MyShareMgrImpl::pathHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listHeaderItemSize_click", boost::bind(&MyShareMgrImpl::sizeHeaderClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listHeaderItemLink_click", boost::bind(&MyShareMgrImpl::linkHeaderClick, this, _1)));

		funcMaps_.insert(std::make_pair(L"searchtxt_return", boost::bind(&MyShareMgrImpl::searchbtnClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"sharePath_click", boost::bind(&MyShareMgrImpl::pathItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"shareLink_click", boost::bind(&MyShareMgrImpl::shareLinkClick, this, _1)));
		//funcMaps_.insert(std::make_pair(L"listItem_itemdbclick", boost::bind(&MyShareMgrImpl::itemDbClick, this, _1)));
		//funcMaps_.insert(std::make_pair(L"iconItem_itemdbclick", boost::bind(&MyShareMgrImpl::iconItemDbClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListShare_listselectall", boost::bind(&MyShareMgrImpl::selectAllItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileShare_listselectall", boost::bind(&MyShareMgrImpl::selectAllItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListLink_listselectall", boost::bind(&MyShareMgrImpl::selectAllItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileLink_listselectall", boost::bind(&MyShareMgrImpl::selectAllItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"List_itemselectall", boost::bind(&MyShareMgrImpl::selectAllClick, this, _1)));

		funcMaps_.insert(std::make_pair(L"List_nextPage", boost::bind(&MyShareMgrImpl::nextPage, this,_1)));

		funcMaps_.insert(std::make_pair(L"ListShare_back", boost::bind(&MyShareMgrImpl::backClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListLink_back", boost::bind(&MyShareMgrImpl::backClick, this, _1)));

		funcMaps_.insert(std::make_pair(L"SharedOpt_selectchanged", boost::bind(&MyShareMgrImpl::sharedClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"LinkedOpt_selectchanged", boost::bind(&MyShareMgrImpl::linkedClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listOpt_selectchanged", boost::bind(&MyShareMgrImpl::listSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"tabloidOpt_selectchanged", boost::bind(&MyShareMgrImpl::tabloidSelectchanged, this, _1)));

		funcMaps_.insert(std::make_pair(L"ListShare_menuadded", boost::bind(&MyShareMgrImpl::menuAdded, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListShare_menuitemclick", boost::bind(&MyShareMgrImpl::menuItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListLink_menuadded", boost::bind(&MyShareMgrImpl::menuAdded, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListLink_menuitemclick", boost::bind(&MyShareMgrImpl::menuItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileShare_menuadded", boost::bind(&MyShareMgrImpl::menuAdded, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileShare_menuitemclick", boost::bind(&MyShareMgrImpl::menuItemClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileLink_menuadded", boost::bind(&MyShareMgrImpl::menuAdded, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileLink_menuitemclick", boost::bind(&MyShareMgrImpl::menuItemClick, this, _1)));

		funcMaps_.insert(std::make_pair(L"clearsearchbtn_click", boost::bind(&MyShareMgrImpl::clearSearchBtnClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"searchtxt_textchanged", boost::bind(&MyShareMgrImpl::searchTextChanged, this, _1)));

		funcMaps_.insert(std::make_pair(L"ListShare_itemdelete", boost::bind(&MyShareMgrImpl::canclshareClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"ListLink_itemdelete", boost::bind(&MyShareMgrImpl::canclshareClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileShare_itemdelete", boost::bind(&MyShareMgrImpl::canclshareClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"TileLink_itemdelete", boost::bind(&MyShareMgrImpl::canclshareClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"listItem_itemdelete", boost::bind(&MyShareMgrImpl::canclshareClick, this, _1)));
    }

	MyShareMgrImpl::~MyShareMgrImpl()
	{
		if (m_pSelectWnd)
		{
			m_pSelectWnd->Close();
			m_pSelectWnd = NULL;
		}
	}
    void MyShareMgrImpl::executeFunc(const std::wstring& funcName, TNotifyUI& msg)
    {
        std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
        if(it!=funcMaps_.end())
        {
            it->second(msg);
        }
    }

	void MyShareMgrImpl::initData()
	{
		m_bShareList = true;
		m_bLinkList = true;
		if (m_pListOpt)
			m_pListOpt->Selected(true);

        browseHistory_.clear();
        browseHistory_.push_back(L"");
        curPage_ = 0;
        curCnt_ = -1;
		isNextPage_ = false;

		pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
		curPageParam_.offset = 0;
        curPageParam_.limit = 2*pageLimit_;
        initHeader();

		if(ProxyMgr::getInstance(context_)->isMyShareCacheExist())
		{
			loadShareList(false);
		}
		else
		{
			loadShareList(true);
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

			if (m_pShareList)
			{
				m_pSelectWnd->Init(L"SelectDialog.xml", m_pShareList->GetManager()->GetPaintWindow(), pt);
				m_pShareList->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				m_pSelectWnd->SetWindowShow(false);
			}

			if (m_pShareTile)
			{
				m_pSelectWnd->Init(L"SelectDialog.xml", m_pShareTile->GetManager()->GetPaintWindow(), pt);
				m_pShareTile->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				m_pSelectWnd->SetWindowShow(false);
			}

			if ( m_pLinkList )
			{
				m_pSelectWnd->Init(L"SelectDialog.xml", m_pLinkList->GetManager()->GetPaintWindow(), pt);
				m_pLinkList->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				m_pSelectWnd->SetWindowShow(false);
			}

			if ( m_pLinkTile )
			{
				m_pSelectWnd->Init(L"SelectDialog.xml", m_pLinkTile->GetManager()->GetPaintWindow(), pt);
				m_pLinkTile->SetSelectWnd(m_pSelectWnd, m_pSelectWnd->m_hParentWnd, &m_pSelectWnd->m_DragPaintm);
				m_pSelectWnd->SetWindowShow(false);
			}
		}
    }

    void MyShareMgrImpl::loadData(const std::wstring& keyWord, bool isHistory)
	{
		if (NULL == m_pShareOrLinkTab || NULL == m_pShareTab || NULL == m_pLinkTab) return;

		if(!(isHistory||browseHistory_[curPage_]==keyWord))
		{
			for(;curPage_<browseHistory_.size()-1;)
			{
				browseHistory_.erase(--browseHistory_.end());
			}
			browseHistory_.push_back(keyWord);
			++curPage_;
		}

		pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
		if(!m_pListOpt->IsSelected())
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

		if (0 == m_pShareOrLinkTab->GetCurSel())
		{
			m_strShareKey = keyWord;

			if (m_pListOpt->IsSelected())
				loadShareList(isHistory);
			else
				loadShareTile(isHistory);
		}
		else
		{
			m_strLinkKey = keyWord;

			if (m_pListOpt->IsSelected())
				loadLinkList(isHistory);
			else
				loadLinkTile(isHistory);

			if (curCnt_ <= 0){
				if (m_pLinkTab)
					m_pLinkTab->SelectItem(2);
			}
		}
	}

	void MyShareMgrImpl::loadDataAsyc(MyShareNodeList& nodeList, bool isShare, bool isFlush)
	{
		if(isShare)
		{
			if(shareIsInCache_)
			{
				//每次启动客户端只强刷一次共享数据
				isFlush = false;
			}
			(void)ProxyMgr::getInstance(context_)->listMyShareRes(SD::Utility::String::wstring_to_utf8(
				browseHistory_[curPage_]), nodeList, curPageParam_, curCnt_, isFlush);
			if(isFlush)
			{
				shareIsInCache_ = true;
			}
		}
		else
		{
			if(linkIsInCache_)
			{
				//每次启动客户端只强刷一次外链数据
				isFlush = false;
			}
			(void)ProxyMgr::getInstance(context_)->listMyLinkRes(SD::Utility::String::wstring_to_utf8(
				browseHistory_[curPage_]), nodeList, curPageParam_, curCnt_, isFlush);
			if(isFlush)
			{
				linkIsInCache_ = true;
			}
		}
		isLoading_ = false;
	}

	void MyShareMgrImpl::loadShareList(bool isFlush /* = false */)
	{
		if (NULL == m_pShareList)	return;
		if(m_pShareList->GetCount()>0 && 0==curPageParam_.offset)
			m_pShareList->RemoveAll();

		MyShareNodeList nodeList;
		isLoading_ = true;
		loadThread_ = boost::thread(boost::bind(&MyShareMgrImpl::loadDataAsyc, this, boost::ref(nodeList), true, isFlush));
		int32_t timeCnt = 0;
		while(isLoading_ && timeCnt<delayLoading_)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(10));
			timeCnt += 10;
		}
		if(isLoading_)
		{
			DelayLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(), m_pShareList->GetPos(), loadThread_);
		}

		for (MyShareNodeList::iterator it = nodeList.begin(); it != nodeList.end(); ++it)
		{
			CDialogBuilder builder;
			CMyShareElementUI* node = static_cast<CMyShareElementUI*>(
				builder.Create(ControlNames::SKIN_XML_MYSHARE_SHAREITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
			if (NULL == node)
				continue;

			node->fillData(*it);
			node->initUI();
			if (!m_pShareList->Add(node))
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
		}

		m_pShareList->SetFocus();
		showButton();

		if (curCnt_ <= 0){
			if (m_pShareTab)
				m_pShareTab->SelectItem(2);
		}else{
			if (m_pShareTab)
				m_pShareTab->SelectItem(0);
		}
	}

	void MyShareMgrImpl::loadLinkList(bool isFlush /* = false */)
	{
		if (NULL == m_pLinkList)	return;
		if(m_pLinkList->GetCount()>0 && 0==curPageParam_.offset)
			m_pLinkList->RemoveAll();

		MyShareNodeList nodeList;
		isLoading_ = true;
		loadThread_ = boost::thread(boost::bind(&MyShareMgrImpl::loadDataAsyc, this, boost::ref(nodeList), false, isFlush));
		int32_t timeCnt = 0;
		while(isLoading_ && timeCnt<delayLoading_)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(10));
			timeCnt += 10;
		}
		if(isLoading_)
		{
			DelayLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(), m_pLinkTile->GetPos(), loadThread_);
		}

		for (MyShareNodeList::iterator it = nodeList.begin(); it != nodeList.end(); ++it){
			CDialogBuilder builder;
			CMyShareElementUI* node = static_cast<CMyShareElementUI*>(
				builder.Create(ControlNames::SKIN_XML_MYSHARE_LINKITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
			if (NULL == node)
				continue;

			node->fillData(*it);
			node->initUI();
			if (!m_pLinkList->Add(node))
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
		}

		m_pLinkList->SetFocus();
		showButton();

		if (curCnt_ <= 0){
			if (m_pLinkTab)
				m_pLinkTab->SelectItem(2);
		}else{
			if (m_pLinkTab)
				m_pLinkTab->SelectItem(0);
		}
	}

	void MyShareMgrImpl::loadShareTile(bool isFlush)
	{
		if (NULL == m_pShareTile)	return;
		if(m_pShareTile->GetCount()>0 && 0==curPageParam_.offset)
			m_pShareTile->RemoveAll();

		MyShareNodeList nodeList;
		isLoading_ = true;
		loadThread_ = boost::thread(boost::bind(&MyShareMgrImpl::loadDataAsyc, this, boost::ref(nodeList), true, isFlush));
		int32_t timeCnt = 0;
		while(isLoading_ && timeCnt<delayLoading_)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(10));
			timeCnt += 10;
		}
		if(isLoading_)
		{
			DelayLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(), m_pShareList->GetPos(), loadThread_);
		}

		for (MyShareNodeList::iterator it = nodeList.begin(); it != nodeList.end(); ++it)
		{
			CDialogBuilder builder;
			MyShareTileLayoutListContainerElement* node = static_cast<MyShareTileLayoutListContainerElement*>(
				builder.Create(ControlNames::SKIN_XML_MYSHARE_ICONITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
			if (NULL == node)
				continue;

			node->fillData(*it);
			node->initUI(false);
			if (!m_pShareTile->Add(node))
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
		}

		m_pShareTile->SetFocus();
		showButton();

		if (curCnt_ <= 0){
			if (m_pShareTab)
				m_pShareTab->SelectItem(2);
		}else{
			if (m_pShareTab)
				m_pShareTab->SelectItem(1);
		}
	}

	void MyShareMgrImpl::loadLinkTile(bool isFlush)
	{
		if (NULL == m_pLinkTile)	return;
		if(m_pLinkTile->GetCount()>0 && 0==curPageParam_.offset)
			m_pLinkTile->RemoveAll();

		MyShareNodeList nodeList;
		isLoading_ = true;
		loadThread_ = boost::thread(boost::bind(&MyShareMgrImpl::loadDataAsyc, this, boost::ref(nodeList), false, isFlush));
		int32_t timeCnt = 0;
		while(isLoading_ && timeCnt<delayLoading_)
		{
			boost::this_thread::sleep(boost::posix_time::milliseconds(10));
			timeCnt += 10;
		}
		if(isLoading_)
		{
			DelayLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(), m_pLinkTile->GetPos(), loadThread_);
		}

		for (MyShareNodeList::iterator it = nodeList.begin(); it != nodeList.end(); ++it)
		{
			CDialogBuilder builder;
			MyShareTileLayoutListContainerElement* node = static_cast<MyShareTileLayoutListContainerElement*>(
				builder.Create(ControlNames::SKIN_XML_MYSHARE_ICONITEM, L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
			if (NULL == node)
				continue;

			node->fillData(*it);
			node->initUI(false);
			if (!m_pLinkTile->Add(node))
				SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
		}

		m_pLinkTile->SetFocus();
		showButton();

		if (curCnt_ <= 0){
			if (m_pLinkTab)
				m_pLinkTab->SelectItem(2);
		}else{
			if (m_pLinkTab)
				m_pLinkTab->SelectItem(1);
		}
	}

	void MyShareMgrImpl::getListUIFileNode(CCustomListUI* pList)
	{
		if(NULL == pList) return;
		selectedItems_.clear();
		CStdValArray* curSelects = pList->GetSelects();
		for (int i = 0; i < curSelects->GetSize(); ++i)
		{
			if(NULL==curSelects->GetAt(i)) continue;
			CMyShareElementUI* element = static_cast<CMyShareElementUI*>(pList->GetItemAt(*(int*)curSelects->GetAt(i)));
			if(NULL==element) continue;
			MyShareNode shareInfo = element->shareInfo;
			selectedItems_.insert(std::make_pair(shareInfo.id, shareInfo));
		}
	}

	void MyShareMgrImpl::getTileUIFileNode(CTileLayoutListUI* pTile)
	{
		if(NULL == pTile) return;
		selectedItems_.clear();

		CStdValArray* curSelects = pTile->GetSelects();
		for (int i = 0; i < curSelects->GetSize(); ++i)
		{
			if(NULL==curSelects->GetAt(i)) continue;
			MyShareTileLayoutListContainerElement* element = static_cast<MyShareTileLayoutListContainerElement*>(pTile->GetItemAt(*(int*)curSelects->GetAt(i)));
			if(NULL==element) continue;
			MyShareNode shareInfo = element->shareInfo;
			selectedItems_.insert(std::make_pair(shareInfo.id, shareInfo));
		}
	}

	void MyShareMgrImpl::getUIFileNode()
	{
		if (NULL == m_pShareOrLinkTab || NULL == m_pShareTab || NULL == m_pLinkTab)	return;

		if (0 == m_pShareOrLinkTab->GetCurSel())
		{
			if (0 == m_pShareTab->GetCurSel())
				getListUIFileNode(m_pShareList);
			else
				getTileUIFileNode(m_pShareTile);
		}
		else
		{ 
			if (0 == m_pLinkTab->GetCurSel())
				getListUIFileNode(m_pLinkList);
			else
				getTileUIFileNode(m_pLinkTile);
		}
	}

	void MyShareMgrImpl::canclshareClick(TNotifyUI& msg)
	{
		if (NULL == m_noticeFrame_)return;
		m_noticeFrame_->Run(Choose,Ask,MSG_SETTING_SHARETILE_KEY,MSG_CANCLSHARECONFIRM_NOTICE_KEY,Modal);

		if (!m_noticeFrame_->IsClickOk())
		{
			return;
		}
		std::string linkCode = "";
		std::string type = "all";
		getUIFileNode();
		std::list<int64_t> selectedItems;
		for(std::map<int64_t, MyShareNode>::iterator it = selectedItems_.begin(); it != selectedItems_.end(); ++it)
		{
			if(0 == m_pShareOrLinkTab->GetCurSel())
			{
				(void)context_->getShareResMgr()->cancelShare(it->second.id);
				ProxyMgr::getInstance(context_)->deleteMyShareRes(selectedItems);
			}
			else
			{
				(void)context_->getShareResMgr()->delShareLink(it->second.id, linkCode, type);
				ProxyMgr::getInstance(context_)->deleteMyLinkRes(selectedItems);
			}
			selectedItems.push_back(it->first);
		}
		selectedItems_.clear();

		loadData(browseHistory_[curPage_], true);
	}

	void MyShareMgrImpl::setshareClick(TNotifyUI& msg)
	{
		getUIFileNode();
		if (selectedItems_.size()!= 1) return;

		UIFileNode item;
		item.basic.id = selectedItems_.begin()->second.id;
		item.basic.name = SD::Utility::String::utf8_to_wstring(selectedItems_.begin()->second.name);
		item.basic.type = selectedItems_.begin()->second.type;
		item.basic.parent = selectedItems_.begin()->second.parent;

		
		if(0 == m_pShareOrLinkTab->GetCurSel())
		{
			//共享
			Onebox::ShareFrame* myShareFrame =  new Onebox::ShareFrame(context_, item, paintManager_);
			myShareFrame->ShowSharedFrame();
			delete myShareFrame;
			myShareFrame = NULL;
		}
		else
		{
			ShareLinkCountDialog::CreateDlg(paintManager_, context_, item);
		}
	}

    void MyShareMgrImpl::searchbtnClick(TNotifyUI& msg)
    {
        CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYSHARE_SEARCHTXT));		
		if (searchtxt){
			std::wstring keyWord = searchtxt->GetText();
			boost::algorithm::trim(keyWord);
			loadData(keyWord);
		}
    }

    void MyShareMgrImpl::flushClick(TNotifyUI& msg)
    {
		loadData(browseHistory_[curPage_], true);
		if (0 == m_pShareOrLinkTab->GetCurSel())
		{
			if (0 == m_pShareTab->GetCurSel())
			{
				m_pShareList->HomeUp();
			}
			else
			{
				m_pShareTile->HomeUp();
			}
		}
		else
		{
			if (0 == m_pLinkTab->GetCurSel())
			{
				m_pLinkList->HomeUp();
			}
			else
			{
				m_pLinkTile->HomeUp();
			}
		}
    }

    void MyShareMgrImpl::backClick(TNotifyUI& msg)
    {
        if(curPage_<1)
        {
            return;
        }
        loadData(browseHistory_[--curPage_], true);
    }

    void MyShareMgrImpl::nextClick(TNotifyUI& msg)
    {
        if(curPage_+2>browseHistory_.size())
        {
            return;
        }
        loadData(browseHistory_[++curPage_], true);
    }

	void MyShareMgrImpl::showButton()
	{
		if(isNextPage_) return;

		CButtonUI* backBtn = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_BACK));
		CButtonUI* nextBtn = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_NEXT));
		bool isFirst = (curPage_==0);
		bool isLast = (curPage_+1==browseHistory_.size());
		if (backBtn)
			backBtn->SetEnabled(!isFirst);
		if (nextBtn)
			nextBtn->SetEnabled(!isLast);

		CScaleIconButtonUI* setshareBtn = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(MYSHARE_SETSHARE));
		CButtonUI* canclshareBtn = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_CANCLSHARE));
		CButtonUI* cntText = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_SHARECOUT));

		getUIFileNode();
		int32_t selectSize = selectedItems_.size();
		if (setshareBtn)
			setshareBtn->SetVisible(1==selectSize);
		if (canclshareBtn)
			canclshareBtn->SetVisible(selectSize>0);

		if(0==selectSize && curCnt_ >=0)
		{
			std::wstringstream showText;
			if (browseHistory_[curPage_].empty())
			{
				showText<< "{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(MSG_MYSHARE_SHOWBTN_START_TEXT_KEY).c_str() << "{/f}{/c}" << " {c #000000}{f 12}"
					<<curCnt_  << "{/f}{/c} " << "{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(MSG_MYSHARE_SHOWBTN_END_TEXT_KEY).c_str() << "{/f}{/c}";
			}
			else
			{
				showText << "{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(MSG_SHARE2ME_SHOWBTN_START_KEYWORD_KEY).c_str() << "{/f}{/c}" << L" {c #000000}{f 12}" 
					<< curCnt_ << L"{/f}{/c} " << "{c #666666}{f 10}" << iniLanguageHelper.GetCommonString(MSG_SHARE2ME_SHOWBTN_END_KEYWORD_KEY).c_str() << "{/f}{/c}";
			}
			if (cntText)
				cntText->SetText(showText.str().c_str());
		}
		if (cntText)
			cntText->SetVisible(0==selectSize && curCnt_ >=0);
	}

    void MyShareMgrImpl::nameHeaderClick(TNotifyUI& msg)
    {
        headerClick(MYLINK_ROW_NAME);
    }

    void MyShareMgrImpl::pathHeaderClick(TNotifyUI& msg)
    {
        headerClick(MYLINK_ROW_PATH);
    }

	void MyShareMgrImpl::sizeHeaderClick(TNotifyUI& msg)
	{
		headerClick(MYLINK_ROW_SIZE);
	}

	void MyShareMgrImpl::linkHeaderClick(TNotifyUI& msg)
	{
		headerClick(MYLINK_ROW_LINKCOUNT);
	}

	void MyShareMgrImpl::initHeader()
	{
		OrderParam orderParam;
		orderParam.field = MYLINK_ROW_NAME;
		orderParam.direction = "asc";
		curPageParam_.orderList.push_back(orderParam);
		orderParam.field = MYLINK_ROW_LINKCOUNT;
		orderParam.direction = "asc";
		curPageParam_.orderList.push_back(orderParam);
		orderParam.field = MYLINK_ROW_SIZE;
		orderParam.direction = "asc";
		curPageParam_.orderList.push_back(orderParam);
		orderParam.field = MYLINK_ROW_PATH;
		orderParam.direction = "asc";
		curPageParam_.orderList.push_back(orderParam);

		CButtonUI* pBtnName = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myShare_listHeaderItemNameSortIcon")));
		CButtonUI* pBtnShareType = static_cast<CButtonUI*>(paintManager_.FindControl(_T("myShare_listHeaderItemTypeSortIcon")));
		if(NULL == pBtnName || NULL == pBtnShareType) return;
		pBtnName->SetVisible(false);
		pBtnShareType->SetNormalImage(_T("..\\img\\list_icon_ase.png"));
		pBtnShareType->SetVisible(true);
	}

    void MyShareMgrImpl::headerClick(const std::string& name)
    {
		CControlUI* pParent = NULL;
		if (0 == m_pShareOrLinkTab->GetCurSel())
			pParent = m_pShareTab;
		else
			pParent = m_pLinkTab;

        CButtonUI* pBtnName = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(pParent, _T("myShare_listHeaderItemNameSortIcon")));
		CButtonUI* pBtnPath = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(pParent, _T("myShare_listHeaderItemPathSortIcon")));
		CButtonUI* pBtnSize = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(pParent, _T("myShare_listHeaderItemSizeSortIcon")));
        CButtonUI* pBtnLink = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(pParent, _T("myShare_listHeaderItemLinkSortIcon")));
		if(NULL == pBtnName || NULL == pBtnPath || NULL == pBtnSize ) return;

        CButtonUI* pControl = NULL;
        if (MYLINK_ROW_NAME == name)
            pControl = pBtnName;
        else if(MYLINK_ROW_PATH == name)
            pControl = pBtnPath;
        else if (MYLINK_ROW_SIZE == name)
			pControl = pBtnSize;
		else if (MYLINK_ROW_LINKCOUNT == name)
			pControl = pBtnLink;
		else return;
		if (NULL == pControl)	return;


		if (pParent == m_pShareTab ){
			if (curPageParam_.orderList.size() == 4)
			{
				for(ParamOrderList::iterator it = curPageParam_.orderList.begin(); it!=curPageParam_.orderList.end(); ++it)
				{
					if(MYLINK_ROW_LINKCOUNT==it->field)
					{
						curPageParam_.orderList.erase(it);
						break;
					}
				}
			}			
		}else if (curPageParam_.orderList.size() == 3)
		{
			OrderParam orderParam;
			orderParam.field = MYLINK_ROW_LINKCOUNT;
			orderParam.direction = "asc";
			curPageParam_.orderList.push_back(orderParam);
		}

		OrderParam orderParam;
		for(ParamOrderList::iterator it = curPageParam_.orderList.begin(); it!=curPageParam_.orderList.end(); ++it)
		{
			if(name==it->field)
			{
				orderParam.field = name;
				orderParam.direction = ("asc"==it->direction)?"desc":"asc";
				curPageParam_.orderList.erase(it);
				curPageParam_.orderList.push_front(orderParam);
				break;
			}
		}

        if ("desc"==orderParam.direction)
        {
			pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,0,6,6' dest='0,6,6,12'"));	//_T("..\\img\\list_icon_desc.png"));
			pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowdown.png' source='0,16,6,22' dest='0,6,6,12'"));
        } 
        else
        {
            pControl->SetNormalImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,0,6,6' dest='0,6,6,12'"));		//_T("..\\img\\list_icon_ase.png"));
			pControl->SetHotImage(_T("file='..\\Image\\ic_tab_head_arrowup.png' source='0,16,6,22' dest='0,6,6,12'"));
        }

        pBtnName->SetVisible(MYLINK_ROW_NAME == name);
		pBtnPath->SetVisible(MYLINK_ROW_PATH == name);
		pBtnSize->SetVisible(MYLINK_ROW_SIZE == name);
		if (pBtnLink)
			pBtnLink->SetVisible(MYLINK_ROW_LINKCOUNT == name);

        loadData(browseHistory_[curPage_], false);
    }

	void MyShareMgrImpl::selectAllItemClick(TNotifyUI& msg)
	{
		if (0 == m_pShareOrLinkTab->GetCurSel())
		{
			if (0 == m_pShareTab->GetCurSel())
				m_pShareList->SelectAllItem(true);
			else
				m_pShareTile->SelectAllItem(true);
		}
		else
		{
			if (0 == m_pLinkTab->GetCurSel())
				m_pLinkList->SelectAllItem(true);
			else
				m_pLinkTile->SelectAllItem(true);
		}

		msg.wParam = true;
		selectAllClick(msg);
	}

	void MyShareMgrImpl::nextPage(TNotifyUI& msg)
	{
		int32_t range = msg.wParam;
		int32_t pos = msg.lParam;
		//无下一页，或未滚到倒数SCROLL_LIMEN条以下，不处理
		if((curPageParam_.offset + curPageParam_.limit >= curCnt_) 
			|| (pos < range - MYSHARE_SCROLL_LIMEN)) return;

		curPageParam_.offset += pageLimit_;
		isNextPage_ = true;
		loadData(browseHistory_[curPage_], false);
	}

	void MyShareMgrImpl::selectAllClick(TNotifyUI& msg)
	{
		selectedItems_.clear();
		if(msg.wParam==0) return;

		//选中全页对象
		MyShareNodeList shareNodes;
		int64_t count = 0;
		PageParam pageParam;
		pageParam.offset = 0;
		pageParam.limit = 0;

		(void)ProxyMgr::getInstance(context_)->listMyShareRes(SD::Utility::String::wstring_to_utf8(browseHistory_[curPage_]),
			shareNodes, pageParam, count, false);

		for (MyShareNodeList::iterator it = shareNodes.begin(); it != shareNodes.end(); ++it)
		{
			selectedItems_.insert(std::make_pair(it->id, *it));
		}
	}

	void MyShareMgrImpl::pathItemClick(TNotifyUI& msg)
	{
		CButtonUI* pBtn = static_cast<CButtonUI*>(msg.pSender);
		if (NULL == pBtn)	return;

		CMyShareElementUI* pElement = reinterpret_cast<CMyShareElementUI*>(pBtn->GetTag());
		if(NULL == pElement) return;
		context_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE,
			SD::Utility::String::type_to_string<std::wstring>(pElement->shareInfo.parent),
			SD::Utility::String::type_to_string<std::wstring>(pElement->shareInfo.id)));
	}

	void MyShareMgrImpl::shareLinkClick(TNotifyUI& msg)
	{
		CButtonUI* pBtn = static_cast<CButtonUI*>(msg.pSender);
		if (NULL == pBtn)	return;

		CMyShareElementUI* pElement = reinterpret_cast<CMyShareElementUI*>(pBtn->GetTag());
		if (NULL == pElement)	return;

		ShareLinkCountDialog::CreateDlg(paintManager_, context_, pElement->nodeData, false);
	}

	void MyShareMgrImpl::itemDbClick(TNotifyUI& msg)
	{
		CMyShareElementUI* pElement = static_cast<CMyShareElementUI*>(msg.pSender);
		if(NULL == pElement) return;
		if(FILE_TYPE_FILE==pElement->shareInfo.type) 
		context_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE,
			SD::Utility::String::type_to_string<std::wstring>(pElement->shareInfo.parent),
			SD::Utility::String::type_to_string<std::wstring>(pElement->shareInfo.id)));
		else if (FILE_TYPE_DIR == pElement->shareInfo.type)
			context_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE,
			SD::Utility::String::type_to_string<std::wstring>(pElement->shareInfo.id)));
	}

	void MyShareMgrImpl::iconItemDbClick(TNotifyUI& msg)
	{
		CMyShareElementUI* pElement = static_cast<CMyShareElementUI*>(msg.pSender);
		if(NULL == pElement) return;
		context_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE,
			SD::Utility::String::type_to_string<std::wstring>(pElement->shareInfo.parent),
			SD::Utility::String::type_to_string<std::wstring>(pElement->shareInfo.id)));
	}

	void MyShareMgrImpl::sharedClick(TNotifyUI& msg)
	{
		if (!m_pShareOrLinkTab) return;

		CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYSHARE_SEARCHTXT));
		if (searchtxt)
		{
			m_strInputLinkKey = searchtxt->GetText();
			searchtxt->resetText(m_strInputShareKey);
			if (m_pBtnClearSearch)
			{
				if (m_strInputShareKey.empty())
					m_pBtnClearSearch->SetVisible(false);
				else
					m_pBtnClearSearch->SetVisible(true);
			}				
		}

		m_pShareOrLinkTab->SelectItem(0);
		if(NULL==m_pListOpt||NULL==m_pTileOpt) return;
		if (m_bShareList)
		{
			m_pListOpt->Selected(true);
		}
		else
		{
			m_pTileOpt->Selected(true);
		}

		CButtonUI* cntText = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_SHARECOUT));
		CScaleIconButtonUI* setshareBtn = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(MYSHARE_SETSHARE));
		CButtonUI* canclshareBtn = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_CANCLSHARE));
		if(cntText)
		{
			cntText->SetVisible(false);
		}
		if(setshareBtn)
		{
			setshareBtn->SetVisible(false);
		}
		if(canclshareBtn)
		{
			canclshareBtn->SetVisible(false);
		}

	 	loadData(m_strShareKey, false);
	}

	void MyShareMgrImpl::linkedClick(TNotifyUI& msg)
	{
		if (!m_pShareOrLinkTab) return;

		CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYSHARE_SEARCHTXT));
		if (searchtxt)
		{
			m_strInputShareKey = searchtxt->GetText();
			searchtxt->resetText(m_strInputLinkKey);
			if (m_pBtnClearSearch)
			{
				if (m_strInputLinkKey.empty())
					m_pBtnClearSearch->SetVisible(false);
				else
					m_pBtnClearSearch->SetVisible(true);
			}
		}

		m_pShareOrLinkTab->SelectItem(1);
		if(NULL==m_pListOpt||NULL==m_pTileOpt) return;
		if (m_bLinkList)
		{
			m_pListOpt->Selected(true);
		}
		else
		{
			m_pTileOpt->Selected(true);
		}

		CButtonUI* cntText = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_SHARECOUT));
		CScaleIconButtonUI* setshareBtn = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(MYSHARE_SETSHARE));
		CButtonUI* canclshareBtn = static_cast<CButtonUI*>(paintManager_.FindControl(MYSHARE_CANCLSHARE));
		if(cntText)
		{
			cntText->SetVisible(false);
		}
		if(setshareBtn)
		{
			setshareBtn->SetVisible(false);
		}
		if(canclshareBtn)
		{
			canclshareBtn->SetVisible(false);
		}
		if(ProxyMgr::getInstance(context_)->isMyShareLinkCacheExist())
		{
			loadData(m_strLinkKey, false);
		}
		else
		{
			loadData(m_strLinkKey, true);
		}
		
	}

	void MyShareMgrImpl::listSelectchanged(TNotifyUI& msg)
	{
		if (m_pShareOrLinkTab && m_pShareTab && m_pLinkTab)
		{
			if (0 == m_pShareOrLinkTab->GetCurSel()){
				m_pShareTab->SelectItem(0);
				m_bShareList = true;
				loadData(m_strShareKey, false);
			}
			else{
				m_pLinkTab->SelectItem(0);	
				m_bLinkList = true;
				loadData(m_strLinkKey, false);
			}
		}
	}

	void MyShareMgrImpl::tabloidSelectchanged(TNotifyUI& msg)
	{
		if (m_pShareOrLinkTab && m_pShareTab && m_pLinkTab)
		{
			if (0 == m_pShareOrLinkTab->GetCurSel()){
				m_pShareTab->SelectItem(1);
				m_bShareList = false;
				loadData(m_strShareKey, false);
			}
			else{
				m_pLinkTab->SelectItem(1);
				m_bLinkList = false;
				loadData(m_strLinkKey, false);
			}
		}
	}

	void MyShareMgrImpl::menuAdded(TNotifyUI& msg)
	{
		if (NULL == msg.pSender)
		{
			return;
		}

		getUIFileNode();			

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
			CMenuItemUI* pMenuItem = NULL;
			// the max menu id is 1
			// only enable: 
			// Cancel Shares (id = 0)
			for (int i = 0; i <= 1; ++i)
			{
				if (i == 0)
				{
					continue;
				}
				pMenuItem = pMenu->GetMenuItemById(i);
				if (NULL == pMenuItem) continue;
				pMenuItem->SetVisible(false);
			}
		}
	}

	void MyShareMgrImpl::menuItemClick(TNotifyUI& msg)
	{
		int menuId = msg.lParam;
		if (menuId < 0)
		{
			return;
		}
		switch (menuId)
		{
			// cancel shares
		case 0:
			canclshareClick(msg);			
			break;
			// view shares
		case 1:
			setshareClick(msg);
			break;
		default:
			break;
		}
	}

	void MyShareMgrImpl::clearSearchBtnClick(TNotifyUI& msg)
	{
		CSearchTxtUI* searchtxt = static_cast<CSearchTxtUI*>(paintManager_.FindControl(MYSHARE_SEARCHTXT));
		if (searchtxt)
			searchtxt->resetText(L"");
		if (m_pBtnClearSearch)
			m_pBtnClearSearch->SetVisible(false);
		loadData(L"", false);

		if (searchtxt)
			searchtxt->SetFocus();
	}

	void MyShareMgrImpl::searchTextChanged(TNotifyUI& msg)
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

	void MyShareMgrImpl::doMenuPoint(CMenuUI* pMenu, POINT pt)
	{
		if (NULL == m_pShareOrLinkTab || NULL == m_pShareTab || NULL == m_pLinkTab || NULL == pMenu)	return;
		if (selectedItems_.size() <= 0)	return;
		pMenu->SetVisible(false);

		if (0 == m_pShareOrLinkTab->GetCurSel())
		{
			if (0 == m_pShareTab->GetCurSel())
			{
				CStdValArray* curSelects = m_pShareList->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CMyShareElementUI* element = static_cast<CMyShareElementUI*>(m_pShareList->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;

					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
			else
			{
				CStdValArray* curSelects = m_pShareTile->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					MyShareTileLayoutListContainerElement* element = static_cast<MyShareTileLayoutListContainerElement*>(m_pShareTile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;
					
					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
		}
		else
		{ 
			if (0 == m_pLinkTab->GetCurSel())
			{
				CStdValArray* curSelects = m_pLinkList->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					CMyShareElementUI* element = static_cast<CMyShareElementUI*>(m_pLinkList->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;

					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
			else
			{
				CStdValArray* curSelects = m_pLinkTile->GetSelects();
				for (int i = 0; i < curSelects->GetSize(); ++i)
				{
					if(NULL==curSelects->GetAt(i)) continue;
					MyShareTileLayoutListContainerElement* element = static_cast<MyShareTileLayoutListContainerElement*>(m_pLinkTile->GetItemAt(*(int*)curSelects->GetAt(i)));
					if(NULL==element) continue;

					RECT rt = element->GetPos();
					if (PtInRect(&rt, pt)){
						pMenu->SetVisible(true);
						break;
					}
				}
			}
		}
	}

    MyShareMgr* MyShareMgr::instance_ = NULL;

    MyShareMgr* MyShareMgr::getInstance(UserContext* context, CPaintManagerUI& paintManager)
    {
        if (NULL == instance_)
        {
            instance_ = new MyShareMgrImpl(context, paintManager);
        }
        return instance_;
    }
}
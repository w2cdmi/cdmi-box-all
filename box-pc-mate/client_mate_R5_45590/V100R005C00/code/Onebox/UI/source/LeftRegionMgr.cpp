#include "stdafxOnebox.h"
#include "LeftRegionMgr.h"
#include "Configure.h"
#include "Utility.h"
#include "Share2MeMgr.h"
#include "MyShareMgr.h"
#include "MyFileMgr.h"
#include "UserInfo.h"
#include "UserInfoMgr.h"
#include "TransTaskMgr.h"
#include "DialogBuilderCallbackImpl.h"
#include "TeamSpaceMgr.h"
#include "ConfigureMgr.h"
#include "UICommonDefine.h"
#include "NotifyMgr.h"
#include <boost/thread.hpp>
#include "FullBackUpMgr.h"
namespace Onebox
{
	struct ReloadKeyInfo
	{
		CurPageType pageType;
		int64_t ownerId;
		int64_t dirId;
		int64_t time;
		
		ReloadKeyInfo():pageType(Page_MyFile), ownerId(-1), dirId(-1), time(0)
		{
		}
	};

	class LeftRegionMgrImpl : public LeftRegionMgr
	{
	public:
		LeftRegionMgrImpl(UserContext* context, CPaintManagerUI& paintManager);

		~LeftRegionMgrImpl()
		{
			if(isThreadAlive_)
			{
				reloadThread_.interrupt();
				reloadThread_.join();
			}
		}

		virtual void initData();

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg);

		virtual CurPageType getCurPageType();

		virtual void setCurPageType(CurPageType type);

		virtual void addReloadInfo(int64_t ownerId, int64_t dirId);

		virtual void addReloadInfo(UserContextType userType, int64_t ownerId, int64_t dirId);

		virtual void reloadThumb(const std::string& thumbKey)
		{
			if(browseHistory_.end()!=browseHistory_.find(Page_MyFile))
			{
				MyFileMgr* myFileMgr = MyFileMgr::getInstance(userContext_,paintManager_);
				if(myFileMgr->reloadThumb(thumbKey))
				{
					return;
				}
			}

			if(browseHistory_.end()!=browseHistory_.find(Page_Share2Me))
			{
				Share2MeMgr* share2MeMgr = Share2MeMgr::getInstance(userContext_, paintManager_);
				if(NULL != share2MeMgr && share2MeMgr->reloadThumb(thumbKey))
				{
					return;
				}
			}

			if(browseHistory_.end()!=browseHistory_.find(Page_MyShare))
			{
				MyShareMgr* myShareMgr = MyShareMgr::getInstance(userContext_, paintManager_);
				if(NULL != myShareMgr && myShareMgr->reloadThumb(thumbKey))
				{
					return;
				}
			}

			if(browseHistory_.end()!=browseHistory_.find(Page_TeamSpace))
			{
				TeamSpaceMgr* teamsapceMgr = TeamSpaceMgr::getInstance(userContext_, paintManager_);
				if(teamsapceMgr->reloadThumb(thumbKey))
				{
					return;
				}
			}
		}

		virtual bool isInHistory(CurPageType type);

		virtual void setSkipReload(bool isSkip)
		{
			isSkip_ = isSkip;
		}

		virtual bool isSkipReload()
		{
			return isSkip_;
		}

	private:
		void myFileSelectchanged(TNotifyUI& msg);

		void share2MeSelectchanged(TNotifyUI& msg);

		void myShareSelectchanged(TNotifyUI& msg);

		void teamSpaceSelectchanged(TNotifyUI& msg);

		void transfersListSelectchanged(TNotifyUI& msg);
		
		void backupSelectchanged(TNotifyUI& msg);
		
		void otherSelectchanged(TNotifyUI& msg);

		void userRegionClick(TNotifyUI& msg);

		void reloadAsync();

	private:
		UserContext* userContext_;
		CPaintManagerUI& paintManager_;
		IDialogBuilderCallback* callback_;
		CurPageType curPageType_;
		std::set<CurPageType> browseHistory_;
		std::set<CurPageType> needReloadPage_;

		std::map<std::wstring, call_func> funcMaps_;

		boost::mutex mutex_;
		boost::thread reloadThread_;
		std::list<ReloadKeyInfo> reloadKeyInfos_;
		bool isThreadAlive_;
		bool isSkip_;
	};

    LeftRegionMgr* LeftRegionMgr::instance_ = NULL;

    LeftRegionMgr* LeftRegionMgr::getInstance(UserContext* context, CPaintManagerUI& paintManager)
    {
        if (NULL == instance_)
        {
            instance_ = new LeftRegionMgrImpl(context, paintManager);
        }
        return instance_;
    }

	LeftRegionMgrImpl::LeftRegionMgrImpl(UserContext* context, CPaintManagerUI& paintManager)
		:userContext_(context)
		,paintManager_(paintManager)
		,curPageType_(Page_MyFile)
		,isThreadAlive_(false)
		,isSkip_(false)
	{
		funcMaps_.insert(std::make_pair(L"myFile_selectchanged", boost::bind(&LeftRegionMgrImpl::myFileSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"share2Me_selectchanged", boost::bind(&LeftRegionMgrImpl::share2MeSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"myShare_selectchanged", boost::bind(&LeftRegionMgrImpl::myShareSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"teamSpace_selectchanged", boost::bind(&LeftRegionMgrImpl::teamSpaceSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"transfersList_selectchanged", boost::bind(&LeftRegionMgrImpl::transfersListSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"backup_selectchanged", boost::bind(&LeftRegionMgrImpl::backupSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"other_selectchanged", boost::bind(&LeftRegionMgrImpl::otherSelectchanged, this, _1)));
		funcMaps_.insert(std::make_pair(L"userRegion_click", boost::bind(&LeftRegionMgrImpl::userRegionClick, this, _1)));
	}

	void LeftRegionMgrImpl::initData()
	{
		//setUsedQuota();
		TNotifyUI msg;
		myFileSelectchanged(msg);
	}

	void LeftRegionMgrImpl::executeFunc(const std::wstring& funcName, TNotifyUI& msg)
	{
		std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
		if(it!=funcMaps_.end())
		{
			it->second(msg);
		}
	}

	void LeftRegionMgrImpl::myFileSelectchanged(TNotifyUI& msg)
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(paintManager_.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMsg->SetVisible(false);
		pMain->SetVisible();

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
		pControl->SelectItem(0);

		curPageType_ = Page_MyFile;
		MyFileMgr* myFileMgr = MyFileMgr::getInstance(userContext_,paintManager_);
		if(browseHistory_.end()==browseHistory_.find(curPageType_))
		{
			myFileMgr->initData();
			browseHistory_.insert(curPageType_);
		}
		else
		{
			myFileMgr->setPageFocus();
			if(needReloadPage_.end()!=needReloadPage_.find(curPageType_))
			{
				myFileMgr->reloadCache();
				needReloadPage_.erase(curPageType_);
			}
		}
	}

	void LeftRegionMgrImpl::share2MeSelectchanged(TNotifyUI& msg)
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(paintManager_.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMsg->SetVisible(false);
		pMain->SetVisible();

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
		pControl->SelectItem(1);

//		CControlUI* pShareMyTip = static_cast<CControlUI*>(paintManager_.FindControl(_T("ShareMyTip")));
		
		curPageType_ = Page_Share2Me;
		Share2MeMgr* share2MeMgr = Share2MeMgr::getInstance(userContext_, paintManager_);
		if(browseHistory_.end()==browseHistory_.find(curPageType_))
		{
			share2MeMgr->initData();
			browseHistory_.insert(curPageType_);
		}
// 		else if(pShareMyTip->IsVisible())
// 		{
// 			share2MeMgr->reloadCache(userContext_->getUserInfoMgr()->getUserId(), 0);
// 		}
		else
		{
			share2MeMgr->setPageFocus();
			if(needReloadPage_.end()!=needReloadPage_.find(curPageType_))
			{
				share2MeMgr->reloadCache();
				needReloadPage_.erase(curPageType_);
			}
		}
//		pShareMyTip->SetVisible(false);
	}

	void LeftRegionMgrImpl::myShareSelectchanged(TNotifyUI& msg)
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(paintManager_.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMsg->SetVisible(false);
		pMain->SetVisible();

        CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
        pControl->SelectItem(2);

		curPageType_ = Page_MyShare;
		MyShareMgr* myShareMgr = MyShareMgr::getInstance(userContext_, paintManager_);
		if(browseHistory_.end()==browseHistory_.find(curPageType_))
		{
			myShareMgr->initData();
			browseHistory_.insert(curPageType_);
		}
		else
		{
			myShareMgr->reloadCache();
		}
	}

	void LeftRegionMgrImpl::teamSpaceSelectchanged(TNotifyUI& msg)
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(paintManager_.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMsg->SetVisible(false);
		pMain->SetVisible();

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
		pControl->SelectItem(3);

		curPageType_ = Page_TeamSpace;
		TeamSpaceMgr* teamsapceMgr = TeamSpaceMgr::getInstance(userContext_, paintManager_);
		if(browseHistory_.end()==browseHistory_.find(curPageType_))
		{
			teamsapceMgr->initData();
			browseHistory_.insert(curPageType_);
		}
		else
		{
			teamsapceMgr->setPageFocus();
			if(needReloadPage_.end()!=needReloadPage_.find(curPageType_))
			{
				teamsapceMgr->reloadCache();
				needReloadPage_.erase(curPageType_);
			}
		}
	}

	void LeftRegionMgrImpl::transfersListSelectchanged(TNotifyUI& msg)
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(paintManager_.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMsg->SetVisible(false);
		pMain->SetVisible();

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
		pControl->SelectItem(4);

		/*CControlUI* pTransfersListTip = static_cast<CControlUI*>(paintManager_.FindControl(_T("TransfersListTip")));
		pTransfersListTip->SetVisible(false);*/

		curPageType_ = Page_Transfers;
		//if(browseHistory_.end()==browseHistory_.find(curPageType_))
		{
			TransTaskMgr::create(userContext_, paintManager_)->initData();
			browseHistory_.insert(curPageType_);
		}
	}

	void LeftRegionMgrImpl::backupSelectchanged(TNotifyUI& msg)
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(paintManager_.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMsg->SetVisible(false);
		pMain->SetVisible();

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
		pControl->SelectItem(5);

		curPageType_ = Page_Backup;
		if(browseHistory_.end()==browseHistory_.find(curPageType_))
		{
			//BackUpMgr* backUpMgr = BackUpMgr::getInstance(userContext_, paintManager_);
			//backUpMgr->initData();
			FullBackUpMgr* fullBackUpMgr = FullBackUpMgr::getInstance(userContext_, paintManager_);
			fullBackUpMgr->initData();
			browseHistory_.insert(curPageType_);
		}
	}

	void LeftRegionMgrImpl::otherSelectchanged(TNotifyUI& msg)
	{
		CHorizontalLayoutUI* pMain = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"mainFrame"));
		CChildLayoutUI* pMsg = static_cast<CChildLayoutUI*>(paintManager_.FindControl(L"msgFrame"));
		if (NULL == pMain || NULL == pMsg ) return;
		pMsg->SetVisible(false);
		pMain->SetVisible();

		CTabLayoutUI* pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
		pControl->SelectItem(6);
		curPageType_ = Page_Other;
		if(browseHistory_.end()==browseHistory_.find(curPageType_))
		{
			browseHistory_.insert(curPageType_);
		}
	}

	void LeftRegionMgrImpl::userRegionClick(TNotifyUI& msg)
	{
		//::MessageBox(NULL, L"Regionclick", L"LeftRegionMgrImpl", MB_OK);
		std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
		Configure* config =  configureMgr->getConfigure();				
		std::wstring  str_server = config->serverUrl();
		str_server = str_server.substr(0,str_server.length()-6) + DEFAULT_USER_SETTINGS_WEB;
		(void)ShellExecute(NULL,L"open",L"explorer.exe",str_server.c_str(),NULL,SW_SHOWNORMAL);
	}

	CurPageType LeftRegionMgrImpl::getCurPageType()
	{
		return curPageType_;
	}

	void LeftRegionMgrImpl::setCurPageType(CurPageType type)
	{
		TNotifyUI msg;
		COptionUI* pSelect = NULL;
		switch(type)
		{
		case Page_MyFile:
			myFileSelectchanged(msg);
			pSelect = static_cast<COptionUI*>(paintManager_.FindControl(L"leftRegion_myFile"));
			break;
		case Page_Share2Me:
			share2MeSelectchanged(msg);
			pSelect = static_cast<COptionUI*>(paintManager_.FindControl(L"leftRegion_share2Me"));
			break;
		//case Page_MyShare:
		//	myShareSelectchanged(msg);
		//	pSelect = static_cast<COptionUI*>(paintManager_.FindControl(L"leftRegion_myShare"));
		//	break;
		case Page_TeamSpace:
			teamSpaceSelectchanged(msg);
			pSelect = static_cast<COptionUI*>(paintManager_.FindControl(L"leftRegion_teamSpace"));
			break;
		//case Page_Transfers:
		//	transfersListSelectchanged(msg);
		//	pSelect = static_cast<COptionUI*>(paintManager_.FindControl(L"leftRegion_transfersList"));
		//	break;
		//case Page_Backup:
		//	backupSelectchanged(msg);
		//	pSelect = static_cast<COptionUI*>(paintManager_.FindControl(L"leftRegion_backup"));
		//	break;
		default:
			break;
		}
		if (pSelect)
			pSelect->Selected(true);
	}

	void LeftRegionMgrImpl::addReloadInfo(int64_t ownerId, int64_t dirId)
	{
		if(browseHistory_.end()!=browseHistory_.find(Page_MyFile))
		{
			MyFileMgr* myFileMgr = MyFileMgr::getInstance(userContext_,paintManager_);
			myFileMgr->reloadAllCache(ownerId, dirId);
		}

		if(browseHistory_.end()!=browseHistory_.find(Page_Share2Me))
		{
			Share2MeMgr* share2MeMgr = Share2MeMgr::getInstance(userContext_, paintManager_);
			share2MeMgr->reloadCache(ownerId, dirId);
		}

		if(browseHistory_.end()!=browseHistory_.find(Page_TeamSpace))
		{
			TeamSpaceMgr* teamsapceMgr = TeamSpaceMgr::getInstance(userContext_, paintManager_);
			teamsapceMgr->reloadAllCache(ownerId, dirId);
		}
	}

	void LeftRegionMgrImpl::addReloadInfo(UserContextType userType, int64_t ownerId, int64_t dirId)
	{
		if(isSkip_) return;
		CurPageType pageType = Page_MyFile;
		if(userType==UserContext_Teamspace)
		{
			pageType = Page_TeamSpace;
		}
		else if(userType==UserContext_ShareUser)
		{
			pageType = Page_Share2Me;
		}
		if(browseHistory_.end()==browseHistory_.find(pageType)) return;
		ReloadKeyInfo reloadKeyInfo;
		reloadKeyInfo.pageType = pageType;
		reloadKeyInfo.ownerId = ownerId;
		reloadKeyInfo.dirId = dirId;
		reloadKeyInfo.time = GetTickCount();
		{
			boost::mutex::scoped_lock lock(mutex_);
			reloadKeyInfos_.push_back(reloadKeyInfo);
		}
		if(!isThreadAlive_)
		{
			isThreadAlive_ = true;
			reloadThread_ = boost::thread(boost::bind(&LeftRegionMgrImpl::reloadAsync, this));
		}
	}

	void LeftRegionMgrImpl::reloadAsync()
	{
		std::list<ReloadKeyInfo> workInfo;

		ReloadKeyInfo lastKeyInfo;
		lastKeyInfo.pageType = Page_Other;
		int64_t waitTime = 0;
		bool lastIsDone = false;
		while(true)
		{
			{
				boost::mutex::scoped_lock lock(mutex_);
				workInfo.swap(reloadKeyInfos_);
			}

			for(std::list<ReloadKeyInfo>::iterator it = workInfo.begin(); it!= workInfo.end(); ++it)
			{
				if(it->pageType==lastKeyInfo.pageType
					&&it->ownerId==lastKeyInfo.ownerId
					&&it->dirId==lastKeyInfo.dirId)
				{
					//每条消息延长3秒等待时间，最长等待30秒
					if(waitTime < 30000)
					{
						waitTime += 3000;
					}
				}
				else
				{
					//消息变更时，立即刷上一页面消息。
					if(!lastIsDone)
					{
						if(curPageType_==lastKeyInfo.pageType)
						{
							userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_FS_DIR_CHANGE,
								SD::Utility::String::type_to_string<std::wstring>(lastKeyInfo.pageType),
								SD::Utility::String::type_to_string<std::wstring>(lastKeyInfo.ownerId),
								SD::Utility::String::type_to_string<std::wstring>(lastKeyInfo.dirId)));
						}
						else if(needReloadPage_.end()==needReloadPage_.find(lastKeyInfo.pageType))
						{
							switch(lastKeyInfo.pageType)
							{
							case Page_MyFile:
								if(MyFileMgr::getInstance(userContext_,paintManager_)->isTheCurPage(lastKeyInfo.ownerId, lastKeyInfo.dirId))
								{
									needReloadPage_.insert(lastKeyInfo.pageType);
								}
								break;
							case Page_Share2Me:
								if(Share2MeMgr::getInstance(userContext_,paintManager_)->isTheCurPage(lastKeyInfo.ownerId, lastKeyInfo.dirId))
								{
									needReloadPage_.insert(lastKeyInfo.pageType);
								}
								break;
							case Page_TeamSpace:
								if(TeamSpaceMgr::getInstance(userContext_,paintManager_)->isTheCurPage(lastKeyInfo.ownerId, lastKeyInfo.dirId))
								{
									needReloadPage_.insert(lastKeyInfo.pageType);
								}
								break;
							default:
								break;
							}
						}
					}
					lastKeyInfo = *it;
				}
				lastIsDone = false;
			}
			workInfo.clear();

			if(!lastIsDone && (GetTickCount()-lastKeyInfo.time)>waitTime)
			{
				waitTime = 0;
				lastIsDone = true;
				if(curPageType_==lastKeyInfo.pageType)
				{
					userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_FS_DIR_CHANGE,
						SD::Utility::String::type_to_string<std::wstring>(lastKeyInfo.pageType),
						SD::Utility::String::type_to_string<std::wstring>(lastKeyInfo.ownerId),
						SD::Utility::String::type_to_string<std::wstring>(lastKeyInfo.dirId)));
				}
				else if(needReloadPage_.end()==needReloadPage_.find(lastKeyInfo.pageType))
				{
					switch(lastKeyInfo.pageType)
					{
					case Page_MyFile:
						if(MyFileMgr::getInstance(userContext_,paintManager_)->isTheCurPage(lastKeyInfo.ownerId, lastKeyInfo.dirId))
						{
							needReloadPage_.insert(lastKeyInfo.pageType);
						}
						break;
					case Page_Share2Me:
						if(Share2MeMgr::getInstance(userContext_,paintManager_)->isTheCurPage(lastKeyInfo.ownerId, lastKeyInfo.dirId))
						{
							needReloadPage_.insert(lastKeyInfo.pageType);
						}
						break;
					case Page_TeamSpace:
						if(TeamSpaceMgr::getInstance(userContext_,paintManager_)->isTheCurPage(lastKeyInfo.ownerId, lastKeyInfo.dirId))
						{
							needReloadPage_.insert(lastKeyInfo.pageType);
						}
						break;
					default:
						break;
					}
				}
			}
			boost::this_thread::sleep(boost::posix_time::milliseconds(500));
		}
		isThreadAlive_ = false;
	}

	bool LeftRegionMgrImpl::isInHistory(CurPageType type)
	{
		return (browseHistory_.end()!=browseHistory_.find(type));
	}
}


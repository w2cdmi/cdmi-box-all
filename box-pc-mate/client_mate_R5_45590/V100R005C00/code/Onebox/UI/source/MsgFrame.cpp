#include "stdafxOnebox.h"
#include "MsgFrame.h"
#include "PageParam.h"
#include "Configure.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "ChildLayout.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "PathMgr.h"
#include "ListContainerElement.h"
#include "DialogBuilderCallbackImpl.h"
#include "SearchTxt.h"
#include "CustomListUI.h"
#include "InIHelper.h"
#include "InILanguage.h"
#include "SimpleNoticeFrame.h"
#include "ProxyMgr.h"
#include "UploadFrame.h"
#include "TransTaskMgr.h"
#include "NoticeFrame.h"
#include "ConfigureMgr.h"
#include "MsgElement.h"
#include <boost/thread.hpp>
#include "NotifyMgr.h"
#include "ShellCommonFileDialog.h"
#include "TeamSpaceResMgr.h"
#include "CommonFileDialog.h"
#include "CommonFileDialogRemoteNotify.h"
#include "RestTaskMgr.h"
#include "ErrorConfMgr.h"

using namespace SD::Utility;

#ifndef MODULE_NAME
#define MODULE_NAME ("MsgFrame")
#endif
namespace Onebox
{
	#define MSG_SCROLL_LIMEN (100)
	#define MSG_ASYNC_LIMEN (50)	//启动异步处理的门限值

	class MsgFrameImpl : public MsgFrame
	{
	public:
		MsgFrameImpl(UserContext* context, CPaintManagerUI& paintManager):userContext_(context), paintManager_(paintManager)
		{
			m_msgList = NULL;
			downloadFlag_ = false;

			m_noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());
			funcMaps_.insert(std::make_pair(L"refresh_click", boost::bind(&MsgFrameImpl::refreshClick, this)));
			funcMaps_.insert(std::make_pair(L"download_click", boost::bind(&MsgFrameImpl::downloadClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"savetomyfile_click", boost::bind(&MsgFrameImpl::saveClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"delete_click", boost::bind(&MsgFrameImpl::deleteClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"view_click", boost::bind(&MsgFrameImpl::viewClick, this,_1)));		
			funcMaps_.insert(std::make_pair(L"listView_nextPage", boost::bind(&MsgFrameImpl::nextPage, this,_1)));
			funcMaps_.insert(std::make_pair(L"more_click", boost::bind(&MsgFrameImpl::moreClick, this, _1)));
			funcMaps_.insert(std::make_pair(L"more_menuitemclick", boost::bind(&MsgFrameImpl::moreMenuItemClick, this, _1)));
		}

		~MsgFrameImpl()
		{
			delete m_noticeFrame_;
			m_noticeFrame_ = NULL;
		}

		virtual void initData()
		{
			m_msgList = static_cast<CCustomListUI*>(paintManager_.FindControl(L"msgFrame_listView"));
		
			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);
			curPageParam_.offset = 0;
			curPageParam_.limit = pageLimit_;
			curCnt_ = 0;
			lastOp_ = L"";
			isNextPage_ = false;
			loadMetaData();
		}

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg)
		{
			std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
			if(it!=funcMaps_.end())
			{
				SERVICE_INFO(MODULE_NAME, RT_OK, "executeFunc %s", SD::Utility::String::wstring_to_string(funcName).c_str());
				it->second(msg);
			}
		}

		virtual void reloadCache()
		{
			loadMetaData();
		}

		virtual void refreshClick()
		{
			if (m_msgList == NULL) return;
			loadMetaData();
			m_msgList->HomeUp();
		}
	private:

		void loadMetaData()
		{
			if (m_msgList == NULL) return;
			m_msgList->SetFocus();
			MsgList lfResult;
			if(RT_OK != ProxyMgr::getInstance(userContext_)->getMsg(curPageParam_, lfResult, curCnt_)) return; 

			showTotal();
			if(isNextPage_)
			{
				isNextPage_ = false;
			}
			else
			{
				curPageParam_.offset = 0;
			}
			if (m_msgList->GetCount() > 0 && 0==curPageParam_.offset)
			{
				m_msgList->RemoveAll();
			}

			for(std::list<MsgNode>::iterator it = lfResult.begin(); it != lfResult.end(); ++it)
 			{
				CDialogBuilder builder;
				Onebox::MsgFrameListContainerElement* node = static_cast<Onebox::MsgFrameListContainerElement*>(
					builder.Create(L"msgShowListItem.xml", L"", DialogBuilderCallbackImpl::getInstance(), &paintManager_, NULL));
				if (NULL == node) continue;
				// fill in file node information
				node->nodeData = *it;
				node->initUI();

				if (!m_msgList->Add(node))
				{
					SERVICE_ERROR(MODULE_NAME, RT_ERROR, "add node failed.");
				}
 			}
			markToReaded();
		}

		void markToReaded()
		{
			batThread_ = boost::thread(boost::bind(&MsgFrameImpl::doMark, this));
		}

		void doMark()
		{
			for (int i=0; i < m_msgList->GetCount(); ++i)
			{
				MsgFrameListContainerElement* pMsgItem = static_cast<MsgFrameListContainerElement*>(m_msgList->GetItemAt(i));

				if (pMsgItem != NULL && MS_UnRead == pMsgItem->nodeData.status)
				{
					ProxyMgr::getInstance(userContext_)->updateMsg(pMsgItem->nodeData.id);
				}
			}
		}


		void showTotal()
		{
			if(NULL == m_msgList)
			{
				return;
			}
			CLabelUI* totalMsgLabel =  static_cast<CLabelUI*>(paintManager_.FindControl(L"msgFrame_msgTotal"));
			if(curCnt_ > MAX_MSG_ITEM_NUM)
			{
				totalMsgLabel->SetFixedWidth(totalMsgLabel->GetMinWidth() + 10);
			}
			else
			{
				totalMsgLabel->SetFixedWidth(totalMsgLabel->GetMinWidth());
			}

			CTabLayoutUI* pTab = static_cast<CTabLayoutUI*>(paintManager_.FindControl(L"msgFrame_Tab"));
			if (pTab){
				if (curCnt_ <= 0)
					pTab->SelectItem(1);
				else
					pTab->SelectItem(0);
			}
			std::wstring total = curCnt_ > MAX_MSG_ITEM_NUM ? MAX_MSG_DESC : SD::Utility::String::type_to_string<std::wstring,int64_t>(curCnt_);
			std::wstringstream strTotal;
			strTotal << "{c #000000}{f 12}" << total << "{/f}{/c} ";
			if (UI_LANGUGE::ENGLISH == iniLanguageHelper.GetLanguage())
				strTotal << "{c #666666}{f 10}" << L"Messages" << "{/f}{/c}";
			else
				strTotal << "{c #666666}{f 10}" << L"消息" << "{/f}{/c}";

			totalMsgLabel->SetText(strTotal.str().c_str());
		}

		void listClick(TNotifyUI& msg)
		{
			if (NULL == m_msgList) return;
			if (1 == msg.wParam)return;
			BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
			if (bCtrl) return;

			MsgFrameListContainerElement* pListItem = static_cast<MsgFrameListContainerElement*>(msg.pSender);	
			if (NULL == pListItem) return;
			if (MS_Readed== pListItem->nodeData.status) return;

			if(RT_OK != ProxyMgr::getInstance(userContext_)->updateMsg(pListItem->nodeData.id)) return;
			pListItem->nodeData.status = MS_Readed;

			CButtonUI* msgIcon = static_cast<CButtonUI*>(pListItem->FindSubControl(L"msgFrame_icon"));
			if (msgIcon == NULL) return;

			std::wstring iconPath = L"";
			if (MT_System == pListItem->nodeData.type)
			{
				iconPath = L"file='..\\img\\msg_sysShowIcon.png' source='17,0,33,16'";
			}
			else
			{
				iconPath = L"file='..\\img\\msg_showIcon.png' source='17,0,33,16'";
			}
			msgIcon->SetNormalImage(iconPath.c_str());
			msgIcon->SetTag((UINT_PTR)pListItem);
		}

		void downloadClick(TNotifyUI& msg)
		{
			if( msg.pSender->GetParent() == NULL)  return;
			if( msg.pSender->GetParent()->GetParent() == NULL)  return;
			MsgFrameListContainerElement* pFileListItem =  static_cast<MsgFrameListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
			if( pFileListItem == NULL)  return;
			if (true == downloadFlag_) return;
			std::list<UIFileNode> itemList;

			UIFileNode ufn;
			ufn.basic.id =  pFileListItem->nodeData.params.nodeId;
			ufn.basic.name =  SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.params.nodeName);
			ufn.basic.type =  pFileListItem->nodeData.params.nodeType;
			
			if(MT_Share ==pFileListItem->nodeData.type)
			{
				ufn.userContext = UserContextMgr::getInstance()->createUserContext(userContext_,
					pFileListItem->nodeData.providerId, UserContext_ShareUser, SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.providerName));
			}
			else if (MT_TeamSpace_Upload ==pFileListItem->nodeData.type)
			{
				ufn.userContext = UserContextMgr::getInstance()->createUserContext(userContext_,
					pFileListItem->nodeData.params.teamSpaceId, UserContext_Teamspace, SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.params.teamSpaceName));
			}

			Path remotePath = ufn.userContext->getPathMgr()->makePath();
			remotePath.id(pFileListItem->nodeData.params.nodeId);
			FILE_DIR_INFO fileNode;
			if (pFileListItem->nodeData.params.nodeType == FILE_TYPE_FILE)
			{
				(void)ufn.userContext->getSyncFileSystemMgr()->getProperty(remotePath, fileNode, ADAPTER_FILE_TYPE_REST);
			}
			else
			{
				(void)ufn.userContext->getSyncFileSystemMgr()->getProperty(remotePath, fileNode, ADAPTER_FOLDER_TYPE_REST);
			}
			ufn.basic.size =  fileNode.size;

			itemList.push_back(ufn);

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

		void saveClick(TNotifyUI& msg)
		{
			if( msg.pSender->GetParent() == NULL)  return;
			if( msg.pSender->GetParent()->GetParent() == NULL)  return;
			MsgFrameListContainerElement* pFileListItem =  static_cast<MsgFrameListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
			if( pFileListItem == NULL)  return;

			UIFileNode ufn;
			ufn.basic.id =  pFileListItem->nodeData.params.nodeId;
			ufn.basic.name =  SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.params.nodeName);
			ufn.basic.type =  pFileListItem->nodeData.params.nodeType;
			if(MT_Share ==pFileListItem->nodeData.type)
			{
				ufn.userContext = UserContextMgr::getInstance()->createUserContext(userContext_,
					pFileListItem->nodeData.providerId, UserContext_ShareUser, SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.providerName));
			}
			else if (MT_TeamSpace_Upload ==pFileListItem->nodeData.type)
			{
				ufn.userContext = UserContextMgr::getInstance()->createUserContext(userContext_,
					pFileListItem->nodeData.params.teamSpaceId, UserContext_Teamspace, SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.params.teamSpaceName));
			}

			std::wstring strButtonName = iniLanguageHelper.GetCommonString(L"comment_save").c_str();
			std::wstring strTitle = iniLanguageHelper.GetCommonString(L"comment_savetoonebox").c_str();
			CommonFileDialogPtr myFileDialog = CommonFileDialog::createInstance(iniLanguageHelper.GetSkinFolderPath(), paintManager_.GetPaintWindow(),NULL,strButtonName,strTitle,strTitle,strTitle,strTitle);
			MyFileCommonFileDialogNotify *notify = new SaveToMyFileDialogNotify(userContext_, iniLanguageHelper.GetLanguage());				
			myFileDialog->setOption(CFDO_only_show_folder);
			myFileDialog->setNotify(notify);

			if (E_CFD_CANCEL == myFileDialog->showModal(resultHanlder,COMMFILEDIALOG_SAVETOMYFILE))
			{
				return;
			}

			if(1==commonFileData.size())
			{
				MyFileCommonFileDialogData *data = (MyFileCommonFileDialogData*)(*(commonFileData.begin()))->data.get();

				bool isRename = true;
				if (checkFileIsRename(ufn,data->id,userContext_))
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

				UserContext* userContext = ufn.userContext;
				if(NULL == userContext) return;

				std::list<int64_t> srcNodeList;
				srcNodeList.push_back(ufn.basic.id);

				int64_t destFolderId = data->id;
				int64_t destOwnerId = userContext_->id.id;
				addRestTask(userContext, srcNodeList, destOwnerId, destFolderId, RESTTASK_SAVE, true);
			}
		}

		void addRestTask(UserContext* userContext, const std::list<int64_t>& srcNodeList, int64_t destOwnerId, int64_t destFolderId, const std::string& type, bool autoRename = false)
		{
			int32_t ret = userContext->getRestTaskMgr()->addRestTask(userContext->getUserInfoMgr()->getUserId(), 
				-1, srcNodeList, destOwnerId, destFolderId, type, autoRename);
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

		void deleteClick(TNotifyUI& msg)
		{
			/*m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
			if (!m_noticeFrame_->IsClickOk()) return;*/
			if( msg.pSender->GetParent() == NULL)  return;
			if( msg.pSender->GetParent()->GetParent() == NULL)  return;
			MsgFrameListContainerElement* pFileListItem = static_cast<MsgFrameListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());		
			if (NULL == pFileListItem) return; 
			int32_t ret = ProxyMgr::getInstance(userContext_)->deleteMsg(pFileListItem->nodeData.id);
			if(RT_OK != ret)
			{
				SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);
				simlpeNoticeFrame->Show(Error,  MSG_SHARE_DELETEFAILED_KEY);					
				delete simlpeNoticeFrame;
				simlpeNoticeFrame = NULL;
			}
			loadMetaData();
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

		bool CheckTeamspace(int64_t teamspaceId)
		{
			UserTeamSpaceNodeInfoArray  teamspaceListArray_;
			PageParam pageparam_;
			userContext_->getTeamSpaceMgr()->getTeamSpaceListUser(teamspaceListArray_,pageparam_);

			bool bCheck = false;
			for (size_t i=0; i<teamspaceListArray_.size(); ++i)
			{
				if (teamspaceId == teamspaceListArray_[i].teamId()){
					bCheck = true;
					break;
				}
			}

			return bCheck;
		}


		void viewClick(TNotifyUI& msg)
		{
			if( msg.pSender->GetParent() == NULL)  return;
			if( msg.pSender->GetParent()->GetParent() == NULL)  return;
			MsgFrameListContainerElement* pFileListItem =  static_cast<MsgFrameListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent());
			if( pFileListItem == NULL)  return;

			if (MT_TeamSpace_Add == pFileListItem->nodeData.type && !CheckTeamspace(pFileListItem->nodeData.params.teamSpaceId)){
				NoticeFrameMgr* noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());
				noticeFrame_->Run(Confirm,Error,MSG_TEAMSPACE_VIEWFAILED_KEY,MSG_TEAMSPACE_CANNOTVIEW_KEY,Modal);
				bool bIsClickOk =  noticeFrame_->IsClickOk();
				delete noticeFrame_;
				noticeFrame_ = NULL;

				return;
			}

			switch(pFileListItem->nodeData.type)
			{
				case MT_TeamSpace_Add:
					{
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_TEAMSPACE,
							SD::Utility::String::type_to_string<std::wstring>(pFileListItem->nodeData.params.teamSpaceId),
							SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.params.teamSpaceName)));
					};
					break;
				
				case MT_Share:
					{
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_SHARE2ME,
							SD::Utility::String::type_to_string<std::wstring>(pFileListItem->nodeData.providerId),
							SD::Utility::String::type_to_string<std::wstring>(pFileListItem->nodeData.params.nodeId)));
					};
					break;
				case MT_TeamSpace_Upload:
					{
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_TEAMSPACE,
							SD::Utility::String::type_to_string<std::wstring>(pFileListItem->nodeData.params.teamSpaceId),
							SD::Utility::String::utf8_to_wstring(pFileListItem->nodeData.params.teamSpaceName)));
					};
					break;
				case MT_System:
					{
						std::auto_ptr<ConfigureMgr> configureMgr(ConfigureMgr::create(NULL));
						Configure* config =  configureMgr->getConfigure();				
						std::wstring  str_server = config->serverUrl();
						str_server = str_server.substr(0,str_server.length()-6);	
						str_server += L"announcement/enter/";
						str_server += SD::Utility::String::type_to_string<std::wstring>(pFileListItem->nodeData.params.announcementId);
						(void)ShellExecute(NULL,L"open",L"explorer.exe",str_server.c_str(),NULL,SW_SHOWNORMAL);	
					};
					break;
				default:
					break;
			}
		}

		void nextPage(TNotifyUI& msg)
		{
			int32_t range = msg.wParam;
			int32_t pos = msg.lParam;

			//无下一页，或未滚到倒数SCROLL_LIMEN条以下，不处理
			if((curPageParam_.offset + curPageParam_.limit >= curCnt_) || (pos < range - MSG_SCROLL_LIMEN)) return;

			curPageParam_.offset += pageLimit_;
			isNextPage_ = true;
			loadMetaData();
		}

		bool checkFileIsRename(UIFileNode& sourceNode, int64_t& destId, UserContext* userContext)
		{
			Path listPath = userContext->getPathMgr()->makePath();
			listPath.id(destId);
			LIST_FOLDER_RESULT lfResult;
			ProxyMgr::getInstance(userContext)->listAll(userContext, listPath, lfResult);

			for (LIST_FOLDER_RESULT::iterator it = lfResult.begin(); it != lfResult.end(); ++it)
			{
				if (sourceNode.basic.name == it->name)
				{
					return true;
				}
			}
			return false;
		}

		void moreMenuItemClick(TNotifyUI& msg)
		{
			int menuId = msg.lParam;
			if (menuId < 0) return;
			switch (menuId)
			{
			case 0:  
				{
					downloadClick(msg);
				}
				break;
			case 1:
				{
					saveClick(msg);
				}
				break;
			case 2:
				{
					viewClick(msg);
				}
				break;
			default:
				break;
			}
		}

	private:
		UserContext* userContext_;
		std::map<std::wstring, call_func> funcMaps_;
		CPaintManagerUI& paintManager_;
		CCustomListUI* m_msgList;
		PageParam curPageParam_;
		int64_t curCnt_;
		int32_t pageLimit_;
		int32_t scrollLimen_;
		NoticeFrameMgr* m_noticeFrame_;

		std::map<int64_t, MsgNode> selectedItems_;
		bool isNextPage_;
		bool downloadFlag_;

		boost::thread batThread_;
		std::wstring lastOp_;
	};

	std::auto_ptr<MsgFrame> MsgFrame::instance_(NULL);

	MsgFrame* MsgFrame::getInstance(UserContext* context, CPaintManagerUI& paintManager)
	{
		if (NULL == instance_.get())
		{
			instance_.reset(new MsgFrameImpl(context, paintManager));
		}
		return instance_.get();
	}
}

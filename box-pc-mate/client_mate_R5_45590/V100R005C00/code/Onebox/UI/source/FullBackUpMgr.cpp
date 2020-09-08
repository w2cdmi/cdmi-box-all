#include "stdafxOnebox.h"
#include "FullBackUpMgr.h"
#include "ListContainerElement.h"
#include "RoundGif.h"
#include "UIScaleIconButton.h"
#include "InILanguage.h"
#include "FullBackUpTree.h"
#include "BackupAllMgr.h"
#include "UserInfoMgr.h"
#include "NotifyMgr.h"
#include "FullBackUpErrorDialog.h"
#include "ErrorConfMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("FullBackUp")
#endif

namespace Onebox
{
	class FullBackUpMgrImpl : public FullBackUpMgr
	{
	public:
		FullBackUpMgrImpl(UserContext* context, CPaintManagerUI& paintManager);

		~FullBackUpMgrImpl();

		virtual void initData();

		virtual void showError(int32_t ret)
		{
			SimpleNoticeFrame* simlpeNoticeFrame = new SimpleNoticeFrame(paintManager_);

			std::wstring desc;
			desc = iniLanguageHelper.GetCommonString(BACKUPERROR_CREATE_KEY).c_str();
			//desc += iniLanguageHelper.GetCommonString(BACKUPERROR_VALUE_KEY).c_str();
			//desc += SD::Utility::String::type_to_string<std::wstring,int32_t>(ret);
			//desc += L" ";
			desc += iniLanguageHelper.GetCommonString(BACKUPERROR_CAUSE_KEY).c_str();
			desc += ErrorConfMgr::getInstance()->getDescription(ret);
			if (ErrorConfMgr::getInstance()->getAdvice(ret) != L"")
			{
				desc += L" ";
				desc += iniLanguageHelper.GetCommonString(BACKUPERROR_PROPOSE_KEY).c_str();
				desc += ErrorConfMgr::getInstance()->getAdvice(ret);
			}

			simlpeNoticeFrame->ShowMessage(desc, (NoticeType)Error);
			delete simlpeNoticeFrame;
			simlpeNoticeFrame = NULL;
		}

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg);

		virtual void updateTaskProcess();

		virtual void updateErrorCount(int32_t errorCount);

		virtual void stop();

	private:
		void startClick(TNotifyUI& msg);

		void SetBackUpTask(TNotifyUI& msg);

		void OnRetractClick(TNotifyUI& msg);

		void loadAllBackupTask(BATaskInfo* taskInfo);

		void CloseBackUpTask(TNotifyUI& msg);

		void ErrorClick(TNotifyUI& msg);

		std::wstring strFormat(std::wstring str_des, bool isBold=false, std::wstring color=L"#000000");

		std::wstring getTimeStr(int64_t time/*S*/,bool isRunning = false);

		void remotePathClick(TNotifyUI& msg);

	private:
		UserContext* userContext_;
		std::map<std::wstring, call_func> funcMaps_;
		CPaintManagerUI& paintManager_;
		CRoundGifUI * m_roundProgress;
		int64_t m_remoteId;
	};

	FullBackUpMgr* FullBackUpMgr::instance_ = NULL;

	FullBackUpMgr* FullBackUpMgr::getInstance(UserContext* context, CPaintManagerUI& paintManager)
	{
		if (NULL == instance_)
		{
			instance_ = new FullBackUpMgrImpl(context, paintManager);
		}
		return instance_;
	}

	FullBackUpMgrImpl::FullBackUpMgrImpl(UserContext* context, CPaintManagerUI& paintManager)
		:userContext_(context)
		,paintManager_(paintManager)
	{
		m_roundProgress = static_cast<CRoundGifUI*>(paintManager_.FindControl(L"fullBackup_roundprogress"));		
		funcMaps_.insert(std::make_pair(L"start_click", boost::bind(&FullBackUpMgrImpl::startClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"roundprogress_click", boost::bind(&FullBackUpMgrImpl::OnRetractClick, this,_1)));	
		funcMaps_.insert(std::make_pair(L"setTask_click", boost::bind(&FullBackUpMgrImpl::SetBackUpTask, this, _1)));
		funcMaps_.insert(std::make_pair(L"close_click", boost::bind(&FullBackUpMgrImpl::CloseBackUpTask, this, _1)));
		funcMaps_.insert(std::make_pair(L"failed_count_click", boost::bind(&FullBackUpMgrImpl::ErrorClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"failed_des_click", boost::bind(&FullBackUpMgrImpl::ErrorClick, this, _1)));
		funcMaps_.insert(std::make_pair(L"remotePath_click", boost::bind(&FullBackUpMgrImpl::remotePathClick, this, _1)));
		m_remoteId = -1;
		BackupAllMgr::getInstance(userContext_);

		std::wstring computerName;
		if(iniLanguageHelper.GetLanguage() ==UI_LANGUGE::CHINESE)
		{
			computerName += userContext_->getUserInfoMgr()->getHostName();
			computerName += iniLanguageHelper.GetCommonString(MYSPACE_BACKUP_FOLDER_NAME).c_str();
		}
		else
		{
			computerName += iniLanguageHelper.GetCommonString(MYSPACE_BACKUP_FOLDER_NAME).c_str();
			computerName += L" ";
			computerName += userContext_->getUserInfoMgr()->getHostName();
		}
		BackupAllMgr::getInstance(userContext_)->setDefault(computerName);
	}

	FullBackUpMgrImpl::~FullBackUpMgrImpl()
	{
		BackupAllMgr::releaseInstance();
	}

	void FullBackUpMgrImpl::initData()
	{
		CVerticalLayoutUI* haveTask = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(L"fullBackup_haveTask"));
		CVerticalLayoutUI* noTask = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(L"fullBackup_noTask"));
		if (NULL == haveTask || NULL == noTask)return;
		BATaskInfo* taskInfo = BackupAllMgr::getInstance(userContext_)->getTaskInfo();
		if(BATS_Stop==taskInfo->status)
		{
			noTask->SetVisible();
			haveTask->SetVisible(false);
			return;
		}
		noTask->SetVisible(false);
		haveTask->SetVisible();
		loadAllBackupTask(taskInfo);
	}

	void FullBackUpMgrImpl::executeFunc(const std::wstring& funcName, TNotifyUI& msg)
	{
		std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
		if(it!=funcMaps_.end())
		{
			SERVICE_INFO(MODULE_NAME, RT_OK, "executeFunc %s", SD::Utility::String::wstring_to_string(funcName).c_str());
			it->second(msg);
		}
	}

	void FullBackUpMgrImpl::startClick(TNotifyUI& msg)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "startClick");
		BATaskInfo* taskInfo = BackupAllMgr::getInstance(userContext_)->getTaskInfo();
		DIALOGTYPE dType = (-1==taskInfo->remoteId)?TYPE_CREATE:TYPE_SETTING;
		FullBackUpTreeDialog* pFullBackUpDialog = new FullBackUpTreeDialog(userContext_,paintManager_.GetPaintWindow(),dType);
		pFullBackUpDialog->Create(paintManager_.GetPaintWindow(), _T("FullBackUpTreeDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		pFullBackUpDialog->CenterWindow();
		pFullBackUpDialog->ShowModal();
		delete pFullBackUpDialog;
		pFullBackUpDialog = NULL;
	}	

	void FullBackUpMgrImpl::SetBackUpTask(TNotifyUI& msg)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "SetBackUpTask");
		FullBackUpTreeDialog* pFullBackUpDialog = new FullBackUpTreeDialog(userContext_,paintManager_.GetPaintWindow(),TYPE_SETTING);
		pFullBackUpDialog->Create(paintManager_.GetPaintWindow(), _T("FullBackUpTreeDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		pFullBackUpDialog->CenterWindow();
		pFullBackUpDialog->ShowModal();
		delete pFullBackUpDialog;
		pFullBackUpDialog = NULL;
	}

	void FullBackUpMgrImpl::updateTaskProcess()
	{
		BATaskInfo* taskInfo = BackupAllMgr::getInstance(userContext_)->getTaskInfo();
		if(taskInfo)
		{
			CVerticalLayoutUI* haveTask = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(L"fullBackup_haveTask"));
			CVerticalLayoutUI* noTask = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(L"fullBackup_noTask"));
			if (NULL == haveTask || NULL == noTask)return;
			noTask->SetVisible(BATS_Stop==taskInfo->status);
			haveTask->SetVisible(!(BATS_Stop==taskInfo->status));
			loadAllBackupTask(taskInfo);
		}
	}

	void FullBackUpMgrImpl::OnRetractClick(TNotifyUI& msg)
	{
		SERVICE_FUNC_TRACE(MODULE_NAME, "OnRetractClick");
		if (L"fullBackup_roundprogress" == std::wstring(msg.pSender->GetName()))
		{
			RUNSTATE state = m_roundProgress->GetRunState(); //根据状态处理备份相关业务
			if (RUNSTATE::LOGOFF == state) return;
			CLabelUI* stateDes = static_cast<CLabelUI*>(paintManager_.FindControl(L"fullBackup_state_des"));
			std::wstring str_tip = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_BACKUP_TOOLTIP).c_str();
			std::wstring str_des = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_BACKUP_TEXT).c_str();
			RUNSTATE taskState = RUNSTATE::START;
			switch (state)
			{
			case RUNSTATE::START:
				BackupAllMgr::getInstance(userContext_) ->resumeBackupTask();
				break;
			case RUNSTATE::STOP:
			case RUNSTATE::FINISH:
			case RUNSTATE::ERRORSTATE:
				BackupAllMgr::getInstance(userContext_)->restartBackupTask();
				break;
			case RUNSTATE::PAUSE:
				{
					BackupAllMgr::getInstance(userContext_)->pauseBackupTask();
					str_tip = iniLanguageHelper.GetCommonString(BACKUP_STATE_PAUSE_DESCRIPTION_TOOLTIP).c_str();
					str_des= iniLanguageHelper.GetCommonString(BACKUP_STATE_PAUSE_DESCRIPTION).c_str();
					taskState = RUNSTATE::PAUSE;
				}
				break;
			default:
				break;
			}
			m_roundProgress->SetToolTip(str_tip.c_str());
			if (NULL != stateDes) stateDes->SetText(str_des.c_str());			
			m_roundProgress->SetRunState(taskState);
		}
	}

	void FullBackUpMgrImpl::loadAllBackupTask(BATaskInfo* taskInfo)
	{
		m_remoteId = taskInfo->remoteId;
		//HSLOG_TRACE(MODULE_NAME, RT_OK, "loadAllBackupTask: task status %d.", taskInfo->status);
		int32_t nProgress = 0;
		if (0 != taskInfo->curSize) nProgress =(int32_t) ((taskInfo->curSize-taskInfo->leftSize)*100/taskInfo->curSize);
		if(nProgress<0) nProgress = 0;
		CScaleIconButtonUI* closeBtn = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_close"));
		CScaleIconButtonUI* setTaskBtn = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_setTask"));		
		CLabelUI* stateDes = static_cast<CLabelUI*>(paintManager_.FindControl(L"fullBackup_state_des"));
		CHorizontalLayoutUI* pStateResult = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(L"fullBackup_state_result"));
		CScaleIconButtonUI* stateFailedCount = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_failed_count"));
		CScaleIconButtonUI* stateFailedDes = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_failed_des"));
		CScaleIconButtonUI* pBackupCount = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_backupCount"));
		CLabelUI* pUsedTime = static_cast<CLabelUI*>(paintManager_.FindControl(L"fullBackup_usedTime"));
		CScaleIconButtonUI* pRemotePath = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_remotePath"));
		CScaleIconButtonUI* pNewTime = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_newTime"));
		if (NULL == closeBtn || NULL == setTaskBtn || NULL == stateDes || NULL == pStateResult || NULL == stateFailedCount 
			|| NULL == stateFailedDes || NULL == pBackupCount || NULL == pUsedTime
			||NULL == pRemotePath || NULL == pNewTime) return;
		pRemotePath->SetVisible(false);
		std::wstring remotePath = iniLanguageHelper.GetCommonString(FULLBACKUP_FOLDER_NAME_DESCRIPTION) + PATH_DELIMITER + taskInfo->remotePath;
		if (taskInfo->remotePath.empty())
		{
			remotePath = L"--";
		}
		pRemotePath->SetText(remotePath.c_str());
		pRemotePath->SetVisible();
		pNewTime->SetVisible(false);
		std::wstring strNewTime = SD::Utility::DateTime::getTime(taskInfo->curStartTime, SD::Utility::UtcType::Crt, (SD::Utility::LanguageType)iniLanguageHelper.GetLanguage());
		if(0 == taskInfo->curStartTime)
		{
			strNewTime = L"--";
		}
		pNewTime->SetText(strNewTime.c_str());
		pNewTime->SetVisible();
		std::wstring str_stateDes = L"";
		std::wstring str_stateDesTips = L"";
		int32_t nAngle = 0;
		bool bEnable = false;
		std::wstring str_errorCount = L"";
		std::wstring str_errorDes = L"";
		RUNSTATE state = RUNSTATE::STOP;
		pStateResult->SetVisible(false);
		pBackupCount->SetVisible(false);
		pUsedTime->SetVisible(false);
		stateFailedCount->SetVisible(false);
		stateFailedDes->SetVisible(false);
		stateFailedDes->SetEnabled(false);
		pRemotePath->SetEnabled();
		switch (taskInfo->status)
		{
		case BATS_Stop:
			initData();
			break;
		case BATS_Running:
			{
				if (!closeBtn->IsEnabled() || !setTaskBtn->IsEnabled() )
				{
					closeBtn->SetEnabled();
					setTaskBtn->SetEnabled();
				}
				str_stateDes = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_BACKUP_TEXT).c_str();
				if (nProgress > 99) nProgress = 99;
				if (nProgress >= 0)
				{
					str_stateDes += L" ";
					str_stateDes +=SD::Utility::String::type_to_string<std::wstring>(nProgress);
					str_stateDes += L"%";
				}
				str_stateDesTips = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_BACKUP_TOOLTIP).c_str();
				state = RUNSTATE::PAUSE;
				nAngle = nProgress<=0?1:nProgress;
				std::wstring str_countDes = iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FIRST).c_str();
				str_countDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->curSize),true);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->curCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_THIRD).c_str();
				int64_t iLeftSize = taskInfo->leftSize;
				int32_t iLeftCnt = taskInfo->leftCnt;
				if (iLeftSize > taskInfo->curSize || iLeftCnt > taskInfo->curCnt)
				{
					iLeftSize = (int64_t)taskInfo->curSize*99/100;
					iLeftCnt =  (int32_t)taskInfo->leftCnt*99/100;
				}
				str_countDes += strFormat(SD::Utility::String::getSizeStr(iLeftSize),true);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(iLeftCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FOURTH).c_str();
				str_countDes =  strFormat(str_countDes);
				pBackupCount->SetText(str_countDes.c_str());
				pBackupCount->SetVisible();
				std::wstring str_timeDes = iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION).c_str();
				if(taskInfo->leftTime < 0)
				{
					str_timeDes += strFormat(L"--",true);
				}
				else
				{
					str_timeDes += strFormat(getTimeStr(taskInfo->leftTime,true),true);
				}
				str_timeDes =  strFormat(str_timeDes);
				pUsedTime->SetText(str_timeDes.c_str());
				pUsedTime->SetVisible();
				if (!taskInfo->curUpload.empty())
				{
					pStateResult->SetVisible();
					std::wstring str_FailedDes = iniLanguageHelper.GetCommonString(FULLBACKUP_BACKUPING_DESCRIPTION).c_str();
					str_FailedDes += taskInfo->curUpload;
					str_FailedDes =  strFormat(str_FailedDes);
					stateFailedDes->SetToolTip(taskInfo->curUpload.c_str());
					stateFailedDes->SetText(str_FailedDes.c_str());
					stateFailedDes->SetVisible();
				}			
			}
			break;
		case BATS_Pausing:
			{
				str_stateDes = iniLanguageHelper.GetCommonString(BACKUP_STATE_PAUSE_DESCRIPTION).c_str();
				str_stateDesTips = iniLanguageHelper.GetCommonString(BACKUP_PAUSE_DESCRIPTION).c_str();
				state = RUNSTATE::START;
				nAngle = nProgress==0?1:nProgress;
				std::wstring str_countDes = iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FIRST).c_str();
				str_countDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->curSize),true);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->curCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_THIRD).c_str();
				str_countDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->leftSize),true);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->leftCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FOURTH).c_str();
				str_countDes =  strFormat(str_countDes);
				pBackupCount->SetText(str_countDes.c_str());
				pBackupCount->SetVisible();
			}
			break;
		case BATS_Complete:
			{
				str_stateDes = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_COMPLETE_TEXT).c_str();
				str_stateDesTips = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_COMPLETE_TOOLTIP).c_str();
				nAngle = 100;
				state = RUNSTATE::FINISH;
				pStateResult->SetVisible();
				std::wstring str_countDes = iniLanguageHelper.GetCommonString(FULLBACKUP_BACKUPED_DESCRIPTION).c_str();
				str_countDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->totalSize),true);
				str_countDes += L" ";
				str_countDes +=  iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->totalCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FOURTH).c_str();
				str_countDes =  strFormat(str_countDes);
				pBackupCount->SetText(str_countDes.c_str());
				pBackupCount->SetVisible();

				std::wstring str_timeDes = iniLanguageHelper.GetCommonString(FULLBACKUP_TIMED_DESCRIPTION).c_str();
				str_timeDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->curSize),true);
				str_timeDes += L" ";
				str_timeDes +=  iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_timeDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->curCnt);
				str_timeDes += L" ";
				str_timeDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FOURTH).c_str();
				str_timeDes += iniLanguageHelper.GetCommonString(FULLBACKUP_TIMED_DESCRIPTION_SECOND).c_str();
				str_timeDes += strFormat(getTimeStr(taskInfo->curRunTime),true);
				str_timeDes =  strFormat(str_timeDes);

				pUsedTime->SetText(str_timeDes.c_str());
				pUsedTime->SetVisible();
				std::wstring str_FailedDes = strFormat( iniLanguageHelper.GetCommonString(FULLBACKUP_FAILED_DESCRIPTION_START).c_str(),false,L"#009900");
				str_FailedDes += L" ";
				str_FailedDes += strFormat( SD::Utility::String::type_to_string<std::wstring>(taskInfo->totalDay),true,L"#009900");
				str_FailedDes += L" ";
				str_FailedDes += strFormat( iniLanguageHelper.GetCommonString(FULLBACKUP_FAILED_DESCRIPTION_END).c_str(),false,L"#009900");
				str_FailedDes =  strFormat(str_FailedDes);
				stateFailedDes->SetText(str_FailedDes.c_str());
				stateFailedDes->SetToolTip(L"");
				stateFailedDes->SetVisible();
			}
			break;
		case BATS_Failed:
			{
				str_stateDes = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_FAILED_TEXT).c_str();
				str_stateDesTips = iniLanguageHelper.GetCommonString(BACKUP_BUTTON_STATE_FAILED_TOOLTIP).c_str();
				if (0 != taskInfo->totalSize) nProgress =(int32_t) ((taskInfo->totalSize-taskInfo->leftSize)*100/taskInfo->totalSize);
				nAngle = nProgress<=0?1:nProgress;
				bEnable = true;
				str_errorCount = SD::Utility::String::type_to_string<std::wstring,int32_t>(taskInfo->failedCnt);
				str_errorDes = strFormat(iniLanguageHelper.GetCommonString(FULLBACKUP_FAILED_COUNT_DESCRIPTION).c_str(),false,L"#006AB0");
				state = RUNSTATE::ERRORSTATE;
				pStateResult->SetVisible();
				std::wstring str_countDes = iniLanguageHelper.GetCommonString(FULLBACKUP_BACKUPED_DESCRIPTION).c_str();
				str_countDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->totalSize),true);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->totalCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FOURTH).c_str();
				str_countDes =  strFormat(str_countDes);
				pBackupCount->SetText(str_countDes.c_str());
				pBackupCount->SetVisible();

				std::wstring str_timeDes = iniLanguageHelper.GetCommonString(FULLBACKUP_TIMED_DESCRIPTION).c_str();
				str_timeDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->curSize),true);
				str_timeDes += L" ";
				str_timeDes +=  iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_timeDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->curCnt);
				str_timeDes += L" ";
				str_timeDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FOURTH).c_str();
				str_timeDes += iniLanguageHelper.GetCommonString(FULLBACKUP_TIMED_DESCRIPTION_SECOND).c_str();
				str_timeDes += strFormat(getTimeStr(taskInfo->curRunTime),true);
				str_timeDes =  strFormat(str_timeDes);

				pUsedTime->SetText(str_timeDes.c_str());
				pUsedTime->SetVisible();
				stateFailedCount->SetEnabled(bEnable);
				stateFailedCount->SetText(str_errorCount.c_str());
				stateFailedCount->SetVisible();
				str_errorDes =  strFormat(str_errorDes);
				stateFailedDes->SetText(str_errorDes.c_str());
				stateFailedDes->SetToolTip(L"");
				stateFailedDes->SetVisible();
				stateFailedDes->SetEnabled();
			}
			break;
		case BATS_Offline:
			{				
				pRemotePath->SetEnabled(false);
				if (closeBtn->IsEnabled() || setTaskBtn->IsEnabled() )
				{
					closeBtn->SetEnabled(false);
					setTaskBtn->SetEnabled(false);
				}
				str_stateDes = iniLanguageHelper.GetCommonString(BACKUP_STATE_PAUSE_DESCRIPTION).c_str();
				str_stateDesTips = L"";
				nAngle = nProgress==0?1:nProgress;
				state = RUNSTATE::LOGOFF;
				pStateResult->SetVisible();
				std::wstring str_countDes = iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FIRST).c_str();
				str_countDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->totalSize),true);
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->totalCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_THIRD).c_str();
				str_countDes += strFormat(SD::Utility::String::getSizeStr(taskInfo->leftSize),true);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_SECOND).c_str();
				str_countDes += SD::Utility::String::type_to_string<std::wstring>(taskInfo->leftCnt);
				str_countDes += L" ";
				str_countDes += iniLanguageHelper.GetCommonString(FULLBACKUP_COUNT_DESCRIPTION_FOURTH).c_str();
				str_countDes =  strFormat(str_countDes);
				pBackupCount->SetText(str_countDes.c_str());
				pBackupCount->SetVisible();
				std::wstring str_FailedDes = strFormat(iniLanguageHelper.GetCommonString(FULLBACKUP_OFFLINE_DESCRIPTION).c_str(),false,L"#FC5043");
				str_FailedDes =  strFormat(str_FailedDes);
				stateFailedDes->SetText(str_FailedDes.c_str());
				stateFailedDes->SetToolTip(L"");
				stateFailedDes->SetVisible();
			}
			break;
		default:
			break;
		}
		m_roundProgress->SetAngle((int32_t)(3.6 * nAngle));
		m_roundProgress->SetRunState(state);
		m_roundProgress->SetToolTip(str_stateDesTips.c_str());
		if (NULL != stateDes)
		{
			stateDes->SetText(str_stateDes.c_str());
			stateDes->SetToolTip(str_stateDes.c_str());
		}
	}
	
	void FullBackUpMgrImpl::CloseBackUpTask(TNotifyUI& msg)
	{
		NoticeFrameMgr* _noticeFrame = new NoticeFrameMgr(paintManager_.GetPaintWindow());
		_noticeFrame->Run(Choose,Ask,L"",MSG_FULLBACKUP_CLOSE_TIPS,Modal);
		bool bIsClickOk =  _noticeFrame->IsClickOk();
		delete _noticeFrame;
		_noticeFrame = NULL;
		if (!bIsClickOk)  return;
		BackupAllMgr::getInstance(userContext_)->closeBackupTask();
	}

	void FullBackUpMgrImpl::ErrorClick(TNotifyUI& msg)
	{
		FullBackUpErrorDialog* pFullBackUpDialog = new FullBackUpErrorDialog(userContext_);
		pFullBackUpDialog->Create(paintManager_.GetPaintWindow(), _T("FullBackUpErrorDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
		pFullBackUpDialog->CenterWindow();
		pFullBackUpDialog->ShowModal();
		delete pFullBackUpDialog;
		pFullBackUpDialog = NULL;
	}

	std::wstring FullBackUpMgrImpl::strFormat(std::wstring str_des, bool isBold, std::wstring color)
	{
		std::wstring font = isBold ? L"13" : L"12";
		str_des = L"{f "+ font + L"}{c " + color + L"}" + str_des + L"{/c}{/f}";
		return str_des;
	}

	std::wstring FullBackUpMgrImpl::getTimeStr(int64_t time/*S*/,bool isRunning)
	{
		std::wstring strTime;
		if(time < 0)
		{
			strTime = L"- -";
		}
		else if(0==time)
		{
			strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_ONESECOND).c_str();
		}
		else if(time<60)
		{
			strTime = SD::Utility::String::type_to_string<std::wstring>(time);
			strTime += L" ";
			strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_SECOND).c_str();
		}
		else if(time<60*60)
		{
			strTime = SD::Utility::String::type_to_string<std::wstring>(time/60);
			strTime += L" ";
			strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_MINUTE).c_str();
			int64_t tempSec = time%60;
			if(tempSec>0)
			{
				strTime += L" ";
				strTime += SD::Utility::String::type_to_string<std::wstring>(tempSec);
				strTime += L" ";
				strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_SECOND).c_str();
			}
		}
		else if(time< 24*60*60)
		{
			int64_t tempHour = time/(60*60);
			strTime = SD::Utility::String::type_to_string<std::wstring>(tempHour);
			strTime += L" ";
			strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_HOUR).c_str();
			int64_t tempMin = (time - tempHour*60*60)/60;
			if(tempMin>0)
			{
				strTime += L" ";
				strTime += SD::Utility::String::type_to_string<std::wstring>(tempMin);
				strTime += L" ";
				strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_MINUTE).c_str();
			}
		}
		else
		{
			int64_t tempDay = time/(24*60*60);
			
			if (isRunning && tempDay >= 30)
			{
				strTime = L" 30";
				strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_MOREDAY).c_str();
				return strTime;
			}
			strTime = SD::Utility::String::type_to_string<std::wstring>(tempDay);
			strTime += L" ";
			strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_DAY).c_str();
			strTime += L" ";
			int64_t tempHour = (time-tempDay*24*60*60)/(60*60);
			strTime += SD::Utility::String::type_to_string<std::wstring>(tempHour);
			strTime += L" ";
			strTime +=iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_HOUR).c_str();
			strTime += L" ";
			strTime += SD::Utility::String::type_to_string<std::wstring>((time - tempDay*24*60*60 - tempHour*60*60)/60);
			strTime += L" ";
			strTime += iniLanguageHelper.GetCommonString(FULLBACKUP_TIME_DESCRIPTION_MINUTE).c_str();
		}
		strTime = L" " + strTime;
		return strTime;
	}

	void FullBackUpMgrImpl::remotePathClick(TNotifyUI& msg)
	{
		/*userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE,
			L"0",ParentID 固定 为 0
			SD::Utility::String::type_to_string<std::wstring>(m_remoteId)));*/

		userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE,
			SD::Utility::String::type_to_string<std::wstring>(m_remoteId),
			L""));
	}

	void FullBackUpMgrImpl::updateErrorCount(int32_t errorCount)
	{
		CScaleIconButtonUI* stateFailedCount = static_cast<CScaleIconButtonUI*>(paintManager_.FindControl(L"fullBackup_failed_count"));
		if (NULL == stateFailedCount) return;
		stateFailedCount->SetText(SD::Utility::String::type_to_string<std::wstring,int32_t>(errorCount).c_str());
	}

	void FullBackUpMgrImpl::stop()
	{
		(void)BackupAllMgr::getInstance(userContext_)->stop(false);
	}
} 
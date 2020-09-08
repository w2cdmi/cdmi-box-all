#include "stdafxOnebox.h"
#include "TransTaskMgr.h"
#include "ControlNames.h"
#include "Configure.h"
#include "CustomListUI.h"
#include "Utility.h"
#include "SkinConfMgr.h"
#include "ListContainerElement.h"
#include "ChildLayout.h"
#include "UserContextMgr.h"
#include "UserInfoMgr.h"
#include "SyncFileSystemMgr.h"
#include "PathMgr.h"
#include "TransTaskErrorShowDialog.h"
#include "DialogBuilderCallbackImpl.h"
#include "AsyncTaskMgr.h"
#include "SimpleNoticeFrame.h"
#include "NoticeFrame.h"
#include "ErrorConfMgr.h"
#include "InILanguage.h"
#include "FilterMgr.h"
#include "ConfigureMgr.h"
#include "ShareResMgr.h"
#include "NotifyMgr.h"
#include "CommonLoadingFrame.h"
#include "UIScaleIconButton.h"

#include <boost/thread.hpp>
#include <boost/function.hpp>
#include <boost/bind.hpp>

namespace Onebox
{
	#define ROOT_DEPTH (1)
	#define TRANSTASK_LIST_ITEM_DEPTH (27)
	#define TRANSTASK_LIST_ITEM_NEXT_PAGE_HEIGHT (15)

	#define IC_OPER_TRANSFER_UPLOAD_PATH L"..\\Image\\ic_oper_transfer_upload.png"
	#define IC_OPER_TRANSFER_DOWNLOAD_PATH L"..\\Image\\ic_oper_transfer_download.png"
	#define  IC_OPER_TRANSFER_UPLOAD_LINK L"..\\Image\\ic_transfer_open_cloudpath.png"
	#define  IC_OPER_TRANSFER_DOWNLOAD_LINK L"..\\Image\\ic_transfer_open_locallist.png"

	#define STATUS_DESCRIBE_RUNNING_FORMAT L"{c #000000}{f 12}%s{/f}{/c}{c #008be8}{f 12} %d%%{/f}{/c}"
	#define STATUS_DESCRIBE_ERROR_FILE_FORMAT L"{c #666666}{f 12}%s - {/f}{/c}{c #FF6B21}{f 12}%s{/f}{/c}"
	#define STATUS_DESCRIBE_ERROR_DIR_FORMAT L"{c #666666}{f 12}%s - {/f}{/c}{c #FF6B21}{f 12}%s%s{/f}{/c}"
	#define STATUS_DESCRIBE_COMPLETE_FORMAT L"{c #666666}{f 12}%s{/f}{/c}{c #666666}{f 12}%d%%{/f}{/c}"
	#define STATUS_DESCRIBE_INFO_FORMART L"{c #666666}{f 12}%s{/f}{/c}"

	#define TRANSERSLISTTIP_BKIMAGE_1 L"file='..\\img\\ic_top_sys_number.png' source='0,0,16,16'"
	#define TRANSERSLISTTIP_BKIMAGE_2 L"file='..\\img\\ic_top_sys_number.png' source='0,26,23,42'"
	#define TRANSERSLISTTIP_BKIMAGE_3 L"file='..\\img\\ic_top_sys_number.png' source='0,52,30,68'"

	#define TRANSFER_SPEED_FORMATING L"{c #000000}{f 10}%s/s{/f}{/c}"

	#define TRANSFER_NUMBER_LEVEL_0 10
	#define TRANSFER_NUMBER_LEVEL_1 100
	#define TRANSFER_TASK_REFRESH_TIME 30
	#define TRANSFER_TASK_REFRESH_SLEEP_TIME 5
	//#define TRANSTASK_SCROLL_LIMEN (100)	//

	class TransTaskMgrImpl : public TransTaskMgr
	{
	public:
		TransTaskMgrImpl(UserContext* context, CPaintManagerUI& paintManager)
			:userContext_(context)
			,paintManager_(paintManager)
			,m_isInitData(false)
			,m_stop_(true)
			,m_refresh_time_(time(NULL))
			,m_iTransNumber(0)
		{
			m_noticeFrame_ = new NoticeFrameMgr(paintManager_.GetPaintWindow());

			funcMaps_.insert(std::make_pair(L"upload_pauseAll_click", boost::bind(&TransTaskMgrImpl::TransTaskPauseAllClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"upload_resumeAll_click", boost::bind(&TransTaskMgrImpl::TransTaskResumeAllClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"upload_cancelAll_click", boost::bind(&TransTaskMgrImpl::TransTaskCancelAllClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"complete_clearAll_click", boost::bind(&TransTaskMgrImpl::clearAllCompleteClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"item_resume_click", boost::bind(&TransTaskMgrImpl::itemResumeClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"item_pause_click", boost::bind(&TransTaskMgrImpl::itemPauseClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"item_delete_click", boost::bind(&TransTaskMgrImpl::itemDeleteClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"item_error_click", boost::bind(&TransTaskMgrImpl::itemErrorClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"item_abnormal_click", boost::bind(&TransTaskMgrImpl::itemAbnormalClick, this,_1)));

			funcMaps_.insert(std::make_pair(L"ListView_nextPage", boost::bind(&TransTaskMgrImpl::nextPage, this,_1)));
			funcMaps_.insert(std::make_pair(L"complete_ListView_nextPage", boost::bind(&TransTaskMgrImpl::nextCompletePage, this,_1)));

			funcMaps_.insert(std::make_pair(L"itemComplete_listItem_itemdbclick", boost::bind(&TransTaskMgrImpl::itemCompleteDoubleClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"itemComplete_Link_click", boost::bind(&TransTaskMgrImpl::itemCompleteOpenFileClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"itemComplete_delete_click", boost::bind(&TransTaskMgrImpl::itemCompleteDeleteClick, this,_1)));
			funcMaps_.insert(std::make_pair(L"uploadTable_selectchanged", boost::bind(&TransTaskMgrImpl::TransTaskTabSelectChanged, this,_1)));
			funcMaps_.insert(std::make_pair(L"completeTable_selectchanged", boost::bind(&TransTaskMgrImpl::TransTaskCompleteTabSelectChanged, this,_1)));
			funcMaps_.insert(std::make_pair(L"item_status_describe_click", boost::bind(&TransTaskMgrImpl::errorStateDescrib, this,_1)));

			pageLimit_ = GetUserConfValue(CONF_SETTINGS_SECTION, CONF_PAGE_LIMIT_KEY, DEFAULT_PAGE_LIMIT_NUM);

			currentPage_.start = 0;
			currentPage_.offset = pageLimit_;

			currentCompletePage_.start = 0;
			currentCompletePage_.offset = pageLimit_;

			SetTransfersListTip(0);
			updateTransSpeed( 0, 0 );

			int32_t transcount = userContext_->getAsyncTaskMgr()->getTasksCount(AsyncTransType(ATT_Download|ATT_Upload));
			SetTransfersListTip(transcount);
		}

		virtual ~TransTaskMgrImpl()
		{
			Release();
		}

		virtual void initData()
		{
			currentCompletePage_.start = 0;
			currentCompletePage_.offset = pageLimit_;

			currentPage_.start = 0;
			currentPage_.offset = pageLimit_;

			CListUI* listView = getList(0);
			if( listView ) listView->RemoveAll();

			loadData(currentPage_);

			if ( !m_isInitData )
			{
				m_isInitData = true;

				thread_ = boost::thread(boost::bind(&TransTaskMgrImpl::RefreshTransTaskStatus, this));
			}
		}

		void executeFunc(const std::wstring& funcName, TNotifyUI& msg)
		{
			std::map<std::wstring, call_func>::const_iterator it = funcMaps_.find(funcName);
			if(it!=funcMaps_.end())
			{
				it->second(msg);
			}
		}

		void SetTransfersListTip( int64_t transdiff )
		{
			boost::mutex::scoped_lock lock(mutex_);
			SetTransfersListTipSelf(transdiff);
		}

		void SetTransfersListTipSelf( int64_t transdiff )
		{
			if( 0 != transdiff)
			{
				m_iTransNumber = GetTransNumber() + transdiff;
			}
			else
			{
				m_iTransNumber = 0;
			}

			if ( m_iTransNumber > TRANSFER_NUMBER_LEVEL_1 
				&& m_iTransNumber - transdiff > TRANSFER_NUMBER_LEVEL_1 )
			{
				return;
			}

			CLabelUI* cltranserslisttip = static_cast<CLabelUI*>(paintManager_.FindControl(ControlNames::TRANSFERSLISTTIP));
			if ( NULL != cltranserslisttip )
			{
				std::wstring stranstasknumber = L"";
				if ( m_iTransNumber > 0 )
				{
					cltranserslisttip->SetFixedHeight(16);
					if ( m_iTransNumber < TRANSFER_NUMBER_LEVEL_0 )
					{		
						cltranserslisttip->SetFixedWidth(16);
						stranstasknumber = SD::Utility::String::type_to_string(m_iTransNumber, stranstasknumber);
						cltranserslisttip->SetBkImage( TRANSERSLISTTIP_BKIMAGE_1 );
					}
					else if ( m_iTransNumber < TRANSFER_NUMBER_LEVEL_1 )
					{
						cltranserslisttip->SetFixedWidth(23);
						stranstasknumber = SD::Utility::String::type_to_string(m_iTransNumber, stranstasknumber);
						cltranserslisttip->SetBkImage( TRANSERSLISTTIP_BKIMAGE_2 );
					}
					else
					{
						cltranserslisttip->SetFixedWidth(30);
						stranstasknumber = L"99+";
						cltranserslisttip->SetBkImage( TRANSERSLISTTIP_BKIMAGE_3 );
					}
					CLabelUI* cltransnumber = static_cast<CLabelUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_NUMBER));
					if ( NULL != cltransnumber)
					{
						cltransnumber->SetVisible(false);
						cltransnumber->SetText(stranstasknumber.c_str());
						cltransnumber->SetVisible(true);
					}
					cltranserslisttip->SetVisible(false);
					cltranserslisttip->SetText( stranstasknumber.c_str() );
					cltranserslisttip->SetVisible(true);					
				}
				else
				{
					cltranserslisttip->SetVisible(false);
					cltranserslisttip->SetText( L"" );
					cltranserslisttip->SetBkImage( L"" );
					cltranserslisttip->SetFixedHeight(16);
					cltranserslisttip->SetFixedWidth(16);	
				}
				if(NULL!=cltranserslisttip->GetParent())
				{
					cltranserslisttip->GetParent()->Invalidate();
				}
			}
		}

		void SetErrorInfo(TransTaskListContainerElement* item, int64_t errortotal)
		{
			if ( NULL == item )
			{
				return;
			}

			std::wstring status_describe = L"";
			CLabelUI* clbstatus_describe = static_cast<CProgressUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
			if (!clbstatus_describe) return;

			clbstatus_describe->SetEnabled();
			clbstatus_describe->SetShowHtml(true);
			
			if ( FILE_TYPE_FILE == item->nodeData.fileType )
			{
				if ( ATT_Download == item->nodeData.type)
				{
					status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_DOWNLOADED);
					clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_ERROR_FILE_FORMAT, 
						status_describe.c_str(), iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_FAILED).c_str()).c_str());
				}
				else if ( ATT_Upload == item->nodeData.type )
				{
					status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADED);
					clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_ERROR_FILE_FORMAT, 
						status_describe.c_str(), iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_FAILED).c_str()).c_str());
				}
			}
			else if ( FILE_TYPE_DIR == item->nodeData.fileType )
			{
				std::wstring serrortotal = L"";
				if ( errortotal > 99 ) serrortotal = L"99+";
				else if( errortotal > 0 )serrortotal = SD::Utility::String::format_string(L"%lld", errortotal);

				if ( ATT_Download == item->nodeData.type)
				{
					status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_DOWNLOADED);
				}
				else if ( ATT_Upload == item->nodeData.type )
				{
					status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADED);
				}
				clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_ERROR_DIR_FORMAT, 
					status_describe.c_str(), serrortotal.c_str(), iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_FAILED).c_str()).c_str());
			}
		}

		
		void updateTaskStatus(const std::wstring& group, AsyncTransStatus status)
		{
			m_refresh_time_ = time(NULL);
			CListUI* listView = NULL;
			listView = getCurrentList();

			switch (status)
			{
			case ATS_Complete:
				{
					deleteTaskItem(group);

					AsyncTransCompleteNode node;
					
					int ret = userContext_->getAsyncTaskMgr()->getHistoricalTask( group, node );
					if( RT_OK != ret ) return;

					if( NULL != listView ) 
					{
						if( listView->GetName() == ControlNames::TRANSTASK_COMPLETE_LISTVIEW )
						{
							UITransTaskRootNode uinode;
							uinode = *node;
							uinode.completeTime = time(NULL);
							addCompleteItem(uinode, 0);

							if( listView->GetCount() > pageLimit_ )
							{
								listView->RemoveAt(listView->GetCount() - 1);
							}
						}
						else
						{
							loadDataForDel();
						}
					}

					boost::mutex::scoped_lock lock(mutex_);
					for (std::list<std::wstring>::iterator iter = openList_.begin();iter != openList_.end();iter++)
					{
						if (*iter == group)
						{
							std::wstring path = node->parent + PATH_DELIMITER + node->name;
							DWORD attr = ::GetFileAttributes(path.c_str());
							if (INVALID_FILE_ATTRIBUTES == attr) return;

							attr |= FILE_ATTRIBUTE_READONLY;
							if (!::SetFileAttributes(path.c_str(), attr))
							{
								return;
							}

							(void)ShellExecute(NULL,L"open",L"explorer.exe",path.c_str(),NULL,SW_SHOWNORMAL);
							openList_.erase(iter);
							break;
						}
					}
				}
				break;
			case ATS_Error:
				{
					if( NULL == listView || listView->GetName() != ControlNames::TRANSTASK_LISTVIEW ) return;
					TransTaskListContainerElement* item = getItemByGroup(group);
					if( NULL == item ) return;

					int32_t errortotal = (item->nodeData.fileType == FILE_TYPE_FILE) ? 1 : 
						userContext_->getAsyncTaskMgr()->getErrorTasksCount(group);
					if(errortotal > 0)
					{
						SetErrorInfo(item, errortotal);
					}

					item->nodeData.status = ATS_Error;
					setItemResumeAndPauseButtonStatus(item);
					break;
				}
			case ATS_Waiting:
				{
					if( NULL == listView || listView->GetName() != ControlNames::TRANSTASK_LISTVIEW ) return;
					TransTaskListContainerElement* item = getItemByGroup(group);
					if( NULL == item ) return;

					item->nodeData.status = status;
					SetDescribeForStatus(item);
					setItemResumeAndPauseButtonStatus(item);
				}
				break;
			default:
				break;
			}
		}

		void updateTask(const std::wstring& group, const int64_t transedSize, const int64_t totalSize)
		{
			m_refresh_time_ = time(NULL);
			CListUI* listView = getCurrentList();
			if ( !listView || listView->GetName() != ControlNames::TRANSTASK_LISTVIEW ) return;

			TransTaskListContainerElement* item = getItemByGroup(group);
			if( !item )
			{
				addItem( group );
				return;
			}

			if ( ATS_Cancel == item->nodeData.status )
			{
				return;
			}

			if ( (transedSize > totalSize) || (transedSize < 0) || (totalSize < 0) ) return;

			int64_t lastTransedSize = item->nodeData.transedSize;
			item->nodeData.transedSize = transedSize;
			item->nodeData.size = totalSize;

			// set size
			CLabelUI* size = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_SIZE));
			if ( !size ) return;
			size->SetText(SD::Utility::String::format_string(L"%s / %s", 
				SD::Utility::String::getSizeStr(transedSize).c_str(), 
				SD::Utility::String::getSizeStr(totalSize).c_str()).c_str());

			// set progress
			int32_t iProgress = (totalSize==0)?100:
				(int32_t)(((float)transedSize)/totalSize*100);
			if ( iProgress == 100 )
			{
				iProgress = 99;
			}

			item->SetValue(iProgress);
			std::wstring status_describe = L"";
			CLabelUI* clbstatus_describe = static_cast<CProgressUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
			clbstatus_describe->SetShowHtml(true);
					
			if ( ATT_Download == item->nodeData.type)
			{
				status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_DOWNLOADING);
				clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_RUNNING_FORMAT, 
					status_describe.c_str(), iProgress).c_str());
			}
			else if ( ATT_Upload == item->nodeData.type )
			{
				status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADING);
				clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_RUNNING_FORMAT, 
					status_describe.c_str(), iProgress).c_str());
			}	

			setItemResumeAndPauseButtonStatus(item);
		}

		void updateTransSpeed(const int64_t uploadSpeed, const int64_t downloadSpeed)
		{
			CListUI* listView = getCurrentList();
			if ( !listView || listView->GetName() != ControlNames::TRANSTASK_LISTVIEW ) return;

			CLabelUI* cluploadpeed = NULL;
			// update total progress
			cluploadpeed = static_cast<CLabelUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_UPLOAD_SPEED));
			if (NULL == cluploadpeed) return;

			std::wstring wsuploadspeed = SD::Utility::String::getSizeStr(uploadSpeed) + L"/s";
			cluploadpeed->SetVisible(false);
			cluploadpeed->SetText(wsuploadspeed.c_str());
			cluploadpeed->SetVisible(true);

			CLabelUI* cldownloadpeed = NULL;
			cldownloadpeed = static_cast<CLabelUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_DOWNLOAD_SPEED));
			if (NULL == cldownloadpeed) return;

			std::wstring wsdownloadspeed = SD::Utility::String::getSizeStr(downloadSpeed) + L"/s";
			cldownloadpeed->SetVisible(false);
			cldownloadpeed->SetText(wsdownloadspeed.c_str());
			cldownloadpeed->SetVisible(true);
		}

		void updateTaskSize(const std::wstring& group, const int64_t totalSize)
		{
			m_refresh_time_ = time(NULL);
			CListUI* listView = getCurrentList();
			if ( !listView || listView->GetName() != ControlNames::TRANSTASK_LISTVIEW ) return;

			TransTaskListContainerElement* item = getItemByGroup(group);
			if( !item )
			{
				addItem( group );
				return ;
			}

			int64_t lastTotalSize = item->nodeData.size;
			item->nodeData.size = totalSize;

			// set size
			CLabelUI* size = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_SIZE));
			if (size == NULL) return;
			//size->SetShowHtml(true);
			size->SetVisible(false);
			size->SetText(SD::Utility::String::format_string(L"%s / %s", 
				SD::Utility::String::getSizeStr(item->nodeData.transedSize).c_str(), 
				SD::Utility::String::getSizeStr(item->nodeData.size).c_str()).c_str());
			size->SetVisible(true);
			// set progress	
			int32_t iProgress = (item->nodeData.size==0)?0:
				(int32_t)(((float)item->nodeData.transedSize)/item->nodeData.size*100);
			if ( 100 == iProgress )
			{
				iProgress = 99;
			}
			item->SetValue(iProgress);

		}

		CControlUI* createTransTaskListItem(const AsyncTransStatus type)
		{
			CDialogBuilder builder;
			if ( ATS_Complete == type )
			{
				return static_cast<CControlUI*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_TRANSTASKCOMPLETE_ITEM, 
					L"", 
					DialogBuilderCallbackImpl::getInstance(), 
					&paintManager_, 
					NULL));		
			}
			else
			{
				return static_cast<CControlUI*>(
					builder.Create(Onebox::ControlNames::SKIN_XML_TRANSTASK_ITEM, 
					L"", 
					DialogBuilderCallbackImpl::getInstance(), 
					&paintManager_,
					NULL));	 
			}			
		}

		void deleteTaskItem(const std::wstring& groupid)
		{
			boost::mutex::scoped_lock lock(mutex_);
			SetTransfersListTipSelf( -1 );

			CListUI* listView = getCurrentList();
			if( NULL == listView || listView->GetName() != ControlNames::TRANSTASK_LISTVIEW ) return;

			TransTaskListContainerElement* item = getItemByGroup(groupid);
			if ( NULL != item )
			{
				listView->RemoveAt(item->GetIndex());
			}

			// if all the task is complete, set the total progress to zero
			if ( 0 == listView->GetCount() )
			{
				setUploadBtnEnabled(false);
				listView->SetVisible(false);
			}
		}

		void deleteCompleteTaskItem(const std::wstring& groupid)
		{
			CListUI* listCompleteView = getList(1);
			if( !listCompleteView ) return;

			TransTaskListContainerElement* item = NULL;
			if ( listCompleteView->GetCount() > 0 )
			{
				for (int i = 0; i < listCompleteView->GetCount(); i++)
				{
					item = static_cast<TransTaskListContainerElement*>(listCompleteView->GetItemAt(i));
					if (NULL == item)
					{
						continue;
					}
					// remove the whole group
					if (item->nodeData.group == groupid)
					{
						listCompleteView->RemoveAt(i);
						break;
					}
				}
			}

			// if all the task is complete, set the total progress to zero
			if (0 == listCompleteView->GetCount())
			{
				setUploadBtnEnabled(false);
				listCompleteView->SetVisible(false);
			}
		}

		void SetDescribeForStatus( TransTaskListContainerElement* item )
		{
			if ( NULL == item)
			{
				return;
			}
			// set progress
			int32_t iProgress = (item->nodeData.size==0) ? 0:(int32_t)(((float)item->nodeData.transedSize)/item->nodeData.size*100);
			if ( 100 == iProgress )
			{
				iProgress = 99;
			}

			if (iProgress >= 0 && iProgress <= 100)
			{
				item->SetValue(iProgress);
			}

			CLabelUI* clbstatus_describe = static_cast<CProgressUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
			if(NULL == clbstatus_describe||NULL==userContext_||NULL==userContext_->getAsyncTaskMgr()) 
			{
				return;
			}
			clbstatus_describe->SetShowHtml(true);
			std::wstring status_describe = L"";
			int32_t errortotal = 0;
			if ( ATT_Upload == item->nodeData.type )
			{
				CButtonUI* btntranstype = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_TYPE));
				btntranstype->SetBkImage(IC_OPER_TRANSFER_UPLOAD_PATH);
				switch ( item->nodeData.status )
				{
				case ATS_Running:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADING);
						clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_RUNNING_FORMAT, 
							status_describe.c_str(), iProgress).c_str());
						break;
					}
				case ATS_Complete:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOADED);
						clbstatus_describe->SetText(status_describe.c_str());
						break;
					}
				case ATS_Error:
					{
						if ( FILE_TYPE_FILE == item->nodeData.fileType)
						{
							errortotal = 1;
						}
						else
						{
							errortotal = userContext_->getAsyncTaskMgr()->getErrorTasksCount(item->nodeData.group);
						}
						if ( errortotal > 0 ) SetErrorInfo(item, errortotal);
						break;
					}
				case ATS_Cancel:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOAD_PAUSED);
						clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_INFO_FORMART, 
							status_describe.c_str()).c_str());
						break;
					}
				case ATS_Waiting:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_UPLOAD_WAITING);
						clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_INFO_FORMART, 
							status_describe.c_str()).c_str());
						break;
					}
				default:
					break;
				}
			}
			else if ( ATT_Download == item->nodeData.type )
			{

				CButtonUI* btntranstype = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_TYPE));
				if ( NULL == btntranstype ) return ;
				btntranstype->SetBkImage(IC_OPER_TRANSFER_DOWNLOAD_PATH);
				switch ( item->nodeData.status )
				{
				case ATS_Running:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_DOWNLOADING);
						clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_RUNNING_FORMAT, 
							status_describe.c_str(), iProgress).c_str());
						break;
					}
				case ATS_Complete:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_DOWNLOADED);
						clbstatus_describe->SetText(status_describe.c_str());
						break;
					}
				case ATS_Error:
					{
						if ( FILE_TYPE_FILE == item->nodeData.fileType)
						{
							errortotal = 1;
						}
						else
						{
							errortotal = userContext_->getAsyncTaskMgr()->getErrorTasksCount(item->nodeData.group);
						}
						if( errortotal > 0 )SetErrorInfo(item, errortotal);
						break;
					}
				case ATS_Cancel:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_DOWNLOAD_PAUSED);
						clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_INFO_FORMART, 
							status_describe.c_str()).c_str());
						break;
					}
				case ATS_Waiting:
					{
						status_describe = iniLanguageHelper.GetCommonString(TRANSTASK_STATUS_DOWNLOAD_WAITING);
						clbstatus_describe->SetText(SD::Utility::String::format_string(STATUS_DESCRIBE_INFO_FORMART, 
							status_describe.c_str()).c_str());
						break;
					}
				default:
					break;
				}
			}
		}

		void addItem( const std::wstring& group )
		{
			CListUI* listView = getList(0);
			if( !listView ) return;
			if( listView->GetCount() >= pageLimit_ ) return;

			AsyncTransRootNode node;
			userContext_->getAsyncTaskMgr()->getTask(group, node);
			if ( !node || !node.get() ) return;

			UITransTaskRootNode rootnode;
			rootnode = *node;
			addItem(rootnode, listView->GetCount());

			SetTransfersListTipSelf( 0 );
			int32_t transcount = userContext_->getAsyncTaskMgr()->getTasksCount(AsyncTransType(ATT_Download|ATT_Upload));
			SetTransfersListTipSelf( transcount );
		}

		bool addItem(const UITransTaskRootNode& node, int index)
		{
			boost::mutex::scoped_lock lock(mutex_);
			TransTaskListContainerElement* newitem = getItemByGroup( node.group );
			if ( !newitem ) 
			{
				CListUI* listView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_LISTVIEW));
				if ( !listView ) return false;

				newitem = static_cast<TransTaskListContainerElement*>(createTransTaskListItem(ATS_Waiting));


				if (index < 0) index = 0;

				if ( !listView->AddAt(newitem, index) )
				{
					return false;
				}

				if (listView->GetCount() > 0 )
				{
					CVerticalLayoutUI* tip = NULL;
					tip = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_UPLOAD_TIP_TEXT ) );

					if( tip )
					{
						setUploadBtnEnabled(true);
					}
				}
			}

			newitem->nodeData = node;
			// set status icon
			// set icon
			CControlUI* icon = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(newitem, ControlNames::TRANSTASK_ITEM_ICON));
			if (icon == NULL) return false;
			std::wstring strIconPath = SkinConfMgr::getInstance()->getIconPath(node.fileType, node.name);
			icon->SetBkImage(strIconPath.c_str());

			// set name
			CLabelUI* name = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(newitem, ControlNames::TRANSTASK_ITEM_NAME));
			if (name == NULL) return false;
			name->SetText(node.name.c_str());
			name->SetToolTip(node.name.c_str());

			// set size
			CLabelUI* size = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(newitem, ControlNames::TRANSTASK_ITEM_SIZE));
			if (size == NULL) return false;
			size->SetText(SD::Utility::String::format_string(L"%s / %s", 
				SD::Utility::String::getSizeStr(node.transedSize).c_str(), 
				SD::Utility::String::getSizeStr(node.size).c_str()).c_str());

			CLabelUI* clbstatus_describe = static_cast<CProgressUI*>(paintManager_.FindSubControlByName(newitem, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
			if (clbstatus_describe) clbstatus_describe->SetEnabled(false);

			SetDescribeForStatus( newitem );
			setItemResumeAndPauseButtonStatus(newitem);

			return true;
		}

		bool addCompleteItem(const UITransTaskRootNode& node, int index = -1)
		{
			CListUI* listCompleteView = getList(1);
			if ( !listCompleteView ) return false;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(createTransTaskListItem(ATS_Complete));
			if ( !item ) return false;

			item->nodeData = node;

			// set icon
			CControlUI* icon = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEMCOMPLETE_ICON));
			if (icon == NULL) return false;
			std::wstring strIconPath = SkinConfMgr::getInstance()->getIconPath(node.fileType, node.name);
			icon->SetBkImage(strIconPath.c_str());			

			//set name
			CLabelUI* name = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEMCOMPLETE_NAME));
			if (name == NULL) return false;
			name->SetText(node.name.c_str());
			name->SetToolTip(node.name.c_str());

			CButtonUI* btnlink = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEMCOMPLETE_LINK));
			if ( !btnlink ) return false;

			if (item->nodeData.type == ATT_Download )
			{
				btnlink->SetNormalImage(IC_OPER_TRANSFER_DOWNLOAD_LINK);
			}
			else
			{
				btnlink->SetNormalImage(IC_OPER_TRANSFER_UPLOAD_LINK);
			}
			//set name
			CTextUI* filesize = static_cast<CTextUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEMCOMPLETE_SIZE));
			if ( !filesize ) return false;			
			filesize->SetText(SD::Utility::String::getSizeStr(node.size).c_str());

			CButtonUI* btntranstype = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEMCOMPLETE_TYPE));
			if ( NULL == btntranstype ) return false;

			if ( ATT_Upload == item->nodeData.type )
			{
				btntranstype->SetBkImage(IC_OPER_TRANSFER_UPLOAD_PATH);
			}
			else if ( ATT_Download == item->nodeData.type )
			{
				btntranstype->SetBkImage(IC_OPER_TRANSFER_DOWNLOAD_PATH);
			}

			CLabelUI* taskcompletetime = static_cast<CLabelUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEMCOMPLETE_TIME));
			if (taskcompletetime == NULL) return false;

			std::wstring scompleterdatetime = L"";
			int32_t ilanguage = iniLanguageHelper.GetLanguage();
			if ( UI_LANGUGE::CHINESE == ilanguage)
			{
				scompleterdatetime = SD::Utility::DateTime::getTime(node.completeTime, SD::Utility::UtcType::Crt, SD::Utility::LanguageType::CHINESE);
			}
			else
			{
				scompleterdatetime = SD::Utility::DateTime::getTime(node.completeTime, SD::Utility::UtcType::Crt, SD::Utility::LanguageType::ENGLISH);
			}
			scompleterdatetime = SD::Utility::String::replace_all(scompleterdatetime, L"//", L"-");
			taskcompletetime->SetText(scompleterdatetime.c_str());

			if ( index < 0 ) index = 0;

			{
				boost::mutex::scoped_lock lock(mutex_);
				if (!listCompleteView->AddAt(item, index))
				{
					return false;
				}	
			}

			return true;
		}

		void showItemMessage(TransTaskListContainerElement* item)
		{
			if (NULL == item)
			{
				return;
			}
		}

		void loadCompleteData(Page& page)
		{
			CListUI* listCompleteView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_LISTVIEW));
			if( !listCompleteView ) return;

			// get children nodes
			AsyncTransCompleteNodes nodes;
			{
				if (RT_OK != userContext_->getAsyncTaskMgr()->getHistoricalTasks( nodes, page ) || 
					nodes.empty())
				{
					setCompleteClearBtnEnabled( 0 != listCompleteView->GetCount() );
					return;
				}
			}

			int index = listCompleteView->GetCount();
			size_t count = 1;

			for (AsyncTransCompleteNodes::iterator itor = nodes.begin(); itor != nodes.end(); ++itor)
			{
				UITransTaskRootNode node;
				node = *(*itor);

				if ( !addCompleteItem( node, listCompleteView->GetCount() ) )
				{
					return;
				}
			}

			setCompleteClearBtnEnabled( 0 != listCompleteView->GetCount() );
		}

		void loadDataForDel()
		{
			CListUI* listView = getCurrentList( );
			int icount = listView->GetCount();

			if ( listView 
				&& listView->GetName() == ControlNames::TRANSTASK_LISTVIEW
				&& listView->GetCount() < pageLimit_ 
				&& GetTransNumber() != listView->GetCount() )
			{
				currentPage_.offset += 10;
				loadData(currentPage_);
			}
		}

		void loadData(Page& page)
		{
			CListUI* listView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_LISTVIEW));
			if( !listView ) return;
			// get children nodes
			AsyncTransRootNodes nodes;
			{
				if (RT_OK != userContext_->getAsyncTaskMgr()->getTasks( AsyncTransType(ATT_Upload|ATT_Download), nodes, page ) || 
					nodes.empty())
				{
					SetTransfersListTip(0);
					listView->RemoveAll();
					setUploadBtnEnabled( false );

					return;
				}
			}

			int index = listView->GetCount();
			size_t count = 1;
			
			for (AsyncTransRootNodes::iterator itor = nodes.begin(); itor != nodes.end(); ++itor)
			{
				UITransTaskRootNode node;
				node = *(*itor);

				if ( !addItem( node, listView->GetCount() ) )
				{
					return;
				}
			}

			setUploadBtnEnabled( 0 != listView->GetCount() );
			return;
		}
		

		void itemCompleteDoubleClick(TNotifyUI& msg)
		{
			if (msg.pSender == NULL) return;
			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(msg.pSender);
			if (NULL == item) return;

			CListUI* lisview = getList(1);
			if(!lisview) return;

			std::wstring path = L"";
			std::wstring pathDir = L" /select,";

			if( ATT_Download == item->nodeData.type)
			{
				path = item->nodeData.parent;
				if( SD::Utility::FS::is_local_root(path) )
				{
					path += item->nodeData.name;
				}
				else
				{
					path += PATH_DELIMITER + item->nodeData.name;
				}
			}
			else
			{
				path = item->nodeData.source;
			}

			(void)ShellExecute(NULL,L"open",L"explorer.exe",path.c_str(),NULL,SW_SHOWNORMAL);
		}

		void TransTaskTabSelectChanged(TNotifyUI& msg)
		{
			SetTransfersListTip( 0 );
			int32_t transcount = userContext_->getAsyncTaskMgr()->getTasksCount(AsyncTransType(ATT_Download|ATT_Upload));
			SetTransfersListTip(transcount);

			static CTabLayoutUI *pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_TABLE));
			if ( pControl ) pControl->SelectItem(0);
			
			CListUI* listView = getList(0);

			listView->RemoveAll();
			SIZE scrl;
			scrl.cy = 0;
			scrl.cx = 0;
			listView->SetScrollPos(scrl);

			currentPage_.start = 0;
			currentPage_.offset = pageLimit_;
			loadData(currentPage_);

			setUploadBtnEnabled( listView && listView->GetCount() > 0 );
		}

		void TransTaskCompleteTabSelectChanged(TNotifyUI& msg)
		{
			static CTabLayoutUI *pControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_TABLE));
			if ( pControl ) pControl->SelectItem(1);		

			CListUI* listCompleteView = getList(1);

			listCompleteView->RemoveAll();
			SIZE scrl;
			scrl.cy = 0;
			scrl.cx = 0;
			listCompleteView->SetScrollPos(scrl);

			currentCompletePage_.start = 0;
			currentCompletePage_.offset = pageLimit_;
			loadCompleteData(currentCompletePage_);
		}

		void errorStateDescrib(TNotifyUI& msg)
		{
			if(NULL==msg.pSender->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>
				(msg.pSender->GetParent()->GetParent()->GetParent()->GetParent());
			if (item == NULL) return;

			::PostMessage(paintManager_.GetPaintWindow(), WM_CUSTOM_TRANSTASK_ITEMERRORSTATECLICK, (WPARAM)msg.pSender, NULL);
		}

		void clearAllCompleteClick(TNotifyUI& msg)
		{
			static CListUI* listCompleteView = static_cast<CListUI*>( paintManager_.FindControl( ControlNames::TRANSTASK_COMPLETE_LISTVIEW ) );
			if ( !listCompleteView ) return;

			if ( RT_OK != userContext_->getAsyncTaskMgr()->deleteHistoricalTask( ) )
			{
				return;
			}
			listCompleteView->RemoveAll();

			setCompleteClearBtnEnabled(false);		

			static CTextUI* tip = static_cast<CTextUI*>(paintManager_.FindControl( ControlNames::TRANSTASK_COMPLETE_TIP_TEXT ));
			if (NULL == tip) return;

			tip->SetVisible(0 == listCompleteView->GetCount());
			listCompleteView->SetVisible(0 != listCompleteView->GetCount());
		}

		void itemCompleteOpenFileClick(TNotifyUI& msg)
		{
			if (msg.pSender->GetParent() == NULL) return;
			if (msg.pSender->GetParent()->GetParent() == NULL) return;
			if (msg.pSender->GetParent()->GetParent()->GetParent() == NULL) return;
			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent()->GetParent());
			if (NULL == item) return;

			std::wstring path = L"";
			std::wstring pathDir = L" /select,";
			pathDir += path;

			if( ATT_Download == item->nodeData.type )
			{
				path = item->nodeData.parent;
				if (SD::Utility::FS::is_exist(path))
				{	
					if( SD::Utility::FS::is_local_root( path) )
					{
						path += item->nodeData.name;
					}
					else
					{
						path += PATH_DELIMITER + item->nodeData.name;
					}
					
					path = pathDir + path;
					(void)ShellExecute(NULL,L"open",L"explorer.exe",path.c_str(),NULL,SW_SHOWNORMAL);
				}
			}
			else if( ATT_Upload == item->nodeData.type )
			{
				switch (item->nodeData.userType)
				{
				case UserContext_User:
					{
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_MYFILE_FOR_NAME,
							SD::Utility::String::type_to_string<std::wstring>(item->nodeData.parent)
							,item->nodeData.name));
					}
					break;
				case UserContext_Teamspace:
					{
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_TEAMSPACE_FOR_NAME,
							SD::Utility::String::type_to_string<std::wstring>(item->nodeData.parent)
							,item->nodeData.name
							,SD::Utility::String::type_to_string<std::wstring>(item->nodeData.userId)
							, item->nodeData.userName));
					}
					break;
				case UserContext_ShareUser:
					{
						userContext_->getNotifyMgr()->notify(NOTIFY_PARAM(NOTIFY_MSG_GOTO_SHARE2ME_FOR_NAME,
							SD::Utility::String::type_to_string<std::wstring>(item->nodeData.parent)
							,item->nodeData.name));
					}
					break;
				case UserContext_Group:
					{

					}
					break;
				default:
					break;
				}
			}
		}

		void itemCompleteDeleteClick(TNotifyUI& msg)
		{
			if (msg.pSender->GetParent() == NULL) return;
			if (msg.pSender->GetParent()->GetParent() == NULL) return;
			if (msg.pSender->GetParent()->GetParent()->GetParent() == NULL) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(msg.pSender->GetParent()->GetParent()->GetParent()->GetParent());
			if ( !item ) return;

			CListUI* listCompleteView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_LISTVIEW));
			if ( !listCompleteView ) return;

			if (RT_OK != userContext_->getAsyncTaskMgr()->deleteHistoricalTask(item->nodeData.group))
			{
				return;
			}

			// remove UI element (all children)
			int startIndex = item->GetIndex();
			
			listCompleteView->RemoveAt(startIndex);

			if (0 == listCompleteView->GetCount())
			{
				setCompleteClearBtnEnabled(false);
			}

			static CTextUI* tip = static_cast<CTextUI*>(paintManager_.FindControl(L"transTask_complete_tip_text"));
			if (NULL == tip) return;

			tip->SetVisible( 0 == listCompleteView->GetCount() );
			listCompleteView->SetVisible( 0 != listCompleteView->GetCount() );
		}

		void TransTaskPauseAllClick(TNotifyUI& msg)
		{
			static CButtonUI* btnPause = static_cast<CButtonUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_UPLOAD_PAUSEALL));
			if (NULL == btnPause) return;

			static CButtonUI* btnResume = static_cast<CButtonUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_UPLOAD_RESUMEALL));
			if (NULL == btnResume) return;

			// cancel async task
			int32_t ret = userContext_->getAsyncTaskMgr()->pauseTask( AsyncTransType(ATT_Upload | ATT_Download) );
			if (RT_OK != ret)
			{
				return;
			}

			btnResume->SetVisible();
			btnPause->SetVisible(false);

			// update UI elements
			CListUI* listView = getList(0);
			TransTaskListContainerElement* item = NULL;
			if (NULL == listView) return;

			CLabelUI* speed = NULL, *leftTime = NULL;

			for (int i = 0; i < listView->GetCount(); ++i)
			{
				item = static_cast<TransTaskListContainerElement*>(listView->GetItemAt(i));
				if (NULL == item) return;

				if ( item->nodeData.transedSize == item->nodeData.size ) continue;

				if ( (item->nodeData.status&ATS_Error) == ATS_Error
					|| item->nodeData.status == ATS_Complete )
				{
					SetDescribeForStatus(item);
					continue;
				}
				// set button status
				item->nodeData.status = ATS_Cancel;

				setItemResumeAndPauseButtonStatus(item);

				CLabelUI* clbstatus_describe = static_cast<CProgressUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
				clbstatus_describe->SetShowHtml(true);

				SetDescribeForStatus(item);
			}
		}

		void TransTaskResumeAllClick(TNotifyUI& msg)
		{
			static CButtonUI* btnResume = static_cast<CButtonUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_UPLOAD_RESUMEALL));
			if (NULL == btnResume) return;

			static CButtonUI* btnPause = static_cast<CButtonUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_UPLOAD_PAUSEALL));
			if (NULL == btnPause) return;

			// cancel async task
			int32_t ret = userContext_->getAsyncTaskMgr()->resumeTask( AsyncTransType(ATT_Upload | ATT_Download) );
			if (RT_OK != ret)
			{
				return;
			}
			
			btnResume->SetVisible(false);
			btnPause->SetVisible(true);

			// update UI elements
			CListUI* listView = getList(0);
			if (NULL == listView) return;

			TransTaskListContainerElement* item = NULL;

			for (int i = 0; i < listView->GetCount(); ++i)
			{
				item = static_cast<TransTaskListContainerElement*>(listView->GetItemAt(i));
				if ( !item ) return;
				
				if ( item->nodeData.transedSize == item->nodeData.size ) continue;

				item->nodeData.status = ATS_Waiting;

				setItemResumeAndPauseButtonStatus(item);

				SetDescribeForStatus(item);
			}
		}

		void TransTaskCancelAllClick(TNotifyUI& msg)
		{
			if( !m_noticeFrame_ ) return;
			m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
			if ( !m_noticeFrame_->IsClickOk() ) return;

			int32_t ret = userContext_->getAsyncTaskMgr()->deleteTask( AsyncTransType(ATT_Upload | ATT_Download) );
			if (RT_OK != ret)
			{
				return;
			}

			// update UI elements
			CListUI* listView = getList(0);
			if (NULL == listView) return;

			if ( listView->GetName() == ControlNames::TRANSTASK_LISTVIEW )
			{
				SetTransfersListTip( 0 );
			}

			listView->RemoveAll();

			setUploadBtnEnabled(false);
			listView->SetVisible(false);

		}

		void itemResumeClick(TNotifyUI& msg)
		{
			if(NULL==msg.pSender->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(
				msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
			if ( !item ) return;

			//if ( item->nodeData.transedSize == item->nodeData.size ) return;

			int32_t ret = RT_INVALID_PARAM;

			ret = userContext_->getAsyncTaskMgr()->resumeTask(item->nodeData.group);
			
			if (RT_OK != ret)
			{
				return;
			}
			msg.pSender->SetVisible(false);

			// set button status
			item->nodeData.status = ATS_Waiting;
			setItemResumeAndPauseButtonStatus(item);
			SetDescribeForStatus( item );
		}

		void itemPauseClick(TNotifyUI& msg)
		{
			if(NULL==msg.pSender->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(
				msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
			if ( !item ) return;

			if ( item->nodeData.transedSize == item->nodeData.size ) return;

			int32_t ret = userContext_->getAsyncTaskMgr()->pauseTask(item->nodeData.group);
			if (RT_OK != ret)
			{
				return;
			}
			msg.pSender->SetVisible(false);

			// set button status
			item->nodeData.status = ATS_Cancel;
			setItemResumeAndPauseButtonStatus(item);

			SetDescribeForStatus(item);
		}

		void itemDeleteClick(TNotifyUI& msg)
		{
			if( !m_noticeFrame_ ) return;
			m_noticeFrame_->Run(Choose,Ask,MSG_DELCONFIRM_TITLE_KEY,MSG_DELCONFIRM_NOTICE_KEY,Modal);
			if (!m_noticeFrame_->IsClickOk()) return;

			if(NULL==msg.pSender->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(
				msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
			if ( !item ) return;

			int32_t ret = userContext_->getAsyncTaskMgr()->deleteTask(item->nodeData.group);
			if (RT_OK == ret)
			{

				deleteTaskItem(item->nodeData.group);

				loadDataForDel();
			}
		}

		void itemErrorClick(TNotifyUI& msg)
		{
			if(NULL==msg.pSender->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(
				msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
			if ( !item ) return;

			showItemMessage(item);
		}
		
		void itemAbnormalClick(TNotifyUI& msg)
		{
			if(NULL==msg.pSender->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()) return;
			if(NULL==msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(
				msg.pSender->GetParent()->GetParent()->GetParent()->GetParent()->GetParent());
			if ( !item ) return;

			showItemMessage(item);
		}

		void nextPage(TNotifyUI& msg)
		{
			int32_t range = msg.wParam;
			int32_t pos = msg.lParam;

			int32_t irootcount = userContext_->getAsyncTaskMgr()->getTasksCount(AsyncTransType(ATT_Download|ATT_Upload));

			if( ( currentPage_.start + pageLimit_ > irootcount) 
				|| (pos < range - pageLimit_) ) 
			{
				return;
			}

			currentPage_.offset += pageLimit_;

			loadData(currentPage_);
		}

		void nextCompletePage(TNotifyUI& msg)
		{
			int32_t range = msg.wParam;
			int32_t pos = msg.lParam;

			if( ( pos < range - pageLimit_) ) return;

			currentCompletePage_.start += currentCompletePage_.offset;
			currentCompletePage_.offset += pageLimit_;

			loadCompleteData(currentCompletePage_);
		}

		CListUI* getCurrentList(CTabLayoutUI *pControl = NULL)
		{
			CTabLayoutUI* pMainControl = static_cast<CTabLayoutUI*>(paintManager_.FindControl(_T("tb_listView")));
			int32_t tbselindex = pMainControl->GetCurSel();
			if( 4 != tbselindex ) return NULL;

			if (NULL == pControl)
			{
				static CTabLayoutUI *control = static_cast<CTabLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_TABLE));
				pControl = control;
				if (NULL == pControl)
				{
					return NULL;
				}
			}

			CListUI* listView = NULL;
			int index = pControl->GetCurSel();
			switch (index)
			{
			case 0:
				listView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_LISTVIEW));
				break;
			case 1:
				listView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_LISTVIEW));
				break;
			default:
				break;
			}
			return listView;
		}

		CListUI* getList( int index )
		{
			static CTabLayoutUI *control = static_cast<CTabLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_TABLE));
			if (NULL == control) return NULL;

			CListUI* listView = NULL;
			switch (index)
			{
			case 0:
				listView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_LISTVIEW));
				break;
			case 1:
				listView = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_LISTVIEW));
				break;
			default:
				break;
			}
			return listView;
		}

		TransTaskListContainerElement* getItemByGroup( const std::wstring& group )
		{
			CListUI* listView = getList(0);
			if ( !listView ) return NULL;

			TransTaskListContainerElement* item = NULL;
			for (int i = 0; i < listView->GetCount(); ++i)
			{
				item = static_cast<TransTaskListContainerElement*>(listView->GetItemAt(i));
				if ( !item ) continue;

				if ( item->nodeData.group == group )
				{
					return item;
				}
			}
			return NULL;
		}

		void setItemResumeAndPauseButtonStatus(TransTaskListContainerElement* item)
		{
			if (NULL == item)
			{
				return;
			}

			CButtonUI* btnItemResume = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_RESUME));
			if (NULL == btnItemResume) return;
			CButtonUI* btnItemPause = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_PAUSE));
			if (NULL == btnItemPause) return;
			CButtonUI* btnItemDescribe = static_cast<CButtonUI*>(paintManager_.FindSubControlByName(item, ControlNames::TRANSTASK_ITEM_STATUS_DESCRIBE));
			if (btnItemDescribe == NULL) return;

			if (item->nodeData.status&ATS_Error)
			{				
				btnItemDescribe->SetMouseEnabled(true);
			}
			else
			{
				btnItemDescribe->SetMouseEnabled(false);
			}

			// set resume status
			if (ATS_Cancel == item->nodeData.status 
				|| ATS_Error == item->nodeData.status )
			{
				btnItemResume->SetVisible(true);
				btnItemPause->SetVisible(false);
				if (ATT_Upload == item->nodeData.type)
				{
					btnItemResume->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_STARTUPLOAD_KEY).c_str());
				}
				else
				{
					btnItemResume->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_STARTDOWNLOAD_KEY).c_str());
				}
			}
			else
			{
				btnItemPause->SetVisible(true);
				btnItemResume->SetVisible(false);
				if (ATT_Upload == item->nodeData.type)
				{
					btnItemPause->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_PAUSEUPLOAD_KEY).c_str());
				}
				else
				{
					btnItemPause->SetToolTip(iniLanguageHelper.GetCommonString(COMMENT_PAUSEDOWNLOAD_KEY).c_str());
				}
			}
		}

		void addOpenTaskId(std::wstring& group)
		{
			boost::mutex::scoped_lock lock(mutex_);
			openList_.push_back(group);
		}

		void setUploadBtnEnabled(bool bFlag)
		{
			static CHorizontalLayoutUI* body = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_BODY));
			if (NULL == body) return;
			body->SetVisible(bFlag);

			static CControlUI* line = static_cast<CControlUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_LINE_HEADER));
			if ( NULL == line ) return;
			line->SetVisible(bFlag);

			static CListUI* lisview = getList(0);
			if (lisview)
			{
				lisview->SetVisible(bFlag);
			}
			CVerticalLayoutUI* tip = NULL;
			tip = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_UPLOAD_TIP_TEXT));
			if ( NULL != tip )
			{
				tip->SetVisible( !bFlag );
			}
		}

		void setCompleteClearBtnEnabled(bool bFlag)
		{
			static CHorizontalLayoutUI* body = static_cast<CHorizontalLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_BODY));
			if (NULL == body) return;
			body->SetVisible(bFlag);	

			static CControlUI* line = static_cast<CControlUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_LINE_HEADER));
			if ( NULL == line ) return;
			line->SetVisible(bFlag);

			static CListUI* listview = static_cast<CListUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_LISTVIEW));
			if( !listview ) return;
			listview->SetVisible(bFlag);

			CVerticalLayoutUI* tip = NULL;
			tip = static_cast<CVerticalLayoutUI*>(paintManager_.FindControl(ControlNames::TRANSTASK_COMPLETE_TIP_TEXT));
			if ( NULL != tip )
			{
				tip->SetVisible( !bFlag );
			}
		}

		int64_t GetTransNumber()
		{
			return m_iTransNumber;
		}

		void SetTransNumber(int64_t transnumber)
		{
			m_iTransNumber = transnumber;
		}
		
		void RefreshTransTaskStatus()
		{
			while ( m_stop_ )
			{
				boost::this_thread::sleep( boost::posix_time::seconds( TRANSFER_TASK_REFRESH_SLEEP_TIME ) );
				if ( (time(NULL) - m_refresh_time_) > TRANSFER_TASK_REFRESH_TIME )
				{
					CListUI* listView = getCurrentList( );
					if( !listView || listView->GetName() != ControlNames::TRANSTASK_LISTVIEW ) continue;

					int index = 0;
					int icount = listView->GetCount();

					AsyncTransRootNode transnode;
					for( int i=0; i<icount; i++)
					{
						TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>(listView->GetItemAt(i));
						if( NULL == item ) continue;

						int ret = userContext_->getAsyncTaskMgr()->getTask( item->nodeData.group, transnode);

						if( !transnode || !transnode.get() )
						{
							listView->RemoveAt(i);
							SetTransfersListTip(-1);
							i--;
							icount--;
						}
						else if( ATS_Running != transnode->status )
						{
							updateTaskStatus( transnode->group, transnode->status );
						}
					}
					setUploadBtnEnabled( 0 != listView->GetCount() );
					m_refresh_time_ = time(NULL);
				}
			}

		}

		void dowloadsthread(int32_t& ret, AsyncDownloadTaskParamExs& downloadTasks, const std::list<UIFileNode>* nodes, const std::wstring& localpath, bool openAfterComplete = false)
		{
			int64_t userId = nodes->front().userContext->id.id;
			for (std::list<UIFileNode>::const_iterator it = nodes->begin(); it != nodes->end(); ++it)
			{
				AsyncDownloadTaskParamEx downloadtask(new st_AsyncDownloadTaskParamEx);
				if( !downloadtask && !downloadtask.get() ) continue;

				if (NULL != it->userContext)
				{
					std::wstring group = SD::Utility::String::gen_uuid();

					downloadtask->group = group;
					downloadtask->size = (it->basic.type == FILE_TYPE_FILE) ? it->basic.size : 0;
					downloadtask->localParentPath = localpath;
					downloadtask->name = it->basic.name;
					downloadtask->fileType = (FILE_TYPE)it->basic.type;
					downloadtask->remoteId = it->basic.id;

					downloadTasks.push_back(downloadtask);
				}
			}

			if( downloadTasks.size() > 0 )
			{
				UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
				if(NULL==defaultUserContext)
				{
					ret = RT_ERROR;
					return;
				}
				ret = defaultUserContext->getAsyncTaskMgr()->downloads(downloadTasks, ATT_Download, userId);
			}
		}

		void uploadsthread(int32_t& ret, AsyncUploadTaskParamExs& uploadTasks, const std::list<std::wstring>* nodes, int64_t userId, int64_t remoteParentId)
		{
			for (std::list<std::wstring>::const_iterator it = nodes->begin(); it != nodes->end(); ++it)
			{
					AsyncUploadTaskParamEx uploadTask( new st_AsyncUploadTaskParamEx);

					uploadTask->group = SD::Utility::String::gen_uuid();
					uploadTask->localPath = *it;
					uploadTask->fileType = SD::Utility::FS::is_directory(*it)?FILE_TYPE_DIR:FILE_TYPE_FILE;
					uploadTask->size = (uploadTask->fileType==FILE_TYPE_FILE)?SD::Utility::FS::get_file_size(uploadTask->localPath):0;
					uploadTask->remoteParentId = remoteParentId;

					uploadTasks.push_back(uploadTask);
			}

			if (uploadTasks.size() > 0)
			{
				UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
				if(NULL==defaultUserContext)
				{
					ret = RT_ERROR;
					return;
				}
				ret = defaultUserContext->getAsyncTaskMgr()->uploads(uploadTasks, ATT_Upload, userId);
			}
		}

		void Release()
		{
			if( NULL != m_noticeFrame_ )
			{
				delete m_noticeFrame_;
				m_noticeFrame_ = NULL;
			}
			m_stop_ = false;
			thread_.interrupt();
			thread_.join();
		}

		virtual void itemErrorStateClick(CControlUI* pItem)
		{
			if(NULL==pItem) return;
			if(NULL==pItem->GetParent()) return;
			if(NULL==pItem->GetParent()->GetParent()) return;
			if(NULL==pItem->GetParent()->GetParent()->GetParent()) return;
			if(NULL==pItem->GetParent()->GetParent()->GetParent()->GetParent()) return;

			TransTaskListContainerElement* item = static_cast<TransTaskListContainerElement*>
				(pItem->GetParent()->GetParent()->GetParent()->GetParent());
			if (item == NULL) return;

			std::wstring group = item->nodeData.group;
			TransTaskShowErrorDialog* pError = new TransTaskShowErrorDialog(userContext_, group, item->nodeData.fileType);
			pError->Create(paintManager_.GetPaintWindow(),_T("TransTaskShowErrorDialog"), UI_WNDSTYLE_DIALOG, WS_EX_WINDOWEDGE);
			pError->CenterWindow();
			(void)pError->ShowModal();
			delete pError;
			pError=NULL;

			AsyncTransRootNode rootnode;
			int32_t ret = userContext_->getAsyncTaskMgr()->getTask(group, rootnode);
			if ( rootnode && rootnode.get() )
			{
				AsyncTransStatus statustemp = ATS_Invalid;
				int32_t errortotal = userContext_->getAsyncTaskMgr()->getErrorTasksCount(group);
				if ( FILE_TYPE_FILE != rootnode->fileType 
					&& 0 == errortotal 
					&& ATS_Error == rootnode->status )
				{
					AsyncTransCompleteNode completeNode(new st_AsyncTransCompleteNode);
					if ( NULL != completeNode && NULL != completeNode.get() )
					{
						completeNode->group = rootnode->group;
						completeNode->source = rootnode->source;
						completeNode->parent = rootnode->parent;
						completeNode->name = rootnode->name;
						completeNode->type = rootnode->type;
						completeNode->fileType = rootnode->fileType;
						completeNode->userId = rootnode->userId;
						completeNode->userType = rootnode->userType;
						completeNode->userName = rootnode->userName;
						completeNode->size = rootnode->size;
					}
					ret = userContext_->getAsyncTaskMgr()->addHistoricalTask(completeNode);
					if( RT_OK == ret )
					{
						(void)userContext_->getAsyncTaskMgr()->deleteTask(group);
					}
					statustemp = ATS_Complete;
				}
				else
				{
					statustemp = rootnode->status;
				}

				item->nodeData.status = statustemp;
				updateTaskStatus(rootnode->group, item->nodeData.status);
				return;
			}
			else
			{
				CListUI* listview = getCurrentList();
				if( listview && listview->GetName() == ControlNames::TRANSTASK_LISTVIEW )
				{
					deleteTaskItem(group);
					loadDataForDel();
				}
				return;
			}
		}

	public:
		CPaintManagerUI& paintManager_;
		Page currentPage_;
		Page currentCompletePage_;

		UserContext* userContext_;

		std::map<std::wstring, call_func> funcMaps_;

		int32_t pageLimit_;
		std::list<std::wstring> openList_;

		boost::mutex mutex_;

	private:
		bool m_isInitData;

		int64_t m_refresh_time_;
		bool m_stop_;

		boost::thread thread_;

		NoticeFrameMgr* m_noticeFrame_;
	protected:
		int64_t m_iTransNumber;
	};

	TransTaskMgr* TransTaskMgr::instance_ = NULL;

	TransTaskMgr* TransTaskMgr::create(UserContext* context, CPaintManagerUI& paintManager)
	{
		if (NULL == instance_)
		{
			instance_ = new TransTaskMgrImpl(context, paintManager);
		}
		return instance_;
	}

	int32_t uploadImpl(TransTaskMgr *instance, const std::list<std::wstring> nodes, const UIFileNode& remoteParent)
	{
		int32_t ret = RT_OK;
		TransTaskMgrImpl* pTask = static_cast<TransTaskMgrImpl*>(instance);
		if(!pTask) return RT_ERROR;

		AsyncUploadTaskParamExs uploadTasks;
		CListUI* listView = pTask->getCurrentList( );

		CListUI* listmyfile = static_cast<CListUI*>(pTask->paintManager_.FindControl(L"myFile_listView"));
		RECT pos;		
		if (listmyfile)
		{
			pos = listmyfile->GetPos();
		}

		CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), pTask->paintManager_.GetPaintWindow(), pos, 
			boost::bind(&TransTaskMgrImpl::uploadsthread
			, pTask
			, boost::ref(ret)
			, boost::ref(uploadTasks)
			, &nodes
			, remoteParent.userContext->id.id
			, remoteParent.basic.id));	

		if( RT_OK == ret )
		{
			AsyncUploadTaskParamExs::iterator iter = uploadTasks.begin();
			for ( iter; iter!=uploadTasks.end(); iter++ )
			{
				if( listView 
					&& listView->GetName() == ControlNames::TRANSTASK_LISTVIEW 
					&& listView->GetCount() < pTask->pageLimit_ )
				{
					UITransTaskRootNode tasknode;

					tasknode.source = (*iter)->localPath;
					tasknode.parent = SD::Utility::String::type_to_string<std::wstring>((*iter)->remoteParentId);
					tasknode.group = (*iter)->group;
					tasknode.name = SD::Utility::FS::get_file_name((*iter)->localPath);
					tasknode.type = ATT_Upload;
					tasknode.fileType = (*iter)->fileType;
					tasknode.size = (*iter)->size;
					tasknode.status = ATS_Waiting;
					tasknode.userType = remoteParent.userContext->id.type;

					pTask->addItem(tasknode, listView->GetCount());
				}
				else
				{
					break;
				}
			}
			pTask->SetTransfersListTip( uploadTasks.size() );
		}
		return RT_OK;
	}

	int32_t TransTaskMgr::upload(const std::list<std::wstring>& nodes, const UIFileNode& remoteParent)
	{
		if(!nodes.empty())
		{
			(void)boost::thread(boost::bind(uploadImpl, instance_, std::move(nodes), remoteParent));
		}
		return RT_OK;
	}

	int32_t downloadImpl(TransTaskMgr *instance, const std::list<UIFileNode> nodes, const std::wstring& localpath, bool openAfterComplete = false)
	{
		if (nodes.empty())
		{
			return RT_OK;
		}

		int32_t ret = RT_OK;
		TransTaskMgrImpl* pTask = static_cast<TransTaskMgrImpl*>(instance);
		if(NULL == pTask) return RT_ERROR;
	
		AsyncDownloadTaskParamExs downloadTasks;
		CListUI* listmyfile = static_cast<CListUI*>(pTask->paintManager_.FindControl(L"myFile_listView"));
		RECT pos;		
		if (listmyfile)
		{
			pos = listmyfile->GetPos();
		}

		CommonLoadingFrame::create(iniLanguageHelper.GetSkinFolderPath(), pTask->paintManager_.GetPaintWindow(), pos, 
			boost::bind(&TransTaskMgrImpl::dowloadsthread
			, pTask
			, boost::ref(ret)
			, boost::ref(downloadTasks)
			, &nodes
			, localpath
			, openAfterComplete));	

		if ( RT_OK == ret )
		{
			CListUI* listView = pTask->getCurrentList( );
			if ( !listView ) ret = RT_ERROR;

			AsyncDownloadTaskParamExs::iterator iter = downloadTasks.begin();

			for ( iter; iter != downloadTasks.end(); iter++)
			{
				if (openAfterComplete)
				{
					pTask->addOpenTaskId( (*iter)->group );
				}

				if( listView 
					&& listView->GetName() == ControlNames::TRANSTASK_LISTVIEW 
					&& listView->GetCount() < pTask->pageLimit_ )
				{
					UITransTaskRootNode tasknode;
					tasknode.source = SD::Utility::String::type_to_string<std::wstring>((*iter)->remoteId);
					tasknode.parent = (*iter)->localParentPath;
					tasknode.group = (*iter)->group;
					tasknode.name = (*iter)->name;
					tasknode.size = (*iter)->size;
					tasknode.type = ATT_Download;
					tasknode.fileType = (*iter)->fileType;
					tasknode.status = ATS_Waiting;

					pTask->addItem( tasknode, listView->GetCount() );
				}
			}

			pTask->SetTransfersListTip( downloadTasks.size() );
		}
		return ret;
	}

	int32_t TransTaskMgr::download(const std::list<UIFileNode>& nodes, const std::wstring& localParent, bool openAfterComplete)
	{
		TransTaskMgrImpl* pTask = static_cast<TransTaskMgrImpl*>(instance_);
		if (NULL == pTask)
		{
			return RT_INVALID_PARAM;
		}
		std::list<UIFileNode> tmpNodes;
		bool flag = false;

		for(std::list<UIFileNode>::const_iterator it = nodes.begin(); it != nodes.end(); ++it)
		{
			if(!flag)
			{
				if(SD::Utility::FS::is_exist(localParent + PATH_DELIMITER + it->basic.name))
				{
					UIFileNode tmpNode;
					NoticeFrameMgr* noticeFrame = new NoticeFrameMgr(pTask->paintManager_.GetPaintWindow());
					noticeFrame->Run(Choose, Ask, L"", MSG_TRANSTASK_HASSAMEFOLDER_KEY, Modal);
					if(noticeFrame->IsClickOk())
					{
						flag = true;
						delete noticeFrame;
						noticeFrame = NULL;
					}
					else
					{
						delete noticeFrame;
						noticeFrame = NULL;
						return RT_OK;
					}
				}
			}
			
			if(SD::Utility::FS::is_exist(localParent + PATH_DELIMITER + it->basic.name))
			{
				UIFileNode tmpNode;
				if(flag)
				{
					int32_t suffix = 2;

					std::wstring fileName;
					std::wstring fileType;
					int32_t nPos = it->basic.name.find_last_of(L".");
					if (nPos != -1){
						fileName = it->basic.name.substr(0, nPos);
						fileType = it->basic.name.substr(nPos);
					}else{
						fileName = it->basic.name;
						fileType = L"";
					}
					std::wstring newTmpName = fileName + L" (2)" + fileType;
					std::wstring newItemPath = localParent + PATH_DELIMITER + newTmpName;
					while (SD::Utility::FS::is_exist(newItemPath))
					{
						newTmpName = fileName + SD::Utility::String::format_string(L" (%d)", suffix++) + fileType;
						newItemPath = localParent + PATH_DELIMITER + newTmpName;
					}
					tmpNode = *it;
					tmpNode.basic.name = newTmpName;
					tmpNodes.push_back(tmpNode);
				}
			}
			else
			{
				tmpNodes.push_back(*it);
			}
		}
		if( !tmpNodes.empty() )
		{
			(void)boost::thread(boost::bind(downloadImpl, instance_, tmpNodes, localParent, openAfterComplete));
		}
		return RT_OK;
	}

	int32_t TransTaskMgr::pauseAllTask()
	{
		int32_t ret = RT_OK;
		UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
		if(NULL==defaultUserContext)
		{
			ret = RT_ERROR;
			return ret;
		}
		ret = defaultUserContext->getAsyncTaskMgr()->pauseTask(AsyncTransType(ATT_Upload|ATT_Download));
		if (RT_OK != ret)
		{
			return ret;
		}
		TNotifyUI msg;
		TransTaskMgrImpl* pTask = static_cast<TransTaskMgrImpl*>(instance_);
		if (NULL != pTask)
		{
			pTask->TransTaskPauseAllClick( msg );
		}
		return ret;
	}

	int32_t TransTaskMgr::resumeAllTask()
	{
		int32_t ret = RT_OK;
		UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
		if(NULL==defaultUserContext)
		{
			ret = RT_ERROR;
			return ret;
		}
		ret = defaultUserContext->getAsyncTaskMgr()->resumeTask(AsyncTransType(ATT_Upload|ATT_Download));
		if (RT_OK != ret)
		{
			return ret;
		}
		TNotifyUI msg;
		TransTaskMgrImpl* pTask = static_cast<TransTaskMgrImpl*>(instance_);
		if (NULL != pTask)
		{
			pTask->TransTaskResumeAllClick( msg );
		}
		return ret;
	}
}

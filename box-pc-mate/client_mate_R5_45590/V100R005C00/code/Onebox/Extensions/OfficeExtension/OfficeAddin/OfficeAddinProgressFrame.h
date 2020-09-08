#pragma once

#include <UIlib.h>
#include "AsyncTaskCommon.h"

using namespace DuiLib;

#define		OFFICEADDIN_ITEM_CHANGE_DISTANCE			20
#define		OFFICEADDIN_ITEM_FRAME_MAX_SIZE			   170

struct UITransTaskId
{
	std::wstring id;
	std::wstring group;
	int32_t type;
	int64_t userId;
    int64_t rowId;
	UITransTaskId()
		:id(L"")
		,group(L"")
		,type(0L)
		,userId(0L)
        ,rowId(0L)
	{
	}
};

struct UITransTaskNode : st_AsyncTransRootNode
{
	int64_t completeTime;
	UITransTaskNode()
		:completeTime(0)
	{
		status = ATS_Waiting;
	}

	UITransTaskNode& operator =(const UITransTaskNode& right)
	{
		group = right.group;
		source = right.source;
		parent = right.parent;
		name = right.name;
		type = right.type;
		fileType = right.fileType;
		userId = right.userId;
		userType = right.userType;
		userName = right.userName;
		size = right.size;
		status = right.status;
		statusEx = right.statusEx;
		priority = right.priority;
		transedSize = right.transedSize;
		errorCode = right.errorCode;

		completeTime = right.completeTime;
		return *this;
	}

	UITransTaskNode& operator =(const st_AsyncTransRootNode& right)
	{
		group = right.group;
		source = right.source;
		parent = right.parent;
		name = right.name;
		type = right.type;
		fileType = right.fileType;
		userId = right.userId;
		userType = right.userType;
		userName = right.userName;
		size = right.size;
		status = right.status;
		statusEx = right.statusEx;
		priority = right.priority;
		transedSize = right.transedSize;
		errorCode = right.errorCode;
		return *this;
	}

	UITransTaskNode& operator =(const st_AsyncTransCompleteNode& right)
	{
		group = right.group;
		source = right.source;
		parent = right.parent;
		name = right.name;
		type = right.type;
		fileType = right.fileType;
		userId = right.userId;
		userType = right.userType;
		userName = right.userName;
		size = right.size;

		completeTime = right.completeTime;

		return *this;
	}
};

typedef boost::function<void(DuiLib::TNotifyUI&)> call_func;

struct OfficeAddinProgressFrameListContainerElement : public CListContainerElementUI
{
	UITransTaskNode nodeData;
};

class OfficeAddinProgressFrame : public WindowImplBase
{
	DUI_DECLARE_MESSAGE_MAP()

public:
	OfficeAddinProgressFrame(const std::wstring& path, const std::wstring& remoteParentPath, int64_t parentId, const std::wstring& id,const int32_t commonFolderEXType);

	virtual ~OfficeAddinProgressFrame();

	virtual void InitWindow();

	virtual CControlUI* CreateControl(LPCTSTR pstrClass);

	virtual bool InitLanguage(CControlUI* control);

	virtual void OnFinalMessage(HWND hWnd);

	virtual LPCTSTR GetWindowClassName(void) const;

	virtual CDuiString GetSkinFolder();

	virtual CDuiString GetSkinFile();

	virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

	void OnClick(TNotifyUI& msg);

private:
	int32_t addTask();

	void refreshTaskList();

	int32_t updateTaskStatus(const AsyncTransStatus status,int32_t errorCode=RT_OK);

	void closeNoticeFrame(UINT nIDEvent);

private:
    std::wstring path_;
	std::wstring remoteParentPath_;
	std::wstring id_;
    UITransTaskNode taskNode_;
	int64_t remoteParentId_;
	int64_t userId_;
	int32_t commonFolderEXType_;

	int32_t m_errorcode_;

	bool	m_stop_;
	boost::thread  m_thread_refresh_;
};
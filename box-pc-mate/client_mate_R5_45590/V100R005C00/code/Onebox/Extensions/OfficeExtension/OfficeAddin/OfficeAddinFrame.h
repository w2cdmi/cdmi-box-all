#pragma once

#include <UIlib.h>
#include "AsyncTaskCommon.h"

using namespace DuiLib;

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

struct UITransTaskNode
{
	UITransTaskId id; // the id information of the transtask node
	FILE_TYPE type;
	std::wstring path;
	std::wstring name;
	AsyncTransTaskStatus status;
	AsyncTransTaskInfo info;
	std::wstring userDefine;

	UITransTaskNode()
		:type(FILE_TYPE_DIR)
		,name(L"")
		,path(L"") {}
};

typedef boost::function<void(DuiLib::TNotifyUI&)> call_func;

struct OfficeAddinFrameListContainerElement : public CListContainerElementUI
{
	UITransTaskNode nodeData;
};

class OfficeAddinFrame : public WindowImplBase
{
	DUI_DECLARE_MESSAGE_MAP()

public:
	OfficeAddinFrame(const std::wstring& path);

	virtual ~OfficeAddinFrame();

	virtual void InitWindow();

	virtual CControlUI* CreateControl(LPCTSTR pstrClass);

	virtual bool InitLanguage(CControlUI* control);

	virtual void OnFinalMessage(HWND hWnd);

	virtual LPCTSTR GetWindowClassName(void) const;

	virtual CDuiString GetSkinFolder();

	virtual CDuiString GetSkinFile();

	virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

	virtual LRESULT OnHandleCopyData(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
	
	void OnClick(TNotifyUI& msg);

private:
	void addTask();

	void loadTask();

	int32_t updateTaskStatus(const AsyncTransTaskStatus status);

	int32_t updateTask(const int64_t transedSize, const int64_t size, const int64_t speed);

    void closeNoticeFrame(UINT nIDEvent);

private:
    std::wstring path_;
	std::wstring id_;
    UITransTaskNode taskNode_;
};
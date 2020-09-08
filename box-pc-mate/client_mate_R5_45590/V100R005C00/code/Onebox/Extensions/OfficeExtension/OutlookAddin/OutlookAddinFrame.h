#pragma once

#include "OutlookAddinThriftClient.h"
#include <UIlib.h>
#include "CommonDefine.h"
#include "ListContainerElement.h"
#include "AsyncTaskCommon.h"
#include "ListContainerElement.h"
#include <boost/thread.hpp>
#include "OutlookTable.h"

using namespace DuiLib;

struct ShareLinkBodyItem
{
	std::wstring path;
	std::wstring shareLink;
	ShareLinkBodyItem():path(L""),shareLink(L"")
	{
	}
};

struct UITransTaskElement : public CShadeListContainerElement
{
	std::wstring group;
	std::wstring name;
	FILE_TYPE fileType;
	AsyncTransStatus status;
	int64_t size;
	int64_t transedSize;
	int32_t errorCode;
};

typedef std::list<ShareLinkBodyItem> ShareLinkBodyItems;
typedef boost::function<int32_t(const ShareLinkBodyItems&)> updateShareLinkBodyCallback;
typedef std::list<TransTask_RootNode> TransTaskRootNodes;

class OutlookaAddinFrame : public WindowImplBase
{
public:
	OutlookaAddinFrame(const std::wstring& emailId, updateShareLinkBodyCallback callback);

	virtual ~OutlookaAddinFrame();

	virtual void InitWindow();

	virtual CControlUI* CreateControl(LPCTSTR pstrClass);

	virtual bool InitLanguage(CControlUI* control);

	virtual void OnFinalMessage(HWND hWnd);

	virtual LPCTSTR GetWindowClassName(void) const;

	virtual CDuiString GetSkinFolder();

	virtual CDuiString GetSkinFile();

	virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

	virtual LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

	virtual void Notify(TNotifyUI& msg);

public:
	int32_t addTask(std::list<std::wstring>& paths);

	bool isComplete();

	void emailSendEvent();

	void emailDeleteEvent();

private:
	void processTasks();

	void refreshUI();

	void itemPauseClick(TNotifyUI& msg);

	void itemResumeClick(TNotifyUI& msg);
	
	void itemDeleteClick(TNotifyUI& msg);

	void itemErrorClick(TNotifyUI& msg);

	void setItemResumeAndPauseButtonStatus(UITransTaskElement* item);

	void setDescribeForStatus( UITransTaskElement* item );

	void setErrorInfo(UITransTaskElement* item);

private:
	std::wstring emailId_;
	updateShareLinkBodyCallback updateShareLinkBody_;
	boost::mutex mutex_;
	boost::thread processThread_;
	ShareLinkBodyItems shareLinkBodyItems_;
	std::list<std::wstring> uploadPaths_;
	std::auto_ptr<OutlookTable> outlookTable_;
	TransTaskRootNodes transRootNodes_;
	bool isComplete_;
};

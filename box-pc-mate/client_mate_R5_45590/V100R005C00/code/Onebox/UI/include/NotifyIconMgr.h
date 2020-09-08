#pragma once

namespace Onebox
{
	enum NotifyIconStatus
	{
		OFFLINE,
		ONLINE,
		SYNCING,
		PAUSE,
		FAILED,
		UPDATING
	};
	class MainFrame;

	class NotifyIconMgr
	{
	public:
		NotifyIconMgr(CNotifyIconUI* notifyIcon, UserContext* userContext, CPaintManagerUI& paintManager, MainFrame* mainframe);
		~NotifyIconMgr(void);

		bool proccessMessage(TNotifyUI& msg);
		void updateStatus(const NotifyIconStatus status);
		void updateStatus(const NotifyIconStatus status, LPCTSTR title);
		void showBalloon(const MessageType type, const std::wstring& pstrTitle, const std::wstring& pstrContent);

		void ShowSettings(int32_t type);
		void Exit();
		void BrowseOnebox();
		void CollectLogs();
		int32_t CollectLogsWrapprt();
		void Help();
		void About();

	private:
		void ShowMainFrame();

	private:
		UserContext *userContext_;
		CNotifyIconUI *notifyIcon_;
		CPaintManagerUI &paintManager_;
		CDuiString m_strUserUrl;
		MainFrame* m_mainFrame;
		bool showFlag_;
	};
}


#pragma once
#include <boost/thread.hpp>

class CCSystemInfoMgr;
namespace Onebox
{
	class SkinConfMgr;
	class Share2MeMgr;
	class LeftRegionMgr;
	class MyFileMgr;
	class TransTaskMgr;
    class MyShareMgr;
	class TeamSpaceMgr;
	class BackUpMgr;
	class CUserBaseInfo;
	class MsgFrame;
	class LoginFrame;
	class NotifyIconMgr;
	class FullBackUpMgr;

	class MainFrame : public WindowImplBase
	{
		DUI_DECLARE_MESSAGE_MAP()

	public:
		MainFrame(const std::wstring& data = L"", const std::wstring& token = L"");

		virtual ~MainFrame();
		
		virtual void InitWindow();
		virtual LPCTSTR GetWindowClassName(void) const;
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual	bool InitLanguage(CControlUI* control);
		virtual LRESULT MessageHandler(UINT uMsg, WPARAM wParam, LPARAM lParam, bool& bHandled);
		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);
		virtual void Notify(TNotifyUI& msg);
		virtual LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		virtual LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		virtual LRESULT OnHandleCopyData(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		virtual LRESULT OnSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual void OnClick(TNotifyUI& msg);
		virtual void executeFunc(TNotifyUI& msg);

		void OnTimer(UINT nIDEvent);

		void msgShow();
		void showTip(const RECT rt, const std::wstring& strText);
		void hideTip();
		void SetTipText(CControlUI* pControl, int64_t num);
		void SetUserPhoto();
		void ChangeLanguage();
		//void menuAdded(TNotifyUI& msg);
		void menuItemClick(TNotifyUI& msg);

		int  GetState();
		void SetSate(int istate);
		void NotifyIconProcess();

	private:
		LRESULT ResponseDefaultKeyEvent(WPARAM wParam);
		void changeWorkMode(int32_t workMode);
		void update();

	private:
		UserContext* mainUserContext_;

		Share2MeMgr* share2MeMgr_;
		LeftRegionMgr* leftRegionMgr_;
		MyFileMgr * myFileMgr_;
        MyShareMgr * myShareMgr_;
		TransTaskMgr* transTaskMgr_;
		TeamSpaceMgr* teamSpaceMgr_;
		BackUpMgr* backUpMgr_;
		UINT m_uMsgTaskbarRestart;  
		MsgFrame* msgFrame_;

		NotifyIconMgr* notifyIconMgr_;

		FullBackUpMgr* fullBackUpMgr_;

		CCSystemInfoMgr* systemInfoMgr_;

		std::wstring data_;
		std::wstring token_;
		bool isUpdating_;
		boost::thread updateThread_;

		HWND		m_hWndTip;
		TOOLINFO	m_InfoTip;

		int			m_State; 
	};
}
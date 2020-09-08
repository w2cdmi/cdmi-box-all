#pragma once

namespace DuiLib
{
	enum BalloonType
	{
		BALLOON_NONE,
		BALLOON_INFO,
		BALLOON_WARNING,
		BALLOON_ERROR
	};

	enum MessageType
	{
		SYSTEM,
		RESOURCE,
		LOGIN
	};

	enum NotifyIconEvent
	{
		NOTIFYICON_CLICK,
		NOTIFYICON_RCLICK,
		NOTIFYICON_DBCLICK,
		NOTIFYICON_RDBCLICK,
		NOTIFYICON_EDIT_SYSTEM_CONF,
		NOTIFYICON_EDIT_RESOURCE_CONF
	};

#ifndef WM_NOTIFYICON
#define WM_NOTIFYICON (WM_APP+2)
#endif

	class UILIB_API CNotifyIconUI : public CControlUI
	{
	public:
		CNotifyIconUI(void);
		virtual ~CNotifyIconUI(void);

		LPCTSTR GetClass() const;
		LPVOID GetInterface(LPCTSTR pstrName);

		int GetId() const;
		void SetNotifyIcon(LPCTSTR pstrIcon);
		void SetNotifyIcon(LPCTSTR pstrIcon, LPCTSTR title);
		LPCTSTR GetNotifyIcon() const;
		void SetBalloonEnabled(bool bEnabled);
		bool GetBalloonEnabled() const;
		void SetBalloonTimeout(const UINT uiTimeout);
		UINT GetBalloonTimeout() const;
		void SetBalloonSkinFile(const LPCTSTR name);
		CDuiString GetBalloonSkinFile();
		void SetBalloonSkinFolder(const LPCTSTR path);
		CDuiString GetBalloonSkinFolder();

		void SetCheckboxTitleSystem(const LPCTSTR title);
		CDuiString SetCheckboxTitleSystem();

		void SetCheckboxTitleResource(const LPCTSTR title);
		CDuiString GetCheckboxTitleResource();

		void Init();
		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
		void DoEvent(TEventUI& event);

		void ShowBalloon(const MessageType type, LPCTSTR pstrTitle, LPCTSTR pstrContent);

		bool ReInit();
	private:
		CDuiString m_sNotifyIcon;
		int m_id;
		bool m_bBalloonEnabled;
		UINT m_uiBalloonTimeout;
		HICON m_hIcon;
		time_t m_balloonElapsed;

		CDuiString m_sCurrToolTip;
		CDuiString m_sBalloonSkinFile;
		CDuiString m_sBalloonSkinFolder;

		CDuiString m_CheckboxTitleSystem;
		CDuiString m_CheckboxTitleResource;
	};
}

#ifndef _ONEBOX_TRAYICON_H_
#define _ONEBOX_TRAYICON_H_

#include <windows.h>
#include "AboutFrame.h"
#include "Menu.h"

#define WM_NOTIFYICON  1006

namespace Onebox
{

	struct TrayIconStauts
	{
		static const wchar_t* TRAYICON_OFFLINE; 
		static const wchar_t* TRAYICON_ONLINE; 
		static const wchar_t* TRAYICON_SYNC_ING; 
		static const wchar_t* TRAYICON_SYNC_PAUSE; 
		static const wchar_t* TRAYICON_SYNC_FAILED; 

		//icon 1
		//icon 2
		//...
	};
	enum  TrayStauts
	{
		TRAY_OFFLINE,
		TRAY_ONLINE,
		TRAY_SYNCING,
		TRAY_PAUSE,
		TRAY_FAILED
	};

	enum TaskStauts
	{
		PAUSE,
		RESUME
	};

	enum BalloonType
	{
		BALLOON_NONE,
		BALLOON_INFO,
		BALLOON_WARNING,
		BALLOON_ERROR
	};

	class TrayIconMgr
	{
	public:
		virtual ~TrayIconMgr(){};
		static TrayIconMgr* getInstance();
	public:
		virtual	BOOL Init() = 0;
		virtual void DoEvent(TRAY_ICON_USER_MESSAGE nEvent) = 0;
		virtual void SetTrayState(TrayStauts trayStauts ) = 0;
		virtual void Dispose() = 0;
		virtual BOOL AddMenu() = 0;
		virtual BOOL ShowBalloon(DWORD dwStype, std::wstring strTitle, std::wstring strContent) = 0;
		static void SetHWND(HWND hWnd);
	private:
		static std::auto_ptr<TrayIconMgr> instance_;
	};
}
#endif

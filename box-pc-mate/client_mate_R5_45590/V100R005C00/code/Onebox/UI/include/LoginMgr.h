#pragma  once

namespace Onebox
{
	#define WM_CUSTOM_LOGINREQ (WM_USER+1001) 
	#define WM_CUSTOM_CHANGE_PASSWORDMODE (WM_USER+1002) 

	enum CONF_SETTINGS
	{
		OFF,
		ON
	};

	class LoginMgr
	{
	public:
		virtual ~LoginMgr(){};
		static LoginMgr* getInstance(HWND parent);
		virtual bool Login(UserContext* userContext,CPaintManagerUI& mainFramePaintManager) = 0;
		static bool LoginSuccess();
		virtual void Exit() = 0;
		virtual void Logout() = 0;
		virtual HWND GetHwnd() = 0;
	private:
		static std::auto_ptr<LoginMgr> instance_;
	};
}
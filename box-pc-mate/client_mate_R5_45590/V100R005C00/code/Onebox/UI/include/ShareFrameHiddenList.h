#pragma  once

namespace Onebox
{
	class ShareFrameHiddenList : public DuiLib::WindowImplBase
	{
		DUI_DECLARE_MESSAGE_MAP()

	public:
		ShareFrameHiddenList(UserContext* context, ShareUserInfoList& shareUserInfos);

		virtual ~ShareFrameHiddenList();

		void Init(HWND hWndParent, POINT ptPos);

		virtual LPCTSTR GetWindowClassName(void) const;

		virtual DuiLib::CDuiString GetSkinFolder();

		virtual DuiLib::CDuiString GetSkinFile();

		virtual void OnFinalMessage(HWND /*hWnd*/);

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

		virtual void Notify(DuiLib::TNotifyUI& msg);

		virtual void OnClick(DuiLib::TNotifyUI& msg);

		virtual void OnItemDoubleClick(DuiLib::TNotifyUI& msg);

		virtual void OnItemClick(DuiLib::TNotifyUI& msg);

		virtual void OnKillFocus(DuiLib::TNotifyUI& msg);

		ShareUserInfo getDBClickItem();

	private:

		void hiddenListItemClick(TNotifyUI& msg);

		void hiddenListItemDBClick(TNotifyUI& msg);

		void loadData();
		
	private:
		UserContext*	userContext_;
		ShareUserInfoList shareUserInfos_;
		ShareUserInfo shareUserInfo_;
	};
}

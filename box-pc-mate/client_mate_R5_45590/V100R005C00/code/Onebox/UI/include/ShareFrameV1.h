#pragma  once
#include "ServerSysConfig.h"
#include "NoticeFrame.h"
#include "ShareLinkNode.h"
#include "GroupNode.h"


namespace Onebox
{
	const TCHAR* const SHAREFRAME_SHARE = _T("shareFrame_share");
	const TCHAR* const SHAREFRAME_LINK = _T("shareFrame_link");
	const TCHAR* const SHAREFRAME_LISTVIEW = _T("shareFrame_listView");
	const TCHAR* const SHAREFRAME_MENU = _T("shareFrame_menu");
	const TCHAR* const SHAREFRAME_USERS = _T("shareFrame_users");
	//const TCHAR* const SHAREFRAME_ADD = _T("shareFrame_add");
	const TCHAR* const SHAREFRAME_SHAREUSERSLIST = _T("shareFrame_sharedUsersList");
	const TCHAR* const SHAREFRAME_MESSAGE = _T("shareFrame_message");
	const TCHAR* const SHAREFRAME_LINKLISTVIEW = _T("shareFrame_linklistView");
	const TCHAR* const SHAREFRAME_PUBLICLINK = _T("shareFrame_publicLink");
	const TCHAR* const SHAREFRAME_PRIVATELINK = _T("shareFrame_privateLink");
	const TCHAR* const SHAREFRAME_PUBLICLINKURL = _T("shareFrame_publiclinkUrl");
	const TCHAR* const SHAREFRAME_PUBLICCOPYURL = _T("shareFrame_publicCopyUrl");
	const TCHAR* const SHAREFRAME_PRIVATECODE = _T("shareFrame_privateCode");
	const TCHAR* const SHAREFRAME_SHARELINKFIRSTDATETIME = _T("shareFrame_shareLinkFirstDatetime");
	const TCHAR* const SHAREFRAME_SHARELINKTEXTDATETIP = _T("shareFrame_shareLinkTextDateTip");
	const TCHAR* const SHAREFRAME_SHARELINKSECONDDATETIME = _T("shareFrame_shareLinkSecondDatetime");
	const TCHAR* const SHAREFRAME_PRIVATELINKURL = _T("shareFrame_privatelinkUrl");
	const TCHAR* const SHAREFRAME_PRIVATECOPYURL = _T("shareFrame_privateCopyUrl");
	const TCHAR* const SHAREFRAME_INVITE = _T("shareFrame_invite");
	const TCHAR* const SHAREFRAME_COMPELETE = _T("shareFrame_compelete");
	const TCHAR* const SHAREFRAME_CANCEL = _T("shareFrame_cancel");
	const TCHAR* const SHAREFRAME_LINKCANCEL = _T("shareFrame_linkCancel");
	const TCHAR* const SHAREFRAME_USERNAME = _T("shareFrame_userName");
	const TCHAR* const SHAREFRAME_ITEMICON = _T("shareFrame_item_icon");
	const TCHAR* const SHAREFRAME_HIDDENUSERSLIST = _T("shareFrame_hiddenUsersList");
	const TCHAR* const SHAREFRAME_CODESELECT = _T("shareFrame_codeSelect");
	const TCHAR* const SHAREFRAME_DATESELECT = _T("shareFrame_dateSelect");
	const TCHAR* const SHAREFRAME_PRIVATECODEREFRESH = _T("shareFrame_privateCodeRefresh");
	const TCHAR* const SHAREFRAME_SAVE = _T("shareFrame_save");
	const TCHAR* const SHAREFRAME_BACK = _T("shareFrame_back");
	
	class ShareFrameV1 : public DuiLib::WindowImplBase
	{
		DUI_DECLARE_MESSAGE_MAP()

	public:
		ShareFrameV1(UserContext* context, UIFileNode& nodeData, CPaintManagerUI& paintManager);

		virtual ~ShareFrameV1();
		
		void init();

		void loadSharedUsersList();

		virtual LPCTSTR GetWindowClassName(void) const;

		virtual DuiLib::CDuiString GetSkinFolder();

		virtual DuiLib::CDuiString GetSkinFile();

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

		virtual LRESULT OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual void Notify(DuiLib::TNotifyUI& msg);

		virtual LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual void OnClick(DuiLib::TNotifyUI& msg);

		virtual void executeFunc(TNotifyUI& msg);

		virtual void ShowSharedFrame(int32_t type);

	private:
		int32_t shareLinkControl();

		void shareSelectchanged(TNotifyUI& msg);
		
		void linkSelectchanged(TNotifyUI& msg);

		void usersSetfocus(TNotifyUI& msg);

		void usersTextchanged(TNotifyUI& msg);

		void usersKillfocus(TNotifyUI& msg);

		void usersReturndown(TNotifyUI& msg);

		void listGroups(const std::wstring& SearchKey, bool fromCache = false);

		void listDomainUsers(const std::wstring& SearchKey, bool fromCache = false);
		
		void addClick(TNotifyUI& msg);

		void menuClick(TNotifyUI& msg);

		void messageSetfocus(TNotifyUI& msg);

		void messageKillfocus(TNotifyUI& msg);

		void messageReturnDown(TNotifyUI& msg);

		void inviteClick(TNotifyUI& msg);

		void hiddenListItemClick(TNotifyUI& msg);

		void itemDBClick(TNotifyUI& msg);

		bool isExistsharedUsers(CListUI* sharedUsers, ShareUserInfo& shareUserInfo, std::string type);

		void addInviteUsersList(ShareUserInfo& shareUserInfo);

		int32_t addSharedUserList(ShareUserInfo& shareUserInfo , std::string type);

		void publicLinkSelectchanged(TNotifyUI& msg);

		void privateLinkSelectchanged(TNotifyUI& msg);

		void showButton(int32_t type);

		void codeSelectchanged(TNotifyUI& msg);

		void datatimeSelectchanged(TNotifyUI& msg);

		void compeleteClick(TNotifyUI& msg);

		void linkCancelClick(TNotifyUI& msg);

		void publicCopyUrlClick(TNotifyUI& msg);

		void praviteCopyUrlClick(TNotifyUI& msg);

		void AddGroupItem(GroupNode& node);

		void AddUserItem(ShareUserInfo& node);

		void deleteShareUserList(TNotifyUI& msg);

		void refreshClick(TNotifyUI& msg);

		void uploadPermissionSelectchanged(TNotifyUI& msg);

		void downloadPermissionSelectchanged(TNotifyUI& msg);
		
		void previewPermissionchanged(TNotifyUI& msg);

		void dynamicCodeSelectchanged(TNotifyUI& msg);

		void saveClick(TNotifyUI& msg);

		void backClick(TNotifyUI& msg);

		int64_t sysTimetoInt64Time(SYSTEMTIME& sysTime);

		SYSTEMTIME int64TimeToSysTime(int64_t time);

		std::vector<std::wstring> getBatchUsers(std::wstring searchKey);

		void findGroupsAndUser(std::vector<std::wstring>& vecRemainUser, ShareUserInfoList& users, GroupNodeList& groups, const std::wstring& searchKey);

	private:
		UserContext*	userContext_;
		UIFileNode		nodeData_;
		CPaintManagerUI& paintManager_;
		CEditUI*    m_editSharedUsers;
		CEditUI*    m_editSharedMessage;
		CButtonUI*    m_btnSharedInvite;
		CButtonUI*    m_btnSharedCancel;
		CTabLayoutUI*	m_tbSharedList;
		NoticeFrameMgr* m_noticeFrame_;
		bool m_isInvite;
		bool skipChange_;
		ShareUserInfoList m_inviteUsersList;
		UIGroupNodeList m_inviteGroupList;
		ServerSysConfig m_serverSysConfig;

		std::map<std::wstring, call_func> funcMaps_;
	};

	typedef ShareFrameV1 ShareFrame;
}
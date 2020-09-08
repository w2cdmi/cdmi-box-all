#pragma  once
#include "ServerSysConfig.h"
#include "NoticeFrame.h"
#include "ShareLinkNode.h"
#include "GroupNode.h"
#include "ListContainerElement.h"
#include "UserInfo.h"

//#include "UserListControl.h"


namespace Onebox
{
	#define		SHARE_FRAME_CHANGE_DISTANCE			80
	#define		SHARE_FRAME_MAX_SIZE			   480

	const TCHAR* const SHAREFRAME_SHARE = _T("shareFrame_share");
	const TCHAR* const SHAREFRAME_LISTVIEW = _T("shareFrame_listView");
	const TCHAR* const SHAREFRAME_MENU = _T("shareFrame_menu");
	const TCHAR* const SHAREFRAME_USERS = _T("shareFrame_users");
	const TCHAR* const SHAREFRAME_TILELAYOUT_LISTVIEW= _T("shareFrame_tileLayout_listView");
	const TCHAR* const SHAREFRAME_SHAREUSERSLIST = _T("shareFrame_sharedUsersList");

	const TCHAR* const SHAREFRAME_INVITE = _T("shareFrame_invite");
	const TCHAR* const SHAREFRAME_DELETEALL = _T("shareFrame_deleteall");
	const TCHAR* const SHAREFRAME_COPYALL = _T("shareFrame_copyall");
	const TCHAR* const SHAREFRAME_COMPELETE = _T("shareFrame_compelete");
	const TCHAR* const SHAREFRAME_CANCEL = _T("shareFrame_cancel");
	const TCHAR* const SHAREFRAME_CLOSE = _T("shareFrame_close");
	const TCHAR* const SHAREFRAME_USERNAME = _T("shareFrame_userName");
	const TCHAR* const SHAREFRAME_ITEMICON = _T("shareFrame_item_icon");
	const TCHAR* const SHAREFRAME_ITEMCOMBO = _T("shareFrame_item_combo");
	const TCHAR* const SHAREFRAME_SCALE_BUTTON = _T("shareFrame_scale_button");
	const TCHAR* const SHAREFRAME_HIDDENUSERSLIST = _T("shareFrame_hiddenUsersList");
	const TCHAR* const SHAREFRAME_DATESELECT = _T("shareFrame_dateSelect");
	const TCHAR* const SHAREFRAME_SAVE = _T("shareFrame_save");
	const TCHAR* const SHAREFRAME_BACK = _T("shareFrame_back");
	const TCHAR* const SHAREFRAME_SEND = _T("shareFrame_send");
	const TCHAR* const SHAREFRAME_LISTUSERVIEW = _T("shareFrame_listUserView");
	const TCHAR* const SHAREFRAME_LISTUSERTIP = _T("shareFrame_listUserTip");
	
	class ShareFrameV2 : public DuiLib::WindowImplBase
	{
		DUI_DECLARE_MESSAGE_MAP()

	public:
		ShareFrameV2::ShareFrameV2(UserContext* context, UIFileNode& nodeData, CPaintManagerUI& paintManager);

		virtual ~ShareFrameV2();
		
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

		virtual void ShowSharedFrame();

	private:
		int32_t shareLinkControl();

		//void shareSelectchanged(TNotifyUI& msg);
		void shareSelectchanged();

		void changed2ShareLink(TNotifyUI& msg);

		void usersSetfocus(TNotifyUI& msg);

		void usersTextchanged(TNotifyUI& msg);

		void usersKillfocus(TNotifyUI& msg);

		void usersReturndown(TNotifyUI& msg);

		void listGroups(const std::wstring& SearchKey, int32_t type, bool fromCache = false);

		void listDomainUsers(const std::wstring& SearchKey, int32_t type, bool fromCache = false);
		
		void addClick(TNotifyUI& msg);

		void menuClick(TNotifyUI& msg);

		void messageSetfocus(TNotifyUI& msg);

		void messageKillfocus(TNotifyUI& msg);

		void messageReturnDown(TNotifyUI& msg);

		void inviteClick(TNotifyUI& msg);

		void deleteallClick(TNotifyUI& msg);

		void copyallClick(TNotifyUI& msg);

		void hiddenListItemClick(TNotifyUI& msg);

		void itemDBClick(TNotifyUI& msg);

		void comboItemSelect(TNotifyUI& msg);
		void timerFunc(TNotifyUI& msg);

		void menuItemSelect(TNotifyUI& msg);

		bool isExistsharedUsers(CListUI* sharedUsers, ShareUserInfo& shareUserInfo, std::string type);
	
		bool isExistsharedTileUsers(ShareNode& node);

		void addInviteUsersList(ShareUserInfo& shareUserInfo);

		int32_t addSharedUserList(ShareUserInfo& shareUserInfo , std::string type, int32_t shareType);

		int32_t addSharedTileUserList(ShareUserInfoList& shareUserInfos,UIGroupNodeList& groupList,bool bDelete=false);

		void showButton();

		void fillShareNode(int64_t& shareUserId,std::string& shareUserName, std::string& shareUserLoginName,std::string& shareUserDepartment, std::string& roleName,ShareNode& rNode);
		void setUserListVisible();
		void compeleteClick(TNotifyUI& msg);

		void AddGroupItem(GroupNode& node, int32_t type);

		void AddUserItem(ShareUserInfo& node, int32_t type);

		void deleteShareUserList(TNotifyUI& msg);

		void saveClick(TNotifyUI& msg);

		void backClick(TNotifyUI& msg);

		void changeShareNum(int32_t nCount);

		void initComboList(CComboUI *combo, SysRoleInfoExList& sysRoleInfoExs, int32_t& viewCount);

		void itemLabelSetfocus(TNotifyUI& msg);

		void codeSetfocus(TNotifyUI& msg);

		void m_editSharedMessagechanged(TNotifyUI& msg);

		std::vector<std::wstring> getBatchUsers(std::wstring searchKey);

		void findGroupsAndUser(const std::wstring& searchKey);

		void findGroupsAndUser(std::vector<std::wstring>& vecRemainUser, ShareUserInfoList& users, GroupNodeList& groups, const std::wstring& searchKey, int curlSel);

		static bool SortSysRoleInfoEx(const SysRoleInfoEx& nodeA, const SysRoleInfoEx& nodeB);
	private:
		UserContext*	userContext_;
		UIFileNode		nodeData_;
		CPaintManagerUI& paintManager_;

		CButtonUI *	m_pCloseBtn;
		CEditUI*    m_editSharedUsers;
		CRichEditUI*    m_editSharedMessage;
		CButtonUI*    m_btnSharedInvite;
		CButtonUI*    m_btnSharedClose;
		CButtonUI*    m_btnSharedCancel;
		CButtonUI* inviteBTN;
		CButtonUI* copyallBTN;
		CButtonUI* cancelBTN;
		CButtonUI* deleteallBTN;
		CButtonUI* closeBTN;
		CTabLayoutUI*	m_tbSharedList;

		CLabelUI* sharedUserText;
		CListUI* sharedUserList;
		CListUI* m_listUser;
		
		CComboUI* shareMenu;
		CEditUI* titleName;
		CVerticalLayoutUI * m_shareFrame_user_layout;
		CHorizontalLayoutUI * m_sHareFrame_menulayout ;
		CHorizontalLayoutUI * m_shareFrame_messagelayout;

		NoticeFrameMgr* m_noticeFrame_;
		bool m_isInvite;
		bool privateLinkFlag_;
		bool skipChange_;
		bool skipInitComboItem_;
		std::wstring linkCode_;
		ShareUserInfoList m_inviteUsersList;
		UIGroupNodeList m_inviteGroupList;
		ServerSysConfig m_serverSysConfig;
		ShareLinkNodeList m_shareLinkNodes;
		SysRoleInfoExList m_sysRoleInfoExs;
		std::list<ShareNode> m_addNodes;

		std::vector<std::string> roleName_;
		std::map<std::wstring, std::wstring> roleMaps_;
		std::map<std::wstring, call_func> funcMaps_;

		TNotifyUI timerMsg;
	};

	typedef ShareFrameV2 ShareFrame;
}
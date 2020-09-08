#pragma once
#include <vector>
#include "TileLayoutListUI.h"
#include "CustomListUI.h"
#include "GroupNode.h"

namespace Onebox
{
	#define		GROUP_MANAGE_DIALOG_CHANGE_DISTANCE			125

	const TCHAR vGroupManageXml[]							=	_T("GroupManage.xml");
	const TCHAR vGroupViewerXml[]							=	_T("GroupViewer.xml");
	const TCHAR vFileGroupManageListItem[]					=	_T("GroupManageListItem.xml");
	const TCHAR vFileGroupViewerListItem[]					=	_T("GroupViewerListItem.xml");
	const TCHAR vFileGroupManageListUserItem[]				=	_T("GroupManageListUserItem.xml");
	const TCHAR vFileGroupManageTileLayoutListItem[]		=	_T("GroupManageTileLayoutListItem.xml");
	const TCHAR vBtnCloseManage[]							=	_T("GroupManage_close_btn");
	const TCHAR vBtnCloseOther[]							=	_T("GroupManage_close");
	const TCHAR vBtnOkManage[]								=	_T("GroupManage_ok");
	const TCHAR vBtnCancelManage[]							=	_T("GroupManage_cancel");
	const TCHAR vRichEditManage[]							=	_T("GroupManage_addMember");
	const TCHAR vVerticalLayoutManage[]						=	_T("GroupManage_change");
	const TCHAR vComboManage[]								=	_T("GroupManage_Combo");
	const TCHAR vListManage[]								=	_T("GroupManage_listView");
	const TCHAR vListUserManage[]							=	_T("GroupManage_listUserView");
	const TCHAR vTileLayoutListManage[]						=	_T("GroupManage_tileLayout_listView");

	class GroupManageDialog:  public CWindowWnd,public INotifyUI,public IDialogBuilderCallback
	{
	public:
		explicit GroupManageDialog(UserContext* context,UserGroupNodeInfo& data);

		~GroupManageDialog();  

	public:
		LPCTSTR GetWindowClassName() const;
		UINT GetClassStyle() const;
		void OnFinalMessage(HWND /*hWnd*/);

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);

		void Init();
		void OnTimer(UINT nIDEvent);
		virtual void  Notify(TNotifyUI& msg);

		LRESULT OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnClose(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnDestroy(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcActivate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcCalcSize(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcPaint(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnKillFocus(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT OnMouseLeave(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);
		LRESULT OnNcBtn(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
    
		void SetTransparent(int nOpacity);

		void AddItem(UserGroupNodeInfo& node);

		void AddItemViewer(UserGroupNodeInfo& node);

		void GetNodes(UserGroupNodeInfoArray& nodes);

		void AddTileLayoutItem(UIGroupManageUserNode& node);

		void AddUserItem(ShareUserInfo& node);

		void AddGroupItem(GroupNode& node);

		int SetUserList();

		int SetGroupList(bool fromCache = false);

		void loadlistuser();

	protected:
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pCloseOtherBtn;
		CButtonUI *	m_pOkBtn;
		CButtonUI *	m_pCancelBtn;
		UserContext* userContext_;
		CPaintManagerUI m_Paintm;
		CDialogBuilder m_dlgBuilder;
		CEditUI* m_pAddManage;
		CComboUI* m_pCombo;
		CVerticalLayoutUI* m_pChange;
		CListUI* m_list;
		CListUI* m_listViewer;
		CListUI* m_listUser;
		CTileLayoutListUI* m_tileLayoutList;
		UserGroupNodeInfo m_groupData;
		std::wstring m_lastText;
		UserGroupNodeInfoArray m_Users;
		CButtonUI *	m_pCloseBtnViewer;
		CButtonUI *	m_pCancelBtnViewer;
	};
}
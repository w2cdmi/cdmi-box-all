#pragma once
#include "SyncFileSystemMgr.h"
#include "ShareLinkNode.h"
#include "ListContainerElement.h"
#include "GroupNode.h"
#include "NoticeFrame.h"
#include "UserInfoMgr.h"
#include "UserListControl.h"

namespace Onebox
{
	class SendShareLinkDialog : public WindowImplBase
	{
	public:
		explicit SendShareLinkDialog(UserContext* context,ShareLinkNode& _linkNode,UIFileNode& _fileNode);

		virtual ~SendShareLinkDialog();  

	public:
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);

		virtual void Notify(TNotifyUI& msg);
		void InitWindow();

		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

	private:
		void formatDes(std::wstring& tmpStr,std::wstring& strTip);
		void usersSetfocus(TNotifyUI& msg);
		void usersTextchanged(TNotifyUI& msg);
		void usersKillfocus(TNotifyUI& msg);
		void usersReturndown(TNotifyUI& msg);
		void listGroups(const std::wstring& SearchKey, int32_t type, bool fromCache = false);
		void AddGroupItem(GroupNode& node, int32_t type);
		void listDomainUsers(const std::wstring& SearchKey, int32_t type, bool fromCache = false);
		void AddUserItem(ShareUserInfo& node, int32_t type);
		void itemClick(TNotifyUI& msg);
		int32_t addSharedTileUserList(ShareUserInfoList& shareUserInfos,UIGroupNodeList& groupList,bool bDelete=false);
		bool isExistsharedTileUsers(ShareNode& node);
		std::vector<std::wstring> getBatchUsers(std::wstring searchKey);
		void findGroupsAndUser(std::vector<std::wstring>& vecRemainUser, ShareUserInfoList& users, GroupNodeList& groups, const std::wstring& searchKey, int curlSel);
		void messageSetfocus(TNotifyUI& msg);
		void messageKillfocus(TNotifyUI& msg);
		void messageReturnDown(TNotifyUI& msg);
		void sendMail(TNotifyUI& msg);
		void fillShareNode(int64_t& shareUserId,std::string& shareUserName, std::string& shareUserLoginName,std::string& shareUserDepartment, std::string& roleName,ShareNode& rNode);
		void setUserListVisible();
		void findGroupsAndUser(const std::wstring& searchKey);
	protected:
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pSendBtn;
		CButtonUI *	m_pCancelBtn;
		UserContext* userContext_;
		ShareLinkNode m_linkNode;
		UIFileNode m_fileNode;
		NoticeFrameMgr* m_noticeFrame_;
		bool skipChange_;
		ShareUserInfoList m_inviteUsersList;
		UIGroupNodeList m_inviteGroupList;
		CEditUI*  m_pUserEdit;
		CListUI*  m_pListUser;
		CRichEditUI* m_pMessage;
		std::list<ShareNode> m_addNodes;
	};
}
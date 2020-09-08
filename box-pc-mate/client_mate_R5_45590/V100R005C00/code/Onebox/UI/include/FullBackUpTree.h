#pragma once
#include "UIScaleIconButton.h"
#include "CommonFileDialog.h"
#include "CommonFileDialogLocalNotify.h"

namespace Onebox
{
	enum DIALOGTYPE
	{
		TYPE_CREATE,
		TYPE_SETTING
	};

	struct BackupTreeNodeInfo
	{
		std::shared_ptr<st_CommonFileDialogItem> _fileData;
		int32_t _nodeLevel;
		bool _bExpand;
		std::wstring _parentPath;
		BackupTreeNodeInfo():_nodeLevel(), _bExpand(false),_parentPath(L"")
		{
			_fileData = std::shared_ptr<st_CommonFileDialogItem>(new st_CommonFileDialogItem());
		}
	};
	struct BackupTreeNode : public CTreeNodeUI
	{
		BackupTreeNodeInfo nodeData;

		BackupTreeNode()
			:isExpanded_(false)
		{

		}

		LPCTSTR GetClass() const
		{
			return _T("TreeNodeUI");
		}

		void RemoveAll()
		{
			CTreeNodeUI *node = GetChildNode(0);
			while (node)
			{
				RemoveAt(node);
				node = GetChildNode(0);
			}
		}

		bool IsExpanded() const
		{
			return isExpanded_;
		}

		void SetExpand(bool bExpand = true)
		{
			isExpanded_ = bExpand;
		}

		void DoEvent(TEventUI& event)
		{
			if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
				if( m_pOwner != NULL ) m_pOwner->DoEvent(event);
				else CContainerUI::DoEvent(event);
				return;
			}

			if( event.Type == UIEVENT_DBLCLICK )
			{
				if( IsEnabled() ) {
					m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDBCLICK);
					if (!m_bSelected)
						Select();
					Invalidate();
				}
				return;
			}
			else if (event.Type == UIEVENT_SETFOCUS){
				m_bFocused = true;
				return;
			}
			else if (event.Type == UIEVENT_KILLFOCUS){
				m_bFocused = false;
				return;
			}
			CTreeNodeUI::DoEvent(event);
		}

	private:
		bool isExpanded_;
	};
	static UINT MSG_TREENODEEXPAND = WM_USER+ 110;
	typedef std::list<BackupTreeNodeInfo> BackupTreeNodeInfoList;
	class FullBackUpTreeDialog : public WindowImplBase
	{
	public:
		explicit FullBackUpTreeDialog(UserContext* context,HWND parent,DIALOGTYPE dType=TYPE_CREATE);
		virtual ~FullBackUpTreeDialog();

	public:
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual LPCTSTR GetWindowClassName(void) const;
		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);
		virtual void  Notify(TNotifyUI& msg);
		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);
		void InitWindow();
		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

	private:
		bool addItem(const BackupTreeNodeInfo& item, BackupTreeNode *parent = NULL, bool addToHeader = false, bool checkBoxIsCheck = false,bool isAuto=false);
		void itemStates(DuiLib::TNotifyUI& msg);
		void itemDBClick(DuiLib::TNotifyUI& msg);
		void loadLocalData(std::wstring str_path, BackupTreeNode* parent = NULL,bool checkBoxIsCheck = false);		
		void finishBtnClick();
		void CheckBoxClick(DuiLib::TNotifyUI& msg);
		void ChildItemControl(BackupTreeNode* parentNode,bool checkBoxIsCheck = false);
		void ParentItemControl(BackupTreeNode* childNode,bool checkBoxIsCheck = false);
		bool bEnableDir(std::wstring str_path);
		int32_t listFolder(const CommonFileDialogItem& parent, CommonFileDialogListResult& result);
		int32_t listRoot(CommonFileDialogListResult& result);
		int32_t listDesktop(CommonFileDialogListResult& result);
		int32_t listMyComputer(CommonFileDialogListResult& result);
		int32_t listVirtualPath(CommonFileDialogListResult& result, LocalCommonFileDialogItemDataType type);
		int32_t listPath(CommonFileDialogListResult& result, const std::wstring path);
		void getLoadNode(std::wstring str_path,int32_t _level);
		void autoExpandNode(BackupTreeNode *node);
		void loadExistData(std::wstring str_path, BackupTreeNode* parent = NULL,bool checkBoxIsCheck = false);
		void PreviousButtonClick(DuiLib::TNotifyUI& msg);
		void NextButtonClick(DuiLib::TNotifyUI& msg);
		void CreateRadioButtonClick(DuiLib::TNotifyUI& msg);
		void ExistRadioButtonClick(DuiLib::TNotifyUI& msg);
		void addListItem(const std::wstring& itemName, bool IsRecommendation=false);
		void ListItemClick(DuiLib::TNotifyUI&  msg);
	protected:
		UserContext* userContext_;
		HWND m_parentPaint;
		CButtonUI*	m_pCloseBtn;
		CScaleIconButtonUI* m_pCancelBtn;
		CScaleIconButtonUI* m_pFinishBtn;	
		CTreeViewUI* m_pLocalTree;
		BackupTreeNode* m_pRootNode;
		std::list<std::wstring> m_pLogicalDriveList;
		DIALOGTYPE m_dType;
		BackupTreeNodeInfoList m_treeNodes;
		int32_t m_languageID;	
		std::set<std::wstring> m_filterPathList;	
		std::set<std::wstring> m_selectPathList;
		CLabelUI* m_pIntro;
		CVerticalLayoutUI* m_pFirstPage;
		CVerticalLayoutUI* m_pSecondPage;
		CHorizontalLayoutUI* m_pSelectArea;
		COptionUI* m_pRadioButton_Create;
		COptionUI* m_pRadioButton_Exist;
		CLabelUI* m_pRemotePath;
		CListUI* m_pRemotePathList;
		CScaleIconButtonUI* m_pPreviousBtn;
		CScaleIconButtonUI* m_pNextBtn;
		std::wstring m_computerName;
		std::set<std::wstring> m_nameList;
		int32_t m_curSel;
	};
}

#pragma  once
#include "ListContainerElement.h"
#include "SkinConfMgr.h"
namespace Onebox
{
	enum UPLOADFRAME_USE_ACTION : int
	{
		UPLOADFRAME_UPLOAD = 0,	
		UPLOADFRAME_DOWNLOAD,
		UPLOADFRAME_COPYMOVE,
		UPLOADFRAME_SAVETOMYFILE,
		UPLOADFRAME_SAVETOTEAMSPACE,
		//UPLOADFRAME_TEAMSPACECOPYMOVE,
		UPLOADFRAME_BACKUPTASK_LOCALL,
		UPLOADFRAME_BACKUPTASK_ONEBOX,
		UPLOADFRAME_RIGHTUPLOAD,
	};

	enum UPLODFRAME_TIME_ID
	{
		AUTOEXPAND_TIMER
	};

	class UploadFrame : public DuiLib::WindowImplBase
	{
	public:
		UploadFrame(UserContext* context, CPaintManagerUI& paintManager);

		UploadFrame(UserContext* context, const FileBaseInfo& rootNode, CPaintManagerUI& paintManager);

		~UploadFrame();
		//iAction 0 upload; 1 download; 2 copyMove;  3 saveToMyfile; 4 saveToTeamspace;  5 right upload;
		void ShowFrame(std::list<FileBaseInfo>& destList,int& controlIndex,int iAction = 0,bool isSetParent = true);

	private:
		void Init();
		void OnTimer(UINT nIDEvent);

		LPCTSTR GetWindowClassName(void) const;

		DuiLib::CDuiString GetSkinFolder();

		DuiLib::CDuiString GetSkinFile();

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual	bool InitLanguage(CControlUI* control);

		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

		LRESULT OnCreate(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		void Notify(DuiLib::TNotifyUI& msg);

		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		void loadLocallFirstNode();

		void loadLocallDate(UploadFrameTreeNode* parentNode,int index=-1);

		void loadOneboxFirstNode();

		void loadOneboxDate(UploadFrameTreeNode* parentNode,int index=-1);

		void loadTeamspaceFirstNode();

		void loadTeamspaceDate(UploadFrameTreeNode* parentNode,int index=-1);

		void uploadClick();

		void createClick();

		void itemStates(DuiLib::TNotifyUI& msg);

		void itemDBClick(DuiLib::TNotifyUI& msg);

		void itemClick(DuiLib::TNotifyUI& msg);

		void killFocus(DuiLib::TNotifyUI& msg);

		FileBaseInfo rootNode_;
		std::list<FileBaseInfo> m_itemList;
		int m_controlIndex;
		UserContext*	userContext_;
		CPaintManagerUI&	paintManager_;
		int  		 m_iAction;
		CTreeViewUI*  m_oneboxTree;
		CButtonUI*    m_pCloseBtn;
		CButtonUI*    m_pUploadBtn;
		//CButtonUI*    m_pDownloadBtn;
		CButtonUI*    m_pCopyBtn;
		CButtonUI*    m_pMoveBtn;
		CButtonUI*    m_pCancelBtn;
		CLabelUI*	  m_title;
		CLabelUI*	  m_selectCloudDir;
		CLabelUI*	  m_warning;
		CHorizontalLayoutUI* m_upLoadArea;
		CHorizontalLayoutUI* m_downLoadArea;
		CHorizontalLayoutUI* m_copyMoveArea;
		std::set<int32_t> m_select;
		UploadFrameTreeNode* m_pRootNode;
		CButtonUI*    m_pCreateBtn;
	};
}
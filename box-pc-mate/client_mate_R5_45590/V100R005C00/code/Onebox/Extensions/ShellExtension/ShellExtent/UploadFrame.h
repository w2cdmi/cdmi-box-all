#pragma  once
#include "ThriftService.h"
#include "NoticeFrame.h"

using namespace OneboxThriftService;

namespace Onebox
{
	enum FILE_TYPE
	{
		FILE_TYPE_DIR,
		FILE_TYPE_FILE
	};

	enum UserContextType
	{
		UserContext_User,
		UserContext_Teamspace,
		UserContext_ShareUser
	};

	struct NodeIcon
	{
		static const wchar_t* MYFILE_ROOTNODE_ICON;
		static const wchar_t* TEAMSPACE_ROOTNODE_ICON;
		static const wchar_t* NORMALNODE_ICON;
	};

	struct DestNodeInfo
	{
		int64_t nodeID;
		int64_t userID;
		UserContextType userContextType;
		DestNodeInfo():nodeID(-1),userID(-1),userContextType(UserContext_User){}
	};

	struct UploadFrameTreeNode : public CTreeNodeUI
	{
		void DoEvent(TEventUI& event);
		File_Node nodeData;
		BOOL isRootNode;
		UploadFrameTreeNode():isRootNode(false){}
	};

	class UploadFrame : public WindowImplBase
	{
		
		DUI_DECLARE_MESSAGE_MAP()

	public:
		UploadFrame(std::wstring wstrPath);

		UploadFrame(std::wstring wstrPath,bool showTaskAddNotice);

		~UploadFrame();

		void InitWindow();

		void OnFinalMessage( HWND hWnd );

		LPCTSTR GetWindowClassName(void) const;

		CDuiString GetSkinFolder();

		CDuiString GetSkinFile();

		virtual DuiLib::CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

		LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);
		
		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
		
		void OnUploadFrameTimer(UINT nIDEvent);

		void OnNoticeMsgTimer(UINT nIDEvent);

		void OnClick(TNotifyUI& msg);

		void OnDBClick(TNotifyUI& msg);

		int32_t Btn_Upload_Clicked();

	public:
		static DestNodeInfo GetSelectedNode();

	private:
		void LoadRemoteDir(UploadFrameTreeNode* parentNode,int index=-1);
		
		void LoadTeamSpace(UploadFrameTreeNode* parentNode,int index=-1);

		void LoadTeamSpaceDir(UploadFrameTreeNode* parentNode, int index=-1);

		UploadFrameTreeNode* GetSecondNode(UploadFrameTreeNode* Node);

		void AutoExpandNode(UINT nIDEvent);

	private: 
		std::wstring m_sourceFilePath;
		CTreeViewUI*  m_oneboxTree;
		UploadFrameTreeNode* pMyFileRootNode_;
		UploadFrameTreeNode* pTeamSpaceNode_;
		int64_t m_userID;
		std::vector<File_Node> lfResult;
		bool isShowTaskAddNotice;
		static DestNodeInfo m_selectedNode;
	};
}


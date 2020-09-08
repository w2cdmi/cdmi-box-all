#pragma once
#include <vector>

namespace Onebox
{
	const TCHAR vFileXml[] =		_T("fileVersion.xml");
	const TCHAR vFileListItem[] =	_T("fileVersionListItem.xml");
	const TCHAR vListView[] =		_T("fileVersion_ListView");
	const TCHAR vLabFileIcon[] =	_T("fileVersion_fileIcon");
	const TCHAR vLabFileName[] =	_T("fileVersion_fileName");
	const TCHAR vBtnClose[] =		_T("fileVersion_close_btn");
	const TCHAR vBtnIconClose[]		= _T("fileVersion_close_icon_btn");
	const TCHAR vLabFileVersion[] =	_T("fileVersion_item_version");
	const TCHAR vLabFileSize[] =	_T("fileVersion_item_size");
	const TCHAR vLabFileTime[] = _T("fileVersion_item_time");
	const TCHAR vBtnDwon[] =		_T("fileVersion_item_down");
	const TCHAR vBtnDelete[] =		_T("fileVersion_item_delete");
	const TCHAR vBtnRestore[] =		_T("fileVersion_item_restore");
	const TCHAR vLabFileTitle[] =	_T("fileVersion_title");

	class FileVersionDialog : public WindowImplBase
	{
	public:
		explicit FileVersionDialog(UserContext* context,FILE_DIR_INFO& node);

		FileVersionDialog(UserContext* context,FILE_DIR_INFO& node,int64_t userId);

		virtual ~FileVersionDialog();  

	public:
		virtual CDuiString GetSkinFolder();
		virtual CDuiString GetSkinFile();
		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);
		virtual bool InitLanguage(CControlUI* control);

		void InitWindow();
		virtual void Notify(TNotifyUI& msg);
		
		LRESULT OnNcHitTest(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
    
	private:
		void SetTransparent(int nOpacity);
		void AddItem(FILE_VERSION_INFO& nodes,int versionNum);
		void GetNodes(LIST_FILEVERSION_RESULT& nodes);
	protected:
		CButtonUI *	m_pCloseBtn;
		CButtonUI *	m_pCloseBtnIcon;
		CListUI* m_pList;
		FILE_DIR_INFO m_node;
		UserContext* userContext_;
		bool noDelete_;
		bool downloadFlag_;
		int m_iVersionSize;		
		Path m_path;
		int64_t m_userId;
		File_Permissions filePermissions_;
	};
}
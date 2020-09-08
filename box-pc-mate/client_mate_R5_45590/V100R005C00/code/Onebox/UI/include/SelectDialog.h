#ifndef WIN_IMPL_SELECTDIALOG_HPP
#define WIN_IMPL_SELECTDIALOG_HPP

namespace Onebox
{
	class SelectDialog :  public CWindowWnd
	{
	public:
		SelectDialog();
		virtual ~SelectDialog();  

		LPCTSTR GetWindowClassName() const;
		UINT GetClassStyle() const;
		void OnFinalMessage(HWND /*hWnd*/);

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

		void SetDialogPos(POINT pt);
		void SetTransparent(int nOpacity);
		void SetWindowShow(bool isShow);

		void Init( LPCTSTR pszXMLPath, HWND hWndParent, POINT pt );
	public:
		CDuiString		m_strXMLPath;
		CPaintManagerUI m_DragPaintm;
		CDialogBuilder	m_dlgBuilder;
		HWND			m_hParentWnd;
		POINT			m_pt;
		HWND			m_hSelectWnd;
		int				m_width;
		int				m_heght;
	};
};

#endif
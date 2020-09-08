#pragma once
#include "DragDialog.h"

namespace Onebox
{
	class CTileLayoutListUI : public CTileLayoutUI, public IListOwnerUI
	{
	public:
		CTileLayoutListUI();
		TListInfoUI* GetListInfo();
		int GetMode() { return UILIST_MODE_ICON; }
		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);

		int GetCurSel() const;
		void Scroll(int dx, int dy);
		void EnsureVisible(int iIndex);
		LPVOID GetInterface(LPCTSTR pstrName);
		bool Add(CControlUI* pControl);
		bool AddAt(CControlUI* pControl, int iIndex);
		bool Remove(CControlUI* pControl);
		bool RemoveAt(int iIndex);
		void RemoveAll();
		void selectChanged();
		bool SelectItem(int iIndex, bool bMultiSelect = false);
		bool SelectRange(int iIndex, bool bTakeFocus = false);
		bool SelectRange(POINT ptstart, POINT ptend);
		void DoEvent(TEventUI& event);
		SIZE GetItemSize() const;
		void SetItemSize(SIZE szItem);
		int GetColumns() const;
		void SetColumns(int nCols);
		CStdValArray* GetSelects();
		void clearOtherSelected();
		//void resumeLastRename();
		//void DoPostPaint(HDC hDC, const RECT& rcPaint);
		void OnDragEnter( IDataObject *pDataObj, DWORD grfKeyState, POINT pt,  DWORD *pdwEffect);
		void OnDragOver(DWORD grfKeyState, POINT pt,DWORD *pdwEffect);  
		void OnDragLeave();  
		void OnDrop(IDataObject *pDataObj, DWORD grfKeyState, POINT pt, DWORD *pdwEffect);
		std::list<std::wstring> getDropFile();
		void clearDropFile();
		void SelectAllItem(bool bCheck);

		DragDialog* getDragDialog();
		void clearDialog();
		void setDragable(bool dragable);

		void SetMouseMoveType(int type);
		int GetMouseMoveType();
		void SetButtonDownPoint(POINT pt);
		POINT GetButtonDownPoint();
		void SetButtonUpPoint(POINT pt);
		POINT GetButtonUpPoint();
		SIZE GetScrollDownPoint();

		void setCurEnter(int curEnter);
		int getCurEnter();

		void ShowSelectWnd(POINT posstart, POINT posend, bool isShow);
		void SetSelectWnd(CWindowWnd* window, HWND parenthwnd, CPaintManagerUI* paintmanager);

		bool IsEncloseSelect();
		void SetEncloseSelect( bool bEncloseSelect );
	private:
		double CalculateDelay(double state);

	protected:
		LONG m_iDelayDeltaY;
		DWORD m_iDelayNumber;
		DWORD m_iDelayLeft;
		int m_lastSelectIndex;
		bool m_bScrollSelect;
		int m_iCurSel;
		TListInfoUI m_ListInfo;
		SIZE m_szItem;
		int m_nColumns;
		CStdValArray m_selects;
		DragDialog *m_pDragDialog;
		int m_iCurEnter;
		std::list<std::wstring> m_dropFiles;
		bool isDragable_;

		//////????¨ª?¨ª?¡Á¡ì////
	private:
		int					m_iMouseMoveType;

		CWindowWnd*			m_wSelectWnd;
		HWND				m_hParentHwnd;
		CPaintManagerUI*	m_pPaintManager;
		bool				m_bEncloseSelect;

		POINT				m_pButtonDownPoint;
		POINT				m_pButtonMovePoint;
		POINT				m_pButtonUpPoint;
		SIZE				m_pScrollDownPoint;
		int					m_iSelStartIndex;
		int					m_iSelEndIndex;
	};
}
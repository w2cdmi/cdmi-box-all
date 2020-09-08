#pragma once
#include "DragDialog.h"

namespace Onebox
{
	class CCustomListUI:public CListUI 
	{
	public:
		CCustomListUI();
		PVOID GetInterface(LPCTSTR pstrName);
		void RemoveAll();
		void clearOtherSelected();
		void selectChanged();
		//void resumeLastRename();
		void DoEvent(TEventUI& event);
		void SelectAllItem(bool bCheck);
		CStdValArray* GetSelects();
		int GetCurSel() const;
		//void DoPostPaint(HDC hDC, const RECT& rcPaint);
		void OnDragEnter( IDataObject *pDataObj, DWORD grfKeyState, POINT pt,  DWORD *pdwEffect);
		void OnDragOver(DWORD grfKeyState, POINT pt,DWORD *pdwEffect);  
		void OnDragLeave();  
		void OnDrop(IDataObject *pDataObj, DWORD grfKeyState, POINT pt, DWORD *pdwEffect);
		std::list<std::wstring> getDropFile();
		void clearDropFile();

		void clearDialog();
		void setDragable(bool dragable);
		bool isDragFileList();

	private:
		double CalculateDelay(double state);
		DragDialog *m_pDragDialog;

		LONG m_iDelayDeltaY;
		DWORD m_iDelayNumber;
		DWORD m_iDelayLeft;
		//std::set<int32_t> m_selects;
		std::list<std::wstring> m_dropFiles;
		bool m_isDragable_;

	};
}

#pragma once
#include <UIlib.h>
using namespace DuiLib;
namespace Onebox
{
	#define DUI_MSGTYPE_RICHEDIT_CHARACTER_EXCCED (_T("richeditcharacterexcced"))
	#define DUI_MSGTYPE_RICHEDIT_INVALID_CHARACTER (_T("richeditinvalidcharacter"))
	#define DUI_MSGTYPE_RICHEDIT_RESTOR_NORMAL (_T("richeditrestornormal"))

	struct CRenameRichEditUI : public CRichEditUI
	{
		HRESULT TxSendMessage(UINT msg, WPARAM wparam, LPARAM lparam, LRESULT *plresult) const;
		void DoEvent(TEventUI& event);
		void DoPaint(HDC hDC, const RECT& rcPaint);
		bool m_lastEnabled;

		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
		void SetLimitChar(LPCTSTR pstrValue);
		LPCTSTR GetLimitChar();

		CRenameRichEditUI()
		{
			m_lastEnabled = false;
			m_pLimitChar = NULL;
		}

		~CRenameRichEditUI()
		{
			if ( NULL != m_pLimitChar )
			{
				delete[] m_pLimitChar;
				m_pLimitChar = NULL;
			}
		}

	protected:
		LPCTSTR m_pLimitChar;

	};
}
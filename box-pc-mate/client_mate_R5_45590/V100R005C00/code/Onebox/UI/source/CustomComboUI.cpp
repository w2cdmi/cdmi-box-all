#include "stdafxOnebox.h"
#include "CustomComboUI.h"

namespace Onebox
{
	CCustomComboUI::CCustomComboUI():isScrollwheel_(true)
	{

	}

	void CCustomComboUI::DoEvent(TEventUI& event)
	{
		if( event.Type == UIEVENT_SCROLLWHEEL)
			//||event.Type ==UIEVENT__LAST )
		{
			//bool bDownward = LOWORD(event.wParam) == SB_LINEDOWN;
			//SelectItem(FindSelectable(m_iCurSel + (bDownward ? 1 : -1), bDownward));
			return;
		}
		return CComboUI::DoEvent(event);
	}

}
#pragma once

#include "CustomListUI.h"

namespace Onebox
{
	class CItemCheckBoxUI : public CCheckBoxUI 
	{
	public:
		CItemCheckBoxUI();

		void initData();

		void DoEvent(TEventUI& event);

	private:
		int m_iIndex;
		CListContainerElementUI* m_pItem;
		IListOwnerUI* m_pOwner;
	};

	class CSelectallCheckBoxUI : public CCheckBoxUI 
	{
	public:
		CSelectallCheckBoxUI();

		void initData();

		void DoEvent(TEventUI& event);

	private:
		bool m_bInit;
		CCustomListUI* m_pOwner;
	};
}
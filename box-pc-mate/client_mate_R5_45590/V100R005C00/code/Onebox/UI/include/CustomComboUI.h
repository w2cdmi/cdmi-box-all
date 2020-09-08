#pragma once

namespace Onebox
{
	class CCustomComboUI : public CComboUI
	{
	public:
		CCustomComboUI();
		void DoEvent(TEventUI& event);

	private:
		bool isScrollwheel_;
	};
}

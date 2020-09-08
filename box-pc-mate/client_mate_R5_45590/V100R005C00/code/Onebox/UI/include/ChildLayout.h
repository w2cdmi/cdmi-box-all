#pragma once

namespace Onebox
{
	class ChildLayoutEx : public CChildLayoutUI
	{
	public:
		ChildLayoutEx(IDialogBuilderCallback* builder);

		void Init();

		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);

	private:
		IDialogBuilderCallback* builder_;

		CDuiString xmlFile_;
	};
}

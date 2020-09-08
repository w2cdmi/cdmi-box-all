#pragma once
#include <UIlib.h>

namespace Onebox
{
	class DialogBuilderCallbackImpl : public DuiLib::IDialogBuilderCallback
	{
	public:
		static DialogBuilderCallbackImpl* getInstance();

		virtual DuiLib::CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

	private:
		static std::auto_ptr<DialogBuilderCallbackImpl> instance_;
	};
}
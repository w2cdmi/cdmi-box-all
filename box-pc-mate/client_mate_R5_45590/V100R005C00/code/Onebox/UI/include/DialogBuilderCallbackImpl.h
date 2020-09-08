#pragma once

namespace Onebox
{
	class DialogBuilderCallbackImpl : public IDialogBuilderCallback
	{
	public:
		static DialogBuilderCallbackImpl* getInstance();

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

	private:
		static std::auto_ptr<DialogBuilderCallbackImpl> instance_;
	};
}
#include "stdafx.h"
#include "DialogBuilderCallbackImpl.h"
#include "UploadFrame.h"
#include <memory>

namespace Onebox
{
	std::auto_ptr<DialogBuilderCallbackImpl> DialogBuilderCallbackImpl::instance_;

	DialogBuilderCallbackImpl* DialogBuilderCallbackImpl::getInstance()
	{
		if (NULL == instance_.get())
		{
			instance_ = std::auto_ptr<DialogBuilderCallbackImpl>(new DialogBuilderCallbackImpl);
		}
		return instance_.get();
	}

	DuiLib::CControlUI* DialogBuilderCallbackImpl::CreateControl(LPCTSTR pstrClass)
	{
		 if (_tcscmp(pstrClass, _T("UploadFrameTreeNode")) == 0)
		{
			return new UploadFrameTreeNode;
		}

		 return NULL;
	}

	bool DialogBuilderCallbackImpl::InitLanguage(CControlUI* control)
	{
		if (NULL == control)
		{
			return false;
		}

		return false;
	}
}
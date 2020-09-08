#include "stdafxOnebox.h"
#include "ChildLayout.h"

namespace Onebox
{
	ChildLayoutEx::ChildLayoutEx(IDialogBuilderCallback* builder)
		:builder_(builder)
	{

	}

	void ChildLayoutEx::Init()
	{
		if (!xmlFile_.IsEmpty())
		{
			CDialogBuilder builder;
			CContainerUI* pChildWindow = static_cast<CContainerUI*>(builder.Create(xmlFile_.GetData(), (UINT)0, builder_, m_pManager));
			if (pChildWindow)
			{
				this->Add(pChildWindow);
			}
			else
			{
				this->RemoveAll();
			}
		}
	}

	void ChildLayoutEx::SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue)
	{
		if( _tcscmp(pstrName, _T("xmlfile")) == 0 )
		{
			xmlFile_ = pstrValue;
		}
		return CChildLayoutUI::SetAttribute(pstrName, pstrValue);
	}
}
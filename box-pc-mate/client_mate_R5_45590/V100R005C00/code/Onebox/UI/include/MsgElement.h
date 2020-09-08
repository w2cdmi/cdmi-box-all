#pragma once

#include "Common.h"
#include "Utility.h"
#include "ListContainerElement.h"

namespace Onebox
{
	static const std::wstring DEFAULD_COLOR = L"#000000";
	static const std::wstring FONT_NORMAL = L"12";
	static const std::wstring FONT_BOLD =L"13";

	class MsgFrameListContainerElement : public CShadeListContainerElement
	{
    public:
        void initUI();

		MsgNode nodeData;

	private:
		void getShowText(MsgNode& msgNode,std::wstring& str_text,std::wstring& str_tip);

		std::wstring decorate(std::wstring content, bool isBold = true, std::wstring color = L"#000000");
	};
}
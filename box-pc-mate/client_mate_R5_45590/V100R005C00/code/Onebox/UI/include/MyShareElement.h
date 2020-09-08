#pragma once

#include "Common.h"
#include "Utility.h"
#include "ListContainerElement.h"

namespace Onebox
{
    const TCHAR* const MYSHARE_SETSHARE = _T("myShare_setShare");
    const TCHAR* const MYSHARE_CANCLSHARE = _T("myShare_canclShare");
    const TCHAR* const MYSHARE_SEARCHTXT = _T("myShare_searchtxt");
    const TCHAR* const MYSHARE_SEARCHBTN = _T("myShare_searchbtn");
    const TCHAR* const MYSHARE_SHARECOUT = _T("myShare_sharecount");
    const TCHAR* const MYSHARE_BACK = _T("myShare_back");
    const TCHAR* const MYSHARE_NEXT = _T("myShare_next");
    const TCHAR* const MYSHARE_UPDATE = _T("myShare_update");

	const TCHAR* const MYSHARE_LIST = _T("myShare_List");
	const TCHAR* const MYSHARE_TILE = _T("myShare_Tile");

    const TCHAR* const MYSHARE_SELECTALL = _T("myShare_selectAll");
    const TCHAR* const MYSHARE_CHECKBOX = _T("myShare_checkbox");

    class CMyShareElementUI : public CShadeListContainerElement
    {
    public:
        void fillData(const MyShareNode& shareNode);

        void initUI();

		bool flushThumb(const std::string& thumbKey);

        MyShareNode shareInfo;
		UIFileNode nodeData;
		bool isNoThumb;
    };

	class MyShareTileLayoutListContainerElement : public CMyShareElementUI
	{
	public:
		void initUI(bool renameable = false);

		void flushThumb();
	};
}
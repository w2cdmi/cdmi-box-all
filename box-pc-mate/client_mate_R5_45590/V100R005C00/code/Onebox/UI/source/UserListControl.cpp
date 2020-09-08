#include "stdafxOnebox.h"
#include "UserListControl.h"
#include "Utility.h"

CUserListControlUI::CUserListControlUI()
{
}

CUserListControlUI::~CUserListControlUI()
{
}

void CUserListControlUI::showList(std::list<ShareNode>& nameNodes,int32_t& _height)
{
	//remove old name	
	this->RemoveAll();

	int realWidth = 0;
	bool isNeedEndellipsis = false;
	CHorizontalLayoutUI* pHor = NULL;  //行容器
	int32_t counts = 0;
	RECT rc;
	rc.left = 5;
	rc.right = 5;
	rc.top = 0;
	rc.bottom = 0;
	for(std::list<ShareNode>::iterator itNode = nameNodes.begin(); itNode != nameNodes.end(); ++itNode)
	{
		counts ++;
		int width = getNameWidth(SD::Utility::String::utf8_to_wstring(itNode->sharedUserName()).c_str());
		if (NULL == pHor)
		{
			realWidth = 0;
			pHor = new CHorizontalLayoutUI();
			pHor->SetFixedWidth(SHOWAREA_DEFAULT_WIDTH - SHOWAREA_OFFSET_WIDTH);
			pHor->SetFixedHeight(25);
			pHor->SetPadding(rc);
		}

		if(width > SHOWAREA_DEFAULT_WIDTH - SHOWAREA_OFFSET_WIDTH)
		{//如果一行放不下就占一行且后面用...显示
			int curWidth = 0;
			std::wstringstream showText;
			showText << L"{f 12}";
			int width = getNameWidth(SD::Utility::String::utf8_to_wstring(itNode->sharedUserName()).c_str(), showText);
			showText << L"{/f}";
			curWidth += width;
			if(curWidth < SHOWAREA_DEFAULT_WIDTH)
			{
				addItem(counts, SD::Utility::String::utf8_to_wstring(itNode->sharedUserName()).c_str(), showText.str(), width,pHor);
			}
			this->Add(pHor);
			pHor = new CHorizontalLayoutUI();
			pHor->SetFixedWidth(SHOWAREA_DEFAULT_WIDTH - SHOWAREA_OFFSET_WIDTH);
			pHor->SetFixedHeight(25);
			pHor->SetPadding(rc);
			continue;
		}
		std::wstringstream showText;
		showText << L"{f 12}" << SD::Utility::String::utf8_to_wstring(itNode->sharedUserName()).c_str() << L"{/f}";
		realWidth += width;
		if (realWidth - SHOWAREA_OFFSET_WIDTH > SHOWAREA_DEFAULT_WIDTH)
		{
			this->Add(pHor);
			pHor = new CHorizontalLayoutUI();
			pHor->SetFixedWidth(SHOWAREA_DEFAULT_WIDTH - SHOWAREA_OFFSET_WIDTH);
			pHor->SetFixedHeight(25);
			realWidth = width;
			pHor->SetPadding(rc);
		}
		addItem(counts, SD::Utility::String::utf8_to_wstring(itNode->sharedUserName()).c_str(), showText.str(), width,pHor);
		if (counts != nameNodes.size()) continue;
		this->Add(pHor);
	}

	int32_t horCount = this->GetCount();
	std::wstring bShow = L"false";
	SIZE size;
	size.cx = 0;
	size.cy = 25*horCount;
	if (horCount > 3)
	{
		horCount = 3;
		bShow = L"true";
	}
	_height = 25*horCount;
	this->SetFixedHeight(_height);
	this->SetAttribute(L"vscrollbar",bShow.c_str());
	this->SetAttribute(L"hscrollbar",L"false");
	this->SetScrollPos(size);
	this->EndDown();
}

int CUserListControlUI::getNameWidth(const std::wstring& str_fileName)
{
	HDC hDC = this->GetManager()->GetPaintDC();
	TEXTMETRIC* pTm = &this->GetManager()->GetDefaultFontInfo()->tm;
	TFontInfo* pFontInfo = this->GetManager()->GetFontInfo(12);	//字体大小
	::SelectObject(hDC, pFontInfo->hFont);
	pTm = &pFontInfo->tm;
	int cchChars = 0;
	int cchSize = 0;
	LPCTSTR pstrNext;
	SIZE szText = { 0 };
	LPCTSTR pstrText = str_fileName.c_str();
	LPCTSTR p = pstrText;
	while( *p != _T('\0') && *p != _T('\n') ) {
		pstrNext = ::CharNext(p);
		cchChars++;
		cchSize += (int)(pstrNext - p);
		szText.cx = cchChars * pTm->tmMaxCharWidth;
		p = ::CharNext(p);
	}
	::GetTextExtentPoint32(hDC, pstrText, cchSize, &szText);
	return szText.cx + 28;
}

int CUserListControlUI::getNameWidth(const std::wstring& str_fileName, std::wstringstream& showText)
{
	HDC hDC = this->GetManager()->GetPaintDC();
	TEXTMETRIC* pTm = &this->GetManager()->GetDefaultFontInfo()->tm;
	TFontInfo* pFontInfo = this->GetManager()->GetFontInfo(12);	//字体大小
	::SelectObject(hDC, pFontInfo->hFont);
	pTm = &pFontInfo->tm;
	int cchChars = 0;
	int cchSize = 0;
	LPCTSTR pstrNext;
	SIZE szText = { 0 };
	LPCTSTR pstrText = str_fileName.c_str();
	LPCTSTR p = pstrText;
	int width = 0;
	std::wstringstream tempShowText;
	while( *p != _T('\0') && *p != _T('\n') ) {
		pstrNext = ::CharNext(p);
		cchChars++;
		cchSize += (int)(pstrNext - p);
		szText.cx = cchChars * pTm->tmMaxCharWidth;
		::GetTextExtentPoint32(hDC, pstrText, cchSize, &szText);
		if(szText.cx > SHOWAREA_DEFAULT_WIDTH - SHOWAREA_OFFSET_WIDTH)
		{
			showText << L"...";
			return width + 12;
		}
		if(szText.cx<=(SHOWAREA_DEFAULT_WIDTH - SHOWAREA_OFFSET_WIDTH -12))
		{
			showText << *p;
			width = szText.cx;
		}
		else
		{
			tempShowText << *p;
		}
		p = ::CharNext(p);
	}
	showText << tempShowText.str();
	return szText.cx  + 28;
}

void CUserListControlUI::addItem(int32_t& _index, const std::wstring& userName,const std::wstring& showName, int width,CHorizontalLayoutUI* pHor)
{
	RECT mainRc = {0, 3, 0, 3};
	CLabelUI* pBtnMain = new CLabelUI();
	pBtnMain->SetPadding(mainRc);
	pBtnMain->SetShowHtml();
	pBtnMain->SetText(showName.c_str());
	std::wstring str_tip = userName;
	pBtnMain->SetToolTip(SD::Utility::String::replace_all(str_tip,L"\r",L" ").c_str());
	pBtnMain->SetFixedWidth(width-28);
	pBtnMain->SetAttribute(L"align",L"left");
	pBtnMain->SetAttribute(L"valign",L"center");
	pBtnMain->SetFont(12);
	pBtnMain->SetTextColor(0xFF000000);

	RECT subRc = {0, 3, 0, 4};
	CButtonUI* pBtnSub = new CButtonUI();
	pBtnSub->SetPadding(subRc);
	pBtnSub->SetFixedWidth(18);
	pBtnSub->SetFixedHeight(18);
	pBtnSub->SetNormalImage(_T("..\\Image\\ic_popup_share_persondelete.png"));
	pBtnSub->SetHotImage(_T("..\\Image\\ic_popup_share_persondelete.png"));
	pBtnSub->SetPushedImage(_T("..\\Image\\ic_popup_share_persondelete.png"));

	std::wstring str_name =  L"userDelete_";
	str_name += SD::Utility::String::type_to_string<std::wstring>(_index);
	pBtnSub->SetName(str_name.c_str());

	CLabelUI* control = new CLabelUI();
	control->SetFixedWidth(10);
	pHor->Add(pBtnMain);
	pHor->Add(pBtnSub);
	pHor->Add(control);
}

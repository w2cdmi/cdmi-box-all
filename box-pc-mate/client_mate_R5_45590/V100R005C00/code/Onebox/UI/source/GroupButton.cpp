#include "stdafxOnebox.h"
#include "GroupButton.h"
#include "Utility.h"

CGroupButtonUI::CGroupButtonUI()
{
}

void CGroupButtonUI::showPath(const std::wstring& moduleName, std::list<PathNode>& pathNodes)
{
	//remove old path
	this->RemoveAll();
	menuNodes_.clear();

	int maxWidth = (this->GetWidth()==0)?CONT_DEFAULT_WIDTH:(this->GetWidth()-CONT_OFFSET_WIDTH);
	int realWidth = 0;
	bool isNeedEndellipsis = false;
	for(std::list<PathNode>::reverse_iterator itNode = pathNodes.rbegin(); itNode != pathNodes.rend(); ++itNode)
	{
		int width = getNameWidth(itNode->fileName);
		realWidth += width + 16;
		if(realWidth > maxWidth)
		{
			isNeedEndellipsis = true;
			this->RemoveAll();
			break;
		}
		std::wstringstream showText;
		showText << L"{f 12}" << itNode->fileName << L"{/f}";
		addShowNode(moduleName, itNode->fileName, itNode->fileId, showText.str(), width);
	}
	if(!isNeedEndellipsis)
	{
		return;
	}

	//show new path
	int curWidth = 0;
	bool addToMenu = false;
	for(std::list<PathNode>::reverse_iterator it = pathNodes.rbegin(); it != pathNodes.rend(); ++it)
	{
		if(addToMenu)
		{
			menuNodes_.push_back(*it);
			continue;
		}
		std::wstringstream showText;
		showText << L"{f 12}";
		int width = getNameWidth(it->fileName, showText);
		showText << L"{/f}";
		curWidth += width + 16;
		if(curWidth < maxWidth)
		{
			addShowNode(moduleName, it->fileName, it->fileId, showText.str(), width);
		}
		else
		{
			addMenuNode(moduleName);
			addToMenu = true;
			menuNodes_.push_back(*it);
		}
	}
}

void CGroupButtonUI::showMenu(const std::wstring& moduleName, TNotifyUI& msg)
{
	if (msg.sType == DUI_MSGTYPE_CLICK)
	{
		msg.pSender->SetContextMenuXmlFile(L"groupButtonContextMenu.xml");
		msg.pSender->SetContextMenuUsed(true);
		TEventUI event;
		event.dwTimestamp = msg.dwTimestamp;
		event.pSender = msg.pSender;
		event.lParam = msg.lParam;
		event.wParam = msg.wParam;
		event.ptMouse = msg.ptMouse;
		event.Type = UIEVENT_CONTEXTMENU;
		msg.pSender->Event(event);
		msg.pSender->SetContextMenuUsed(false);
	}
	else if (msg.sType == DUI_MSGTYPE_MENUADDED)
	{
		CMenuUI* pMenu = (CMenuUI*)msg.wParam;
		if (NULL == pMenu) return;
		int count = 0;
		CLabelUI* pLabelHead = new CLabelUI;
		pLabelHead->SetFixedHeight(20);
		(void)pMenu->Add(pLabelHead);
		for (std::list<PathNode>::reverse_iterator it = menuNodes_.rbegin(); it != menuNodes_.rend(); ++it, ++count)
		{
			CMenuItemUI* pMenuItem = new CMenuItemUI;
			if (NULL == pMenuItem) return;
			pMenuItem->SetFixedHeight(32);
			pMenuItem->SetId(SD::Utility::String::format_string(L"%d", count).c_str());
			RECT rt={1,0,1,0};
			pMenuItem->SetPadding(rt);
			pMenuItem->SetBkImage(L"file='..\\Image\\icon\\icon_folder.png' dest='15,4,39,28'");
			CLabelUI* pLabel = new CLabelUI;
			if (NULL == pLabel) {
				delete pMenuItem;
				pMenuItem=NULL;
				return;
			}
			pMenuItem->Add(pLabel);

			CDuiString text = it->fileName.c_str();
			pLabel->SetText(text);
			pLabel->SetTextColor(0xFF000000);
			pLabel->SetFont(12);
			pLabel->SetToolTip(text);
			RECT rcPadding = {44, 0, 15, 0};
			pLabel->SetTextPadding(rcPadding);
			pLabel->SetFont(12);
			pLabel->SetAttribute(L"endellipsis",L"true");

			(void)pMenu->Add(pMenuItem);
		}
		CLabelUI* pLabelTail = new CLabelUI;
		pLabelTail->SetFixedHeight(10);
		(void)pMenu->Add(pLabelTail);
	}
}

PathNode CGroupButtonUI::getPathNodeById(int id)
{
	PathNode node;
	node.fileId = INVALID_ID;
	if (id < 0 || (std::list<PathNode>::size_type)id > (menuNodes_.size() - 1))
	{
		return node;
	}
	std::list<PathNode>::size_type index = 0;
	for (std::list<PathNode>::reverse_iterator it = menuNodes_.rbegin(); it != menuNodes_.rend(); ++it, ++index)
	{
		if (index == id)
		{
			return *it;
		}
	}
	return node;
}

int CGroupButtonUI::getNameWidth(const std::wstring& str_fileName)
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
	return szText.cx;
}

int CGroupButtonUI::getNameWidth(const std::wstring& str_fileName, std::wstringstream& showText)
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
		if(szText.cx>TEXT_DEFAULT_WIDTH)
		{
			showText << L"...";
			return width + 12;
		}
		if(szText.cx<=(TEXT_DEFAULT_WIDTH-12))
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
	return szText.cx;
}

void CGroupButtonUI::addShowNode(const std::wstring& moduleName, const std::wstring& fileName, int64_t& fileId,
								 const std::wstring& showName, int width)
{
	RECT mainRc = {2, 6, 0, 5};
	CButtonUI* pBtnMain = new CButtonUI();
	pBtnMain->SetPadding(mainRc);                                                                                                                                                                                                                                                                                           
	pBtnMain->SetShowHtml(false);

 	pBtnMain->SetText(fileName.c_str());
	std::wstring str_tip = fileName;
	pBtnMain->SetToolTip(SD::Utility::String::replace_all(str_tip,L"\r",L" ").c_str());
	pBtnMain->SetFixedWidth(width);
	pBtnMain->SetAttribute(L"multiline",L"false");
	pBtnMain->SetAttribute(L"align",L"left");
	pBtnMain->SetAttribute(L"endellipsis",L"true");
	pBtnMain->SetAttribute(L"valign",L"center");
	pBtnMain->SetFont(12);
	pBtnMain->SetTextColor(0xFF008BE8);
	pBtnMain->SetAttribute(L"hottextcolor",L"#FF006AB0");
	pBtnMain->SetAttribute(L"pushedtextcolor",L"#FF2694FF");
	pBtnMain->SetAttribute(L"disabledtextcolor",L"#FF999999");
	std::wstringstream str_name;
	str_name << moduleName << L"_groupMain_" << fileId;
	pBtnMain->SetName(str_name.str().c_str());

	RECT subRc = {0, 12, 0, 0};
	CButtonUI* pBtnSub = new CButtonUI();
	pBtnSub->SetPadding(subRc);
	pBtnSub->SetFixedWidth(14);
	pBtnSub->SetFixedHeight(10);
	if(0==this->GetCount())
	{
		pBtnMain->SetTextColor(0xFFFFFFFF);	
		pBtnMain->SetEnabled(false);
		pBtnSub->SetEnabled(false);
	}
	else
	{
		pBtnSub->SetNormalImage(_T("file='..\\Image\\ic_toolmenu_arrowright.png' dest='2,0,12,10'"));
		pBtnSub->SetHotImage(_T("file='..\\Image\\ic_toolmenu_arrowright.png' dest='2,0,12,10'"));
		pBtnSub->SetPushedImage(_T("file='..\\Image\\ic_toolmenu_arrowright.png' dest='2,0,12,10'"));
	}
	std::wstringstream str_subName;
	str_subName << moduleName << L"_groupSub_" << fileId;
	pBtnSub->SetName(str_subName.str().c_str());

	this->AddAt(pBtnSub, 0);
	this->AddAt(pBtnMain, 0);
}

void CGroupButtonUI::addMenuNode(const std::wstring& moduleName)
{
	CButtonUI *pMenuBtn = new CButtonUI();																	
	std::wstring file = L"file='..\\Image\\ic_toolmenu_menutree.png' dest='0,5,10,15'";
	pMenuBtn->SetNormalImage(file.c_str());
	pMenuBtn->SetHotImage(file.c_str());
	pMenuBtn->SetPushedImage(file.c_str());
	RECT rc = {0, 5, 0, 6};
	pMenuBtn->SetPadding(rc);
	std::wstring str_name = moduleName;
	str_name += L"_groupMenu";
	pMenuBtn->SetName(str_name.c_str());
	pMenuBtn->SetFixedWidth(12);
	pMenuBtn->SetFixedHeight(15);
	this->AddAt(pMenuBtn, 0);
}
#include "stdafxOnebox.h"
#include "SearchTxt.h"

const DWORD g_dwTextColor = 0xFF000000;//0xFFbbbbbb;
const DWORD g_dwDefaultColor= 0xFF999999;

const std::wstring g_strNormalImage = L"file='..\\Image\\ic_input.png' source='0,0,40,30' corner='5,5,5,5'";
const std::wstring g_strHotImage = L"file='..\\Image\\ic_input.png' source='0,40,40,70' corner='5,5,5,5'";
const std::wstring g_strPushedImage = L"file='..\\Image\\ic_input.png' source='0,40,40,70' corner='5,5,5,5'";
//const std::wstring g_strPushedImage = L"file='..\\Image\\ic_input.png' source='0,80,40,110' corner='5,5,5,5'";

namespace Onebox
{
	CSearchTxtUI::CSearchTxtUI():isInit_(true), enableInit_(true),defaultTxt_(_T("search"))
	{
		CEditUI::SetText(defaultTxt_);
		CEditUI::SetTextColor(g_dwDefaultColor);
	}

	void CSearchTxtUI::DoEvent(TEventUI& event)
	{
		if( event.Type == UIEVENT_SETFOCUS ) 
		{
			std::wstring keyWord = this->GetText();
			if(isInit_)
			{
				this->SetText(L"");
				this->SetTextColor(g_dwTextColor);
				setInit(false);
			}

			CHorizontalLayoutUI* pLayout = dynamic_cast<CHorizontalLayoutUI*>(this->GetParent());
			if (pLayout && IsEnabled()){
				pLayout->SetBkImage(g_strPushedImage.c_str());
			}
		}
		else if( event.Type == UIEVENT_KILLFOCUS )  
		{
			if(CEditUI::GetText().IsEmpty())
			{
				setInit(true);
				this->SetText(defaultTxt_);
				this->SetTextColor(g_dwDefaultColor);
			}
			else
			{
				this->SetTextColor(g_dwTextColor);
				setInit(false);
				return;
			}

			CHorizontalLayoutUI* pLayout = dynamic_cast<CHorizontalLayoutUI*>(this->GetParent());
			if (pLayout && IsEnabled()){
				pLayout->SetBkImage(g_strNormalImage.c_str());
			}
		}
		else if (event.Type == UIEVENT_MOUSEENTER){
			CHorizontalLayoutUI* pLayout = dynamic_cast<CHorizontalLayoutUI*>(this->GetParent());
			if (pLayout && !IsFocused() && IsEnabled()){
				pLayout->SetBkImage(g_strHotImage.c_str());
			}
		}
		else if (event.Type == UIEVENT_MOUSELEAVE && !IsFocused()){
			CHorizontalLayoutUI* pLayout = dynamic_cast<CHorizontalLayoutUI*>(this->GetParent());
			if (pLayout && IsEnabled()){
				pLayout->SetBkImage(g_strNormalImage.c_str());
			}
		}
		
		CEditUI::DoEvent(event);
	}

	CDuiString CSearchTxtUI::GetText() const
	{
		if(isInit_)
		{
			return _T("");
		}
		else
		{
			return CEditUI::GetText();
		}
	}

	void CSearchTxtUI::setDefaultTxt(CDuiString& defaultTxt)
	{
		defaultTxt_ = defaultTxt;
		if(isInit_)
		{
			this->SetText(defaultTxt_);
			this->SetTextColor(g_dwDefaultColor);
		}
	}
	
	void CSearchTxtUI::resetText(const std::wstring& keyword)
	{
		if(keyword.empty())
		{
			this->SetText(defaultTxt_);
			this->SetTextColor(g_dwDefaultColor);
			setInit(true);
		}
		else
		{
			this->SetText(keyword.c_str());
			this->SetTextColor(g_dwTextColor);
			setInit(false);
		}
	}

	void CSearchTxtUI::enableInit(bool enableInit)
	{
		enableInit_ = enableInit;
		isInit_ = false;
	}

	void CSearchTxtUI::setInit(bool isInit)
	{
		if(enableInit_)
		{
			isInit_ = isInit;
		}
	}

	void CSearchTxtUI::SetAttribute( LPCTSTR pstrName, LPCTSTR pstrValue )
	{
		if( _tcscmp(pstrName, _T("limitchars")) == 0 ) SetLimitChar(pstrValue);
		else CEditUI::SetAttribute(pstrName, pstrValue);
	}

	void CSearchTxtUI::SetLimitChar( LPCTSTR pstrValue )
	{
		int ilen = _tcslen(pstrValue);
		if( (ilen<0) || (ilen >= INT_MAX-1) ) return;

		m_pLimitChar = new wchar_t[ilen+1];
		memset_s((void*)m_pLimitChar, sizeof(wchar_t)*(ilen+1), 0, sizeof(wchar_t)*(ilen+1));
		memcpy_s((void*)m_pLimitChar, sizeof(wchar_t)*ilen, pstrValue, sizeof(wchar_t)*ilen);
	}

	LPCTSTR CSearchTxtUI::GetLimitChar()
	{
		if( NULL != m_pLimitChar ) return m_pLimitChar;
		return L"";
	}

	CSearchTxtUI::~CSearchTxtUI()
	{
		if ( NULL != m_pLimitChar )
		{
			delete[] m_pLimitChar;
			m_pLimitChar = NULL;
		}
	}

}
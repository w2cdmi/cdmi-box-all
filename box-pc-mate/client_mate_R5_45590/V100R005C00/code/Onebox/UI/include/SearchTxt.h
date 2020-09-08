#pragma once

namespace Onebox
{
	class CSearchTxtUI:public CEditUI 
	{
	public:
		CSearchTxtUI();
		~CSearchTxtUI();
		void DoEvent(TEventUI& event);
		CDuiString GetText() const;
		void setDefaultTxt(CDuiString& defaultTxt);
		void resetText(const std::wstring& keyword);
		void enableInit(bool enableInit);

		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
		void SetLimitChar(LPCTSTR pstrValue);
		LPCTSTR GetLimitChar();
	private:
		void setInit(bool isInit);
	private:
		bool isInit_;
		bool enableInit_;
		CDuiString defaultTxt_;
	};
}
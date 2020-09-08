#pragma once

namespace DuiLib
{
	#define DUI_MSGTYPE_MENUADDED (_T("menuadded")) // wparam is the menu

	class UILIB_API CMenuItemUI : public CListContainerElementUI
	{
	public:
		CMenuItemUI();
		virtual ~CMenuItemUI(void);

		LPCTSTR GetClass() const;
		LPVOID GetInterface(LPCTSTR pstrName);
		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
		void SetId(LPCTSTR pstrId);
		int GetId() const;

		void DoEvent(TEventUI& event);  
		void DoPaint(HDC hDC, const RECT& rcPaint);
		void DrawItemBk(HDC hDC, const RECT& rcItem);

	private:
		CDuiString m_sSecondMenuXmlFile;
		CDuiString m_strNormalImage;
		CDuiString m_strHotImage;
		int m_id;
	};

	class CMenuWnd;

	class UILIB_API CMenuUI : public CListUI
	{
	public:
		CMenuUI();
		virtual ~CMenuUI();

		LPCTSTR GetClass() const;
		LPVOID GetInterface(LPCTSTR pstrName);

		void SetOwner(CControlUI* pOwner);
		CControlUI* GetOwner() const;
		void SetMenuPaddingEnabled(bool bMenuPaddingEnabled);
		bool GetMenuPaddingEnabled() const;
		void SetMenuPadding(RECT rcPadding);
		RECT GetMenuPadding() const;
		void SetTrianglePadding(const RECT rcPadding);
		RECT GetTrianglePadding() const;
		void SetTriangleEnabled(bool bTriangleUsed);
		bool GetTriangleEnabled() const;

		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
		void SetVisible(bool bVisible = true );
		void hiddenMenuItemById(int iMenuId);
		CMenuItemUI* GetMenuItemById(int iMenuId);

	private:
		CControlUI* m_pOwner;
		bool m_bMenuPaddingEnabled;
		RECT m_rcMenuPadding;
		RECT m_trianglePadding;
		bool m_bTriangleEnabled;
	};

	class UILIB_API CContextMenuUI : public CControlUI
	{
	public:
		CContextMenuUI();
		virtual ~CContextMenuUI(void);

		void Init();

		LPCTSTR GetClass() const;
		LPVOID GetInterface(LPCTSTR pstrName);

		void SetXmlFile(LPCTSTR pStrXmlFile);
		LPCTSTR GetXmlFile();
		void SetDialogBuilder(IDialogBuilderCallback* pCallback);
		IDialogBuilderCallback* GetDialogBuilder() const;

	private:
		CDuiString m_sXmlFile;
		CMenuWnd* m_pWindow;
		IDialogBuilderCallback* m_pCallback;
	};
}

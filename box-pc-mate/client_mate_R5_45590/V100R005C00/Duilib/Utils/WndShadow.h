#include <tchar.h>
#include <windows.h>
#include <map>

#pragma once

namespace DuiLib{
class UILIB_API CWndShadow
{
public:
	CWndShadow(void);
public:
	virtual ~CWndShadow(void);

protected:

	static HINSTANCE s_hInstance;
#pragma warning(disable: 4251)
	static std::map<HWND, CWndShadow *> s_Shadowmap;
#pragma warning(default: 4251)	

	typedef BOOL (WINAPI *pfnUpdateLayeredWindow)(HWND hWnd, HDC hdcDst, POINT *pptDst,
		SIZE *psize, HDC hdcSrc, POINT *pptSrc, COLORREF crKey,
		BLENDFUNCTION *pblend, DWORD dwFlags);
	/*static*/ pfnUpdateLayeredWindow s_UpdateLayeredWindow;

	HWND m_hWnd;

	LONG m_OriParentProc;	

	enum ShadowStatus
	{
		SS_ENABLED = 1,	
		SS_VISABLE = 1 << 1,
		SS_PARENTVISIBLE = 1<< 2
	};
	BYTE m_Status;

	unsigned char m_nDarkness;	
	unsigned char m_nSharpness;	
	signed char m_nSize;	

	signed char m_nxOffset;
	signed char m_nyOffset;

	LPARAM m_WndSize;

	bool m_bUpdate;

	COLORREF m_Color;

public:
	/*static*/ bool Initialize(HINSTANCE hInstance, LPCTSTR strWndClass=_T(""));
	void Create(HWND hParentWnd);

	bool SetSize(int NewSize = 0);
	bool SetSharpness(unsigned int NewSharpness = 5);
	bool SetDarkness(unsigned int NewDarkness = 200);
	bool SetShadowPos(int NewXOffset = 5, int NewYOffset = 5);
	bool SetColor(COLORREF NewColor = 0);

protected:
	static LRESULT CALLBACK ParentProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam);

	void Update(HWND hParent);

	void MakeShadow(UINT32 *pShadBits, HWND hParent, RECT *rcParent);

	inline DWORD PreMultiply(COLORREF cl, unsigned char nAlpha)
	{
		return (GetRValue(cl) * (DWORD)nAlpha / 255) << 16 |
			(GetGValue(cl) * (DWORD)nAlpha / 255) << 8 |
			(GetBValue(cl) * (DWORD)nAlpha / 255);
	}
};}

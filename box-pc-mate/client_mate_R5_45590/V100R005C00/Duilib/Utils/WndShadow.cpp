#include "stdafx.h"
#include "WndShadow.h"
#include "math.h"
#include "crtdbg.h"

#ifdef _MSC_VER
#if _MSC_VER == 1200
#define for if(false);else for
#endif
#endif

#ifndef WS_EX_LAYERED
#define WS_EX_LAYERED 0x00080000
#endif

#ifndef AC_SRC_ALPHA
#define AC_SRC_ALPHA 0x01
#endif

#ifndef ULW_ALPHA
#define ULW_ALPHA 0x00000002
#endif

//CWndShadow::pfnUpdateLayeredWindow CWndShadow::s_UpdateLayeredWindow = NULL;
namespace DuiLib{
	/*const*/ TCHAR* strWndClassName = _T("PerryShadowWnd");

	HINSTANCE CWndShadow::s_hInstance = (HINSTANCE)INVALID_HANDLE_VALUE;

	std::map<HWND, CWndShadow *> CWndShadow::s_Shadowmap;

	CWndShadow::CWndShadow(void)
		: m_hWnd((HWND)INVALID_HANDLE_VALUE)
		, m_OriParentProc(NULL)
		, m_nDarkness(150)
		, m_nSharpness(5)
		, m_nSize(0)
		, m_nxOffset(5)
		, m_nyOffset(5)
		, m_Color(RGB(0, 0, 0))
		, m_WndSize(0)
		, m_bUpdate(false)
	{
		s_UpdateLayeredWindow = NULL;
		m_Status = 0;
	}

	CWndShadow::~CWndShadow(void)
	{
	}

	bool CWndShadow::Initialize(HINSTANCE hInstance, LPCTSTR strWndClass /*= _T("")*/)
	{
		if (NULL != s_UpdateLayeredWindow)
			return false;

		HMODULE hUser32 = GetModuleHandle(_T("USER32.DLL"));
		s_UpdateLayeredWindow = 
			(pfnUpdateLayeredWindow)GetProcAddress(hUser32, 
			"UpdateLayeredWindow");

		if (NULL == s_UpdateLayeredWindow)
			return false;

		s_hInstance = hInstance;
		if (_T("") == strWndClass)
			strWndClass = strWndClassName;
		else
			strWndClassName = (TCHAR*)strWndClass;

		WNDCLASSEX wcex;

		memset(&wcex, 0, sizeof(wcex));

		wcex.cbSize = sizeof(WNDCLASSEX); 
		wcex.style			= CS_HREDRAW | CS_VREDRAW;
		wcex.lpfnWndProc	= DefWindowProc;
		wcex.cbClsExtra		= 0;
		wcex.cbWndExtra		= 0;
		wcex.hInstance		= hInstance;
		wcex.hIcon			= NULL;
		wcex.hCursor		= LoadCursor(NULL, IDC_ARROW);
		wcex.hbrBackground	= (HBRUSH)(COLOR_WINDOW+1);
		wcex.lpszMenuName	= NULL;
		wcex.lpszClassName	= strWndClass;
		wcex.hIconSm		= NULL;

		RegisterClassEx(&wcex);

		return true;
	}

	void CWndShadow::Create(HWND hParentWnd)
	{
		if(NULL == s_UpdateLayeredWindow)
			return;

		_ASSERT(s_hInstance != INVALID_HANDLE_VALUE);

		_ASSERT(s_Shadowmap.find(hParentWnd) == s_Shadowmap.end());	
		s_Shadowmap[hParentWnd] = this;

		m_hWnd = CreateWindowEx(WS_EX_LAYERED | WS_EX_TRANSPARENT, strWndClassName, NULL,
			WS_CAPTION | WS_POPUPWINDOW,
			CW_USEDEFAULT, 0, 0, 0, hParentWnd, NULL, s_hInstance, NULL);

		LONG lParentStyle = GetWindowLong(hParentWnd, GWL_STYLE);
		if(!(WS_VISIBLE & lParentStyle))	
			m_Status = SS_ENABLED;
		else if((WS_MAXIMIZE | WS_MINIMIZE) & lParentStyle)	
			m_Status = SS_ENABLED | SS_PARENTVISIBLE;
		else	
		{
			m_Status = SS_ENABLED | SS_VISABLE | SS_PARENTVISIBLE;
			::ShowWindow(m_hWnd, SW_SHOWNA);
			Update(hParentWnd);
		}

		m_OriParentProc = GetWindowLongPtr(hParentWnd, GWLP_WNDPROC );

#pragma warning(disable: 4311)	
		SetWindowLongPtr(hParentWnd, GWLP_WNDPROC , (LONG)ParentProc);
#pragma warning(default: 4311)

	}

	LRESULT CALLBACK CWndShadow::ParentProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
	{
		_ASSERT(s_Shadowmap.find(hwnd) != s_Shadowmap.end());

		CWndShadow *pThis = s_Shadowmap[hwnd];

		switch(uMsg)
		{
		case WM_MOVE:
			if(pThis->m_Status & SS_VISABLE)
			{
				RECT WndRect;
				GetWindowRect(hwnd, &WndRect);
				SetWindowPos(pThis->m_hWnd, 0,
					WndRect.left + pThis->m_nxOffset - pThis->m_nSize, WndRect.top + pThis->m_nyOffset - pThis->m_nSize,
					0, 0, SWP_NOSIZE | SWP_NOACTIVATE);
			}
			break;

		case WM_SIZE:
			if(pThis->m_Status & SS_ENABLED)
			{
				if(SIZE_MAXIMIZED == wParam || SIZE_MINIMIZED == wParam)
				{
					::ShowWindow(pThis->m_hWnd, SW_HIDE);
					pThis->m_Status &= ~SS_VISABLE;
				}
				else if(pThis->m_Status & SS_PARENTVISIBLE)	
				{
					if(LOWORD(lParam) > LOWORD(pThis->m_WndSize) || HIWORD(lParam) > HIWORD(pThis->m_WndSize))
						pThis->m_bUpdate = true;
					else
						pThis->Update(hwnd);
					if(!(pThis->m_Status & SS_VISABLE))
					{
						::ShowWindow(pThis->m_hWnd, SW_SHOWNA);
						pThis->m_Status |= SS_VISABLE;
					}
				}
				pThis->m_WndSize = lParam;
			}
			break;

		case WM_PAINT:
			{
				if(pThis->m_bUpdate)
				{
					pThis->Update(hwnd);
					pThis->m_bUpdate = false;
				}
				break;
			}

		case WM_EXITSIZEMOVE:
			if(pThis->m_Status & SS_VISABLE)
			{
				pThis->Update(hwnd);
			}
			break;

		case WM_SHOWWINDOW:
			if(pThis->m_Status & SS_ENABLED)
			{
				if(!wParam)	
				{
					::ShowWindow(pThis->m_hWnd, SW_HIDE);
					pThis->m_Status &= ~(SS_VISABLE | SS_PARENTVISIBLE);
				}
				else if(!(pThis->m_Status & SS_PARENTVISIBLE))
				{
					pThis->m_bUpdate = true;
					::ShowWindow(pThis->m_hWnd, SW_SHOWNA);
					pThis->m_Status |= SS_VISABLE | SS_PARENTVISIBLE;
				}
			}
			break;

		case WM_DESTROY:
			DestroyWindow(pThis->m_hWnd);	
			break;

		case WM_NCDESTROY:
			s_Shadowmap.erase(hwnd);
			break;

		}


#pragma warning(disable: 4312)	
		return ((WNDPROC)pThis->m_OriParentProc)(hwnd, uMsg, wParam, lParam);
#pragma warning(default: 4312)

	}

	void CWndShadow::Update(HWND hParent)
	{
		RECT WndRect;
		GetWindowRect(hParent, &WndRect);
		int nShadWndWid = WndRect.right - WndRect.left + m_nSize * 2;
		int nShadWndHei = WndRect.bottom - WndRect.top + m_nSize * 2;

		BITMAPINFO bmi;

		ZeroMemory(&bmi, sizeof(BITMAPINFO));
		bmi.bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
		bmi.bmiHeader.biWidth = nShadWndWid;
		bmi.bmiHeader.biHeight = nShadWndHei;
		bmi.bmiHeader.biPlanes = 1;
		bmi.bmiHeader.biBitCount = 32; 
		bmi.bmiHeader.biCompression = BI_RGB;
		bmi.bmiHeader.biSizeImage = nShadWndWid * nShadWndHei * 4;

		BYTE *pvBits; 
		HBITMAP hbitmap = CreateDIBSection(NULL, &bmi, DIB_RGB_COLORS, (void **)&pvBits, NULL, 0);

		ZeroMemory(pvBits, bmi.bmiHeader.biSizeImage);
		MakeShadow((UINT32 *)pvBits, hParent, &WndRect);

		HDC hMemDC = CreateCompatibleDC(NULL);
		HBITMAP hOriBmp = (HBITMAP)SelectObject(hMemDC, hbitmap);

		POINT ptDst = {WndRect.left + m_nxOffset - m_nSize, WndRect.top + m_nyOffset - m_nSize};
		POINT ptSrc = {0, 0};
		SIZE WndSize = {nShadWndWid, nShadWndHei};
		BLENDFUNCTION blendPixelFunction= { AC_SRC_OVER, 0, 255, AC_SRC_ALPHA };

		MoveWindow(m_hWnd, ptDst.x, ptDst.y, nShadWndWid, nShadWndHei, FALSE);

		BOOL bRet= s_UpdateLayeredWindow(m_hWnd, NULL, &ptDst, &WndSize, hMemDC,
			&ptSrc, 0, &blendPixelFunction, ULW_ALPHA);

		_ASSERT(bRet); 

		SelectObject(hMemDC, hOriBmp);
		DeleteObject(hbitmap);
		DeleteDC(hMemDC);

	}

	void CWndShadow::MakeShadow(UINT32 *pShadBits, HWND hParent, RECT *rcParent)
	{
		HRGN hParentRgn = CreateRectRgn(0, 0, 0, 0);
		GetWindowRgn(hParent, hParentRgn);

		SIZE szParent = {rcParent->right - rcParent->left, rcParent->bottom - rcParent->top};
		SIZE szShadow = {szParent.cx + 2 * m_nSize, szParent.cy + 2 * m_nSize};
		int nAnchors = max(szParent.cy, szShadow.cy); 
		int (*ptAnchors)[2] = new int[nAnchors + 2][2];
		int (*ptAnchorsOri)[2] = new int[szParent.cy][2];
		ptAnchors[0][0] = szParent.cx;
		ptAnchors[0][1] = 0;
		ptAnchors[nAnchors + 1][0] = szParent.cx;
		ptAnchors[nAnchors + 1][1] = 0;
		if(m_nSize > 0)
		{
			for(int i = 0; i < m_nSize; i++)
			{
				ptAnchors[i + 1][0] = szParent.cx;
				ptAnchors[i + 1][1] = 0;
				ptAnchors[szShadow.cy - i][0] = szParent.cx;
				ptAnchors[szShadow.cy - i][1] = 0;
			}
			ptAnchors += m_nSize;
		}
		for(int i = 0; i < szParent.cy; i++)
		{
			int j;
			for(j = 0; j < szParent.cx; j++)
			{
				if(PtInRegion(hParentRgn, j, i))
				{
					ptAnchors[i + 1][0] = j + m_nSize;
					ptAnchorsOri[i][0] = j;
					break;
				}
			}

			if(j >= szParent.cx)
			{
				ptAnchors[i + 1][0] = szParent.cx;
				ptAnchorsOri[i][1] = 0;
				ptAnchors[i + 1][0] = szParent.cx;
				ptAnchorsOri[i][1] = 0;
			}
			else
			{
				for(j = szParent.cx - 1; j >= ptAnchors[i + 1][0]; j--)
				{
					if(PtInRegion(hParentRgn, j, i))
					{
						ptAnchors[i + 1][1] = j + 1 + m_nSize;
						ptAnchorsOri[i][1] = j + 1;
						break;
					}
				}
			}
		}

		if(m_nSize > 0)
			ptAnchors -= m_nSize;  
		int (*ptAnchorsTmp)[2] = new int[nAnchors + 2][2]; 
		ptAnchorsTmp[0][0] = szParent.cx;
		ptAnchorsTmp[0][1] = 0;
		ptAnchorsTmp[nAnchors + 1][0] = szParent.cx;
		ptAnchorsTmp[nAnchors + 1][1] = 0;
		int nEroTimes = 0;
		for(int i = 0; i < m_nSharpness - m_nSize; i++)
		{
			nEroTimes++;
			for(int j = 1; j < nAnchors + 1; j++)
			{
				ptAnchorsTmp[j][0] = max(ptAnchors[j - 1][0], max(ptAnchors[j][0], ptAnchors[j + 1][0])) + 1;
				ptAnchorsTmp[j][1] = min(ptAnchors[j - 1][1], min(ptAnchors[j][1], ptAnchors[j + 1][1])) - 1;
			}
			int (*ptAnchorsXange)[2] = ptAnchorsTmp;
			ptAnchorsTmp = ptAnchors;
			ptAnchors = ptAnchorsXange;
		}

		ptAnchors += (m_nSize < 0 ? -m_nSize : 0) + 1;
		int nKernelSize = m_nSize > m_nSharpness ? m_nSize : m_nSharpness;
		int nCenterSize = m_nSize > m_nSharpness ? (m_nSize - m_nSharpness) : 0;
		UINT32 *pKernel = new UINT32[(2 * nKernelSize + 1) * (2 * nKernelSize + 1)];
		UINT32 *pKernelIter = pKernel;
		for(int i = 0; i <= 2 * nKernelSize; i++)
		{
			for(int j = 0; j <= 2 * nKernelSize; j++)
			{
				double dLength = sqrt((i - nKernelSize) * (i - nKernelSize) + (j - nKernelSize) * (double)(j - nKernelSize));
				if(dLength < nCenterSize)
					*pKernelIter = m_nDarkness << 24 | PreMultiply(m_Color, m_nDarkness);
				else if(dLength <= nKernelSize)
				{
					UINT32 nFactor = ((UINT32)((1 - (dLength - nCenterSize) / (m_nSharpness + 1)) * m_nDarkness));
					*pKernelIter = nFactor << 24 | PreMultiply(m_Color, nFactor);
				}
				else
					*pKernelIter = 0;
				pKernelIter ++;
			}
		}
		for(int i = nKernelSize; i < szShadow.cy - nKernelSize; i++)
		{
			int j;
			if(ptAnchors[i][0] < ptAnchors[i][1])
			{

				for(j = ptAnchors[i][0];
					j < min(max(ptAnchors[i - 1][0], ptAnchors[i + 1][0]) + 1, ptAnchors[i][1]);
					j++)
				{
					for(int k = 0; k <= 2 * nKernelSize; k++)
					{
						UINT32 *pPixel = pShadBits +
							(szShadow.cy - i - 1 + nKernelSize - k) * szShadow.cx + j - nKernelSize;
						UINT32 *pKernelPixel = pKernel + k * (2 * nKernelSize + 1);
						for(int l = 0; l <= 2 * nKernelSize; l++)
						{
							if(*pPixel < *pKernelPixel)
								*pPixel = *pKernelPixel;
							pPixel++;
							pKernelPixel++;
						}
					}
				}

				for(j = max(j, min(ptAnchors[i - 1][1], ptAnchors[i + 1][1]) - 1);
					j < ptAnchors[i][1];
					j++)
				{
					for(int k = 0; k <= 2 * nKernelSize; k++)
					{
						UINT32 *pPixel = pShadBits +
							(szShadow.cy - i - 1 + nKernelSize - k) * szShadow.cx + j - nKernelSize;
						UINT32 *pKernelPixel = pKernel + k * (2 * nKernelSize + 1);
						for(int l = 0; l <= 2 * nKernelSize; l++)
						{
							if(*pPixel < *pKernelPixel)
								*pPixel = *pKernelPixel;
							pPixel++;
							pKernelPixel++;
						}
					}
				}

			}
		}

		UINT32 clCenter = m_nDarkness << 24 | PreMultiply(m_Color, m_nDarkness);
		for(int i = min(nKernelSize, max(m_nSize - m_nyOffset, 0));
			i < max(szShadow.cy - nKernelSize, min(szParent.cy + m_nSize - m_nyOffset, szParent.cy + 2 * m_nSize));
			i++)
		{
			UINT32 *pLine = pShadBits + (szShadow.cy - i - 1) * szShadow.cx;
			if(i - m_nSize + m_nyOffset < 0 || i - m_nSize + m_nyOffset >= szParent.cy)        // Line is not covered by parent window
			{
				for(int j = ptAnchors[i][0]; j < ptAnchors[i][1]; j++)
				{
					*(pLine + j) = clCenter;
				}
			}
			else
			{
				for(int j = ptAnchors[i][0];
					j < min(ptAnchorsOri[i - m_nSize + m_nyOffset][0] + m_nSize - m_nxOffset, ptAnchors[i][1]);
					j++)
					*(pLine + j) = clCenter;
				for(int j = max(ptAnchorsOri[i - m_nSize + m_nyOffset][0] + m_nSize - m_nxOffset, 0);
					j < min(ptAnchorsOri[i - m_nSize + m_nyOffset][1] + m_nSize - m_nxOffset, szShadow.cx);
					j++)
					*(pLine + j) = 0;
				for(int j = max(ptAnchorsOri[i - m_nSize + m_nyOffset][1] + m_nSize - m_nxOffset, ptAnchors[i][0]);
					j < ptAnchors[i][1];
					j++)
					*(pLine + j) = clCenter;
			}
		}

		delete[] (ptAnchors - (m_nSize < 0 ? -m_nSize : 0) - 1);
		delete[] ptAnchorsTmp;
		delete[] ptAnchorsOri;
		delete[] pKernel;
		DeleteObject(hParentRgn);
	}

	bool CWndShadow::SetSize(int NewSize)
	{
		if(NewSize > 20 || NewSize < -20)
			return false;

		m_nSize = (signed char)NewSize;
		if(SS_VISABLE & m_Status)
			Update(GetParent(m_hWnd));
		return true;
	}

	bool CWndShadow::SetSharpness(unsigned int NewSharpness)
	{
		if(NewSharpness > 20)
			return false;

		m_nSharpness = (unsigned char)NewSharpness;
		if(SS_VISABLE & m_Status)
			Update(GetParent(m_hWnd));
		return true;
	}

	bool CWndShadow::SetDarkness(unsigned int NewDarkness)
	{
		if(NewDarkness > 255)
			return false;

		m_nDarkness = (unsigned char)NewDarkness;
		if(SS_VISABLE & m_Status)
			Update(GetParent(m_hWnd));
		return true;
	}

	bool CWndShadow::SetShadowPos(int NewXOffset, int NewYOffset)
	{
		if(NewXOffset > 20 || NewXOffset < -20 ||
			NewYOffset > 20 || NewYOffset < -20)
			return false;

		m_nxOffset = (signed char)NewXOffset;
		m_nyOffset = (signed char)NewYOffset;
		if(SS_VISABLE & m_Status)
			Update(GetParent(m_hWnd));
		return true;
	}

	bool CWndShadow::SetColor(COLORREF NewColor)
	{
		m_Color = NewColor;
		if(SS_VISABLE & m_Status)
			Update(GetParent(m_hWnd));
		return true;
	}}

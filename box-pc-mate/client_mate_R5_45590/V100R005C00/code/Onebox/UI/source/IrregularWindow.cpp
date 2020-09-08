#include "stdafxOnebox.h"
#include "IrregularWindow.h"
#include <gdiplus.h>
#include <windowsx.h>
#include <locale.h>
#include "InILanguage.h"

#pragma comment(lib, "gdiplus.lib")

static const TCHAR* __className  =  _T("__IrregularWindowClass");

ULONG_PTR CIrregularWindow::gdiplusToken = 0;

HHOOK CIrregularWindow::hhk = NULL;

CIrregularWindow::CIrregularWindow(const TCHAR* pBackImgFullPath,DWORD dwAttachWndTransColor)
:m_hWnd(NULL)
,m_hAttachWnd(NULL)
,m_dwAttachWndTransColor(dwAttachWndTransColor)
{
	if(pBackImgFullPath != NULL)
	{
		m_strBackImg = pBackImgFullPath;

		if(!RegisterWindowClass() || !Create())
		{
			assert(false && _T("The background window create failed!"));
		}
	}

	setlocale(LC_ALL,"");
}

bool CIrregularWindow::RegisterWindowClass()
{
	WNDCLASS wc = { 0 };
	wc.style = 0;
	wc.cbClsExtra = 0;
	wc.cbWndExtra = 0;
	wc.hIcon = NULL;
	wc.lpfnWndProc = CIrregularWindow::__WndProc;
	wc.hInstance = (HINSTANCE)::GetModuleHandle(NULL);
	wc.hCursor = ::LoadCursor(NULL, IDC_ARROW);
	wc.hbrBackground = NULL;
	wc.lpszMenuName  = NULL;
	wc.lpszClassName =__className;
	ATOM ret = ::RegisterClass(&wc);
	ASSERT(ret!=NULL || ::GetLastError()==ERROR_CLASS_ALREADY_EXISTS);
	return ret != NULL || ::GetLastError() == ERROR_CLASS_ALREADY_EXISTS;
}

bool CIrregularWindow::Create()
{
	m_hWnd = ::CreateWindowEx(WS_EX_LAYERED, __className,iniLanguageHelper.GetWindowName(WINDOWNAME_UPGRADE_KEY).c_str(),WS_OVERLAPPEDWINDOW,
		CW_USEDEFAULT, CW_USEDEFAULT, 
		CW_USEDEFAULT, CW_USEDEFAULT, 
		NULL, NULL, (HINSTANCE)::GetModuleHandle(NULL), 0);

	if(m_hWnd == NULL || !::IsWindow(m_hWnd))
		return false;

	ShowWindow(m_hWnd,SW_SHOWNORMAL);

	return true;
}

bool CIrregularWindow::AttachWindow(HWND hWnd)
{
	if(m_hAttachWnd != NULL)
	{
		assert(false && _T("Not again AttachWindow!"));
		return false;
	}

	if(!IsWindow(hWnd))
	{
		assert(false && _T("invalid window handle!"));
		return false;
	}

	m_hAttachWnd = hWnd;

	SetWindowLong(m_hWnd,GWL_USERDATA,(LONG)m_hAttachWnd);

	//将附加窗体样式 加上WS_EX_LAYERED
	DWORD dwAttachWndStyleEx = GetWindowLong(m_hAttachWnd,GWL_EXSTYLE);

	::SetWindowLong(m_hAttachWnd,GWL_EXSTYLE,dwAttachWndStyleEx | WS_EX_LAYERED);

	SetBackground(m_strBackImg.c_str());

	SetAttachWndTransColor(m_dwAttachWndTransColor);

	return true;
}

void CIrregularWindow::SetBackground(const TCHAR* pBackImgFullPath)
{
	if(pBackImgFullPath != NULL)
	{
		m_strBackImg = pBackImgFullPath;
	}

	if(!m_strBackImg.empty())
	{
#ifdef UNICODE
		Gdiplus::Image* pImage = Gdiplus::Image::FromFile(pBackImgFullPath);
#else
		size_t szHasConv = 0;
		DWORD dwSize = m_strBackImg.size() + 1;
		wchar_t* pImgPath = new wchar_t[dwSize];
		(void)memset_s((void*)pImgPath,sizeof(wchar_t) * dwSize,0,sizeof(wchar_t) * dwSize);
		::mbstowcs_s(&szHasConv,pImgPath,dwSize,m_strBackImg.c_str(),m_strBackImg.size());
		Gdiplus::Image* pImage = Gdiplus::Image::FromFile(pImgPath);
		delete [] pImgPath;	
#endif

		if(pImage)
		{
			BLENDFUNCTION blendFunc;
			blendFunc.BlendOp = 0;
			blendFunc.BlendFlags = 0;
			blendFunc.AlphaFormat = 1;
			blendFunc.SourceConstantAlpha = 255;//AC_SRC_ALPHA

			SIZE sizeWindow = { pImage->GetWidth(), pImage->GetHeight()};

			HDC hDC = ::GetDC(m_hWnd);

			HDC hdcMemory = CreateCompatibleDC(hDC);

			HBITMAP hBitMap = CreateCompatibleBitmap(hDC, sizeWindow.cx, sizeWindow.cy);

			::SelectObject(hdcMemory, hBitMap);

			RECT rcWindow;
			GetWindowRect(m_hWnd,&rcWindow);

			BITMAPINFOHEADER stBmpInfoHeader = { 0 };   
			int nBytesPerLine = ((sizeWindow.cx * 32 + 31) & (~31)) >> 3;
			stBmpInfoHeader.biSize = sizeof(BITMAPINFOHEADER);   
			stBmpInfoHeader.biWidth = sizeWindow.cx;   
			stBmpInfoHeader.biHeight = sizeWindow.cy;   
			stBmpInfoHeader.biPlanes = 1;   
			stBmpInfoHeader.biBitCount = 32;   
			stBmpInfoHeader.biCompression = BI_RGB;   
			stBmpInfoHeader.biClrUsed = 0;   
			stBmpInfoHeader.biSizeImage = nBytesPerLine * sizeWindow.cy;   

			PVOID pvBits = NULL;   

			HBITMAP hbmpMem = ::CreateDIBSection(NULL, (PBITMAPINFO)&stBmpInfoHeader, DIB_RGB_COLORS, &pvBits, NULL, 0);

			assert(hbmpMem != NULL);

			SecureZeroMemory(pvBits, sizeWindow.cx * 4 * sizeWindow.cy);
			*(volatile char*)pvBits = *(volatile char*)pvBits;

			if(hbmpMem)   
			{   
				HGDIOBJ hbmpOld = ::SelectObject( hdcMemory, hbmpMem);
				
				POINT ptWinPos = { rcWindow.left, rcWindow.top };

				Gdiplus::Graphics graph(hdcMemory);

				graph.SetSmoothingMode(Gdiplus::SmoothingModeNone);

				graph.DrawImage(pImage, 0, 0, sizeWindow.cx, sizeWindow.cy);

				std::wstring libPath = SD::Utility::FS::get_system_directory() + _T("User32.DLL");
				HMODULE hFuncInst = LoadLibrary(libPath.c_str());
				if(NULL == hFuncInst) return;
				typedef BOOL (WINAPI *MYFUNC)(HWND, HDC, POINT*, SIZE*, HDC, POINT*, COLORREF, BLENDFUNCTION*, DWORD);          

				MYFUNC UpdateLayeredWindow;

				UpdateLayeredWindow = (MYFUNC)::GetProcAddress(hFuncInst, "UpdateLayeredWindow");
				if(NULL==UpdateLayeredWindow)
				{
					::FreeLibrary(hFuncInst);
					return;
				}
				POINT ptSrc = { 0, 0};

				//不会发送 WM_SIZE和WM_MOVE消息
				if(!UpdateLayeredWindow(m_hWnd, hDC, &ptWinPos, &sizeWindow, hdcMemory, &ptSrc, 0, &blendFunc, ULW_ALPHA))
				{
					TCHAR tmp[255] = {_T('\0')};
#ifdef UNICODE
	#define	MySprintf swprintf_s
#else
	#define MySprintf sprintf_s
#endif
					MySprintf(tmp,255,__T("UpdateLayeredWindow  called failed,error code:%u"),GetLastError());

					MessageBox(m_hWnd,tmp,_T("Notice"),MB_OK);
				}

				//::SendMessage(m_hWnd,WM_SIZE,0,MAKELONG(sizeWindow.cy,sizeWindow.cx));

				//::SendMessage(m_hWnd,WM_MOVE,0,MAKELONG(ptSrc.y,ptSrc.x));

				graph.ReleaseHDC(hdcMemory);

				::SelectObject( hdcMemory, hbmpOld);   

				::DeleteObject(hbmpMem);

				::FreeLibrary(hFuncInst);
			}

			::DeleteDC(hdcMemory);

			::DeleteDC(hDC);
		}
		else
		{
			assert(false && _T("background picture open failed!"));
		}
	}
}

void CIrregularWindow::SetAttachWndTransColor(DWORD dwColor)
{
	m_dwAttachWndTransColor = dwColor;
	std::wstring libPath = SD::Utility::FS::get_system_directory() + _T("User32.DLL");
	HMODULE hInst = LoadLibrary(libPath.c_str()); 
	if(NULL == hInst) return;
	typedef BOOL (WINAPI *MYFUNC)(HWND, COLORREF, BYTE, DWORD); 

	MYFUNC SetLayeredWindowAttributes = NULL; 

	SetLayeredWindowAttributes = (MYFUNC)::GetProcAddress(hInst, "SetLayeredWindowAttributes"); 

	if(SetLayeredWindowAttributes)
	{
		if(!SetLayeredWindowAttributes(m_hAttachWnd, m_dwAttachWndTransColor, 0, LWA_COLORKEY))
		{
			assert(false && _T("SetLayeredWindowAttributes Failded!"));
		}
	}

	::FreeLibrary(hInst);
}

void CIrregularWindow::InitGDIplus()
{
	//Gdiplus::GdiplusStartupInput gdiplusStartupInput;
	
	//Gdiplus::GdiplusStartup(&gdiplusToken, &gdiplusStartupInput, NULL);
}

void CIrregularWindow::UnInitGDIplus()
{
	//Gdiplus::GdiplusShutdown(gdiplusToken);
}

void CIrregularWindow::CenterWindow()
{
	RECT rcDlg = {0};
    ::GetWindowRect(m_hWnd, &rcDlg);
    RECT rcArea = { 0 };
    RECT rcCenter = { 0 };
    HWND hWndCenter = ::GetWindowOwner(m_hWnd);
    (void)::SystemParametersInfo(SPI_GETWORKAREA, NULL, &rcArea, NULL);
    if( hWndCenter == NULL ) rcCenter = rcArea; else ::GetWindowRect(hWndCenter, &rcCenter);

    int DlgWidth = rcDlg.right - rcDlg.left;
    int DlgHeight = rcDlg.bottom - rcDlg.top;

    // Find dialog's upper left based on rcCenter
    int xLeft = (rcCenter.left + rcCenter.right - DlgWidth)/ 2;
    int yTop = (rcCenter.top + rcCenter.bottom - DlgHeight) / 2;

    // The dialog is outside the screen, move it inside
    if( xLeft < rcArea.left ) xLeft = rcArea.left;
    else if( xLeft + DlgWidth > rcArea.right ) xLeft = rcArea.right - DlgWidth;
    if( yTop < rcArea.top ) yTop = rcArea.top;
    else if( yTop + DlgHeight > rcArea.bottom ) yTop = rcArea.bottom - DlgHeight;
    ::SetWindowPos(m_hWnd, NULL, xLeft, yTop, -1, -1, SWP_NOSIZE | SWP_NOZORDER | SWP_NOACTIVATE);
}

LRESULT CALLBACK  CIrregularWindow::__WndProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	HWND hAttackWnd = (HWND)GetWindowLong(hWnd,GWL_USERDATA);
	switch(uMsg)
	{
	case WM_CREATE:
		{
			LONG styleValue = ::GetWindowLong(hWnd, GWL_STYLE);
			styleValue &= ~WS_CAPTION;
			styleValue &= ~WS_MAXIMIZEBOX; 
			styleValue &= ~WS_MINIMIZEBOX; 
			styleValue &= ~WS_THICKFRAME; 
			styleValue &= ~WS_BORDER; 
			styleValue &= ~WS_CAPTION;
			::SetWindowLong(hWnd, GWL_STYLE, styleValue | WS_CLIPSIBLINGS | WS_CLIPCHILDREN);
		}
		break;
	case WM_SIZE:
		{
			short cx = LOWORD(lParam); 
			short cy = HIWORD(lParam);

			::SetWindowPos(hAttackWnd, NULL, 0, 0, cx, cy, SWP_NOZORDER | SWP_NOMOVE);
		}
		break;
	case WM_MOVE:
		{
			RECT rect;
			::GetWindowRect(hWnd,&rect);
			::SetWindowPos(hAttackWnd, NULL, rect.left, rect.top, 0, 0, SWP_NOZORDER | SWP_NOSIZE);
		}
		break;
	case WM_LBUTTONDOWN:
		{
			::SendMessage( hWnd, WM_SYSCOMMAND, 0xF012, 0);
			::SendMessage( hAttackWnd, WM_SYSCOMMAND, 0xF012, 0);
		}
		break;
	}

	return ::DefWindowProc(hWnd,uMsg,wParam,lParam);
}
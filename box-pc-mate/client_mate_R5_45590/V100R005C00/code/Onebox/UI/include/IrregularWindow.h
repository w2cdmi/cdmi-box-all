#ifndef _IRREGULARWINDOW_H_
#define _IRREGULARWINDOW_H_
/**
  * ����������
*/

#include <windows.h>
#include <string>
#include <tchar.h>
#include <assert.h>

#ifndef __String
	#ifdef UNICODE
		#define __String wstring
	#else
		#define __String string
	#endif
#endif


const DWORD c_dwTransColor = 0x00ffffff;

class CIrregularWindow
{
public:

	CIrregularWindow(const TCHAR* pBackImgFullPath,DWORD dwAttachWndTransColor = c_dwTransColor);

	//���Ӵ���
	bool AttachWindow(HWND hWnd);

	//����ͼƬ(ȫ·�� png)
	void SetBackground(const TCHAR* pBackImgFullPath);

	//һ����� �ǲ��õ���������� ����������� 
	//�����ӵĴ�����Ӵ��� ��ʾ����ȫ����� ����Ҫ�������ֵ
	void SetAttachWndTransColor(DWORD dwColor);

	HWND GetHandle(){return m_hWnd;}

	void CenterWindow();
protected:

	virtual bool RegisterWindowClass();

	virtual bool Create();

public:

	static void InitGDIplus();

	static void UnInitGDIplus();

private:

	static LRESULT CALLBACK  __WndProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam);

	static LRESULT CALLBACK __HookProc(int nCode,WPARAM wParam,LPARAM lParam);

	static ULONG_PTR gdiplusToken;

	static HHOOK hhk;

protected:

	HWND m_hWnd;

	//�����ӵĴ���ľ��
	HWND m_hAttachWnd;

	DWORD m_dwAttachWndTransColor;

	//����ͼƬ(png)
	std::wstring m_strBackImg;
};

#endif
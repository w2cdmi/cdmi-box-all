#pragma once
#include "stdafxOnebox.h"
const TCHAR* const MenuBkImage =_T("file='..\\Image\\ic_more_popup_bg.png' corner='20,20,20,20'");
const TCHAR* const ExplandIcon = _T("..\\img\\menu_expand.png");
const TCHAR* const MenuDefaultList = _T("inset=\"0,0,0,0\" bkcolor=\"0xFFFAFAFA\" itemshowhtml=\"true\"");

struct ContextMenuParam
{
	HWND hWnd;
	WPARAM wParam;
};

/*
* ע�ͣ�������ӦID
*/
enum TRAY_ICON_USER_MESSAGE : unsigned long
{
	TRAY_ICON_WM_USER_MSG = WM_USER + 10000,
	TRAY_ICON_WM_USER_MSG_ABOUT, //����
	TRAY_ICON_WM_USER_MSG_QUIT,	//�˳�
	TRAY_ICON_WM_USER_MSG_INFO,	//��Ϣ
	TRAY_ICON_WM_USER_MSG_HELP,	//����
	TRAY_ICON_WM_USER_MSG_COLLECT_LOG, //�ռ��ռ�
	TRAY_ICON_WM_USER_MSG_OPEN_WEB, //��web ����
	TRAY_ICON_WM_USER_MSG_OPTION, //ѡ��
	TRAY_ICON_WM_USER_MSG_OPERATOR, //��ͣ/�ָ��ϴ���������
	TRAY_ICON_WM_USER_MSG_LANGUAGE,	//��������������
};

enum MYFILE_USER_MESSAGE : unsigned long
{
	MYFILE_WM_USER_MSG = WM_USER + 10100,
	MYFILE_WM_USER_MSG_OPEN,	//��
	MYFILE_WM_USER_MSG_SHARE,							//����
	MYFILE_WM_USER_MSG_LINK,							//��������
	MYFILE_WM_USER_MSG_DOWNLOAD,						//����
	MYFILE_WM_USER_MSG_SETSYNC,							//����ͬ��
	MYFILE_WM_USER_MSG_CANCELSYNC,						//ȡ��ͬ��
	MYFILE_WM_USER_MSG_TRANSFERTOTEAMSPACE,				//ת�����Ŷӿռ�
	MYFILE_WM_USER_MSG_COPYMOVE,						//�����ƶ�
	MYFILE_WM_USER_MSG_DELETE,							//ɾ��
	MYFILE_WM_USER_MSG_RENAME,							//������
	MYFILE_WM_USER_MSG_VIEWVERSION,						//�鿴�汾
};

enum MYTEAMSPACE_USER_MESSAGE : unsigned long
{
	MYTEAMSPACEFILE_WM_USER_MSG = WM_USER + 10200,
	MYTEAMSPACEFILE_WM_USER_MSG_OPEN,	//��
	MYTEAMSPACEFILE_WM_USER_MSG_LINK,							//��������
	MYTEAMSPACEFILE_WM_USER_MSG_DOWNLOAD,						//����
	MYTEAMSPACEFILE_WM_USER_MSG_TRANSFERTOMYFILE,				//���浽�ҵ�����
	MYTEAMSPACEFILE_WM_USER_MSG_COPYMOVE,						//�����ƶ�
	MYTEAMSPACEFILE_WM_USER_MSG_DELETE,							//ɾ��
	MYTEAMSPACEFILE_WM_USER_MSG_RENAME,							//������
	MYTEAMSPACEFILE_WM_USER_MSG_VIEWVERSION,					//�鿴�汾
	MYTEAMSPACEFILE_WM_USER_MSG_AUTHORISE,						//�ļ�����Ȩ
	MYTEAMSPACEFILE_WM_USER_MSG_LOOK,							//�鿴����
	MYTEAMSPACEFILE_WM_USER_MSG_DISBANDTEAM,					//��ɢ�Ŷӿռ�
	MYTEAMSPACEFILE_WM_USER_MSG_EXITTEAM,						//�˳��Ŷӿռ�
	MYTEAMSPACEFILE_WM_USER_MSG_MODIFYADMIN,					//���ӵ����
	MYTEAMSPACEFILE_WM_USER_MSG_MANAGEMEMBER,					//�����Ա
	MYTEAMSPACEFILE_WM_USER_MSG_VIEWMEMBER,						//�鿴��Ա
	MYTEAMSPACEFILE_WM_USER_MSG_OPENTEAMSPACE,					//���Ŷӿռ�
};

enum MYSHARE_USER_MESSAGE : unsigned long
{
    MYSHARE_WM_USER_MSG_SETSHARE = WM_USER + 10300, //����
    MYSHARE_WM_USER_MSG_CANCELSHARE,	                         //ȡ������
};

enum SHARE2ME_USER_MESSAGE : unsigned long
{
    SHARE2ME_WM_USER_MSG_OPEN = WM_USER + 10400,		//��
	SHARE2ME_WM_USER_MSG_DOWNLOAD,						//����
    SHARE2ME_WM_USER_MSG_SAVE,							//���浽�ҵ��ļ�
	SHARE2ME_WM_USER_MSG_EXIT,							//�˳�����
	SHARE2ME_WM_USER_MSG_VERSION,						//�鿴�汾
	SHARE2ME_WM_USER_MSG_LINK,							//��������
	SHARE2ME_WM_USER_MSG_COPYMOVE,						//����/�ƶ�
	SHARE2ME_WM_USER_MSG_DELETE,						//ɾ��
	SHARE2ME_WM_USER_MSG_RENAME							//������
};

enum BACKUP_USER_MESSAGE : unsigned long
{
	BACKUP_WM_USER_MSG = WM_USER + 10500,
	BACKUP_WM_USER_MSG_EDIT,							//�༭
	BACKUP_WM_USER_MSG_DELETE,							//ɾ��
};

#define CRUMBBTN_WM_USER_MSG_CLICK WM_USER + 11000     //���м��Ϣ

class MenuElementUI;
class CMenuUI:
	public CWindowWnd
{
public:
	enum menuLayout : unsigned long
	{
		TEXT_ALIGN_LEFT_DEFAULT = 0, //�ı�Ĭ�϶���
		TEXT_ALIGN_CENTER,	//�ı�������ʾ
		TEXT_ALIGN_RIGHT,	//�ı��Ҷ���
		
		ITEM_DEFAULT_HEIGHT = 32, //ÿһ��item��Ĭ�ϸ߶�
		ITEM_DEFAULT_ICON_WIDTH = 20, //Ĭ��ͼ����ռ�߶�
		ITEM_DEFAULT_WIDTH = 200, //���ڵ�Ĭ�ϸ߶�

		DEFAULT_TEXT_COLOR = 0xFF000000, //Ĭ��������ɫ
		DEFAULT_LINE_COLOR = 0xFFE5E5E5, //Ĭ�Ϸָ�����ɫ
		DEFAULT_LINE_HEIGHT = 0, //Ĭ�Ϸָ�����ռ�߶�

		HEADER_DEFAULT_HEIGHT = 10, //Ĭ�ϵı�ͷ�߶�
	};

public:
	CMenuUI(menuLayout textalign = TEXT_ALIGN_LEFT_DEFAULT);
	virtual ~CMenuUI(void);
	LPCTSTR GetWindowClassName() const { return _T("MenuUI"); };
	UINT GetClassStyle() const { return UI_CLASSSTYLE_FRAME | CS_DBLCLKS; }
	LPVOID GetInterface(LPCTSTR pstrName);
	void OnFinalMessage(HWND hWnd);
	LRESULT OnCreate(UINT uMsg, WPARAM wParam , LPARAM lParam, BOOL& bHandle);
	LRESULT OnDestroy(UINT uMsg, WPARAM wParam , LPARAM lParam, BOOL& bHandle);
	LRESULT OnKillFocus(UINT uMsg, WPARAM wParam , LPARAM lParam, BOOL& bHandle);
	LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);
	
	/*****
	*��		�ܣ����ܺ����� ��Ӳ˵���
	*��		����contentѡ����ı���Ϣ�� itemIDѡ���ӦID�� iconImageNameѡ��
	*��	��	ֵ��֧����չʱ������չ����ָ�룬 ���򷵻�NULL
	*****/
	CMenuUI* Add(LPCTSTR contentKey, UINT itemID = 0,LPCTSTR iconImageName = _T(""), bool isEnable=true,DWORD dwColor = 0xFF000000, int height = 0, 
		bool extend = false,std::wstring fileInfo=L"");//֧��html�ı�
	CMenuUI* Add(MenuElementUI* pctrl, int height = 0, bool extend = false);
	/****** 
	*��		�ܣ����Ʒָ���
	*��		����color�ָ�����ʾ��ɫ
	******/
	void AddLine(DWORD color = DEFAULT_LINE_COLOR);
	void SetItemHeight(int height = ITEM_DEFAULT_HEIGHT);
	void SetItemWidth(int width = ITEM_DEFAULT_WIDTH);
	/***** 
	*��		�ܣ��������ź�
	*��		����param�ź�����
	*****/
	void ReceiveCloseMsg(ContextMenuParam& param);
	/*****
	*��		�ܣ����ڳ�ʼ���� ��ɻ�����������
	*��		����pt������ʾ�Ļ��㣬 hParent�����ھ��
	*pOwner����ѡ� nLeftPos ���Ԥ��ͼ����
	*****/
	void InitWnd(POINT& pt, HWND hParent = NULL, MenuElementUI* pOwner = NULL, int iLeftPreWidth = ITEM_DEFAULT_WIDTH);
	void UpdateWnd(bool isTrayIcon=false);//���´���
	POINT GetPos();
	void SetPos();
	void AddMenuHeader(int height = HEADER_DEFAULT_HEIGHT, LPCTSTR text = _T(""), LPCTSTR bkimagename = _T("..\\img\\TrayMenu.png"));
	std::wstring GetMenuContent(std::wstring key);
public:
	CPaintManagerUI m_pm;
	HWND m_hParent; //������
	CListUI* m_list; //Ĭ�����ԡ�inset=\"2,2,2,2\" bkcolor=\"0xFFE9FFFF\" itemtextpadding=\"30,0,0,0\" header=\"hidden\" itemshowhtml=\"true\""
private:
	MenuElementUI* m_pOwner; //����������ѡ��
	POINT m_basePoint; //�������ϽǶ���λ��

	int m_itemHeight; //ÿһ��ĸ߶�
	int m_itemWidth; //���ڵĿ��
	int m_wndHeight; //���ڵĸ߶�
	int m_leftWidth; //���ͼ����ռ�Ŀ��

	menuLayout m_textAlign; //�ı����뷽ʽ�� Ĭ�������
};

class MenuElementUI : public CListContainerElementUI
{
public:
	MenuElementUI(UINT ID = 0,bool isEnable=true,int64_t ownerID=-1,int64_t fileID=-1,bool isShowToolTip=false);
	~MenuElementUI(){}
	LPCTSTR GetClass() const { return _T("MenuElementUI"); }
	LPVOID GetInterface(LPCTSTR pstrName);
	virtual void DoEvent(TEventUI& event);
	virtual void DoPaint(HDC hDC, const RECT& rcPaint);

	void SetText(LPCTSTR pstrText, DWORD textColor, CMenuUI::menuLayout textalign = CMenuUI::TEXT_ALIGN_LEFT_DEFAULT);
	void SetIcon(int width, LPCTSTR iconName = _T("")); //16*16BMPͼƬ
	void SetExplandIcon(LPCTSTR iconName = ExplandIcon); //16*16BMPͼƬ
	void SetLine(bool isline, DWORD col, int width);
	bool AddCtrl(CControlUI* pControl);//����Զ���ؼ�
	CMenuUI* CreateWnd(POINT& pt, HWND hParent, int nLeftPos);
	void DrawItemBk(HDC hDC, const RECT& rcItem);
public:
	UINT uID; //�˵�ID
	int64_t uOwenrID; //owenrID
	int64_t uFileID; //fileID
	bool m_isShowToolTip;
	enum 
	{
		ICON_LEFT_WIDTH = 64,  //ͼ�����߾�
		EXPLAND_ICON_WIDTH = 20, 
	};

private:
	CMenuUI* m_pWnd;//��������
	CHorizontalLayoutUI *m_Layout;
	CLabelUI* m_Text;//��ʾ�ı�
	CLabelUI* m_Icon;//ͼ��
	bool m_bDrawLine;//�Ƿ���
	bool m_bextend;
	DWORD m_lineColor; //�ָ�����ɫ
	int m_iconWidth;
	bool m_isEnable; //�Ƿ񲻿ɵ��
};

class WndsVector
{
private:
	typedef std::vector<CMenuUI*> ReceiversVector;
	static ReceiversVector receiverWnd;
public:
	static HWND rootHwnd;
	/*****
	*��		�ܣ�������ָ��ѹ�봰�ڶ���
	****/
	static inline void AddWnd(CMenuUI* ptr)
	{
		receiverWnd.push_back(ptr);
	}

	/*****
	*��		�ܣ��������źŷ��͸������е�ÿһ������
	*****/
	static void BroadCast(ContextMenuParam& param)
	{
		if (receiverWnd.empty())
			return;

		ReceiversVector::reverse_iterator it = receiverWnd.rbegin();
		for (; it != receiverWnd.rend(); it++)
		{
			if (*it != NULL)
			{
				(*it)->ReceiveCloseMsg(param);
			}
		}
	}

	/*****
	*��		�ܣ����Ҵ��ڶ����а����þ���Ĵ�����ָ��
	*****/
	static CMenuUI* FindWndClass(HWND hwnd)
	{
		if (receiverWnd.empty())
		{
			return NULL;
		}

		ReceiversVector::iterator it = receiverWnd.begin();
		for (; it != receiverWnd.end(); it++)
		{
			if (*it != NULL && (*it)->GetHWND() == hwnd)
			{
				return *it;
			}
		}

		return NULL;
	}

	static inline void RemoveAll()
	{
		receiverWnd.clear();
	}
};




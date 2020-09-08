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
* 注释：托盘响应ID
*/
enum TRAY_ICON_USER_MESSAGE : unsigned long
{
	TRAY_ICON_WM_USER_MSG = WM_USER + 10000,
	TRAY_ICON_WM_USER_MSG_ABOUT, //关于
	TRAY_ICON_WM_USER_MSG_QUIT,	//退出
	TRAY_ICON_WM_USER_MSG_INFO,	//信息
	TRAY_ICON_WM_USER_MSG_HELP,	//帮助
	TRAY_ICON_WM_USER_MSG_COLLECT_LOG, //收集日记
	TRAY_ICON_WM_USER_MSG_OPEN_WEB, //打开web 网盘
	TRAY_ICON_WM_USER_MSG_OPTION, //选项
	TRAY_ICON_WM_USER_MSG_OPERATOR, //暂停/恢复上传下载任务
	TRAY_ICON_WM_USER_MSG_LANGUAGE,	//主窗口设置语言
};

enum MYFILE_USER_MESSAGE : unsigned long
{
	MYFILE_WM_USER_MSG = WM_USER + 10100,
	MYFILE_WM_USER_MSG_OPEN,	//打开
	MYFILE_WM_USER_MSG_SHARE,							//共享
	MYFILE_WM_USER_MSG_LINK,							//分享链接
	MYFILE_WM_USER_MSG_DOWNLOAD,						//下载
	MYFILE_WM_USER_MSG_SETSYNC,							//设置同步
	MYFILE_WM_USER_MSG_CANCELSYNC,						//取消同步
	MYFILE_WM_USER_MSG_TRANSFERTOTEAMSPACE,				//转发到团队空间
	MYFILE_WM_USER_MSG_COPYMOVE,						//复制移动
	MYFILE_WM_USER_MSG_DELETE,							//删除
	MYFILE_WM_USER_MSG_RENAME,							//重命名
	MYFILE_WM_USER_MSG_VIEWVERSION,						//查看版本
};

enum MYTEAMSPACE_USER_MESSAGE : unsigned long
{
	MYTEAMSPACEFILE_WM_USER_MSG = WM_USER + 10200,
	MYTEAMSPACEFILE_WM_USER_MSG_OPEN,	//打开
	MYTEAMSPACEFILE_WM_USER_MSG_LINK,							//分享链接
	MYTEAMSPACEFILE_WM_USER_MSG_DOWNLOAD,						//下载
	MYTEAMSPACEFILE_WM_USER_MSG_TRANSFERTOMYFILE,				//保存到我的云盘
	MYTEAMSPACEFILE_WM_USER_MSG_COPYMOVE,						//复制移动
	MYTEAMSPACEFILE_WM_USER_MSG_DELETE,							//删除
	MYTEAMSPACEFILE_WM_USER_MSG_RENAME,							//重命名
	MYTEAMSPACEFILE_WM_USER_MSG_VIEWVERSION,					//查看版本
	MYTEAMSPACEFILE_WM_USER_MSG_AUTHORISE,						//文件夹授权
	MYTEAMSPACEFILE_WM_USER_MSG_LOOK,							//查看详情
	MYTEAMSPACEFILE_WM_USER_MSG_DISBANDTEAM,					//解散团队空间
	MYTEAMSPACEFILE_WM_USER_MSG_EXITTEAM,						//退出团队空间
	MYTEAMSPACEFILE_WM_USER_MSG_MODIFYADMIN,					//变更拥有者
	MYTEAMSPACEFILE_WM_USER_MSG_MANAGEMEMBER,					//管理成员
	MYTEAMSPACEFILE_WM_USER_MSG_VIEWMEMBER,						//查看成员
	MYTEAMSPACEFILE_WM_USER_MSG_OPENTEAMSPACE,					//打开团队空间
};

enum MYSHARE_USER_MESSAGE : unsigned long
{
    MYSHARE_WM_USER_MSG_SETSHARE = WM_USER + 10300, //共享
    MYSHARE_WM_USER_MSG_CANCELSHARE,	                         //取消共享
};

enum SHARE2ME_USER_MESSAGE : unsigned long
{
    SHARE2ME_WM_USER_MSG_OPEN = WM_USER + 10400,		//打开
	SHARE2ME_WM_USER_MSG_DOWNLOAD,						//下载
    SHARE2ME_WM_USER_MSG_SAVE,							//保存到我的文件
	SHARE2ME_WM_USER_MSG_EXIT,							//退出共享
	SHARE2ME_WM_USER_MSG_VERSION,						//查看版本
	SHARE2ME_WM_USER_MSG_LINK,							//分享链接
	SHARE2ME_WM_USER_MSG_COPYMOVE,						//复制/移动
	SHARE2ME_WM_USER_MSG_DELETE,						//删除
	SHARE2ME_WM_USER_MSG_RENAME							//重命名
};

enum BACKUP_USER_MESSAGE : unsigned long
{
	BACKUP_WM_USER_MSG = WM_USER + 10500,
	BACKUP_WM_USER_MSG_EDIT,							//编辑
	BACKUP_WM_USER_MSG_DELETE,							//删除
};

#define CRUMBBTN_WM_USER_MSG_CLICK WM_USER + 11000     //面包屑消息

class MenuElementUI;
class CMenuUI:
	public CWindowWnd
{
public:
	enum menuLayout : unsigned long
	{
		TEXT_ALIGN_LEFT_DEFAULT = 0, //文本默认对齐
		TEXT_ALIGN_CENTER,	//文本居中显示
		TEXT_ALIGN_RIGHT,	//文本右对齐
		
		ITEM_DEFAULT_HEIGHT = 32, //每一个item的默认高度
		ITEM_DEFAULT_ICON_WIDTH = 20, //默认图标所占高度
		ITEM_DEFAULT_WIDTH = 200, //窗口的默认高度

		DEFAULT_TEXT_COLOR = 0xFF000000, //默认字体颜色
		DEFAULT_LINE_COLOR = 0xFFE5E5E5, //默认分割线颜色
		DEFAULT_LINE_HEIGHT = 0, //默认分割线所占高度

		HEADER_DEFAULT_HEIGHT = 10, //默认的表头高度
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
	*功		能：功能函数， 添加菜单项
	*参		数：content选项的文本信息， itemID选项对应ID， iconImageName选项
	*返	回	值：支持扩展时返回扩展窗口指针， 否则返回NULL
	*****/
	CMenuUI* Add(LPCTSTR contentKey, UINT itemID = 0,LPCTSTR iconImageName = _T(""), bool isEnable=true,DWORD dwColor = 0xFF000000, int height = 0, 
		bool extend = false,std::wstring fileInfo=L"");//支持html文本
	CMenuUI* Add(MenuElementUI* pctrl, int height = 0, bool extend = false);
	/****** 
	*功		能：绘制分割线
	*参		数：color分割线显示颜色
	******/
	void AddLine(DWORD color = DEFAULT_LINE_COLOR);
	void SetItemHeight(int height = ITEM_DEFAULT_HEIGHT);
	void SetItemWidth(int width = ITEM_DEFAULT_WIDTH);
	/***** 
	*功		能：处理窗口信号
	*参		数：param信号数据
	*****/
	void ReceiveCloseMsg(ContextMenuParam& param);
	/*****
	*功		能：窗口初始化， 完成基本属性设置
	*参		数：pt窗口显示的基点， hParent父窗口句柄
	*pOwner所属选项， nLeftPos 左边预留图标宽度
	*****/
	void InitWnd(POINT& pt, HWND hParent = NULL, MenuElementUI* pOwner = NULL, int iLeftPreWidth = ITEM_DEFAULT_WIDTH);
	void UpdateWnd(bool isTrayIcon=false);//更新窗口
	POINT GetPos();
	void SetPos();
	void AddMenuHeader(int height = HEADER_DEFAULT_HEIGHT, LPCTSTR text = _T(""), LPCTSTR bkimagename = _T("..\\img\\TrayMenu.png"));
	std::wstring GetMenuContent(std::wstring key);
public:
	CPaintManagerUI m_pm;
	HWND m_hParent; //父窗口
	CListUI* m_list; //默认属性”inset=\"2,2,2,2\" bkcolor=\"0xFFE9FFFF\" itemtextpadding=\"30,0,0,0\" header=\"hidden\" itemshowhtml=\"true\""
private:
	MenuElementUI* m_pOwner; //窗口所属的选项
	POINT m_basePoint; //窗口左上角顶点位置

	int m_itemHeight; //每一项的高度
	int m_itemWidth; //窗口的宽度
	int m_wndHeight; //窗口的高度
	int m_leftWidth; //左边图标所占的宽度

	menuLayout m_textAlign; //文本对齐方式， 默认左对齐
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
	void SetIcon(int width, LPCTSTR iconName = _T("")); //16*16BMP图片
	void SetExplandIcon(LPCTSTR iconName = ExplandIcon); //16*16BMP图片
	void SetLine(bool isline, DWORD col, int width);
	bool AddCtrl(CControlUI* pControl);//添加自定义控件
	CMenuUI* CreateWnd(POINT& pt, HWND hParent, int nLeftPos);
	void DrawItemBk(HDC hDC, const RECT& rcItem);
public:
	UINT uID; //菜单ID
	int64_t uOwenrID; //owenrID
	int64_t uFileID; //fileID
	bool m_isShowToolTip;
	enum 
	{
		ICON_LEFT_WIDTH = 64,  //图标的左边距
		EXPLAND_ICON_WIDTH = 20, 
	};

private:
	CMenuUI* m_pWnd;//弹出窗体
	CHorizontalLayoutUI *m_Layout;
	CLabelUI* m_Text;//显示文本
	CLabelUI* m_Icon;//图标
	bool m_bDrawLine;//是否划线
	bool m_bextend;
	DWORD m_lineColor; //分割线颜色
	int m_iconWidth;
	bool m_isEnable; //是否不可点击
};

class WndsVector
{
private:
	typedef std::vector<CMenuUI*> ReceiversVector;
	static ReceiversVector receiverWnd;
public:
	static HWND rootHwnd;
	/*****
	*功		能：将窗口指针压入窗口队列
	****/
	static inline void AddWnd(CMenuUI* ptr)
	{
		receiverWnd.push_back(ptr);
	}

	/*****
	*功		能：将窗口信号发送给队列中的每一个窗口
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
	*功		能：查找窗口队列中包含该句柄的窗口类指针
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




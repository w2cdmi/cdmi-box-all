#pragma  once
#include "SimpleNoticeFrame.h"
#include "UserContext.h"

#define RIGHTICO ( L"..\\Image\\ic_diago_info_success.png")
#define INFOICO ( L"..\\Image\\ic_diago_info_inform.png")
#define ASKICO ( L"..\\Image\\ic_diago_info_quoto.png")
#define WARNINGICO ( L"..\\Image\\ic_diago_info_wranning.png")
#define ERRORICO ( L"..\\Image\\ic_diago_info_wrong.png")
#define NOTICE_FRAME_WINDOW_MAX_HIGHT 200
#define NOTICE_FRAME_CHANGE_DISTANCE 10

namespace Onebox
{
	enum ShowType
	{
		NotModal,
		Modal
	};

	enum FrameType
	{
		Confirm,
		Choose
	};

	struct NoticeData
	{
		NoticeType noticeType;
		std::wstring noticeTitleCode;
		std::wstring noticeCode;

		NoticeData():noticeType(Info),noticeTitleCode(L""),noticeCode(L"")
		{
		}
		NoticeData(NoticeType noticetype,std::wstring noticetitle,std::wstring noticemsg):
			noticeType(noticetype),
			noticeTitleCode(noticetitle),
			noticeCode(noticemsg)
		{
		}
		NoticeData(NoticeType noticetype,std::wstring noticemsg):
			noticeType(noticetype),
			noticeTitleCode(L""),
			noticeCode(noticemsg)
		{
		}

		bool operator==(const NoticeData&  noticeData)const
		{
			if (noticeType ==noticeData.noticeType && noticeTitleCode== noticeData.noticeTitleCode &&  noticeCode==noticeData.noticeCode)
			{
				return true;
			}

			return false;
		}
	};

	class NoticeFrame : public WindowImplBase
	{
		DUI_DECLARE_MESSAGE_MAP() 

	public:
		NoticeFrame (HWND parent,UserContext* userContext=NULL);
		~NoticeFrame();

		virtual LPCTSTR GetWindowClassName(void) const;

		virtual CDuiString GetSkinFolder();

		virtual CDuiString GetSkinFile();

		virtual void OnFinalMessage( HWND hWnd );

		virtual CControlUI* CreateControl(LPCTSTR pstrClass);

		virtual bool InitLanguage(CControlUI* control);

		virtual LRESULT MessageHandler(UINT uMsg, WPARAM wParam, LPARAM lParam, bool& bHandled);

		virtual LRESULT OnSysCommand(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam);

		virtual void OnClick(DuiLib::TNotifyUI& msg);
	public:
		void ShowNoticeWindow(FrameType frameType,NoticeData& noticeData,ShowType showType=NotModal,bool bShowLink = false);

		static bool IsClickOk();

	private:
		std::wstring GetTitle(std::wstring MsgCode, ...);

		std::wstring GetMsg(std::wstring MsgCode, ...);

		void InitData(FrameType frameType, NoticeData noticeData,bool bShowLink);

		void RedrawWindow(int nNumber,CVerticalLayoutUI* LayoutMsg);

		void SetMessageLayout(int32_t ilen,CVerticalLayoutUI* LayoutMs,CRichEditUI* NoticeMsg);
	private:
		static bool m_bIsClickOk;
		HWND m_parentHwnd;
		UserContext* userContext_;
	public:
		FrameType m_frameType;
		NoticeData m_noticeData;
		ShowType m_showType;
	};

	class NoticeFrameMgr
	{
	public:
		NoticeFrameMgr(HWND parent,UserContext* userContext=NULL);
		~NoticeFrameMgr();
		NoticeFrame* Find(FrameType frameType,NoticeData& noticeData,ShowType showType);
		void Run(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,ShowType showType=NotModal,bool bShowLink=false);
		bool IsClickOk();
	private:
		HWND m_parent;
		UserContext* userContext_;
		std::vector<NoticeFrame*> m_vecFrame;
	};
}

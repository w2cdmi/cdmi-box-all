#pragma  once
#include <UIlib.h>
#include <boost/function.hpp>
#include <boost/bind.hpp>

#define RIGHTICO ( L"..\\Image\\ic_diago_info_success.png")
#define INFOICO ( L"..\\Image\\ic_diago_info_inform.png")
#define ASKICO ( L"..\\Image\\ic_diago_info_quoto.png")
#define WARNINGICO ( L"..\\Image\\ic_diago_info_wranning.png")
#define ERRORICO ( L"..\\Image\\ic_diago_info_wrong.png")
#define NOTICE_FRAME_WINDOW_MAX_HIGHT 200
#define NOTICE_FRAME_CHANGE_DISTANCE 10

using namespace DuiLib;
namespace Onebox
{
	enum NoticeType
	{
		Right,
		Info,
		Ask,
		Warning,
		Error
	};

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

		bool operator==(const NoticeData&  noticeData)
		{
			if (noticeType ==noticeData.noticeType && noticeTitleCode== noticeData.noticeTitleCode &&  noticeCode==noticeData.noticeCode)
			{
				return true;
			}

			return false;
		}
	};


	class NoticeImpl
	{
	public:
		virtual ~NoticeImpl(){};
		static NoticeImpl* getInstance(HWND mainWindow);
		virtual void showWindow(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,...)=0;
		virtual void showModal(FrameType frameType,NoticeType noticeType,std::wstring noticeTitle,std::wstring noticeCode,...)=0;
		static bool IsClickOk();

		FrameType m_frameType;
		NoticeData m_noticeData;
	private:
		static std::auto_ptr<NoticeImpl> instance_;;
	};
}
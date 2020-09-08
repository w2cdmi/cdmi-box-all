#pragma  once
#include <UIlib.h>
#include <stdint.h>
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

	class SimpleNoticeFrame 
	{
	public:
		SimpleNoticeFrame(CPaintManagerUI& parent);

		virtual ~SimpleNoticeFrame();

	public:
		void Show(NoticeType noticeType, std::wstring msgKey,...);

		void ShowMessage(const std::wstring& msg, NoticeType noticeType=Error);

		void SetPos();

	    void RestoreNoticeArea();

		std::wstring GetShowMsg(std::wstring msgKey);

		std::wstring GetMsg(std::wstring msgKey,...);

		int getNameWidth(const std::wstring& str_fileName, std::wstringstream& showText);
	private:
		CPaintManagerUI& paintManager_;
		int32_t m_areaWidth;
	};
}
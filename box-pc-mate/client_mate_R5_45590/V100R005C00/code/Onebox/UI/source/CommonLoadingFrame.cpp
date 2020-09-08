//#include "stdafxOnebox.h"
#include "UIlib.h"
#include <boost/thread.hpp>
#include "CommonLoadingFrame.h"
#include "UICommonDefine.h"
#include "InILanguage.h"

namespace Onebox
{
	using namespace DuiLib;
	enum LOADING_STATUS
	{
		LS_START,
		LS_COMPLETE
	};

	class CommonLoadingFrame::Impl : public WindowImplBase
	{
	public:
		Impl(const std::wstring& xmlFolder,invoker_type invoker)
			:xmlFolder_(xmlFolder)
			,invoker_(invoker)
			,status_(LS_START)
		{

		}

		virtual ~Impl()
		{

		}

	private:
		virtual CDuiString GetSkinFolder()
		{
			return xmlFolder_.c_str();
			//std::wstring xmlFolder_=iniLanguageHelper.GetSkinFolderPath();
			//return iniLanguageHelper.GetSkinFolderPath().c_str();
		}

		virtual CDuiString GetSkinFile()
		{
			return L"CommonLoadingFrame.xml";
		}

		virtual LPCTSTR GetWindowClassName(void) const
		{
			return L"CommonLoadingFrame";
		}

		virtual void InitWindow()
		{
			boost::thread(boost::bind(&Impl::invok, this));
		}

		void invok()
		{
			updateLoadingStatus(LS_START);
			invoker_();
			updateLoadingStatus(LS_COMPLETE);
			if (::IsWindow(m_hWnd))
			{
				PostMessage(WM_CLOSE);
			}
		}

		void updateLoadingStatus(LOADING_STATUS status)
		{
			boost::mutex::scoped_lock lock(mutex_);
			status_ = status;			
		}

		LOADING_STATUS getLoadingStatus()
		{
			boost::mutex::scoped_lock lock(mutex_);
			return status_;
		}

	private:
		std::wstring xmlFolder_;
		invoker_type invoker_;
		boost::mutex mutex_;
		LOADING_STATUS status_;
	};

	std::shared_ptr<CommonLoadingFrame> CommonLoadingFrame::create(const std::wstring& xmlFolder,HWND parent, invoker_type invoker)
	{
		return std::shared_ptr<CommonLoadingFrame>(new CommonLoadingFrame(xmlFolder,parent, invoker));
	}

	std::shared_ptr<CommonLoadingFrame> CommonLoadingFrame::create(const std::wstring& xmlFolder,HWND parent, RECT parentRect, invoker_type invoker)
	{
		return std::shared_ptr<CommonLoadingFrame>(new CommonLoadingFrame(xmlFolder,parent, parentRect, invoker));
	}

	CommonLoadingFrame::CommonLoadingFrame(const std::wstring& xmlFolder,HWND parent, CommonLoadingFrame::invoker_type invoker)
		:impl_(new Impl(xmlFolder,invoker))
	{
		impl_->Create(parent, L"CommonLoadingFrame", UI_WNDSTYLE_FRAME, WS_EX_STATICEDGE, 0, 0, 0, 0);
		impl_->CenterWindow();
		impl_->ShowModal();
		// delay 10 ms to release memory
		boost::this_thread::sleep(boost::posix_time::milliseconds(10));
	}

	CommonLoadingFrame::CommonLoadingFrame(const std::wstring& xmlFolder,HWND parent,RECT parentRect, CommonLoadingFrame::invoker_type invoker)
		:impl_(new Impl(xmlFolder,invoker))
	{
		impl_->Create(parent, L"CommonLoadingFrame", UI_WNDSTYLE_FRAME, WS_EX_STATICEDGE, 0, 0, 0, 0);
		impl_->CenterWindow(parentRect);
		impl_->ShowModal();
		// delay 10 ms to release memory
		boost::this_thread::sleep(boost::posix_time::milliseconds(10));
	}
}
#include "UIlib.h"
#include "DelayLoadingFrame.h"
#include "UICommonDefine.h"
#include "InILanguage.h"

namespace Onebox
{
	using namespace DuiLib;

	class LoadingWindow : public WindowImplBase
	{
	public:
		LoadingWindow(const std::wstring& xmlFolder, boost::thread& loadThread):xmlFolder_(xmlFolder), loadThread_(loadThread), closeFlag_(false)
		{
		}

		virtual ~LoadingWindow()
		{
		}

		virtual LRESULT HandleMessage(UINT uMsg, WPARAM wParam, LPARAM lParam)
		{
			if (!closeFlag_ && uMsg == WM_CLOSE)
			{
				return S_OK;
			}
			return WindowImplBase::HandleMessage(uMsg, wParam, lParam);
		}

	private:
		virtual CDuiString GetSkinFolder()
		{
			return xmlFolder_.c_str();
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
			boost::thread(boost::bind(&LoadingWindow::load, this));
		}

	private:
		void load()
		{
			loadThread_.join();
			if (::IsWindow(m_hWnd))
			{
				closeFlag_ = true;
				PostMessage(WM_CLOSE);
			}
		}

	private:
		std::wstring xmlFolder_;
		boost::thread& loadThread_;
		bool closeFlag_;
	};

	class DelayLoadingFrameImpl : public DelayLoadingFrame
	{
	public:
		DelayLoadingFrameImpl(const std::wstring& xmlFolder, HWND parent, RECT parentRect, boost::thread& loadThread)
		{
			if(0==parentRect.left)
			{
				loadThread.join();
				return;
			}
			std::auto_ptr<LoadingWindow> loadWindow(new LoadingWindow(xmlFolder, loadThread));
			loadWindow->Create(parent, L"CommonLoadingFrame", UI_WNDSTYLE_FRAME, WS_EX_STATICEDGE, 0, 0, 0, 0);
			loadWindow->CenterWindow(parentRect);
			loadWindow->ShowModal();
		}

		virtual ~DelayLoadingFrameImpl()
		{
		}
	};

	std::auto_ptr<DelayLoadingFrame> DelayLoadingFrame::create(const std::wstring& xmlFolder, HWND parent, RECT parentRect, boost::thread& loadThread)
	{
		return std::auto_ptr<DelayLoadingFrame> (new DelayLoadingFrameImpl(xmlFolder, parent, parentRect, loadThread));
	}
}
#ifndef _ONEBOX_DelayLoading_H_
#define _ONEBOX_DelayLoading_H_

#include <boost/thread.hpp>

namespace Onebox
{
	class DelayLoadingFrame
	{
	public:
		virtual ~DelayLoadingFrame(){}

		static std::auto_ptr<DelayLoadingFrame> create(const std::wstring& xmlFolder, HWND parent, RECT parentRect, boost::thread& loadThread);

	};
}

#endif
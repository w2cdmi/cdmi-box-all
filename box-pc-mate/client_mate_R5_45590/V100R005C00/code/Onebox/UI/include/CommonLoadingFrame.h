#pragma once

namespace Onebox
{
	class CommonLoadingFrame
	{
	public:
		typedef boost::function<void(void)> invoker_type;

		static std::shared_ptr<CommonLoadingFrame> create(const std::wstring& xmlFolder,HWND parent, invoker_type invoker);

		static std::shared_ptr<CommonLoadingFrame> create(const std::wstring& xmlFolder,HWND parent, RECT parentRect, invoker_type invoker);

		virtual ~CommonLoadingFrame() {}

	private:
		explicit CommonLoadingFrame(const std::wstring& xmlFolder,HWND parent, invoker_type invoker);

		explicit CommonLoadingFrame(const std::wstring& xmlFolder,HWND parent, RECT parentRect, invoker_type invoker);
	private:
		class Impl;
		std::shared_ptr<Impl> impl_;
	};
}
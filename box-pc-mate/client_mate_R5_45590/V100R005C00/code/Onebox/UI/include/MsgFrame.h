#ifndef _ONEBOX_MSGFRAME_NOTIFY_H_
#define _ONEBOX_MSGFRAME_NOTIFY_H_

namespace Onebox
{
	static const int MAX_MSG_ITEM_NUM = 99;

	static const std::wstring MAX_MSG_DESC = L"99+";

	class MsgFrame
	{
	public:
		virtual ~MsgFrame(){}

		static MsgFrame* getInstance(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;

//		virtual void rightClick(unsigned long msg) = 0;

		virtual void reloadCache() = 0;

		virtual void refreshClick() = 0;
	private:
		static std::auto_ptr<MsgFrame> instance_;
	};
}

#endif

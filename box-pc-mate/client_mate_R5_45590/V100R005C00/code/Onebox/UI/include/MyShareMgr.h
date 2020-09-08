#ifndef _ONEBOX_MYSHARE_NOTIFY_H_
#define _ONEBOX_MYSHARE_NOTIFY_H_

namespace Onebox
{
	class MyShareMgr
	{
	public:
		virtual ~MyShareMgr(){}

        static MyShareMgr* getInstance(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;

		virtual void reloadCache() = 0;

        virtual bool reloadThumb(const std::string& thumbKey) = 0;

		virtual void flushClick(TNotifyUI& msg) = 0;
    private:
        static MyShareMgr* instance_;
	};
}

#endif
#ifndef _ONEBOX_FULLBACKUP_NOTIFY_H_
#define _ONEBOX_FULLBACKUP_NOTIFY_H_

namespace Onebox
{
	class FullBackUpMgr
	{
	public:
		virtual ~FullBackUpMgr(){}

		static FullBackUpMgr* getInstance(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void showError(int32_t ret) = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;

		virtual void updateTaskProcess() = 0;

		virtual void updateErrorCount(int32_t errorCount) = 0;

		virtual void stop() = 0;

	private:
		static FullBackUpMgr* instance_;
	};
}

#endif
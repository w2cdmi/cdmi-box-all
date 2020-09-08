#ifndef _ONEBOX_SHARE2ME_NOTIFY_H_
#define _ONEBOX_SHARE2ME_NOTIFY_H_

namespace Onebox
{
	class Share2MeMgr
	{
	public:
		virtual ~Share2MeMgr(){}

		static Share2MeMgr* getInstance(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;

		virtual void reloadCache(int64_t ownerId, int64_t curId, bool isFlush = false) = 0;

		virtual bool isTheCurPage(int64_t ownerId, int64_t curId) = 0;
		
		//确定需要刷时才可调用
		virtual void reloadCache() = 0;

		virtual void showPage(int64_t ownerId, int64_t fileId) = 0;

		virtual void showPage(int64_t ownerId, std::wstring& selectFilename) = 0;

		virtual bool reloadThumb(const std::string& thumbKey) = 0;

		virtual void setPageFocus() = 0;

		virtual void flushClick(TNotifyUI& msg) = 0;

	private:
		static Share2MeMgr* instance_;
	};
}

#endif

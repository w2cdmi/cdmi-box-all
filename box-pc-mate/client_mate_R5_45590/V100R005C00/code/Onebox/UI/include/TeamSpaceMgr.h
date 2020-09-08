
#ifndef _ONEBOX_TeamSpaceMgr_H_
#define _ONEBOX_TeamSpaceMgr_H_

namespace Onebox
{
	class TeamSpaceMgr
	{
	public:
		virtual ~TeamSpaceMgr(){}

		static TeamSpaceMgr* getInstance(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;

		virtual void showPage(UserContext* userContext, int64_t pageId, int64_t selectId = -1) = 0;

		virtual void showPage(UserContext* userContext, int64_t pageId, std::wstring& selectFilename) = 0;

		virtual void enterTeamspace(int64_t selectTeamId) = 0;
		
		virtual void reloadCache(int64_t ownerId, int64_t curId, bool isFlush = false) = 0;

		virtual bool isTheCurPage(int64_t ownerId, int64_t curId) = 0;
		
		//确定需要刷时才可调用
		virtual void reloadCache() = 0;
	
		virtual void reloadAllCache(int64_t ownerId, int64_t curId, bool isFlush = false) = 0;

		virtual bool reloadThumb(const std::string& thumbKey) = 0;

		virtual void setPageFocus() = 0;

		virtual void updateClick(TNotifyUI& msg) = 0;

	private:
		static TeamSpaceMgr* instance_;
	};
}

#endif
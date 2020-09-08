#ifndef _ONEBOX_LEFTREGION_NOTIFY_H_
#define _ONEBOX_LEFTREGION_NOTIFY_H_

namespace Onebox
{
	enum CurPageType
	{
		Page_MyFile,
		Page_Share2Me,
		Page_MyShare,
		Page_TeamSpace,
		Page_Transfers,
		Page_Backup,
		Page_Other
	};

	class LeftRegionMgr
	{
	public:
		virtual ~LeftRegionMgr(){}

		static LeftRegionMgr* getInstance(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;

		virtual CurPageType getCurPageType() = 0;

		virtual void setCurPageType(CurPageType type) = 0;

		virtual void addReloadInfo(int64_t ownerId, int64_t dirId) = 0;

		virtual void addReloadInfo(UserContextType userType, int64_t ownerId, int64_t dirId) = 0;

		virtual void reloadThumb(const std::string& thumbKey) = 0;

		virtual bool isInHistory(CurPageType type) = 0;

		virtual void setSkipReload(bool isSkip) = 0;

		virtual bool isSkipReload() = 0;

    private:
        static LeftRegionMgr* instance_;
	};
}

#endif

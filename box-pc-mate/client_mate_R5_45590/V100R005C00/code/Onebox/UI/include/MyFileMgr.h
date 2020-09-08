#ifndef _ONEBOX_MYFILE_NOTIFY_H_
#define _ONEBOX_MYFILE_NOTIFY_H_

namespace Onebox
{
	struct MyFilesDirNode
	{
		bool isListView;
		std::wstring keyWord;
		int64_t ownerId;
		std::wstring ownerName;
		UIFileNode fileData;

		MyFilesDirNode():isListView(true), keyWord(L""), ownerId(-1), ownerName(L"")
		{
			fileData.basic.id = -1;
			fileData.basic.name = L"";
		}

		bool operator == (MyFilesDirNode& des) const
		{
			if(isListView!=des.isListView) return false;
			if(keyWord!=des.keyWord) return false;
			if(fileData.basic.id!=des.fileData.basic.id) return false;
			if(ownerId!=des.ownerId) return false;
			return true;
		}
	};

	class MyFileMgr
	{
	public:
		virtual ~MyFileMgr(){}

		static MyFileMgr* getInstance(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;;

		virtual void reloadCache(int64_t ownerId, int64_t curId, bool isFlush = false) = 0;

		virtual bool isTheCurPage(int64_t ownerId, int64_t curId) = 0;
		
		//确定需要刷时才可调用
		virtual void reloadCache() = 0;

		virtual void reloadAllCache(int64_t ownerId, int64_t curId, bool isFlush = false) = 0;

		virtual void showPage(int64_t pageId, int64_t selectId = -1) = 0;

		virtual void showPage(int64_t pageId, std::wstring& selectFileName) = 0;

		virtual bool reloadThumb(const std::string& thumbKey) = 0;

		virtual void updateClick(TNotifyUI& msg) = 0;

		virtual void setPageFocus() = 0;
	private:
		static MyFileMgr* instance_;
	};
}

#endif

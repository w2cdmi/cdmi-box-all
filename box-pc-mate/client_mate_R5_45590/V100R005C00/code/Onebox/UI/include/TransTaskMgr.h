#ifndef _ONEBOX_TRANSTASK_H_
#define _ONEBOX_TRANSTASK_H_

namespace Onebox
{
	#define WM_CUSTOM_TRANSTASK_AFTERADDTASK (WM_USER+100) // WPARAM (0 for upload, 1 for download),  LPARAM(0 for success, none zero for error code)
	#define WM_CUSTOM_TRANSTASK_LOADNEWTASK (WM_USER+101) // WPARAM (a pointer of task id)
	#define WM_CUSTOM_TRANSTASK_ITEMERRORSTATECLICK (WM_USER+102) // WPARAM (a pointer of item)

	class TransTaskMgr
	{
	public:
		virtual ~TransTaskMgr(){}

		static TransTaskMgr* create(UserContext* context, CPaintManagerUI& paintManager);

		virtual void initData() = 0;

		virtual void executeFunc(const std::wstring& funcName, TNotifyUI& msg) = 0;

		virtual void updateTaskStatus(const std::wstring& group, AsyncTransStatus status) = 0;

		virtual void updateTask(const std::wstring& group, const int64_t transedSize, const int64_t totalSize) = 0;

		virtual void updateTransSpeed(const int64_t uploadSpeed, const int64_t downloadSpeed) = 0;

		virtual void updateTaskSize(const std::wstring& group, const int64_t totalSize) = 0;

		virtual void SetTransNumber(int64_t transnumber) = 0;

		virtual int64_t GetTransNumber() = 0;

		virtual void SetTransfersListTip( int64_t transdiff ) = 0;

		static int32_t upload(const std::list<std::wstring>& nodes, const UIFileNode& remoteParent);

		static int32_t download(const std::list<UIFileNode>& nodes, const std::wstring& localParent, bool openAfterComplete = false);

		static int32_t pauseAllTask();

		static int32_t resumeAllTask();

	public:
		virtual void itemErrorStateClick(CControlUI* pItem) = 0;

	protected:
		int32_t pageLimit_;

	private:
		static TransTaskMgr* instance_;
	};
}

#endif

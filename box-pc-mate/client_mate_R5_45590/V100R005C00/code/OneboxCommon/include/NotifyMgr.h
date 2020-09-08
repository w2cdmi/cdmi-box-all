#ifndef _ONEBOX_NOTIFY_MGR_H_
#define _ONEBOX_NOTIFY_MGR_H_

#include "UserContext.h"
#include "NotifyMsg.h"

struct NOTIFY_PARAM 
{
	int32_t type;
	std::wstring msg1;
	std::wstring msg2;
	std::wstring msg3;
	std::wstring msg4;
	std::wstring msg5;
	std::wstring msg6;
	std::wstring msg7;

	NOTIFY_PARAM()
		:type(0)
		,msg1(L"")
		,msg2(L"")
		,msg3(L"")
		,msg4(L"")
		,msg5(L"")
		,msg6(L"")
		,msg7(L""){}

	NOTIFY_PARAM(int32_t type_, 
		const std::wstring& msg1_ = L"", 
		const std::wstring& msg2_ = L"", 
		const std::wstring& msg3_ = L"", 
		const std::wstring& msg4_ = L"", 
		const std::wstring& msg5_ = L"", 
		const std::wstring& msg6_ = L"", 
		const std::wstring& msg7_ = L"")
		:type(type_)
		,msg1(msg1_)
		,msg2(msg2_)
		,msg3(msg3_)
		,msg4(msg4_)
		,msg5(msg5_)
		,msg6(msg6_)
		,msg7(msg7_){}
};

class NotifyMgr
{
public:
	virtual ~NotifyMgr(){}

	static NotifyMgr* create(UserContext* userContext, int64_t uiHandle);

	virtual int32_t notify(const NOTIFY_PARAM& param) = 0;

	virtual void addNotify(int64_t uiHandle) = 0;

	virtual void removeNotify(int64_t uiHandle) = 0;

	virtual void interrupt() = 0;
};

#endif
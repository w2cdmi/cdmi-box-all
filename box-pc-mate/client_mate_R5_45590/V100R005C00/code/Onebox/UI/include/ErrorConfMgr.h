#ifndef _ONEBOX_ERRORCONF_MGR_H_
#define _ONEBOX_ERRORCONF_MGR_H_

namespace Onebox
{
	class ErrorConfMgr
	{
	public:
		static ErrorConfMgr* getInstance();

		virtual ~ErrorConfMgr(){}

		virtual int32_t loadErrorConf() = 0;

		virtual std::wstring getDescription(int32_t errorCode) = 0;

		virtual std::wstring getAdvice(int32_t errorCode) = 0;

	private:
		static ErrorConfMgr* instance_;
	};
}

#endif

#ifndef _SYNC_UTILITY_H_
#define _SYNC_UTILITY_H_

#include  "SyncCommon.h"

namespace Sync
{
	std::string getInStr(const std::list<int64_t>& idList);

	std::string getInStrEx(const std::list<int64_t>& idList);
}

class PrintObj
{
public:
	PrintObj()
	{
		len_ = 0;
	}

	virtual ~PrintObj(void)
	{
	}

	template<typename T>
	void addField(const T& value)
	{
		tempStr_ << value << "-";
	}

	template<typename T>
	void lastField(const T& value)
	{
		tempStr_ << value << ",";
		if((len_ + tempStr_.str().length()) > 200)
		{
			debugStr_<<"\n";
			len_ = tempStr_.str().length();
			debugStr_<<tempStr_.str();
		}
		else
		{
			len_ += tempStr_.str().length();
			debugStr_<<tempStr_.str();
		}
		tempStr_.str("");
	}

	std::string getMsg()
	{
		return debugStr_.str();
	}

	static std::auto_ptr<PrintObj> create()
	{
		return std::auto_ptr<PrintObj>(new PrintObj());
	}

private:
	std::stringstream debugStr_;
	std::stringstream tempStr_;
	int32_t len_;
};

#endif

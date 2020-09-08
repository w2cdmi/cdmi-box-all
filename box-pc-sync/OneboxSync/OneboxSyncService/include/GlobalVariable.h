#ifndef _ONEBOX_GLOBALVARIABLE_MGR_H_
#define _ONEBOX_GLOBALVARIABLE_MGR_H_

#include "UserContext.h"
#include <boost/thread.hpp>
#include <list>

class GlobalMutex
{
public:
	boost::mutex& localMutex()
	{
		return localMutex_;
	}

	boost::mutex& remoteMutex()
	{
		return remoteMutex_;
	}

private:
	boost::mutex localMutex_;
	boost::mutex remoteMutex_;
};

class BoundTimeCounter
{
public:
	BoundTimeCounter(const uint32_t timeBound = 1000/*miliseconds*/)
		//const uint32_t countBound = 10)
		:timeBound_(timeBound)
		//,countBound_(countBound)
		,baseTime_(GetTickCount())
		//,baseCount_(0)
	{

	}

	bool testBoundary()
	{
		uint32_t tempTime = GetTickCount();
		if (/*++baseCount_ > countBound_ ||*/(tempTime-baseTime_) > timeBound_)
		{
			baseTime_ = tempTime;
			//baseCount_ = 0;
			return true;
		}
		return false;
	}

private:
	uint32_t baseTime_;
	//uint32_t baseCount_;
	uint32_t timeBound_;
	//uint32_t countBound_;
};

class GlobalHandler
{
public:
	typedef boost::function<void(void)> HandlerType;

	void addHandler(HandlerType handler)
	{
		handlers_.push_back(handler);
	}

	void invok()
	{
		if (!boundTimeCounter_.testBoundary())
		{
			return;
		}
		for (std::list<HandlerType>::iterator it = handlers_.begin(); 
			it != handlers_.end(); ++it)
		{
			(*it)();
		}
	}

private:
	std::list<HandlerType> handlers_;
	BoundTimeCounter boundTimeCounter_;
};

class GlobalVariable
{
public:
	GlobalVariable()
		:globalMutex_(new GlobalMutex)
		,globalHandler_(new GlobalHandler) {}

	virtual ~GlobalVariable() {}

	virtual GlobalMutex* globalMutex()
	{
		return globalMutex_.get();
	}

	virtual GlobalHandler* globalHandler()
	{
		return globalHandler_.get();
	}

	static GlobalVariable* create(UserContext* userContext)
	{
		UNUSED_ARG(userContext);
		return (new GlobalVariable);
	}

private:
	std::auto_ptr<GlobalMutex> globalMutex_;
	std::auto_ptr<GlobalHandler> globalHandler_;
};

#endif
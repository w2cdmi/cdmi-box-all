#ifndef _ONEBOX_TIME_COUNTER_H_
#define _ONEBOX_TIME_COUNTER_H_

#include <boost/asio.hpp>
#include <boost/thread.hpp>

class TimeCounter
{
public:
	typedef boost::function<void()> callback_type;

	TimeCounter(const int64_t& interval/*milisecond*/, callback_type callback)
		:callback_(callback)
		,interval_(interval)
		,deadline_timer_(asio_)
		,stoped_(true)
	{

	}

	~TimeCounter()
	{
		try
		{
			kill();
		}
		catch(...){}
	}

	void start()
	{
		if (stoped_)
		{
			deadline_timer_.expires_from_now(boost::posix_time::milliseconds(interval_));
			deadline_timer_.async_wait(boost::bind(&TimeCounter::deadline_timer_callback, 
				this, boost::asio::placeholders::error));
			asio_service_thread_ = boost::thread(boost::bind(&TimeCounter::io_service_impl, this));
			stoped_ = false;
		}
	}

	void kill()
	{
		if (!stoped_)
		{
			callback_thread_.interrupt();
			while (0 == deadline_timer_.cancel());
			while (!asio_.stopped());
			stoped_ = true;
		}
	}

private:
	void io_service_impl()
	{
		asio_.reset();
		asio_.run();
	}

	void deadline_timer_callback(const boost::system::error_code& error)
	{
		if (!error)
		{
			callback_thread_ = boost::thread(callback_);
			callback_thread_.join();
			// reset timer
			deadline_timer_.expires_from_now(boost::posix_time::milliseconds(interval_));
			deadline_timer_.async_wait(boost::bind(&TimeCounter::deadline_timer_callback, 
				this, boost::asio::placeholders::error));
		}
	}

private:
	int64_t interval_;
	callback_type callback_;

	boost::asio::io_service asio_;
	boost::asio::deadline_timer deadline_timer_;
	
	boost::thread asio_service_thread_;
	boost::thread callback_thread_;

	bool stoped_;
};

#endif

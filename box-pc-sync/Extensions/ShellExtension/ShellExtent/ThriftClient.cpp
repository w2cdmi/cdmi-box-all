#include "ThriftClient.h"
#include "Notifymsg.h"
#include "Utility.h"

using namespace SD;

#ifndef MODUL_NAME
#define MODUL_NAME ("SyncServiceClientWrapper")
#endif

SyncServiceClientWrapper* SyncServiceClientWrapper::instance_ = NULL;

SyncServiceClientWrapper* SyncServiceClientWrapper::getInstance()
{
	if (NULL == instance_)
	{
		instance_ = new SyncServiceClientWrapper;
	}
	return instance_;
}

SyncServiceClientWrapper::~SyncServiceClientWrapper()
{
	if (instance_)
	{
		delete instance_;
		instance_ = NULL;
	}
}

SyncServiceClientWrapper::SyncServiceClientWrapper()
{
    socket_ = boost::shared_ptr<TSocket>(new TSocket());
    socket_->setHost("localhost");
    transport_ = boost::shared_ptr<TTransport>(new TBufferedTransport(socket_));
    protocol_ =  boost::shared_ptr<TProtocol>(new TBinaryProtocol(transport_));
    client_ = boost::shared_ptr<SyncServiceClient>(new SyncServiceClient(protocol_));
}

int32_t SyncServiceClientWrapper::open()
{
    try
    {
		std::wstring port = L"0";
		int32_t ret = Utility::Registry::get<std::wstring>(HKEY_CLASSES_ROOT, THRIFT_PORT_PATH, THRIFT_PORT_NAME, port);
		if (RT_OK != ret)
		{
			return ret;
		}
		socket_->setPort(Utility::String::string_to_type<int32_t>(port));
        transport_->open();
    }
    catch (TException &tx)
    {
		(void)tx;
        return RT_ERROR;
    }
    return RT_OK;
}

int32_t SyncServiceClientWrapper::close()
{
    try
    {
        transport_->close();
    }
    catch (TException &tx)
    {
		(void)tx;
        return RT_ERROR;
    }
    return RT_OK;
}

/*
int32_t SyncServiceClientWrapper::getLocalNode(const std::wstring& path, Local_Node& _return)
{
	if(path.empty())
	{
		return RT_INVALID_PARAM;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		client_->getLocalNode(_return, Utility::String::wstring_to_utf8(path));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}
*/

int32_t SyncServiceClientWrapper::getRemoteId(const std::wstring& path, int64_t& _return)
{
	if(path.empty())
	{
		return RT_INVALID_PARAM;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		_return = client_->getRemoteId(Utility::String::wstring_to_utf8(path));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::getOverlayIconStatus(const std::wstring& path)
{
	if(path.empty())
	{
		return RT_INVALID_PARAM;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		ret = client_->getOverlayIconStatus(Utility::String::wstring_to_utf8(path));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::setShare(const std::wstring& path)
{
	if(path.empty())
	{
		return RT_INVALID_PARAM;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		ret = client_->sendMessage(NOTIFY_MSG_MENU_SHARE, 
			Utility::String::wstring_to_utf8(path), "", "", "", "");
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::setShareLink(const std::wstring& path)
{
	if(path.empty())
	{
		return RT_INVALID_PARAM;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		ret = client_->sendMessage(NOTIFY_MSG_MENU_SHARE_LINK, 
			Utility::String::wstring_to_utf8(path), "", "", "", "");
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::upload(const std::wstring& source, const std::wstring& target, int32_t type)
{
	if(source.empty() || target.empty())
	{
		return RT_INVALID_PARAM;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		ret = client_->uploadFile(Utility::String::wstring_to_utf8(source), 
			Utility::String::wstring_to_utf8(target), 
			(File_Type::type)type);
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::notify(const std::wstring& path)
{
	if(path.empty())
	{
		return RT_INVALID_PARAM;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		ret = client_->sendMessage(NOTIFY_MSG_MENU_LISTREMOTEDIR, 
			Utility::String::wstring_to_utf8(path), "","", "", "");
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;

}

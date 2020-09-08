#include "ThriftClient.h"
#include "Utility.h"
#include "CommonDefine.h"
#include "ErrorCode.h"

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
    client_ = boost::shared_ptr<ThriftServiceClient>(new ThriftServiceClient(protocol_));
}

int32_t SyncServiceClientWrapper::open()
{
    try
    {
		std::wstring port = L"0";
		int32_t ret = Utility::Registry::get<std::wstring>(HKEY_CURRENT_USER, THRIFT_PORT_PATH, THRIFT_PORT_NAME, port);
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

int64_t SyncServiceClientWrapper::getCurrentUserId()
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	int64_t _return;
	try
	{
		_return = client_->getCurrentUserId();
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return _return;
}

int32_t SyncServiceClientWrapper::getServerStatus()
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t status = RT_ERROR;
	try
	{
		status = client_->getServiceStatus();
	}
	catch (TException &tx)
	{
		(void)tx;
	}

	close();

	return status;
}

int32_t SyncServiceClientWrapper::listRemoteDir(std::vector<File_Node> & _return, const int64_t fileId, const int64_t userId, const int32_t userType)
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		client_->listRemoteDir(_return,fileId,userId,userType);
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::upload(const std::wstring& source, const int64_t remoteParentId, const int64_t userId, const int32_t userType)
{
	if(source.empty())
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
		ret = client_->upload(Utility::String::wstring_to_utf8(source), 
			remoteParentId, 
			userId,
			userType, 
			Utility::String::wstring_to_utf8(Utility::String::gen_uuid()));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::listTeamspace(std::vector<TeamSpace_Node> & _return)
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		client_->listTeamspace(_return);
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::sendMessage(const int32_t type, const std::string& msg1, const std::string& msg2, const std::string& msg3, const std::string& msg4, const std::string& msg5)
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		client_->sendMessage(type,msg1,msg2,msg3,msg4,msg5);
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

bool SyncServiceClientWrapper::needAddFullBackup(const std::string& strPath)
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return false;
	}
	bool ret = true;
	try
	{
		ret = client_->needAddFullBackup(strPath);
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=false;
	}

	close();

	return ret;
}

int32_t SyncServiceClientWrapper::addFullBackup(const std::string& strPath)
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		ret = client_->addFullBackup(strPath);
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

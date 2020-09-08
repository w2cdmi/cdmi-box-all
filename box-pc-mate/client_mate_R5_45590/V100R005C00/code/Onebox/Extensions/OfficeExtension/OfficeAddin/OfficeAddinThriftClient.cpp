#include "OfficeAddinThriftClient.h"
#include "Utility.h"
#include "CommonDefine.h"

using namespace SD;

std::auto_ptr<OfficeAddinThriftClient> OfficeAddinThriftClient::instance_(NULL);

OfficeAddinThriftClient* OfficeAddinThriftClient::getInstance()
{
	if (NULL == instance_.get())
	{
		instance_ = std::auto_ptr<OfficeAddinThriftClient>(new(std::nothrow) OfficeAddinThriftClient);
	}
	return instance_.get();
}

OfficeAddinThriftClient::OfficeAddinThriftClient(void)
{
	socket_ = boost::shared_ptr<TSocket>(new TSocket());
	socket_->setHost("localhost");
	transport_ = boost::shared_ptr<TTransport>(new TBufferedTransport(socket_));
	protocol_ =  boost::shared_ptr<TProtocol>(new TBinaryProtocol(transport_));
	client_ = boost::shared_ptr<ThriftServiceClient>(new ThriftServiceClient(protocol_));
}

int32_t OfficeAddinThriftClient::open()
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

int32_t OfficeAddinThriftClient::close()
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

OfficeAddinThriftClient::~OfficeAddinThriftClient(void)
{
}

int64_t OfficeAddinThriftClient::getUserId()
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int64_t userId = INVALID_ID;
	try
	{
		userId = client_->getCurrentUserId();
	}
	catch (TException &tx)
	{
		(void)tx;
	}

	close();

	return userId;
}

int32_t OfficeAddinThriftClient::getServerStatus()
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

int32_t OfficeAddinThriftClient::upload(const std::wstring& path, const int64_t parent, const std::wstring& group)
{
	if (path.empty() || parent < 0)
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
		ret = client_->uploadOffice(Utility::String::wstring_to_utf8(path), 
			parent, 
			Utility::String::wstring_to_utf8(group));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t OfficeAddinThriftClient::getTask(const std::wstring& group, TransTask_RootNode& transTaskNode)
{
	if(group.empty())
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
		client_->getTask(transTaskNode, Utility::String::wstring_to_utf8(group));
		if (transTaskNode.group.empty())
		{
			ret = RT_ERROR;
		}
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t OfficeAddinThriftClient::delTask(const std::wstring& group)
{
    if(group.empty())
    {
        return RT_OK;
    }
    boost::mutex::scoped_lock lock(mutex_);
    if(RT_OK != open())
    {
        return RT_ERROR;
    }
    int32_t ret = RT_OK;
    try
    {
        ret = client_->delTask(Utility::String::wstring_to_utf8(group));
    }
    catch (TException &tx)
    {
        (void)tx;
        ret=RT_ERROR;
    }

    close();

    return ret;
}

int32_t OfficeAddinThriftClient::resumeTask(const std::wstring& group)
{
    if( group.empty() )
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
        ret = client_->resumeTask(SD::Utility::String::wstring_to_utf8(group));
    }
    catch (TException &tx)
    {
        (void)tx;
        ret=RT_ERROR;
    }

    close();

    return ret;
}

int32_t OfficeAddinThriftClient::isTaskExist(const std::wstring& group)
{
	if( group.empty() )
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
		ret = client_->isTaskExist(SD::Utility::String::wstring_to_utf8(group));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

File_Node OfficeAddinThriftClient::createFolder(const int64_t userId, const int32_t userType, const int64_t parentId, const std::wstring& name,const int32_t extraType)
{
	boost::mutex::scoped_lock lock(mutex_);
	File_Node ret;
	ret.id = -1;
	ret.name = "";
	if(RT_OK != open())
	{
		return ret;
	}
	try
	{
		client_->createFolderNoSync(ret, userId, userType, parentId, Utility::String::wstring_to_utf8(name),extraType);
	}
	catch (TException &tx)
	{
		(void)tx;
	}

	close();

	return ret;
}

int32_t OfficeAddinThriftClient::getPathByFileId(int64_t fileId, std::wstring& path)
{
	boost::mutex::scoped_lock lock(mutex_);

	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	try
	{
		std::string result = "";
		client_->GetPathName(result, fileId);

		if ( !result.empty() )
		{
			path = SD::Utility::String::utf8_to_wstring(result);
		}
	}
	catch (TException &tx)
	{
		(void)tx;
		return RT_ERROR;
	}

	close();

	return RT_OK;
}

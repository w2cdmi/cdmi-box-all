#include "OutlookAddinThriftClient.h"
#include "CommonDefine.h"
#include "Utility.h"

using namespace SD;

std::shared_ptr<OutlookAddinThriftClient> OutlookAddinThriftClient::create()
{
	return std::shared_ptr<OutlookAddinThriftClient>(new(std::nothrow) OutlookAddinThriftClient);
}

OutlookAddinThriftClient::OutlookAddinThriftClient(void)
{
	socket_ = boost::shared_ptr<TSocket>(new TSocket());
	socket_->setHost("localhost");
	transport_ = boost::shared_ptr<TTransport>(new TBufferedTransport(socket_));
	protocol_ =  boost::shared_ptr<TProtocol>(new TBinaryProtocol(transport_));
	client_ = boost::shared_ptr<ThriftServiceClient>(new ThriftServiceClient(protocol_));
}

int32_t OutlookAddinThriftClient::open()
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

int32_t OutlookAddinThriftClient::close()
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

OutlookAddinThriftClient::~OutlookAddinThriftClient(void)
{
}

int64_t OutlookAddinThriftClient::getUserId()
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

int32_t OutlookAddinThriftClient::getServerStatus()
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

int32_t OutlookAddinThriftClient::upload(const std::wstring& localPath, const int64_t remoteParentId, const std::wstring& group)
{
	if(localPath.empty() || remoteParentId == INVALID_ID || group.empty())
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
		ret = client_->uploadOutlook(Utility::String::wstring_to_utf8(localPath), 
			remoteParentId, 
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

int32_t OutlookAddinThriftClient::getTasks(const std::list<std::wstring> groups, std::list<TransTask_RootNode>& transTaskNodes)
{
	if(groups.empty())
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
		for (std::list<std::wstring>::const_iterator it = groups.begin(); it != groups.end(); ++it)
		{
			TransTask_RootNode transTaskNode;
			client_->getTask(transTaskNode, Utility::String::wstring_to_utf8(*it));
			if (!transTaskNode.group.empty())
			{
				transTaskNodes.push_back(transTaskNode);
			}
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

int32_t OutlookAddinThriftClient::pauseTask(const std::wstring& group)
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
		ret = client_->pauseTask(Utility::String::wstring_to_utf8(group));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t OutlookAddinThriftClient::delTask(const std::wstring& group)
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

int32_t OutlookAddinThriftClient::resumeTask(const std::wstring& group)
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
		ret = client_->resumeTask(Utility::String::wstring_to_utf8(group));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int64_t OutlookAddinThriftClient::createRemoteFolder(const int64_t remoteParentId, const std::wstring& name)
{
	if(remoteParentId < 0 || name.empty())
	{
		return RT_INVALID_PARAM;
	}
	int64_t userId = getUserId();
	if (userId < 0)
	{
		return RT_ERROR;
	}

	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_OK;
	try
	{
		File_Node fileNode;
		client_->createFolderNoSync(fileNode, userId, 1/*user context type is UserContext_User*/, 
			remoteParentId, Utility::String::wstring_to_utf8(name),0);
		if (fileNode.id <= 0)
		{
			ret = RT_ERROR;
		}
		return fileNode.id;
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

#include "CommonFileDialogThriftClient.h"
#include "Utility.h"
#include "CommonDefine.h"

using namespace SD;

std::auto_ptr<CommonFileDailogThriftClient> CommonFileDailogThriftClient::instance_(NULL);

CommonFileDailogThriftClient* CommonFileDailogThriftClient::getInstance()
{
	if (NULL == instance_.get())
	{
		instance_ = std::auto_ptr<CommonFileDailogThriftClient>(new(std::nothrow) CommonFileDailogThriftClient);
	}
	return instance_.get();
}

CommonFileDailogThriftClient::CommonFileDailogThriftClient(void)
{
	socket_ = boost::shared_ptr<TSocket>(new TSocket());
	socket_->setHost("localhost");
	transport_ = boost::shared_ptr<TTransport>(new TBufferedTransport(socket_));
	protocol_ =  boost::shared_ptr<TProtocol>(new TBinaryProtocol(transport_));
	client_ = boost::shared_ptr<ThriftServiceClient>(new ThriftServiceClient(protocol_));
}

int32_t CommonFileDailogThriftClient::open()
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

int32_t CommonFileDailogThriftClient::close()
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

CommonFileDailogThriftClient::~CommonFileDailogThriftClient(void)
{
}

int32_t CommonFileDailogThriftClient::listTeamspace(std::vector<TeamSpace_Node> & _return)
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

int32_t CommonFileDailogThriftClient::listRemoteDir(std::vector<File_Node> & _return, const int64_t fileId, const int64_t userId, const int32_t userType)
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

std::wstring CommonFileDailogThriftClient::getNewName(const int64_t userId, const int32_t userType, const int64_t parentId, const std::wstring& name)
{
	boost::mutex::scoped_lock lock(mutex_);
	std::wstring ret = L"";
	if(RT_OK != open())
	{
		return ret;
	}
	try
	{
		std::string newName = Utility::String::wstring_to_utf8(name);
		client_->getNewName(newName, userId, userType, parentId, newName);
		ret = Utility::String::utf8_to_wstring(newName);
	}
	catch (TException &tx)
	{
		(void)tx;
	}

	close();

	return ret;
}

File_Node CommonFileDailogThriftClient::createFolder(const int64_t userId, const int32_t userType, const int64_t parentId, const std::wstring& name)
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
		client_->createFolder(ret, userId, userType, parentId, Utility::String::wstring_to_utf8(name));
	}
	catch (TException &tx)
	{
		(void)tx;
	}

	close();

	return ret;
}

int32_t CommonFileDailogThriftClient::renameFolder(const int64_t userId, const int32_t userType, const int64_t fileId, const std::wstring& name)
{
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return RT_ERROR;
	}
	int32_t ret = RT_ERROR;
	try
	{
		ret = client_->renameFolder(userId, userType, fileId, Utility::String::wstring_to_utf8(name));
	}
	catch (TException &tx)
	{
		(void)tx;
	}

	close();

	return ret;
}

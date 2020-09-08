#include "OutlookAddinThriftClient.h"
#include "CommonDefine.h"
#include "Utility.h"

using namespace SD;

std::auto_ptr<OutlookAddinThriftClient> OutlookAddinThriftClient::instance_(NULL);

OutlookAddinThriftClient* OutlookAddinThriftClient::getInstance()
{
	if (NULL == instance_.get())
	{
		instance_ = std::auto_ptr<OutlookAddinThriftClient>(new(std::nothrow) OutlookAddinThriftClient);
	}
	return instance_.get();
}

OutlookAddinThriftClient::OutlookAddinThriftClient(void)
{
	socket_ = boost::shared_ptr<TSocket>(new TSocket());
	socket_->setHost("localhost");
	transport_ = boost::shared_ptr<TTransport>(new TBufferedTransport(socket_));
	protocol_ =  boost::shared_ptr<TProtocol>(new TBinaryProtocol(transport_));
	client_ = boost::shared_ptr<SyncServiceClient>(new SyncServiceClient(protocol_));
}

int32_t OutlookAddinThriftClient::open()
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

int32_t OutlookAddinThriftClient::uploadAttachements(const std::list<std::wstring> attachements, const std::wstring& parent, const std::wstring& taskGroupId)
{
	if(attachements.empty())
	{
		return RT_OK;
	}
	if (parent.empty() || taskGroupId.empty())
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
		std::vector<std::string> tempAttachements;
		for (std::list<std::wstring>::const_iterator it = attachements.begin(); it != attachements.end(); ++it)
		{
			tempAttachements.push_back(Utility::String::wstring_to_utf8(*it));
		}
		ret = client_->uploadAttachements(tempAttachements, Utility::String::wstring_to_utf8(parent), Utility::String::wstring_to_utf8(taskGroupId));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

bool OutlookAddinThriftClient::isAttachementsTransComplete(const std::wstring& taskGroupId)
{
	if(taskGroupId.empty())
	{
		return true;
	}
	boost::mutex::scoped_lock lock(mutex_);
	if(RT_OK != open())
	{
		return false;
	}
	bool ret = false;
	try
	{
		ret = client_->isAttachementsTransComplete(Utility::String::wstring_to_utf8(taskGroupId));
	}
	catch (TException &tx)
	{
		(void)tx;
	}

	close();

	return ret;
}

int32_t OutlookAddinThriftClient::showTransTask(const std::wstring& taskGroupId)
{
	if(taskGroupId.empty())
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
		ret = client_->showTransTask(Utility::String::wstring_to_utf8(taskGroupId));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

int32_t OutlookAddinThriftClient::getAttachementsLinks(std::map<std::wstring, std::wstring>& attachementsLinks, const std::wstring transGroupId)
{
	if(transGroupId.empty())
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
		std::map<std::string, std::string> tempAttachementsLinks;
		client_->getAttachementsLinks(tempAttachementsLinks, Utility::String::wstring_to_utf8(transGroupId));
		if (tempAttachementsLinks.empty())
		{
			return RT_ERROR;
		}		
		for (std::map<std::string, std::string>::iterator it = tempAttachementsLinks.begin(); it != tempAttachementsLinks.end(); ++it)
		{
			attachementsLinks[Utility::String::utf8_to_wstring(it->first)] = Utility::String::utf8_to_wstring(it->second);
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

int32_t OutlookAddinThriftClient::deleteTransTasks(const std::wstring& transGroupId)
{
	if(transGroupId.empty())
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
		ret = client_->deleteTransTasksByGroupId(Utility::String::wstring_to_utf8(transGroupId));
	}
	catch (TException &tx)
	{
		(void)tx;
		ret=RT_ERROR;
	}

	close();

	return ret;
}

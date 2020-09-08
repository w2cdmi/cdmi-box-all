#pragma once

#include "OneboxSyncService/SyncService.h"
#include "thrift/protocol/TBinaryProtocol.h"
#include "thrift/transport/TBufferTransports.h"
#include "thrift/server/TThreadPoolServer.h"
#include "thrift/concurrency/ThreadManager.h"
#include "thrift/concurrency/PosixThreadFactory.h"
#include "thrift/concurrency/Thread.h"
#include "thrift/transport/TSocket.h"
#include <list>
#include <map>
#include <memory>

using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;
using namespace SyncService;

class OutlookAddinThriftClient
{
public:
	static OutlookAddinThriftClient* getInstance();

	virtual ~OutlookAddinThriftClient(void);

	int32_t uploadAttachements(const std::list<std::wstring> attachements, const std::wstring& parent, const std::wstring& taskGroupId);

	bool isAttachementsTransComplete(const std::wstring& taskGroupId);

	int32_t showTransTask(const std::wstring& taskGroupId);

	int32_t getAttachementsLinks(std::map<std::wstring, std::wstring>& attachementsLinks, const std::wstring transGroupId);

	int32_t deleteTransTasks(const std::wstring& transGroupId);

private:
	OutlookAddinThriftClient(void);

	int32_t open();

	int32_t close();

	static std::auto_ptr<OutlookAddinThriftClient> instance_;
	boost::shared_ptr<TSocket> socket_;
	boost::shared_ptr<TTransport> transport_;
	boost::shared_ptr<TProtocol> protocol_;
	boost::shared_ptr<SyncServiceClient> client_;
	boost::mutex mutex_;
};


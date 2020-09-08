#pragma once

#include "ThriftService.h"
#include "thrift/protocol/TBinaryProtocol.h"
#include "thrift/transport/TBufferTransports.h"
#include "thrift/server/TThreadPoolServer.h"
#include "thrift/concurrency/ThreadManager.h"
#include "thrift/concurrency/PosixThreadFactory.h"
#include "thrift/concurrency/Thread.h"
#include "thrift/transport/TSocket.h"
#include <list>

using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;
using namespace OneboxThriftService;

class OutlookAddinThriftClient
{
public:
	static std::shared_ptr<OutlookAddinThriftClient> create();

	virtual ~OutlookAddinThriftClient(void);

	int64_t getUserId();

	int32_t getServerStatus();

	int32_t upload(const std::wstring& localPath, const int64_t remoteParentId, const std::wstring& group);

	int32_t getTasks(const std::list<std::wstring> groups, std::list<TransTask_RootNode>& transTaskNodes);

	int32_t pauseTask(const std::wstring& group);

	int32_t delTask(const std::wstring& group);

	int32_t resumeTask(const std::wstring& group);

	int64_t createRemoteFolder(const int64_t remoteParentId, const std::wstring& name);

private:
	OutlookAddinThriftClient(void);

	int32_t open();

	int32_t close();

	static std::auto_ptr<OutlookAddinThriftClient> instance_;
	boost::shared_ptr<TSocket> socket_;
	boost::shared_ptr<TTransport> transport_;
	boost::shared_ptr<TProtocol> protocol_;
	boost::shared_ptr<ThriftServiceClient> client_;
	boost::mutex mutex_;
};


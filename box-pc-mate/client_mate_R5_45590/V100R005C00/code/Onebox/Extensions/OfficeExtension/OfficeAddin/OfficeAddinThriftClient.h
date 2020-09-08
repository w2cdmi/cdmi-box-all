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

class OfficeAddinThriftClient
{
public:
	static OfficeAddinThriftClient* getInstance();

	virtual ~OfficeAddinThriftClient(void);

	int64_t getUserId();

	int32_t getServerStatus();

	int32_t upload(const std::wstring& path, const int64_t parent, const std::wstring& group);

	int32_t getTask(const std::wstring& group, TransTask_RootNode& transTaskNode);

    int32_t delTask(const std::wstring& group);

    int32_t resumeTask(const std::wstring& group);

	int32_t isTaskExist(const std::wstring& group);

	File_Node createFolder(const int64_t userId, const int32_t userType, const int64_t parentId, const std::wstring& name,const int32_t extraType);

	int32_t getPathByFileId(int64_t fileId, std::wstring& path);

private:

	OfficeAddinThriftClient(void);

	int32_t open();

	int32_t close();

	static std::auto_ptr<OfficeAddinThriftClient> instance_;
	boost::shared_ptr<TSocket> socket_;
	boost::shared_ptr<TTransport> transport_;
	boost::shared_ptr<TProtocol> protocol_;
	boost::shared_ptr<ThriftServiceClient> client_;
	boost::mutex mutex_;
};


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

class CommonFileDailogThriftClient
{
public:
	static CommonFileDailogThriftClient* getInstance();

	virtual ~CommonFileDailogThriftClient(void);

	int32_t listTeamspace(std::vector<TeamSpace_Node> & _return);

	int32_t listRemoteDir(std::vector<File_Node> & _return, const int64_t fileId, const int64_t userId, const int32_t userType);

	std::wstring getNewName(const int64_t userId, const int32_t userType, const int64_t parentId, const std::wstring& name);

	File_Node createFolder(const int64_t userId, const int32_t userType, const int64_t parentId, const std::wstring& name);

	int32_t renameFolder(const int64_t userId, const int32_t userType, const int64_t fileId, const std::wstring& name);

private:

	CommonFileDailogThriftClient(void);

	int32_t open();

	int32_t close();

	static std::auto_ptr<CommonFileDailogThriftClient> instance_;
	boost::shared_ptr<TSocket> socket_;
	boost::shared_ptr<TTransport> transport_;
	boost::shared_ptr<TProtocol> protocol_;
	boost::shared_ptr<ThriftServiceClient> client_;
	boost::mutex mutex_;
};


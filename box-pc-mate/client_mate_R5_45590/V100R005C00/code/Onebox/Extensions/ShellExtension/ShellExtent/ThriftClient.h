#ifndef _ShellExtent_THRIFTCLIENT_H_
#define  _ShellExtent_THRIFTCLIENT_H_
#include "ThriftService.h"
#include "thrift/protocol/TBinaryProtocol.h"
#include "thrift/transport/TBufferTransports.h"
#include "thrift/server/TThreadPoolServer.h"
#include "thrift/concurrency/ThreadManager.h"
#include "thrift/concurrency/PosixThreadFactory.h"
#include "thrift/concurrency/Thread.h"
#include "thrift/transport/TSocket.h"

using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;
using namespace OneboxThriftService;

class SyncServiceClientWrapper
{
public:
	static SyncServiceClientWrapper* getInstance();

	virtual ~SyncServiceClientWrapper();

	 int64_t getCurrentUserId();

	 int32_t getServerStatus();

	 int32_t listRemoteDir(std::vector<File_Node> & _return, const int64_t fileId, const int64_t userId, const int32_t userType);

	 int32_t upload(const std::wstring& source, const int64_t remoteParentId, const int64_t userId, const int32_t userType);

	 int32_t listTeamspace(std::vector<TeamSpace_Node> & _return);

	 int32_t sendMessage(const int32_t type, const std::string& msg1, const std::string& msg2, const std::string& msg3, const std::string& msg4, const std::string& msg5);

	 bool  needAddFullBackup(const std::string& strPath);

	 int32_t addFullBackup(const std::string& strPath);

private:
	SyncServiceClientWrapper();

	int open();

	int close();

private:
	static SyncServiceClientWrapper* instance_;
	boost::shared_ptr<TSocket> socket_;
	boost::shared_ptr<TTransport> transport_;
	boost::shared_ptr<TProtocol> protocol_;
	boost::shared_ptr<ThriftServiceClient> client_;
	boost::mutex mutex_;
};



#endif // !_ShellExtent_THRIFTCLIENT_H_
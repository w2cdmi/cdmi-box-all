
#ifndef _ONEBOX_SHELL_EXT_THRIFT_CLIENT_H_
#define _ONEBOX_SHELL_EXT_THRIFT_CLIENT_H_

#include "OneboxSyncService/SyncService.h"
#include "thrift/protocol/TBinaryProtocol.h"
#include "thrift/transport/TBufferTransports.h"
#include "thrift/server/TThreadPoolServer.h"
#include "thrift/concurrency/ThreadManager.h"
#include "thrift/concurrency/PosixThreadFactory.h"
#include "thrift/concurrency/Thread.h"
#include "thrift/transport/TSocket.h"
#include "CommonDefine.h"

using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;
using namespace Onebox::SyncService;

class SyncServiceClientWrapper
{
public:
	static SyncServiceClientWrapper* getInstance();

    virtual ~SyncServiceClientWrapper();

	//int32_t getLocalNode(const std::wstring& path, Local_Node& _return);

	int32_t getRemoteId(const std::wstring& path, int64_t& _return);

	int32_t getOverlayIconStatus(const std::wstring& path);

	int32_t setShare(const std::wstring& path);

	int32_t setShareLink(const std::wstring& path);

	int32_t upload(const std::wstring& source, const std::wstring& target, int type);

	int32_t notify(const std::wstring& path);

private:
	SyncServiceClientWrapper();

    int open();

    int close();

private:
	static SyncServiceClientWrapper* instance_;
	boost::shared_ptr<TSocket> socket_;
	boost::shared_ptr<TTransport> transport_;
	boost::shared_ptr<TProtocol> protocol_;
	boost::shared_ptr<SyncServiceClient> client_;
	boost::mutex mutex_;
};

#endif

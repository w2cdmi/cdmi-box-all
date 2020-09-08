#ifndef _ONEBOX_SYNCSERVICE_IMPL_H_
#define _ONEBOX_SYNCSERVICE_IMPL_H_

#include "OneboxSyncService/SyncService.h"
#include "thrift/server/TThreadPoolServer.h"
#include "thrift/transport/TServerSocket.h"
#include "thrift/transport/TBufferTransports.h"
#include "thrift/concurrency/ThreadManager.h"
#include "thrift/concurrency/BoostThreadFactory.h"
#include "thrift/concurrency/thread.h"
#include "UserContext.h"

using namespace ::apache::thrift;
using namespace ::apache::thrift::protocol;
using namespace ::apache::thrift::transport;
using namespace ::apache::thrift::server;
using namespace ::apache::thrift::concurrency;
using namespace Onebox::SyncService;

class SyncServiceImpl : public SyncServiceIf
{
public:
	static int32_t startService();
	static int32_t stopService();

public:
	virtual ~SyncServiceImpl();

private:
	SyncServiceImpl();

private:
	static int32_t startServiceImpl(uint16_t port);

private:
	virtual int32_t initUserContext(const int64_t uiHandle, const std::string& confPath);
	virtual int32_t releaseUserContext();
	virtual int32_t getServiceStatus();
	int32_t sendMessage(const int32_t type, const std::string& msg1, const std::string& msg2, const std::string& msg3, const std::string& msg4, const std::string& msg5);
	virtual int32_t changeServiceWorkMode(const Service_Status::type status);
	virtual int32_t uploadFile(const std::string& source, const std::string& target, const File_Type::type fileType);
	virtual int32_t downloadFile(const std::string& source, const std::string& target, const File_Type::type fileType);
	virtual int32_t deleteFile(const std::string& filePath, const File_Type::type fileType);
	virtual int32_t renameFile(const std::string& source, const std::string& target, const File_Type::type fileType);
	virtual int32_t moveFile(const std::string& source, const std::string& target, const File_Type::type fileType);
	virtual int32_t copyFile(const std::string& source, const std::string& target, const File_Type::type fileType);
	virtual int32_t createDir(const std::string& dirPath);
	virtual void listRemoteDir(std::vector<List_Info> & _return, const int64_t parent, const int64_t owner_id);
	virtual int64_t upload(const std::string& path, const int64_t parent, const int64_t owner_id);
	virtual int32_t login(const int32_t type, const std::string& username, const std::string& password, const std::string& domain);
	virtual int32_t logout();
	virtual void encyptString(std::string& _return, const std::string& src);
	virtual void decyptString(std::string& _return, const std::string& src);
	virtual int32_t updateConfigure();
	virtual void getTransSpeed(Trans_Speed_Info& _return);
	virtual int64_t getUserId();
	virtual void getUpdateInfo(Update_Info& _return);
	virtual int32_t downloadClient(const std::string& downloadUrl, const std::string& location);
	virtual void getNodeInfo(Node_Info& _return, const std::string& path);
	virtual int32_t getOverlayIconStatus(const std::string& path);
	virtual int64_t getRemoteId(const std::string& path);
	virtual void listError(std::vector<Error_Info> & _return, const int32_t offset, const int32_t limit);
	virtual void listDomainUsers(std::vector<Share_User_Info> & _return, const std::string& keyword);
	virtual void listShareUsers(std::vector<Share_User_Info> & _return, const std::string& path);
	virtual int32_t setShareMember(const std::string& path, const std::vector<Share_User_Info> & shareUserInfos, const std::string& emailMsg);
	virtual int32_t delShareMember(const std::string& path, const Share_User_Info& shareUserInfo);
	virtual int32_t cancelShare(const std::string& path);
	virtual void getShareLink(Share_Link_Info& _return, const std::string& path);
	virtual void modifyShareLink(Share_Link_Info& _return, const std::string& path, const Share_Link_Info& shareLinkInfo);
	virtual int32_t delShareLink(const std::string& path);
	virtual void getRandomString(std::string& _return);
	virtual int32_t sendEmail(const std::string& type, const std::string& path, const Share_Link_Info& shareLinkInfo, const std::string& emailMsg, const std::vector<std::string> & mailto);
	virtual void listBatchDomainUsers(std::map<std::string, Share_User_Info> & _return, const std::vector<std::string> & keyword);
	virtual int32_t uploadAttachements(const std::vector<std::string> & attachements, const std::string& parent, const std::string& taskGroupId);
	virtual bool isAttachementsTransComplete(const std::string& taskGroupId);
	virtual void getAttachementsLinks(std::map<std::string, std::string> & _return, const std::string& transGroupId);
	virtual int32_t deleteTransTasksByGroupId(const std::string& transGroupId);
	virtual int32_t showTransTask(const std::string& transGroupId);
	virtual void listTeamspacesByUser(std::vector<Teamspace_Membership> & _return, const int64_t userId);

private:
	static bool isInit_;
	static TThreadPoolServer* server_;
	UserContext* userContext_;
};

#endif

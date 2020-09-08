#ifndef __ONEBOX_REST_CLIENT__H__
#define __ONEBOX_REST_CLIENT__H__

#include "OneboxExport.h"
#include "CommonDefine.h"
#include "Token.h"
#include "Configure.h"
#include "HttpRequest.h"
#include "JsonParser.h"
#include "JsonGeneration.h"
#include <boost/thread/mutex.hpp>

class ONEBOX_DLL_EXPORT RestClient
{
public:
    static int32_t initialize();
    static void deinitialize();

public:
    int64_t getUploadSpeed();
    int64_t getDownloadSpeed();
	int32_t setUploadSpeedLimit(int64_t speed);
	int32_t setDownloadSpeedLimit(int64_t speed);
	int32_t getErrorCode();
	std::string getErrorMsg();
	uint32_t getRequstTime();
	void setToken(const TOKEN& token);
	void setConfigure(const Configure& configure);

public:
    RestClient(const TOKEN& token);
    RestClient(const TOKEN& token, const Configure& configure);

    int32_t request(const std::map<std::string, std::string>& mapProperty,
                const std::string & strUri,
				const SERVICE_TYPE& type,
                HttpRequest& request,
                RequestParam& param,
				bool ignoreRet = false);

    int32_t login(const std::string& strUseName,
              const std::string& strPsd,
			  const std::string& domain,
              LoginRespInfo& loginResp, 
              const std::string& clientSN = DEFAULT_CLIENT_SN,
              const std::string& clientOS = DEFAULT_CLIENT_OS,
              const std::string& clientName = DEFAULT_CLIENT_NAME,
              const std::string& clientVersion = DEFAULT_CLIENT_VERSION,
              const std::string& clientType = DEFAULT_CLIENT_TYPE);

    int32_t logout();

    int32_t refreshToken(LoginRespInfo& loginResp);

    int32_t checkHealthy();

    int32_t listen(const int32_t syncVersion, 
		int32_t& lastVersion);

	int32_t createLdapUser(const std::string& loginName, 
		int64_t& userId);

	int32_t setShareRes(const int64_t& ownerId,
					const int64_t& fileId,
					const ShareNodeEx& shareNodeEx);

    //int32_t delShareRes(const ShareNode& srcsharesNode);

    int32_t delShareResOwner(const int64_t& ownerId, 
		const int64_t& fileId, 
		const ShareNodeEx& shareNodeEx);

    int32_t quitShared(const int64_t& fileId, 
		const int64_t& ownerId);

    int32_t listDomainUsers(const std::string& keyWord, 
		ShareUserInfoList& shareUserInfos);

    int32_t getShareLink(const int64_t& fileId, 
                     const int64_t& ownerId, 
                     ShareLinkNode& shareLinkNode);

    int32_t modifyShareLink(const int64_t& fileId, 
                        const int64_t& ownerId, 
                        const ShareLinkNodeEx& shareLinkNodeEx, 
                        ShareLinkNode& shareLinkNode);

	int32_t addShareLink(const int64_t& fileId, 
                        const int64_t& ownerId, 
                        ShareLinkNode& shareLinkNode);

    int32_t delShareLink(const int64_t& fileId, 
		const int64_t& ownerId);

    int32_t sendShareLinkByEmail(const int64_t& fileId, 
                             const int64_t& ownerId, 
                             const std::string& linkUrl, 
                             EmailList& emails);

	int32_t getServerSysConfig(ServerSysConfig& serverSysConfig, 
		const OPTION_TYPE& option = OPTION_ALL);

	int32_t getIncSyncPeriod(int64_t& incSyncPeriod);

	int32_t getUpdateInfo(UpdateInfo& updateInfo);

	int32_t downloadClient(const std::string& downloadUrl, 
		const std::string location);

	int32_t getFileInfoByShareLink(ShareLinkNode& linknode, 
		FileItem& fileitem);

	int32_t createShareLink(const int64_t& fileId, 
		const int64_t& ownerId, 
		const std::string& access, 
		const ShareLinkNodeEx& shareLinkNodeEx, 
		ShareLinkNode& shareLinkNode);

	int32_t listShareRes(const int64_t& ownerId, 
		const int64_t& fileId, 
		ShareNodeList& shareNodes, 
		int32_t& nextOffset, 
		const int32_t offset = 0, 
		const int32_t limit = 100);

	int32_t getServerUrl(const SERVICE_TYPE& type, 
		std::string& serverurl);

	int32_t removeFile(const int64_t& ownerId, 
		const int64_t& fileId, 
		const FILE_TYPE type);

	int32_t renameFile(const int64_t& ownerId, 
		const int64_t& fileId, 
		const std::string& newName, 
		const FILE_TYPE type, 
		FileItem& fileItem);

	int32_t moveFile(const int64_t& ownerId, 
		const int64_t& fileId, 
		const int64_t& destParentId,
		const bool autorename, 
		const FILE_TYPE type,
		FileItem& fileItem);

	int copyFile(const FileItem& srcfileItem, 
		const ShareLinkNode& linkInfo, 
		const bool autorename, 
		FileItem& fileItem);

	int32_t checkFileExist(const int64_t& ownerId, 
		const int64_t& fileId, 
		const FILE_TYPE type);

	int32_t createFolder(const int64_t& ownerId, 
		const int64_t& parentId, 
		const std::string& name, 
		const int64_t& contentctime,
		const int64_t& contentmtime,
		FileItem& fileItem);

	int32_t getFileInfo(const int64_t& ownerId, 
		const int64_t& fileId, 
		const FILE_TYPE type,
		FileItem& fileItem);

	int32_t getFileInfoByParentAndName(const int64_t& ownerId, 
		const int64_t& parentId, 
		const std::string& name,
		std::list<FileItem*>& fileItems);

	int32_t listFolder(const int64_t& ownerId,
		const int64_t& folderId,
		const PageParam& pageParam,
		int64_t& nextOffset,
		std::list<FileItem*>& fileItems);

	int32_t preUpload(const FileItem& fileItem, 
		const UploadType uploadType,  
		FileItem& existFileItem,
		UploadInfo& uploadInfo, 
		const std::string& encryptKey = "");

    int32_t refreshUploadURL(const int64_t& ownerId, 
		const int64_t& fileId, 
		const std::string uploadUrl, 
		std::string& outUploadUrl);

	int32_t totalUpload(const std::string& uploadUrl, 
		const unsigned char* ucBuffer, 
		uint32_t ulBufSize);

	int32_t partUpload(const std::string& uploadUrl, 
		const int32_t partId,
		const unsigned char* ucBuffer, 
		uint32_t ulBufSize);

	int32_t partUploadComplete(const std::string& uploadUrl,
		const PartList& partList,
		FileItem& fileItem);

	int32_t partUploadCancel(const std::string& uploadUrl);

	int32_t getDownloadUrl(const int64_t& ownerId, 
		const int64_t& fileId, 
		std::string& downloadUrl);

	int32_t downloadFile(const std::string& downloadUrl, 
		unsigned char* fileBuffer,
		int64_t& lBufSize, 
		int64_t lOffset);

	int32_t getUploadPart(const std::string& uploadUrl, 
		PartInfoList& partInfoList);

	int32_t createVersion(const int64_t& ownerId, 
		const int64_t& fileId);

	int32_t setFolderSync(const int64_t& ownerId, 
		const int64_t& fileId, 
		const bool isSync);

	int32_t getSyncMetadata(const int64_t& ownerId, 
		int64_t syncVersion, 
		std::string& limitCnt, 
		std::string& curCnt);

	int32_t getAllMetadata(const int64_t& ownerId, 
		const int64_t& obj_id, 
		std::string& limitCnt, 
		std::string& curCnt);

	int32_t setFileVersion(const int64_t& fileId, 
		const int64_t& ownerId);

	int32_t sendEmail(const EmailNode& emailnode);

	int32_t getCurUserInfo(StorageUserInfo& storageUserInfo);

	int32_t listRegionIdInfo(RegionIdInfoArray& regionIdInfoArray);

	int32_t listAllTeamspaces(TeamspaceNodes& teamspaceNodes, 
		int64_t& totalCount, 
		const std::string& keyword = "", 
		const int64_t offset = 0, 
		const int32_t limit = DEFAULT_TEAMSPACE_LIST_LIMIT, 
		const std::string& orderField = TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE, 
		const std::string& orderDirection = TEAMSPACE_LIST_ORDER_DIRECTION_ASC);

	int32_t listTeamspacesByUser(TeamspaceMemberships& teamspaceMemberships, 
		int64_t& totalCount, 
		const int64_t userId, 
		const int64_t offset = 0, 
		const int32_t limit = DEFAULT_TEAMSPACE_LIST_LIMIT, 
		const std::string& orderField = TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE, 
		const std::string& orderDirection = TEAMSPACE_LIST_ORDER_DIRECTION_ASC);

	int32_t createTeamspace(TeamspaceNode& teamspaceNode, 
		const std::string& name, 
		const std::string& description = "", 
		const int64_t spaceQuota = TEAMSPACE_UNLIMIT_VALUE, 
		const TeamspaceStatus status = TeamspaceStatus_Normal, 
		const int32_t maxVersion = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t maxMembers = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t regionId = DEFAULT_REGION_ID);

	int32_t updateTeamspace(TeamspaceNode& teamspaceNode, 
		const int64_t teamspaceId, 
		const std::string& name, 
		const std::string& description = "", 
		const int64_t spaceQuota = TEAMSPACE_UNLIMIT_VALUE, 
		const TeamspaceStatus status = TeamspaceStatus_Normal, 
		const int32_t maxVersion = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t maxMembers = TEAMSPACE_UNLIMIT_VALUE, 
		const int32_t regionId = DEFAULT_REGION_ID);

	int32_t setTeamspaceExtAttribute(const int64_t teamspaceId, 
		const std::string& key, 
		const std::string& value);

	int32_t getTeamspaceExtAttribute(std::string& value, 
		const int64_t teamspaceId, 
		const std::string& key);

	int32_t getTeamspace(TeamspaceNode& teamspaceNode, 
		const int64_t teamspaceId);

	int32_t deleteTeamspace(const int64_t teamspaceId);

	int32_t addTeamspaceMember(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const TeamspaceRoleType teamRole, 
		const std::string& role, 
		const int64_t memberId, 
		const TeamspaceMemberType memberType);

	int32_t getTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId);

	int32_t updateTeamspaceMembership(TeamspaceMembership& teamspaceMembership, 
		const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId, 
		const TeamspaceRoleType teamRole, 
		const std::string& role);

	int32_t listTeamspaceMembers(TeamspaceMemberships& teamspaceMemberships, 
		int64_t& totalCount, 
		const int64_t teamspaceId, 
		const std::string& keyword = "", 
		const TeamspaceRoleType roleType = TeamspaceRoleType_All, 
		const int64_t offset = 0, 
		const int32_t limit = DEFAULT_TEAMSPACE_LIST_LIMIT, 
		const std::string& orderField = TEAMSPACE_LIST_ORDER_FIELD_TEAMROLE, 
		const std::string& orderDirection = TEAMSPACE_LIST_ORDER_DIRECTION_ASC);

	int32_t deleteTeamspaceMember(const int64_t teamspaceId, 
		const int64_t teamspaceMemebershiId);

private:
	RestClient();

private:
    static uint32_t refCount_;
	static std::string ufmUrl_;
	static std::string uamUrl_;
	TOKEN token_;
	Configure configure_;
	HttpRequest request_;
	int32_t errorCode_;
	std::string errorMessage_;
	uint32_t requestTime_;
	boost::mutex mutex_;
};

#endif

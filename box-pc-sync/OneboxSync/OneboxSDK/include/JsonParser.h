#ifndef __ONEBOX__JSON_PARSER__H__
#define __ONEBOX__JSON_PARSER__H__

#include "OneboxExport.h"
#include <json.h>
#include <list>
#include "FileItem.h"
#include "UserInfo.h"
#include "UploadInfo.h"
#include "ShareNode.h"
#include "ShareLinkNode.h"
#include "ShareUserInfo.h"
#include "ServerSysConfig.h"
#include "UpdateInfo.h"
#include "TeamspaceInfo.h"

class ONEBOX_DLL_EXPORT JsonParser
{
public:
    static int32_t parseListFolderResult(DataBuffer &dataBuf, std::list<FileItem*>& fileItems, int64_t& total_count);

	static int32_t parseFileFolders(DataBuffer &dataBuf, std::list<FileItem*>& fileItems);

	static int32_t parseLoginRespInfo(DataBuffer &dataBuf, LoginRespInfo& loginResp);

	static int32_t parseLoginRespInfoEx(DataBuffer &dataBuf, LoginRespInfo& loginResp);

	static int32_t parseFileObj(DataBuffer &dataBuf, FileItem& fileItem);

	static int32_t parseExistFileObj(DataBuffer &dataBuf, FileItem& fileItem);

	static int32_t parseFolderObj(DataBuffer &dataBuf, FileItem& fileItem);

	static int32_t parseUploadInfo(DataBuffer &dataBuf, UploadInfo& uploadInfo);

	static int32_t parseDownloadInfo(DataBuffer &dataBuf, std::string& downloadUrl);

	static int32_t parsePartInfo(DataBuffer &dataBuf, PartInfoList& partInfoList);

	static int32_t parseShareNodeList(DataBuffer &dataBuf, ShareNodeList& shareNodes, int32_t& total_cnt);

	static int32_t parseShareUserId(DataBuffer &dataBuf, int64_t& userId);

	static int32_t parseShareUserInfoList(DataBuffer &dataBuf, ShareUserInfoList& shareUserInfos);

	static int32_t parseShareLinkNode(DataBuffer &dataBuf, ShareLinkNode& shareLinkNode);
	
	static int32_t parseServerSysConfig(DataBuffer &dataBuf, ServerSysConfig& serverSysConfig);

	static int32_t parseIncSyncPeriod(DataBuffer &dataBuf, int64_t& incSyncPeriod);

	static int32_t parseUpdateInfo(DataBuffer &dataBuf, UpdateInfo& updateInfo);

	static int32_t parseFileInfoByShareLink(DataBuffer &dataBuf, ShareLinkNode& linknode, FileItem& fileitem);

	static int32_t parseServerUrl(DataBuffer &dataBuf, std::string& serverurl);

	static int32_t parseCurUserInfo(DataBuffer &dataBuf, StorageUserInfo& storageUserInfo);

	static int32_t parseRegionInfo(DataBuffer &dataBuf, RegionIdInfoArray &regionIdInfoArray);

	static int32_t parseTeamspaces(DataBuffer &dataBuf, TeamspaceNodes& teamspaces, int64_t& total);

	static int32_t parseTeamspaceMemberships(DataBuffer &dataBuf, TeamspaceMemberships& teamspaceMemberships, int64_t& total);

	static int32_t parseTeamspace(DataBuffer &dataBuf, TeamspaceNode& teamspace);

	static int32_t parseTeamspaceMembership(DataBuffer &dataBuf, TeamspaceMembership& teamspaceMembership);

private:
	static int32_t getFolderUnitFromJson(const Json::Value& jsonObj, FileItem &objectUnit);

private:
    template<typename DataUnit>
    class Parser
    {
    public:
        typedef int32_t(*UnitParserPtr)(const Json::Value&, DataUnit&);
        Parser(DataBuffer &dataBuf);
        ~Parser();
        int32_t parseList(const std::string& strKey, UnitParserPtr parser, std::list<DataUnit*>& dataUnits);
		int32_t parseList(const std::string& strKey, Json::Value& jsonList);
        int32_t parseLeaf(const std::string& strLeafKey, std::string& strLeafValue);
		int32_t parseLeafSafe(const std::string& strLeafKey, std::string& strLeafValue);
		int32_t parseLeaf(const std::string& strLeafKey, int32_t& strLeafValue);
		int32_t parseLeaf(const std::string& strLeafKey, int64_t& strLeafValue);
		int32_t parseLeaf(const std::string& strLeafKey, uint64_t& strLeafValue);
		int32_t parseLeaf(const std::string& strLeafKey, bool& strLeafValue);
		int32_t parseLeaf(const std::string& strLeafKey, Json::Value& jsonValue);

    private:
		Json::Value jsonRoot_;
    };
};

#include "JsonParser.inl"

#endif // end of defined __ONEBOX__JSON_PARSER__H__

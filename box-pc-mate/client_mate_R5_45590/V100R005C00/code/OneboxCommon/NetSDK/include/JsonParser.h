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
#include "TeamSpacesNode.h"
#include "MsgInfo.h"
#include "EmailItem.h"
#include "GroupNode.h"
#include "AccesNode.h"
#include "PermissionRole.h"
#include "RestParam.h"

class ONEBOX_DLL_EXPORT JsonParser
{
public:
    static int32_t parseListFolderResult(DataBuffer &dataBuf, std::list<FileItem*>& fileItems, int64_t& total_count);

	static int32_t parseFileFolders(DataBuffer &dataBuf, std::list<FileItem*>& fileItems);

	static int32_t parseLoginRespInfo(DataBuffer &dataBuf, LoginRespInfo& loginResp);

	static int32_t parseLoginRespInfoEx(DataBuffer &dataBuf, LoginRespInfo& loginResp);

	static int32_t parseFileObj(DataBuffer &dataBuf, FileItem& fileItem);

	static int32_t parseFilePermissions(DataBuffer &dataBuf, File_Permissions& filePermissions);

	static int32_t parseExistFileObj(DataBuffer &dataBuf, FileItem& fileItem);

	static int32_t parseFolderObj(DataBuffer &dataBuf, FileItem& fileItem);

	static int32_t parseUploadInfo(DataBuffer &dataBuf, UploadInfo& uploadInfo);

	static void getErrorMessageFromJson(DataBuffer &dataBuf, std::string &errorMessege);

	static void parseUploadUrl(DataBuffer &dataBuf, std::string& uploadUrl);

	static int32_t parseDownloadInfo(DataBuffer &dataBuf, std::string& downloadUrl);

	static int32_t parseListenerUrl(DataBuffer &dataBuf, std::string& url);

	static int32_t parsePartInfo(DataBuffer &dataBuf, PartInfoList& partInfoList);

	static int32_t parseShareNodeList(DataBuffer &dataBuf, ShareNodeList& shareNodes, int64_t& total_cnt);

	static int32_t parseMyShareNodeList(DataBuffer &dataBuf, MyShareNodeList& shareNodes, int64_t& total_cnt);

	static int32_t parseShareUserId(DataBuffer &dataBuf, int64_t& userId);

	static int32_t parseShareUserInfoList(DataBuffer &dataBuf, ShareUserInfoList& shareUserInfos);

	static int32_t parseShareLinkNode(DataBuffer &dataBuf, ShareLinkNode& shareLinkNode);
	
	static int32_t parseShareLinkNodeV2(DataBuffer &dataBuf, ShareLinkNode& shareLinkNode);
	
	static int32_t parseShareLinkNodeList(DataBuffer &dataBuf, ShareLinkNodeList& shareLinkNodes, int64_t& total_cnt);

	static int32_t parseServerSysConfig(DataBuffer &dataBuf, ServerSysConfig& serverSysConfig);

	static int32_t parseUpdateInfo(DataBuffer &dataBuf, UpdateInfo& updateInfo);

	static int32_t parseFeatureCode(DataBuffer &dataBuf, std::string& featureCode);

	static int32_t parseFileInfoByShareLink(DataBuffer &dataBuf, ShareLinkNode& linknode, FileItem& fileitem);

	static int32_t parseServerUrl(DataBuffer &dataBuf, std::string& serverurl);

	static int32_t parseTeamSpacesUserInfoList(DataBuffer &dataBuf, UserTeamSpaceNodeInfoArray& _list, int64_t& total);

	static int32_t JsonToTeamSpaceMemberNodeInfo(Json::Value _value, TeamSpaceMemberNodeInfo& _tmp);

	static int32_t JsonToTeamSpacesNode(Json::Value _value, TeamSpacesNode& _tmp);

	static int32_t parseTeamSpaceNode(DataBuffer &dataBuf, TeamSpacesNode& _info);

	static int32_t parseTeamSpacesMemberInfo(DataBuffer &dataBuf, UserTeamSpaceNodeInfo& _info);

	static int32_t parseCurUserInfo(DataBuffer &writeBuf, StorageUserInfo &storageUserInfo);

	static int32_t parseRegionInfo(DataBuffer &writeBuf, RegionIdInfoArray &regionIdInfoArray);

	static int32_t parseFileVersionInfo(DataBuffer &writeBuf, FileVersionList &fileVersionNodes, int64_t& total_cnt);

	static int32_t parseFilesHadShareLink(DataBuffer &writeBuf, MyShareNodeList& nodes, int64_t& count);

	static int32_t parseMsgInfo(DataBuffer &writeBuf, MsgList &msgNodes, int64_t& total_cnt);

	static int32_t parseSysMsgInfo(DataBuffer &writeBuf, MsgList &msgNodes, int64_t& total_cnt);

	static int32_t parseMsgInfo(DataBuffer &writeBuf, MsgNode &msgNode);

	static int32_t parseEmailInfoNode(DataBuffer &writeBuf, EmailInfoNode &emailInfoNode);

	static int32_t parseGroupNode(DataBuffer &writeBuf, GroupNodeList &groupNodes, int64_t& total_cnt);
	
	static int32_t parseGroupUserInfoList(DataBuffer &dataBuf, UserGroupNodeInfoArray& _list, int64_t& total);

	static int32_t JsonToGroupMemberNodeInfo(Json::Value _value, GroupMemberNodeInfo& _tmp);

	static int32_t JsonToGroupNode(Json::Value _value, GroupNode& _tmp);

	static int32_t JsonToPermissionsInfo(Json::Value _value, Permission& _tmp);

	static int32_t parseGroupNode(DataBuffer &dataBuf,GroupNode& _info);

	static int32_t parseGroupMemberInfo(DataBuffer &dataBuf,UserGroupNodeInfo& _info);

	static int32_t parseSystemRoleList(DataBuffer &dataBuf,PermissionRoleArray& _info);

	static int32_t parseAccessNode(DataBuffer &dataBuf, AccesNode& _list);

	static int32_t JsonToResource(Json::Value _value, AccesNodeResource& _tmp);

	static int32_t JsonToUser(Json::Value _value, AccesNodeUser& _tmp);

	static int32_t parseAccessNodeList(DataBuffer &dataBuf,int64_t& total, AccesNodeArray& _list);

	static int32_t parseGetAccessNode(DataBuffer &dataBuf, AccesNode& _list);

	static int32_t parseTaskInfo(DataBuffer &writeBuf, std::string &taskId);
	
	static int32_t parseTaskStatus(DataBuffer &writeBuf, std::string &taskStatus);

	static int32_t parseSysRoleInfo(DataBuffer &writeBuf, SysRoleInfoExList &nodes);

	static int32_t parseFilePath(DataBuffer &writeBuf, std::vector<int64_t>& parentIds, std::vector<std::string>& parentNames);

	static int32_t parsePathInfo(DataBuffer &writeBuf, std::map<int64_t, std::wstring>& pathInfo);

	static int32_t parseDeclarationInfo(DataBuffer& dataBuf, DeclarationInfo& declarationInfo);

	static int32_t parseSignDeclaration(DataBuffer& dataBuf, std::string& isSign);

	static int32_t parseBatchPreUploadInfo(DataBuffer &dataBuf, BatchPreUploadResponse& response);

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
		int32_t parseLeaf(const std::string& strKey, Json::Value& jsonLeafValue);

    private:
		Json::Value jsonRoot_;
    };
};

#include "JsonParser.inl"

#endif // end of defined __ONEBOX__JSON_PARSER__H__

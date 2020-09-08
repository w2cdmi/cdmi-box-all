#ifndef __CLOUD__STORE__REST_CLIENT__H__
#define __CLOUD__STORE__REST_CLIENT__H__

#include "CommonDefine.h"
#include "Token.h"
#include "Configure.h"
#include "HttpRequest.h"
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
#include "PageParam.h"
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

    /*****************************************************************************************
    Function Name : request
    Description   : 构造并发送http请求
    Input         : mapProperty	请求头
                    strUri 请求URI
    Output        : request 请求
                    param 请求参数
    Return        : RT_OK:成功 Others:失败
    *******************************************************************************************/
    int32_t request(const std::map<std::string, std::string>& mapProperty,
                const std::string & strUri,
				const SERVICE_TYPE& type,
                HttpRequest& request,
                RequestParam& param,
				bool ignoreRet = false);

    /*****************************************************************************************
    Function Name : login
    Description   : 登录
    Input         : strUseName 用户名 
                    strPsd 用户密码
                    clientSN 客户端序列表
                    clientName 客户端名称
                    clientVersion 客户端版本
                    clientType 客户端类型
    Output        : loginResp 登录响应
    Return        : 成功 RT_OK 失败 Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t login(const std::string& strDomain,
				const std::string& strUseName,
				const std::string& strPsd,
				LoginRespInfo& loginResp, 
				const std::string& clientSN = DEFAULT_CLIENT_SN,
				const std::string& clientOS = DEFAULT_CLIENT_OS,
				const std::string& clientName = DEFAULT_CLIENT_NAME,
				const std::string& clientVersion = DEFAULT_CLIENT_VERSION,
				const std::string& clientType = DEFAULT_CLIENT_TYPE);

    /*****************************************************************************************
    Function Name : logout
    Description   : 注销登录
    Input         : None.
    Output        : None.
    Return        : 成功 RT_OK 失败 Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t logout();

    /*****************************************************************************************
    Function Name : refreshToken
    Description   : 刷新token
    Input         : None.
    Output        : loginResp 登录响应
    Return        : 成功 RT_OK 失败 Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t refreshToken(LoginRespInfo& loginResp);

	/*****************************************************************************************
    Function Name : checkHealthy
    Description   : 检查服务器健康状态
    Input         : None.
    Output        : None.
    Return        : 成功 RT_OK 失败 Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t checkHealthy();

    /*****************************************************************************************
    Function Name : listen
    Description   : 监听云端变化
    Input         : syncVersion 本地最新同步版本号
    Output        : lastVersion 云端最新同步版本号
    Return        : 成功 RT_OK 失败 Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t listen(int32_t syncVersion, 
		int32_t& lastVersion);

	//自动开户
	int32_t createLdapUser(const std::string& loginName, 
		int64_t& userId);

    /*****************************************************************************************
    Function Name : setShareRes
    Description   : 设置共享关系
    Input         : ownerId 用户ID
					fileId 文件/文件夹id
                    shareNodeExs 共享扩展节点对象, 
    Output        : 无
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t setShareRes(const int64_t& ownerId,
					const int64_t& fileId,
					ShareNodeEx& shareNodeEx);

	int32_t setShareResV2(const int64_t& ownerId,
		const int64_t& fileId,
		ShareNodeEx& shareNodeEx);
    /*****************************************************************************************
    Function Name : delShareRes
    Description   : 删除共享关系
    Input         : srcsharesNode 源共享资源 
    Output        : None.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
    //int32_t delShareRes(const ShareNode& srcsharesNode);

    /*****************************************************************************************
    Function Name : delShareResOwner
    Description   : 删除共享资源成员
    Input         : file_id 文件/文件夹id, shareNodeEx 共享节点扩展信息.
    Output        : None.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t delShareResOwner(const int64_t& ownerId, 
		const int64_t& file_id, 
		ShareNodeEx& shareNodeEx);

    /*****************************************************************************************
    Function Name : quitShared
    Description   : 退出共享
    Input         : file_id 文件/文件夹id, shareOwnerId 资源所有者id
    Output        : None.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t quitShared(const int64_t& file_id, 
		const int64_t& shareOwnerId);

    /*****************************************************************************************
    Function Name : listDomainUsers
    Description   : 列举AD域用户
    Input         : keyWord 用户名关键字
    Output        : shareUserInfos 共享用户信息.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t listDomainUsers(const std::string& keyWord, 
		ShareUserInfoList& shareUserInfos, 
		int32_t limit);

    /*****************************************************************************************
    Function Name : getShareLink
    Description   : 获取外链
    Input         : file_id 文件/文件夹id
                    ownerId 资源所有者id
    Output        : shareLinkNode 外链信息.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t getShareLink(const int64_t& file_id, 
                     const int64_t& owner_id, 
                     ShareLinkNode& shareLinkNode);
					 
    int32_t getShareLink(const int64_t& file_id, 
                     const int64_t& owner_id,
					 const std::string& linkCode,
                     ShareLinkNode& shareLinkNode);

	int32_t listShareLinkByFile(const int64_t& file_id, 
		const int64_t& owner_id,
		int64_t& count,
		ShareLinkNodeList& shareLinkNode);
	
    /*****************************************************************************************
    Function Name : modifyShareLink
    Description   : 修改外链
    Input         : file_id 文件/文件夹id
                    ownerId 资源所有者id
                    shareLinkNodeEx 外链扩展信息
    Output        : shareLinkNode 修改后的外链对象.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t modifyShareLink(const int64_t& file_id, 
                        const int64_t& owner_id, 
                        const ShareLinkNodeEx& shareLinkNodeEx, 
                        ShareLinkNode& shareLinkNode);
	
    int32_t modifyShareLink(const int64_t& file_id, 
                        const int64_t& owner_id,
						const std::string& linkCode,
                        const ShareLinkNodeEx& shareLinkNodeEx, 
                        ShareLinkNode& shareLinkNode);

	int32_t addShareLink(const int64_t& file_id, 
                        const int64_t& owner_id,
                        ShareLinkNode& shareLinkNode);

	int32_t addShareLink(const int64_t& file_id, 
		const int64_t& owner_id, 
		const std::string& accessMode,
		ShareLinkNode& shareLinkNode);
    /*****************************************************************************************
    Function Name : delShareLink
    Description   : 删除外链
    Input         : file_id 文件/文件夹id
                    ownerId 资源所有者id
    Output        : None.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t delShareLink(const int64_t& file_id, 
		const int64_t& owner_id);
    int32_t delShareLink(const int64_t& file_id, 
		const int64_t& owner_id, 
		const std::string& linkCode, 
		const std::string& type = "all");

    /*****************************************************************************************
    Function Name : sendShareLinkByEmail
    Description   : 邮件发送外链
    Input         : file_id 文件/文件夹id
                    ownerId 资源所有者id
                    linkUrl 外链地址
                    emails 发送到的email地址
    Output        : None.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t sendShareLinkByEmail(const int64_t& file_id, 
                             const int64_t& owner_id, 
                             const std::string& linkUrl, 
                             EmailList& emails);

    /*****************************************************************************************
    Function Name : getServerSysConfig
    Description   : 获取服务器配置
    Input         : None.
    Output        : ServerSysConfig对像
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getServerSysConfig(ServerSysConfig& serverSysConfig, 
		const OPTION_TYPE& option = OPTION_ALL);

	/*****************************************************************************************
    Function Name : getUpdateInfo
    Description   : 获取升级信息
    Input         : None.
    Output        : updateInfo对像
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getUpdateInfo(UpdateInfo& updateInfo);

	/*****************************************************************************************
    Function Name : downloadClient
    Description   : 下载升级客户端
    Input         : downloadUrl 客户端下载地址 location 客户端下载到的本地路径.
    Output        : None.
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t downloadClient(const std::string& downloadUrl, 
		const std::string location);

	/*****************************************************************************************
    Function Name : getFeatureCode
    Description   : 获得校验码
    Input         : clientType 客户端类型  version 客户端版本.
    Output        : featureCode 校验码
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getFeatureCode(const std::string& clientType, 
		const std::string& version, 
		std::string& featureCode);

	/*****************************************************************************************
    Function Name : downloadClient
    Description   : 下载升级客户端
    Input         : None.
    Output        : linknode 外链对象
					fileitem 文件对象（文件夹对象）
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getFileInfoByShareLink(ShareLinkNode& linknode, 
		FileItem& fileitem);

	/*****************************************************************************************
    Function Name : downloadClient
    Description   : 下载升级客户端
	Input         : fileId 外链对象
					ownerId 文件对象（文件夹对象）
					access	外链码访问方式
					shareLinkNodeEx	外链扩展信息.
    Output        : ShareLinkNode	外链对象
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t createShareLink(const int64_t& fileId, 
		const int64_t& ownerId, 
		const std::string& access, 
		const ShareLinkNodeEx& shareLinkNodeEx, 
		ShareLinkNode& shareLinkNode);

	/*****************************************************************************************
    Function Name : listShareRes
    Description   : 列举共享关系
	Input         : fileId 外链对象
					ownerId 文件对象（文件夹对象）
					offset	偏移量
					limit	最大反馈数
    Output        : ShareNodeList	共享节点列表
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t listShareRes(const int64_t& ownerId, 
		const int64_t& fileId, 
		ShareNodeList& shareNodes, 
		int64_t& nextOffset, 
		const int64_t offset = 0, 
		const int32_t limit = 100);

	/*****************************************************************************************
    Function Name : listShareRes
    Description   : 获取服务器地址
	Input         : type 服务类型
    Output        : serverurl	服务器地址
    Return        : 成功 0 失败 其他
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getServerUrl(const SERVICE_TYPE& type, 
		std::string& serverurl);

	/*****************************************************************************************
	Function Name : removeFile
	Description   : 删除文件或目录(RemoveRemoteFile)
	Input         : owner_id		资源拥有者ID
					file_id			文件夹ID(文件ID)
					type			类型(文件/文件夹)
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t removeFile(const int64_t& owner_id, 
		const int64_t& file_id, 
		FILE_TYPE type);

	/*****************************************************************************************
	Function Name : renameFile
	Description   : 重命名文件或目录(RenameRemoteFile)
	Input         : owner_id		资源拥有者ID
					file_id			文件夹ID(文件ID)
					new_name		重命名的新名称
					type			类型(文件/文件夹)
	Output		  :	fileItem		成功时返回的文件对象(文件夹对象)
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t renameFile(const int64_t& owner_id, 
		const int64_t& file_id, 
		const std::string& new_name, 
		FILE_TYPE type, 
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : moveFile
	Description   : 移动文件或目录(MoveRemoteFile)
	Input         : owner_id		资源拥有者ID
					file_id			文件夹ID(文件ID)
					dest_parent_id	目标文件夹ID
					dest_name		目标名称	
					type			类型(文件/文件夹)
	Output		  :	fileItem		成功时返回的文件对象(文件夹对象)
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t moveFile(const int64_t& owner_id, 
		const int64_t& file_id, 
		const int64_t& dest_parent_id,
		const bool auto_rename, 
		FILE_TYPE type,
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : copyFile
	Description   : 复制文件或目录(CopyRemoteFile)
	Input         : owner_id		资源拥有者ID
					file_id			文件夹ID(文件ID)
					dest_owner_id		目标文件夹拥有者ID
					dest_parent_id	目标文件夹ID
					type			类型(文件/文件夹)
	Output		  :	fileItem		成功时返回的文件对象(文件夹对象)
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int copyFile(const int64_t& owner_id, 
		const int64_t& file_id,
		const int64_t& dest_owner_id,
		const int64_t& dest_parent_id,
		const bool auto_rename, 
		FILE_TYPE type,
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : checkFileExist
	Description   : 检查文件或目录是否存在(CheckRemoteFileExist)
	Input         : owner_id		资源拥有者ID
					file_id			文件夹ID(文件ID)
					type			类型(文件/文件夹)
	Return        : RT_OK:存在 Others:不存在
	*******************************************************************************************/
	int32_t checkFileExist(const int64_t& owner_id, 
		const int64_t& file_id, 
		FILE_TYPE type);

	/*****************************************************************************************
	Function Name : createFolder
	Description   : 创建目录(CreateRemoteFolder)
	Input         : owner_id		资源拥有者ID
					parent_id		父文件夹ID
					name			文件夹名
	Output		  :	fileItem		成功时返回的文件夹对象
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t createFolder(const int64_t& owner_id, 
		const int64_t& parent_id, 
		const std::string& name, 
		const int64_t& contentcreatedat,
		const int64_t& contentmodifiedat,
		const int32_t& extraType,
		const bool autoMerge,
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : getFileInfo
	Description   : 获取文件/夹信息
	Input         : owner_id		资源拥有者ID
					file_id			文件夹ID(文件ID)
					type			类型(文件/文件夹)
	Output		  :	fileItem		成功时返回的文件/文件夹对象
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getFileInfo(const int64_t& owner_id, 
		const int64_t& file_id, 
		FILE_TYPE type,
		FileItem& fileItem);

	int32_t getFilePath(const int64_t& owner_id, 
		const int64_t& file_id,
		std::vector<int64_t>& parentIds,
		std::vector<std::string>& parentNames);

	int32_t getFilePermissions(const int64_t& owner_id, 
		const int64_t& file_id, 
		const int64_t& user_id, 
		File_Permissions& filePermissions);

	/*****************************************************************************************
	Function Name : getFileInfoByParentAndName
	Description   : 获取文件/夹信息
	Input         : owner_id		资源拥有者ID
					parent_id		父ID
					name			文件/夹名
	Output		  :	fileItems		成功时返回的文件/文件夹对象
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getFileInfoByParentAndName(const int64_t& owner_id, 
		const int64_t& parent_id, 
		const std::string& name,
		std::list<FileItem*>& fileItems);

	int32_t checkExistByParentAndName(const int64_t& owner_id, 
		const int64_t& parent_id, 
		const std::string& name);

	/*****************************************************************************************
	Function Name : listFolder
	Description   : 列举目录(ListRemoteFolder)
	Input         : owner_id       资源拥有者ID
					folder_id      文件夹ID(可以为空，表示根目录列举)
					pageparam     分页参数
					orderparam		排序参数
					trumbparam		缩略图参数
	Output        : total_count    返回文件（包括目录）总数
					nextOffset     下一个分页起始位置，返回0表示无下一分页
					pList          文件列表
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t listFolder(const int64_t& owner_id,
		const int64_t& folder_id,
		const PageParam& pageParam,
		int64_t& nextOffset,
		std::list<FileItem*>& fileItems);

	int32_t listPage(const int64_t& owner_id,
		const int64_t& folder_id,
		const PageParam& pageParam,
		int64_t& count,
		std::list<FileItem*>& fileItems);

	int32_t search(const int64_t& owner_id,
		const std::string& name,
		const PageParam& pageParam,
		int64_t& count,
		std::list<FileItem*>& fileItems,
		bool needPath,
		std::map<int64_t, std::wstring>& pathInfo);

	/*****************************************************************************************
	Function Name : preUpload
	Description   : 预上传(PreUpload)
	Input         : fileItem       文件信息
					upload_type    上传类型：整体/分片
					encrypt_key    密钥被加密后的字符串
	Output        : existFileItem  sha1存在时返回已存在的文件对象
					uploadInfo     不存在时返回文件上传信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t preUpload(const FileItem& fileItem, 
		const UploadType upload_type,  
		FileItem& existFileItem,
		UploadInfo& uploadInfo, 
		const std::string& encrypt_key = "");

	/*****************************************************************************************
	Function Name : batchPreUpload
	Description   : 批量预上传(BatchPreUpload)
	Input         : ownerId    文件所有者ID
					request    批量预上传请求对象
	Output        : response   批量预上传响应对象
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t RestClient::batchPreUpload(const int64_t ownerId, 
		const BatchPreUploadRequest& request,
		BatchPreUploadResponse& response);

	/*****************************************************************************************
    Function Name : refreshUploadURL
    Description   : 刷新上传Url
    Input         : ownerId:资源拥有者ID
                    file_id:文件ID
                    uploadUrl:预上传生成的uploadUrl
    Output        : outUploadUrl 返回新的uploadUrl
    Return        : 成功 RT_OK 失败 Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t refreshUploadURL(const int64_t& ownerId, 
		const int64_t& file_id, 
		const std::string uploadUrl, 
		std::string& outUploadUrl);

	/*****************************************************************************************
	Function Name : totalUpload
	Description   : 整体上传(totalUpload)
	Input         : upload_url		上传url
					ucBuffer		上传文件内容
					ulBufSize		上传文件大小
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t totalUpload(const std::string& upload_url, 
		const unsigned char* ucBuffer, 
		uint32_t ulBufSize);

	/*****************************************************************************************
	Function Name : partUpload
	Description   : 分片上传(UploadPart)
	Input         : upload_url		上传url
					part_id			分片序号，调用者指定，建议从1到N
					ucBuffer		上传文件内容
					ulBufSize		上传文件大小
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t partUpload(const std::string& upload_url, 
		const int32_t part_id,
		const unsigned char* ucBuffer, 
		uint32_t ulBufSize);

	/*****************************************************************************************
	Function Name : partUploadComplete
	Description   : 分片上传完成(CompleteUploadPart)
	Input         : upload_url		上传url
					file_id			文件ID
					partList		分片数组，组装文件的分片信息
	Output        : fileItem		操作成功返回文件信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t partUploadComplete(const std::string& upload_url,
		const PartList& partList,
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : partUploadCancel
	Description   : 分片上传取消(CancelUploadPart)
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t partUploadCancel(const std::string& upload_url);

	int32_t getDownloadUrl(const int64_t& owner_id, 
		const int64_t& file_id, 
		std::string& downloadUrl);

	int32_t downloadFile(const std::string& download_url, 
		unsigned char* fileBuffer,
		int64_t& lBufSize, 
		int64_t lOffset);

	int32_t getUploadPart(const std::string& upload_url, 
		PartInfoList& partInfoList);

	int32_t createVersion(const int64_t& owner_id, 
		const int64_t& file_id);

	int32_t setSyncStatus(const int64_t& owner_id, 
		const int64_t& file_id,
		const FILE_TYPE type, 
		const bool is_sync);

	int32_t getSyncMetadata(const int64_t& owner_id, 
		int64_t syncVersion, std::string& limitCnt, 
		std::string& curCnt);

	int32_t getAllMetadata(const int64_t& owner_id, 
		const int64_t& obj_id, std::string& limitCnt, 
		std::string& curCnt);

	int32_t setFileVersion(const int64_t& file_id, 
		const int64_t& owner_id);

	int32_t sendEmail(const EmailNode& emailnode);

	int32_t listReceiveShareRes(const std::string& keyword, const PageParam& pageparam, int64_t& count, ShareNodeList& shareNodes);

	int32_t listDistributeShareRes(const std::string& keyword, const PageParam& pageparam, int64_t& count, MyShareNodeList& shareNodes);

	/*****************************************************************************************
	Function Name : getTeamSpaceListUser
	Description   : 获取团队空间列表(getTeamSpaceListUser)
	Input         : owner_id			资源拥有者ID
					order_fileid		排序方式
					order_direction		升降序方式，"ASC":升序，“DESC”：降序
					nextOffset			下一个分页起始位置，返回0表示无下一分页
					limit				一次返回的数量
	Output        : total_count			返回文件（包括目录）总数
					pList				团队空间列表
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getTeamSpaceListUser(const int64_t& owner_id, 
		const PageParam& pageparam,
		int64_t& total_count,
		UserTeamSpaceNodeInfoArray& TSNodeList);

	/*****************************************************************************************
	Function Name : createTeamSpace
	Description   : 创建团队空间(createTeamSpace)
	Input         : name			团队空间的名字
					desc			团队空间描述
					spaceQuota		团队空间最大容量。单位MB。-1表示无限制。默认值为-1。
					status			团队空间状态。0 表示可用。1表示停用。默认值为0。
					maxVersions		最大版本数。默认值为-1，表示无限制。
	Output        : _return			团队空间信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t createTeamSpace(const std::string& name,
		const std::string& desc, 
		const int64_t spaceQuota, 
		const int32_t status,
		const int32_t maxVersions,
		TeamSpacesNode& _return);

	/*****************************************************************************************
	Function Name : updateTeamSpace
	Description   : 更新团队空间(updateTeamSpace)
	Input         : teamId			团队空间ID
					name			团队空间的名字
					desc			团队空间描述
					spaceQuota		团队空间最大容量。单位MB。-1表示无限制。默认值为-1。
					status			团队空间状态。0 表示可用。1表示停用。默认值为0。
	Output        : _return			团队空间信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t updateTeamSpace(const int64_t& teamId,
		const std::string& name, 
		const std::string& desc,
		const int64_t spaceQuota,
		const int32_t status,
		TeamSpacesNode& _return);

	/*****************************************************************************************
	Function Name : getTeamSpace
	Description   : 获取团队空间信息(getTeamSpace)
	Input         : teamId			团队空间ID
	Output        : _return			团队空间信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getTeamSpace(const int64_t& teamId, 
		TeamSpacesNode& _return);

	/*****************************************************************************************
	Function Name : deleteTeamSpace
	Description   : 删除团队空间(deleteTeamSpace)
	Input         : teamId			团队空间ID
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t deleteTeamSpace(const int64_t& teamId);

	/*****************************************************************************************
	Function Name : addTeamSpaceMember
	Description   : 增加团队空间成员(addTeamSpaceMember)
	Input         : teamId:			团队空间ID
					member_type:	成员类型。user: 用户。group: 群组。默认值为user。当前版本只支持user。
					member_id:		成员的用户ID或者群组ID。
					teamRole:		团队空间角色。admin 拥有者,manager 管理者,member 普通用户,当前只支持添加manager和member,默认值为member。
					role:			权限角色名称。
	Output        : _return			团队空间信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t addTeamSpaceMember(const int64_t& teamId,
		const std::string& member_type,
		const int64_t& member_id, 
		const std::string& teamRole, 
		const std::string& role,
		UserTeamSpaceNodeInfo& _return);

	/*****************************************************************************************
	Function Name : getTeamSpaceMemberInfo
	Description   : 获取团队空间成员信息(getTeamSpaceMemberInfo)
	Input         : teamId		团队空间ID
					id			团队空间成员关系ID
	Output		  : _return     团队空间成员信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getTeamSpaceMemberInfo(const int64_t& teamId, 
		const std::string& id, 
		UserTeamSpaceNodeInfo& _return);

	/*****************************************************************************************
	Function Name : updateTeamSpaceUserInfo
	Description   : 更新团队空间成员关系信息(updateTeamSpaceUserInfo)
	Input         : teamId:			团队空间ID
					id:				团队空间成员关系ID
					teamRole：		团队空间角色。admin 拥有者，manager 管理者，member 普通用户，默认值为member。
					role：			权限角色名称
	Output        : _return			团队空间成员信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t updateTeamSpaceUserInfo(const int64_t& teamId,
		const int64_t& id,
		const std::string& teamRole,
		const std::string& role,
		UserTeamSpaceNodeInfo& _return);

	/*****************************************************************************************
	Function Name : getTeamSpaceListMemberInfo
	Description   : 列举团队空间成员
	Input         : teamId:			团队空间ID
					order_field:	排序字段。取值范围：teamRole 空间角色,createdAt 创建时间,userName 用户名
					order_direction:升降序方式。可设置为“ASC”或者“DESC”
					teamRole:		团队空间角色。admin 拥有者,manager 管理者,member 普通用户,all 所有用户,默认值为all。
					keyword:		搜索关键字。该关键字可匹配用户或群组的名称。
					limit:			一页最多得到的项
					offset:			分页
	Output        : total:			总共的项数
					_return			团队空间成员列表
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getTeamSpaceListMemberInfo(const int64_t& teamId, 
		const std::string& keyword, 
		const std::string& teamRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserTeamSpaceNodeInfoArray& _return);

	/*****************************************************************************************
	Function Name : deleteTeamSpaceMember
	Description   : 删除团队空间成员
	Input         : teamId:			团队空间ID
					id:				团队空间成员关系ID
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t deleteTeamSpaceMember(const int64_t& teamId, 
		const int64_t& id);

	int32_t getCurUserInfo(StorageUserInfo& storageUserInfo);

	int32_t listRegionIdInfo(RegionIdInfoArray& regionIdInfoArray);

	int32_t listFileVersion(const int64_t ownerId, 
		const int64_t fileId, 
		const PageParam& pageparam, 
		int64_t& nextOffset, 
		FileVersionList& fileVersionNodes);

	int32_t listFilesHadShareLink(const int64_t& owner_id, 
		const std::string& keyword, 
		const PageParam& pageparam, 
		int64_t& count, 
		MyShareNodeList& nodes);
	
	/*****************************************************************************************
	Function Name : getMsg
	Description   : 获取用户消息
	Input         : ownerId:		消息接收者
					startId:		消息起始Id
					offset:			偏移量
					status:			已读/未读，默认获取全部
	Output        : msgNodes:		消息列表
					total_cnt:		消息总数
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getMsg(const int64_t startId, 
		MsgList &msgNodes, 
		MsgStatus status = MS_All);

	int32_t getMsg(const int64_t offset, 
		MsgList &msgNodes, 
		int64_t& totalCnt, 
		bool isSys, 
		MsgStatus status = MS_All);

	int32_t getSysMsg(const int64_t offset, 
		MsgList &msgNodes, 
		int64_t& totalCnt);

	/*****************************************************************************************
	Function Name : updateMsg
	Description   : 更新消息状态
	Input         : ownerId:		消息接收者
					startId:		消息Id
					status:			已读/未读，默认更新为已读
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t updateMsg(const int64_t msgId,
		bool isSys, 
		MsgStatus status = MS_Readed);

	/*****************************************************************************************
	Function Name : deleteMsg
	Description   : 删除消息
	Input         : ownerId:		消息接收者
					startId:		消息Id
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t deleteMsg(const int64_t msgId);

	/*****************************************************************************************
	Function Name : getMsgListener
	Description   : 获取消息通知url
	Output        : url:		Websocket连接Url
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getMsgListener(std::string& url);

	int32_t downloadByUrl(const std::string& downloadUrl, 
		const std::string location);

	int32_t getThumbUrl(int64_t ownerId, 
		int64_t fileId, 
		int32_t height, 
		int32_t width, 
		std::string& thumbnailUrl);

	int32_t getMailInfo(const int64_t ownerId, 
		const int64_t fileId, 
		std::string source, 
		EmailInfoNode& emailInfoNode);

	int32_t setMailInfo(const int64_t ownerId, 
		const int64_t fileId, 
		EmailInfoNode& emailInfoNode);

	int32_t listGroups(const std::string& keyword, 
		const std::string& type, 
		const PageParam& pageparam, 
		int64_t& count, 
		GroupNodeList& nodes);
	
	/*****************************************************************************************
	Function Name : getGroupListUser
	Description   : 获取群组列表(getGroupListUser)
	Input         : owner_id			资源拥有者ID
					order_fileid		排序方式
					order_direction		升降序方式，"ASC":升序，“DESC”：降序
					nextOffset			下一个分页起始位置，返回0表示无下一分页
					limit				一次返回的数量
	Output        : total_count			返回文件（包括目录）总数
					pList				群组列表
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getGroupListUser(const int64_t& owner_id, 
		const PageParam& pageparam,
		const std::string& keyword,
		const std::string& type,
		const std::string& listRole,
		int64_t& total_count,
		UserGroupNodeInfoArray& TSNodeList);

	/*****************************************************************************************
	Function Name : createGroup
	Description   : 创建群组(createGroup)
	Input         : name			群组的名字
					desc			群组描述
					type ?			private 私有群组,public 公共群组,默认值为private
					status			群组状态。0 表示可用。1表示停用。默认值为0。
	Output        : _return			群组信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t createGroup(const std::string& name,
		const std::string& description,
		const std::string& type,
		const std::string& status,
		GroupNode& _return);

	/*****************************************************************************************
	Function Name : updateGroup
	Description   : 更新群组(updateGroup)
	Input         : groupId			群组ID
					name			群组名字
					desc			群组描述
					type			群组类型,private 私有群组,public 公共群组,默认值为public
					status			群组状态。0 表示可用。1表示停用。默认值为0。
	Output        : _return			群组信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t updateGroup(const int64_t& groupId,
		const std::string& name, 
		const std::string& desc,
		const std::string& type,
		const std::string& status,
		GroupNode& _return);

	/*****************************************************************************************
	Function Name : deleteGroup
	Description   : 删除群组(deleteGroup)
	Input         : groupId			群组ID
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t deleteGroup(const int64_t& groupId);

	/*****************************************************************************************
	Function Name : getGroup
	Description   : 获取群组信息(getGroup)
	Input         : groupId			群组ID
	Output        : _return			群组信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getGroup(const int64_t& groupId, 
		GroupNode& _return);

	/*****************************************************************************************
	Function Name : addGroupMember
	Description   : 增加群组成员(addGroupMember)
	Input         : groupId:		群组ID
					member_type:	成员类型。user: 用户。group: 群组。默认值为user。
					member_id:		成员的用户ID或者群组ID。
					groupRole:		群组角色。admin 拥有者,manager 管理者,member 普通用户,默认值为member。
	Output        : _return			群组信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t addGroupMember(const int64_t& groupId,
		const std::string& member_type,
		const int64_t& member_id, 
		const std::string& groupRole, 
		UserGroupNodeInfo& _return);

	/*****************************************************************************************
	Function Name : deleteGroupMember
	Description   : 删除群组成员
	Input         : groupId:		群组ID
					id:				用户ID
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t deleteGroupMember(const int64_t& groupId, 
		const int64_t& id);

	/*****************************************************************************************
	Function Name : getGroupListMemberInfo
	Description   : 列举群组成员
	Input         : groupId:		群组ID
					order_field:	排序字段。取值范围：groupRole 群组角色,userName 用户名
					order_direction:升降序方式。可设置为“ASC”或者“DESC”
					groupRole:		群组角色。admin 拥有者,manager 管理者,member 普通用户,all 所有用户,默认值为all。
					keyword:		搜索关键字。该关键字可匹配用户或群组的名称。
					limit:			一页最多得到的项
					offset:			分页
	Output        : total:			总共的项数
					_return			群组成员列表
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t getGroupListMemberInfo(const int64_t& groupId, 
		const std::string& keyword, 
		const std::string& groupRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserGroupNodeInfoArray& _return);

	/*****************************************************************************************
	Function Name : updateGroupUserInfo
	Description   : 更新群组成员信息(updateGroupUserInfo)
	Input         : groupId:		群组ID
					id:				用户ID
					groupRole：		群组角色。admin 拥有者，manager 管理者，member 普通用户，默认值为member。
	Output        : _return			团队空间成员信息
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
	int32_t updateGroupUserInfo(const int64_t& groupId,
		const int64_t& id,
		const std::string& groupRole,
		UserGroupNodeInfo& _return);

	int32_t getSystemRoleList(PermissionRoleArray& _return);

	int32_t deleteNodeAccesControl(const int64_t& ownerId, 
		const int64_t& aclId);

	int32_t addNodeAccesControl(const int64_t& ownerId, 
		const int64_t& id, 
		const int64_t& nodeId, 
		const std::string& type, 
		const std::string& role, 
		AccesNode& _return);

	int32_t updateNodeAccesControl(const int64_t& ownerId, 
		const int64_t& aclId, 
		const std::string& role, 
		AccesNode& _return);

	int32_t listNodeAccesControl(const int64_t& ownerId, 
		const int64_t& nodeId, 
		const int32_t& offset, 
		const int32_t& limit, 
		int64_t& total, 
		AccesNodeArray& _return);

	int32_t getNodeAccesControl(const int64_t& ownerId, 
		const int64_t& nodeId, 
		const int64_t& userId, 
		AccesNode& _return);

	int32_t addRestTask(int64_t srcOwnerId, 
		const std::list<int64_t>& srcNodeId,
		int64_t destOwnerId, 
		int64_t destFolderId,
		const std::string& type, 
		bool autoRename, 
		std::string& taskId);

	int32_t queryTaskStatus(const std::string& taskId, 
		std::string& taskStatus);

	int32_t listSystemRole(SysRoleInfoExList& nodes);

	int32_t getDeclaration(const std::string& clientType, 
		DeclarationInfo& declarationInfo);

	int32_t signDeclaration(const std::string& declarationID, 
		std::string isSign);

	int32_t totalUpload(const std::string& upload_url, 
		const int64_t len, 
		UploadCallback callback, 
		void* callbackData, 
		ProgressCallback progressCallback = NULL, 
		void* progressCallbackData = NULL);

	int32_t partUpload(const std::string& upload_url, 
		const int32_t part_id, 
		const int64_t len, 
		UploadCallback callback, 
		void* callbackData, 
		ProgressCallback progressCallback = NULL, 
		void* progressCallbackData = NULL);

	int32_t downloadFile(const std::string& download_url, 
		int64_t offset, 
		int64_t len, 
		DownloadCallback callback, 
		void* callbackData, 
		ProgressCallback progressCallback = NULL, 
		void* progressCallbackData = NULL);

private:
	RestClient();

	int32_t errorCodeDispatch(int32_t& resultCode, 
		const int32_t errorCode, 
		Malloc_Buffer& writeBuf);
	
	int32_t getHttpPerconditionErrorCode(const std::string& jsonStr);

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

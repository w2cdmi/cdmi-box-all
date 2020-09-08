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
    Description   : ���첢����http����
    Input         : mapProperty	����ͷ
                    strUri ����URI
    Output        : request ����
                    param �������
    Return        : RT_OK:�ɹ� Others:ʧ��
    *******************************************************************************************/
    int32_t request(const std::map<std::string, std::string>& mapProperty,
                const std::string & strUri,
				const SERVICE_TYPE& type,
                HttpRequest& request,
                RequestParam& param,
				bool ignoreRet = false);

    /*****************************************************************************************
    Function Name : login
    Description   : ��¼
    Input         : strUseName �û��� 
                    strPsd �û�����
                    clientSN �ͻ������б�
                    clientName �ͻ�������
                    clientVersion �ͻ��˰汾
                    clientType �ͻ�������
    Output        : loginResp ��¼��Ӧ
    Return        : �ɹ� RT_OK ʧ�� Others
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
    Description   : ע����¼
    Input         : None.
    Output        : None.
    Return        : �ɹ� RT_OK ʧ�� Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t logout();

    /*****************************************************************************************
    Function Name : refreshToken
    Description   : ˢ��token
    Input         : None.
    Output        : loginResp ��¼��Ӧ
    Return        : �ɹ� RT_OK ʧ�� Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t refreshToken(LoginRespInfo& loginResp);

	/*****************************************************************************************
    Function Name : checkHealthy
    Description   : ������������״̬
    Input         : None.
    Output        : None.
    Return        : �ɹ� RT_OK ʧ�� Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t checkHealthy();

    /*****************************************************************************************
    Function Name : listen
    Description   : �����ƶ˱仯
    Input         : syncVersion ��������ͬ���汾��
    Output        : lastVersion �ƶ�����ͬ���汾��
    Return        : �ɹ� RT_OK ʧ�� Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t listen(int32_t syncVersion, 
		int32_t& lastVersion);

	//�Զ�����
	int32_t createLdapUser(const std::string& loginName, 
		int64_t& userId);

    /*****************************************************************************************
    Function Name : setShareRes
    Description   : ���ù����ϵ
    Input         : ownerId �û�ID
					fileId �ļ�/�ļ���id
                    shareNodeExs ������չ�ڵ����, 
    Output        : ��
    Return        : �ɹ� 0 ʧ�� ����
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
    Description   : ɾ�������ϵ
    Input         : srcsharesNode Դ������Դ 
    Output        : None.
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
    //int32_t delShareRes(const ShareNode& srcsharesNode);

    /*****************************************************************************************
    Function Name : delShareResOwner
    Description   : ɾ��������Դ��Ա
    Input         : file_id �ļ�/�ļ���id, shareNodeEx ����ڵ���չ��Ϣ.
    Output        : None.
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t delShareResOwner(const int64_t& ownerId, 
		const int64_t& file_id, 
		ShareNodeEx& shareNodeEx);

    /*****************************************************************************************
    Function Name : quitShared
    Description   : �˳�����
    Input         : file_id �ļ�/�ļ���id, shareOwnerId ��Դ������id
    Output        : None.
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t quitShared(const int64_t& file_id, 
		const int64_t& shareOwnerId);

    /*****************************************************************************************
    Function Name : listDomainUsers
    Description   : �о�AD���û�
    Input         : keyWord �û����ؼ���
    Output        : shareUserInfos �����û���Ϣ.
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t listDomainUsers(const std::string& keyWord, 
		ShareUserInfoList& shareUserInfos, 
		int32_t limit);

    /*****************************************************************************************
    Function Name : getShareLink
    Description   : ��ȡ����
    Input         : file_id �ļ�/�ļ���id
                    ownerId ��Դ������id
    Output        : shareLinkNode ������Ϣ.
    Return        : �ɹ� 0 ʧ�� ����
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
    Description   : �޸�����
    Input         : file_id �ļ�/�ļ���id
                    ownerId ��Դ������id
                    shareLinkNodeEx ������չ��Ϣ
    Output        : shareLinkNode �޸ĺ����������.
    Return        : �ɹ� 0 ʧ�� ����
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
    Description   : ɾ������
    Input         : file_id �ļ�/�ļ���id
                    ownerId ��Դ������id
    Output        : None.
    Return        : �ɹ� 0 ʧ�� ����
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
    Description   : �ʼ���������
    Input         : file_id �ļ�/�ļ���id
                    ownerId ��Դ������id
                    linkUrl ������ַ
                    emails ���͵���email��ַ
    Output        : None.
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t sendShareLinkByEmail(const int64_t& file_id, 
                             const int64_t& owner_id, 
                             const std::string& linkUrl, 
                             EmailList& emails);

    /*****************************************************************************************
    Function Name : getServerSysConfig
    Description   : ��ȡ����������
    Input         : None.
    Output        : ServerSysConfig����
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getServerSysConfig(ServerSysConfig& serverSysConfig, 
		const OPTION_TYPE& option = OPTION_ALL);

	/*****************************************************************************************
    Function Name : getUpdateInfo
    Description   : ��ȡ������Ϣ
    Input         : None.
    Output        : updateInfo����
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getUpdateInfo(UpdateInfo& updateInfo);

	/*****************************************************************************************
    Function Name : downloadClient
    Description   : ���������ͻ���
    Input         : downloadUrl �ͻ������ص�ַ location �ͻ������ص��ı���·��.
    Output        : None.
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t downloadClient(const std::string& downloadUrl, 
		const std::string location);

	/*****************************************************************************************
    Function Name : getFeatureCode
    Description   : ���У����
    Input         : clientType �ͻ�������  version �ͻ��˰汾.
    Output        : featureCode У����
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getFeatureCode(const std::string& clientType, 
		const std::string& version, 
		std::string& featureCode);

	/*****************************************************************************************
    Function Name : downloadClient
    Description   : ���������ͻ���
    Input         : None.
    Output        : linknode ��������
					fileitem �ļ������ļ��ж���
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getFileInfoByShareLink(ShareLinkNode& linknode, 
		FileItem& fileitem);

	/*****************************************************************************************
    Function Name : downloadClient
    Description   : ���������ͻ���
	Input         : fileId ��������
					ownerId �ļ������ļ��ж���
					access	��������ʷ�ʽ
					shareLinkNodeEx	������չ��Ϣ.
    Output        : ShareLinkNode	��������
    Return        : �ɹ� 0 ʧ�� ����
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
    Description   : �оٹ����ϵ
	Input         : fileId ��������
					ownerId �ļ������ļ��ж���
					offset	ƫ����
					limit	�������
    Output        : ShareNodeList	����ڵ��б�
    Return        : �ɹ� 0 ʧ�� ����
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
    Description   : ��ȡ��������ַ
	Input         : type ��������
    Output        : serverurl	��������ַ
    Return        : �ɹ� 0 ʧ�� ����
    Modification  :
    Others        :
    *******************************************************************************************/
	int32_t getServerUrl(const SERVICE_TYPE& type, 
		std::string& serverurl);

	/*****************************************************************************************
	Function Name : removeFile
	Description   : ɾ���ļ���Ŀ¼(RemoveRemoteFile)
	Input         : owner_id		��Դӵ����ID
					file_id			�ļ���ID(�ļ�ID)
					type			����(�ļ�/�ļ���)
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t removeFile(const int64_t& owner_id, 
		const int64_t& file_id, 
		FILE_TYPE type);

	/*****************************************************************************************
	Function Name : renameFile
	Description   : �������ļ���Ŀ¼(RenameRemoteFile)
	Input         : owner_id		��Դӵ����ID
					file_id			�ļ���ID(�ļ�ID)
					new_name		��������������
					type			����(�ļ�/�ļ���)
	Output		  :	fileItem		�ɹ�ʱ���ص��ļ�����(�ļ��ж���)
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t renameFile(const int64_t& owner_id, 
		const int64_t& file_id, 
		const std::string& new_name, 
		FILE_TYPE type, 
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : moveFile
	Description   : �ƶ��ļ���Ŀ¼(MoveRemoteFile)
	Input         : owner_id		��Դӵ����ID
					file_id			�ļ���ID(�ļ�ID)
					dest_parent_id	Ŀ���ļ���ID
					dest_name		Ŀ������	
					type			����(�ļ�/�ļ���)
	Output		  :	fileItem		�ɹ�ʱ���ص��ļ�����(�ļ��ж���)
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t moveFile(const int64_t& owner_id, 
		const int64_t& file_id, 
		const int64_t& dest_parent_id,
		const bool auto_rename, 
		FILE_TYPE type,
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : copyFile
	Description   : �����ļ���Ŀ¼(CopyRemoteFile)
	Input         : owner_id		��Դӵ����ID
					file_id			�ļ���ID(�ļ�ID)
					dest_owner_id		Ŀ���ļ���ӵ����ID
					dest_parent_id	Ŀ���ļ���ID
					type			����(�ļ�/�ļ���)
	Output		  :	fileItem		�ɹ�ʱ���ص��ļ�����(�ļ��ж���)
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ����ļ���Ŀ¼�Ƿ����(CheckRemoteFileExist)
	Input         : owner_id		��Դӵ����ID
					file_id			�ļ���ID(�ļ�ID)
					type			����(�ļ�/�ļ���)
	Return        : RT_OK:���� Others:������
	*******************************************************************************************/
	int32_t checkFileExist(const int64_t& owner_id, 
		const int64_t& file_id, 
		FILE_TYPE type);

	/*****************************************************************************************
	Function Name : createFolder
	Description   : ����Ŀ¼(CreateRemoteFolder)
	Input         : owner_id		��Դӵ����ID
					parent_id		���ļ���ID
					name			�ļ�����
	Output		  :	fileItem		�ɹ�ʱ���ص��ļ��ж���
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ��ȡ�ļ�/����Ϣ
	Input         : owner_id		��Դӵ����ID
					file_id			�ļ���ID(�ļ�ID)
					type			����(�ļ�/�ļ���)
	Output		  :	fileItem		�ɹ�ʱ���ص��ļ�/�ļ��ж���
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ��ȡ�ļ�/����Ϣ
	Input         : owner_id		��Դӵ����ID
					parent_id		��ID
					name			�ļ�/����
	Output		  :	fileItems		�ɹ�ʱ���ص��ļ�/�ļ��ж���
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : �о�Ŀ¼(ListRemoteFolder)
	Input         : owner_id       ��Դӵ����ID
					folder_id      �ļ���ID(����Ϊ�գ���ʾ��Ŀ¼�о�)
					pageparam     ��ҳ����
					orderparam		�������
					trumbparam		����ͼ����
	Output        : total_count    �����ļ�������Ŀ¼������
					nextOffset     ��һ����ҳ��ʼλ�ã�����0��ʾ����һ��ҳ
					pList          �ļ��б�
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : Ԥ�ϴ�(PreUpload)
	Input         : fileItem       �ļ���Ϣ
					upload_type    �ϴ����ͣ�����/��Ƭ
					encrypt_key    ��Կ�����ܺ���ַ���
	Output        : existFileItem  sha1����ʱ�����Ѵ��ڵ��ļ�����
					uploadInfo     ������ʱ�����ļ��ϴ���Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t preUpload(const FileItem& fileItem, 
		const UploadType upload_type,  
		FileItem& existFileItem,
		UploadInfo& uploadInfo, 
		const std::string& encrypt_key = "");

	/*****************************************************************************************
	Function Name : batchPreUpload
	Description   : ����Ԥ�ϴ�(BatchPreUpload)
	Input         : ownerId    �ļ�������ID
					request    ����Ԥ�ϴ��������
	Output        : response   ����Ԥ�ϴ���Ӧ����
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t RestClient::batchPreUpload(const int64_t ownerId, 
		const BatchPreUploadRequest& request,
		BatchPreUploadResponse& response);

	/*****************************************************************************************
    Function Name : refreshUploadURL
    Description   : ˢ���ϴ�Url
    Input         : ownerId:��Դӵ����ID
                    file_id:�ļ�ID
                    uploadUrl:Ԥ�ϴ����ɵ�uploadUrl
    Output        : outUploadUrl �����µ�uploadUrl
    Return        : �ɹ� RT_OK ʧ�� Others
    Modification  :
    Others        :
    *******************************************************************************************/
    int32_t refreshUploadURL(const int64_t& ownerId, 
		const int64_t& file_id, 
		const std::string uploadUrl, 
		std::string& outUploadUrl);

	/*****************************************************************************************
	Function Name : totalUpload
	Description   : �����ϴ�(totalUpload)
	Input         : upload_url		�ϴ�url
					ucBuffer		�ϴ��ļ�����
					ulBufSize		�ϴ��ļ���С
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t totalUpload(const std::string& upload_url, 
		const unsigned char* ucBuffer, 
		uint32_t ulBufSize);

	/*****************************************************************************************
	Function Name : partUpload
	Description   : ��Ƭ�ϴ�(UploadPart)
	Input         : upload_url		�ϴ�url
					part_id			��Ƭ��ţ�������ָ���������1��N
					ucBuffer		�ϴ��ļ�����
					ulBufSize		�ϴ��ļ���С
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t partUpload(const std::string& upload_url, 
		const int32_t part_id,
		const unsigned char* ucBuffer, 
		uint32_t ulBufSize);

	/*****************************************************************************************
	Function Name : partUploadComplete
	Description   : ��Ƭ�ϴ����(CompleteUploadPart)
	Input         : upload_url		�ϴ�url
					file_id			�ļ�ID
					partList		��Ƭ���飬��װ�ļ��ķ�Ƭ��Ϣ
	Output        : fileItem		�����ɹ������ļ���Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t partUploadComplete(const std::string& upload_url,
		const PartList& partList,
		FileItem& fileItem);

	/*****************************************************************************************
	Function Name : partUploadCancel
	Description   : ��Ƭ�ϴ�ȡ��(CancelUploadPart)
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ��ȡ�Ŷӿռ��б�(getTeamSpaceListUser)
	Input         : owner_id			��Դӵ����ID
					order_fileid		����ʽ
					order_direction		������ʽ��"ASC":���򣬡�DESC��������
					nextOffset			��һ����ҳ��ʼλ�ã�����0��ʾ����һ��ҳ
					limit				һ�η��ص�����
	Output        : total_count			�����ļ�������Ŀ¼������
					pList				�Ŷӿռ��б�
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t getTeamSpaceListUser(const int64_t& owner_id, 
		const PageParam& pageparam,
		int64_t& total_count,
		UserTeamSpaceNodeInfoArray& TSNodeList);

	/*****************************************************************************************
	Function Name : createTeamSpace
	Description   : �����Ŷӿռ�(createTeamSpace)
	Input         : name			�Ŷӿռ������
					desc			�Ŷӿռ�����
					spaceQuota		�Ŷӿռ������������λMB��-1��ʾ�����ơ�Ĭ��ֵΪ-1��
					status			�Ŷӿռ�״̬��0 ��ʾ���á�1��ʾͣ�á�Ĭ��ֵΪ0��
					maxVersions		���汾����Ĭ��ֵΪ-1����ʾ�����ơ�
	Output        : _return			�Ŷӿռ���Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t createTeamSpace(const std::string& name,
		const std::string& desc, 
		const int64_t spaceQuota, 
		const int32_t status,
		const int32_t maxVersions,
		TeamSpacesNode& _return);

	/*****************************************************************************************
	Function Name : updateTeamSpace
	Description   : �����Ŷӿռ�(updateTeamSpace)
	Input         : teamId			�Ŷӿռ�ID
					name			�Ŷӿռ������
					desc			�Ŷӿռ�����
					spaceQuota		�Ŷӿռ������������λMB��-1��ʾ�����ơ�Ĭ��ֵΪ-1��
					status			�Ŷӿռ�״̬��0 ��ʾ���á�1��ʾͣ�á�Ĭ��ֵΪ0��
	Output        : _return			�Ŷӿռ���Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t updateTeamSpace(const int64_t& teamId,
		const std::string& name, 
		const std::string& desc,
		const int64_t spaceQuota,
		const int32_t status,
		TeamSpacesNode& _return);

	/*****************************************************************************************
	Function Name : getTeamSpace
	Description   : ��ȡ�Ŷӿռ���Ϣ(getTeamSpace)
	Input         : teamId			�Ŷӿռ�ID
	Output        : _return			�Ŷӿռ���Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t getTeamSpace(const int64_t& teamId, 
		TeamSpacesNode& _return);

	/*****************************************************************************************
	Function Name : deleteTeamSpace
	Description   : ɾ���Ŷӿռ�(deleteTeamSpace)
	Input         : teamId			�Ŷӿռ�ID
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t deleteTeamSpace(const int64_t& teamId);

	/*****************************************************************************************
	Function Name : addTeamSpaceMember
	Description   : �����Ŷӿռ��Ա(addTeamSpaceMember)
	Input         : teamId:			�Ŷӿռ�ID
					member_type:	��Ա���͡�user: �û���group: Ⱥ�顣Ĭ��ֵΪuser����ǰ�汾ֻ֧��user��
					member_id:		��Ա���û�ID����Ⱥ��ID��
					teamRole:		�Ŷӿռ��ɫ��admin ӵ����,manager ������,member ��ͨ�û�,��ǰֻ֧�����manager��member,Ĭ��ֵΪmember��
					role:			Ȩ�޽�ɫ���ơ�
	Output        : _return			�Ŷӿռ���Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t addTeamSpaceMember(const int64_t& teamId,
		const std::string& member_type,
		const int64_t& member_id, 
		const std::string& teamRole, 
		const std::string& role,
		UserTeamSpaceNodeInfo& _return);

	/*****************************************************************************************
	Function Name : getTeamSpaceMemberInfo
	Description   : ��ȡ�Ŷӿռ��Ա��Ϣ(getTeamSpaceMemberInfo)
	Input         : teamId		�Ŷӿռ�ID
					id			�Ŷӿռ��Ա��ϵID
	Output		  : _return     �Ŷӿռ��Ա��Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t getTeamSpaceMemberInfo(const int64_t& teamId, 
		const std::string& id, 
		UserTeamSpaceNodeInfo& _return);

	/*****************************************************************************************
	Function Name : updateTeamSpaceUserInfo
	Description   : �����Ŷӿռ��Ա��ϵ��Ϣ(updateTeamSpaceUserInfo)
	Input         : teamId:			�Ŷӿռ�ID
					id:				�Ŷӿռ��Ա��ϵID
					teamRole��		�Ŷӿռ��ɫ��admin ӵ���ߣ�manager �����ߣ�member ��ͨ�û���Ĭ��ֵΪmember��
					role��			Ȩ�޽�ɫ����
	Output        : _return			�Ŷӿռ��Ա��Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t updateTeamSpaceUserInfo(const int64_t& teamId,
		const int64_t& id,
		const std::string& teamRole,
		const std::string& role,
		UserTeamSpaceNodeInfo& _return);

	/*****************************************************************************************
	Function Name : getTeamSpaceListMemberInfo
	Description   : �о��Ŷӿռ��Ա
	Input         : teamId:			�Ŷӿռ�ID
					order_field:	�����ֶΡ�ȡֵ��Χ��teamRole �ռ��ɫ,createdAt ����ʱ��,userName �û���
					order_direction:������ʽ��������Ϊ��ASC�����ߡ�DESC��
					teamRole:		�Ŷӿռ��ɫ��admin ӵ����,manager ������,member ��ͨ�û�,all �����û�,Ĭ��ֵΪall��
					keyword:		�����ؼ��֡��ùؼ��ֿ�ƥ���û���Ⱥ������ơ�
					limit:			һҳ���õ�����
					offset:			��ҳ
	Output        : total:			�ܹ�������
					_return			�Ŷӿռ��Ա�б�
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t getTeamSpaceListMemberInfo(const int64_t& teamId, 
		const std::string& keyword, 
		const std::string& teamRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserTeamSpaceNodeInfoArray& _return);

	/*****************************************************************************************
	Function Name : deleteTeamSpaceMember
	Description   : ɾ���Ŷӿռ��Ա
	Input         : teamId:			�Ŷӿռ�ID
					id:				�Ŷӿռ��Ա��ϵID
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ��ȡ�û���Ϣ
	Input         : ownerId:		��Ϣ������
					startId:		��Ϣ��ʼId
					offset:			ƫ����
					status:			�Ѷ�/δ����Ĭ�ϻ�ȡȫ��
	Output        : msgNodes:		��Ϣ�б�
					total_cnt:		��Ϣ����
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ������Ϣ״̬
	Input         : ownerId:		��Ϣ������
					startId:		��ϢId
					status:			�Ѷ�/δ����Ĭ�ϸ���Ϊ�Ѷ�
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t updateMsg(const int64_t msgId,
		bool isSys, 
		MsgStatus status = MS_Readed);

	/*****************************************************************************************
	Function Name : deleteMsg
	Description   : ɾ����Ϣ
	Input         : ownerId:		��Ϣ������
					startId:		��ϢId
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t deleteMsg(const int64_t msgId);

	/*****************************************************************************************
	Function Name : getMsgListener
	Description   : ��ȡ��Ϣ֪ͨurl
	Output        : url:		Websocket����Url
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ��ȡȺ���б�(getGroupListUser)
	Input         : owner_id			��Դӵ����ID
					order_fileid		����ʽ
					order_direction		������ʽ��"ASC":���򣬡�DESC��������
					nextOffset			��һ����ҳ��ʼλ�ã�����0��ʾ����һ��ҳ
					limit				һ�η��ص�����
	Output        : total_count			�����ļ�������Ŀ¼������
					pList				Ⱥ���б�
	Return        : RT_OK:�ɹ� Others:ʧ��
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
	Description   : ����Ⱥ��(createGroup)
	Input         : name			Ⱥ�������
					desc			Ⱥ������
					type ?			private ˽��Ⱥ��,public ����Ⱥ��,Ĭ��ֵΪprivate
					status			Ⱥ��״̬��0 ��ʾ���á�1��ʾͣ�á�Ĭ��ֵΪ0��
	Output        : _return			Ⱥ����Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t createGroup(const std::string& name,
		const std::string& description,
		const std::string& type,
		const std::string& status,
		GroupNode& _return);

	/*****************************************************************************************
	Function Name : updateGroup
	Description   : ����Ⱥ��(updateGroup)
	Input         : groupId			Ⱥ��ID
					name			Ⱥ������
					desc			Ⱥ������
					type			Ⱥ������,private ˽��Ⱥ��,public ����Ⱥ��,Ĭ��ֵΪpublic
					status			Ⱥ��״̬��0 ��ʾ���á�1��ʾͣ�á�Ĭ��ֵΪ0��
	Output        : _return			Ⱥ����Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t updateGroup(const int64_t& groupId,
		const std::string& name, 
		const std::string& desc,
		const std::string& type,
		const std::string& status,
		GroupNode& _return);

	/*****************************************************************************************
	Function Name : deleteGroup
	Description   : ɾ��Ⱥ��(deleteGroup)
	Input         : groupId			Ⱥ��ID
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t deleteGroup(const int64_t& groupId);

	/*****************************************************************************************
	Function Name : getGroup
	Description   : ��ȡȺ����Ϣ(getGroup)
	Input         : groupId			Ⱥ��ID
	Output        : _return			Ⱥ����Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t getGroup(const int64_t& groupId, 
		GroupNode& _return);

	/*****************************************************************************************
	Function Name : addGroupMember
	Description   : ����Ⱥ���Ա(addGroupMember)
	Input         : groupId:		Ⱥ��ID
					member_type:	��Ա���͡�user: �û���group: Ⱥ�顣Ĭ��ֵΪuser��
					member_id:		��Ա���û�ID����Ⱥ��ID��
					groupRole:		Ⱥ���ɫ��admin ӵ����,manager ������,member ��ͨ�û�,Ĭ��ֵΪmember��
	Output        : _return			Ⱥ����Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t addGroupMember(const int64_t& groupId,
		const std::string& member_type,
		const int64_t& member_id, 
		const std::string& groupRole, 
		UserGroupNodeInfo& _return);

	/*****************************************************************************************
	Function Name : deleteGroupMember
	Description   : ɾ��Ⱥ���Ա
	Input         : groupId:		Ⱥ��ID
					id:				�û�ID
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t deleteGroupMember(const int64_t& groupId, 
		const int64_t& id);

	/*****************************************************************************************
	Function Name : getGroupListMemberInfo
	Description   : �о�Ⱥ���Ա
	Input         : groupId:		Ⱥ��ID
					order_field:	�����ֶΡ�ȡֵ��Χ��groupRole Ⱥ���ɫ,userName �û���
					order_direction:������ʽ��������Ϊ��ASC�����ߡ�DESC��
					groupRole:		Ⱥ���ɫ��admin ӵ����,manager ������,member ��ͨ�û�,all �����û�,Ĭ��ֵΪall��
					keyword:		�����ؼ��֡��ùؼ��ֿ�ƥ���û���Ⱥ������ơ�
					limit:			һҳ���õ�����
					offset:			��ҳ
	Output        : total:			�ܹ�������
					_return			Ⱥ���Ա�б�
	Return        : RT_OK:�ɹ� Others:ʧ��
	*******************************************************************************************/
	int32_t getGroupListMemberInfo(const int64_t& groupId, 
		const std::string& keyword, 
		const std::string& groupRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserGroupNodeInfoArray& _return);

	/*****************************************************************************************
	Function Name : updateGroupUserInfo
	Description   : ����Ⱥ���Ա��Ϣ(updateGroupUserInfo)
	Input         : groupId:		Ⱥ��ID
					id:				�û�ID
					groupRole��		Ⱥ���ɫ��admin ӵ���ߣ�manager �����ߣ�member ��ͨ�û���Ĭ��ֵΪmember��
	Output        : _return			�Ŷӿռ��Ա��Ϣ
	Return        : RT_OK:�ɹ� Others:ʧ��
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

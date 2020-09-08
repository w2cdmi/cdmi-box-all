#include "RestClient.h"
#include "HttpCbFuns.h"
#include "JsonParser.h"
#include "JsonGeneration.h"
#include "Utility.h"
#include "Util.h"
#include "SmartHandle.h"
#include <fstream>

#ifndef MODULE_NAME
#define MODULE_NAME ("RestClient")
#endif

using namespace SD;

static std::map<std::string, int32_t> initErrorCodes400();
static std::map<std::string, int32_t> initErrorCodes404();

static std::map<std::string,int32_t> errorCodeMap400=initErrorCodes400();
static std::map<std::string,int32_t> errorCodeMap404=initErrorCodes404();

/*****************************************************************************************
Function Name : removeFile
Description   : 删除文件或目录(RemoveRemoteFile)
Input         : owner_id		资源拥有者ID
				file_id			文件夹ID(文件ID)
				type			类型(文件/文件夹)
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::removeFile(const int64_t& owner_id, const int64_t& file_id, FILE_TYPE type)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::removeFile id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//DELETE /api/v2/folders/{ownerId}/{folderId}
	//DELETE /api/v2/files/{ownerId}/{fileId}
	//HttpRequest request(HttpRequestTypeDELETE);

	std::ostringstream ostr;
	if(FILE_TYPE_DIR == type)
	{
		ostr << "/folders/";
	}
	else if(FILE_TYPE_FILE == type)
	{
		ostr << "/files/";
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	ostr << owner_id << "/" << file_id;

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::removeFile id:%I64d, ret:%d", file_id, ret);
	}
	
	return ret;
}

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
int32_t RestClient::renameFile(const int64_t& owner_id, 
							 const int64_t& file_id, 
							 const std::string& new_name, 
							 FILE_TYPE type, 
							 FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::renameFile new_name:" + 
		Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(new_name)));
	int32_t ret = RT_OK;

	if (new_name.empty())
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/v2/folders/{ownerID}/{folderID}
	//PUT /api/v2/files/{ownerID}/{folderID}
	std::ostringstream ostr;
	if(FILE_TYPE_DIR == type)
	{
		ostr << "/folders/";
	}
	else if(FILE_TYPE_FILE == type)
	{
		ostr << "/files/";
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	ostr << owner_id << "/" << file_id;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	JsonGeneration::genNewName(new_name, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		return ret;
	}

	if(FILE_TYPE_FILE == type)
	{
		ret = JsonParser::parseFileObj(writeBuf, fileItem);
	}
	else
	{
		ret = JsonParser::parseFolderObj(writeBuf, fileItem);
	}

	if(file_id != fileItem.id())
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::renameFile error, old id:%I64d, new id:%I64d, ret:%d", 
				file_id, fileItem.id(), ret);
		return RT_INVALID_PARAM;
	}
	else if (HTTP_NOT_FOUND == ret || HTTP_BAD_REQUEST == ret)
	{
		int32_t resultErrorCode = 0;
		if (!errorCodeDispatch(resultErrorCode, ret, writeBuf))
		{
			ret = resultErrorCode;
		}
	}

	return ret;
}

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
int32_t RestClient::moveFile(const int64_t& owner_id, 
						   const int64_t& file_id,
						   const int64_t& dest_parent_id, 
						   const bool auto_rename,
						   FILE_TYPE type,
						   FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::moveFile id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/v2/folders/{ownerId}/{folderId}/move
	//PUT /api/v2/files/{ownerId}/{fileId}/move
	
	std::ostringstream ostr;
	if(FILE_TYPE_DIR == type)
	{
		ostr << "/folders/";
	}
	else if(FILE_TYPE_FILE == type)
	{
		ostr << "/files/";
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	ostr << owner_id << "/" << file_id << "/move";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genDestFolder(dest_parent_id, owner_id, auto_rename, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		return ret;
	}
	
	if(FILE_TYPE_FILE == type)
	{
		ret = JsonParser::parseFileObj(writeBuf, fileItem);
	}
	else
	{
		ret = JsonParser::parseFolderObj(writeBuf, fileItem);
	}
	if(file_id!=fileItem.id())
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::moveFile error, old id:%I64d, new id:%I64d, ret:%d", 
				file_id, fileItem.id(), ret);
		return RT_INVALID_PARAM;
	}

	return ret;
}

/*****************************************************************************************
Function Name : copyFile
Description   : 复制文件或目录(CopyRemoteFile)
Input         : owner_id		资源拥有者ID
				file_id			文件夹ID(文件ID)
				dest_parent_id	目标文件夹ID
				dest_name		目标文件名
				type			类型(文件/文件夹)
Output		  :	fileItem		成功时返回的文件对象(文件夹对象)
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int RestClient::copyFile(const int64_t& owner_id, 
						   const int64_t& file_id,
						   const int64_t& dest_owner_id,
						   const int64_t& dest_parent_id, 
						   const bool auto_rename,
						   FILE_TYPE type,
						   FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::copyFile id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/v2/folders/{ownerId}/{folderId}/copy
	//PUT /api/v2/files/{ownerId}/{fileId}/copy
	
	std::ostringstream ostr;
	if(FILE_TYPE_DIR == type)
	{
		ostr << "/folders/";
	}
	else if(FILE_TYPE_FILE == type)
	{
		ostr << "/files/";
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	ostr << owner_id << "/" << file_id << "/copy";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genDestFolder(dest_parent_id, dest_owner_id, auto_rename, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		return ret;
	}
	
	if(FILE_TYPE_FILE == type)
	{
		ret = JsonParser::parseFileObj(writeBuf, fileItem);
	}
	else
	{
		ret = JsonParser::parseFolderObj(writeBuf, fileItem);
	}

	return ret;
}

/*****************************************************************************************
Function Name : checkFileExist
Description   : 检查文件或目录是否存在(CheckRemoteFileExist)
Input         : owner_id		资源拥有者ID
				file_id			文件夹ID(文件ID)
				type			类型(文件/文件夹)
Return        : RT_OK:存在 Others:不存在
*******************************************************************************************/
int32_t RestClient::checkFileExist(const int64_t& owner_id, const int64_t& file_id, FILE_TYPE type)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::checkFileExist id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);

	//GET /api/folders/{owneId}/{folderId}
	//GET /api/files/{owneId}/{fileId}
	std::ostringstream ostr;
	if(FILE_TYPE_DIR == type)
	{
		ostr << "/folders/";
	}
	else if(FILE_TYPE_FILE == type)
	{
		ostr << "/files/";
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	ostr << owner_id << "/" << file_id;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		return ret;
	}

	//能正常获取到文件/文件夹信息，则表示文件存在
	FileItem fileItem;
	if(FILE_TYPE_FILE == type)
	{
		ret = JsonParser::parseFileObj(writeBuf, fileItem);
	}
	else
	{
		ret = JsonParser::parseFolderObj(writeBuf, fileItem);
	}

	return ret;
}

/*****************************************************************************************
Function Name : createFolder
Description   : 创建目录(CreateRemoteFolder)
Input         : owner_id		资源拥有者ID
				parent_id		父文件夹ID
				name			文件夹名
Output		  :	fileItem		成功时返回的文件夹对象
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::createFolder(const int64_t& owner_id, 
							   const int64_t& parent_id, 
							   const std::string& name, 
							   const int64_t& contentcreatedat,
							   const int64_t& contentmodifiedat,
							   const int32_t& extraType,
							   const bool autoMerge,
							   FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::createFolder name:" + 
		Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(name)));
	int32_t ret = RT_OK;

	if (name.empty())
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//POST /api/v2/folders/{ownerId}
	std::ostringstream ostr;
	ostr << "/folders/" << owner_id << "/";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genNewFolder(name, parent_id, contentcreatedat, contentmodifiedat, extraType, autoMerge, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (FILE_CREATED == ret)
	{
		return JsonParser::parseFolderObj(writeBuf, fileItem);;
	}
	return ret;
}

/*****************************************************************************************
Function Name : getFileInfo
Description   : 获取文件/夹信息
Input         : owner_id		资源拥有者ID
				file_id			文件夹ID(文件ID)
				type			类型(文件/文件夹)
Output		  :	fileItem		成功时返回的文件/文件夹对象
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::getFileInfo(const int64_t& owner_id, 
							  const int64_t& file_id, 
							  FILE_TYPE type,
							  FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getFileInfo id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypeGET);

	//GET /api/folders/{owneId}/{folderId}
	//GET /api/files/{owneId}/{fileId}
	std::ostringstream ostr;
	if(FILE_TYPE_DIR == type)
	{
		ostr << "/folders/";
	}
	else if(FILE_TYPE_FILE == type)
	{
		ostr << "/files/";
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	ostr << owner_id << "/" << file_id;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		return ret;
	}

	if(FILE_TYPE_FILE == type)
	{
		ret = JsonParser::parseFileObj(writeBuf, fileItem);
	}
	else
	{
		ret = JsonParser::parseFolderObj(writeBuf, fileItem);
	}

	return ret;
}

int32_t RestClient::getFilePath(const int64_t& owner_id, const int64_t& file_id, std::vector<int64_t>& parentIds, std::vector<std::string>& parentNames)
{
	if(0==file_id)
	{
		return RT_OK;
	}
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getFilePath id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/nodes/ownerId/nodeId/path
	std::ostringstream ostr;
	ostr << "/nodes/" << owner_id << "/" << file_id << "/path";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseFilePath(writeBuf, parentIds, parentNames);
	}
	return ret;
}

int32_t RestClient::getFilePermissions(const int64_t& owner_id, const int64_t& file_id, const int64_t& user_id, File_Permissions& filePermissions)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getFilePermissions id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/permissions/ownerId/nodeId/userId
	std::ostringstream ostr;
	ostr << "/permissions/" << owner_id << "/" << file_id << "/" << user_id;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseFilePermissions(writeBuf, filePermissions);
	}

	return ret;
}

/*****************************************************************************************
	Function Name : getFileInfoByParentAndName
	Description   : 获取文件/夹信息
	Input         : owner_id		资源拥有者ID
					parent_id		父ID
					name			文件/夹名
	Output		  :	fileItems		成功时返回的文件/文件夹对象
	Return        : RT_OK:成功 Others:失败
	*******************************************************************************************/
int32_t RestClient::getFileInfoByParentAndName(const int64_t& owner_id, 
											   const int64_t& parent_id, 
											   const std::string& name, 
											   std::list<FileItem*>& fileItems)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string(
		"RestClient::getFileInfoByParentAndName parent:%I64d, name:%s", parent_id, 
		Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(name)).c_str()));

	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//POST /api/v2/folders/{ownerId}/{folderId}/children
	std::ostringstream ostr;
	ostr << "/folders/" << owner_id << "/" << parent_id << "/children";

	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	reqRoot["name"] = name;
	std::string result = writer.write(reqRoot);
	Malloc_Buffer readBuf(result.length());
	string2Buff(result, readBuf);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		return ret;
	}

	return JsonParser::parseFileFolders(writeBuf, fileItems);
}

int32_t RestClient::checkExistByParentAndName(const int64_t& owner_id, 
											   const int64_t& parent_id, 
											   const std::string& name)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string(
		"RestClient::getFileInfoByParentAndName parent:%I64d, name:%s", parent_id, 
		Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(name)).c_str()));

	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//POST /api/v2/folders/{ownerId}/{folderId}/children
	std::ostringstream ostr;
	ostr << "/folders/" << owner_id << "/" << parent_id << "/children";

	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	reqRoot["name"] = name;
	std::string result = writer.write(reqRoot);
	Malloc_Buffer readBuf(result.length());
	string2Buff(result, readBuf);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(RT_OK==ret && writeBuf.lOffset <= 25)
	{
		//{"files":[],"folders":[]}
		return HTTP_NOT_FOUND;
	}
	return ret;
}

/*****************************************************************************************
Function Name : listFolder
Description   : 列举目录
Input         : owner_id       资源拥有者ID
				folder_id      文件夹ID(可以为空，表示根目录列举)
				pageparam     分页参数
				orderparam		排序参数
				trumbparam		缩略图参数
Output        : total_count    返回文件（包括目录）总数
				nextOffset     下一个分页起始位置，返回0表示无下一分页
				fileItems      文件列表
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::listFolder(const int64_t& owner_id,
							 const int64_t& folder_id,
							 const PageParam& pageParam,
							 int64_t& nextOffset,
							 std::list<FileItem*>& fileItems)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listFolder folder_id:%I64d", folder_id));
	int32_t ret = RT_OK;
	int64_t total_count = 0;

	//CSRequest request(HttpRequestTypeGET);

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
		
	//POST /folders/{ownerId}/{folderId}/items 
	std::ostringstream ostr;
	ostr << "/folders/"<< owner_id << "/"
		 << folder_id << "/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	if(false != pageParam.is_used)
	{
		JsonGeneration::genListFolder(pageParam, readBuf);
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseListFolderResult(writeBuf, fileItems, total_count);
	}

	if (false != pageParam.is_used)
	{
		nextOffset = nextOffset + pageParam.limit;
		if(total_count <= nextOffset)
		{
			nextOffset = 0;
		}
	}
	else
	{
		nextOffset = 0;
	}

	return ret;
}

int32_t RestClient::listPage(const int64_t& owner_id,
							   const int64_t& folder_id,
							   const PageParam& pageParam,
							   int64_t& count,
							   std::list<FileItem*>& fileItems)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listPage folder_id:%I64d", folder_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
		
	//POST /folders/{ownerId}/{folderId}/items 
	std::ostringstream ostr;
	ostr << "/folders/"<< owner_id << "/"
		 << folder_id << "/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	if(false != pageParam.is_used)
	{
		JsonGeneration::genListFolder(pageParam, readBuf);
		param.readData = &readBuf;
		param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	}
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseListFolderResult(writeBuf, fileItems, count);
	}

	return ret;
}

int32_t RestClient::search(const int64_t& owner_id,
						   const std::string& name,
						   const PageParam& pageParam,
						   int64_t& count,
						   std::list<FileItem*>& fileItems,
						   bool needPath,
						   std::map<int64_t, std::wstring>& pathInfo)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::search name:%s", name.c_str()));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
		
	//POST /nodes/{ownerId}/search 
	std::ostringstream ostr;
	ostr << "/nodes/"<< owner_id << "/search";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	if(false != pageParam.is_used)
	{
		(void)JsonGeneration::genSearch(pageParam, name, needPath, readBuf);
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseListFolderResult(writeBuf, fileItems, count);
		if(needPath)
		{
			JsonParser::parsePathInfo(writeBuf, pathInfo);
		}
	}

	return ret;	
}

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
int32_t RestClient::preUpload(const FileItem& fileItem,
							const UploadType upload_type, 
							FileItem& existFileItem,
							UploadInfo& uploadInfo, 
							const std::string& encrypt_key)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::preUpload name:" + 
		Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(fileItem.name())));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/files/{owneId}/
	std::ostringstream ostr;
	ostr << "/files/" << fileItem.ownerId()<<"/";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	ret = JsonGeneration::genUploadReq(fileItem, upload_type, encrypt_key, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (FILE_CREATED == ret)
	{
		JsonParser::parseExistFileObj(writeBuf, existFileItem);
	}
	else if (RT_OK == ret)
	{
		ret = JsonParser::parseUploadInfo(writeBuf, uploadInfo);
		SERVICE_DEBUG(MODULE_NAME, ret, "id is %I64d, upload url is %s.", 
			uploadInfo.file_id, uploadInfo.upload_url.c_str());
	}
	else if (HTTP_NOT_FOUND == ret || HTTP_BAD_REQUEST == ret)
	{
		int32_t resultErrorCode = 0;
		if (!errorCodeDispatch(resultErrorCode, ret, writeBuf))
		{
			ret = resultErrorCode;
		}		
	}
	
	return ret;
}

/*****************************************************************************************
Function Name : batchPreUpload
Description   : 批量预上传
Input         : request   批量预上传请求对象
Output        : response  批量预上传响应对象
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::batchPreUpload(const int64_t ownerId, const BatchPreUploadRequest& request,
								   BatchPreUploadResponse& response)
{
	SERVICE_FUNC_TRACE(MODULE_NAME,  Utility::String::format_string("RestClient::batchPreUpload parent: %I64d", request.parent));
	if(request.fileList.empty())
	{
		SERVICE_WARN(MODULE_NAME, RT_INVALID_PARAM, "Invalid parameter: file list is empty");
		return RT_OK;
	}

	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT /api/files/{owneId}/multi
	std::ostringstream ostr;
	ostr << "/files/" << ownerId <<"/multi";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	ret = JsonGeneration::genBatchPreUploadReq(request, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseBatchPreUploadInfo(writeBuf, response);
		SERVICE_DEBUG(MODULE_NAME, RT_OK, "Batch preupload response: %s", (char *)writeBuf.pBuf);
	}

	return ret;
}

/*****************************************************************************************
Function Name : refreshUploadURL
Description   : 刷新上传Url
Input         : ownerId:资源拥有者ID
                file_id:文件ID
                uploadUrl:预上传生成的uploadUrl
Output        : outUploadUrl 返回新的uploadUrl
Return        : 成功 RT_OK 失败 Others
Created By    : dailinye, 2014.01.14
Modification  :
Others        :
*******************************************************************************************/
int32_t RestClient::refreshUploadURL(const int64_t& ownerId, const int64_t& file_id, const std::string uploadUrl, std::string& outUploadUrl)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::refreshUploadURL uploadUrl:" + uploadUrl);

	int32_t ret = RT_OK;

	outUploadUrl = "";

	if (uploadUrl.empty())
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/v2/files/{ownerId}/{fileId}/refreshurl
	std::ostringstream ostr;
	ostr << "/files/" << ownerId <<"/" << file_id << "/refreshurl";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	JsonGeneration::genUploadUrl(uploadUrl, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		JsonParser::parseUploadUrl(writeBuf, outUploadUrl);
	}

	return ret;
}

/*****************************************************************************************
Function Name : totalUpload
Description   : 整体上传(totalUpload)
Input         : upload_url		上传url
				ucBuffer		上传文件内容
				ulBufSize		上传文件大小
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::totalUpload(const std::string& upload_url, const unsigned char* ucBuffer, uint32_t ulBufSize)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::totalUpload upload_url:" + upload_url);
	int32_t ret = RT_OK;
	
	if (upload_url.empty() || !ucBuffer)
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT /{upload_url}
	//HttpRequest request(HttpRequestTypePUT);

	DataBuffer readBuf;
    readBuf.pBuf = (unsigned char *)ucBuffer;
    readBuf.lBufLen = ulBufSize;
    readBuf.lOffset = 0;

    RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
    param.readDataCallback =  HttpCbFuns::putObjectDataCallback;
    param.readData = &readBuf;
    param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, upload_url, SERVICE_CLOUDAPP, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::totalUpload upload_url:%s, ret:%d", upload_url.c_str(), ret);
	}

	return ret;
}

/*****************************************************************************************
Function Name : partUpload
Description   : 分片上传(UploadPart)
Input         : upload_url		上传url
				part_id			分片序号，调用者指定，建议从1到N
				ucBuffer		上传文件内容
				ulBufSize		上传文件大小
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::partUpload(const std::string& upload_url, 
							 const int32_t part_id,
							 const unsigned char* ucBuffer, 
							 uint32_t ulBufSize)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::partUpload upload_url:" + upload_url + ", part_id:" + Utility::String::format_string("%d", part_id));
	int32_t ret = RT_OK;

	if (upload_url.empty() || !ucBuffer)
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT /{upload_url}?upload_id={upload_id}&partID={part_id}
	//HttpRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << upload_url << "?partId=" << part_id;

	DataBuffer readBuf;
    readBuf.pBuf = (unsigned char *)ucBuffer;
    readBuf.lBufLen = ulBufSize;
    readBuf.lOffset = 0;

    RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
    param.readDataCallback =  HttpCbFuns::putObjectDataCallback;
    param.readData = &readBuf;
    param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_CLOUDAPP, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::partUpload upload_url:%s, part_id:%d, ret:%d", 
			upload_url.c_str(), part_id, ret);
	}

	return ret;
}

/*****************************************************************************************
Function Name : partUploadComplete
Description   : 分片上传完成(CompleteUploadPart)
Input         : upload_url		上传url
				partList		分片数组，组装文件的分片信息
Output        : fileItem		操作成功返回文件信息
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::partUploadComplete(const std::string& upload_url, 
									 const PartList& partList,
									 FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::partUploadComplete upload_url:" + upload_url);
	int32_t ret = RT_OK;

	if (upload_url.empty())
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/files/{owneId}/{fileId}/parts
	std::ostringstream ostr;
	ostr << upload_url << "?commit";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	(void)JsonGeneration::genPartList(partList, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	//param.writeData = &writeBuf;
	//param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_CLOUDAPP, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::partUploadComplete upload_url:%s, ret:%d", 
			upload_url.c_str(), ret);
	}

	if (HTTP_NOT_FOUND == ret || HTTP_BAD_REQUEST == ret)
	{
		int32_t resultErrorCode = 0;
		if (!errorCodeDispatch(resultErrorCode, ret, writeBuf))
		{
			ret = resultErrorCode;
		}
	}

	return ret;
}

/*****************************************************************************************
Function Name : partUploadCancel
Description   : 分片上传取消(CancelUploadPart)
Return        : RT_OK:成功 Others:失败
*******************************************************************************************/
int32_t RestClient::partUploadCancel(const std::string& upload_url)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::partUploadCancel"));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	
	//DELETE /{upload_url}
	//HttpRequest request(HttpRequestTypeDELETE);

	std::ostringstream ostr;
	ostr << upload_url;

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_CLOUDAPP, request_, param);
	
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::partUploadCancel, ret:%d", ret);
	}

	return ret;
}

int32_t RestClient::getDownloadUrl(const int64_t& owner_id, const int64_t& file_id, std::string& downloadUrl)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getDownloadUrl id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/files/{owneId}/{fileId}/url
	std::ostringstream ostr;
	ostr << "/files/" << owner_id <<"/" << file_id << "/url";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseDownloadInfo(writeBuf, downloadUrl);
		/*
		std::map<std::string,std::string> mapResponseHeader = request_.getResponseHeaders();
		if(mapResponseHeader.find("x-content-id") != mapResponseHeader.end())
		{
			versionId = mapResponseHeader.find("x-content-id")->second;
		}
		*/
	}

	return ret;
}


int32_t RestClient::downloadFile(const std::string& download_url, 
							   unsigned char* fileBuffer,
							   int64_t& lBufSize, 
							   int64_t lOffset)
{
	std::ostringstream ostr_range;
    int64_t ullEnd;
    ullEnd = lOffset + lBufSize -1;
    ostr_range << "bytes=" << lOffset << "-" <<  ullEnd;
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::downloadFile, range:%s", ostr_range.str().c_str()));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /{downloadUrl}
	//HttpRequest request(HttpRequestTypeGET);

	std::ostringstream ostr;
	ostr << download_url;

	Malloc_Buffer writeBuf((size_t)lBufSize);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
    param.writeData = &writeBuf;
    param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
    param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

    mapHeaders[HEAD_RANGE] = ostr_range.str();

	ret = this->request(mapHeaders, ostr.str(), SERVICE_CLOUDAPP, request_, param);
	if (RT_OK == ret)
    {
		lBufSize = writeBuf.lOffset;
		memcpy_s(fileBuffer, size_t(lBufSize), writeBuf.pBuf, size_t(lBufSize));
		ret = RT_OK;
    }
	
	return ret;
}

int32_t RestClient::getUploadPart(const std::string& upload_url, PartInfoList& partInfoList)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getUploadPart upload_url:" + upload_url);
	int32_t ret = RT_OK;

	if (upload_url.empty())
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /upload_url
	//HttpRequest request(HttpRequestTypeGET);

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, upload_url, SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parsePartInfo(writeBuf, partInfoList);
	}

	return ret;
}

int32_t RestClient::createVersion(const int64_t& owner_id, const int64_t& file_id)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::createVersion id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT api/v1/files/{owneId}/{fileId}/versions
	//HttpRequest request(HttpRequestTypePUT);

	std::ostringstream ostr;
	ostr << "/files/" << owner_id << "/" << file_id << "/versions";

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	return ret;
}

int32_t RestClient::setSyncStatus(const int64_t& owner_id, const int64_t& file_id, const FILE_TYPE type, const bool is_sync)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::setSyncStatus id:%I64d %s", file_id, is_sync?"true":"false"));

	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT api/v2/folders/{owneId}/{folderId}
	std::ostringstream ostr;
	ostr << ((FILE_TYPE_DIR == type)?"/folders/":"/files/");
	ostr << owner_id << "/" << file_id;

	DataBuffer readBuf;
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	reqRoot["syncStatus"] = is_sync;
	std::string renameJsonStr = writer.write(reqRoot);
	string2Buff(renameJsonStr, readBuf);
	if (NULL == readBuf.pBuf)
	{
		return RT_MEMORY_MALLOC_ERROR;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;	

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	return ret;
}

int32_t RestClient::getSyncMetadata(const int64_t& owner_id, int64_t syncVersion, std::string& limitCnt, std::string& curCnt)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getSyncMetadata syncVersion:" + Utility::String::type_to_string<std::string>(syncVersion));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/metadata/{ownerId}?syncVersion={syncVersion}&zip={zip}
	std::ostringstream ostr;
	ostr << "/metadata/" << owner_id << "?syncVersion=" << syncVersion << "&zip=false";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	std::wstring path = configure_.userDataPath();
	if (!SD::Utility::FS::is_exist(path))
	{
		(void)SD::Utility::FS::create_directories(path);
	}
	path = path + PATH_DELIMITER + SYNCDATA_DIR + PATH_DELIMITER + SYNCDATA_TABLE;
	SmartFile file = _wfsopen(path.c_str(), L"wb", _SH_DENYRW);
	if (NULL == file)
	{
		return RT_FILE_OPEN_ERROR;
	}
	writeBuf.uDefParam = file;

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::downloadFileCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		std::map<std::string,std::string> mapResponseHeader = request_.getResponseHeaders();
		if(mapResponseHeader.find("x-user-node-limit") != mapResponseHeader.end())
		{
			limitCnt = mapResponseHeader.find("x-user-node-limit")->second;
		}
		if(mapResponseHeader.find("x-user-node-current") != mapResponseHeader.end())
		{
			curCnt = mapResponseHeader.find("x-user-node-current")->second;
		}

		SERVICE_ERROR(MODULE_NAME, ret, 
			"RestClient::getSyncMetadata syncVersion:%d, ret:%d", 
			syncVersion, ret);

		return ret;
	}

	return RT_OK;
}

int32_t RestClient::getAllMetadata(const int64_t& owner_id, const int64_t& file_id, std::string& limitCnt, std::string& curCnt)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getAllMetadata folder_id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/metadata/{ownerId}/{folderId}?zip={zip}
	std::ostringstream ostr;
	ostr << "/metadata/" << owner_id << "/" << file_id << "?zip=false";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	std::wstring path = configure_.userDataPath();
	if (!SD::Utility::FS::is_exist(path))
	{
		(void)SD::Utility::FS::create_directories(path);
	}
	path = path + PATH_DELIMITER + SYNCDATA_DIR + PATH_DELIMITER + SYNCDATA_TABLE;
	SmartFile file = _wfsopen(path.c_str(), L"wb", _SH_DENYRW);
	if (NULL == file)
	{
		return RT_FILE_OPEN_ERROR;
	}
	writeBuf.uDefParam = file;

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::downloadFileCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		std::map<std::string,std::string> mapResponseHeader = request_.getResponseHeaders();
		if(mapResponseHeader.find("x-user-node-limit") != mapResponseHeader.end())
		{
			limitCnt = mapResponseHeader.find("x-user-node-limit")->second;
		}
		if(mapResponseHeader.find("x-user-node-current") != mapResponseHeader.end())
		{
			curCnt = mapResponseHeader.find("x-user-node-current")->second;
		}

		SERVICE_ERROR(MODULE_NAME, ret, 
			"RestClient::getAllMetadata folder_id:%I64d, ret:%d", 
			file_id, ret);

		return ret;
	}

	return RT_OK;
}

int32_t RestClient::setFileVersion(const int64_t& file_id, const int64_t& owner_id)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::setFileVersion id:%I64d", file_id));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/v1/files/{owneId}/{fileId}/versions
	std::ostringstream ostr;
	ostr << "/files/" << owner_id << "/" << file_id << "/versions";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::setFileVersion id:%I64d, ret:%d", file_id, ret);
	}

	return ret;
}

int32_t RestClient::sendEmail(const EmailNode& emailNode)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::sendEmail "));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePOST);

	//POST /api/v2/mails
	std::ostringstream ostr;
	std::string strTmp = "/mails";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	(void)JsonGeneration::genEmailNode(emailNode, readBuf);

	RequestParam param;
	param.httpMethod = 	HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UAM, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::sendEmail");
	}

	return ret;
}

/*****************************************************************************************
	Function Name : getTeamSpaceListUser
	Description   : Get user's list of team spaces
	Input         : owner_id			resource owner ID
					pageparam			page parameter
	Output        : total_count			Returns the total number of files (including directory)
					TSNodeList			list of team spaces
	Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::getTeamSpaceListUser(const int64_t& ownerId, 
	const PageParam& pageparam,
	int64_t& total_count,
	UserTeamSpaceNodeInfoArray& TSNodeList)
{
	int ret = RT_OK;

	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getTeamSpaceListUser owner_id:%I64d", ownerId));

	//POST /api/v2/teamspaces/items
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePOST);
	std::string strTmp = "/teamspaces/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genTeamSpacesParam(ownerId, pageparam, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getTeamSpaceListUser id:%64d, ret:%d", ownerId, ret);
		return ret;
	}		

	return JsonParser::parseTeamSpacesUserInfoList(writeBuf, TSNodeList, total_count);
}

/*****************************************************************************************
Function Name : createTeamSpace
Description   : Create team space
Input         : name			name of team space
				desc			description of team space
				spaceQuota		max capacity of team space.unit: MB.-1indicates no limit.default value is -1.
				status			status of team space.0 indicates available.1 indicates disabled.default value is 0。
				maxVersions		number of max versions. default value is -1，indicates no limit.
Output        : _return			team space information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::createTeamSpace(const std::string& name,
	const std::string& desc, 
	const int64_t spaceQuota, 
	const int32_t status,
	const int32_t maxVersions,
	TeamSpacesNode& _return)
{
	int ret = RT_OK;

	//POST /api/v2/teamspaces
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePOST);
	std::string strTmp = "/teamspaces";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genTeamSpacesInfo(name, desc, spaceQuota, status, maxVersions, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);

	if(ret == FILE_CREATED)
	{
		return JsonParser::parseTeamSpaceNode(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::createTeamSpace ret:%d", ret);
	return ret;
}

/*****************************************************************************************
Function Name : updateTeamSpace
Description   : Update team space
Input         : teamId			team space ID
				name			name of team space
				desc			description of team space
				spaceQuota		max capacity of team space.unit: MB.-1indicates no limit.default value is -1.
				status			status of team space.0 indicates available.1 indicates disabled.default value is 0。
Output        : _return			team space information
Return        : RT_OK:succeed  Others:failed
*******************************************************************************************/
int32_t RestClient::updateTeamSpace(const int64_t& teamId,
	const std::string& name, 
	const std::string& desc,
	const int64_t spaceQuota,
	const int32_t status,
	TeamSpacesNode& _return)
{
	int ret = RT_OK;

	//PUT /api/v2/teamspaces/{teamId}
	//CSRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << "/teamspaces/" << teamId;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genTeamSpacesInfo(name, desc, spaceQuota, status, 1, readBuf, 1);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseTeamSpaceNode(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::updateTeamSpace TeamID:%64d ret:%d", teamId,ret);

	return ret;
}

/*****************************************************************************************
Function Name : getTeamSpace
Description   : get team space information
Input         : teamId			team space ID
Output        : _return			team space information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::getTeamSpace(const int64_t& teamId,TeamSpacesNode& _return)
{
	int ret = RT_OK;

	// 	GET /api/v2/teamspaces/{teamId}

	std::ostringstream ostr;
	ostr << "/teamspaces/"<< teamId;

	//CSRequest request(HttpRequestTypeGET);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseTeamSpaceNode(writeBuf, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getTeamSpace TeamID:%64d ret:%d", teamId,ret);

	return ret;
}

/*****************************************************************************************
Function Name : deleteTeamSpace
Description   : delete team space
Input         : teamId	team space ID
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::deleteTeamSpace(const int64_t& teamId)
{
	int ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//DELETE /api/v2/teamspaces/{teamId}
	//CSRequest request(HttpRequestTypeDELETE);

	std::ostringstream ostr;
	ostr << "/teamspaces/" << teamId;

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret == RT_OK)
	{
		return ret;
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::deleteTeamSpace TeamID:%64d ret:%d", teamId,ret);

	return ret;
}

/*****************************************************************************************
Function Name : addTeamSpaceMember
Description   : add team space member
Input         : teamId:			team spaceID
				member_type:	The type of the member. User: users. Group: group. The default value is user. 
								The current version supports only user.
				member_id:		Members's user ID or group ID.
				teamRole:		team space role. admin: owner, manager: , member: normal user, 
								currently only supports adding manager and member, the default value is member.
				role:			authority role name.
Output        : _return			team space member information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::addTeamSpaceMember(const int64_t& teamId,
	const std::string& memberType,
	const int64_t& memberId, 
	const std::string& teamRole, 
	const std::string& role,
	UserTeamSpaceNodeInfo& _return)
{
	int ret = RT_OK;

	//POST /api/v2/teamspaces/{teamId}/memberships
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/teamspaces/" << teamId << "/memberships";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genTeamSpacesMember(memberType, memberId, teamRole, role, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret == FILE_CREATED)
	{
		return JsonParser::parseTeamSpacesMemberInfo(writeBuf, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::addTeamSpaceMember TeamID:%64d ret:%d", teamId, ret);

	return ret;
}

/*****************************************************************************************
Function Name : getTeamSpaceMemberInfo
Description   : Obtain the team space member information
Input         : teamId		team space ID
				id			team space member relationship ID
Output		  : _return     team space member information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::getTeamSpaceMemberInfo(const int64_t& teamId, const std::string& id, UserTeamSpaceNodeInfo& _return)
{
	int ret = RT_OK;

	// GET/api/v2/teamspaces/{teamId}/memberships/{id}

	std::ostringstream ostr;
	ostr << "/teamspaces/"<< teamId << "/memberships/" << id;

	//CSRequest request(HttpRequestTypeGET);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret ==RT_OK)
	{
		return JsonParser::parseTeamSpacesMemberInfo(writeBuf, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getTeamSpaceMemberInfo TeamID:%64d ret:%d", teamId, ret);

	return ret;
}

/*****************************************************************************************
Function Name : updateTeamSpaceUserInfo
Description   : update team space member relationship information
Input         : teamId:			team space ID
				id:				team space member relationship ID
				teamRole：		team space role. admin: owner, manager: , member: normal user, the default value is member.
				role：			authority role name
Output        : _return			team space member information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::updateTeamSpaceUserInfo(const int64_t& teamId,
	const int64_t& id,
	const std::string& teamRole,
	const std::string& role,
	UserTeamSpaceNodeInfo& _return)
{
	int ret = RT_OK;

	//PUT /api/v2/teamspaces/{teamId}/memberships/{id}
	//CSRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << "/teamspaces/" << teamId << "/memberships/" << id;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genTeamSpacesMemberInfo(teamRole, role, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readDataCallback =  HttpCbFuns::putObjectDataCallback;
	param.readData = &readBuf;
	param.responseHeader = &rspHeader;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseTeamSpacesMemberInfo(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::updateTeamSpaceUserInfo TeamID:%64d ret:%d", teamId, ret);

	return ret;		
}

/*****************************************************************************************
Function Name : getTeamSpaceListMemberInfo
Description   : list team space member
Input         : teamId:			 team space ID
				order_field:	 Sort field. value range：teamRole: space character, createdAt: creation time,userName: user name
				order_direction: ascending or descending.value can be "ASC" or "DESC"
				teamRole:		 team space role.admin: owner, manager: manager, member: normal user, all all user, default value is all.
				keyword:		 Keyword search. The keyword name can match the user or group.
				limit:			 items can be gotten per page
				offset:			 paging
Output        : total:			 The total number of items
				_return			 team space member list
				Return:          RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::getTeamSpaceListMemberInfo(const int64_t& teamId, 
	const std::string& keyword, 
	const std::string& teamRole, 
	const PageParam& pageParam,
	int64_t& total,
	std::vector<UserTeamSpaceNodeInfo> & _return)
{
	int ret = RT_OK;
	
	//POST /api/v2/teamspaces/{teamId}/memberships/items
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/teamspaces/" << teamId << "/memberships/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genTeamSpacesListMember(keyword, teamRole, pageParam, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders; 

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret == RT_OK)
	{
		ret = JsonParser::parseTeamSpacesUserInfoList(writeBuf, _return, total);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getTeamSpaceListMemberInfo TeamID:%64d ret:%d", teamId, ret);

	return ret;
}

/*****************************************************************************************
Function Name : deleteTeamSpaceMember
Description   : Delete team space member
Input         : teamId:			team space ID
				id:				team space member relationship ID
				Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::deleteTeamSpaceMember(const int64_t& teamId, const int64_t& id)
{
	int ret = RT_OK;

	//DELETE /api/v2/teamspaces/{teamId}/memberships/{id}
	//CSRequest request(HttpRequestTypeDELETE);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/teamspaces/" << teamId << "/memberships/" << id;

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		return ret;
	}
	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::deleteTeamSpaceMember TeamID:%d ret:%d", teamId, ret);
	return ret;
}

int32_t RestClient::getCurUserInfo(StorageUserInfo& storageUserInfo)
{
	int ret = RT_OK;

	//GET /api/v2/users/me
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/users/me";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);
	if(ret == RT_OK)
	{
		ret = JsonParser::parseCurUserInfo(writeBuf, storageUserInfo);
	}
	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getCurUserInfo error.");
	return ret;
}

int32_t RestClient::listRegionIdInfo(RegionIdInfoArray& regionIdInfoArray)
{
	int ret = RT_OK;

	//DELETE /api/v2/regions
	//CSRequest request(HttpRequestTypeGET);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/regions";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret == RT_OK)
	{
		ret = JsonParser::parseRegionInfo(writeBuf, regionIdInfoArray);
	}
	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::listRegionIdInfo ret:%d", ret);
	return ret;
}

int32_t RestClient::listFileVersion(const int64_t ownerId, const int64_t fileId, const PageParam& pageparam, int64_t& nextOffset, FileVersionList& fileVersionNodes)
{
	int32_t ret = RT_OK;
	int64_t total_cnt = 0;

	//DELETE /api/v2/regions
	//CSRequest request(HttpRequestTypeGET);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/files/" << ownerId << "/" << fileId << "/versions?offset="
		<< pageparam.offset << "&limit=" << pageparam.limit;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret == RT_OK)
	{
		ret = JsonParser::parseFileVersionInfo(writeBuf, fileVersionNodes, total_cnt);
	}

	if (false != pageparam.is_used)
	{
		nextOffset = nextOffset + pageparam.limit;
		if(total_cnt <= nextOffset)
		{
			nextOffset = 0;
		}
	}
	else
	{
		nextOffset = 0;
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::listRegionIdInfo ret:%d", ret);
	return ret;
}

int32_t RestClient::getMsg(const int64_t startId, MsgList &msgNodes, MsgStatus status)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getMsg startId:%I64d", startId));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
		
	//POST /api/v2/message/items 
	std::ostringstream ostr;
	ostr << "/message/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	(void)JsonGeneration::genMsgStartId(startId, status, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		int64_t totalCnt;
		ret = JsonParser::parseMsgInfo(writeBuf, msgNodes, totalCnt);
	}

	return ret;
}

int32_t RestClient::getMsg(const int64_t offset, MsgList &msgNodes, int64_t& totalCnt, bool isSys, MsgStatus status)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getMsg offset:%I64d", offset));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
		
	//POST /api/v2/messages/items 
	std::ostringstream ostr;
	ostr << "/messages/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	(void)JsonGeneration::genMsgOffset(offset, status, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), isSys?SERVICE_UAM:SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseMsgInfo(writeBuf, msgNodes, totalCnt);
	}

	if (HTTP_NOT_FOUND == ret || HTTP_BAD_REQUEST == ret)
	{
		int resultErrorCode=0;
		if (!errorCodeDispatch(resultErrorCode, ret, writeBuf))
		{
			ret = resultErrorCode;
		}
	}

	return ret;
}

int32_t RestClient::getSysMsg(const int64_t offset, MsgList &msgNodes, int64_t& totalCnt)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getSysMsg offset:%I64d", offset));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
		
	//POST /api/v2/announcements/items 
	std::ostringstream ostr;
	ostr << "/announcements/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	(void)JsonGeneration::genSysMsgOffset(offset, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);
	if (RT_OK == ret)
	{
		ret = JsonParser::parseSysMsgInfo(writeBuf, msgNodes, totalCnt);
	}

	return ret;
}

int32_t RestClient::updateMsg(const int64_t msgId, bool isSys, MsgStatus status)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::updateMsg msgId:%I64d, status:%d", msgId, status));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT /api/v2/messages/messageId
	std::ostringstream ostr;
	ostr << "/messages/" << msgId;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	(void)JsonGeneration::genMsgStatus(status, readBuf);

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), isSys?SERVICE_UAM:SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::updateMsg id:%I64d", msgId);
	}

	return ret;
}

int32_t RestClient::deleteMsg(const int64_t msgId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::deleteMsg msgId:%I64d", msgId));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//DELETE /api/v2/messages/messageId
	std::ostringstream ostr;
	ostr << "/messages/" << msgId;

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::deleteMsg id:%I64d", msgId);
	}
	
	return ret;
}

int32_t RestClient::getMsgListener(std::string& url)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getMsgListener");
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/messages/listener
	std::ostringstream ostr;
	ostr << "/messages/listener";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parseListenerUrl(writeBuf, url);
	}

	return ret;
}

int32_t RestClient::downloadByUrl(const std::string& downloadUrl, const std::string location)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "CSClient::downloadByUrl");

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	int32_t ret = this->request(mapHeaders, downloadUrl.c_str(), SERVICE_CLOUDAPP, request_, param);
	if (RT_OK == ret)
	{
		try
		{
			std::fstream fs(location.c_str(), std::ios::out|std::ios::binary);
			fs.write((char*)writeBuf.pBuf, writeBuf.lOffset);
			fs.close();
		}
		catch(...)
		{
			SERVICE_ERROR(MODULE_NAME, RT_ERROR, "download by url failed, can not write file to location");
			return RT_FILE_WRITE_ERROR;
		}
	}
	else
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::downloadClient");
	}

	return ret;
}

/*****************************************************************************************
	Function Name : getGroupListUser
	Description   : Get user's list of group
	Input         : owner_id			resource owner ID
					pageparam			page parameter
	Output        : total_count			Returns the total number of files (including directory)
					TSNodeList			list of tgroup
	Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::getGroupListUser(const int64_t& owner_id, 
		const PageParam& pageparam,
		const std::string& keyword,
		const std::string& type,
		const std::string& listRole,
		int64_t& total_count,
		UserGroupNodeInfoArray& TSNodeList)
{
	int ret = RT_OK;

	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getGroupListUser owner_id:%I64d", owner_id));

	//POST /api/v2/groups/items
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePOST);
	std::string strTmp = "/groups/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genGroupParam( pageparam, readBuf,keyword,type,listRole);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePOST;
	param.readData				= &readBuf;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;
	param.responseHeader		= &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);
	if(ret != RT_OK)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getGroupListUser id:%64d, ret:%d", owner_id, ret);
		return ret;
	}		

	return JsonParser::parseGroupUserInfoList(writeBuf, TSNodeList, total_count);
}

/*****************************************************************************************
Function Name : createGroup
Description   : Create Group
Input         : name			name of Group
				desc			description of Group
				type
				status			status of Group.0 indicates available.1 indicates disabled.default value is 0。				
Output        : _return			Group information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::createGroup(const std::string& name,
		const std::string& description,
		const std::string& type,
		const std::string& status,
		GroupNode& _return)
{
	int ret = RT_OK;

	//POST /api/v2/groups 
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePOST);
	std::string strTmp = "/groups ";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genGroupInfo(name, description, status,type, readBuf);

	RequestParam param;
	param.httpMethod		= HttpRequestTypePOST;
	param.readData			= &readBuf;
	param.readDataCallback	= HttpCbFuns::putObjectDataCallback;
	param.writeData			= &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader	= &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, strTmp.c_str(), SERVICE_UFM, request_, param);

	if(ret == FILE_CREATED)
	{
		return JsonParser::parseGroupNode(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::createGroup ret:%d", ret);
	return ret;
}

/*****************************************************************************************
Function Name : updateGroup
Description   : Update Group
Input         : groupId			Group ID
				name			name of Groupe
				desc			description of Group
				type
				status			status of Group.0 indicates available.1 indicates disabled.default value is 0。
Output        : _return			Group information
Return        : RT_OK:succeed  Others:failed
*******************************************************************************************/
int32_t RestClient::updateGroup(const int64_t& groupId,
		const std::string& name, 
		const std::string& desc,
		const std::string& type,
		const std::string& status,
		GroupNode& _return)
{
	int ret = RT_OK;

	//PUT /api/v2//groups /{Id}
	//CSRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << "/groups/" << groupId;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genGroupInfo(name, desc, status,type,readBuf);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePUT;
	param.readData				= &readBuf;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;
	param.responseHeader		= &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseGroupNode(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::updateGroup GroupID:%64d ret:%d", groupId,ret);

	return ret;
}

/*****************************************************************************************
Function Name : deleteGroup
Description   : delete Group
Input         : groupId	Group ID
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::deleteGroup(const int64_t& groupId)
{
	int ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//DELETE /api/v2/groups/{groupId}
	//CSRequest request(HttpRequestTypeDELETE);

	std::ostringstream ostr;
	ostr << "/groups/" << groupId;

	RequestParam param;
	param.httpMethod		= HttpRequestTypeDELETE;
	param.responseHeader	= &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret == RT_OK)
	{
		return ret;
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::deleteGroup GroupID:%64d ret:%d", groupId,ret);

	return ret;
}

/*****************************************************************************************
Function Name : getGroup
Description   : get Group information
Input         : groupId			Group ID
Output        : _return			Group information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::getGroup(const int64_t& groupId,GroupNode& _return)
{
	int ret = RT_OK;

	// 	GET /api/v2/groups/{groupId}

	std::ostringstream ostr;
	ostr << "/groups/"<< groupId;

	//CSRequest request(HttpRequestTypeGET);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	RequestParam param;
	param.httpMethod		= HttpRequestTypeGET;
	param.writeData			= &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader	= &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseGroupNode(writeBuf, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getGroup GroupID:%64d ret:%d", groupId,ret);

	return ret;
}

/*****************************************************************************************
Function Name : addGroupMember
Description   : add Group member
Input         : groupId:		Group ID
				member_type:	The type of the member. User: users. Group: group. The default value is user. 
								The current version supports only user.
				member_id:		Members's user ID or group ID.
				groupRole:		group role. admin: owner, manager: , member: normal user, 
								currently only supports adding manager and member, the default value is member.
Output        : _return			team space member information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::addGroupMember(const int64_t& groupId,
		const std::string& member_type,
		const int64_t& member_id, 
		const std::string& groupRole, 
		UserGroupNodeInfo& _return)
{
	int ret = RT_OK;

	//POST /api/v2/groups/{groupId}/memberships
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypePOST);
	std::ostringstream ostr;
	ostr << "/groups/" << groupId << "/memberships";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genGroupMember(member_type, member_id, groupRole, readBuf);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePOST;
	param.readData				= &readBuf;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;
	param.responseHeader		= &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret == FILE_CREATED)
	{
		return JsonParser::parseGroupMemberInfo(writeBuf, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::addGroupMember GroupID:%64d ret:%d", groupId, ret);

	return ret;
}

/*****************************************************************************************
Function Name : deleteGroupMember
Description   : Delete group member
Input         : groupId:		group ID
				id:				group member relationship ID
				Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::deleteGroupMember(const int64_t& groupId, const int64_t& id)
{
	int ret = RT_OK;

	//DELETE /api/v2/groups/{groupId}/memberships/{id}
	//CSRequest request(HttpRequestTypeDELETE);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/groups/" << groupId << "/memberships/" << id;

	RequestParam param;
	param.httpMethod		= HttpRequestTypeDELETE;
	param.responseHeader	= &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		return ret;
	}
	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::deleteGroupMember GroupID:%d ret:%d", groupId, ret);
	return ret;
}

/*****************************************************************************************
Function Name : updateGroupUserInfo
Description   : update group member relationship information
Input         : groupId:		group ID
				id:				group member relationship ID
				groupRole：		group role. admin: owner, manager: , member: normal user, the default value is member.
Output        : _return			group member information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::updateGroupUserInfo(const int64_t& groupId,
		const int64_t& id,
		const std::string& groupRole,
		UserGroupNodeInfo& _return)
{
	int ret = RT_OK;

	//PUT /api/v2/groups/{groupId}/memberships/{id}
	//CSRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << "/groups/" << groupId << "/memberships/" << id;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genGroupMemberInfo(groupRole, readBuf);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePUT;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.readData				= &readBuf;
	param.responseHeader		= &rspHeader;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseGroupMemberInfo(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::updateGroupUserInfo GroupID:%64d ret:%d", groupId, ret);

	return ret;		
}

/*****************************************************************************************
Function Name : getTeamSpaceMemberInfo
Description   : Obtain the team space member information
Input         : teamId		team space ID
				id			team space member relationship ID
Output		  : _return     team space member information
Return        : RT_OK:succeed Others:failed
*******************************************************************************************/
int32_t RestClient::getGroupListMemberInfo(const int64_t& groupId, 
		const std::string& keyword, 
		const std::string& groupRole, 
		const PageParam& pageParam,
		int64_t& total,
		UserGroupNodeInfoArray& _return)
{
	int ret = RT_OK;

	// GET/api/v2/groups/{groupId}/memberships/items

	std::ostringstream ostr;
	ostr << "/groups/"<< groupId << "/memberships/items";

	//CSRequest request(HttpRequestTypeGET);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genGroupListMemberInfoParam( pageParam, readBuf, keyword, groupRole);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePOST;
	param.readData				= &readBuf;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;
	param.responseHeader		= &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret ==RT_OK)
	{
		return JsonParser::parseGroupUserInfoList(writeBuf, _return,total);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getGroupListMemberInfo GroupID:%64d ret:%d", groupId, ret);

	return ret;
}

int32_t RestClient::getSystemRoleList(PermissionRoleArray& _return)
{
	int ret = RT_OK;

	//POST /api/v2/roles  
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//CSRequest request(HttpRequestTypeGET);
	std::string strTmp = "/roles  ";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	RequestParam param;
	param.httpMethod		= HttpRequestTypeGET;
	param.writeData			= &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader	= &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, strTmp, SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseSystemRoleList(writeBuf, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getSystemRoleList",ret);

	return ret;
}

int32_t RestClient::deleteNodeAccesControl(const int64_t& ownerId,const int64_t& aclId)
{
	int ret = RT_OK;

	//DELETE/api/v2/acl/ownerId/aclId 
	//CSRequest request(HttpRequestTypeDELETE);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << "/ownerId/" << ownerId << "/aclId/" << aclId;

	RequestParam param;
	param.httpMethod		= HttpRequestTypeDELETE;
	param.responseHeader	= &rspHeader;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK == ret)
	{
		return ret;
	}
	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::deleteNodeAccesControl aclId:%d ret:%d", aclId, ret);
	return ret;
}

int32_t  RestClient::addNodeAccesControl(const int64_t& ownerId,
										 const int64_t& id,
										 const int64_t& nodeId,
										 const std::string& type,
										 const std::string& role,
										 AccesNode& _return)
{
	int ret = RT_OK;

	//POST /api/v2/acl 

	std::string strTmp = "/acl";

	//CSRequest request(HttpRequestTypePost);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genAddAccessNode( ownerId, id, nodeId, type,role,readBuf);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePOST;
	param.readData				= &readBuf;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;
	param.responseHeader		= &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, strTmp, SERVICE_UFM, request_, param);
	if(ret ==RT_OK)
	{
		return JsonParser::parseAccessNode(writeBuf, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::addNodeAccesControl ownerId:%64d ret:%d", ownerId, ret);

	return ret;
}

int32_t RestClient::updateNodeAccesControl(const int64_t& ownerId,const int64_t& aclId,const std::string& role,AccesNode& _return)
{
	int ret = RT_OK;

	//PUT /api/v2/acl/ownerId/aclId 
	//CSRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << "/ownerId/" << ownerId << "/aclId/" << aclId;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genUpdateAccessNode(role,readBuf);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePUT;
	param.readData				= &readBuf;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.responseHeader		= &rspHeader;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;

	std::map<std::string, std::string> mapHeaders;
		
	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if(ret == RT_OK)
	{
		return JsonParser::parseAccessNode(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::updateNodeAccesControl ownerId:%64d ret:%d", ownerId, ret);

	return ret;		
}

int32_t RestClient::listNodeAccesControl(const int64_t& ownerId,const int64_t& nodeId,const int32_t& offset,const int32_t& limit,int64_t& total,AccesNodeArray& _return)
{
	int ret = RT_OK;

	//POST /api/v2/acl/ownerId 

	std::ostringstream ostr;
	ostr << "/acl/ownerId/" << ownerId;

	//CSRequest request(HttpRequestTypePost);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	(void)JsonGeneration::genListAccessNode( nodeId, offset,limit,readBuf);

	RequestParam param;
	param.httpMethod			= HttpRequestTypePOST;
	param.readData				= &readBuf;
	param.readDataCallback		= HttpCbFuns::putObjectDataCallback;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;
	param.responseHeader		= &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret ==RT_OK)
	{
		return JsonParser::parseAccessNodeList(writeBuf,total, _return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::listNodeAccesControl ownerId:%64d ret:%d", ownerId, ret);

	return ret;
}

int32_t RestClient::getNodeAccesControl(const int64_t& ownerId,const int64_t& nodeId,const int64_t& userId,AccesNode& _return)
{
	int ret = RT_OK;

	//GET /api/v2/permissions/ownerId/nodeId/userId 

	std::ostringstream ostr;
	ostr << "/permissions/ownerId/" <<ownerId<<"/nodeId/"<<nodeId<<"/userId/"<<userId;

	//CSRequest request(HttpRequestTypeGet);
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);

	RequestParam param;
	param.httpMethod			= HttpRequestTypeGET;
	param.writeData				= &writeBuf;
	param.writeDataCallback		= HttpCbFuns::readBodyDataCallback;
	param.responseHeader		= &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(ret ==RT_OK)
	{
		return JsonParser::parseGetAccessNode(writeBuf,_return);
	}

	SERVICE_ERROR(MODULE_NAME, ret, "RestClient::getNodeAccesControl ownerId:%64d ret:%d", ownerId, ret);

	return ret;
}

int32_t RestClient::getThumbUrl(int64_t ownerId, int64_t fileId, int32_t height, int32_t width, std::string& thumbnailUrl)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "CSClient::downloadThumb");

	int32_t ret = RT_OK;
	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/files/ownerId/fileId/thumbUrl?height=height&width=width
	std::ostringstream ostr;
	ostr << "/files/" << ownerId << "/" << fileId
		<< "/thumbUrl?height=" << height << "&width=" << width;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if(RT_OK == ret)
	{
		std::string tmp_str;
		Json::Value jsonObj;
		Json::Reader reader; 
		tmp_str.assign((char *)writeBuf.pBuf);
		reader.parse(tmp_str, jsonObj);

		if(jsonObj["thumbnailUrl"].isString())				
		{											
			thumbnailUrl = jsonObj["thumbnailUrl"].asString();			
		}
	}

	return ret;
}

int32_t RestClient::totalUpload(const std::string& upload_url, 
								const int64_t len, 
					UploadCallback callback, 
					void* callbackData, 
					ProgressCallback progressCallback, 
					void* progressCallbackData)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::totalUpload upload_url:" + upload_url);
	int32_t ret = RT_OK;

	if (upload_url.empty() || NULL == callbackData)
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
	DataBuffer readBuf;
	readBuf.pBuf = (unsigned char *)callbackData;
	readBuf.lBufLen = len;

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readDataCallback = callback;
	param.readData = &readBuf;
	param.responseHeader = &rspHeader;
	param.progressCallback = progressCallback;
	param.progressData = progressCallbackData;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, upload_url, SERVICE_CLOUDAPP, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::totalUpload upload_url:%s, ret:%d", upload_url.c_str(), ret);
	}

	return ret;
}

int32_t RestClient::partUpload(const std::string& upload_url, 
				   const int32_t part_id, 
				   const int64_t len, 
				   UploadCallback callback, 
				   void* callbackData, 
				   ProgressCallback progressCallback, 
				   void* progressCallbackData)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string(
		"RestClient::partUpload upload_url:%s, part_id:%d, part size:%I64d", 
		upload_url.c_str(), part_id, len));
	int32_t ret = RT_OK;

	if (upload_url.empty() || NULL == callbackData)
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	std::ostringstream ostr;
	ostr << upload_url << "?partId=" << part_id;

	DataBuffer readBuf;
	readBuf.pBuf = (unsigned char *)callbackData;
	readBuf.lBufLen = len;

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readDataCallback = callback;
	param.readData = &readBuf;
	param.responseHeader = &rspHeader;
	param.progressCallback = progressCallback;
	param.progressData = progressCallbackData;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_CLOUDAPP, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::partUpload upload_url:%s, part_id:%d, ret:%d", 
			upload_url.c_str(), part_id, ret);
	}

	return ret;
}

int32_t RestClient::downloadFile(const std::string& download_url, 
					 int64_t offset, 
					 int64_t len, 
					 DownloadCallback callback, 
					 void* callbackData, 
					 ProgressCallback progressCallback, 
					 void* progressCallbackData)
{
	if (download_url.empty() || len == 0 || NULL == callbackData)
	{
		return RT_INVALID_PARAM;
	}

	std::ostringstream ostr_range;
	int64_t end;
	end = offset + len -1;
	ostr_range << "bytes=" << offset << "-" <<  end;
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::downloadFile, range:%s", ostr_range.str().c_str()));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	DataBuffer writeBuf;
	writeBuf.pBuf = (unsigned char *)callbackData;

	RequestParam param;
	param.httpMethod = HttpRequestTypeGET;
	param.writeData = &writeBuf;
	param.writeDataCallback = callback;
	param.responseHeader = &rspHeader;
	param.progressCallback = progressCallback;
	param.progressData = progressCallbackData;

	std::map<std::string, std::string> mapHeaders;

	mapHeaders[HEAD_RANGE] = ostr_range.str();

	ret = this->request(mapHeaders, download_url, SERVICE_CLOUDAPP, request_, param);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::downloadFile download_url:%s, ret:%d", 
			download_url.c_str(), ret);
		return ret;
	}

	return RT_OK;
}

/*****************************************************************************************
Function Name : errorCodeDispatch
Description   : 400、404错误码内部分类，返回内部分类的错误码
Input         : errorCode:-1400\-1404等错误码
                writeBuf
Output        : resultCode 返回新的内部错误码
Return        : 成功 RT_OK 失败 Others
Created By    : zhenglinsheng, 2016.01.20
Modification  :
Others        :
*******************************************************************************************/
int32_t RestClient::errorCodeDispatch(int32_t& resultCode, const int32_t errorCode, Malloc_Buffer& writeBuf)
{
	int32_t ret = RT_OK;
	int32_t resultErrorCode = RT_OK;
	std::string errorMessageCode = "";
	JsonParser::getErrorMessageFromJson(writeBuf, errorMessageCode);
	if (errorCode == HTTP_BAD_REQUEST)
	{
		for (std::map<std::string,int32_t>::iterator iter = errorCodeMap400.begin(); iter != errorCodeMap400.end(); ++iter)
		{
			if (errorMessageCode == iter->first)
			{
				resultCode = iter->second;
				return ret;
			}
		}
	}
	else if (errorCode == HTTP_NOT_FOUND)
	{
		for (std::map<std::string,int32_t>::iterator iter = errorCodeMap404.begin(); iter != errorCodeMap404.end(); ++iter)
		{
			if (errorMessageCode == iter->first)
			{
				resultCode = iter->second;
				return ret;
			}
		}
	}

	return RT_ERROR;
}

int32_t RestClient::getHttpPerconditionErrorCode(const std::string& jsonStr)
{
	if(std::string::npos!=jsonStr.find("ExceedMaxLinkNum"))
	{
		return EXCEED_MAX_LINK_NUM;
	}
	else if(std::string::npos!=jsonStr.find("FileScanning"))
	{
		return FILE_SCANNING;
	}
	else if(std::string::npos!=jsonStr.find("TooManyRequests"))
	{
		return TOO_MANY_REQUESTS;
	}
	else if(std::string::npos!=jsonStr.find("EmailChangeConflict"))
	{
		return EMAIL_CHANGE_CONFLICT;
	}
	else if(std::string::npos!=jsonStr.find("ExceedUserMaxNodeNum"))
	{
		return EXCEED_USER_MAX_NODE_NUM;
	}
	else if(std::string::npos!=jsonStr.find("ExceedMaxMembers"))
	{
		return EXCEED_MAX_MEMBERS;
	}
	else if(std::string::npos!=jsonStr.find("FileConverting"))
	{
		return FILE_CONVERTING;
	}
	else if(std::string::npos!=jsonStr.find("FileConvertNotSupport"))
	{
		return FILE_CONVERT_NOT_SUPPORT;
	}
	else if(std::string::npos!=jsonStr.find("FileConvertFailed"))
	{
		return FILE_CONVERT_FAILED;
	}
	else
	{
		return HTTP_PRECONDITION_FAILED;
	}
}

std::map<std::string, int32_t> initErrorCodes400()
{
	std::map<std::string,int32_t> errorCodeMap400;
	errorCodeMap400.insert(std::make_pair("InvalidParameter", RT_INVD_PARAMETER));
	errorCodeMap400.insert(std::make_pair("InvalidPart", RT_INVD_PART));
	errorCodeMap400.insert(std::make_pair("InvalidRange", RT_INVD_PANGE));
	errorCodeMap400.insert(std::make_pair("InvalidTeamRole", RT_INVD_TEAMROLE));
	errorCodeMap400.insert(std::make_pair("InvalidRegion", RT_INVD_REGION));
	errorCodeMap400.insert(std::make_pair("InvalidPermissionRole", RT_INVD_PMNROLE));
	errorCodeMap400.insert(std::make_pair("InvalidFileType", RT_INVD_FILETYPE));
	errorCodeMap400.insert(std::make_pair("UnmatchedDownloadUrl", RT_UMATH_URL));	
	return errorCodeMap400;
}

std::map<std::string, int32_t> initErrorCodes404()
{
	std::map<std::string,int32_t> errorCodeMap404;
	errorCodeMap404.insert(std::make_pair("NoSuchUser", RT_NOSUCHUSER));
	errorCodeMap404.insert(std::make_pair("NoSuchItem", RT_NOSUCHITEM));
	errorCodeMap404.insert(std::make_pair("NoSuchFolder", RT_NOSUCHFOLDER));
	errorCodeMap404.insert(std::make_pair("NoSuchFile", RT_NOSUCHFILE));
	errorCodeMap404.insert(std::make_pair("NoSuchVersion", RT_NOSUCHVERSION));
	errorCodeMap404.insert(std::make_pair("NoSuchToken", RT_NOSUCHTOKEN));
	errorCodeMap404.insert(std::make_pair("NoSuchLink", RT_NOSUCHLINK));
	errorCodeMap404.insert(std::make_pair("NoSuchShare", RT_NOSUCHSHARE));
	errorCodeMap404.insert(std::make_pair("NoSuchRegion", RT_NOSUCHREGION));
	errorCodeMap404.insert(std::make_pair("NoSuchParent", RT_NOSUCHPARENT));
	errorCodeMap404.insert(std::make_pair("NoSuchApplication", RT_NOSUCHAPP));
	errorCodeMap404.insert(std::make_pair("NoSuchRole", RT_NOSUCHROLE));
	errorCodeMap404.insert(std::make_pair("LinkNotEffective", RT_LINK_NOTEFF));
	errorCodeMap404.insert(std::make_pair("LinkExpired", RT_LINK_EXPIRED));
	errorCodeMap404.insert(std::make_pair("NoSuchSource", RT_NOSUCHSOURCE));
	errorCodeMap404.insert(std::make_pair("NoSuchDest", RT_NOSUCHDEST));
	errorCodeMap404.insert(std::make_pair("NoThumbnail", RT_NOSUCHTHUMBNAIL));
	errorCodeMap404.insert(std::make_pair("NoSuchOption", RT_NOSUCHOPTION));
	errorCodeMap404.insert(std::make_pair("NoSuchEnterprise", RT_NOSUCHENTERPRISE));
	errorCodeMap404.insert(std::make_pair("AbnormalTeamStatus", RT_ABNORMALTEAMSTATUS));
	errorCodeMap404.insert(std::make_pair("NoSuchGroup", RT_NOSUCHGROUP));
	errorCodeMap404.insert(std::make_pair("NoSuchMember", RT_NOSUCHMEMBER));
	errorCodeMap404.insert(std::make_pair("AbnormalGroupStatus", RT_ABNORMALGROUPSTATUS));
	errorCodeMap404.insert(std::make_pair("NoSuchTeamspace", RT_NOSUCHTEAMSPACE));
	errorCodeMap404.insert(std::make_pair("NoSuchACL", RT_NOSUCHACL));
	errorCodeMap404.insert(std::make_pair("ObjectNotFound", RT_NOHECTNOTFOUND));
	errorCodeMap404.insert(std::make_pair("NoSuchClient", RT_NOSUCHCLIENT));
	return errorCodeMap404;
}

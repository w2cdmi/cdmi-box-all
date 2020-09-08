#include "RestClient.h"
#include "HttpCbFuns.h"
#include "Utility.h"
#include "Util.h"
#include "SmartHandle.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("RestClient")
#endif

using namespace SD;

int32_t RestClient::removeFile(const int64_t& ownerId, 
							   const int64_t& fileId, 
							   const FILE_TYPE type)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::removeFile id:%I64d", fileId));
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
	ostr << ownerId << "/" << fileId;

	RequestParam param;
	param.httpMethod = HttpRequestTypeDELETE;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);
	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::removeFile id:%I64d, ret:%d", fileId, ret);
	}
	
	return ret;
}

int32_t RestClient::renameFile(const int64_t& ownerId, 
							 const int64_t& fileId, 
							 const std::string& newName, 
							 const FILE_TYPE type, 
							 FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::renameFile new_name:" + 
		Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(newName)));
	int32_t ret = RT_OK;

	if (newName.empty())
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
	ostr << ownerId << "/" << fileId;

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	ret = JsonGeneration::genNewName(newName, readBuf);
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

	if(fileId!=fileItem.id())
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::renameFile error, old id:%I64d, new id:%I64d, ret:%d", 
				fileId, fileItem.id(), ret);
		return RT_INVALID_PARAM;
	}

	return ret;
}

int32_t RestClient::moveFile(const int64_t& ownerId, 
							 const int64_t& fileId, 
							 const int64_t& destParentId, 
							 const bool autorename, 
							 const FILE_TYPE type, 
							 FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::moveFile id:%I64d", fileId));
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
	ostr << ownerId << "/" << fileId << "/move";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ret = JsonGeneration::genDestFolder(destParentId, ownerId, autorename, readBuf);
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
	if(fileId!=fileItem.id())
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::moveFile error, old id:%I64d, new id:%I64d, ret:%d", 
				fileId, fileItem.id(), ret);
		return RT_INVALID_PARAM;
	}

	return ret;
}

int RestClient::copyFile(const FileItem& srcfileItem, 
						 const ShareLinkNode& linkInfo, 
						 const bool autorename, 
						 FileItem& fileItem)
{
	return RT_NOT_IMPLEMENT;
}

int32_t RestClient::checkFileExist(const int64_t& ownerId, 
								   const int64_t& fileId, 
								   const FILE_TYPE type)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::checkFileExist id:%I64d", fileId));
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
		ostr << "/users/";
	}
	else if(FILE_TYPE_FILE == type)
	{
		ostr << "/files/";
	}
	else
	{
		return RT_INVALID_PARAM;
	}
	ostr << ownerId << "/" << fileId;

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

int32_t RestClient::createFolder(const int64_t& ownerId, 
							   const int64_t& parentId, 
							   const std::string& name, 
							   const int64_t& contentctime,
							   const int64_t& contentmtime,
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
	ostr << "/folders/" << ownerId << "/";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	ret = JsonGeneration::genNewFolder(name, parentId, contentctime, contentmtime, readBuf);
	if (RT_OK != ret)
	{
		return ret;
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

	if (FILE_CREATED != ret)
	{
		return ret;
	}

	ret = JsonParser::parseFolderObj(writeBuf, fileItem);

	return ret;
}

int32_t RestClient::getFileInfo(const int64_t& ownerId, 
							  const int64_t& fileId, 
							  const FILE_TYPE type,
							  FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getFileInfo id:%I64d", fileId));
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
	ostr << ownerId << "/" << fileId;

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

int32_t RestClient::getFileInfoByParentAndName(const int64_t& ownerId, 
											   const int64_t& parentId, 
											   const std::string& name, 
											   std::list<FileItem*>& fileItems)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string(
		"RestClient::getFileInfoByParentAndName parent:%I64d, name:%s", parentId, 
		Utility::String::wstring_to_string(Utility::String::utf8_to_wstring(name)).c_str()));

	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//POST /api/v2/folders/{ownerId}/{folderId}/children
	std::ostringstream ostr;
	ostr << "/folders/" << ownerId << "/" << parentId << "/children";

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

int32_t RestClient::listFolder(const int64_t& ownerId,
							 const int64_t& folderId,
							 const PageParam& pageParam,
							 int64_t& nextOffset,
							 std::list<FileItem*>& fileItems)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::listFolder folder_id:%I64d", folderId));
	int32_t ret = RT_OK;
	int64_t total_count = 0;

	//CSRequest request(HttpRequestTypeGET);

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;
		
	//POST /folders/{ownerId}/{folderId}/items 
	std::ostringstream ostr;
	ostr << "/folders/"<< ownerId << "/"
		 << folderId << "/items";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	if(false != pageParam.is_used)
	{
		ret = JsonGeneration::genListFolder(pageParam, readBuf);
	}
	
	if (ret != RT_OK)
	{
		return ret;
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

int32_t RestClient::preUpload(const FileItem& fileItem,
							const UploadType uploadType, 
							FileItem& existFileItem,
							UploadInfo& uploadInfo, 
							const std::string& encryptKey)
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
	ret = JsonGeneration::genUploadReq(fileItem, uploadType, encryptKey, readBuf);
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
		if(RT_OK != JsonParser::parseExistFileObj(writeBuf, existFileItem))
		{
			return FAILED_TO_PARSEJSON;
		}
	}
	else if (RT_OK == ret)
	{
		ret = JsonParser::parseUploadInfo(writeBuf, uploadInfo);
		SERVICE_DEBUG(MODULE_NAME, ret, "id is %I64d, upload url is %s.", 
			uploadInfo.file_id, uploadInfo.upload_url.c_str());
	}

	return ret;
}

int32_t RestClient::refreshUploadURL(const int64_t& ownerId, 
									 const int64_t& fileId, 
									 const std::string uploadUrl, 
									 std::string& outUploadUrl)
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
	ostr << "/files/" << ownerId <<"/" << fileId << "/refreshurl";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.writeData = &writeBuf;
	param.writeDataCallback = HttpCbFuns::readBodyDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	// build body
	{
		Json::StyledWriter writer;
		Json::Value reqRoot(Json::objectValue);
		reqRoot["uploadUrl"] = uploadUrl;
		std::string renameJsonStr = writer.write(reqRoot);
		string2Buff(renameJsonStr, readBuf);
		if (NULL == readBuf.pBuf)
		{
			return RT_MEMORY_MALLOC_ERROR;
		}
	}

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	if (RT_OK != ret)
	{
		return ret;
	}
	//parse body
	{
		std::string jsonStr;
		Json::Reader reader;
		Json::Value jsonValue = Json::nullValue;

		if ((NULL != writeBuf.pBuf) && (0 < writeBuf.lOffset))
		{
			jsonStr.assign((char *)writeBuf.pBuf);
			reader.parse(jsonStr, jsonValue);
			if (jsonValue["uploadUrl"].isString())
			{
				outUploadUrl = jsonValue["uploadUrl"].asString();
			}
		}
	}

	return ret;
}

int32_t RestClient::totalUpload(const std::string& uploadUrl, 
								const unsigned char* ucBuffer, 
								uint32_t ulBufSize)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::totalUpload upload_url:" + uploadUrl);
	int32_t ret = RT_OK;
	
	if (uploadUrl.empty() || !ucBuffer)
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

	ret = this->request(mapHeaders, uploadUrl, SERVICE_CLOUDAPP, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::totalUpload upload_url:%s, ret:%d", uploadUrl.c_str(), ret);
	}

	return ret;
}

int32_t RestClient::partUpload(const std::string& uploadUrl, 
							 const int32_t partId,
							 const unsigned char* ucBuffer, 
							 uint32_t ulBufSize)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::partUpload upload_url:" + uploadUrl + ", part_id:" + Utility::String::format_string("%d", partId));
	int32_t ret = RT_OK;

	if (uploadUrl.empty() || !ucBuffer)
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT /{upload_url}?upload_id={upload_id}&partID={part_id}
	//HttpRequest request(HttpRequestTypePUT);
	std::ostringstream ostr;
	ostr << uploadUrl << "?partId=" << partId;

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
			uploadUrl.c_str(), partId, ret);
	}

	return ret;
}

int32_t RestClient::partUploadComplete(const std::string& uploadUrl, 
									 const PartList& partList,
									 FileItem& fileItem)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::partUploadComplete upload_url:" + uploadUrl);
	int32_t ret = RT_OK;

	if (uploadUrl.empty())
	{
		return RT_INVALID_PARAM;
	}

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/files/{owneId}/{fileId}/parts
	std::ostringstream ostr;
	ostr << uploadUrl << "?commit";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	ret = JsonGeneration::genPartList(partList, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

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
			uploadUrl.c_str(), ret);
	}

	return ret;
}

int32_t RestClient::partUploadCancel(const std::string& uploadUrl)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::partUploadCancel"));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	
	//DELETE /{upload_url}
	//HttpRequest request(HttpRequestTypeDELETE);

	std::ostringstream ostr;
	ostr << uploadUrl;

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

int32_t RestClient::getDownloadUrl(const int64_t& ownerId, 
								   const int64_t& fileId, 
								   std::string& downloadUrl)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getDownloadUrl id:%I64d", fileId));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/files/{owneId}/{fileId}/url
	std::ostringstream ostr;
	ostr << "/files/" << ownerId <<"/" << fileId << "/url";

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
		SERVICE_ERROR(MODULE_NAME, ret, "failed to get download url of %I64d.", fileId);
		return ret;
	}

	return JsonParser::parseDownloadInfo(writeBuf, downloadUrl);
}

int32_t RestClient::downloadFile(const std::string& downloadUrl, 
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
	ostr << downloadUrl;

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
		memcpy(fileBuffer, writeBuf.pBuf, size_t(lBufSize));
		ret = RT_OK;
    }
	
	return ret;
}

int32_t RestClient::getUploadPart(const std::string& uploadUrl, 
								  PartInfoList& partInfoList)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getUploadPart upload_url:" + uploadUrl);
	int32_t ret = RT_OK;

	if (uploadUrl.empty())
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

	ret = this->request(mapHeaders, uploadUrl, SERVICE_UFM, request_, param);

	if (RT_OK == ret)
	{
		ret = JsonParser::parsePartInfo(writeBuf, partInfoList);
	}

	return ret;
}

int32_t RestClient::createVersion(const int64_t& ownerId, 
								  const int64_t& fileId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::createVersion id:%I64d", fileId));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT api/v1/files/{owneId}/{fileId}/versions
	//HttpRequest request(HttpRequestTypePUT);

	std::ostringstream ostr;
	ostr << "/files/" << ownerId << "/" << fileId << "/versions";

	RequestParam param;
	param.httpMethod = HttpRequestTypePUT;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UFM, request_, param);

	return ret;
}

int32_t RestClient::setFolderSync(const int64_t& ownerId, 
								  const int64_t& fileId, 
								  const bool isSync)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::setFolderSync id:%I64d %s", fileId, isSync?"true":"false"));

	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//PUT api/v2/folders/{owneId}/{folderId}

	std::ostringstream ostr;
	ostr << "/folders/" << ownerId << "/" << fileId;

	DataBuffer readBuf;
	Json::StyledWriter writer;
	Json::Value reqRoot(Json::objectValue);
	reqRoot["syncStatus"] = isSync;
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

int32_t RestClient::getSyncMetadata(const int64_t& ownerId, 
									int64_t syncVersion, 
									std::string& limitCnt, 
									std::string& curCnt)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, "RestClient::getSyncMetadata syncVersion:" + Utility::String::type_to_string<std::string>(syncVersion));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/metadata/{ownerId}?syncVersion={syncVersion}&zip={zip}
	std::ostringstream ostr;
	ostr << "/metadata/" << ownerId << "?syncVersion=" << syncVersion << "&zip=false";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	std::wstring path = configure_.userDataPath();
	if (!SD::Utility::FS::is_exist(path))
	{
		(void)SD::Utility::FS::create_directories(path);
	}
	path += L"/syncdata.db";
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

int32_t RestClient::getAllMetadata(const int64_t& ownerId, 
								   const int64_t& fileId, 
								   std::string& limitCnt, 
								   std::string& curCnt)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::getAllMetadata folder_id:%I64d", fileId));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//GET /api/v2/metadata/{ownerId}/{folderId}?zip={zip}
	std::ostringstream ostr;
	ostr << "/metadata/" << ownerId << "/" << fileId << "?zip=false";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	std::wstring path = configure_.userDataPath();
	if (!SD::Utility::FS::is_exist(path))
	{
		(void)SD::Utility::FS::create_directories(path);
	}
	path += L"/syncdata.db";
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
			fileId, ret);

		return ret;
	}

	return RT_OK;
}

int32_t RestClient::setFileVersion(const int64_t& fileId, 
								   const int64_t& ownerId)
{
	SERVICE_FUNC_TRACE(MODULE_NAME, Utility::String::format_string("RestClient::setFileVersion id:%I64d", fileId));
	int32_t ret = RT_OK;

	HeaderBuffer rspHeader;
	rspHeader.strHeader = "";
	rspHeader.done = false;

	//HttpRequest request(HttpRequestTypePUT);

	//PUT /api/v1/files/{owneId}/{fileId}/versions
	std::ostringstream ostr;
	ostr << "/files/" << ownerId << "/" << fileId << "/versions";

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
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::setFileVersion id:%I64d, ret:%d", fileId, ret);
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

	//POST /api/v2/mail
	std::ostringstream ostr;
	ostr << "/mail";

	Malloc_Buffer writeBuf(BODY_BUF_SIZE);
	DataBuffer readBuf;
	ret = JsonGeneration::genEmailNode(emailNode, readBuf);
	if (RT_OK != ret)
	{
		return ret;
	}

	RequestParam param;
	param.httpMethod = HttpRequestTypePOST;
	param.readData = &readBuf;
	param.readDataCallback = HttpCbFuns::putObjectDataCallback;
	param.responseHeader = &rspHeader;

	std::map<std::string, std::string> mapHeaders;

	ret = this->request(mapHeaders, ostr.str(), SERVICE_UAM, request_, param);

	if (RT_OK != ret)
	{
		SERVICE_ERROR(MODULE_NAME, ret, "RestClient::sendEmail");
	}

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
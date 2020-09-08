/******************************************************************************
Description  : Rest请求和响应对象定义
Created By   : t90006461
*******************************************************************************/
#ifndef _RESTPARAM_H_
#define _RESTPARAM_H_

#include "CommonValue.h"
#include <vector>
#include "FileItem.h"

struct UploadUrl
{
	std::string name;
	int64_t fileId;
	std::string uploadUrl;

	UploadUrl()
		:name("")
		,fileId(0L)
		,uploadUrl(""){}
};

struct ErrorInfo
{
	std::string type;
	std::string code;
	std::string message;
	std::string requestId;

	ErrorInfo()
		:type("")
		,code("")
		,message("")
		,requestId(""){}

};

struct FailedInfo
{
	std::string name;
	ErrorInfo errorInfo;
	std::wstring source;
	int64_t size;
	int32_t errorCode;

	FailedInfo()
		:name("")
		,source(L"")
		,size(0)
		,errorCode(0){}
};

struct FilePreUploadInfo
{
	std::string name;
	int64_t size;
	std::string md5;
	std::string blockMD5;
	int64_t contentCreatedAt;
	int64_t contentModifiedAt;
	std::string encryptKey;

	FilePreUploadInfo()
		:name("")
		,size(0L)
		,md5("")
		,blockMD5("")
		,contentCreatedAt(0L)
		,contentModifiedAt(0L)
		,encryptKey(""){}
};

typedef std::list<UploadUrl> UploadUrlList;
typedef std::list<FailedInfo> FailedList;
typedef std::list<FilePreUploadInfo> FilePreUploadInfoList;

struct BatchPreUploadRequest
{
	int64_t tokenTimeout;
	int64_t parent;
	FilePreUploadInfoList fileList;

	BatchPreUploadRequest()
		:tokenTimeout(0L)
		,parent(0L){}
};

struct BatchPreUploadResponse
{
	UploadUrlList uploadUrlList;
	FileList uploadedList;
	FailedList failedList;
};

#endif
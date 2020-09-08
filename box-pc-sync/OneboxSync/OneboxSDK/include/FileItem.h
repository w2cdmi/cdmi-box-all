#ifndef __ONEBOX__FILEITEM__H__
#define __ONEBOX__FILEITEM__H__

#include "CommonValue.h"

class FileItem
{
public:
    FileItem()
		:id_(0L)
		,type_(FILE_TYPE_DIR)
		,name_("")
		,description_("")
		,parent_(0L)
		,ownerId_(0L)
		,size_(0L)
		,createTime_(0L)
		,modifieTime_(0L)
		,version_("")
		,isShare_(false)
		,isSync_(false)
		,isEncrypt_(false)
		,isSharelink_(false)
		,status_(0)
		,contentCreatedAt_(INVALID_TIME)
		,contentModifiedAt_(INVALID_TIME)
		,createdBy_(0L)
		,modifiedBy_(0L)
		,uploadID_("")
    {
    };

    virtual ~FileItem()
    {
    };

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(FILE_TYPE, type)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(std::string, description)
	FUNC_DEFAULT_SET_GET(int64_t, parent)
	FUNC_DEFAULT_SET_GET(int64_t, ownerId)
	FUNC_DEFAULT_SET_GET(int64_t, size)
	FUNC_DEFAULT_SET_GET(int64_t, createTime)
	FUNC_DEFAULT_SET_GET(int64_t, modifieTime)
	FUNC_DEFAULT_SET_GET(std::string, version)
	FUNC_DEFAULT_SET_GET(bool, isShare)
	FUNC_DEFAULT_SET_GET(bool, isSync)
	FUNC_DEFAULT_SET_GET(bool, isEncrypt)
	FUNC_DEFAULT_SET_GET(bool, isSharelink)
	FUNC_DEFAULT_SET_GET(int32_t, status)
	FUNC_DEFAULT_SET_GET(FileSignature, signature)
	FUNC_DEFAULT_SET_GET(int64_t, contentCreatedAt)
	FUNC_DEFAULT_SET_GET(int64_t, contentModifiedAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedBy)
	FUNC_DEFAULT_SET_GET(std::string, uploadID)

private:
	int64_t id_;				//文件/目录Id，通过元数据查询路径
	FILE_TYPE type_;			//类型：文件/目录
	std::string name_;				//文件、目录名称
	std::string description_;		//文件、目录描述
	int64_t parent_;				//父文件夹Id
	int64_t ownerId_;			//拥有者User ID
	int64_t size_;	//文件大小
	int64_t createTime_;			//创建时间
	int64_t modifieTime_;		//修改时间
	std::string version_;			//文件版本
	bool isShare_;	//文件是否被共享
	bool isSync_;		//文件是否同步
	bool isEncrypt_;			//文件是否加密
	bool isSharelink_;			//文件是否有外链
	int32_t status_;				//文件状态
	FileSignature signature_;				//文件SHA1(MD5)
	int64_t contentCreatedAt_;	//客户端文件创建时间
	int64_t contentModifiedAt_;	//客户端文件修改时间
	int64_t createdBy_;		//文件创建者
	int64_t modifiedBy_;	//文件修改者
	std::string uploadID_;			//分片上传时的ID
};

#endif // end of defined __ONEBOX__FILEITEM__H__

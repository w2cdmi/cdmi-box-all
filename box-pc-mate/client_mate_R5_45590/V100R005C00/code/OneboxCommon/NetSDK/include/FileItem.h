/******************************************************************************
Description  : 文件/文件夹属性
Created By   : z00178165
*******************************************************************************/
#ifndef __ONEBOX__FILEITEM__H__
#define __ONEBOX__FILEITEM__H__

#include "CommonValue.h"
#include <vector>

enum File_Permissions
{
	FP_INVALID = 0x00000000,
	FP_BROWSE = 0x00000001,
	FP_PREVIEW = 0x00000002,
	FP_DOWNLOAD = 0x00000004,
	FP_UPLOAD = 0x00000008,
	FP_EDIT = 0x00000010,
	FP_DELETE = 0x00000020,
	FP_PUBLISHLINK = 0x00000040,
	FP_AUTHORIZE = 0x00000080
};

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
		,objectId_("")
		,version_(0)
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
		,extraType_("")
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
	FUNC_DEFAULT_SET_GET(std::string, objectId)
	FUNC_DEFAULT_SET_GET(int32_t, version)
	FUNC_DEFAULT_SET_GET(bool, isShare)
	FUNC_DEFAULT_SET_GET(bool, isSync)
	FUNC_DEFAULT_SET_GET(bool, isEncrypt)
	FUNC_DEFAULT_SET_GET(bool, isSharelink)
	FUNC_DEFAULT_SET_GET(int32_t, status)
	FUNC_DEFAULT_SET_GET(Fingerprint, fingerprint)
	FUNC_DEFAULT_SET_GET(int64_t, contentCreatedAt)
	FUNC_DEFAULT_SET_GET(int64_t, contentModifiedAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedBy)
	FUNC_DEFAULT_SET_GET(std::string, uploadID)
	FUNC_DEFAULT_SET_GET(std::string, extraType)

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
	std::string objectId_;
	int32_t version_;			//文件版本
	bool isShare_;	//文件是否被共享
	bool isSync_;		//文件是否同步
	bool isEncrypt_;			//文件是否加密
	bool isSharelink_;			//文件是否有外链
	int32_t status_;				//文件状态
	Fingerprint fingerprint_;				//??SHA1(MD5)
	int64_t contentCreatedAt_;	//客户端文件创建时间
	int64_t contentModifiedAt_;	//客户端文件修改时间
	int64_t createdBy_;		//文件创建者
	int64_t modifiedBy_;	//文件修改者
	std::string uploadID_;			//分片上传时的ID
	std::string extraType_;			//
};

class FileVersionItem
{
public:
    FileVersionItem()
		:id_(0L)
		,type_(FILE_TYPE_DIR)
		,name_("")
		,size_(0L)
		,status_(0)
		,objectId_("")
		,createTime_(0L)
		,modifieTime_(0L)
		,ownedBy_(0L)
		,createdBy_(0L)
		,modifiedBy_(0L)
		,contentCreatedAt_(INVALID_TIME)
		,contentModifiedAt_(INVALID_TIME)
		,parent_(0L)
		,isEncrypt_(false)

    {
    };

    virtual ~FileVersionItem()
    {
    };

	FUNC_DEFAULT_SET_GET(int64_t, id)
	FUNC_DEFAULT_SET_GET(FILE_TYPE, type)
	FUNC_DEFAULT_SET_GET(std::string, name)
	FUNC_DEFAULT_SET_GET(int64_t, parent)
	FUNC_DEFAULT_SET_GET(int64_t, ownedBy)
	FUNC_DEFAULT_SET_GET(int64_t, size)
	FUNC_DEFAULT_SET_GET(int64_t, createTime)
	FUNC_DEFAULT_SET_GET(int64_t, modifieTime)
	FUNC_DEFAULT_SET_GET(std::string, objectId)
	FUNC_DEFAULT_SET_GET(bool, isEncrypt)
	FUNC_DEFAULT_SET_GET(int32_t, status)
	FUNC_DEFAULT_SET_GET(Fingerprint, fingerprint)
	FUNC_DEFAULT_SET_GET(int64_t, contentCreatedAt)
	FUNC_DEFAULT_SET_GET(int64_t, contentModifiedAt)
	FUNC_DEFAULT_SET_GET(int64_t, createdBy)
	FUNC_DEFAULT_SET_GET(int64_t, modifiedBy)

	FileVersionItem& operator=(const FileVersionItem &rhs)
	{
		if (&rhs != this)
		{
			id_ = rhs.id();
			type_ = rhs.type();
			name_ = rhs.name();
			parent_ = rhs.parent();
			ownedBy_ = rhs.ownedBy();
			size_ = rhs.size();
			createTime_ = rhs.createTime();
			modifieTime_ = rhs.modifieTime();
			objectId_ = rhs.objectId();
			isEncrypt_ = rhs.isEncrypt();
			status_ = rhs.status();
			fingerprint_ = rhs.fingerprint();
			contentCreatedAt_ = rhs.contentCreatedAt();
			contentModifiedAt_ = rhs.contentModifiedAt();
			createdBy_ = rhs.createdBy();
			modifiedBy_ = rhs.createdBy();
		}
		return *this;
	}

private:
	int64_t id_;				//文件/目录Id，通过元数据查询路径
	FILE_TYPE type_;			//类型：文件/目录
	std::string name_;				//文件、目录名称
	int64_t parent_;				//该文件对应的最新文件ID
	int64_t ownedBy_;			//拥有者User ID
	int64_t size_;			//文件大小
	int64_t createTime_;			//创建时间
	int64_t modifieTime_;		//修改时间
	std::string objectId_;			//文件版本
	bool isEncrypt_;			//文件是否加密
	int32_t status_;				//文件状态
	Fingerprint fingerprint_;				//??SHA1(MD5)
	int64_t contentCreatedAt_;	//客户端文件创建时间
	int64_t contentModifiedAt_;	//客户端文件修改时间
	int64_t createdBy_;		//文件创建者
	int64_t modifiedBy_;	//文件修改者
};

typedef std::vector<FileItem> FileList;
typedef std::vector<FileVersionItem> FileVersionList;

#endif // end of defined __ONEBOX__FILEITEM__H__

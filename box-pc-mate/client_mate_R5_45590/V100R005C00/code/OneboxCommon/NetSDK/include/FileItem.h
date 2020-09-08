/******************************************************************************
Description  : �ļ�/�ļ�������
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
	int64_t id_;				//�ļ�/Ŀ¼Id��ͨ��Ԫ���ݲ�ѯ·��
	FILE_TYPE type_;			//���ͣ��ļ�/Ŀ¼
	std::string name_;				//�ļ���Ŀ¼����
	std::string description_;		//�ļ���Ŀ¼����
	int64_t parent_;				//���ļ���Id
	int64_t ownerId_;			//ӵ����User ID
	int64_t size_;	//�ļ���С
	int64_t createTime_;			//����ʱ��
	int64_t modifieTime_;		//�޸�ʱ��
	std::string objectId_;
	int32_t version_;			//�ļ��汾
	bool isShare_;	//�ļ��Ƿ񱻹���
	bool isSync_;		//�ļ��Ƿ�ͬ��
	bool isEncrypt_;			//�ļ��Ƿ����
	bool isSharelink_;			//�ļ��Ƿ�������
	int32_t status_;				//�ļ�״̬
	Fingerprint fingerprint_;				//??SHA1(MD5)
	int64_t contentCreatedAt_;	//�ͻ����ļ�����ʱ��
	int64_t contentModifiedAt_;	//�ͻ����ļ��޸�ʱ��
	int64_t createdBy_;		//�ļ�������
	int64_t modifiedBy_;	//�ļ��޸���
	std::string uploadID_;			//��Ƭ�ϴ�ʱ��ID
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
	int64_t id_;				//�ļ�/Ŀ¼Id��ͨ��Ԫ���ݲ�ѯ·��
	FILE_TYPE type_;			//���ͣ��ļ�/Ŀ¼
	std::string name_;				//�ļ���Ŀ¼����
	int64_t parent_;				//���ļ���Ӧ�������ļ�ID
	int64_t ownedBy_;			//ӵ����User ID
	int64_t size_;			//�ļ���С
	int64_t createTime_;			//����ʱ��
	int64_t modifieTime_;		//�޸�ʱ��
	std::string objectId_;			//�ļ��汾
	bool isEncrypt_;			//�ļ��Ƿ����
	int32_t status_;				//�ļ�״̬
	Fingerprint fingerprint_;				//??SHA1(MD5)
	int64_t contentCreatedAt_;	//�ͻ����ļ�����ʱ��
	int64_t contentModifiedAt_;	//�ͻ����ļ��޸�ʱ��
	int64_t createdBy_;		//�ļ�������
	int64_t modifiedBy_;	//�ļ��޸���
};

typedef std::vector<FileItem> FileList;
typedef std::vector<FileVersionItem> FileVersionList;

#endif // end of defined __ONEBOX__FILEITEM__H__

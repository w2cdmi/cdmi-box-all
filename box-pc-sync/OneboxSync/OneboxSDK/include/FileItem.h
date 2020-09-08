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
	int64_t id_;				//�ļ�/Ŀ¼Id��ͨ��Ԫ���ݲ�ѯ·��
	FILE_TYPE type_;			//���ͣ��ļ�/Ŀ¼
	std::string name_;				//�ļ���Ŀ¼����
	std::string description_;		//�ļ���Ŀ¼����
	int64_t parent_;				//���ļ���Id
	int64_t ownerId_;			//ӵ����User ID
	int64_t size_;	//�ļ���С
	int64_t createTime_;			//����ʱ��
	int64_t modifieTime_;		//�޸�ʱ��
	std::string version_;			//�ļ��汾
	bool isShare_;	//�ļ��Ƿ񱻹���
	bool isSync_;		//�ļ��Ƿ�ͬ��
	bool isEncrypt_;			//�ļ��Ƿ����
	bool isSharelink_;			//�ļ��Ƿ�������
	int32_t status_;				//�ļ�״̬
	FileSignature signature_;				//�ļ�SHA1(MD5)
	int64_t contentCreatedAt_;	//�ͻ����ļ�����ʱ��
	int64_t contentModifiedAt_;	//�ͻ����ļ��޸�ʱ��
	int64_t createdBy_;		//�ļ�������
	int64_t modifiedBy_;	//�ļ��޸���
	std::string uploadID_;			//��Ƭ�ϴ�ʱ��ID
};

#endif // end of defined __ONEBOX__FILEITEM__H__

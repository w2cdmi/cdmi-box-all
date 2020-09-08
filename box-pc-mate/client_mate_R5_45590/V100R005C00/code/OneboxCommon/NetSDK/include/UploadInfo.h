/******************************************************************************
Description  : 上传信息
*******************************************************************************/
#ifndef _UPLOADINFO_H_
#define _UPLOADINFO_H_

#include "CommonValue.h"
#include <vector>
#include <list>

enum UploadType
{
	Upload_Sigle,
	Upload_MultiPart
};

struct UploadInfo
{
	int64_t file_id;
	std::string upload_url;
	UploadInfo():file_id(0L), upload_url("")
	{
	}
};

struct PartInfo
{
	int32_t  partId;
	int64_t size;
};

typedef std::vector<PartInfo> PartInfoList;
typedef std::list<int32_t> PartList;

#endif

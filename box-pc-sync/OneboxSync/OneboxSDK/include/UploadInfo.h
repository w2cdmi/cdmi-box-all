#ifndef __ONEBOX__UPLOADINFO_H_
#define __ONEBOX__UPLOADINFO_H_

#include "CommonValue.h"
#include <vector>

enum UploadType
{
	Upload_Sigle,		//total upload
	Upload_MultiPart	//multi-part upload
};

typedef std::vector<int32_t> PartList;

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

#endif

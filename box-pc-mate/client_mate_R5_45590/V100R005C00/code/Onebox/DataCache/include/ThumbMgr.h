#ifndef _ONEBOX_THUMB_MGR_H_
#define _ONEBOX_THUMB_MGR_H_

#include "UserContext.h"

#define SQLITE_CACHE_THUMB (L"thumbInfo.db")
#define TABLE_THUMB ("tb_thumb")
#define THUMB_ROW_KEY ("key")
#define THUMB_ROW_FILENAME ("fileName")
#define THUMB_ROW_DEST ("dest")

class ONEBOX_DLL_EXPORT ThumbMgr
{
public:
	virtual ~ThumbMgr(){}

	static ThumbMgr* getInstance();

	virtual int32_t addThumb(int64_t ownerId, int64_t fileId, int32_t type) = 0;

	//type 0:Ð¡Í¼¡¢1:´óÍ¼
	virtual std::wstring getThumbPath(int64_t ownerId, int64_t fileId, int32_t type) = 0;

	virtual int32_t deleteThumb(int64_t ownerId, int64_t fileId) = 0;

	virtual std::string getShowPath(const std::string key) = 0;

private:
	static std::auto_ptr<ThumbMgr> instance_;
};

#endif
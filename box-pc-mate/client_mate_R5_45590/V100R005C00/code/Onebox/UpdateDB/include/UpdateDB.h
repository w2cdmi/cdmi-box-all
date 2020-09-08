#ifndef _ONEBOX_UPDATEMGR_H_
#define _ONEBOX_UPDATEMGR_H_
#include "OneboxExport.h"
#include <string>

class  UserContext;

class  ONEBOX_DLL_EXPORT UpdateDBMgr
{
public:
	virtual ~UpdateDBMgr(){};
public:
	static UpdateDBMgr* getInstance(UserContext* usercontext);
	static void release();
public:
	virtual bool isUpdate() = 0;
	virtual bool update() = 0;
private:
	static UpdateDBMgr* instance_;
};

#endif
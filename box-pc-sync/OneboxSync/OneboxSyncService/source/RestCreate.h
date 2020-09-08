#ifndef _ONEBOX_REST_CREATE_H_
#define _ONEBOX_REST_CREATE_H_

#include "TransTask.h"
#include "IFolder.h"

class RestCreate : public ITransmit
{
public:
	RestCreate(UserContext* userContext, TransTask* transTask);

	virtual ~RestCreate();

	virtual void transmit();

	virtual void finishTransmit();

private:
	int32_t createDir(const int64_t parent, const std::wstring& name);

	int32_t createDir(const std::wstring& path);

private:
	UserContext* userContext_;
	TransTask* transTask_;
	IFolderPtr remoteFolder_;
};

#endif

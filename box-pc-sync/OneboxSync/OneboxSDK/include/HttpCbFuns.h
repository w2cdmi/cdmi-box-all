#ifndef __ONEBOX__HTTPCALLBACKFUNS__H__
#define __ONEBOX__HTTPCALLBACKFUNS__H__

#include "CommonDefine.h"

class HttpCbFuns
{
public:
    static int32_t readBodyDataCallback(void *buffer, uint32_t bufferSize, void *callbackData);
    static int32_t putObjectDataCallback(void *buffer, uint32_t bufferSize, void *callbackData);
    static int32_t getObjectDataCallback(void *buffer, uint32_t bufferSize, void *callbackData);
    static int32_t downloadFileCallback(void *buffer, uint32_t bufferSize, void *callbackData);
	static int32_t uploadFileCallback(void *buffer, uint32_t bufferSize, void *callbackData);
};

#endif // end of defined __ONEBOX__HTTPCALLBACKFUNS__H__

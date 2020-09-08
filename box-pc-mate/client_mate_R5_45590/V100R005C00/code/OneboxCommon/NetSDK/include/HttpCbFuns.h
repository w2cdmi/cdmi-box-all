/******************************************************************************
版权所有  : 2010-2020，华为赛门铁克科技有限公司
文 件 名  : HttpCbFuns.h 
作    者  : 陈云松
版    本  : V1.0
创建日期  : 2011-12-24
描    述  : CS http回调函数头文件
函数列表  :

历史记录   :
1.日    期 : 2011-12-24
作    者   : 陈云松
修改内容   : 完成初稿

*******************************************************************************/
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

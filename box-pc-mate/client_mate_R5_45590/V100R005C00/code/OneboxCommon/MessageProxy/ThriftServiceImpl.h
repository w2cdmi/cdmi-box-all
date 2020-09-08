#ifndef _ONEBOX_THRIFT_SERVICE_IMPL_H_
#define _ONEBOX_THRIFT_SERVICE_IMPL_H_

#include "OneboxExport.h"
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif
	int32_t ONEBOX_DLL_EXPORT startService();

	int32_t ONEBOX_DLL_EXPORT stopService();
#ifdef __cplusplus
}
#endif

#endif

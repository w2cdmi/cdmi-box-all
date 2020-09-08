#ifndef _ONEBOX_EXPORT_H_
#define _ONEBOX_EXPORT_H_

#if defined(_WIN32) && defined(ONEBOX_EXPORTS)
#pragma warning(push)
#pragma warning(disable:4251)
#define ONEBOX_DLL_EXPORT __declspec(dllexport)
#define _ISSP_BUILD_DLL
#else
#define ONEBOX_DLL_EXPORT
#endif

#endif

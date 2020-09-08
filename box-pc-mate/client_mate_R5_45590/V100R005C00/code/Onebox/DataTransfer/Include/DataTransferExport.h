#ifndef _DATA_TRANSFER_EXPORT_H_
#define _DATA_TRANSFER_EXPORT_H_

#if defined(_WIN32) && defined(DATATRANSFER152038TO5_EXPORTS)
#pragma warning(push)
#pragma warning(disable:4251)
#define DATA_TRANSFER_DLL_EXPORT __declspec(dllexport)
#define _ISSP_BUILD_DLL
#else
#define DATA_TRANSFER_DLL_EXPORT
#endif

#endif
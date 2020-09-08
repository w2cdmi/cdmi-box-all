#ifndef _SYSTEMINFO_EXPORT_H_
#define _SYSTEMINFO_EXPORT_H_

#if defined(SYSTEMINFO_EXPORTS)
#pragma warning(push)
#pragma warning(disable:4251)
#define SYSTEMINFO_DLL_EXPORT __declspec(dllexport)
#else
#define SYSTEMINFO_DLL_EXPORT
#endif

#endif
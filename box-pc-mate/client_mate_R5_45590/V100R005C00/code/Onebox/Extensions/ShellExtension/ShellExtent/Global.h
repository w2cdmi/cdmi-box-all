#ifndef _ShellExtent_GLOBAL_H_
#define _ShellExtent_GLOBAL_H_

#include <Windows.h>
#include <stdint.h>
#include <string>

namespace Onebox
{
	bool IsOneboxRunning();

   std::wstring GetMenuLanguageString(std::wstring key);

   std::wstring getShortPath(std::wstring fullPath,int32_t length);

   bool isSyncDir(const std::wstring& path);
}

#endif


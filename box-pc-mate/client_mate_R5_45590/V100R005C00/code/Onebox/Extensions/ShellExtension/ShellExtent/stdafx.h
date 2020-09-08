// stdafx.h : 标准系统包含文件的包含文件，
// 或是经常使用但不常更改的
// 特定于项目的包含文件

#pragma once

#ifndef STRICT
#define STRICT
#endif

#include "targetver.h"

#define _ATL_APARTMENT_THREADED

#define _ATL_NO_AUTOMATIC_NAMESPACE

#define _ATL_CSTRING_EXPLICIT_CONSTRUCTORS	// 某些 CString 构造函数将是显式的


#define ATL_NO_ASSERT_ON_DESTROY_NONEXISTENT_WINDOW

#include "resource.h"
#include <atlbase.h>
#include <atlcom.h>
#include <atlctl.h>
#include <atlimage.h>


#include <commctrl.h>
#include <string>
#include <list>
//#include <atlwin.h> 
typedef std::list<std::wstring> string_list; 

#include <UIlib.h>
using namespace DuiLib;

#ifdef _DEBUG
#ifdef _WIN64
#pragma comment(lib, "DuiLibLib_mt_x64_d.lib")
#else
#pragma comment(lib, "DuiLibLib_mt_d.lib")
#endif
#else
#ifdef _WIN64
#pragma comment(lib, "DuiLibLib_mt_x64.lib")
#else
#pragma comment(lib, "DuiLibLib_mt.lib")
#endif
#endif

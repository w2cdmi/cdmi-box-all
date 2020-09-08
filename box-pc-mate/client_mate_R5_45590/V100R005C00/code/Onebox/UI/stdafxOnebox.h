#pragma once

#include "targetver.h"
#include <stdio.h>
#include <tchar.h>
#include <windows.h>
#include <xstring>
#include <string>
#include <stdint.h>
#include <memory>
#include <map>
#include <set>
#include <UIlib.h>
#include "Common.h"
#include "UserContextMgr.h"

using namespace DuiLib;

#ifdef _DEBUG
#pragma comment(lib, "DuiLib_d.lib")
#else
#pragma comment(lib, "DuiLib.lib")
#endif

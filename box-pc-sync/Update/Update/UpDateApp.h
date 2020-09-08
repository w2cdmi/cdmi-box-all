#pragma once
#ifndef _UPDATEAPP_H_
#define _UPDATEAPP_H_

#include "UpdateIniFile/UpdateIniFile.h"
#include <string>


using namespace std;
class UpdateApp
{
public:
     bool Update(void);
private:
	 bool UpdateDatabase(void);
	 bool UpdateConfigFile(void);
	 void CompareVersion(wstring wstrOldVersion,wstring strNewVersion,int iRet);
};

#endif
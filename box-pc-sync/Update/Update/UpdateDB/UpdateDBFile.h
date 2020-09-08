#pragma once
#ifndef _UPDATEFBFILE_H_
#define _UPDATEFBFILE_H_

#include <string>
#include <boost/thread.hpp>
#include "../sqlite/include/CppSQLite3.h"

using namespace std;

#ifndef UPDATE_TANSTABLE_18VERSION
#define UPDATE_TANSTABLE_18VERSION (L"1.2.3.18")
#endif

#ifndef UPDATE_TANSTABLE_20VERSION
#define UPDATE_TANSTABLE_20VERSION (L"1.2.3.20")
#endif

class UpdateDBFile
{
public:
	bool UpdateTransTaskDBTo18(void);
	bool UpdateTransTaskDBTo20(void);
	bool UpdateSyncInfoTaskDB(void);
	bool UpdateRemoteInfoDB(void);
	bool UpdateLocalInfoDB(void);
	bool UpdateErrorInfoDB(void);
	bool UpdateDiffInfoDB(void);
	void CompareVersion(wstring wstrOldVersion,wstring wstrNewVersion,int& iRet);

private:
	bool CheckDBFileExist(string DBFileName);

private:
	boost::mutex m_mutexDB;
	CppSQLite3DB m_dbSQLite;
};
#endif
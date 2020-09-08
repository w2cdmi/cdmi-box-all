#include "../stdafx.h"
#include "UpdateDBFile.h"
#include "DBCommonDefine.h"
#include "../UpdateIniFile/UpdateIniFile.h"
#include "../Utility/Utility.h"

bool UpdateDBFile::UpdateErrorInfoDB(void)
{
	return true;
}

bool UpdateDBFile::UpdateTransTaskDBTo18(void)
{
	int iRet = 0;
	bool bRet = true;
	wstring wstrOldMainVersion;

	try
	{
		if(Utility::GetMainVersion(wstrOldMainVersion))
		{
			wstring wstrAppPath;
			string strDBFilePath;
			wstring wstrUserDataPath;
			boost::mutex::scoped_lock lock(m_mutexDB);

			if(Utility::GetAppPath(wstrAppPath))
			{
				wstrUserDataPath = wstrAppPath + CSE_DB_FILE;
				strDBFilePath = Utility::ws2s(wstrUserDataPath) + "\\" + TRANS_TASK_DB_NAME;
				strDBFilePath = Utility::replace_all(strDBFilePath,"\\","/");
			}
		
			wstring wstrEndChild = wstrOldMainVersion.substr(wstrOldMainVersion.find_last_of('.')+1,wstrOldMainVersion.length()-wstrOldMainVersion.find_last_of('.'));
		
			//过滤中间版本
			if(2 <= wstrEndChild.length())
			{
				wstring wstrNewEnd = wstrEndChild.substr(0,2);
				wstrOldMainVersion = wstrOldMainVersion.substr(0,wstrOldMainVersion.find_last_of('.')+1) + wstrNewEnd;
				CompareVersion(wstrOldMainVersion,UPDATE_TANSTABLE_18VERSION,iRet);
			}

			if(-2 >= iRet)
			{
				if(Utility::is_exist(wstrUserDataPath))
				{
					Utility::remove_all(wstrUserDataPath);
				}
			}
			else if(iRet == -1)
			{
				if(CheckDBFileExist(strDBFilePath))
				{
					m_dbSQLite.open(strDBFilePath.c_str());

					if(m_dbSQLite.tableExists(TRANSMIT_TABLE_NAME))
					{
						CppSQLite3Buffer bufSQL;

						(void)bufSQL.format("ALTER TABLE %s RENAME TO _temp_%s;",TRANSMIT_TABLE_NAME,TRANSMIT_TABLE_NAME);
						(void)m_dbSQLite.execDML(bufSQL);

						(void)bufSQL.format("CREATE TABLE %s (\
										%s VARCHAR(40)  PRIMARY KEY NOT NULL,\
										%s INTEGER  NOT NULL,\
										%s INTEGER NOT NULL,\
										%s INTEGER NOT NULL,\
										%s INTEGER NOT NULL,\
										%s BLOB NULL\
										);", TRANSMIT_TABLE_NAME, TRANSMIT_ROW_FILE_ID, 
										TRANSMIT_ROW_TRANS_STATE, TRANSMIT_ROW_PRIORITY, 
										TRANSMIT_ROW_SHCDULE_TIMES, TRANSMIT_ROW_VERSION, 
										TRANSMIT_ROW_META_DATA);
						(void)m_dbSQLite.execDML(bufSQL);

						int itemp =0;
						(void)bufSQL.format("INSERT INTO %s SELECT %s,%s,%s,0,%s,%s FROM  _temp_%s;",\
										TRANSMIT_TABLE_NAME, TRANSMIT_ROW_FILE_ID, 
										TRANSMIT_ROW_TRANS_STATE, TRANSMIT_ROW_PRIORITY, 
										TRANSMIT_ROW_VERSION,TRANSMIT_ROW_META_DATA,TRANSMIT_TABLE_NAME);
						(void)m_dbSQLite.execDML(bufSQL);

						(void)bufSQL.format("DROP TABLE   _temp_%s;" ,TRANSMIT_TABLE_NAME);
						(void)m_dbSQLite.execDML(bufSQL);
					}

					m_dbSQLite.close();
				}
			}
		}
	}
	catch(...)
	{	
		return  false;
	}
	
	return true;
}

bool UpdateDBFile::UpdateTransTaskDBTo20(void)
{
	int iRet = 0;
	bool bRet = true;
	wstring wstrOldMainVersion;

	try
	{
		if(Utility::GetMainVersion(wstrOldMainVersion))
		{
			wstring wstrAppPath;
			string strDBFilePath;
			wstring wstrUserDataPath;
			boost::mutex::scoped_lock lock(m_mutexDB);

			if(Utility::GetAppPath(wstrAppPath))
			{
				wstrUserDataPath = wstrAppPath + CSE_DB_FILE;
				strDBFilePath = Utility::ws2s(wstrUserDataPath) + "\\" + TRANS_TASK_DB_NAME;
				strDBFilePath = Utility::replace_all(strDBFilePath,"\\","/");
			}
		
			wstring wstrEndChild = wstrOldMainVersion.substr(wstrOldMainVersion.find_last_of('.')+1,wstrOldMainVersion.length()-wstrOldMainVersion.find_last_of('.'));
		
			//过滤中间版本
			if(2 <= wstrEndChild.length())
			{
				wstring wstrNewEnd = wstrEndChild.substr(0,2);
				wstrOldMainVersion = wstrOldMainVersion.substr(0,wstrOldMainVersion.find_last_of('.')+1) + wstrNewEnd;
				CompareVersion(wstrOldMainVersion,UPDATE_TANSTABLE_20VERSION,iRet);
			}

			if(-2 >= iRet)
			{
				if(Utility::is_exist(wstrUserDataPath))
				{
					Utility::remove_all(wstrUserDataPath);
				}
			}
			else if(iRet == -1)
			{
				if(CheckDBFileExist(strDBFilePath))
				{
					m_dbSQLite.open(strDBFilePath.c_str());

					if(m_dbSQLite.tableExists(TRANSMIT_TABLE_NAME))
					{
						CppSQLite3Buffer bufSQL;

						(void)bufSQL.format("ALTER TABLE %s RENAME TO _temp_%s;",TRANSMIT_TABLE_NAME,TRANSMIT_TABLE_NAME);
						(void)m_dbSQLite.execDML(bufSQL);

						(void)bufSQL.format("CREATE TABLE %s (\
										%s VARCHAR(40)  PRIMARY KEY NOT NULL,\
										%s INTEGER  NOT NULL,\
										%s INTEGER NOT NULL,\
										%s INTEGER NOT NULL,\
										%s INTEGER NOT NULL,\
										%s BLOB NULL\
										);", TRANSMIT_TABLE_NAME, TRANSMIT_ROW_FILE_ID, 
										TRANSMIT_ROW_TRANS_STATE, TRANSMIT_ROW_PRIORITY, 
										TRANSMIT_ROW_SHCDULE_TIMES, TRANSMIT_ROW_VERSION, 
										TRANSMIT_ROW_META_DATA);
						(void)m_dbSQLite.execDML(bufSQL);

						(void)bufSQL.format("CREATE INDEX ind_id ON %s(%s)", 
								TRANSMIT_TABLE_NAME, TRANSMIT_ROW_FILE_ID);
						(void)m_dbSQLite.execDML(bufSQL);

						int itemp =0;
						(void)bufSQL.format("INSERT INTO %s SELECT %s,%s,%s,%s,%s,%s FROM  _temp_%s;",\
										 TRANSMIT_TABLE_NAME, TRANSMIT_ROW_FILE_ID, 
										TRANSMIT_ROW_TRANS_STATE, TRANSMIT_ROW_PRIORITY, 
										TRANSMIT_ROW_SHCDULE_TIMES,TRANSMIT_ROW_VERSION,
										TRANSMIT_ROW_META_DATA,TRANSMIT_TABLE_NAME);
						(void)m_dbSQLite.execDML(bufSQL);

						(void)bufSQL.format("DROP TABLE   _temp_%s;" ,TRANSMIT_TABLE_NAME);
						(void)m_dbSQLite.execDML(bufSQL);
					}

					m_dbSQLite.close();
				}
			}
		}
	}
	catch(...)
	{	
		return  false;
	}
	
	return true;
}

bool UpdateDBFile::UpdateSyncInfoTaskDB(void)
{
	return true;
}

bool UpdateDBFile::UpdateRemoteInfoDB(void)
{
	return true;
}

bool UpdateDBFile::UpdateLocalInfoDB(void)
{
	return true;
}

bool UpdateDBFile::UpdateDiffInfoDB(void)
{
	return true;
}

bool UpdateDBFile::CheckDBFileExist(string strDBFilePath)
{
	wstring wstrDBFilePath = Utility::s2ws(strDBFilePath);

	if(Utility::is_exist(wstrDBFilePath.c_str()))
	{
		return true;
	}
	else
	{
		return false;
	}
}

void UpdateDBFile::CompareVersion(wstring wstrOldVersion,wstring wstrNewVersion,int& iRet)
{
	string strOldVersion = Utility::ws2s(wstrOldVersion);
	string strNewVersion = Utility::ws2s(wstrNewVersion);
	string strOldRemain;
	string strOldCompare; 
	string strNewRemain;
	string strNewCompare; 

	strOldCompare = strOldVersion.substr(0,strOldVersion.find_first_of('.'));
	strOldRemain = strOldVersion.substr(strOldVersion.find_first_of('.')+1,strOldVersion.length()-strOldVersion.find_first_of('.'));
	strNewCompare = strNewVersion.substr(0,strNewVersion.find_first_of('.'));
	strNewRemain = strNewVersion.substr(strNewVersion.find_first_of('.')+1,strNewVersion.length()-strNewVersion.find_first_of('.'));

	int iOldVersion = atoi(strOldCompare.c_str());
	int iNewVersion = atoi(strNewCompare.c_str());

	if(iRet != 0)
	{
		return;
	}

	if(iOldVersion == iNewVersion)
	{
		if(strOldCompare ==strOldRemain || strNewCompare == strNewRemain)
		{
			iRet = 0;
			return;
		}

		CompareVersion(Utility::s2ws(strOldRemain),Utility::s2ws(strNewRemain),iRet);
	}
	else if(iOldVersion < iNewVersion)
	{
		if(strOldCompare ==strOldRemain && strNewCompare == strNewRemain)
		{
			iRet = iOldVersion -  iNewVersion;
		}
		else
		{
			iRet = -1;
		}
	}
	else if(iOldVersion > iNewVersion)
	{
		if(strOldCompare ==strOldRemain && strNewCompare == strNewRemain)
		{
			iRet = iOldVersion -  iNewVersion;
		}
		else
		{
			iRet = 1;
		}
	}
}
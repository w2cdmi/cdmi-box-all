#include "stdafx.h"
#include "UpdateApp.h"
#include "UpdateIniFile/UpdateIniFile.h"
#include "Utility/Utility.h"
#include "UpdateDB/UpdateDBFile.h"


bool UpdateApp::Update(void)
{
	bool bRet = true;

	if(!UpdateConfigFile())
	{
		 bRet = false;
	}

	if(!UpdateDatabase())
	{
		  bRet = false;
	}

	return bRet;
}

bool UpdateApp::UpdateConfigFile(void)
{
	UpdateIniFile varUpDateIniFile;

	if (!varUpDateIniFile.UpdateConfigFile())
	{
		return false;
	}

	return true;
}

bool UpdateApp::UpdateDatabase(void)
{
	bool bRet = true;
	UpdateDBFile VarUpdateDBFile;
	if(!VarUpdateDBFile.UpdateTransTaskDBTo18())
	{
		bRet =  false;
	}
		
	if(!VarUpdateDBFile.UpdateTransTaskDBTo20())
	{
		bRet =  false;
	}
		
	return bRet;
}


/******************************************************************************
Description  : 获取服务器配置
Created By   : l00100468
*******************************************************************************/
#ifndef __ONEBOX__SYSCONFIG__H__
#define __ONEBOX__SYSCONFIG__H__

#include "CommonValue.h"

class ServerSysConfig
{
public:
	ServerSysConfig()
	{
		loginFailNotify_ = false;
		loginFailNum_ = 0;
		notRemberMe_ = false;
		complexCode_ = false;
		backupAllowedRule_ = "";
		backupForbiddenRule_ = "";
	};

	virtual ~ServerSysConfig()
	{
	};

		FUNC_DEFAULT_SET_GET(bool, loginFailNotify)
		FUNC_DEFAULT_SET_GET(bool, notRemberMe)
		FUNC_DEFAULT_SET_GET(bool, complexCode)
		FUNC_DEFAULT_SET_GET(int32_t, loginFailNum)
		FUNC_DEFAULT_SET_GET(std::string, backupAllowedRule)
		FUNC_DEFAULT_SET_GET(std::string, backupForbiddenRule)

private:
	bool loginFailNotify_;	//登陆失败通知管理员；
	bool notRemberMe_;		//是否记住用户登陆信息；
	bool complexCode_;			//链接提取码是否为强复杂度；
	int32_t loginFailNum_;				//允许失败次数，
	std::string backupAllowedRule_;		//全盘备份白名单
	std::string backupForbiddenRule_;	//全盘备份黑名单
};

#endif // end of defined __ONEBOX__SYSCONFIG__H__

/******************************************************************************
Description  : Email参数
Created By   : l00295403
*******************************************************************************/
#ifndef __ONEBOX__EMAILNODE__H__
#define __ONEBOX__EMAILNODE__H__

#include "CommonValue.h"


struct EmailParam
{
	std::string message;	//邮件自定义消息内容
	std::string nodename;	//文件或文件夹名称
	std::string sender;		//发件人全名
	int32_t	type;			//共享文件类型
	int64_t ownerid;		//持有人ID
	int64_t nodeid;			//file/folder ID
	std::string plainaccesscode;	//外链提取码
	int64_t start;		//外链生效时间
	int64_t end;		//外链失效时间
	std::string linkurl;	//外链访问url

	EmailParam():message(""),nodename(""),sender(""),
		type(0),ownerid(-1L),nodeid(-1L),
		plainaccesscode(""),start(0L),end(0L),linkurl("")
	{
	}
};

struct EmailNode
{
	std::string type;	//邮件类型
	std::string mailto;	//收件人电子邮箱
	std::string copyto;	//抄送人电子邮箱
	EmailParam email_param;	//参数列表
};

struct EmailInfoNode
{
	int64_t sender;		//消息发送者ID
	std::string source;	//邮件消息来源 share or link
	int64_t ownedBy;	//资源拥有者
	int64_t nodeId;		//文件夹或文件ID
	std::string subject;	//邮件标题
	std::string message;	//邮件内容

	EmailInfoNode():
		sender(0L),
		source(""),
		ownedBy(0L),
		nodeId(0L),
		subject(""),
		message("")
	{
	}
};

#endif // end of defined __ONEBOX__EMAILNODE__H__
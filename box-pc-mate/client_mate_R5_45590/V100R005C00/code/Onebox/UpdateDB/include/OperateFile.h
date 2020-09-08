#ifndef  _ONEBOX_OPERATEFILE_H_
#define  _ONEBOX_OPERATEFILE_H_
#include <string>

#ifndef COMMAND_IFCOPY
#define COMMAND_IFCOPY (L"IFCOPY")
#endif

#ifndef COMMAND_IFRENAME
#define COMMAND_IFRENAME (L"IFRENAME")
#endif

class OperateFileMgr
{
public:
	virtual ~OperateFileMgr(){}
public:
	static OperateFileMgr* getInstance();
    static void releaseInstance();
public:
	virtual bool runCommand(std::wstring command,std::wstring param)=0;
	virtual std::wstring getDesDirPath()=0;
	virtual std::wstring getSourceDirPath()=0;
private:
	static OperateFileMgr* instance_;
};

#endif
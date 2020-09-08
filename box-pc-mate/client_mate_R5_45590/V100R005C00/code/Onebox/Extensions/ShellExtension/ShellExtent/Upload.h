#pragma  once
#include "ThriftService.h"
#include <UIlib.h>
#include "CommonFileDialogThriftNotify.h"

#define  RIGHT_UPLOADFRAME_CLSNAME L"RIGHT_UPLOADFRAME_CLSNAME"
using namespace OneboxThriftService;

class UploadImpl
{
public:
	virtual ~UploadImpl(){};

	static UploadImpl* getInstance();

	virtual void UploadFile(std::wstring FilePath) = 0;

private:
	static std::auto_ptr<UploadImpl> instance_;
};

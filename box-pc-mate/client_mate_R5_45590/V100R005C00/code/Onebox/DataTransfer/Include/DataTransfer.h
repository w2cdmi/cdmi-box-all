#pragma once

#include "DataTransferExport.h"
#include <xstring>
#include "UserContext.h"

class DATA_TRANSFER_DLL_EXPORT CDataTransfer
{
public:
	CDataTransfer(const std::wstring& userId);
	~CDataTransfer(void);
	bool UpdateDB();

private:
	class Impl;
	std::shared_ptr<Impl> impl_;
};


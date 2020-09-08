#include "TransTask.h"
#include "Utility.h"
#include "RestUpload.h"
#include "RestDownload.h"
#include "RestCreateFolder.h"
#include "LocalCreateFolder.h"
#include <windows.h>
#include <Wincrypt.h>
#include "SmartHandle.h"
#include "UserContextMgr.h"
#include "ConfigureMgr.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("TransTask")
#endif

using namespace SD;

class TransTaskImpl : public TransTask
{
private:
	UserContext* userContext_;
	TransSerializer* searializer_;
	ITransmitNotifies transmitNotifies_;
	AsyncTransDetailNode transDetailNode_;
	std::auto_ptr<ITransmit> transmit_;

public:
	TransTaskImpl(UserContext* userContext, 
		AsyncTransDetailNode& transDetailNode, 
		TransSerializer* searializer, 
		ITransmitNotifies& transmitNotifies, 
		CISSPNotifyPtr notify)
		:userContext_(userContext)
		,searializer_(searializer)
		,transmitNotifies_(transmitNotifies)
		,transDetailNode_(transDetailNode)
		,transmit_(NULL)
	{
		SetTaskNotify(notify);
	}

	virtual void TaskExecute()
	{
		AsyncTransType type = transDetailNode_->root->type;
		if (ATT_Upload == type || ATT_Backup == type || ATT_Upload_Outlook == type || ATT_Upload_Office == type)
		{
			if (FILE_TYPE_FILE == transDetailNode_->fileType)
			{
				transmit_.reset(static_cast<ITransmit*>(new RestUpload(userContext_, this)));
			}
			else
			{
				transmit_.reset(static_cast<ITransmit*>(new RestCreateFolder(userContext_, this)));
			}
		}
		else if(ATT_Download == type)
		{
			if (FILE_TYPE_FILE == transDetailNode_->fileType)
			{
				transmit_.reset(static_cast<ITransmit*>(new RestDownload(userContext_, this)));
			}
			else
			{
				transmit_.reset(static_cast<ITransmit*>(new LocalCreateFolder(userContext_, this)));
			}
		}
		else
		{
			SetErrorCode(RT_INVALID_PARAM);
			return;
		}

		for (ITransmitNotifies::iterator it = transmitNotifies_.begin(); it != transmitNotifies_.end(); ++it)
		{
			transmit_->addNotify(*it);
		}		
		transmit_->setSerializer(searializer_);

		transmit_->transmit();
		transmit_->finishTransmit();
	}

	virtual void doEnterComplete()
	{
		std::wostringstream strBuffer;
		if (IsCompletedWithSuccess())
		{
			
			HSLOG_TRACE(MODULE_NAME, RT_OK, "Source file: %s, %s || Target parent: %s || Run time: %u ms", 
				Utility::String::wstring_to_string(transDetailNode_->source).c_str(),
				Utility::String::wstring_to_string(transDetailNode_->name).c_str(),
				Utility::String::wstring_to_string(transDetailNode_->parent).c_str(),
				GetTaskRunningTime());
		}
		else
		{
			HSLOG_TRACE(MODULE_NAME, GetErrorCode(), "Source file: %s, %s || Target parent: %s || Run time: %u ms || Error desc: %s",
				Utility::String::wstring_to_string(transDetailNode_->source).c_str(),
				Utility::String::wstring_to_string(transDetailNode_->name).c_str(),
				Utility::String::wstring_to_string(transDetailNode_->parent).c_str(),
				GetTaskRunningTime(), GetErrorMessage().c_str());
		}
	}

	virtual AsyncTransDetailNode& getTransDetailNode()
	{
		return transDetailNode_;
	}

	virtual ITransmitNotifies& getTransmitNotifies()
	{
		return transmitNotifies_;
	}
};

TransTask* TransTask::create(UserContext* userContext, 
							 AsyncTransDetailNode& transDetailNode, 
							 TransSerializer* searializer, 
							 ITransmitNotifies& transmitNotifies, 
							 CISSPNotifyPtr notify)
{
	return static_cast<TransTask*>(new TransTaskImpl(userContext, transDetailNode, searializer, transmitNotifies, notify));
}

static int32_t getFingerprint(const std::wstring& path, CISSPTask* task, Fingerprint& fingerprint)
{
	if (path.empty() || fingerprint.algorithm == Fingerprint::Invalid)
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;

	const DWORD BUFSIZE = 4*1024*1024; // 4MB
	HCRYPTPROV hProv = 0;
	HCRYPTHASH hHash = 0;
	ALG_ID ai;
	SmartHandle hFile = NULL;
	DWORD cbRead = 0;
	DWORD cbHash = 0;
	CHAR rgbDigits[] = "0123456789abcdef";
	DWORD SIGNATURELEN = 0;

	if (Fingerprint::SHA1 == fingerprint.algorithm)
	{
		SIGNATURELEN = 40;
		ai = CALG_SHA1;
	}
	else if (Fingerprint::MD5 == fingerprint.algorithm)
	{
		SIGNATURELEN = 32;
		ai = CALG_MD5;
	}
	else
	{
		ret = RT_INVALID_PARAM;
		return ret;
	}

	std::auto_ptr<BYTE> rgbFile(new BYTE[BUFSIZE]);
	std::auto_ptr<BYTE> rgbHash(new BYTE[SIGNATURELEN]);

	hFile = CreateFile(std::wstring(L"\\\\?\\"+path).c_str(),
		GENERIC_READ,
		FILE_SHARE_READ|FILE_SHARE_WRITE,
		NULL,
		OPEN_EXISTING,
		FILE_FLAG_SEQUENTIAL_SCAN,
		NULL);

	if (INVALID_HANDLE_VALUE == hFile)
	{
		ret = GetLastError();
		return ret;
	}

	// Get handle to the crypt provider
	if (!CryptAcquireContext(&hProv,
		NULL,
		NULL,
		PROV_RSA_FULL,
		CRYPT_VERIFYCONTEXT))
	{
		ret = GetLastError();
		return ret;
	}

	if (!CryptCreateHash(hProv, ai, 0, 0, &hHash))
	{
		CryptReleaseContext(hProv, 0);
		ret = GetLastError();
		return ret;
	}

	while (true)
	{
		if (task->IsCancel())
		{
			CryptReleaseContext(hProv, 0);
			CryptDestroyHash(hHash);
			ret = GetLastError();
			return ret;
		}

		if (!ReadFile(hFile, rgbFile.get(), BUFSIZE, &cbRead, NULL))
		{
			CryptReleaseContext(hProv, 0);
			CryptDestroyHash(hHash);
			ret = GetLastError();
			// convert the error code
			if (ERROR_INVALID_PARAMETER == ret)
			{
				ret = ERROR_SHARING_VIOLATION;
			}
			return ret;
		}
		if (0 == cbRead)
		{
			break;
		}

		if (!CryptHashData(hHash, rgbFile.get(), cbRead, 0))
		{
			CryptReleaseContext(hProv, 0);
			CryptDestroyHash(hHash);
			ret = GetLastError();
			return ret;
		}
	}

	cbHash = SIGNATURELEN;
	(void)memset_s(rgbHash.get(), SIGNATURELEN, 0, SIGNATURELEN);
	if (CryptGetHashParam(hHash, HP_HASHVAL, rgbHash.get(), &cbHash, 0))
	{
		CHAR* buf = new CHAR[SIGNATURELEN+1];
		if ( buf )
		{
			(void)memset_s(buf, SIGNATURELEN+1, 0, SIGNATURELEN+1);
			for (DWORD i = 0; i < cbHash; i++)
			{
				buf[2*i] = rgbDigits[rgbHash.get()[i] >> 4];
				buf[2*i+1] = rgbDigits[rgbHash.get()[i] & 0xf];
			}
			fingerprint.fingerprint = std::string(buf);

			delete[] buf;
			buf = NULL;
		}
	}

	CryptDestroyHash(hHash);
	CryptReleaseContext(hProv, 0);

	return ret;
}

static int32_t getBlockMD5(const std::wstring& path, Fingerprint& fingerprint)
{
	if (path.empty() || fingerprint.algorithm != Fingerprint::MD5)
	{
		return RT_INVALID_PARAM;
	}

	int32_t ret = RT_OK;
	UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
	if(NULL==defaultUserContext)
	{
		ret = RT_ERROR;
		return ret;
	}

	AlgorithmConf algorithmConf = defaultUserContext->getConfigureMgr()->getConfigure()->algorithmConf();		
	const DWORD MD5LEN = 32;
	HCRYPTPROV hProv = 0;
	HCRYPTHASH hHash = 0;
	SmartHandle hFile = NULL;
	DWORD cbRead = 0;
	BYTE rgbHash[MD5LEN] = {0};
	DWORD cbHash = 0;
	CHAR rgbDigits[] = "0123456789abcdef";

	hFile = CreateFile(std::wstring(L"\\\\?\\"+path).c_str(),
		GENERIC_READ,
		FILE_SHARE_READ|FILE_SHARE_WRITE,
		NULL,
		OPEN_EXISTING,
		FILE_FLAG_SEQUENTIAL_SCAN,
		NULL);

	if (INVALID_HANDLE_VALUE == hFile)
	{
		ret = GetLastError();
		return ret;
	}

	// Get handle to the crypt provider
	if (!CryptAcquireContext(&hProv,
		NULL,
		NULL,
		PROV_RSA_FULL,
		CRYPT_VERIFYCONTEXT))
	{
		ret = GetLastError();
		return ret;
	}

	if (!CryptCreateHash(hProv, CALG_MD5, 0, 0, &hHash))
	{
		CryptReleaseContext(hProv, 0);
		ret = GetLastError();
		return ret;
	}

	int64_t size = Utility::FS::get_file_size(path);
	if (0 > size)
	{
		CryptReleaseContext(hProv, 0);
		CryptDestroyHash(hHash);
		return RT_INVALID_PARAM;
	}

	DWORD firstBlockSize = 0;
	// less than 256 byte
	if (algorithmConf.lowMD5Size >= size)
	{
		CryptReleaseContext(hProv, 0);
		CryptDestroyHash(hHash);
		return RT_OK;
	}
	// less than 256 kb
	else if (algorithmConf.middleMD5Size >= size)
	{
		firstBlockSize = algorithmConf.lowMD5Size;
	}
	// more than 256 kb
	else
	{
		firstBlockSize = algorithmConf.middleMD5Size;
	}

	std::auto_ptr<BYTE> rgbFile(new BYTE[firstBlockSize]);

	while (true)
	{
		if (0 >= firstBlockSize)
		{
			break;
		}

		if (!ReadFile(hFile, rgbFile.get(), firstBlockSize, &cbRead, NULL))
		{
			CryptReleaseContext(hProv, 0);
			CryptDestroyHash(hHash);
			ret = GetLastError();
			// convert the error code
			if (ERROR_INVALID_PARAMETER == ret)
			{
				ret = ERROR_SHARING_VIOLATION;
			}
			return ret;
		}

		firstBlockSize -= cbRead;

		if (0 == cbRead)
		{
			break;
		}

		if (!CryptHashData(hHash, rgbFile.get(), cbRead, 0))
		{
			CryptReleaseContext(hProv, 0);
			CryptDestroyHash(hHash);
			ret = GetLastError();
			return ret;
		}
	}

	cbHash = MD5LEN;
	if (!CryptGetHashParam(hHash, HP_HASHVAL, rgbHash, &cbHash, 0))
	{
		CryptReleaseContext(hProv, 0);
		CryptDestroyHash(hHash);
		ret = GetLastError();
		return ret;
	}

	CHAR buf[MD5LEN+1] = {0};
	for (DWORD i = 0; i < cbHash; i++)
	{
		buf[2*i] = rgbDigits[rgbHash[i] >> 4];
		buf[2*i+1] = rgbDigits[rgbHash[i] & 0xf];
	}
	fingerprint.blockFingerprint = std::string(buf);

	CryptDestroyHash(hHash);
	CryptReleaseContext(hProv, 0);

	return ret;
}

int32_t getFingerprint(Fingerprint& fingerprint, const std::wstring& path, CISSPTask* task)
{
	if (Fingerprint::SHA1 == fingerprint.algorithm)
	{
		fingerprint.algorithm = Fingerprint::SHA1;
		return getFingerprint(path, task, fingerprint);
	}
	else if (Fingerprint::MD5 == fingerprint.algorithm)
	{
		fingerprint.algorithm = Fingerprint::MD5;
		int32_t ret = getFingerprint(path, task, fingerprint);
		if (RT_OK != ret)
		{
			return ret;
		}
		return getBlockMD5(path, fingerprint);
	}
	return RT_INVALID_PARAM;
}

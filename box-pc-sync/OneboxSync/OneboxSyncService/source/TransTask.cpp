#include "TransTask.h"
#include "Utility.h"
#include "RestUpload.h"
#include "RestDownload.h"
#include "RestCreate.h"
#include <windows.h>
#include <Wincrypt.h>
#include "SmartHandle.h"
#include "ConfigureMgr.h"
#ifndef MODULE_NAME
#define MODULE_NAME ("TransTask")
#endif

#ifndef ONEBOX_SETTING_PATH
#ifdef _WIN64
#define ONEBOX_SETTING_PATH (L"SOFTWARE\\Wow6432Node\\Chinasoft\\Onebox\\Setting")
#else
#define ONEBOX_SETTING_PATH (L"SOFTWARE\\Chinasoft\\Onebox\\Setting")
#endif
#endif

using namespace SD;

class TransTaskImpl : public TransTask
{
private:
	UserContext* userContext_;
	std::shared_ptr<AsyncTransTaskNode> transTaskNode_;
	CustomNotifyInfo customNotifyInfo_;
	std::auto_ptr<ITransmit> transmit_;

public:
	TransTaskImpl(UserContext* userContext, std::shared_ptr<AsyncTransTaskNode> transTaskNode, CISSPNotifyPtr& notify)
		:userContext_(userContext)
		,transTaskNode_(transTaskNode)
		,transmit_(NULL)
	{
		SetTaskNotify(notify);
	}

	virtual void TaskExecute()
	{
		if (ATT_Upload == transTaskNode_->id.type)
		{
			transmit_ = std::auto_ptr<ITransmit>(static_cast<ITransmit*>(new RestUpload(userContext_, this)));
		}
		else if(ATT_Download == transTaskNode_->id.type)
		{
			transmit_ = std::auto_ptr<ITransmit>(static_cast<ITransmit*>(new RestDownload(userContext_, this)));
		}
		else if (ATT_Upload_Manual == transTaskNode_->id.type)
		{
			if (Utility::FS::is_directory(transTaskNode_->id.id))
			{
				transmit_ = std::auto_ptr<ITransmit>(static_cast<ITransmit*>(new RestCreate(userContext_, this)));
			}
			else
			{
				transmit_ = std::auto_ptr<ITransmit>(static_cast<ITransmit*>(new RestUpload(userContext_, this)));
			}
		}
		else if (ATT_Upload_Attachements == transTaskNode_->id.type)
		{
			// upload attachements should create the remote folder first
			std::wstring tempId = transTaskNode_->id.id;
			transTaskNode_->id.id = transTaskNode_->parent;
			transmit_ = std::auto_ptr<ITransmit>(static_cast<ITransmit*>(new RestCreate(userContext_, this)));
			transmit_->transmit();
			transmit_->finishTransmit();
			if (IsError())
			{
				transTaskNode_->id.id = tempId;
				return;
			}
			transTaskNode_->id.id = tempId;
			transmit_.reset(static_cast<ITransmit*>(new RestUpload(userContext_, this)));
		}
		else
		{
			return;
		}

		transmit_->transmit();

		transmit_->finishTransmit();
	}

	virtual void doEnterComplete()
	{
		std::wostringstream strBuffer;
		bool bSuccess = true;
		if (IsCompletedWithSuccess())
		{
			/*
			strBuffer << L"------------------------------Begin--------------------------------" << endl;
			strBuffer << Utility::String::format_string(L"||Source file: %s, %s", transTaskNode_->id.id.c_str(), transTaskNode_->name.c_str()) << endl;
			strBuffer << Utility::String::format_string(L"||Target parent: %s", transTaskNode_->parent.c_str()) << endl;
			strBuffer << Utility::String::format_string(L"||Run    time: %u s", GetTaskRunningTime()/1000) << endl;
			strBuffer << Utility::String::format_string(L"||Operation state: %s", L"Success") << endl;
			strBuffer << L"------------------------------End---------------------------------";
			*/
			HSLOG_TRACE(MODULE_NAME, RT_OK, "Source file: %s, %s || Target parent: %s || Run time: %u ms", 
				Utility::String::wstring_to_string(transTaskNode_->id.id).c_str(),
				Utility::String::wstring_to_string(transTaskNode_->name).c_str(),
				Utility::String::wstring_to_string(transTaskNode_->parent).c_str(),
				GetTaskRunningTime());
		}
		else
		{
			/*
			strBuffer << L"------------------------------Begin--------------------------------" << endl;
			strBuffer << Utility::String::format_string(L"||Source file: %s, %s", transTaskNode_->id.id.c_str(), transTaskNode_->name.c_str()) << endl;
			strBuffer << Utility::String::format_string(L"||Target parent: %s", transTaskNode_->parent.c_str()) << endl;
			strBuffer << Utility::String::format_string(L"||Run    time: %u s", GetTaskRunningTime()/1000) << endl;
			strBuffer << Utility::String::format_string(L"||Error  code: %d", GetErrorCode()) << endl;
			strBuffer << Utility::String::format_string(L"||Error  desc: %s", Utility::String::string_to_wstring(GetErrorMessage()).c_str()) << endl;
			strBuffer << Utility::String::format_string(L"||Operation state: %s", L"Error") << endl;
			strBuffer << L"------------------------------End---------------------------------";
			*/
			HSLOG_TRACE(MODULE_NAME, GetErrorCode(), "Source file: %s, %s || Target parent: %s || Run time: %u ms || Error desc: %s",
				Utility::String::wstring_to_string(transTaskNode_->id.id).c_str(),
				Utility::String::wstring_to_string(transTaskNode_->name).c_str(),
				Utility::String::wstring_to_string(transTaskNode_->parent).c_str(),
				GetTaskRunningTime(), GetErrorMessage().c_str());
			bSuccess = false;
		}
	}

	int32_t getSignature(const std::wstring& localPath, FileSignature& signature)
	{
		if (localPath.empty() || signature.algorithm == FileSignature::Invalid)
		{
			SetErrorCode(RT_INVALID_PARAM);
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

		if (FileSignature::SHA1 == signature.algorithm)
		{
			SIGNATURELEN = 40;
			ai = CALG_SHA1;
		}
		else if (FileSignature::MD5 == signature.algorithm)
		{
			SIGNATURELEN = 32;
			ai = CALG_MD5;
		}
		else
		{
			ret = RT_INVALID_PARAM;
			SetErrorAndMessage(ret, "invalid signature algorithm");
			return ret;
		}
		
		std::auto_ptr<BYTE> rgbFile(new BYTE[BUFSIZE]);
		std::auto_ptr<BYTE> rgbHash(new BYTE[SIGNATURELEN]);

		hFile = CreateFile(std::wstring(L"\\\\?\\"+localPath).c_str(),
			GENERIC_READ,
			FILE_SHARE_READ,
			NULL,
			OPEN_EXISTING,
			FILE_FLAG_SEQUENTIAL_SCAN,
			NULL);

		if (INVALID_HANDLE_VALUE == hFile)
		{
			ret = GetLastError();
			SetErrorCode(ret);
			return ret;
		}

		// Get handle to the crypto provider
		if (!CryptAcquireContext(&hProv,
			NULL,
			NULL,
			PROV_RSA_FULL,
			CRYPT_VERIFYCONTEXT))
		{
			ret = GetLastError();
			SetErrorCode(ret);
			return ret;
		}

		if (!CryptCreateHash(hProv, ai, 0, 0, &hHash))
		{
			CryptReleaseContext(hProv, 0);
			ret = GetLastError();
			SetErrorCode(ret);
			return ret;
		}

		while (true)
		{
			if (IsCancel())
			{
				CryptReleaseContext(hProv, 0);
				CryptDestroyHash(hHash);
				ret = GetLastError();
				SetErrorCode(ret);
				return ret;
			}

			if (!ReadFile(hFile, rgbFile.get(), BUFSIZE, &cbRead, NULL))
			{
				CryptReleaseContext(hProv, 0);
				CryptDestroyHash(hHash);
				ret = GetLastError();
				SetErrorCode(ret);
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
				SetErrorCode(ret);
				return ret;
			}
		}

		cbHash = SIGNATURELEN;
		memset(rgbHash.get(), 0, SIGNATURELEN);
		if (CryptGetHashParam(hHash, HP_HASHVAL, rgbHash.get(), &cbHash, 0))
		{
			std::auto_ptr<CHAR> buf(new CHAR[SIGNATURELEN+1]);
			memset(buf.get(), 0, SIGNATURELEN);
			for (DWORD i = 0; i < cbHash; i++)
			{
#pragma warning(push)
#pragma warning(disable:4996)
				sprintf(&buf.get()[2*i], "%c%c", rgbDigits[rgbHash.get()[i] >> 4], rgbDigits[rgbHash.get()[i] & 0xf]);
#pragma warning(pop)
			}
			signature.signature = std::string(buf.get());
		}

		CryptDestroyHash(hHash);
		CryptReleaseContext(hProv, 0);

		return ret;
	}

	int32_t getBlockMD5(const std::wstring& localPath, FileSignature& signature)
	{
		if (localPath.empty() || signature.algorithm != FileSignature::MD5)
		{
			SetErrorCode(RT_INVALID_PARAM);
			return RT_INVALID_PARAM;
		}

		int32_t ret = RT_OK;
		
		AlgorithmConf algorithmConf = userContext_->getConfigureMgr()->getConfigure()->algorithmConf();		
		const DWORD MD5LEN = 32;
		HCRYPTPROV hProv = 0;
		HCRYPTHASH hHash = 0;
		SmartHandle hFile = NULL;
		DWORD cbRead = 0;
		BYTE rgbHash[MD5LEN] = {0};
		DWORD cbHash = 0;
		CHAR rgbDigits[] = "0123456789abcdef";

		hFile = CreateFile(std::wstring(L"\\\\?\\"+localPath).c_str(),
			GENERIC_READ,
			FILE_SHARE_READ,
			NULL,
			OPEN_EXISTING,
			FILE_FLAG_SEQUENTIAL_SCAN,
			NULL);

		if (INVALID_HANDLE_VALUE == hFile)
		{
			ret = GetLastError();
			SetErrorCode(ret);
			return ret;
		}

		// Get handle to the crypto provider
		if (!CryptAcquireContext(&hProv,
			NULL,
			NULL,
			PROV_RSA_FULL,
			CRYPT_VERIFYCONTEXT))
		{
			ret = GetLastError();
			SetErrorCode(ret);
			return ret;
		}

		if (!CryptCreateHash(hProv, CALG_MD5, 0, 0, &hHash))
		{
			CryptReleaseContext(hProv, 0);
			ret = GetLastError();
			SetErrorCode(ret);
			return ret;
		}

		int64_t size = Utility::FS::get_file_size(localPath);
		if (0 > size)
		{
			CryptReleaseContext(hProv, 0);
			CryptDestroyHash(hHash);
			SetErrorAndMessage(RT_INVALID_PARAM, "get size for md5 failed");
			ret = RT_INVALID_PARAM;
			SetErrorCode(ret);
			return ret;
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
				SetErrorCode(ret);
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
				SetErrorCode(ret);
				return ret;
			}
		}

		cbHash = MD5LEN;
		if (!CryptGetHashParam(hHash, HP_HASHVAL, rgbHash, &cbHash, 0))
		{
			CryptReleaseContext(hProv, 0);
			CryptDestroyHash(hHash);
			ret = GetLastError();
			SetErrorCode(ret);
			return ret;
		}

		CHAR buf[MD5LEN+1] = {0};
		for (DWORD i = 0; i < cbHash; i++)
		{
#pragma warning(push)
#pragma warning(disable:4996)
			sprintf(&buf[2*i], "%c%c", rgbDigits[rgbHash[i] >> 4], rgbDigits[rgbHash[i] & 0xf]);
#pragma warning(pop)
		}
		signature.blockSignature = std::string(buf);

		CryptDestroyHash(hHash);
		CryptReleaseContext(hProv, 0);

		return ret;
	}

	virtual FileSignature getSignature(const std::wstring& localPath)
	{
		FileSignature signature;
		if (FileSignature::SHA1 == transTaskNode_->signature.algorithm)
		{
			signature.algorithm = FileSignature::SHA1;
			(void)getSignature(localPath, signature);
			return signature;
		}
		else if (FileSignature::MD5 == transTaskNode_->signature.algorithm)
		{
			signature.algorithm = FileSignature::MD5;
			if (RT_OK == getSignature(localPath, signature))
			{
				(void)getBlockMD5(localPath, signature);
			}
			return signature;
		}
		else
		{
			SetErrorAndMessage(RT_INVALID_PARAM, "invalid algorithm");
			return signature;
		}
	}

	virtual AsyncTransTaskNode& getTaskNode()
	{
		return *(transTaskNode_.get());
	}

	virtual CustomNotifyInfo& getCustomInfo()
	{
		return customNotifyInfo_;
	}

	virtual int64_t getTransLen() 
	{
		if (NULL != transmit_.get())
		{
			return transmit_->getTransLen();
		}
		return 0;
	}

	virtual void notifyCustomInfo()
	{
		SendCustomNotify();
	}
};

TransTask* TransTask::create(UserContext* userContext, std::shared_ptr<AsyncTransTaskNode> transTaskNode, CISSPNotifyPtr& notify)
{
	return static_cast<TransTask*>(new TransTaskImpl(userContext, transTaskNode, notify));
}

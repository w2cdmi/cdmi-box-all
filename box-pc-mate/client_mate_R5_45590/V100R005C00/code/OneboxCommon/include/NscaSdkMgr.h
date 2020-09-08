#ifndef __CLOUD__NSCA__SDK__H__
#define __CLOUD__NSCA__SDK__H__


#include <string>
#include <windows.h>



enum InputCallbackRet
{
	INPUT_RET_FAILED,
	INPUT_RET_SUCCESS_ONECE,
	INPUT_RET_SUCCESS_ALL
};

enum OutputCallbackRet
{
	OUTPUT_RET_FAILED,
	OUTPUT_RET_SUCCESS
};

typedef InputCallbackRet (*nsca_file_sync_input)(void* handle,size_t bufferSize,size_t* dataSize, unsigned char* dataBuffer);
typedef  OutputCallbackRet (*nsca_file_sync_output)(void* handle,size_t dataSize,const unsigned char* dataBuffer);
typedef void (*set_input_call_back)(nsca_file_sync_input inputCallback);
typedef void (*set_output_call_back)(nsca_file_sync_output outputCallback);
typedef bool (*nsca_file_sync_encrypt)(const char* file,void* outputHandle);
typedef bool (*nsca_file_sync_decrypt)(void* inputHandle, const char* outFile);
typedef bool (*nsca_file_sync_get_config_size)(const char* outFile,size_t& configSize);
typedef bool (*nsca_file_sync_get_fkey)(const char* filePath, unsigned char* keyBuffer, size_t& keySize, unsigned char* configBuffer, size_t& configSize);
typedef bool (*nsca_file_sync_encrypt_part)(const unsigned char* keyBuffer, size_t keySize, unsigned char* contentBuffer, size_t& contentsize);
typedef bool (*nsca_file_sync_encrypt_first_part)(const char* filePath, unsigned char* keyBuffer, size_t& keySize, unsigned char* contentBuffer, size_t& contentsize);

typedef bool (*nsca_file_sync_decrypt_first_part)(const char* filePath, unsigned char* keyBuffer, size_t& keySize, size_t& tailSize, unsigned char* contentBuffer, size_t& contentsize);
typedef bool (*nsca_file_sync_decrypt_part)(const unsigned char* keyBuffer, size_t keySize, unsigned char* contentBuffer, size_t& contentsize);
typedef bool (*nsca_file_sync_decrypt_last_part)(const unsigned char* keyBuffer, size_t keySize, unsigned char* configBuffer, size_t& configSize, unsigned char* contentBuffer, size_t& contentsize);


typedef bool (*nsca_file_sync_decrypt_file)(const char* inFile, const char* outFile);
typedef bool (*nsca_file_sync_encrypt_file)(const char* inFile, const char* outFile);
class ILabelHelper
{
public:
	virtual int getLabel(const char* pszPath) = 0;
	virtual int setLabel(const char* pszPath , const char* pszBigID , const char* pszSecLevel , unsigned int iLifeTime=0) = 0;
	virtual const char* getBigGuid() = 0;
	virtual const char* getLabelGUID() = 0;
	virtual~ILabelHelper(){}
	virtual int clearCodeLabel(const char* pszPath) = 0;
};

typedef ILabelHelper* (*nsca_label_helper_get_instance)();

enum PartFlag
{
	FIRST_PART,
	LAST_PART,
	COMMON_PART
};

class NscaSdkMgr
{
public:
	static NscaSdkMgr* instance();

	bool SyncSetWriteBack(nsca_file_sync_output outputCallback);
	bool SyncEnc(const char* filePath,void* handle);

	bool SyncGetkey(const char* filePath,std::string& key, std::string& config);

	size_t SyncGetConfigSize(const char* filePath);
	bool SyncEncFirstPart(const char* filePath, std::string& key,unsigned char* contentBuffer,size_t& contentSize);
	bool SyncEncPart(const std::string& key,unsigned char* contentBuffer,size_t& contentSize);
	bool SyncEncFile(const std::string inFile, const std::string outFile);
	bool SyncDecFile(const std::string inFile, const std::string outFile);

	bool SyncDecPart(const std::string& key, unsigned char* contentBuffer,size_t& contentSize);
	bool SyncDecFirstPart(const char* filePath, std::string& key,size_t& tailSize, unsigned char* contentBuffer,size_t& contentSize);
	bool SyncDecLastPart(const std::string& key,const std::string& config, unsigned char* contentBuffer,size_t& contentSize);

	bool isKia(const char* pszPath);
protected:
private:
	bool dllLoaded();

	
	NscaSdkMgr(void);
	~NscaSdkMgr(void);
private:
	static NscaSdkMgr* m_instance;
	HMODULE m_hDll;
};


#endif
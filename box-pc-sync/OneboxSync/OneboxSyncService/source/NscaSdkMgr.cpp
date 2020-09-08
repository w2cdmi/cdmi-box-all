#include "NscaSdkMgr.h"

NscaSdkMgr* NscaSdkMgr::m_instance = nullptr;

NscaSdkMgr::NscaSdkMgr(void):m_hDll(NULL)
{

}
NscaSdkMgr::~NscaSdkMgr(void)
{

}

NscaSdkMgr* NscaSdkMgr::instance()
{
	if (nullptr==m_instance)
	{
		m_instance = new NscaSdkMgr();
	}
	return m_instance;
}

bool NscaSdkMgr::dllLoaded()
{
	if (m_hDll!=NULL)
	{
		return true;
	}
#ifdef _DEBUG
	m_hDll = LoadLibraryW(L"NscaMiniLibD.dll");
#else
	m_hDll = LoadLibraryW(L"NscaMiniLib.dll");
#endif
	if (m_hDll==NULL){
		return false;
	}
	return true;
}

bool NscaSdkMgr::SyncSetWriteBack(nsca_file_sync_output outputCallback)
{
	if (!dllLoaded())
	{
		return false;
	}

	set_output_call_back p_set_output_callback = (set_output_call_back)GetProcAddress(m_hDll,"set_output_call_back");
	p_set_output_callback(outputCallback);

	return true;
}

bool NscaSdkMgr::SyncEnc(const char* filePath,void* handle)
{
	if (!dllLoaded())
	{
		return false;
	}
	/*std::string fileEncIn = "fileSyncEncIn.txt";
	set_input_call_back p_set_input_callback = (set_input_call_back)GetProcAddress(m_hDll,"set_input_call_back");
	set_output_call_back p_set_output_callback = (set_output_call_back)GetProcAddress(m_hDll,"set_output_call_back");
	p_set_output_callback(writeToStream);
	p_set_input_callback(readFromStream);
	nsca_file_sync_decrypt p_sync_dec = (nsca_file_sync_decrypt)GetProcAddress(m_hDll,"nsca_file_sync_decrypt");
	nsca_file_sync_encrypt p_sync_enc = (nsca_file_sync_encrypt)GetProcAddress(m_hDll,"nsca_file_sync_encrypt");*/

	nsca_file_sync_encrypt p_sync_enc = (nsca_file_sync_encrypt)GetProcAddress(m_hDll,"nsca_file_sync_encrypt");
	return p_sync_enc(filePath,handle);
}

bool NscaSdkMgr::SyncGetkey(const char* filePath,std::string& key, std::string& config)
{
	if (!dllLoaded())
	{
		return false;
	}

	nsca_file_sync_get_fkey p_sync_get_key = (nsca_file_sync_get_fkey)GetProcAddress(m_hDll,"nsca_file_sync_get_fkey");

	unsigned char keyBuffer[16];
	size_t keysize = 16;
	unsigned char configBuffer[1024];
	size_t configSize = 1024;
	p_sync_get_key(filePath,keyBuffer,keysize,configBuffer,configSize);
	std::string tempKey((char*)keyBuffer,keysize);
	std::string tempConfig((char*)configBuffer,configSize);
	key = tempKey;
	config = tempConfig;
	return true;
}

size_t NscaSdkMgr::SyncGetConfigSize(const char* filePath)
{
	size_t configSize = 0;
	if (!dllLoaded())
	{
		return configSize;
	}
	nsca_file_sync_get_config_size  p_sync_get_config_size =  (nsca_file_sync_get_config_size)GetProcAddress(m_hDll,"nsca_file_sync_get_config_size");
	p_sync_get_config_size(filePath,configSize);
	return configSize;
}

bool NscaSdkMgr::SyncEncFirstPart(const char* filePath, std::string& key,unsigned char* contentBuffer,size_t& contentSize)
{
	if (!dllLoaded())
	{
		return false;
	}

	unsigned char keyBuffer[64];
	size_t keysize = 64;
	nsca_file_sync_encrypt_first_part  p_sync_encrypt_first_part =  (nsca_file_sync_encrypt_first_part)GetProcAddress(m_hDll,"nsca_file_sync_encrypt_first_part");
	p_sync_encrypt_first_part(filePath,keyBuffer,keysize,contentBuffer,contentSize);
	std::string tempKey((char*)keyBuffer,keysize);
	key = tempKey;
	return true;
}

bool NscaSdkMgr::SyncEncPart(const std::string& key,unsigned char* contentBuffer,size_t& contentSize)
{
	if (!dllLoaded())
	{
		return false;
	}
	nsca_file_sync_encrypt_part p_sync_encrypt_part = (nsca_file_sync_encrypt_part)GetProcAddress(m_hDll,"nsca_file_sync_encrypt_part");
	p_sync_encrypt_part((const unsigned char*)key.c_str(),key.size(),contentBuffer,contentSize);
	return true;
}

bool NscaSdkMgr::SyncDecPart(const std::string& key, unsigned char* contentBuffer,size_t& contentSize)
{
	if (!dllLoaded())
	{
		return false;
	}
	nsca_file_sync_decrypt_part p_sync_decrypt_part = (nsca_file_sync_decrypt_part)GetProcAddress(m_hDll,"nsca_file_sync_decrypt_part");
	p_sync_decrypt_part((const unsigned char*)key.c_str(),key.size(),contentBuffer,contentSize);
	return true;
}

bool NscaSdkMgr::SyncEncFile(const std::string inFile, const std::string outFile)
{
	if (!dllLoaded())
	{
		return false;
	}
	nsca_file_sync_encrypt_file p_sync_encrypt_file = (nsca_file_sync_encrypt_file)GetProcAddress(m_hDll,"nsca_file_sync_encrypt_file");
	p_sync_encrypt_file(inFile.c_str(),outFile.c_str());
	return true;
}
bool NscaSdkMgr::SyncDecFile(const std::string inFile, const std::string outFile)
{
	if (!dllLoaded())
	{
		return false;
	}
	nsca_file_sync_decrypt_file p_sync_decrypt_file = (nsca_file_sync_decrypt_file)GetProcAddress(m_hDll,"nsca_file_sync_decrypt_file");
	p_sync_decrypt_file(inFile.c_str(),outFile.c_str());
	return true;
}
bool NscaSdkMgr::SyncDecFirstPart(const char* filePath, std::string& key,size_t& tailSize, unsigned char* contentBuffer,size_t& contentSize)
{	
	if (!dllLoaded())
	{
		return false;
	}

	unsigned char keyBuffer[64];
	size_t keysize = 64;

	nsca_file_sync_decrypt_first_part p_sync_decrypt_first_part = (nsca_file_sync_decrypt_first_part)GetProcAddress(m_hDll,"nsca_file_sync_decrypt_first_part");
	p_sync_decrypt_first_part(filePath,keyBuffer,keysize,tailSize,contentBuffer,contentSize);

	std::string tempKey((char*)keyBuffer,keysize);
	key = tempKey;

	return true;
}

bool NscaSdkMgr::SyncDecLastPart(const std::string& key,const std::string& config, unsigned char* contentBuffer,size_t& contentSize)
{	
	if (!dllLoaded())
	{
		return false;
	}
	return true;
}

//bool NscaSdkMgr::SyncGetkey(const char* filePath,unsigned char* keyBuffer,size_t& keySize,unsigned char* configBuffer,size_t configSize)
//{
//	return true;
//}
bool NscaSdkMgr::isKia(const char* pszPath)
{
	if (!dllLoaded())
	{
		return false;
	}
	bool iskia=false;
	nsca_label_helper_get_instance p_label_helper_fun = (nsca_label_helper_get_instance)GetProcAddress(m_hDll,"nsca_label_helper_get_instance");
	ILabelHelper* helperObj = p_label_helper_fun();
	int ret= helperObj->getLabel(pszPath);
	if (ret == 1)
	{
		iskia=true;
	}
	return  iskia;
}
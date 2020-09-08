#include "Aes.h"
#include <openssl/aes.h>
#include <sstream>

CAes::CAes(const std::string& strKey)
	:m_strKey(strKey)
{
}

CAes::~CAes(void)
{
}

std::string CAes::EncryptString(const std::string& strIn)
{	
	std::string strOut = "";
	size_t len = (strIn.size()/AES_BLOCK_SIZE+1)*AES_BLOCK_SIZE;
	size_t wrap_len = len-strIn.size();
	std::string strTmp = strIn;
	while (wrap_len>0)
	{
		strTmp += "\0";
		--wrap_len;
	}
	AES_KEY aeskey;
	AES_set_encrypt_key((unsigned char*)m_strKey.c_str(), AES_v256, &aeskey);
	for (size_t i = 0; i < len/AES_BLOCK_SIZE; ++i)
	{
		unsigned char szOut[AES_BLOCK_SIZE] = {0};
		AES_encrypt((unsigned char*)strTmp.substr(i*AES_BLOCK_SIZE, AES_BLOCK_SIZE).c_str(), szOut, &aeskey);
		strOut += std::string((char*)szOut, AES_BLOCK_SIZE);
	}

	return strOut;
}

std::string CAes::DecryptString(const std::string& strIn)
{
	std::string strOut = "";
	AES_KEY aeskey;
	AES_set_decrypt_key((unsigned char*)m_strKey.c_str(), AES_v256, &aeskey);
	for (size_t i = 0; i < strIn.size()/AES_BLOCK_SIZE; ++i)
	{
		unsigned char szOut[AES_BLOCK_SIZE] = {0};
		AES_decrypt((unsigned char*)strIn.substr(i*AES_BLOCK_SIZE, AES_BLOCK_SIZE).c_str(), szOut, &aeskey);
		strOut += std::string((char*)szOut, AES_BLOCK_SIZE);
	}

	return strOut;
}

std::string CAes::EncryptPassword(const std::string& strIn)
{
	std::string strTmp = EncryptString(strIn);
	size_t len = strTmp.size()*2+1;
	char* buf = new char[len];
	if (NULL == buf)
	{
		return "";
	}
	memset(buf, 0, len);
	for (size_t i = 0; i < strTmp.size(); ++i)
	{
		sprintf_s(buf+2*i, len-2*i, "%02X", (unsigned char)(strTmp[i]));
	}
	
	std::string strOut = std::string(buf, len);
	delete buf;

	return strOut;
}

std::string CAes::DecryptPassword(const std::string& strIn)
{
	size_t len = strIn.size()/2;
	unsigned char* buf = new unsigned char[len*2];
	if (NULL == buf)
	{
		return "";
	}
	memset(buf, 0, len*2);
	for (size_t i = 0; i < len; ++i)
	{
		sscanf_s(strIn.substr(i*2,2).c_str(), "%02X", &buf[i]);
	}

	std::string strOut = DecryptString(std::string((char*)buf, len));
	delete buf;

	return strOut;
}

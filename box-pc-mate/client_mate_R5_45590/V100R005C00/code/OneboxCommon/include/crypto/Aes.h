#ifndef _AES_ENCRYPT_H_
#define _AES_ENCRYPT_H_

#include <string>

#define AES_v128   128
#define AES_v192   192
#define AES_v256   256

#define AES_DEFAULT AES_v256

#ifdef __cplusplus
extern "C"
{
#endif

/* ��Ϊ���а�ȫ���� */
extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */

class CAes
{
public:
	CAes(const std::string& strKey);
	virtual ~CAes(void);

public:
	std::string EncryptString(const std::string& strIn);
	std::string DecryptString(const std::string& strIn);

	std::string EncryptPassword(const std::string& strIn);
	std::string DecryptPassword(const std::string& strIn);

private:
	std::string m_strKey;
};

#endif

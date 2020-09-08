#ifndef _CSHA1_H_
#define _CSHA1_H_

#include "sha1.h"
#include <string>
#include <fstream>

class CSha1
{
public:
	CSha1();
	virtual ~CSha1();

public:
	enum {MAX_BUF_SIZE = 1024};

public:
	bool Reset();
	bool Update(unsigned char ch);
	bool Update(unsigned char* buf, uint32_t len);
	bool Update(const char* str);
	bool Update(std::fstream& fs);
	bool Result();

	std::string ToString();

private:
	SHA1Context m_sha;
    uint8_t m_digest[SHA1HashSize];
};

#endif
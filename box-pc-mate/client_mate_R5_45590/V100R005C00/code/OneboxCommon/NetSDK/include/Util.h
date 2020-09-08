#ifndef __ONEBOX__UTIL__H__
#define __ONEBOX__UTIL__H__

#include "CommonValue.h"
#include <sstream>

std::string getDate(void);

// base64 encode bytes.  The output buffer must have at least
// ((4 * (inLen + 1)) / 3) bytes in it.  Returns the number of bytes written
// to [out].
int32_t base64Encode(const unsigned char *in, int32_t inLen, unsigned char *out);

std::string urlEncode(const std::string &strSrc);

std::string urlDecode(const std::string& src);

int32_t urlUtf8Encode(std::string &strDest, const std::string &strSrc);

int32_t urlUtf8Decode(std::string &strDest, const std::string& src);

void string2Buff(const std::string& src, DataBuffer& loginReqBuf);

#define atoll ::_atoi64

#endif  //end of __ONEBOX__H__

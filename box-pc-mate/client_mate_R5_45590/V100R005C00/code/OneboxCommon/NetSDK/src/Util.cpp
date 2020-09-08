/******************************************************************************
版权所有  : 2010-2020，华为赛门铁克科技有限公司
文 件 名  : CSUtil.cpp     
作    者  : 陈云松
版    本  : V1.0
创建日期  : 2011-12-24
描    述  : 工具函数源文件
函数列表  :

历史记录   :
1.日    期 : 2011-12-24
作    者   : 陈云松
修改内容   : 完成初稿

*******************************************************************************/
#include "Util.h"
#include "ErrorCode.h"
#include <assert.h>

#include <openssl/evp.h>
#include <openssl/hmac.h>
#include "Utility.h"

std::string getDate(void)
{
	std::string strTimeNow = "Thu, 03 Nov 2011 02:27:39 GMT";
	char chBuf[128] = {0};
	time_t now = time(NULL);
	//strftime( chBuf , 127 , "%a, %d %b %Y %H:%M:%S GMT" , gmtime( &now ) );
	tm tmNow;
	gmtime_s(&tmNow, &now);
	strftime( chBuf , 127 , "%a, %d %b %Y %H:%M:%S GMT", &tmNow);
	std::string strTime = std::string(chBuf);
	if (strTime.empty())
	{
		return strTimeNow;
	}
	return strTime;
}

int32_t base64Encode(const unsigned char *in, int32_t inLen, unsigned char *out)
{
    assert(in != NULL);
    assert(out != NULL);
    assert(inLen >= 0);

    if (NULL == in || NULL == out || inLen < 0)
    {
        return RT_INVALID_PARAM;
    }

    EVP_ENCODE_CTX ectx;
    int32_t outLen = 0;
    int32_t totalLen = 0;

    EVP_EncodeInit(&ectx);

    EVP_EncodeUpdate(&ectx, out, &outLen, in, inLen);
    totalLen += outLen;

    EVP_EncodeFinal(&ectx,out+totalLen,&outLen);
    totalLen += outLen;

    return totalLen;
}

std::string urlEncode(const std::string &strSrc)
{
	static const std::string strUrlSafe = "-_.!~*'()/";

	size_t len = strSrc.length();

	std::string strEncodeUrl;

	for (size_t i=0;i<len;i++)
	{
		unsigned char ucItem = (unsigned char)strSrc.at(i);
		if(isalnum(ucItem) || (strUrlSafe.find_first_of(ucItem) != std::string::npos))
		{
			char tmpbuf[2]={0};
			_snprintf_s(tmpbuf, sizeof(tmpbuf), sizeof(tmpbuf), "%c", ucItem);
			strEncodeUrl.append(tmpbuf);
		}
		else if (isspace(ucItem))
		{
			//strEncodeUrl.append("+");
			strEncodeUrl.append("%20"); // '+' should replace with '%20' (space)
		}
		else
		{
			char tmpbuf[4] = {0};
			_snprintf_s(tmpbuf, sizeof(tmpbuf), sizeof(tmpbuf), "%%%X%X", (ucItem>>4), (ucItem %16));
			strEncodeUrl.append(tmpbuf);
		}
	}

	return strEncodeUrl;
}

int32_t urlUtf8Encode(std::string &strDest, const std::string &strSrc)
{
	std::string strUtf8 = SD::Utility::String::wstring_to_utf8(SD::Utility::String::string_to_wstring(strSrc));
	strDest = urlEncode(strUtf8);
	return RT_OK;
}

int32_t urlUtf8Encode(char* szDest, const std::string &strSrc)
{
	std::string strUtf8 = SD::Utility::String::wstring_to_utf8(SD::Utility::String::string_to_wstring(strSrc));
	std::string strTmp = urlEncode(strUtf8);
	memcpy_s(szDest, strTmp.size(), strTmp.c_str(), strTmp.size());
	return RT_OK;
}

std::string urlDecode(const std::string& src)
{
	std::string dst;

	size_t srclen = src.size();

	for (size_t i = 0; i < srclen; i++)
	{
		if (src[i] == '%' && ((i+2) < srclen))
		{
			if(isxdigit(src[i + 1]) && isxdigit(src[i + 2]))
			{
				char c1 = src[++i];
				char c2 = src[++i];
				c1 = c1 - 48 - ((c1 >= 'A') ? 7 : 0) - ((c1 >= 'a') ? 32 : 0);
				c2 = c2 - 48 - ((c2 >= 'A') ? 7 : 0) - ((c2 >= 'a') ? 32 : 0);
				dst += (unsigned char)(c1 * 16 + c2);
			}
		}
		else
			if (src[i] == '+')
			{
				dst += ' ';
			}
			else
			{
				dst += src[i];
			}
	}
	return dst;
}

int32_t urlUtf8Decode(std::string &strDest, const std::string& src)
{
	std::string strUtf8 = urlDecode(src);
	strDest = SD::Utility::String::wstring_to_string(SD::Utility::String::utf8_to_wstring(strUtf8));
	return RT_OK;
}

void string2Buff(const std::string& src, DataBuffer& loginReqBuf)
{
	size_t lBufLen = src.size() + 1;
	loginReqBuf.pBuf = (unsigned char *)::malloc(lBufLen);
	if (NULL == loginReqBuf.pBuf)
	{
		return;
	}

	loginReqBuf.lBufLen = lBufLen;
	loginReqBuf.pFreeFunc = &::free;
	(void)memset_s(loginReqBuf.pBuf, lBufLen, 0, lBufLen);
	strcpy_s((char*)loginReqBuf.pBuf, lBufLen, src.c_str());
}

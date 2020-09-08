#ifndef _OUTLOOK_LANGUAGE_H_
#define _OUTLOOK_LANGUAGE_H_

#include "CommonDefine.h"
#include "InILanguage.h"

extern int32_t languageID_;

static inline std::wstring GetMessageFromIniFile(const std::wstring& msgId)
{
	if (languageID_ == INVALID_LANG_ID || msgId.empty())
	{
		return L"";
	}
	CInIHelper iniHelper(IniLanguageHelper(languageID_).GetLanguageFilePath());
	return iniHelper.GetString(LANGUAGE_COMMON_SECTION, msgId, L"");
}

static inline std::wstring GetNoticeInfoFromIniFile(std::wstring msgId,...)
{
	if (languageID_ == INVALID_LANG_ID || msgId.empty())
	{
		return L"";
	}
	CInIHelper iniHelper(IniLanguageHelper(languageID_).GetMsgLanguageFilePath());
	std::wstring strTemp =  iniHelper.GetString(MSG_DESC_SECTION, msgId, L"");
	if (L"" == strTemp)
	{
		return msgId;
	}

	TCHAR buffer[MAX_MSG_LENGTH] = {0};
	va_list args;
	va_start (args, msgId);
	(void)_vstprintf_s(buffer, strTemp.c_str(), args);
	va_end (args);
	return buffer;
}

static inline std::wstring getShortPath(std::wstring fullPath,int32_t length)
{
	std::wstring _return = L"";
	int32_t fullPathWidth = 0;
	for (size_t i=0; i< fullPath.length(); i++)
	{
		if((31<fullPath[i] && fullPath[i]<65) || 90<fullPath[i] && fullPath[i]<127)
		{
			fullPathWidth += 1;
		}
		else
		{
			fullPathWidth += 2;
		}

		if (fullPathWidth > length)
		{ 
			_return = fullPath.substr(0,length);
			break;
		}
	}

	return _return == L""? fullPath:_return+L"......";
}

#endif
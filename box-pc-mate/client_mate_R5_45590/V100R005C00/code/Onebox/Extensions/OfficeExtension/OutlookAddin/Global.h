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

	return CInIHelper(IniLanguageHelper(languageID_).GetLanguageFilePath()).GetString(LANGUAGE_COMMON_SECTION, msgId, L"");
}

#endif
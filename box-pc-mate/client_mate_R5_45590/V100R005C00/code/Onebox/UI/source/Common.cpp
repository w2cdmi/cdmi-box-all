#include "stdafxOnebox.h"
#include "Common.h"
#include "UserConfigure.h"

namespace Onebox
{
	bool thumbEnabled(const std::wstring& name)
	{
		if (name.empty())
		{
			return false;
		}
		std::wstring thumbEnabledStr = GetUserConfValue<std::wstring>(CONF_SETTINGS_SECTION, CONF_THUMB_NAME_EXT, DEFAULT_THUMB_NAME_EXT);
		if (thumbEnabledStr.empty())
		{
			return false;
		}
		std::wstring extName = SD::Utility::FS::get_extension_name(name);
		if (extName.empty())
		{
			return false;
		}
		extName = SD::Utility::String::to_lower(extName);
		thumbEnabledStr = SD::Utility::String::to_lower(thumbEnabledStr);
		return (thumbEnabledStr.find(extName) != std::wstring::npos);
	}

	bool dbClickEnabled(const std::wstring& name)
	{
		if (name.empty())
		{
			return false;
		}

		std::wstring dbClickEnabledStr = GetUserConfValue<std::wstring>(CONF_SETTINGS_SECTION, CONF_OPEN_NAME_EXT, DEFAULT_OPEN_NAME_EXT);
		if (dbClickEnabledStr.empty())
		{
			return false;
		}

		std::wstring extName = SD::Utility::FS::get_extension_name(name);
		extName = SD::Utility::String::to_lower(extName);
		dbClickEnabledStr = SD::Utility::String::to_lower(dbClickEnabledStr);

		if (extName.empty())
		{
			return false;
		}

		return (dbClickEnabledStr.find(extName) != std::wstring::npos);
	}
}
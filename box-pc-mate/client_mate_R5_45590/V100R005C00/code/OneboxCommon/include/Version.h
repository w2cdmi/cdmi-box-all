#ifndef _ONEBOX_VERSION_H_
#define _ONEBOX_VERSION_H_

#include <xstring>
#include <stdint.h>

#ifndef VERSION_CHECK_LESS_THAN
#define VERSION_CHECK_LESS_THAN(ver) if (ver < rhs.ver) return true; else if (ver > rhs.ver) return false;
#endif

class Version
{
public:
	Version(const std::wstring& version)
		:version_(version)
		,V_version_(0)
		,R_version_(0)
		,C_version_(0)
		,B_version_(0)
		,major_(0)
		,minor_(0)
	{
		(void)parseVersion();
	}

	~Version()
	{

	}

	bool operator==(const Version& rhs) const
	{
		return ((V_version_ == rhs.V_version_) && 
			(R_version_ == rhs.R_version_) && 
			(C_version_ == rhs.C_version_) && 
			(B_version_ == rhs.B_version_) && 
			(major_ == rhs.major_) && 
			(minor_ == rhs.minor_));
	}

	bool operator<(const Version& rhs) const
	{
		if (*this == rhs)
		{
			return false;
		}
		VERSION_CHECK_LESS_THAN(V_version_);
		VERSION_CHECK_LESS_THAN(R_version_);
		VERSION_CHECK_LESS_THAN(C_version_);
		VERSION_CHECK_LESS_THAN(B_version_);
		VERSION_CHECK_LESS_THAN(major_);
		VERSION_CHECK_LESS_THAN(minor_);
		return false;
	}

	bool operator>(const Version& rhs) const
	{
		if (*this == rhs)
		{
			return false;
		}
		else if (*this < rhs)
		{
			return false;
		}
		return true;
	}

	static bool isValid(const std::wstring& version)
	{
		if (version.empty())
		{
			return false;
		}
		// check valid
		for (std::wstring::size_type i = 0; i < version.size(); ++i)
		{
			const std::wstring::traits_type::char_type& c = version[i];
			if (c != L'.' && (c > L'9' || c < L'0'))
			{
				return false;
			}
		}
		return true;
	}

private:
	int32_t parseVersion()
	{
		try
		{
			if (!isValid(version_))
			{
				return -1;
			}		

			uint32_t verArray[5] = {0}, index = 0;
			std::wstring::size_type pos = 0, start = 0;
			while (true)
			{
				pos = version_.find(L'.', start);
				verArray[index++] = _wtoi(version_.substr(start, pos).c_str());
				if (std::wstring::npos == pos || index > 4)
				{
					break;
				}
				start = ++pos;				
			}

			V_version_ = verArray[0];
			R_version_ = verArray[1];
			C_version_ = verArray[2];

			// the max B_version is 99 and the max major is 99
			if (verArray[3] > 9999)
			{
				while ((verArray[3] = verArray[3]/10) > 9999);
			}
			B_version_ = verArray[3]/100;
			major_ = verArray[3]%100;

			minor_ = verArray[4];

			return 0;
		}
		catch(...)
		{
			return -1;
		}
		return 0;
	}

private:
	std::wstring version_; // 1.5.00.1328.1 (V1R5C00B013 28.1)
	uint32_t V_version_; // 1
	uint32_t R_version_; // 5
	uint32_t C_version_; // 00
	uint32_t B_version_; // 13
	uint32_t major_; // 28
	uint32_t minor_; // 1
};

#endif

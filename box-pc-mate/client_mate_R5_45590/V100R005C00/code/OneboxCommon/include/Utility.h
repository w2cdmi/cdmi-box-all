#ifndef _SD_UTILITY_H_
#define _SD_UTILITY_H_

#include <xstring>
#include <string>
#include <vector>
#include <stdint.h>
#ifdef ENABLE_UTILITY_ALL
	#ifndef ENABLE_UTILITY_FS
	#define ENABLE_UTILITY_FS
	#endif
	#ifndef ENABLE_UTILITY_ENCRYPT
	#define ENABLE_UTILITY_ENCRYPT
	#endif
	#ifndef ENABLE_UTILITY_MD5
	#define ENABLE_UTILITY_MD5
	#endif
	#ifndef ENABLE_UTILITY_SHA1
	#define ENABLE_UTILITY_SHA1
	#endif
	#ifndef ENABLE_UTILITY_STRING
	#define ENABLE_UTILITY_STRING
	#endif
	#ifndef ENABLE_UTILITY_REGISTRY
	#define ENABLE_UTILITY_REGISTRY
	#endif
#endif

#ifdef ENABLE_UTILITY_FS
	#ifndef ENABLE_UTILITY_STRING
	#define ENABLE_UTILITY_STRING
	#endif
#endif

#ifdef ENABLE_UTILITY_ENCRYPT
	#ifndef ENABLE_UTILITY_STRING
	#define ENABLE_UTILITY_STRING
	#endif
#endif

#ifdef ENABLE_UTILITY_MD5
	#ifndef ENABLE_UTILITY_STRING
	#define ENABLE_UTILITY_STRING
	#endif
#endif

#ifdef ENABLE_UTILITY_SHA1
	#ifndef ENABLE_UTILITY_STRING
	#define ENABLE_UTILITY_STRING
	#endif
#endif

#ifdef ENABLE_UTILITY_REGISTRY
	#include <windows.h>
	#include <memory>
#endif

#ifdef __cplusplus
extern "C"
{
#endif

/* 华为特有安全函数 */
extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */

#ifndef INVALID_TIME
#define INVALID_TIME int64_t(-1)
#endif

namespace SD
{
	namespace Utility
	{
#ifdef ENABLE_UTILITY_FS
		namespace FS
		{
			int32_t remove(const std::wstring& path);
			int32_t remove_all(const std::wstring& path);
			int32_t rename(const std::wstring& oldpath, const std::wstring& newpath);
			int32_t create_directory(const std::wstring& path);
			int32_t create_directories(const std::wstring& path);
			int32_t copy_file(const std::wstring& oldpath, const std::wstring& newpath);
			bool is_directory(const std::wstring& path);
			bool is_local_root(const std::wstring& path);
			bool is_invalidPath(const std::wstring& path);
			bool is_invalidLocalPath(const std::wstring& path);
			bool is_invalidCharPath(const std::wstring& path, const std::wstring& limitchar);
			bool is_exist(const std::wstring& path);
			int64_t get_file_size(const std::wstring& path);
			std::wstring get_file_name(const std::wstring& path);
			std::wstring get_extension_name(const std::wstring& path);
			std::wstring get_conflict_newname(const std::wstring& name);
			std::wstring get_parent_path(const std::wstring& path);
			std::wstring get_topdir_name(const std::wstring& path);
			std::wstring format_path(const std::wstring& path);
			std::string format_path(const std::string& path);
			std::wstring format_time(const time_t& time);
			int64_t get_file_createtime(const std::wstring& path);
			int64_t get_file_modifytime(const std::wstring& time);
			int64_t get_file_accesstime(const std::wstring& time);
			std::wstring get_current_sys_time();
			std::wstring get_work_directory();
			std::wstring get_system_directory();
			std::wstring get_system_user_app_path();
		}	// end namespace FS
#endif
#ifdef ENABLE_UTILITY_STRING
		namespace String
		{
			std::wstring replace_all(const std::wstring& src, const std::wstring& oldvalue, const std::wstring& newvalue);
			std::string replace_all(const std::string& src, const std::string& oldvalue, const std::string& newvalue);
			std::wstring string_to_wstring(const std::string& str);
			std::string wstring_to_string(const std::wstring& str);
			std::wstring utf8_to_wstring(const std::string& str);
			std::string wstring_to_gb2312(const std::wstring& str);
			std::string wstring_to_utf8(const std::wstring& str);
			std::wstring ltrim(const std::wstring& src, const std::wstring& value);
			std::wstring rtrim(const std::wstring& src, const std::wstring& value);
			std::string ltrim(const std::string& src, const std::string& value);
			std::string rtrim(const std::string& src, const std::string& value);
			std::wstring format_string(const wchar_t* format, ...);
			std::string format_string(const char* format, ...);
			std::wstring to_lower(const std::wstring& str);
			std::wstring to_upper(const std::wstring& str);
			std::string to_lower(const std::string& str);
			std::string to_upper(const std::string& str);
			std::wstring gen_uuid();
#ifdef ENABLE_UTILITY_ENCRYPT
			std::wstring encrypt_string(const std::wstring& str);
			std::wstring decrypt_string(const std::wstring& str);
#endif
			int32_t get_random_num(uint32_t min, uint32_t max);
			std::wstring create_random_string();
			std::wstring getSizeStr(int64_t size);
			void split(const std::wstring& str, std::vector<std::wstring>& vecInfo, const std::wstring& splitStr);
			void split(const std::string& str, std::vector<std::string>& vecInfo, const std::string& splitStr);

			template<typename T>
			struct str_conv_traits
			{
				typedef std::wstring result_type;
				typedef std::wstringstream conv_type;
			};

			template<>
			struct str_conv_traits<std::string>
			{
				typedef std::string result_type;
				typedef std::stringstream conv_type;
			};

			template<typename R, typename T>
			R type_to_string(const T& value, R)
			{
				typename str_conv_traits<R>::conv_type stream;
				stream << value;
				return stream.str();
			}

			template<typename R, typename T>
			R type_to_string(const T& value)
			{
				return type_to_string<R, T>(value, typename str_conv_traits<R>::result_type());
			}

			template<typename T>
			T string_to_type(const std::wstring& value)
			{
				typename str_conv_traits<std::wstring>::conv_type stream;
				stream << value;
				T result;
				stream >> result;
				return result;
			}

			template<typename T>
			T string_to_type(const std::string& value)
			{
				typename str_conv_traits<std::string>::conv_type stream;
				stream << value;
				T result;
				stream >> result;
				return result;
			}	
		}	// end namespace std::string
#endif
#ifdef ENABLE_UTILITY_MD5
		namespace MD5
		{
			std::wstring getMD5ByString(const char* szSource);
			std::wstring getMD5ByFile(const wchar_t* szSource);
		} // end namespace MD5
#endif
#ifdef ENABLE_UTILITY_SHA1
		namespace SHA1
		{
			std::wstring getSHA1ByString(const char* szSource);
			std::wstring getSHA1ByFile(const wchar_t* szSource);
		} // end namespace SHA1
#endif

		enum UtcType
		{
			Crt,
			Windows,
			Unix
		};
		enum LanguageType
		{
			CHINESE = 2052,
			ENGLISH = 2057,
			DEFAULT = -1
		};
		class DateTime
		{
		public:
			DateTime(int64_t ticks, UtcType type=Windows,LanguageType languageType=CHINESE)
				:windowsFileTime_(0L)
				,unixFileTime_(0l)
				,crtFileTime_(0L)
			{
				switch(type)
				{
				case Windows:
					windowsFileTime_ = ticks;
					crtFileTime_ = (windowsFileTime_-baseTicks_)/10000000;
					unixFileTime_ = (windowsFileTime_-baseTicks_)/10000;
					break;
				case Crt:
					crtFileTime_ = ticks;
					windowsFileTime_ = (crtFileTime_*10000000)+baseTicks_;
					unixFileTime_ = (windowsFileTime_-baseTicks_)/10000;
					break;
				case Unix:
					unixFileTime_ = ticks;
					windowsFileTime_ = (unixFileTime_*10000)+baseTicks_;
					crtFileTime_ = (windowsFileTime_-baseTicks_)/10000000;
				default:
					break;
				}
				languageType_ = languageType;
			}

			int64_t getWindowsFileTime()
			{
				return windowsFileTime_<0?0:windowsFileTime_;
			}

			int64_t getUnixFileTime()
			{
				return unixFileTime_<0?0:unixFileTime_;
			}

			int64_t getCrtFileTime()
			{
				return crtFileTime_<0?0:crtFileTime_;
			}

			// 2015/3/20 18:00:00
			std::wstring getTime();

			static std::wstring getTime(int64_t ticks, UtcType type=Windows, LanguageType languageType=CHINESE);

		private:
			static const int64_t baseTicks_ = 116444736000000000L;
			int64_t windowsFileTime_;
			int64_t unixFileTime_;
			int64_t crtFileTime_;
			uint32_t languageType_;
		};

#ifdef ENABLE_UTILITY_REGISTRY
		class Registry
		{
		public:
			template<typename T>
			static int32_t get(HKEY root, const std::wstring& key, const std::wstring& name, T& value)
			{
				LONG ret;
				HKEY hkey = NULL;
				DWORD cb = MAX_PATH;
				std::auto_ptr<BYTE> buf(new BYTE[cb]);
				(void)memset_s(buf.get(), cb, 0, cb);

				ret = RegOpenKeyEx(root , 
					(LPCWSTR)(key.c_str()),
					NULL,  
					KEY_QUERY_VALUE,
					&hkey);
				if (ERROR_SUCCESS != ret)
				{
					return ret;
				}

				ret = RegQueryValueEx(hkey,  
					(LPCWSTR)(name.c_str()),  
					NULL, 
					NULL,  
					(LPBYTE)buf.get(), 
					&cb);
				if (ERROR_MORE_DATA == ret)
				{
					buf.reset(new BYTE[cb]);
					ret = RegQueryValueEx(hkey,  
						(LPCWSTR)(name.c_str()),  
						NULL, 
						NULL,  
						(LPBYTE)buf.get(), 
						&cb);
				}
				if (ERROR_SUCCESS != ret)
				{
					RegCloseKey(hkey);
					return ret;
				}
				RegCloseKey(hkey);

				convert(value, buf);

				return 0;
			}

			template<typename T>
			static int32_t set(HKEY root, const std::wstring& key, const std::wstring& name, const T& default)
			{
				LONG ret;
				HKEY hkey = NULL;
				std::wstring value = String::type_to_string<std::wstring>(default);

				ret = RegCreateKeyEx(root , 
					(LPCWSTR)(key.c_str()),
					0, 
					NULL, 
					REG_OPTION_NON_VOLATILE, 
					KEY_SET_VALUE, 
					NULL, 
					&hkey, 
					NULL);
				if (ERROR_SUCCESS != ret)
				{
					return ret;
				}

				ret = RegSetValueEx(hkey,  
					(LPCWSTR)(name.c_str()),  
					0, 
					REG_SZ,  
					(LPBYTE)value.c_str(), 
					(value.length()+1)*sizeof(wchar_t));

				RegCloseKey(hkey);

				return ret;
			}

#if _WIN32_WINNT >= 0x0600
			static int32_t del(HKEY root, const std::wstring& key, const std::wstring& name)
			{
				if (key.empty())
				{
					return 0;
				}
				if (name.empty())
				{
					return RegDeleteTree(root, key.c_str());
				}

				LONG ret;
				HKEY hkey = NULL;
				ret = RegOpenKeyEx(root , 
					(LPCWSTR)(key.c_str()),
					NULL,  
					KEY_SET_VALUE,
					&hkey);
				if (ERROR_SUCCESS != ret)
				{
					return ret;
				}

				ret = RegDeleteValue(hkey, name.c_str());

				RegCloseKey(hkey);

				return ret;
			}
#endif

		private:
			template<typename T>
			static void convert(T& value, std::auto_ptr<BYTE>& buf)
			{
				try
				{
					std::wstring temp = L"";
					temp.append((wchar_t*)buf.get());
					std::wstringstream str;
					str << temp.c_str();
					str >> value;
				}
				catch (...) {}
			}

			template<>
			static void convert(std::wstring& value, std::auto_ptr<BYTE>& buf)
			{
				value.append((wchar_t*)buf.get());
			}

			template<>
			static void convert(int32_t& value, std::auto_ptr<BYTE>& buf)
			{
				value = *(int32_t*)buf.get();
			}
		};
#endif

	}	// end namespace Utility
}	// end namespac SD

#endif

#ifndef _SD_UTILITY_H_
#define _SD_UTILITY_H_

#include <xstring>
#include <string>
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
			bool is_exist(const std::wstring& path);
			int64_t get_file_size(const std::wstring& path);
			std::wstring get_file_name(const std::wstring& path);
			std::wstring get_extension_name(const std::wstring& path);
			std::wstring get_conflict_newname(const std::wstring& name);
			std::wstring get_parent_name(const std::wstring& path);
			std::wstring get_topdir_name(const std::wstring& path);
			std::wstring format_path(const std::wstring& path);
			std::string format_path(const std::string& path);
			std::wstring format_time(const time_t& time);
			int64_t get_file_createtime(const std::wstring& path);
			int64_t get_file_modifytime(const std::wstring& time);
			int64_t get_file_accesstime(const std::wstring& time);
			std::wstring get_current_sys_time();
			std::wstring get_work_directory();
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
		class DateTime
		{
		public:
			DateTime(int64_t ticks, UtcType type=Windows)
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
			}

			int64_t getWindowsFileTime()
			{
				return windowsFileTime_;
			}

			int64_t getUnixFileTime()
			{
				return unixFileTime_;
			}

			int64_t getCrtFileTime()
			{
				return crtFileTime_;
			}

		private:
			static const int64_t baseTicks_ = 116444736000000000L;
			int64_t windowsFileTime_;
			int64_t unixFileTime_;
			int64_t crtFileTime_;
		};

#ifdef ENABLE_UTILITY_REGISTRY
		class Registry
		{
		public:
			template<typename T>
			static int32_t get(HKEY root, const std::wstring& key, const std::wstring& name, T& value)
			{
				LONG ret;
				HKEY hkey;
				DWORD cb = MAX_PATH;
				std::auto_ptr<BYTE> buf(new BYTE[cb]);
				memset(buf.get(), 0, cb);

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

				return RT_OK;
			}

			template<typename T>
			static int32_t set(HKEY root, const std::wstring& key, const std::wstring& name, const T& default)
			{
				LONG ret;
				HKEY hkey;
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
			template<typename T>
			static int32_t setDword(HKEY root, const std::wstring& key, const std::wstring& name, const T& default)
			{
				LONG ret;
				HKEY hkey;

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
					REG_DWORD,  
					(LPBYTE)(&default), 
					sizeof(DWORD));
				RegCloseKey(hkey);
				return ret;
			}
		private:
			template<typename T>
			static void convert(T& value, std::auto_ptr<BYTE>& buf)
			{
				std::wstring temp = L"";
				temp.append((wchar_t*)buf.get());
				std::wstringstream str;
				str << temp.c_str();
				str >> value;
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

#ifndef _NSCA_DEVICE_INFO_H_
#define _NSCA_DEVICE_INFO_H_
#include "../base.h"

//所有的属性均使用UTF8编码
/*!
	设备名称，即windows，linux等主机的hostname。
*/
#define NSCA_DEVICE_PROP_HOST_NAME "nsca_device_name"

/*!
	设备唯一ID。一般不重复。但全局范围内不排除设备id重复的情况
*/
#define NSCA_DEVICE_PROP_DEVICE_ID "nsca_device_id"

/*!
	设备历史ID。获取设备唯一ID的算法有可能发生改变。
	当算法发生改变后，通过此key可以获取之前最多2个版本算法对应的历史设备ID。
	不同ID之间使用;分割
*/
#define NSCA_DEVICE_PROP_HISTORY_DEVICE_ID "nsca_history_device_id"

/*!
	返回设备OS类型，分为：WINDOWS,IOS，ANDROID,LINUX等
*/
#define NSCA_DEVICE_PROP_OS	"nsca_device_os"

/*!
	返回设备OS版本，格式为：OS类型.主版本.次版本.CPU类型。
	OS类型：windows,ios，android,linux等
	主版本：数字
	次版本：如sp0，sp1，sp2等
	CPU类型：x86,x64,arm等
*/
#define NSCA_DEVICE_PROP_OS_VERSION	"nsca_device_os_version"

/*!
	判断是否安装了spes软件。
*/
#define NSCA_DEVICE_PROP_IS_SPES_INSTALLED	"nsca_device_is_spes_installed"
extern "C"
{

	/*!
		获取设备相关的信息
		@param key 属性名
		@param buffer [out] 属性值接受缓存。属性值为以\0结尾的字符串。
		@param bufferSize [in] 缓存大小，应包括\0的空间。			
		@param requiredBufferSize 需要的换成大小，包括接受\0字符在内需要的缓存大小。
		@return true 成功。false 失败。如果返回false，但是requiredBufferSize 小于等于 bufferSize，说明不是因为缓存长度不够，是其它错误。
	*/
	bool NSCA_COMPONENT_API nsca_get_device_info(const char* key,char* buffer,size_t bufferSize,size_t& requiredBufferSize);
};

#endif
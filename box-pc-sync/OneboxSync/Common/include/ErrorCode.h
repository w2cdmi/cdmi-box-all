#ifndef _ONEBOX_ERROR_CODE_H_
#define _ONEBOX_ERROR_CODE_H_

#define RT_OK (0)
#define RT_ERROR (-1)
#define RT_CONTINUE (-3)
#define RT_NOT_IMPLEMENT (-4)
#define RT_PART_FAILED (-5)
#define RT_INVALID_PARAM (-1000)
#define RT_INVALID_DEVICE (-9999)

//action return code 
#define RT_CANCEL -100
#define RT_READY -101
#define RT_RUNNING -102

//文件访问相关的错误信息定义
#define RT_FILE_OPEN_ERROR          (-9000)   //文件Open出错
#define RT_FILE_SEEK_ERROR          (-9001)   //文件Seek出错
#define RT_FILE_READ_ERROR          (-9002)   //文件Read出错  
#define RT_FILE_WRITE_ERROR         (-9003)   //文件Write出错  
#define RT_FILE_CLOSE_ERROR         (-9004)   //文件Close出错  
#define RT_FILE_REMOVE_ERROR        (-9005)   //文件Remove出错 
#define RT_FILE_COPY_ERROR          (-9006)   //文件Copy出错 
#define RT_FILE_PATH_ERROR          (-9007)   //文件Path出错
#define RT_FILE_EXIST_ERROR         (-9008)   //文件已经存在
#define RT_FILE_NOEXIST_ERROR       (-9009)   //文件不存在
#define RT_FILE_DATA_ERROR          (-9010)   //文件内容错误
#define RT_FILE_ZERO_ERROR          (-9011)   //文件长度为0
#define RT_FILE_STAT_ERROR          (-9012)   //文件stat失败
#define RT_FILE_OFFSET_ERROR        (-9013)   //文件偏移位置错误
#define RT_FOLDER_ACCESS_ERROR      (-9014)   //该文件夹无访问权限
#define RT_FOLDER_SPACE_ERROR       (-9015)   //指定文件夹的空间不够
#define RT_FILE_SRC_ERROR           (-9016)   //源文件不存在
#define RT_FILE_DST_ERROR           (-9017)   //目标文件不存在
#define RT_FILE_RENAME_ERROR        (-9018)   //文件重命名失败
#define RT_PATH_CREATE_ERROR        (-9019)   //路径创建失败
#define RT_FILE_FILTER              (-9020)   //根据策略过滤掉文件
#define RT_PARENT_NOEXIST_ERROR     (-9021)   //父目录不存在
#define RT_DEVICE_NOEXIST_ERROR     (-9022)   //该盘符不存在

#define RT_FILE_CREATE_ERROR        (-9100)	  //WIN32打开文件失败
#define RT_FILE_MAP_ERROR           (-9101)	  //WIN32文件镜像失败
#define RT_FILE_VIEW_ERROR          (-9102)	  //WIN32文件映射失败
#define RT_WINDLL_LOAD_ERROR        (-9103)   //WIN32平台加载DLL出错(适用于Windows平台CPU利用率获取函数)
#define RT_WINDLLFUN_CALL_ERROR     (-9104)   //WIN32平台调用DLL函数出错(适用于Windows平台CPU利用率获取函数)

#define RT_OPERATOR_NEW_ERROR       (-9200)   //new操作符出错
#define RT_MEMORY_MALLOC_ERROR      (-9201)   //内存分配出错

//SQLite数据库异常错误
#define RT_SQLITE_ERROR			(-9300)
#define RT_SQLITE_NOEXIST		(-9301)
#define RT_SQLITE_EXIST			(-9302)

//差异处理错误
#define RT_DIFF_INVALIDPATH		(-9400)		//路径无效
#define RT_DIFF_CONFILCTPATH	(-9401)		//路径冲突
#define RT_DIFF_NOSYNCRULE		(-9402)		//无冲突规则
#define RT_DIFF_FILTER			(-9403)		//特殊字符过滤
#define RT_DIFF_MAXPATH			(-9404)		//超长路径
#define RT_DIFF_KIA				(-9405)		//KIA文件
#define RT_DIFF_HIDDEN			(-9406)		//隐藏文件
#define RT_DIFF_BIGFILE			(-9407)		//过大文件

#define FAILED_TO_INITIALIZE_REQUEST (RT_INVALID_PARAM-1)
#define FAILED_TO_BUILDJSON (RT_INVALID_PARAM-2)
#define FAILED_TO_PARSEJSON (RT_INVALID_PARAM-3)
#define TOKEN_EXPIRED (RT_INVALID_PARAM-4)
#define FILE_CREATED (RT_INVALID_PARAM-5)

// HTTP 状态码定义区
#define HTTP_BAD_REQUEST (RT_INVALID_PARAM-400)
#define HTTP_UNAUTHORIZED (RT_INVALID_PARAM-401)
#define HTTP_FORBIDDEN (RT_INVALID_PARAM-403)
#define HTTP_NOT_FOUND (RT_INVALID_PARAM-404)
#define HTTP_NOT_ALLOWD (RT_INVALID_PARAM-405)
#define HTTP_CONFLICT (RT_INVALID_PARAM-409)
#define HTTP_PRECONDITION_FAILED (RT_INVALID_PARAM-412)
#define HTTP_EXCEPTATION_FAILED (RT_INVALID_PARAM-417)
#define HTTP_LOCKED (RT_INVALID_PARAM-423)
#define HTTP_INTERNAL_ERROR (RT_INVALID_PARAM-500)
#define HTTP_SERVICE_UNVAILABLE (RT_INVALID_PARAM-503)
#define HTTP_INSUFFICIENT_STORAGE (RT_INVALID_PARAM-507)

// CURL 错误码定义区
#define CURL_ERROR_CODE (-2000)

#endif

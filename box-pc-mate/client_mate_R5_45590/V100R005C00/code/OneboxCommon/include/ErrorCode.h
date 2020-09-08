#ifndef _ERROR_CODE_H_
#define _ERROR_CODE_H_

#define RT_OK (0)
#define RT_ERROR (-1)
#define RT_CONTINUE (-3)
#define RT_NOT_IMPLEMENT (-4)
#define RT_ERRORCOMPLETE (-99)
#define RT_INVALID_PARAM (-1000)
#define RT_INVALID_DEVICE (-9999)

//action return code 
#define RT_CANCEL (-100)
#define RT_READY (-101)
#define RT_RUNNING (-102)
#define RT_RETRY (-103)

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
#define RT_DIFF_INVALID_FILE_NAME (-9408)	//文件名不合法

#define RT_IS_SYNCPATH			(-9410)		//该文件夹是同步文件夹

//异步任务冲突
#define RT_RESTTASK_DOING	(-9450)			//正在执行其他异步任务

//400，404中的错误码再分派解析
#define RT_INVD_PARAMETER	 (-9501) // InvalidParameter	错误请求参数	400
#define RT_INVD_PART	     (-9502) // InvalidPart	无效分片	400
#define RT_INVD_PANGE        (-9503) // InvalidRange	无效范围	400
#define RT_INVD_TEAMROLE     (-9504) // InvalidTeamRole	无效的团队角色	400
#define RT_INVD_REGION       (-9505) // InvalidRegion	无效的存储区域	400
#define RT_INVD_PMNROLE      (-9506) // InvalidPermissionRole	无效的权限角色	400
#define RT_INVD_FILETYPE     (-9507) // InvalidFileType	无效的文件类型	400
#define RT_UMATH_URL         (-9508) // UnmatchedDownloadUrl	不匹配的预下载URL	400

//Unauthorized	鉴权失败	401
//ClientUnauthorized	终端被禁用	401
//Forbidden	用户权限不足，禁止操作	403
//UserLocked	用户被锁定	403
//InvalidSpaceStatus	用户空间或者团队空间停用	403
//SourceForbidden	源文件或者文件夹无相关权限	403
//DestForbidden	目标文件或者文件夹无相关权限	403
//ScannedForbidden	文件经过安全扫描属于禁止下载的文件	403
//DynamicMailForbidden	外链需要进行邮件动态提取码的校验	403
//DynamicPhoneForbidden	外链需要进行手机动态提取码的校验	403
//NoSuchApplication	应用不存在	403

#define RT_NOSUCHUSER          (-9511) //NoSuchUser	该用户不存在	404
#define RT_NOSUCHITEM          (-9512) //NoSuchItem	访问节点不存在	404
#define RT_NOSUCHFOLDER        (-9513) //NoSuchFolder	该文件夹不存在	404
#define RT_NOSUCHFILE          (-9514) //NoSuchFile	该文件不存在	404
#define RT_NOSUCHVERSION       (-9515) //NoSuchVersion	该版本不存在	404
#define RT_NOSUCHTOKEN         (-9516) //NoSuchToken	Token不存在	404
#define RT_NOSUCHLINK          (-9517) //NoSuchLink	链接不存在	404
#define RT_NOSUCHSHARE         (-9518) //NoSuchShare	共享不存在	404
#define RT_NOSUCHREGION        (-9519) //NoSuchRegion	存储区域不存在	404
#define RT_NOSUCHPARENT        (-9520) //NoSuchParent	父文件夹不存在	404
#define RT_NOSUCHAPP           (-9521) //NoSuchApplication	找不到匹配的应用	404
#define RT_NOSUCHROLE          (-9522) //NoSuchRole	找不到权限角色	404
#define RT_LINK_NOTEFF         (-9523) //LinkNotEffective	链接未生效	404
#define RT_LINK_EXPIRED        (-9524) //LinkExpired	链接已过期	404
#define RT_NOSUCHSOURCE        (-9525) //NoSuchSource	原文件或文件夹不存在	404
#define RT_NOSUCHDEST          (-9526) //NoSuchDest	目标文件夹不存在	404
#define RT_NOSUCHTHUMBNAIL     (-9527) //NoThumbnail	没有缩略图	404
#define RT_NOSUCHOPTION        (-9528) //NoSuchOption	没有该选项	404
#define RT_NOSUCHENTERPRISE    (-9529) //NoSuchEnterprise	企业不存在	404
#define RT_ABNORMALTEAMSTATUS  (-9530) //AbnormalTeamStatus	团队空间处于非正常状态	404
#define RT_NOSUCHGROUP         (-9531) //NoSuchGroup	群组不存在	404
#define RT_NOSUCHMEMBER        (-9532) //NoSuchMember	成员不存在	404
#define RT_ABNORMALGROUPSTATUS (-9533) //AbnormalGroupStatus	群组处于非启用状态	404
#define RT_NOSUCHTEAMSPACE     (-9534) //NoSuchTeamspace	团队空间不存在	404
#define RT_NOSUCHACL           (-9535) //NoSuchACL	访问控制不存在	404
#define RT_NOHECTNOTFOUND      (-9536) //ObjectNotFound	对象不存在	404
#define RT_NOSUCHCLIENT        (-9537) //NoSuchClient	客户端不存在不存在	404
#define RT_NOLOGINUSER         (-9538) //NoLoginUser	该用户不存在	404

//MethodNotAllowed	该请求不允许	405
//InvalidProtocol	无效的HTTP协议	405
//InvalidLicense	License无效	405
//Conflict	资源已经存在	409
//ConflictUser	用户冲突	409
//ConflictDomain	企业域名冲突	409
//ConflictEmail	邮箱地址冲突	409
//RepeatNameConflict	重名冲突	409
//SubFolderConflict	目标文件夹是源文件夹的子文件夹	409
//SameParentConflict	目标文件夹是源文件夹的父文件夹	409
//LinkExistedConflict	外链已经存在	409
//ExistMemberConflict	团队空间成员已经存在	409
//ExistTeamspaceConflict	团队空间已经存在	409
//AsyncNodesConflict	异步任务执行遇到冲突	409
//ExceedQuota	空间配额已经达到阈值	409
//ExceedMaxLinkNum	外链数量超过了系统限制	412
//FileScanning	文件正在进行安全扫描	412
//TooManyRequests	请求过多	412
//EmailChangeConflict	不能对邮箱属性进行修改	412
//PreconditionFailed	该请求的预置条件失败	412
//ExceedUserMaxNodeNum	用户空间文件数已经达到系统同步限额	412
//ExceedMaxMembers	成员数超过最大限制	412
//FileConverting	office文件正在进行预览转换	412
//FileConvertNotSupport	文件当前不支持office转换	412
//FileConvertFailed	office文件转换失败	412
//InternalServerError	服务器内部错误	500
//FSException	文件系统异常	500
//InsufficientStorage	服务器无法存储完成请求所必须的内容	507

#define FAILED_TO_INITIALIZE_REQUEST	(RT_INVALID_PARAM-1)
#define FAILED_TO_BUILDJSON				(RT_INVALID_PARAM-2)
#define FAILED_TO_PARSEJSON				(RT_INVALID_PARAM-3)
#define TOKEN_EXPIRED					(RT_INVALID_PARAM-4)
#define FILE_CREATED					(RT_INVALID_PARAM-5)
#define SECURITY_MATRIX_FORBIDDEN		(RT_INVALID_PARAM-6)
#define ACCOUNT_DISABLE					(RT_INVALID_PARAM-7)

#define EXCEED_MAX_LINK_NUM				(RT_INVALID_PARAM-8)
#define FILE_SCANNING					(RT_INVALID_PARAM-9)
#define TOO_MANY_REQUESTS				(RT_INVALID_PARAM-10)
#define EMAIL_CHANGE_CONFLICT			(RT_INVALID_PARAM-11)
#define EXCEED_USER_MAX_NODE_NUM		(RT_INVALID_PARAM-12)
#define EXCEED_MAX_MEMBERS				(RT_INVALID_PARAM-13)
#define FILE_CONVERTING					(RT_INVALID_PARAM-14)
#define FILE_CONVERT_NOT_SUPPORT		(RT_INVALID_PARAM-15)
#define FILE_CONVERT_FAILED				(RT_INVALID_PARAM-16)
#define CHANGE_PASSWORD 				(RT_INVALID_PARAM-17)
#define LOCAL_NETWORK_INFO_GET_ERROR    (RT_INVALID_PARAM-18)

// HTTP 状态码定义区
#define HTTP_BAD_REQUEST			(RT_INVALID_PARAM-400)
#define HTTP_UNAUTHORIZED			(RT_INVALID_PARAM-401)
#define HTTP_FORBIDDEN				(RT_INVALID_PARAM-403)
#define HTTP_NOT_FOUND				(RT_INVALID_PARAM-404)
#define HTTP_NOT_ALLOWD				(RT_INVALID_PARAM-405)
#define HTTP_CONFLICT				(RT_INVALID_PARAM-409)
#define HTTP_PRECONDITION_FAILED	(RT_INVALID_PARAM-412)
#define HTTP_EXCEPTATION_FAILED		(RT_INVALID_PARAM-417)
#define HTTP_LOCKED					(RT_INVALID_PARAM-423)
#define HTTP_INTERNAL_ERROR			(RT_INVALID_PARAM-500)
#define HTTP_PROXY_ERROR			(RT_INVALID_PARAM-502)
#define HTTP_SERVICE_UNVAILABLE		(RT_INVALID_PARAM-503)
#define HTTP_INSUFFICIENT_STORAGE	(RT_INVALID_PARAM-507)
 
// CURL 错误码定义区
#define CURL_ERROR_CODE (-2000)
#define CURL_RESOLVE_PROXY_FAILED (CURL_ERROR_CODE-5)
#define CURL_RESOLVE_HOST_FAILED  (CURL_ERROR_CODE-6)
#define CURL_CONNECT_FAILED       (CURL_ERROR_CODE-7)
#define CURL_TIMEDOUT             (CURL_ERROR_CODE-28)

#endif

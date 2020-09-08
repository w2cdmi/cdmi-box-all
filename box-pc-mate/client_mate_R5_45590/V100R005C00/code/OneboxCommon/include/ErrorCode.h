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

//�ļ�������صĴ�����Ϣ����
#define RT_FILE_OPEN_ERROR          (-9000)   //�ļ�Open����
#define RT_FILE_SEEK_ERROR          (-9001)   //�ļ�Seek����
#define RT_FILE_READ_ERROR          (-9002)   //�ļ�Read����  
#define RT_FILE_WRITE_ERROR         (-9003)   //�ļ�Write����  
#define RT_FILE_CLOSE_ERROR         (-9004)   //�ļ�Close����  
#define RT_FILE_REMOVE_ERROR        (-9005)   //�ļ�Remove���� 
#define RT_FILE_COPY_ERROR          (-9006)   //�ļ�Copy���� 
#define RT_FILE_PATH_ERROR          (-9007)   //�ļ�Path����
#define RT_FILE_EXIST_ERROR         (-9008)   //�ļ��Ѿ�����
#define RT_FILE_NOEXIST_ERROR       (-9009)   //�ļ�������
#define RT_FILE_DATA_ERROR          (-9010)   //�ļ����ݴ���
#define RT_FILE_ZERO_ERROR          (-9011)   //�ļ�����Ϊ0
#define RT_FILE_STAT_ERROR          (-9012)   //�ļ�statʧ��
#define RT_FILE_OFFSET_ERROR        (-9013)   //�ļ�ƫ��λ�ô���
#define RT_FOLDER_ACCESS_ERROR      (-9014)   //���ļ����޷���Ȩ��
#define RT_FOLDER_SPACE_ERROR       (-9015)   //ָ���ļ��еĿռ䲻��
#define RT_FILE_SRC_ERROR           (-9016)   //Դ�ļ�������
#define RT_FILE_DST_ERROR           (-9017)   //Ŀ���ļ�������
#define RT_FILE_RENAME_ERROR        (-9018)   //�ļ�������ʧ��
#define RT_PATH_CREATE_ERROR        (-9019)   //·������ʧ��
#define RT_FILE_FILTER              (-9020)   //���ݲ��Թ��˵��ļ�
#define RT_PARENT_NOEXIST_ERROR     (-9021)   //��Ŀ¼������
#define RT_DEVICE_NOEXIST_ERROR     (-9022)   //���̷�������

#define RT_FILE_CREATE_ERROR        (-9100)	  //WIN32���ļ�ʧ��
#define RT_FILE_MAP_ERROR           (-9101)	  //WIN32�ļ�����ʧ��
#define RT_FILE_VIEW_ERROR          (-9102)	  //WIN32�ļ�ӳ��ʧ��
#define RT_WINDLL_LOAD_ERROR        (-9103)   //WIN32ƽ̨����DLL����(������Windowsƽ̨CPU�����ʻ�ȡ����)
#define RT_WINDLLFUN_CALL_ERROR     (-9104)   //WIN32ƽ̨����DLL��������(������Windowsƽ̨CPU�����ʻ�ȡ����)

#define RT_OPERATOR_NEW_ERROR       (-9200)   //new����������
#define RT_MEMORY_MALLOC_ERROR      (-9201)   //�ڴ�������

//SQLite���ݿ��쳣����
#define RT_SQLITE_ERROR			(-9300)
#define RT_SQLITE_NOEXIST		(-9301)
#define RT_SQLITE_EXIST			(-9302)

//���촦�����
#define RT_DIFF_INVALIDPATH		(-9400)		//·����Ч
#define RT_DIFF_CONFILCTPATH	(-9401)		//·����ͻ
#define RT_DIFF_NOSYNCRULE		(-9402)		//�޳�ͻ����
#define RT_DIFF_FILTER			(-9403)		//�����ַ�����
#define RT_DIFF_MAXPATH			(-9404)		//����·��
#define RT_DIFF_KIA				(-9405)		//KIA�ļ�
#define RT_DIFF_HIDDEN			(-9406)		//�����ļ�
#define RT_DIFF_BIGFILE			(-9407)		//�����ļ�
#define RT_DIFF_INVALID_FILE_NAME (-9408)	//�ļ������Ϸ�

#define RT_IS_SYNCPATH			(-9410)		//���ļ�����ͬ���ļ���

//�첽�����ͻ
#define RT_RESTTASK_DOING	(-9450)			//����ִ�������첽����

//400��404�еĴ������ٷ��ɽ���
#define RT_INVD_PARAMETER	 (-9501) // InvalidParameter	�����������	400
#define RT_INVD_PART	     (-9502) // InvalidPart	��Ч��Ƭ	400
#define RT_INVD_PANGE        (-9503) // InvalidRange	��Ч��Χ	400
#define RT_INVD_TEAMROLE     (-9504) // InvalidTeamRole	��Ч���Ŷӽ�ɫ	400
#define RT_INVD_REGION       (-9505) // InvalidRegion	��Ч�Ĵ洢����	400
#define RT_INVD_PMNROLE      (-9506) // InvalidPermissionRole	��Ч��Ȩ�޽�ɫ	400
#define RT_INVD_FILETYPE     (-9507) // InvalidFileType	��Ч���ļ�����	400
#define RT_UMATH_URL         (-9508) // UnmatchedDownloadUrl	��ƥ���Ԥ����URL	400

//Unauthorized	��Ȩʧ��	401
//ClientUnauthorized	�ն˱�����	401
//Forbidden	�û�Ȩ�޲��㣬��ֹ����	403
//UserLocked	�û�������	403
//InvalidSpaceStatus	�û��ռ�����Ŷӿռ�ͣ��	403
//SourceForbidden	Դ�ļ������ļ��������Ȩ��	403
//DestForbidden	Ŀ���ļ������ļ��������Ȩ��	403
//ScannedForbidden	�ļ�������ȫɨ�����ڽ�ֹ���ص��ļ�	403
//DynamicMailForbidden	������Ҫ�����ʼ���̬��ȡ���У��	403
//DynamicPhoneForbidden	������Ҫ�����ֻ���̬��ȡ���У��	403
//NoSuchApplication	Ӧ�ò�����	403

#define RT_NOSUCHUSER          (-9511) //NoSuchUser	���û�������	404
#define RT_NOSUCHITEM          (-9512) //NoSuchItem	���ʽڵ㲻����	404
#define RT_NOSUCHFOLDER        (-9513) //NoSuchFolder	���ļ��в�����	404
#define RT_NOSUCHFILE          (-9514) //NoSuchFile	���ļ�������	404
#define RT_NOSUCHVERSION       (-9515) //NoSuchVersion	�ð汾������	404
#define RT_NOSUCHTOKEN         (-9516) //NoSuchToken	Token������	404
#define RT_NOSUCHLINK          (-9517) //NoSuchLink	���Ӳ�����	404
#define RT_NOSUCHSHARE         (-9518) //NoSuchShare	��������	404
#define RT_NOSUCHREGION        (-9519) //NoSuchRegion	�洢���򲻴���	404
#define RT_NOSUCHPARENT        (-9520) //NoSuchParent	���ļ��в�����	404
#define RT_NOSUCHAPP           (-9521) //NoSuchApplication	�Ҳ���ƥ���Ӧ��	404
#define RT_NOSUCHROLE          (-9522) //NoSuchRole	�Ҳ���Ȩ�޽�ɫ	404
#define RT_LINK_NOTEFF         (-9523) //LinkNotEffective	����δ��Ч	404
#define RT_LINK_EXPIRED        (-9524) //LinkExpired	�����ѹ���	404
#define RT_NOSUCHSOURCE        (-9525) //NoSuchSource	ԭ�ļ����ļ��в�����	404
#define RT_NOSUCHDEST          (-9526) //NoSuchDest	Ŀ���ļ��в�����	404
#define RT_NOSUCHTHUMBNAIL     (-9527) //NoThumbnail	û������ͼ	404
#define RT_NOSUCHOPTION        (-9528) //NoSuchOption	û�и�ѡ��	404
#define RT_NOSUCHENTERPRISE    (-9529) //NoSuchEnterprise	��ҵ������	404
#define RT_ABNORMALTEAMSTATUS  (-9530) //AbnormalTeamStatus	�Ŷӿռ䴦�ڷ�����״̬	404
#define RT_NOSUCHGROUP         (-9531) //NoSuchGroup	Ⱥ�鲻����	404
#define RT_NOSUCHMEMBER        (-9532) //NoSuchMember	��Ա������	404
#define RT_ABNORMALGROUPSTATUS (-9533) //AbnormalGroupStatus	Ⱥ�鴦�ڷ�����״̬	404
#define RT_NOSUCHTEAMSPACE     (-9534) //NoSuchTeamspace	�Ŷӿռ䲻����	404
#define RT_NOSUCHACL           (-9535) //NoSuchACL	���ʿ��Ʋ�����	404
#define RT_NOHECTNOTFOUND      (-9536) //ObjectNotFound	���󲻴���	404
#define RT_NOSUCHCLIENT        (-9537) //NoSuchClient	�ͻ��˲����ڲ�����	404
#define RT_NOLOGINUSER         (-9538) //NoLoginUser	���û�������	404

//MethodNotAllowed	����������	405
//InvalidProtocol	��Ч��HTTPЭ��	405
//InvalidLicense	License��Ч	405
//Conflict	��Դ�Ѿ�����	409
//ConflictUser	�û���ͻ	409
//ConflictDomain	��ҵ������ͻ	409
//ConflictEmail	�����ַ��ͻ	409
//RepeatNameConflict	������ͻ	409
//SubFolderConflict	Ŀ���ļ�����Դ�ļ��е����ļ���	409
//SameParentConflict	Ŀ���ļ�����Դ�ļ��еĸ��ļ���	409
//LinkExistedConflict	�����Ѿ�����	409
//ExistMemberConflict	�Ŷӿռ��Ա�Ѿ�����	409
//ExistTeamspaceConflict	�Ŷӿռ��Ѿ�����	409
//AsyncNodesConflict	�첽����ִ��������ͻ	409
//ExceedQuota	�ռ�����Ѿ��ﵽ��ֵ	409
//ExceedMaxLinkNum	��������������ϵͳ����	412
//FileScanning	�ļ����ڽ��а�ȫɨ��	412
//TooManyRequests	�������	412
//EmailChangeConflict	���ܶ��������Խ����޸�	412
//PreconditionFailed	�������Ԥ������ʧ��	412
//ExceedUserMaxNodeNum	�û��ռ��ļ����Ѿ��ﵽϵͳͬ���޶�	412
//ExceedMaxMembers	��Ա�������������	412
//FileConverting	office�ļ����ڽ���Ԥ��ת��	412
//FileConvertNotSupport	�ļ���ǰ��֧��officeת��	412
//FileConvertFailed	office�ļ�ת��ʧ��	412
//InternalServerError	�������ڲ�����	500
//FSException	�ļ�ϵͳ�쳣	500
//InsufficientStorage	�������޷��洢������������������	507

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

// HTTP ״̬�붨����
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
 
// CURL �����붨����
#define CURL_ERROR_CODE (-2000)
#define CURL_RESOLVE_PROXY_FAILED (CURL_ERROR_CODE-5)
#define CURL_RESOLVE_HOST_FAILED  (CURL_ERROR_CODE-6)
#define CURL_CONNECT_FAILED       (CURL_ERROR_CODE-7)
#define CURL_TIMEDOUT             (CURL_ERROR_CODE-28)

#endif

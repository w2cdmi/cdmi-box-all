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

#define FAILED_TO_INITIALIZE_REQUEST (RT_INVALID_PARAM-1)
#define FAILED_TO_BUILDJSON (RT_INVALID_PARAM-2)
#define FAILED_TO_PARSEJSON (RT_INVALID_PARAM-3)
#define TOKEN_EXPIRED (RT_INVALID_PARAM-4)
#define FILE_CREATED (RT_INVALID_PARAM-5)

// HTTP ״̬�붨����
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

// CURL �����붨����
#define CURL_ERROR_CODE (-2000)

#endif

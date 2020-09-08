package pw.cdmi.file.engine.manage.config;

/**
 * 系统所有属性的KEY的集合
 * 
 * @author s90006125
 *         
 */
public interface SystemConfigKeys
{
    String SUPPORT_FILESYSTEM_TYPES = "support.filesystem.types";
    
    String UDS_HTTPS_ONLY = "support.filesystem.uds.https.only";
    
    /** UDS 的bucket个数 */
    String UDS_BUCKETS_NUMBER = "uds.buckets.number";
    
    String FS_NORMAL_SUBFOLDER_LEVEL = "fs.normal.subfolder.level";
    
    String FS_NORMAL_SUBFOLDER_NUMBER = "fs.normal.subfolder.number";
    
    String FS_NORMAL_SUBFOLDER_PREFIX = "fs.normal.subfolder.prefix";
    
    String FS_NORMAL_SUBFOLDER_PATH_GENERATOR = "fs.normal.subfolder.path.generator";
    
    /**
     * 存储接入点选择器算法，默认为com.huawei.sharedrive.dataserver.filesystem.manage.
     * RandomSelectAlgorithm
     */
    String FS_ENDPOINT_SELECT_ALGORITHM = "fs.endpoint.select.algorithm";
    
    /** UDS 存储的检查的重试次数 */
    String FS_UDS_CHECK_RETRY = "fs.uds.check.retry";
    
    /** 存储检查任务的超时时间，单位秒，默认55秒 */
    String FS_STORAGE_CHECK_TIMEOUT = "fs.storage.check.timeout";
    
    /** 存储检查任务超时时间，单位分钟，默认30分钟 */
    String FS_STORAGE_CHECK_TASK_TIMEOUT = "fs.storage.check.task.timeout";
    
    /** 存储检查任务设置为失效的超时时间，单位为秒，默认120秒，该时间必须比FS_STORAGE_CHECK_TASK_TIMEOUT短 */
    String FS_STORAGE_CHECK_FAILED_TIMEOUT = "fs.storage.check.failed.timeout";
    
    /** 支持文件预览的文件格式列表 */
    String FS_PREVIEW_SUPPORT_LIST = "fs.preview.support.list";
    
    /** 文件预览产生的临时文件的存放地址 */
    String FS_PREVIEW_TEMP_DIR = "fs.preview.temp.dir";
    
    /** 文件预览，生成SWF文件的最大页数 */
    String FS_PREVIEW_SWF_CONVERT_MAX_PAGE = "fs.preview.swf.convert.maxpage";
    
    /** 文件预览，将pdf转换为swf的命令 */
    String FS_PREVIEW_PDF_TO_SWF_COMMOND = "fs.preview.pdf2swf.commond";
    
    /** 文件预览，将pdf转换为swf的命令执行成功之后的信息 */
    String FS_PREVIEW_PDF_TO_SUCCESS_MESSAGE = "fs.preview.pdf2swf.success.message";
    
    /** appserver的thrift请求IP */
    String THRIFT_APPSERVER_SERVER_IP = "thrift.appserver.server.ip";
    
    /** appserver的thrift请求端口 */
    String THRIFT_APPSERVER_SERVER_PORT = "thrift.appserver.server.port";
    
    /** dataserver发布thrift服务的端口 */
    String THRIFT_DATASERVER_PUBLISH_PORT = "thrift.dataserver.publish.port";
    
    /** appserver的thrift请求超时时间 */
    String THRIFT_APPSERVER_REQUEST_TIMEOUT = "thrift.appserver.request.timeout";
    
    /** 日志备份地址 */
    String SYSTEM_LOGFILE_BACKUP_PATH = "system.logfile.backup.path";
    
    /** linux的shell路径 */
    String SYSTEM_LINUX_SHELL_PATH = "system.linux.shell.path";
    
    /** 标识在执行删除操作的时候，是否真正的删除文件 ，默认为false，标识不真正删除 */
    String SYSTEM_OBJECT_DELETE_REAL = "system.object.delete.real";
    
    /** cron类型的job的线程池大小 */
    String SYSTEM_JOB_CRON_THREADCOUNT = "system.job.cron.threadcount";
    
    /** cluster类型的job的线程池大小 */
    String SYSTEM_JOB_CLUSTER_THREADCOUNT = "system.job.cluster.threadcount";
    
    /** cluster集群节点检查频率，单位为毫秒 */
    String SYSTEM_JOB_CLUSTER_CHECKIN_INTERVAL = "system.job.cluster.checkin.interval";
    
    /** cluster集群节点离线时长，单位为毫秒 */
    String SYSTEM_ENVIRONMENT_CLUSTER_NODE_TIMEOUT = "system.environment.cluster.node.timeout";
    
    /** 管理端口 */
    String SYSTEM_ENVIRONMENT_MANAGER_PORT = THRIFT_DATASERVER_PUBLISH_PORT;
    
    /** get请求的协议类型 */
    String SYSTEM_ENVIRONMENT_SERVICE_GET_PROTOCOL = "system.environment.service.get.protocol";
    
    /** put请求的协议类型 */
    String SYSTEM_ENVIRONMENT_SERVICE_PUT_PROTOCOL = "system.environment.service.put.protocol";
    
    /** 业务http访问端口 */
    String SYSTEM_ENVIRONMENT_SERVICE_PORT_HTTP = "system.environment.service.port.http";
    
    /** 业务https访问端口 */
    String SYSTEM_ENVIRONMENT_SERVICE_PORT_HTTPS = "system.environment.service.port.https";
    
    /** 业务访问工程名 */
    String SYSTEM_ENVIRONMENT_SERVICE_PATH = "system.environment.service.path";
    
    /** 已初始化，值为true false */
    String SYSTEM_DATACENTER_BEEN_INITIALIZED = "system.datacenter.been.initialized";
    
    /** dc_id */
    String SYSTEM_DATACENTER_ID = "system.datacenter.id";
    
    /** dc 上报地址 */
    String SYSTEM_DATACENTER_REPORT_ADDR = THRIFT_APPSERVER_SERVER_IP;
    
    /** dc 上报端口 */
    String SYSTEM_DATACENTER_REPORT_PORT = THRIFT_APPSERVER_SERVER_PORT;
    
    /** object上传回调失败之后的重试次数，默认为3 */
    String OBJECT_CALLBACK_RETRY_TIMES = "object.callback.retry.times";
    
    /** object上传回调失败之后的重试次数，之间的间隔，单位为毫秒 */
    String OBJECT_CALLBACK_RETRY_INTERVAL = "object.callback.retry.interval";
    
    /** 记录系统健康度的问题路径 */
    String SYSTEM_ENVIRONMENT_HEALTH_RECORD_FILE = "system.environment.health.record.file";
    
    /**
     * 内部负载均衡配置
     */
    String SYS_INNER_LOADBLANCE_CONFIG = "system.inner.loadbalance.enable";
    
    /**
     * 重试此次
     */
    String SYS_INNER_LOADBLANCE_TRY_COUNTS = "system.inner.loadbalance.try.counts";
    
    /**
     * 部署方式
     */
    String SYSTEM_DATACENTER_TYPE = "system.datacenter.type";
    
    /** SK明文缓存超时时间，单位为毫秒 */
    String SYSTEM_SK_CACHE_TIMEOUT = "system.sk.cache.timeout";
    
    String SZ_UAS_REGION_NAME = "sz";
    
    String DG_UAS_REGION_NAME = "dg";
    
    String SZ_THRIFT_APPSERVER_SERVER_IP = "sz.thrift.appserver.server.ip";
    
    String DG_THRIFT_APPSERVER_SERVER_IP = "dg.thrift.appserver.server.ip";
    
    byte UAS_REGION_NAME_LENGTH = 2;
    
    /**
     * 实时异地复制中，文件采取分片下载的临界大小，单位为字节
     */
    String REALTIME_DEFAULT_SPLIT_SIZE = "realtime.default.split.size";
    
}

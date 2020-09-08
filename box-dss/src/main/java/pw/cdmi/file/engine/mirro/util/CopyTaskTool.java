/**
 * 
 */
package pw.cdmi.file.engine.mirro.util;

import org.springframework.util.StringUtils;

import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.mirro.domain.CopyTask;

/**
 * @author w00186884
 * 
 */
public final class CopyTaskTool
{
    private CopyTaskTool()
    {
        
    }
    
    /**
     * 所有异步复制特性日志的前缀。
     */
    public static final String LOG_TAG = "[" + EnvironmentUtils.getHostName() + "] MIRRO:";
    
    /**
     * 接受任务放入队列等待时长
     */
    public static final long WAIT_OFFERPOLL_TIME = 200;
    
    /**
     * 默认接受任务队列长度
     */
    public static final int DEFAULT_TASK_QUEUE_SIZE = 10000;
    
    /**
     * 批量提交单次的个数
     */
    public static final int BATCH_COMMIT_NUM = 100;
    
    /**
     * 默认的文件分片后的单片大小，必须要小于最小 DEFAULT_SPLIT_SIZE
     */
    public static final long DEFAULT_PART_SIZE = 50 * 1024 * 1024;
    
    /**
     * 默认采用多片Copy的文件最小值,当大于这个值就采用多片。
     */
    public static final long DEFAULT_SPLIT_SIZE = 50 * 1024 * 1024;
    
    /**
     * 默认Copy重试次数
     */
    public static final int DEAFULT_RETRYNUM = 50;
    
    /**
     * 重试获取下载地址的次数。
     */
    public static final int GET_URL_TRYNUM = 3;
    
    /**
     * 失败后下次重试时间最小间隔,分钟
     */
    public static final int NEXT_INTERVAL_MINUTE = 10;
    
    /**
     * 弹出执行任务但是失败时间超过一定时间，默认60分钟。
     */
    public static final int POP_MAX_MINUTE = 1 * 60;
    
    /**
     * Mirro类型的Callback Key后缀
     */
    public static final String CALLBACKKEY_SUFIX = ";MIRRO";
    
    /**
     * 当任务已经不存在时将MD5写为固定值
     */
    public static final String NOT_EXISTS_MD5 = "MIRRO_NOT_EXISTS";
    
    /**
     * 当任务已经存在时将MD5写为固定值
     */
    public static final String EXISTS_OBJECT = "MIRRO_EXISTS_OBJECT";
    
    /**
     * 任务启动等待时长，超过时长则还原任务状态。
     */
    public static final int TASK_START_TIMEOUT = 500;
    
    public static boolean isMirrorCallbackKey(String key)
    {
        if (StringUtils.isEmpty(key))
        {
            return false;
        }
        if (key.indexOf(CALLBACKKEY_SUFIX) >= 0)
        {
            return true;
        }
        return false;
    }
    
    public static String getMD5(String sha1)
    {
        if (StringUtils.isEmpty(sha1))
        {
            return null;
        }
        return sha1.split(";")[0].replaceAll("MD5:", "");
    }
    
    public static String getBloackMD5(String sha1)
    {
        if (StringUtils.isEmpty(sha1))
        {
            return null;
        }
        String[] shas = sha1.split(";");
        if (shas.length > 1)
        {
            return shas[1].replaceAll("BlockMD5:", "");
        }
        return null;
    }
    
    public static String getRemark(String remark)
    {
        if (remark != null && remark.length() > 255)
        {
            remark = remark.substring(0, 255);
        }
        return remark;
    }
    
    /**
     * 获取当前任务下发ac标识，并将内存中copy_task恢复正常（恢复srcObjectId）
     * 注意：在dss端，不能修改copytask中的srcObjectId，destObjectId关键属性
     * ，所以在修改后跟新copytask状态不会改变srcObjectId 不影响后续功能对标识的使用
     * 
     * @param copyTask
     * @return
     */
    public static String getUasRegionName(CopyTask copyTask)
    {
        if (null == copyTask)
        {
            return "";
        }
        String srcObjectId = copyTask.getSrcObjectId();
        if (null == srcObjectId)
        {
            return "";
        }
        
        String uasRegionName = srcObjectId.substring(0, SystemConfigKeys.UAS_REGION_NAME_LENGTH);
        if (SystemConfigKeys.SZ_UAS_REGION_NAME.equals(uasRegionName)
            || SystemConfigKeys.DG_UAS_REGION_NAME.equals(uasRegionName))
        {
            copyTask.setSrcObjectId(srcObjectId.substring(SystemConfigKeys.UAS_REGION_NAME_LENGTH));
            return uasRegionName;
        }
        
        return "";
    }
    
}

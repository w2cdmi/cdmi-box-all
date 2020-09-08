/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.realtime.util;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.utils.EnvironmentUtils;
import pw.cdmi.file.engine.manage.config.SystemConfigKeys;
import pw.cdmi.file.engine.realtime.domain.RealTimeCopyTask;

public final class RealTimeCopyTaskTool
{
    private RealTimeCopyTaskTool()
    {
        
    }
    
    /**
     * 所有实时异地复制特性日志的前缀。
     */
    public static final String LOG_TAG = "[" + EnvironmentUtils.getHostName() + "] REALTIME:";
    
    /**
     * 默认采用多片Copy的文件最小值,当大于这个值就采用多片。
     */
    public static final long DEFAULT_SPLIT_SIZE = 20 * 1024 * 1024;
    
    /**
     * 重试获取下载地址的次数。
     */
    public static final int GET_URL_TRYNUM = 3;
    
    /**
     * RealTime类型的Callback Key后缀
     */
    public static final String CALLBACKKEY_SUFIX = ";RealTime";
    
    /**
     * 任务启动等待时长，超过时长则还原任务状态。
     */
    public static final int TASK_START_TIMEOUT = 500;
    
    /**
     * 获取当前任务下发ac标识，并将内存中realtime_copy_task恢复正常（恢复srcObjectId）
     * 注意：在dss端，不能修改realtime_copy_task中的srcObjectId，destObjectId关键属性
     * ，所以在修改后跟新copytask状态不会改变srcObjectId 不影响后续功能对标识的使用
     * 
     * @param copyTask
     * @return
     */
    public static String getUasRegionName(RealTimeCopyTask realTimeCopyTask)
    {
        if (null == realTimeCopyTask)
        {
            return "";
        }
        String srcObjectId = realTimeCopyTask.getSrcObjectId();
        if (null == srcObjectId)
        {
            return "";
        }
        
        String uasRegionName = srcObjectId.substring(0, SystemConfigKeys.UAS_REGION_NAME_LENGTH);
        if (SystemConfigKeys.SZ_UAS_REGION_NAME.equals(uasRegionName)
            || SystemConfigKeys.DG_UAS_REGION_NAME.equals(uasRegionName))
        {
            realTimeCopyTask.setSrcObjectId(srcObjectId.substring(SystemConfigKeys.UAS_REGION_NAME_LENGTH));
            return uasRegionName;
        }
        
        return "";
    }
    
    public static String getMD5(String sha1)
    {
        if (StringUtils.isEmpty(sha1))
        {
            return null;
        }
        return sha1.split(";")[0].replaceAll("MD5:", "");
    }
    
    public static String getBlockMD5(String sha1)
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
    
    public static boolean isRealTimeCallbackKey(String key)
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
    
    public static String getRemark(String remark)
    {
        if (remark != null && remark.length() > 255)
        {
            remark = remark.substring(0, 255);
        }
        return remark;
    }
}

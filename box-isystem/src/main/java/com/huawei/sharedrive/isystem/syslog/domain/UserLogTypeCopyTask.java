package com.huawei.sharedrive.isystem.syslog.domain;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.isystem.system.LogListener;

import pw.cdmi.core.utils.BundleUtil;

/**
 * 標記異地復制任務狀態
 * @author w00355328
 *
 */
public enum UserLogTypeCopyTask
{
    Uncertain("", "", -1, false, "", ""),
    
    COPYTASK_ALL_STOP("copytask.all.stop", "copytask.all.stop", 80, true,"log.low","log.low"),
    
    COPYTASK_ALL_START("copytask.all.start", "copytask.all.start", 81, true,"log.low","log.low"),
    
    COPYPOLICY_STATUS_STOP("copypolicy.status.stop", "copypolicy.status.stop", 82, true,"log.low","log.low"),
    
    COPYPOLICY_STATUS_START("copypolicy.status.start", "copypolicy.status.start", 83, true,"log.low","log.low");
    
    private static final String ADMIN_LOG_FILE = "isystemLog";
    
    public static final int PARAM_MAX_LEN = 50;
    
    public static final String PARAM_ELLIPSIS = "...";
    
    public static final String PARAM_INVALID = "parameter.calibration";
    
    static
    {
        BundleUtil.addBundle(ADMIN_LOG_FILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
        BundleUtil.setDefaultBundle(ADMIN_LOG_FILE);
        BundleUtil.setDefaultLocale(Locale.ENGLISH);
    }
    
    private String type;
    
    private boolean enable;
    
    private int value;
    
    private String detail;
    
    private String rightRank;
    
    private String wrongRank;
    
    public static UserLogType build(int typeCode)
    {
        UserLogType[] allType = UserLogType.values();
        for (UserLogType tmpType : allType)
        {
            if (tmpType.getValue() == typeCode)
            {
                return tmpType;
            }
        }
        return null;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    private UserLogTypeCopyTask(String type, String detail, int value, boolean enable, String rightRank, String wrongRank)
    {
        this.type = type;
        this.value = value;
        this.enable = enable;
        this.detail = detail;
        this.rightRank = rightRank;
        this.wrongRank = wrongRank;
    }
    
    public String getRightRank(String[] params)
    {
        params = replacelongString(params);
        if (StringUtils.isEmpty(this.rightRank))
        {
            return "";
        }
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.rightRank, params);
        
    }
    
    public String getWrongRank(String[] params)
    {
        params = replacelongString(params);
        if (StringUtils.isEmpty(this.wrongRank))
        {
            return "";
        }
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.wrongRank, params);
        
    }
    
    public String getType(String[] params)
    {
        params = replacelongString(params);
        if (StringUtils.isEmpty(this.type))
        {
            return "";
        }
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.type, params);
        
    }
    
    public String getCommonErrorParamDetails(String[] params)
    {
        params = replacelongString(params);
        return "[ERROR]" + BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), PARAM_INVALID, params);
    }
    
    public String getDetails(String[] params)
    {
        params = replacelongString(params);
        return BundleUtil.getText(ADMIN_LOG_FILE, LogListener.getLanguage(), this.detail, params);
    }
    
    public String getErrorDetails(String[] params)
    {
        return "[ERROR]" + getDetails(params);
    }
    
    public boolean isEnable()
    {
        return enable;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public String[] replacelongString(String[] params)
    {
        if (null == params)
        {
            return params;
            
        }
        String p = null;
        for (int i = 0; i < params.length; i++)
        {
            p = params[i];
            if (p != null && p.length() > PARAM_MAX_LEN)
            {
                p = p.substring(0, PARAM_MAX_LEN);
                p = p + PARAM_ELLIPSIS;
                params[i] = p;
            }
        }
        return params;
    }
}

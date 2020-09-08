package com.huawei.sharedrive.cloudapp.cmb.control;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.cloudapp.utils.PropertiesUtils;
import com.huawei.sharedrive.common.util.DBAccessKeyParser;

public class CMBConstants
{
    public static final boolean isCMB = StringUtils.isBlank(PropertiesUtils.getProperty("is.cmb")) ? false
        : Boolean.parseBoolean(PropertiesUtils.getProperty("is.cmb"));
    
    public static final String CLOUDAPP_URL = PropertiesUtils.getProperty("cmb.cloudapp.url");
    
    public static final String CMB_URL = PropertiesUtils.getProperty("cmb.oams.url");
    
    public static final String CMB_SYS_ID = PropertiesUtils.getProperty("cmb.oams.sysid");
    
    public static final boolean IS_BUSINESS_BOX = StringUtils.isBlank(PropertiesUtils.getProperty("is.cmb.business")) ? false
        : Boolean.parseBoolean(PropertiesUtils.getProperty("is.cmb.business"));
    
    public static final int LIST_ORG_MAX = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.org.max")) ? 0
        : Integer.parseInt(PropertiesUtils.getProperty("cmb.org.max"));
    
    public static final int LIST_USER_MAX = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.user.max")) ? 0
        : Integer.parseInt(PropertiesUtils.getProperty("cmb.user.max"));
    
    public static final String SMS_ADDRESS = PropertiesUtils.getProperty("cmb.sms.address");
    
    public static final int SMS_PORT = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.sms.port")) ? 0
        : Integer.parseInt(PropertiesUtils.getProperty("cmb.sms.port"));
    
    public static final String SMS_USER = PropertiesUtils.getProperty("cmb.sms.user");
    
    public static final String SMS_PASS = StringUtils.isBlank(PropertiesUtils.getProperty("cmb.sms.pass")) ? ""
        : DBAccessKeyParser.decode(PropertiesUtils.getProperty("cmb.sms.pass"));
    
    public static final String SMS_CLIENTID = PropertiesUtils.getProperty("cmb.sms.clientid");
    
    public static final String SMS_BUSINESS_TYPE = PropertiesUtils.getProperty("cmb.sms.business.type");
    
    public static final String SMS_FEE_DEPT1 = PropertiesUtils.getProperty("cmb.sms.fee.dept1");
    
    public static final String SMS_FEE_DEPT2 = PropertiesUtils.getProperty("cmb.sms.fee.dept2");
    
    public static final String SMS_AM = PropertiesUtils.getProperty("cmb.sms.am");
}

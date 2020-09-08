package pw.cdmi.box.uam.util;

public class Constants
{
    public final static String THRIFT_IP = PropertiesUtils.getProperty("thrift.app.ip");
    
    public final static int THRIFT_PORT = Integer.parseInt(PropertiesUtils.getProperty("thrift.app.port",
        "12000"));
    
    public final static int EMAIL_QUEUE_TASK_NUM = Integer.parseInt(PropertiesUtils.getProperty("email.taskNumber",
        "20"));
    
    public final static int SMS_QUEUE_TASK_NUM = Integer.parseInt(PropertiesUtils.getProperty("sms.taskNumber",
    		"20"));
    
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public final static String UDS_LOG_SPLIT_CHAR = ";";
    
    public final static String UDS_STORAGE_SPLIT_CHAR = ":";
    
    public static final byte DOMAIN_TYPE_LOCAL = (byte) 1;
    
    public static final byte DOMAIN_TYPE_AD = (byte) 2;
    
    public static final byte ROLE_ENTERPRISE_ADMIN = (byte) -2;
    
    public static final byte ROLE_COMMON_ADMIN = (byte) 1;
    
    public static final byte ROLE_SUPER_ADMIN = (byte) -1;
    
    public static final int LOGO_DEFAULT_ID = 0;
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    public static final int DEFAULT_EXPORT_USER_SIZE = 10000;
    
    public static final String HW_VERIFY_CODE_CONST = "HWVerifyCode";
    
    public static final String RESET_PWD_MAIL_SUBJECT = "resetPasswordSubject.ftl";
    
    public static final String RESET_PWD_MAIL_CONTENT = "resetPasswordContent.ftl";
    
    public static final String RESET_APP_PWD_MAIL_SUBJECT = "resetAppPasswordSubject.ftl";
    
    public static final String RESET_APP_PWD_MAIL_CONTENT = "resetAppPasswordContent.ftl";
    
    public static final String MAIL_TEMPLATE_ROOT = "email";
    
    public static final String INITSET_PWD_MAIL_SUBJECT = "initSetPasswordSubject.ftl";
    
    public static final String INITSET_PWD_MAIL_CONTENT = "initSetPasswordContent.ftl";
    
    public static final String OPEN_ACCOUNT_CONTENT = "openAccountContent.ftl";
    
    public static final String OPEN_ACCOUNT_SUBJECT = "openAccountSubject.ftl";
    
    public static final String TEST_MAIL_SUBJECT = "testMailSubject.ftl";
    
    public static final String TEST_MAIL_CONTENT = "testMailContent.ftl";
    
    public static final String SERVICE_URL = PropertiesUtils.getServiceUrl();
    
    public static final String SESS_OBJ_KEY = "session.user.id";
    
    public static final String SESS_ROLE_KEY = "session.user.roles";
    
    public static final String UAM_DEFAULT_APP_ID = "-1";
    
    public static final int DEFAULT_WEB_APP_TYPE = 1;
    
    public static final int OTHER_APP_TYPE = 2;
    
    public static final String AUTH_SERVER_TYPE_LOCAL = "LocalAuth";
    
    public static final int STATUS_OF_ACCOUNT_ENABLE = 0;
    
    public static final int STATUS_OF_ACCOUNT_DISABLE = 1;
    
    public static final String ROLE_OF_ADMIN_ACCOUNT_APP_MANAGER = "APP_MANAGER";
    
    public static final String ROLE_OF_ADMIN_ACCOUNT_ENTERPRISE_BUSINESS_MANAGER = "ENTERPRISE_BUSINESS_MANAGER";
    
    public static final String ROLE_OF_ADMIN_ACCOUNT_SYSTEM_CONFIG = "SYSTEM_CONFIG";
    
    public static final String ROLE_OF_ADMIN_ACCOUNT_ANNOUNCEMENT_MANAGER = "ANNOUNCEMENT_MANAGER";
    
    public static final String ROLE_OF_ADMIN_ACCOUNT_STATISTICS_MANAGER = "STATISTICS_MANAGER";
    
    public static final String ROLE_OF_ADMIN_ACCOUNT_JOB_MANAGER = "JOB_MANAGER";
    
    public static final String DISPLAY_STAR_VALUE = "*****************************";
    
    public static final int AUTH_APP_TYPE = 1;
    
	public static final String ROLE_OF_ADMIN_ACCOUNT_FEEDBACK_MANAGER = "FEEDBACK_MANAGER";
	
	public static final int DEFAULT_FEEDBACK_PAGE_SIZE = 15;
	
	public static final String ANSWER_FEEDBACK_MAIL_SUBJECT = "answerFeedBackSubject.ftl";
	
	public static final String ANSWER_FEEDBACK_MAIL_CONTENT = "answerFeedBackContent.ftl";
}

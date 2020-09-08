package pw.cdmi.box.uam.httpclient.rest.common;

public final class Constants
{
    
    public static final int MESSAGE_SUCCESS = 1;
    
    public static final int MESSAGE_FAILURE = 2;
    
    public static final String SLASH = "/";
    
    public static final String QUESTION = "?";
    
    public static final String AND = "&";
    
    /* ============== HTTP Constants============== */
    /** JSON TYPE */
    public static final String JSON_TYPE = "application/json";
    
    /** RANGE */
    public static final String RANGE = "Range";
    
    /** HTTP AUTHOR */
    public static final String HTTP_AUTHOR = "Authorization";
    
    /** HTTP Date */
    public static final String HTTP_DATE = "Date";
    
    /** HTTP ACCEPT */
    public static final String HTTP_ACCEPT = "Accept";
    
    /** HEADER OFFSET */
    public static final String HEADER_OFFSET = "offset";
    
    /** HEADER ORDER_BY */
    public static final String HEADER_ORDER_BY = "orderby";
    
    /** HEADER ORDER_DES */
    public static final String HEADER_DES = "des";
    
    /** HEADER LIMIT */
    public static final String HEADER_LIMIT = "limit";
    
    public static final int CLIENT_TYPE = 5;

    public static final String RESOURCE_REGIONLIST_V2 = "/api/v2/regions";
    
    public static final String SUFFIX_FILE_COPY = "copy";
    
    public static final String SUFFIX_FILE_CONTENTS = "contents";
    
    public static final String SUFFIX_OBJECTID_URL = "url";
    
    public static final String SUFFIX_FILE_VERSIONS = "versions";
    
    public static final String SUFFIX_FILE_MOVE = "move";
    
    public static final String SUFFIX_FILE_SHARELINK = "sharelink";
    
    public static final String SUFFIX_FILE_RENAME = "rename";
    
    public static final String SUFFIX_FILE_PRE_UPLOAD = "preupload";
    
    public static final String SUFFIX_FOLDER_COPY = "copy";
    
    public static final String SUFFIX_FOLDER_MOVE = "move";
    
    public static final String SUFFIX_CHANGE_METADATA = "change";
    
    public static final String SUFFIX_SHARE = "share";
    
    public static final String SUFFIX_SHARE_LIST = "list";
    
    public static final String SUFFIX_SHARE_PUT_SHARE = "add";
    
    public static final String PARAM_SYNCVERSION = "syncVersion";
    
    public static final String PARAM_LIST_USER = "listUser";
    
    public static final String PARAM_DELETE_SHARED_USER = "deleteUsers";
    
    public static final String PARAM_REJECT_SHARE = "reject";
    
    public static final String PARAM_LIST_AD_USER = "listADUser";
    
    public final static String FINISHE_UPLOADFILE_BYPARTS_URL_SUFFIX = "?commit=true";
    
    public final static String FILE_UPLOADFILE_BYPARTS_URL_SUFFIX = "?partID=";
    
    public static final long SINGLE_FILEPART_MAXSIZE = 1024 * 1024 * 5;
    
    public final static String SHAREPREFERENCES_NAME = "uploading";
    
    public static final int FILE_UPLOAD_PARTS = 2;
    
    public static final int FILE_COMMAND_SERVER_FINISH = 3;
    
    public static final int FILE_COTINUE_UPLOAD_NONEDD = 4;
    
    public static final int FILE_CONTINE_UPLOAD_NEED = 5;
    
    public static final int FILE_UPLOAD_TOTAL = 6;
    
    public static final int FAILRUE = 7;
    
    public static final int CALL_BACK_ONSTART = 8;
    
    /**
     * Request id for a HTTP GET request, values == {@value} .
     */
    public static final int HTTP_GET = 0;
    
    /**
     * Request id for a HTTP POST request, values == {@value} .
     */
    public static final int HTTP_POST = 1;
    
    /**
     * Request id for a HTTP PUT request, values == {@value} .
     */
    public static final int HTTP_PUT = 2;
    
    /**
     * Request id for a HTTP DELETE request, values == {@value} .
     */
    public static final int HTTP_DELETE = 3;
    
}

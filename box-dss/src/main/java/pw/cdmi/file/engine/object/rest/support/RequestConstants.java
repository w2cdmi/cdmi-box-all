/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest.support;

/**
 * 
 * @author s90006125
 * 
 */
public interface RequestConstants
{
    public static interface URL
    {
        String BASE_URL = "/api/{objectID}";
        
        String URL_DOWNLOAD_FILE = "/{showName}";
        
        String URL_DOWNLOAD_THUMBNAIL = "/{showName}/thumbnail";
        
        String URL_DOWNLOAD_PREVIEW = "/{showName}/preview";
    }
    
    String CALLBACK_KEY = "callback_key";
    
    String REQUEST_ID = "request_id";
    
    String OBJECT_ID = "objectID";
    
    String REQUEST_TOKEN = "token";
    
    String REQUEST_CONTENT_LENGTH = "Content-Length";
    
    String REQUEST_OBJECT_LENGTH = "objectLength";
    
    String REQUEST_OBJECT_UPLOAD_COMMIT = "commit";
    
    String REQUEST_OBJECT_PARTS_START = "parts";
    
    String REQUEST_OBJECT_PART_ID = "partID";
    
    String REQUEST_OBJECT_SHOW_NAME = "showName";
    
    String REQUEST_OBJECT_RANGE = "Range";
    
    String REQUEST_THUMBNAIL_HEIGTH = "minHeight";
    
    String REQUEST_THUMBNAIL_WIDTH = "minWidth";
    
    String REQUEST_THUMBNAIL_DEFAULT = "default";
    
    String REQUEST_PREVIEW_DIST_TYPE = "distType";
    
    String REQUEST_PREVIEW_SRC_TYPE = "srcType";
    
    String CALLBACK_LAST_MODIFIED = "nodeLastModified";
}

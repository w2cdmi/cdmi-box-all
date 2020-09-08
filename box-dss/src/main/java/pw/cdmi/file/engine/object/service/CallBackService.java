/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service;

import pw.cdmi.file.engine.object.domain.FileObject;

/**
 * 上传文件等操作之后的回调操作
 * 
 * @author s90006125
 * 
 */
public interface CallBackService
{
    /**
     * 回调，通知中心测，文件上传完成
     * 
     * @param fileObject
     */
    void updateFileObject(FileObject fileObject);
    
    void abortUpload(String objectID, String callBackKey);
}

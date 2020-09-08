/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service;

import java.util.List;

import pw.cdmi.file.engine.object.domain.MultipartFileObject;
import pw.cdmi.file.engine.object.domain.MultipartPart;

/**
 * 
 * @author s90006125
 * 
 */
public interface MultipartFileObjectService
{
    /**
     * 初始化分片上传
     * 
     * @param fileObject
     * @return
     */
    void initMultipartUpload(MultipartFileObject fileObject);
    
    /**
     * 获取分片对象
     * 
     * @param fileObject
     * @return
     */
    MultipartFileObject getMultipartUpload(String objectID);
    
    /**
     * 创建文件对象分片
     * 
     * @param fileObject
     * @param part
     * @param inputStream
     * @return
     */
    MultipartPart createMultipartPart(MultipartFileObject fileObject, MultipartPart part);
    
    /**
     * 取消分片上传
     * 
     * @param fileObject
     * @return
     */
    boolean abortMultipartUpload(MultipartFileObject fileObject);
    
    /**
     * 清理分片对象<br>
     * 应用于两个场景：<br>
     * 1、如果是uds存储，则在异步计算完sha1之后调用；<br>
     * 2、如果是NAS存储，则在异步计算完sha1，并且清理掉老的分片文件之后调用<br>
     * 
     * 做两个事情：<br>
     * 1、删除slice_object中分片对象数据<br>
     * 2、更新fileobject表状态
     * @param fileObject
     * @return
     */
    boolean clearMultipartUpload(MultipartFileObject fileObject);
    
    /**
     * 列举分片文件分片列表
     * 
     * @param fileObject
     * @return
     */
    MultipartFileObject listMultipartParts(MultipartFileObject fileObject);
    
    /**
     * 完成分片上传
     * 
     * @param fileObject
     * @return
     */
    MultipartFileObject completeMultipartUpload(MultipartFileObject fileObject);
    
    /**
     * 列举执行了提交命令后的对象
     * <br>
     *  state=FileObjectStatus.COMMITTING，mergeAt > 超时时间, mergeTimes  < 最大合并次数的任务
     * @param timeout                     合并的超时时间，单位为小时
     * @param maxMergeTimes     合并的最大次数
     * @return
     */
    MultipartFileObject selectCommittingMultipartFileObject(int timeout, int maxMergeTimes);
    
    /**
     * 列举合并后，待清理的分片对象
     * @param reserveTime  保留时长，单位为分钟：表示一个文件合并完后，多长时间后再删除老文件
     * @maxClearTimes 最大清理次数
     * @param limit  每次获取清理任务的最大数量
     * @return
     */
    List<MultipartFileObject> selectWaitClearMultipartFileObject(int reserveTime, int maxClearTimes, int limit);
    
    /**
     * 列举同一個UploadId下的分片數據
     * @param fileObject  上傳文件對象
     * @param uploadId 分片上傳的uploadId
     * @return
     */
    public List<MultipartFileObject> selectMultipartForUploadId(String fileObjectId, String uploadId);
    
    /**
     * 更新对象
     * @param fileObject
     */
    void updateMultipartUpload(MultipartFileObject fileObject);
    
    /**
     * 删除无效的数据
     * <br>
     * mergeAt > 超时时间 && mergeTimes >= 最大合并次数的任务，或者state=FileObjectStatus.COMPLETED的任务，可以删除
     * @param timeout                        合并的超时时间，单位为小时
     * @param maxMergeTimes        合并的最大次数
     * @param maxClearTimes          清理的最大次数
     */
    void deleteInvalidData(int timeout, int maxMergeTimes, int maxClearTimes);
    
    /**
     * 更新最后修改时间
     * @param fileObject
     */
    void updateLastModifyTime(MultipartFileObject fileObject);
}

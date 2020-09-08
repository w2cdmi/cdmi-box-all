/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.service;

import java.util.List;

import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.FileObjectDeleteTask;

/**
 * 
 * @author s90006125
 *
 */
public interface FileObjectDeleteService
{
    /**
     * 保存对象彻底删除任务，该方法执行以下两个动作：<br>
     * 1、上传fileobject表<br>
     * 2、保存进fileobject_delete_task表
     * 
     * @param fileObject
     */
    void createFileObjectDeleteTask(FileObject fileObject); 
    
    /**
     * 更新彻底删除对象
     * @param fileObjectDeleteTask
     */
    int updateFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask);
    
    /**
     * 标记删除任务，只有标记state=FileObjectStatus.WAITDELETE状态的数据，标记成功返回true，否则返回false，该方法用于避免多线程执行状况下，并发冲突的问题
     * @param fileObjectDeleteTask
     * @return
     */
    boolean markFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask);
    
    /**
     * 完成彻底删除任务，该方法执行以下两个动作：<br>
     * 1、删除fileobject_delete_task表；<br>
     * 2、将数据保存进fileobject_delete_log表
     * 
     * @param fileObjectDeleteTask
     */
    void completeFileObjectDeleteTask(FileObjectDeleteTask fileObjectDeleteTask);
    
    /**
     * 恢复彻底删除失败的数据 以及 彻底删除超时的数据，满足以下条件的数据的状态，将恢复为FileObjectStatus.WAITDELETE状态：<br>
     *1、删除超时：state=FileObjectStatus.STARTDELETE && (modified is not null && (modified+timeout)<=now) && deleteTimes < retryTimes<br>
     *2、删除失败：state=FileObjectStatus.DELETEFAILE
     * 该方法只更新state字段
     * 
     * @see pw.cdmi.file.dataserver.object.domain.FileObjectStatus.WAITDELETE
     * 
     * @param dbIndex  ds_fileobjectdb数据库的索引
     * @param tableIndex  表的索引
     * @param timeout  删除失败后的重试超时时间，单位为：分钟；
     * @param retryTimes  删除失败后的重试次数，最新为0，表示只删除一次
     */
    void resumeFailedFileObjectDeleteTask(int dbIndex, int tableIndex, int timeout, int retryTimes);
    
    /**
     * 列举需要执行彻底删除的任务，列举出的任务满足以下条件:<br>
     * <p>state=FileObjectStatus.WAITDELETE && deleteTimes < retryTimes && (deleteAt + reserveTime)<=now </p>
     * 
     * @param dbIndex  ds_fileobjectdb数据库的索引
     * @param tableIndex  表的索引
     * @param retryTimes  删除失败后的重试次数，最新为0，表示只删除一次
     * @param reserveTime 保留时间，即收到删除命令后，需保留多长时间后再执行彻底删除，未防止误删除的最后规避手段，单位为：天
     * @param limit 每次查询的最大条数（每张表的最大条数，而不是总条数）
     * @return
     */
    List<FileObjectDeleteTask> listFileObjectDeleteTask(int dbIndex, int tableIndex, int retryTimes, int reserveTime, int limit);
}

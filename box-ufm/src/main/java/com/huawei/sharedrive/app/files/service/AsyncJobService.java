package com.huawei.sharedrive.app.files.service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.ResponseGetTask;

/**
 * 异步任务
 * 
 * @author l90003768
 *
 */
public interface AsyncJobService
{
    /**
     * 异步清空回收站
     * @param user
     * @param taskRequest
     * @return
     * @throws Exception
     */
    String asyncCleanTrash(final UserToken user, RequestAddAsyncTask taskRequest) throws BaseRunException;
    
    /**
     * 异步复制节点，支持批量
     * @param user
     * @param taskRequest
     * @return
     * @throws Exception
     */
    String asyncCopy(final UserToken user, RequestAddAsyncTask taskRequest, boolean valiLinkAccessCode) throws BaseRunException;
    
    /**
     * 异步删除节点，放入回收站，支持批量
     * @param user
     * @param taskRequest
     * @return
     * @throws Exception
     */
    String asyncDelete(final UserToken user, RequestAddAsyncTask taskRequest) throws BaseRunException;
    
    /**
     * 异步移动节点，支持批量
     * @param user
     * @param taskRequest
     * @return
     * @throws Exception
     */
    String asyncMove(final UserToken user, RequestAddAsyncTask taskRequest) throws BaseRunException;
    
    
    /**
     * 异步全部还原回收站
     * @param user
     * @param taskRequest
     * @return
     * @throws Exception
     */
    String asyncRestoreTrash(final UserToken user, RequestAddAsyncTask taskRequest) throws BaseRunException;
    
    /**
     * 获取异步任务执行结果
     * @param user
     * @param taskId
     * @return
     */
    ResponseGetTask getTaskStatus(final UserToken user, String taskId) throws InternalServerErrorException;
    
    
}

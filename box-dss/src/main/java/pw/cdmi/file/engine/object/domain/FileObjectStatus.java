/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

/**
 * 文件状态
 * 
 * @author s90006125
 * 
 */
public enum FileObjectStatus
{
    /**
     * 正在上传
     */
    UPLOADING(0),
    /**
     * 提交了分片，用户已经可以使用，如果是NAS，表示还未合并
     */
    COMMITTING(1),
    /**
     * 已完成
     */
    COMPLETED(2),
    
    /**
     * 等待清理表示文件系统中的老的分片数据还未删除干净，需要清理老数据（目前只针对nas文件系统）
     */
    WAITCLEAR(3),
    
//    /**
//     * 已被删除：<br/>
//     * 如果dss不开启彻底删除功能，则收到ufm发起的删除命令后，只是更新对象状态为该状态，并且数据保留在fileobject表中；<br/>
//     * 如果开启了彻底删除功能，则不会存在该状态，直接将数据移动到fileobject_delete_task表中，并且状态更新为WAITDELETE(5)
//     */
//    BEENDELETE(4),
    
    /**
     * 等待删除：接收了删除命令的数据，成功将fileobject表中的数据移动到fileobject_delete_task表，表示该数据已被彻底删除，只是还没有删除底层文件，该状态只存在于fileobject_delete_task表中
     */
    WAITDELETE(4),
    
    /**
     * 已被异步定时任务查询出来，删除过程中，在这个状态的数据，除非超时，否则其他机器无法查询出来，避免被重复执行删除
     */
    STARTDELETE(5),
    
    /**
     * 删除底层文件失败
     */
    DELETEFAILE(6),
    
    /**
     * 删除底层文件成功，该状态只存在于fileobject_delete_log表中
     */
    DELETESUCCESS(7);
    
    private int code;
    
    private FileObjectStatus(int code)
    {
        this.code = code;
    }
    
    public int getCode()
    {
        return this.code;
    }
    
    public static FileObjectStatus parseState(int code)
    {
        for (FileObjectStatus s : FileObjectStatus.values())
        {
            if (s.getCode() == code)
            {
                return s;
            }
        }
        return null;
    }
}

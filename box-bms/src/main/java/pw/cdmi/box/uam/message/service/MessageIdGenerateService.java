package pw.cdmi.box.uam.message.service;

public interface MessageIdGenerateService
{
    /**
     * 获取用户的下一条消息ID
     * 
     * @param userId
     * @return
     */
    long getNextId();
    
}

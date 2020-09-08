package pw.cdmi.box.uam.terminal.service;

import java.util.Date;

public interface TerminalService
{
    /**
     * @param appId
     * @param deviceType
     * @param beginDate
     * @param endDate
     * @return
     */
    long getAgentCountByLoginAt(String appId, int deviceType, Date beginDate, Date endDate);
    
    int getByUserCount(long cloudUserId);
}

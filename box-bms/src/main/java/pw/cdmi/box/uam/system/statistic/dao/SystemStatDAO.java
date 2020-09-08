package pw.cdmi.box.uam.system.statistic.dao;

import java.util.Date;
import java.util.List;

import pw.cdmi.box.uam.system.statistic.domain.SystemStat;

public interface SystemStatDAO
{
    
    void create(SystemStat stat);
    
    SystemStat get(Date statDate, String appId);
    
    List<SystemStat> listSystemStat(Date beginDate, Date endDate, String appId);
}

package pw.cdmi.box.uam.log.service;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.uam.log.domain.QueryCondition;
import pw.cdmi.common.log.SystemLog;

public interface SystemLogService
{
    
    String create(SystemLog systemLog);
    
    Page<SystemLog> getFilterd(QueryCondition filter);
    
    Page<SystemLog> getByLoginName(SystemLog systemLog, PageRequest pageRequest);
    
    void updateSuccess(String id);
    
    void deleteById(String id);
    
}

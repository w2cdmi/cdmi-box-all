package pw.cdmi.box.uam.log.manager;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.domain.QueryCondition;
import pw.cdmi.common.log.SystemLog;

public interface SystemLogManager
{
    
    String saveFailLog(SystemLog systemLog, OperateType operateType, OperateDescription operateDescription,
        String[] paramsType, String[] paramsDescription);
    
    String saveFailLog(HttpServletRequest request, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription);
    
    void updateSuccess(String id);
    
    Page<SystemLog> getFilterd(QueryCondition filter, Locale locale);
    
    String saveSuccessLog(HttpServletRequest request, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription);
    
    String saveSuccessLog(SystemLog systemLog, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription);
    
   
}

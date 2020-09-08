package pw.cdmi.box.uam.authapp.manager;

import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.box.uam.log.domain.UserLogQueryCondition;

public interface AppAdminLogManager
{
    String getOperationType(UserLogQueryCondition condition);
    
    String getOperationLevel(UserLogQueryCondition condition);
    
    boolean isVaildConditon(UserLogQueryCondition condition, UserLogListReq userLogReq);
}

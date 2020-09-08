package pw.cdmi.box.uam.authapp.manager.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.authapp.manager.AppAdminLogManager;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.log.domain.UserLogCategory;
import pw.cdmi.box.uam.log.domain.UserLogListReq;
import pw.cdmi.box.uam.log.domain.UserLogQueryCondition;

@Component
public class AppAdminLogManagerImpl implements AppAdminLogManager
{
    private static final String LOG_ENUM_TYPE = "ALL_LOG";
    
    private static final int OPERATION_STATUS_ALL = -1;
    
    private static final int OPERATION_STATUS_OK = 0;
    
    private static final int OPERATION_STATUS_FAIL = 1;
    
    private static final int ENUM_TYPE_PID = 0;
    
    private static final int ENUM_TYPE_SELFID = -1;
    
    private static Logger logger = LoggerFactory.getLogger(AppAdminLogManagerImpl.class);
    
    @Override
    public String getOperationType(UserLogQueryCondition condition)
    {
        checkQueryCondition(condition);
        int status = condition.getStatus();
        String firstCategory = condition.getFirstCategory();
        String secondCategory = condition.getSecondCategory();
        if (StringUtils.isNotBlank(secondCategory) && secondCategory.equals(LOG_ENUM_TYPE))
        {
            String logType = getFirstCategoryParamter(firstCategory, status);
            return logType == null ? null : logType.replaceAll("\\s*", "");
        }
        if (StringUtils.isNotBlank(secondCategory) && !secondCategory.equals(LOG_ENUM_TYPE))
        {
            String logType = getSecondCategoryParamter(secondCategory, status);
            return logType == null ? null : logType.replaceAll("\\s*", "");
        }
        return null;
    }
    
    private String getFirstCategoryParamter(String modelName, int status)
    {
        UserLogCategory userLogCategory = UserLogCategory.valueOf(modelName);
        int selfId = userLogCategory.getSelfId();
        if (selfId == ENUM_TYPE_SELFID)
        {
            return null;
        }
        if (selfId > ENUM_TYPE_PID)
        {
            UserLogCategory[] userLogCategories = UserLogCategory.values();
            StringBuilder categoryParamter = new StringBuilder();
            String[] includeType;
            for (UserLogCategory logCategory : userLogCategories)
            {
                if (logCategory.getpId() != selfId)
                {
                    continue;
                }
                includeType = logCategory.getIncludeType();
                if (status == OPERATION_STATUS_OK)
                {
                    categoryParamter.append(includeType[0]).append(',');
                }
                if (status == OPERATION_STATUS_FAIL && includeType.length > 1)
                {
                    categoryParamter.append(includeType[1]).append(',');
                }
                if (status == OPERATION_STATUS_ALL)
                {
                    categoryParamter.append(includeType[0]).append(',');
                    if (includeType.length > 1)
                    {
                        categoryParamter.append(includeType[1]).append(',');
                    }
                }
            }
            return categoryParamter.toString();
        }
        return null;
    }
    
    private String getSecondCategoryParamter(String modelName, int status)
    {
        UserLogCategory userLogCategory = UserLogCategory.valueOf(modelName);
        String[] includeType = userLogCategory.getIncludeType();
        if (status == OPERATION_STATUS_OK)
        {
            return includeType[0];
        }
        if (status == OPERATION_STATUS_FAIL && includeType.length > 1)
        {
            return includeType[1];
        }
        if (status == OPERATION_STATUS_ALL)
        {
            StringBuilder categoryParamter = new StringBuilder();
            categoryParamter.append(includeType[0]).append(',');
            if (includeType.length > 1)
            {
                categoryParamter.append(includeType[1]);
            }
            return categoryParamter.toString();
        }
        return null;
    }
    
    private void checkQueryCondition(UserLogQueryCondition queryCondition)
    {
        int status = queryCondition.getStatus();
        String firstCategory = queryCondition.getFirstCategory();
        String secondCategory = queryCondition.getSecondCategory();
        try
        {
            UserLogCategory.valueOf(firstCategory);
            UserLogCategory.valueOf(secondCategory);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("invalid paramter", e);
        }
        if (status < OPERATION_STATUS_ALL || status > OPERATION_STATUS_FAIL)
        {
            throw new BusinessException(401, "invalid paramter");
        }
    }
    
    @Override
    public String getOperationLevel(UserLogQueryCondition condition)
    {
        int status = condition.getStatus();
        String firstCategory = condition.getFirstCategory();
        String secondCategory = condition.getSecondCategory();
        if (LOG_ENUM_TYPE.equalsIgnoreCase(firstCategory) && LOG_ENUM_TYPE.equalsIgnoreCase(secondCategory))
        {
            if (status == OPERATION_STATUS_ALL)
            {
                return null;
            }
            if (status == OPERATION_STATUS_OK)
            {
                return "INFO";
            }
            if (status == OPERATION_STATUS_FAIL)
            {
                return "ERROR";
            }
        }
        return null;
    }
    
    @Override
    public boolean isVaildConditon(UserLogQueryCondition condition, UserLogListReq userLogReq)
    {
        String firstCategory = condition.getFirstCategory();
        String secondCategory = condition.getSecondCategory();
        String operationType = userLogReq.getType();
        if (!LOG_ENUM_TYPE.equals(secondCategory) && StringUtils.isBlank(operationType))
        {
            return false;
        }
        if (LOG_ENUM_TYPE.equals(secondCategory) && !LOG_ENUM_TYPE.equals(firstCategory)
            && StringUtils.isBlank(operationType))
        {
            return false;
        }
        return true;
    }
}

package com.huawei.sharedrive.uam.log.manager.impl;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.log.domain.OperateDescription;
import com.huawei.sharedrive.uam.log.domain.OperateType;
import com.huawei.sharedrive.uam.log.manager.SystemLogManager;
import com.huawei.sharedrive.uam.log.service.SystemLogService;

import pw.cdmi.common.log.SystemLog;

@Component
public class SystemLogManagerImpl implements SystemLogManager
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemLogManagerImpl.class);
    
    private static final int SUFFIX_LENG_THREE = 3;
    
    private static final int MAX_LENG_FIFTY_FIVE = 255;
    
    private static final int MAX_LENG_TWENTY_SEVEN = 127;
    
    private static final int MAX_LENG_SIXTY_FOUR = 64;
    
    @Autowired
    private SystemLogService systemLogService;
    
    @Override
    public String save(HttpServletRequest request, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription)
    {
        
        SystemLog log = OperateType.getSystemLog(request);
        return saveSystemLog(log, operateType, operateDescription, paramsType, paramsDescription);
    }
    
    @Override
    public String saveSystemLog(SystemLog systemLog, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription)
    {
        try
        {
            systemLog.setOperateType(operateType.getDetails(paramsType));
            systemLog.setType(operateType.getCode());
            if (null != paramsType)
            {
                StringBuilder sb = new StringBuilder();
                for (String iter : paramsType)
                {
                    sb.append(iter + SystemLog.SPLIT_SEPARATOR);
                }
                String content = sb.toString();
                content = content.substring(0, content.length() - SystemLog.SPLIT_SEPARATOR.length());
                systemLog.setTypeContent(content);
            }
            systemLog.setOperateDescription(operateDescription.getDetails(paramsDescription));
            systemLog.setDescription(operateDescription.getCode());
            if (null != paramsDescription)
            {
                StringBuilder sb = new StringBuilder();
                for (String iter : paramsDescription)
                {
                    sb.append(iter + SystemLog.SPLIT_SEPARATOR);
                }
                String content = sb.toString();
                content = content.substring(0, content.length() - SystemLog.SPLIT_SEPARATOR.length());
                systemLog.setDescriptionContent(content);
            }
            systemLog.setLoginName(getValue(systemLog.getLoginName(), MAX_LENG_FIFTY_FIVE));
            systemLog.setShowName(getValue(systemLog.getShowName(), MAX_LENG_FIFTY_FIVE));
            systemLog.setClientDeviceSN(getValue(systemLog.getClientDeviceSN(), MAX_LENG_TWENTY_SEVEN));
            systemLog.setClientDeviceName(getValue(systemLog.getClientDeviceName(), MAX_LENG_TWENTY_SEVEN));
            systemLog.setClientAddress(getValue(systemLog.getClientAddress(), MAX_LENG_SIXTY_FOUR));
            systemLog.setClientOS(getValue(systemLog.getClientOS(), MAX_LENG_SIXTY_FOUR));
            systemLog.setClientVersion(getValue(systemLog.getClientVersion(), MAX_LENG_TWENTY_SEVEN));
            systemLog.setOperateType(getValue(systemLog.getOperateType(), MAX_LENG_FIFTY_FIVE));
            systemLog.setTypeContent(getValue(systemLog.getTypeContent(), MAX_LENG_FIFTY_FIVE));
            systemLog.setOperateDescription(getValue(systemLog.getOperateDescription(), MAX_LENG_FIFTY_FIVE));
            systemLog.setDescriptionContent(getValue(systemLog.getDescriptionContent(), MAX_LENG_FIFTY_FIVE));
            return systemLogService.create(systemLog);
        }
        catch (RuntimeException e)
        {
            LOGGER.debug("save systemLog fail", e);
        }
        return null;
    }
    
    @Override
    public void updateSuccess(String id)
    {
        try
        {
            systemLogService.updateSuccess(id);
        }
        catch (Exception e)
        {
            LOGGER.debug("update systemLog fail", e);
        }
    }
    
    private String getValue(String value, int maxLength)
    {
        if (null != value && value.length() > maxLength)
        {
            int lengthBeyond = value.length() - maxLength;
            String str = value.substring(0, value.length() - (lengthBeyond + SUFFIX_LENG_THREE));
            return str + "...";
        }
        return value;
    }
    
    @Override
    public String saveSystemLog(SystemLog systemLog, OperateType operateType,
        OperateDescription operateDescription, String[] paramsDescription)
    {
        try
        {
            systemLog.setType(operateType.getCode());
            systemLog.setOperateDescription(operateDescription.getDetails(paramsDescription));
            systemLog.setDescription(operateDescription.getCode());
            if (null != paramsDescription)
            {
                StringBuilder sb = new StringBuilder();
                for (String iter : paramsDescription)
                {
                    sb.append(iter + SystemLog.SPLIT_SEPARATOR);
                }
                String content = sb.toString();
                content = content.substring(0, content.length() - SystemLog.SPLIT_SEPARATOR.length());
                systemLog.setDescriptionContent(content);
            }
            systemLog.setLoginName(getValue(systemLog.getLoginName(), MAX_LENG_FIFTY_FIVE));
            systemLog.setShowName(getValue(systemLog.getShowName(), MAX_LENG_FIFTY_FIVE));
            systemLog.setClientDeviceSN(getValue(systemLog.getClientDeviceSN(), MAX_LENG_TWENTY_SEVEN));
            systemLog.setClientDeviceName(getValue(systemLog.getClientDeviceName(), MAX_LENG_TWENTY_SEVEN));
            systemLog.setClientAddress(getValue(systemLog.getClientAddress(), MAX_LENG_SIXTY_FOUR));
            systemLog.setClientOS(getValue(systemLog.getClientOS(), MAX_LENG_SIXTY_FOUR));
            systemLog.setClientVersion(getValue(systemLog.getClientVersion(), MAX_LENG_TWENTY_SEVEN));
            systemLog.setOperateType(getValue(systemLog.getOperateType(), MAX_LENG_FIFTY_FIVE));
            systemLog.setTypeContent(getValue(systemLog.getTypeContent(), MAX_LENG_FIFTY_FIVE));
            systemLog.setOperateDescription(getValue(systemLog.getOperateDescription(), MAX_LENG_FIFTY_FIVE));
            systemLog.setDescriptionContent(getValue(systemLog.getDescriptionContent(), MAX_LENG_FIFTY_FIVE));
            return systemLogService.create(systemLog);
        }
        catch (RuntimeException e)
        {
            LOGGER.debug("save systemLog fail", e);
        }
        return null;
    }
    
}

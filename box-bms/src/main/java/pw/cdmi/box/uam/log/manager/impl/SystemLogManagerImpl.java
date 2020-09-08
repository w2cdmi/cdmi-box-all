package pw.cdmi.box.uam.log.manager.impl;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.domain.QueryCondition;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.log.service.SystemLogService;
import pw.cdmi.common.log.SystemLog;
import pw.cdmi.core.utils.BundleUtil;

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
    public String saveFailLog(HttpServletRequest request, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription)
    {
        SystemLog log = OperateType.getSystemLog(request);
        return saveLog(log, operateType, operateDescription, paramsType, paramsDescription);
    }
    
    @Override
    public String saveSuccessLog(HttpServletRequest request, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription)
    {
        SystemLog log = OperateType.getSystemLog(request);
        log.setOperateResult(true);
        return saveLog(log, operateType, operateDescription, paramsType, paramsDescription);
    }
    
    @Override
    public String saveSuccessLog(SystemLog systemLog, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription)
    {
        
        systemLog.setOperateResult(true);
        return saveLog(systemLog, operateType, operateDescription, paramsType, paramsDescription);
    }
    
    @Override
    public String saveFailLog(SystemLog systemLog, OperateType operateType,
        OperateDescription operateDescription, String[] paramsType, String[] paramsDescription)
    {
        return saveLog(systemLog, operateType, operateDescription, paramsType, paramsDescription);
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
    
    @Override
    public Page<SystemLog> getFilterd(QueryCondition filter, Locale locale)
    {
        Page<SystemLog> content = systemLogService.getFilterd(filter);
        fillOperatrDetails(content.getContent(), locale);
        return content;
    }
    
    private void fillOperatrDetails(List<SystemLog> content, Locale locale)
    {
        if (null == content || content.size() <= 0)
        {
            return;
        }
        for (SystemLog systemLog : content)
        {
            try
            {
                String[] type = null;
                if (null != systemLog.getTypeContent())
                {
                    type = systemLog.getTypeContent().split(SystemLog.SPLIT_SEPARATOR);
                }
                String[] description = null;
                if (null != systemLog.getDescriptionContent())
                {
                    description = systemLog.getDescriptionContent().split(SystemLog.SPLIT_SEPARATOR);
                }
                OperateType parseType = OperateType.parseType(systemLog.getType());
                
                if (parseType != null)
                {
                    systemLog.setOperateType(parseType.getDetails(locale, type));
                }
                OperateDescription parseDescription = OperateDescription.parseDescription(systemLog.getDescription());
                if (parseDescription != null)
                {
                    systemLog.setOperateDescription(parseDescription.getDetails(locale, description));
                }
                setLevel(systemLog, locale);
            }
            catch (RuntimeException e)
            {
                LOGGER.error(e.toString());
                systemLog.setOperateType(String.valueOf(systemLog.getType()));
                systemLog.setOperateDescription(String.valueOf(systemLog.getDescription()));
            }
        }
    }
    
    private void setLevel(SystemLog systemLog, Locale locale)
    {
        try
        {
            switch (OperateType.setLogLevel(systemLog.getType()))
            {
                case 0:
                    systemLog.setLevelString(BundleUtil.getText("systemLog", locale, "low"));
                    break;
                case 1:
                    systemLog.setLevelString(BundleUtil.getText("systemLog", locale, "medium"));
                    break;
                case 2:
                    systemLog.setLevelString(BundleUtil.getText("systemLog", locale, "high"));
                    break;
                default:
                    break;
            }
            
            if (systemLog.getType() == OperateType.SyncUser.getCode() && !systemLog.getOperateResult())
            {
                systemLog.setLevelString(BundleUtil.getText("systemLog", locale, "high"));
            }
            if (systemLog.getType() == OperateType.AsyncTask.getCode() && !systemLog.getOperateResult())
            {
                systemLog.setLevelString(BundleUtil.getText("systemLog", locale, "high"));
            }
            if (systemLog.getType() == OperateType.Statistics.getCode() && !systemLog.getOperateResult())
            {
                systemLog.setLevelString(BundleUtil.getText("systemLog", locale, "high"));
            }
            if (systemLog.getType() == OperateType.LockModifyPassword.getCode()
                && !systemLog.getOperateResult())
            {
                systemLog.setLevelString(BundleUtil.getText("systemLog", locale, "high"));
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
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
    
    private String saveLog(SystemLog systemLog, OperateType operateType,
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

}

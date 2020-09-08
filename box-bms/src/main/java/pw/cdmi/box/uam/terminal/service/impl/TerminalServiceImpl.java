package pw.cdmi.box.uam.terminal.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.terminal.dao.TerminalDAO;
import pw.cdmi.box.uam.terminal.service.TerminalService;
import pw.cdmi.common.domain.Terminal;

@Service
public class TerminalServiceImpl implements TerminalService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalServiceImpl.class);
    
    public static void checkDeviceParamter(HttpServletRequest request) throws InvalidParamterException
    {
        String deviceTypeStr = request.getHeader("x-device-type");
        String deviceSN = request.getHeader("x-device-sn");
        String deviceOS = request.getHeader("x-device-os");
        String deviceName = request.getHeader("x-device-name");
        String deviceAgent = request.getHeader("x-client-version");
        String requestIp = request.getHeader("x-request-ip");
        
        if (StringUtils.isBlank(deviceOS) || StringUtils.isBlank(deviceAgent)
            || StringUtils.isBlank(deviceTypeStr))
        {
            LOGGER.error("invalidate parameter deviceOS:" + deviceOS + " deviceAgent:" + deviceAgent
                + " deviceTypeStr" + deviceTypeStr);
            throw new InvalidParamterException();
        }
        if (!deviceTypeStr.equals(Terminal.CLIENT_TYPE_WEB_STR)
            && (StringUtils.isBlank(deviceName) || StringUtils.isBlank(deviceSN)))
        {
            LOGGER.error("invalidate parameter deviceSN:" + deviceSN + " deviceName:" + deviceName);
            throw new InvalidParamterException();
        }
        if (!deviceTypeStr.equals(Terminal.CLIENT_TYPE_WEB_STR))
        {
            if (deviceSN.length() > 127 || deviceName.length() > 127)
            {
                LOGGER.error("The parameter is too long deviceSN:" + deviceSN + " deviceName:" + deviceName);
                throw new InvalidParamterException();
            }
        }
        if (deviceOS.length() > 64 || deviceAgent.length() > 127)
        {
            LOGGER.error("The parameter is too long deviceOS:" + deviceOS + " deviceAgent:" + deviceAgent);
            throw new InvalidParamterException();
        }
        if (StringUtils.isNotBlank(requestIp))
        {
            if (requestIp.length() > 64)
            {
                LOGGER.error("The parameter is too long requestIp:" + requestIp);
                throw new InvalidParamterException();
            }
        }
        checkDeviceType(deviceTypeStr);
    }
    
    private static void checkDeviceType(String deviceType) throws InvalidParamterException
    {
        if (deviceType.equals(Terminal.CLIENT_TYPE_ANDROID_STR))
        {
            return;
        }
        if (deviceType.equals(Terminal.CLIENT_TYPE_IOS_STR))
        {
            return;
        }
        if (deviceType.equals(Terminal.CLIENT_TYPE_PC_STR))
        {
            return;
        }
        if (deviceType.equals(Terminal.CLIENT_TYPE_WEB_STR))
        {
            return;
        }
        throw new InvalidParamterException();
    }
    
    @Autowired
    private TerminalDAO terminalDAO;
    
    @Override
    public long getAgentCountByLoginAt(String appId, int deviceType, Date beginDate, Date endDate)
    {
        
        return terminalDAO.getAgentCountByLoginAt(appId, deviceType, beginDate, endDate);
    }
    
    @Override
    public int getByUserCount(long cloudUserId)
    {
        int total = terminalDAO.getByUserCount(cloudUserId);
        return total;
    }
    
}

package com.huawei.sharedrive.app.system.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.LicenseException;
import com.huawei.sharedrive.app.system.service.impl.LicenseServiceImpl;
import com.huawei.sharedrive.common.license.CseLicenseConstants;
import com.huawei.sharedrive.common.license.CseLicenseInfo;

@Component("licenseChecker")
public class LicenseChecker
{
    private static boolean isValid = false;
    
    private static Logger logger = LoggerFactory.getLogger(LicenseServiceImpl.class);
    
    private static Calendar getDateFromString(String str)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        try
        {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(str));
        }
        catch (Exception e)
        {
            return null;
        }
        return calendar;
    }
    
    /**
     * @param currentLicense
     * @return
     */
    private static int getMaxTeamspaces(int currentUsers)
    {
        int result = 0;
        if(currentUsers > 1000000)
        {
            result = currentUsers / 1000000 * 100000;
        }
        else if(currentUsers > 100000)
        {
            result = currentUsers / 100000 * 10000;
        }
        else if(currentUsers > 10000)
        {
            result = currentUsers / 10000 * 1000;
        }
        else if(currentUsers > 1000)
        {
            result = currentUsers / 1000 * 100;
        }
        else
        {
            result = currentUsers / 100 * 10;
        }
        return result;
    }
    
    @Autowired
    private LicenseAlarmHelper licenseAlarmHelper;
    
    @Autowired
    private LicenseService licenseService;
    
    public void checkCurrentNode() throws LicenseException
    {
        CseLicenseInfo currentLicense = LicenseServiceImpl.currentLicense();
        if(null == currentLicense)
        {
            throw new LicenseException("Current license is null");
        }
        if(!StringUtils.equals(currentLicense.getDeadline(), CseLicenseConstants.PERMANENT))
        {
            Calendar deadline = getDateFromString(currentLicense.getDeadline());
            if(null == deadline)
            {
                return;
            }
            Calendar now = Calendar.getInstance();
            //日期延长一天，规避0时0分0秒截止的问题
            deadline.add(Calendar.DAY_OF_MONTH, 1);
            if(deadline.before(now))
            {
                try
                {
                    if(isValid)
                    {
                        setIsValid(false);
                        licenseService.loadLastestLicense();
                    }
                }
                catch(Exception e)
                {
                    logger.error("[licenseLog] validate license is invalid.", e);
                }
                throw new LicenseException("deadline is " + currentLicense.getDeadline());
            }
            setIsValid(true);
        }
    }
    
    private static void setIsValid(boolean isValid)
    {
        LicenseChecker.isValid = isValid;
    }
    
    public void checkTeamSpaces(long currentTotalTeamspaces) throws LicenseException
    {
        CseLicenseInfo currentLicense = LicenseServiceImpl.currentLicense();
        if(null == currentLicense)
        {
            throw new LicenseException("Current license is null");
        }
        int allowTeamspaces = getMaxTeamspaces(currentLicense.getUsers()) + currentLicense.getTeamSpaceNumber();
        
        licenseAlarmHelper.checkTeams(currentTotalTeamspaces, allowTeamspaces);
        
        if(currentTotalTeamspaces >= allowTeamspaces)
        {
            throw new LicenseException("Current users will exceed teamspace total: " + allowTeamspaces);
        }
    }
    
    public void checkTotalUsers(long currentTotalUsers) throws LicenseException
    {
        /*CseLicenseInfo currentLicense = LicenseServiceImpl.currentLicense();
        if(null == currentLicense)
        {
            throw new LicenseException("Current license is null");
        }
        
        licenseAlarmHelper.checkUsers((int)currentTotalUsers, currentLicense.getUsers());
        
        if(currentTotalUsers >= currentLicense.getUsers())
        {
            throw new LicenseException("Current users will exceed license total: " + currentLicense.getUsers());
        }*/
    }
    
}

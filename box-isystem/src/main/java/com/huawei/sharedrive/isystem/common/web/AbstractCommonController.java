/**
 * 
 */
package com.huawei.sharedrive.isystem.common.web;

import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;

import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.RuntimeStatus;
import com.huawei.sharedrive.isystem.cluster.domain.ResourceGroup.Status;
import com.huawei.sharedrive.isystem.cluster.service.RegionService;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.util.CSRFTokenManager;

/**
 * 
 * 
 * Controller通用处理类
 * 
 * @author d00199602
 * 
 */
public abstract class AbstractCommonController
{
    @Autowired
    private RegionService regionService;
    
    @Autowired
    protected MessageSource messageSource;
    
    @Autowired
    protected UserLogService userLogService;
    
    @Autowired
    protected Validator validator;
    
    @Value("${copyplolicy.config.ishow}")
    protected String showCopyPlocy;
    
    protected void fillStatus(Model model)
    {
        model.addAttribute("runStatusNormal", RuntimeStatus.Normal.getCode());
        model.addAttribute("runStatusAbnormal", RuntimeStatus.Abnormal.getCode());
        model.addAttribute("runStatusOffline", RuntimeStatus.Offline.getCode());
        model.addAttribute("useStatusInit", Status.Initial.getCode());
    }
    
    /**
     * 区域列表
     * 
     * @param model
     */
    public void fillRegion(Model model)
    {
        model.addAttribute("regionList", regionService.listRegion());
    }
    
    /**
     * token校验
     * 
     * @param token
     */
    protected void checkToken(String token)
    {
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
                .getSession()
                .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
        {
            throw new BusinessException(401, "invalid token");
        }
    }
    
    protected void checkCopyPlocyIsOpen()
    {
        if (StringUtils.isEmpty(showCopyPlocy))
        {
            throw new BusinessException(401, "CopyPolicy is not open");
        }
        if (!StringUtils.equalsIgnoreCase(showCopyPlocy, Boolean.TRUE.toString()))
        {
            throw new BusinessException(401, "CopyPolicy is not open");
        }
    }
    
}

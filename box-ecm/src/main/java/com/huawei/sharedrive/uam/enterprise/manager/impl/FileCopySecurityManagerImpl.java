package com.huawei.sharedrive.uam.enterprise.manager.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import com.huawei.sharedrive.uam.enterprise.domain.FileCopyDelRequest;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurity;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurityResponse;
import com.huawei.sharedrive.uam.enterprise.domain.SecurityRole;
import com.huawei.sharedrive.uam.enterprise.manager.FileCopySecurityManager;
import com.huawei.sharedrive.uam.enterprise.service.FileCopySecurityIdService;
import com.huawei.sharedrive.uam.enterprise.service.FileCopySecurityService;
import com.huawei.sharedrive.uam.enterprise.service.SecurityRoleService;
import com.huawei.sharedrive.uam.exception.ExistFileCopyConfigConflictException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;

@Component
public class FileCopySecurityManagerImpl implements FileCopySecurityManager
{
    
    private static final long DEFAULT_ROLE_ID = -1;
    
    @Autowired
    private FileCopySecurityIdService fileCopyIdService;
    
    @Autowired
    private FileCopySecurityService fileCopyService;
    
    @Autowired
    @Qualifier("messageSource")
    private ResourceBundleMessageSource messageSource;
    
    private Logger logger = LoggerFactory.getLogger(FileCopySecurityManagerImpl.class);
    
    @Autowired
    private SecurityRoleService securityRoleService;
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void addFileCopySecurity(long srcSafeRoleId, long targetSafeRoleId, long accountId)
    {
        FileCopySecurity fileCopySecurity = new FileCopySecurity();
        fileCopySecurity.setSrcSafeRoleId(srcSafeRoleId);
        fileCopySecurity.setTargetSafeRoleId(targetSafeRoleId);
        fileCopySecurity.setAccountId(accountId);
        FileCopySecurity oldFileCopySecurity = fileCopyService.get(fileCopySecurity);
        if (oldFileCopySecurity != null)
        {
            String message = "File of copy configing already existed,accountId :" + accountId;
            logger.warn(message);
            throw new ExistFileCopyConfigConflictException(message);
        }
        long id = fileCopyIdService.getNextId();
        fileCopySecurity.setId(id);
        fileCopyService.create(fileCopySecurity);
        logger.info("Create success,appId : " + accountId);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int delete(FileCopyDelRequest fileCopyDelRequest, long accountId)
    {
        return fileCopyService.delete(fileCopyDelRequest, accountId);
    }
    
    @Override
    public Page<FileCopySecurityResponse> queryForList(PageRequest request, Long srcSafeRoleId,
        Long targetSafeRoleId, Long accountId, Locale locale)
    {
        FileCopySecurity fileCopySecurity = new FileCopySecurity();
        if (srcSafeRoleId != null)
        {
            fileCopySecurity.setSrcSafeRoleId(srcSafeRoleId);
        }
        if (targetSafeRoleId != null)
        {
            fileCopySecurity.setTargetSafeRoleId(targetSafeRoleId);
        }
        if (accountId != null)
        {
            fileCopySecurity.setAccountId(accountId);
        }
        List<FileCopySecurity> copySecurities = fileCopyService.query(request.getLimit(),
            request.getOrder(),
            fileCopySecurity);
        int total = fileCopyService.queryCount(request.getLimit(), fileCopySecurity);
        List<FileCopySecurityResponse> newContents = new ArrayList<FileCopySecurityResponse>(
            copySecurities.size());
        for (FileCopySecurity fcs : copySecurities)
        {
            transResponse(newContents, fcs, locale);
        }
        Page<FileCopySecurityResponse> page = new PageImpl<FileCopySecurityResponse>(newContents, request,
            total);
        return page;
    }
    
    private void transResponse(List<FileCopySecurityResponse> newContents, FileCopySecurity fcs, Locale locale)
    {
        SecurityRole tempSrcRole;
        FileCopySecurityResponse tempResponse = new FileCopySecurityResponse();
        tempResponse.setSrcSafeRoleId(fcs.getSrcSafeRoleId());
        tempResponse.setTargetSafeRoleId(fcs.getTargetSafeRoleId());
        if (fcs.getSrcSafeRoleId() == DEFAULT_ROLE_ID)
        {
            tempResponse.setSrcSafeRoleName(getText("fileCopy.security.any", null, locale));
        }
        else
        {
            SecurityRole role = securityRoleService.getById(fcs.getSrcSafeRoleId());
            if (role != null)
            {
                tempSrcRole = role;
                tempResponse.setSrcSafeRoleName(HtmlUtils.htmlEscape(tempSrcRole.getRoleName()));
            }
        }
        if (fcs.getTargetSafeRoleId() == DEFAULT_ROLE_ID)
        {
            tempResponse.setTargetSafeRoleName(getText("fileCopy.security.any", null, locale));
        }
        else
        {
            SecurityRole role = securityRoleService.getById(fcs.getTargetSafeRoleId());
            if (role != null)
            {
                tempResponse.setTargetSafeRoleName(HtmlUtils.htmlEscape(role.getRoleName()));
            }
        }
        tempResponse.setAccountId(fcs.getAccountId());
        tempResponse.setModifiedAt(fcs.getModifiedAt());
        tempResponse.setCreatedAt(fcs.getCreatedAt());
        newContents.add(tempResponse);
    }
    
    private String getText(String code, Object[] params, Locale locale)
    {
        return messageSource.getMessage(code, params, locale);
    }
}

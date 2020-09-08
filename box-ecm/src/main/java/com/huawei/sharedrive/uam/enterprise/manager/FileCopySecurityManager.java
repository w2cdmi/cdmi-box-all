package com.huawei.sharedrive.uam.enterprise.manager;

import java.util.Locale;

import com.huawei.sharedrive.uam.enterprise.domain.FileCopyDelRequest;
import com.huawei.sharedrive.uam.enterprise.domain.FileCopySecurityResponse;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface FileCopySecurityManager
{
    
    void addFileCopySecurity(long srcSafeRoleId, long targetSafeRoleId, long accountId);
    
    int delete(FileCopyDelRequest fileCopyDelRequest, long accountId);
    
    Page<FileCopySecurityResponse> queryForList(PageRequest request, Long srcSafeRoleId,
        Long targetSafeRoleId, Long accountId, Locale locale);
    
}

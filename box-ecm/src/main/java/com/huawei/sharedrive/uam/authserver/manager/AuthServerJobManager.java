package com.huawei.sharedrive.uam.authserver.manager;

import pw.cdmi.common.domain.enterprise.ldap.LdapNodeFilterConfig;
import pw.cdmi.common.job.exception.JobException;

public interface AuthServerJobManager
{
    void addJobManager(LdapNodeFilterConfig ldapNodeFilterConfig, Long authServerId) throws JobException;
}

package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EnterpriseUserImportor
{
    private EnterpriseUserImportor()
    {
    }
    
    private static final ExecutorService IMPORT_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterpriseUserImportor.class);
    
    public static void doImport(ImportEnterpriseUsersThread importThread)
    {
        Future<?> f = IMPORT_THREAD_EXECUTOR.submit(importThread);
        LOGGER.debug("Submit ImportEnterpriseUsersThread, the future done is " + f.isDone());
    }
    
}

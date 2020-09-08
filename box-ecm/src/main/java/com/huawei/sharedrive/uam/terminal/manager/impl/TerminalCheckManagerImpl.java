package com.huawei.sharedrive.uam.terminal.manager.impl;

import java.security.InvalidParameterException;

import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.openapi.domain.ListTerminalRequest;
import com.huawei.sharedrive.uam.terminal.manager.TerminalCheckManager;

@Component
public class TerminalCheckManagerImpl implements TerminalCheckManager
{
    @Override
    public void checkRequestParameter(ListTerminalRequest listTerminaRequest)
        throws InvalidParameterException
    {
        Integer limit = listTerminaRequest.getLimit();
        Long offset = listTerminaRequest.getOffset();
        if (limit != null && (limit < 1 || limit > ListTerminalRequest.MAX_LIMIT))
        {
            throw new InvalidParameterException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParameterException();
        }
    }
    
}

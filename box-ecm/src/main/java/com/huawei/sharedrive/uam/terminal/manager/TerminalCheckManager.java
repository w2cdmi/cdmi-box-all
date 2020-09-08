package com.huawei.sharedrive.uam.terminal.manager;

import java.security.InvalidParameterException;

import com.huawei.sharedrive.uam.openapi.domain.ListTerminalRequest;

public interface TerminalCheckManager
{
    void checkRequestParameter(ListTerminalRequest listTerminaRequest) throws InvalidParameterException;
    
}

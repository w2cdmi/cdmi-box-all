package com.huawei.sharedrive.uam.openapi.domain.terminal;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.common.domain.Terminal;

public class UpdateTerminalRequest
{
    
    private String deviceSN;

    private Byte status;

    public void checkParam()
    {
        if (status == null)
        {
            throw new InvalidParamterException("[terminal] status is null");
        }
        if (status != Terminal.STATUS_IGNORE && status != Terminal.STATUS_NORMAL)
        {
            throw new InvalidParamterException("[terminal] status is invalid status:" + status);
        }
        if (StringUtils.isBlank(deviceSN))
        {
            throw new InvalidParamterException("[terminal] deviceSN is invalid deviceSN:" + deviceSN);
        }
        if (getDeviceSN().length() > 127)
        {
            throw new InvalidParamterException("[terminal] deviceSN is invalid deviceSN:" + deviceSN);
        }
    }

    public String getDeviceSN()
    {
        return deviceSN;
    }
    
    public Byte getStatus()
    {
        return status;
    }

    public void setDeviceSN(String deviceSN)
    {
        this.deviceSN = deviceSN;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
}

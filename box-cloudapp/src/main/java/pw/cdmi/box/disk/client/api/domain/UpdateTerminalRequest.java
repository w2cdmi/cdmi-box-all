package pw.cdmi.box.disk.client.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;


public class UpdateTerminalRequest
{
    @JsonProperty(value = "deviceSN")
    private String deviceSn;

    private Byte status;
    
    public Byte getStatus()
    {
        return status;
    }

    public void setStatus(Byte status)
    {
        this.status = status;
    }

    public String getDeviceSn()
    {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn)
    {
        this.deviceSn = deviceSn;
    }
    
}

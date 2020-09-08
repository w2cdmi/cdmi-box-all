package pw.cdmi.file.engine.manage.datacenter.domain;

public class DcConfig
{
    private Integer dataserverCenterId;
    
    private String getProtocol;
    
    private String putProtocol;
    
    private String reportip;
    
    private Integer reportport;
    
    public DcConfig(Integer dataserverCenterId, String reportip, Integer reportport, String getProtocol, String putProtocol)
    {
        this.dataserverCenterId = dataserverCenterId;
        this.reportip = reportip;
        this.reportport = reportport;
        this.getProtocol = getProtocol;
        this.putProtocol = putProtocol;
    }
    public Integer getDataserverCenterId()
    {
        return dataserverCenterId;
    }
    public String getGetProtocol()
    {
        return getProtocol;
    }
    public String getPutProtocol()
    {
        return putProtocol;
    }
    public String getReportip()
    {
        return reportip;
    }
    public Integer getReportport()
    {
        return reportport;
    }
    public void setDataserverCenterId(Integer dcid)
    {
        this.dataserverCenterId = dcid;
    }
    public void setGetProtocol(String getProtocol)
    {
        this.getProtocol = getProtocol;
    }
    public void setPutProtocol(String putProtocol)
    {
        this.putProtocol = putProtocol;
    }
    public void setReportip(String reportip)
    {
        this.reportip = reportip;
    }
    public void setReportport(Integer reportport)
    {
        this.reportport = reportport;
    }
    
}

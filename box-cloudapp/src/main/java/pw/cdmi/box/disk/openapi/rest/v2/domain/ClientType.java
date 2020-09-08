package pw.cdmi.box.disk.openapi.rest.v2.domain;

public enum ClientType
{
    
    ANDROID("ANDROID"),
    
    IOS("IOS"),
    
    PLIST("PLIST"),
    
    PC("PC"),
    
    PCCLOUD("PCCLOUD");
    
    private String value;
    
    private ClientType(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
}

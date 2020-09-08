package pw.cdmi.file.engine.object.thrift;

public enum DssLocalStatusEnum
{
    
    NORMAL("normal"),
    
    ABNORNAL("abNormal"),
    
    OFFLINE("offline");
    
    private String value;
    
    private DssLocalStatusEnum(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
}

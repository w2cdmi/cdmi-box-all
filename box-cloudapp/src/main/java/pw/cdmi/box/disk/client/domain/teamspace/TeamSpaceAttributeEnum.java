package pw.cdmi.box.disk.client.domain.teamspace;

public enum TeamSpaceAttributeEnum {

	UPLOADNOTICE("uploadNotice"),
    AUTOPREVIEW("autoPreview");

    private String name;
    
    private TeamSpaceAttributeEnum(String name)
    {
        this.name = name;
    }
    
    public static TeamSpaceAttributeEnum getTeamSpaceConfig(String name)
    {
        for (TeamSpaceAttributeEnum config : TeamSpaceAttributeEnum.values())
        {
            if (config.getName().equals(name))
            {
                return config;
            }
        }
        return null;
    }
    
    public String getName()
    {
        return name;
    }
    
}

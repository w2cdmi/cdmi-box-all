package pw.cdmi.box.disk.favorite.common;

public enum FavoriteType
{
    
    MYFILE(1, "MyFiles"), TEAMSPACE(2, "TeamSapce"), SHARE(3, "Share"), LINK(4, "ShareLink"), LINK_FOR_OTHER(
        4, "favor.other"), MYFILETIP(5, "tip.myFile"), TEAMSPACETIP(6, "tip.teamSpace"), SHARETIP(7,
        "tip.share"), LINKTIP(8, "tip.link");
    
    private int code;
    
    private String name;
    
    private FavoriteType(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public String getName()
    {
        return name;
    }
    
    public static FavoriteType parseType(int code)
    {
        for (FavoriteType f : FavoriteType.values())
        {
            if (f.getCode() == code)
            {
                return f;
            }
        }
        return null;
    }
}

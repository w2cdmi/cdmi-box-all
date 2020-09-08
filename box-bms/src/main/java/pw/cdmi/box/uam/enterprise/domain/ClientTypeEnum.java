package pw.cdmi.box.uam.enterprise.domain;

import java.util.Locale;

import pw.cdmi.box.uam.adminlog.dao.impl.LogLanguageHelper;
import pw.cdmi.core.utils.BundleUtil;

public enum ClientTypeEnum
{
    
    CLIENT_ALL(-1, "clienttype.all"), CLIENT_WEB(0, "clienttype.web"), CLIENT_PC(1, "clienttype.pc"), CLIENT_ANDROID(
        2, "clienttype.android"), CLIENT_IOS(3, "clienttype.ios");
    
    private int code;
    
    private String name;
    
    private ClientTypeEnum(int code, String name)
    {
        this.code = code;
        this.name = name;
    }
    
    public int getCode()
    {
        return this.code;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public static ClientTypeEnum parseType(int code)
    {
        for (ClientTypeEnum s : ClientTypeEnum.values())
        {
            if (s.getCode() == code)
            {
                return s;
            }
        }
        return null;
    }
    
    private static final String RESOURCE_FEILE = "messages";
    
    public String getDetails(String[] params)
    {
        String details = BundleUtil.getText(RESOURCE_FEILE,
            LogLanguageHelper.getLanguage(),
            this.name,
            params);
        return details;
    }
    
    public String getDetails(Locale locale, String[] params)
    {
        String details = BundleUtil.getText(RESOURCE_FEILE, locale, this.name, params);
        return details;
    }
    
    static
    {
        BundleUtil.addBundle(RESOURCE_FEILE, new Locale[]{Locale.ENGLISH, Locale.CHINESE});
    }
}

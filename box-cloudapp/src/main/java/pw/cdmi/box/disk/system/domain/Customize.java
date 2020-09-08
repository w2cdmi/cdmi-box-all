package pw.cdmi.box.disk.system.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.common.domain.BasicConfig;
import pw.cdmi.common.domain.SystemConfig;

public class Customize extends BasicConfig implements Serializable
{
    private static final long serialVersionUID = 6715844470358523896L;
    
    private String copyright;
    
    private String icon;
    
    private String iconContentType;
    
    private String iconFormatName;
    
    private String logo;
    
    private String logoContentType;
    
    private String logoFormatName;
    
    private String systemName;
    
    /**
     * 
     * @param itemList
     * @return
     */
    public static Customize buildMailServer(List<SystemConfig> itemList)
    {
        Map<String, String> map = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        for (SystemConfig configItem : itemList)
        {
            map.put(configItem.getId(), configItem.getValue());
        }
        Customize customize = new Customize();
        customize.systemName = map.get("customize.systemName");
        customize.logo = map.get("customize.logo");
        customize.logoContentType = map.get("customize.logoContentType");
        customize.logoFormatName = map.get("customize.logoFormatName");
        customize.icon = map.get("customize.icon");
        customize.iconContentType = map.get("customize.iconContentType");
        customize.iconFormatName = map.get("customize.iconFormatName");
        customize.copyright = map.get("customize.copyright");
        return customize;
    }
    
    public String getCopyright()
    {
        return copyright;
    }
    
    public String getIcon()
    {
        return icon;
    }
    
    public String getIconContentType()
    {
        return iconContentType;
    }
    
    public String getIconFormatName()
    {
        return iconFormatName;
    }
    
    public String getLogo()
    {
        return logo;
    }
    
    public String getLogoContentType()
    {
        return logoContentType;
    }
    
    public String getLogoFormatName()
    {
        return logoFormatName;
    }
    
    public String getSystemName()
    {
        return systemName;
    }
    
    public void setCopyright(String copyright)
    {
        this.copyright = copyright;
    }
    
    public void setIcon(String icon)
    {
        this.icon = icon;
    }
    
    public void setIconContentType(String iconContentType)
    {
        this.iconContentType = iconContentType;
    }
    
    public void setIconFormatName(String iconFormatName)
    {
        this.iconFormatName = iconFormatName;
    }
    
    public void setLogo(String logo)
    {
        this.logo = logo;
    }
    
    public void setLogoContentType(String logoContentType)
    {
        this.logoContentType = logoContentType;
    }
    
    public void setLogoFormatName(String logoFormatName)
    {
        this.logoFormatName = logoFormatName;
    }
    
    public void setSystemName(String systemName)
    {
        this.systemName = systemName;
    }
    
    public List<SystemConfig> toConfigItem()
    {
        List<SystemConfig> list = new ArrayList<SystemConfig>(BusinessConstants.INITIAL_CAPACITIES);
        list.add(new SystemConfig(getAppId(), "customize.systemName", systemName));
        list.add(new SystemConfig(getAppId(), "customize.logo", logo));
        list.add(new SystemConfig(getAppId(), "customize.logoContentType", logoContentType));
        list.add(new SystemConfig(getAppId(), "customize.logoFormatName", logoFormatName));
        list.add(new SystemConfig(getAppId(), "customize.icon", icon));
        list.add(new SystemConfig(getAppId(), "customize.iconContentType", iconContentType));
        list.add(new SystemConfig(getAppId(), "customize.iconFormatName", iconFormatName));
        list.add(new SystemConfig(getAppId(), "customize.copyright", copyright));
        return list;
    }
}

package pw.cdmi.box.disk.enterpriseindividual.domain;

import java.io.File;
import java.io.FilenameFilter;

public class WebIconPcLogoFileter implements FilenameFilter
{
    
    private long accountId;
    
    private static final String ICON_NAME_PREFIX = "logo";
    
    private static final String ICON_NAME_SUFFIX = ".ico";
    
    public WebIconPcLogoFileter(long accountId)
    {
        this.accountId = accountId;
    }
    
    @Override
    public boolean accept(File dir, String name)
    {
        return name.startsWith(ICON_NAME_PREFIX + accountId) && name.endsWith(ICON_NAME_SUFFIX);
    }
}

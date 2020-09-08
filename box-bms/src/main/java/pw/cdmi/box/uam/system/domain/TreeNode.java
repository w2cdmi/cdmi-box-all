package pw.cdmi.box.uam.system.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import pw.cdmi.box.uam.util.PropertiesUtils;

public class TreeNode implements Serializable
{
    
    private static final long serialVersionUID = 8884560887988263137L;
    
    public static final int LDAP_SEARCH_USER_LIMIT = Integer.parseInt(PropertiesUtils.getProperty("ldap.search.user.limit",
        "10000"));
    
    public static final int LDAP_SEARCH_GROUP_LIMIT = Integer.parseInt(PropertiesUtils.getProperty("ldap.search.group.limit",
        "1000"));
    
    public static final String SORT_BY = PropertiesUtils.getProperty("ldap.search.sortby", "name");
    
    static final List<String> LDAPSPECIALCHARACTERS = new ArrayList<String>(10);
    static
    {
        LDAPSPECIALCHARACTERS.add("#");
        LDAPSPECIALCHARACTERS.add(",");
        LDAPSPECIALCHARACTERS.add("+");
        LDAPSPECIALCHARACTERS.add("\\");
        LDAPSPECIALCHARACTERS.add("<");
        LDAPSPECIALCHARACTERS.add(">");
        LDAPSPECIALCHARACTERS.add(";");
        LDAPSPECIALCHARACTERS.add("\"");
        LDAPSPECIALCHARACTERS.add("\'");
        LDAPSPECIALCHARACTERS.add("/");
    }
    
    private String name;
    
    private String baseDn;
    
    private boolean checked;
    
    private int page;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setBaseDn(String baseDn)
    {
        this.baseDn = baseDn;
    }
    
    public String getBaseDn()
    {
        return baseDn;
    }
    
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }
    
    public boolean isChecked()
    {
        return checked;
    }
    
    public boolean isParent()
    {
        return true;
    }
    
    public void setPage(int page)
    {
        this.page = page;
    }
    
    public int getPage()
    {
        return page;
    }
    
    public static String formatDn(LdapContext ldapContext, String baseDn)
    {
        NameParser parser;
        Name contextBase;
        try
        {
            parser = ldapContext.getNameParser("");
            contextBase = parser.parse(baseDn);
            
        }
        catch (NamingException e)
        {
            return baseDn;
        }
        Enumeration<String> dnList = contextBase.getAll();
        List<String> nameList = new ArrayList<String>(16);
        String dnName = null;
        while (dnList.hasMoreElements())
        {
            dnName = dnList.nextElement();
            nameList.add(dnName);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = nameList.size() - 1; i >= 0; i--)
        {
            sb.append(nameList.get(i));
            if (i > 0)
            {
                sb.append(',');
            }
        }
        String dn = sb.toString();
        dn = dn.replaceAll("\\\\", "\\\\\\\\");
        dn = formatDnSlash(dn);
        dn = dn.replaceAll("/", "\\\\/");
        dn = dn.replaceAll("\\#", "#");
        dn = dn.replaceAll("\\,", ",");
        dn = dn.replaceAll("\\+", "+");
        dn = dn.replaceAll("\\<", "<");
        dn = dn.replaceAll("\\>", ">");
        dn = dn.replaceAll("\\;", ";");
        
        return dn;
    }
    
    private static String formatDnSlash(String dn)
    {
        int length = dn.length() - 2;
        String preStr = null;
        String nextStr = null;
        for (int i = 0; i < length; i++)
        {
            preStr = dn.substring(i, i + 1);
            nextStr = dn.substring(i + 1, i + 2);
            if ("\\".equals(preStr) && !LDAPSPECIALCHARACTERS.contains(nextStr))
            {
                dn = dn.substring(0, i) + dn.substring(i + 1, dn.length());
                length--;
            }
        }
        return dn;
    }
    
}

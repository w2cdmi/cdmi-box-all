package pw.cdmi.box.disk.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.exception.BadRquestException;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.InvalidParamException;

public final class FilesCommonUtils
{
    private FilesCommonUtils()
    {
        
    }
    
    public static final int MAX_LENGTH_NODENAME = 255;
    
    public static final String SUSPENSION_POINTS = "...";
    
    public static final String BLANK_HYPHEN = "-";
    
    private static final String[] IMG_TYPE_ARRAY = {"jpg", "jpeg", "gif", "bmp", "png"};
    
    private static Logger logger = LoggerFactory.getLogger(FilesCommonUtils.class);
    
    private static final int MAX_ENCRYPTKEY_LENGTH = 255;
    
    private static final Pattern PATTERN_FILENAME = Pattern.compile("[^/\\\\]{1,246}");
    
    private static final Pattern PATTERN_NON_NEGATIVE_INTEGER = Pattern.compile("^\\d+$");
    
    private static final Pattern PATTERN_POSITIVE_INTEGER = Pattern.compile("^[0-9]*[1-9][0-9]*$");
    
    private static final Pattern PATTERN_SHA1 = Pattern.compile("[A-Za-z0-9]{40,40}");
    
    private static final Pattern PATTERN_TEAMNAME = Pattern.compile("[^•!#/<>%?'\"&,;\\\\]{1,255}");
    
    private static final Pattern PATTERN_GROUPNAME = Pattern.compile("[^•!#/<>%?'\"&,;\\\\]{1,255}");//
    
    /**
     * 
     * @param offset
     * @param limit
     * @return
     * @throws BadRquestException
     */
    public static Limit checkAndSetLimitObj(Integer offset, Integer limit) throws InvalidParamException
    {
        Limit limitObj = new Limit();
        
        if (offset == null)
        {
            offset = 0;
        }
        
        if (limit == null)
        {
            limit = Limit.DEFAULT_LENGTH;
        }
        
        if (offset < 0 || limit <= 0 || limit > Limit.MAX_LENGTH)
        {
            logger.error("offset or limit invalid: offset = " + offset + ", limit = " + limit);
            throw new InvalidParamException();
        }
        
        limitObj.setLength(limit);
        limitObj.setOffset(Long.valueOf(offset));
        return limitObj;
    }
    
    /**
     * 
     * @param encryptKey
     * @throws BadRquestException
     */
    public static void checkEncryptKey(String encryptKey) throws BadRquestException
    {
        String errorMsg = "Invalid encrypt key : " + encryptKey;
        if (StringUtils.isBlank(encryptKey) || encryptKey.length() > MAX_ENCRYPTKEY_LENGTH)
        {
            throw new BadRquestException(errorMsg);
        }
    }
    
    /**
     * 
     * @param name
     * @throws BadRquestException
     */
    public static void checkNodeNameVaild(String name) throws BadRquestException
    {
        if (!isFormatFileName(name) || name.charAt(name.length() - 1) == '.')
        {
            throw new InvalidParamException("Invalid node name: " + name);
        }
    }
    
    /**
     * 
     * @param number
     * @throws BadRquestException
     */
    
    public static void checkNonNegativeIntegers(Integer... numbers) throws InvalidParamException
    {
        String errorMsg = null;
        
        Matcher m = null;
        for (Integer temp : numbers)
        {
            errorMsg = temp + " is not a non-negative integer";
            if (temp == null)
            {
                throw new InvalidParamException(errorMsg);
            }
            
            m = PATTERN_NON_NEGATIVE_INTEGER.matcher(String.valueOf(temp));
            if (!m.matches())
            {
                throw new InvalidParamException(errorMsg);
            }
        }
    }
    
    /**
     * 
     * @param number
     * @throws BadRquestException
     */
    public static void checkNonNegativeIntegers(Long... numbers) throws BadRquestException
    {
        String errorMsg = null;
        
        Matcher m = null;
        for (Long temp : numbers)
        {
            errorMsg = temp + " is not a non-negative integer";
            if (temp == null)
            {
                throw new BadRquestException(errorMsg);
            }
            
            m = PATTERN_NON_NEGATIVE_INTEGER.matcher(String.valueOf(temp));
            if (!m.matches())
            {
                throw new BadRquestException(errorMsg);
            }
        }
    }
    
    public static void checkPositiveIntegers(Long... numbers) throws BadRquestException
    {
        String errorMsg;
        Matcher m;
        for (Long temp : numbers)
        {
            errorMsg = temp + " is not a positive integer";
            if (temp == null)
            {
                throw new BadRquestException(errorMsg);
            }
            
            m = PATTERN_POSITIVE_INTEGER.matcher(String.valueOf(temp));
            if (!m.matches())
            {
                throw new BadRquestException(errorMsg);
            }
        }
    }
    
    /**
     * 
     * @param name
     * @throws BadRquestException
     */
    public static void checkTeamNameVaild(String name) throws InvalidParamException
    {
        if (StringUtils.isEmpty(name) || !isFormatTeamName(name))
        {
            throw new InvalidParamException("Invalid Team name: " + name);
        }
    }
    
    public static void checkGroupNameValid(String name) throws InvalidParamException
    {
        if (StringUtils.isEmpty(name) || !isFormatGroupName(name))
        {
            throw new InvalidParamException("Invalid Group Name: " + name);
        }
    }
    
    public static void checkGroupDescriValid(String description) throws InvalidParamException
    {
        if (StringUtils.isEmpty(description))
        {
            description = BLANK_HYPHEN;
        }
        
        if (description.length() > 255)
        {
            throw new InvalidParamException("Invalid Group Description:" + description);
        }
    }
    
    /**
     * 
     * @param name
     * @throws BadRquestException
     */
    public static boolean checkTeamNameVaildIgnoreNull(String name) throws InvalidParamException
    {
        if (!isFormatTeamName(name))
        {
            throw new InvalidParamException("Invalid Team name: " + name);
        }
        
        Matcher m = PATTERN_TEAMNAME.matcher(name);
        return m.matches();
    }
    
    /**
     * 
     * 
     * @param sha1
     * @throws BadRquestException
     */
    public static void checkVaildSha1(String sha1) throws BaseRunException
    {
        Matcher m = PATTERN_SHA1.matcher(sha1);
        if (!m.matches())
        {
            throw new InvalidParamException();
        }
    }
    
    public static String decodeUft8Value(String name) throws BaseRunException
    {
        if (StringUtils.isBlank(name))
        {
            return name;
        }
        
        try
        {
            return URLDecoder.decode(name, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            String msg = "name is invaild,name:" + name;
            logger.error(msg, e);
            throw new BadRquestException(e);
        }
        
    }
    
    public static String encodeUft8Value(String name) throws BaseRunException
    {
        if (StringUtils.isBlank(name))
        {
            return name;
        }
        
        try
        {
            return URLEncoder.encode(name, "utf-8").replaceAll("\\+", "%20");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("name is invaild,name:" + name, e);
            throw new BadRquestException(e);
        }
        
    }
    
    /**
     * 
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName)
    {
        if (fileName.lastIndexOf(".") == -1)
        {
            return "";
        }
        if ('.' == fileName.indexOf(fileName.length() - 1))
        {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    /**
     * 
     * @param oldName
     * @param length
     * @return
     */
    public static String getLimitedLengthFileName(String oldName, int length)
    {
        int oldLength = oldName.length();
        if (oldLength <= length)
        {
            return oldName;
        }
        StringBuilder sb = new StringBuilder(oldName);
        int lastPos = oldName.lastIndexOf('(');
        int moreLength = oldLength - lastPos;
        int start = lastPos - moreLength - SUSPENSION_POINTS.length();
        sb.replace(start, lastPos, SUSPENSION_POINTS);
        return sb.toString();
    }
    
    public static String getNewName(byte type, String name, int i)
    {
        if (INode.TYPE_FILE == type && name.lastIndexOf('.') != -1)
        {
            int lastIndex = name.lastIndexOf(".");
            return getLimitedLengthFileName(name.substring(0, lastIndex) + '(' + i + ')'
                + name.substring(lastIndex),
                MAX_LENGTH_NODENAME);
        }
        String preName = name;
        if (')' == preName.charAt(preName.length() - 1))
        {
            int lastIndex = preName.lastIndexOf("(");
            if (lastIndex != -1)
            {
                String sn = preName.substring(lastIndex, preName.length() - 1);
                try
                {
                    int isn = Integer.parseInt(sn) + 1;
                    return getLimitedLengthFileName(preName.substring(0, lastIndex) + '(' + isn + ')',
                        MAX_ENCRYPTKEY_LENGTH);
                }
                catch (NumberFormatException e)
                {
                    return getLimitedLengthFileName(preName + '(' + i + ')', MAX_LENGTH_NODENAME);
                }
            }
        }
        return getLimitedLengthFileName(preName + '(' + i + ')', MAX_LENGTH_NODENAME);
    }
    
    public static boolean isImage(String fileName)
    {
        if (null == fileName || !fileName.contains("."))
        {
            return false;
        }
        String fix = fileName.substring(fileName.lastIndexOf(".") + 1);
        for (String img : IMG_TYPE_ARRAY)
        {
            if (img.equalsIgnoreCase(fix))
            {
                return true;
            }
        }
        return false;
    }
    
    public static String transferString(String source)
    {
        if (StringUtils.isBlank(source))
        {
            return "";
        }
        return source.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
    }
    
    public static String transferStringForSql(String source)
    {
        if (StringUtils.isBlank(source))
        {
            return "";
        }
        
        return source.replaceAll("\\\\", "\\\\\\\\\\\\\\\\")
            .replaceAll("'", "\\\\'")
            .replaceAll("%", "\\\\%")
            .replaceAll("\"", "\\\\\"")
            .replaceAll("_", "\\\\_");
    }
    
    private static boolean isFormatFileName(String name)
    {
        if (StringUtils.isBlank(name))
        {
            return false;
        }
        
        Matcher m = PATTERN_FILENAME.matcher(name);
        return m.matches();
    }
    
    private static boolean isFormatTeamName(String name)
    {
        Matcher m = PATTERN_TEAMNAME.matcher(name);
        return m.matches();
    }
    
    private static boolean isFormatGroupName(String name)
    {
        Matcher m = PATTERN_GROUPNAME.matcher(name);
        return m.matches();
    }
    
    public static boolean isFolderType(int type)
    {
        return INode.TYPE_FOLDER_ALL == type || INode.TYPE_FOLDER == type|| INode.TYPE_WECHAT == type
            || INode.TYPE_BACKUP_COMPUTER == type || INode.TYPE_BACKUP_DISK == type || INode.TYPE_MIGRATION == type;
    }
    
    public static boolean isBackupFolderType(int type)
    {
        return INode.TYPE_BACKUP_COMPUTER == type || INode.TYPE_BACKUP_DISK == type;
    }
}

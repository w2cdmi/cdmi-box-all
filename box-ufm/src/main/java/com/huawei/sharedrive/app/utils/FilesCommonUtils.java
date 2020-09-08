package com.huawei.sharedrive.app.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huawei.sharedrive.app.exception.BadRequestException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;

import pw.cdmi.box.domain.Limit;

public final class FilesCommonUtils
{
    /** 文件夹和文件最大名称长度 */
    public static final int MAX_LENGTH_NODENAME = 246;
    
    /** 省略号 */
    public static final String SUSPENSION_POINTS = "...";
    
    /** 支持缩略图显示的图片类型 */
    private static final String[] IMG_TYPE_ARRAY = {"jpg", "jpeg", "gif", "bmp", "png"};
    
    private static final String[] FOLDER_TYPE_EXT_ATTR = {INode.TYPE_BACKUP_COMPUTER_STR,
        INode.TYPE_BACKUP_DISK_STR, INode.TYPE_BACKUP_EMAIL_STR};
    
    private static Logger logger = LoggerFactory.getLogger(FilesCommonUtils.class);
    
    /** 加密密鈅最大長度 */
    private static final int MAX_ENCRYPTKEY_LENGTH = 255;
    
    /** 文件名、目录名正则表达式 */
    private static final Pattern PATTERN_FILENAME = Pattern.compile("[^/\\\\]{1,246}");
    
    /** MD5值正则表达式 */
    private static final Pattern PATTERN_MD5 = Pattern.compile("[A-Za-z0-9]{32,32}");
    
    /** 非负整数正则表达式 */
    private static final Pattern PATTERN_NON_NEGATIVE_INTEGER = Pattern.compile("^\\d+$");
    
    /** sha1值正则表达式 */
    private static final Pattern PATTERN_SHA1 = Pattern.compile("[A-Za-z0-9]{40,40}");
    
    /** 团队空间名正则表达式 */
    private static final Pattern PATTERN_TEAMNAME = Pattern.compile("[^•!#/<>%?'\"&,;\\\\]{1,255}");
    
    /** MD5值正则表达式 */
    private static final Pattern PATTERN_LINKCODE = Pattern.compile("[a-z0-9]{1,50}");
    
    private FilesCommonUtils()
    {
        
    }
    
    /**
     * 用于分页参数合法性校验
     * 
     * @param offset
     * @param limit
     * @return
     * @throws BadRequestException
     */
    public static Limit checkAndSetLimitObj(Long offset, Integer limit) throws InvalidParamException
    {
        Limit limitObj = new Limit();
        
        // 判断参数是否为空，否则设置默认值
        if (offset == null)
        {
            offset = 0L;
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
        limitObj.setOffset(offset);
        return limitObj;
    }
    
    /**
     * 加密密钥合法性校验
     * 
     * @param encryptKey
     * @throws BadRequestException
     */
    public static void checkEncryptKey(String encryptKey) throws BadRequestException
    {
        if (StringUtils.isBlank(encryptKey) || encryptKey.length() > MAX_ENCRYPTKEY_LENGTH)
        {
            throw new BadRequestException("Bad encryptKey format");
        }
    }
    
    /**
     * 判断文件/目录名是否合法
     * 
     * @param name
     * @throws BadRequestException
     */
    public static void checkNodeNameVaild(String name) throws InvalidParamException
    {
        if (!isFormatFileName(name) || name.charAt(name.length() - 1) == '.')
        {
            throw new InvalidParamException("Invalid node name: " + name);
        }
    }
    
    /**
     * 校验是否为非负整数
     * 
     * @param number
     * @throws BadRequestException
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
     * 校验是否为非负整数
     * 
     * @param number
     * @throws BadRequestException
     */
    public static void checkNonNegativeIntegers(Number... numbers) throws InvalidParamException
    {
        String errorMsg = null;
        
        Matcher m = null;
        for (Number temp : numbers)
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
     * 判断团队空间名是否合法
     * 
     * @param name
     * @throws BadRequestException
     */
    public static void checkTeamNameVaild(String name) throws InvalidParamException
    {
        if (StringUtils.isEmpty(name) || !isFormatTeamName(name))
        {
            throw new InvalidParamException("Invalid Team name: " + name);
        }
    }
    
    /**
     * 判断团队空间名是否合法
     * 
     * @param name
     * @throws BadRequestException
     */
    public static void checkTeamNameVaildIgnoreNull(String name) throws InvalidParamException
    {
        if (!isFormatTeamName(name))
        {
            throw new InvalidParamException("Invalid Team name: " + name);
        }
    }
    
    /**
     * 判断外链码是否合法
     * 
     * @param name
     * @throws BadRequestException
     */
    public static void checkLinkCodeVaild(String linkCode) throws InvalidParamException
    {
        if (StringUtils.isBlank(linkCode))
        {
            throw new InvalidParamException("linkCode is blank");
        }
        
        Matcher m = PATTERN_LINKCODE.matcher(linkCode);
        if (!m.matches())
        {
            throw new InvalidParamException("linkCode is invalid:" + linkCode);
        }
    }
    
    /**
     * md5值合法性校驗
     * 
     * @param sha1
     * @throws BadRequestException
     */
    public static void checkVaildMD5(String md5) throws BaseRunException
    {
        Matcher m = PATTERN_MD5.matcher(md5);
        if (!m.matches())
        {
            throw new InvalidParamException();
        }
    }
    
    /**
     * sha1值合法性校驗
     * 
     * @param sha1
     * @throws BadRequestException
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
            throw new BadRequestException(e);
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
            throw new BadRequestException(e);
        }
        
    }
    
    /**
     * 获取文件后缀名
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
        if (fileName.charAt(fileName.length() - 1) == '.')
        {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    /**
     * 获取受限长度的文件名称
     * 
     * @param oldName
     * @param length
     * @return
     */
    public static String getLimitedLengthFileName(String oldName, byte nodeType, int length)
    {
        int oldLength = oldName.length();
        if (oldLength <= length)
        {
            return oldName;
        }
        StringBuilder sb = new StringBuilder(oldName);
        int lastPos = oldName.lastIndexOf('(');
        int moreLength = oldLength - lastPos;
        if (INode.TYPE_FILE == nodeType && oldName.lastIndexOf('.') != -1)
        {
            int prefixLength = oldName.length() - oldName.lastIndexOf(".");
            moreLength -= prefixLength;
        }
        int start = lastPos - moreLength - SUSPENSION_POINTS.length();
        sb.replace(start, lastPos, SUSPENSION_POINTS);
        return sb.toString();
    }
    
    /**
     * 获取新名字，采用在原名后加(i)的方式
     * 
     * @param name
     * @param i
     * @return 新名字
     */
    public static String getNewName(byte type, String name, int i)
    {
        String preName = null;
        int lastIndex = -1;
        if (INode.TYPE_FILE == type && name.lastIndexOf('.') != -1)
        {
            lastIndex = name.lastIndexOf(".");
            return getLimitedLengthFileName(name.substring(0, lastIndex) + '(' + i + ')'
                + name.substring(lastIndex),
                type,
                MAX_LENGTH_NODENAME);
        }
        preName = name;
        if (preName.charAt(preName.length() - 1) == ')')
        {
            lastIndex = preName.lastIndexOf("(");
            if (lastIndex != -1)
            {
                String sn = preName.substring(lastIndex, preName.length() - 1);
                try
                {
                    int isn = Integer.parseInt(sn) + 1;
                    return getLimitedLengthFileName(preName.substring(0, lastIndex) + '(' + isn + ')',
                        type,
                        MAX_ENCRYPTKEY_LENGTH);
                }
                catch (NumberFormatException e)
                {
                    return getLimitedLengthFileName(preName + '(' + i + ')', type, MAX_LENGTH_NODENAME);
                }
            }
        }
        return getLimitedLengthFileName(preName + '(' + i + ')', type, MAX_LENGTH_NODENAME);
    }
    
    /**
     * 判断文件是否为图片类型
     * 
     * @param fileName
     * @return
     */
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
    
    public static boolean isValidFolderExtArr(String name)
    {
        if (null == name)
        {
            return false;
        }
        for (String item : FOLDER_TYPE_EXT_ATTR)
        {
            if (item.equals(name))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 解析整文件MD5和取样MD5, 格式为"MD5:xxx;BlockMD5:yyy"
     * 
     * @param md5Str
     * @return
     */
    public static Map<String, String> parseMD5(String md5Str)
    {
        Map<String, String> map = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        String[] md5Array = md5Str.split(";");
        String[] tempMd5 = null;
        for (String temp : md5Array)
        {
            tempMd5 = temp.split(":");
            if (tempMd5.length > 1)
            {
                map.put(tempMd5[0], tempMd5[1]);
            }
        }
        return map;
    }
    
    /**
     * V2版本RestFileInfo对象新增历史版本总数字段versions,原version字段修改为objectId,
     * 此方法为实现V1,V2版本共用RestFileInfo对象做的兼容处理
     * 
     * @param node
     */
    public static void setNodeVersionsForV2(INode node)
    {
        if (StringUtils.isNotBlank(node.getVersion()))
        {
            node.setVersions(Integer.parseInt(node.getVersion()));
            node.setVersion(null);
        }
    }
    
    public static String transferString(String source)
    {
        if (StringUtils.isBlank(source))
        {
            return "";
        }
        return source.replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
    }
    
    /**
     * 用于sql查询时的特殊字符转换
     * 
     * @param source
     * @return
     */
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
    
    public static boolean isFolderType(int type)
    {
        return type<=0;
    }
    
    public static boolean isBackupFolderType(int type)
    {
        return INode.TYPE_BACKUP_COMPUTER == type || INode.TYPE_BACKUP_DISK == type;
    }
    
    public static boolean isEmailBackupFolderType(int type)
    {
        return INode.TYPE_BACKUP_EMAIL == type;
    }
}

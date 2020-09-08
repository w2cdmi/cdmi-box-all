package com.huawei.sharedrive.uam.cmb.oa.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.cmb.oa.domain.CMBOrgInfo;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBSapUser;
import com.huawei.sharedrive.uam.cmb.oa.domain.CMBUserRelationInfo;

public class CMBOAUtil
{
    private static Logger LOGGER = LoggerFactory.getLogger(CMBOAUtil.class);
    
    public static boolean compareCMBOrgInfo(CMBOrgInfo org1, CMBOrgInfo org2)
    {
        if (null == org1 || null == org2)
        {
            return false;
        }
        return EqualsBuilder.reflectionEquals(org1, org2);
    }
    
    public static boolean compareCMBSapUser(CMBSapUser user1, CMBSapUser user2)
    {
        if (null == user1 || null == user2)
        {
            return false;
        }
        return EqualsBuilder.reflectionEquals(user1, user2);
    }
    
    public static boolean compareUserRelation(CMBUserRelationInfo selCMBUserRelationInfo,
        CMBUserRelationInfo cmbUserRelationInfo)
    {
        boolean isValueCompared = true;
        try
        {
            if (null == selCMBUserRelationInfo || null == cmbUserRelationInfo)
            {
                return false;
            }
            if (!selCMBUserRelationInfo.getGroupId().equals(cmbUserRelationInfo.getGroupId()))
            {
                return false;
            }
            if (!selCMBUserRelationInfo.getName().equals(cmbUserRelationInfo.getName()))
            {
                return false;
            }
            if (selCMBUserRelationInfo.getOrgId() != cmbUserRelationInfo.getOrgId())
            {
                return false;
            }
            if (!selCMBUserRelationInfo.getUserId().equals(cmbUserRelationInfo.getUserId()))
            {
                return false;
            }
            if (selCMBUserRelationInfo.getUserOrd() != cmbUserRelationInfo.getUserOrd())
            {
                return false;
            }
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.error(e.toString());
            isValueCompared = false;
        }
        return isValueCompared;
    }
    
    public static String convertToFirstSpell(String name)
    {
        String convertName = "";
        if (StringUtils.isBlank(name))
        {
            return convertName;
        }
        name = name.trim();
        try
        {
            char[] nameChar = name.toCharArray();
            if (null == nameChar)
            {
                return "";
            }
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nameChar.length; i++)
            {
                convertAndJoinPinyin(nameChar, format, sb, i);
            }
            return sb.toString();
        }
        catch (BadHanyuPinyinOutputFormatCombination e)
        {
            LOGGER.error("convert failed name:" + name, e);
            return "";
        }
    }
    
    private static void convertAndJoinPinyin(char[] nameChar, HanyuPinyinOutputFormat format,
        StringBuilder sb, int i) throws BadHanyuPinyinOutputFormatCombination
    {
        String[] nameCharString;
        String firstString;
        if (nameChar[i] > 128)
        {
            nameCharString = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], format);
            if (null != nameCharString && nameCharString.length > 0)
            {
                firstString = nameCharString[0];
                if (StringUtils.trimToEmpty(firstString).length() > 0)
                {
                    sb.append(firstString.charAt(0));
                }
            }
        }
        else
        {
            sb.append(nameChar[i]);
        }
    }
    
    public static String convertToSpell(String name)
    {
        String convertName = "";
        if (StringUtils.isBlank(name))
        {
            return convertName;
        }
        name = name.trim();
        try
        {
            
            char[] nameChar = name.toCharArray();
            if (null == nameChar || nameChar.length == 0)
            {
                return "";
            }
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            for (int i = 0; i < nameChar.length; i++)
            {
                convertName = appendChar(convertName, nameChar, format, i);
            }
        }
        catch (BadHanyuPinyinOutputFormatCombination e)
        {
            LOGGER.error("convert failed name:" + name, e);
            convertName = "";
        }
        return convertName;
    }
    
    private static String appendChar(String convertName, char[] nameChar, HanyuPinyinOutputFormat format,
        int i) throws BadHanyuPinyinOutputFormatCombination
    {
        if (nameChar[i] > 128)
        {
            String[] pinyinString = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], format);
            if (null != pinyinString && pinyinString.length > 0)
            {
                if (pinyinString[0] != null)
                {
                    convertName += pinyinString[0];
                }
            }
        }
        else
        {
            convertName += nameChar[i];
        }
        return convertName;
    }
}

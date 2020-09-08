package com.huawei.sharedrive.uam.cmb.sso.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.uam.cmb.control.manager.Constants;

public class CMBCertUtils
{
    private static Logger LOGGER = LoggerFactory.getLogger(CMBCertUtils.class);
    
    private static final String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    
    private static Certificate cert = null;
    
    public static String verifySign(String dataBase64, String tokenBase64)
    {
        String data = "";
        if (StringUtils.isBlank(dataBase64) || StringUtils.isBlank(tokenBase64))
        {
            return data;
        }
        FileInputStream fs = null;
        try
        {
            byte[] dataBase64Byte = dataBase64.getBytes();
            byte[] plainTextByte = new Base64().decode(dataBase64Byte);
            String dataByte = new String(plainTextByte, "GBK");
            byte[] tokenBase64Byte = tokenBase64.getBytes();
            byte[] signatureByte = new Base64().decode(tokenBase64Byte);
            if (null == cert)
            {
                LOGGER.info("create Certificate...");
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                fs = new FileInputStream(Constants.CMB_CERT_PATH);
                cert = factory.generateCertificate(fs);
            }
            Signature signatureChecker = Signature.getInstance("SHA1withRSA");
            signatureChecker.initVerify(cert);
            signatureChecker.update(plainTextByte);
            if (signatureChecker.verify(signatureByte))
            {
                LOGGER.info("ssocmb check sucessed");
                data = dataByte;
            }
            else
            {
                LOGGER.error("ssocmb check failed dataBase64");
            }
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("encode failed", e.getMessage());
        }
        catch (CertificateException e)
        {
            LOGGER.error("CertificateException", e);
        }
        catch (FileNotFoundException e)
        {
            LOGGER.error("FileNotFoundException", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.error("NoSuchAlgorithmException", e);
        }
        catch (InvalidKeyException e)
        {
            LOGGER.error("InvalidKeyException", e.getMessage());
        }
        catch (SignatureException e)
        {
            LOGGER.error("SignatureException", e.getMessage());
        }
        finally
        {
            if (null != fs)
            {
                try
                {
                    fs.close();
                }
                catch (IOException e)
                {
                    LOGGER.warn("", e);
                }
            }
        }
        return data;
    }
    
    // 校验SSO登录时间时间是否过期
    public static boolean isSSOTimeExpired(String reslut)
    {
        String time = "";
        if (StringUtils.isBlank(reslut))
        {
            LOGGER.error("reslut is null");
            return true;
        }
        String[] resultArg = reslut.split("\\" + Constants.CERT_RESULT_SPLIT);
        for (int i = 0; i < resultArg.length; i++)
        {
            if (StringUtils.isBlank(resultArg[i]))
            {
                continue;
            }
            if (resultArg[i].toLowerCase().contains(Constants.CERT_RESULT_TIME))
            {
                if (resultArg[i].length() == Constants.CERT_RESULT_TIME.length())
                {
                    LOGGER.error("time is null");
                    return true;
                }
                time = resultArg[i].substring(Constants.CERT_RESULT_TIME.length(), resultArg[i].length());
                break;
            }
        }
        DateFormat df = new SimpleDateFormat(TIME_FORMAT);
        Date expireTime = new Date(System.currentTimeMillis() - Constants.CERT_EXPIRED_TIME);
        time = time.replace("T", " ");
        Date currentTime = null;
        try
        {
            currentTime = df.parse(time);
        }
        catch (ParseException e)
        {
            LOGGER.error(e.toString());
            return true;
        }
        if (currentTime.getTime() > expireTime.getTime())
        {
            LOGGER.error("login time expired ssologinTime:" + currentTime + " currentSysTime:"
                + new Date(System.currentTimeMillis()));
            return true;
        }
        return false;
    }
    
    public static String phraseSSOResult(String reslut)
    {
        String sapId = "";
        if (StringUtils.isBlank(reslut))
        {
            return null;
        }
        String[] resultArg = reslut.split("\\" + Constants.CERT_RESULT_SPLIT);
        for (int i = 0; i < resultArg.length; i++)
        {
            if (StringUtils.isBlank(resultArg[i]))
            {
                continue;
            }
            if (resultArg[i].toLowerCase().contains(Constants.CERT_RESULT_SAPID))
            {
                if (resultArg[i].length() == Constants.CERT_RESULT_SAPID.length())
                {
                    return null;
                }
                sapId = resultArg[i].substring(Constants.CERT_RESULT_SAPID.length(), resultArg[i].length());
                sapId = sapId.trim();
                break;
            }
        }
        return sapId;
    }
}

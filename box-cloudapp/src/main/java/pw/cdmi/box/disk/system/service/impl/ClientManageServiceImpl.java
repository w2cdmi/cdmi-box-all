package pw.cdmi.box.disk.system.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.openapi.rest.v2.domain.ClientObject;
import pw.cdmi.box.disk.openapi.rest.v2.domain.ClientType;
import pw.cdmi.box.disk.system.dao.ClientManageDAO;
import pw.cdmi.box.disk.system.service.ClientManageService;
import pw.cdmi.box.disk.system.service.CustomizeLogoService;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.FreeMarkers;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.exception.ErrorCode;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.exception.InvalidVersionsException;
import pw.cdmi.core.exception.NoSuchClientException;
import pw.cdmi.core.utils.FileSizeUtil;

@Component
public class ClientManageServiceImpl implements ClientManageService, ConfigListener
{
    public static final String PLIST_NAME = PropertiesUtils.getProperty("plist.file.path", "ios.plist");
    
    private static final String ANDROID_CLIENT_FILE_NAME = PropertiesUtils.getProperty("client.file.name.android",
        "HuaYiBox_Android.apk");
    
    private static final String CLIENT_CODE_PATH = PropertiesUtils.getProperty("client.code.path",
        "/static/clientDownload/");
    
    private static final String CLIENT_FILE_PATH = PropertiesUtils.getProperty("client.file.path", "/static/clientDownload/");
    
    private static final String IOS_CLIENT_FILE_NAME = PropertiesUtils.getProperty("client.file.name.ios",
        "HuaYiBox_iOS.ipa");
    
    private static Logger logger = LoggerFactory.getLogger(ClientManageServiceImpl.class);
    
    private static final String PC_CLIENT_FILE_NAME = PropertiesUtils.getProperty("client.file.name.pc",
        "HuaYiBox_Sync.exe");
    
    private static final String PCCLOUD_CLIENT_FILE_NAME = PropertiesUtils.getProperty("client.file.name.clouder",
        "HuaYiBox_cloud.exe");
    
    private ClientManage androidClientCache;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private ClientManageDAO clientManageDAO;
    
    @Autowired
    private CustomizeLogoService customizeLogoService;
    
    private ClientManage iosClientCache;
    
    private ClientManage pcClientCache;
    
    private ClientManage clouderClientCache;
    
    private String twoDimCodeUrl = null;
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (ClientManage.class.getSimpleName().equals(key))
        {
            logger.info("Reload Client By Cluster Notify [ " + value + " ]");
            
            ClientManage newClient = clientManageDAO.getClient(authAppService.getCurrentAppId(),
                ((ClientManage) value).getType());
            if (newClient == null)
            {
                logger.info("New client is empty");
                return;
            }
            logger.info(newClient.toString());
            if (ClientManage.ClientType.Android.name().equals(newClient.getType()))
            {
                synClient(newClient.getContent(),
                    getClientFileName(newClient),
                    androidClientCache == null ? null : getClientFileName(androidClientCache));
                newClient.setContent(null);
                byte[] data = newClient.getTwoDimCodeImage();
                synTDCodeName(data, newClient.getTwoDimCodeName(), androidClientCache == null ? null
                    : androidClientCache.getTwoDimCodeName());
                String relativeTDCodeUrl = CLIENT_CODE_PATH + newClient.getTwoDimCodeName();
                
                newClient.setTwoDimCodeUrl(relativeTDCodeUrl);
                newClient.setTwoDimCodeImage(null);
                androidClientCache = newClient;
                synStaticHtml();
            }
            else if (ClientManage.ClientType.IOS.name().equals(newClient.getType()))
            {
                synClient(newClient.getContent(), getClientFileName(newClient), iosClientCache == null ? null
                    : getClientFileName(iosClientCache));
                byte[] data = newClient.getTwoDimCodeImage();
                synTDCodeName(data, newClient.getTwoDimCodeName(), iosClientCache == null ? null
                    : iosClientCache.getTwoDimCodeName());
                if (newClient.getPlistContent() != null)
                {
                    savePlistToLocal(newClient.getPlistContent());
                }
                String relativeTDCodeUrl = CLIENT_CODE_PATH + newClient.getTwoDimCodeName();
                newClient.setTwoDimCodeUrl(relativeTDCodeUrl);
                newClient.setTwoDimCodeImage(null);
                newClient.setContent(null);
                iosClientCache = newClient;
                synIosStaticHtml();
            }
            else if (ClientManage.ClientType.PC.name().equals(newClient.getType()))
            {
                synClient(newClient.getContent(), getClientFileName(newClient), pcClientCache == null ? null
                    : getClientFileName(pcClientCache));
                newClient.setContent(null);
                pcClientCache = newClient;
            }
            else if (ClientManage.ClientType.Pccloud.name().equals(newClient.getType()))
            {
                synClient(newClient.getContent(),
                    getClientFileName(newClient),
                    clouderClientCache == null ? null : getClientFileName(clouderClientCache));
                newClient.setContent(null);
                clouderClientCache = newClient;
            }
        }
        
    }
    
    @Override
    public List<ClientManage> getAll()
    {
        return clientManageDAO.getAll(authAppService.getCurrentAppId());
    }
    
    @Override
    public ClientManage getAndroidClient()
    {
        if (androidClientCache == null)
        {
            return new ClientManage();
        }
        return androidClientCache;
    }
    
    @Override
    public ClientObject getClientObject(String type) throws InvalidParamException, NoSuchClientException
    {
        String fileName = null;
        switch (ClientType.valueOf(type.toUpperCase(Locale.ENGLISH)))
        {
            case PC:
                fileName = PC_CLIENT_FILE_NAME;
                break;
            case ANDROID:
                fileName = ANDROID_CLIENT_FILE_NAME;
                break;
            case IOS:
                fileName = IOS_CLIENT_FILE_NAME;
                break;
            case PLIST:
                fileName = "ios.plist";
                break;
            case PCCLOUD:
                fileName = PCCLOUD_CLIENT_FILE_NAME;
                break;
            default:
                throw new InvalidParamException();
        }
        StringBuffer filePathBuff = new StringBuffer(getClientFilePath() + fileName);
        String filePath = filePathBuff.toString();
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile())
        {
            throw new NoSuchClientException();
        }
        ClientObject client = new ClientObject();
        client.setName(fileName);
        client.setSize(file.length());
        try
        {
            client.setContent(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            logger.info("FileNotFoundException:" + e.getMessage());
            throw new NoSuchClientException();
        }
        return client;
    }
    
    @Override
    public ClientManage getIOSClient()
    {
        if (iosClientCache == null)
        {
            return new ClientManage();
        }
        return iosClientCache;
    }
    
    @Override
    public ClientManage getPcClient()
    {
        if (pcClientCache == null)
        {
            return new ClientManage();
        }
        return pcClientCache;
    }
    
    @Override
    public ClientManage getClouderClient()
    {
        if (null == clouderClientCache)
        {
            return new ClientManage();
        }
        return clouderClientCache;
    }
    
    @Override
    public String getTDCodePath()
    {
        StringBuffer codePrePathBuff = new StringBuffer(getRootPath() + CLIENT_CODE_PATH);
        if ('/' != codePrePathBuff.charAt(codePrePathBuff.length() - 1))
        {
            codePrePathBuff = codePrePathBuff.append("/");
        }
        return codePrePathBuff.toString();
    }
    
    @Override
    public ClientManage getVersionInfoByType(String type)
    {
        return clientManageDAO.getVersionInfoByType(authAppService.getCurrentAppId(), type);
    }
    
    @PostConstruct
    public void init()
    {
        List<ClientManage> clients = getAll();
        String clientFileName = null;
        File clientFile = null;
        String relativeTDCodeUrl = null;
        for (ClientManage client : clients)
        {
            clientFileName = getClientFileName(client);
            clientFile = new File(getClientFilePath() + clientFileName);
            if (!clientFile.isFile() || !clientFile.exists())
            {
                synClient(client.getContent(), clientFileName, null);
            }
            client.setContent(null);
            if (ClientManage.ClientType.Android.name().equals(client.getType())
                || ClientManage.ClientType.IOS.name().equals(client.getType()))
            {
                byte[] data = client.getTwoDimCodeImage();
                synTDCodeName(data, client.getTwoDimCodeName(), null);
                relativeTDCodeUrl = CLIENT_CODE_PATH + client.getTwoDimCodeName();
                client.setTwoDimCodeUrl(relativeTDCodeUrl);
                client.setTwoDimCodeImage(null);
                if (ClientManage.ClientType.Android.name().equals(client.getType()))
                {
                    androidClientCache = client;
                }
                else if (ClientManage.ClientType.IOS.name().equals(client.getType()))
                {
                    iosClientCache = client;
                }
                synStaticHtml();
            }
            else if (ClientManage.ClientType.PC.name().equals(client.getType()))
            {
                pcClientCache = client;
            }
            else if (ClientManage.ClientType.Pccloud.name().equals(client.getType()))
            {
                clouderClientCache = client;
            }
        }
    }
    
    private String getClientFileName(ClientManage client)
    {
        if (ClientManage.ClientType.Android.name().equals(client.getType()))
        {
            return ANDROID_CLIENT_FILE_NAME;
        }
        else if (ClientManage.ClientType.IOS.name().equals(client.getType()))
        {
            return IOS_CLIENT_FILE_NAME;
        }
        else if (ClientManage.ClientType.PC.name().equals(client.getType()))
        {
            return PC_CLIENT_FILE_NAME;
        }
        else if (ClientManage.ClientType.Pccloud.name().equals(client.getType()))
        {
            return PCCLOUD_CLIENT_FILE_NAME;
        }
        return "";
    }
    
    private String getClientFilePath()
    {
        StringBuffer filePathBuff = new StringBuffer(getRootPath() + CLIENT_FILE_PATH);
        if ('/' != filePathBuff.charAt(filePathBuff.length() - 1))
        {
            filePathBuff = filePathBuff.append("/");
        }
        return filePathBuff.toString();
    }
    
    private String getPlistPath()
    {
        StringBuffer plistPathBuff = new StringBuffer(this.getClientFilePath() + PLIST_NAME);
        if ('/' != plistPathBuff.charAt(plistPathBuff.length() - 1))
        {
            plistPathBuff = plistPathBuff.append("/");
        }
        return plistPathBuff.toString();
    }
    
    private String getRootPath()
    {
        ClassLoader loader = ClientManageServiceImpl.class.getClassLoader();
        if (loader == null)
        {
            throw new BusinessException();
        }
        URL url = loader.getResource("");
        if (url == null)
        {
            throw new BusinessException();
        }
        String classPath = url.getPath();
        String rootPath = "";
        // windows
        if ("\\".equals(File.separator))
        {
            // add by wuzhiyuan ,use in juint env
            boolean isWebApp = classPath.indexOf("/WEB-INF/classes") != -1;
            if (isWebApp)
            {
                rootPath = classPath.substring(1, classPath.indexOf("/WEB-INF/classes"));// ?应该没有"/"
            }
        }
        // linux
        if ("/".equals(File.separator))
        {
            rootPath = classPath.substring(0, classPath.indexOf("/WEB-INF/classes"));
            rootPath = rootPath.replace("\\", "/");
        }
        return rootPath;
    }
    
    /**
     * @param data
     * @param newTDCodeName
     * @param oldTDCodeName
     */
    private void savePlistToLocal(byte[] data)
    {
        if (data == null)
        {
            return;
        }
        OutputStream outputStream = null;
        try
        {
            String plistFile = getPlistPath();
            outputStream = new FileOutputStream(plistFile);
            outputStream.write(data);
        }
        catch (IOException e)
        {
            logger.error("Error in output two dim code img!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    private void synClient(byte[] data, String newFileName, String oldFileName)
    {
        if (data == null)
        {
            return;
        }
        OutputStream outputStream = null;
        try
        {
            String clientPath = getClientFilePath();
            File basePath = new File(clientPath);
            if (!basePath.exists())
            {
                if (!basePath.mkdirs())
                {
                    logger.info(basePath.getCanonicalPath() + "dir make failure");
                }
            }
            
            outputStream = new FileOutputStream(clientPath + newFileName);
            outputStream.write(data);
            
            if (oldFileName != null && !newFileName.equals(oldFileName))
            {
                File oldFile = new File(clientPath + oldFileName);
                if (oldFile.isFile() && oldFile.exists())
                {
                    FileUtils.forceDelete(oldFile);
                }
            }
        }
        catch (IOException e)
        {
            logger.error("Error in write client!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
        
    }
    
    private void synIosStaticHtml()
    {
        CustomizeLogo customLogo = customizeLogoService.getCustomize();
        Map<String, Object> params = new HashMap<String, Object>(BusinessConstants.INITIAL_CAPACITIES);
        params.put("title", StringUtils.trimToEmpty(customLogo.getTitle()));
        
        ClientManage iosClient = getIOSClient();
        
        params.put("iosSize", FileSizeUtil.byteToMBString(iosClient.getSize()));
        params.put("iosVersion",
            iosClient.getVersion() == null ? "" : HtmlUtils.htmlEscape(iosClient.getVersion()));
        params.put("iosSupportSys",
            iosClient.getSupportSys() == null ? "" : HtmlUtils.htmlEscape(iosClient.getSupportSys()));
        params.put("iosDownloadUrl",
            iosClient.getPlistDownloadUrl() == null ? "" : iosClient.getPlistDownloadUrl());
        
        String htmlFileName = getRootPath() + "/static/clientDownload/ios_client.html";
        File htmlFile = new File(htmlFileName);
        
        Writer out = null;
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try
        {
            if (htmlFile.isFile() && htmlFile.exists())
            {
                FileUtils.forceDelete(htmlFile);
            }
            Configuration cf = FreeMarkers.buildConfiguration("client");
            Template template = cf.getTemplate("downloadiOS.ftl");
            fos = new FileOutputStream(new File(htmlFileName));
            osw = new OutputStreamWriter(fos, "UTF-8");
            out = new BufferedWriter(osw);
            template.process(params, out);
        }
        catch (TemplateException e)
        {
            logger.error("TemplateException output static html fail", e);
        }
        catch (IOException e)
        {
            logger.error("IOException output static html fail", e);
        }
        finally
        {
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(osw);
            IOUtils.closeQuietly(out);
        }
    }
    
    private void synStaticHtml()
    {
        CustomizeLogo customLogo = customizeLogoService.getCustomize();
        Map<String, Object> params = new HashMap<String, Object>(BusinessConstants.INITIAL_CAPACITIES);
        params.put("title", StringUtils.trimToEmpty(customLogo.getTitle()));
        
        ClientManage androidClient = getAndroidClient();
        ClientManage iosClient = getIOSClient();
        
        params.put("androidSize", FileSizeUtil.byteToMBString(androidClient.getSize()));
        params.put("androidVersion",
            androidClient.getVersion() == null ? "" : HtmlUtils.htmlEscape(androidClient.getVersion()));
        params.put("androidSupportSys",
            androidClient.getSupportSys() == null ? "" : HtmlUtils.htmlEscape(androidClient.getSupportSys()));
        params.put("androidDownloadUrl",
            androidClient.getDownloadUrl() == null ? "" : androidClient.getDownloadUrl());
        
        params.put("iosSize", FileSizeUtil.byteToMBString(iosClient.getSize()));
        params.put("iosVersion",
            iosClient.getVersion() == null ? "" : HtmlUtils.htmlEscape(iosClient.getVersion()));
        params.put("iosSupportSys",
            iosClient.getSupportSys() == null ? "" : HtmlUtils.htmlEscape(iosClient.getSupportSys()));
        params.put("iosDownloadUrl",
            iosClient.getPlistDownloadUrl() == null ? "" : iosClient.getPlistDownloadUrl());
        
        String htmlFileName = getRootPath() + "/static/clientDownload/androidphone_client.html";
        File htmlFile = new File(htmlFileName);
        
        Writer out = null;
        try
        {
            if (htmlFile.isFile() && htmlFile.exists())
            {
                FileUtils.forceDelete(htmlFile);
            }
            Configuration cf = FreeMarkers.buildConfiguration("client");
            Template template = cf.getTemplate("downloadAndroid.ftl");
            
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(htmlFileName)),
                "UTF-8"));
            template.process(params, out);
        }
        catch (IOException e)
        {
            logger.error("output static html fail", e);
        }
        catch (TemplateException e)
        {
            logger.error("output static html fail", e);
        }
        finally
        {
            IOUtils.closeQuietly(out);
        }
    }
    
    /**
     * @param data
     * @param newTDCodeName
     * @param oldTDCodeName
     */
    private void synTDCodeName(byte[] data, String newTDCodeName, String oldTDCodeName)
    {
        if (data == null)
        {
            return;
        }
        OutputStream outputStream = null;
        try
        {
            String twoDimCodePath = getTDCodePath();
            File basePath = new File(twoDimCodePath);
            if (!basePath.exists())
            {
                if (!basePath.mkdirs())
                {
                    logger.info(basePath.getCanonicalPath() + "dir make failure");
                }
            }
            
            twoDimCodeUrl = twoDimCodePath + newTDCodeName;
            outputStream = new FileOutputStream(twoDimCodeUrl);
            outputStream.write(data);
            
            if (oldTDCodeName != null && !newTDCodeName.equals(oldTDCodeName))
            {
                File oldFile = new File(twoDimCodePath + oldTDCodeName);
                if (oldFile.isFile() && oldFile.exists())
                {
                    FileUtils.forceDelete(oldFile);
                }
            }
        }
        catch (IOException e)
        {
            logger.error("Error in output two dim code img!", e);
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    @Override
    public ClientManage getClientManage(ClientManage client)
    {
        ClientManage clientManage = getClientManageService(client);
        if (null == clientManage)
        {
            throw new InvalidVersionsException(ErrorCode.NO_SUCH_VERSION.getMessage(),
                ErrorCode.NO_SUCH_VERSION.getCode());
        }
        return clientManage;
    }
    
    @Override
    public ClientManage getClientManageService(ClientManage client)
    {
        ClientManage clientManage = null;
        if (client != null)
        {
            clientManage = clientManageDAO.getClientManageByVer(client.getType(), client.getVersion());
        }
        return clientManage;
    }
}

package pw.cdmi.box.uam.authapp.web;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.ClientManageService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.PropertiesUtils;
import pw.cdmi.common.domain.ClientManage;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/app/clientManage")
public class AppClientManageController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppClientManageController.class);
    
    private static final long MAX_PACKAGE_FILE_SIZE = Long.parseLong(PropertiesUtils.getProperty("client.max.packageFile.size",
        String.valueOf(64 * 1024))) * 1024L;
    
    private static final long MAX_TWO_DIM_CODE_FILE_SIZE = 64 * 1024L;
    
    private static final long MAX_VERSION_FILE_SIZE = Long.parseLong(PropertiesUtils.getProperty("client.max.versionFile.size",
        String.valueOf(8))) * 1024L;
    
    private static final long MAX_PLIST_FILE_SIZE = Long.parseLong(PropertiesUtils.getProperty("client.max.plistFile.size",
        String.valueOf(8))) * 1024L;
    
    private static final String PACKAGE_FILE = "packageFile";
    
    private static final String PLIST_FILE = "plistFile";
    
    private static final String PACKAGE_FILE_TYPE_ALLOWED = PropertiesUtils.getProperty("client.packageFile.type.allowed",
        "");
    
    private static final String TWO_DIM_CODE_IMAGE = "twoDimCode";
    
    private static final String TWO_DIM_CODE_TYPE_ALLOWED = PropertiesUtils.getProperty("client.twoDimCodeImage.type.allowed",
        "");
    
    private static final String VERSION_FILE_TYPE_ALLOWED = PropertiesUtils.getProperty("client.versionFile.type.allowed",
        "");
    
    private static final String PLIST_FILE_TYPE_ALLOWED = PropertiesUtils.getProperty("client.plistFile.type.allowed",
        "");
    
    private static final String VERSION_INFO_FILE = "versionFile";
    
    private static final String ALGORITHM = "SHA-256";
    
    private static final String DEFAULT_STR = "0";
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private ClientManageService clientManageService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "goUpload/{appId}", method = RequestMethod.GET)
    public String enterUpload(@PathVariable(value = "appId") String appId, Model model)
    {
        checkAuthAppType(appId);
        model.addAttribute("typeList", ClientManage.ClientType.values());
        model.addAttribute("curType", ClientManage.ClientType.PC.name());
        model.addAttribute("appId", appId);
        return "app/sysConfigManage/clientUpload";
    }
    
    private AuthApp checkAuthAppType(String appId)
    {
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if (null == authApp)
        {
            throw new InvalidParamterException("authApp is null");
        }
        if (authApp.getType() != Constants.AUTH_APP_TYPE)
        {
            throw new InvalidParamterException("your request url is not allowed");
        }
        return authApp;
    }
    
    @RequestMapping(value = "config/{appId}", method = RequestMethod.GET)
    public String load(@PathVariable(value = "appId") String appId, Model model)
    {
        permissionCheck(appId);
        List<ClientManage> clients = clientManageService.getAll(appId);
        model.addAttribute("clients", clients);
        model.addAttribute("appId", appId);
        AuthApp authApp = checkAuthAppType(appId);
        model.addAttribute("appType", authApp.getType());
        return "app/sysConfigManage/clientManage";
    }
    
    /**
     * 
     * @param customize
     * @param model
     * @return
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = "uploadClient", method = RequestMethod.POST)
    public String uploadClient(MultipartHttpServletRequest request, ClientManage client, Model model,
        String token) throws NoSuchAlgorithmException
    {
        String id = null;
        String saveState = "success";
        try
        {
            super.checkToken(token);
            
            AuthApp authApp = authAppService.getByAuthAppID(client.getAppId());
            if (null == authApp)
            {
                saveValidateLog(request, OperateType.UploadClient);
                throw new InvalidParamterException("authApp is null");
            }
            if (authApp.getType() != Constants.AUTH_APP_TYPE)
            {
                saveValidateLog(request, OperateType.UploadClient);
                throw new InvalidParamterException("your request url is not allowed");
            }
            
            permissionCheck(client.getAppId());
            Set<ConstraintViolation<ClientManage>> violations = validator.validate(client);
            if (!violations.isEmpty() || !client.validate())
            {
                saveValidateLog(request, OperateType.UploadClient);
                throw new ValidationException();
            }
            String[] description = new String[]{client.getAppId(), client.getVersion(),
                client.getSupportSys(), client.getDownloadUrl()};
            id = systemLogManager.saveFailLog(request,
                OperateType.UploadClient,
                OperateDescription.ADMIN_UPLOAD,
                null,
                description);
            fillClientObj(request, client);
            clientManageService.updateClient(client);
        }
        catch (ValidationException e)
        {
            LOGGER.error("client upload fail!", e);
            saveState = "fail";
        }
        catch (IOException e)
        {
            LOGGER.error("client upload fail!", e);
            saveState = "fail";
        }
        if ("success".equals(saveState))
        {
            systemLogManager.updateSuccess(id);
        }
        model.addAttribute("saveState", saveState);
        model.addAttribute("client", client);
        model.addAttribute("typeList", ClientManage.ClientType.values());
        model.addAttribute("curType", client.getType());
        model.addAttribute("appId", client.getAppId());
        return "app/sysConfigManage/clientUpload";
    }
    
    private void setClientCheckCode(byte[] fileBytes, ClientManage client) throws NoSuchAlgorithmException
    {
        MessageDigest fileDigest = MessageDigest.getInstance(ALGORITHM);
        byte[] digestBytes = fileDigest.digest(fileBytes);
        String checkCode = getDigestText(digestBytes);
        client.setCheckCode(checkCode);
    }
    
    private String getDigestText(byte[] digestBytes)
    {
        String tempStr = null;
        StringBuilder sb = new StringBuilder();
        for (byte b : digestBytes)
        {
            tempStr = Integer.toHexString(b & 0xff);
            if (tempStr.length() == 1)
            {
                sb.append(DEFAULT_STR);
            }
            sb.append(tempStr);
        }
        return sb.toString();
    }
    
    private void fillClientObj(MultipartHttpServletRequest request, ClientManage client) throws IOException,
        NoSuchAlgorithmException
    {
        Map<String, MultipartFile> fileMap = request.getFileMap();
        String fieldName = null;
        MultipartFile file = null;
        long fileSize;
        String fileName = null;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
        {
            fieldName = entry.getKey();
            file = entry.getValue();
            fileSize = file.getSize();
            fileName = file.getOriginalFilename();
            if (PACKAGE_FILE.equals(fieldName))
            {
                fillPackageFile(client, file, fileSize, fileName);
            }
            if (isIOSPlistFile(client, fieldName))
            {
                if (fileSize <= 0 || fileSize > MAX_PLIST_FILE_SIZE)
                {
                    // continue;
                    if (fileSize == 0)
                    {
                        continue;
                    }
                    throw new ValidationException("PLIST_FILE file size invalid: " + fileSize);
                    
                }
                if (!isPlistFileTypeValid(fileName))
                {
                    throw new ValidationException("Plist file type invalid: " + fileName);
                }
                
                byte[] fileBytes = FileCopyUtils.copyToByteArray(file.getInputStream());
                
                client.setPlistContent(fileBytes);
                client.setPlistFileName(fileName);
                client.setPlistSize(fileSize);
            }
            else if (isTwoDimCode(client, fieldName))
            {
                if (fileSize <= 0 || fileSize > MAX_TWO_DIM_CODE_FILE_SIZE)
                {
                    // continue;
                    if (fileSize == 0)
                    {
                        continue;
                    }
                    throw new ValidationException("TWO_DIM_CODE_IMAGE file size invalid: " + fileSize);
                    
                }
                if (!isTDCodeImageTypeValid(fileName))
                {
                    throw new ValidationException("Two-Dementional-Code image type invalid: " + fileName);
                }
                byte[] fileBytes = FileCopyUtils.copyToByteArray(file.getInputStream());
                String suffix = getFileSuffix(fileName);
                client.setTwoDimCodeImage(fileBytes);
                String twoDimCodeName = getRandomName() + "." + suffix;
                client.setTwoDimCodeName(twoDimCodeName);
            }
            else if (isVersionFile(fieldName, fileSize))
            {
                fillVersionInfo(client, file, fileSize, fileName);
            }
        }
    }
    
    private boolean isVersionFile(String fieldName, long fileSize)
    {
        return VERSION_INFO_FILE.equals(fieldName) && fileSize > 0;
    }
    
    private boolean isTwoDimCode(ClientManage client, String fieldName)
    {
        return TWO_DIM_CODE_IMAGE.equals(fieldName)
            && !StringUtils.equals(ClientManage.ClientType.PC.name(), client.getType());
    }
    
    private boolean isIOSPlistFile(ClientManage client, String fieldName)
    {
        return PLIST_FILE.equals(fieldName) && ClientManage.ClientType.IOS.name().equals(client.getType());
    }
    
    private void fillVersionInfo(ClientManage client, MultipartFile file, long fileSize, String fileName)
        throws IOException
    {
        if (fileSize > MAX_VERSION_FILE_SIZE)
        {
            throw new ValidationException("Version file size invalid: " + fileSize);
        }
        
        if (!isVersionFileTypeValid(fileName))
        {
            throw new ValidationException("Version file type invalid: " + fileName);
        }
        
        byte[] versionFileBytes = FileCopyUtils.copyToByteArray(file.getInputStream());
        client.setVersionInfo(versionFileBytes);
    }
    
    private void fillPackageFile(ClientManage client, MultipartFile file, long fileSize, String fileName)
        throws IOException, NoSuchAlgorithmException
    {
        if (fileSize <= 0 || fileSize > MAX_PACKAGE_FILE_SIZE)
        {
            throw new ValidationException("Package file size invalid: " + fileSize);
        }
        if (!isPackageFileTypeValid(fileName))
        {
            throw new ValidationException("Package file type invalid: " + fileName);
        }
        
        byte[] fileBytes = FileCopyUtils.copyToByteArray(file.getInputStream());
        
        client.setContent(fileBytes);
        client.setFileName(fileName);
        client.setSize(fileSize);
        setClientCheckCode(fileBytes, client);
    }
    
    /**
     * 
     * @param fileName
     * @return
     */
    private String getFileSuffix(String fileName)
    {
        if (fileName.lastIndexOf(".") == -1)
        {
            return "";
        }
        if (StringUtils.isBlank(fileName) || fileName.charAt(fileName.length() - 1) == '.')
        {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    /**
     * 
     * @param fileSuffix
     * @return
     */
    private boolean isPackageFileTypeValid(String fileName)
    {
        if (StringUtils.isBlank(fileName))
        {
            return false;
        }
        if (StringUtils.isNotBlank(PACKAGE_FILE_TYPE_ALLOWED))
        {
            String fileSuffix = getFileSuffix(fileName);
            String[] allowedTypes = PACKAGE_FILE_TYPE_ALLOWED.split(",");
            for (String allowedType : allowedTypes)
            {
                if (allowedType.equalsIgnoreCase(fileSuffix))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 
     * @param fileSuffix
     * @return
     */
    private boolean isPlistFileTypeValid(String fileName)
    {
        if (StringUtils.isBlank(fileName))
        {
            return false;
        }
        if (StringUtils.isNotBlank(PLIST_FILE_TYPE_ALLOWED))
        {
            String fileSuffix = getFileSuffix(fileName);
            String[] allowedTypes = PLIST_FILE_TYPE_ALLOWED.split(",");
            for (String allowedType : allowedTypes)
            {
                if (allowedType.equalsIgnoreCase(fileSuffix))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 
     * @param fileSuffix
     * @return
     */
    private boolean isVersionFileTypeValid(String fileName)
    {
        if (StringUtils.isBlank(fileName))
        {
            return false;
        }
        if (StringUtils.isNotBlank(VERSION_FILE_TYPE_ALLOWED))
        {
            String fileSuffix = getFileSuffix(fileName);
            String[] allowedTypes = VERSION_FILE_TYPE_ALLOWED.split(",");
            for (String allowedType : allowedTypes)
            {
                if (allowedType.equalsIgnoreCase(fileSuffix))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isTDCodeImageTypeValid(String fileName)
    {
        if (StringUtils.isBlank(fileName))
        {
            return false;
        }
        if (StringUtils.isNotBlank(TWO_DIM_CODE_TYPE_ALLOWED))
        {
            String fileSuffix = getFileSuffix(fileName);
            String[] allowedTypes = TWO_DIM_CODE_TYPE_ALLOWED.split(",");
            for (String allowedType : allowedTypes)
            {
                if (allowedType.equalsIgnoreCase(fileSuffix))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    private String getRandomName()
    {
        UUID uuid = UUID.randomUUID();
        String randomName = uuid.toString().replace("-", "");
        return randomName;
    }
    
}

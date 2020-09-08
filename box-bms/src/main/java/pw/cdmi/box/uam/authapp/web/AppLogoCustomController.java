package pw.cdmi.box.uam.authapp.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

import org.apache.commons.io.IOUtils;
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

import pw.cdmi.box.uam.authapp.domain.LogoIconFileName;
import pw.cdmi.box.uam.authapp.service.AuthAppService;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BadRquestException;
import pw.cdmi.box.uam.exception.BaseRunTimeException;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.system.service.CustomizeLogoService;
import pw.cdmi.box.uam.system.service.SecurityService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.ImageCheckUtil;
import pw.cdmi.common.domain.CustomizeLogo;
import pw.cdmi.common.domain.CustomizeLogoVo;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.uam.domain.AuthApp;

@Controller
@RequestMapping(value = "/app/logo")
public class AppLogoCustomController extends AbstractCommonController
{
    
    private static Logger logger = LoggerFactory.getLogger(AppLogoCustomController.class);
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private CustomizeLogoService customizeService;
    
    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    private static final String SUCCESS_FLAG = "success";
    
    @RequestMapping(value = "config/{appId}", method = RequestMethod.GET)
    public String load(@PathVariable(value = "appId") String appId, Model model)
    {
        permissionCheck(appId);
        model.addAttribute("saveState", "enter");
        fillParam(model, appId);
        return "app/sysConfigManage/webCustomize";
    }
    
    /**
     * 
     * @param customize
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public String save(MultipartHttpServletRequest request, CustomizeLogo customize,LogoIconFileName logoIconFileName,Model model, String token)
    {
        if (null == customize)
        {
            throw new BadRquestException("customize is null");
        }
        setDomainName(customize);
        
        checkAuthAppValid(request, customize);
        
        Set<ConstraintViolation<CustomizeLogo>> violations = validator.validate(customize);
        if (!violations.isEmpty())
        {
            saveValidateLog(request, OperateType.ChangeCustom);
            logger.error("param validate fail");
            model.addAttribute("saveState", "fail");
            fillParam(model, customize.getAppId());
            return "app/sysConfigManage/webCustomize";
        }
        
        
        String[] description = new String[]{customize.getAppId(), customize.getTitle(),
            customize.getTitleEn(), customize.getDomainName(), customize.getAppEmailTitle(),logoIconFileName.getLogoFileValue(),logoIconFileName.getIconFileValue()};
        String id = systemLogManager.saveFailLog(request,
            OperateType.ChangeCustom,
            OperateDescription.ADMIN_SYSTEM_PERSONALIZED,
            null,
            description);
        
        super.checkToken(token);
        String saveState = "success";
        try
        {
            permissionCheck(customize.getAppId());
            Map<String, MultipartFile> fileMap = request.getFileMap();
            String fieldName;
            MultipartFile file;
            String formatName;
            byte[] fileBytes;
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet())
            {
                fieldName = entry.getKey();
                file = entry.getValue();
                validPictrueFormat(file.getOriginalFilename());
                if (file.getSize() == 0)
                {
                    setFromDb(customize, fieldName);
                    continue;
                }
                
                if (file.getSize() > 64 * 1024)
                {
                    model.addAttribute("whichOne", fieldName);
                    model.addAttribute("saveState", "ImageSizeException");
                    fillParam(model, customize.getAppId());
                    return "app/sysConfigManage/webCustomize";
                }
                
                formatName = getFormatName(file.getInputStream(), file.getOriginalFilename());
                fileBytes = FileCopyUtils.copyToByteArray(file.getInputStream());
                
                if ("logoFile".equals(fieldName))
                {
                    customize.setLogo(fileBytes);
                    customize.setLogoContentType(file.getContentType());
                    customize.setLogoFormatName(formatName);
                }
                if ("iconFile".equals(fieldName))
                {
                    customize.setIcon(fileBytes);
                    customize.setIconContentType(file.getContentType());
                    customize.setIconFormatName(formatName);
                    customize.setNeedRefreshIcon(true);
                }
                ImageCheckUtil.validPictrueFormat(file);
            }
            customizeService.updateCustomize(customize);
        }
        catch (ValidationException e)
        {
            logger.error("File upload fail!", e);
            saveState = "errorType";
            customize = customizeService.getCustomize();
        }
        catch (IOException e)
        {
            logger.error("File upload fail!", e);
            saveState = "fail";
            customize = customizeService.getCustomize();
        }
        catch (BaseRunTimeException e)
        {
            logger.error("File upload fail!", e);
            saveState = "fail";
            customize = customizeService.getCustomize();
        }
        catch (Exception e)
        {
            logger.error("File upload fail!", e);
            saveState = "fail";
            customize = customizeService.getCustomize();
        }
        model.addAttribute("saveState", saveState);
        fillParam(model, customize.getAppId());
        if (SUCCESS_FLAG.equalsIgnoreCase(saveState))
        {
            systemLogManager.updateSuccess(id);
        }
        return "app/sysConfigManage/webCustomize";
    }
    
    private void checkAuthAppValid(MultipartHttpServletRequest request, CustomizeLogo customize)
    {
        AuthApp authApp = authAppService.getByAuthAppID(customize.getAppId());
        if (null == authApp)
        {
            saveValidateLog(request, OperateType.ChangeCustom);
            throw new BadRquestException("authApp is null");
        }
        if (authApp.getType() != Constants.AUTH_APP_TYPE)
        {
            saveValidateLog(request, OperateType.ChangeCustom);
            throw new BadRquestException("not allowed to save app logo custom");
        }
    }
    
    private void setDomainName(CustomizeLogo customize)
    {
        if (StringUtils.isNotEmpty(customize.getDomainName()))
        {
            String domainName = customize.getDomainName();
            if (!"/".equals(domainName.substring(domainName.length() - 1, domainName.length()))
                && !"\\".equals(domainName.substring(domainName.length() - 1, domainName.length())))
            {
                customize.setDomainName(domainName + "/");
            }
        }
    }
    
    private void setFromDb(CustomizeLogo customize, String fieldName)
    {
        CustomizeLogo localCustomize = customizeService.getCustomize();
        if ("logoFile".equals(fieldName))
        {
            customize.setLogo(localCustomize.getLogo());
            customize.setLogoContentType(localCustomize.getLogoContentType());
            customize.setLogoFormatName(localCustomize.getLogoFormatName());
        }
        if ("iconFile".equals(fieldName))
        {
            customize.setIcon(localCustomize.getIcon());
            customize.setIconContentType(localCustomize.getIconContentType());
            customize.setIconFormatName(localCustomize.getIconFormatName());
            customize.setNeedRefreshIcon(false);
        }
    }
    
    private CustomizeLogoVo convertToVo(CustomizeLogo cacheCustomize)
    {
        CustomizeLogoVo vo = new CustomizeLogoVo();
        if (cacheCustomize.getTitle() == null)
        {
            vo.setTitle(messageSource.getMessage("main.title.app", null, Locale.CHINA));
            vo.setTitleEn(messageSource.getMessage("main.title.app", null, Locale.ENGLISH));
            vo.setCopyright(messageSource.getMessage("corpright", null, Locale.CHINA));
            vo.setCopyrightEn(messageSource.getMessage("corpright", null, Locale.ENGLISH));
        }
        else
        {
            vo.setTitle(cacheCustomize.getTitle());
            vo.setTitleEn(cacheCustomize.getTitleEn());
            vo.setCopyright(cacheCustomize.getCopyright());
            vo.setCopyrightEn(cacheCustomize.getCopyrightEn());
            vo.setDomainName(cacheCustomize.getDomainName());
            vo.setAppEmailTitle(cacheCustomize.getAppEmailTitle());
        }
        
        if (cacheCustomize.getLogo() != null && cacheCustomize.getLogo().length > 0)
        {
            vo.setExistLogo(true);
        }
        if (cacheCustomize.getIcon() != null && cacheCustomize.getIcon().length > 0)
        {
            vo.setExistIcon(true);
        }
        return vo;
    }
    
    private void fillParam(Model model, String appId)
    {
        model.addAttribute("appId", appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        CustomizeLogo cacheCustomize = customizeService.getCustomize();
        model.addAttribute("customize", convertToVo(cacheCustomize));
        SecurityConfig securityConfig = securityService.getSecurityConfig(appId);
        model.addAttribute("securityConfig", securityConfig);
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     */
    private String getFormatName(InputStream inputStream, String originalFileName)
    {
        ImageInputStream iis = null;
        try
        {
            iis = ImageIO.createImageInputStream(inputStream);
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext())
            {
                return originalFileName.substring(originalFileName.indexOf(".") + 1);
            }
            ImageReader reader = iter.next();
            return reader.getFormatName();
        }
        catch (IOException ex)
        {
            return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        }
        finally
        {
            if (iis != null)
            {
                IOUtils.closeQuietly(iis);
            }
        }
    }
    
    private void validPictrueFormat(String fileName)
    {
        if (fileName == null || "".equals(fileName))
        {
            return;
        }
        if (fileName.indexOf(".") == -1)
        {
            throw new ValidationException();
        }
        String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String[] picTypes = {"bmp", "gif", "jpg", "jpeg", "png", "ico", "icon"};
        boolean formatVaid = false;
        for (String picType : picTypes)
        {
            if (picType.equalsIgnoreCase(formatName))
            {
                formatVaid = true;
                break;
            }
        }
        if (!formatVaid)
        {
            throw new ValidationException();
        }
    }
}

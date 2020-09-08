package com.huawei.sharedrive.uam.watermark.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.AdminLogType;
import com.huawei.sharedrive.uam.enterpriseadminlog.domain.LogOwner;
import com.huawei.sharedrive.uam.enterpriseadminlog.manager.AdminLogManager;
import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.util.ImageCheckUtil;
import com.huawei.sharedrive.uam.watermark.exception.WaterMarkException;
import com.huawei.sharedrive.uam.watermark.service.WaterMarkServcie;

import pw.cdmi.core.utils.IpUtils;

@Controller
@RequestMapping(value = "/watermark")
public class WaterMarkController extends AbstractCommonController
{
    
    public static final Logger LOGGER = LoggerFactory.getLogger(WaterMarkController.class);
    
    public static final String WATERMARK_MAX_SIZE_KEY = "watermark.max.size";
    
    public static final String SUFFIX1 = "PNG";
    
    public static final String SUFFIX2 = "png";
    
    public static final String PNGHEADER = "8950";
    
    private int watermarkMaxSize;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private AdminLogManager adminLogManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @PostConstruct
    public void init()
    {
        watermarkMaxSize = Integer.parseInt(systemConfigDAO.getByPriKey("-1", WATERMARK_MAX_SIZE_KEY)
            .getValue());
    }
    
    @Autowired
    private WaterMarkServcie waterMarkServcie;
    
    @RequestMapping(value = "/{appId}", method = RequestMethod.GET)
    public String init(Model model, @PathVariable("appId") String appId, HttpServletResponse response)
        throws IOException
    {
        enterpriseAccountManager.bindAppCheck(appId);
        model.addAttribute("appId", appId);
        return "enterprise/watermark/watermark";
        
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String putWaterMark(String appId, MultipartHttpServletRequest request, Model model, Locale local,
        HttpServletRequest req, String token)
    {
        super.checkToken(token);
        
        String[] description = new String[]{getEnterpriseName(), appId};
        LogOwner owner = new LogOwner();
        owner.setEnterpriseId(checkAdminAndGetId());
        owner.setAppId(appId);
        owner.setIp(IpUtils.getClientAddress(req));
        
        String message = "";
        long accoutId = getAccoutId(appId);
        try
        {
            Map<String, MultipartFile> map = request.getFileMap();
            if (map != null)
            {
                MultipartFile file = map.get("fileName");
                
                if (StringUtils.isEmpty(file.getOriginalFilename()))
                {
                    throw new WaterMarkException(messageSource.getMessage("watermask.imge.null.error",
                        null,
                        local));
                }
                InputStream in = file.getInputStream();
                
                byte[] data = getBytesFromImage(in, local);
                if (data.length == 0)
                {
                    throw new WaterMarkException(messageSource.getMessage("watermask.imge.null.error",
                        null,
                        local));
                }
                
                if (!ImageCheckUtil.checksuffix(file.getOriginalFilename(), SUFFIX1, false)
                    && !ImageCheckUtil.checksuffix(file.getOriginalFilename(), SUFFIX2, false))
                {
                    throw new WaterMarkException(messageSource.getMessage("watermask.imge.from.error",
                        null,
                        local));
                }
                if (!ImageCheckUtil.bytesToHexString(data, 2).endsWith(PNGHEADER))
                {
                    throw new WaterMarkException(messageSource.getMessage("watermask.imge.from.error",
                        null,
                        local));
                }
                if (!ImageCheckUtil.validateImageType(data))
                {
                    throw new WaterMarkException(messageSource.getMessage("watermask.imge.from.error",
                        null,
                        local));
                }
                data = ImageCheckUtil.addWaterMark(data,
                    0,
                    0,
                    0,
                    ImageCheckUtil.getformatName(file.getOriginalFilename()));
                if (null == data)
                {
                    throw new WaterMarkException(messageSource.getMessage("watermask.imge.from.error",
                        null,
                        local));
                }
                
                waterMarkServcie.putWaterMark(accoutId, new ByteArrayInputStream(data), data.length);
            }
            else
            {
                throw new WaterMarkException(messageSource.getMessage("watermask.imge.from.error",
                    null,
                    local));
            }
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_WATER_MARK_UPLOAD, description);
        }
        catch (WaterMarkException e)
        {
            message = e.getMessage();
            LOGGER.info("upload exception", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_WATER_MARK_UPLOAD_ERROR, description);
        }
        catch (RuntimeException e)
        {
            if (StringUtils.equals(message, ""))
            {
                message = messageSource.getMessage("watermask.umage.upload.error", null, local);
            }
            LOGGER.info("upload exception", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_WATER_MARK_UPLOAD_ERROR, description);
        }
        catch (Exception e)
        {
            if (StringUtils.equals(message, ""))
            {
                message = messageSource.getMessage("watermask.umage.upload.error", null, local);
            }
            LOGGER.info("upload exception", e);
            adminLogManager.saveAdminLog(owner, AdminLogType.KEY_WATER_MARK_UPLOAD_ERROR, description);
        }
        model.addAttribute("message", message);
        model.addAttribute("appId", appId);
        return "enterprise/watermark/watermark";
    }
    
    @RequestMapping(value = "/{appId}/image", method = RequestMethod.GET)
    public void getImage(@PathVariable("appId") String appId, Model model, HttpServletResponse response)
    {
        ServletOutputStream out = null;
        try
        {
            out = response.getOutputStream();
            response.setContentType("image/png");
            long accoutId = getAccoutId(appId);
            byte[] byteImage = waterMarkServcie.getWaterMark(accoutId);
            if (null == byteImage || byteImage.length == 0)
            {
                return;
            }
            ByteArrayInputStream in = new ByteArrayInputStream(byteImage);
            BufferedImage image = ImageIO.read(in);
            ImageIO.write(image, "png", out);
        }
        catch (IOException e)
        {
            LOGGER.info(" get image error", e);
        }
        finally
        {
            if (out != null)
            {
                IOUtils.closeQuietly(out);
            }
        }
        
    }
    
    private byte[] getBytesFromImage(InputStream in, Locale local) throws IOException,
        NoSuchMessageException, WaterMarkException
    {
        byte[] buffer = new byte[watermarkMaxSize];
        int off = 0;
        int len = buffer.length;
        int n = 0;
        try
        {
            while (off < watermarkMaxSize)
            {
                n = in.read(buffer, off, len);
                if (n < 0)
                {
                    break;
                }
                off += n;
                len -= n;
            }
            
            if (n > 0)
            {
                n = in.read();
                if (n > 0)
                {
                    off++;
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.warn("error occur when read watermark from client", e);
            throw e;
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
        if (off > watermarkMaxSize)
        {
            LOGGER.warn(" file is to large");
            throw new WaterMarkException(messageSource.getMessage("watermask.imge.big.error", null, local));
        }
        byte[] data = Arrays.copyOf(buffer, off);
        return data;
    }
    
}

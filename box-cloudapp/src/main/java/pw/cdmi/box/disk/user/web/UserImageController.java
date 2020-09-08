package pw.cdmi.box.disk.user.web;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import blade.kit.json.JSONObject;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.domain.UserAccount;
import pw.cdmi.box.disk.user.domain.UserImage;
import pw.cdmi.box.disk.user.domain.UserImageVo;
import pw.cdmi.box.disk.user.manager.UserImageManager;
import pw.cdmi.box.disk.user.service.AccountUserService;
import pw.cdmi.box.disk.user.service.UserImageService;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.exception.ImageInvalidException;
import pw.cdmi.core.exception.ImageScaleException;
import pw.cdmi.core.exception.ImageSizeException;
import pw.cdmi.core.exception.LoginAuthFailedException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;

@Controller
@RequestMapping(value = "/userimage")
public class UserImageController extends CommonController
{
    private static Logger logger = LoggerFactory.getLogger(UserImageController.class);
    
    @Resource
    private RestClient ufmClientService;
    
    @Resource
    private RestClient uamClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Autowired
    private UserImageService userImageService;
    
	@Autowired
	private AccountUserService userAccountService;
    
    @Autowired
    private UserImageManager userImageManager;
    
    @RequestMapping(value = "/goChangeLogo", method = RequestMethod.GET)
    public String goChangeLogo()
    {
        return "common/localUserChgLogo";
    }
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "getLogo", method = RequestMethod.GET)
    public void getLogoImg(HttpServletRequest req, HttpServletResponse resp)
    {
        UserImage userImage = new UserImage();
        User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
        userImage.setAccountId(sessUser.getAccountId());
        userImage.setUserId(sessUser.getId());

        byte[] bytes = null;
        UserImage image = userImageManager.getUserImage(req, userImage);
        if(image != null) {
            bytes = image.getUserImage();
        }

        if(bytes == null) {
            try {
                resp.sendRedirect(req.getContextPath() + "/static/skins/default/img/head-photo-default.png");
            } catch (IOException e) {
//                e.printStackTrace();
            }
            return;
        }

        ImageOuter.outputImage(resp, bytes, "application/octet-stream");
    }
    
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "getUserImage/{cloudUserId}", method = RequestMethod.GET)
    public void getUserImage(@PathVariable("cloudUserId") long cloudUserId,HttpServletRequest req, HttpServletResponse resp)
    {
    	User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
    	UserAccount useraccount=userAccountService.getUserByCloudUserId(sessUser.getAccountId(), cloudUserId);
        UserImage userImage = new UserImage();
        userImage.setUserId(useraccount.getUserId());
        userImage.setAccountId(sessUser.getAccountId());
        byte[] bytes = null;
        UserImage image = userImageManager.getUserImage(req, userImage);
        if(image != null) {
            bytes = image.getUserImage();
        }

        if(bytes == null) {
            try {
                resp.sendRedirect(req.getContextPath() + "/static/skins/default/img/head-photo-default.png");
            } catch (IOException e) {
//                e.printStackTrace();
            }
            return;
        }

        ImageOuter.outputImage(resp, bytes, "application/octet-stream");
    }
    
    @RequestMapping(value = "loadconfig", method = RequestMethod.GET)
    @ResponseBody
    public UserImageVo load()
    {
        permissionCheck();
        UserImage userImage = new UserImage();
        User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
        userImage.setAccountId(sessUser.getAccountId());
        userImage.setUserId(sessUser.getId());
        int userId = userImageService.getUserId(userImage);
        
        UserImageVo vo = new UserImageVo();
        if (userId == 0)
        {
            vo.setExitUserImage(false);
        }
        else
        {
            vo.setExitUserImage(true);
        }
        return vo;
    }
    
    @Override
    protected String getToken()
    {
        return userTokenManager.getToken();
    }
    
    
    
    @RequestMapping(value = "loadconfig", method = RequestMethod.POST)
    @ResponseBody
    public UserImageVo loadConfig()
    {
        permissionCheck();
        UserImage userImage = new UserImage();
        User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
        userImage.setAccountId(sessUser.getAccountId());
        userImage.setUserId(sessUser.getId());
        int userId = userImageService.getUserId(userImage);
        
        UserImageVo vo = new UserImageVo();
        if (userId == 0)
        {
            vo.setExitUserImage(false);
        }
        else
        {
            vo.setExitUserImage(true);
        }
        return vo;
    }
    
    public void permissionCheck()
    {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", getToken());
        TextResponse restResponse = ufmClientService.performGetText("/api/v2/users/" + user.getCloudUserId(),
            headerMap);
        if (null == restResponse || restResponse.getStatusCode() != 200)
        {
            logger.error("get user interface return code is not 200");
            throw new LoginAuthFailedException();
        }
        String responseBody = restResponse.getResponseBody();
        if (StringUtils.isBlank(responseBody))
        {
            logger.error("get user return code is 200 but body is null");
            throw new LoginAuthFailedException();
        }
    }
    
    @RequestMapping(value = "/changeLogo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> doChangeLogos(MultipartHttpServletRequest servletRequest, UserImage userImage,Model model)
    {
        
        if (null == servletRequest.getFileMap().get("photoFile"))
        {
            model.addAttribute("imageUploadFail", "imageUploadFail");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User sessUser = (User) SecurityUtils.getSubject().getPrincipal();
        userImage.setAccountId(sessUser.getAccountId());
        userImage.setUserId(sessUser.getId());
        try
        {
            userImageManager.fileToByte(servletRequest, userImage);
        }
        catch (ImageSizeException e)
        {
            model.addAttribute("imageSizeError", "imageSizeError");
        }
        catch (ImageScaleException e)
        {
            model.addAttribute("imageScaleError", "imageScaleError");
        }
        catch (ImageInvalidException e)
        {
            model.addAttribute("imageInvalidError", "imageInvalidError");
        }
        catch (ValidationException e)
        {
            model.addAttribute("imageUploadFail", "imageUploadFail");
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            model.addAttribute("imageUploadFail", "imageUploadFail");
            // for record error log in uam, false byte array
            uamClientService.performBinaryPutTextResponse("/api/v2/users/image", getHeaderMap(), new byte[]{
                1, 3});
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        MultipartFile file = servletRequest.getFileMap().get("photoFile");
        byte[] array = null;
        try
        {
        	  JSONObject avatar=JSONObject.readFrom(servletRequest.getParameter("avatar_data")); 
        	  int x=(int) avatar.getDouble("x", 0);
        	  int y=(int) avatar.getDouble("y", 0);
        	  int width=(int) avatar.getDouble("width", 128);
        	  int height=(int) avatar.getDouble("height", 128);
        	  Rectangle rectangle=new Rectangle(x,y,width,height);
        	  array= cutImage(file.getInputStream(),rectangle,file.getOriginalFilename()); 
        }
        catch (IOException e)
        {
            model.addAttribute("imageUploadFail", "imageUploadFail");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        TextResponse response = uamClientService.performBinaryPutTextResponse("/api/v2/users/image",
            getHeaderMap(),
            array);
        if (response.getStatusCode() != 200)
        {
            model.addAttribute("imageUploadFail", "imageUploadFail");
        }
        if (response.getStatusCode() == 200)
        {
            userImage.setUserImage(array);
            userImageService.updateCache(servletRequest, userImage);
            model.addAttribute("imageUploadOK", "imageUploadOK");
        }
        
        return new ResponseEntity<String>("{}",HttpStatus.OK);
        
    }
    
    private Map<String, String> getHeaderMap()
    {
        UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
        Map<String, String> headerMap = new HashMap<String, String>(2);
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        headerMap.put("Authorization", getToken());
        headerMap.put("Date", dateStr);
        headerMap.put("Method", "PUT");
        return headerMap;
    }
    
    /**
     * <p>Title: cutImage</p>
     * <p>Description:  根据原图与裁切size截取局部图片</p>
     * @param srcImg    源图片
     * @param output    图片输出流
     * @param rect        需要截取部分的坐标和大小
     */
    public byte[] cutImage(InputStream fis, java.awt.Rectangle rect,String fieldName){
            ImageInputStream iis = null;
            try {
                String suffix = fieldName.substring(fieldName.lastIndexOf(".")+1);
                iis = ImageIO.createImageInputStream(fis);
                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
                reader.setInput(iis,true);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(rect);
                BufferedImage bi = reader.read(0, param);
                ByteArrayOutputStream os=new ByteArrayOutputStream();//新建流。
                ImageIO.write(bi,suffix,os);
                return os.toByteArray();
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fis != null) fis.close();
                    if(iis != null) iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
			return null;
    }
}

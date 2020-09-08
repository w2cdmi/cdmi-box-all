package com.huawei.sharedrive.uam.openapi.rest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.enterprise.domain.ExtendUserLog;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.exception.ErrorCode;
import com.huawei.sharedrive.uam.exception.ForbiddenException;
import com.huawei.sharedrive.uam.exception.ImageInvalidException;
import com.huawei.sharedrive.uam.exception.ImageScaleException;
import com.huawei.sharedrive.uam.exception.ImageSizeException;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.exception.NoSuchResourceStrategyException;
import com.huawei.sharedrive.uam.exception.NoSuchUserException;
import com.huawei.sharedrive.uam.log.domain.UserLogType;
import com.huawei.sharedrive.uam.log.service.UserLogService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.manager.AuthManager;
import com.huawei.sharedrive.uam.oauth2.manager.impl.AuthManagerImpl;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.user.domain.UserImage;
import com.huawei.sharedrive.uam.user.manager.UserImageManager;
import com.huawei.sharedrive.uam.user.service.UserImageService;
import com.huawei.sharedrive.uam.util.ImageCheckUtil;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.log.UserLog;

@Controller
@RequestMapping(value = "/api/v2/users")
public class UsersImageAPIController
{
    private static final int USERIMAGEMAXSIZE = 1024 * 1024;
    
    public static final int BUFFER_REST = 64 * 1024;
    
    private static final String DEFAULT_USERLOGO = "static/skins/default/img/user-logo.png";
    
    private static Logger logger = LoggerFactory.getLogger(UsersImageAPIController.class);
    
    @Autowired
    private UserImageManager userImageManager;
    
    @Autowired
    private AuthManager authManager;
    
    @Autowired
    private UserImageService userImageService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private UserLogService userLogService;
    
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public void getLogo(@RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, HttpServletRequest req,
        HttpServletResponse resp, @RequestParam(value = "id", required = false) Long id)
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        ExtendUserLog userLog = null;
        UserImage userImage = new UserImage();
        userImage.setAccountId(userToken.getAccountId());
        checkUserValid(id, userToken);
        long queryId = (null == id ? userToken.getId() : id);
        userImage.setUserId(queryId);
        int userId = userImageService.getUserId(userImage);
        userLog = tokenOrAccountAuth(authorization, date);
        if (userId == 0)
        {
            String path = req.getSession().getServletContext().getRealPath("/");
            File file = new File(path + DEFAULT_USERLOGO);
            if (!(file.exists()))
            {
                userLogService.saveFailLog(userLog.getUserLog().getLoginName(), userLog.getUserLog()
                    .getAppId(), null, UserLogType.KEY_GET_USER_IMAGE_ERR);
                throw new NoSuchResourceStrategyException(ErrorCode.NO_SUCH_RESOURCE_STRATEGY.getMessage());
            }
        }
        try
        {
            if (userId == 0)
            {
                String path = req.getSession().getServletContext().getRealPath("/");
                File file = new File(path + DEFAULT_USERLOGO);
                if (file.exists())
                {
                    outputImage(resp, getBytesFromFile(file), MediaType.IMAGE_PNG.toString());
                }
            }
            if (userId > 0)
            {
                UserImage image = userImageManager.getUserImage(userImage);
                if ("png".equalsIgnoreCase(image.getImageFormatName()))
                {
                    outputImage(resp, image.getUserImage(), MediaType.IMAGE_PNG.toString());
                }
                else
                {
                    outputImage(resp, image.getUserImage(), MediaType.IMAGE_JPEG.toString());
                }
                
            }
            userLogService.saveUserLog(userLog.getUserLog(), UserLogType.KEY_GET_USER_IMAGE, null);
        }
        catch (RuntimeException e)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_GET_USER_IMAGE_ERR,
                new String[]{""});
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
        catch (Exception e)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_GET_USER_IMAGE_ERR,
                new String[]{""});
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
    }
    
    
    /**
     * 
     * @param req
     * @param resp
     */
    @RequestMapping(value = "getUserImage/{cloudUserId}", method = RequestMethod.GET)
    public void getUserImage(@PathVariable("cloudUserId") long cloudUserId,@RequestHeader("Authorization") String authorization,
    		HttpServletRequest req, HttpServletResponse resp)
    {
    	UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        UserAccount useraccount = userAccountManager.getUserByCloudUserId(userToken.getAccountId(), cloudUserId);
        UserImage userImage = new UserImage();
        userImage.setUserId(useraccount.getUserId());
        userImage.setAccountId(userToken.getAccountId());
        byte[] bytes = null;
        UserImage image = userImageManager.getUserImage(userImage);
        if(image != null) {
            bytes = image.getUserImage();
        }

        if(bytes == null) {
            try {
                resp.sendRedirect(req.getContextPath() + "/static/skins/default/img/putting-QQ.png");
            } catch (IOException e) {
//                e.printStackTrace();
            }
            return;
        }

        try {
			outputImage(resp, bytes, "application/octet-stream");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     *
     * @param req
     * @param resp
     */
    @RequestMapping(value = "getAuthUserImage/{cloudUserId}", method = RequestMethod.GET)
    public void getAuthUserImage(@PathVariable("cloudUserId") long cloudUserId, String authorization, HttpServletRequest req, HttpServletResponse resp)
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        UserAccount useraccount = userAccountManager.getUserByCloudUserId(userToken.getAccountId(), cloudUserId);
        UserImage userImage = new UserImage();
        userImage.setUserId(useraccount.getUserId());
        userImage.setAccountId(userToken.getAccountId());
        byte[] bytes = null;
        UserImage image = userImageManager.getUserImage(userImage);
        if(image != null) {
            bytes = image.getUserImage();
        }

        if(bytes == null) {
            try {
                resp.sendRedirect(req.getContextPath() + "/static/skins/default/img/putting-QQ.png");
            } catch (IOException e) {
//                e.printStackTrace();
            }
            return;
        }

        try {
            outputImage(resp, bytes, "application/octet-stream");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "/image", method = RequestMethod.PUT)
    public ResponseEntity<String> setUserImage(HttpServletRequest req,
        @RequestHeader(value = "Content-Length", required = false) Long objectLength,
        @RequestHeader("Authorization") String authorization,
        @RequestHeader(value = "Date", required = false) String date, InputStream in,
        HttpServletResponse response) throws IOException
    {
        UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
        UserImage userImage = new UserImage();
        userImage.setAccountId(userToken.getAccountId());
        userImage.setUserId(userToken.getId());
        byte[] fileBytes = FileCopyUtils.copyToByteArray(in);
        boolean image = ImageCheckUtil.isImage(fileBytes);
        boolean imageScale = true;/*ImageCheckUtil.isSquareImage(fileBytes);*/
        boolean size = ImageCheckUtil.isRightSize(fileBytes, USERIMAGEMAXSIZE, BUFFER_REST);
        boolean typeJpng = ImageCheckUtil.validateImageTypeJpng(fileBytes);
        String formatName = ImageCheckUtil.getFileFormat(fileBytes);
        ExtendUserLog userLog = null;
        userLog = tokenOrAccountAuth(authorization, date);
        if (!image)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_UPDATE_USER_IMAGE_ERR,
                new String[]{"invalid file"});
            throw new ImageInvalidException(ErrorCode.INVALID_IMAGE.getMessage(),
                ErrorCode.INVALID_IMAGE.getCode());
        }
        if (objectLength > USERIMAGEMAXSIZE)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_UPDATE_USER_IMAGE_ERR,
                new String[]{"invalid size"});
            throw new ImageSizeException(ErrorCode.INVALID_SIZE.getMessage(),
                ErrorCode.INVALID_SIZE.getCode());
        }
        if (!imageScale)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_UPDATE_USER_IMAGE_ERR,
                new String[]{"invalid scale"});
            throw new ImageScaleException(ErrorCode.INVALID_SCALE.getMessage(),
                ErrorCode.INVALID_SCALE.getCode());
        }
        if (!typeJpng)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_UPDATE_USER_IMAGE_ERR,
                new String[]{"invalid image"});
            throw new ImageInvalidException(ErrorCode.INVALID_IMAGE.getMessage(),
                ErrorCode.INVALID_IMAGE.getCode());
        }
        if (!size)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_UPDATE_USER_IMAGE_ERR,
                new String[]{"invalid size"});
            throw new ImageSizeException(ErrorCode.INVALID_SIZE.getMessage(),
                ErrorCode.INVALID_SIZE.getCode());
        }
        try
        {
            userImage.setImageFormatName(formatName);
            userImage.setUserImage(fileBytes);
            userImageManager.create(req.getSession().getId(), userImage);
            response.setHeader("Connection", "close");
            userLogService.saveUserLog(userLog.getUserLog(), UserLogType.KEY_UPDATE_USER_IMAGE, null);
            return new ResponseEntity<String>(HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_UPDATE_USER_IMAGE_ERR,
                new String[]{"internal error"});
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
        catch (Exception e)
        {
            userLogService.saveFailLog(userLog.getUserLog(),
                UserLogType.KEY_UPDATE_USER_IMAGE_ERR,
                new String[]{"internal error"});
            throw new InternalServerErrorException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), e);
        }
    }
    
    private void outputImage(HttpServletResponse resp, byte[] data, String contentType) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            if (null == data || data.length == 0)
            {
                return;
            }
            resp.setContentType(contentType);
            outputStream = resp.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        }
        finally
        {
            IOUtils.closeQuietly(outputStream);
        }
    }
    
    public static byte[] getBytesFromFile(File f)
    {
        if (f == null)
        {
            return new byte[]{};
        }
        FileInputStream stream = null;
        ByteArrayOutputStream out = null;
        try
        {
            stream = new FileInputStream(f);
            out = new ByteArrayOutputStream(1000);
            
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
            {
                out.write(b, 0, n);
                out.flush();
            }
            return out.toByteArray();
        }
        catch (IOException e)
        {
            logger.error("read file to byte fail!", e);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
            IOUtils.closeQuietly(out);
        }
        return new byte[0];
    }
    
    private ExtendUserLog tokenOrAccountAuth(String authorization, String date)
    {
        ExtendUserLog userLog = new ExtendUserLog();
        if (!authorization.startsWith(AuthManagerImpl.APP_PREFIX)
            && !authorization.startsWith(AuthManagerImpl.ACCOUNT_PREFIX))
        {
            UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
            userLog.setUserLog(UserLogType.getUserLog(userToken));
            userLog.setAccountId(userToken.getAccountId());
        }
        else
        {
            EnterpriseAccount enterpriseAccount = authManager.checkAppToken(authorization, date);
            UserLog tempUserLog = new UserLog();
            tempUserLog.setAppId(enterpriseAccount.getAuthAppId());
            tempUserLog.setLoginName(enterpriseAccount.getAccessKeyId());
            userLog.setUserLog(tempUserLog);
            userLog.setAccountId(enterpriseAccount.getAccountId());
        }
        return userLog;
    }
    
    private void checkUserValid(Long userId, UserToken userToken)
    {
        if (null == userId)
        {
            return;
        }
        long enterpriseId = userToken.getEnterpriseId();
        long accountId = userToken.getAccountId();
        EnterpriseUser enterpriseUser = enterpriseUserService.get(userId, enterpriseId);
        if (null == enterpriseUser)
        {
            throw new NoSuchUserException("NoSuchUser");
        }
        UserAccount userAccount = userAccountManager.get(userId, accountId);
        if (null == userAccount)
        {
            throw new ForbiddenException("Forbidden");
        }
    }
}

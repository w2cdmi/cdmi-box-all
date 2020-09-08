package pw.cdmi.box.disk.user.manager;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pw.cdmi.box.disk.user.domain.UserImage;

public interface UserImageManager
{
    void create(String sessionId, UserImage userImage);
    
    UserImage getUserImage(HttpServletRequest req, UserImage userImage);
    
    UserImage getUserImage(UserImage userImage);
    
    void validPictrueFormat(String fileName, String[] picTypes);
    
    String getFormatName(InputStream inputStream, String originalFileName);
    
    boolean fileToByte(MultipartHttpServletRequest request, UserImage userImage);
    
    void checkImageInvalid(MultipartFile file);
    
}

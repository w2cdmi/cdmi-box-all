package pw.cdmi.box.disk.utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import pw.cdmi.box.disk.uploadFolder.web.UploadFolderController;


public class MyCommonsMultipartResolver extends CommonsMultipartResolver
{


     public boolean isMultipart(HttpServletRequest request)
     {
          if ( !request.getServletPath().contains("folderPreupload") && !request.getServletPath().contains("linkDirPreUpload"))
          {
               return super.isMultipart(request);
          }
          ServletInputStream iputstream;
          try
          {
               iputstream = request.getInputStream();
               
               StringBuffer sb = new StringBuffer();
               List<String> inputString = new ArrayList<String>();
               
               inputString = IOUtils.readLines(iputstream , "UTF-8");
               
               for (String s : inputString)
               {
                    sb.append(s.trim());
               }
               
               ThreadLocal <String> flyDragon = UploadFolderController.FLY_DRAGON;

               flyDragon.set(sb.toString());
          }
          catch (IOException e)
          {
               logger.error("Excute isMultipart() fail!" , e);
          }

          return false;

     }
}

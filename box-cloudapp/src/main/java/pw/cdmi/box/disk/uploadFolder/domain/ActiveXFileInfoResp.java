package pw.cdmi.box.disk.uploadFolder.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 
 * IE插件预上传相应信息
 * 
 * Project Name:cloudapp_cmb_v1
 * 
 * File Name:ActiveXFileInfoResp.java
 * 
 * 
 * 修改时间：2016年7月21日 下午2:23:36
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActiveXFileInfoResp
{


     /**
      * 文件id 唯一标识
      */
     @JsonProperty("key")
     private String fileId;

     /**
      * 文件上传地址
      */
     @JsonProperty("uploadUrl")
     private String uploadUrl;


     public String getFileId()
     {
          return fileId;
     }


     public void setFileId(String fileId)
     {
          this.fileId = fileId;
     }


     public String getUploadUrl()
     {
          return uploadUrl;
     }


     public void setUploadUrl(String uploadUrl)
     {
          this.uploadUrl = uploadUrl;
     }

}

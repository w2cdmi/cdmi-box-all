package pw.cdmi.box.disk.uploadFolder.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 
 * 从IE上传插件获取的文件信息
 * 
 * Project Name:cloudapp
 * 
 * File Name:ActiveXFileInfo.java
 * 
 * 
 * 修改时间：2016年7月13日 上午10:21:28
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActiveXFileInfo
{


     /**
      * 文件id 唯一标识
      */
     @JsonProperty("key")
     private String fileId;

     /**
      * 文件名称
      */
     @JsonProperty("file")
     private String fileName;

     /**
      * 文件大小
      */
     @JsonProperty("size")
     private long size;

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


     public String getFileName()
     {
          return fileName;
     }


     public void setFileName(String fileName)
     {
          this.fileName = fileName;
     }


     public long getSize()
     {
          return size;
     }


     public void setSize(long size)
     {
          this.size = size;
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

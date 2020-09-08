package pw.cdmi.box.uam.user.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import pw.cdmi.box.uam.util.PropertiesUtils;
import pw.cdmi.common.domain.BasicConfig;
import pw.cdmi.common.domain.SystemConfig;

public class UserQos extends BasicConfig
{
    private static final long serialVersionUID = 2005354715875508084L;
    
    public static final String USER_QOS_CONFIG_PREFIX = "user.qos";
    
    private static final String USER_QOS_UPLOAD_TRAFFICE = "user.qos.upload.traffice";
    
    private static final String USER_QOS_DOWNLOAD_TRAFFICE = "user.qos.download.traffice";
    
    private long userId;
    
    @NotNull
    @Min(value = 0)
    @Digits(fraction = 0, integer = 10)
    private long uploadTraffic;
    
    @NotNull
    @Min(value = 0)
    @Digits(fraction = 0, integer = 10)
    private long downloadTraffic;
    
    private int concurrent;
    
    public UserQos()
    {
    }
    
    public UserQos(long uploadTraffic, long downloadTraffic, int concurrent)
    {
        this.uploadTraffic = uploadTraffic;
        this.downloadTraffic = downloadTraffic;
        this.concurrent = concurrent;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public long getUploadTraffic()
    {
        return uploadTraffic;
    }
    
    public void setUploadTraffic(long uploadTraffic)
    {
        this.uploadTraffic = uploadTraffic;
    }
    
    public long getDownloadTraffic()
    {
        return downloadTraffic;
    }
    
    public void setDownloadTraffic(long downloadTraffic)
    {
        this.downloadTraffic = downloadTraffic;
    }
    
    public int getConcurrent()
    {
        return concurrent;
    }
    
    public void setConcurrent(int concurrent)
    {
        this.concurrent = concurrent;
    }
    
    public List<SystemConfig> toConfigItem()
    {
        List<SystemConfig> list = new ArrayList<SystemConfig>(2);
        list.add(
            new SystemConfig(getAppId(), USER_QOS_UPLOAD_TRAFFICE, String.valueOf(this.getUploadTraffic())));
        list.add(new SystemConfig(getAppId(), USER_QOS_DOWNLOAD_TRAFFICE,
            String.valueOf(this.getDownloadTraffic())));
        return list;
    }
    
    /**
     * @param itemList
     * @return
     */
    public static UserQos buildUserQos(List<SystemConfig> itemList)
    {
        // Map<String, String> map = new HashMap<String, String>(16);
        // for (SystemConfig configItem : itemList)
        // {
        // map.put(configItem.getId(), configItem.getValue());
        // }
        UserQos userQos = new UserQos();
        userQos.setUploadTraffic(-1);
        userQos.setDownloadTraffic(-1);
        userQos.setAppId(PropertiesUtils.getProperty("defaultAppId"));
        return userQos;
    }
}

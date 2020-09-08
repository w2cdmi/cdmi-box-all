/**
 * 
 */
package com.huawei.sharedrive.isystem.cluster.domain.filesystem;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;

import com.huawei.sharedrive.isystem.cluster.FileSystemConstant;
import com.huawei.sharedrive.isystem.exception.InvalidParamException;
import com.huawei.sharedrive.isystem.util.Constants;
import com.huawei.sharedrive.thrift.filesystem.StorageInfo;

/**
 * 
 * @author d00199602
 * 
 */
public class UdsStorage extends BaseStorageInfo
{
    private static final long serialVersionUID = -4091122123285540962L;
    
    private static final int STORAGE_SIZE = 5;
    
    @NotBlank
    @Size(min = 1, max = 64)
    private String accessKey;
    
    @NotBlank
    @Size(min = 1, max = 64)
    private String secretKey;
    
    @NotBlank
    @Size(min = 1, max = 255)
    private String domain;
    
    @Max(65535)
    @Min(1)
    private int port;
    
    @Max(65535)
    @Min(1)
    private int httpsport;
    
    private String provider;
    
    public UdsStorage()
    {
        super();
    }
    
    public UdsStorage(StorageInfo storageInfo)
    {
        super(storageInfo);
        String[] temp = StringUtils.trimToEmpty(storageInfo.getEndpoint())
            .split(Constants.UDS_STORAGE_SPLIT_CHAR);
        if (temp.length < STORAGE_SIZE)
        {
            throw new InvalidParamException("temp's length is less than" + STORAGE_SIZE);
        }
        this.setDomain(temp[0]);
        this.setPort(Integer.parseInt(temp[1]));
        this.setHttpsport(Integer.parseInt(temp[2]));
        this.setAccessKey(temp[3]);
        // secretkey是EDToolsEnhance.decode加密的
        this.setSecretKey(temp[4]);
        if(temp.length > 6){
        	if("ALIAI".equals(temp[6])){
        		this.setProvider("storage.class.aliyun.ai");
        	}else{
        		this.setProvider("storage.class.unkown");
        	}
        }else{
        	this.setProvider("storage.class.standard");
        }
    }
    
    public String getAccessKey()
    {
        return accessKey;
    }
    
    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
    @Override
    public String getFsType()
    {
        return FileSystemConstant.FILE_SYSTEM_UDS;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    public int getHttpsport()
    {
        return httpsport;
    }
    
    public void setHttpsport(int port)
    {
        this.httpsport = port;
    }

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}

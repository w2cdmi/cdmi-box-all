package pw.cdmi.box.disk.user.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

import java.util.Date;

public class WxUserToken extends UsernamePasswordToken {
	
	public static byte IDENTITY_PERSON=1;
	
	public static byte IDENTITY_ENTERPRISE=0;
	
    private String code;

    private Long enterpriseId;

    private String deviceType;

    private String deviceAddress;

    private String proxyAddress;

    private String deviceAgent;

    private String deviceArea;

    private String deviceOS;

    private String objectSid;

    private String sessionWebId;

    private Date expired;

    private String regionIp;

    private String ownerDomain;
    
    private byte identity;

    public WxUserToken() {
        super();
    }

    public WxUserToken(String code) {
        super(code, new char[0], false);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOwnerDomain() {
        return ownerDomain;
    }

    public void setOwnerDomain(String ownerDomain) {
        this.ownerDomain = ownerDomain;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public String getProxyAddress() {
        return proxyAddress;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public String getDeviceAgent() {
        return deviceAgent;
    }

    public String getDeviceArea() {
        return deviceArea;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public String getObjectSid() {
        return objectSid;
    }

    public String getSessionWebId() {
        return sessionWebId;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void setDeviceAgent(String deviceAgent) {
        this.deviceAgent = deviceAgent;
    }

    public void setDeviceArea(String deviceArea) {
        this.deviceArea = deviceArea;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public void setObjectSid(String objectSid) {
        this.objectSid = objectSid;
    }

    public void setSessionWebId(String sessionWebId) {
        this.sessionWebId = sessionWebId;
    }

    public Date getExpired() {
        if (expired == null) {
            return null;
        }
        return (Date) expired.clone();
    }

    public void setExpired(Date expired) {
        if (expired == null) {
            this.expired = null;
        } else {
            this.expired = (Date) expired.clone();
        }
    }

    public String getRegionIp() {
        return regionIp;
    }

    public void setRegionIp(String regionIp) {
        this.regionIp = regionIp;
    }

	public byte getIdentity() {
		return identity;
	}

	public void setIdentity(byte identity) {
		this.identity = identity;
	}


    
    
}

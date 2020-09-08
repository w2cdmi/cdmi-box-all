package pw.cdmi.box.disk.user.shiro;

import java.util.Date;

import org.apache.shiro.authc.UsernamePasswordToken;

public class WxWorkUserToken extends UsernamePasswordToken {
    private String corpId;

    private String code;

    private String authCode;

    private String deviceType;

    private String deviceAddress;

    private String proxyAddress;

    private String deviceAgent;

    private String deviceArea;

    private String deviceOS;

    private String objectSid;

    private String sessionWebId;

    private Date expired;

    private long enterpriseId;

    private long accountId;

    private String regionIp;

    private String ownerDomain;

    public WxWorkUserToken() {
        super();
    }

    public WxWorkUserToken(String corpId, String code) {
        super(code, new char[0], false);
        this.corpId = corpId;
        this.code = code;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOwnerDomain() {
        return ownerDomain;
    }

    public void setOwnerDomain(String ownerDomain) {
        this.ownerDomain = ownerDomain;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getEnterpriseId() {
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
}

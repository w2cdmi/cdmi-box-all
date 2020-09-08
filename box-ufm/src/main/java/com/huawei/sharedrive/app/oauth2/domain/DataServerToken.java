package com.huawei.sharedrive.app.oauth2.domain;

import java.io.Serializable;
import java.util.Date;

public interface DataServerToken extends Serializable
{
    String CACHE_KEY_TEMP_PREFIX_ID = "temp_token_";
    
    String getToken();
    
    void setToken(String token);
    
    Date getExpiredAt();
    
    void setExpiredAt(Date expiredAt);
    
    String getAuth();
    
    void setAuth(String auth);
    
    String getTokenType();
    
    void setTokenType(String tokenType);
}

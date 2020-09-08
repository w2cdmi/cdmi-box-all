/**
 * 
 */
package com.huawei.sharedrive.app.share.service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.share.RestSharePageRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.SharePageV2;

/**
 * ShareToMe接口
 * 
 * @author l90003768
 * 
 */
public interface ShareToMeService
{
    
    /**
     * 获取共享给我的资源列表
     * 
     * @param user
     * @param sharedUserId
     * @param pageRequest
     * @return
     */
    SharePageV2 listShareToMeForClientV2(UserToken user, long sharedUserId, RestSharePageRequestV2 pageRequest)
        throws BaseRunException;
    
}

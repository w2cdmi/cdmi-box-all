package com.huawei.sharedrive.app.openapi.domain.share;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.share.domain.SharedUser;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 添加共享请求
 *
 * @author l90005448
 */
public class RestPutShareRequestV2 {

    private final static int MAX_LENGTH_OF_ADDITIONALLOG = 2048;// 共享的审批号最大长度为512

    private String additionalLog;

    private String roleName;

    private List<SharedUser> sharedUserList;
    
    public void checkAdditionalLog() {
        if (this.additionalLog == null) {
            return;
        }

        this.additionalLog = StringUtils.trimToEmpty(additionalLog);
        if (this.additionalLog.length() > MAX_LENGTH_OF_ADDITIONALLOG) {
            throw new InvalidParamException("additionLog length is " + additionalLog.length());
        }
    }

    public String getAdditionalLog() {
        return additionalLog;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setAdditionalLog(String additionalLog) {
        this.additionalLog = additionalLog;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<SharedUser> getSharedUserList() {
        return sharedUserList;
    }

    public void setSharedUserList(List<SharedUser> sharedUserList) {
        this.sharedUserList = sharedUserList;
    }
}

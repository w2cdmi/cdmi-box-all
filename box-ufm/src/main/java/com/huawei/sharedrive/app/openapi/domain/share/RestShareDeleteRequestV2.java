package com.huawei.sharedrive.app.openapi.domain.share;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.share.domain.SharedUser;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除共享，将被共享人从共享表中移除。
 *
 * @author l90005448
 */
public class RestShareDeleteRequestV2 {
    private List<SharedUser> userList = new ArrayList<>();

    public List<SharedUser> getUserList() {
        return userList;
    }

    public void setUserList(List<SharedUser> userList) {
        this.userList = userList;
    }
}

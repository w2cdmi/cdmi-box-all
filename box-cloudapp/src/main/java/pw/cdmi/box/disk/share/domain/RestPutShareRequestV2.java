package pw.cdmi.box.disk.share.domain;

import java.io.Serializable;
import java.util.List;

public class RestPutShareRequestV2 implements Serializable {
    private String roleName;

    private List<SharedUserV2> sharedUserList;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<SharedUserV2> getSharedUserList() {
        return sharedUserList;
    }

    public void setSharedUserList(List<SharedUserV2> sharedUserList) {
        this.sharedUserList = sharedUserList;
    }
}

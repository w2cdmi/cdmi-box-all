package pw.cdmi.box.website.domain;

import java.io.Serializable;

public class WxworkAuthCode implements Serializable {
    private String preauthCode;

    private String registerCode;

    public String getPreauthCode() {
        return preauthCode;
    }

    public void setPreauthCode(String preauthCode) {
        this.preauthCode = preauthCode;
    }

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }
}

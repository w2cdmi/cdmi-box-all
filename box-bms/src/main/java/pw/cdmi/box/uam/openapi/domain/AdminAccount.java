package pw.cdmi.box.uam.openapi.domain;

public class AdminAccount {
	 
    private String loginName;
    
    private String name;
    
    //1: isystem super admin
    //2: isystem admin
    //3: bms super admin
    //4: bms admin
    private byte type;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}
    
}

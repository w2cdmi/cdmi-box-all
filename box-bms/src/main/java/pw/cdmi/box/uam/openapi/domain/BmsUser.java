package pw.cdmi.box.uam.openapi.domain;

public class BmsUser 
{
    
	private String loginName;
    
    private String name;
    

    private String email;
    
  
    
    private String noteDesc;
   
    private byte type;
    
//    public Set<AdminRole> getRoles()
//    {
//        return roles;
//    }
//    
//    public void setRoles(Set<AdminRole> roles)
//    {
//        this.roles = roles;
//    }


	public String getLoginName() {
		return loginName;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNoteDesc() {
		return noteDesc;
	}

	public void setNoteDesc(String noteDesc) {
		this.noteDesc = noteDesc;
	}
    
    
    
}

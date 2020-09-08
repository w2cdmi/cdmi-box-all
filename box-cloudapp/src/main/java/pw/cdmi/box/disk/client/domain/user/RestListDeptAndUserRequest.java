package pw.cdmi.box.disk.client.domain.user;

import java.io.Serializable;

public class RestListDeptAndUserRequest implements Serializable{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long deptId;

	    public Long getDeptId() {
	        return deptId;
	    }

	    public void setDeptId(Long deptId) {
	        this.deptId = deptId;
	    }
}

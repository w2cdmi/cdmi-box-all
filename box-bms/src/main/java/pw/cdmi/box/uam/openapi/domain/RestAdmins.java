package pw.cdmi.box.uam.openapi.domain;

import java.util.List;

public class RestAdmins {
	List<AdminAccount> admins;

	public List<AdminAccount> getAdmins() {
		return admins;
	}

	public void setAdmins(List<AdminAccount> admins) {
		this.admins = admins;
	}
}

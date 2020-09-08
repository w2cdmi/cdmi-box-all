package pw.cdmi.box.disk.doctype.domain;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.disk.utils.BusinessConstants;


public class RestDocTypeList {
	private List<DocUserConfig> docUserConfigs;

	public RestDocTypeList() {
		this.docUserConfigs = new ArrayList<DocUserConfig>(BusinessConstants.INITIAL_CAPACITIES);
	}

	public List<DocUserConfig> getDocUserConfigs() {
		return docUserConfigs;
	}

	public void setDocUserConfigs(List<DocUserConfig> docUserConfigs) {
		this.docUserConfigs = docUserConfigs;
	}

}

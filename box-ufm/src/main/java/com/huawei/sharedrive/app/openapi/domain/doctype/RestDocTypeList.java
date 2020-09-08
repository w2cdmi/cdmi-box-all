package com.huawei.sharedrive.app.openapi.domain.doctype;

import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.utils.BusinessConstants;

import pw.cdmi.box.ufm.tools.domain.DocUserConfig;

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

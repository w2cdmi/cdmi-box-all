package pw.cdmi.box.disk.doctype.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.client.api.DoctypeClient;
import pw.cdmi.box.disk.doctype.domain.DocUserConfig;
import pw.cdmi.box.disk.doctype.service.DocTypeService;
import pw.cdmi.core.restrpc.RestClient;

@Component
public class DocTypeServiceImpl implements DocTypeService {

	@Resource
	private RestClient ufmClientService;

	private DoctypeClient doctypeClient;

	@PostConstruct
	public void init() {
		doctypeClient = new DoctypeClient(ufmClientService);
	}

	@Override
	public DocUserConfig getDoctypeById(long id, String token) {
		DocUserConfig docType = doctypeClient.findDoctype(id, token);
		return docType;
	}

}

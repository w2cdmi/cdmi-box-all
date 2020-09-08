package pw.cdmi.box.disk.doctype.service;

import pw.cdmi.box.disk.doctype.domain.DocUserConfig;

public interface DocTypeService {
	DocUserConfig getDoctypeById(long id,String token);
}

package pw.cdmi.box.uam.httpclient;

import java.io.InputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.box.uam.system.service.AccessAddressService;
import pw.cdmi.common.domain.AccessAddressConfig;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;
/**
 * 用以调用UAM中的指定rest接口
 *
 * author Jeffrey, 20161117
 * 
 * 

*/
public class UAMRestClient extends RestClient {

	@Autowired
	private AccessAddressService accessAddressService;

	
	private String getUAMServicePath() {
		AccessAddressConfig address = accessAddressService.getAccessAddress();
		return address.getUamInnerAddress();
	}
	

	@Override
	public TextResponse performDelete(String apiPath, Map<String, String> headers) throws ServiceException {
		String requestUri = getUAMServicePath() + apiPath;
		return performDeleteByUri(requestUri, headers);
	}

	@Override
	public TextResponse performGetText(String apiPath, Map<String, String> headers) throws ServiceException {
		String requestUri = getUAMServicePath() + apiPath;
		return performGetTextByUri(requestUri, headers);
	}

	@Override
	public TextResponse performJsonPostTextResponse(String apiPath, Map<String, String> headers, Object requestBody)
			throws ServiceException {
		String requestUri = getUAMServicePath() + apiPath;
		return performJsonPostTextResponseByUri(requestUri, headers, requestBody);
	}

	@Override
	public StreamResponse performJsonPostStreamResponse(String apiPath, Map<String, String> headers, Object requestBody)
			throws ServiceException {
		String requestUri = getUAMServicePath() + apiPath;
		return performJsonPostStreamResponseByUri(requestUri, headers, requestBody);
	}

	@Override
	public TextResponse performJsonPutTextResponse(String apiPath, Map<String, String> headers, Object requestBody)
			throws ServiceException {
		String requestUri = getUAMServicePath() + apiPath;
		return performJsonPutTextResponseByUri(requestUri, headers, requestBody);
	}

	@Override
	public StreamResponse performGetStream(String apiPath, Map<String, String> headers) throws ServiceException {
		String requestUri = getUAMServicePath() + apiPath;
		return performGetStreamByUri(requestUri, headers);
	}

	@Override
	public TextResponse performStreamPutTextResponse(String apiPath, Map<String, String> headers, InputStream in,
			long length) throws ServiceException {
		String requestUri = getUAMServicePath() + apiPath;
		return performStreamPutTextResponseByUri(requestUri, headers, in, length);
	}
}

package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.doctype.domain.DocUserConfig;
import pw.cdmi.box.disk.doctype.domain.RestCreateDocTypeRequest;
import pw.cdmi.box.disk.doctype.domain.RestDocTypeList;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class DoctypeClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DoctypeClient.class);

	private RestClient ufmClientService;

	public DoctypeClient(RestClient ufmClientService) {
		this.ufmClientService = ufmClientService;
	}
	
	public String modifyUserDoctype(RestCreateDocTypeRequest restCreateDocTypeRequest){
		StringBuilder uri = new StringBuilder(Constants.DOCTYPE_API_PATH);
		uri.append("/update");
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), null,
				restCreateDocTypeRequest);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return String.valueOf(response.getStatusCode());
		}

		LOGGER.error("createUserDoctype failure, response:" + response.getResponseBody());
		return response.getResponseBody();
	}

	public String createUserDoctype(RestCreateDocTypeRequest restCreateDocTypeRequest, String token) {
		StringBuilder uri = new StringBuilder(Constants.DOCTYPE_API_PATH);
		uri.append("/create");
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", token);
		TextResponse response = ufmClientService.performJsonPostTextResponse(uri.toString(), headerMap,
				restCreateDocTypeRequest);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return String.valueOf(response.getStatusCode());
		}

		LOGGER.error("createUserDoctype failure, response:" + response.getResponseBody());
		return response.getResponseBody();
	}

	public String delDocType(long id, String token) {
		StringBuilder uri = new StringBuilder(Constants.DOCTYPE_API_PATH);
		uri.append("/remove/id/").append(id);
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", token);
		TextResponse response = ufmClientService.performDelete(uri.toString(), headerMap);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return String.valueOf(response.getStatusCode());
		}

		LOGGER.error("createUserDoctype failure, response:" + response.getResponseBody());
		return response.getResponseBody();
	}

	public String delDoctypeByOwner(long ownerId, String token) {
		StringBuilder uri = new StringBuilder(Constants.DOCTYPE_API_PATH);
		uri.append("/remove/userId/").append(ownerId);
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", token);
		TextResponse response = ufmClientService.performDelete(uri.toString(), headerMap);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return String.valueOf(response.getStatusCode());
		}

		LOGGER.error("createUserDoctype failure, response:" + response.getResponseBody());
		return response.getResponseBody();
	}

	public RestDocTypeList findDoctypesByOwner(long ownerId, String token) {
		StringBuilder uri = new StringBuilder(Constants.DOCTYPE_API_PATH);
		uri.append("/find/owner/").append(ownerId);
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", token);

		TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
		String content = response.getResponseBody();

		RestDocTypeList restDocTypeList = null;
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			restDocTypeList = JsonUtils.stringToObject(content, RestDocTypeList.class);
		}
		return restDocTypeList;
	}

	public DocUserConfig findDoctype(long id, String token) {
		StringBuilder uri = new StringBuilder(Constants.DOCTYPE_API_PATH);
		uri.append("/find/id/").append(id);
		Map<String, String> headerMap = new HashMap<String, String>(1);
		headerMap.put("Authorization", token);

		TextResponse response = ufmClientService.performGetText(uri.toString(), headerMap);
		String content = response.getResponseBody();

		DocUserConfig docUserConfig = null;
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			docUserConfig = JsonUtils.stringToObject(content, DocUserConfig.class);
		}
		return docUserConfig;
	}
}

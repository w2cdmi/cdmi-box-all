package com.huawei.sharedrive.isystem.httpclient;

import java.util.Map;

import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;

public class UAMRestClient extends RestClient {
	private String uamInnerAddress;

	public String getUamInnerAddress() {
		return uamInnerAddress;
	}

	public void setUamInnerAddress(String uamInnerAddress) {
		this.uamInnerAddress = uamInnerAddress;
	}

	@Override
	public TextResponse performDelete(String apiPath, Map<String, String> headers) throws ServiceException {
		String requestUri = uamInnerAddress + apiPath;
		return performDeleteByUri(requestUri, headers);
	}

	@Override
	public TextResponse performGetText(String apiPath, Map<String, String> headers) throws ServiceException {
		String requestUri = uamInnerAddress + apiPath;
		return performGetTextByUri(requestUri, headers);
	}

	@Override
	public TextResponse performJsonPostTextResponse(String apiPath, Map<String, String> headers, Object requestBody)
			throws ServiceException {
		String requestUri = uamInnerAddress + apiPath;
		return performJsonPostTextResponseByUri(requestUri, headers, requestBody);
	}

	@Override
	public StreamResponse performJsonPostStreamResponse(String apiPath, Map<String, String> headers, Object requestBody)
			throws ServiceException {
		String requestUri = uamInnerAddress + apiPath;
		return performJsonPostStreamResponseByUri(requestUri, headers, requestBody);
	}

	@Override
	public TextResponse performJsonPutTextResponse(String apiPath, Map<String, String> headers, Object requestBody)
			throws ServiceException {
		String requestUri = uamInnerAddress + apiPath;
		return performJsonPutTextResponseByUri(requestUri, headers, requestBody);
	}

	@Override
	public StreamResponse performGetStream(String apiPath, Map<String, String> headers) throws ServiceException {
		String requestUri = uamInnerAddress + apiPath;
		return performGetStreamByUri(requestUri, headers);
	}

}

package pw.cdmi.box.disk.openapi.rest.v2;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.openapi.rest.v2.domain.JsAuthObject;
import pw.cdmi.box.disk.openapi.rest.v2.domain.PullImageRequest;
import pw.cdmi.box.disk.openapi.rest.v2.task.OfflineDownloadTask;
import pw.cdmi.box.disk.utils.CSRFTokenManager;
import pw.cdmi.common.deamon.DeamonService;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/api/v2/jsSDK")
public class JsSDKAPIController extends CommonController {
	private static Logger LOGGER = LoggerFactory.getLogger(JsSDKAPIController.class);

	@Resource
	private RestClient uamClientService;

	@Autowired
	private DeamonService deamonService;

	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/signature", method = RequestMethod.GET)
	public ResponseEntity<JsAuthObject> getAuthData(@RequestParam String url, @RequestParam String corpId, HttpServletRequest request) throws Exception {
		checkToken(request);

		//本系统使用的token机制
		String sToken = (String) request.getSession().getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
		JsAuthObject ticket = getJsSdkApiTicket(sToken, corpId, url);
		if(ticket == null) {
			LOGGER.error("Failed to create Corp JS-SDK signature: code = {}, ticket from ECM is null.");
			return new ResponseEntity<>(new JsAuthObject(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(ticket, HttpStatus.OK);
	}

	@RequestMapping(value = "/uploadPhoto", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadPhoto2Server(@RequestBody PullImageRequest pullRequest, HttpServletRequest request) {
        LOGGER.info("Receive the pull request: ownerId={}, parentId={}, filename={}, corpId={}, serverId={}", pullRequest.getOwnerId(), pullRequest.getParentId(), pullRequest.getFileName(), pullRequest.getCorpId(), pullRequest.getServerId());

        if(pullRequest.getOwnerId() == null || pullRequest.getParentId() == null || pullRequest.getCorpId() == null || pullRequest.getServerId() == null) {
            LOGGER.error("Invalid parameter. ownerId={}, parentId={}, corpId={}, serverId={}", pullRequest.getOwnerId(), pullRequest.getParentId(), pullRequest.getCorpId(), pullRequest.getServerId());
            return new ResponseEntity<>("Invalid parameter", HttpStatus.BAD_REQUEST);
        }

	    //如果没有指定文件名，使用随机字符串
        if (pullRequest.getFileName() == null) {
            pullRequest.setFileName(UUID.randomUUID() + ".jpg");
        }

		String corpToken = getCorpToken(pullRequest.getToken(), pullRequest.getCorpId());
		if(StringUtils.isBlank(corpToken)) {
			return new ResponseEntity<>("Can't get the corp access token: corpId=" + pullRequest.getCorpId(), HttpStatus.FORBIDDEN);
		}

		String requestUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
		requestUrl = requestUrl.replace("ACCESS_TOKEN", corpToken).replace("MEDIA_ID", pullRequest.getServerId());

		LOGGER.debug("Download the photo from wx server: " + requestUrl);

		//后台执行
		OfflineDownloadTask task = SpringContextUtil.getBean(OfflineDownloadTask.class);
		task.setUrl(requestUrl);
		task.setOwnerId(pullRequest.getOwnerId());
		task.setParentId(pullRequest.getParentId());
		task.setFileName(pullRequest.getFileName());
		task.setToken(super.getToken());

		deamonService.execute(task);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private String getCorpToken(String token, String corpId) {
		Map<String, String> headerMap = new HashMap<>(1);
		headerMap.put("Authorization", token);
		TextResponse response = uamClientService.performGetText("/api/v2/wxOauth2/getCorpToken?corpId=" + corpId, headerMap);

		if(response.getStatusCode() != HttpStatus.OK.value()) {
			LOGGER.error("Failed to get Corp Access Token: code = {}, error = {}", response.getStatusCode(), response.getResponseBody());
			return null;
		}

		return response.getResponseBody();
	}

	private JsAuthObject getJsSdkApiTicket(String token, String corpId, String url) {
		Map<String, String> headerMap = new HashMap<>(1);
		headerMap.put("Authorization", token);
		try {
			TextResponse response = uamClientService.performGetText("/api/v2/wxOauth2/getWxWorkJsApiTicket?corpId=" + corpId + "&url=" + URLEncoder.encode(url, "UTF-8"), headerMap);

			if(response.getStatusCode() != HttpStatus.OK.value()) {
                LOGGER.error("Failed to get Corp JS-SDK Ticket: code = {}, error = {}", response.getStatusCode(), response.getResponseBody());
                return null;
            }

			return  JsonUtils.stringToObject(response.getResponseBody(), JsAuthObject.class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
}
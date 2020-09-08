package pw.cdmi.box.disk.doctype.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pw.cdmi.box.disk.client.api.DoctypeClient;
import pw.cdmi.box.disk.doctype.domain.DocUserConfig;
import pw.cdmi.box.disk.doctype.domain.RestCreateDocTypeRequest;
import pw.cdmi.box.disk.doctype.domain.RestDocTypeList;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.core.restrpc.RestClient;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/user/doctype")
public class DoctypeUserController extends CommonController {

	@Resource
	private RestClient ufmClientService;

	private DoctypeClient doctypeClient;

	@PostConstruct
	public void init() {
		doctypeClient = new DoctypeClient(ufmClientService);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model) {
		UserToken userInfo = getCurrentUser();
		model.addAttribute("user", userInfo);
		return "user/settings/doctype";
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createDoctype() {
		return "user/settings/addDoctype";
	}

	@RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
	public String modifyDoctype(@PathVariable("id") long id, Model model) {
		String token = getToken();
		DocUserConfig docUserConfig = doctypeClient.findDoctype(id, token);
		model.addAttribute("doctypeConfig", docUserConfig);
		return "user/settings/addDoctype";
	}

	@ResponseBody
	@RequestMapping(value = "/modifyDoctype", method = RequestMethod.POST)
	public ResponseEntity<String> modifyDoctype(RestCreateDocTypeRequest restCreateDocTypeRequest) {
		try {
			restCreateDocTypeRequest.setUserId(getCurrentUser().getCloudUserId());
			String responsText = doctypeClient.modifyUserDoctype(restCreateDocTypeRequest);
			if (responsText.equals(String.valueOf(HttpStatus.OK.value()))) {
				return new ResponseEntity<String>(HttpStatus.OK);
			}
			return new ResponseEntity<String>(responsText, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/createDoctype", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createDoctype(RestCreateDocTypeRequest restCreateDocTypeRequest,
			HttpServletRequest httpServletRequest) {
		try {
			super.checkToken(httpServletRequest);
			restCreateDocTypeRequest.setUserId(getCurrentUser().getCloudUserId());
			String responsText = doctypeClient.createUserDoctype(restCreateDocTypeRequest, getToken());

			if (responsText.equals(String.valueOf(HttpStatus.OK.value()))) {
				return new ResponseEntity<String>(HttpStatus.OK);
			}
			return new ResponseEntity<String>(responsText, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/remove/id/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delDoctype(@PathVariable("id") long id, HttpServletRequest request) {
		try {
			// super.checkToken(request);

			String responsText = doctypeClient.delDocType(id, getToken());

			if (responsText.equals(String.valueOf(HttpStatus.OK.value()))) {
				return new ResponseEntity<String>(HttpStatus.OK);
			}
			return new ResponseEntity<String>(responsText, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/remove/owner", method = RequestMethod.DELETE)
	public ResponseEntity<String> delDoctypeByOwner(long userId, HttpServletRequest request) {
		try {
			super.checkToken(request);
			String responsText = doctypeClient.delDocType(userId, getToken());

			if (responsText.equals(String.valueOf(HttpStatus.OK.value()))) {
				return new ResponseEntity<String>(HttpStatus.OK);
			}
			return new ResponseEntity<String>(responsText, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/find/owner", method = RequestMethod.GET)
	public ResponseEntity<RestDocTypeList> findDoctypeByOwner(HttpServletRequest request) {
		try {
			// super.checkToken(request);
			UserToken user = getCurrentUser();
			RestDocTypeList docTypeList = doctypeClient.findDoctypesByOwner(user.getCloudUserId(), getToken());

			return new ResponseEntity<RestDocTypeList>(docTypeList, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<RestDocTypeList>(HttpStatus.BAD_REQUEST);
		}
	}

	// @ResponseBody
	// @RequestMapping(value = "/find/id", method = RequestMethod.GET)
	// public ResponseEntity<DocUserConfig> findDoctypeById(long id,
	// HttpServletRequest request) {
	// try {
	// super.checkToken(request);
	// DocUserConfig docType = doctypeClient.findDoctypesByOwner(userId,
	// getToken());
	//
	// return new ResponseEntity<DocUserConfig>(docTypeList, HttpStatus.OK);
	// } catch (Exception e) {
	// return new ResponseEntity<DocUserConfig>(HttpStatus.BAD_REQUEST);
	// }
	// }
}

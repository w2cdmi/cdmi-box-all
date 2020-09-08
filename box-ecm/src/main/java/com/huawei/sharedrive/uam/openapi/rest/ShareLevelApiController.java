package com.huawei.sharedrive.uam.openapi.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huawei.sharedrive.uam.weixin.service.ShareLevelService;

@Controller
@RequestMapping(value = "/api/v2/shareLevel")
public class ShareLevelApiController {
	
	@Autowired
	private ShareLevelService shareLevelService;
	
	@RequestMapping(value = "/items", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> shareLevelList() {
		
		return new ResponseEntity<>(shareLevelService.list(),HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable("id") int id) {
		
		return new ResponseEntity<>(shareLevelService.get(id),HttpStatus.OK);
	}


}

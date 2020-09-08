package pw.cdmi.box.disk.wxrobot.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.WxRobot;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.box.disk.weixin.domain.WxUser;
import pw.cdmi.box.disk.weixin.service.WxUserService;
import pw.cdmi.box.disk.wxrobot.model.WxRobotConfig;
import pw.cdmi.box.disk.wxrobot.service.WxLoginTask;
import pw.cdmi.box.disk.wxrobot.service.WxRobotService;
import pw.cdmi.box.disk.wxrobot.util.Robot;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.common.deamon.DeamonService;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/wxRobot")
public class WxRobotController extends CommonController{
	
	@Resource
	private WxRobotService  wxRobotService;
	
	@Autowired
	private DeamonService executeService;
	
	@Autowired
	private RestClient ufmClientService;
	
	@Autowired
	Robot robot;
	
	@Resource(name = "cacheClient")
	private CacheClient cacheClient;
	
	@Autowired
	private WxUserService wxUserService;
	
	private static Logger logger = LoggerFactory.getLogger(WxRobotController.class);

	private String wwRedirectUrl = "https://www.jmapi.cn/?qr=ww";

	private String wwAppId = "wwc7342fa63c523b9a";

	public String getWwRedirectUrl() {
		return wwRedirectUrl;
	}

	public void setWwRedirectUrl(String wwRedirectUrl) {
		this.wwRedirectUrl = wwRedirectUrl;
	}

	public String getWwAppId() {
		return wwAppId;
	}

	public void setWwAppId(String wwAppId) {
		this.wwAppId = wwAppId;
	}

	@PostConstruct
    public void startRobot(){
    	robot.listenMsgMode();
    }
	
    @RequestMapping(value = "/createQrCode", method = RequestMethod.GET)
    public String createQrCode(HttpServletRequest req, HttpServletResponse resp,Locale locale)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	try {
    		WxRobot wxRobot=wxRobotService.getRunningWxRobot(getToken());
    		boolean isRun=false;
    		if(wxRobot==null){
    			String uuid = wxRobotService.getUUID();
        		String tempWxFilePath= req.getSession().getServletContext().getRealPath("/")+"/WEB-INF/views/qrcode/";
        	    //处理登录事件
        		if(userToken.getEnterpriseId()!=0){
        			executeService.execute(new WxLoginTask(uuid,robot,wxRobotService,cacheClient,getCurrentUser(),tempWxFilePath,getToken(),locale,ufmClientService));
        		}else{
        			WxUser wxUser=wxUserService.getByCloudUserId(userToken.getCloudUserId());
        			executeService.execute(new WxLoginTask(uuid,robot,wxRobotService,cacheClient,wxUser,tempWxFilePath,getCurrentUser(),getToken(),locale,ufmClientService));
        		}
        		
        		req.setAttribute("uuid", uuid.replaceAll("=", ""));
        		req.setAttribute("serverAddr", PropertiesUtils.getProperty("self.serviceAddr"));	
    		}else{
    			isRun=true;
    			req.setAttribute("uin", wxRobot.getWxUin());
    			req.setAttribute("robotId", wxRobot.getId());
    			req.setAttribute("wxName", wxRobot.getWxName());
    		}
    		req.setAttribute("isRun",isRun);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return "qrcode/qrcode";
    }
    
    
    
    @RequestMapping(value = "/wxBak", method = RequestMethod.GET)
    public String wxBak(HttpServletRequest req, Model model, Locale locale)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	try {
    		WxRobot wxRobot=wxRobotService.getRunningWxRobot(getToken());
    		boolean isRun=false;
    		if(wxRobot==null){
    			String uuid = wxRobotService.getUUID();
        		String tempWxFilePath= req.getSession().getServletContext().getRealPath("/")+"/WEB-INF/views/qrcode/";
        		cacheClient.addCache("robot"+uuid, "200");
        	    //处理登录事件
        		if(userToken.getEnterpriseId()!=0){
        			executeService.execute(new WxLoginTask(uuid,robot,wxRobotService,cacheClient,getCurrentUser(),tempWxFilePath,getToken(),locale,ufmClientService));
        		}else{
        			WxUser wxUser=wxUserService.getByCloudUserId(userToken.getCloudUserId());
        			executeService.execute(new WxLoginTask(uuid,robot,wxRobotService,cacheClient,wxUser,tempWxFilePath,getCurrentUser(),getToken(),locale,ufmClientService));
        		}
        		
        		req.setAttribute("uuid", uuid.replaceAll("=", ""));
        		req.setAttribute("serverAddr", PropertiesUtils.getProperty("self.serviceAddr"));	
    		}else{
    			isRun=true;
    			req.setAttribute("uin", wxRobot.getWxUin());
    			req.setAttribute("robotId", wxRobot.getId());
    			req.setAttribute("wxName", wxRobot.getWxName());
    		}

			model.addAttribute("wwRedirectUrl", URLEncoder.encode(wwRedirectUrl, "UTF-8"));
			model.addAttribute("wwAppId", wwAppId);

    		req.setAttribute("isRun",isRun);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return "qrcode/wxBak";
    }
    
    
    
    @RequestMapping(value = "/listRobots", method = RequestMethod.GET)
    @ResponseBody
    public List<WxRobot> listRobots(HttpServletRequest req, HttpServletResponse resp)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	List<WxRobot>  robotList=wxRobotService.listRobots(getToken());
		return robotList;
    }
    
    
    @RequestMapping(value = "/stopRobot", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> stopRobot(HttpServletRequest req, HttpServletResponse resp,long robotId)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	wxRobotService.stopRobot(robotId,getToken());
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/listWxRobotConfig", method = RequestMethod.POST)
    @ResponseBody
    public List<WxRobotConfig> getWxRobotConfig(long robotId,HttpServletRequest req, HttpServletResponse resp)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	List<WxRobotConfig>  configList=wxRobotService.listWxRobotConfig(robotId,getToken());
		return configList;
    }
    
    
    @RequestMapping(value = "/createConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createConfig(HttpServletRequest req, HttpServletResponse resp,
    		long robotId,String name,long value,byte type)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	WxRobotConfig wxRobotConfig=new WxRobotConfig();
    	wxRobotConfig.setRobotId(robotId);
    	wxRobotConfig.setName(name);
    	wxRobotConfig.setType(type);
    	wxRobotConfig.setValue(value);
    	wxRobotService.createConfig(wxRobotConfig, getToken());
		return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/updateConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> updateConfig(HttpServletRequest req, HttpServletResponse resp,
    		long robotId,long value,byte type)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	WxRobotConfig wxRobotConfig=new WxRobotConfig();
    	wxRobotConfig.setRobotId(robotId);
    	wxRobotConfig.setValue(value);
    	wxRobotConfig.setType(type);
    	wxRobotService.updateConfig(wxRobotConfig, getToken());
		return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "/deleteConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> deleteConfig(HttpServletRequest req, HttpServletResponse resp,
    		long robotId,String name,byte type)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	WxRobotConfig wxRobotConfig=new WxRobotConfig();
    	wxRobotConfig.setRobotId(robotId);
    	wxRobotConfig.setName(name);
    	wxRobotConfig.setType(type);
    	wxRobotService.deleteConfig(wxRobotConfig, getToken());
		return new ResponseEntity<>(HttpStatus.OK);
    }
    
    
    
    @RequestMapping(value = "/listWxRobotGroups", method = RequestMethod.POST)
    @ResponseBody
    public List<String>  listWxRobotGroups(String uin,HttpServletRequest req, HttpServletResponse resp)throws IOException, ServletException {
    	UserToken userToken=getCurrentUser();
    	WxRobot  wxRobot=wxRobotService.getRobotByUin(uin, getToken());
    	if(wxRobot.getStatus()==WxRobot.STATUS_RUNNING){
    		String key = WxRobotService.WX_PREFIX + wxRobot.getWxUin();
			JSONObject wxUser = (JSONObject) cacheClient.getCache(key);
			JSONArray groupList=wxUser.getJSONArray("groupList");
			List<String> groupNames=new ArrayList<>();
			for(int i=0;i<groupList.size();i++){
				JSONValue NickName=groupList.getJSONObject(i).get("NickName");
//				System.out.println(NickName);
				if(!NickName.isNull()){
					groupNames.add(NickName.asString());
				}
			}
			return groupNames;
    	}
		return null;
    }
   
    @RequestMapping(value = "/checkRobotStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> checkRobotStatus( HttpServletRequest req, HttpServletResponse resp){
    	UserToken userToken=getCurrentUser();
    	try {
    		WxRobot wxRobot=null;
    		if(cacheClient.getCache("r"+userToken.getCloudUserId())!=null){
    			if(cacheClient.getCache("r"+userToken.getCloudUserId()).equals("1203")){
    				return new ResponseEntity<String>("notsupport",HttpStatus.OK);
    			}
    		}
    		List<WxRobot> list=wxRobotService.listRobots(getToken());
    		for(int i=0;i<list.size();i++){
    			wxRobot=list.get(i);
    		}
    		if(wxRobot!=null){
    			if(wxRobot.getStatus()==WxRobot.STATUS_RUNNING){
    				return new ResponseEntity<String>("true",HttpStatus.OK);
    			}else if(wxRobot.getStatus()==WxRobot.STATUS_NONSUPPORT){
    				return new ResponseEntity<String>("notsupport",HttpStatus.OK);
    			}else{
    				return new ResponseEntity<String>("false",HttpStatus.OK);	
    			}
    			
    		}else{
    			return new ResponseEntity<String>("false",HttpStatus.OK);
    		}
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
    }
    
}
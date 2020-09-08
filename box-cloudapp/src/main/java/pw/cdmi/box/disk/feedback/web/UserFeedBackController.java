package pw.cdmi.box.disk.feedback.web;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.client.api.UserFeedBackClient;
import pw.cdmi.box.disk.feedback.domain.FeedBackStatus;
import pw.cdmi.box.disk.feedback.domain.RestFeedBackCreateRequest;
import pw.cdmi.box.disk.feedback.domain.RestFeedBackDetail;
import pw.cdmi.box.disk.feedback.domain.RestFeedBackInfo;
import pw.cdmi.box.disk.feedback.domain.RestFeedBackSubInfo;
import pw.cdmi.box.disk.feedback.domain.RestUserFeedBackListRequest;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.CSRFTokenManager;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.restrpc.RestClient;



@Controller
@RequestMapping(value = "/feedback")
public class UserFeedBackController extends CommonController
{

     private static final Logger logger = LoggerFactory.getLogger(UserFeedBackController.class);

     @Resource
     private RestClient uamClientService ;
     
     @Autowired
     private UserTokenManager userTokenManager;
     
     
     protected String getToken()
     {
         return userTokenManager.getToken();
     }
     
     /**
      * 
      * 前往用户反馈列表
      * 
      * @return 用户反馈列表页面
      * 
      */
     @RequestMapping(value = "feedBackList",method = RequestMethod.GET)
     public String list(Model model)
     {
    	 RestUserFeedBackListRequest condition = new RestUserFeedBackListRequest();
          
         UserToken userInfo = getCurrentUser();
         condition.setCustomerID(String.valueOf(userInfo.getCloudUserId()));//userInfo.getId()

         PageRequest pageRequest = new PageRequest();
         pageRequest.setSize( Constants.DEFAULT_PAGE_SIZE);
         
         condition.setLimit(pageRequest.getPageSize());

         Page<RestFeedBackInfo> feedBackInfoList = new UserFeedBackClient(uamClientService).getFeedBackList(getToken(),condition,pageRequest);
         model.addAttribute("userFeedBackList", feedBackInfoList);
         model.addAttribute("condition", condition);
         model.addAttribute("statusTypeList",getStatusTypeList());
         
        return "feedback/userFeedBackList";
     }



     /**
      * 
      * 点击查询按钮查询用户反馈列表
      *
      * @param condition 查询条件
      * @param page 分页
      * @param model 返回信息
      * @param token 鉴权
      * @return 用户反馈信息集合
      *
      */
     @RequestMapping(value = "list", method = RequestMethod.POST)
     public String list(RestUserFeedBackListRequest condition, Integer page, Model model, String token)
     {
         if (StringUtils.isBlank(token)
             || !token.equals(SecurityUtils.getSubject()
                 .getSession()
                 .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
         {
             throw new BusinessException(401, "invalid token");
         }

         Date twBeginTime = condition.getTwBeginTime();
         Date twEndTime = condition.getTwEndTime();
         Date dfBeginTime = condition.getDfBeginTime();
         Date dfEndTime = condition.getDfEndTime();

         if (null != twBeginTime && null != twEndTime && twBeginTime.after(twEndTime))
         {
              throw new InvalidParamException("start time cannot be lagger than end time");
         }

         if (null != dfBeginTime && null != dfEndTime && dfBeginTime.after(dfEndTime))
         {
              throw new InvalidParamException("start time cannot be lagger than end time");
         }
         
         PageRequest pageRequest = new PageRequest();
         pageRequest.setSize(Constants.DEFAULT_PAGE_SIZE);
         int offset = 0;
         if (page != null)
         {
              pageRequest.setPage(page.intValue());
              
              int currentPage= page.intValue();
              if(currentPage>0){
            	  offset = (currentPage-1)*pageRequest.getPageSize();
              }		  
         }
         condition.setLimit(pageRequest.getPageSize());
         condition.setOffset(Long.valueOf(offset));
        

         Page<RestFeedBackInfo> feedBackInfoList = new UserFeedBackClient(uamClientService).getFeedBackList(getToken(),condition,pageRequest);
         model.addAttribute("userFeedBackList", feedBackInfoList);
         model.addAttribute("condition", condition);
         model.addAttribute("statusTypeList",getStatusTypeList());
         return "feedback/userFeedBackList";
     }
 
     
     /**
      * 查询意见反馈列表（分页）
      * @param problemStatus 状态
      * @param cycle 周期
      * @param problemTitle 标题
      * @param customerID 用户ID（当前登录用户）
      * @param page 分页信息
      * @param req 请求
      * @param token 鉴权
      * @return
      */
     @RequestMapping(value = "listFeedback", method = RequestMethod.POST)
     public ResponseEntity<Object[]> listFeedback(String problemStatus,String cycle,String problemTitle,String customerID,
    		Integer page, HttpServletRequest req, String token)
     {
    	 RestUserFeedBackListRequest condition = new RestUserFeedBackListRequest();

         condition.setProblemStatus(problemStatus);
         condition.setCycle(cycle);
         condition.setProblemTitle(problemTitle);
         condition.setCustomerID(customerID);

    	 if (StringUtils.isBlank(token)
	             || !token.equals(SecurityUtils.getSubject()
	                 .getSession()
	                 .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
	         {
	             throw new BusinessException(401, "invalid token");
	         }

	         Date twBeginTime = condition.getTwBeginTime();
	         Date twEndTime = condition.getTwEndTime();
	         Date dfBeginTime = condition.getDfBeginTime();
	         Date dfEndTime = condition.getDfEndTime();

	         if (null != twBeginTime && null != twEndTime && twBeginTime.after(twEndTime))
	         {
	              throw new InvalidParamException("start time cannot be lagger than end time");
	         }

	         if (null != dfBeginTime && null != dfEndTime && dfBeginTime.after(dfEndTime))
	         {
	              throw new InvalidParamException("start time cannot be lagger than end time");
	         }
	         
	         PageRequest pageRequest = new PageRequest();
	         pageRequest.setSize(Constants.DEFAULT_PAGE_SIZE);
	         int offset = 0;
	         if (page != null)
	         {
	              pageRequest.setPage(page.intValue());
	              
	              int currentPage= page.intValue();
	              if(currentPage>0){
	            	  offset = (currentPage-1)*pageRequest.getPageSize();
	              }		  
	         }
	         condition.setLimit(pageRequest.getPageSize());
	         condition.setOffset(Long.valueOf(offset));
	         
    	 Page<RestFeedBackInfo> feedBackInfoList = new UserFeedBackClient(uamClientService).getFeedBackList(getToken(),condition,pageRequest);

    	 RestFeedBackInfo.htmlEscape(feedBackInfoList.getContent());
         Object[] arr = new Object[]{feedBackInfoList};
         return new ResponseEntity<Object[]>(arr, HttpStatus.OK);
         
     }
     
     
     
     
     /**
      * 
      * 前往添加用户反馈页面
      * 
      * @param model
      *             用户反馈信息
      * @return 用户反馈页面
      * 
      */
     @RequestMapping(value = "/openAddFeedBack" , method = RequestMethod.GET)
     public String openAddFeedBack(Model model)
     {
          return "feedback/addFeedBack";
     }


     /**
      * 
      * 创建用户反馈
      * 
      * @param restFeedBackCreateRequest
      *             用户反馈请求信息
      * @param httpServletRequest
      *             通用http请求
      * @return 返回结果
      * 
      */
     @RequestMapping(value = "/createFeedBack" , method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity <String> createFeedBack(RestFeedBackCreateRequest restFeedBackCreateRequest ,
               HttpServletRequest httpServletRequest)
     {
    	 
          try
          {
        	   logger.info("Enter into /feedback/createFeedBack,restFeedBackCreateRequest="+restFeedBackCreateRequest.toString());
        	   
               super.checkToken(httpServletRequest);
               UserToken userInfo = getCurrentUser();
               restFeedBackCreateRequest.setCustomerID(String.valueOf(userInfo.getCloudUserId()));//String.valueOf(userInfo.getId())
               restFeedBackCreateRequest.setCustomerName(userInfo.getName());
               restFeedBackCreateRequest.setCustomerEmail(userInfo.getEmail());
               Date date = new Date();
               restFeedBackCreateRequest.setCustomerTwTime(date);
               restFeedBackCreateRequest.setNewestTwTime(date);
               restFeedBackCreateRequest.setProblemStatus("0");

              String status = new UserFeedBackClient(uamClientService).createFeedBack(getToken(),restFeedBackCreateRequest);
             
              // 当在uam禁用用户时，此用户已登录云盘，打开‘意见反馈’反馈页面，再次提意见应该提示失败。
              if(Integer.parseInt(status) != HttpStatus.OK.value() ) {
            	  return new ResponseEntity <String>(HttpStatus.BAD_REQUEST);
              }

              return new ResponseEntity <String>(HttpStatus.OK);
          }
          catch (Exception e)
          {
               logger.error(e.getMessage() , e);
               return new ResponseEntity <String>(HttpStatus.BAD_REQUEST);
          }
     }
     


	@RequestMapping(value = "/updateFeedBack" , method = RequestMethod.PUT)
     @ResponseBody
     public ResponseEntity <String> updateFeedBack(RestFeedBackInfo restFeedBackInfo ,
               HttpServletRequest httpServletRequest)
     {
          try
          {
        	  new UserFeedBackClient(uamClientService).updateFeedBack(getToken(),restFeedBackInfo);

               return new ResponseEntity <String>(HttpStatus.OK);
          }
          catch (Exception e)
          {
               logger.error(e.getMessage() , e);
               return new ResponseEntity <String>(HttpStatus.BAD_REQUEST);
          }
     }
     
     
     /**
      * 更新用户反馈（追问）
      * @param model
      * @return
      */
     @RequestMapping(value = "toAsk", method = RequestMethod.GET)
     public String toAskPage(long problemID,Model model)
     {
    	 RestFeedBackInfo feedBackInfo = new UserFeedBackClient(uamClientService).getFeedBackByID(problemID,getToken());
            
    	 RestFeedBackDetail feedBackDetail = new RestFeedBackDetail();
    	 
    	 feedBackDetail.setCustomerTwTime(feedBackInfo.getCustomerTwTime());
    	 feedBackDetail.setCustomerID(feedBackInfo.getCustomerID());
    	 feedBackDetail.setCustomerName(feedBackInfo.getCustomerName());
    	 feedBackDetail.setProblemID(feedBackInfo.getProblemID());
    	 feedBackDetail.setProblemDescription(feedBackInfo.getProblemDescription());
    	 feedBackDetail.setProblemStatus(feedBackInfo.getProblemStatus());
    	 feedBackDetail.setProblemTitle(feedBackInfo.getProblemTitle());
    	 feedBackDetail.setProblemType(feedBackDetail.getProblemType());
    	 
    	 
         List<RestFeedBackSubInfo> restFeedBackSubList = new UserFeedBackClient(uamClientService).getFeedBackSubList(problemID,getToken());

         model.addAttribute("feedBackDetail",feedBackDetail);
         model.addAttribute("restFeedBackSubList", restFeedBackSubList);
         
         return "feedback/feedBackAsk";
     }
     
     
     
     
   
     @RequestMapping(value = "modify", method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity <String> userAsk(RestFeedBackDetail feedBackDetail, HttpServletRequest request, String token)
     {
          try{
        	   RestFeedBackInfo restFeedBackInfo = new UserFeedBackClient(uamClientService).getFeedBackByID(feedBackDetail.getProblemID(),getToken());

        	   feedBackDetail.setCustomerID(String.valueOf(restFeedBackInfo.getCustomerID()));
        	   feedBackDetail.setCustomerName(restFeedBackInfo.getCustomerName());
               
               //问题状态为打开
               feedBackDetail.setProblemStatus("0");
               feedBackDetail.setNewestTwTime(new Date());    
               feedBackDetail.setManagerAnswerTime(restFeedBackInfo.getManagerAnswerTime());
               new UserFeedBackClient(uamClientService).updateTeedBackTime(feedBackDetail,token);
               
               //表示追问问题
               RestFeedBackSubInfo restFeedBackInfoSub= new RestFeedBackSubInfo();
               restFeedBackInfoSub.setIsAnswer("0");
               restFeedBackInfoSub.setDescription(feedBackDetail.getProblemDescription());
               restFeedBackInfoSub.setProblemID(feedBackDetail.getProblemID());
               restFeedBackInfoSub.setUpdateTime(new Date());
               restFeedBackInfoSub.setUserID(feedBackDetail.getCustomerID());
               restFeedBackInfoSub.setUserName(feedBackDetail.getCustomerName());
               new UserFeedBackClient(uamClientService).addNewFeedBackSub(restFeedBackInfoSub,token);  
        
               //调用发邮件接口
          }catch(Exception e){
               logger.error("answer user feedback is failed!"+e);
               
               return new ResponseEntity <String>(HttpStatus.BAD_REQUEST);
          }
          return new ResponseEntity <String>(HttpStatus.OK);
     }


     @RequestMapping(value = "/delete" , method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity <String> deleteFeedBack(long problemID ,
               HttpServletRequest httpServletRequest)
     {
          try
          {
        	  new UserFeedBackClient(uamClientService).deleteFeedBack(getToken(),problemID);
              return new ResponseEntity <String>(HttpStatus.OK);
          }
          catch (Exception e)
          {
               logger.error(e.getMessage() , e);
               return new ResponseEntity <String>(HttpStatus.BAD_REQUEST);
          }
     }
     
     
     
     /**
      * 更新用户反馈（答复）
      * @param model
      * @return
      */
     @RequestMapping(value = "detail", method = RequestMethod.GET)
     public String showFeedBack(long problemID,Model model)
     {
         
         RestFeedBackInfo restFeedBackInfo = new UserFeedBackClient(uamClientService).getFeedBackByID(problemID,getToken());
        
         List<RestFeedBackSubInfo> restFeedBackSubList = new UserFeedBackClient(uamClientService).getFeedBackSubList(problemID,getToken());
 
         model.addAttribute("restFeedBackInfo",restFeedBackInfo);
         model.addAttribute("restFeedBackSubList", restFeedBackSubList);
         
         return "feedback/feedBackDetail";
     }
     
     
     /**
      * 批量删除用户反馈接口
      * @param ids 问题ID列表
      * @param request 请求信息
      * @param token 消息头
      * @return
      */
     @SuppressWarnings("rawtypes")
	 @RequestMapping(value = "deleteList", method = RequestMethod.POST)
     public ResponseEntity<?> delete(String ids, HttpServletRequest request, String token)
     {
    	 if (StringUtils.isBlank(token)
                 || !token.equals(SecurityUtils.getSubject()
                     .getSession()
                     .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
             {
                 throw new BusinessException(401, "invalid token");
             }

    	 String[] idArray = null;
    	 //当选择全部,传递过来的id不是通过“，”分割，而是等于“all”，所以需要特殊处理
         if("all".equals(ids)){
        	 
        	 UserToken userInfo = getCurrentUser();
             String customerID = String.valueOf(userInfo.getCloudUserId());//userInfo.getId()
        	 
             RestUserFeedBackListRequest condition = new RestUserFeedBackListRequest();
	         condition.setCustomerID(customerID);
	         //查询当前用户全部问题列表
	         Page<RestFeedBackInfo> feedBackInfoList = new UserFeedBackClient(uamClientService).getFeedBackList(getToken(),condition,null);
	         //封装问题ID集合
	         if(null!=feedBackInfoList && feedBackInfoList.getTotalElements()>0){
	        	 
	        	 List<RestFeedBackInfo> feedbackInfos = feedBackInfoList.getContent();
	        	 
		         if(null!=feedbackInfos&&feedbackInfos.size()>0){
		        	 
		        	 idArray = new String[feedbackInfos.size()];
		        	 
		        	 for(int i=0;i<feedbackInfos.size();i++){
		        		 
		        		 idArray[i]=String.valueOf(feedbackInfos.get(i).getProblemID());
		        	 }
		         } 
	         }
         }else{
        	 idArray = ids.split(",");
         }
    	 
         //遍历问题ID集合，删除问题及其问题相关追问和答复
         for (String problemID : idArray)
         {
             if(StringUtils.isEmpty(problemID))
             {
                 throw new InvalidParamException("problemID exception id problemID "+problemID);
             }
           
             new UserFeedBackClient(uamClientService).deleteFeedBack(getToken(),Long.parseLong(problemID));
         }
         
         return new ResponseEntity(HttpStatus.OK);
     }

     
     private List<FeedBackStatus> getStatusTypeList()
     {
         
          List<FeedBackStatus> statusTypeList = new ArrayList<FeedBackStatus>();
          
          FeedBackStatus oneStatus = new FeedBackStatus();
          oneStatus.setValue("1");
          oneStatus.setName("已回答");
          
          FeedBackStatus towStatus = new FeedBackStatus();
          towStatus.setValue("0");
          towStatus.setName("未回答");
          
          statusTypeList.add(oneStatus);
          statusTypeList.add(towStatus);
          
          return statusTypeList;
     }
     
 	/**
 	 * 表单提交 Date类型数据绑定 <功能详细描述>
 	 * 
 	 * @param binder
 	 * @see [类、类#方法、类#成员]
 	 */
 	@InitBinder
 	public void initBinder(WebDataBinder binder) {
 		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 		dateFormat.setLenient(false);
 		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
 	}
   
}

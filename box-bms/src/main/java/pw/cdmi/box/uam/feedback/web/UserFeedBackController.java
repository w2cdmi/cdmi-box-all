package pw.cdmi.box.uam.feedback.web;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.box.uam.exception.InvalidParamterException;
import pw.cdmi.box.uam.feedback.domain.QueryUserFeedBackCondition;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackDetail;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackInfo;
import pw.cdmi.box.uam.feedback.domain.RestFeedBackSubInfo;
import pw.cdmi.box.uam.feedback.manager.UserFeedBackManager;
import pw.cdmi.box.uam.feedback.util.PageRequest;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.log.web.SystemLogController;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.service.MailServerService;
import pw.cdmi.box.uam.user.domain.Admin;
import pw.cdmi.box.uam.util.CSRFTokenManager;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.core.restrpc.RestClient;


@Controller
@RequestMapping(value = "/feedback/uam")
public class UserFeedBackController extends AbstractCommonController
{
     private static Logger logger = LoggerFactory.getLogger(SystemLogController.class);
     
     @Autowired
     UserFeedBackManager userFeedBackManager;
     
     @Autowired
     private MailServerService mailServerService;
     
     @Autowired
     private SystemLogManager systemLogManager;
     
     @Resource
     private RestClient ufmClientService;

     
     /**
      * 
      * 用户反馈主页
      *
      * @param model
      * @return <参数描述>
      *
      */
     @RequestMapping(value = "manage", method = RequestMethod.GET)
     public String enter(Model model)
     {
         return "feedback/feedBackManageMain";
     }
     
     
     /**
      * 
      * 用户反馈未答复页
      *
      * @param model
      * @param request
      * @return <参数描述>
      *
      */
     @RequestMapping(value = "/open", method = RequestMethod.GET)
     public String feedBackOpenList(Model model,HttpServletRequest request)
     {
          
          QueryUserFeedBackCondition condition = new QueryUserFeedBackCondition();
          
          PageRequest pageRequest = new PageRequest();
          
          pageRequest.setSize(Constants.DEFAULT_FEEDBACK_PAGE_SIZE);
          
          condition.setPageRequest(pageRequest);
          condition.setProblemStatus("0");
          
          
          Page<RestFeedBackInfo> userFeedBackList = userFeedBackManager.queryUserFeedBackByPage(condition);
          
          model.addAttribute("userFeedBackList", userFeedBackList);
          model.addAttribute("condition", condition);
          

         return "feedback/feedBackOpenList";
     }
     

     
     
     /**
      * 
      * 用户反馈已答复页面
      *
      * @param model
      * @param request
      * @return <参数描述>
      *
      */
     @RequestMapping(value = "/close", method = RequestMethod.GET)
     public String feedBackCloseList(Model model,HttpServletRequest request)
     {
          QueryUserFeedBackCondition condition = new QueryUserFeedBackCondition();
          
          PageRequest pageRequest = new PageRequest();
          
          pageRequest.setSize(Constants.DEFAULT_FEEDBACK_PAGE_SIZE);
          
          condition.setPageRequest(pageRequest);
          condition.setProblemStatus("1");
          
          Page<RestFeedBackInfo> userFeedBackList = userFeedBackManager.queryUserFeedBackByPage(condition);
          
          model.addAttribute("userFeedBackList", userFeedBackList);
          model.addAttribute("condition", condition);
          
         return "feedback/feedBackCloseList";
     }
     
     
     /**
      * 
      * 点击查询按钮查询用户反馈列表（未答复）
      *
      * @param condition 查询条件
      * @param page 分页
      * @param model 返回信息
      * @param token 鉴权
      * @return 用户反馈信息集合
      *
      */
     @RequestMapping(value = "listopen", method = RequestMethod.POST)
     public String listopen(QueryUserFeedBackCondition condition, Integer page, Model model, String token,HttpServletRequest request)
     {
    	 
         if (StringUtils.isBlank(token)
             || !token.equals(SecurityUtils.getSubject()
                 .getSession()
                 .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
         {
        	 logger.error("invalid token");
             throw new BusinessException(401, "invalid token");
         }
         
         Date twBeginTime = condition.getTwBeginTime();
         Date twEndTime = condition.getTwEndTime();
         Date dfBeginTime = condition.getDfBeginTime();
         Date dfEndTime = condition.getDfEndTime();

         if (null != twBeginTime && null != twEndTime && twBeginTime.after(twEndTime))
         {
        	  logger.error("start time cannot be lagger than end time");
              throw new InvalidParamterException("start time cannot be lagger than end time");
         }

         if (null != dfBeginTime && null != dfEndTime && dfBeginTime.after(dfEndTime))
         {
        	  logger.error("start time cannot be lagger than end time");
              throw new InvalidParamterException("start time cannot be lagger than end time");
         }
         
         PageRequest pageRequest = new PageRequest();
         pageRequest.setSize(Constants.DEFAULT_FEEDBACK_PAGE_SIZE);
         if (page != null)
         {
              pageRequest.setPage(page.intValue());
         }
         condition.setPageRequest(pageRequest);

         Page<RestFeedBackInfo> userFeedBackList = userFeedBackManager.queryUserFeedBackByPage(condition);
         model.addAttribute("userFeedBackList", userFeedBackList);
         model.addAttribute("condition", condition);
         
         return "feedback/feedBackOpenList";
     } 
     
     /**
      * 
      * 点击查询按钮查询用户反馈列表（已经答复）
      *
      * @param condition 查询条件
      * @param page 分页
      * @param model 返回信息
      * @param token 鉴权
      * @return 用户反馈信息集合
      *
      */
     @RequestMapping(value = "listclose", method = RequestMethod.POST)
     public String listclose(QueryUserFeedBackCondition condition, Integer page, Model model, String token,HttpServletRequest request)
     {
    	 
         if (StringUtils.isBlank(token)
             || !token.equals(SecurityUtils.getSubject()
                 .getSession()
                 .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
         {
        	 logger.error("invalid token");
             throw new BusinessException(401, "invalid token");
         }
         
         Date twBeginTime = condition.getTwBeginTime();
         Date twEndTime = condition.getTwEndTime();
         Date dfBeginTime = condition.getDfBeginTime();
         Date dfEndTime = condition.getDfEndTime();

         if (null != twBeginTime && null != twEndTime && twBeginTime.after(twEndTime))
         {
              throw new InvalidParamterException("start time cannot be lagger than end time");
         }

         if (null != dfBeginTime && null != dfEndTime && dfBeginTime.after(dfEndTime))
         {
              throw new InvalidParamterException("start time cannot be lagger than end time");
         }
         
         PageRequest pageRequest = new PageRequest();
         pageRequest.setSize(Constants.DEFAULT_FEEDBACK_PAGE_SIZE);
         if (page != null)
         {
              pageRequest.setPage(page.intValue());
         }
         condition.setPageRequest(pageRequest);

         Page<RestFeedBackInfo> userFeedBackList = userFeedBackManager.queryUserFeedBackByPage(condition);
         model.addAttribute("userFeedBackList", userFeedBackList);
         model.addAttribute("condition", condition);
         
         return "feedback/feedBackCloseList";
     } 
     
     
     /**
      * 删除用户反馈
      * @param problemID 问题ID
      * @param token
      * @param request
      * @return
      */
     @SuppressWarnings("rawtypes")
     @RequestMapping(value = "deleteFeedBack", method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity<?> delete(long problemID, String token,HttpServletRequest request)
     {
         super.checkToken(token);
         
         if(problemID<1){
        	 
             logger.error("Delete user feedback is failed,problemID can not be null!");
             
             return new ResponseEntity(HttpStatus.BAD_REQUEST);
         }
         //删除用户反馈记录
         userFeedBackManager.logicDeleteUserFeedBack(problemID);
         //删除用户反馈相关记录的答复追问等子记录
//         userFeedBackManager.deleteUserFeedBackSub(problemID);
         
         logger.info("Delete user feedback is success!");
         
         return new ResponseEntity(HttpStatus.OK);
     }
     
     
     /**
      * 前往管理员答复页面
      * @param model
      * @return
      */
     @RequestMapping(value = "toAnswer", method = RequestMethod.GET)
     public String toAnswerPage(long problemID,Model model)
     {
         List<RestFeedBackSubInfo> restFeedBackSubList = userFeedBackManager.getFeedBackSubList(problemID);
         RestFeedBackDetail feedBackDetail =  getFeedBackDetailMethod(problemID);

         model.addAttribute("feedBackDetail",feedBackDetail);
         model.addAttribute("restFeedBackSubList", restFeedBackSubList);
         
         return "feedback/feedBackAnswer";
     }
     
    


     /**
      * 管理员答复
      * @param feedBackDetail 前台页面返回用户反馈信息
      * @param request 请求
      * @param token
      * @return
      */
     @SuppressWarnings("rawtypes")
     @RequestMapping(value = "modify", method = RequestMethod.POST)
     @ResponseBody
     public ResponseEntity<?> modify(RestFeedBackDetail feedBackDetail, HttpServletRequest request, String token)
     {
          try{
               super.checkToken(token);
               
               
               String answerDescription = feedBackDetail.getAnswerDescription();
               feedBackDetail = getFeedBackDetailMethod(feedBackDetail.getProblemID());
               feedBackDetail.setAnswerDescription(answerDescription);
               
               //问题状态为关闭
               feedBackDetail.setProblemStatus("1");
               feedBackDetail.setManagerAnswerTime(new Date());    
               userFeedBackManager.updateTeedBackTime(feedBackDetail);
               
               //1表示答复问题
               RestFeedBackSubInfo restFeedBackInfo= new RestFeedBackSubInfo();
               restFeedBackInfo.setIsAnswer("1");
               restFeedBackInfo.setDescription(feedBackDetail.getAnswerDescription());
               restFeedBackInfo.setProblemID(feedBackDetail.getProblemID());
               restFeedBackInfo.setUpdateTime(new Date());
               restFeedBackInfo.setUserID(feedBackDetail.getManagerID());
               restFeedBackInfo.setUserName(feedBackDetail.getManagerName());
               userFeedBackManager.addNewFeedBackSub(restFeedBackInfo);  
        
               //调用发邮件接口
               sendEmail(feedBackDetail);
               
               
               //创建消息通知对象
               Map<String, String> headers = new HashMap<String, String>(1);
               headers.put("Authorization", token);
           
               String uri = "/api/v2/addFeedBackMessage";
               
//               TextResponse response = ufmClientService.performJsonPutTextResponse(uri.toString(),
//                         headers,
//                         feedBackDetail);
               
//               if(response.getStatusCode() == HttpStatus.OK.value()){
//            	   logger.info("Send message success");
//               }else{
//            	   logger.error("Send message failed!");
//               }
               
          }catch(Exception e){
               logger.error("Answer user feedback is failed!"+e);
               
               return new ResponseEntity(HttpStatus.BAD_REQUEST);
          }
          
          return new ResponseEntity(HttpStatus.OK);
     }
     
   
     
    


	/**
      * 前往用户反馈详情页面
      * @param model
      * @return
      */
     @RequestMapping(value = "detail", method = RequestMethod.GET)
     public String showFeedBack(long problemID, String token,Model model)
     {
         
         RestFeedBackInfo restFeedBackInfo = userFeedBackManager.getFeedBackByID(problemID);
        
         List<RestFeedBackSubInfo> restFeedBackSubList = userFeedBackManager.getFeedBackSubList(problemID);
 
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
         super.checkToken(token);
         
         String[] idArray = ids.split(",");

         for (String problemID : idArray)
         {
             if(StringUtils.isEmpty(problemID))
             {
                 throw new InvalidParamterException("problemID exception problemID is "+problemID);
             }
             /*RestFeedBackInfo restFeedBackInfo = userFeedBackManager.getFeedBackByID(Long.parseLong(problemID));
             if (null == restFeedBackInfo)
             {
                 logger.error("Get user feedback failed,user feedback is not exist");
                 return new ResponseEntity<String>("userFeedbackNotExist", HttpStatus.NOT_FOUND);
             }*/
             //删除用户反馈记录
             userFeedBackManager.logicDeleteUserFeedBack(Long.parseLong(problemID));
         }
         
         return new ResponseEntity(HttpStatus.OK);
     }
     
     
     
     /**
      * 
      * 封装RestFeedBackDetail信息
      *
      * @param problemID 问题ID
      * @return feedBackDetail 用户反馈详情
      *
      */
     private RestFeedBackDetail getFeedBackDetailMethod(long problemID)
     {
          
          Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal(); 
          
          RestFeedBackInfo restFeedBackInfo = userFeedBackManager.getFeedBackByID(problemID);
          
          RestFeedBackDetail feedBackDetail = new RestFeedBackDetail();
          
          feedBackDetail.setProblemID(restFeedBackInfo.getProblemID());
          feedBackDetail.setProblemTitle(restFeedBackInfo.getProblemTitle());
          feedBackDetail.setProblemType(restFeedBackInfo.getProblemType());
          feedBackDetail.setProblemStatus(restFeedBackInfo.getProblemStatus());
          feedBackDetail.setProblemDescription(restFeedBackInfo.getProblemDescription());
          feedBackDetail.setNewestTwTime(restFeedBackInfo.getNewestTwTime());
          feedBackDetail.setCustomerTwTime(restFeedBackInfo.getCustomerTwTime());
          feedBackDetail.setCustomerName(restFeedBackInfo.getCustomerName());
          feedBackDetail.setCustomerID(restFeedBackInfo.getCustomerID());
          feedBackDetail.setManagerName(admin.getLoginName());
          feedBackDetail.setManagerID(String.valueOf(admin.getId()));
          feedBackDetail.setCustomerEmail(restFeedBackInfo.getCustomerEmail());
          
          return feedBackDetail;
     }
     
     /**
      * 发邮件
      * @param feedBackDetail 用户反馈信息
      * @throws IOException 异常
      */
     private void sendEmail(RestFeedBackDetail feedBackDetail) throws IOException {
		
    	 /**
    	  * 格式化数据
    	  */
    	 feedBackDetail = formatParam(feedBackDetail);
    	 
    	 MailServer mailServer = mailServerService.getDefaultMailServer();
         if (mailServer == null)
         {
             throw new BusinessException();
         }
         Map<String, Object> messageModel = new HashMap<String, Object>(5);
         messageModel.put("customerName", feedBackDetail.getCustomerName());
         messageModel.put("problemDescription", feedBackDetail.getProblemDescription());
         messageModel.put("answerDescription", feedBackDetail.getAnswerDescription());
         messageModel.put("managerName", feedBackDetail.getManagerName());
         messageModel.put("problemTitle", feedBackDetail.getProblemTitle());
         String msg = mailServerService.getEmailMsgByTemplate(Constants.ANSWER_FEEDBACK_MAIL_CONTENT, messageModel);
         String subject = mailServerService.getEmailMsgByTemplate(Constants.ANSWER_FEEDBACK_MAIL_SUBJECT,new HashMap<String, Object>(1));

//         mailServerService.sendHtmlMail(mailServer.getId(), feedBackDetail.getCustomerEmail(), null, null, subject, msg);
         if(StringUtils.isEmpty(feedBackDetail.getCustomerEmail())){
        	 
        	 logger.error("Email is null,Send mail failed!");
        	 
         }else{
        	 
        	 if(checkEmail(feedBackDetail.getCustomerEmail())){
        		 
        		 mailServerService.sendHtmlMail(mailServer.getId(), feedBackDetail.getCustomerEmail(), null, null, subject, msg);
        	 }else{
        		 
        		 logger.error("Eamil format is illegal,Send mail failed!");
        		 
        	 }
         }
	}

     /**
      * 格式化信息，将尖括号转译，防止注入攻击
      * @param feedBackDetail
      * @return
      */
     private RestFeedBackDetail formatParam(RestFeedBackDetail feedBackDetail) {
		
    	 String problemTitle = feedBackDetail.getProblemTitle();
    	 String problemTitleLeft = problemTitle.replaceAll("<", "&lt;");
    	 String problemTitleRight = problemTitleLeft.replaceAll(">", "&gt;");
    	 
    	 String problemDescription = feedBackDetail.getProblemDescription();
    	 String problemDescriptionLeft = problemDescription.replaceAll("<", "&lt;");
    	 String problemDescriptionRight = problemDescriptionLeft.replaceAll(">", "&gt;");
    	 
    	 String answerDescription = feedBackDetail.getAnswerDescription();
    	 String answerDescriptionLeft = answerDescription.replaceAll("<", "&lt;");
    	 String answerDescriptionRight = answerDescriptionLeft.replaceAll(">", "&gt;");
    	 
    	 
    	 feedBackDetail.setProblemTitle(problemTitleRight);
    	 feedBackDetail.setProblemDescription(problemDescriptionRight);
    	 feedBackDetail.setAnswerDescription(answerDescriptionRight);

		return feedBackDetail;
	}


	/**
      * 验证邮箱
      * @param email
      * @return
      */
     private static boolean checkEmail(String email){
         boolean flag = false;
         try{
                 String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                 Pattern regex = Pattern.compile(check);
                 Matcher matcher = regex.matcher(email);
                 flag = matcher.matches();
             }catch(Exception e){
                 flag = false;
             }
         return flag;
     }

}

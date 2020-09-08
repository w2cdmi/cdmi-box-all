package pw.cdmi.box.disk.client.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.feedback.domain.RestFeedBackCreateRequest;
import pw.cdmi.box.disk.feedback.domain.RestFeedBackDetail;
import pw.cdmi.box.disk.feedback.domain.RestFeedBackInfo;
import pw.cdmi.box.disk.feedback.domain.RestFeedBackSubInfo;
import pw.cdmi.box.disk.feedback.domain.RestUserFeedBackListRequest;
import pw.cdmi.box.disk.feedback.domain.RestUserFeedBackListResponse;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class UserFeedBackClient
{
     private static final Logger logger = LoggerFactory.getLogger(UserFeedBackClient.class);
     

     private RestClient uamClientService;
     
     public UserFeedBackClient(RestClient uamClientService)
     {
         this.uamClientService = uamClientService;
     }


     /**
      * 
      * 获取用户反馈分页
      * @param token 
      *
      * @param condition
     * @param pageRequest 
     * @param pageRequest 
      * @return <参数描述>
      *
      */
     public Page <RestFeedBackInfo> getFeedBackList(String token, RestUserFeedBackListRequest condition, PageRequest pageRequest) throws RestException
     {
          
          Map<String, String> headers = new HashMap<String, String>(1);
          headers.put("Authorization", token);
          
          int total = this.getFeedBackAccount(token,condition);
          
          StringBuffer uri = new StringBuffer(Constants.API_USER_FEED_BACK_LIST);
          
          TextResponse listResponse = uamClientService.performJsonPutTextResponse(uri.toString(),
                    headers,
              condition);
          
          String content = listResponse.getResponseBody();
          
          RestUserFeedBackListResponse list = null;
          
          List<RestFeedBackInfo> restFeedBackInfos = null;
          
          if (listResponse.getStatusCode() == HttpStatus.OK.value())
          {
        	   list = JsonUtils.stringToObject(content, RestUserFeedBackListResponse.class);
        	   
        	   if(null!=list){
        		   
        		   restFeedBackInfos = list.getFeedBackInfoList();
        		   condition.setOffset(list.getOffset());
        		   condition.setLimit(list.getLimit());
        	   }
           
               
               Page<RestFeedBackInfo> page = new PageImpl<RestFeedBackInfo>(restFeedBackInfos, pageRequest, total);
               
               return page;
          }
 
          return new PageImpl<RestFeedBackInfo>(restFeedBackInfos, pageRequest, total);
     }

     
     /**
      * 
      * 统计用回反馈信息条数
      *
      * @param condition
      * @return <参数描述>
      *
      */
     private int getFeedBackAccount(String token,RestUserFeedBackListRequest condition) throws RestException
     {
         
          Map<String, String> headers = new HashMap<String, String>(1);
          headers.put("Authorization", token);
          
          StringBuffer uri = new StringBuffer(Constants.API_USER_FEED_BACK_COUNT);
          
          TextResponse accountResponse = uamClientService.performJsonPutTextResponse(uri.toString(),
                    headers,
                    condition);
          
          String content = accountResponse.getResponseBody();
          
          if(StringUtils.isNotEmpty(content)&&accountResponse.getStatusCode() == HttpStatus.OK.value()){
               
              int total = JsonUtils.stringToObject(content , Integer.class);
              
              return total;
          }
          return 0;
     }




     /**
      * 
      * 调用uam创建用户反馈接口
      * 
      * @param uri
      *             请求地址
      * @param restFeedBackCreateRequest
      *             请求信息
      * @return
      * @throws RestException
      *              rest异常
      * 
      */
     public String createFeedBack(String token,RestFeedBackCreateRequest restFeedBackCreateRequest)
               throws RestException
     {
          Map<String, String> headers = new HashMap<String, String>(1);
          headers.put("Authorization", token);

          logger.info("createFeedBack start,Enter into /api/v2/terminal/add");
          TextResponse response = uamClientService.performJsonPutTextResponse(Constants.API_ADD_FEED_BACK  , headers ,
                    restFeedBackCreateRequest);

          if (response.getStatusCode() == HttpStatus.OK.value())
          {
        	  	logger.info("createFeedBack success");
               return HttpStatus.OK.toString();
          }

          logger.error("createFeedBack failure, response:" + response.getResponseBody());

          return HttpStatus.BAD_REQUEST.toString();
     }


     /**
      * 
      * 删除用户反馈信息
      *
      * @param problemID
      * @return <参数描述>
      *
      */
     public String deleteFeedBack(String token,long problemID)
     {
          Map<String, String> headers = new HashMap<String, String>(1);
          headers.put("Authorization", token);

          String uri = Constants.API_DELETE_FEED_BACK+"?problemID="+problemID;
          
          TextResponse response = uamClientService.performDelete(uri, headers);

          if (response.getStatusCode() == HttpStatus.OK.value())
          {
               return String.valueOf(response.getStatusCode());
          }

          logger.error("createFeedBack failure, response:" + response.getResponseBody());

          return HttpStatus.BAD_REQUEST.toString();
          
     }
     

     public String updateFeedBack(String token,RestFeedBackInfo restFeedBackInfo)
     {
          Map<String, String> headers = new HashMap<String, String>(1);
          headers.put("Authorization", token);

          TextResponse response = uamClientService.performJsonPutTextResponse(Constants.API_UPDATE_FEED_BACK , headers ,
                    restFeedBackInfo);

          if (response.getStatusCode() == HttpStatus.OK.value())
          {
               return String.valueOf(response.getStatusCode());
          }
          return HttpStatus.BAD_REQUEST.toString();

     }


	public RestFeedBackInfo getFeedBackByID(long problemID,String token) {
		 Map<String, String> headers = new HashMap<String, String>(1);
         headers.put("Authorization", token);
         
         TextResponse response = uamClientService.performJsonPutTextResponse(Constants.API_GET_FEED_BACK, headers,problemID);
         
         String content = response.getResponseBody();
         
         RestFeedBackInfo restFeedBackInfo = null;
         
         if(StringUtils.isNotEmpty(content)&&response.getStatusCode() == HttpStatus.OK.value()){
              
        	 restFeedBackInfo = JsonUtils.stringToObject(content, RestFeedBackInfo.class);
             
             return restFeedBackInfo;
         }
         
		return restFeedBackInfo;
	}


	@SuppressWarnings("unchecked")
	public List<RestFeedBackSubInfo> getFeedBackSubList(long problemID,String token) {
		 
		 Map<String, String> headers = new HashMap<String, String>(1);
         headers.put("Authorization", token);
         
         
         TextResponse response = uamClientService.performJsonPutTextResponse(Constants.API_GET_FEED_BACK_SUB, headers,problemID);
         
         List<RestFeedBackSubInfo> feedBackSubList = new ArrayList<RestFeedBackSubInfo>();
         
         String content = response.getResponseBody();
         
         
         if(StringUtils.isNotEmpty(content)&&response.getStatusCode() == HttpStatus.OK.value()){
             
        	 feedBackSubList = (List<RestFeedBackSubInfo>) JsonUtils.stringToList(content, ArrayList.class, RestFeedBackSubInfo.class);
             
             return feedBackSubList;
         }
         

		return feedBackSubList;
	}


	public String updateTeedBackTime(RestFeedBackDetail feedBackDetail, String token) {
		 Map<String, String> headers = new HashMap<String, String>(1);
         headers.put("Authorization", token);
         

         TextResponse response = uamClientService.performJsonPutTextResponse(Constants.API_UPDATE_FEED_BACK_TIME, headers,feedBackDetail);

         if(response.getStatusCode() == HttpStatus.OK.value()){

             return HttpStatus.BAD_REQUEST.toString();
         }else{
        	 return HttpStatus.OK.toString();
         }
         
         
	}


	public String addNewFeedBackSub(RestFeedBackSubInfo restFeedBackInfo, String token) {
		 Map<String, String> headers = new HashMap<String, String>(1);
         headers.put("Authorization", token);
         
         TextResponse response = uamClientService.performJsonPutTextResponse(Constants.API_ADD_FEED_BACK_SUB, headers,restFeedBackInfo);
		
         if(response.getStatusCode() == HttpStatus.OK.value()){

             return HttpStatus.BAD_REQUEST.toString();
         }else{
        	 return HttpStatus.OK.toString();
         }
	}

}
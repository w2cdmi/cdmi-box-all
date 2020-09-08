package pw.cdmi.box.disk.converttask.web;



import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.box.disk.converttask.client.api.ConvertClient;
import pw.cdmi.box.disk.converttask.client.domain.ConvertInfo;
import pw.cdmi.box.disk.converttask.client.domain.DeleteConvertRequest;
import pw.cdmi.box.disk.converttask.client.domain.RetryConvertRequest;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/convertTask")
public class ConvertController extends CommonController
{
   
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertController.class);
	
    private ConvertClient convertClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @PostConstruct
    public void init()
    {
    	convertClient = new ConvertClient(ufmClientService);
    }
    
    /**
     * Query Doing Task
     * 
     * @param ownerId
     * @param spaceType
     * @return
     */
    @RequestMapping(value = "queryDoing", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ConvertInfo> queryDoing(long ownerId,Integer spaceType,Integer curpage,Integer size,
        HttpServletRequest httpServletRequest)
    {
    	LOGGER.info("queryDoingConvertTask==>ownerId:"+ownerId+"  spaceType:"+spaceType);
        super.checkToken(httpServletRequest);
        ConvertInfo result = convertClient.queryDoingConvert(getToken(),  ownerId,spaceType,curpage,size);
        return new ResponseEntity<ConvertInfo>(result, HttpStatus.OK);
    }
    
    /**
     * Query Done Task
     * 
     * @param ownerId
     * @param spaceType
     * @return
     */
    @RequestMapping(value = "queryDone", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ConvertInfo> queryDone(long ownerId,Integer spaceType,Integer curpage,Integer size,
        HttpServletRequest httpServletRequest)
    {
    	LOGGER.info("queryDoneConvertTask==>ownerId:"+ownerId+"  spaceType:"+spaceType);
        super.checkToken(httpServletRequest);
        ConvertInfo result = convertClient.queryDoneConvert(getToken(), ownerId,spaceType,curpage,size);
        return new ResponseEntity<ConvertInfo>(result, HttpStatus.OK);
    }
    
    /**
     * delete Done Task
     * 
     * @param taskids
     * @param ownerId
     * @param spaceType
     * @return
     */
    @RequestMapping(value = "deleteDone", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> deleteDone(String [] taskids,long ownerId,Integer spaceType,
        HttpServletRequest httpServletRequest)
    {
    	LOGGER.info("deleteDoneConvertTask==>ownerId:"+ownerId+"  spaceType:"+spaceType);
        super.checkToken(httpServletRequest);
        DeleteConvertRequest request=new DeleteConvertRequest(taskids,ownerId);
        String result = convertClient.deleteDoneConvert(getToken(),request,spaceType);
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
    /**
     * retry Doing Task
     * 
     * @param taskId
     * @param ownerId
     * @param spaceType
     * @return
     */
    @RequestMapping(value = "retryDoing", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> retryDoing(String taskId,long ownerId,Integer spaceType,
    		HttpServletRequest httpServletRequest)
    {
    	LOGGER.info("retryDoingConvertTask==>ownerId:"+ownerId+"  spaceType:"+spaceType+"  taskId:"+taskId);
    	super.checkToken(httpServletRequest);
    	RetryConvertRequest request=new RetryConvertRequest(ownerId);
    	String result = convertClient.retryDingConvert(getToken(), request, spaceType, taskId);
    	return new ResponseEntity<String>(result, HttpStatus.OK);
    }
    
}

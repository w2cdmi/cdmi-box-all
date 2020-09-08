package pw.cdmi.box.isystem.convertTask.web;

import java.security.InvalidParameterException;

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

import com.huawei.sharedrive.isystem.common.web.AbstractCommonController;
import com.huawei.sharedrive.isystem.exception.BusinessException;
import com.huawei.sharedrive.isystem.util.CSRFTokenManager;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.isystem.convertTask.domain.QueryCondition;
import pw.cdmi.box.isystem.convertTask.domain.TaskBean;
import pw.cdmi.box.isystem.convertTask.service.ConvertTaskService;

@Controller
@RequestMapping(value = "/convertTask")
public class ConvertTaskController extends AbstractCommonController
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertTaskController.class);
	
	private static final int DEFAULT_PAGE_SIZE = 10;
	
	@Autowired
	private ConvertTaskService convertTaskService;
	
	@RequestMapping(value = "enterList", method = {RequestMethod.GET})
    public String enterList(Model model)
    {
		QueryCondition condition = new QueryCondition();
	    PageRequest pageRequest = new PageRequest();
	    pageRequest.setSize(DEFAULT_PAGE_SIZE);
	    condition.setPageRequest(pageRequest);
	    Page<TaskBean> taskList = convertTaskService.getTaskBeanList(condition);
	    model.addAttribute("taskList", taskList);
	    model.addAttribute("queryCondition", condition);
        return "convertTask/convertTaskList";
    }
	
	@RequestMapping(value = "list", method = RequestMethod.POST)
    public String list(QueryCondition condition, Integer page, Model model, String token)
    {
        if (StringUtils.isBlank(token)
            || !token.equals(SecurityUtils.getSubject()
                .getSession()
                .getAttribute(CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME)))
        {
            throw new BusinessException(401, "invalid token");
        }
        
        if (condition.getStartTime() != null && condition.getEndTime() != null
            && condition.getStartTime().after(condition.getEndTime()))
        {
            throw new InvalidParameterException("Start EndTime Exception");
        }
        PageRequest request = new PageRequest();
        request.setSize(DEFAULT_PAGE_SIZE);
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        condition.setPageRequest(request);
        condition.setFileName(condition.getFileName());
        Page<TaskBean> taskList = convertTaskService.getTaskBeanList(condition);
        model.addAttribute("taskList", taskList);
        model.addAttribute("queryCondition", condition);
        return "convertTask/convertTaskList";
    }
	
	@SuppressWarnings("rawtypes")
    @RequestMapping(value = "save", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseEntity<?> save(String taskId, int level, HttpServletRequest request)
    {
		int result = convertTaskService.save(taskId, level);
		if(result == 0)
		{
			return new ResponseEntity(HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	@SuppressWarnings("rawtypes")
    @RequestMapping(value = "resetState", method = {RequestMethod.POST})
    @ResponseBody
	public ResponseEntity<?> resetState(String taskId,HttpServletRequest request)
	{
		try
		{
			convertTaskService.resetState(taskId);
			return new ResponseEntity(HttpStatus.OK);
		} catch (Exception e)
		{
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}

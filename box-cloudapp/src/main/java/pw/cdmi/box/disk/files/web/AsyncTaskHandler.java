package pw.cdmi.box.disk.files.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.disk.client.api.AsyncTaskClient;
import pw.cdmi.box.disk.client.domain.task.ResponseGetTask;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.utils.SpringContextUtil;

//@WebServlet(urlPatterns = "/nodes/listen", asyncSupported = true)
@Controller
@RequestMapping(value = "/nodes/listen")
public class AsyncTaskHandler
{
    
    public static final String FORBID_EXCEPTION = "forbidden";
    
    public static final long serialVersionUID = -4134343691007808561L;
    
    public static final String SRC_NOT_FOUND = "src_not_found";
    
    public static final String STATUS_AYSNC_NODES_CONFLICT = "AsyncNodesConflict";
    
    public static final String STATUS_DOING = "Doing";
    
    public static final String STATUS_FORBIDDEN = "Forbidden";
    
    public static final String STATUS_NO_SOURCE = "NoSuchSource";
    
    public static final String STATUS_NOT_FOUND = "NotFound";
    
    public static final String STATUS_REPEATNAME_CONFLICT = "RepeatNameConflict";
    
    public static final String STATUS_SUBFOLDER_CONFLICT = "SubFolderConflict";
    
    public static final String STATUS_SYSTEM_ERROR = "SystemException";
    
    public static final String SYS_EXCEPTION = "sys_exception";
    
    private static AsyncTaskClient asyncTaskClient = new AsyncTaskClient(
        (RestClient) SpringContextUtil.getBean("ufmClientService"));
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTaskHandler.class);
    
    private static UserTokenManager userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    
    @RequestMapping(value="",method = RequestMethod.GET)
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        String taskId = request.getParameter("taskId");
        try
        {
            ResponseGetTask result = asyncTaskClient.getTaskStatus(userTokenManager.getToken(), taskId);
            if (result.getStatus().equals(STATUS_AYSNC_NODES_CONFLICT))
            {
                String[] conflicId = new String[result.getConflictNodes().size()];
                int len = result.getConflictNodes().size();
                for (int i = 0; i < len; i++)
                {
                    conflicId[i] = String.valueOf(result.getConflictNodes().get(i).getId());
                }
                String conflidStr = StringUtils.join(conflicId, ",");
                response.getWriter().write(conflidStr);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            else
            {
                response.getWriter().write(result.getStatus());
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
    }
}

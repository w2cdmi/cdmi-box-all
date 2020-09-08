package pw.cdmi.box.disk.teamspace.web;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.TeamSpaceClient;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.teamspace.domain.RestTeamMemberInfo;
import pw.cdmi.box.disk.teamspace.domain.RestTeamSpaceInfo;
import pw.cdmi.box.disk.teamspace.service.TeamSpaceService;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/teamspace/trash")
public class TeamSpaceTrashController extends CommonController
{
    private TeamSpaceClient teamSpaceHttpClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Resource
    private TeamSpaceService teamSpaceService;
    
    /**
     * 
     * @param teamId
     * @param model
     * @return
     */
    @RequestMapping(value = "changeParent/{teamId}", method = RequestMethod.GET)
    public String changeParent(@PathVariable Long teamId, Model model, HttpServletRequest request)
    {
        model.addAttribute("ownerId", teamId);
        return "teamspace/spaceChangeParent";
    }
    
    /**
     * 
     * @param teamId
     * @param teamRole
     * @param model
     * @return
     */
    @RequestMapping(value = "/{teamId}", method = RequestMethod.GET)
    public String enter(@PathVariable Long teamId, Model model)
    {
        UserToken user = getCurrentUser();
        RestTeamSpaceInfo teamSpace = teamSpaceHttpClient.getTeamSpace(teamId, getToken());
        if (teamSpace == null)
        {
            model.addAttribute("exceptionName", "NoSuchTeamSpace");
            return "teamspace/teamSpace404";
        }
        RestTeamMemberInfo memberInfo = teamSpaceService.getMemberByLoginName(teamId,
            user.getLoginName(),
            null,
            user.getLoginName());
        
        if (memberInfo == null)
        {
            model.addAttribute("exceptionName", "Forbidden");
            return "teamspace/teamSpace404";
        }
        
        model.addAttribute("teamId", teamId);
        model.addAttribute("teamName", HtmlUtils.htmlEscape(teamSpace.getName()));
        
        return "teamspace/spaceTrashIndex";
    }
    
    @PostConstruct
    public void init()
    {
        teamSpaceHttpClient = new TeamSpaceClient(ufmClientService);
    }
}

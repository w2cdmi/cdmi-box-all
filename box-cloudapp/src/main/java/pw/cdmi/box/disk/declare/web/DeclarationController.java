package pw.cdmi.box.disk.declare.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

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
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.common.domain.ConcealDeclare;
import pw.cdmi.common.domain.Terminal;
import pw.cdmi.common.domain.UserSignDeclare;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.LoginAuthFailedException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

@Controller
@RequestMapping(value = "/syscommon")
public class DeclarationController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationController.class);
    
    @Resource
    private RestClient uamClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @RequestMapping(value = "declaration", method = RequestMethod.GET)
    public String getdeclaration(Model model) throws BaseRunException
    {
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", getToken());
        TextResponse restResponse = uamClientService.performGetText("/api/v2/declaration/"
            + Terminal.CLIENT_TYPE_WEB_STR, headerMap);
        if (null == restResponse || restResponse.getStatusCode() != 200)
        {
            LOGGER.error("get user interface return code is not 200");
            throw new LoginAuthFailedException();
        }
        String responseBody = restResponse.getResponseBody();
        if (StringUtils.isBlank(responseBody))
        {
            LOGGER.error("get user return code is 200 but body is null");
            throw new LoginAuthFailedException();
        }
        ConcealDeclare concealDeclare = JsonUtils.stringToObject(responseBody, ConcealDeclare.class);
        concealDeclare.setDeclaration(HtmlUtils.htmlEscape(concealDeclare.getDeclaration()));
        model.addAttribute("concealDeclare", concealDeclare);
        return "common/declaration";
    }
    
    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UserSignDeclare> sign(ConcealDeclare concealDeclare) throws BaseRunException
    {
        ConcealDeclare request = new ConcealDeclare();
        request.setId(concealDeclare.getId());
        TextResponse restResponse = uamClientService.performJsonPutTextResponse("/api/v2/declaration/sign",
            getHeaderMap(),
            request);
        if (null == restResponse || restResponse.getStatusCode() != 200)
        {
            LOGGER.error("get user interface return code is not 200");
            throw new LoginAuthFailedException();
        }
        String responseBody = restResponse.getResponseBody();
        if (StringUtils.isBlank(responseBody))
        {
            LOGGER.error("get user return code is 200 but body is null");
            throw new LoginAuthFailedException();
        }
        UserSignDeclare userDeclare = JsonUtils.stringToObject(responseBody, UserSignDeclare.class);
        return new ResponseEntity<UserSignDeclare>(userDeclare, HttpStatus.OK);
    }
    
    protected String getToken()
    {
        return userTokenManager.getToken();
    }
    
    private Map<String, String> getHeaderMap()
    {
        UserToken sessUser = (UserToken) SecurityUtils.getSubject().getPrincipal();
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put("Authorization", sessUser.getToken());
        return headerMap;
    }
}

package pw.cdmi.box.disk.user.web;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import pw.cdmi.box.disk.client.api.UserConfigClient;
import pw.cdmi.box.disk.client.domain.user.RestUserConfig;
import pw.cdmi.box.disk.client.domain.user.RestUserConfigList;
import pw.cdmi.box.disk.files.web.CommonController;
import pw.cdmi.core.restrpc.RestClient;

@Controller
@RequestMapping(value = "/user/config")
public class UserConfigController extends CommonController
{
    @Resource
    private RestClient ufmClientService;
    
    private UserConfigClient userConfigClient;
    
    @PostConstruct
    public void init()
    {
        userConfigClient = new UserConfigClient(ufmClientService);
    }
    
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<RestUserConfig> getConfig(@PathVariable Long userId, @RequestParam String name)
    {
        if (StringUtils.isBlank(name))
        {
            return new ResponseEntity<RestUserConfig>(HttpStatus.BAD_REQUEST);
        }
        RestUserConfig config = null;
        RestUserConfigList configList = userConfigClient.getConfig(userId, name, getToken());
        if (CollectionUtils.isNotEmpty(configList.getConfigs()))
        {
            config = configList.getConfigs().get(0);
            if (null != config)
            {
                config.setName(HtmlUtils.htmlEscape(config.getName()));
                config.setValue(HtmlUtils.htmlEscape(config.getValue()));
            }
        }
        return new ResponseEntity<RestUserConfig>(config, HttpStatus.OK);
    }
    
}

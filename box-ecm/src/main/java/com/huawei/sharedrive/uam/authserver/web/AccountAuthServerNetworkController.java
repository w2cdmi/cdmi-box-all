package com.huawei.sharedrive.uam.authserver.web;

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

import com.huawei.sharedrive.uam.authserver.domain.AccountAuthserverNetwork;
import com.huawei.sharedrive.uam.authserver.manager.AccountAuthserverNetworkManager;
import com.huawei.sharedrive.uam.authserver.manager.AuthServerManager;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.common.domain.AuthServer;

@Controller
@RequestMapping(value = "/enterprise/admin/accountauthservernetwork")
public class AccountAuthServerNetworkController extends AbstractCommonController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAuthServerNetworkController.class);
    
    @Autowired
    private AccountAuthserverNetworkManager accountAuthserverNetworkManager;
    
    @Autowired
    private AuthServerManager authServerManager;
    
    @RequestMapping(value = "enterList/{accountId}/{authServerId}", method = RequestMethod.GET)
    public String enterList(@PathVariable(value = "accountId") Long accountId,
        @PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        AuthServer authServer = authServerManager.getAuthServer(authServerId);
        model.addAttribute("accountId", accountId);
        model.addAttribute("authServerId", authServerId);
        model.addAttribute("authServer", authServer);
        return "/app/authServer/accountAuthServerNetwork";
    }
    
    /**
     * list authServer by enterprise id
     * 
     * @param enterpriseId
     * @param model
     * @return
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseEntity<Page<AccountAuthserverNetwork>> list(Integer size, Integer page, Long accountId,
        Long authServerId, String token)
    {
        super.checkToken(token);
        
        PageRequest request = new PageRequest();
        if (size <= 0)
        {
            size = 1;
        }
        if (page != null && page <= 0)
        {
            page = 1;
        }
        request.setSize(size);
        if (page != null)
        {
            request.setPage(page.intValue());
        }
        Page<AccountAuthserverNetwork> pageNetwork = accountAuthserverNetworkManager.listNetwork(authServerId,
            accountId,
            request);
        return new ResponseEntity<Page<AccountAuthserverNetwork>>(pageNetwork, HttpStatus.OK);
    }
    
    @RequestMapping(value = "enterCreate/{accountId}/{authServerId}", method = RequestMethod.GET)
    public String enterCreate(@PathVariable(value = "accountId") Long accountId,
        @PathVariable(value = "authServerId") Long authServerId, Model model)
    {
        model.addAttribute("accountId", accountId);
        model.addAttribute("authServerId", authServerId);
        return "app/authServer/createAccountAuthServerNetwork";
    }
    
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> create(AccountAuthserverNetwork accountAuthServerNetwork, String token)
    {
        super.checkToken(token);
        if (null == accountAuthServerNetwork)
        {
            LOGGER.error("accountAuthServerNetwork is null");
            throw new InvalidParamterException("accountAuthServerNetwork is null");
        }
        accountAuthserverNetworkManager.create(accountAuthServerNetwork.getAccountId(),
            accountAuthServerNetwork.getAuthServerId(),
            accountAuthServerNetwork.getIpStart(),
            accountAuthServerNetwork.getIpEnd());
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "deleteByIds", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> delete(String ids, Long accountId, Long authServerId, String token)
    {
        super.checkToken(token);
        if ("all".equalsIgnoreCase(ids))
        {
            accountAuthserverNetworkManager.deleteAll(authServerId, accountId);
        }
        else
        {
            accountAuthserverNetworkManager.deleteByIds(ids);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> update(AccountAuthserverNetwork accountAuthServerNetwork, String token)
    {
        super.checkToken(token);
        if (null == accountAuthServerNetwork)
        {
            LOGGER.error("accountAuthServerNetwork is null");
            throw new InvalidParamterException("accountAuthServerNetwork is null");
        }
        accountAuthserverNetworkManager.update(accountAuthServerNetwork.getId(),
            accountAuthServerNetwork.getIpStart(),
            accountAuthServerNetwork.getIpEnd());
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}

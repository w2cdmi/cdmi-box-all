package com.huawei.sharedrive.uam.statistics.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huawei.sharedrive.uam.authapp.service.AuthAppService;
import com.huawei.sharedrive.uam.common.web.AbstractCommonController;
import com.huawei.sharedrive.uam.core.RankRequest;
import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.statistics.manager.AppStatisticsManager;
import com.huawei.sharedrive.uam.user.domain.User;

import pw.cdmi.box.domain.Order;
import pw.cdmi.box.domain.Pager;

@Controller
@RequestMapping(value = "/enterprise/admin/statistics")
public class AppStatisticsController extends AbstractCommonController
{
    
    private static final String DEFAULT_DIRECTION = "ASC";
    
    private static final String DEFAULT_FIELD = "ID";
    
    private static final int DEFAULT_LIMIT = 10;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private AppStatisticsManager appStatisticsManager;
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @RequestMapping(value = "list/{appId}", method = RequestMethod.GET)
    public String list(@PathVariable(value = "appId") String appId, Model model)
    {
        enterpriseAccountManager.bindAppCheck(appId);
        model.addAttribute("appId", appId);
        model.addAttribute("appType", authAppService.getByAuthAppID(appId).getType());
        return "statistics/appStatisticsList";
    }
    
    @RequestMapping(value = "list/{appId}", method = RequestMethod.POST)
    public ResponseEntity<?> listAppStatistics(@PathVariable(value = "appId") String appId, String token)
    {
        super.checkToken(token);
        List<User> users = appStatisticsManager.listStatisticsByAppId(appId);
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
        
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @RequestMapping(value = "rank/{appId}", method = RequestMethod.POST)
    public ResponseEntity<Pager<User>> rankUser(String field, String direction,
        @PathVariable(value = "appId") String appId, int page, int limit, String token)
    {
        super.checkToken(token);
        User user = new User();
        user.setAppId(appId);
        
        Pager<User> userPage = new Pager<User>();
        RankRequest request = new RankRequest();
        request.setLimit(DEFAULT_LIMIT);
        List<Order> orders = new ArrayList<Order>(3);
        Order order = new Order();
        order.setDirection(DEFAULT_DIRECTION);
        order.setField(DEFAULT_FIELD);
        orders.add(order);
        if (null != field)
        {
            order.setField(field);
        }
        if (null != direction)
        {
            order.setDirection(direction);
        }
        if (limit != 0)
        {
            request.setLimit(limit);
        }
        
        request.setOrder(orders);
        request.setOffset((long) (page - 1) * request.getLimit());
        
        userPage = appStatisticsManager.getRankedUser(userPage, user, request);
        userPage.setPage(page);
        userPage.setRestRegionInfo(appStatisticsManager.getRegionInfo(appId));
        return new ResponseEntity<Pager<User>>(userPage, HttpStatus.OK);
    }
    
}

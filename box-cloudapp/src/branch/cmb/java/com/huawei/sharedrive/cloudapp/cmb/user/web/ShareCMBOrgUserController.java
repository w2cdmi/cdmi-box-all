package com.huawei.sharedrive.cloudapp.cmb.user.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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

import com.huawei.sharedrive.cloudapp.cmb.user.domain.OrgTreeNode;
import com.huawei.sharedrive.cloudapp.cmb.user.domain.RetrieveUser;
import com.huawei.sharedrive.cloudapp.cmb.user.manager.CMBOrgUserManager;
import com.huawei.sharedrive.cloudapp.core.domain.Page;
import com.huawei.sharedrive.cloudapp.core.domain.PageImpl;
import com.huawei.sharedrive.cloudapp.files.web.CommonController;
import com.huawei.sharedrive.cloudapp.group.domain.GroupConstants;
import com.huawei.sharedrive.cloudapp.group.domain.RestGroup;
import com.huawei.sharedrive.cloudapp.httpclient.rest.response.MultiUserSearchResponse;
import com.huawei.sharedrive.cloudapp.share.domain.RestMultiUser;
import com.huawei.sharedrive.cloudapp.share.service.ShareService;
import com.huawei.sharedrive.cloudapp.utils.BusinessConstants;
import com.huawei.sharedrive.cloudapp.utils.ShareLinkExceptionUtil;
import com.huawei.sharedrive.cloudapp.utils.StringTokenizerUtils;

@Controller
@RequestMapping(value = "/cmb")
public class ShareCMBOrgUserController extends CommonController
{
    public static final int MAX_USERNAME_SEARCH_SIZE = 2000;
    
    private static Logger logger = LoggerFactory.getLogger(ShareCMBOrgUserController.class);
    
    @Autowired
    private CMBOrgUserManager cMBOrgUserManager;
    
    @Autowired
    private ShareService shareService;
    
    @RequestMapping(value = "listOrgTreeNode", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<List<OrgTreeNode>> listTreeNode(String groupId)
    {
        List<OrgTreeNode> list = cMBOrgUserManager.listOrgTree(groupId);
        if (null != list && list.size() > 0)
        {
            for (OrgTreeNode orgTreeNode : list)
            {
                orgTreeNode.setName(HtmlUtils.htmlEscape(orgTreeNode.getName()));
                orgTreeNode.setIsParent("true");
            }
        }
        return new ResponseEntity<List<OrgTreeNode>>(list, HttpStatus.OK);
    }
    
    @RequestMapping(value = "listRetrieveUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<RetrieveUser>> listRetrieveUser(Integer orgId, String keyword)
    {
        List<RetrieveUser> list = cMBOrgUserManager.listCmbUser(orgId, keyword);
        int total = 0;
        if (null != list && list.size() > 0)
        {
            for (RetrieveUser retrieveUser : list)
            {
                retrieveUser.setDescription(HtmlUtils.htmlEscape(retrieveUser.getDescription()));
                retrieveUser.setEmail(HtmlUtils.htmlEscape(retrieveUser.getEmail()));
                retrieveUser.setLoginName(HtmlUtils.htmlEscape(retrieveUser.getLoginName()));
                retrieveUser.setName(HtmlUtils.htmlEscape(retrieveUser.getName() + "/"
                    + retrieveUser.getLoginName()));
            }
            total = list.size();
        }
        Page<RetrieveUser> page = new PageImpl<RetrieveUser>(list, null, total);
        return new ResponseEntity<Page<RetrieveUser>>(page, HttpStatus.OK);
    }
    
    @RequestMapping(value = "listMultiUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> listMultiUser(Model model, String userNames)
    {
        MultiUserSearchResponse controlUserResponse = new MultiUserSearchResponse();
        try
        {
            if (!checkUserNamesValid(userNames))
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            
            HashSet<String> nikeNames = StringTokenizerUtils.wsString(userNames);
            
            controlUserResponse.setSingle(nikeNames.size() == 1);
            List<RestMultiUser> restMultiUsers = new ArrayList<RestMultiUser>(
                BusinessConstants.INITIAL_CAPACITIES);
            if (nikeNames.size() == 1)
            {
                setListForOneNikeName(controlUserResponse, nikeNames, restMultiUsers);
            }
            else
            {
                setListForMultiNikeNames(controlUserResponse, nikeNames, restMultiUsers);
            }
            
            return new ResponseEntity<MultiUserSearchResponse>(controlUserResponse, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<String>(ShareLinkExceptionUtil.getClassName(e), HttpStatus.BAD_REQUEST);
        }
    }
    
    private void setListForOneNikeName(MultiUserSearchResponse controlUserResponse,
        HashSet<String> nikeNames, List<RestMultiUser> restMultiUsers)
    {
        List<RestGroup> groups = shareService.getListGroupByNickName(nikeNames.iterator().next());
        for (RestGroup group : groups)
        {
            RestMultiUser multiUser = RestMultiUser.buildObject(null,
                group,
                GroupConstants.GROUP_USERTYPE_GROUP);
            restMultiUsers.add(multiUser);
        }
        List<RetrieveUser> res = cMBOrgUserManager.listCmbUser(null, nikeNames.iterator().next());
        for (RetrieveUser user : res)
        {
            RestMultiUser multiUser = RetrieveUser.buildObject(user,
                null,
                GroupConstants.GROUP_USERTYPE_USER);
            restMultiUsers.add(multiUser);
        }
        
        controlUserResponse.setSuccessList(restMultiUsers);
    }
    private void setListForMultiNikeNames(MultiUserSearchResponse controlUserResponse,
        HashSet<String> nikeNames, List<RestMultiUser> restMultiUsers)
    {
        List<RestMultiUser> multiUsers = new ArrayList<RestMultiUser>(
            BusinessConstants.INITIAL_CAPACITIES);
        for (String u : nikeNames)
        {
            List<RetrieveUser> res = cMBOrgUserManager.listCmbUser(null, u);
            List<RestGroup> groups = shareService.getListGroupByNickName(u);
            if (CollectionUtils.isNotEmpty(res) && res.size() == 1
                || (CollectionUtils.isNotEmpty(groups) && groups.size() == 1))
            {
                if (groups.size() == 1 && res.size() == 0)
                {
                    RestGroup restGroup = groups.get(0);
                    RestMultiUser multiUser = RestMultiUser.buildObject(null,
                        restGroup,
                        GroupConstants.GROUP_USERTYPE_GROUP);
                    restMultiUsers.add(multiUser);
                    continue;
                }
                
                if (res.size() == 1 && groups.size() == 0)
                {
                    RetrieveUser user = res.get(0);
                    RestMultiUser multiUser = RetrieveUser.buildObject(user,
                        null,
                        GroupConstants.GROUP_USERTYPE_USER);
                    multiUsers.add(multiUser);
                    continue;
                }
            }
            controlUserResponse.getFailList().add(u);
        }
        for (RestMultiUser multiUser : multiUsers)
        {
            restMultiUsers.add(multiUser);
        }
        controlUserResponse.setSuccessList(restMultiUsers);
    }
    private boolean checkUserNamesValid(String userNames)
    {
        if (StringUtils.isBlank(userNames))
        {
            logger.error("userName is blank!");
            return false;
        }
        
        if (userNames.length() > MAX_USERNAME_SEARCH_SIZE)
        {
            logger.error("search userName exceed max size(1000):size=" + userNames.length());
            return false;
        }
        return true;
    }
}

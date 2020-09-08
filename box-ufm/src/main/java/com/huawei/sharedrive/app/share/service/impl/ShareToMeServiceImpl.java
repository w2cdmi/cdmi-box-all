package com.huawei.sharedrive.app.share.service.impl;

import com.huawei.sharedrive.app.acl.domain.INodeACL;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.impl.FileBaseServiceImpl;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.share.RestSharePageRequestV2;
import com.huawei.sharedrive.app.openapi.domain.share.SharePageV2;
import com.huawei.sharedrive.app.share.dao.INodeShareDeleteDao;
import com.huawei.sharedrive.app.share.dao.ShareDAO;
import com.huawei.sharedrive.app.share.dao.ShareToMeDAO;
import com.huawei.sharedrive.app.share.domain.INodeShare;
import com.huawei.sharedrive.app.share.domain.INodeShareDelete;
import com.huawei.sharedrive.app.share.domain.UserType;
import com.huawei.sharedrive.app.share.service.OrderComparatorUtil;
import com.huawei.sharedrive.app.share.service.ShareToMeService;
import com.huawei.sharedrive.app.user.domain.Department;
import com.huawei.sharedrive.app.user.domain.GroupInfo;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.DepartmentService;
import com.huawei.sharedrive.app.user.service.GroupMemberService;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ShareToMe服务实现类
 * 
 * @author l90003768
 * 
 */
@Service("shareToMeService")
public class ShareToMeServiceImpl implements ShareToMeService
{
    private static final String ORDER_TYPE = "type";
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private ShareToMeDAO shareToMeDAO;
    
    @Autowired
    private ShareDAO shareDAO;
    
    @Autowired
    private GroupMemberService groupMemberService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private INodeShareDeleteDao iNodeShareDeleteDao;

    /**
     * 列举共享给我的资源
     * 
     * @throws BaseRunException
     */
    @Override
    public SharePageV2 listShareToMeForClientV2(UserToken user, long sharedUserId,
        RestSharePageRequestV2 pageRequest) throws BaseRunException
	{
		int total = shareToMeDAO.getShareToMeTotals(sharedUserId, UserType.TYPE_USER, pageRequest.getKeyword());

		if (user.getAccountVistor() != null) {
			List<Long> deptIds = departmentService.getDeptCloudUserIdByCloudUserId(user.getAccountVistor().getEnterpriseId(), user.getId(), user.getAccountId());
			for (long deptId : deptIds) {
				total += shareToMeDAO.getListWithDeleteTotal(user.getId(), deptId, UserType.TYPE_DEP, pageRequest.getKeyword());
			}
		}
		List<INodeShare> totalList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		// 分页
		Integer limit = pageRequest.getLimit().getLength();
		Long offset = pageRequest.getLimit().getOffset();
		Long end = (offset + limit) <= total ? (offset + limit) : total;
		Limit newLimit = new Limit(0L, end.intValue());
		
		List<INodeShare> dbList = shareToMeDAO.getListV2(sharedUserId, UserType.TYPE_USER, FilesCommonUtils.transferStringForSql(pageRequest.getKeyword()), null, newLimit);
		totalList.addAll(dbList);
		dbList.clear();
		
		
		if (user.getAccountVistor() != null) {
			List<Long> deptIds = departmentService.getDeptCloudUserIdByCloudUserId(user.getAccountVistor().getEnterpriseId(), user.getId(), user.getAccountId());
			for (long deptId : deptIds) {
				List<INodeShare> deptlist = shareToMeDAO.getListWithDelete(user.getId(), deptId, UserType.TYPE_DEP, FilesCommonUtils.transferStringForSql(pageRequest.getKeyword()), null, newLimit);
				totalList.addAll(deptlist);
			}
		}
		
		
	
		
		List<INodeShare> folderList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		List<INodeShare> fileList = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
		int oldtotalList = totalList.size();
		// 排序
		List<Order> orders = pageRequest.getOrderList();
		if (orders == null) {
			orders = new ArrayList<Order>(1);
			orders.add(new Order("modifiedAt", "DESC"));
			orders.add(new Order("type", "ASC"));
		}
		totalList = order(totalList, orders, folderList, fileList);
		total = total - (oldtotalList - totalList.size());
		folderList.clear();
		fileList.clear();
		List<INodeShare> currentTotalList = new ArrayList<INodeShare>(totalList.size());
		for (int i = 0; i < currentTotalList.size() - 1; i++) {
			for (int j = currentTotalList.size() - 1; j > i; j--) {
				if (currentTotalList.get(j).getOwnerId() == currentTotalList.get(i).getOwnerId() && currentTotalList.get(j).getiNodeId() == currentTotalList.get(i).getiNodeId()) {
					currentTotalList.remove(j);
					total = total - 1;
				}
			}
		}
		if (end > totalList.size()) {
			for (int i = offset.intValue(); i < totalList.size(); i++) {
				currentTotalList.add(totalList.get(i));
			}
		} else {
			for (int i = offset.intValue(); i < end; i++) {
				currentTotalList.add(totalList.get(i));
			}
		}
		totalList.clear();
		for (INodeShare nodeShare : currentTotalList) {
			INode node = iNodeDAO.get(nodeShare.getOwnerId(), nodeShare.getiNodeId());
			if (node != null) {
				nodeShare.setType(node.getType());
			}
			if (nodeShare.getType() == INode.TYPE_FILE && FilesCommonUtils.isImage(nodeShare.getName())) {
				fillThumbnailUrl(pageRequest.getThumbnail(), user.getId(), nodeShare);
			}
			if (nodeShare.getType() == INode.TYPE_BACKUP_COMPUTER) {
				nodeShare.setType(INode.TYPE_FOLDER);
				nodeShare.setExtraType(INode.TYPE_BACKUP_COMPUTER_STR);
			} else if (nodeShare.getType() == INode.TYPE_BACKUP_DISK) {
				nodeShare.setType(INode.TYPE_FOLDER);
				nodeShare.setExtraType(INode.TYPE_BACKUP_DISK_STR);
			} else if (nodeShare.getType() == INode.TYPE_BACKUP_EMAIL) {
				nodeShare.setType(INode.TYPE_FOLDER);
				nodeShare.setExtraType(INode.TYPE_BACKUP_EMAIL_STR);
			}
		}
		String[] logMsgs = new String[] { String.valueOf(sharedUserId), String.valueOf(total) };
		String keyword = "LIST SHARE TO ME TOTAL:" + total + ("keyword:" + StringUtils.trimToEmpty(pageRequest.getKeyword()));
		fileBaseService.sendINodeEvent(user, EventType.OTHERS, null, null, UserLogType.LIST_SHARETO_ME, logMsgs, keyword);
		
		return new SharePageV2(currentTotalList, total);
	}
    
//    private List<INodeShare> assembleINodeShareV2(List<INodeShare> currentTotalList)
//    {
//        List<INodeShareV2> dbListV2 = new ArrayList<INodeShareV2>(BusinessConstants.INITIAL_CAPACITIES);
//        INodeShareV2 isv2;
//        for (INodeShare is : currentTotalList)
//        {
//            isv2 = new INodeShareV2();
//            isv2.setCreatedBy(is.getCreatedBy());
//            isv2.setModifiedAt(is.getModifiedAt());
//            isv2.setModifiedBy(is.getModifiedBy());
//            isv2.setCreatedBy(is.getCreatedBy());
//            isv2.setName(is.getName());
//            isv2.setOwnerId(is.getOwnerId());
//            isv2.setOwnerLoginName(is.getOwnerLoginName());
//            isv2.setOwnerName(is.getOwnerName());
//            
//            isv2.setRoleName(is.getRoleName());
//            isv2.setSharedUserEmail(is.getSharedUserEmail());
//            isv2.setSharedUserId(is.getSharedUserId());
//            isv2.setSharedUserLoginName(is.getSharedUserLoginName());
//            isv2.setSharedUserName(is.getSharedUserName());
//            isv2.setSharedUserType(getUserType(is.getSharedUserType()));
//            isv2.setSize(is.getSize());
//            isv2.setStatus(is.getStatus());
//            isv2.setNodeId(is.getiNodeId());
//            isv2.setSharedUserDescrip(is.getSharedDepartment());
//            isv2.setType(is.getType());
//            isv2.setOriginalType(is.getOriginalType());
//            isv2.setOriginalOwnerId(is.getOriginalOwnerId());
//            isv2.setOriginalNodeId(is.getOriginalNodeId());
//
//            dbListV2.add(isv2);
//        }
//        return dbListV2;
//    }
    
    /**
     * 填充缩略图地址
     * 
     * @param userId
     * @param nodeShare
     * @throws BaseRunException
     */
    private void fillThumbnailUrl(List<Thumbnail> sizeList, long userId, INodeShare nodeShare)
        throws BaseRunException
    {
        if (!FilesCommonUtils.isImage(nodeShare.getName()) || sizeList == null)
        {
            return;
        }
        INode node = iNodeDAO.get(nodeShare.getOwnerId(), nodeShare.getiNodeId());
        if (node == null)
        {
            return;
        }
        DataAccessURLInfo urlInfo = null;
        List<ThumbnailUrl> thumbList = new ArrayList<ThumbnailUrl>(BusinessConstants.INITIAL_CAPACITIES);
        ThumbnailUrl thumbnailUrl = null;
        for (Thumbnail thumbnail : sizeList)
        {
            urlInfo = fileBaseService.getINodeInfoDownURL(userId, node);
            thumbnailUrl = new ThumbnailUrl(urlInfo.getDownloadUrl()
                + FileBaseServiceImpl.getThumbnaliSuffix(thumbnail));
            thumbList.add(thumbnailUrl);
        }
        nodeShare.setType(node.getType());
        nodeShare.setThumbnailUrlList(thumbList);
    }
    
   
    
    private String getUserType(byte userType)
    {
        if (userType == User.USER_TYPE_USER)
        {
            return INodeACL.TYPE_USER;
        }
        
        return INodeACL.TYPE_GROUP;
    }
    
    private List<INodeShare> order(List<INodeShare> totalList, List<Order> orders,
        List<INodeShare> folderList, List<INodeShare> fileList)
    {
        boolean isContainType = false;
        for (Order order : orders)
        {
            if (StringUtils.equalsIgnoreCase(ORDER_TYPE, order.getField()))
            {
                isContainType = true;
                break;
            }
        }
        if (isContainType)
        {
            totalList = orderContainType(totalList, orders, folderList, fileList);
        }
        else
        {
            OrderComparatorUtil orderComparatorUtil = null;
            for (Order o : orders)
            {
                orderComparatorUtil = new OrderComparatorUtil(o.getField(), o.getDirection());
                Collections.sort(totalList, orderComparatorUtil);
            }
        }
        return totalList;
    }
    
    private List<INodeShare> orderContainType(List<INodeShare> totalList, List<Order> orders,
        List<INodeShare> folderList, List<INodeShare> fileList)
    {
        String field = null;
        String direct = null;
        String directType = null;
        for (INodeShare inodeShare : totalList)
        {
            if (inodeShare.getType() == INode.TYPE_FILE)
            {
                fileList.add(inodeShare);
            }
            else if (FilesCommonUtils.isFolderType(inodeShare.getType()))
            {
                folderList.add(inodeShare);
            }else{
            	shareToMeDAO.deleteByInode(inodeShare);
            	shareDAO.deleteByInodeAndSharedUser(inodeShare);
            }
        }
        
        OrderComparatorUtil fileOrderComparatorUtil = null;
        OrderComparatorUtil folderOrderComparatorUtil = null;
        for (Order o : orders)
        {
            if ("type".equals(o.getField()))
            {
                directType = o.getDirection();
                continue;
            }
            field = o.getField();
            direct = o.getDirection();
            fileOrderComparatorUtil = new OrderComparatorUtil(field, direct);
            folderOrderComparatorUtil = new OrderComparatorUtil(field, direct);
            Collections.sort(fileList, fileOrderComparatorUtil);
            Collections.sort(folderList, folderOrderComparatorUtil);
        }
        
        if (directType != null && "DESC".equalsIgnoreCase(directType))
        {
            totalList = new ArrayList<INodeShare>(fileList.size() + folderList.size());
            for (INodeShare inodeShare : fileList)
            {
                totalList.add(inodeShare);
            }
            for (INodeShare inodeShare : folderList)
            {
                totalList.add(inodeShare);
            }
        }
        else if (directType != null)
        {
            totalList = new ArrayList<INodeShare>(fileList.size() + folderList.size());
            for (INodeShare inodeShare : folderList)
            {
                totalList.add(inodeShare);
            }
            for (INodeShare inodeShare : fileList)
            {
                totalList.add(inodeShare);
            }
        }
        return totalList;
    }
    
}

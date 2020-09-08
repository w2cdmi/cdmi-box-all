package com.huawei.sharedrive.app.share.service.impl;

import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.link.RestLinkApproveList;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkApproveDetail;
import com.huawei.sharedrive.app.share.dao.INodeLinkApproveDao;
import com.huawei.sharedrive.app.share.dao.INodeLinkApproveRecordDao;
import com.huawei.sharedrive.app.share.dao.LinkApproveUserDao;
import com.huawei.sharedrive.app.share.domain.INodeLinkApprove;
import com.huawei.sharedrive.app.share.domain.INodeLinkApproveRecord;
import com.huawei.sharedrive.app.share.domain.LinkApproveUser;
import com.huawei.sharedrive.app.share.service.INodeLinkApproveService;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;
import com.huawei.sharedrive.app.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class INodeLinkApproveServiceImpl implements INodeLinkApproveService {
	@Autowired
	private INodeLinkApproveDao linkApproveDao;
	@Autowired
	private INodeLinkApproveRecordDao approveRecordDao;
	@Autowired
    private UserReverseDAO userReverseDAO;
	@Autowired
    private FileBaseService fileBaseService;
	
    @Autowired
    private LinkApproveUserDao inkApproveUserDao;
	
	@Override
	public void create(INodeLinkApprove linkApprove) {
		if(linkApprove.getStartTime() == null) {
			linkApprove.setStartTime(new Date());
		}
		User user= userReverseDAO.getBycloudUserId(linkApprove.getAccountId(), linkApprove.getLinkOwner());
		linkApprove.setLinkOwnerName(user.getName());
		linkApproveDao.create(linkApprove);
	}

	@Override
	public RestLinkApproveList listLinkApprove(INodeLinkApprove linkApprove, long offset, int limit, String orderField, String order,Byte type) {
		Map<String, Object> filter = new HashMap<>();
		filter.put("offset", offset);
		filter.put("limit", limit);
		filter.put("orderField", orderField);
		filter.put("order", order);
		filter.put("type", type);

		RestLinkApproveList restLinkApproveList = new RestLinkApproveList();
		List<INodeLinkApprove> rlist = null ;
		int totalCount = 0;
		if(linkApprove.getStatus()==INodeLinkApprove.APPROVE_STATUS_APPROVAL){
			//待我审批
			rlist = linkApproveDao.listLinkApprove(linkApprove, filter);
			totalCount=linkApproveDao.listCount(linkApprove, filter);
		}else if (linkApprove.getStatus()==INodeLinkApprove.APPROVE_STATUS_COMPLETE){
			//我已审批
			rlist = linkApproveDao.listUserApprove(linkApprove, filter);
			totalCount=linkApproveDao.listCountUserApprove(linkApprove, filter);
		}
	
		
		for (INodeLinkApprove approve : rlist) {
			INode inode = fileBaseService.getINodeInfo(approve.getLinkOwner(), approve.getNodeId());
			approve.setNodeName(inode.getName());
			approve.setType(inode.getType());
		}
		
		restLinkApproveList.setTotalCount(totalCount);
		restLinkApproveList.setLinkApproveList(rlist);
		restLinkApproveList.setLimit(limit);
		restLinkApproveList.setOffset(offset);

		return restLinkApproveList;
	}
	
	@Override
	public RestLinkApproveList listAllLinkApprove(INodeLinkApprove linkApprove, long offset, int limit, String orderField, String order) {
		Map<String, Object> filter = new HashMap<>();
		filter.put("offset", offset);
		filter.put("limit", limit);
		filter.put("orderField", orderField);
		filter.put("order", order);
	
		RestLinkApproveList restLinkApproveList = new RestLinkApproveList();
		List<INodeLinkApprove> rlist = linkApproveDao.listAllLinkApprove(linkApprove, filter);
		for (INodeLinkApprove approve : rlist) {
			User user = userReverseDAO.getBycloudUserId(linkApprove.getAccountId(), approve.getLinkOwner());
			approve.setLinkOwnerName(user.getName());
			INode inode = fileBaseService.getINodeInfo(approve.getLinkOwner(), approve.getNodeId());
			approve.setNodeName(inode.getName());
			approve.setType(inode.getType());
		}
		int totalCount = linkApproveDao.listAllCount(linkApprove, filter);
		restLinkApproveList.setTotalCount(totalCount);
		restLinkApproveList.setLinkApproveList(rlist);
		restLinkApproveList.setLimit(limit);
		restLinkApproveList.setOffset(offset);

		return restLinkApproveList;
	}

	@Override
	public void update(INodeLinkApprove iNodeLinkApprove) {
		linkApproveDao.update(iNodeLinkApprove);
	}

	@Override
	@Transactional
	public void updateStatus(INodeLinkApprove linkApprove) {
		if(linkApprove.getApproveAt() == null) {
			linkApprove.setApproveAt(new Date());
		}
		
		linkApproveDao.updateStatus(linkApprove);
		//生成审批记录
		INodeLinkApproveRecord record = new INodeLinkApproveRecord();
		record.setLinkCode(linkApprove.getLinkCode());
		record.setApproveAt(linkApprove.getApproveAt());
		record.setApproveBy(linkApprove.getApproveBy());
		record.setStatus(linkApprove.getStatus());
		record.setComment(linkApprove.getApplyReason());
		approveRecordDao.create(record);
	}

	@Override
	public INodeLinkApprove getApproveByLinkCode(String linkCode) {
		 return linkApproveDao.getApproveByLinkCode(linkCode);
	}

	@Override
	@Transactional
	public void deleteByLinkCode(String linkCode) {
		linkApproveDao.deleteByLinkCode(linkCode);
		approveRecordDao.deleteByLinkCode(linkCode);
	}
	

	@Override
	public RestLinkApproveDetail getApproveDetailByLinkCode(String linkCode) {
		INodeLinkApprove approve = linkApproveDao.getApproveByLinkCode(linkCode);
		RestLinkApproveDetail detail = new RestLinkApproveDetail();
		detail.setLinkApprove(approve);
		List<INodeLinkApproveRecord> recordList = approveRecordDao.listByLinkCode(linkCode);
		for (INodeLinkApproveRecord record : recordList) {
			User user = userReverseDAO.getBycloudUserId(approve.getAccountId(), approve.getLinkOwner());
			record.setApproveByName(user.getName());
		}
		detail.setApproveRecordList(recordList);

		return detail;
	}

	@Override
	public void deleteByNodeId(UserToken userInfo, INode iNode) {
		// TODO Auto-generated method stub
		linkApproveDao.deleteByNodeId(userInfo.getCloudUserId(),iNode.getId());
		linkApproveDao.updateLinkStatus(userInfo.getCloudUserId(),iNode.getId());
	}


}

package com.huawei.sharedrive.uam.weixin.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.openapi.domain.user.PageRequestUserProfits;
import com.huawei.sharedrive.uam.weixin.dao.UserProfitDetailDao;
import com.huawei.sharedrive.uam.weixin.domain.UserProfitDetail;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
@Repository
public class UserProfitDetailDaoImpl extends CacheableSqlMapClientDAO implements UserProfitDetailDao{

	@Override
	public List<UserProfitDetail> listByTypeAndStatus(byte type,byte status) {
		// TODO Auto-generated method stub
		UserProfitDetail userProfitDetail =new UserProfitDetail();
		userProfitDetail.setType(type);
		userProfitDetail.setStatus(status);
		return sqlMapClientTemplate.queryForList("UserProfitDetail.listByTypeAndStatus",userProfitDetail);
	}

	@Override
	public void create(UserProfitDetail userProfitDetail) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("UserProfitDetail.insert",userProfitDetail);
	}
	
	@Override
	public void updateStatus(UserProfitDetail userProfitDetail) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("UserProfitDetail.updateStatus",userProfitDetail);
	}

	@Override
	public List<UserProfitDetail> list(UserProfitDetail filter,PageRequestUserProfits requestUserProfits) {
		// TODO Auto-generated method stub
		
	    
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        if (CollectionUtils.isNotEmpty(requestUserProfits.getOrder()))
        {
            map.put("orderBy", getOrderByStr(requestUserProfits.getOrder()));
        }else{
        	List<Order> orderList = new ArrayList<>();
        	Order order = new Order("createAt", true);
        	orderList.add(order);
        	map.put("orderBy", getOrderByStr(orderList));
        }
        Limit limit = new Limit();
        limit.setOffset(requestUserProfits.getOffset());
        limit.setLength(requestUserProfits.getLimit());
        map.put("limit", limit);
		return sqlMapClientTemplate.queryForList("UserProfitDetail.getPageList",map);
	}

	
    private String getOrderByStr(List<Order> orderList)
    {
        if (null == orderList)
        {
            return "";
        }
        StringBuffer orderBy = new StringBuffer();
        String field = null;
        for (Order order : orderList)
        {
            field = order.getField();
            if ("name".equalsIgnoreCase(field))
            {
                field = "convert(name using gb2312)";
            }
            if ("sharedUserName".equalsIgnoreCase(field))
            {
                field = "convert(sharedUserName using gb2312)";
            }
            orderBy.append(field).append(" ").append(order.getDirection()).append(",");
        }
        orderBy = orderBy.deleteCharAt(orderBy.length() - 1);
        return orderBy.toString();
    }
}

package com.huawei.sharedrive.app.openapi.domain.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class GroupOrderUserRequest extends GroupOrderRequest
{
    private String listRole;
    
    public String getListRole()
    {
        return listRole;
    }
    
    public void setListRole(String listRole)
    {
        this.listRole = listRole;
    }
    
    public void checkParameter()
    {
        if (listRole == null)
        {
            listRole = "false";
        }
        else if (!StringUtils.equals(listRole, "true")
            && !StringUtils.equals(listRole, "false"))
        {
            throw new InvalidParamException("listRole error:" + listRole);
        }
        super.checkParameter();
    }
    
    public void checkOrder()
    {
        if (super.getOrder() == null)
        {
            super.setOrder(getDefaultOrderList());
        }
        for (GroupOrder groupOrder : super.getOrder())
        {
            groupOrder.checkParameter();
        }
    }
    
    private List<GroupOrder> getDefaultOrderList()
    {
        List<GroupOrder> orderList = new ArrayList<GroupOrder>(2);
        // 默认按照用户名升序排列
        orderList.add(new GroupOrder("name", "asc"));
        orderList.add(new GroupOrder("groupRole", "asc"));
        return orderList;
    }
}

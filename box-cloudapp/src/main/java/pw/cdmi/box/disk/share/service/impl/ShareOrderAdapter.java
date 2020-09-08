package pw.cdmi.box.disk.share.service.impl;

import java.util.ArrayList;
import java.util.List;

import pw.cdmi.box.domain.Order;

public final class ShareOrderAdapter
{
    private static final String DIRECTION_ASC = "ASC";
    
    private static final String DIRECTION_DESC = "DESC";
    
    private static final String FIELD_NAME = "name";
    
    private static final String FIELD_SHARE_DATE = "modifiedAt";
    
    private static final String FIELD_SHARER = "ownerName";
    
    private static final String FIELD_TYPE = "type";
    
    private static volatile List<Order> nameOrderAsc = null;
    
    private static volatile List<Order> nameOrderDesc = null;
    
    private static volatile List<Order> sharerOrderAsc = null;
    
    private static volatile List<Order> sharerOrderDesc = null;
    
    private static volatile List<Order> shareTimeOrderAsc = null;
    
    private static volatile List<Order> shareTimeOrderDesc = null;
    
    public static List<Order> getOrderList(Order orderV1)
    {
        if (orderV1.isDesc())
        {
            if (FIELD_NAME.equals(orderV1.getField()))
            {
                return getNameOrderDesc();
            }
            else if (FIELD_SHARER.equals(orderV1.getField()))
            {
                return getSharerOrderDesc();
            }
            else
            {
                return getTimeOrderDesc();
            }
        }
        if (FIELD_NAME.equals(orderV1.getField()))
        {
            return getNameOrderAsc();
        }
        else if (FIELD_SHARER.equals(orderV1.getField()))
        {
            return getSharerOrderAsc();
        }
        else
        {
            return getTimeOrderAsc();
        }
    }
    
    private static List<Order> getNameOrderAsc()
    {
        if (null != nameOrderAsc)
        {
            return nameOrderAsc;
        }
        synchronized (ShareToMeServiceImpl.class)
        {
            if (null == nameOrderAsc)
            {
                nameOrderAsc = new ArrayList<Order>(2);
                nameOrderAsc.add(new Order(FIELD_TYPE, DIRECTION_ASC));
                nameOrderAsc.add(new Order(FIELD_NAME, DIRECTION_ASC));
            }
        }
        return nameOrderAsc;
    }
    
    private static List<Order> getNameOrderDesc()
    {
        if (null != nameOrderDesc)
        {
            return nameOrderDesc;
        }
        synchronized (ShareToMeServiceImpl.class)
        {
            if (null == nameOrderDesc)
            {
                nameOrderDesc = new ArrayList<Order>(2);
                nameOrderDesc.add(new Order(FIELD_TYPE, DIRECTION_ASC));
                nameOrderDesc.add(new Order(FIELD_NAME, DIRECTION_DESC));
            }
        }
        return nameOrderDesc;
    }
    
    private static List<Order> getSharerOrderAsc()
    {
        if (null != sharerOrderAsc)
        {
            return sharerOrderAsc;
        }
        synchronized (ShareToMeServiceImpl.class)
        {
            if (null == sharerOrderAsc)
            {
                sharerOrderAsc = new ArrayList<Order>(3);
                sharerOrderAsc.add(new Order(FIELD_SHARER, DIRECTION_ASC));
                sharerOrderAsc.add(new Order(FIELD_TYPE, DIRECTION_ASC));
            }
        }
        return sharerOrderAsc;
    }
    
    private static List<Order> getSharerOrderDesc()
    {
        if (null != sharerOrderDesc)
        {
            return sharerOrderDesc;
        }
        synchronized (ShareToMeServiceImpl.class)
        {
            if (null == sharerOrderDesc)
            {
                sharerOrderDesc = new ArrayList<Order>(3);
                sharerOrderDesc.add(new Order(FIELD_SHARER, DIRECTION_DESC));
                sharerOrderDesc.add(new Order(FIELD_TYPE, DIRECTION_ASC));
            }
        }
        return sharerOrderDesc;
    }
    
    private static List<Order> getTimeOrderAsc()
    {
        if (null != shareTimeOrderAsc)
        {
            return shareTimeOrderAsc;
        }
        synchronized (ShareToMeServiceImpl.class)
        {
            if (null == shareTimeOrderAsc)
            {
                shareTimeOrderAsc = new ArrayList<Order>(1);
                shareTimeOrderAsc.add(new Order(FIELD_SHARE_DATE, DIRECTION_ASC));
                shareTimeOrderAsc.add(new Order(FIELD_TYPE, DIRECTION_ASC));
            }
        }
        return shareTimeOrderAsc;
    }
    
    private static List<Order> getTimeOrderDesc()
    {
        if (null != shareTimeOrderDesc)
        {
            return shareTimeOrderDesc;
        }
        synchronized (ShareToMeServiceImpl.class)
        {
            if (null == shareTimeOrderDesc)
            {
                shareTimeOrderDesc = new ArrayList<Order>(1);
                shareTimeOrderDesc.add(new Order(FIELD_SHARE_DATE, DIRECTION_DESC));
                shareTimeOrderDesc.add(new Order(FIELD_TYPE, DIRECTION_ASC));
            }
        }
        return shareTimeOrderDesc;
    }
    
    private ShareOrderAdapter()
    {
        
    }
    
}

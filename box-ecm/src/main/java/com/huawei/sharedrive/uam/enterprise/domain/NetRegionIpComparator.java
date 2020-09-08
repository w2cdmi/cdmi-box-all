package com.huawei.sharedrive.uam.enterprise.domain;

import java.io.Serializable;
import java.util.Comparator;

public class NetRegionIpComparator implements Comparator<NetRegionIp>, Serializable 
{
    private static final long serialVersionUID = 4109350825990015615L;

    @Override
    public int compare(NetRegionIp o1, NetRegionIp o2)
    {
        if (o1.getIpStartValue() < o2.getIpStartValue())
        {
            return -1;
        }
        return 0;
    }
}

package com.huawei.sharedrive.uam.statistics.domain;

import java.io.Serializable;
import java.util.Comparator;

import com.huawei.sharedrive.uam.statistics.StatisticsHistoryNode;

public class TerminalHistoryDateSortor implements Comparator<StatisticsHistoryNode>, Serializable
{
    private static final long serialVersionUID = -5609992568409500661L;

    @Override
    public int compare(StatisticsHistoryNode o1, StatisticsHistoryNode o2)
    {
        if (o1 == null)
        {
            return -1;
        }
        if (o2 == null)
        {
            return 1;
        }
        int res = o1.getTimePoint().getYear() - o2.getTimePoint().getYear();
        if (res != 0)
        {
            return res;
        }
        return o1.getTimePoint().getNumber() - o2.getTimePoint().getNumber();
    }
    
}

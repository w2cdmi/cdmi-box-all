package pw.cdmi.box.uam.statistics.domain.sortor;

import java.io.Serializable;
import java.util.Comparator;

import pw.cdmi.box.uam.statistics.domain.TerminalStatisticsDay;

public class TerminalStatisticsDaySortor implements Comparator<TerminalStatisticsDay>, Serializable
{
    
    private static final long serialVersionUID = 3139659443866743085L;
    
    @Override
    public int compare(TerminalStatisticsDay o1, TerminalStatisticsDay o2)
    {
        return o1.getDay() - o2.getDay();
    }
}

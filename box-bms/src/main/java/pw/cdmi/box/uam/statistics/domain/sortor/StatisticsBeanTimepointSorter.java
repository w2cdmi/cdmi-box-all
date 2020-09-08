package pw.cdmi.box.uam.statistics.domain.sortor;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.exception.InternalServerErrorException;
import pw.cdmi.box.uam.statistics.domain.BasicStatisticsBean;

public class StatisticsBeanTimepointSorter implements Comparator<BasicStatisticsBean>, Serializable
{
    
    private static final long serialVersionUID = -6358631000255697265L;
    
    @Override
    public int compare(BasicStatisticsBean o1, BasicStatisticsBean o2)
    {
        if (null == o1.getTimePoint())
        {
            return -1;
        }
        if (null == o2.getTimePoint())
        {
            return 1;
        }
        if (o1.getTimePoint().getYear().intValue() != o2.getTimePoint().getYear().intValue())
        {
            return o1.getTimePoint().getYear() - o2.getTimePoint().getYear();
        }
        if (!StringUtils.equalsIgnoreCase(o1.getTimePoint().getUnit(), o2.getTimePoint().getUnit()))
        {
            throw new InternalServerErrorException("No match unit");
        }
        return o1.getTimePoint().getNumber() - o2.getTimePoint().getNumber();
    }
    
}

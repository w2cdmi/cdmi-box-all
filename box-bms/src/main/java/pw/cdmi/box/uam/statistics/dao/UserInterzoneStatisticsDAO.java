package pw.cdmi.box.uam.statistics.dao;

import pw.cdmi.box.uam.statistics.domain.UserInterzone;

public interface UserInterzoneStatisticsDAO
{
    void save(UserInterzone interzone);
    
    void delete();
    
    UserInterzone query();
}

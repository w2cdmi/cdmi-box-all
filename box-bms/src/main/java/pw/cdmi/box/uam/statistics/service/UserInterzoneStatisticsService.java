package pw.cdmi.box.uam.statistics.service;

import pw.cdmi.box.uam.statistics.domain.UserInterzone;

public interface UserInterzoneStatisticsService
{
    void save(UserInterzone interzone);
    
    void delete();
    
    UserInterzone query();
}

package pw.cdmi.box.uam.statistics.manager;

import pw.cdmi.box.uam.statistics.domain.UserInterzone;

public interface UserInterzoneStatisticsManager
{
    void saveAndDelete(String interzone);
    
    UserInterzone query();
}

package pw.cdmi.box.uam.statistics.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.statistics.domain.UserInterzone;
import pw.cdmi.box.uam.statistics.manager.UserInterzoneStatisticsManager;
import pw.cdmi.box.uam.statistics.service.UserInterzoneStatisticsService;

@Component
public class UserInterzoneStatisticsManagerImpl implements UserInterzoneStatisticsManager
{
    @Autowired
    private UserInterzoneStatisticsService userInterService;
    
    @Override
    public void saveAndDelete(String interzone)
    {
        UserInterzone userInterzone = new UserInterzone();
        userInterzone.setInterzone(interzone);
        userInterService.delete();
        userInterService.save(userInterzone);
    }
    
    @Override
    public UserInterzone query()
    {
        return userInterService.query();
    }
    
}

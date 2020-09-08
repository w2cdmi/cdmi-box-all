package pw.cdmi.box.uam.statistics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.statistics.dao.UserInterzoneStatisticsDAO;
import pw.cdmi.box.uam.statistics.domain.UserInterzone;
import pw.cdmi.box.uam.statistics.service.UserInterzoneStatisticsService;
import pw.cdmi.core.utils.UUIDUtils;

@Component
public class UserInterzoneStatisticsServiceImpl implements UserInterzoneStatisticsService
{
    @Autowired
    private UserInterzoneStatisticsDAO userInterzoneDAO;
    
    @Override
    public void save(UserInterzone interzone)
    {
        interzone.setId(UUIDUtils.getValueAfterMD5());
        userInterzoneDAO.save(interzone);
    }
    
    @Override
    public void delete()
    {
        userInterzoneDAO.delete();
    }
    
    @Override
    public UserInterzone query()
    {
        return userInterzoneDAO.query();
    }
    
}

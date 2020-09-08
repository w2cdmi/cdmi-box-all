package pw.cdmi.box.uam.statistics.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pw.cdmi.box.uam.common.web.AbstractCommonController;
import pw.cdmi.box.uam.log.domain.OperateDescription;
import pw.cdmi.box.uam.log.domain.OperateType;
import pw.cdmi.box.uam.log.manager.SystemLogManager;
import pw.cdmi.box.uam.statistics.domain.StatisticsConstants;
import pw.cdmi.box.uam.statistics.domain.UserInterzone;
import pw.cdmi.box.uam.statistics.domain.UserInterzoneRequest;
import pw.cdmi.box.uam.statistics.manager.UserInterzoneStatisticsManager;
import pw.cdmi.box.uam.util.PatternRegUtil;

@Controller
@RequestMapping(value = "/userInterzone")
public class UserInterzoneStatisticsController extends AbstractCommonController
{
    
    private static final String INTERZONE_REGEX = "[0-9]+";
    
    private static final int MAX_LENGTH = 6;
    
    @Autowired
    private UserInterzoneStatisticsManager userInterManager;
    
    @Autowired
    private SystemLogManager systemLogManager;
    
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> saveInterzone(UserInterzoneRequest interzone, HttpServletRequest request,
        String token)
    {
        String[] description = new String[]{interzone.getInterzone()};
        String id = systemLogManager.saveFailLog(request,
            OperateType.Statistics,
            OperateDescription.STATISTICS_SPACE_SET,
            null,
            description);
        super.checkToken(token);
        String[] interzones = interzone.getInterzone().split(StatisticsConstants.SIGNL);
        
        if (interzones.length > MAX_LENGTH)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        
        Arrays.sort(interzones);
        for (int i = 0; i < interzones.length; i++)
        {
            if (!PatternRegUtil.isParameterLegal(interzones[i], INTERZONE_REGEX))
            {
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }
            if (i+1 < interzones.length && interzones[i].equals(interzones[i+1]))
            {
                return new ResponseEntity<String>("existRegion", HttpStatus.BAD_REQUEST);
            }
            
        }
        
        userInterManager.saveAndDelete(interzone.getInterzone());
        systemLogManager.updateSuccess(id);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = "getInterzone", method = RequestMethod.POST)
    public ResponseEntity<?> getInterzone(String token)
    {
        super.checkToken(token);
        UserInterzone interzone = userInterManager.query();
        if (interzone == null || interzone.getInterzone() == null)
        {
            interzone = new UserInterzone();
            interzone.setInterzone(StatisticsConstants.DEFAULT_ZONE);
            userInterManager.saveAndDelete(interzone.getInterzone());
        }
        List<Integer> intezoneList = new ArrayList<Integer>(StatisticsConstants.LIST_SIZE);
        String[] interzones = interzone.getInterzone().split(StatisticsConstants.SIGNL);
        for (int i = 0; i < interzones.length; i++)
        {
            intezoneList.add(Integer.valueOf(interzones[i]));
        }
        Collections.sort(intezoneList);
        return new ResponseEntity<List<Integer>>(intezoneList, HttpStatus.OK);
    }
    
}

/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.announcement.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import pw.cdmi.common.domain.BasicConfig;
import pw.cdmi.common.domain.SystemConfig;


public class AnnouncementConfig extends BasicConfig
{
    private static final long serialVersionUID = -3844893278793580374L;
    
    private static final String ANNOUNCEMENT_MESSAGE_SAVING_TIMES = "announcement.message.saving.times";
    
    public static final String ANNOUNCEMENT_CONFIG_PREFIX = "announcement.";
    
    @NotNull
    @Min(1)
    @Max(30)
    private int messageSavingTimes;
    
    public int getMessageSavingTimes()
    {
        return messageSavingTimes;
    }
    
    public void setMessageSavingTimes(int messageSavingTimes)
    {
        this.messageSavingTimes = messageSavingTimes;
    }
    
    public List<SystemConfig> toConfigItem()
    {
        List<SystemConfig> list = new ArrayList<SystemConfig>(1);
        list.add(new SystemConfig(getAppId(), ANNOUNCEMENT_MESSAGE_SAVING_TIMES,
            String.valueOf(messageSavingTimes)));
        return list;
    }
    
    /**
     * 
     * @param itemList
     * @return
     */
    public static AnnouncementConfig buildAccessAddressConfig(List<SystemConfig> itemList)
    {
        if (null == itemList || itemList.isEmpty())
        {
            return null;
        }
        
        Map<String, String> map = new HashMap<String, String>(3);
        for (SystemConfig configItem : itemList)
        {
            map.put(configItem.getId(), configItem.getValue());
        }
        AnnouncementConfig announcementConfig = new AnnouncementConfig();
        announcementConfig.setMessageSavingTimes(Integer.parseInt(map.get(ANNOUNCEMENT_MESSAGE_SAVING_TIMES)));
        return announcementConfig;
    }
}

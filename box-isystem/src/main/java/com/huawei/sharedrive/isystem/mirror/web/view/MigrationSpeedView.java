package com.huawei.sharedrive.isystem.mirror.web.view;

import java.io.Serializable;
import java.util.Date;

import com.huawei.sharedrive.isystem.mirror.appdatamigration.domain.MigrationProcessInfo;

public class MigrationSpeedView implements Serializable
{
    private static final long serialVersionUID = 5495384418514796559L;
    
    public static final String GB = "GB(";
    
    private String sweep;
    
    private String success;
    
    private String fail;
    
    private boolean needRed = false;
    
    private Date startTime;
    
    private Date endTime;
    
    public MigrationSpeedView()
    {
        
    }
    
    public MigrationSpeedView(MigrationProcessInfo migrationProcessInfo)
    {
        this.startTime = migrationProcessInfo.getModifiedAt();
        this.endTime = migrationProcessInfo.getEndTime();
        this.sweep = formart(migrationProcessInfo.getTotalSizes(), migrationProcessInfo.getTotalFiles());
        this.success = formart(migrationProcessInfo.getCurFiles(), migrationProcessInfo.getCurFiles());
        this.fail = formart(migrationProcessInfo.getFailedSizes(), migrationProcessInfo.getFailedFiles());
        this.id = migrationProcessInfo.getId();
        if (migrationProcessInfo.getTotalSizes() != 0)
        {
            this.prosess = (float) (migrationProcessInfo.getCurSizes() + migrationProcessInfo.getFailedSizes())
                * 100 / migrationProcessInfo.getTotalSizes();
        }
        else
        {
            this.prosess = 100.0f;
        }
        if (migrationProcessInfo.getFailedSizes() > 0 || migrationProcessInfo.getFailedFiles() > 0)
        {
            this.needRed = true;
        }
    }
    
    public static String formart(long l, long m)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(l);
        sb.append(GB);
        sb.append(m);
        sb.append(')');
        return sb.toString();
    }
    
    public boolean isNeedRed()
    {
        return needRed;
    }
    
    public void setNeedRed(boolean needRed)
    {
        this.needRed = needRed;
    }
    
    private String id;
    
    private float prosess;
    
    private int times;
    
    public String getSweep()
    {
        return sweep;
    }
    
    public void setSweep(String sweep)
    {
        this.sweep = sweep;
    }
    
    public String getSuccess()
    {
        return success;
    }
    
    public void setSuccess(String success)
    {
        this.success = success;
    }
    
    public String getFail()
    {
        return fail;
    }
    
    public void setFail(String fail)
    {
        this.fail = fail;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public float getProsess()
    {
        return prosess;
    }
    
    public void setProsess(float prosess)
    {
        this.prosess = prosess;
    }
    
    public Date getStartTime()
    {
        if (this.startTime == null)
        {
            return null;
        }
        return (Date) startTime.clone();
    }
    
    public void setStartTime(Date startTime)
    {
        if (startTime == null)
        {
            this.startTime = null;
        }
        else
        {
            this.startTime = (Date) startTime.clone();
        }
    }
    
    public Date getEndTime()
    {
        if (this.endTime == null)
        {
            return null;
        }
        return (Date) endTime.clone();
    }
    
    public void setEndTime(Date endTime)
    {
        if (endTime == null)
        {
            this.endTime = null;
        }
        else
        {
            this.endTime = (Date) endTime.clone();
        }
    }
    
    public int getTimes()
    {
        return times;
    }
    
    public void setTimes(int times)
    {
        this.times = times;
    }
    
}

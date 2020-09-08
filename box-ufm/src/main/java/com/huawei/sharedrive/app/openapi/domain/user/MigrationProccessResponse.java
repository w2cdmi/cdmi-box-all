package com.huawei.sharedrive.app.openapi.domain.user;


import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;


/**
 * 数据迁移请求
 * @author c00287749
 *
 */
public class MigrationProccessResponse extends MigrationResponse
{
    
    //已经迁移的当前文件数
    private long curFiles;
    
    //已经迁移的当前文件大小数
    private long curSizes;

    //总文件或者版本数
    private long totalFiles;

    //总文件大小数
    private long totalSizes;


    public MigrationProccessResponse(UserDataMigrationTask task)
    {
        super(task);
        this.totalFiles =  task.getTotalFiles();
        this.totalSizes =  task.getTotalSizes();
        this.status = task.getStatus();
        this.curFiles = task.getCurFiles();
        this.curSizes = task.getCurSizes();
        
    }

    public long getCurFiles()
    {
        return curFiles;
    }

    public long getCurSizes()
    {
        return curSizes;
    }

    public long getTotalFiles()
    {
        return totalFiles;
    }

    public long getTotalSizes()
    {
        return totalSizes;
    }

    public void setCurFiles(long curFiles)
    {
        this.curFiles = curFiles;
    }
    
    public void setCurSizes(long curSizes)
    {
        this.curSizes = curSizes;
    }
    
    public void setTotalFiles(long totalFiles)
    {
        this.totalFiles = totalFiles;
    }
    
    public void setTotalSizes(long totalSizes)
    {
        this.totalSizes = totalSizes;
    }
    
}

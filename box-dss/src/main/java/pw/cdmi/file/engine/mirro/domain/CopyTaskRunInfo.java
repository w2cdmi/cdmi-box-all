package pw.cdmi.file.engine.mirro.domain;

import java.io.Serializable;

/**
 * 保存dss各节点执行任务情况
 * 
 * @author cWX348274
 * 
 */
public class CopyTaskRunInfo implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8979208851526748511L;
    
    private String exeAgent;
    
    private Integer copystatus;
    
    private Integer taskNum;
    
    private Integer addNum;
    
    public String getExeAgent()
    {
        return exeAgent;
    }
    
    public void setExeAgent(String exeAgent)
    {
        this.exeAgent = exeAgent;
    }
    
    public Integer getCopustatus()
    {
        return copystatus;
    }
    
    public void setCopystatus(Integer copystatus)
    {
        this.copystatus = copystatus;
    }
    
    public Integer getTaskNum()
    {
        return taskNum;
    }
    
    public void setTaskNum(Integer taskNum)
    {
        this.taskNum = taskNum;
    }
    
    public Integer getAddNum()
    {
        return addNum;
    }
    
    public void setAddNum(Integer addNum)
    {
        this.addNum = addNum;
    }
    
}

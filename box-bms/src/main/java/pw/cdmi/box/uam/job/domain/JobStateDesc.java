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
package pw.cdmi.box.uam.job.domain;

public enum JobStateDesc
{
    /** 已停止 */
    STOP(0, "job.state.stop"),
    /** 运行中 */
    RUNNING(1, "job.state.running");
    
    private int code = 0;
    
    private String nameCode;
    
    private JobStateDesc(int code, String nameCode)
    {
        this.code = code;
        this.nameCode = nameCode;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public String getNameCode()
    {
        return this.nameCode;
    }
    
    public static JobStateDesc parseStatus(int code)
    {
        for (JobStateDesc state : JobStateDesc.values())
        {
            if (code == state.getCode())
            {
                return state;
            }
        }
        
        return null;
    }
}

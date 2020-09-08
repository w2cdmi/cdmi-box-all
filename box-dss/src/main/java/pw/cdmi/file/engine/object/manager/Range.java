/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.manager;

import pw.cdmi.file.engine.core.common.AutoToStringEntity;

/**
 * 
 * @author s90006125
 * 这个数据区间是个闭合区间，起始值是 0，所以“Range: bytes=0-1”这样一个请求实际上是在请求开头的 2 个字节。<br>
 * 表示头500个字节：bytes=0-499<br>
 * 表示第二个500字节：bytes=500-999<br>
 * 表示最后500个字节：bytes=-500<br>
 * 表示500字节以后的范围：bytes=500-<br>
 * 第一个和最后一个字节：bytes=0-0,-1<br>
 * 同时指定几个范围：bytes=500-600,601-999 
 */
public class Range extends AutoToStringEntity
{
    private static final long serialVersionUID = 1991406404647066690L;

    private Long start;
    
    private Long end;
    
    public Range(Long start, Long end)
    {
        this.start = start;
        this.end = end;
    }
    
    public Long getStart()
    {
        return start;
    }
    
    public void setStart(Long start)
    {
        this.start = start;
    }
    
    public Long getEnd()
    {
        return end;
    }
    
    public void setEnd(Long end)
    {
        this.end = end;
    }
}

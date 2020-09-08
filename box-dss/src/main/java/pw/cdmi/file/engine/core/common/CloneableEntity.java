/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.core.common;

import org.springframework.beans.BeanUtils;

public class CloneableEntity extends AutoToStringEntity implements Cloneable
{
    private static final long serialVersionUID = -1877372550641803628L;
    
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Object destObject = super.clone();
        
        BeanUtils.copyProperties(this, destObject);
        
        return destObject;
    }
}

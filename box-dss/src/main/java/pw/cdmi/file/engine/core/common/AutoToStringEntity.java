/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
package pw.cdmi.file.engine.core.common;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 实现toString方法的基础对象
 * 
 * @author s90006125
 * 
 */
public class AutoToStringEntity extends BaseEntity
{
    private static final long serialVersionUID = 6848344177599277581L;
    
    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this);
    }
}

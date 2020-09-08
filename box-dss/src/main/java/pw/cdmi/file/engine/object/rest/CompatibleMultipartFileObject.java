/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest;

import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonProperty;

import pw.cdmi.file.engine.object.domain.MultipartPart;

/**
 * 分片文件对象的兼容性对象
 * 
 * @author s90006125
 * 
 */
public class CompatibleMultipartFileObject
{
    @JsonProperty("parts")
    private Set<CompatibleMultipartPart> parts = new TreeSet<CompatibleMultipartPart>();
    
    public CompatibleMultipartFileObject(Set<MultipartPart> partList)
    {
        if (null != partList)
        {
            CompatibleMultipartPart compatibleMultipartPart;
            for (MultipartPart part : partList)
            {
                compatibleMultipartPart = new CompatibleMultipartPart(part.getPartId(), part.getPartSize());
                this.parts.add(compatibleMultipartPart);
            }
        }
    }
    
    public Set<CompatibleMultipartPart> getParts()
    {
        if (null == this.parts)
        {
            parts = new TreeSet<CompatibleMultipartPart>();
        }
        return parts;
    }
}

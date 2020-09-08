/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.domain;

/**
 * 
 * @author s90006125
 * 
 */
public class PreviewFileObject extends FileObjectAttachment
{
    private static final long serialVersionUID = -6003123658100847117L;
    
    private static final String SUFFIX_PREVIEW = "preview";
    
    /** 需要生成的类型 */
    private PreviewFileType distType;
    
    /** 原始类型 */
    private String srcType;
    
    public PreviewFileObject(String objectID, PreviewFileType distType, String srcType)
    {
        super(objectID);
        this.distType = distType;
        this.srcType = srcType;
        this.setAttachment(SUFFIX_PREVIEW + '.' + distType.getType());
    }
    
    public PreviewFileType getDistType()
    {
        return distType;
    }
    
    public void setDistType(PreviewFileType distType)
    {
        this.distType = distType;
    }
    
    public String getSrcType()
    {
        return srcType;
    }
    
    public void setSrcType(String srcType)
    {
        this.srcType = srcType;
    }
}

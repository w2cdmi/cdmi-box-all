/*
 * 版权声明(Copyright Notice)：
 *      Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 *      Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved. 
 * 
 *      警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业目
 */ 
package pw.cdmi.file.engine.object.rest.support;

/**
 * 
 * @author s90006125
 * 
 */
public enum Method
{
    /**
     * POST整体上传
     */
    OPTION_OBJECT,
    
    /**
     * PUT整体上传
     */
    PUT_OBJECT, 
    
    /**
     * 整体下载
     */
    GET_OBJECT, 
    
    /**
     * POST整体上传
     */
    POST_OBJECT,
    
    /**
     * PUT方式分片上传
     */
    PUT_PART,
    
    /**
     *POST方式分片上传 
     */
    POST_PART, 
    
    /**
     * 获取分片列表
     */
    GET_PARTS, 
    
    /**
     * 分片上传完毕，整体提交
     */
    PUT_COMMIT, 
    
    /**
     * 取消分片
     */
    DELETE_PART,
    
    /**
     * 获取缩略图
     */
    GET_THUMBNAIL, 
    
    /**
     * 获取文件预览
     */
    GET_PREVIEW;
    
    public static Method parse(String name)
    {
        for(Method m : Method.values())
        {
            if(m.name().equalsIgnoreCase(name))
            {
                return m;
            }
        }
        
        return null;
    }
}

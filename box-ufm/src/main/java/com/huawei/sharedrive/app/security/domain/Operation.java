package com.huawei.sharedrive.app.security.domain;

public enum Operation
{
    /** 浏览文件夹 */
    BROWSER("browser"),
    
    /** 复制 */
    COPY("copy"),
    
    /** 创建文件夹 */
    CREATE("create"),
    
    /** 删除 */
    DELETE("delete"),
    
    /** 下载文件 */
    DOWNLOAD("download"),
    
    /** 发布外链 */
    LINK("link"),
    
    /** 移动 */
    MOVE("move"),
    
    /** 文件预览 */
    PREVIEW("preview"),
    
    /** 共享 */
    SHARE("share"),
    
    /** 上传文件 */
    UPLOAD("upload");
    
    private String code;
    
    private Operation(String code)
    {
        this.code = code;
    }
    
    public static Operation getOperation(String code)
    {
        for (Operation operation : Operation.values())
        {
            if (operation.getCode().equals(code))
            {
                return operation;
            }
        }
        return null;
    }
    
    public String getCode()
    {
        return code;
    }
    
}

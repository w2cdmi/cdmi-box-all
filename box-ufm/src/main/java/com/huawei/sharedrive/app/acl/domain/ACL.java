package com.huawei.sharedrive.app.acl.domain;


/**
 * 
 */
// 个 十 百 千 万 十万 百万 千万
// 浏览 上传 下载 预览 删除 编辑 获取链接 权限变更
public class ACL
{
    // 明确允许
    public final static int OPER_ALLOW = 1;
    
    // 明确拒绝
    public final static int OPER_DENY = 2;
    
    // 不明确拒绝
    public final static int OPER_UNKNOWN_DENY = 0;
    
    private boolean author;
    
    private int authorValue;
    
    private boolean delete;
    
    private int deleteValue;
    
    private boolean download;
    
    private int downloadValue;
    
    private boolean edit;
    
    private int editValue;
    
    private boolean publishLink;
    
    private int publishLinkValue;
    
    private boolean list;
    
    private int listValue;
    
    private boolean preview;
    
    private int previewValue;
    
    private boolean upload;
    
    private int uploadValue;
    
    public ACL(ResourceRole role)
    {
        this.getPriviledge(role);
        this.getOperPermissible();
    }
    
    public int getAuthorValue()
    {
        return authorValue;
    }
    
    public int getDeleteValue()
    {
        return deleteValue;
    }
    
    public int getDownloadValue()
    {
        return downloadValue;
    }
    
    public int getEditValue()
    {
        return editValue;
    }
    
    public int getPublishLinkValue()
    {
        return publishLinkValue;
    }
    
    public int getListValue()
    {
        return listValue;
    }
    
    public ACL getOperPermissible()
    {
        this.list = (this.listValue == OPER_ALLOW ? true : false);
        this.upload = (this.uploadValue == OPER_ALLOW ? true : false);
        this.download = (this.downloadValue == OPER_ALLOW ? true : false);
        this.preview = (this.previewValue == OPER_ALLOW ? true : false);
        this.delete = (this.deleteValue == OPER_ALLOW ? true : false);
        this.edit = (this.editValue == OPER_ALLOW ? true : false);
        this.publishLink = (this.publishLinkValue == OPER_ALLOW ? true : false);
        this.author = (this.authorValue == OPER_ALLOW ? true : false);
        
        return this;
    }
    
    public int getPreviewValue()
    {
        return previewValue;
    }
    
    public void getPriviledge(ResourceRole role)
    {
        this.listValue = (int) (role.getACL() % 10);
        this.uploadValue = (int) (role.getACL() % 100) / 10;
        this.downloadValue = (int) (role.getACL() % 1000) / 100;
        this.previewValue = (int) (role.getACL() % 10000) / 1000;
        this.deleteValue = (int) (role.getACL() % 100000) / 10000;
        this.editValue = (int) (role.getACL() % 1000000) / 100000;
        this.publishLinkValue = (int) (role.getACL() % 10000000) / 1000000;
        this.authorValue = (int) (role.getACL() % 100000000) / 10000000;
    }
    
    public int getUploadValue()
    {
        return uploadValue;
    }
    
    public boolean isAuthor()
    {
        return author;
    }
    
    public boolean isDelete()
    {
        return delete;
    }
    
    public boolean isDownload()
    {
        return download;
    }
    
    public boolean isEdit()
    {
        return edit;
    }
    
    public boolean isPublishLink()
    {
        return publishLink;
    }
    
    public boolean isList()
    {
        return list;
    }
    
    public boolean isPreview()
    {
        return preview;
    }
    
    public boolean isUpload()
    {
        return upload;
    }
    
    public void setAuthor(boolean author)
    {
        this.author = author;
    }
    
    public void setAuthorValue(int authorValue)
    {
        this.authorValue = authorValue;
        this.author = (this.authorValue == OPER_ALLOW ? true : false);
    }
    
    public void setDelete(boolean delete)
    {
        this.delete = delete;
    }
    
    public void setDeleteValue(int deleteValue)
    {
        this.deleteValue = deleteValue;
        this.delete = (this.deleteValue == OPER_ALLOW ? true : false);
    }
    
    public void setDownload(boolean download)
    {
        this.download = download;
    }
    
    public void setDownloadValue(int downloadValue)
    {
        this.downloadValue = downloadValue;
        this.download = (this.downloadValue == OPER_ALLOW ? true : false);
    }
    
    public void setEdit(boolean edit)
    {
        this.edit = edit;
    }
    
    public void setEditValue(int editValue)
    {
        this.editValue = editValue;
        this.edit = (this.editValue == OPER_ALLOW ? true : false);
    }
    
    public void setPublishLink(boolean publishLink)
    {
        this.publishLink = publishLink;
    }
    
    public void setPublishLinkValue(int publishLinkValue)
    {
        this.publishLinkValue = publishLinkValue;
        this.publishLink = (this.publishLinkValue == OPER_ALLOW ? true : false);
    }
    
    
    public void setList(boolean list)
    {
        this.list = list;
    }
    
    public void setListValue(int listValue)
    {
        this.listValue = listValue;
        this.list = (this.listValue == OPER_ALLOW ? true : false);
    }
    
    public void setPreview(boolean preview)
    {
        this.preview = preview;
    }
    
    public void setPreviewValue(int previewValue)
    {
        this.previewValue = previewValue;
        this.preview = (this.previewValue == OPER_ALLOW ? true : false);
    }
    
    public void setUpload(boolean upload)
    {
        this.upload = upload;
    }
    
    public void setUploadValue(int uploadValue)
    {
        this.uploadValue = uploadValue;
        this.upload = (this.uploadValue == OPER_ALLOW ? true : false);
    }
}

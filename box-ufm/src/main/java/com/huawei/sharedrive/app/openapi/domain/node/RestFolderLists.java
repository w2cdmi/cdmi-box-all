package com.huawei.sharedrive.app.openapi.domain.node;

import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class RestFolderLists {
    private List<RestFileInfo> files;

    private List<RestFolderInfo> folders;

    private int limit;

    private long offset;

    private int totalCount;
    
    private MessageSource messageSource;
    
    private Locale locale;

    public RestFolderLists() {
        files = new ArrayList<>();
        folders = new ArrayList<>();
    }

    public RestFolderLists(FileINodesList relist, int clientType,MessageSource messageSource,Locale locale) {
    	this.messageSource=messageSource;
        this.locale=locale;
        this.addToFiles(relist.getFiles(), clientType);
        this.addToFolders(relist.getFolders());
        this.setTotalCount(relist.getTotalCount());
        this.setLimit(relist.getLimit());
        this.setOffset(relist.getOffset());
       
    }

    public void addToFiles(List<INode> files, int clientType) {
        if (null == files) {
            return;
        }

        this.files = new ArrayList<RestFileInfo>(files.size());
        RestFileInfo fileInfo = null;
        for (INode inode : files) {
            fileInfo = new RestFileInfo(inode, clientType);
            this.files.add(fileInfo);
        }

    }

    public void addToFolders(List<INode> folders) {
        if (null == folders) {
            return;
        }
        this.folders = new ArrayList<RestFolderInfo>(folders.size());
        RestFolderInfo folderInfo = null;
        for (INode inode : folders) {
            folderInfo = new RestFolderInfo(inode);
            if(inode.getType()==INode.TYPE_INBOX){
                locale =  new Locale("zh", "CN");
            	folderInfo.setName(messageSource.getMessage(inode.getName(), null, locale));
                folderInfo.setName("来自:收件箱");
            }
            this.folders.add(folderInfo);
        }
    }

    public List<RestFileInfo> getFiles() {
        return files;
    }

    public List<RestFolderInfo> getFolders() {
        return folders;
    }

    public int getLimit() {
        return limit;
    }

    public long getOffset() {
        return offset;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setFiles(List<RestFileInfo> files) {
        this.files = files;
    }

    public void setFolders(List<RestFolderInfo> folders) {
        this.folders = folders;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}

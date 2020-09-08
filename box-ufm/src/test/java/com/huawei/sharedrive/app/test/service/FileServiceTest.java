/**
 * 
 */
package com.huawei.sharedrive.app.test.service;

import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.domain.FileINodesList;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectUpdateInfo;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.files.service.FolderService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderLists;
import com.huawei.sharedrive.app.test.other.AbstractSpringTest;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import pw.cdmi.box.domain.Limit;

;

public class FileServiceTest extends AbstractSpringTest
{
    @Qualifier("fileService")
    @Autowired
    private FileService fileservice;
    
    @Autowired
    private UserService userservice;
    
    @Autowired
    private FolderService folderservice;
    
    
    @Test
    public void updateObjectInfo() throws BaseRunException
    {
        // User user = userservice.AuthUserByLoginName("chenkeyun@huawei.com", "123456");
        //
        // System.out.println(ToStringBuilder.reflectionToString(user));
        // INode filenode = new INode();
        // filenode.setOwnedBy(user.getId());
        // filenode.setEncrypted(false);
        // filenode.setParentId(user.getId());
        // filenode.setName("myfile1231");
        // // PreUploadRSP rsp = fileservice.preUploadFile(user, filenode, false);
        //
        ObjectUpdateInfo objectupdateinfo = new ObjectUpdateInfo();
        
        objectupdateinfo.setObjectId("cd2b8a5b833fb608123456");
        objectupdateinfo.setSha1("dfdf");
        objectupdateinfo.setLength(123);
        objectupdateinfo.setStoragePath("");
        //objectupdateinfo.setOwner_id(123456);
        fileservice.updateObjectInfo(objectupdateinfo);
        
        // System.out.println(ToStringBuilder.reflectionToString(rsp));
        
    }
    
    @Test
    public void listSub()
    {
        
        User user = new User();
        user.setId(123456);
        INode inode = new INode();
        inode.setOwnedBy(123456);
        inode.setId(29199014L);
        inode.setStatus(INode.STATUS_NORMAL);
        
        Limit limit = new Limit();
        limit.setLength(100);
        limit.setOffset(0L);
        
        try
        {
            FileINodesList list = folderservice.listNodesbyFilter(new UserToken(), inode, null,  null);
            
            for (INode node : list.getFiles())
            {
                System.out.println(ToStringBuilder.reflectionToString(node));
            }
            
            for (INode node : list.getFolders())
            {
                System.out.println(ToStringBuilder.reflectionToString(node));
            }
            
            RestFolderLists folderList = new RestFolderLists(list, user.getType(), null, null);
            
            logger.info(ToStringBuilder.reflectionToString(folderList));
            
        }
        catch (BaseRunException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void getFileDownloadUrl() throws BaseRunException
    {
        // User user = userservice.AuthUserByLoginName("chenkeyun@huawei.com", "123456");
        //
        // System.out.println(ToStringBuilder.reflectionToString(user));
        // INode filenode = new INode();
        // filenode.setOwnedBy(user.getId());
        // filenode.setEncrypted(false);
        // filenode.setParentId(user.getId());
        // filenode.setName("myfile1231");
        // // PreUploadRSP rsp = fileservice.preUploadFile(user, filenode, false);
        //
        User user = new User();
        user.setId(123456);
        INode inode = new INode();
        inode.setOwnedBy(123456);
        inode.setId(29199014L);
        inode.setStatus(INode.STATUS_NORMAL);
        String url = fileservice.getFileDownloadUrl(new UserToken(), 123456L, 24630258L);
        
        System.out.println(ToStringBuilder.reflectionToString(url));
        
    }
    
    
}


/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.file.dao.FileObjectDao;
import pw.cdmi.file.domain.FileObject;
import pw.cdmi.file.service.FileObjectService;

/************************************************************
 * @Description:
 * <pre> 文件信息 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/

@Service
public class FileObjectServiceImpl implements FileObjectService {
    @Autowired
    private FileObjectDao fileObjectDao;

    @Override
    public FileObject get(String objectId) {
        return fileObjectDao.get(objectId);
    }
}


/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.engine;

import pw.cdmi.file.domain.FileObject;
import pw.cdmi.file.domain.FsEndpoint;
import pw.cdmi.file.model.S3FsEndpoint;
import pw.cdmi.file.model.S3FsObject;

/************************************************************
 * @Description:
 * <pre>云存储文件系统接口</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
public interface CloudFilesClient {
    public String getName();

    //下载URL
    public String getDownloadUrl(S3FsEndpoint endpoint, S3FsObject fsObject);
}

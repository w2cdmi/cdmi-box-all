
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.domain;

import java.io.Serializable;

/************************************************************
 * @Description:
 * <pre>云存储endpoint配置信息</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
public class FsEndpoint implements Serializable {
    private String id;
    private String endpoint;
    private String fsType;
    private int state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getFsType() {
        return fsType;
    }

    public void setFsType(String fsType) {
        this.fsType = fsType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

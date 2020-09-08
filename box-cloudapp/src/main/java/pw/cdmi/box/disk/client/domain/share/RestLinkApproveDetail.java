/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.box.disk.client.domain.share;

import pw.cdmi.box.disk.share.domain.INodeLinkApprove;
import pw.cdmi.box.disk.share.domain.INodeLinkApproveRecord;
import pw.cdmi.box.disk.share.domain.LinkAndNodeV2;

import java.util.List;


/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description:审批详情</pre>
 * @Project Alpha CDMI Service Platform, box-ufm Component. 2017/9/12
 ************************************************************/
public class RestLinkApproveDetail {
    private LinkAndNodeV2 linkAndNode;
    private INodeLinkApprove linkApprove;
    private List<INodeLinkApproveRecord> approveRecordList;

    public LinkAndNodeV2 getLinkAndNode() {
        return linkAndNode;
    }

    public void setLinkAndNode(LinkAndNodeV2 linkAndNode) {
        this.linkAndNode = linkAndNode;
    }

    public INodeLinkApprove getLinkApprove() {
        return linkApprove;
    }

    public void setLinkApprove(INodeLinkApprove linkApprove) {
        this.linkApprove = linkApprove;
    }

    public List<INodeLinkApproveRecord> getApproveRecordList() {
        return approveRecordList;
    }

    public void setApproveRecordList(List<INodeLinkApproveRecord> approveRecordList) {
        this.approveRecordList = approveRecordList;
    }
}

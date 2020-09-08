package pw.cdmi.box.disk.client.domain.node;

/**
 * 
 * Desc  : 文件标签绑定请求参数信息
 * Author: 77235
 * Date	 : 2016年11月29日
 */
public class RestFileLabelRequest {

    /** 默认未绑定标签编号 */
    public static final long DEFAULT_UNBIND_LABELID = -1;
    /** 文件标签编号 */
    private Long labelId = DEFAULT_UNBIND_LABELID;
    /** 标签名称 */
    private String labelName = "";
    /** 文件实体编号 */
    private long nodeId = DEFAULT_UNBIND_LABELID;
    /** 绑定者 */
    private long bindUserId = DEFAULT_UNBIND_LABELID;
    /** 企业编号 */
    private long enterpriseId = DEFAULT_UNBIND_LABELID;
    /** 绑定类型 */
    private int bindType = 0;
    /** 虚拟用户编号 */
    private long  ownerId= DEFAULT_UNBIND_LABELID;
    
    
    public long getLabelId() {
        return labelId;
    }
    
    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }
    
    public String getLabelName() {
        return labelName;
    }
    
    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
    
    public long getBindUserId() {
        return bindUserId;
    }
    
    public void setBindUserId(long bindUserId) {
        this.bindUserId = bindUserId;
    }

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public int getBindType() {
        return bindType;
    }

    public void setBindType(int bindType) {
        this.bindType = bindType;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }
    
    
}

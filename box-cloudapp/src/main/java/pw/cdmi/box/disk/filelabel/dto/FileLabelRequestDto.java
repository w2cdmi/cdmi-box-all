package pw.cdmi.box.disk.filelabel.dto;

/**
 * 
 * Desc  : 业务数据传输对象
 * Author: 77235
 * Date	 : 2016年11月28日
 */
public class FileLabelRequestDto{
    /** 我的文件夹绑定类型 */
    public static final int CONST_MY_FOLDER_BIND_TYPE = 1;
    /** 团队空间绑定类型 */
    public static final int CONST_TEAMSPACE_BIND_TYPE = 2;
    /** 文件标签编号 */
    private long labelId;
    /** 标签名称 */
    private String labelName;
    /** 文件实体编号 */
    private long nodeId;
    /** 绑定类型 1:我的文件夹 2:团队空间 */
    private int bindType;
    /** 虚拟用户编号 */
    private long  ownerId;

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
    
}

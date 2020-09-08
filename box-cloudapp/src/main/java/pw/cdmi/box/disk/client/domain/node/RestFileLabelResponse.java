package pw.cdmi.box.disk.client.domain.node;

import java.io.Serializable;

/**
 * 
 * Desc  : 绑定文件标签响应
 * Author: 77235
 * Date	 : 2016年11月29日
 */
public class RestFileLabelResponse implements Serializable{
    
    private static final long serialVersionUID = 1L;
    /** 文件标签编号 */
    private long labelId;

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }
}

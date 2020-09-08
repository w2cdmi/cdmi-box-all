package pw.cdmi.box.app.convertservice.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.sql.Timestamp;

import java.util.List;


public class TaskBean {
    private String objectId;
    private String taskId;
    private String owneId;
    private Integer level;
    private Integer status;
    private Integer percent;
    private String imageObjectId;
    private List<String> objectIds;
    private int taskSize;
    private Integer retryCount;
    private String fileName;
    private Timestamp convertTime;
    private String resourceGroupId;
    private List<String> taskIds;
    private String OrderyBy;
    private String pageSize;
    private String currPage;
    private String csIp;
    private Timestamp convertBeginTime;
    private Timestamp convertEndTime;
    private int bigFileFlag;
    private int destFileFlag;
    private long inodeId;

    public String getImageObjectId() {
        return imageObjectId;
    }

    public void setImageObjectId(String imageObjectId) {
        this.imageObjectId = imageObjectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<String> getObjectIds() {
        return objectIds;
    }

    public void setObjectIds(List<String> objectIds) {
        this.objectIds = objectIds;
    }

    public Timestamp getConvertTime() {
        return convertTime;
    }

    public void setConvertTime(Timestamp convertTime) {
        this.convertTime = convertTime;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }

    public String getOwneId() {
        return owneId;
    }

    public void setOwneId(String owneId) {
        this.owneId = owneId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    public String getOrderyBy() {
        return OrderyBy;
    }

    public void setOrderyBy(String orderyBy) {
        OrderyBy = orderyBy;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getCurrPage() {
        return currPage;
    }

    public void setCurrPage(String currPage) {
        this.currPage = currPage;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getCsIp() {
        return csIp;
    }

    public void setCsIp(String csIp) {
        this.csIp = csIp;
    }

    public Timestamp getConvertBeginTime() {
        return convertBeginTime;
    }

    public void setConvertBeginTime(Timestamp convertBeginTime) {
        this.convertBeginTime = convertBeginTime;
    }

    public Timestamp getConvertEndTime() {
        return convertEndTime;
    }

    public void setConvertEndTime(Timestamp convertEndTime) {
        this.convertEndTime = convertEndTime;
    }

    public int getBigFileFlag() {
        return bigFileFlag;
    }

    public void setBigFileFlag(int bigFileFlag) {
        this.bigFileFlag = bigFileFlag;
    }

    public int getDestFileFlag() {
        return destFileFlag;
    }

    public void setDestFileFlag(int destFileFlag) {
        this.destFileFlag = destFileFlag;
    }

    public long getInodeId() {
        return inodeId;
    }

    public void setInodeId(long inodeId) {
        this.inodeId = inodeId;
    }
}

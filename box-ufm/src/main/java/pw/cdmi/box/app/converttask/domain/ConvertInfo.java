package pw.cdmi.box.app.converttask.domain;

import java.util.List;

import pw.cdmi.box.app.convertservice.domain.TaskBean;


public class ConvertInfo {
    private List<TaskBean> convertTasks;
    private int totalCount;
    private int totalPage;

    public ConvertInfo() {
    }

    public ConvertInfo(List<TaskBean> convertTasks) {
        this.convertTasks = convertTasks;
    }

    public ConvertInfo(List<TaskBean> convertTasks, int totalCount, int size) {
        this.convertTasks = convertTasks;
        this.totalCount = totalCount;
        this.totalPage = ((totalCount - 1) / size) + 1;
    }

    public List<TaskBean> getConvertTasks() {
        return convertTasks;
    }

    public void setConvertTasks(List<TaskBean> convertTasks) {
        this.convertTasks = convertTasks;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}

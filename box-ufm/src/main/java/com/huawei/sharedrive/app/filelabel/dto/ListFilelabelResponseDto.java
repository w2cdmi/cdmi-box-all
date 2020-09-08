package com.huawei.sharedrive.app.filelabel.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;

/**
 * 
 * Desc  : 返回集合
 * Author: 77235
 * Date	 : 2016年12月7日
 */
public class ListFilelabelResponseDto<T extends BaseFileLabelInfo> extends BaseFilelabelResponseDto {

    private static final long serialVersionUID = 1L;
    
    private List<T> fileLabelList;
    
    private long totalCount;
    
    private int currPage;
    
    private int pageSize;
    
    private int totalPageSize;

    public List<? extends BaseFileLabelInfo> getFileLabelList() {
        return fileLabelList;
    }

    public void setFileLabelList(List<T> fileLabelList) {
        this.fileLabelList = fileLabelList;
    }

    public ListFilelabelResponseDto() {
        super();
    }
    
    public ListFilelabelResponseDto(HttpStatus status) {
        super(status);
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPageSize() {
        return totalPageSize;
    }

    public void setTotalPageSize(int totalPageSize) {
        this.totalPageSize = totalPageSize;
    }
}

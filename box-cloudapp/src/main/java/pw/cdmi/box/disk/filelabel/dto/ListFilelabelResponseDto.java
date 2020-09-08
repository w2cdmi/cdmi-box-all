package pw.cdmi.box.disk.filelabel.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * 
 * Desc  : 返回集合
 * Author: 77235
 * Date	 : 2016年12月7日
 */
public class ListFilelabelResponseDto extends BaseFilelabelResponseDto {

    private static final long serialVersionUID = 1L;
    
    private List<BaseFileLabelInfo> fileLabelList;
    
    private long totalCount;
    
    private int currPage;
    
    private int pageSize;
    
    private int totalPageSize;

    public List<BaseFileLabelInfo> getFileLabelList() {
        return fileLabelList;
    }

    public void setFileLabelList(List<BaseFileLabelInfo> fileLabelList) {
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

package com.huawei.sharedrive.app.filelabel.dto;

/**
 * 
 * Desc  : 查询条件
 * Author: 77235
 * Date	 : 2016年11月29日
 */
public abstract class BaseQueryCondition {
    /** 默认每页显示数据条数 */
    private static final int CONST_DEFAULT_PAGE_SIZE = 10;
    /** 默认页数 */
    private static final int CONST_DEFAULT_PAGE_NUM = 1;
    /** 每页展示数量 */
    private int pageSize = CONST_DEFAULT_PAGE_SIZE;
    /** 当前页数:从第1页开始 */
    private int currPage = CONST_DEFAULT_PAGE_NUM;
    /** 总页数 */
    private int totalPage;
    /** 总记录数 */
    private long totalCount;
    /** 开始行数 */
    private int startRow;
    /** 结束行数 */
    private int endRow;
    /** 查询的字段信息 */
    private String fields;
    
    
    public BaseQueryCondition(int pageNum){
        this(pageNum, CONST_DEFAULT_PAGE_SIZE);
    }
    
    public BaseQueryCondition(int currPage, int pageSize){
        this.currPage = currPage;
        this.pageSize = pageSize;
        recalculat();
    }

    private void recalculat() {
        this.startRow = (this.currPage - 1)  * this.pageSize;
        this.endRow = this.startRow + this.pageSize - 1;
    }

    public int getPageSize() {
        return pageSize;
    }
    
    public BaseQueryCondition setPageSize(int pageSize) {
        this.pageSize = pageSize;
        recalculat();
        
        return this;
    }
    
    public int getStartRow() {
        return startRow;
    }
    
    public BaseQueryCondition setStartRow(int startRow) {
        this.startRow = startRow;
        return this;
    }
    
    public int getEndRow() {
        return endRow;
    }
    
    public BaseQueryCondition setEndRow(int endRow) {
        this.endRow = endRow;
        return this;
    }
    
    public String getFields() {
        return fields;
    }
    
    public BaseQueryCondition setFields(String fields) {
        this.fields = fields;
        return this;
    }

    public int getCurrPage() {
        return currPage;
    }

    public BaseQueryCondition setCurrPage(int currPage) {
        this.currPage = currPage;
        recalculat();
        
        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public BaseQueryCondition setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        
        return this;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public BaseQueryCondition setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        
        return this;
    }
    
    
}

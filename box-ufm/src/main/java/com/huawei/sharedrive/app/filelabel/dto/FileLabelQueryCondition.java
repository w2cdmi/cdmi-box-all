package com.huawei.sharedrive.app.filelabel.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Desc  : 文件标签查询条件
 * Author: 77235
 * Date	 : 2016年11月29日
 */
public class FileLabelQueryCondition extends BaseQueryCondition {
    /** 主键查询条件 */
    private Long id;
    /** 企业编号查询条件 */
    private Long enterpriseId;
    /** 文件标签名称条件 */
    private String labelName;
    /** 文件标签名称条件查询方式 */
    private boolean labelNameLike;
    /** 標簽類型 ：1：個人文件 2：團隊空間*/
    private int labelType;
    /** 當前用戶編號 */
    private long createBy;
    /** 當前虛擬用戶編號 */
    private long ownerId;
    /** 排序字段 */
    private List<OrderField> orderFields ;
    
    /**
     * 根据主键排序
     * @param isAsc
     * @return
     */
    public FileLabelQueryCondition orderById(boolean isAsc){
        checkOrderFieldsIsNull();
        
        orderFields.add(new OrderField("id", isAsc ? "asc" : "desc"));
        return this;
    }
    
    /**
     * 根据标签名称排序
     * @param isAsc
     * @return
     */
    public FileLabelQueryCondition orderByLabelName(boolean isAsc){
        checkOrderFieldsIsNull();
        
        orderFields.add(new OrderField("labelName", isAsc ? "asc" : "desc"));
        return this;
    }

    private void checkOrderFieldsIsNull() {
        if (orderFields == null){
            orderFields = new ArrayList<OrderField>(2);
        }
    }
    
    public FileLabelQueryCondition(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }
    
    public Long getId() {
        return id;
    }
    
    public FileLabelQueryCondition setId(Long id) {
        this.id = id;
        return this;
    }
    
    public Long getEnterpriseId() {
        return enterpriseId;
    }
    
    public FileLabelQueryCondition setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
        return this;
    }
    
    public String getLabelName() {
        return labelName;
    }
    
    public FileLabelQueryCondition setLabelName(String labelName) {
        this.labelName = labelName;
        return this;
    }
    
    public boolean isLabelNameLike() {
        return labelNameLike;
    }
    
    public FileLabelQueryCondition setLabelNameLike(boolean labelNameLike) {
        this.labelNameLike = labelNameLike;
        return this;
    }

    public int getLabelType() {
        return labelType;
    }

    public FileLabelQueryCondition setLabelType(int labelType) {
        this.labelType = labelType;
        return this;
    }
    
    public long getCreateBy() {
        return createBy;
    }

    public FileLabelQueryCondition setCreateBy(long createBy) {
        this.createBy = createBy;
        return this;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public FileLabelQueryCondition setOwnerId(long ownerId) {
        this.ownerId = ownerId;
        return this;
    }



    /**
     * 
     * Desc  : 排序处理
     */
    class OrderField{
        /** 排序字段名 */
        private String filedName;
        /** 排序方式 */
        private String orderType;
        
        OrderField(String filedName, String orderType){
            this.filedName = filedName;
            this.orderType = orderType;
        }
        
        public OrderField setFiledName(String filedName) {
            this.filedName = filedName;
            return this;
        }

        public OrderField setOrderType(String orderType) {
            this.orderType = orderType;
            return this;
        }

        public String getFiledName() {
            return filedName;
        }

        public String getOrderType() {
            return orderType;
        }
    }
}

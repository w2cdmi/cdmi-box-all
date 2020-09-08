package com.huawei.sharedrive.app.openapi.domain.node;

import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import pw.cdmi.box.domain.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 列举目录请求对象
 *
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-4
 * @see
 * @since
 */
public class ListFolderRequest {

    private static final int DEFAULT_LIMIT = 100;

    private static final long DEFAULT_OFFSET = 0L;

    private static final int MAX_LIMIT = 1000;

    // 最多可支持生成的缩略图个数
    private static final int MAX_THUMBNAIL_SIZE = 5;

    // 查询条数
    private Integer limit;
    // 偏移量
    private Long offset;

    private List<Order> order;

    private List<Thumbnail> thumbnail;

    private Boolean withExtraType;

    private Integer docType;
    
    private Byte type;
    
    private Boolean withPath;
    
    private String name;
    
    /** 新增查询条件字段：标签编号列表 */
    private String labelIds;
    
    private Long createdBy;
    
    

    public ListFolderRequest() {
        limit = DEFAULT_LIMIT;
        offset = DEFAULT_OFFSET;
    }

    public ListFolderRequest(Integer limit, Long offset) {
        this.limit = limit != null ? limit : DEFAULT_LIMIT;
        this.offset = offset != null ? offset : DEFAULT_OFFSET;
    }

    public void addOrder(Order orderV2) {
        if (orderV2 == null) {
            return;
        }
        if (order == null) {
            order = new ArrayList<Order>(BusinessConstants.INITIAL_CAPACITIES);
        }
        order.add(orderV2);
    }

    public void addThumbnail(Thumbnail thumb) {
        if (thumb == null) {
            return;
        }
        if (thumbnail == null) {
            thumbnail = new ArrayList<Thumbnail>(BusinessConstants.INITIAL_CAPACITIES);
        }
        thumbnail.add(thumb);
    }

    public void checkParameter() throws InvalidParamException {
        if (withExtraType == null) {
            withExtraType = Boolean.TRUE;
        }

        if (limit != null && (limit < 1 || limit > MAX_LIMIT)) {
            throw new InvalidParamException();
        }
        if (offset != null && offset < 0) {
            throw new InvalidParamException();
        }

        if (order != null) {
            for (Order temp : order) {
                temp.checkParameter();
            }
        }

        if (thumbnail != null) {
            if (thumbnail.size() > MAX_THUMBNAIL_SIZE) {
                throw new InvalidParamException();
            }
            for (Thumbnail temp : thumbnail) {
                temp.checkParameter();
            }
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public Long getOffset() {
        return offset;
    }

    public List<Order> getOrder() {
        return order;
    }

    public List<Thumbnail> getThumbnail() {
        return thumbnail;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public void setOrder(List<Order> order) {
        this.order = order;
    }

    public void setThumbnail(List<Thumbnail> thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Boolean getWithExtraType() {
        return withExtraType == null ? Boolean.TRUE : withExtraType;
    }

    public void setWithExtraType(Boolean withExtraType) {
        this.withExtraType = withExtraType;
    }

	public Integer getDocType() {
		return docType;
	}

	public void setDocType(Integer docType) {
		this.docType = docType;
	}


	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Boolean getWithPath() {
		return withPath;
	}

	public void setWithPath(Boolean withPath) {
		this.withPath = withPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabelIds() {
		return labelIds;
	}

	public void setLabelIds(String labelIds) {
		this.labelIds = labelIds;
	}
	
	
	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public INode tran2Filter(){
		INode filter=new INode();
		if(this.docType!=null){
			filter.setDoctype(this.docType);
		}
		if(this.type!=null){
			filter.setType(this.type);
		}
		if(this.name!=null&&!"".equals(name)){
			filter.setName(labelIds);
		}
		if(this.labelIds!=null&&!"".equals(labelIds)){
			filter.setFilelabelIds(labelIds);
		}
		
		if(this.createdBy!=null&&!"".equals(createdBy)){
			filter.setCreatedBy(createdBy);
		}
		
		return filter;
	}
    
}

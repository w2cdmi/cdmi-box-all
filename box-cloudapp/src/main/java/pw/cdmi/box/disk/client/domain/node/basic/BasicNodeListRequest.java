package pw.cdmi.box.disk.client.domain.node.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.exception.InvalidParamException;

public class BasicNodeListRequest
{
    
    public BasicNodeListRequest(Integer limit, Long offset, List<Order> order, List<Thumbnail> thumbnail)
    {
        this(limit, offset);
        this.order = order;
        this.thumbnail = thumbnail; 
    }
    
    public void addOrder(Order orderV2)
    {
        if (orderV2 == null)
        {
            return;
        }
        if (order == null)
        {
            order = new ArrayList<Order>(SIZE_ORDER);
        }
        order.add(orderV2);
    }
    
    private final static int SIZE_ORDER = 3;
    
    public void addThumbnail(Thumbnail thumb)
    {
        if (thumb == null)
        {
            return;
        }
        if (thumbnail == null)
        {
            thumbnail = new ArrayList<Thumbnail>(SIZE_ORDER);
        }
        thumbnail.add(thumb);
    }
    
    public BasicNodeListRequest()
    {
        this.setLimit(DEFAULT_LIMIT);
        this.setOffset(DEFAULT_OFFSET);
    }
    
    private static final int DEFAULT_LIMIT = 100;
    
    private static final long DEFAULT_OFFSET = 0L;
    
    public BasicNodeListRequest(Integer limit, Long offset)
    {
        if(null != limit)
        {
            this.setLimit(limit);
        }
        else
        {
            this.setLimit(DEFAULT_LIMIT);
        }
        if(null == offset)
        {
            this.setOffset(DEFAULT_OFFSET);
        }
        else
        {
            this.setOffset(offset);
        }
    }
    
    private static final int MAX_LIMIT = 1000;
    
    private static final int MAX_THUMBNAIL_SIZE = 5;
    
    private Integer limit;
    
    private String name;
    
    private Long offset;

    private List<Order> order;

    private List<Thumbnail> thumbnail;

    /** 新增查询条件字段：标签编号列表 */
    private String labelIds;
    /** 新增查询条件字段：文档类型 */
    private String docType;
    /** 新增查询条件字段：搜索类型;0:普通搜索  1:高级搜索 */

    private Byte type;

    private Boolean withPath;

    
	public void checkParameter() throws InvalidParamException
    {
        if (limit != null && (limit < 0 || limit > MAX_LIMIT))
        {
            throw new InvalidParamException();
        }
        if (offset != null && offset < 0)
        {
            throw new InvalidParamException();
        }
        
        if (StringUtils.isBlank(name) || name.length() > 255)
        {
            throw new InvalidParamException();
        }
        
        if (order != null)
        {
            for (Order temp : order)
            {
                temp.checkParameter();
            }
        }
        
        if (thumbnail != null)
        {
            if (thumbnail.size() > MAX_THUMBNAIL_SIZE)
            {
                throw new InvalidParamException();
            }
            for (Thumbnail temp : thumbnail)
            {
                temp.checkParameter();
            }
        }
    }

    public Integer getLimit()
    {
        return limit;
    }

    public String getName()
    {
        return name;
    }

    public Long getOffset()
    {
        return offset;
    }

    public List<Order> getOrder()
    {
        return order;
    }

    public List<Thumbnail> getThumbnail()
    {
        return thumbnail;
    }

    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    public void setOrder(List<Order> order)
    {
        this.order = order;
    }
    
    public void setThumbnail(List<Thumbnail> thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public String getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(String labelIds) {
        this.labelIds = labelIds;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
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
}

package pw.cdmi.box.isystem.menu.domain;

import pw.cdmi.box.domain.PageRequest;

/**
 * 查询条件
 */
public class QueryCondition
{
    /** 分页参数 */
    private PageRequest pageRequest;
    

	public PageRequest getPageRequest()
	{
		return pageRequest;
	}

	public void setPageRequest(PageRequest pageRequest)
	{
		this.pageRequest = pageRequest;
	}
    
    
}

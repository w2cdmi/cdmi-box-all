package pw.cdmi.box.disk.client.domain.node;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *  請查看ufm工程對應的PreviewMetaResponse類說明
 */

public class PreviewMetaResponse
{
    private boolean previewSupport;
    private String  totalPages;
    private String  range;
	private long inodeSize = 0;  
	private long maxSize = 0;
    /**
     * 返回码
     */
    private String resultCode = "00000000";

    public boolean isPreviewSupport()
    {
        return previewSupport;
    }

    public void setPreviewSupport(boolean previewSupport)
    {
        this.previewSupport = previewSupport;
    }

    public String getTotalPages()
    {
        return totalPages;
    }

    public void setTotalPages(String totalPages)
    {
        this.totalPages = totalPages;
    }

    public String getRange()
    {
        return range;
    }

    public void setRange(String range)
    {
        this.range = range;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

	public long getInodeSize() {
		return inodeSize;
	}

	public void setInodeSize(long inodeSize) {
		this.inodeSize = inodeSize;
	}

	public long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}
}

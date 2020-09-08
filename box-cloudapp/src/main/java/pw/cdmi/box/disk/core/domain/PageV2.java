package pw.cdmi.box.disk.core.domain;

import java.util.List;

public class PageV2<E>
{
    private List<E> data;
    
    private Long page;
    
    private int numOfPage;
    
    private Long totalNums;
    
    private Long totalPage;
    
    private int currentNum;
    
    public List<E> getData()
    {
        return data;
    }
    
    public void setData(List<E> data)
    {
        this.data = data;
    }
    
    public Long getPage()
    {
        return page;
    }
    
    public void setPage(Long page)
    {
        this.page = page;
    }
    
    public int getNumOfPage()
    {
        return numOfPage;
    }
    
    public void setNumOfPage(int numOfPage)
    {
        this.numOfPage = numOfPage;
    }
    
    public Long getTotalNums()
    {
        return totalNums;
    }
    
    public void setTotalNums(Long totalNums)
    {
        this.totalNums = totalNums;
    }
    
    public Long getTotalPage()
    {
        return totalPage;
    }
    
    public void setTotalPage(Long totalPage)
    {
        this.totalPage = totalPage;
    }
    
    public int getCurrentNum()
    {
        return currentNum;
    }
    
    public void setCurrentNum(int currentNum)
    {
        this.currentNum = currentNum;
    }
    
}

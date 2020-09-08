package pw.cdmi.box.disk.converttask.client.domain;

import java.util.List;



public class ConvertInfo
{
    
    
    
    private List<TaskBean> convertTasks;
    
    private int totalCount;
    
    private int totalPage;
    
   
    
    public ConvertInfo()
    {
        
    }



	public List<TaskBean> getConvertTasks() {
		return convertTasks;
	}



	public void setConvertTasks(List<TaskBean> convertTasks) {
		this.convertTasks = convertTasks;
	}



	public ConvertInfo(List<TaskBean> convertTasks) {
		this.convertTasks = convertTasks;
	}



	public int getTotalCount() {
		return totalCount;
	}



	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}



	public ConvertInfo(List<TaskBean> convertTasks, int totalCount) {
		this.convertTasks = convertTasks;
		this.totalCount = totalCount;
	}



	public int getTotalPage() {
		return totalPage;
	}



	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

    
    
}

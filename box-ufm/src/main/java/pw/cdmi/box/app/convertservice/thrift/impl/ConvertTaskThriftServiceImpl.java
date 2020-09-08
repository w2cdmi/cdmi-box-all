package pw.cdmi.box.app.convertservice.thrift.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;

import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.box.app.convertservice.thrift.Condition;
import pw.cdmi.box.app.convertservice.thrift.ConvertServiceException;
import pw.cdmi.box.app.convertservice.thrift.ImgObject;
import pw.cdmi.box.app.convertservice.thrift.TaskInfo;
import pw.cdmi.box.app.convertservice.thrift.ConvertTaskThriftService.Iface;

public class ConvertTaskThriftServiceImpl implements Iface
{

	@Autowired
	private ConvertService convertService;

	@Override
	public List<TaskInfo> getTaskBeanList(Condition condition) throws ConvertServiceException, TException
	{
		 List<TaskInfo> taskInfoList = new ArrayList<TaskInfo>();
		TaskBean taskBean = null;
		if(condition != null)
		{
			taskBean = new TaskBean();
			taskBean.setTaskSize(condition.getTaskSize());
			taskBean.setObjectIds(condition.getObjectId());
			taskBean.setResourceGroupId(condition.getResourceGroupId());
			taskBean.setCsIp(condition.getCsIp());
			taskBean.setTaskSize(condition.getTaskSize());
		}
		List<TaskBean> queryTaskBean = convertService.getTaskBeanThriftList(taskBean);
		List<String> taskIds = new ArrayList<String>();
		TaskInfo temp = null;
		if(CollectionUtils.isNotEmpty(queryTaskBean))
		{
			for(TaskBean tempTaskBean : queryTaskBean)
			{
				temp = new TaskInfo();
				temp.setObjectId(tempTaskBean.getObjectId());
				temp.setImageObjectId(tempTaskBean.getImageObjectId());
				temp.setOwneId(tempTaskBean.getOwneId());
				temp.setStatus(Integer.valueOf(tempTaskBean.getStatus()));
				temp.setPercent(Integer.valueOf(tempTaskBean.getPercent()));
				temp.setLevel(Integer.valueOf(tempTaskBean.getLevel()));
				temp.setFileName(tempTaskBean.getFileName());
				temp.setTaskId(tempTaskBean.getTaskId());
				temp.setResourceGroupId(tempTaskBean.getResourceGroupId());
				temp.setRetryCount(tempTaskBean.getRetryCount());
				
				taskIds.add(tempTaskBean.getTaskId());
				
				taskInfoList.add(temp);
			}
			
			convertService.updateTaskStatus(taskIds);
			
		}
		return taskInfoList;
	}

	@Override
	public String updateTask(TaskInfo taskInfo) throws ConvertServiceException, TException
	{
		TaskBean taskBean = new TaskBean();
		taskBean.setTaskId(taskInfo.getTaskId());
		taskBean.setObjectId(taskInfo.getObjectId());
		taskBean.setStatus(taskInfo.getStatus());
		taskBean.setPercent(taskInfo.getPercent());
		taskBean.setRetryCount(taskInfo.getRetryCount());
		taskBean.setResourceGroupId(taskInfo.getResourceGroupId());
		return convertService.updateTask(taskBean);
	}

	@Override
	public String addImgObject(ImgObject imgObject) throws ConvertServiceException, TException
	{
		pw.cdmi.box.app.convertservice.domain.ImgObject imgObjectBean = new pw.cdmi.box.app.convertservice.domain.ImgObject();
		imgObjectBean.setSourceObjectId(imgObject.getSourceObjectId());
		imgObjectBean.setImageObjectId(imgObject.getImageObjectId());
		imgObjectBean.setAccountId(imgObject.getOwneId());
		imgObjectBean.setTotalPages(imgObject.getTotalPages());
		imgObjectBean.setPageIndex(imgObject.getPageIdex());
		imgObjectBean.setResourceGroupId(imgObject.getResourceGroupId());
		imgObjectBean.setConvertTime(new Timestamp(System.currentTimeMillis()));
		convertService.deleteImageObject(imgObjectBean.getSourceObjectId());
		return convertService.addImage(imgObjectBean);
	}
	

}

package pw.cdmi.box.app.convertservice.service.impl;

import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.mirror.dao.MirrorObjectDAO;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.teamspace.dao.TeamSpaceDAO;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpace;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceAttribute;

import pw.cdmi.box.app.convertservice.dao.ImageDAO;
import pw.cdmi.box.app.convertservice.dao.TaskBeanDAO;
import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.app.convertservice.domain.NodeRunningInfo;
import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.convertservice.service.CSMonitorService;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.box.app.convertservice.util.ConvertPropertiesUtils;
import pw.cdmi.box.app.teamspaceattribute.dao.TeamSpaceAttributeDAO;
import pw.cdmi.core.utils.RandomGUID;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("convertService")
public class ConvertServiceImpl implements ConvertService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertServiceImpl.class);
    private static final int failedLevel = 9;
    @Autowired
    private ImageDAO imageDao;
    @Autowired
    private TaskBeanDAO taskBeanDao;
    @Autowired
    private TeamSpaceDAO teamSpaceDAO;
    @Autowired
    private TeamSpaceAttributeDAO teamSpaceAttributeDAO;
    @Autowired
    private INodeDAO iNodeDAO;
    private int resetTotal = Integer.parseInt(ConvertPropertiesUtils.getProperty(
                "convert.task.retrycount", "3",
                ConvertPropertiesUtils.BundleName.CONVERT));
    private long bigFileSize = Long.parseLong(ConvertPropertiesUtils.getProperty(
                "convert.task.bigFileSize", "100000",
                ConvertPropertiesUtils.BundleName.CONVERT));
    @Autowired
    private MirrorObjectDAO mirrorObjectDAO;
    private volatile Map<String, List<NodeRunningInfo>> nodeRunMap = new HashMap<String, List<NodeRunningInfo>>();
    @Autowired
    private CSMonitorService cSMonitorService;
    private Map<String, String> assignMap = new HashMap<String, String>();
    private Map<String, Integer> nodeCountMap = new HashMap<String, Integer>();

    /**
     * 转换文件任务-新增
     */
    @Override
	public String addTask(TaskBean task) {
		LOGGER.info("Enter addTask,task={}", task);
		
		try {
			// 根据ObjectId查询INode信息
			INode iNode = iNodeDAO.getINodeByObjectId(Long.parseLong(task.getOwneId()), task.getObjectId()).get(0);
			
			if (null != iNode) {
				LOGGER.info("iNode={},{},{}", ToStringBuilder.reflectionToString(iNode), iNode.getObjectId(), iNode.getOwnedBy());
				// 判断是否支持预览
				boolean isSupportPreview = isSupportPreview(iNode);
				
				if (!isSupportPreview) {
					LOGGER.info("File does not support previews");
					return "failed";
				}
				// 目前實現的是所有團隊空間的都支持轉換， 后期有需求再優化，沒有就可以不用了。
				int level = getLevel(task, iNode);
				if (failedLevel == level) {
					LOGGER.info("TeamSpac does not support previews");
					return "failed";
				}
				
				List<MirrorObject> list = mirrorObjectDAO.getBySrcObjectIds(iNode.getObjectId(), iNode.getOwnedBy());
				LOGGER.info("mirrorObject,list={}", ToStringBuilder.reflectionToString(list));
				
				for (int i = 0; i < (list.size() + 1); i++) {
					TaskBean taskBean = new TaskBean();
					
					if (i == 0) {
						taskBean.setObjectId(iNode.getObjectId());
						taskBean.setOwneId(String.valueOf(iNode.getOwnedBy()));
						taskBean.setResourceGroupId(String.valueOf(iNode.getResourceGroupId()));
						taskBean.setDestFileFlag(0);
						taskBean.setInodeId(iNode.getId());
					} else {
						MirrorObject mirrorObject = list.get(i - 1);
						taskBean.setObjectId(mirrorObject.getDestObjectId());
						taskBean.setOwneId(String.valueOf(mirrorObject.getOwnedBy()));
						taskBean.setResourceGroupId(String.valueOf(mirrorObject.getDestResourceGroupId()));
						taskBean.setDestFileFlag(1);
						taskBean.setInodeId(0);
					}
					
					taskBean.setLevel(level);
					addConvertTask(taskBean, iNode);
				}
				
				LOGGER.info("Exit addTask");
				
				return "successed";
			} else {
				LOGGER.info("File node does not exist");
				return "failed";
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "failed";
		}
	}

	private void addConvertTask(TaskBean taskBean, INode iNode) {
		try {
			LOGGER.info("Enter the addConvertTask,taskBean={}", taskBean);
			
			// 查询条件
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("objectId", taskBean.getObjectId());
			map.put("retryCount", resetTotal);
			
			// 验证ObjectId是否已在转换任务中以及是否已转换?
			int taskCount = taskBeanDao.checkTaskId(map);
			int imgCount = imageDao.checkImageObjectId(taskBean.getObjectId());
			
			if ((taskCount == 0) && (imgCount == 0)) {
				// 获取当前时间的时间戳
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				if ((nodeRunMap.get(taskBean.getResourceGroupId()) == null) || nodeRunMap.get(taskBean.getResourceGroupId()).isEmpty() || (null == assignMap.get(taskBean.getResourceGroupId()))) {
					NodeRunningInfo nodeRunningInfo = new NodeRunningInfo();
					nodeRunningInfo.setResourceGroupID(Integer.parseInt(taskBean.getResourceGroupId()));
					getNodeRuningInfo(nodeRunningInfo);
				}
				
				// 填充转换文件任务的其余属性
				taskBean.setFileName(iNode.getName());
				taskBean.setImageObjectId(new RandomGUID().getValueAfterMD5().toString());
				taskBean.setRetryCount(0);
				taskBean.setPercent(0);
				taskBean.setStatus(9);
				taskBean.setConvertTime(ts);
				taskBean.setTaskId(new RandomGUID(true).getValueAfterMD5());
				
				if (bigFileSize <= iNode.getSize()) {
					taskBean.setBigFileFlag(1);
					
					if (nodeCountMap.get(taskBean.getResourceGroupId()) > 1) {
						taskBean.setCsIp(assignMap.get(taskBean.getResourceGroupId()));
					}
				} else {
					taskBean.setBigFileFlag(0);
					
					if (0 != nodeCountMap.get(taskBean.getResourceGroupId())) {
						NodeRunningInfo nodeRunningInfo = nodeRunMap.get(taskBean.getResourceGroupId()).remove(0);
						
						if (nodeCountMap.get(taskBean.getResourceGroupId()) > 1) {
							if (assignMap.get(taskBean.getResourceGroupId()).equals(nodeRunningInfo.getHostIP())) {
								if (taskBeanDao.getAssignIpTaskCount(nodeRunningInfo.getHostIP()) > 0) {
									nodeRunMap.get(taskBean.getResourceGroupId()).add(nodeRunningInfo);
									nodeRunningInfo = nodeRunMap.get(taskBean.getResourceGroupId()).remove(0);
								}
							}
						}
						
						taskBean.setCsIp(nodeRunningInfo.getHostIP());
						nodeRunMap.get(taskBean.getResourceGroupId()).add(nodeRunningInfo);
					}
				}
				
				LOGGER.info("ufm.csip: " + taskBean.getCsIp());
				LOGGER.info("task={}", taskBean);
				taskBeanDao.addTask(taskBean);
				LOGGER.info("Exit the addConvertTask");
			} else {
				LOGGER.info("Conversion task already exists Or Converted completed");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

    private int getLevel(TaskBean task, INode iNode) {
    	int  level = 1; // 這里不做優先級處理，都以默認方式
    	/*
        int level = 0;
        TeamSpace teamSpace = teamSpaceDAO.get(Long.parseLong(task.getOwneId()));

        // 如果为null,(ownedBy)资源拥有者为个人,反之为团隊
        if (null == teamSpace) {
            level = 1;
        } else {
            //是否转换文件??
            TeamSpaceAttribute autopreviewInfo = new TeamSpaceAttribute();
            autopreviewInfo.setCloudUserId(iNode.getOwnedBy());
            autopreviewInfo.setName("autoPreview");

            //是否转换文件??  ?
            String autopreview = (teamSpaceAttributeDAO.select(autopreviewInfo) == null)
                ? "0" : teamSpaceAttributeDAO.select(autopreviewInfo).getValue();

            if ("0".equals(autopreview)) {
            	LOGGER.info("teamspace view failedLevel: " + autopreviewInfo.getValue());
                return failedLevel;
            }
            
            // 团队优先? 查询条件填充
            TeamSpaceAttribute priorityInfo = new TeamSpaceAttribute();
            priorityInfo.setCloudUserId(Long.parseLong(task.getOwneId()));
            priorityInfo.setName("priority");

            // 团队优先? ?
            String priority = (teamSpaceAttributeDAO.select(priorityInfo) == null)
                ? "1" : teamSpaceAttributeDAO.select(priorityInfo).getValue();

            level = Integer.parseInt(priority);
            
        }
		*/
        return level;
    }

    /**
     * 转换任务更新,主键taskId
     */
    @Override
    public String updateTask(TaskBean task) {
        LOGGER.info("Enter updateTask,task={}", task);

        try {
            if ((resetTotal == task.getRetryCount()) &&
                    (8 == task.getStatus())) {
                task.setStatus(99);
            }

            if (2 == task.getStatus()) {
                LOGGER.info(
                    "Beginning of the file conversion,update convertBeginTime");

                Timestamp ts = new Timestamp(System.currentTimeMillis());
                task.setConvertBeginTime(ts);
                taskBeanDao.updateConvertBeginTime(task);
            }

            if (0 == task.getStatus()) {
                LOGGER.info("End of File Conversion,update convertEndTime");

                Timestamp ts = new Timestamp(System.currentTimeMillis());
                task.setConvertEndTime(ts);
                taskBeanDao.updateConvertEndTime(task);
            }

            taskBeanDao.updateTask(task);

            if (0 == task.getStatus()) {
                LOGGER.info(
                    "The current task status is successful conversion,detele task");

                List<String> taskIds = new ArrayList<String>();
                taskIds.add(task.getTaskId());
                taskBeanDao.deleteTask(taskIds);
            }

            LOGGER.info("Exit the updateTask");

            return "successed";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            return "failed";
        }
    }

    /**
     * 转换任务删除，主键taskId,支持批量删除
     */
    @Override
    public String deleteTask(List<String> taskIds) {
        LOGGER.info("Enter deleteTask,taskIds={}", taskIds);

        try {
            taskBeanDao.deleteTask(taskIds);
            LOGGER.info("Exit the deleteTask");

            return "successed";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            return "failed";
        }
    }

    /**
     * 转换任务历史表删除，主键taskId,支持批量删除
     */
    @Override
    public String deleteHistoryTask(List<String> taskIds) {
        LOGGER.info("Enter deleteHistoryTask,taskIds={}", taskIds);

        try {
            taskBeanDao.deleteHistoryTask(taskIds);
            LOGGER.info("Exit the deleteHistoryTask");

            return "successed";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            return "failed";
        }
    }

    /**
     * 根据objectId查询任务 （额外条件：重置次数少于配置的上限）
     */
    @Override
    public TaskBean getTaskBean(String objectId) {
        LOGGER.info("Enter getTaskBean,objectId={}" + objectId);

        // 查询条件
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("objectId", objectId);
        map.put("retryCount", resetTotal);
        LOGGER.info("Exit the getTaskBean");

        return taskBeanDao.getTaskBean(map);
    }

    @Override
    public TaskBean getTaskInfo(String objectId) {
        LOGGER.info("Enter getTaskInfo,objectId={}" + objectId);

        // 查询条件
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("objectId", objectId);
        LOGGER.info("Exit the getTaskInfo");

        return taskBeanDao.getTaskInfo(map);
    }

    @Override
    public List<TaskBean> getTaskBeanList(TaskBean condition) {
        LOGGER.info("getTaskBeanList,condition={}", condition);

        return taskBeanDao.getTaskBeanList(condition);
    }

    @Override
    public List<TaskBean> getTaskBeanThriftList(TaskBean condition) {
        LOGGER.info("getTaskBeanList,condition={}", condition);

        return taskBeanDao.getTaskBeanThriftList(condition);
    }

    @Override
    public List<TaskBean> getHistoryTaskBeanList(TaskBean condition) {
        LOGGER.info("condition={}", condition);

        return taskBeanDao.getHistoryTaskBeanList(condition);
    }

    @Override
    public String addImage(ImgObject o) {
        LOGGER.info("Enter addImage,ImgObject={}", o);

        try {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            o.setConvertTime(ts);
            imageDao.addImage(o);
            LOGGER.info("Exit the addImage");

            return "successed";
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            return "failed";
        }
    }

    @Override
    public ImgObject getImage(String sourceObjectId) {
        LOGGER.info("sourceObjectId={}", sourceObjectId);

        return imageDao.getImage(sourceObjectId);
    }

    @Override
    public void deleteImageObject(String sourceObjectId) {
        LOGGER.info("sourceObjectId={}", sourceObjectId);
        imageDao.deleteImageObject(sourceObjectId);
    }

    private boolean isSupportPreview(INode iNode) {
        boolean isSupportPreview = false;
        String[] array = { "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf" };
        List<String> slist = Arrays.asList(array);

        if (iNode.getType() == INode.TYPE_FILE) {
            String filetype = iNode.getName()
                                   .substring(iNode.getName().lastIndexOf(".") +
                    1, iNode.getName().length());

            if (slist.contains(filetype.toLowerCase())) {
                isSupportPreview = true;
            }
        }

        return isSupportPreview;
    }

    @Override
    public void cleanTaskIp(NodeRunningInfo nodeRunningInfo) {
    	final String ip = nodeRunningInfo.getHostIP();
    	final String resourceGroupId = ""+nodeRunningInfo.getResourceGroupID();
        LOGGER.info("cleanTaks,Ip={},resourceGroupId={}", ip, resourceGroupId);

        if (ip.equals(assignMap.get(resourceGroupId))) {
            assignMap.remove(resourceGroupId);
            taskBeanDao.deleteNodeSign(resourceGroupId);
            getNodeRuningInfo(nodeRunningInfo);
        }

        new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        LOGGER.info("cleanTaksRun,Ip={},resourceGroupId={}",
                            ip, resourceGroupId);

                        List<NodeRunningInfo> nodeRuningInfoList = cSMonitorService.getNormalNodes(resourceGroupId);

                        String assignIP = taskBeanDao.getNodeSign(resourceGroupId);
                        int nodeCount = nodeRuningInfoList.size();
                        List<TaskBean> list = taskBeanDao.getTasks(ip);

                        for (TaskBean taskBean : list) {
                            String csip = "";

                            if (1 == taskBean.getBigFileFlag()) {
                                if (nodeCount > 1) {
                                    csip = assignIP;
                                }
                            } else {
                                if (0 != nodeRuningInfoList.size()) {
                                    NodeRunningInfo nodeRunningInfo = nodeRuningInfoList.remove(0);

                                    if (nodeCount > 1) {
                                        if (assignIP.equals(
                                                    nodeRunningInfo.getHostIP())) {
                                            if (taskBeanDao.getAssignIpTaskCount(
                                                        nodeRunningInfo.getHostIP()) > 0) {
                                                nodeRuningInfoList.add(nodeRunningInfo);
                                                nodeRunningInfo = nodeRuningInfoList.remove(0);
                                            }
                                        }
                                    }

                                    csip = nodeRunningInfo.getHostIP();
                                    nodeRuningInfoList.add(nodeRunningInfo);
                                }
                            }

                            LOGGER.info("convertTask,taskId={},ip={},csip={}",
                                taskBean.getTaskId(), ip, csip);
                            taskBeanDao.updateTaskHandleIp(taskBean.getTaskId(),
                                csip);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }).start();
    }

    @Override
    public void updateTaskStatus(List<String> taskIds) {
        LOGGER.info("updateTaskStastus,strar taskIds={}", taskIds);
        taskBeanDao.updateTaskStatus(taskIds);
    }

	private void getNodeRuningInfo(NodeRunningInfo nodeRunningInfo) {
		try {
			String resourceGroupID = "" + nodeRunningInfo.getResourceGroupID();
			LOGGER.info("Enter NodeRuningInfo,resourceGroupID={}", resourceGroupID);
			
			List<NodeRunningInfo> nodeRuningInfoList = cSMonitorService.getNormalNodes(resourceGroupID);
			LOGGER.info("normalDssList=", nodeRuningInfoList);
			
			if (null == assignMap.get(resourceGroupID)) {
				String csip = taskBeanDao.getNodeSign(resourceGroupID);
				LOGGER.info("csip={}", csip);
				
				if (csip.isEmpty()) {
					if (0 != nodeRuningInfoList.size()) {
						csip = nodeRuningInfoList.get(0).getHostIP();
						
						Map<String, Object> nodeSign = new HashMap<String, Object>();
						nodeSign.put("resourceGroupID", resourceGroupID);
						nodeSign.put("csip", csip);
						
						taskBeanDao.insertNodeSign(nodeSign);
						
						assignMap.put(resourceGroupID, csip);
					}
				} else {
					assignMap.put(resourceGroupID, csip);
				}
			}
			
			nodeRunMap.put(resourceGroupID, nodeRuningInfoList);
			nodeCountMap.put(resourceGroupID, nodeRuningInfoList.size());
			LOGGER.info("End NodeRuningInfo");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

    /**
     * 恢复异常的任?
     */
	@Override
    public void renewTask(NodeRunningInfo nodeRunningInfo) {
    	final String ip = nodeRunningInfo.getHostIP();
    	final String resourceGroupId = ""+nodeRunningInfo.getResourceGroupID();
        LOGGER.info("renewTask,ip={},resourceGroupId={}", ip, resourceGroupId);

        if (null == assignMap.get(resourceGroupId)) {
            getNodeRuningInfo(nodeRunningInfo);
        }

        new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        LOGGER.info("renewTask,Ip={},resourceGroupId={}", ip,
                            resourceGroupId);

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("ip", ip);
                        map.put("resourceGroupId", resourceGroupId);

                        List<NodeRunningInfo> nodeRuningInfoList = cSMonitorService.getNormalNodes(resourceGroupId);
                        String assignIP = taskBeanDao.getNodeSign(resourceGroupId);
                        int nodeCount = nodeRuningInfoList.size();
                        List<TaskBean> list = taskBeanDao.getRenewTasks(map);

                        for (TaskBean taskBean : list) {
                            String csip = "";

                            if (1 == taskBean.getBigFileFlag()) {
                                csip = assignIP;
                            } else {
                                NodeRunningInfo nodeRunningInfo = nodeRuningInfoList.remove(0);

                                if (nodeCount > 1) {
                                    if (assignIP.equals(
                                                nodeRunningInfo.getHostIP())) {
                                        if (taskBeanDao.getAssignIpTaskCount(
                                                    nodeRunningInfo.getHostIP()) > 0) {
                                            nodeRuningInfoList.add(nodeRunningInfo);
                                            nodeRunningInfo = nodeRuningInfoList.remove(0);
                                        }
                                    }
                                }

                                csip = nodeRunningInfo.getHostIP();
                                nodeRuningInfoList.add(nodeRunningInfo);
                            }

                            LOGGER.info("convertTask,taskId={},ip={},csip={}",
                                taskBean.getTaskId(), ip, csip);
                            taskBeanDao.updateTaskHandleIp(taskBean.getTaskId(),
                                csip);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }).start();
    }

    public void resetNodeRunningStatus() {
        if ((assignMap != null) && assignMap.isEmpty()) {
            for (String key : assignMap.keySet()) {
            	NodeRunningInfo nodeRunningInfo = new NodeRunningInfo();
            	nodeRunningInfo.setResourceGroupID(Integer.parseInt(key));
                getNodeRuningInfo(nodeRunningInfo);
            }
        }
    }
}

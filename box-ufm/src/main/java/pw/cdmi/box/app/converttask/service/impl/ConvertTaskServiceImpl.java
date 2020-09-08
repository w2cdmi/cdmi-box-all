package pw.cdmi.box.app.converttask.service.impl;

import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.teamspace.domain.TeamRole;
import com.huawei.sharedrive.app.teamspace.domain.TeamSpaceMemberships;
import com.huawei.sharedrive.app.teamspace.service.TeamSpaceMembershipService;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.box.app.converttask.domain.ConvertInfo;
import pw.cdmi.box.app.converttask.service.ConvertTaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service("convertTaskService")
public class ConvertTaskServiceImpl implements ConvertTaskService {
    //团队空间
    private final static String SPACETYPE = "1";
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertTaskServiceImpl.class);
    @Autowired
    private TeamSpaceMembershipService teamSpaceMembershipService;
    @Autowired
    private ConvertService convertService;

    @Override
    public ConvertInfo getDoingConvertTask(UserToken user, long ownerId,
        String spaceType, String curpage, String size) {
        LOGGER.info("service->getDoingConvertTask");

        if (SPACETYPE.equals(spaceType)) {
            checkACL(user, ownerId,""+user.getAccountVistor().getEnterpriseId());
        }

        TaskBean task = new TaskBean();
        task.setOwneId(String.valueOf(ownerId));

        List<TaskBean> tasks = convertService.getTaskBeanList(task);
        int totalCount = tasks.size();
        task.setCurrPage(curpage);
        task.setPageSize(size);
        tasks = convertService.getTaskBeanList(task);

        ConvertInfo info = new ConvertInfo(tasks, totalCount,
                Integer.parseInt(size));

        return info;
    }

    @Override
    public ConvertInfo getDoneConvertTask(UserToken user, long ownerId,
        String spaceType, String curpage, String size) {
        LOGGER.info("service->getDoneConvertTask");

        if (SPACETYPE.equals(spaceType)) {
            checkACL(user, ownerId,""+user.getAccountVistor().getEnterpriseId());
        }

        TaskBean task = new TaskBean();
        task.setOwneId(String.valueOf(ownerId));
        task.setOrderyBy("convertTime desc");

        List<TaskBean> tasks = convertService.getHistoryTaskBeanList(task);
        int totalCount = tasks.size();
        LOGGER.info("totalCount->" + totalCount);
        task.setCurrPage(curpage);
        task.setPageSize(size);
        tasks = convertService.getHistoryTaskBeanList(task);

        ConvertInfo info = new ConvertInfo(tasks, totalCount,
                Integer.parseInt(size));

        return info;
    }

    @Override
    public void deleteDoneConvertTask(UserToken user, String[] taskids,
        String spaceType, long ownerId) {
        LOGGER.info("service->deleteDoneConvertTask");
        LOGGER.info("service->taskids:" + Arrays.toString(taskids));

        if (SPACETYPE.equals(spaceType)) {
            checkACL(user, ownerId,""+user.getAccountVistor().getEnterpriseId());
        }

        if (taskids != null) {
            List<String> taskidList = new ArrayList<String>(taskids.length);

            for (int i = 0; i < taskids.length; i++) {
                taskidList.add(taskids[i]);
            }

            LOGGER.info("service->taskidList:" + taskidList);
            convertService.deleteHistoryTask(taskidList);
        } else {
            TaskBean task = new TaskBean();
            task.setOwneId(String.valueOf(ownerId));

            List<TaskBean> tasks = convertService.getHistoryTaskBeanList(task);
            List<String> taskidList = new ArrayList<String>();

            for (int i = 0; (tasks != null) && (i < tasks.size()); i++) {
                taskidList.add(tasks.get(i).getTaskId());
            }

            LOGGER.info("service->taskidList:" + taskidList);

            if (taskidList.size() > 0) {
                convertService.deleteHistoryTask(taskidList);
            }
        }
    }

    private void checkACL(UserToken user, long teamSpaceId,String enterpriseId) {
        if (user.getId() != User.APP_USER_ID) {
            TeamSpaceMemberships teamSpaceMembler = teamSpaceMembershipService.getUserMemberShips(teamSpaceId,
                    user.getId(),""+user.getAccountVistor().getEnterpriseId());

            if (teamSpaceMembler == null) {
                LOGGER.error("User {} is not the member of the teamspace {}",
                    user.getId(), teamSpaceId);
                throw new ForbiddenException("User not the member of teamspace");
            }

            if (!TeamRole.ROLE_ADMIN.equals(teamSpaceMembler.getTeamRole())) {
                String excepMessage = "Operation is not allowed , team Role:" +
                    teamSpaceMembler.getTeamRole();
                throw new ForbiddenException(excepMessage);
            }
        }
    }

    @Override
    public void retryDoing(UserToken user, String taskId, String spaceType,
        long ownerId) {
        LOGGER.info("service->retryDoing");

        if (SPACETYPE.equals(spaceType)) {
            checkACL(user, ownerId,""+user.getAccountVistor().getEnterpriseId());
        }

        TaskBean taskBean = new TaskBean();
        taskBean.setTaskId(taskId);
        taskBean.setStatus(9);
        taskBean.setRetryCount(0);
        taskBean.setPercent(0);
        convertService.updateTask(taskBean);
    }
}

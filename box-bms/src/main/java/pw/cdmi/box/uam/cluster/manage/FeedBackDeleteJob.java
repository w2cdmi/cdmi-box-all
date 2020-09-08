package pw.cdmi.box.uam.cluster.manage;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.feedback.service.UserFeedBackService;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

@Service("feedBackDeleteJob")
public class FeedBackDeleteJob extends QuartzJobTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeedBackDeleteJob.class);
	
	@Autowired
	private UserFeedBackService  feedBackService;

	@Override
	public void doTask(JobExecuteContext context, JobExecuteRecord record) {
		Date now = new Date();
		try {
			feedBackService.physicsDeleteUserFeedBack(now);
		} catch (Exception e) {
			String message = "delete feedback failed.";
            LOGGER.error(message, e);
		}
	}

}

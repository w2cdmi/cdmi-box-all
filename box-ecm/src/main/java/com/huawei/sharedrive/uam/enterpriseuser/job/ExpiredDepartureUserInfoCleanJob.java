package com.huawei.sharedrive.uam.enterpriseuser.job;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.service.IMigrationRecordService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;

/**
 * 过期的移交记录数据清理
 * @author 77235
 *
 */
@Component("cleanDepartureUserInfoJob")
public class ExpiredDepartureUserInfoCleanJob extends QuartzJobTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredDepartureUserInfoCleanJob.class);

	private Integer submeterTableNum;
	
	@Autowired
	private IMigrationRecordService migrationRecordService;

	@Override
	public void doTask(JobExecuteContext context, JobExecuteRecord record) {
		LOGGER.info("[CleanDepartureUserInfoJob] start clean expired message.");
		
		Date now = new Date();
		int tableNum = this.getSubmeterTableNum();
		
		for (int i = 0; i < tableNum; i++) {
			try {
				migrationRecordService.cleanExpiredMigrationRecord(now, i);
			} catch (Exception e) {
				String message = "clear table system_message_status_" + i + " failed.";
				LOGGER.error(message, e);
				record.setSuccess(false);
				record.setOutput(StringUtils.trimToEmpty(record.getOutput()) + ';' + message);
			}
		}
	}

	/**
	 * 分表数
	 * @return
	 */
	private Integer getSubmeterTableNum() {
		if (null == this.submeterTableNum) {
			this.submeterTableNum = Integer.parseInt(this.getParameter());
		}

		return submeterTableNum;
	}
}

package com.techne.bankprocessor.scheduler;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchedulerService {

    @Autowired
    private Scheduler scheduler;

    public void scheduleJob(Long jobId, String jobName, String cronExpression) {
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("jobId", jobId);

            JobDetail jobDetail = JobBuilder.newJob(BankProcessorJob.class)
                    .withIdentity("job-" + jobId, "bank-processor-jobs")
                    .setJobData(jobDataMap)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger-" + jobId, "bank-processor-triggers")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Scheduled job '{}' with ID {} using cron expression: {}", jobName, jobId, cronExpression);

        } catch (SchedulerException e) {
            log.error("Error scheduling job with ID {}: {}", jobId, e.getMessage());
            throw new RuntimeException("Failed to schedule job", e);
        }
    }

    public void unscheduleJob(Long jobId) {
        try {
            JobKey jobKey = new JobKey("job-" + jobId, "bank-processor-jobs");
            TriggerKey triggerKey = new TriggerKey("trigger-" + jobId, "bank-processor-triggers");
            
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(jobKey);
            log.info("Unscheduled job with ID {}", jobId);

        } catch (SchedulerException e) {
            log.error("Error unscheduling job with ID {}: {}", jobId, e.getMessage());
            throw new RuntimeException("Failed to unschedule job", e);
        }
    }

    public void rescheduleJob(Long jobId, String jobName, String cronExpression) {
        unscheduleJob(jobId);
        scheduleJob(jobId, jobName, cronExpression);
    }
    
    public java.time.LocalDateTime getNextExecutionTime(Long jobId) {
		try {
			TriggerKey triggerKey = new TriggerKey("trigger-" + jobId, "bank-processor-triggers");
			Trigger trigger = scheduler.getTrigger(triggerKey);
			if (trigger != null) {
				java.util.Date nextFireTime = trigger.getNextFireTime();
				if (nextFireTime != null) {
					return java.time.LocalDateTime.ofInstant(nextFireTime.toInstant(), java.time.ZoneId.systemDefault());
				}
			}
			return null;
		} catch (SchedulerException e) {
			log.error("Error getting next execution time for job ID {}: {}", jobId, e.getMessage());
			throw new RuntimeException("Failed to get next execution time", e);
		}
	}
    
}
package com.techne.bankprocessor.scheduler;

import java.time.LocalDateTime;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.techne.bankprocessor.repository.JobRepository;
import com.techne.bankprocessor.service.JobService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BankProcessorJob implements Job {

    @Autowired
    private JobService jobService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long jobId = context.getJobDetail().getJobDataMap().getLong("jobId");        
                
        log.info("Executing routine for Job ID: {}", jobId);

        
        jobService.updateLastExecution(jobId, LocalDateTime.now());
        jobService.updateNextExecution(jobId);
        
        // TODO: Implement routine logic
        executeRoutine(jobId);
    }
        
    private void executeRoutine(Long jobId) {
    	// TODO: Implement routine logic     
    	log.info("Routine executed for Job ID: {}", jobId);
    }
}
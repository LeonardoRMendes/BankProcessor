package com.techne.bankprocessor.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.techne.bankprocessor.entity.Job;
import com.techne.bankprocessor.model.StatusJob;
import com.techne.bankprocessor.repository.JobRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JobSchedulerInitializer implements CommandLineRunner {

    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing job scheduler...");
        
        List<Job> existingJobs = jobRepository.findByStatusNot(StatusJob.DESATIVADO);
        
        for (Job job : existingJobs) {
            try {
                schedulerService.scheduleJob(job.getId(), job.getNome(), job.getCronExpression());
                log.info("Scheduled existing job: {} (ID: {})", job.getNome(), job.getId());
            } catch (Exception e) {
                log.error("Failed to schedule job: {} (ID: {}). Error: {}", job.getNome(), job.getId(), e.getMessage());
            }
        }
        
        log.info("Job scheduler initialization completed. Scheduled {} jobs.", existingJobs.size());
    }
}
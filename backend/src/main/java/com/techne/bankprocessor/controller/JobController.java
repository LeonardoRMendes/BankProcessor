package com.techne.bankprocessor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techne.bankprocessor.dto.CreateJobDTO;
import com.techne.bankprocessor.dto.JobDTO;
import com.techne.bankprocessor.scheduler.SchedulerService;
import com.techne.bankprocessor.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;
    
    @Autowired 
    private SchedulerService schedulerService;

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody CreateJobDTO createJobDTO) {
        JobDTO jobDTO = jobService.createJob(createJobDTO);
        return new ResponseEntity<>(jobDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        List<JobDTO> jobDTOs = jobService.getAllJobs();
        return ResponseEntity.ok(jobDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        JobDTO jobDTO = jobService.getJobById(id);
        if (jobDTO != null) {
            return ResponseEntity.ok(jobDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @RequestBody CreateJobDTO createJobDTO) {
        try {
        	JobDTO updatedJob = jobService.updateJob(id, createJobDTO);            
        	updatedJob = jobService.updateNextExecution(updatedJob.getId());
        	return ResponseEntity.ok(updatedJob);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
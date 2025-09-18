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
import com.techne.bankprocessor.entity.Job;
import com.techne.bankprocessor.mapper.JobMapper;
import com.techne.bankprocessor.model.StatusJob;
import com.techne.bankprocessor.repository.JobRepository;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@RequestBody CreateJobDTO createJobDTO) {
        Job job = jobMapper.toEntity(createJobDTO);
        job.setStatus(StatusJob.AGENDADO);
        Job savedJob = jobRepository.save(job);
        JobDTO jobDTO = jobMapper.toDTO(savedJob);
        return new ResponseEntity<>(jobDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOs = jobs.stream()
                .map(jobMapper::toDTO)
                .toList();
        return ResponseEntity.ok(jobDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(job -> ResponseEntity.ok(jobMapper.toDTO(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @RequestBody CreateJobDTO createJobDTO) {
        return jobRepository.findById(id)
                .map(existingJob -> {
                    existingJob.setNome(createJobDTO.getNome());
                    existingJob.setCronExpression(createJobDTO.getCronExpression());
                    Job updatedJob = jobRepository.save(existingJob);
                    return ResponseEntity.ok(jobMapper.toDTO(updatedJob));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

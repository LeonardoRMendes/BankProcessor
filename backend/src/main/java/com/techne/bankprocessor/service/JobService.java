package com.techne.bankprocessor.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techne.bankprocessor.dto.CreateJobDTO;
import com.techne.bankprocessor.dto.JobDTO;
import com.techne.bankprocessor.entity.Job;
import com.techne.bankprocessor.mapper.JobMapper;
import com.techne.bankprocessor.repository.JobRepository;
import com.techne.bankprocessor.scheduler.QuartzSchedulerService;
import com.techne.bankprocessor.model.StatusJob;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private QuartzSchedulerService quartzSchedulerService;

    @Transactional
    public JobDTO createJob(CreateJobDTO dto) {
        Job job = jobMapper.toEntity(dto);
        job.setStatus(StatusJob.AGENDADO);
        job = jobRepository.save(job);
        
        quartzSchedulerService.scheduleJob(job.getId(), job.getNome(), job.getCronExpression());        
        
        return jobMapper.toDTO(job);
    }

    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream()
            .map(jobMapper::toDTO)
            .collect(Collectors.toList());
    }

    public JobDTO getJobById(Long id) {
        return jobRepository.findById(id)
            .map(jobMapper::toDTO)
            .orElse(null);
    }

    @Transactional
    public JobDTO updateJob(Long id, CreateJobDTO dto) {
        return jobRepository.findById(id).map(job -> {
            String oldCronExpression = job.getCronExpression();
            
            job.setNome(dto.getNome());
            job.setCronExpression(dto.getCronExpression());
            job = jobRepository.save(job);
            
            if (!oldCronExpression.equals(dto.getCronExpression())) {
                quartzSchedulerService.rescheduleJob(job.getId(), job.getNome(), job.getCronExpression());
            }
            
            return jobMapper.toDTO(job);
        }).orElseThrow(() -> new EntityNotFoundException("Job não encontrado com id: " + id));
    }

    public void deleteJob(Long id) {
        quartzSchedulerService.unscheduleJob(id);
        jobRepository.deleteById(id);
    }
}
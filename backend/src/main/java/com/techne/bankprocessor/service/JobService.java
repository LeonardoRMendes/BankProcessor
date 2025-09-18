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

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobMapper jobMapper;

    @Transactional
    public JobDTO createJob(CreateJobDTO dto) {
        Job job = jobMapper.toEntity(dto);
        job = jobRepository.save(job);
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
            job.setNome(dto.getNome());
            job.setCronExpression(dto.getCronExpression());
            job = jobRepository.save(job);
            return jobMapper.toDTO(job);
        }).orElseThrow(() -> new EntityNotFoundException("Job não encontrado com id: " + id));
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}
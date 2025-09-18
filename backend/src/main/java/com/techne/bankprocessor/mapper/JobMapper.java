package com.techne.bankprocessor.mapper;

import org.springframework.stereotype.Component;

import com.techne.bankprocessor.dto.CreateJobDTO;
import com.techne.bankprocessor.dto.JobDTO;
import com.techne.bankprocessor.entity.Job;

@Component
public class JobMapper {
    public Job toEntity(CreateJobDTO dto) {
        Job job = new Job();
        job.setNome(dto.getNome());
        job.setCronExpression(dto.getCronExpression());        
        return job;
    }

    public JobDTO toDTO(Job job) {
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setNome(job.getNome());
        dto.setCronExpression(job.getCronExpression());
        dto.setStatus(job.getStatus());
        dto.setUltimaExecucao(job.getUltimaExecucao());
        dto.setProximaExecucao(job.getProximaExecucao());
        return dto;
    }
}